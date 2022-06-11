package Classes

import Interfaces.Action

class EventAdd: Action {
    override val actionName: String
        get() = "Event"

    override val parentName: String
        get() = "Room"

    override fun execute(c: Controller, parent: CompositeEntity, undoStack: UndoStack) {
        undoStack.execute(AddChildCommand(c, CompositeEntity(actionName, mutableListOf(Attribute("descrição", ""), Attribute("data", "1-January-2023"), Attribute("reservada", "true"))), parent))
    }

}