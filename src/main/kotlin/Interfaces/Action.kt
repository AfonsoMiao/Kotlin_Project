package Interfaces

import Classes.CompositeEntity
import Classes.Controller
import Classes.UndoStack

interface Action {
    val actionName: String
    val parentName: String
    fun execute(c: Controller, parent: CompositeEntity, undoStack: UndoStack)
}