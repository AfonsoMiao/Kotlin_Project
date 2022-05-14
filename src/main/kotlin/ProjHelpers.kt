import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.border.CompoundBorder

/** TODO
 * Functions:
 *  --> Edit attributes: properties from class Attribute should be public
 *  --> Rename entities name: variable name in Entity class should be public to let it rename
 *  --> Add and remove attributes: In case of adding - create object attribute - and remove, the list should be mutable
 *  --> Add and remove entities: this feature affects the child. It just removes and adds new composite into child list
 *  --> History of actions: implement stack commands like MVC exercise
 *  --> Save the state as a file: Save the current state as a file
 */
class ComponentSkeleton(val text: String) : JPanel() {

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        g.font = Font("Arial", Font.BOLD, 16)
        g.drawString(text, 10, 20)
    }

    init {
        layout = GridLayout(0, 1)
        border = CompoundBorder(
            BorderFactory.createEmptyBorder(30, 10, 10, 10),
            BorderFactory.createLineBorder(Color.BLACK, 2, true)
        )
        createPopupMenu()
    }

    private fun createPopupMenu() {
        val popupmenu = JPopupMenu("Actions")
        val a = JMenuItem("Add child")
        a.addActionListener {
            val text = JOptionPane.showInputDialog("text")
            add(ComponentSkeleton(text))
            revalidate()
        }
        popupmenu.add(a)

        val b = JMenuItem("Add child")
        b.addActionListener {
            val text = JOptionPane.showInputDialog("text")
            add(JLabel(text))
            revalidate()
        }
        popupmenu.add(b)


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
        size = Dimension(300, 300)

        add(ComponentSkeleton("root"))
    }

    fun open() {
        isVisible = true
    }
}

fun main() {
    val w = WindowSkeleton()
    w.open()
}