package com.fmz.compox2d.engine.core

class SceneEntityManager {

    val activeInstances = mutableListOf<GameObject>()
    private val pendingAdditions = mutableListOf<GameObject>()
    private val pendingRemovals = mutableListOf<GameObject>()

    fun enqueueAdd(instance: GameObject) {
        pendingAdditions += instance
    }

    fun enqueueRemove(instance: GameObject) {
        pendingRemovals += instance
    }

    fun enqueueRemoveWhere(predicate: (GameObject) -> Boolean) {
        pendingRemovals += activeInstances.filter(predicate)
    }

    fun sync(
        onRemoved: (GameObject) -> Unit,
        onAdded: (GameObject) -> Unit
    ) {
        while (pendingRemovals.isNotEmpty() || pendingAdditions.isNotEmpty()) {
            if (pendingRemovals.isNotEmpty()) {
                val toRemove = pendingRemovals.toList()
                pendingRemovals.clear()
                toRemove.forEach { instance ->
                    activeInstances -= instance
                    onRemoved(instance)
                }
            }

            if (pendingAdditions.isNotEmpty()) {
                val toAdd = pendingAdditions.toList()
                pendingAdditions.clear()
                toAdd.forEach { instance ->
                    activeInstances += instance
                    onAdded(instance)
                }
            }
        }
    }

    inline fun forEach(action: (GameObject) -> Unit) {
        activeInstances.forEach(action)
    }
}
