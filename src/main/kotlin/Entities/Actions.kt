package Entities

import Enumerations.EventType
import Interfaces.AttributeFrameSetup
import Interfaces.RootComponentSetup
import java.awt.BorderLayout
import java.awt.GridLayout
import java.awt.event.*
import javax.swing.*

class RootComponent: RootComponentSetup {
    override val name: String
        get() = "Room"
    override val listAttributes: MutableList<Attribute>
        get() = mutableListOf(Attribute("sala reservada", "true"))
    override fun getRootComponent(): CompositeEntity {
        val root = CompositeEntity("Room", listAttributes)
        CompositeEntity("Person1", attrs = mutableListOf(Attribute("Nome", "Afonso")), parent = root)
        return root
    }

}

//Load this classes into list
class DescriptionFrame: AttributeFrameSetup {
    override val typeAttribute: String
        get() = "descrição"

    override fun execute(c: Controller, entity: CompositeEntity, a: Attribute, newName: String?, value: String?, undoStack: UndoStack, typeExecution: EventType) {
        when(typeExecution) {
            EventType.REMOVE_ATTRIBUTE -> undoStack.execute(RemoveAttributeCommand(c, a, entity))
            EventType.RENAME_ATTRIBUTE -> undoStack.execute(RenameAttributeNameCommand(c, a, newName!!, entity))
            EventType.RENAME_ATTRIBUTE_VALUE -> undoStack.execute(RenameAttributeValueCommand(c, a, value!!, entity))

        }

    }

    override fun getFrame(c: Controller, entity: CompositeEntity, a: Attribute, undoStack: UndoStack): JPanel {
        var panel = JPanel()
        var label = JLabel(a.name)
        var textField = JTextField(a.attrValue)
        textField.addKeyListener(object: KeyAdapter() {
            override fun keyReleased(e: KeyEvent?) {
                execute(c, entity, a, null, (e!!.source as JTextField).text, undoStack, EventType.RENAME_ATTRIBUTE_VALUE)
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
                        execute(c, entity, a, text, null, undoStack, EventType.RENAME_ATTRIBUTE)
                    }
                    popupmenu.add(renameAttribute)

                    val removeAttribute = JMenuItem("Remove Attribute ${a.name}")
                    removeAttribute.addActionListener {
                        execute(c, entity, a, null, null, undoStack, EventType.REMOVE_ATTRIBUTE)
                    }
                    popupmenu.add(removeAttribute)

                    popupmenu.show(e.component, e.x, e.y)
                }
            }
        })
        //panel.size = Dimension(100, 100)
        return panel
    }
}

class DateFrame: AttributeFrameSetup {
    override val typeAttribute: String
        get() = "data"

    override fun execute(c: Controller, entity: CompositeEntity, a: Attribute, newName: String?, value: String?, undoStack: UndoStack, typeExecution: EventType) {
        when(typeExecution) {
            EventType.REMOVE_ATTRIBUTE -> undoStack.execute(RemoveAttributeCommand(c, a, entity))
            EventType.RENAME_ATTRIBUTE -> undoStack.execute(RenameAttributeNameCommand(c, a, newName!!, entity))
            EventType.RENAME_ATTRIBUTE_VALUE -> undoStack.execute(RenameAttributeValueCommand(c, a, value!!, entity))

        }

    }

