package com.fmz.compox2d.physics

import com.fmz.compox2d.engine.core.GameObject

data class PhysicsContactPairKey(
    val minId: Int,
    val maxId: Int,
    val ownerA: GameObject,
    val ownerB: GameObject,
) {
    fun includes(owner: GameObject): Boolean = ownerA == owner || ownerB == owner

    fun includes(target: PhysicsContactTarget): Boolean = minId == target.id || maxId == target.id

    companion object {
        fun from(a: PhysicsContactTarget, b: PhysicsContactTarget): PhysicsContactPairKey {
            return if (a.id <= b.id) {
                PhysicsContactPairKey(a.id, b.id, a.owner, b.owner)
            } else {
                PhysicsContactPairKey(b.id, a.id, b.owner, a.owner)
            }
        }
    }
}