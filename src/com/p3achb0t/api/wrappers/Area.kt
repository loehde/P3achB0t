package com.p3achb0t.api.wrappers

import com.p3achb0t.api.Context
import com.p3achb0t.api.wrappers.interfaces.Locatable
import java.awt.Point
import java.awt.Polygon
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.random.Random


//Ripped this from Powerbot here: https://github.com/powerbot/powerbot/blob/a5986a7eaaed0b07d952f14d637592a4c548630c/src/main/java/org/powerbot/script/Area.java

class Area {
    var tiles = arrayListOf<Tile>()
    var polygon = Polygon()
    var plane = 0
    var ctx: Context?

    constructor(t1: Tile, t2: Tile, ctx: Context?) {
        this.ctx = ctx
        Area(
            Tile(min(t1.x, t2.x), min(t1.y, t2.y), t1.z),
            Tile(max(t1.x, t2.x), min(t1.y, t2.y), t1.z ),
            Tile(max(t1.x, t2.x), max(t1.y, t2.y), t2.z),
            Tile(min(t1.x, t2.x), max(t1.y, t2.y), t2.z)
        )
    }

    constructor(vararg tiles: Tile, ctx: Context?=null) {
        this.plane = tiles[0].z
        this.ctx = ctx
        //Create a polygon from the files
        for (tile in tiles) {
            polygon.addPoint(tile.x + 1, tile.y + 1)
        }


        //Convert the polygon to all tiles that would be associated with this area
        val r = polygon.bounds
        var c = 0
        val lTiles = Array(r.width * r.height) { _ -> Tile() }
        for (x in 0 until r.width) {
            for (y in 0 until r.height) {
                val _x = r.x + x
                val _y = r.y + y
                if (polygon.contains(_x, _y)) {
                    lTiles[c++] = Tile(_x, _y, plane)
                }
            }
        }

        val t = lTiles.clone()
        this.tiles.clear()
        t.iterator().forEach { this.tiles.add(it) }
    }

    /**
     * PolygonUtils
     * http://www.shodor.org/~jmorrell/interactivate/org/shodor/util11/PolygonUtils.java
     */
    private object PolygonUtils {
        /**
         * Finds the centroid of a polygon with integer verticies.
         *
         * @param pg The polygon to find the centroid of.
         * @return The centroid of the polygon.
         */

        fun getCenter(pg: Polygon): Point {

            val N = pg.npoints
            val polygon = Array(N) { _ -> Point() }

            for (q in 0 until N) {
                polygon[q] = Point(pg.xpoints[q], pg.ypoints[q])
            }

            var cx = 0.0
            var cy = 0.0
            val A = getArea(polygon, N)
            var i: Int = 0
            var j: Int

            var factor: Double
            while (i < N) {
                j = ((i + 1) % N)
                factor = ((polygon[i].x) * polygon[j].y - (polygon[j].x) * (polygon[i].y)).toDouble()
                cx += ((polygon[i].x) + (polygon[j].x)) * factor
                cy += ((polygon[i].y) + (polygon[j].y)) * factor
                i++
            }
            factor = 1.0 / (6.0 * A)
            cx *= factor
            cy *= factor
            return Point(abs(cx.roundToInt()), abs(cy.roundToInt()))
        }

        /**
         * Computes the area of any two-dimensional polygon.
         *
         * @param polygon The polygon to compute the area of input as an array of points
         * @param N       The number of points the polygon has, first and last point
         * inclusive.
         * @return The area of the polygon.
         */
        fun getArea(polygon: Array<Point>, N: Int): Double {
            var i: Int = 0
            var j: Int
            var area = 0.0

            while (i < N) {
                j = (i + 1) % N
                area += polygon[i].x * polygon[j].y
                area -= polygon[i].y * polygon[j].x
                i++
            }

            area /= 2.0
            return abs(area)
        }
    }

    fun isPlayerInArea(): Boolean{
        return ctx?.players?.getLocal()?.getGlobalLocation()?.let { this.containsOrIntersects(it) } ?: false
    }

    fun contains(vararg locatables: Locatable): Boolean {
        for (locatable in locatables) {
            val tile = locatable.getGlobalLocation()
            if (tile.z != plane || !polygon.contains(tile.x, tile.y)) {
                return false
            }
        }
        return true
    }

    fun containsOrIntersects(vararg locatables: Locatable): Boolean {
        for (locatable in locatables) {
            val tile = locatable.getGlobalLocation()
            if (tile.z != plane || !polygon.contains(tile.x, tile.y) && !polygon.intersects(
                            tile.x + 0.5,
                            tile.y + 0.5,
                            1.0,
                            1.0
                    )
            ) {
                return false
            }
        }
        return true
    }

    fun getCentralTile(): Tile {
        val point = PolygonUtils.getCenter(polygon)
        return Tile(point.x, point.y, plane)
    }

    fun getRandomTile(): Tile {
        val len = tiles.size
        return if (len != 0) this.tiles[Random.nextInt(0, len)] else Tile()
    }

    fun getClosestTo(locatable: Locatable?): Tile {
        val t = if (locatable != null) locatable.getGlobalLocation() else Tile.NIL
        if (t != Tile.NIL) {
            var dist = java.lang.Double.POSITIVE_INFINITY
            var tile = Tile.NIL
            for (item in tiles) {
                val d = t.distanceTo(item).toDouble()
                if (d < dist) {
                    dist = d
                    tile = item
                }
            }
            return tile
        }
        return Tile.NIL
    }

}

