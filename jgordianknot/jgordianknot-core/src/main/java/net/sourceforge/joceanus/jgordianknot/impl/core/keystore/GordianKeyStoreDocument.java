/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2020 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.impl.core.keystore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.x500.X500Name;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianKnuthObfuscater;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianCertificateId;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianKeySetHashSpecASN1;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianKeyStoreElement.GordianKeyStoreCertificateElement;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianKeyStoreElement.GordianKeyStoreCertificateKey;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianKeyStoreElement.GordianKeyStoreHashElement;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianKeyStoreElement.GordianKeyStoreKeyElement;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianKeyStoreElement.GordianKeyStorePairElement;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianKeyStoreElement.GordianKeyStoreSetElement;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;
import net.sourceforge.joceanus.jtethys.date.TethysDate;

/**
 * KeyStore Document.
 */
public final class GordianKeyStoreDocument {
    /**
     * The Invalid XML error.
     */
    private static final String ERROR_BADXML = "Invalid XML";

    /**
     * The KeyStore element.
     */
    private static final String DOC_KEYSTORE = "KeyStore";

    /**
     * The KeySetSpec attribute.
     */
    private static final String ATTR_KEYSETSPEC = "KeySetSpec";

    /**
     * The CreationDate attribute.
     */
    private static final String ATTR_DATE = "CreationDate";

    /**
     * The Aliases element.
     */
    private static final String ELEMENT_ALIASES = "Aliases";

    /**
     * The Alias element.
     */
    private static final String ATTR_ALIAS = "Alias";

    /**
     * The Certificates element.
     */
    private static final String ELEMENT_CERTIFICATES = "Certificates";

    /**
     * The Certificate element.
     */
    private static final String ELEMENT_CERT = "Certificate";

    /**
     * The keyType attribute.
     */
    private static final String ATTR_KEYSPEC = "KeySpec";

    /**
     * The securedKey element.
     */
    private static final String ELEMENT_SECUREDKEY = "SecuredKey";

    /**
     * The hash element.
     */
    private static final String ELEMENT_HASH = "HashBytes";

    /**
     * The CertificateKey element.
     */
    private static final String ELEMENT_CERTKEY = "CertificateKey";

    /**
     * The Subject element.
     */
    private static final String ELEMENT_SUBJECT = "Subject";

    /**
     * The Issuer element.
     */
    private static final String ELEMENT_ISSUER = "Issuer";

    /**
     * The Name element.
     */
    private static final String ELEMENT_NAME = "Name";

    /**
     * The Id element.
     */
    private static final String ELEMENT_ID = "Id";

    /**
     * The keyStore.
     */
    private final GordianCoreKeyStore theKeyStore;

    /**
     * The document.
     */
    private final Document theDocument;

    /**
     * Constructor from keyStore.
     * @param pKeyStore the keyStore
     * @throws OceanusException on error
     */
    public GordianKeyStoreDocument(final GordianCoreKeyStore pKeyStore) throws OceanusException {
        try {
            /* Store the keyStore */
            theKeyStore = pKeyStore;

            /* Create the document */
            final DocumentBuilderFactory myFactory = DocumentBuilderFactory.newInstance();
            myFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            myFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            myFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            final DocumentBuilder myBuilder = myFactory.newDocumentBuilder();
            theDocument = myBuilder.newDocument();

            /* Create the main document node */
            final Element myMain = theDocument.createElement(DOC_KEYSTORE);
            theDocument.appendChild(myMain);

            /* Record the keySetSpec */
            final GordianKeySetHashSpecASN1 mySpecASN1 = new GordianKeySetHashSpecASN1(pKeyStore.getKeySetSpec());
            final String myAttrSpec = TethysDataConverter.byteArrayToBase64(mySpecASN1.getEncodedBytes());
            myMain.setAttribute(ATTR_KEYSETSPEC, myAttrSpec);

            /* Create the aliases */
            final Element myAliases = theDocument.createElement(ELEMENT_ALIASES);
            myMain.appendChild(myAliases);
            buildAliases(myAliases);

            /* Create the certificates */
            final Element myCerts = theDocument.createElement(ELEMENT_CERTIFICATES);
            myMain.appendChild(myCerts);
            buildCertificates(myCerts);

        } catch (ParserConfigurationException e) {
            throw new GordianIOException("Failed to initialise author", e);
        }
    }

