/**
 * Attributes:
 *  --> attributes: <entity att="...">
 *  --> text: <entity>anything</entity>
 */
abstract class Entity(var name: String, var attrs: MutableList<Attribute> = mutableListOf(), var text: String = "", val parent: CompositeEntity? = null) {
    init {
        parent?.children?.add(this)
        text = cleanText(text)
    }

    private fun mapEscapingCharacter(t: String): String =
        t
            .replace("&", "&amp")
            .replace("<", "&lt")
            .replace(">", "&gt")
            .replace("‘", "&apos")
            .replace("“", "&quot")

    private fun cleanText(t: String): String {
        val escapingCharacters = arrayOf("<", ">", "&", "‘", "“")
        val match = escapingCharacters.any { it in t }
        return if (match) {
            mapEscapingCharacter(t)
        } else {
            t
        }
    }

    val depth: Int
        get() =
            if (parent == null) 0
            else 1 + parent.depth

    fun buildAttrs(): String {
        return if (attrs.isEmpty()) "" else attrs.joinToString(separator = " ", transform = { it.name + "=" + "\"${it.attrValue}\""})//.joinToString{ it.name + "=" + "\"${it.attrValue}\""}
    }

    open fun print() {
        var attrString = buildAttrs()
        var xml = "<$name${if(attrString != "") attrString else ""}>${text.ifEmpty { "" }}</$name>"
        println(xml)
    }

    abstract fun accept(v: Visitor)
}

/**
 * Create a class that represents the header of XML
 * <?xml version="1.0" encoding="UTF-8"?>
 */

/**
 * Composite Element should be the entities
 * Attributes from composite element:
 *  --> other elements: <entity><child>...</child><child><subchild/></child></entity>
 */
class CompositeEntity(name: String = "", attrs: MutableList<Attribute> = mutableListOf(), text: String = "", parent: CompositeEntity? = null) : Entity(name, attrs, text ,parent) {
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
//            if (text.isNotEmpty() && attrs.isEmpty()) {
//                println("$text")
//            }
            //println("${text.ifEmpty { "" }}")
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

    // Functions to add and remove entities from composite
    fun addChild(e: Entity) {
        if(children.add(e)) {
//                notifyObservers {
//                    it(EventType.ADD, p, null)
//                }
        }
    }

    fun removeChild(e: Entity) {
        children.remove(e)
//            notifyObservers {
//                it(EventType.REMOVE, p, null)
//            }
    }

    // Function to rename the name of the composite
    fun renameEntity(n: String) {
        name = n
    }

    // Functions to add and remove entities from composite
    fun addAttribute(a: Attribute) {
        if(attrs.add(a)) {}
//                notifyObservers {
//                    it(EventType.ADD, p, null)
//                }
    }

    fun removeAttribute(a: Attribute) {
        attrs.remove(a)
//                notifyObservers {
//                    it(EventType.ADD, p, null)
//                }
    }

    // Functions to edit attributes from composite
    fun renameAttribute(a: Attribute, n: String) {
        a.name = n
    }

    fun editAttributeValue(a: Attribute, v: String) {
        a.attrValue = v
    }

}

/**
 * Leaf element should be the child
 */

class SimpleEntity(name: String = "", attrs: MutableList<Attribute> = mutableListOf(), text: String = "", parent: CompositeEntity? = null) : Entity(name, attrs, text ,parent) {

    override fun accept(v: Visitor) {
        v.visit(this)
    }

}


// Add criteria as decision function
fun CompositeEntity.searchEntity(name: String, criteria: (Entity)->Boolean = {true}): Entity? {
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

/**
 * <room>
 *     <class>
 *         <person>
 *             <age><age>
 *             <gender><gender>
 *         <person>
 *         <person>
 *             <age><age>
 *             <gender><gender>
 *         <person>
 *     <class>
 * <room>
 */
/**
 * <room>
 *     <class>
 *         <person>
 *             <age><age>
 *             <gender><gender>
 *         <person>
 *     <class>
 * <room>
 */
fun CompositeEntity.buildXML(criteria: (Entity) -> Boolean = {true}): Entity? {
    var cEntity = CompositeEntity(name = this.name, attrs = this.attrs, text = this.text)
    var nextAuxComposite: CompositeEntity? = null
    var getCompositeTags = false
    var listUndo: MutableList<CompositeEntity> = mutableListOf()
    var prevAuxComposite: CompositeEntity? = cEntity
    val v = object : Visitor {
        override fun visit(e: SimpleEntity) {
            if(getCompositeTags) {
                SimpleEntity(e.name, e.attrs, e.text, nextAuxComposite)
            }
        }

        override fun visit(c: CompositeEntity): Boolean {
//            println("Current: " + c.name)
//            println("Current text: " + c.text)
            if(criteria(c) && c.name != cEntity.name) {
//                println("Previous: " + prevAuxComposite!!.name)
                //println("Visiting: " + c.name)
                nextAuxComposite  = CompositeEntity(c.name, c.attrs, c.text, prevAuxComposite)
                prevAuxComposite = nextAuxComposite
                getCompositeTags = true
                listUndo.add(nextAuxComposite!!)
            }
            return true
        }

        override fun endVisit(c: CompositeEntity) {
            getCompositeTags = false
            if (listUndo.isNotEmpty())
                prevAuxComposite = listUndo.removeLastOrNull()
        }
    }
    accept(v)
    return cEntity
}