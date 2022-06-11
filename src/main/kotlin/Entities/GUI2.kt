package Entities

import Enumerations.EventType
import Interfaces.AttributeFrameSetup
import Interfaces.Command
import Interfaces.IObservable
import Interfaces.RootComponentSetup

import java.awt.*
import java.awt.event.*
import java.io.File
import java.io.FileInputStream
import java.util.*
import javax.swing.*
import javax.swing.border.CompoundBorder
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.typeOf

class ComponentSkeleton2(val c: Controller, private val undoStack: UndoStack, private val attributesFrames: MutableList<AttributeFrameSetup>) : JPanel(), IObservable<ComponentSkeleton2.ComponentEvent> {
    interface ComponentEvent {
        fun addTag(parent: CompositeEntity, newEntity: CompositeEntity) {}
        fun removeTag(parent: CompositeEntity, entity: CompositeEntity) {}
        fun renameTag(entity: CompositeEntity, newTagName: String) {}

        fun addAttribute(entity: CompositeEntity, attName: String, attValue: String) {}
        fun removeAttribute(entity: CompositeEntity, a: Attribute) {}
        fun renameAttribute(entity: CompositeEntity, a: Attribute, newName: String) {}
        fun editValueAttribute(entity: CompositeEntity, a: Attribute, value: String) {}
    }

    override val observers: MutableList<ComponentEvent> = mutableListOf()
    val listComponents: MutableList<Component> = mutableListOf()