    /**
     * Constructor from document.
     * @param pFactory the factory
     * @param pDocument the document
     * @throws OceanusException on error
     */
    public GordianKeyStoreDocument(final GordianFactory pFactory,
                                   final Document pDocument) throws OceanusException {
        /* Access the document element */
        final Element myDocElement = pDocument.getDocumentElement();

        /* Check that it is correct document */
        if (!DOC_KEYSTORE.equals(myDocElement.getNodeName())) {
            throw new GordianDataException("Invalid Document");
        }

        /* Access the keySetSpec */
        final String myAttrSpec = myDocElement.getAttribute(ATTR_KEYSETSPEC);
        final byte[] myAttrArray = TethysDataConverter.base64ToByteArray(myAttrSpec);
        final GordianKeySetHashSpecASN1 mySpecASN1 = GordianKeySetHashSpecASN1.getInstance(myAttrArray);

        /* Create the empty keyStore */
        theKeyStore = new GordianCoreKeyStore((GordianCoreFactory) pFactory, mySpecASN1.getSpec());
        theDocument = pDocument;

        /* Loop through the nodes */
        Node myNode = myDocElement.getFirstChild();
        while (myNode != null) {
            /* Access the Node name */
            final String myNodeName = myNode.getNodeName();

            /* If this is the aliases Node */
            if (ELEMENT_ALIASES.equals(myNodeName)) {
                /* Parse the aliases */
                parseAliases(myNode);
            }

            /* If this is the certificates Node */
            if (ELEMENT_CERTIFICATES.equals(myNodeName)) {
                /* Parse the certificates */
                parseCertificates(myNode);
            }

            /* Move to next node */
            myNode = myNode.getNextSibling();
        }
    }

    /**
     * Obtain the keyStore.
     * @return the keyStore
     */
    public GordianCoreKeyStore getKeyStore() {
        return theKeyStore;
    }

    /**
     * Obtain the document.
     * @return the document
     */
    public Document getDocument() {
        return theDocument;
    }

    /**
     * build the aliases.
     * @param pAliases the aliases element
     * @throws OceanusException on error
     */
    private void buildAliases(final Node pAliases) throws OceanusException {
        /* Access the Alias entries */
        for (Entry<String, GordianCoreKeyStoreEntry> myEntry : theKeyStore.getAliasMap().entrySet()) {
            /* Determine the entry type */
            final GordianCoreKeyStoreEntry myElement = myEntry.getValue();
            final GordianStoreEntryType myType = GordianStoreEntryType.determineEntryType(myElement);

            /* Build alias entry */
            final Element myAliasEl = theDocument.createElement(myType.getElementName());
            myAliasEl.setAttribute(ATTR_ALIAS, myEntry.getKey());
            myAliasEl.setAttribute(ATTR_DATE, myElement.getCreationDate().toString());
            pAliases.appendChild(myAliasEl);

            /* Switch on element type */
            switch (myType) {
                case KEY:
                    buildKeyElement(myAliasEl, (GordianKeyStoreKeyElement<?>) myElement);
                    break;
                case KEYSET:
                    buildKeySetElement(myAliasEl, (GordianKeyStoreSetElement) myElement);
                    break;
                case KEYSETHASH:
                    buildKeySetHashElement(myAliasEl, (GordianKeyStoreHashElement) myElement);
                    break;
                case PRIVATEKEY:
                    buildPrivateKeyElement(myAliasEl, (GordianKeyStorePairElement) myElement);
                    break;
                case TRUSTEDCERT:
                default:
                    buildCertificateElement(myAliasEl, (GordianKeyStoreCertificateElement) myElement);
                    break;
            }
        }
    }

    /**
     * build the keySetHash element.
     * @param pNode the keySetHash node to build
     * @param pEntry the keySetHash entry
     */
    private void buildKeySetHashElement(final Element pNode,
                                        final GordianKeyStoreHashElement pEntry) {
        /* Build hash entry */
        final Element myHashEl = theDocument.createElement(ELEMENT_HASH);
        pNode.appendChild(myHashEl);
        myHashEl.setTextContent(TethysDataConverter.byteArrayToBase64(pEntry.getHash()));
    }

