package com.p3achb0t.api.wrappers

import com.p3achb0t._runestar_interfaces.EvictingDualNodeHashTable
import com.p3achb0t._runestar_interfaces.Model
import com.p3achb0t.api.*
import com.p3achb0t.api.wrappers.interfaces.Interactable
import com.p3achb0t.api.wrappers.interfaces.Locatable
import com.p3achb0t.api.wrappers.tabs.Inventory
import kotlinx.coroutines.delay
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Point
import java.awt.Polygon
import java.util.*

class GroundItem(client: com.p3achb0t._runestar_interfaces.Client, val id: Int, val position: ObjectPositionInfo, val stackSize: Int = 0, override var loc_client: com.p3achb0t._runestar_interfaces.Client? = client) : Interactable(client),
    Locatable {
    override fun getNamePoint(): Point {
        val region = getRegionalLocation()
        return client?.let { Calculations.worldToScreen(region.x, region.y, client?.getPlane(), it) } ?: Point()
    }
    override fun isMouseOverObj(): Boolean {
        //val mousePoint = Point(MainApplet.mouseEvent?.x ?: -1,MainApplet.mouseEvent?.y ?: -1)
        return true //getConvexHull().contains(mousePoint)
    }
    override suspend fun clickOnMiniMap(): Boolean {
        return true//client?.let { Calculations.worldToMiniMap(position.x, position.y, it) }?.let { MainApplet.mouse.click(it) } ?: false
    }

    override fun getInteractPoint(): Point {
        return getRandomPoint(getConvexHull())
    }

    override fun draw(g: Graphics2D) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun draw(g: Graphics2D, color: Color) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getGlobalLocation(): Tile {
        return Tile(
            position.x / 128 + client?.getBaseX()!!,
            position.y / 128 + client?.getBaseY()!!,
            position.plane
        )
    }


    override fun isOnScreen(): Boolean {
        return client?.let { Calculations.isOnscreen(it,getConvexHull().bounds ) } ?: false
    }

    suspend fun take() {
        val inventoryCount = client?.let { Inventory(it).getCount() }
        if (interact("Take")) {
            Utils.waitFor(2, object : Utils.Condition {

                override suspend fun accept(): Boolean {
                    delay(100)
                    println("Waiting for inventory to change $inventoryCount == ${client?.let { Inventory(it).getCount() }}")
                    return inventoryCount != client?.let { Inventory(it).getCount() }
                }
            })
        }
    }

    fun getTriangles(): ArrayList<Polygon> {
        val groundItemModels = client?.getObjType_cachedModels()
        val model: Model? = groundItemModels?.let { getModel(it) }
        return if(model != null && client != null) {
            getTrianglesFromModel(position, model, client)
        }else{
            ArrayList()
        }
    }

    private fun getModel(
        groundItemModels: EvictingDualNodeHashTable
    ): Model? {
        var model1: Model? = null
        groundItemModels.getHashTable().getBuckets().iterator().forEach {
            if (it != null) {
                var next = it.getNext()
                while (next.getKey() > 0 && next is Model) {
                    try {
                        if (next.getKey().toInt() == this.id) {
                            model1 = next
                            break
                        }
                        next = next.getNext()
                    } catch (e: Exception) {
                        println(e.stackTrace)
                    }
                }
            }
        }
        return model1
    }

    fun getConvexHull(): Polygon {
        val groundItemModels = client?.getObjType_cachedModels()
        val model: Model? = groundItemModels?.let { getModel(it) }
        return if(model != null && client != null) {
            getConvexHullFromModel(position, model,client )
        }else{
            Polygon()
        }
    }

}