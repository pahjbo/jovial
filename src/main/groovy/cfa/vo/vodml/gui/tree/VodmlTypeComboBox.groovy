package cfa.vo.vodml.gui.tree

import ca.odell.glazedlists.BasicEventList
import ca.odell.glazedlists.matchers.TextMatcherEditor
import ca.odell.glazedlists.swing.AutoCompleteSupport
import cfa.vo.vodml.utils.Resolver

import javax.swing.*
import java.text.FieldPosition
import java.text.Format
import java.text.ParsePosition

class VodmlTypeComboBox extends JComboBox {
    AutoCompleteSupport acs

    public VodmlTypeComboBox() {
        super()
        def resolver = Resolver.instance
        def elements = resolver.types.keySet() as BasicEventList
        acs = AutoCompleteSupport.install(this, elements, { baseList, element ->
            def type = resolver.resolveType(element.toString())
            baseList.add(type.vodmlid.toString())
            baseList.add(element.toString())
            baseList.add(element.toString().replaceFirst(":", "\\."))
        }, new RefFormat())
        acs.setFilterMode(TextMatcherEditor.CONTAINS)
        acs.setStrict(false)
    }

    private class RefFormat extends Format {
        private Resolver resolver = Resolver.instance

        @Override
        StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
            if (obj != null) {
                return toAppendTo.append(obj.toString().replaceFirst(":", "\\."))
            }
            return toAppendTo
        }

        @Override
        Object parseObject(String source, ParsePosition pos) {
            try {
                return resolver.resolveType(source.toString())
            } catch (Exception ignored) {
                return null
            }
        }
    }
}
