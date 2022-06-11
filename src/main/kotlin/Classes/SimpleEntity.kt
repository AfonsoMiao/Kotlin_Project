package Classes

import Interfaces.Visitor

/**
 * Leaf element should be the child
 */

class SimpleEntity(name: String = "", attrs: MutableList<Attribute> = mutableListOf(), text: String = "", parent: CompositeEntity? = null) : Entity(name, attrs, text ,parent) {

    override fun accept(v: Visitor) {
        v.visit(this)
    }

}