    /**
     * build the keySet element.
     * @param pNode the keySet node to build
     * @param pEntry the keySet entry
     */
    private void buildKeySetElement(final Element pNode,
                                    final GordianKeyStoreSetElement pEntry) {
        /* Build securedKey entry */
        final Element myKeyEl = theDocument.createElement(ELEMENT_SECUREDKEY);
        pNode.appendChild(myKeyEl);
        myKeyEl.setTextContent(TethysDataConverter.byteArrayToBase64(pEntry.getSecuredKeySet()));

        /* Build hash entry */
        final Element myHashEl = theDocument.createElement(ELEMENT_HASH);
        pNode.appendChild(myHashEl);
        myHashEl.setTextContent(TethysDataConverter.byteArrayToBase64(pEntry.getSecuringHash()));
    }

    /**
     * build the key element.
     * @param pNode the key node to build
     * @param pEntry the key entry
     * @throws OceanusException on error
     */
    private void buildKeyElement(final Element pNode,
                                 final GordianKeyStoreKeyElement<?> pEntry) throws OceanusException {
        /* Build securedKey entry */
        final Element myKeyEl = theDocument.createElement(ELEMENT_SECUREDKEY);
        pNode.appendChild(myKeyEl);
        myKeyEl.setTextContent(TethysDataConverter.byteArrayToBase64(pEntry.getSecuredKey()));

        /* Add the keySpec */
        final GordianKnuthObfuscater myObfuscater = theKeyStore.getFactory().getObfuscater();
        final int myId = myObfuscater.deriveExternalIdFromType(pEntry.getKeyType());
        myKeyEl.setAttribute(ATTR_KEYSPEC, Integer.toString(myId));

        /* Build hash entry */
        final Element myHashEl = theDocument.createElement(ELEMENT_HASH);
        pNode.appendChild(myHashEl);
        myHashEl.setTextContent(TethysDataConverter.byteArrayToBase64(pEntry.getSecuringHash()));
    }

    /**
     * build the privateKey element.
     * @param pNode the privateKey node to build
     * @param pEntry the privateKey entry
     * @throws OceanusException on error
     */
    private void buildPrivateKeyElement(final Element pNode,
                                        final GordianKeyStorePairElement pEntry) throws OceanusException {
        /* Build securedKey entry */
        final Element myKeyEl = theDocument.createElement(ELEMENT_SECUREDKEY);
        pNode.appendChild(myKeyEl);
        myKeyEl.setTextContent(TethysDataConverter.byteArrayToBase64(pEntry.getSecuredKey()));

        /* Build hash entry */
        final Element myHashEl = theDocument.createElement(ELEMENT_HASH);
        pNode.appendChild(myHashEl);
        myHashEl.setTextContent(TethysDataConverter.byteArrayToBase64(pEntry.getSecuringHash()));

        /* Build certificates entry */
        final Element myChainEl = theDocument.createElement(ELEMENT_CERTIFICATES);
        pNode.appendChild(myChainEl);

        /* Build the certificate chain */
        for (GordianKeyStoreCertificateKey myCert : pEntry.getCertificateChain()) {
            /* Build certificateKey */
            buildCertificateKey(myChainEl, myCert);
        }
    }

    /**
     * build the certificate element.
     * @param pNode the certificate node to build
     * @param pEntry the certificate entry
     * @throws OceanusException on error
     */
    private void buildCertificateElement(final Element pNode,
                                         final GordianKeyStoreCertificateElement pEntry) throws OceanusException {
        /* Build certificateKey */
        buildCertificateKey(pNode, pEntry.getCertificateKey());
    }

