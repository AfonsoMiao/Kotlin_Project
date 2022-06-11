package Interfaces

import Classes.Attribute
import Classes.CompositeEntity

interface RootComponentSetup {
    val name: String
    val listAttributes: MutableList<Attribute>
    fun getRootComponent(): CompositeEntity
}