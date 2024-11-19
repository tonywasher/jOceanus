/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.crmf.CertReqMsg;
import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import net.sourceforge.joceanus.gordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianKnuthObfuscater;
import net.sourceforge.joceanus.gordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianCertificate;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreEntry;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStoreKey;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePair;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStoreCertificate;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStoreSet;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreGateway.GordianLockResolver;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.gordianknot.impl.core.keystore.GordianCoreKeyStoreEntry.GordianCoreKeyStoreKey;
import net.sourceforge.joceanus.gordianknot.impl.core.keystore.GordianCoreKeyStoreEntry.GordianCoreKeyStorePair;
import net.sourceforge.joceanus.gordianknot.impl.core.keystore.GordianCoreKeyStoreEntry.GordianCoreKeyStoreCertificate;
import net.sourceforge.joceanus.gordianknot.impl.core.keystore.GordianCoreKeyStoreEntry.GordianCoreKeyStoreSet;
import net.sourceforge.joceanus.gordianknot.impl.core.keystore.GordianPEMObject.GordianPEMObjectType;
import net.sourceforge.joceanus.gordianknot.impl.core.zip.GordianCoreZipLock;
import net.sourceforge.joceanus.gordianknot.impl.core.zip.GordianZipLockASN1;
import net.sourceforge.joceanus.tethys.OceanusException;
import net.sourceforge.joceanus.tethys.TethysDataConverter;
import net.sourceforge.joceanus.tethys.date.TethysDate;

/**
 * PEM Coder/deCoder.
 */
public class GordianPEMCoder {
    /**
     * Unsupported objectType error.
     */
    private static final String ERROR_UNSUPPORTED = "Unsupported object type";

    /**
     * The Factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * The Parser.
     */
    private final GordianPEMParser theParser;

    /**
     * The lock callback.
     */
    private GordianLockResolver theLockResolver;

    /**
     * Constructor.
     * @param pKeyStore the keyStore
     */
    GordianPEMCoder(final GordianCoreKeyStore pKeyStore) {
        /* Store details */
        theFactory = pKeyStore.getFactory();
        theParser = new GordianPEMParser();
    }

    /**
     * Set the lock resolver the lock resolver.
     * @param pResolver the resolver
     */
    void setLockResolver(final GordianLockResolver pResolver) {
        theLockResolver = pResolver;
    }

    /**
     * Export a keyStoreEntry to stream.
     * @param pEntry the entry
     * @param pStream the output stream
     * @param pLock the lock
     * @throws OceanusException on error
     */
    public void exportKeyStoreEntry(final GordianKeyStoreEntry pEntry,
                                    final OutputStream pStream,
                                    final GordianCoreZipLock pLock) throws OceanusException {
        /* Check that the lock is usable */
        if (pLock == null || !pLock.isFresh()) {
            throw new GordianDataException("Invalid lock");
        }
        pLock.markAsUsed();

        /* Encode and write the object */
        final List<GordianPEMObject> myObjects = encodeKeyStoreEntry(pEntry, pLock);
        theParser.writePEMFile(pStream, myObjects);
    }

    /**
     * Import a keyStoreEntry from stream.
     * @param pStream the input stream
     * @return the decoded object.
     * @throws OceanusException on error
     */
    public GordianKeyStoreEntry importKeyStoreEntry(final InputStream pStream) throws OceanusException {
        final List<GordianPEMObject> myObjects = theParser.parsePEMFile(pStream);
        return decodePEMObjectList(myObjects);
    }

    /**
     * Import a list of certificates from stream.
     * @param pStream the input stream
     * @return the list of certificates.
     * @throws OceanusException on error
     */
    public List<GordianKeyStoreEntry> importCertificates(final InputStream pStream) throws OceanusException {
        final List<GordianPEMObject> myObjects = theParser.parsePEMFile(pStream);
        return decodePEMCertificateList(myObjects);
    }