    /**
     * build the certificateKey element.
     * @param pNode the holding node
     * @param pKey the certificate key
     * @throws OceanusException on error
     */
    private void buildCertificateKey(final Element pNode,
                                     final GordianKeyStoreCertificateKey pKey) throws OceanusException {
        /* Build certificateKey entry */
        final Element myKeyEl = theDocument.createElement(ELEMENT_CERTKEY);
        pNode.appendChild(myKeyEl);

        /* Build subject entry */
        final Element mySubEl = theDocument.createElement(ELEMENT_SUBJECT);
        myKeyEl.appendChild(mySubEl);
        buildCertificateId(mySubEl, pKey.getSubject());

        /* Build issuer entry */
        final Element myIssEl = theDocument.createElement(ELEMENT_ISSUER);
        myKeyEl.appendChild(myIssEl);
        buildCertificateId(myIssEl, pKey.getIssuer());
    }

    /**
     * build the certificateId element.
     * @param pNode the holding node
     * @param pId the certificate id
     * @throws OceanusException on error
     */
    private void buildCertificateId(final Element pNode,
                                    final GordianCertificateId pId) throws OceanusException {
        /* protecte against exceptions */
        try {
            /* Build Name entry */
            final Element myNameEl = theDocument.createElement(ELEMENT_NAME);
            pNode.appendChild(myNameEl);
            myNameEl.setTextContent(TethysDataConverter.byteArrayToBase64(pId.getName().toASN1Primitive().getEncoded()));

            /* Build id entry */
            final Element myIdEl = theDocument.createElement(ELEMENT_ID);
            pNode.appendChild(myIdEl);
            myIdEl.setTextContent(TethysDataConverter.byteArrayToBase64(pId.getId().getEncoded()));

            /* Handle exceptions */
        } catch (IOException e) {
            throw new GordianIOException("Failed to parse certificate Id", e);
        }
    }

    /**
     * build the certificates.
     * @param pCerts the certificates element
     */
    private void buildCertificates(final Node pCerts) {
        /* Access the Subject MapOfMaps */
        for (Map<GordianCertificateId, GordianCoreKeyPairCertificate> myMap : theKeyStore.getSubjectMapOfMaps().values()) {
            for (GordianCoreKeyPairCertificate myCert : myMap.values()) {
                /* Build certificate entry */
                final Element myCertEl = theDocument.createElement(ELEMENT_CERT);
                pCerts.appendChild(myCertEl);

                /* Build the certificate element */
                myCertEl.setTextContent(TethysDataConverter.byteArrayToBase64(myCert.getEncoded()));
            }
        }
    }

    /**
     * parse the aliases.
     * @param pAliases the aliases element
     * @throws OceanusException on error
     */
    private void parseAliases(final Node pAliases) throws OceanusException {
        /* Loop through the nodes */
        Node myNode = pAliases.getFirstChild();
        while (myNode != null) {
            /* Determine the entry type */
            final GordianStoreEntryType myType = GordianStoreEntryType.determineEntryType(myNode.getNodeName());

            /* Access alias and creationDate */
            final String myAlias = ((Element) myNode).getAttribute(ATTR_ALIAS);
            final TethysDate myDate = new TethysDate(((Element) myNode).getAttribute(ATTR_DATE));

            /* Switch on element type */
            switch (myType) {
                case KEY:
                    parseKeyElement(myNode, myAlias, myDate);
                    break;
                case KEYSET:
                    parseKeySetElement(myNode, myAlias, myDate);
                    break;
                case KEYSETHASH:
                    parseKeySetHashElement(myNode, myAlias, myDate);
                    break;
                case PRIVATEKEY:
                    parsePrivateKeyElement(myNode, myAlias, myDate);
                    break;
                case TRUSTEDCERT:
                default:
                    parseCertificateElement(myNode, myAlias, myDate);
                    break;
            }

            /* Move to next node */
            myNode = myNode.getNextSibling();
        }
    }

    /**
     * parse the keySetHash alias.
     * @param pNode the node to parse
     * @param pAlias the alias
     * @param pDate the creation date
     */
    private void parseKeySetHashElement(final Node pNode,
                                        final String pAlias,
                                        final TethysDate pDate) {
        /* Loop through the nodes */
        Node myNode = pNode.getFirstChild();
        while (myNode != null) {
            /* Access the Node name */
            final String myNodeName = myNode.getNodeName();

            /* If this is a hash node */
            if (ELEMENT_HASH.equals(myNodeName)) {
                /* Obtain the hash and build the entry */
                final byte[] myHash = TethysDataConverter.base64ToByteArray(myNode.getTextContent());
                final GordianKeyStoreHashElement myEntry = new GordianKeyStoreHashElement(myHash, pDate);
                theKeyStore.getAliasMap().put(pAlias, myEntry);
                return;
            }

            /* Move to next node */
            myNode = myNode.getNextSibling();
        }
    }

