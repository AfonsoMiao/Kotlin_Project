import Classes.Attribute
import Classes.CompositeEntity
import Enumerations.EventType
import Interfaces.IObservable

class Controller (var data: CompositeEntity) : IObservable<(EventType, Any, Any?, Any?) -> Unit>{
    override val observers: MutableList<(EventType, Any, Any?, Any?) -> Unit> = mutableListOf()

    fun addChild(newTagName: String, parent: CompositeEntity, undo: Boolean) {
        println("Create new child")
        println(parent.name)
        val newComposite = CompositeEntity(newTagName, parent = parent)
        parent.print()
        notifyObservers { it(EventType.ADD_TAG, newComposite, parent, null) }
    }

    fun removeChild(tagName: String, parent: CompositeEntity, undo: Boolean) {
        val childRemove = data.searchEntity(tagName)
        parent.children.remove(childRemove)
        childRemove!!.parent = null
        parent.print()
        notifyObservers { it(EventType.REMOVE_TAG, childRemove, parent, null) }
    }

    fun addAttribute(entity: CompositeEntity, a: Attribute, undo: Boolean) {//attName: String, attValue: String) {
        val child = data.searchEntity(entity.name) {it == entity}
        child!!.attrs += a
        child!!.print()
        //data.attrs += a
        notifyObservers { it(EventType.ADD_ATTRIBUTE, entity, a, null) }
    }

    fun removeAttribute(entity: CompositeEntity, attr: Attribute, undo: Boolean) {
        val child = data.searchEntity(entity.name) {it == entity}
        child!!.attrs.remove(attr)
        data.print()
        notifyObservers { it(EventType.REMOVE_ATTRIBUTE, entity, attr, null) }
    }

    fun renameAttributeValue(entity: CompositeEntity, a: Attribute, value: String, undo: Boolean) {
        val element = data.searchEntity(entity.name) {it == entity}
        val elementAttribute = element!!.attrs.find { it.name == a.name }
        elementAttribute!!.attrValue = value
        data.print()
    }

    fun renameAttribute(entity: CompositeEntity, a: Attribute, newName: String, undo: Boolean) {
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

    fun renameTag(entity: CompositeEntity, old: String, newName: String, undo: Boolean) {
        val element = data.searchEntity(entity.name) {it == entity}
        element!!.name = newName
        data.print()
        notifyObservers {
            it(EventType.RENAME_TAG, old, newName, null)
        }
    }
}