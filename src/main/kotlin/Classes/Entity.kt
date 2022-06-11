package Classes

import Interfaces.Visitor

/**
 * Attributes:
 *  --> attributes: <entity att="...">
 *  --> text: <entity>anything</entity>
 */
abstract class Entity(var name: String, var attrs: MutableList<Attribute> = mutableListOf(), var text: String = "", var parent: CompositeEntity? = null) {
    init {
        parent?.children?.add(this)
        text = cleanText(text)
    }

    private fun mapEscapingCharacter(t: String): String =
        t
            .replace("&", "&amp")
            .replace("<", "&lt")
            .replace(">", "&gt")
            .replace("‘", "&apos")
            .replace("“", "&quot")

    private fun cleanText(t: String): String {
        val escapingCharacters = arrayOf("<", ">", "&", "‘", "“")
        val match = escapingCharacters.any { it in t }
        return if (match) {
            mapEscapingCharacter(t)
        } else {
            t
        }
    }

    val depth: Int
        get() =
            if (parent == null) 0
            else 1 + parent!!.depth

    fun buildAttrs(): String {
        return if (attrs.isEmpty()) "" else attrs.joinToString(separator = " ", transform = { it.name + "=" + "\"${it.attrValue}\""})//.joinToString{ it.name + "=" + "\"${it.attrValue}\""}
    }

    open fun print() {
        var attrString = buildAttrs()
        var xml = "<$name ${if(attrString != "") attrString else ""}>${text.ifEmpty { "" }}</$name>"
        println(xml)
    }

    open fun getXML(): String {
        var attrString = buildAttrs()
        var xml = "<$name${if(attrString != "") " $attrString" else ""}>${text.ifEmpty { "" }}</$name>\n"
        return xml
    }

    abstract fun accept(v: Visitor)
}