    /**
     * parse the keySet alias.
     * @param pNode the node to parse
     * @param pAlias the alias
     * @param pDate the creation date
     * @throws OceanusException on error
     */
    private void parseKeySetElement(final Node pNode,
                                    final String pAlias,
                                    final TethysDate pDate) throws OceanusException {
        /* Loop through the nodes */
        byte[] mySecuredKey = null;
        Node myNode = pNode.getFirstChild();
        while (myNode != null) {
            /* Access the Node name */
            final String myNodeName = myNode.getNodeName();

            /* If this is a securedKey node */
            if (ELEMENT_SECUREDKEY.equals(myNodeName)) {
                /* Obtain the securedKey */
                mySecuredKey = TethysDataConverter.base64ToByteArray(myNode.getTextContent());
            }

            /* If this is a hash node */
            if (ELEMENT_HASH.equals(myNodeName)) {
                /* Check for valid xml */
                if (mySecuredKey == null) {
                    throw new GordianDataException(ERROR_BADXML);
                }

                /* Obtain the hash and build the entry */
                final byte[] myHash = TethysDataConverter.base64ToByteArray(myNode.getTextContent());
                final GordianKeyStoreSetElement myEntry = new GordianKeyStoreSetElement(mySecuredKey, myHash, pDate);
                theKeyStore.getAliasMap().put(pAlias, myEntry);
                return;
            }

            /* Move to next node */
            myNode = myNode.getNextSibling();
        }
    }

    /**
     * parse the key alias.
     * @param pNode the node to parse
     * @param pAlias the alias
     * @param pDate the creation date
     * @throws OceanusException on error
     */
    private void parseKeyElement(final Node pNode,
                                 final String pAlias,
                                 final TethysDate pDate) throws OceanusException {
        /* Loop through the nodes */
        byte[] mySecuredKey = null;
        GordianKeySpec mySpec = null;
        Node myNode = pNode.getFirstChild();
        while (myNode != null) {
            /* Access the Node name */
            final String myNodeName = myNode.getNodeName();

            /* If this is a securedKey node */
            if (ELEMENT_SECUREDKEY.equals(myNodeName)) {
                /* Obtain the securedKey */
                mySecuredKey = TethysDataConverter.base64ToByteArray(myNode.getTextContent());

                /* Obtain the keySpec */
                final GordianKnuthObfuscater myObfuscater = theKeyStore.getFactory().getObfuscater();
                final String mySpecId = ((Element) myNode).getAttribute(ATTR_KEYSPEC);
                mySpec = (GordianKeySpec) myObfuscater.deriveTypeFromExternalId(Integer.parseInt(mySpecId));
            }

            /* If this is a hash node */
            if (ELEMENT_HASH.equals(myNodeName)) {
                /* Check for valid xml */
                if (mySecuredKey == null) {
                    throw new GordianDataException(ERROR_BADXML);
                }

                /* Obtain the hash and build the entry */
                final byte[] myHash = TethysDataConverter.base64ToByteArray(myNode.getTextContent());
                final GordianKeyStoreKeyElement<GordianKeySpec> myEntry = new GordianKeyStoreKeyElement<>(mySpec, mySecuredKey, myHash, pDate);
                theKeyStore.getAliasMap().put(pAlias, myEntry);
                return;
            }

            /* Move to next node */
            myNode = myNode.getNextSibling();
        }
    }


