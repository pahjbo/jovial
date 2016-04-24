package cfa.vo.vodml.io

import groovy.util.logging.Slf4j
/**
 * A groovy builder for VODML Instances to be serialized as VOTable.
 * Currently there is not much VOTable specific, and in the future
 * one may desire to decouple the standard instance builder from the
 * serialization format to produce instance serializations in different
 * formats.
 *
 * This is a standard Groovy Builder which extends {@link groovy.util.BuilderSupport},
 * so it can be used as a regular builder.
 *
 * The root element must always be called 'votable'
 *
 * The supported types are the ones extending the {@link cfa.vo.vodml.instance.Instance}
 * interface and listed in the {@link cfa.vo.vodml.instance} package.
 *
 * The `model` clause can be used any number of times to declare models.
 * The builder will index the contents of the model and will resolve attributes names
 * if possible (error handling is still crude, but exceptions should be thrown if
 * names cannot be resolved or are ambiguous).
 *
 * Full `vodml-refs` can be used if necessary.
 *
 * Example:
 *
 * <pre>
 * {@code
 *
 * def reader = new VodmlReader()
 * def modelSpec = reader.read(new URL("file://some/location/to.vodml.xml))
 *
 * def votableInstance = new VoTableBuilder().votable {
 *     model(modelSpec)
 *     object(type: "ds:party.Organization") {
 *         value(role: "name", value:"OrgName")
 *         value(role: "address", value:"An Address")
 *     }
 * }
 * }
 * </pre>
 *
 * In the above example a single model is declared, along with an `objectInstance`, i.e.
 * and instance of an `ObjectType`. Two attributes for the instance are created using the
 * attribute names.
 *
 */

@Slf4j
class VoTableBuilder extends BuilderSupport {
    private static final String MODEL_PACKAGE = "cfa.vo.vodml.instance"

    private static final Map SUPPORTED_MODELS = [build: ArrayList,].withDefault {
        Class.forName("${MODEL_PACKAGE}.${it.capitalize()}Instance")
    }

    @Override
    protected void setParent(Object parent, Object child) {
        log.debug("setting child $child to parent $parent")
        if (parent == child) return

        child.parent = parent
        if (parent.respondsTo("apply")) {
            parent.apply()
        }

        if (parent) {
            log.debug("$parent << $child")
            try {
                parent << child
            } catch (Exception ex) {
                throw new IllegalStateException("Cannot attach node $child to parent $parent.\nDoes ${parent.class.simpleName} implement 'leftShift(${child.class.simpleName} object)'?")
            }
        }

        if (child.respondsTo("apply")) {
            child.apply()
        }
        log.debug("DONE setting child $child to parent $parent")
    }

    @Override
    protected Object createNode(Object name) {
        log.debug("create node with name $name")
        def ret = createNode(name, null, null)
        log.debug("DONE create node with name $name")
        return ret
    }

    @Override
    protected Object createNode(Object name, Object value) {
        log.debug("create node with name $name and value $value")
        def ret = createNode(name, null, value)
        log.debug("DONE create node with name $name and value $value")
        return ret
    }

    @Override
    protected Object createNode(Object name, Map attrs) {
        log.debug("create node with name $name and attrs $attrs")
        def ret = createNode(name, attrs, null)
        log.debug("DONE create node with name $name and attrs $attrs")
        return ret
    }

    @Override
    protected Object createNode(Object name, Map attrs, Object value) {
        try {
            def instance
            if (!value) {
                instance = SUPPORTED_MODELS[name].newInstance()
            } else {
                instance = SUPPORTED_MODELS[name].newInstance(value)
            }
            instance.start(attrs)
            return instance
        }
        catch (ClassNotFoundException ex) {
            if (value && !attrs) {
                current."$name" = value
                return current
            } else {
                throw ex
            }
        }
    }

    @Override
    protected void nodeCompleted(Object parent, Object node) {
        log.debug("finishing node $node in parent $parent")

        if (node.respondsTo("end")) {
            node.end()
        }

        log.debug("DONE")
    }
}
