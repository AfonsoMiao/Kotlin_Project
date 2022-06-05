package Classes

import Interfaces.Visitor
import Interfaces.IObservable
import Enumerations.EventType
import Enumerations.ObjectType


/**
 * Create a class that represents the header of XML
 * <?xml version="1.0" encoding="UTF-8"?>
 */

/**
 * Composite Element should be the entities
 * Attributes from composite element:
 *  --> other elements: <entity><child>...</child><child><subchild/></child></entity>
 */

// TODO IMPLEMENT OBSERVABLE

class CompositeEntity(name: String = "", attrs: MutableList<Attribute> = mutableListOf(), text: String = "", parent: CompositeEntity? = null,
) : Entity(name, attrs, text ,parent), IObservable<(EventType, ObjectType, Any?, Any?) -> Unit> {
    val children = mutableListOf<Entity>()

    override val observers: MutableList<(EventType, ObjectType, Any?, Any?) -> Unit> = mutableListOf()

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
            //println()
            children.forEach {
                //print()
                xml += "\t".repeat(it.depth)
                xml += it.getXML()
                //it.print()
            }
            //print("\t".repeat(this.depth))
            xml += "\t".repeat(this.depth)
            //println("</$name>")
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

   // Functions to add and remove entities from composite
   fun addChild(e: Entity) {
       println("Adding child: " + e.name)
       println("With parent: " + this.name)
       if (children.add(e)) {
           notifyObservers {
               it(EventType.ADD, ObjectType.ENTITY, e, null)
           }
       }
   }
   fun removeChild(e: Entity) {
       children.remove(e)
       notifyObservers {
           it(EventType.REMOVE, ObjectType.ENTITY, e, null)
       }
   }

   // Function to rename the name of the composite
   fun renameEntity(n: String) {
       if (n != this.name) {
           var oldName = this.name
           this.name = n
           notifyObservers {
               it(EventType.REPLACE, ObjectType.ENTITY, n, oldName)
           }
       }
   }

   // TODO MAKE ATTRIBUTE OBSERVABLE TOO
   // Composite entity and attributes are distinct objects
   // Functions to add and remove entities from composite
   fun addAttribute(a: Attribute) {
       if (attrs.add(a)) {
           notifyObservers {
               it(EventType.ADD, ObjectType.ATTRIBUTE, a, null)
           }
       }
   }
   fun removeAttribute(a: Attribute) {
       if (attrs.remove(a)) {
           notifyObservers {
               it(EventType.REMOVE, ObjectType.ATTRIBUTE, a, null)
           }
       }
   }
   // Functions to edit attributes from composite
   fun renameAttribute(a: Attribute, n: String) {
       a.name = n
   }


   fun editAttributeValue(a: Attribute, v: String) {
       a.attrValue = v
   }

}