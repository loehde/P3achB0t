package com.p3achb0t.scripts.paint_debug

import com.p3achb0t.api.Context
import java.awt.Color
import java.awt.Graphics

fun groundItemsPaint(g: Graphics, ctx: Context) {
    try {
        val groundItems = ctx.groundItems.getAllItems()

        groundItems.forEach {

            if (it.isOnScreen()) {

                val namePoint = it.getNamePoint()
                g.drawString(it.id.toString(), namePoint.x, namePoint.y)

                val triangles = it.getTriangles()
                g.color = Color.CYAN
                triangles.forEach { poly ->
                    g.drawPolygon(poly)
                }

                val ch1 = it.getConvexHull()
                g.color = Color.RED
                g.drawPolygon(ch1)
            }
        }
    } catch (e: Exception) {
        println("Error: GroundItems Paint " + e.message)
        e.stackTrace.iterator().forEach {
            println(it)
        }
    }
}