import Classes.*
import Data.Room
import Data.Student
import Enumerations.Gender
import Enumerations.RoomType
import Enumerations.StudentType
import Interfaces.Visitor

// Add criteria as decision function
//fun CompositeEntity.searchEntity(name: String, criteria: (Entity)->Boolean = {true}): Entity? {
//    var entity: Entity? = null
//    val v = object : Visitor {
//        override fun visit(e: SimpleEntity) {
//            if(e.name == name && criteria(e))
//                entity = e
//        }
//
//        override fun visit(e: CompositeEntity): Boolean {
//            if(e.name == name && criteria(e)) {
//                entity = e
//                return false
//            }
//            return true
//        }
//    }
//    accept(v)
//    return entity
//}

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
            if(criteria(c) && c.name != cEntity.name) {
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

fun main(args: Array<String>) {
    // Example entity name
    println("EXAMPLE 1")
    val room = CompositeEntity("room")
    val roomClass1 = CompositeEntity("class", parent = room)
//    val roomClass2 = CompositeEntity("class", parent = room)
    roomClass1.attrs += Attribute("classname", "EB1")
//    roomClass2.attrs += Classes.Attribute("classname", "EB2")
    val p1 = CompositeEntity(name = "person", text = "person1“‘<>&", parent = roomClass1)
    p1.attrs += Attribute("gender", "male")
    p1.attrs += Attribute("age", "24")
    val p2 = CompositeEntity(name = "person", text = "person2", parent = roomClass1)
    p2.attrs += Attribute("gender", "female")
//    val p3 = CompositeEntity(name = "person", text = "person3", parent = roomClass2)
//    val p4 = CompositeEntity(name = "person", text = "person4", parent = roomClass2)


    //p2.attrs += Classes.Attribute("age", "28")
    room.print()

    println("")

    // Example search entity
    println("EXAMPLE 2")
    SimpleEntity("gender", text = "male", parent = p1)
    SimpleEntity("age", text = "24", parent = p1)
    SimpleEntity("gender", text = "female", parent = p2)
//    SimpleEntity("gender", text = "male", parent = p3)
//    SimpleEntity("age", text = "24", parent = p3)
//    SimpleEntity("gender", text = "female", parent = p4)
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
    val finalXML = room.buildXML { x ->
        if (x.name == "person") {
            (x as CompositeEntity).children.any { it.name == "gender" && it.text == "male"}
        } else {
            true
        }
    }
    finalXML!!.print()

    println()
    println("FASE 2")
    val s1 = Student("afonso", 24, Gender.Male, StudentType.Master)
    val s2 = Student("catarina", 26, Gender.Female)
    val room2 = Room("C06.02", 50, RoomType.Medium, listOf(s1,s2))
    room2.listStudent += s2
    val modelGenerator = ModelGenerator()
    val model = modelGenerator.createModel(room2)
    model.print()
}