    /**
     * parse the key alias.
     * @param pNode the node to parse
     * @param pAlias the alias
     * @param pDate the creation date
     * @throws OceanusException on error
     */
    private void parsePrivateKeyElement(final Node pNode,
                                        final String pAlias,
                                        final TethysDate pDate) throws OceanusException {
        /* Loop through the nodes */
        byte[] mySecuredKey = null;
        byte[] myHash = null;
        final List<GordianKeyStoreCertificateKey> myChain = new ArrayList<>();
        Node myNode = pNode.getFirstChild();
        while (myNode != null) {
            /* Access the Node name */
            final String myNodeName = myNode.getNodeName();

            /* If this is a securedKey node */
            if (ELEMENT_SECUREDKEY.equals(myNodeName)) {
                /* Obtain the securedKey */
                mySecuredKey = TethysDataConverter.base64ToByteArray(myNode.getTextContent());
            }

            /* If this is a hash node */
            if (ELEMENT_HASH.equals(myNodeName)) {
                /* Obtain the hash */
                myHash = TethysDataConverter.base64ToByteArray(myNode.getTextContent());
            }

            /* If this is a hash node */
            if (ELEMENT_CERTIFICATES.equals(myNodeName)) {
                /* Check for valid xml */
                if (mySecuredKey == null || myHash == null) {
                    throw new GordianDataException(ERROR_BADXML);
                }

                /* Loop through the certificates */
                Node myChild = myNode.getFirstChild();
                while (myChild != null) {
                    /* Access the Node name */
                    final String myName = myChild.getNodeName();

                    /* If this is a certificateKey node */
                    if (ELEMENT_CERTKEY.equals(myName)) {
                        /* Add the certficate to the list */
                        myChain.add(parseCertificateKey(myChild));
                    }

                    /* Move to next node */
                    myChild = myChild.getNextSibling();
                }

                /* build the entry */
                final GordianKeyStorePairElement myEntry = new GordianKeyStorePairElement(mySecuredKey, myHash, myChain, pDate);
                theKeyStore.getAliasMap().put(pAlias, myEntry);
                return;
            }

            /* Move to next node */
            myNode = myNode.getNextSibling();
        }
    }

    /**
     * parse the certificate alias.
     * @param pNode the node to parse
     * @param pAlias the alias
     * @param pDate the creation date
     * @throws OceanusException on error
     */
    private void parseCertificateElement(final Node pNode,
                                         final String pAlias,
                                         final TethysDate pDate) throws OceanusException {
        /* Loop through the nodes */
        Node myNode = pNode.getFirstChild();
        while (myNode != null) {
            /* Access the Node name */
            final String myNodeName = myNode.getNodeName();

            /* If this is a certificateKey node */
            if (ELEMENT_CERTKEY.equals(myNodeName)) {
                /* Obtain the key and build the entry */
                final GordianKeyStoreCertificateKey myKey = parseCertificateKey(myNode);
                final GordianKeyStoreCertificateElement myEntry = new GordianKeyStoreCertificateElement(myKey, pDate);
                theKeyStore.getAliasMap().put(pAlias, myEntry);
                return;
            }

            /* Move to next node */
            myNode = myNode.getNextSibling();
        }
    }

    /**
     * parse the certificateKey.
     * @param pNode the node to parse
     * @return the key
     * @throws OceanusException on error
     */
    private static GordianKeyStoreCertificateKey parseCertificateKey(final Node pNode) throws OceanusException {
        /* Loop through the nodes */
        GordianCertificateId mySubject = null;
        Node myNode = pNode.getFirstChild();
        while (myNode != null) {
            /* Access the Node name */
            final String myNodeName = myNode.getNodeName();

            /* If this is a subject node */
            if (ELEMENT_SUBJECT.equals(myNodeName)) {
                /* Parse the subject */
                mySubject = parseCertificateId(myNode);
            }

            /* If this is a issuer node */
            if (ELEMENT_ISSUER.equals(myNodeName)) {
                /* Check for valid xml */
                if (mySubject == null) {
                    throw new GordianDataException(ERROR_BADXML);
                }

                /* Parse the issuer and build the key */
                final GordianCertificateId myIssuer = parseCertificateId(myNode);
                return new GordianKeyStoreCertificateKey(myIssuer, mySubject);
            }

            /* Move to next node */
            myNode = myNode.getNextSibling();
        }

        /* Failed to parse the key */
        throw new GordianDataException("Failed to parse certificate Key");
    }

