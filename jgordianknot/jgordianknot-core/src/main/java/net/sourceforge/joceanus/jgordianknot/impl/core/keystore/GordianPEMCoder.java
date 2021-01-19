/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2021 Tony Washer
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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianKnuthObfuscater;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSet;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHashSpec;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairCertificate;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairSetCertificate;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStoreKey;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePair;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePairCertificate;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePairSet;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePairSetCertificate;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStoreSet;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreGateway.GordianLockResolver;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianCoreKeyStoreEntry.GordianCoreKeyStoreKey;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianCoreKeyStoreEntry.GordianCoreKeyStorePair;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianCoreKeyStoreEntry.GordianCoreKeyStorePairCertificate;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianCoreKeyStoreEntry.GordianCoreKeyStorePairSet;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianCoreKeyStoreEntry.GordianCoreKeyStorePairSetCertificate;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianCoreKeyStoreEntry.GordianCoreKeyStoreSet;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianPEMObject.GordianPEMObjectType;
import net.sourceforge.joceanus.jgordianknot.impl.core.zip.GordianCoreLock;
import net.sourceforge.joceanus.jgordianknot.impl.core.zip.GordianLockASN1;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;
import net.sourceforge.joceanus.jtethys.date.TethysDate;

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
     * The KeySetSpec.
     */
    private final GordianKeySetHashSpec theKeySetHashSpec;

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
        theKeySetHashSpec = pKeyStore.getKeySetSpec();
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
                                    final GordianCoreLock pLock) throws OceanusException {
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
     * @param pPassword the password
     * @return the decoded object.
     * @throws OceanusException on error
     */
    public GordianKeyStoreEntry importKeyStoreEntry(final InputStream pStream,
                                                    final char[] pPassword) throws OceanusException {
        final List<GordianPEMObject> myObjects = theParser.parsePEMFile(pStream);
        return decodePEMObjectList(myObjects, pPassword);
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
                                                       final GordianCoreLock pLock) throws OceanusException {
        /* Handle certificates */
        if (pEntry instanceof GordianKeyStorePairCertificate) {
             final GordianKeyPairCertificate myCert = ((GordianKeyStorePairCertificate) pEntry).getCertificate();
             return Collections.singletonList(encodeCertificate(myCert));
        }
        if (pEntry instanceof GordianKeyStorePairSetCertificate) {
            final GordianKeyPairSetCertificate myCert = ((GordianKeyStorePairSetCertificate) pEntry).getCertificate();
            return Collections.singletonList(encodeCertificate(myCert));
        }

        /* Handle keyPair and keyPairSet */
        if (pEntry instanceof GordianKeyStorePair) {
            return encodePrivateKeyPair((GordianKeyStorePair) pEntry, pLock);
        }
        if (pEntry instanceof GordianKeyStorePairSet) {
            return encodePrivateKeyPairSet((GordianKeyStorePairSet) pEntry, pLock);
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
     * @param pPassword the password
     * @return the decoded object.
     * @throws OceanusException on error
     */
     private GordianKeyStoreEntry decodePEMObjectList(final List<GordianPEMObject> pObjects,
                                                      final char[] pPassword) throws OceanusException {
         /* List must be non-empty */
         if (pObjects.isEmpty()) {
             throw new GordianDataException("Empty list");
         }

         /* Access first element and switch on object type */
         final GordianPEMObject myFirst = pObjects.get(0);
         switch (myFirst.getObjectType()) {
             /* Decode objects */
             case PRIVATEKEY:
                 return decodeKeyPair(pObjects, pPassword);
             case PRIVATEKEYSET:
                 return decodeKeyPairSet(pObjects, pPassword);
             case KEYPAIRCERT:
                 return decodeKeyPairCertificate(pObjects);
             case KEYPAIRSETCERT:
                 return decodeKeyPairSetCertificate(pObjects);
             case KEYSET:
                 return decodeKeySet(pObjects, pPassword);
             case KEY:
                 return decodeKey(pObjects, pPassword);

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

            /* switch on object type */
            switch (myObject.getObjectType()) {
                /* Decode objects */
                case KEYPAIRCERT:
                    final GordianCoreKeyPairCertificate myKeyPairCert = decodeKeyPairCertificate(myObject);
                    myChain.add(new GordianCoreKeyStorePairCertificate(myKeyPairCert, myDate));
                    break;
                case KEYPAIRSETCERT:
                    final GordianCoreKeyPairSetCertificate myKeyPairSetCert = decodeKeyPairSetCertificate(myObject);
                    myChain.add(new GordianCoreKeyStorePairSetCertificate(myKeyPairSetCert, myDate));
                    break;

                    /* Unsupported entry */
                default:
                    throw new GordianDataException(ERROR_UNSUPPORTED);
            }
        }

        /* Return the chain */
        return myChain;
    }

    /**
     * Encode a keyPairCertificate.
     * @param pCertificate the certificate
     * @return the encoded object.
     */
    static GordianPEMObject encodeCertificate(final GordianKeyPairCertificate pCertificate) {
        return new GordianPEMObject(GordianPEMObjectType.KEYPAIRCERT, pCertificate.getEncoded());
    }

    /**
     * Encode a keyPairSetCertificate.
     * @param pCertificate the certificate
     * @return the encoded object.
     */
    static GordianPEMObject encodeCertificate(final GordianKeyPairSetCertificate pCertificate) {
        return new GordianPEMObject(GordianPEMObjectType.KEYPAIRSETCERT, pCertificate.getEncoded());
    }

    /**
     * Encode a keyPair.
     * @param pKeyPair the keyPair
     * @param pLock the lock
     * @return the encoded object.
     * @throws OceanusException on error
     */
    private List<GordianPEMObject> encodePrivateKeyPair(final GordianKeyStorePair pKeyPair,
                                                        final GordianCoreLock pLock) throws OceanusException {
        /* Create the list */
        final List<GordianPEMObject> myList = new ArrayList<>();

        /* Add the private key entry */
        myList.add(encodePrivateKey(pKeyPair, pLock));

        /* Loop through the certificates */
        for (GordianKeyPairCertificate myCert : pKeyPair.getCertificateChain()) {
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
                                              final GordianCoreLock pLock) throws OceanusException {
        /* Protect against exception */
        try {
            /* Build encoded object and return it */
            final GordianKeySetHash myHash = pLock.getKeySetHash();
            final byte[] mySecuredKey = myHash.getKeySet().securePrivateKey(pKeyPair.getKeyPair());
            final EncryptedPrivateKeyInfo myInfo = buildPrivateKeyInfo(pLock, mySecuredKey);
            return new GordianPEMObject(GordianPEMObjectType.PRIVATEKEY, myInfo.getEncoded());

            /* Handle exceptions */
        } catch (IOException e) {
            throw new GordianIOException("Failed to encode privateKey", e);
        }
    }

    /**
     * Encode a keyPairSet.
     * @param pKeyPairSet the keyPairSet
     * @param pLock the lock
     * @return the encoded object.
     * @throws OceanusException on error
     */
    private List<GordianPEMObject> encodePrivateKeyPairSet(final GordianKeyStorePairSet pKeyPairSet,
                                                           final GordianCoreLock pLock) throws OceanusException {
        /* Create the list */
        final List<GordianPEMObject> myList = new ArrayList<>();

        /* Add the private key entry */
        myList.add(encodePrivateKeySet(pKeyPairSet, pLock));

        /* Loop through the certificates */
        for (GordianKeyPairSetCertificate myCert : pKeyPairSet.getCertificateChain()) {
            /* Add the encoded certificate */
            myList.add(encodeCertificate(myCert));
        }

        /* Return the list */
        return myList;
    }

    /**
     * Encode a privateKeySet.
     * @param pKeyPairSet the keyPairSet
     * @param pLock the lock
     * @return the encoded object.
     * @throws OceanusException on error
     */
    private GordianPEMObject encodePrivateKeySet(final GordianKeyStorePairSet pKeyPairSet,
                                                 final GordianCoreLock pLock) throws OceanusException {
        /* Protect against exception */
        try {
            /* Build encoded object and return it */
            final GordianKeySetHash myHash = pLock.getKeySetHash();
            final byte[] mySecuredKey = myHash.getKeySet().securePrivateKeySet(pKeyPairSet.getKeyPairSet());
            final EncryptedPrivateKeyInfo myInfo = buildPrivateKeyInfo(pLock, mySecuredKey);
            return new GordianPEMObject(GordianPEMObjectType.PRIVATEKEYSET, myInfo.getEncoded());

           /* Handle exceptions */
        } catch (IOException e) {
            throw new GordianIOException("Failed to encode privateKeySet", e);
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
                                          final GordianCoreLock pLock) throws OceanusException {
        /* Protect against exception */
        try {
            /* Build encoded object and return it */
            final GordianKeySetHash myHash = pLock.getKeySetHash();
            final byte[] mySecuredKeySet = myHash.getKeySet().secureKeySet(pKeySet.getKeySet());
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
                                       final GordianCoreLock pLock) throws OceanusException {
        /* Protect against exception */
        try {
            /* Access keyType */
            final GordianKey<?> myKey = pKey.getKey();
            final GordianKnuthObfuscater myObfuscater = theFactory.getObfuscater();
            final int myId = myObfuscater.deriveExternalIdFromType(myKey.getKeyType());
            final byte[] myTypeDef = TethysDataConverter.integerToByteArray(myId);

            /* Secure the key */
            final GordianKeySetHash myHash = pLock.getKeySetHash();
            final byte[] mySecuredKey = myHash.getKeySet().secureKey(myKey);

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
     * Decode a keyPairCertificate.
     * @param pObjects the PEM object list
     * @return the keyPairCertificate.
     * @throws OceanusException on error
     */
    private GordianKeyStorePairCertificate decodeKeyPairCertificate(final List<GordianPEMObject> pObjects) throws OceanusException {
        /* Reject if not singleton list */
        checkSingletonList(pObjects);

        /* parse the certificate */
        return new GordianCoreKeyStorePairCertificate(decodeKeyPairCertificate(pObjects.get(0)), new TethysDate());
    }

    /**
     * Decode a keyPairCertificate.
     * @param pObject the PEM object
     * @return the keyPairCertificate.
     * @throws OceanusException on error
     */
    private GordianCoreKeyPairCertificate decodeKeyPairCertificate(final GordianPEMObject pObject) throws OceanusException {
        /* Reject if not keySetCertificate */
        checkObjectType(pObject, GordianPEMObjectType.KEYPAIRCERT);

        /* parse the encoded bytes */
        return new GordianCoreKeyPairCertificate(theFactory, pObject.getEncoded());
    }

    /**
     * Decode a keyPairSetCertificate.
     * @param pObjects the PEM object list
     * @return the keyPairSetCertificate.
     * @throws OceanusException on error
     */
    private GordianKeyStorePairSetCertificate decodeKeyPairSetCertificate(final List<GordianPEMObject> pObjects) throws OceanusException {
        /* Reject if not singleton list */
        checkSingletonList(pObjects);

        /* parse the certificate */
        return new GordianCoreKeyStorePairSetCertificate(decodeKeyPairSetCertificate(pObjects.get(0)), new TethysDate());
    }

    /**
     * Decode a keyPairSetCertificate.
     * @param pObject the PEM object
     * @return the keyPairSetCertificate.
     * @throws OceanusException on error
     */
    private GordianCoreKeyPairSetCertificate decodeKeyPairSetCertificate(final GordianPEMObject pObject) throws OceanusException {
        /* Reject if not keySetCertificate */
        checkObjectType(pObject, GordianPEMObjectType.KEYPAIRSETCERT);

        /* parse the encoded bytes */
        return new GordianCoreKeyPairSetCertificate(theFactory, pObject.getEncoded());
    }

    /**
     * Decode a keyPair.
     * @param pObjects the list of objects
     * @param pPassword the password
     * @return the keyPair.
     * @throws OceanusException on error
     */
    private GordianKeyStorePair decodeKeyPair(final List<GordianPEMObject> pObjects,
                                              final char[] pPassword) throws OceanusException {
        /* Initialise variables */
        EncryptedPrivateKeyInfo myPrivateInfo = null;
        final List<GordianKeyPairCertificate> myChain = new ArrayList<>();

        /* Loop through the entries */
        for (GordianPEMObject myObject : pObjects) {
            /* Decode private key if first element */
            if (myPrivateInfo == null) {
                myPrivateInfo = EncryptedPrivateKeyInfo.getInstance(myObject.getEncoded());

                /* else decode next certificate in chain */
            } else {
                myChain.add(decodeKeyPairCertificate(myObject));
            }
        }

        /* Check that we have a privateKey and at least one certificate */
        if (myPrivateInfo == null || myChain.isEmpty()) {
            throw new GordianDataException("Insufficient entries");
        }

        /* Derive the keyPair */
        final GordianKeySet mySecuringKeySet = deriveSecuringKeySet(myPrivateInfo, pPassword);
        final GordianCoreKeyPairCertificate myCert = (GordianCoreKeyPairCertificate) myChain.get(0);
        final GordianKeyPair myPair = mySecuringKeySet.deriveKeyPair(myCert.getX509KeySpec(), myPrivateInfo.getEncryptedData());

        /* Return the new keyPair */
        return new GordianCoreKeyStorePair(myPair, myChain, new TethysDate());
    }

    /**
     * Decode a keyPairSet.
     * @param pObjects the list of objects
     * @param pPassword the password
     * @return the keyPairSet.
     * @throws OceanusException on error
     */
    private GordianKeyStorePairSet decodeKeyPairSet(final List<GordianPEMObject> pObjects,
                                                    final char[] pPassword) throws OceanusException {
        /* Initialise variables */
        EncryptedPrivateKeyInfo myPrivateInfo = null;
        final List<GordianKeyPairSetCertificate> myChain = new ArrayList<>();

        /* Loop through the entries */
        for (GordianPEMObject myObject : pObjects) {
            /* Decode private key if first element */
            if (myPrivateInfo == null) {
                myPrivateInfo = EncryptedPrivateKeyInfo.getInstance(myObject.getEncoded());

                /* else decode next certificate in chain */
            } else {
                myChain.add(decodeKeyPairSetCertificate(myObject));
            }
        }

        /* Check that we have a privateKey and at least one certificate */
        if (myPrivateInfo == null || myChain.isEmpty()) {
            throw new GordianDataException("Insufficient entries");
        }

        /* Derive the keyPair */
        final GordianKeySet mySecuringKeySet = deriveSecuringKeySet(myPrivateInfo, pPassword);
        final GordianCoreKeyPairSetCertificate myCert = (GordianCoreKeyPairSetCertificate) myChain.get(0);
        final GordianKeyPairSet myPairSet = mySecuringKeySet.deriveKeyPairSet(myCert.getX509KeySpec(), myPrivateInfo.getEncryptedData());

        /* Return the new keyPairSet */
        return new GordianCoreKeyStorePairSet(myPairSet, myChain, new TethysDate());
    }

    /**
     * Decode a keySet.
     * @param pObjects the PEM object list
     * @param pPassword the password
     * @return the keySet.
     * @throws OceanusException on error
     */
    private GordianKeyStoreSet decodeKeySet(final List<GordianPEMObject> pObjects,
                                            final char[] pPassword) throws OceanusException {
        checkSingletonList(pObjects);
        return decodeKeySet(pObjects.get(0), pPassword);
    }

    /**
     * Decode a keySet.
     * @param pObject the PEM object
     * @param pPassword the password
     * @return the keySet.
     * @throws OceanusException on error
     */
    private GordianCoreKeyStoreSet decodeKeySet(final GordianPEMObject pObject,
                                                final char[] pPassword) throws OceanusException {
        /* Reject if not KeySet */
        checkObjectType(pObject, GordianPEMObjectType.KEYSET);

        /* Derive the securing keySet */
        final EncryptedPrivateKeyInfo myInfo = EncryptedPrivateKeyInfo.getInstance(pObject.getEncoded());
        final GordianKeySet mySecuringKeySet = deriveSecuringKeySet(myInfo, pPassword);

        /* Derive the keySet */
        final GordianKeySet myKeySet = mySecuringKeySet.deriveKeySet(myInfo.getEncryptedData());
        return new GordianCoreKeyStoreSet(myKeySet, new TethysDate());
    }

    /**
     * Decode a key.
     * @param pObjects the PEM object list
     * @param pPassword the password
     * @return the key.
     * @throws OceanusException on error
     */
    private GordianKeyStoreKey<?> decodeKey(final List<GordianPEMObject> pObjects,
                                            final char[] pPassword) throws OceanusException {
        checkSingletonList(pObjects);
        return decodeKey(pObjects.get(0), pPassword);
    }

    /**
     * Decode a key.
     * @param pObject the PEM object
     * @param pPassword the password
     * @return the key.
     * @throws OceanusException on error
     */
    private GordianCoreKeyStoreKey<?> decodeKey(final GordianPEMObject pObject,
                                                final char[] pPassword) throws OceanusException {
        /* Reject if not Key */
        checkObjectType(pObject, GordianPEMObjectType.KEY);

        /* Derive the securing keySet */
        final EncryptedPrivateKeyInfo myInfo = EncryptedPrivateKeyInfo.getInstance(pObject.getEncoded());
        final GordianKeySet mySecuringKeySet = deriveSecuringKeySet(myInfo, pPassword);

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
    private EncryptedPrivateKeyInfo buildPrivateKeyInfo(final GordianCoreLock pLock,
                                                        final byte[] pInfo) {
        return new EncryptedPrivateKeyInfo(pLock.getAlgorithmId(), pInfo);
    }

    /**
     * Derive securing keySet.
     * @param pInfo the encrypted private keyInfo
     * @param pPassword the password
     * @return the keySet
     * @throws OceanusException on error
     */
    private GordianKeySet deriveSecuringKeySet(final EncryptedPrivateKeyInfo pInfo,
                                               final char[] pPassword) throws OceanusException {
        /* Validate the algorithmId */
        final AlgorithmIdentifier myId = pInfo.getEncryptionAlgorithm();
        if (!myId.getAlgorithm().equals(GordianLockASN1.LOCKOID)) {
            throw new GordianDataException("Unsupported algorithm");
        }
        if (theLockResolver == null) {
            throw new GordianDataException("No lock resolver set");
        }

        /* Resolve the lock */
        final GordianCoreLock myLock = new GordianCoreLock(theFactory, myId.getParameters());
        theLockResolver.resolveLock(myLock);
        if (myLock.isLocked()) {
            throw new GordianDataException("Lock was not resolved");
        }

        /* Derive the securing hash */
        final GordianKeySetHash myHash = myLock.getKeySetHash();
        return myHash.getKeySet();
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
