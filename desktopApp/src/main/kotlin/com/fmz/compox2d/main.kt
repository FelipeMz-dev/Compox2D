package com.fmz.compox2d

import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


fun main() {
    val player1 = Entity(
        position = Vector(10f, 15f),
        angle = 90f,
        polygon = Polygon(
            Vector(-10f, -8f),
            Vector(10f, -8f),
            Vector(10f, 8f),
            Vector(-10f, 8f)
        )
    ).apply {
        computePolygon()
    }

    val player2 = Entity(
        position = Vector(30f, 15f),
        angle = 0f,
        polygon = Polygon(
            Vector(-10f, -8f),
            Vector(10f, -8f),
            Vector(10f, 8f),
            Vector(-10f, 8f)
        )
    ).apply {
        computePolygon()
    }

    while (!hasCollision(player1.polygon, player2.polygon)) {
        player1.position = player1.position.copy(y = player1.position.y + 1)
        player1.computePolygon()
        println("not collision")
    }

    val collision = getCollision(player1.polygon, player2.polygon)
    if (collision != null) {
        println("has collision")
        println("Penetration Depth: ${collision.depth}")
        println("Collision Normal: ${collision.normal}")

        // 1. Calcular distancia y ángulo entre los centros
        val diff = player2.position - player1.position
        val distance = diff.length()
        val angleRad = atan(diff.y / diff.x)
        val angleDeg = Math.toDegrees(angleRad.toDouble())

        println("Distancia entre centros: $distance")
        println("Ángulo entre centros: $angleDeg grados")

        // 2. Separar los objetos basándose en la penetración
        // Dividimos la profundidad entre 2 para mover cada uno la mitad
        val separationVector = collision.normal.times(collision.depth / 2f)

        player1.position = player1.position - separationVector
        player2.position = player2.position + separationVector

        // 3. Recalcular polígonos tras la separación
        player1.computePolygon()
        player2.computePolygon()
        println("Objetos separados. Nueva colisión: ${hasCollision(player1.polygon, player2.polygon)}")
    }
}

data class CollisionInfo(val depth: Float, val normal: Vector)

fun getCollision(polygon1: Polygon, polygon2: Polygon): CollisionInfo? {
    val vertices1 = polygon1.getVertices()
    val vertices2 = polygon2.getVertices()

    val axes = getAxes(vertices1) + getAxes(vertices2)
    var minOverlap = Float.MAX_VALUE
    var smallestAxis: Vector? = null

    for (axis in axes) {
        val projection1 = project(vertices1, axis)
        val projection2 = project(vertices2, axis)

        if (projection1.first > projection2.second || projection2.first > projection1.second) {
            return null
        } else {
            val overlap = minOf(projection1.second, projection2.second) - maxOf(projection1.first, projection2.first)
            if (overlap < minOverlap) {
                minOverlap = overlap
                smallestAxis = axis
            }
        }
    }

    // Asegurar que la normal apunte de polygon1 a polygon2
    var normal = smallestAxis!!
    val center1 = polygon1.getCenter()
    val center2 = polygon2.getCenter()
    val direction = center2 - center1
    if (direction.dot(normal) < 0) {
        normal = Vector(-normal.x, -normal.y)
    }

    return CollisionInfo(minOverlap, normal)
}

fun hasCollision(polygon1: Polygon, polygon2: Polygon): Boolean = getCollision(polygon1, polygon2) != null

private fun getAxes(vertices: List<Vector>): List<Vector> {
    val axes = mutableListOf<Vector>()
    for (i in vertices.indices) {
        val p1 = vertices[i]
        val p2 = vertices[(i + 1) % vertices.size]
        val edge = Vector(p1.x - p2.x, p1.y - p2.y)
        // El eje es la normal del lado (perpendicular)
        axes.add(Vector(-edge.y, edge.x).normalize())
    }
    return axes
}

private fun project(vertices: List<Vector>, axis: Vector): Pair<Float, Float> {
    var min = Float.MAX_VALUE
    var max = -Float.MAX_VALUE
    for (v in vertices) {
        val dot = v.dot(axis)
        if (dot < min) min = dot
        if (dot > max) max = dot
    }
    return Pair(min, max)
}

data class Vector(val x: Float, val y: Float) {
    operator fun plus(other: Vector): Vector {
        return Vector(x + other.x, y + other.y)
    }

    operator fun minus(other: Vector): Vector {
        return Vector(x - other.x, y - other.y)
    }

    fun times(scalar: Float): Vector {
        return Vector(x * scalar, y * scalar)
    }

    fun dot(other: Vector): Float {
        return x * other.x + y * other.y
    }

    fun length(): Float {
        return sqrt(x * x + y * y)
    }

    fun normalize(): Vector {
        val l = length()
        return if (l != 0f) Vector(x / l, y / l) else Vector(0f, 0f)
    }
}

data class Polygon(
    val vec1: Vector,
    val vec2: Vector,
    val vec3: Vector,
    val vec4: Vector
) {
    fun getVertices() = listOf(vec1, vec2, vec3, vec4)

    fun getCenter(): Vector {
        val vertices = getVertices()
        return Vector(vertices.sumOf { it.x.toDouble() }.toFloat() / 4f, vertices.sumOf { it.y.toDouble() }.toFloat() / 4f)
    }
}

class Entity(var position: Vector, var angle: Float, var polygon: Polygon) {
    fun computePolygon() {
        polygon = Polygon(
            position + polygon.vec1.rotate(angle),
            position + polygon.vec2.rotate(angle),
            position + polygon.vec3.rotate(angle),
            position + polygon.vec4.rotate(angle)
        )
    }

    fun Vector.rotate(angleDegrees: Float): Vector {
        val rad = Math.toRadians(angleDegrees.toDouble())
        val cosTheta = cos(rad)
        val sinTheta = sin(rad)
        return Vector(
            x = (this.x.toDouble() * cosTheta - this.y.toDouble() * sinTheta).toFloat(),
            y = (this.x.toDouble() * sinTheta + this.y.toDouble() * cosTheta).toFloat()
        )
    }
}