package com.p3achb0t.api

import com.p3achb0t._runestar_interfaces.Client
import com.p3achb0t._runestar_interfaces.Component
import com.p3achb0t.api.user_inputs.Camera
import com.p3achb0t.api.user_inputs.Keyboard
import com.p3achb0t.api.user_inputs.Mouse
import com.p3achb0t.api.wrappers.*
import com.p3achb0t.api.wrappers.tabs.*
import com.p3achb0t.api.wrappers.widgets.Widgets
import java.applet.Applet

class Context(val obj: Any) {


    val applet: Applet
    var selectedWidget: Component? = null

    val client: Client
    val players: Players
    val groundItems: GroundItems
    val npcs: NPCs
    val mouse: Mouse
    val keyboard: Keyboard
    val camera: Camera
    val bank: Bank
    val clientMode: ClientMode
    val dialog: Dialog
    val gameObjects: GameObjects
    val items: Items
    val menu: Menu
    val miniMap: MiniMap
    val equipment: Equipment
    val inventory: Inventory
    val magic: Magic
    val prayer: Prayer
    val tabs: Tabs
    val widgets: Widgets
    val vars: Vars

    init {
        client = obj as Client
        mouse = Mouse(obj)
        keyboard = Keyboard(obj)
        applet = obj as Applet
        camera = Camera(this)
        npcs = NPCs(this)
        players = Players(this)
        groundItems = GroundItems(this)
        bank = Bank(this)
        clientMode = ClientMode(this)
        dialog = Dialog(this)
        gameObjects = GameObjects(this)
        items = Items(this.client)
        menu = Menu(this.client)
        miniMap = MiniMap(this)
        equipment = Equipment(this)
        inventory = Inventory(this)
        magic = Magic(this)
        prayer = Prayer(this)
        tabs = Tabs(this)
        widgets = Widgets(this)
        vars = Vars(this)
    }
}

