package Classes

import Annotations.Inject
import Annotations.InjectAdd
import Interfaces.AttributeFrameSetup
import Interfaces.RootComponentSetup
import Interfaces.Action

import java.awt.*
import java.io.File
import javax.swing.*

class Window: JFrame("title") {
    @InjectAdd("action")
    private var menuActions = mutableListOf<Action>()
    @InjectAdd("attribute")
    private var attributesFrames = mutableListOf<AttributeFrameSetup>()
    @Inject
    private lateinit var rootEntity: RootComponentSetup
    private lateinit var controller: Controller
    private val undoStack = UndoStack()

    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        size = Dimension(1000, 1000)
    }

    fun open() {
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
        var rootComponent = ComponentSkeleton(controller, undoStack, attributesFrames)
        add(rootComponent)
        menuActions.forEach {a ->
            rootComponent.listComponents.forEach {c ->
                c.popupmenu.add(a.execute(controller, c.e, undoStack))
            }
        }
        isVisible = true

    }
}