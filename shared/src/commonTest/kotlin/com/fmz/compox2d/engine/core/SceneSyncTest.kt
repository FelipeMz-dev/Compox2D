package com.fmz.compox2d.engine.core

import com.fmz.compox2d.engine.math.Vec2
import com.fmz.compox2d.engine.render.Viewport
import com.fmz.compox2d.physics.CollisionBodyType
import com.fmz.compox2d.physics.PhysicsManager
import com.fmz.compox2d.physics.Shape
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SceneSyncTest {

    @Test
    fun testSyncWithNestedSpawning() {
        val manager = SceneEntityManager()
        
        val child = object : GameObject() {}
        val parent = object : GameObject() {
            override fun onEnterScene() {
                manager.enqueueAdd(child)
            }
        }

        manager.enqueueAdd(parent)
        
        manager.sync(
            onRemoved = {},
            onAdded = { it.onAddedToScene(createMockContext()) }
        )

        assertTrue(manager.activeInstances.contains(parent), "Parent should be active")
        assertTrue(manager.activeInstances.contains(child), "Child should be active (nested spawn handled in same sync)")
        assertEquals(2, manager.activeInstances.size)
    }

    @Test
    fun testPreviousTransformInitialization() {
        val gameObject = object : GameObject() {
            fun setPos(x: Float, y: Float) {
                updatePosition { Vec2(x, y) }
            }
        }
        
        gameObject.setPos(100f, 100f)
        gameObject.onAddedToScene(createMockContext())
        
        val state = gameObject.currentState()
        assertEquals(100f, state.position.x)
    }

    @Test
    fun testPhysicsTransformSyncsOwnerGameObject() {
        val gameObject = object : GameObject() {}
        val manager = PhysicsManager()
        gameObject.onAddedToScene(createMockContext())
        val body = manager.createRigidBody(
            owner = gameObject,
            config = com.fmz.compox2d.physics.RigidBodyConfig(
                shape = Shape.CircleShape(10f),
                state = TransformState(Vec2(20f, 30f), 45f),
                type = CollisionBodyType.Dynamic,
            )
        )

        val newState = TransformState(Vec2(50f, 60f), 90f)
        manager.updateBodyTransform(body.body, newState)
        manager.syncOwnerTransforms()

        assertTrue(kotlin.math.abs(50f - gameObject.currentState().position.x) < 0.01f)
        assertTrue(kotlin.math.abs(60f - gameObject.currentState().position.y) < 0.01f)
        assertTrue(kotlin.math.abs(90f - gameObject.currentState().angle) < 0.01f)
    }

    private fun createMockContext(): WorldContext = object : WorldContext {
        override fun audioPlayer() = null
        override fun physicsManager() = PhysicsManager()
        override fun camera2D() = null
        override fun viewport() = Viewport(Vec2.Zero, Vec2(1f, 1f))
        override fun spriteSize(id: SpriteId) = Vec2.Zero
        override fun removeGameObject(gameObject: GameObject) {}
        override fun spawnGameObject(gameObject: GameObject) {}
        override fun calculateFromViewport(position: Vec2) = position
        override fun screenToWorld(position: Vec2) = position
        override fun worldToScreen(position: Vec2) = position
    }
}