    /**
     * Encode a keyStoreEntry.
     * @param pEntry the entry
     * @param pLock the lock
     * @return the encoded object list.
     * @throws OceanusException on error
     */
    private List<GordianPEMObject> encodeKeyStoreEntry(final GordianKeyStoreEntry pEntry,
                                                       final GordianCoreZipLock pLock) throws OceanusException {
        /* Handle certificates */
        if (pEntry instanceof GordianKeyStoreCertificate) {
             final GordianCertificate myCert = ((GordianKeyStoreCertificate) pEntry).getCertificate();
             return Collections.singletonList(encodeCertificate(myCert));
        }

        /* Handle keyPair */
        if (pEntry instanceof GordianKeyStorePair) {
            return encodePrivateKeyPair((GordianKeyStorePair) pEntry, pLock);
        }

        /* Handle keySet and key */
        if (pEntry instanceof GordianKeyStoreSet) {
            return Collections.singletonList(encodeKeySet((GordianKeyStoreSet) pEntry, pLock));
        }
        if (pEntry instanceof GordianKeyStoreKey) {
            return Collections.singletonList(encodeKey((GordianKeyStoreKey<?>) pEntry, pLock));
        }

        /* Unsupported entry */
        throw new GordianDataException(ERROR_UNSUPPORTED);
    }

    /**
     * Decode a PEMObject list.
     * @param pObjects the object list
     * @return the decoded object.
     * @throws OceanusException on error
     */
     private GordianKeyStoreEntry decodePEMObjectList(final List<GordianPEMObject> pObjects) throws OceanusException {
         /* List must be non-empty */
         if (pObjects.isEmpty()) {
             throw new GordianDataException("Empty list");
         }

         /* Access first element and switch on object type */
         final GordianPEMObject myFirst = pObjects.get(0);
         switch (myFirst.getObjectType()) {
             /* Decode objects */
             case PRIVATEKEY:
                 return decodeKeyPair(pObjects);
             case CERT:
                 return decodeCertificate(pObjects);
             case KEYSET:
                 return decodeKeySet(pObjects);
             case KEY:
                 return decodeKey(pObjects);

             /* Unsupported entry */
             default:
                throw new GordianDataException(ERROR_UNSUPPORTED);
         }
    }

    /**
     * Decode a PEMCertificate list.
     * @param pObjects the object list
     * @return the decoded list.
     * @throws OceanusException on error
     */
    private List<GordianKeyStoreEntry> decodePEMCertificateList(final List<GordianPEMObject> pObjects) throws OceanusException {
        /* List must be non-empty */
        if (pObjects.isEmpty()) {
            throw new GordianDataException("Empty list");
        }

        /* Prepare for loop */
        final List<GordianKeyStoreEntry> myChain = new ArrayList<>();
        final GordianPEMObjectType myType = pObjects.get(0).getObjectType();
        final TethysDate myDate = new TethysDate();

        /* Loop through the objects */
        for (GordianPEMObject myObject : pObjects) {
            /* Check that the list is homogenous */
            if (myObject.getObjectType() != myType) {
                throw new GordianDataException("Inconsistent chain");
            }

             /* Decode objects */
            if (myObject.getObjectType() == GordianPEMObjectType.CERT) {
                final GordianCoreCertificate myKeyPairCert = decodeCertificate(myObject);
                myChain.add(new GordianCoreKeyStoreCertificate(myKeyPairCert, myDate));

                /* Unsupported entry */
            } else {
                throw new GordianDataException(ERROR_UNSUPPORTED);
            }
        }

        /* Return the chain */
        return myChain;
    }

    /**
     * Encode a Certificate.
     * @param pCertificate the certificate
     * @return the encoded object.
     */
    static GordianPEMObject encodeCertificate(final GordianCertificate pCertificate) {
        return new GordianPEMObject(GordianPEMObjectType.CERT, pCertificate.getEncoded());
    }

