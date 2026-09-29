package com.fmz.compox2d.physics

import com.fmz.compox2d.engine.math.Vec2
import org.jbox2d.collision.shapes.CircleShape
import org.jbox2d.collision.shapes.EdgeShape
import org.jbox2d.collision.shapes.PolygonShape
import org.jbox2d.dynamics.BodyType
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import org.jbox2d.common.Vec2 as Vector2

internal class Box2dFactor(private var pixelsPerMeter: Float) {

    fun pxToM(px: Float): Float = (px / pixelsPerMeter)
    fun mToPx(m: Float): Float = (m * pixelsPerMeter)

    fun toBox2d(vec2: Vec2): Vector2 = Vector2(pxToM(vec2.x), pxToM(vec2.y))
    fun toEngine(vec2: Vector2): Vec2 = Vec2(mToPx(vec2.x), mToPx(vec2.y))

    fun radToDeg(rad: Double): Float = ((rad * 180) / PI).toFloat()
    fun degToRad(deg: Float): Double = (deg * PI) / 180

    fun toBox2d(bodyType: CollisionBodyType) = when (bodyType) {
        CollisionBodyType.Static -> BodyType.STATIC
        CollisionBodyType.Dynamic -> BodyType.DYNAMIC
        CollisionBodyType.Kinematic -> BodyType.KINEMATIC
    }

    fun toEngine(bodyType: BodyType) = when (bodyType) {
        BodyType.STATIC -> CollisionBodyType.Static
        BodyType.DYNAMIC -> CollisionBodyType.Dynamic
        BodyType.KINEMATIC -> CollisionBodyType.Kinematic
    }

    fun toBox2d(shape: Shape): org.jbox2d.collision.shapes.Shape {
        return when (shape) {
            is Shape.BoxShape -> {
                PolygonShape().apply {
                    setAsBox(pxToM(shape.size.x / 2f), pxToM(shape.size.y / 2f))
                }
            }

            is Shape.CircleShape -> {
                CircleShape(pxToM(shape.radius))
            }

            is Shape.EllipseShape -> {
                val vertices = Array(8) { i ->
                    val angle = 2.0 * PI * i / 8.0
                    Vector2(
                        pxToM(shape.radius.x * cos(angle).toFloat()),
                        pxToM(shape.radius.y * sin(angle).toFloat()),
                    )
                }
                PolygonShape().apply {
                    set(vertices, vertices.size)
                }
            }

            is Shape.PolygonShape -> {
                val vertices = shape.vertices.map { toBox2d(it) }.toTypedArray()
                PolygonShape().apply {
                    set(vertices, vertices.size)
                }
            }

            is Shape.SegmentShape -> {
                EdgeShape().apply {
                    set(toBox2d(shape.start), toBox2d(shape.end))
                }
            }

            is Shape.CapsuleShape -> {
                val h = pxToM(shape.size.y / 2f)
                val w = pxToM(shape.size.x / 2f)
                val d = (h - w).coerceAtLeast(0f)
                
                val vertices = mutableListOf<Vector2>()
                for (i in 0..3) {
                    val angle = PI * i / 3.0
                    vertices.add(
                        Vector2(
                            (w * cos(angle)).toFloat(),
                            (d + w * sin(angle)).toFloat()
                        )
                    )
                }
                for (i in 0..3) {
                    val angle = PI + PI * i / 3.0
                    vertices.add(
                        Vector2(
                            (w * cos(angle)).toFloat(),
                            (-d + w * sin(angle)).toFloat(),
                        )
                    )
                }
                
                PolygonShape().apply {
                    val vArray = vertices.toTypedArray()
                    set(vArray, vArray.size)
                }
            }

            is Shape.SliceShape -> {
                val r = pxToM(shape.radius)
                val thetaRad = degToRad(shape.theta)
                val vertices = mutableListOf<Vector2>()
                vertices.add(Vector2(0f, 0f))
                
                val steps = 7
                for (i in 0..steps) {
                    val angle = -thetaRad / 2.0 + thetaRad * i / steps.toDouble()
                    vertices.add(
                    Vector2(
                        (r * cos(angle)).toFloat(),
                        (r * sin(angle)).toFloat()
                    )
                )
                }

                PolygonShape().apply {
                    val vArray = vertices.toTypedArray()
                    set(vArray, vArray.size)
                }
            }
        }
    }

    fun toEngine(shape: org.jbox2d.collision.shapes.Shape): Shape {
        return when (shape) {
            is CircleShape -> Shape.CircleShape(mToPx(shape.radius))
            is PolygonShape -> {
                if (shape.count == 4) {
                    val v0 = shape.vertices[0]
                    val v1 = shape.vertices[1]
                    val v2 = shape.vertices[2]
                    val v3 = shape.vertices[3]
                    if (v0.x == -v1.x && v0.y == v1.y && v1.x == v2.x && v1.y == -v2.y && v2.x == -v3.x && v2.y == v3.y) {
                        Shape.BoxShape(Vec2(mToPx(v1.x * 2), mToPx(v1.y * 2)))
                    } else {
                        Shape.PolygonShape((0 until shape.count).map { toEngine(shape.vertices[it]) })
                    }
                } else {
                    Shape.PolygonShape((0 until shape.count).map { toEngine(shape.vertices[it]) })
                }
            }
            is EdgeShape -> {
                Shape.SegmentShape(toEngine(shape.vertex1), toEngine(shape.vertex2))
            }
            else -> throw IllegalArgumentException("Unsupported shape type: ${shape::class.simpleName}")
        }
    }
}