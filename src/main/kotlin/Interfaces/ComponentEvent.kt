package Interfaces

import Classes.Attribute
import Classes.CompositeEntity

interface ComponentEvent {

    fun addTag(parent: CompositeEntity, newTagName: String) {}
    fun removeTag(parent: CompositeEntity, removeTagName: String) {}
    fun renameTag(entity: CompositeEntity, newTagName: String) {}

    fun addAttribute(entity: CompositeEntity, attName: String, attValue: String) {}
    fun removeAttribute(entity: CompositeEntity, a: Attribute) {}
    fun renameAttribute(entity: CompositeEntity, a: Attribute, newName: String) {}
    fun editValueAttribute(entity: CompositeEntity, a: Attribute, value: String) {}
}