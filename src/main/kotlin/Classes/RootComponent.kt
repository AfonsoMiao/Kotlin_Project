package Classes

import Interfaces.RootComponentSetup

class RootComponent: RootComponentSetup {
    override val name: String
        get() = "Data.Room"
    override val listAttributes: MutableList<Attribute>
        get() = mutableListOf(Attribute("sala reservada", "true"))
    override fun getRootComponent(): CompositeEntity {
        val root = CompositeEntity("Data.Room", listAttributes)
        CompositeEntity("Person1", attrs = mutableListOf(Attribute("Nome", "Afonso")), parent = root)
        return root
    }

}