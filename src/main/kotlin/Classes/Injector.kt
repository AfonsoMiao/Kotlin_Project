package Classes

import Annotations.Inject
import Annotations.InjectAdd
import Interfaces.Action
import Interfaces.AttributeFrameSetup
import java.io.File
import java.io.FileInputStream
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.jvm.isAccessible

class Injector {
    fun create(c: KClass<*>): Window {
        val file = File("src/main/kotlin/di.properties")
        val prop = Properties()
        FileInputStream(file).use { prop.load(it) }
        val obj = c.createInstance()
        val className = "${c.simpleName}."
        var classProperty: String = ""
        c.declaredMemberProperties.filter { it.hasAnnotation<Inject>()}
            .forEach {
                classProperty = prop.getProperty(className + it.name)
                val clazz: KClass<*> = Class.forName(classProperty).kotlin
                it.isAccessible = true
                (it as KMutableProperty<*>).setter.call(obj, clazz.createInstance())
            }

        c.declaredMemberProperties.filter { it.hasAnnotation<InjectAdd>() && it.findAnnotation<InjectAdd>()!!.name == "attribute"}
            .forEach {
                classProperty = prop.getProperty(className + it.name)
                var actions = classProperty.split(",")
                actions.forEach {x ->
                    val clazz: KClass<*> = Class.forName(x).kotlin
                    it.isAccessible = true
                    val currentObj = clazz.createInstance()
                    val lst = (it as KMutableProperty<*>).call(obj) as MutableList<AttributeFrameSetup>
                    lst.add(currentObj as AttributeFrameSetup)
                }
            }
        c.declaredMemberProperties.filter { it.hasAnnotation<InjectAdd>() && it.findAnnotation<InjectAdd>()!!.name == "action"}
            .forEach {
                classProperty = prop.getProperty(className + it.name)
                var actions = classProperty.split(",")
                actions.forEach {x ->
                    val clazz: KClass<*> = Class.forName(x).kotlin
                    it.isAccessible = true
                    val currentObj = clazz.createInstance()
                    val lst = (it as KMutableProperty<*>).call(obj) as MutableList<Action>
                    lst.add(currentObj as Action)
                }
            }
        return obj as Window
    }
}