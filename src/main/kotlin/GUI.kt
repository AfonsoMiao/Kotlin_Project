//import AddAttributeCommand
//import Classes.*
//import Enumerations.EventType
//import Interfaces.Command
//import Interfaces.IObservable
//import javafx.scene.Parent
//
//import java.awt.*
//import java.awt.event.*
//import javax.swing.*
//import javax.swing.border.CompoundBorder
//import javax.swing.text.View
//
////TODO
//// Adding parent view for undostack:
//// Passing parent view for child
//// Create a new parameter to know if it's undo or not --> adding parameter in controller functions to know if it's undo or not
//class ComponentSkeleton2(val c: Controller?, val data: CompositeEntity, val undoStack: UndoStack, val parentView: JPanel?) : JPanel(), IObservable<ComponentSkeleton2.ComponentEvent>{
//
//    interface ComponentEvent {
//
//        // Tag functions
//        fun addTag(newTagName: String, parent: CompositeEntity) {}
//        fun removeTag(tagName: String, parent: CompositeEntity) {}
//        fun renameTag(oldName: String, newName: String, entity: CompositeEntity) {}
//
//        // Attribute functions
//        fun addAttribute(attName: String, attValue: String, entity: CompositeEntity) {}
//        fun removeAttribute(a: Attribute, entity: CompositeEntity) {}
//        fun renameAttribute(oldName: String, newName: String, entity: CompositeEntity) {}
//        fun editValueAttribute(a: Attribute, value: String, entity: CompositeEntity) {}
//
//
//    }
//
//    override val observers: MutableList<ComponentEvent> = mutableListOf()
//    private val attributes: MutableList<JPanel> = mutableListOf()
//    var entityName:String = ""
//
//    override fun paintComponent(g: Graphics) {
//        super.paintComponent(g)
//        g.font = Font("Arial", Font.BOLD, 16)
//        g.drawString(entityName, 10, 20)
//    }
//
//    init {
//        layout = GridLayout(0, 1)
//        border = CompoundBorder(
//            BorderFactory.createEmptyBorder(30, 10, 10, 10),
//            BorderFactory.createLineBorder(Color.BLACK, 2, true)
//        )
//        println("Building window ${data.name}")
//        entityName = data.name
//        buildAttributes()
//        createPopupMenu()
//        for (children in data.children) {
//            println("Building child ${children.name}")
//            addChild(children as CompositeEntity, false)
//        }
//        c!!.addObserver { e, p, aux, undo ->
//            when(e) {
//                EventType.RENAME_ATTRIBUTE -> renameAttribute(p as String, aux!! as String)
//                EventType.RENAME_TAG -> renameTag(p as String)
//                EventType.ADD_TAG -> addChild(p as CompositeEntity, undo)
//                EventType.ADD_ATTRIBUTE -> addAttribute(p as Attribute)
//                EventType.REMOVE_ATTRIBUTE -> removeAttribute(p as Attribute)
//                EventType.REMOVE_TAG -> removeTag()
//            }
//        }
//    }
//
//    private fun removeTag() {
//        println("Deleting this view: ${data.name}")
//        remove(this)
//        revalidate()
//        repaint()
//    }
//
//    private fun removeAttribute(att: Attribute) {
//        if(attributes.size == 0) {
//            println("Attributes 0")
//        }
//        val panelRemove = attributes.find { (it.getComponent(0)!! as JLabel).text == att.name }
//        for (m in panelRemove!!.mouseListeners) {
//            panelRemove!!.removeMouseListener(m)
//        }
//        panelRemove!!.removeAll()
//        attributes.remove(panelRemove)
//        revalidate()
//        repaint()
//    }
//
//    private fun addAttribute(att: Attribute) {
//        var panel = JPanel()
//        var label = JLabel(att.name)
//        var textField = JTextField(att.attrValue)
//        textField.addKeyListener(object: KeyAdapter() {
//            override fun keyReleased(e: KeyEvent?) {
//                notifyObservers { it.editValueAttribute(att, (e!!.source as JTextField).getText(), data) }
//            }
//        })
//        panel.add(label)
//        panel.add(textField)
//        panel.addMouseListener(object: MouseAdapter() {
//            override fun mouseClicked(e: MouseEvent) {
//                val popupmenu = JPopupMenu("Actions")
//                val renameAttribute = JMenuItem("Rename Attribute ${att.name}")
//                renameAttribute.addActionListener {
//                    val text = JOptionPane.showInputDialog("New name")
//                    notifyObservers { it.renameAttribute(att.name, text, data) }
//                }
//                popupmenu.add(renameAttribute)
//
//                val removeAttribute = JMenuItem("Remove Attribute ${att.name}")
//                removeAttribute.addActionListener {
//                    notifyObservers { it.removeAttribute(att, data) }
//                }
//                popupmenu.add(removeAttribute)
//
//                popupmenu.show(e.component, e.x, e.y)
//            }
//        })
//        attributes += panel
//        add(panel, 0)
//        revalidate()
//        repaint()
//    }
//    private fun renameAttribute(oldName: String, newName: String) {
//        (this.attributes.find { (it.getComponent(0)!! as JLabel).text == oldName}!!.getComponent(0) as JLabel).text = newName
//        revalidate()
//        repaint()
//    }
//
//
//    private fun renameTag(newName: String) {
//        entityName = newName
//        revalidate()
//        repaint()
//    }
//
//    private fun addChild(c: CompositeEntity, undo: Boolean) {
//        fun execute(c: Command) {
//            undoStack.execute(c)
//        }
//        c.print()
//        val window = ComponentSkeleton2(null, c, undoStack, this)
//        window.addObserver(object : ComponentEvent {
//            override fun renameAttribute(oldName: String, newName: String, entity: CompositeEntity) {
//                execute(RenameAttributeNameCommand(oldName, newName,entity))
//                //c.renameAttribute(oldName, newName)
//            }
//            //renameTag
//            override fun renameTag(oldName: String, newName: String, entity: CompositeEntity) {
//                execute(RenameTagCommand(oldName, newName, entity))
//                //c.renameTag(oldName, newName)
//            }
//
//            override fun addTag(newTagName: String, parent: CompositeEntity) {
//                execute(AddChildCommand(newTagName, parent))
//                //c.addChild(newTagName)
//            }
//
//            override fun addAttribute(attName: String, attValue: String, entity: CompositeEntity) {
//                execute(AddAttributeCommand(Attribute(attName, attValue), entity))
//                //c.addAttribute(Attribute(attName, attValue))
//            }
//
//            override fun removeAttribute(a: Attribute, entity: CompositeEntity) {
//                execute(RemoveAttributeCommand(a, entity))
//                //c.removeAttribute(a)
//            }
//
//            override fun editValueAttribute(a: Attribute, value: String, entity: CompositeEntity) {
//                execute(RenameAttributeValueCommand(c, a, value, entity))
//                //c.renameAttributeValue(a, value)
//            }
//
//            override fun removeTag(view: Component, c:Controller, tagName: String, parent: CompositeEntity) {
//                //c.removeChild(tagName)
//                execute(RemoveChildCommand(c, tagName, parent))
//                //view.removeNotify()
//                //remove(view)
//                //revalidate()
//                //repaint()
//            }
//        })
//        /**
//        if(undo) {
//            parentView!!.add(window)
//            revalidate()
//            repaint()
//        } else { */
//            add(window)
//            revalidate()
//            repaint()
//        //}
//
//    }
//
//
//    // TODO can be more modular like createPopMenu
//    private fun buildAttributes() {
//        if(data.attrs.size != 0) {
//            for(a in data.attrs) {
//                var panel = JPanel()
//                var label = JLabel(a.name)
//                var textField = JTextField(a.attrValue)
//                textField.addKeyListener(object: KeyAdapter() {
//                    override fun keyReleased(e: KeyEvent?) {
//                        notifyObservers { it.editValueAttribute(c, a, (e!!.source as JTextField).getText(), data) }
//                    }
//                })
//                panel.add(label)
//                panel.add(textField)
//                panel.addMouseListener(object: MouseAdapter() {
//                    override fun mouseClicked(e: MouseEvent) {
//                        val popupmenu = JPopupMenu("Actions")
//                        val renameAttribute = JMenuItem("Rename Attribute ${a.name}")
//                        renameAttribute.addActionListener {
//                            val text = JOptionPane.showInputDialog("New name")
//                            notifyObservers { it.renameAttribute(c, a.name, text, data) }
//                        }
//                        popupmenu.add(renameAttribute)
//
//                        val removeAttribute = JMenuItem("Remove Attribute ${a.name}")
//                        removeAttribute.addActionListener {
//                            notifyObservers { it.removeAttribute(c, a, data) }
//                        }
//                        popupmenu.add(removeAttribute)
//
//                        popupmenu.show(e.component, e.x, e.y)
//                    }
//                })
//                attributes += panel
//                add(panel)
//            }
//        }
//    }
//
//    private fun createPopupMenu() {
//        val popupmenu = JPopupMenu("Actions")
//        val addTag = JMenuItem("Add Tag")
//        addTag.addActionListener {
//            val text = JOptionPane.showInputDialog("New Tag Name")
//            notifyObservers { it.addTag(c, text, data.parent!!) }
//        }
//        popupmenu.add(addTag)
//
//        val addAttribute = JMenuItem("Add Attribute")
//        addAttribute.addActionListener {
//            val attName = JOptionPane.showInputDialog("Attribute Name")
//            val attValue = JOptionPane.showInputDialog("Attribute value")
//
//            notifyObservers { it.addAttribute(c, attName, attValue, data) }
//        }
//        popupmenu.add(addAttribute)
//
//        val removeTag = JMenuItem("Remove Tag ${data.name}")
//        removeTag.addActionListener {
//            notifyObservers {
//                it.removeTag(this, c, data.name, data.parent!!)
//            }
//        }
//        popupmenu.add(removeTag)
//
//        val renameTag = JMenuItem("Rename Tag")
//        renameTag.addActionListener {
//            val text = JOptionPane.showInputDialog("New tag name")
//            notifyObservers { it.renameTag(c, data.name, text, data) }
//        }
//        popupmenu.add(renameTag)
//
//        val printCurrentEntity = JMenuItem("Print Tag")
//        printCurrentEntity.addActionListener {
//            println(data.print())
//        }
//        popupmenu.add(printCurrentEntity)
//
//        val stack = JMenuItem("Undo")
//        stack.addActionListener {
//            println(undoStack.stack)
//            undoStack.undo()
//        }
//        popupmenu.add(stack)
//
//
//
//        addMouseListener(object : MouseAdapter() {
//            override fun mouseClicked(e: MouseEvent) {
//                if (SwingUtilities.isRightMouseButton(e))
//                    popupmenu.show(e.component, e.x, e.y)
//            }
//        })
//    }
//
//}
//
//class WindowSkeleton2: JFrame("title") {
//    init {
//        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
//        size = Dimension(500, 500)
//        val room = CompositeEntity("room", attrs = mutableListOf(Attribute("ClassName", "C0.96")))
//        val p1 = CompositeEntity("p1", parent=room)
//        val age1 = CompositeEntity("age1", parent = p1)
//        val p2 = CompositeEntity("p2", parent=room)
//        val age2 = CompositeEntity("age2", parent = p2)
//        val controller = Controller(room)
//        val undoStack = UndoStack()
//        fun execute(c: Command) {
//            undoStack.execute(c)
//        }
//        val skeleton = ComponentSkeleton2(controller, room, undoStack, null)
//        skeleton.addObserver(object : ComponentSkeleton2.ComponentEvent {
//            override fun renameAttribute(c:Controller, oldName: String, newName: String, entity: CompositeEntity) {
//                execute(RenameAttributeNameCommand(c, oldName, newName,entity))
//                //c.renameAttribute(oldName, newName)
//            }
//            //renameTag
//            override fun renameTag(c:Controller, oldName: String, newName: String, entity: CompositeEntity) {
//                execute(RenameTagCommand(c, oldName, newName, entity))
//                //c.renameTag(oldName, newName)
//            }
//
//            override fun addTag(c:Controller, newTagName: String, parent: CompositeEntity) {
//                execute(AddChildCommand(c, newTagName, parent))
//                //c.addChild(newTagName)
//            }
//
//            override fun addAttribute(c: Controller, attName: String, attValue: String, entity: CompositeEntity) {
//                execute(AddAttributeCommand(c, Attribute(attName, attValue), entity))
//                //c.addAttribute(Attribute(attName, attValue))
//            }
//
//            override fun removeAttribute(c: Controller, a: Attribute, entity: CompositeEntity) {
//                execute(RemoveAttributeCommand(c, a, entity))
//                //c.removeAttribute(a)
//            }
//
//            override fun editValueAttribute(c: Controller, a: Attribute, value: String, entity: CompositeEntity) {
//                execute(RenameAttributeValueCommand(c, a, value, entity))
//                //c.renameAttributeValue(a, value)
//            }
//            /**
//            override fun removeTag(view: Component, c:Controller, tagName: String, entity: CompositeEntity) {
//                //c.removeChild(tagName)
//                execute(RemoveChildCommand(c, tagName, entity))
//                remove(view)
//                revalidate()
//                repaint()
//            }
//            */
//        })
//        add(skeleton)
//    }
//
//    fun open() {
//        isVisible = true
//    }
//}
//
//fun main() {
//    val w = WindowSkeleton2()
//    w.open()
//}