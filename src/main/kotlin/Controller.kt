import Classes.Attribute
import Classes.CompositeEntity
import Enumerations.EventType
import Interfaces.IObservable

class Controller (val data: CompositeEntity) : IObservable<(EventType, Any, Any?, Any?) -> Unit>{
    override val observers: MutableList<(EventType, Any, Any?, Any?) -> Unit> = mutableListOf()

    fun addChild(newEntity: CompositeEntity, parent: CompositeEntity) {
        println("Create new child")
        println(parent.name)
        newEntity.parent = parent
        parent.children += newEntity
        parent.print()
        notifyObservers { it(EventType.ADD_TAG, newEntity, parent, null) }
    }

    fun removeChild(newEntity: CompositeEntity, parent: CompositeEntity) {
        println("Removing child ${newEntity.name}")
        val childRemove = data.searchEntity(newEntity.name)
        parent.children.remove(childRemove)
        childRemove!!.parent = null
        parent.print()
        notifyObservers { it(EventType.REMOVE_TAG, childRemove, parent, null) }
    }

    fun addAttribute(entity: CompositeEntity, a: Attribute) {
        val child = data.searchEntity(entity.name) {it == entity}
        child!!.attrs += a
        child.print()
        notifyObservers { it(EventType.ADD_ATTRIBUTE, entity, a, null) }
    }

    fun removeAttribute(entity: CompositeEntity, attr: Attribute) {
        val child = data.searchEntity(entity.name) {it == entity}
        child!!.attrs.remove(attr)
        data.print()
        notifyObservers { it(EventType.REMOVE_ATTRIBUTE, entity, attr, null) }
    }

    fun renameAttributeValue(entity: CompositeEntity, a: Attribute, value: String) {
        val element = data.searchEntity(entity.name) {it == entity}
        val elementAttribute = element!!.attrs.find { it.name == a.name }
        elementAttribute!!.attrValue = value
        data.print()
    }

    fun renameAttribute(entity: CompositeEntity, a: Attribute, newName: String) {
        val child = data.searchEntity(entity.name) {it == entity}
        val childAttribute = child!!.attrs.find { it.name == a.name }
        val oldName = a.name
        childAttribute!!.name = newName
        data.print()
        println(a.name)
        notifyObservers {
            it(EventType.RENAME_ATTRIBUTE, entity, oldName, newName)
        }
    }

    fun renameTag(entity: CompositeEntity, old: String, newName: String) {
        val element = data.searchEntity(entity.name) {it == entity}
        element!!.name = newName
        data.print()
        notifyObservers {
            it(EventType.RENAME_TAG, old, newName, null)
        }
    }

    fun getXML():String {
        return data.getXML()
    }
}