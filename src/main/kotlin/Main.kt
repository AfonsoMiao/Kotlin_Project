import java.awt.Composite
import kotlin.reflect.typeOf
import kotlin.reflect.*
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor
import ModelGenerator

fun main(args: Array<String>) {
    // Example entity name
    println("EXAMPLE 1")
    val room = CompositeEntity("room")
    val roomClass1 = CompositeEntity("class", parent = room)
//    val roomClass2 = CompositeEntity("class", parent = room)
    roomClass1.attrs += Attribute("classname", "EB1")
//    roomClass2.attrs += Attribute("classname", "EB2")
    val p1 = CompositeEntity(name = "person", text = "person1“‘<>&", parent = roomClass1)
    p1.attrs += Attribute("gender", "male")
    p1.attrs += Attribute("age", "24")
    val p2 = CompositeEntity(name = "person", text = "person2", parent = roomClass1)
    p2.attrs += Attribute("gender", "female")
//    val p3 = CompositeEntity(name = "person", text = "person3", parent = roomClass2)
//    val p4 = CompositeEntity(name = "person", text = "person4", parent = roomClass2)


    //p2.attrs += Attribute("age", "28")
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
    //room.children.forEach { println(it) }
    //val finalXML = room.buildXML() {x -> (x as CompositeEntity).children.any { it.name == "gender" && it.text == "male"}}
    val finalXML = room.buildXML() {x ->
        if (x.name == "person") {
            (x as CompositeEntity).children.any { it.name == "gender" && it.text == "male"}
        } else {
            true
        }
    } // TODO melhorar filtro com if(class name) else
    finalXML!!.print()

    println()
    println("FASE 2")
    val s1 = Student("afonso", 24, Gender.Male, StudentType.Master)
    val s2 = Student("catarina", 26, Gender.Female)
    val room2 = Room("C06.02", 50, RoomType.Medium, listOf(s1,s2))
    room2.listStudent += s2
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
