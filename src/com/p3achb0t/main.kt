package com.p3achb0t

import com.p3achb0t.Main.Data.customCanvas
import com.p3achb0t.Main.Data.mouseEvent
import com.p3achb0t.analyser.DreamBotAnalyzer
import com.p3achb0t.downloader.Downloader
import com.p3achb0t.downloader.Parameters
import com.p3achb0t.interfaces.PaintListener
import com.p3achb0t.rsclasses.Client
import com.p3achb0t.rsclasses.Widget
import com.p3achb0t.widgetexplorer.WidgetExplorer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tornadofx.App
import tornadofx.launch
import java.applet.Applet
import java.applet.AppletContext
import java.applet.AppletStub
import java.awt.*
import java.awt.event.ActionListener
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionListener
import java.awt.image.BufferedImage
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.util.jar.JarFile
import javax.swing.JFrame
import javax.swing.WindowConstants

class MyApp : App(WidgetExplorer::class)

class RSLoader:AppletStub {
    var params = Parameters.data.PARAMETER_MAP
    override fun getCodeBase(): URL {
        return URL(params["codebase"])
    }

    override fun getParameter(name: String?): String {
        println("Getting $name : ${params[name]}")
        return params[name]!!
    }

    override fun getAppletContext(): AppletContext = null!!

    override fun appletResize(width: Int, height: Int) {
    }

    override fun getDocumentBase(): URL {
        return URL(params["codebase"])
    }

    override fun isActive(): Boolean {
        return true
    }
}

class CustomApplet : Applet()

class CustomCanvas(var oldCanvasHash: Int) : Canvas() {

    var counter = 0

    val image = BufferedImage(765, 503, BufferedImage.TYPE_INT_RGB)
    @Transient
    var paintListener: PaintListener? = null

    fun addPaintListener(listener: ActionListener) {
        this.paintListener = AWTEventMulticaster.add(this.paintListener, listener) as PaintListener
    }
    override fun getGraphics(): Graphics {
        val g = image.graphics
        g.color = Color.GREEN

//        g.drawString("RS-HAcking $counter", 50,50)
//        g.drawRect(100,100,100,100)
        paintListener?.onPaint(g)


        if (Main.selectedWidget != null) {
            if (Main.selectedWidget!!.type == "2") {
                val retcs = Main.selectedWidget?.getItemsRects()
                retcs?.iterator()?.forEach { rect ->
                    g.drawRect(rect.x, rect.y, rect.width, rect.height)
                }
            } else {
                val rect = Main.selectedWidget?.getDrawableRect()!!
                g.drawRect(rect.x, rect.y, rect.width, rect.height)
            }
        }

        super.getGraphics().drawImage(image, 0, 0, null)
        counter += 1

        return g
    }

    override fun hashCode(): Int {
        return oldCanvasHash
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CustomCanvas

        if (oldCanvasHash != other.oldCanvasHash) return false
        if (counter != other.counter) return false
        if (image != other.image) return false

        return true
    }

}

class Main(game: Applet) {
    init {
        client = game
    }

    companion object Data {
        var client: Applet? = null
        var dream: DreamBotAnalyzer? = null
        var classLoader: ClassLoader? = null
        var selectedWidget: Widget? = null
        var customCanvas: CustomCanvas? = null
        var mouseEvent: MouseEvent? = null
    }
}

