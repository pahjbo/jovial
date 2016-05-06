package cfa.vo.vodml

import ca.odell.glazedlists.EventList
import cfa.vo.vodml.io.Validator
import cfa.vo.vodml.io.VodmlWriter
import org.custommonkey.xmlunit.XMLAssert
import org.custommonkey.xmlunit.XMLUnit
import org.joda.time.DateTime
import org.junit.Test


class MetaModelTest {
    Model model

    @Test
    void testSerialization() {
        String expected = setUpModel();
        def writer = new VodmlWriter()
        ByteArrayOutputStream os = new ByteArrayOutputStream()
        writer.write(model, os)
        String out = os.toString("UTF-8")
        XMLAssert.assertXMLEqual(out, expected)
        assert new Validator().validate(new ByteArrayInputStream(out.bytes))
    }

    String setUpModel() {
        XMLUnit.ignoreWhitespace = true

        ObjectType role = new ObjectType(
                name: "Role",
                vodmlid: "party.Role",
                references: [
                        new Reference(
                                name: "party",
                                vodmlid: "party.Role.party",
                                dataType: new ElementRef (
                                        vodmlref: "ds:party.Party"
                                )
                        )
                ]
        )

        ObjectType party = new ObjectType(
                name: "Party",
                vodmlid: "party.Party",
                attributes: [
                        new Attribute(
                                name: "name",
                                vodmlid: "party.Party.name",
                                dataType: new ElementRef(vodmlref: "ivoa:string")
                        )
                ]
        )

        ObjectType organization = new ObjectType(
                name: "Organization",
                vodmlid: "party.Organization",
                extends_: new ElementRef ( vodmlref: "ds:party.Party" ),
                attributes: [
                        new Attribute(
                                name: "address",
                                vodmlid: "party.Organization.address",
                                dataType: new ElementRef(vodmlref: "ivoa:string")
                        ),
                        new Attribute(
                                name: "phone",
                                vodmlid: "party.Organization.phone",
                                dataType: new ElementRef(vodmlref: "ivoa:string")
                        ),
                        new Attribute(
                                name: "email",
                                vodmlid: "party.Organization.email",
                                dataType: new ElementRef(vodmlref: "ivoa:string")
                        ),
                        new Attribute(
                                name: "logo",
                                vodmlid: "party.Organization.logo",
                                dataType: new ElementRef(vodmlref: "ivoa:anyURI")
                        )
                ]
        )

        ObjectType individual = new ObjectType(
                name: "Individual",
                description: "",
                vodmlid: "party.Individual",
                extends_: new ElementRef ( vodmlref: "ds:party.Party" ),
                attributes: [
                        new Attribute(
                                name: "email",
                                vodmlid: "party.Individual.email",
                                dataType: new ElementRef(vodmlref: "ivoa:string")
                        ),
                        new Attribute(
                                name: "address",
                                vodmlid: "party.Individual.address",
                                dataType: new ElementRef(vodmlref: "ivoa:string")
                        ),
                        new Attribute(
                                name: "phone",
                                vodmlid: "party.Individual.phone",
                                dataType: new ElementRef(vodmlref: "ivoa:string")
                        )
                ]
        )

        DataType collection = new DataType(
                name: "Collection",
                description: "",
                vodmlid: "dataset.Collection",
                attributes: [
                        new Attribute(
                                name: "name",
                                vodmlid: "dataset.Collection.name",
                                dataType: new ElementRef(vodmlref: "ivoa:string")
                        )
                ]
        )

        DataType contributor = new DataType(
                name: "Contributor",
                description: "",
                vodmlid: "dataset.Contributor",
                extends_: new ElementRef (vodmlref: "ds:party.Role"),
                attributes: [
                        new Attribute(
                                name: "acknowledgment",
                                vodmlid: "dataset.Contributor.acknowledgment",
                                dataType: new ElementRef(vodmlref: "ivoa:string")
                        )
                ]
        )

        DataType creator = new DataType(
                name: "Creator",
                description: "",
                vodmlid: "dataset.Creator",
                extends_: new ElementRef (vodmlref: "ds:party.Role"),
        )

        ObjectType dataId = new ObjectType(
                name: "DataID",
                description: "",
                vodmlid: "dataset.DataID",
                attributes: [
                        new Attribute(
                                name: "title",
                                vodmlid: "dataset.DataID.title",
                                dataType: new ElementRef(vodmlref: "ivoa:string"),
                                multiplicity: new Multiplicity(minOccurs: 0, maxOccurs: 1)
                        ),
                        new Attribute(
                                name: "datasetID",
                                vodmlid: "dataset.DataID.datasetID",
                                dataType: new ElementRef(vodmlref: "ivoa:anyURI"),
                                multiplicity: new Multiplicity(minOccurs: 0, maxOccurs: 1)
                        ),
                        new Attribute(
                                name: "creatorDID",
                                vodmlid: "dataset.DataID.creatorDID",
                                dataType: new ElementRef(vodmlref: "ivoa:anyURI"),
                                multiplicity: new Multiplicity(minOccurs: 0, maxOccurs: 1)
                        ),
                        new Attribute(
                                name: "version",
                                vodmlid: "dataset.DataID.version",
                                dataType: new ElementRef(vodmlref: "ivoa:string"),
                                multiplicity: new Multiplicity(minOccurs: 0, maxOccurs: 1)
                        ),
                        new Attribute(
                                name: "date",
                                vodmlid: "dataset.DataID.version",
                                dataType: new ElementRef(vodmlref: "ivoa:datetime"),
                                multiplicity: new Multiplicity(minOccurs: 0, maxOccurs: 1)
                        ),
                        new Attribute(
                                name: "creationType",
                                vodmlid: "dataset.DataID.creationType",
                                dataType: new ElementRef(vodmlref: "ds:CreationType"),
                                multiplicity: new Multiplicity(minOccurs: 0, maxOccurs: 1)
                        ),
                        new Attribute(
                                name: "creator",
                                vodmlid: "dataset.DataID.creator",
                                dataType: new ElementRef(vodmlref: "ds:dataset.Creator"),
                                multiplicity: new Multiplicity(minOccurs: 0, maxOccurs: 1),
                        ),
                ],
                collections: [
                        new Composition(
                                name: "collection",
                                vodmlid: "dataset.DataID.collection",
                                dataType: new ElementRef(vodmlref: "ds:dataset.Collection"),
                                multiplicity: new Multiplicity(minOccurs: 0, maxOccurs: -1)
                        ),
                        new Composition(
                                name: "contributor",
                                vodmlid: "dataset.DataID.contributor",
                                dataType: new ElementRef(vodmlref: "ds:dataset.Contributor"),
                                multiplicity: new Multiplicity(minOccurs: 0, maxOccurs: -1)
                        )
                ]
        )

        DataType contact = new DataType(
                name: "Contact",
                description: "",
                vodmlid: "dataset.Contact",
                extends_: new ElementRef(vodmlref: "ds:party.Individual"),
        )

        DataType publisher = new DataType(
                name: "Publisher",
                description: "",
                vodmlid: "dataset.Publisher",
                attributes: [
                        new Attribute(
                                name: "publisherId",
                                vodmlid: "dataset.Publisher.publisherId",
                                dataType: new ElementRef(vodmlref: "ivoa:anyURI")
                        )
                ]
        )

        DataType publication = new DataType(
                name: "Publication",
                description: "",
                vodmlid: "dataset.Publication",
                attributes: [
                        new Attribute(
                                name: "refCode",
                                vodmlid: "dataset.Publication.refCode",
                                dataType: new ElementRef(vodmlref: "ivoa:string")
                        )
                ]
        )

        ObjectType curation = new ObjectType(
                name: "Curation",
                description: "",
                vodmlid: "dataset.Curation",
                attributes: [
                        new Attribute(
                                name: "publisherDID",
                                vodmlid: "dataset.Curation.publisherDID",
                                dataType: new ElementRef(vodmlref: "ivoa:anyURI"),
                                multiplicity: new Multiplicity(minOccurs: 0, maxOccurs: 1)
                        ),
                        new Attribute(
                                name: "version",
                                vodmlid: "dataset.Curation.version",
                                dataType: new ElementRef(vodmlref: "ivoa:string"),
                                multiplicity: new Multiplicity(minOccurs: 0, maxOccurs: 1)
                        ),
                        new Attribute(
                                name: "releaseDate",
                                vodmlid: "dataset.Curation.releaseDate",
                                dataType: new ElementRef(vodmlref: "ivoa:datetime"),
                                multiplicity: new Multiplicity(minOccurs: 0, maxOccurs: 1)
                        ),
                        new Attribute(
                                name: "rights",
                                vodmlid: "dataset.Curation.rights",
                                dataType: new ElementRef(vodmlref: "ds:RightsType"),
                                multiplicity: new Multiplicity(minOccurs: 0, maxOccurs: 1)
                        ),
                        new Attribute(
                                name: "publisher",
                                vodmlid: "dataset.Curation.publisher",
                                dataType: new ElementRef(vodmlref: "ds:dataset.Publisher")
                        ),
                        new Attribute(
                                name: "contact",
                                vodmlid: "dataset.Curation.contact",
                                dataType: new ElementRef(vodmlref: "ds:dataset.Contact"),
                                multiplicity: new Multiplicity(minOccurs: 0, maxOccurs: 1),
                        ),
                ],
                collections: [
                        new Composition(
                                name: "reference",
                                vodmlid: "dataset.Curation.reference",
                                dataType: new ElementRef(vodmlref: "ds:dataset.Publication"),
                                multiplicity: new Multiplicity(minOccurs: 0, maxOccurs: -1)
                        )
                ]
        )

        ObjectType dataset = new ObjectType(
                name: "Dataset",
                description: "",
                vodmlid: "dataset.Dataset",
                attributes: [
                        new Attribute(
                                name: "dataProductType",
                                vodmlid: "dataset.Dataset.dataProductType",
                                dataType: new ElementRef(vodmlref: "ds:DataProductType")
                        ),
                        new Attribute(
                                name: "dataProductSubType",
                                vodmlid: "dataset.Dataset.dataProductSubType",
                                dataType: new ElementRef(vodmlref: "ivoa:string"),
                                multiplicity: new Multiplicity(minOccurs: 0, maxOccurs: 1)

                        ),
                ],
                collections: [
                        new Composition(
                                name: "dataID",
                                vodmlid: "dataset.Dataset.dataID",
                                dataType: new ElementRef(vodmlref: "ds:dataset.DataID")
                        ),
                        new Composition(
                                name: "curation",
                                vodmlid: "dataset.Dataset.curation",
                                dataType: new ElementRef(vodmlref: "ds:dataset.Curation")
                        ),
                ]
        )

        Package partyPackage = new Package(
                name: "party",
                vodmlid: "party",
                objectTypes: [ role, party, organization, individual ]
        )

        Package datasetPackage = new Package(
                name: "dataset",
                vodmlid: "dataset",
                objectTypes: [ dataset, dataId, curation ],
                dataTypes: [ collection, creator, contributor, contact, publication, publisher ],
        )

        Enumeration_ dataProductType = new Enumeration_(
                name: "dataProductType",
                vodmlid: "dataset.DataProductType",
                literals: [
                        new EnumLiteral(
                                vodmlid: "dataset.DataProductType.CUBE",
                                name: "CUBE"
                        ),
                        new EnumLiteral(
                                name: "IMAGE",
                                vodmlid: "dataset.DataProductType.IMAGE",
                        ),
                        new EnumLiteral(
                                name: "PHOTOMETRY",
                                vodmlid: "dataset.DataProductType.PHOTOMETRY",
                        ),
                        new EnumLiteral(
                                name: "SPECTRUM",
                                vodmlid: "dataset.DataProductType.SPECTRUM",
                        ),
                        new EnumLiteral(
                                name: "TIMESERIES",
                                vodmlid: "dataset.DataProductType.TIMESERIES",
                        ),
                        new EnumLiteral(
                                name: "SED",
                                vodmlid: "dataset.DataProductType.SED",
                        ),
                        new EnumLiteral(
                                name: "VISIBILITY",
                                vodmlid: "dataset.DataProductType.VISIBILITY",
                        ),
                        new EnumLiteral(
                                name: "EVENT",
                                vodmlid: "dataset.DataProductType.EVENT",
                        ),
                        new EnumLiteral(
                                name: "CATALOG",
                                vodmlid: "dataset.DataProductType.CATALOG",
                        ),
                ]
        )

        Enumeration_ creationType = new Enumeration_(
                name: "CreationType",
                vodmlid: "dataset.CreationType",
                literals: [
                        new EnumLiteral(
                                name: "ARCHIVAL",
                                vodmlid: "dataset.CreationType.ARCHIVAL",
                        ),
                        new EnumLiteral(
                                name: "CUTOUT",
                                vodmlid: "dataset.CreationType.CUTOUT",
                        ),
                        new EnumLiteral(
                                name: "FILTERED",
                                vodmlid: "dataset.CreationType.FILTERED",
                        ),
                        new EnumLiteral(
                                name: "MOSAIC",
                                vodmlid: "dataset.CreationType.MOSAIC",
                        ),
                        new EnumLiteral(
                                name: "SPECTRAL_EXTRACTION",
                                vodmlid: "dataset.CreationType.SPECTRAL_EXTRACTION",
                        ),
                        new EnumLiteral(
                                name: "CATALOG_EXTRACTION",
                                vodmlid: "dataset.CreationType.CATALOG_EXTRACTION",
                        ),
                ]
        )

        Enumeration_ rightsType = new Enumeration_(
                name: "rightsType",
                vodmlid: "dataset.RightsType",
                literals: [
                        new EnumLiteral(
                                name: "PUBLIC",
                                vodmlid: "dataset.RightsType.PUBLIC",
                        ),
                        new EnumLiteral(
                                name: "PROPRIETARY",
                                vodmlid: "dataset.RightsType.PROPRIETARY",
                        ),
                        new EnumLiteral(
                                name: "SECURE",
                                vodmlid: "dataset.RightsType.SECURE",
                        ),
                ]
        )

        ModelImport ivoa = new ModelImport(
                name: "ivoa",
                version: "1.0",
                url: new URL("http://some/url"),
                documentationURL: new URL("http://someother/url)")
        )

        model = new Model(
                name: "ds",
                title: "Dataset Metadata",
                description: "Generic, high-level metadata associated with an IVOA Dataset.",
                imports: [ ivoa ] as EventList,
                authors: [ "Jane Doe", "John Doe" ] as EventList,
                packages: [ datasetPackage, partyPackage ],
                enumerations: [ dataProductType, creationType, rightsType ],
                lastModified: DateTime.parse("2016-04-20T16:44:59.239-04:00")
        )

        return getClass().getResource("/dataset.vo-dml.xml").text
    }
}