    inner class Component(val e: CompositeEntity, private val undoStack: UndoStack): JPanel() {

        var entityName: String = e.name
        var listAttributes: MutableList<JPanel> = mutableListOf()
        val popupmenu = JPopupMenu("Actions")

        override fun paintComponent(g: Graphics) {
            super.paintComponent(g)
            g.font = Font("Arial", Font.BOLD, 16)
            g.drawString(entityName, 10, 20)
        }

        init {
            layout = GridLayout(0, 1)
            border = CompoundBorder(
                BorderFactory.createEmptyBorder(30, 10, 10, 10),
                BorderFactory.createLineBorder(Color.BLACK, 2, true)
            )
            listComponents += this
            createPopupMenu()
            buildAttributes()
            buildChild()
        }

        fun buildChild() {
            this.e.children.forEach {e ->
                addTag(e as CompositeEntity, this.e)
            }
        }

        private fun buildAttributes() {
            if(e.attrs.size != 0) {
                for(a in e.attrs) {
                    var frame = attributesFrames.find { a.name.contains(it.typeAttribute) }
                    if(frame != null) {
                        var panel = frame.getFrame(c, e, a, undoStack)
                        listAttributes += panel
                        add(panel)
                    } else {
                        var panel = JPanel()
                        var label = JLabel(a.name)
                        var textField = JTextField(a.attrValue)
                        textField.addKeyListener(object: KeyAdapter() {
                            override fun keyReleased(k: KeyEvent?) {
                                notifyObservers { it.editValueAttribute(e, a, (k!!.source as JTextField).text) }
                            }
                        })
                        panel.add(label)
                        panel.add(textField)
                        panel.addMouseListener(object: MouseAdapter() {
                            override fun mouseClicked(m: MouseEvent) {
                                if (SwingUtilities.isRightMouseButton(m)) {
                                    val popupmenu = JPopupMenu("Actions")
                                    val renameAttribute = JMenuItem("Rename Attribute ${a.name}")
                                    renameAttribute.addActionListener {
                                        val text = JOptionPane.showInputDialog("New name")
                                        notifyObservers { it.renameAttribute(e, a, text) }
                                    }
                                    popupmenu.add(renameAttribute)

                                    val removeAttribute = JMenuItem("Remove Attribute ${a.name}")
                                    removeAttribute.addActionListener {
                                        notifyObservers { it.removeAttribute(e, a) }
                                    }
                                    popupmenu.add(removeAttribute)

                                    popupmenu.show(m.component, m.x, m.y)
                                    }
                                }
                        })
                        listAttributes += panel
                        add(panel)
                    }
                    }
            }
        }

        private fun createPopupMenu() {
            val addTag = JMenuItem("Add Tag $entityName")
            addTag.addActionListener {
                val text = JOptionPane.showInputDialog("New Tag Name")
                notifyObservers { it.addTag(this.e, CompositeEntity(text)) }
            }
            popupmenu.add(addTag)

            val addAttribute = JMenuItem("Add Attribute $entityName")
            addAttribute.addActionListener {
                val attName = JOptionPane.showInputDialog("Attribute Name")
                val attValue = JOptionPane.showInputDialog("Attribute value")

                notifyObservers { it.addAttribute(this.e, attName, attValue) }
            }
            popupmenu.add(addAttribute)

            val removeTag = JMenuItem("Remove Tag $entityName")
            removeTag.addActionListener {
                notifyObservers {
                    it.removeTag(this.e.parent!!, e)
                }
            }
            popupmenu.add(removeTag)

            val renameTag = JMenuItem("Rename Tag")
            renameTag.addActionListener {
                val text = JOptionPane.showInputDialog("New tag name")
                notifyObservers { it.renameTag(this.e, text) }
            }
            popupmenu.add(renameTag)

            val stack = JMenuItem("Undo")
            stack.addActionListener {
                println(undoStack.stack)
                undoStack.undo()
            }
            popupmenu.add(stack)

            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    if (SwingUtilities.isRightMouseButton(e))
                        popupmenu.show(e.component, e.x, e.y)
                }
            })
        }
    }

    init {
        layout = GridLayout(0,1)
        fun execute(c: Command) {
            undoStack.execute(c)
        }
        this.addObserver(object: ComponentEvent {
            override fun addTag(parent: CompositeEntity, newEntity: CompositeEntity) {
                execute(AddChildCommand(c, newEntity, parent))
            }

            override fun removeTag(parent: CompositeEntity, entity: CompositeEntity) {
                execute(RemoveChildCommand(c, entity, parent))
            }

            override fun renameTag(entity: CompositeEntity, newTagName: String) {
                execute(RenameTagCommand(c, entity.name, newTagName, entity))
            }

            override fun addAttribute(entity: CompositeEntity, attName: String, attValue: String) {
                execute(AddAttributeCommand(c, Attribute(attName, attValue), entity))
            }
            override fun removeAttribute(entity: CompositeEntity, a: Attribute) {
                execute(RemoveAttributeCommand(c, a, entity))
            }
            override fun renameAttribute(entity: CompositeEntity, a: Attribute, newName: String) {
                execute(RenameAttributeNameCommand(c, a, newName, entity))
            }
            override fun editValueAttribute(entity: CompositeEntity, a: Attribute, value: String) {
                execute(RenameAttributeValueCommand(c, a, value, entity))
            }
        })
        c.addObserver { e, p, aux, aux2 ->
            when(e) {
                EventType.ADD_TAG -> addTag(p as CompositeEntity, aux as CompositeEntity)
                EventType.REMOVE_TAG -> removeTag(p as CompositeEntity, aux as CompositeEntity)
                EventType.RENAME_TAG -> renameTag(p as String, aux as String)
                EventType.ADD_ATTRIBUTE -> addAttribute(p as CompositeEntity, aux as Attribute)
                EventType.REMOVE_ATTRIBUTE -> removeAttribute(p as CompositeEntity, aux as Attribute)
                EventType.RENAME_ATTRIBUTE -> renameAttributeName(p as CompositeEntity, aux as String, aux2 as String)
            }
        }

        add(Component(c.data, undoStack))
    }

    private fun renameTag(oldTagName: String, newTagName: String) {
        val jComponent = listComponents.find { it.entityName == oldTagName }
        jComponent!!.entityName = newTagName
        revalidate()
        repaint()
    }


    private fun removeAttribute(t:CompositeEntity, a: Attribute) {
        val jComponent = listComponents.find { it.entityName == t.name }
        val jComponentAttribute = jComponent!!.listAttributes.find { (it.getComponent(0)!! as JLabel).text == a.name }
        jComponent.remove(jComponentAttribute!!)
        jComponent.listAttributes.remove(jComponentAttribute)
        revalidate()
        repaint()
    }

    private fun renameAttributeName(t:CompositeEntity, oldName: String, newName: String) {
        val jComponent = listComponents.find { it.entityName == t.name }
        val jComponentAttribute = jComponent!!.listAttributes.find { (it.getComponent(0)!! as JLabel).text == oldName }
        (jComponentAttribute!!.getComponent(0)!! as JLabel).text = newName
        revalidate()
        repaint()
    }

    private fun addAttribute(t: CompositeEntity, a: Attribute) {
        val jComponent = listComponents.find { it.entityName == t.name }
        var panel = JPanel()
        var label = JLabel(a.name)
        var textField = JTextField(a.attrValue)
        textField.addKeyListener(object: KeyAdapter() {
            override fun keyReleased(e: KeyEvent?) {
                notifyObservers { it.editValueAttribute(t, a, (e!!.source as JTextField).text) }
            }
        })
        panel.add(label)
        panel.add(textField)
        panel.addMouseListener(object: MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    val popupmenu = JPopupMenu("Actions")
                    val renameAttribute = JMenuItem("Rename Attribute ${a.name}")
                    renameAttribute.addActionListener {
                        val text = JOptionPane.showInputDialog("New name")
                        notifyObservers { it.renameAttribute(t, a, text) }
                    }
                    popupmenu.add(renameAttribute)

                    val removeAttribute = JMenuItem("Remove Attribute ${a.name}")
                    removeAttribute.addActionListener {
                        notifyObservers { it.removeAttribute(t, a) }
                    }
                    popupmenu.add(removeAttribute)

                    popupmenu.show(e.component, e.x, e.y)
                }

            }
        })
        jComponent!!.listAttributes += panel
        jComponent.add(panel)
        revalidate()
        repaint()
    }

    private fun removeTag(t: CompositeEntity, parent: CompositeEntity) {
        val jComponentParent = listComponents.find { it.entityName == parent.name }
        val jComponentChild = listComponents.find { it.entityName == t.name }
        jComponentParent!!.remove(jComponentChild)
        listComponents.remove(jComponentChild)
        revalidate()
        repaint()

    }

    fun addTag(t: CompositeEntity, parent: CompositeEntity) {
        val jComponent = listComponents.find { it.entityName == parent.name }
        jComponent!!.add(Component(t, undoStack))
        revalidate()
        repaint()
    }
}

