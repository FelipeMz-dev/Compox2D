package com.fmz.compox2d.physics

import com.fmz.compox2d.engine.core.GameObject
import com.fmz.compox2d.engine.core.TransformState
import com.fmz.compox2d.engine.math.MathShape
import com.fmz.compox2d.engine.math.Vec2
import com.fmz.compox2d.engine.render.RenderPivot
import com.soywiz.korma.geom.Angle
import org.jbox2d.callbacks.ContactImpulse
import org.jbox2d.callbacks.ContactListener
import org.jbox2d.collision.Manifold
import org.jbox2d.collision.WorldManifold
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.BodyDef
import org.jbox2d.dynamics.Filter
import org.jbox2d.dynamics.FixtureDef
import org.jbox2d.dynamics.World
import org.jbox2d.dynamics.contacts.Contact
import org.jbox2d.dynamics.forEachFixture
import org.jbox2d.dynamics.joints.DistanceJointDef
import org.jbox2d.dynamics.joints.RevoluteJointDef
import org.jbox2d.dynamics.joints.WeldJointDef
import kotlin.math.abs
import kotlin.math.sqrt
import org.jbox2d.common.Vec2 as Vector2

class PhysicsManager(
    gravity: Vec2 = Vec2(0f, 9.8f),
    pixelsPerMeter: Float = 100f,
) {

    private val factor = Box2dFactor(pixelsPerMeter)
    private val activeContactPairs = mutableMapOf<PhysicsContactPairKey, Int>()
    private var lastStepDt = 1f / 60f
    private val joints = mutableSetOf<PhysicsJoint>()
    private val deferredActions = mutableListOf<() -> Unit>()
    internal val world = World(factor.toBox2d(gravity)).apply {
        setContactListener(PhysicsContactListener())
    }

    fun update(dt: Float) {
        lastStepDt = dt.coerceAtLeast(MIN_STEP_DT)
        world.step(dt, 8, 3)
        executeDeferredActions()
    }

    private fun executeDeferredActions() {
        if (deferredActions.isEmpty()) return
        val actions = deferredActions.toList()
        deferredActions.clear()
        actions.forEach { it() }
        if (deferredActions.isNotEmpty()) executeDeferredActions()
    }

    private fun runSafe(action: () -> Unit) {
        if (world.isLocked) {
            deferredActions.add(action)
        } else {
            action()
        }
    }

    internal fun createRigidBody(
        owner: GameObject,
        config: RigidBodyConfig,
    ): RigidBody {
        val body = createBody(owner, config)
        val rigidBody = RigidBody(
            owner = owner,
            body = body,
            manager = this,
            layer = config.layer,
            mask = config.mask,
        )
        body.forEachFixture { fixture ->
            fixture.userData = rigidBody
            fixture.filterData = Filter().apply {
                categoryBits = config.layer
                maskBits = config.mask
            }
        }
        return rigidBody
    }

    internal fun createTrigger(
        owner: GameObject,
        config: TriggerConfig,
    ): Trigger {
        val body = createBody(owner = owner, config = config)
        val trigger = Trigger(
            owner = owner,
            body = body,
            manager = this,
            layer = config.layer,
            mask = config.mask,
        )
        body.forEachFixture { fixture ->
            fixture.userData = trigger
            fixture.filterData = Filter().apply {
                categoryBits = config.layer
                maskBits = config.mask
            }
        }
        return trigger
    }

    internal fun createJoint(config: PhysicsJointConfig): PhysicsJoint {
        val bodyA = config.bodyA.body
        val bodyB = config.bodyB.body
        val jointDef = when (config) {
            is PhysicsJointConfig.Distance -> DistanceJointDef().apply {
                initialize(
                    bodyA,
                    bodyB,
                    factor.toBox2d(config.anchorA),
                    factor.toBox2d(config.anchorB)
                )
                length = factor.pxToM(config.length)
                frequencyHz = config.frequencyHz
                dampingRatio = config.dampingRatio
                collideConnected = config.collisionAllowed
            }

            is PhysicsJointConfig.Revolute -> RevoluteJointDef().apply {
                initialize(bodyA, bodyB, factor.toBox2d(config.anchor))
                enableLimit = config.enableLimit
                lowerAngle = Angle(factor.degToRad(config.lowerAngle))
                upperAngle = Angle(factor.degToRad(config.upperAngle))
                enableMotor = config.enableMotor
                motorSpeed = config.motorSpeed
                maxMotorTorque = config.maxMotorTorque
                collideConnected = config.collisionAllowed
            }

            is PhysicsJointConfig.Weld -> WeldJointDef().apply {
                initialize(bodyA, bodyB, factor.toBox2d(config.anchor))
                frequencyHz = config.frequencyHz
                dampingRatio = config.dampingRatio
                collideConnected = config.collisionAllowed
            }
        }

        val jointBox2d =
            world.createJoint(jointDef) ?: throw IllegalStateException("Failed to create joint")
        val joint = PhysicsJoint(
            joint = jointBox2d,
            bodyA = bodyA,
            bodyB = bodyB,
            manager = this,
        )
        joints += joint
        return joint
    }

    private fun createBody(
        owner: GameObject,
        config: RigidBodyConfig,
    ): Body {
        val massType = factor.toBox2d(config.type)
        return createBody(
            owner = owner,
            shape = config.shape,
            state = config.state,
            bodyType = massType,
            material = config.material,
            layer = config.layer,
            mask = config.mask,
            physicState = config.physicState,
            isTrigger = false,
        )
    }

    private fun createBody(
        owner: GameObject,
        config: TriggerConfig,
    ): Body {
        return createBody(
            owner = owner,
            shape = config.shape,
            state = config.state,
            bodyType = factor.toBox2d(CollisionBodyType.Static),
            material = PhysicsMaterial(),
            layer = config.layer,
            mask = config.mask,
            physicState = config.physicState,
            isTrigger = true,
        )
    }

    private fun createBody(
        owner: GameObject,
        shape: Shape,
        state: TransformState,
        bodyType: org.jbox2d.dynamics.BodyType,
        material: PhysicsMaterial,
        layer: Int,
        mask: Int,
        physicState: PhysicState,
        isTrigger: Boolean,
    ): Body {
        val angleRadians = factor.degToRad(state.angle).toFloat()
        val positionMeters = factor.toBox2d(state.position)
        val scaledShape = MathShape.scale(shape, state.scale)
        val jBox2DShape = factor.toBox2d(scaledShape)
        val bodyDef = BodyDef(
            type = bodyType,
            userData = owner,
            position = positionMeters,
            angleRadians = angleRadians
        )
        val fixtureDef = FixtureDef(
            shape = jBox2DShape,
            density = material.density,
            friction = material.friction,
            restitution = material.restitution,
            isSensor = isTrigger,
            filter = Filter().apply {
                categoryBits = layer
                maskBits = mask
            }
        )
        val body = world.createBody(bodyDef)
        body.createFixture(fixtureDef)
        updateBodyPhysicState(body, physicState)

        return body
    }

    internal fun getTransformState(body: Body): TransformState {
        return TransformState(
            position = factor.toEngine(body.position),
            angle = body.angleDegrees,
            scale = Vec2(1f, 1f),
            pivot = RenderPivot.Center,
        )
    }

    internal fun updateBodyTransform(body: Body, state: TransformState) = runSafe {
        body.setTransformDegrees(
            factor.toBox2d(state.position),
            state.angle
        )
    }

    internal fun getPhysicState(body: Body): PhysicState {
        return PhysicState(
            linearVelocity = factor.toEngine(body.linearVelocity),
            angularVelocity = body.angularVelocity,
            linearDamping = body.m_linearDamping,
            angularDamping = body.m_angularDamping,
            bullet = body.isBullet
        )
    }

    internal fun updateBodyPhysicState(body: Body, physicState: PhysicState) {
        body.m_linearDamping = physicState.linearDamping
        body.m_angularDamping = physicState.angularDamping
        body.linearVelocity = factor.toBox2d(physicState.linearVelocity)
        body.angularVelocity = physicState.angularVelocity
        body.isBullet = physicState.bullet
    }

    internal fun getMaterial(body: Body): PhysicsMaterial? {
        val fixture = body.getFixtureList()
        return fixture?.let {
            PhysicsMaterial(
                density = it.density,
                friction = it.friction,
                restitution = it.restitution
            )
        }
    }

    internal fun getShape(body: Body): Shape? {
        val fixture = body.getFixtureList()
        return fixture?.let {
            factor.toEngine(it.getShape()!!)
        }
    }

    internal fun getType(body: Body): CollisionBodyType {
        return factor.toEngine(body.type)
    }

    internal fun updateBodyType(body: Body, type: CollisionBodyType) = runSafe {
        val massType = factor.toBox2d(type)
        body.type = massType
    }

    internal fun updateBodyMaterial(body: Body, material: PhysicsMaterial) = runSafe {
        body.forEachFixture { fixture ->
            fixture.density = material.density
            fixture.friction = material.friction
            fixture.restitution = material.restitution
        }
    }

    internal fun updateBodyShape(body: Body, transformState: TransformState, shape: Shape) =
        runSafe {
            val previousFixture = body.getFixtureList()
            val scaledShape = MathShape.scale(shape, transformState.scale)
            val jShape = factor.toBox2d(scaledShape)

            // Remove all fixtures
            var current = body.getFixtureList()
            while (current != null) {
                val next = current.getNext()
                body.destroyFixture(current)
                current = next
            }

            val fixture = body.createFixture(jShape, 1.0f)
            if (fixture != null && previousFixture != null) {
                fixture.density = previousFixture.density
                fixture.friction = previousFixture.friction
                fixture.restitution = previousFixture.restitution
                fixture.isSensor = previousFixture.isSensor
                fixture.filterData = previousFixture.filterData
                fixture.userData = previousFixture.userData
            }
        }

    internal fun verifyOwnerRemoved(owner: GameObject) {
        var body = world.bodyList
        while (body != null) {
            val next = body.getNext()
            if (body.userData == owner) {
                removeBody(body)
            }
            body = next
        }
        val iterator = activeContactPairs.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (entry.key.includes(owner)) {
                iterator.remove()
            }
        }
    }

    internal fun applyImpulseTowards(
        body: Body,
        target: Vec2,
        force: Float = 40f,
        point: Vec2? = null
    ) {
        val targetXM = factor.pxToM(target.x)
        val targetYM = factor.pxToM(target.y)

        val bodyPos = body.position

        val dirX = targetXM - bodyPos.x
        val dirY = targetYM - bodyPos.y

        val length = sqrt(dirX * dirX + dirY * dirY)

        if (length > 0) {
            val normal = Vector2(dirX / length, dirY / length)
            val impulse = Vector2(normal.x * force, normal.y * force)
            val jPoint = point?.let { factor.toBox2d(it) } ?: body.worldCenter
            body.applyLinearImpulse(impulse, jPoint, true)
        }
    }

    internal fun applyImpulse(body: Body, impulse: Vec2, point: Vec2? = null) {
        val jImpulse = factor.toBox2d(impulse)
        val jPoint = point?.let { factor.toBox2d(it) } ?: body.worldCenter
        body.applyLinearImpulse(jImpulse, jPoint, true)
    }

    internal fun applyForce(body: Body, force: Vec2, point: Vec2? = null) {
        val jForce = factor.toBox2d(force)
        val jPoint = point?.let { factor.toBox2d(it) }
        if (jPoint != null) {
            body.applyForce(jForce, jPoint)
        } else {
            body.applyForceToCenter(jForce)
        }
    }

    internal fun applyTorque(body: Body, torque: Float) {
        body.applyTorque(torque)
    }

    internal fun stopBody(body: Body) {
        body.linearVelocity = Vector2(0f, 0f)
        body.angularVelocity = 0f
    }

    internal fun removeBody(body: Body) = runSafe {
        body.forEachFixture { fixture ->
            (fixture.userData as? PhysicsContactTarget)?.let { target ->
                val iterator = activeContactPairs.iterator()
                while (iterator.hasNext()) {
                    val entry = iterator.next()
                    if (entry.key.includes(target)) {
                        iterator.remove()
                    }
                }
            }
        }
        joints
            .filter { it.includes(body) }
            .forEach { removeJoint(it) }
        world.destroyBody(body)
    }

    internal fun removeBody(gameObject: GameObject) = runSafe {
        var body = world.bodyList
        while (body != null) {
            val next = body.getNext()
            if (body.userData == gameObject) {
                removeBody(body)
            }
            body = next
        }
    }

    internal fun removeJoint(joint: PhysicsJoint) = runSafe {
        world.destroyJoint(joint.joint)
        joints -= joint
    }

    internal fun toEngine(vector: Vector2): Vec2 {
        return factor.toEngine(vector)
    }

    private fun handleContact(
        contact: Contact,
        phase: CollisionPhase,
        impulse: ContactImpulse? = null,
    ) {
        val fixtureA = contact.getFixtureA() ?: return
        val fixtureB = contact.getFixtureB() ?: return
        val targetA = fixtureA.userData.asContactTarget() ?: return
        val targetB = fixtureB.userData.asContactTarget() ?: return

        val isRigidBodyCollision = targetA.source is RigidBody && targetB.source is RigidBody
        if (phase != CollisionPhase.Exit && !targetA.canNotify(targetB)) return

        val key = PhysicsContactPairKey.from(targetA, targetB)

        if (isRigidBodyCollision) {
            val details = contact.toDetails(impulse)
            dispatchPhase(
                key = key,
                phase = phase,
                dispatchAction = { dispatchCollision(targetA, targetB, phase, details) }
            )
        } else dispatchPhase(
            key = key,
            phase = phase,
            dispatchAction = { dispatchTrigger(targetA, targetB, phase) }
        )
    }

    private fun dispatchPhase(
        key: PhysicsContactPairKey,
        phase: CollisionPhase,
        dispatchAction: () -> Unit,
    ) {
        when (phase) {
            CollisionPhase.Enter -> {
                val contacts = activeContactPairs[key] ?: 0
                activeContactPairs[key] = contacts + 1
                if (contacts == 0) dispatchAction()
            }

            CollisionPhase.Stay -> dispatchAction()

            CollisionPhase.Exit -> {
                val remainingContacts = ((activeContactPairs[key] ?: 1) - 1).coerceAtLeast(0)
                if (remainingContacts == 0) {
                    activeContactPairs.remove(key)
                    dispatchAction()
                } else {
                    activeContactPairs[key] = remainingContacts
                }
            }
        }
    }

    private fun dispatchCollision(
        a: PhysicsContactTarget,
        b: PhysicsContactTarget,
        phase: CollisionPhase,
        details: CollisionDetails,
    ) {
        (a.owner as? CollisionListener)?.onCollision(
            CollisionEvent(
                a.source,
                b.source,
                phase,
                details
            )
        )
        (b.owner as? CollisionListener)?.onCollision(
            CollisionEvent(
                b.source,
                a.source,
                phase,
                details.reversed()
            )
        )
    }

    private fun dispatchTrigger(
        a: PhysicsContactTarget,
        b: PhysicsContactTarget,
        phase: CollisionPhase,
    ) {
        (a.owner as? TriggerListener)?.onTrigger(
            TriggerEvent(
                a.source,
                b.source,
                phase
            )
        )
        (b.owner as? TriggerListener)?.onTrigger(
            TriggerEvent(
                b.source,
                a.source,
                phase
            )
        )
    }

    private fun Contact.toDetails(
        impulse: ContactImpulse? = null,
    ): CollisionDetails {
        val worldManifold = WorldManifold()
        this.getWorldManifold(worldManifold)
        val normal = worldManifold.normal
        val point =
            if (worldManifold.points.isNotEmpty()) worldManifold.points[0] else Vector2(0f, 0f)

        val bodyA = this.getFixtureA()!!.getBody()!!
        val bodyB = this.getFixtureB()!!.getBody()!!

        val relativeVelocity = Vector2(
            bodyB.linearVelocity.x - bodyA.linearVelocity.x,
            bodyB.linearVelocity.y - bodyA.linearVelocity.y,
        )
        val relativeSpeed = sqrt(
            relativeVelocity.x * relativeVelocity.x + relativeVelocity.y * relativeVelocity.y
        )
        val normalSpeed = (relativeVelocity.x * normal.x + relativeVelocity.y * normal.y)

        val normalImpulse = impulse?.normalImpulses?.getOrNull(0) ?: 0f
        val tangentImpulse = impulse?.tangentImpulses?.getOrNull(0) ?: 0f
        val force = if (normalImpulse > 0f) {
            normalImpulse / lastStepDt
        } else {
            abs(normalSpeed) / lastStepDt
        }

        return CollisionDetails(
            point = factor.toEngine(point),
            normal = Vec2(normal.x, normal.y),
            depth = 0f,
            relativeVelocity = factor.toEngine(relativeVelocity),
            relativeSpeed = factor.mToPx(relativeSpeed),
            normalSpeed = factor.mToPx(normalSpeed),
            normalImpulse = normalImpulse,
            tangentImpulse = tangentImpulse,
            estimatedForce = force,
        )
    }

    private fun CollisionDetails.reversed(): CollisionDetails {
        return copy(
            normal = Vec2(-normal.x, -normal.y),
            relativeVelocity = Vec2(-relativeVelocity.x, -relativeVelocity.y),
            normalSpeed = -normalSpeed,
        )
    }

    private inner class PhysicsContactListener : ContactListener {
        override fun beginContact(contact: Contact) {
            handleContact(contact, CollisionPhase.Enter)
        }

        override fun endContact(contact: Contact) {
            handleContact(contact, CollisionPhase.Exit)
        }

        override fun preSolve(contact: Contact, oldManifold: Manifold) {
            handleContact(contact, CollisionPhase.Stay)
        }

        override fun postSolve(contact: Contact, impulse: ContactImpulse) {
            handleContact(contact, CollisionPhase.Stay, impulse)
        }
    }

    private fun Any?.asContactTarget(): PhysicsContactTarget? {
        return when (this) {
            is RigidBody -> PhysicsContactTarget(
                id = this.id,
                owner = this.owner,
                source = this,
                layer = this.layer,
                mask = this.mask,
            )

            is Trigger -> PhysicsContactTarget(
                id = this.id,
                owner = this.owner,
                source = this,
                layer = this.layer,
                mask = this.mask,
            )

            else -> null
        }
    }

    private companion object {
        private const val MIN_STEP_DT = 0.0001f
    }
}