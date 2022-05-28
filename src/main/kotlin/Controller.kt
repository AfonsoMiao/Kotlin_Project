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

    fun addAttribute(attName: String, attValue: String) {
        val newAttr = Attribute(attName, attValue)
        data.attrs += newAttr
        notifyObservers { it(EventType.ADD_ATTRIBUTE, newAttr, null) }
    }

    fun removeAttribute(attr: Attribute) {
        //val attrDelete = data.attrs.find { it.name == attrName }
        data.attrs.remove(attr)
        notifyObservers { it(EventType.REMOVE_ATTRIBUTE, attr, null) }
    }

    //fun removeEntity(child: CompositeEntity) {
    //    //data.parent = null
    //    //data.print()
    //    notifyObservers { it(EventType.REMOVE_TAG, child, null) }
    //}

    fun renameAttribute(old: String, newName: String) {
        var element = data.attrs.find { it.name == old }
        element!!.name = newName
        //data.print()
        notifyObservers {
            it(EventType.RENAME_ATTRIBUTE, old, newName)
        }
    }

    fun renameTag(old: String, newName: String) {
        data.name = newName
        //data.print()
        notifyObservers {
            it(EventType.RENAME_TAG, newName, null)
        }
    }
}