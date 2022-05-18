import Classes.Attribute
import Classes.CompositeEntity
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

class ComponentSkeleton(val data: CompositeEntity) : JPanel(), IObservable<ComponentSkeleton.ComponentEvent> {

    interface ComponentEvent {

        // Tag functions
        fun addTag() {}
        fun removeTag() {}
        fun renameTag() {}

        // Attribute functions
        fun addAttribute() {}
        fun removeAttribute() {}
        fun renameAttribute() {}
        fun editValueAttribute() {}


    }

    override val observers: MutableList<ComponentEvent> = mutableListOf()

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        g.font = Font("Arial", Font.BOLD, 16)
        g.drawString(data.name, 10, 20)
    }

    init {
        layout = GridLayout(0, 1)
        border = CompoundBorder(
            BorderFactory.createEmptyBorder(30, 10, 10, 10),
            BorderFactory.createLineBorder(Color.BLACK, 2, true)
        )
        createPopupMenu()
        //Criar um observador para entity e para atributo
        data.addObserver { e, e2, p, aux->
            if(e2 == ObjectType.STRING) {
                when (e) {
                    EventType.ADD -> ""//addPair(p)
                    EventType.REMOVE -> ""//removePair(p)
                    EventType.REPLACE -> ""//replacePair(aux!!, p)
                }
            } else if (e2 == ObjectType.ENTITY) {
                when (e) {
                    EventType.ADD -> ""//addEntity(p)
                    EventType.REMOVE -> ""//removePair(p)
                    EventType.REPLACE -> ""//replacePair(aux!!, p)
                }
            } else if (e2 == ObjectType.ATTRIBUTE) {
                when (e) {
                    EventType.ADD -> ""//addPair(p)
                    EventType.REMOVE -> ""//removePair(p)
                    EventType.REPLACE -> ""//replacePair(aux!!, p)
                }
            }

        }

//        data.addObserver { e, p, aux ->
//            when (e) {
//                EventType.ADD -> ""//addPair(p)
//                EventType.REMOVE -> ""//removePair(p)
//                EventType.REPLACE -> ""//replacePair(aux!!, p)
//            }
//        }
    }

    //TODO testar primeiro com adição de childs em ROOM
    private fun addEntitiy(e: Entity) {
        add(ComponentSkeleton(e as CompositeEntity))
        revalidate()
    }


    private fun createPopupMenu() {
        val popupmenu = JPopupMenu("Actions")
        val rename = JMenuItem("Rename")
        // Rename current name of composite entity TODO
        rename.addActionListener {
            val text = JOptionPane.showInputDialog("New Name")
            add(ComponentSkeleton(CompositeEntity()))
            revalidate()
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
            val tagName = JOptionPane.showInputDialog("Tag name")
            add(ComponentSkeleton(CompositeEntity(tagName, parent = data)))
            revalidate()
        }
        popupmenu.add(addTag)

        // Remove current Tag?? --> last thing TODO
        val delete = JMenuItem("Delete Tag")
        delete.addActionListener {
            val text = JOptionPane.showInputDialog("text")
            add(JLabel(text))
            revalidate()
        }
        popupmenu.add(delete)


        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (SwingUtilities.isRightMouseButton(e))
                    popupmenu.show(this@ComponentSkeleton, e.x, e.y)
            }
        })
    }

}

class WindowSkeleton : JFrame("title") {
    init {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        size = Dimension(500, 500)

        add(ComponentSkeleton(CompositeEntity("room")))
    }

    fun open() {
        isVisible = true
    }
}

fun main() {
    val w = WindowSkeleton()
    w.open()
}