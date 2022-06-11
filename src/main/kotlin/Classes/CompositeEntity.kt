package Classes

import Interfaces.Visitor


/**
 * Create a class that represents the header of XML
 * <?xml version="1.0" encoding="UTF-8"?>
 */

/**
 * Composite Element should be the entities
 * Attributes from composite element:
 *  --> other elements: <entity><child>...</child><child><subchild/></child></entity>
 */

class CompositeEntity(name: String = "", attrs: MutableList<Attribute> = mutableListOf(), text: String = "", parent: CompositeEntity? = null,
) : Entity(name, attrs, text ,parent) {
    val children = mutableListOf<Entity>()


    override fun accept(v: Visitor) {
        if(v.visit(this))
            children.forEach {
                it.accept(v)
            }
        v.endVisit(this)
    }

    override fun print() {
        if (children.isNotEmpty()) {
            var attrString = buildAttrs()
            println("<$name $attrString>")
            children.forEach {
                print("\t".repeat(it.depth))
                it.print()
            }
            print("\t".repeat(this.depth))
            println("</$name>")
        } else {
            super.print()
        }
    }

    override fun getXML(): String {
        if (children.isNotEmpty()) {
            var attrString = buildAttrs()
            var xml = "<$name${if(attrString != "") " $attrString" else ""}>\n"
            children.forEach {
                xml += "\t".repeat(it.depth)
                xml += it.getXML()
            }
            xml += "\t".repeat(this.depth)
            xml += "</$name>\n"
            return xml
        } else {
            return super.getXML()
        }
    }

    fun searchEntity(name: String, criteria: (Entity)->Boolean = {true}): Entity? {
        var entity: Entity? = null
        val v = object : Visitor {
            override fun visit(e: SimpleEntity) {
                if(e.name == name && criteria(e))
                    entity = e
            }

            override fun visit(e: CompositeEntity): Boolean {
                if(e.name == name && criteria(e)) {
                    entity = e
                    return false
                }
                return true
            }
        }
        accept(v)
        return entity
    }

}