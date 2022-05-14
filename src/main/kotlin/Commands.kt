import java.util.*

interface Command {
    fun run()
    fun undo()
}

class UndoStack {
    val stack = Stack<Command>()

    fun execute(c: Command) {
        c.run()
        stack.add(c)
    }

    fun undo() {
        if (stack.isNotEmpty())
            stack.pop().undo()
    }
}


class AddEntityCommand(val model: CompositeEntity, val entity: CompositeEntity) : Command {
    override fun run() {
        model.addChild(entity)
    }

    override fun undo() {
        model.removeChild(entity)
    }
}

class RenameEntityCommand(val model: CompositeEntity, val n:String) : Command {
    private val currentName = model.name
    override fun run() {
        model.renameEntity(n)
    }

    override fun undo() {
        model.renameEntity(currentName)
    }
}

class AddAttributeCommand(val model: CompositeEntity, val attribute: Attribute) : Command {
    override fun run() {
        model.addAttribute(attribute)
    }

    override fun undo() {
        model.removeAttribute(attribute)
    }
}

class RenameAttributeCommand(val model: CompositeEntity, val attribute: Attribute, val n: String) : Command {
    private val currentName = attribute.name

    override fun run() {
        model.renameAttribute(attribute, n)
    }

    override fun undo() {
        model.renameAttribute(attribute, currentName)
    }
}

class EditAttributeValueCommand(val model: CompositeEntity, val attribute: Attribute, val n: String) : Command {
    private val currentValue = attribute.attrValue

    override fun run() {
        model.editAttributeValue(attribute, n)
    }

    override fun undo() {
        model.editAttributeValue(attribute, currentValue)
    }
}


