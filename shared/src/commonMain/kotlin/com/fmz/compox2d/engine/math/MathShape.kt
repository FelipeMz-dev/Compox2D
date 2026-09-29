package com.fmz.compox2d.engine.math

import com.fmz.compox2d.physics.Shape

object MathShape {
    fun scale(shape: Shape, scale: Vec2): Shape {
        return when (shape) {
            is Shape.BoxShape -> Shape.BoxShape(
                Vec2(
                    shape.size.x * scale.x,
                    shape.size.y * scale.y
                )
            )

            is Shape.EllipseShape -> Shape.EllipseShape(
                Vec2(
                    shape.radius.x * scale.x,
                    shape.radius.y * scale.y
                )
            )

            is Shape.CapsuleShape -> Shape.CapsuleShape(
                Vec2(
                    shape.size.x * scale.x,
                    shape.size.y * scale.y
                )
            )

            is Shape.SliceShape -> Shape.SliceShape(shape.radius * scale.x, shape.theta)
            is Shape.PolygonShape -> Shape.PolygonShape(shape.vertices.map {
                Vec2(
                    it.x * scale.x,
                    it.y * scale.y
                )
            })

            is Shape.SegmentShape -> Shape.SegmentShape(
                Vec2(
                    shape.start.x * scale.x,
                    shape.start.y * scale.y
                ), Vec2(shape.end.x * scale.x, shape.end.y * scale.y)
            )

            is Shape.CircleShape -> {
                val avgScale = (scale.x + scale.y) / 2f
                Shape.CircleShape(shape.radius * avgScale)
            }
        }
    }
}