    override fun getFrame(c: Controller, entity: CompositeEntity, a: Attribute, undoStack: UndoStack): JPanel {
        //var frame = JFrame()
        var panel = JPanel(BorderLayout(2,2))
        panel.layout = GridLayout(0, 4)
        //val a = Attribute(typeAttribute, "1-January-2022")
        val dateArray = a.attrValue.split("-")
        var label = JLabel(a.name)
        var dayList = JComboBox(arrayOf("1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31"))
        var monthList = JComboBox(arrayOf("January","February","March","April","May","June","July","August","September","October","November","December"))
        var yearList = JComboBox(arrayOf("2000","2001","2002","2003","2004","2005","2006","2007","2008","2009","2010","2011","2012","2013","2014","2015","2016","2017","2018","2019","2020","2021","2022","2023","2024","2025","2026","2027","2028","2029","2030"))
        dayList.selectedItem = dateArray[0]
        monthList.selectedItem = dateArray[1]
        yearList.selectedItem = dateArray[2]
        dayList.setSize(100, 100)
        monthList.setSize(100, 100)
        yearList.setSize(100, 100)
        dayList.addItemListener {
            if(it.stateChange == ItemEvent.SELECTED) {
                val newAttributeValue = "${it.item}-${monthList.selectedItem}-${yearList.selectedItem}"
                execute(c, entity, a, null, newAttributeValue, undoStack, EventType.RENAME_ATTRIBUTE_VALUE)
            }
        }
        monthList.addItemListener {
            if(it.stateChange == ItemEvent.SELECTED) {
                val newAttributeValue = "${it.item}-${monthList.selectedItem}-${yearList.selectedItem}"
                execute(c, entity, a, null, newAttributeValue, undoStack, EventType.RENAME_ATTRIBUTE_VALUE)
            }
        }
        yearList.addItemListener {
            if(it.stateChange == ItemEvent.SELECTED) {
                val newAttributeValue = "${it.item}-${monthList.selectedItem}-${yearList.selectedItem}"
                execute(c, entity, a, null, newAttributeValue, undoStack, EventType.RENAME_ATTRIBUTE_VALUE)
            }
        }
        panel.add(label)
        panel.add(dayList)
        panel.add(monthList)
        panel.add(yearList)
        panel.addMouseListener(object: MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    val popupmenu = JPopupMenu("Actions")
                    val renameAttribute = JMenuItem("Rename Attribute ${a.name}")
                    renameAttribute.addActionListener {
                        val text = JOptionPane.showInputDialog("New name")
                        execute(c, entity, a, text, null, undoStack, EventType.RENAME_ATTRIBUTE)
                    }
                    popupmenu.add(renameAttribute)

                    val removeAttribute = JMenuItem("Remove Attribute ${a.name}")
                    removeAttribute.addActionListener {
                        execute(c, entity, a, null, null, undoStack, EventType.REMOVE_ATTRIBUTE)
                    }
                    popupmenu.add(removeAttribute)

                    popupmenu.show(e.component, e.x, e.y)
                }
            }
        })
        return panel
    }
}

class MandatoryFrame: AttributeFrameSetup {
    override val typeAttribute: String
        get() = "reservada"

    override fun execute(c: Controller, entity: CompositeEntity, a: Attribute, newName: String?, value: String?, undoStack: UndoStack, typeExecution: EventType) {
        when(typeExecution) {
            EventType.REMOVE_ATTRIBUTE -> undoStack.execute(RemoveAttributeCommand(c, a, entity))
            EventType.RENAME_ATTRIBUTE -> undoStack.execute(RenameAttributeNameCommand(c, a, newName!!, entity))
            EventType.RENAME_ATTRIBUTE_VALUE -> undoStack.execute(RenameAttributeValueCommand(c, a, value!!, entity))

        }

    }

    override fun getFrame(c: Controller, entity: CompositeEntity, a: Attribute, undoStack: UndoStack): JPanel {
        var panel = JPanel(BorderLayout(2,2))
        //val a = Attribute(typeAttribute, "true")
        var checkBox = JCheckBox(a.name)
        checkBox.horizontalTextPosition = JCheckBox.LEFT
        checkBox.isSelected = a.attrValue.toBoolean()
        checkBox.addItemListener {
            val state = if(it.stateChange == ItemEvent.SELECTED) "true" else "false"
            execute(c, entity, a, null, state, undoStack, EventType.RENAME_ATTRIBUTE_VALUE)
        }
        panel.addMouseListener(object: MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    val popupmenu = JPopupMenu("Actions")
                    val renameAttribute = JMenuItem("Rename Attribute ${a.name}")
                    renameAttribute.addActionListener {
                        val text = JOptionPane.showInputDialog("New name")
                        execute(c, entity, a, text, null, undoStack, EventType.RENAME_ATTRIBUTE)
                    }
                    popupmenu.add(renameAttribute)

                    val removeAttribute = JMenuItem("Remove Attribute ${a.name}")
                    removeAttribute.addActionListener {
                        execute(c, entity, a, null, null, undoStack, EventType.REMOVE_ATTRIBUTE)
                    }
                    popupmenu.add(removeAttribute)

                    popupmenu.show(e.component, e.x, e.y)
                }
            }
        })
        panel.add(checkBox)
        return panel
    }
}


interface Action {
    val actionName: String
    val parentName: String
    fun execute(c: Controller, parent: CompositeEntity, undoStack: UndoStack)
}

class EventAdd: Action {
    override val actionName: String
        get() = "Event"

    override val parentName: String
        get() = "Room"

    override fun execute(c: Controller, parent: CompositeEntity, undoStack: UndoStack) {
        undoStack.execute(AddChildCommand(c, CompositeEntity(actionName, mutableListOf(Attribute("descrição", ""), Attribute("data", "1-January-2023"), Attribute("reservada", "true"))), parent))
    }

}