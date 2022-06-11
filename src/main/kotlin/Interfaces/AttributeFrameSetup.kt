package Interfaces

import Entities.Attribute
import Entities.CompositeEntity
import Entities.Controller
import Entities.UndoStack
import Enumerations.EventType
import javax.swing.JPanel

interface AttributeFrameSetup {
    val typeAttribute: String
    fun execute(c: Controller, entity: CompositeEntity, a: Attribute, newName: String?, value: String?, undoStack: UndoStack, typeExecution: EventType)
    fun getFrame(c: Controller, entity: CompositeEntity, a: Attribute, undoStack: UndoStack): JPanel
}