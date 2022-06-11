package Classes

import Interfaces.Action
import javax.swing.JMenuItem

class EventAdd: Action {
    override val actionName: String
        get() = "Add Event"

    override val parentName: String
        get() = "Room"

    override val entityName: String
        get() = "Event"

    override fun execute(c: Controller, parent: CompositeEntity, undoStack: UndoStack): JMenuItem {
        val item = JMenuItem(actionName)
        if(parent.name == parentName) {
            item.addActionListener { undoStack.execute(AddChildCommand(c, CompositeEntity(actionName, mutableListOf(Attribute("descrição", "teste descrição"), Attribute("data", "1-January-2023"), Attribute("reservada", "true"))), parent)) }
        } else {
            item.isEnabled = false
        }
        return item
    }

}