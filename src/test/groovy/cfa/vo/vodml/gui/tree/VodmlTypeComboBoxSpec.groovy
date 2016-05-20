package cfa.vo.vodml.gui.tree

import cfa.vo.vodml.io.ModelBuilder
import cfa.vo.vodml.metamodel.Model
import cfa.vo.vodml.utils.Resolver
import groovy.swing.SwingBuilder
import org.uispec4j.Window
import org.uispec4j.assertion.UISpecAssert
import spock.lang.Specification


class VodmlTypeComboBoxSpec extends Specification {
    private resolver = Resolver.instance
    private swing = new SwingBuilder()

    def "get list of types matching string"() {
        given: "a test model"
        Model model = new ModelBuilder().model(name: "my") {
            primitiveType("PrimitiveTypo")
            dataType("DataTypo")
            objectType("ObjectTypo")
            enumeration("EnumerationTypo", extends: none.someTypo)
            pack("packageTypo") {
                primitiveType("PrimitiveTypo")
                dataType("DataTypo")
                objectType("ObjectTypo")
                enumeration("EnumerationTypo", extends: none.someTypo)
            }
        }
        and: "model is added to resolver"
        resolver << model

        and: "a test frame with a TypComboBox"
        def window
        def jFrame
        swing.registerBeanFactory("typeComboBox", VodmlTypeComboBox)
        swing.edt {
            jFrame = frame(pack: true, show: true) {
                typeComboBox()
            }
        }
        window = new Window(jFrame)

        when: "text is inserted in the comboBox"
        window.getComboBox().text = "packagetypo"

        then:
        def expected = ["my.packageTypo.PrimitiveTypo", "my.packageTypo.DataTypo", "my.packageTypo.ObjectTypo",
                        "my.packageTypo.Enumeration"]
        UISpecAssert.waitUntil({
            def actual = window.comboBox.awtComponent.acs.filteredItems
            if(!actual.equals(expected)) {
                throw new AssertionError("combobox filter is not what it should be: $expected vs $actual")
            }
        }, 1000)

    }
}