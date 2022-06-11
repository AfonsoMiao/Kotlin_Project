package Interfaces

import Entities.Attribute
import Entities.CompositeEntity

interface RootComponentSetup {
    val name: String
    val listAttributes: MutableList<Attribute>
    fun getRootComponent(): CompositeEntity
}