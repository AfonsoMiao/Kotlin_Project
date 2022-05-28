package Classes

import Controller
import Interfaces.Command

class AddChildCommand(val c: Controller, val newTagName: String) : Command {
    override fun run() {
        c.addChild(newTagName)
    }

    override fun undo() {
        c.removeChild(newTagName)
    }
}

class RenameTagCommand(val c: Controller, val old: String, val newTagName: String): Command {
    override fun run() {
        c.renameTag(old, newTagName)
    }

    override fun undo() {
        c.renameTag(newTagName, old)
    }
}

class AddAttributeCommand(val c: Controller, val att: Attribute) : Command {
    override fun run() {
        c.addAttribute(att)
    }

    override fun undo() {
        c.removeAttribute(att)
    }
}

class RenameAttributeName(val c: Controller, val old: String, val newName: String) : Command {
    override fun run() {
        c.renameAttribute(old, newName)
    }

    override fun undo() {
        c.renameAttribute(newName, old)
    }
}