    /**
     * parse the certificateKey.
     * @param pNode the node to parse
     * @return the key
     * @throws OceanusException on error
     */
    private static GordianCertificateId parseCertificateId(final Node pNode) throws OceanusException {
        /* Loop through the nodes */
        X500Name myName = null;
        Node myNode = pNode.getFirstChild();
        while (myNode != null) {
            /* Access the Node name */
            final String myNodeName = myNode.getNodeName();

            /* If this is a name node */
            if (ELEMENT_NAME.equals(myNodeName)) {
                /* Parse the name */
                final byte[] myNameBytes = TethysDataConverter.base64ToByteArray(myNode.getTextContent());
                myName = X500Name.getInstance(myNameBytes);
            }

            /* If this is a id node */
            if (ELEMENT_ID.equals(myNodeName)) {
                /* Check for valid xml */
                if (myName == null) {
                    throw new GordianDataException(ERROR_BADXML);
                }

                /* Parse the issuer and build the key */
                final byte[] myIdBytes = TethysDataConverter.base64ToByteArray(myNode.getTextContent());
                final DERBitString myId = DERBitString.getInstance(myIdBytes);
                return new GordianCoreCertificateId(myName, myId);
            }

            /* Move to next node */
            myNode = myNode.getNextSibling();
        }

        /* Failed to parse the id */
        throw new GordianDataException("Failed to parse certificate Id");
    }

    /**
     * parse the certificates.
     * @param pCerts the certificates element
     * @throws OceanusException on error
     */
    private void parseCertificates(final Node pCerts) throws OceanusException {
        /* Loop through the nodes */
        Node myNode = pCerts.getFirstChild();
        while (myNode != null) {
            /* Access the Node name */
            final String myNodeName = myNode.getNodeName();

            /* If this is a certificate node */
            if (ELEMENT_CERT.equals(myNodeName)) {
                /* Access the encoded certificate */
                final byte[] myEncoded = TethysDataConverter.base64ToByteArray(myNode.getTextContent());
                final GordianCoreKeyPairCertificate myCert = new GordianCoreKeyPairCertificate(theKeyStore.getFactory(), myEncoded);
                theKeyStore.storeCertificate(myCert);
            }

            /* Move to next node */
            myNode = myNode.getNextSibling();
        }
    }

    /**
     * The Entry types.
     */
    private enum GordianStoreEntryType {
        /**
         * Certificate.
         */
        TRUSTEDCERT("TrustedCertificate"),

        /**
         * PrivateKey.
         */
        PRIVATEKEY("PrivateKey"),

        /**
         * Key.
         */
        KEY("Key"),

        /**
         * KeySet.
         */
        KEYSET("KeySet"),

        /**
         * KeySetHash.
         */
        KEYSETHASH("KeySetHash");

        /**
         * The Element Name.
         */
        private final String theElement;

        /**
         * Constructor.
         * @param pElement the element name
         */
        GordianStoreEntryType(final String pElement) {
            theElement = pElement;
        }

        /**
         * Obtain the element name.
         * @return the element name
         */
        public String getElementName() {
            return theElement;
        }

        /**
         * Determine entry type.
         * @param pEntry the entry
         * @return the entry type
         */
        public static GordianStoreEntryType determineEntryType(final GordianCoreKeyStoreEntry pEntry) {
            if (pEntry instanceof GordianKeyStoreSetElement) {
                return KEYSET;
            }
            if (pEntry instanceof GordianKeyStoreHashElement) {
                return KEYSETHASH;
            }
            if (pEntry instanceof GordianKeyStoreKeyElement) {
                return KEY;
            }
            if (pEntry instanceof GordianKeyStorePairElement) {
                return PRIVATEKEY;
            }
            if (pEntry instanceof GordianKeyStoreCertificateElement) {
                return TRUSTEDCERT;
            }
            throw new IllegalArgumentException();
        }

        /**
         * Determine entry type.
         * @param pElement the element
         * @return the entry type
         */
        public static GordianStoreEntryType determineEntryType(final String pElement) {
            for (GordianStoreEntryType myType : GordianStoreEntryType.values()) {
                if (pElement.equals(myType.getElementName())) {
                    return myType;
                }
            }
            throw new IllegalArgumentException();
        }
    }
}
