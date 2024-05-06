package com.mooo.koziol.mkb2.data.minimax

import com.mooo.koziol.mkb2.data.Hold

data class Graph(
    val edges: Collection<Edge>
) {
    fun getNodes(): Collection<Hold> {
        return edges.flatMap { setOf(it.first, it.second) }.distinct()
    }

    fun getEdgedForNode(hold: Hold): Collection<Edge> {
        return edges.filter { it.first == hold ||it.second == hold }
    }
}