fun main(args: Array<String>){

    val downloader = Downloader()
//    val gamePackWithPath = downloader.getGamepack()
    val gamePackWithPath = downloader.getLocalGamepack()
//    val analyser = Analyser()
    val gamePackJar = JarFile(gamePackWithPath)
//    analyser.parseJar(gamePackJar)
    val dream = DreamBotAnalyzer()

    dream.getDreamBotHooks()
    dream.parseHooks()
//    dream.createAccessorFieldsForClass()

    dream.parseJar(gamePackJar)


    // Getting parameters
    Parameters(1)
    println("Starting Client")
    val file = File(gamePackWithPath)
    val urlArray: Array<URL> = Array(1, init = { file.toURI().toURL() })
    Main.classLoader = URLClassLoader(urlArray)
    val game: Applet = Main.classLoader?.loadClass("client")?.newInstance() as Applet
    Main(game)
    Main.dream = dream

    val client = game::class.java


    game.apply {
        preferredSize = Dimension(765, 503)
        val loader = RSLoader()
        game.setStub(loader)
    }
    val jFrame = JFrame()
    jFrame.title = "Runescape"
//    val jMenuBar = JMenuBar()
//    val jmenu = JMenu()
//    val jItem = JMenuItem("size")
//    jmenu.add(jItem)
//    jMenuBar.add(jmenu)
    jFrame.apply {

        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
//        add(jMenuBar)
        add(game)

        pack()
        preferredSize = size
        minimumSize = game.minimumSize
        setLocationRelativeTo(null)
        isVisible = true
    }

    game.apply {
        init()
        start()

        println(client.name)
        val currentWorldObs = dream.analyzers[Client::class.java.simpleName]?.fields?.get("currentWorld")?.obsName
        val modifer = dream.analyzers[Client::class.java.simpleName]?.fields?.get("currentWorld")?.modifier?.toInt()
        println(modifer)

        dream.classRefObs["client"]?.fields?.get("currentWorld")?.obsName
        val worldField = Main.client!!::class.java.getDeclaredField(currentWorldObs)
        worldField.isAccessible = true

        val worldNum = worldField.getInt(game) * modifer!!
        println("currentWorld :$worldNum")

    }

    game.apply {

        val canvasType = dream.analyzers[Client::class.java.simpleName]?.fields?.get("canvas")?.fieldTypeObsName
        val canvasFieldName = dream.analyzers[Client::class.java.simpleName]?.fields?.get("canvas")?.obsName

        println("canvas " + canvasType + " Field parentId : $canvasFieldName")
        var loaded = false
        //Wait till canvas is not null so we can replace it
        while (!loaded) {
            try {

                val canvasClazz = Main.classLoader?.loadClass(canvasType)
                val canvasField = canvasClazz?.getDeclaredField(canvasFieldName)
                canvasField?.isAccessible = true
                val oldCanvas = canvasField?.get(game) as Canvas // Needed to have Applet instead of null
                game.remove(oldCanvas)
                customCanvas = CustomCanvas(oldCanvas.hashCode())
                for (ml in oldCanvas.mouseListeners) {
                    customCanvas?.addMouseListener(ml)
                }
                for (ml in oldCanvas.mouseMotionListeners) {

                    customCanvas?.addMouseMotionListener(ml)
                }
                val mouseListener = object : MouseMotionListener {
                    override fun mouseMoved(e: MouseEvent?) {
                        mouseEvent = e

                    }

                    override fun mouseDragged(e: MouseEvent?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                }
                customCanvas?.addPaintListener(object : PaintListener {
                    override fun onPaint(g: Graphics) {
                        g.color = Color.white
                        g.drawString("Mouse x:${mouseEvent?.x} y:${mouseEvent?.y}", 50, 50)
                        mouseEvent?.x?.let { mouseEvent?.y?.let { it1 -> g.drawRect(it, it1, 5, 5) } }
                    }

                })
                customCanvas?.addMouseMotionListener(mouseListener)
                for (kl in oldCanvas.keyListeners) {
                    customCanvas?.addKeyListener(kl)
                }
                for (fl in oldCanvas.focusListeners) {
                    customCanvas?.addFocusListener(fl)
                }
                canvasField.set(game, customCanvas) // Needed to have Applet instead of null
                game.add(customCanvas)
                loaded = true


                var x = 0
                var y = 0
                GlobalScope.launch {
                    repeat(1000) {
                        x += 10
                        if (x == 400) x = 0
                        y += 5
                        if (y == 400) y = 0

                        val me = MouseEvent(
                            customCanvas,
                            MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, x, y, 0, false
                        )
                        customCanvas?.dispatchEvent(me)
                        delay(400)
                    }
                }

                break

            } catch (e: Exception) {
                println("Error hooking canvas $e ")
                for (item in e.stackTrace) {
                    println(item.toString())
                }
            }
        }
    }

    launch<MyApp>(args)



}