package Interfaces

import Classes.Attribute
import Classes.CompositeEntity
import Classes.Controller
import Classes.UndoStack
import Enumerations.EventType
import javax.swing.JPanel

interface AttributeFrameSetup {
    val typeAttribute: String
    fun execute(c: Controller, entity: CompositeEntity, a: Attribute, newName: String?, value: String?, undoStack: UndoStack, typeExecution: EventType)
    fun getFrame(c: Controller, entity: CompositeEntity, a: Attribute, undoStack: UndoStack): JPanel
}