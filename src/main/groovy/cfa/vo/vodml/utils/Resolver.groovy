/*
 * #%L
 * jovial
 * %%
 * Copyright (C) 2016 Smithsonian Astrophysical Observatory
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Smithsonian Astrophysical Observatory nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package cfa.vo.vodml.utils

import cfa.vo.vodml.metamodel.Model
import cfa.vo.vodml.metamodel.Role
import cfa.vo.vodml.metamodel.Type

/**
 * Singleton class that resolves VODML references to types and roles.
 *
 * Models can be added at any time, and they are immediately indexed.
 */
@Singleton
class Resolver {
    private List<Model> models = []
    private Map<VodmlRef, Type> types = [:]
    private Map<VodmlRef, Role> roles = [:]

    /**
     * Resolve a {@link Type} given a reference as a String
     *
     * @param ref A VODML Reference as a String.
     * @return the Type represented by this reference
     */
    public Type resolveType(String ref) {
        def key = new VodmlRef(ref)
        return types[key]
    }

    /**
     * Resolve a {@link Role} given a reference as a String
     * @param ref A VODML Reference as a String.
     * @return the Role represented by this reference
     */
    public Role resolveRole(String ref) {
        def key = new VodmlRef(ref)
        resolveRole(key)
    }

    /**
     * Resolve a {@link Role} by a proper {@link VodmlRef}.
     * @param ref The VODML Reference to resolve
     * @return The resolved Role
     */
    public Role resolveRole(VodmlRef ref) {
        roles[ref]
    }

    /**
     * Inverse resolution of attributes name to a {@link VodmlRef}. This method allows to refer to attributes
     * univocally by their name, assuming the owning type's {@link VodmlRef} is known.
     *
     * @param typeRef The {@link VodmlRef} that own the attribute.
     * @param attributeName The {@link cfa.vo.vodml.metamodel.Attribute}'s name.
     * @return The fully qualified {@link VodmlRef} of the attibute.
     */
    public VodmlRef resolveAttribute(VodmlRef typeRef, String attributeName) {
        if (roles[attributeName]) {
            return roles[attributeName]
        }
        Type type = types[typeRef]
        def matches = match(type, attributeName)
        if (matches.size() == 1) {
            return new VodmlRef(typeRef.prefix, matches[0].vodmlid)
        } else if (matches.size() == 0) {
            throw new IllegalArgumentException(String.format("No Such Attribute '%s' in %s", attributeName, typeRef))
        } else if (matches.size() > 1) {
            throw new IllegalArgumentException(String.format("Ambiguous Attribute '%s' in %s", attributeName, typeRef))
        }
    }

    /**
     * Convenience method to perform the inverse resolution of attribute names using strings
     * @param typeref
     * @param attributeName
     * @return
     */
    public VodmlRef resolveAttribute(String typeref, String attributeName) {
        VodmlRef typeRef = new VodmlRef(typeref)
        resolveAttribute(typeRef, attributeName)
    }

    /**
     * This class extends the left shift operator to let clients load models into the resolver
     *
     * @param spec a {@link Model} object representing the data model to be indexed.
     */
    public leftShift(Model spec) {
        models << spec
        index(spec)
    }

    /**
     * Check whether a string representing a fully qualified {@link VodmlRef} of a {@link Type} represents
     * a supertype, i.e. if child extends parent.
     *
     * @param child
     * @param parent
     * @return
     */
    public boolean "extends"(String child, String parent) {
        this.extends(new VodmlRef(child), new VodmlRef(parent))
    }

    /**
     * Check whether a fully qualified {@link VodmlRef} of a {@link Type} represents
     * a supertype, i.e. if child extends parent.
     * @param child
     * @param parent
     * @return
     */
    public boolean "extends"(VodmlRef child, VodmlRef parent) {
        Type childType = types[child]

        def typeExtends = childType.extends_

        if (typeExtends) {
            def directParent = typeExtends.vodmlref
            if (directParent == parent) {
                true
            } else {
                return "extends"(directParent, parent)
            }
        }
    }

    def getTypesMatching(String s) {
        return types.values().findAll {
            it.name.contains(s)
        }*.vodmlref
    }

    /**
     * Utility method that finds matches for attribute names in the context of their type definition.
     * The function matches attribute names for the type and all its parents.
     *
     * @param type
     * @param attributeName
     * @return
     */
    private match(Type type, String attributeName) {
        def matches = ["attributes", "references", "collections"].findResults {
            if (type.hasProperty(it)) {
                type."$it".findResults {
                    if (it.name == attributeName) {
                        it
                    }
                }
            }
        }.flatten()

        if (type.extends_) {
            type = types[new VodmlRef(type.extends_.vodmlref)]
            matches += match(type, attributeName)
        }
        matches
    }

    private void index(Model spec) {
        indexPackage(spec.name, spec)
        spec.packages.each {
            indexPackage(spec.name, it)
        }
    }

    private indexPackage(String prefix, pkg) {
        ["dataTypes", "objectTypes", "primitiveTypes", "enumerations"].each {
            pkg."$it".each { type ->
                VodmlRef key
                if (!type.vodmlid || !type.vodmlid.prefix) {
                    key = new VodmlRef(prefix, type.vodmlid)
                } else {
                    key = type.vodmlid
                }
                types[key] = type

                ["attributes", "references", "collections"].each {
                    if (type.hasProperty(it)) {
                        type?."$it".each { role ->
                            VodmlRef rkey = role.vodmlid
                            if (!role.vodmlid.prefix) {
                                rkey = new VodmlRef(prefix, role.vodmlid)
                            }
                            roles[rkey] = role
                        }
                    }
                }
            }
        }
    }
}