    /**
     * Create a PEM Object.
     * @param pObjectType the objectType
     * @param pObject the object
     * @return the PEM Object
     * @throws OceanusException on error
     */
    static GordianPEMObject createPEMObject(final GordianPEMObjectType pObjectType,
                                            final ASN1Object pObject) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Create a PEM Object */
            return new GordianPEMObject(pObjectType, pObject.getEncoded());

        } catch (IOException e) {
            throw new GordianIOException("Failed to create PEMObject", e);
        }
    }

    /**
     * Encode a keyPair.
     * @param pKeyPair the keyPair
     * @param pLock the lock
     * @return the encoded object.
     * @throws OceanusException on error
     */
    private List<GordianPEMObject> encodePrivateKeyPair(final GordianKeyStorePair pKeyPair,
                                                        final GordianCoreZipLock pLock) throws OceanusException {
        /* Create the list */
        final List<GordianPEMObject> myList = new ArrayList<>();

        /* Add the private key entry */
        myList.add(encodePrivateKey(pKeyPair, pLock));

        /* Loop through the certificates */
        for (GordianCertificate myCert : pKeyPair.getCertificateChain()) {
            /* Add the encoded certificate */
            myList.add(encodeCertificate(myCert));
        }

        /* Return the list */
        return myList;
    }

    /**
     * Encode a privateKey.
     * @param pKeyPair the keyPair
     * @param pLock the lock
     * @return the encoded object.
     * @throws OceanusException on error
     */
    private GordianPEMObject encodePrivateKey(final GordianKeyStorePair pKeyPair,
                                              final GordianCoreZipLock pLock) throws OceanusException {
        /* Protect against exception */
        try {
            /* Build encoded object and return it */
            final GordianKeySet myKeySet = pLock.getKeySet();
            final byte[] mySecuredKey = myKeySet.securePrivateKey(pKeyPair.getKeyPair());
            final EncryptedPrivateKeyInfo myInfo = buildPrivateKeyInfo(pLock, mySecuredKey);
            return new GordianPEMObject(GordianPEMObjectType.PRIVATEKEY, myInfo.getEncoded());

            /* Handle exceptions */
        } catch (IOException e) {
            throw new GordianIOException("Failed to encode privateKey", e);
        }
    }

    /**
     * Encode a keySet.
     * @param pKeySet the keySet
     * @param pLock the lock
     * @return the encoded object.
     * @throws OceanusException on error
     */
    private GordianPEMObject encodeKeySet(final GordianKeyStoreSet pKeySet,
                                          final GordianCoreZipLock pLock) throws OceanusException {
        /* Protect against exception */
        try {
            /* Build encoded object and return it */
            final GordianKeySet myKeySet = pLock.getKeySet();
            final byte[] mySecuredKeySet = myKeySet.secureKeySet(pKeySet.getKeySet());
            final EncryptedPrivateKeyInfo myInfo = buildPrivateKeyInfo(pLock, mySecuredKeySet);
            return new GordianPEMObject(GordianPEMObjectType.KEYSET, myInfo.getEncoded());

            /* Handle exceptions */
        } catch (IOException e) {
            throw new GordianIOException("Failed to encode keySet", e);
        }
    }

    /**
     * Encode a key.
     * @param pKey the key
     * @param pLock the Lock
     * @return the encoded object.
     * @throws OceanusException on error
     */
    private GordianPEMObject encodeKey(final GordianKeyStoreKey<?> pKey,
                                       final GordianCoreZipLock pLock) throws OceanusException {
        /* Protect against exception */
        try {
            /* Access keyType */
            final GordianKey<?> myKey = pKey.getKey();
            final GordianKnuthObfuscater myObfuscater = theFactory.getObfuscater();
            final int myId = myObfuscater.deriveExternalIdFromType(myKey.getKeyType());
            final byte[] myTypeDef = TethysDataConverter.integerToByteArray(myId);

            /* Secure the key */
            final GordianKeySet myKeySet = pLock.getKeySet();
            final byte[] mySecuredKey = myKeySet.secureKey(myKey);

            /* Build key definition */
            final byte[] myKeyDef = new byte[mySecuredKey.length + Integer.BYTES];
            System.arraycopy(mySecuredKey, 0, myKeyDef, Integer.BYTES, mySecuredKey.length);
            System.arraycopy(myTypeDef, 0, myKeyDef, 0, Integer.BYTES);

            /* Build encoded object and return it */
            final EncryptedPrivateKeyInfo myInfo = buildPrivateKeyInfo(pLock, myKeyDef);
            return new GordianPEMObject(GordianPEMObjectType.KEY, myInfo.getEncoded());

            /* Handle exceptions */
        } catch (IOException e) {
            throw new GordianIOException("Failed to encode keySet", e);
        }
    }

    /**
     * Decode a Certificate.
     * @param pObjects the PEM object list
     * @return the Certificate.
     * @throws OceanusException on error
     */
    private GordianKeyStoreCertificate decodeCertificate(final List<GordianPEMObject> pObjects) throws OceanusException {
        /* Reject if not singleton list */
        checkSingletonList(pObjects);

        /* parse the certificate */
        return new GordianCoreKeyStoreCertificate(decodeCertificate(pObjects.get(0)), new TethysDate());
    }

    /**
     * Decode a Certificate.
     * @param pObject the PEM object
     * @return the Certificate.
     * @throws OceanusException on error
     */
    private GordianCoreCertificate decodeCertificate(final GordianPEMObject pObject) throws OceanusException {
        /* Reject if not keySetCertificate */
        checkObjectType(pObject, GordianPEMObjectType.CERT);

        /* parse the encoded bytes */
        return new GordianCoreCertificate(theFactory, pObject.getEncoded());
    }

    /**
     * Decode a Certificate Request.
     * @param pObjects the PEM object list
     * @return the Certificate Request.
     * @throws OceanusException on error
     */
    static CertReqMsg decodeCertRequest(final List<GordianPEMObject> pObjects) throws OceanusException {
        /* Reject if not singleton list */
        checkSingletonList(pObjects);
        final GordianPEMObject myObject = pObjects.get(0);

        /* Reject if not certificateRequest */
        checkObjectType(myObject, GordianPEMObjectType.CERTREQ);

        /* parse the encoded bytes */
        return CertReqMsg.getInstance(myObject.getEncoded());
    }

    /**
     * Decode a Certificate Response.
     * @param pObjects the PEM object list
     * @return the Certificate Response.
     * @throws OceanusException on error
     */
    static GordianCertResponseASN1 decodeCertResponse(final List<GordianPEMObject> pObjects) throws OceanusException {
        /* Reject if not singleton list */
        checkSingletonList(pObjects);
        final GordianPEMObject myObject = pObjects.get(0);

        /* Reject if not certificateResponse */
        checkObjectType(myObject, GordianPEMObjectType.CERTRESP);

        /* parse the encoded bytes */
        return GordianCertResponseASN1.getInstance(myObject.getEncoded());
    }

    /**
     * Decode a Certificate Ack.
     * @param pObjects the PEM object list
     * @return the Certificate Ack.
     * @throws OceanusException on error
     */
    static GordianCertAckASN1 decodeCertAck(final List<GordianPEMObject> pObjects) throws OceanusException {
        /* Reject if not singleton list */
        checkSingletonList(pObjects);
        final GordianPEMObject myObject = pObjects.get(0);

        /* Reject if not certificateAck */
        checkObjectType(myObject, GordianPEMObjectType.CERTACK);

        /* parse the encoded bytes */
        return GordianCertAckASN1.getInstance(myObject.getEncoded());
    }

    /**
     * Decode a keyPair.
     * @param pObjects the list of objects
     * @return the keyPair.
     * @throws OceanusException on error
     */
    private GordianKeyStorePair decodeKeyPair(final List<GordianPEMObject> pObjects) throws OceanusException {
        /* Initialise variables */
        EncryptedPrivateKeyInfo myPrivateInfo = null;
        final List<GordianCertificate> myChain = new ArrayList<>();

        /* Loop through the entries */
        for (GordianPEMObject myObject : pObjects) {
            /* Decode private key if first element */
            if (myPrivateInfo == null) {
                myPrivateInfo = EncryptedPrivateKeyInfo.getInstance(myObject.getEncoded());

                /* else decode next certificate in chain */
            } else {
                myChain.add(decodeCertificate(myObject));
            }
        }

        /* Check that we have a privateKey and at least one certificate */
        if (myPrivateInfo == null || myChain.isEmpty()) {
            throw new GordianDataException("Insufficient entries");
        }

        /* Derive the keyPair */
        final GordianKeySet mySecuringKeySet = deriveSecuringKeySet(myPrivateInfo);
        final GordianCoreCertificate myCert = (GordianCoreCertificate) myChain.get(0);
        final GordianKeyPair myPair = mySecuringKeySet.deriveKeyPair(myCert.getX509KeySpec(), myPrivateInfo.getEncryptedData());

        /* Return the new keyPair */
        return new GordianCoreKeyStorePair(myPair, myChain, new TethysDate());
    }

    /**
     * Decode a keySet.
     * @param pObjects the PEM object list
     * @return the keySet.
     * @throws OceanusException on error
     */
    private GordianKeyStoreSet decodeKeySet(final List<GordianPEMObject> pObjects) throws OceanusException {
        checkSingletonList(pObjects);
        return decodeKeySet(pObjects.get(0));
    }

    /**
     * Decode a keySet.
     * @param pObject the PEM object
     * @return the keySet.
     * @throws OceanusException on error
     */
    private GordianCoreKeyStoreSet decodeKeySet(final GordianPEMObject pObject) throws OceanusException {
        /* Reject if not KeySet */
        checkObjectType(pObject, GordianPEMObjectType.KEYSET);

        /* Derive the securing keySet */
        final EncryptedPrivateKeyInfo myInfo = EncryptedPrivateKeyInfo.getInstance(pObject.getEncoded());
        final GordianKeySet mySecuringKeySet = deriveSecuringKeySet(myInfo);

        /* Derive the keySet */
        final GordianKeySet myKeySet = mySecuringKeySet.deriveKeySet(myInfo.getEncryptedData());
        return new GordianCoreKeyStoreSet(myKeySet, new TethysDate());
    }

    /**
     * Decode a key.
     * @param pObjects the PEM object list
     * @return the key.
     * @throws OceanusException on error
     */
    private GordianKeyStoreKey<?> decodeKey(final List<GordianPEMObject> pObjects) throws OceanusException {
        checkSingletonList(pObjects);
        return decodeKey(pObjects.get(0));
    }

    /**
     * Decode a key.
     * @param pObject the PEM object
     * @return the key.
     * @throws OceanusException on error
     */
    private GordianCoreKeyStoreKey<?> decodeKey(final GordianPEMObject pObject) throws OceanusException {
        /* Reject if not Key */
        checkObjectType(pObject, GordianPEMObjectType.KEY);

        /* Derive the securing keySet */
        final EncryptedPrivateKeyInfo myInfo = EncryptedPrivateKeyInfo.getInstance(pObject.getEncoded());
        final GordianKeySet mySecuringKeySet = deriveSecuringKeySet(myInfo);

        /* Extract key definition */
        final byte[] mySecured = myInfo.getEncryptedData();
        final byte[] myTypeDef = new byte[Integer.BYTES];
        System.arraycopy(mySecured, 0, myTypeDef, 0, Integer.BYTES);
        final byte[] myKeyDef = new byte[mySecured.length - Integer.BYTES];
        System.arraycopy(mySecured, Integer.BYTES, myKeyDef, 0, mySecured.length - Integer.BYTES);

        /* Obtain the keySpec */
        final GordianKnuthObfuscater myObfuscater = theFactory.getObfuscater();
        final int myType = TethysDataConverter.byteArrayToInteger(myTypeDef);
        final GordianKeySpec myKeyType = (GordianKeySpec) myObfuscater.deriveTypeFromExternalId(myType);

        /* Derive the key */
        final GordianKey<?> myKey = mySecuringKeySet.deriveKey(myKeyDef, myKeyType);
        return new GordianCoreKeyStoreKey<>(myKey, new TethysDate());
    }

    /**
     * Build EncryptedPrivateKeyInfo.
     * @param pLock the Lock
     * @param pInfo the encryptedInfo
     * @return the algorithmId.
     */
    private static EncryptedPrivateKeyInfo buildPrivateKeyInfo(final GordianCoreZipLock pLock,
                                                               final byte[] pInfo) {
        return new EncryptedPrivateKeyInfo(pLock.getAlgorithmId(), pInfo);
    }

    /**
     * Derive securing keySet.
     * @param pInfo the encrypted private keyInfo
     * @return the keySet
     * @throws OceanusException on error
     */
    private GordianKeySet deriveSecuringKeySet(final EncryptedPrivateKeyInfo pInfo) throws OceanusException {
        /* Validate the algorithmId */
        final AlgorithmIdentifier myId = pInfo.getEncryptionAlgorithm();
        if (!myId.getAlgorithm().equals(GordianZipLockASN1.LOCKOID)) {
            throw new GordianDataException("Unsupported algorithm");
        }
        if (theLockResolver == null) {
            throw new GordianDataException("No lock resolver set");
        }

        /* Resolve the lock */
        final GordianCoreZipLock myLock = new GordianCoreZipLock(theFactory, myId.getParameters());
        theLockResolver.resolveLock(myLock);
        if (myLock.isLocked()) {
            throw new GordianDataException("Lock was not resolved");
        }

        /* Derive the securing keySet */
        return myLock.getKeySet();
    }

    /**
     * Check for singleton list.
     * @param pObjects the object list
     * @throws OceanusException on error
     */
    private static void checkSingletonList(final List<GordianPEMObject> pObjects) throws OceanusException {
        /* Throw error on non-singleton */
        if (pObjects.size() != 1) {
            throw new GordianDataException("Too many objects");
        }
    }

    /**
     * Check PEM objectType.
     * @param pObject the objectType
     * @param pRequired the required objectType
     * @throws OceanusException on error
     */
    static void checkObjectType(final GordianPEMObject pObject,
                                final GordianPEMObjectType pRequired) throws OceanusException {
        /* Throw error on mismatch */
        final GordianPEMObjectType myType = pObject.getObjectType();
        if (myType != pRequired) {
            throw new GordianDataException("unexpected objectType " + myType + " - Expected " + pRequired);
        }
    }
}
