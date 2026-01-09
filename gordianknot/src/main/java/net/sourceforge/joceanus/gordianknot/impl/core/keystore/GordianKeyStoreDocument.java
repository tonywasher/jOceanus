/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.core.keystore;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.gordianknot.api.cert.GordianCertificate;
import net.sourceforge.joceanus.gordianknot.api.cert.GordianCertificateId;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianKnuthObfuscater;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreEntry;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianDataConverter;
import net.sourceforge.joceanus.gordianknot.impl.core.cert.GordianCoreCertificate;
import net.sourceforge.joceanus.gordianknot.impl.core.cert.GordianCoreCertificateId;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianIOException;
import net.sourceforge.joceanus.gordianknot.impl.core.keystore.GordianBaseKeyStore.GordianKeyStoreCertificateKey;
import net.sourceforge.joceanus.gordianknot.impl.core.keystore.GordianKeyStoreElement.GordianKeyStoreCertificateElement;
import net.sourceforge.joceanus.gordianknot.impl.core.keystore.GordianKeyStoreElement.GordianKeyStoreKeyElement;
import net.sourceforge.joceanus.gordianknot.impl.core.keystore.GordianKeyStoreElement.GordianKeyStoreLockElement;
import net.sourceforge.joceanus.gordianknot.impl.core.keystore.GordianKeyStoreElement.GordianKeyStorePairElement;
import net.sourceforge.joceanus.gordianknot.impl.core.keystore.GordianKeyStoreElement.GordianKeyStoreSetElement;
import net.sourceforge.joceanus.gordianknot.impl.core.lock.GordianPasswordLockSpecASN1;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.x500.X500Name;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * KeyStore Document.
 */
public final class GordianKeyStoreDocument {
    /**
     * The Invalid XML error.
     */
    private static final DateTimeFormatter DATEFORMATTER = DateTimeFormatter.BASIC_ISO_DATE;

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
     * The PairCertificate element.
     */
    private static final String ELEMENT_PAIRCERT = "PairCertificate";

    /**
     * The keyType attribute.
     */
    private static final String ATTR_KEYSPEC = "KeySpec";

    /**
     * The securedKey element.
     */
    private static final String ELEMENT_SECUREDKEY = "SecuredKey";

    /**
     * The lock element.
     */
    private static final String ELEMENT_LOCK = "LockBytes";

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
    private final GordianBaseKeyStore theKeyStore;

    /**
     * The document.
     */
    private final Document theDocument;

