import java.awt.Composite
import kotlin.reflect.typeOf
import kotlin.reflect.*
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor
import ModelGenerator


class Attribute(val name:String, val attrValue:String) {

}

interface Visitor {
    fun visit(c: CompositeEntity): Boolean = true
    fun endVisit(c: CompositeEntity) { }
    fun visit(l: SimpleEntity) { }
}



/**
 * Attributes:
 *  --> attributes: <entity att="...">
 *  --> text: <entity>anything</entity>
 */
abstract class Entity(val name: String, var attrs: MutableList<Attribute> = mutableListOf(), var text: String = "", val parent: CompositeEntity? = null) {
    init {
        parent?.children?.add(this)
        text = cleanText(text)
    }

    // TODO
    // replace is always replace & character of previous replaces
    // TEST IT
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
            if (text.isNotEmpty() && attrs.isEmpty()) {
                println("$text")
            }
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

fun CompositeEntity.buildXML(criteria: (Entity) -> Boolean = {true}): Entity? {
    var cEntity = CompositeEntity(name = this.name, attrs = this.attrs, text = this.text)
    var auxComposite: CompositeEntity? = null
    //var auxSimple: SimpleEntity? = null
    val v = object : Visitor {
        override fun visit(e: SimpleEntity) {
            SimpleEntity(e.name, e.attrs, e.text, auxComposite)
        }

        override fun visit(c: CompositeEntity): Boolean {
            //println(criteria(c))
            if(criteria(c))
                auxComposite = CompositeEntity(c.name, c.attrs, c.text, cEntity)
            return true
        }

        override fun endVisit(c: CompositeEntity) {
            //auxSimple = null
            auxComposite = null
        }
    }
    accept(v)
    return cEntity
}


fun main(args: Array<String>) {
    // Example entity name
    println("EXAMPLE 1")
    val room = CompositeEntity("room")
    val p1 = CompositeEntity(name = "person", text = "person1“‘<>&", parent = room)
    p1.attrs += Attribute("gender", "male")
    p1.attrs += Attribute("age", "24")
    val p2 = CompositeEntity(name = "person", text = "person2", parent = room)
    p2.attrs += Attribute("gender", "female")
    //p2.attrs += Attribute("age", "28")
    room.print()

    println("")

    // Example search entity
    println("EXAMPLE 2")
    SimpleEntity("gender", text = "male", parent = p1)
    SimpleEntity("age", text = "24", parent = p1)
    SimpleEntity("gender", text = "female", parent = p2)
//    SimpleEntity("age", text = "28", parent = p2)
    room.print()

    val entity1 = room.searchEntity("person") { (it as CompositeEntity).children.size == 2}
    val entity2 = room.searchEntity("person") { it.attrs.size == 1}
    println("")
    entity1!!.print()
    println("")
    entity2!!.print()

    // Example --> build an XML document while filtering elements from another XML
    println("EXAMPLE 3")
    //room.children.forEach { println(it) }
    val finalXML = room.buildXML() {x -> (x as CompositeEntity).children.any { it.name == "gender" && it.text == "male"}}
    finalXML!!.print()

    println()
    println("FASE 2")
    val s1 = Student("afonso", 24, Gender.Male, StudentType.Master)
    val s2 = Student("catarina", 26, Gender.Female)
    val room2 = Room("C06.02", 50, RoomType.Medium, listOf(s1,s2))
    room2.listStudent += s2
    val c = room2::class
//    val entityName = c.findAnnotation<XmlName>()!!.name
//    val entityHasNum = c.declaredMemberProperties.any { it.returnType.classifier.isEnum() }
//    println("First entity name: $entityName")
//    println("Entity has enum: $entityHasNum")
//    println()
    val modelGenerator = ModelGenerator()
    val model = modelGenerator.createModel(room2)
    model.print()
}




@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class XmlName(val name: String)

@Target(AnnotationTarget.PROPERTY)
annotation class XmlTagContent

@Target(AnnotationTarget.PROPERTY)
annotation class XmlIgnore
