import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor

// saber se um KClassifier é um enumerado
fun KClassifier?.isEnum() = this is KClass<*> && this.isSubclassOf(Enum::class)

// obter uma lista de constantes de um tipo enumerado
fun <T : Any> KClass<T>.enumConstants(): List<T> {
    require(isEnum()) { "class must be enum" }
    return this.java.enumConstants.toList()
}

class ModelGenerator {
    private fun fields(c: KClass<*>): List<KProperty1<*, *>> {
        require(c.isData)
        val consParams = c.primaryConstructor!!.parameters
        return c.declaredMemberProperties.sortedWith { a, b ->
            consParams.indexOfFirst { it.name == a.name } - consParams.indexOfFirst { it.name == b.name }
        }
    }

    // TODO
    // Add object as parent
    // This function should be recursive cause children will be composite entity
    private fun createListObjects(l: List<*>, parent: CompositeEntity): List<Entity> {
        var list = mutableListOf<Entity>()
        l.forEach {
            list += createModel(it!!, parent)!!
        }

        return list
    }

    // TODO
    // create get object value like insertInto
    // create function that builds the model according to attributes

    // Type of objects
    // int --> simpleEntity
    // string --> simpleEntity
    // enumeration --> attribute
    // collection --> function that creates other models (recursively) and after receive it add to children
    // Everything that it's in the list --> CompositeEntity
    fun createModel(o: Any, parent: CompositeEntity? = null): Entity {
        val c = o::class
        var e = CompositeEntity(name = c.findAnnotation<XmlName>()!!.name, parent = parent)
        fields(c).forEach {
            if(it.call(o) != null) {
                when(it.returnType.classifier) {
                    Int::class -> SimpleEntity(name = it.name, text = it.call(o).toString(), parent = e)
                    String::class -> SimpleEntity(name = it.name, text = it.call(o).toString(), parent = e)
                    List::class -> createListObjects(it.call(o) as List<*>, e)
                    RoomType::class -> e.attrs += Attribute("type", it.call(o).toString())
                    StudentType::class -> e.attrs += Attribute("type", it.call(o).toString())
                    Gender::class -> e.attrs += Attribute("gender", it.call(o).toString())
                    else -> "NA"
                }
            }
        }
        //e.print()
        return e
    }
}