package Classes

import Enumerations.EventType
import Interfaces.AttributeFrameSetup
import Interfaces.Command
import Interfaces.IObservable
import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.awt.GridLayout
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.border.CompoundBorder

class ComponentSkeleton (val c: Controller, private val undoStack: UndoStack, private val attributesFrames: MutableList<AttributeFrameSetup>) : JPanel(), IObservable<ComponentSkeleton.ComponentEvent> {
        interface ComponentEvent {
            fun addTag(parent: CompositeEntity, newEntity: CompositeEntity) {}
            fun removeTag(parent: CompositeEntity, entity: CompositeEntity) {}
            fun renameTag(entity: CompositeEntity, newTagName: String) {}

            fun addAttribute(entity: CompositeEntity, attName: String, attValue: String) {}
            fun removeAttribute(entity: CompositeEntity, a: Attribute) {}
            fun renameAttribute(entity: CompositeEntity, a: Attribute, newName: String) {}
            fun editValueAttribute(entity: CompositeEntity, a: Attribute, value: String) {}
        }

        override val observers: MutableList<ComponentEvent> = mutableListOf()
        val listComponents: MutableList<Component> = mutableListOf()

        inner class Component(val e: CompositeEntity, private val undoStack: UndoStack): JPanel() {

            var entityName: String = e.name
            var listAttributes: MutableList<JPanel> = mutableListOf()
            val popupmenu = JPopupMenu("Actions")

            override fun paintComponent(g: Graphics) {
                super.paintComponent(g)
                g.font = Font("Arial", Font.BOLD, 16)
                g.drawString(entityName, 10, 20)
            }

            init {
                layout = GridLayout(0, 1)
                border = CompoundBorder(
                    BorderFactory.createEmptyBorder(30, 10, 10, 10),
                    BorderFactory.createLineBorder(Color.BLACK, 2, true)
                )
                listComponents += this
                createPopupMenu()
                buildAttributes()
                buildChild()
            }

            fun buildChild() {
                this.e.children.forEach {e ->
                    addTag(e as CompositeEntity, this.e)
                }
            }

            private fun buildAttributes() {
                if(e.attrs.size != 0) {
                    for(a in e.attrs) {
                        var frame = attributesFrames.find { a.name.contains(it.typeAttribute) }
                        if(frame != null) {
                            var panel = frame.getFrame(c, e, a, undoStack)
                            listAttributes += panel
                            add(panel)
                        } else {
                            var panel = JPanel()
                            var label = JLabel(a.name)
                            var textField = JTextField(a.attrValue)
                            textField.addKeyListener(object: KeyAdapter() {
                                override fun keyReleased(k: KeyEvent?) {
                                    notifyObservers { it.editValueAttribute(e, a, (k!!.source as JTextField).text) }
                                }
                            })
                            panel.add(label)
                            panel.add(textField)
                            panel.addMouseListener(object: MouseAdapter() {
                                override fun mouseClicked(m: MouseEvent) {
                                    if (SwingUtilities.isRightMouseButton(m)) {
                                        val popupmenu = JPopupMenu("Actions")
                                        val renameAttribute = JMenuItem("Rename Attribute ${a.name}")
                                        renameAttribute.addActionListener {
                                            val text = JOptionPane.showInputDialog("New name")
                                            notifyObservers { it.renameAttribute(e, a, text) }
                                        }
                                        popupmenu.add(renameAttribute)

                                        val removeAttribute = JMenuItem("Remove Attribute ${a.name}")
                                        removeAttribute.addActionListener {
                                            notifyObservers { it.removeAttribute(e, a) }
                                        }
                                        popupmenu.add(removeAttribute)

                                        popupmenu.show(m.component, m.x, m.y)
                                    }
                                }
                            })
                            listAttributes += panel
                            add(panel)
                        }
                    }
                }
            }

            private fun createPopupMenu() {
                val addTag = JMenuItem("Add Tag $entityName")
                addTag.addActionListener {
                    val text = JOptionPane.showInputDialog("New Tag Name")
                    notifyObservers { it.addTag(this.e, CompositeEntity(text)) }
                }
                popupmenu.add(addTag)

                val addAttribute = JMenuItem("Add Attribute $entityName")
                addAttribute.addActionListener {
                    val attName = JOptionPane.showInputDialog("Attribute Name")
                    val attValue = JOptionPane.showInputDialog("Attribute value")

                    notifyObservers { it.addAttribute(this.e, attName, attValue) }
                }
                popupmenu.add(addAttribute)

                val removeTag = JMenuItem("Remove Tag $entityName")
                removeTag.addActionListener {
                    notifyObservers {
                        it.removeTag(this.e.parent!!, e)
                    }
                }
                popupmenu.add(removeTag)

                val renameTag = JMenuItem("Rename Tag")
                renameTag.addActionListener {
                    val text = JOptionPane.showInputDialog("New tag name")
                    notifyObservers { it.renameTag(this.e, text) }
                }
                popupmenu.add(renameTag)

                val stack = JMenuItem("Undo")
                stack.addActionListener {
                    println(undoStack.stack)
                    undoStack.undo()
                }
                popupmenu.add(stack)

                addMouseListener(object : MouseAdapter() {
                    override fun mouseClicked(e: MouseEvent) {
                        if (SwingUtilities.isRightMouseButton(e))
                            popupmenu.show(e.component, e.x, e.y)
                    }
                })
            }
        }

