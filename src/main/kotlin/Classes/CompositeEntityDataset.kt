package Classes

import Enumerations.EventType
import Enumerations.ObjectType
import Interfaces.IObservable

class CompositeEntityDataset (vararg cEntities: CompositeEntity): Iterable<CompositeEntity>, IObservable<(EventType, ObjectType, Any?, Any?) -> Unit> {
    val data = mutableSetOf<CompositeEntity>()
    override val observers: MutableList<(EventType, ObjectType, Any?, Any?) -> Unit> = mutableListOf()
    override fun iterator(): Iterator<CompositeEntity> = data.iterator()

    init {
        cEntities.forEach { data.add(it) }
    }

    private fun getElement(c: CompositeEntity): CompositeEntity {
        val indexElement = data.indexOf(c)
        return data.elementAt(indexElement)
    }

    // Functions to add and remove entities from composite
    fun addChild(c: CompositeEntity) {
        if(data.add(c)) {
            notifyObservers {
                it(EventType.ADD, ObjectType.ENTITY, c, null)
            }
        }
    }

    fun removeChild(e: CompositeEntity) {
        data.remove(e)
        notifyObservers {
            it(EventType.REMOVE, ObjectType.ENTITY, e, null)
        }
    }

    // Function to rename the name of the composite
    fun renameEntity(n: String, c: CompositeEntity) {
        //val indexElement = data.indexOf(c)
        //val element = data.elementAt(indexElement)
        val element = getElement(c)
        if(n != element.name) {
            var oldName = element.name
            element.name = n
            notifyObservers {
                it(EventType.REPLACE, ObjectType.ENTITY, n, oldName)
            }
        }

    }

    // TODO MAKE ATTRIBUTE OBSERVABLE TOO
    // Composite entity and attributes are distinct objects
    // Functions to add and remove entities from composite
    fun addAttribute(a: Attribute, c: CompositeEntity) {
        val element = getElement(c)
        if(element.attrs.add(a)) {
            notifyObservers {
                it(EventType.ADD, ObjectType.ATTRIBUTE, a, null)
            }
        }
    }

    fun removeAttribute(a: Attribute,  c: CompositeEntity) {
        val element = getElement(c)
        if(element.attrs.remove(a)) {
            notifyObservers {
                it(EventType.REMOVE, ObjectType.ATTRIBUTE, a, null)
            }
        }
    }

    // Functions to edit attributes from composite
    //fun renameAttribute(a: Attribute, n: String) {
//
    //    a.name = n
    //}

    fun editAttributeValue(a: Attribute, c: CompositeEntity, v: String) {
        //val element = getElement(c)
        //element.attrs.remove(a)
        //element.attrs.add(Attribute())
    }

}