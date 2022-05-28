import Classes.Attribute
import Classes.CompositeEntity
import Enumerations.EventType
import Interfaces.IObservable

class Controller (var data: CompositeEntity) : IObservable<(EventType, Any, Any?) -> Unit>{
    override val observers: MutableList<(EventType, Any, Any?) -> Unit> = mutableListOf()


    fun addChild(newTagName: String) {
        val newComposite = CompositeEntity(newTagName, parent = data)
        data.children += newComposite
        notifyObservers { it(EventType.ADD_TAG, newComposite, null) }
    }

    fun removeChild(tagName: String) {
        //data.print()
        //val childRemove = data.children.find { it.name == tagName }
        //data.children.remove(childRemove)
        data.parent!!.children.remove(data)
        data.parent = null
        notifyObservers { it(EventType.REMOVE_TAG, data, null) }
    }

    fun addAttribute(a: Attribute) {//attName: String, attValue: String) {
        //val newAttr = Attribute(attName, attValue)
        data.attrs += a
        notifyObservers { it(EventType.ADD_ATTRIBUTE, a, null) }
    }

    fun removeAttribute(attr: Attribute) {
        //val attrDelete = data.attrs.find { it.name == attrName }
        data.attrs.remove(attr)
        notifyObservers { it(EventType.REMOVE_ATTRIBUTE, attr, null) }
    }

    fun renameAttributeValue(a: Attribute, value: String) {
        val element = data.attrs.find { it == a }
        element!!.attrValue = value
        println("New value of attribute ${element!!.name}: $value")
        notifyObservers { it(EventType.RENAME_ATTRIBUTE_VALUE, element, null) }
    }

    fun renameAttribute(old: String, newName: String) {
        var element = data.attrs.find { it.name == old }
        element!!.name = newName
        notifyObservers {
            it(EventType.RENAME_ATTRIBUTE, old, newName)
        }
    }

    fun renameTag(old: String, newName: String) {
        data.name = newName
        notifyObservers {
            it(EventType.RENAME_TAG, newName, null)
        }
    }
}