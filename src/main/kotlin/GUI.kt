import Classes.Attribute
import Classes.CompositeEntity
import Classes.CompositeEntityDataset
import Classes.Entity
import Enumerations.EventType
import Interfaces.IObservable

import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.border.CompoundBorder

class ComponentSkeleton2(val c: Controller) : JPanel(), IObservable<ComponentSkeleton2.ComponentEvent>{

    interface ComponentEvent {

        // Tag functions
        fun addTag(c: Controller, newTagName: String) {}
        fun removeTag(view: Component) {}
        fun renameTag(c: Controller, oldName: String, newName: String) {}

        // Attribute functions
        fun addAttribute(c: Controller, attName: String, attValue: String) {}
        fun removeAttribute(c: Controller, a: Attribute) {}
        fun renameAttribute(c: Controller, oldName: String, newName: String) {}
        fun editValueAttribute(c: Controller, a: Attribute, value: String) {}


    }

    override val observers: MutableList<ComponentEvent> = mutableListOf()
    private val attributes: MutableList<JPanel> = mutableListOf()
    var entityName:String = ""

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
        println("being refreshed")
        entityName = c.data.name
        buildAttributes()
        createPopupMenu()
        for (children in c.data.children) {
            addChild(children as CompositeEntity)
        }
        c.addObserver { e, p, aux ->
            when(e) {
                EventType.RENAME_ATTRIBUTE -> renameAttribute(p as String, aux!! as String)
                EventType.RENAME_TAG -> renameTag(p as String)
                EventType.ADD_TAG -> addChild(p as CompositeEntity)
                //EventType.REMOVE_TAG -> //removeTag()
                EventType.ADD_ATTRIBUTE -> addAttribute(p as Attribute)
                EventType.REMOVE_ATTRIBUTE -> removeAttribute(p as Attribute)
            }
        }
    }

    private fun removeAttribute(att: Attribute) {
        println("Remove attribute: ${att.name}")
        val panelRemove = attributes.find { (it.getComponent(0)!! as JLabel).text == att.name }
        attributes.remove(panelRemove)
        panelRemove!!.removeAll()
        revalidate()
        repaint()
    }

    private fun addAttribute(att: Attribute) {
        var panel = JPanel()
        var label = JLabel(att.name)
        panel.add(label)
        panel.add(JTextField(att.attrValue))
        panel.addMouseListener(object: MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                val popupmenu = JPopupMenu("Actions")
                val renameAttribute = JMenuItem("Rename Attribute ${att.name}")
                renameAttribute.addActionListener {
                    val text = JOptionPane.showInputDialog("New name")
                    notifyObservers { it.renameAttribute(c, att.name, text) }
                }
                popupmenu.add(renameAttribute)

                val removeAttribute = JMenuItem("Remove Attribute ${att.name}")
                removeAttribute.addActionListener {
                    notifyObservers { it.removeAttribute(c, att) }
                }
                popupmenu.add(removeAttribute)

                popupmenu.show(e.component, e.x, e.y)
            }
        })
        attributes += panel
        add(panel, 0)
        revalidate()
        repaint()
    }
    private fun renameAttribute(oldName: String, newName: String) {
        (this.attributes.find { (it.getComponent(0)!! as JLabel).text == oldName}!!.getComponent(0) as JLabel).text = newName
        revalidate()
        repaint()
    }

    // It should
    //private fun removeTag() {
    //    println("current view: $entityName")
    //    removeAll()
    //    remove(this)
    //    remove(0)
    //    revalidate()
    //    repaint()
    //}

    private fun renameTag(newName: String) {
        entityName = newName
        revalidate()
        repaint()
    }

    private fun addChild(c: CompositeEntity) {
        val window = ComponentSkeleton2(Controller(c))
        window.addObserver(object : ComponentEvent {
            override fun renameAttribute(c:Controller, oldName: String, newName: String) {
                c.renameAttribute(oldName, newName)
            }

            override fun renameTag(c:Controller, oldName: String, newName: String) {
                c.renameTag(oldName, newName)
            }

            override fun addTag(c:Controller, newTagName: String) {
                c.addChild(newTagName)
            }

            override fun addAttribute(c: Controller, attName: String, attValue: String) {
                c.addAttribute(attName, attValue)
            }

            override fun removeTag(view: Component) {
                remove(view)
                revalidate()
                repaint()
            }

            override fun removeAttribute(c: Controller, a: Attribute) {
                c.removeAttribute(a)
            }
        })
        add(window)
        revalidate()
        repaint()
    }


    // TODO can be more modular like createPopMenu
    private fun buildAttributes() {
        if(c.data.attrs.size != 0) {
            for(a in c.data.attrs) {
                var panel = JPanel()
                var label = JLabel(a.name)
                //label
                panel.add(label)
                panel.add(JTextField(a.attrValue))
                panel.addMouseListener(object: MouseAdapter() {
                    override fun mouseClicked(e: MouseEvent) {
                        val popupmenu = JPopupMenu("Actions")
                        val renameAttribute = JMenuItem("Rename Attribute ${a.name}")
                        renameAttribute.addActionListener {
                            val text = JOptionPane.showInputDialog("New name")
                            notifyObservers { it.renameAttribute(c, a.name, text) }
                        }
                        popupmenu.add(renameAttribute)

                        val removeAttribute = JMenuItem("Remove Attribute ${a.name}")
                        removeAttribute.addActionListener {
                            notifyObservers { it.removeAttribute(c, a) }
                        }
                        popupmenu.add(removeAttribute)

                        popupmenu.show(e.component, e.x, e.y)
                    }
                })
                attributes += panel
                add(panel)
            }
        }
    }

    /** TODO
     * Functions:
     *  --> Edit attributes: properties from class Classes.Attribute should be public
     *  --> Rename entities name: variable name in Entity class should be public to let it rename
     *  --> Add and remove attributes: In case of adding - create object attribute - and remove, the list should be mutable
     *  --> Add and remove entities: this feature affects the child. It just removes and adds new composite into child list
     *  --> History of actions: implement stack commands like MVC exercise
     *  --> Save the state as a file: Save the current state as a file
     */
    private fun createPopupMenu() {
        val popupmenu = JPopupMenu("Actions")
        val addTag = JMenuItem("Add Tag")
        addTag.addActionListener {
            val text = JOptionPane.showInputDialog("New Tag Name")
            notifyObservers { it.addTag(c, text) }
        }
        popupmenu.add(addTag)

        val addAttribute = JMenuItem("Add Attribute")
        addAttribute.addActionListener {
            val attName = JOptionPane.showInputDialog("Attribute Name")
            val attValue = JOptionPane.showInputDialog("Attribute value")

            notifyObservers { it.addAttribute(c, attName, attValue) }
        }
        popupmenu.add(addAttribute)

        val removeTag = JMenuItem("Remove Tag ${c.data.name}")
        removeTag.addActionListener {
            notifyObservers {
                //println(this.entityName)
                it.removeTag(this)
            }
        }
        popupmenu.add(removeTag)

        val renameTag = JMenuItem("Rename Tag")
        renameTag.addActionListener {
            val text = JOptionPane.showInputDialog("New tag name")
            notifyObservers { it.renameTag(c, c.data.name, text) }
        }
        popupmenu.add(renameTag)


        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (SwingUtilities.isRightMouseButton(e))
                    popupmenu.show(e.component, e.x, e.y)
            }
        })
    }

}

class WindowSkeleton2: JFrame("title") {
    init {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        size = Dimension(500, 500)
        val room = CompositeEntity("room", attrs = mutableListOf(Attribute("ClassName", "C0.96")))//, Attribute("Capacity", "100")))
        val p1 = CompositeEntity("p1", parent=room)
        val age1 = CompositeEntity("age1", parent = p1)
        val p2 = CompositeEntity("p2", parent=room)
        val age2 = CompositeEntity("age2", parent = p2)
        val controller = Controller(room)
        val skeleton = ComponentSkeleton2(controller)
        skeleton.addObserver(object : ComponentSkeleton2.ComponentEvent {
            override fun renameAttribute(c:Controller, oldName: String, newName: String) {
                c.renameAttribute(oldName, newName)
            }
            //renameTag
            override fun renameTag(c:Controller, oldName: String, newName: String) {
                c.renameTag(oldName, newName)
            }

            override fun addTag(c:Controller, newTagName: String) {
                c.addChild(newTagName)
            }

            override fun addAttribute(c: Controller, attName: String, attValue: String) {
                c.addAttribute(attName, attValue)
            }

            override fun removeAttribute(c: Controller, a: Attribute) {
                c.removeAttribute(a)
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