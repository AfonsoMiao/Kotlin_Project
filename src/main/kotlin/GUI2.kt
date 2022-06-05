import AddAttributeCommand
import Classes.*
import Classes.UndoStack
import Enumerations.EventType
import Interfaces.Command
import Interfaces.IObservable
import javafx.scene.Parent

import java.awt.*
import java.awt.event.*
import javax.swing.*
import javax.swing.border.CompoundBorder
import javax.swing.text.View

class ComponentSkeleton2(val c: Controller, val undoStack: UndoStack) : JPanel(), IObservable<ComponentSkeleton2.ComponentEvent> {
    interface ComponentEvent {
        fun addTag(parent: CompositeEntity, newTagName: String) {}
        fun removeTag(parent: CompositeEntity, removeTagName: String) {}
        fun renameTag(entity: CompositeEntity, newTagName: String) {}

        fun addAttribute(entity: CompositeEntity, attName: String, attValue: String) {}
        fun removeAttribute(entity: CompositeEntity, a: Attribute) {}
        fun renameAttribute(entity: CompositeEntity, a: Attribute, newName: String) {}
        fun editValueAttribute(entity: CompositeEntity, a: Attribute, value: String) {}
    }

    override val observers: MutableList<ComponentEvent> = mutableListOf()
    private var listComponents: MutableList<Component> = mutableListOf()

    inner class Component(val e: CompositeEntity, val undoStack: UndoStack): JPanel() {

        var entityName: String = e.name
        var listAttributes: MutableList<JPanel> = mutableListOf()

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
            createPopupMenu()
            buildAttributes()
        }

        private fun buildAttributes() {
//            if(listAttributes.size != 0) {
//                for(a in listAttributes) {
//                    var panel = JPanel()
//                    var label = JLabel(a.name)
//                    var textField = JTextField(a.attrValue)
//                    //textField.addKeyListener(object: KeyAdapter() {
//                    //    override fun keyReleased(e: KeyEvent?) {
//                    //        notifyObservers { it.editValueAttribute(c, a, (e!!.source as JTextField).getText(), data) }
//                    //    }
//                    //})
//                    panel.add(label)
//                    panel.add(textField)
//                    panel.addMouseListener(object: MouseAdapter() {
//                        override fun mouseClicked(e: MouseEvent) {
//                            val popupmenu = JPopupMenu("Actions")
//                            val renameAttribute = JMenuItem("Rename Attribute ${a.name}")
//                            //renameAttribute.addActionListener {
//                            //    val text = JOptionPane.showInputDialog("New name")
//                            //    notifyObservers { it.renameAttribute(c, a.name, text, data) }
//                            //}
//                            popupmenu.add(renameAttribute)
//
//                            val removeAttribute = JMenuItem("Remove Attribute ${a.name}")
////                            removeAttribute.addActionListener {
////                                notifyObservers { it.removeAttribute(c, a, data) }
////                            }
//                            popupmenu.add(removeAttribute)
//
//                            popupmenu.show(e.component, e.x, e.y)
//                        }
//                    })
//                    //attributes += panel
//                    add(panel)
//                }
//            }
        }

        private fun createPopupMenu() {
            val popupmenu = JPopupMenu("Actions")
            val addTag = JMenuItem("Add Tag $entityName")
            addTag.addActionListener {
                val text = JOptionPane.showInputDialog("New Tag Name")
                notifyObservers { it.addTag(this.e, text) }
            }
            popupmenu.add(addTag)

            val addAttribute = JMenuItem("Add Attribute $entityName")
            addAttribute.addActionListener {
                val attName = JOptionPane.showInputDialog("Attribute Name")
                val attValue = JOptionPane.showInputDialog("Attribute value")

                notifyObservers { it.addAttribute(this.e, attName, attValue) }
            }
            popupmenu.add(addAttribute)
//
            val removeTag = JMenuItem("Remove Tag ${entityName}")
            removeTag.addActionListener {
                notifyObservers {
                    it.removeTag(this.e.parent!!, entityName)
                }
            }
            popupmenu.add(removeTag)

            val renameTag = JMenuItem("Rename Tag")
            renameTag.addActionListener {
                val text = JOptionPane.showInputDialog("New tag name")
                notifyObservers { it.renameTag(this.e, text) }
            }
            popupmenu.add(renameTag)
//
//            val printCurrentEntity = JMenuItem("Print Tag")
//            printCurrentEntity.addActionListener {
//                println(data.print())
//            }
//            popupmenu.add(printCurrentEntity)
//
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
        val rootComponent = Component(c.data, undoStack)
        this.listComponents += rootComponent
        add(rootComponent)
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
                notifyObservers { it.editValueAttribute(t, a, (e!!.source as JTextField).getText()) }
            }
        })
        panel.add(label)
        panel.add(textField)
        panel.addMouseListener(object: MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
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

    private fun addTag(t: CompositeEntity, parent: CompositeEntity) {
        val jComponent = listComponents.find { it.entityName == parent.name }
        val newComponent = Component(t, undoStack)
        listComponents += newComponent
        jComponent!!.add(newComponent)
        revalidate()
        repaint()
    }
}

class WindowSkeleton2: JFrame("title") {
    init {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        size = Dimension(500, 500)
        val room = CompositeEntity("room", attrs = mutableListOf(Attribute("ClassName", "C0.96")))
        val controller = Controller(room)
        val undoStack = UndoStack()
        fun execute(c: Command) {
            undoStack.execute(c)
        }
        val skeleton = ComponentSkeleton2(controller, undoStack)
        skeleton.addObserver(object: ComponentSkeleton2.ComponentEvent {
            override fun addTag(parent: CompositeEntity, newTagName: String) {
                execute(AddChildCommand(controller, newTagName, parent))
            }

            override fun removeTag(parent: CompositeEntity, removeTagName: String) {
                execute(RemoveChildCommand(controller, removeTagName, parent))
            }

            override fun renameTag(entity: CompositeEntity, newTagName: String) {
                execute(RenameTagCommand(controller, entity.name, newTagName, entity))
            }

            override fun addAttribute(entity: CompositeEntity, attName: String, attValue: String) {
                execute(AddAttributeCommand(controller, Attribute(attName, attValue), entity))
            }
            override fun removeAttribute(entity: CompositeEntity, a: Attribute) {
                execute(RemoveAttributeCommand(controller, a, entity))
            }
            override fun renameAttribute(entity: CompositeEntity, a: Attribute, newName: String) {
                execute(RenameAttributeNameCommand(controller, a, newName, entity))
            }
            override fun editValueAttribute(entity: CompositeEntity, a: Attribute, value: String) {
                execute(RenameAttributeValueCommand(controller, a, value, entity))
            }
        })
    add(skeleton)
    }

    fun open() {
        isVisible = true
    }
}


fun main() {
    val w = WindowSkeleton2()
    w.open()
}