    /**
     * Constructor from keyStore.
     * @param pKeyStore the keyStore
     * @throws GordianException on error
     */
    public GordianKeyStoreDocument(final GordianBaseKeyStore pKeyStore) throws GordianException {
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
            final GordianPasswordLockSpecASN1 mySpecASN1 = new GordianPasswordLockSpecASN1(pKeyStore.getPasswordLockSpec());
            final String myAttrSpec = GordianDataConverter.byteArrayToBase64(mySpecASN1.getEncodedBytes());
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
     * @throws GordianException on error
     */
    public GordianKeyStoreDocument(final GordianFactory pFactory,
                                   final Document pDocument) throws GordianException {
        /* Access the document element */
        final Element myDocElement = pDocument.getDocumentElement();

        /* Check that it is correct document */
        if (!DOC_KEYSTORE.equals(myDocElement.getNodeName())) {
            throw new GordianDataException("Invalid Document");
        }

        /* Access the keySetSpec */
        final String myAttrSpec = myDocElement.getAttribute(ATTR_KEYSETSPEC);
        final byte[] myAttrArray = GordianDataConverter.base64ToByteArray(myAttrSpec);
        final GordianPasswordLockSpecASN1 mySpecASN1 = GordianPasswordLockSpecASN1.getInstance(myAttrArray);

        /* Create the empty keyStore */
        theKeyStore = (GordianBaseKeyStore) pFactory.getAsyncFactory().getKeyStoreFactory().createKeyStore(mySpecASN1.getLockSpec());
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
    public GordianBaseKeyStore getKeyStore() {
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
     * @throws GordianException on error
     */
    private void buildAliases(final Node pAliases) throws GordianException {
        /* Access the Alias entries */
        for (Entry<String, GordianKeyStoreEntry> myEntry : theKeyStore.getAliasMap().entrySet()) {
            /* Determine the entry type */
            final GordianKeyStoreEntry myElement = myEntry.getValue();
            final GordianStoreEntryType myType = GordianStoreEntryType.determineEntryType(myElement);

            /* Build alias entry */
            final Element myAliasEl = theDocument.createElement(myType.getElementName());
            myAliasEl.setAttribute(ATTR_ALIAS, myEntry.getKey());
            myAliasEl.setAttribute(ATTR_DATE, myElement.getCreationDate().format(DATEFORMATTER));
            pAliases.appendChild(myAliasEl);

            /* Switch on element type */
            switch (myType) {
                case KEY:
                    buildKeyElement(myAliasEl, (GordianKeyStoreKeyElement<?>) myElement);
                    break;
                case KEYSET:
                    buildKeySetElement(myAliasEl, (GordianKeyStoreSetElement) myElement);
                    break;
                case KEYSETLOCK:
                    buildKeySetLockElement(myAliasEl, (GordianKeyStoreLockElement) myElement);
                    break;
                case PRIVATEKEYPAIR:
                    buildPrivateKeyElement(myAliasEl, (GordianKeyStorePairElement) myElement);
                    break;
                case TRUSTEDPAIRCERT:
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
    private void buildKeySetLockElement(final Element pNode,
                                        final GordianKeyStoreLockElement pEntry) {
        /* Build lock entry */
        final Element myLockEl = theDocument.createElement(ELEMENT_LOCK);
        pNode.appendChild(myLockEl);
        myLockEl.setTextContent(GordianDataConverter.byteArrayToBase64(pEntry.getLock()));
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
        myKeyEl.setTextContent(GordianDataConverter.byteArrayToBase64(pEntry.getSecuredKeySet()));

        /* Build lock entry */
        final Element myLockEl = theDocument.createElement(ELEMENT_LOCK);
        pNode.appendChild(myLockEl);
        myLockEl.setTextContent(GordianDataConverter.byteArrayToBase64(pEntry.getSecuringLockBytes()));
    }

    /**
     * build the key element.
     * @param pNode the key node to build
     * @param pEntry the key entry
     * @throws GordianException on error
     */
    private void buildKeyElement(final Element pNode,
                                 final GordianKeyStoreKeyElement<?> pEntry) throws GordianException {
        /* Build securedKey entry */
        final Element myKeyEl = theDocument.createElement(ELEMENT_SECUREDKEY);
        pNode.appendChild(myKeyEl);
        myKeyEl.setTextContent(GordianDataConverter.byteArrayToBase64(pEntry.getSecuredKey()));

        /* Add the keySpec */
        final GordianKnuthObfuscater myObfuscater = theKeyStore.getFactory().getObfuscater();
        final int myId = myObfuscater.deriveExternalIdFromType(pEntry.getKeyType());
        myKeyEl.setAttribute(ATTR_KEYSPEC, Integer.toString(myId));

        /* Build lock entry */
        final Element myLockEl = theDocument.createElement(ELEMENT_LOCK);
        pNode.appendChild(myLockEl);
        myLockEl.setTextContent(GordianDataConverter.byteArrayToBase64(pEntry.getSecuringLockBytes()));
    }

    /**
     * build the privateKey element.
     * @param pNode the privateKey node to build
     * @param pEntry the privateKey entry
     * @throws GordianException on error
     */
    private void buildPrivateKeyElement(final Element pNode,
                                        final GordianKeyStorePairElement pEntry) throws GordianException {
        /* Build securedKey entry */
        final Element myKeyEl = theDocument.createElement(ELEMENT_SECUREDKEY);
        pNode.appendChild(myKeyEl);
        myKeyEl.setTextContent(GordianDataConverter.byteArrayToBase64(pEntry.getSecuredKey()));

        /* Build lock entry */
        final Element myLockEl = theDocument.createElement(ELEMENT_LOCK);
        pNode.appendChild(myLockEl);
        myLockEl.setTextContent(GordianDataConverter.byteArrayToBase64(pEntry.getSecuringLockBytes()));

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
     * @throws GordianException on error
     */
    private void buildCertificateElement(final Element pNode,
                                         final GordianKeyStoreCertificateElement pEntry) throws GordianException {
        /* Build certificateKey */
        buildCertificateKey(pNode, pEntry.getCertificateKey());
    }

    /**
     * build the certificateKey element.
     * @param pNode the holding node
     * @param pKey the certificate key
     * @throws GordianException on error
     */
    private void buildCertificateKey(final Element pNode,
                                     final GordianKeyStoreCertificateKey pKey) throws GordianException {
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
     * @throws GordianException on error
     */
    private void buildCertificateId(final Element pNode,
                                    final GordianCertificateId pId) throws GordianException {
        /* protecte against exceptions */
        try {
            /* Build Name entry */
            final Element myNameEl = theDocument.createElement(ELEMENT_NAME);
            pNode.appendChild(myNameEl);
            myNameEl.setTextContent(GordianDataConverter.byteArrayToBase64(pId.getName().toASN1Primitive().getEncoded()));

            /* Build id entry */
            final Element myIdEl = theDocument.createElement(ELEMENT_ID);
            pNode.appendChild(myIdEl);
            myIdEl.setTextContent(GordianDataConverter.byteArrayToBase64(pId.getId().getEncoded()));

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
        for (Map<GordianCertificateId, GordianCertificate> myMap : theKeyStore.getSubjectMapOfMaps().values()) {
            for (GordianCertificate myCert : myMap.values()) {
                /* Build certificate entry */
                final Element myCertEl = theDocument.createElement(ELEMENT_PAIRCERT);
                pCerts.appendChild(myCertEl);

                /* Build the certificate element */
                myCertEl.setTextContent(GordianDataConverter.byteArrayToBase64(myCert.getEncoded()));
            }
        }
    }

    /**
     * parse the aliases.
     * @param pAliases the aliases element
     * @throws GordianException on error
     */
    private void parseAliases(final Node pAliases) throws GordianException {
        /* Loop through the nodes */
        Node myNode = pAliases.getFirstChild();
        while (myNode != null) {
            /* Determine the entry type */
            final GordianStoreEntryType myType = GordianStoreEntryType.determineEntryType(myNode.getNodeName());

            /* Access alias and creationDate */
            final String myAlias = ((Element) myNode).getAttribute(ATTR_ALIAS);
            final LocalDate myDate = LocalDate.parse(((Element) myNode).getAttribute(ATTR_DATE), DATEFORMATTER);

            /* Switch on element type */
            switch (myType) {
                case KEY:
                    parseKeyElement(myNode, myAlias, myDate);
                    break;
                case KEYSET:
                    parseKeySetElement(myNode, myAlias, myDate);
                    break;
                case KEYSETLOCK:
                    parseKeySetLockElement(myNode, myAlias, myDate);
                    break;
                case PRIVATEKEYPAIR:
                    parsePrivateKeyElement(myNode, myAlias, myDate);
                    break;
                case TRUSTEDPAIRCERT:
                default:
                    parseCertificateElement(myNode, myAlias, myDate);
                    break;
            }

            /* Move to next node */
            myNode = myNode.getNextSibling();
        }
    }

    /**
     * parse the keySetLock alias.
     * @param pNode the node to parse
     * @param pAlias the alias
     * @param pDate the creation date
     */
    private void parseKeySetLockElement(final Node pNode,
                                        final String pAlias,
                                        final LocalDate pDate) {
        /* Loop through the nodes */
        Node myNode = pNode.getFirstChild();
        while (myNode != null) {
            /* Access the Node name */
            final String myNodeName = myNode.getNodeName();

            /* If this is a lock node */
            if (ELEMENT_LOCK.equals(myNodeName)) {
                /* Obtain the hash and build the entry */
                final byte[] myLock = GordianDataConverter.base64ToByteArray(myNode.getTextContent());
                final GordianKeyStoreLockElement myEntry = new GordianKeyStoreLockElement(myLock, pDate);
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
     * @throws GordianException on error
     */
    private void parseKeySetElement(final Node pNode,
                                    final String pAlias,
                                    final LocalDate pDate) throws GordianException {
        /* Loop through the nodes */
        byte[] mySecuredKey = null;
        Node myNode = pNode.getFirstChild();
        while (myNode != null) {
            /* Access the Node name */
            final String myNodeName = myNode.getNodeName();

            /* If this is a securedKey node */
            if (ELEMENT_SECUREDKEY.equals(myNodeName)) {
                /* Obtain the securedKey */
                mySecuredKey = GordianDataConverter.base64ToByteArray(myNode.getTextContent());
            }

            /* If this is a lock node */
            if (ELEMENT_LOCK.equals(myNodeName)) {
                /* Check for valid xml */
                if (mySecuredKey == null) {
                    throw new GordianDataException(ERROR_BADXML);
                }

                /* Obtain the lock and build the entry */
                final byte[] myLock = GordianDataConverter.base64ToByteArray(myNode.getTextContent());
                final GordianKeyStoreSetElement myEntry = new GordianKeyStoreSetElement(mySecuredKey, myLock, pDate);
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
     * @throws GordianException on error
     */
    private void parseKeyElement(final Node pNode,
                                 final String pAlias,
                                 final LocalDate pDate) throws GordianException {
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
                mySecuredKey = GordianDataConverter.base64ToByteArray(myNode.getTextContent());

                /* Obtain the keySpec */
                final GordianKnuthObfuscater myObfuscater = theKeyStore.getFactory().getObfuscater();
                final String mySpecId = ((Element) myNode).getAttribute(ATTR_KEYSPEC);
                mySpec = (GordianKeySpec) myObfuscater.deriveTypeFromExternalId(Integer.parseInt(mySpecId));
            }

            /* If this is a lock node */
            if (ELEMENT_LOCK.equals(myNodeName)) {
                /* Check for valid xml */
                if (mySecuredKey == null) {
                    throw new GordianDataException(ERROR_BADXML);
                }

                /* Obtain the lock and build the entry */
                final byte[] myLock = GordianDataConverter.base64ToByteArray(myNode.getTextContent());
                final GordianKeyStoreKeyElement<GordianKeySpec> myEntry = new GordianKeyStoreKeyElement<>(mySpec, mySecuredKey, myLock, pDate);
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
     * @throws GordianException on error
     */
    private void parsePrivateKeyElement(final Node pNode,
                                        final String pAlias,
                                        final LocalDate pDate) throws GordianException {
        /* Loop through the nodes */
        byte[] mySecuredKey = null;
        byte[] myLock = null;
        final List<GordianKeyStoreCertificateKey> myChain = new ArrayList<>();
        Node myNode = pNode.getFirstChild();
        while (myNode != null) {
            /* Access the Node name */
            final String myNodeName = myNode.getNodeName();

            /* If this is a securedKey node */
            if (ELEMENT_SECUREDKEY.equals(myNodeName)) {
                /* Obtain the securedKey */
                mySecuredKey = GordianDataConverter.base64ToByteArray(myNode.getTextContent());
            }

            /* If this is a lock node */
            if (ELEMENT_LOCK.equals(myNodeName)) {
                /* Obtain the lock */
                myLock = GordianDataConverter.base64ToByteArray(myNode.getTextContent());
            }

            /* If this is a hash node */
            if (ELEMENT_CERTIFICATES.equals(myNodeName)) {
                /* Check for valid xml */
                if (mySecuredKey == null || myLock == null) {
                    throw new GordianDataException(ERROR_BADXML);
                }

                /* Loop through the certificates */
                Node myChild = myNode.getFirstChild();
                while (myChild != null) {
                    /* Access the Node name */
                    final String myName = myChild.getNodeName();

                    /* If this is a certificateKey node */
                    if (ELEMENT_CERTKEY.equals(myName)) {
                        /* Add the certificate to the list */
                        myChain.add(parseCertificateKey(myChild));
                    }

                    /* Move to next node */
                    myChild = myChild.getNextSibling();
                }

                /* build the entry */
                final GordianKeyStorePairElement myEntry = new GordianKeyStorePairElement(mySecuredKey, myLock, myChain, pDate);
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
     * @throws GordianException on error
     */
    private void parseCertificateElement(final Node pNode,
                                         final String pAlias,
                                         final LocalDate pDate) throws GordianException {
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
     * @throws GordianException on error
     */
    private static GordianKeyStoreCertificateKey parseCertificateKey(final Node pNode) throws GordianException {
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
     * @throws GordianException on error
     */
    private static GordianCertificateId parseCertificateId(final Node pNode) throws GordianException {
        /* Loop through the nodes */
        X500Name myName = null;
        Node myNode = pNode.getFirstChild();
        while (myNode != null) {
            /* Access the Node name */
            final String myNodeName = myNode.getNodeName();

            /* If this is a name node */
            if (ELEMENT_NAME.equals(myNodeName)) {
                /* Parse the name */
                final byte[] myNameBytes = GordianDataConverter.base64ToByteArray(myNode.getTextContent());
                myName = X500Name.getInstance(myNameBytes);
            }

            /* If this is a id node */
            if (ELEMENT_ID.equals(myNodeName)) {
                /* Check for valid xml */
                if (myName == null) {
                    throw new GordianDataException(ERROR_BADXML);
                }

                /* Parse the issuer and build the key */
                final byte[] myIdBytes = GordianDataConverter.base64ToByteArray(myNode.getTextContent());
                final DERBitString myId = DERBitString.convert(ASN1BitString.getInstance(myIdBytes));
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
     * @throws GordianException on error
     */
    private void parseCertificates(final Node pCerts) throws GordianException {
        /* Loop through the nodes */
        Node myNode = pCerts.getFirstChild();
        while (myNode != null) {
            /* Access the Node name */
            final String myNodeName = myNode.getNodeName();

            /* If this is a pairCertificate node */
            if (ELEMENT_PAIRCERT.equals(myNodeName)) {
                /* Access the encoded certificate */
                final byte[] myEncoded = GordianDataConverter.base64ToByteArray(myNode.getTextContent());
                final GordianCoreCertificate myCert = new GordianCoreCertificate(theKeyStore.getFactory(), myEncoded);
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
        TRUSTEDPAIRCERT("TrustedCertificate"),

        /**
         * PrivateKey.
         */
        PRIVATEKEYPAIR("PrivateKeyPair"),

        /**
         * Key.
         */
        KEY("Key"),

        /**
         * KeySet.
         */
        KEYSET("KeySet"),

        /**
         * KeySetLock.
         */
        KEYSETLOCK("KeySetLock");

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
        public static GordianStoreEntryType determineEntryType(final GordianKeyStoreEntry pEntry) {
            if (pEntry instanceof GordianKeyStoreSetElement) {
                return KEYSET;
            }
            if (pEntry instanceof GordianKeyStoreLockElement) {
                return KEYSETLOCK;
            }
            if (pEntry instanceof GordianKeyStoreKeyElement) {
                return KEY;
            }
            if (pEntry instanceof GordianKeyStorePairElement) {
                return PRIVATEKEYPAIR;
            }
            if (pEntry instanceof GordianKeyStoreCertificateElement) {
                return TRUSTEDPAIRCERT;
            }
            throw new IllegalArgumentException();
        }

        /**
         * Determine entry type.
         * @param pElement the element
         * @return the entry type
         */
        public static GordianStoreEntryType determineEntryType(final String pElement) {
            for (GordianStoreEntryType myType : values()) {
                if (pElement.equals(myType.getElementName())) {
                    return myType;
                }
            }
            throw new IllegalArgumentException();
        }
    }
}
