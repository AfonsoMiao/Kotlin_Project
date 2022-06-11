package Interfaces

import Entities.CompositeEntity
import Entities.SimpleEntity

interface Visitor {
    fun visit(c: CompositeEntity): Boolean = true
    fun endVisit(c: CompositeEntity) { }
    fun visit(l: SimpleEntity) { }
}