        init {
            layout = GridLayout(0,1)
            fun execute(c: Command) {
                undoStack.execute(c)
            }
            this.addObserver(object: ComponentEvent {
                override fun addTag(parent: CompositeEntity, newEntity: CompositeEntity) {
                    execute(AddChildCommand(c, newEntity, parent))
                }

                override fun removeTag(parent: CompositeEntity, entity: CompositeEntity) {
                    execute(RemoveChildCommand(c, entity, parent))
                }

                override fun renameTag(entity: CompositeEntity, newTagName: String) {
                    execute(RenameTagCommand(c, entity.name, newTagName, entity))
                }

                override fun addAttribute(entity: CompositeEntity, attName: String, attValue: String) {
                    execute(AddAttributeCommand(c, Attribute(attName, attValue), entity))
                }
                override fun removeAttribute(entity: CompositeEntity, a: Attribute) {
                    execute(RemoveAttributeCommand(c, a, entity))
                }
                override fun renameAttribute(entity: CompositeEntity, a: Attribute, newName: String) {
                    execute(RenameAttributeNameCommand(c, a, newName, entity))
                }
                override fun editValueAttribute(entity: CompositeEntity, a: Attribute, value: String) {
                    execute(RenameAttributeValueCommand(c, a, value, entity))
                }
            })
            c.addObserver { e, p, aux, aux2 ->
                when(e) {
                    EventType.ADD_TAG -> addTag(p as CompositeEntity, aux as CompositeEntity)
                    EventType.REMOVE_TAG -> removeTag(p as CompositeEntity, aux as CompositeEntity)
                    EventType.RENAME_TAG -> renameTag(p as String, aux as String)
                    EventType.ADD_ATTRIBUTE -> addAttribute(p as CompositeEntity, aux as Attribute)
                    EventType.REMOVE_ATTRIBUTE -> removeAttribute(p as CompositeEntity, aux as Attribute)
                    EventType.RENAME_ATTRIBUTE -> renameAttributeName(p as CompositeEntity, aux as String, aux2 as String)
                }
            }

            add(Component(c.data, undoStack))
        }

        private fun renameTag(oldTagName: String, newTagName: String) {
            val jComponent = listComponents.find { it.entityName == oldTagName }
            jComponent!!.entityName = newTagName
            revalidate()
            repaint()
        }


        private fun removeAttribute(t:CompositeEntity, a: Attribute) {
            val jComponent = listComponents.find { it.entityName == t.name }
            val jComponentAttribute = jComponent!!.listAttributes.find { (it.getComponent(0)!! as JLabel).text == a.name }
            jComponent.remove(jComponentAttribute!!)
            jComponent.listAttributes.remove(jComponentAttribute)
            revalidate()
            repaint()
        }

        private fun renameAttributeName(t:CompositeEntity, oldName: String, newName: String) {
            val jComponent = listComponents.find { it.entityName == t.name }
            val jComponentAttribute = jComponent!!.listAttributes.find { (it.getComponent(0)!! as JLabel).text == oldName }
            (jComponentAttribute!!.getComponent(0)!! as JLabel).text = newName
            revalidate()
            repaint()
        }

        private fun addAttribute(t: CompositeEntity, a: Attribute) {
            val jComponent = listComponents.find { it.entityName == t.name }
            var panel = JPanel()
            var label = JLabel(a.name)
            var textField = JTextField(a.attrValue)
            textField.addKeyListener(object: KeyAdapter() {
                override fun keyReleased(e: KeyEvent?) {
                    notifyObservers { it.editValueAttribute(t, a, (e!!.source as JTextField).text) }
                }
            })
            panel.add(label)
            panel.add(textField)
            panel.addMouseListener(object: MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        val popupmenu = JPopupMenu("Actions")
                        val renameAttribute = JMenuItem("Rename Attribute ${a.name}")
                        renameAttribute.addActionListener {
                            val text = JOptionPane.showInputDialog("New name")
                            notifyObservers { it.renameAttribute(t, a, text) }
                        }
                        popupmenu.add(renameAttribute)

                        val removeAttribute = JMenuItem("Remove Attribute ${a.name}")
                        removeAttribute.addActionListener {
                            notifyObservers { it.removeAttribute(t, a) }
                        }
                        popupmenu.add(removeAttribute)

                        popupmenu.show(e.component, e.x, e.y)
                    }

                }
            })
            jComponent!!.listAttributes += panel
            jComponent.add(panel)
            revalidate()
            repaint()
        }

        private fun removeTag(t: CompositeEntity, parent: CompositeEntity) {
            val jComponentParent = listComponents.find { it.entityName == parent.name }
            val jComponentChild = listComponents.find { it.entityName == t.name }
            jComponentParent!!.remove(jComponentChild)
            listComponents.remove(jComponentChild)
            revalidate()
            repaint()

        }

        fun addTag(t: CompositeEntity, parent: CompositeEntity) {
            val jComponent = listComponents.find { it.entityName == parent.name }
            jComponent!!.add(Component(t, undoStack))
            revalidate()
            repaint()
        }
}