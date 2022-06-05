package Classes

import Controller
import Interfaces.Command

class AddChildCommand(val c: Controller, val newTagName: String, val parent: CompositeEntity) : Command {
    override fun run() {
        c.addChild(newTagName, parent, false)
    }

    override fun undo() {
        c.removeChild(newTagName, parent, true)
    }
}

class RemoveChildCommand(val c: Controller, val newTagName: String, val parent: CompositeEntity) : Command {
    override fun run() {
        println("Called")
        println("Removing child $newTagName with parent ${parent.name}")
        c.removeChild(newTagName, parent, false)
    }

    override fun undo() {
        println("Called undo")
        println("Creating new child $newTagName with parent ${parent.name}")
        parent.print()
        c.addChild(newTagName, parent, true)
    }
}

class RenameTagCommand(val c: Controller, val old: String, val newTagName: String, val entity: CompositeEntity): Command {
    override fun run() {
        c.renameTag(entity, old, newTagName, false)
    }

    override fun undo() {
        c.renameTag(entity, newTagName, old, true)
    }
}

class AddAttributeCommand(val c: Controller, val att: Attribute, val entity: CompositeEntity) : Command {
    override fun run() {
        c.addAttribute(entity, att, false)
    }

    override fun undo() {
        c.removeAttribute(entity, att, true)
    }
}

class RenameAttributeNameCommand(val c: Controller, val a: Attribute, val newName: String, val entity: CompositeEntity) : Command {

    val oldName = a.name

    override fun run() {
        c.renameAttribute(entity, a, newName, false)
    }

    override fun undo() {
        c.renameAttribute(entity,a, oldName, true)
    }
}

class RenameAttributeValueCommand(val c: Controller, val a: Attribute, val value: String, val entity: CompositeEntity) : Command {
    val oldValue = a.attrValue
    override fun run() {
        c.renameAttributeValue(entity, a, value, false)
    }

    override fun undo() {
        c.renameAttributeValue(entity, a, oldValue, true)
    }
}

class RemoveAttributeCommand(val c: Controller, val a: Attribute, val entity: CompositeEntity) : Command {
    override fun run() {
        c.removeAttribute(entity, a, false)
    }

    override fun undo() {
        c.addAttribute(entity, a, true)
    }
}