class Window: JFrame("title") {
    @InjectAdd("action")
    private var menuActions = mutableListOf<Action>()//(EventAdd())
    @InjectAdd("attribute")
    private var attributesFrames = mutableListOf<AttributeFrameSetup>()//(DescriptionFrame(), DateFrame(), MandatoryFrame())
    @Inject
    private lateinit var rootEntity: RootComponentSetup
    private lateinit var controller: Controller
    private val undoStack = UndoStack()

    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        size = Dimension(1000, 1000)
    }

    fun open() {
//        val room = CompositeEntity("room", attrs = mutableListOf(Attribute("descrição", "100"), Attribute("sala reservada", "true")))
//        val p1 = CompositeEntity("p1", attrs = mutableListOf(Attribute("Name", "Afonso")) ,parent = room)
//        CompositeEntity("age", attrs = mutableListOf(Attribute("Value", "21")) ,parent=p1)
//        controller = Classes.Controller(room)
        //val button =
        controller = Controller(rootEntity.getRootComponent())
        val xmlButton = JButton("Save XML")
        xmlButton.addActionListener {
            val filename = JOptionPane.showInputDialog("Filename")
            val xml = controller.getXML()
            println(xml)
            File("src/main/kotlin/Output/$filename.txt").writeText(xml)
        }
        xmlButton.setBounds(50, 150, 100, 30)
        add(xmlButton, BorderLayout.NORTH)
        var rootComponent = ComponentSkeleton2(controller, undoStack, attributesFrames)
        add(rootComponent)
        menuActions.forEach {a ->
            rootComponent.listComponents.forEach {c ->
                val item = JMenuItem(a.actionName)
                if(c.e.name == a.parentName) {
                    item.addActionListener { a.execute(controller, c.e, undoStack) }
                } else {
                    item.isEnabled = false
                }
                c.popupmenu.add(item)
            }
        }
        isVisible = true

    }
}

annotation class Inject

annotation class InjectAdd(val name: String)

class Injector {
    fun create(c: KClass<*>): Window {
        val file = File("C:\\Users\\afonso.miao\\IdeaProjects\\Kotlin_Project\\src\\main\\kotlin\\Entities\\di.properties")
        val prop = Properties()
        FileInputStream(file).use { prop.load(it) }
        val obj = c.createInstance()
        val className = "${c.simpleName}."
        var classProperty: String = ""
        c.declaredMemberProperties.filter { it.hasAnnotation<Inject>()}
            .forEach {
                classProperty = prop.getProperty(className + it.name)
                val clazz: KClass<*> = Class.forName(classProperty).kotlin
                it.isAccessible = true
                (it as KMutableProperty<*>).setter.call(obj, clazz.createInstance())
            }

        c.declaredMemberProperties.filter { it.hasAnnotation<InjectAdd>() && it.findAnnotation<InjectAdd>()!!.name == "attribute"}
            .forEach {
                classProperty = prop.getProperty(className + it.name)
                var actions = classProperty.split(",")
                actions.forEach {x ->
                    val clazz: KClass<*> = Class.forName(x).kotlin
                    it.isAccessible = true
                    val currentObj = clazz.createInstance()
                    val lst = (it as KMutableProperty<*>).call(obj) as MutableList<AttributeFrameSetup>
                    lst.add(currentObj as AttributeFrameSetup)
                }
            }
        c.declaredMemberProperties.filter { it.hasAnnotation<InjectAdd>() && it.findAnnotation<InjectAdd>()!!.name == "action"}
            .forEach {
                classProperty = prop.getProperty(className + it.name)
                var actions = classProperty.split(",")
                actions.forEach {x ->
                    val clazz: KClass<*> = Class.forName(x).kotlin
                    it.isAccessible = true
                    val currentObj = clazz.createInstance()
                    val lst = (it as KMutableProperty<*>).call(obj) as MutableList<Action>
                    lst.add(currentObj as Action)
                }
            }
        return obj as Window
    }
}

fun main() {
    val w = Injector().create(Window::class)
    w.open()
}