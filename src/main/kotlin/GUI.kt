import Classes.Attribute
import Classes.CompositeEntity
import Classes.CompositeEntityDataset
import Classes.Entity
import Interfaces.IObservable
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.border.CompoundBorder
import Enumerations.EventType
import Enumerations.ObjectType

/** TODO
 * Functions:
 *  --> Edit attributes: properties from class Classes.Attribute should be public
 *  --> Rename entities name: variable name in Entity class should be public to let it rename
 *  --> Add and remove attributes: In case of adding - create object attribute - and remove, the list should be mutable
 *  --> Add and remove entities: this feature affects the child. It just removes and adds new composite into child list
 *  --> History of actions: implement stack commands like MVC exercise
 *  --> Save the state as a file: Save the current state as a file
 */

class RootComponent(val data: CompositeEntityDataset) : JPanel(), IObservable<RootComponent.ComponentEvent> {

    interface ComponentEvent {

        // Tag functions
        fun addTag(p: CompositeEntity) {}
        fun removeTag() {}
        fun renameTag() {}

        // Attribute functions
        fun addAttribute() {}
        fun removeAttribute() {}
        fun renameAttribute() {}
        fun editValueAttribute() {}


    }

    inner class EntityComponent(var cEntity: CompositeEntity) : JComponent() {
        //val first = JTextField("${pair.first}")
        //val second = JTextField("${pair.second}")

        //inner class MouseClick(val first: Boolean) : MouseAdapter() {
        //    override fun mouseClicked(e: MouseEvent?) {
        //        val v = JOptionPane.showInputDialog("new value").toString().toIntOrNull()
        //        v?.let {
        //            notifyObservers {
        //                it.pairModified(
        //                    pair,
        //                    if (first) IntPair(v.toInt(), pair.second)
        //                    else IntPair(pair.first, v.toInt())
        //                )
        //            }
        //        }
        //    }
        //}

        override fun paintComponent(g: Graphics) {
            super.paintComponent(g)
            g.font = Font("Arial", Font.BOLD, 16)
            g.drawString(cEntity.name, 10, 20)
        }
        //TODO CREATE A RECURSIVE FUNCTION TO ADD ALL CHILDREN and attributes
        // IT might not need recursive function because while adding new composite entity we are initializing another object
        // SO we should add this in init function?
        init {
            layout = GridLayout(0, 1)
            border = CompoundBorder(
                BorderFactory.createEmptyBorder(30, 10, 10, 10),
                BorderFactory.createLineBorder(Color.BLACK, 2, true)
            )
            //first.isEnabled = false
            //first.addMouseListener(MouseClick(true))
            //add(first)
//
            //second.isEnabled = false
            //second.addMouseListener(MouseClick(false))
            //add(second)
//
            //add(button("delete") {
            //    notifyObservers {
            //        it.pairDeleted(pair)
            //    }
            //})
        }

        fun modify(new: CompositeEntity) {
            //pair = new
            //first.text = "${new.first}"
            //second.text = "${new.second}"
        }

        fun matches(p: CompositeEntity) = cEntity == p

        private fun createPopupMenu() {
            val popupmenu = JPopupMenu("Actions")
            val rename = JMenuItem("Rename")
            // Rename current name of composite entity TODO
            rename.addActionListener {
                //val text = JOptionPane.showInputDialog("New Name")
                val tagName = JOptionPane.showInputDialog("Tag name") //TODO adicionar notify observers como a inner class PairComponent
                //tagName?.let {
                //    notifyObservers {
                //        it.addTag(CompositeEntity(name=tagName, parent=data))
                //    }
                //}
                //p
                //add(ComponentSkeleton(CompositeEntity()))
                //revalidate()
            }
            popupmenu.add(rename)

            // Add new attribute to current object TODO
            val addAttribute = JMenuItem("Add Attribute")
            addAttribute.addActionListener {
                val text = JOptionPane.showInputDialog("Attribute Name")
                add(JLabel(text))
                revalidate()
            }
            popupmenu.add(addAttribute)

            // Creates another child --> passing current object as parent TODO
            val addTag = JMenuItem("Add Tag")
            addTag.addActionListener {
                val tagName = JOptionPane.showInputDialog("Tag name") //TODO adicionar notify observers como a inner class PairComponent
                tagName?.let {
                    notifyObservers {
                        it.addTag(CompositeEntity(name=tagName))
                    }
                }
            }
            popupmenu.add(addTag)

            // Remove current Tag?? --> last thing TODO
            val delete = JMenuItem("Delete Tag")
            delete.addActionListener {
                //val text = JOptionPane.showInputDialog("text")
                //println("Want to delete on: " + data.name)
                //data.name.let {
                //    notifyObservers {
                //        it.removeTag()
                //    }
                //}
                //add(JLabel(text))
                //revalidate()
            }
            popupmenu.add(delete)


            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    if (SwingUtilities.isRightMouseButton(e))
                        popupmenu.show(this@RootComponent, e.x, e.y)
                }
            })
        }
    }

    override val observers: MutableList<ComponentEvent> = mutableListOf()



    init {
        //layout = GridLayout(0, 1)
        //border = CompoundBorder(
        //    BorderFactory.createEmptyBorder(30, 10, 10, 10),
        //    BorderFactory.createLineBorder(Color.BLACK, 2, true)
        //)
        layout = GridLayout(0, 1)
        for (p in data) {
            addPair(p)

        }
        data.addObserver { e, e2, p, aux ->
            when (e) {
                EventType.ADD -> addPair(p as CompositeEntity)
                EventType.REMOVE -> removePair(p as CompositeEntity)
                EventType.REPLACE -> replacePair(aux!! as CompositeEntity, p as CompositeEntity)
            }
        }
        //createPopupMenu()
        ////Criar um observador para entity e para atributo
        //data.addObserver { e, e2, p, aux->
        //    if(e2 == ObjectType.STRING) {
        //        when (e) {
        //            EventType.ADD -> ""//addPair(p)
        //            EventType.REMOVE -> ""//removePair(p)
        //            EventType.REPLACE -> ""//replacePair(aux!!, p)
        //        }
        //    } else if (e2 == ObjectType.ENTITY) {
        //        when (e) {
        //            EventType.ADD -> ""//addEntity(p as Entity)//addEntity(p)
        //            EventType.REMOVE -> ""//removePair(p)
        //            EventType.REPLACE -> ""//replacePair(aux!!, p)
        //        }
        //    } else if (e2 == ObjectType.ATTRIBUTE) {
        //        when (e) {
        //            EventType.ADD -> ""//addPair(p)
        //            EventType.REMOVE -> ""//removePair(p)
        //            EventType.REPLACE -> ""//replacePair(aux!!, p)
        //        }
        //    }
//
        //}

    }

    //private fun addEntity(e: Entity) {
    //    val window = RootComponent(e as CompositeEntity)
    //    window.addObserver(object: ComponentEvent {
    //        override fun addTag(p: Entity) {
    //            e.addChild(p)
    //        }
    //    })
    //    add(window)
    //    revalidate()
    //}

    private fun addPair(p: CompositeEntity) {
        val window = EntityComponent(p)
        if(p.children.size != 0) {
            for (c in p.children) {
                window.add(EntityComponent(c as CompositeEntity))
            }
        }
        add(window)
        revalidate()
        repaint()
    }

    fun removePair(p: CompositeEntity) {
        val find = components.find { it is EntityComponent && it.matches(p) }
        find?.let {
            remove(find)
        }
        revalidate()
        repaint()
    }

    fun replacePair(old: CompositeEntity, new: CompositeEntity) {
        val find = components.find { it is EntityComponent && it.matches(old) } as? EntityComponent
        find?.let {
            find.modify(new)
        }
    }

}

class WindowSkeleton2 : JFrame("title") {
    init {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        size = Dimension(500, 500)
        val room = CompositeEntity("roomA")
        CompositeEntity("p1", parent = room)
        val room2 = CompositeEntity("roomB")
        val data = CompositeEntityDataset(room, room2)
        val window = RootComponent(data)
        window.addObserver(object: RootComponent.ComponentEvent {
            override fun addTag(p: CompositeEntity) {
                data.addChild(p)
            }
        })
        add(window)
    }

    fun open() {
        isVisible = true
    }
}

fun main() {
    val w = WindowSkeleton2()
    w.open()
}