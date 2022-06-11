package Entities

import Interfaces.Command

class AddChildCommand(val c: Controller, val newEntity: CompositeEntity/**val newTagName: String*/ , val parent: CompositeEntity) : Command {
    override fun run() {
        c.addChild(newEntity, parent)
    }

    override fun undo() {
        c.removeChild(newEntity, parent)
    }
}

class RemoveChildCommand(val c: Controller, val entity: CompositeEntity, val parent: CompositeEntity) : Command {
    override fun run() {
        c.removeChild(entity, parent)
    }

    override fun undo() {
        c.addChild(entity, parent)
    }
}

class RenameTagCommand(val c: Controller, val old: String, val newTagName: String, val entity: CompositeEntity): Command {
    override fun run() {
        c.renameTag(entity, old, newTagName)
    }

    override fun undo() {
        c.renameTag(entity, newTagName, old)
    }
}

class AddAttributeCommand(val c: Controller, val att: Attribute, val entity: CompositeEntity) : Command {
    override fun run() {
        c.addAttribute(entity, att)
    }

    override fun undo() {
        c.removeAttribute(entity, att)
    }
}

class RenameAttributeNameCommand(val c: Controller, val a: Attribute, val newName: String, val entity: CompositeEntity) : Command {

    val oldName = a.name

    override fun run() {
        c.renameAttribute(entity, a, newName)
    }

    override fun undo() {
        c.renameAttribute(entity,a, oldName)
    }
}

class RenameAttributeValueCommand(val c: Controller, val a: Attribute, val value: String, val entity: CompositeEntity) : Command {
    val oldValue = a.attrValue
    override fun run() {
        c.renameAttributeValue(entity, a, value)
    }

    override fun undo() {
        c.renameAttributeValue(entity, a, oldValue)
    }
}

class RemoveAttributeCommand(val c: Controller, val a: Attribute, val entity: CompositeEntity) : Command {
    override fun run() {
        c.removeAttribute(entity, a)
    }

    override fun undo() {
        c.addAttribute(entity, a)
    }
}