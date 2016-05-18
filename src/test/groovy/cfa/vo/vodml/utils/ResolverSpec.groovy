package cfa.vo.vodml.utils

import cfa.vo.vodml.io.ModelBuilder
import cfa.vo.vodml.metamodel.Model
import spock.lang.Specification


class ResolverSpec extends Specification {
    def "get list of types matching string"() {
        given:
        def resolver = Resolver.instance
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
        when:
        resolver << model
        and:
        def types = resolver.getTypesMatching("Typo")
        and:
        def expected = ["my:PrimitiveTypo", "my:DataTypo", "my:ObjectTypo", "my:EnumerationTypo",
         "my:packageTypo.PrimitiveTypo", "my:packageTypo:DataTypo", "my:packageTypo:ObjectTypo",
         "my:packageTypo.EnumerationTypo"]
        def actual = types*.toString()
        def sortedExpected = Collections.sort(expected)
        def sortedActual = Collections.sort(actual)
        then:
        sortedExpected == sortedActual
    }
}
