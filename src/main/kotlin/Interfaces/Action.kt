package Interfaces

import Classes.CompositeEntity
import Classes.Controller
import Classes.UndoStack
import javax.swing.JMenuItem

interface Action {
    val actionName: String
    val parentName: String
    val entityName: String
    fun execute(c: Controller, parent: CompositeEntity, undoStack: UndoStack): JMenuItem
}