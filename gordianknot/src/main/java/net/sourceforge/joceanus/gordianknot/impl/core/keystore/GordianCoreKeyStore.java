/*
 * GordianKnot: Security Suite
 * Copyright 2012-2026. Tony Washer
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
 */
package net.sourceforge.joceanus.gordianknot.impl.core.keystore;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.gordianknot.api.cert.GordianCertificate;
import net.sourceforge.joceanus.gordianknot.api.cert.GordianCertificateId;
import net.sourceforge.joceanus.gordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreEntry;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStoreCertificate;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStoreKey;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePair;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStoreSet;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianPasswordLockSpec;
import net.sourceforge.joceanus.gordianknot.api.zip.GordianZipFactory;
import net.sourceforge.joceanus.gordianknot.api.zip.GordianZipLock;
import net.sourceforge.joceanus.gordianknot.api.zip.GordianZipWriteFile;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.cert.GordianCoreCertificate;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianIOException;
import net.sourceforge.joceanus.gordianknot.impl.core.keystore.GordianCoreKeyStoreEntry.GordianCoreKeyStoreCertificate;
import net.sourceforge.joceanus.gordianknot.impl.core.keystore.GordianKeyStoreElement.GordianKeyStoreCertificateElement;
import net.sourceforge.joceanus.gordianknot.impl.core.keystore.GordianKeyStoreElement.GordianKeyStoreCertificateHolder;
import net.sourceforge.joceanus.gordianknot.impl.core.keystore.GordianKeyStoreElement.GordianKeyStoreKeyElement;
import net.sourceforge.joceanus.gordianknot.impl.core.keystore.GordianKeyStoreElement.GordianKeyStorePairElement;
import net.sourceforge.joceanus.gordianknot.impl.core.keystore.GordianKeyStoreElement.GordianKeyStoreSetElement;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.x500.X500Name;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * KeyStore implementation.
 * <p>
 * It should be noted that this implementation is based upon a number of assumptions.
 * </p>
 * <ol>
 *     <li>The combination of Subject Name/Subject Id is viewed as a unique identifier for a public key. Multiple Certificates for the same subject,
 *     issued by different authorities must describe the same publicKey. It is expected that different publicKeys for the same subjectName
 *     WILL have different subjectIDs.</li>
 *     <li>An issuerCertificate will only issue a single certificate at a time for a subjectName/subjectID combination.
 *     If two such certificates are received, the later one will overwrite the first one, (assuming the publicKey is the same).
 *     No attempt will be made to determine a BETTER certificate (e.g. longer validity)</li>
 * </ol>
 */
public class GordianCoreKeyStore
        implements GordianBaseKeyStore {
    /**
     * The ZipFile entry name.
     */
    static final String ZIPENTRY = "KeyStore";

    /**
     * The factory.
     */
    private final GordianBaseFactory theFactory;

    /**
     * The passwordLockSpec.
     */
    private final GordianPasswordLockSpec thePasswordLockSpec;

    /**
     * The map of certificates by Subject.
     */
    private final Map<GordianCertificateId, Map<GordianCertificateId, GordianCertificate>> theSubjectCerts;

    /**
     * The map of certificates by Issuer.
     */
    private final Map<GordianCertificateId, Map<GordianCertificateId, GordianCertificate>> theIssuerCerts;

    /**
     * The aliases.
     */
    private final Map<String, GordianKeyStoreEntry> theAliases;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     * @param pSpec    the passwordLockSpec
     */
    GordianCoreKeyStore(final GordianBaseFactory pFactory,
                        final GordianPasswordLockSpec pSpec) {
        /* Store parameters */
        theFactory = pFactory;
        thePasswordLockSpec = pSpec;

        /* Create the maps */
        theSubjectCerts = new LinkedHashMap<>();
        theIssuerCerts = new LinkedHashMap<>();
        theAliases = new LinkedHashMap<>();
    }

    @Override
    public GordianBaseFactory getFactory() {
        return theFactory;
    }

    /**
     * Obtain the passwordLockSpec.
     *
     * @return the passwordLockSpec
     */
    public GordianPasswordLockSpec getPasswordLockSpec() {
        return thePasswordLockSpec;
    }

    @Override
    public Map<GordianCertificateId, Map<GordianCertificateId, GordianCertificate>> getSubjectMapOfMaps() {
        return theSubjectCerts;
    }

    /**
     * Obtain the issuerMapofMaps.
     *
     * @return the map
     */
    private Map<GordianCertificateId, Map<GordianCertificateId, GordianCertificate>> getIssuerMapOfMaps() {
        return theIssuerCerts;
    }

    @Override
    public Map<String, GordianKeyStoreEntry> getAliasMap() {
        return theAliases;
    }

    @Override
    public GordianCertificate getCertificate(final GordianKeyStoreCertificateKey pKey) {
        final Map<GordianCertificateId, GordianCertificate> myCertMap = theSubjectCerts.get(pKey.getSubject());
        return myCertMap == null ? null : myCertMap.get(pKey.getIssuer());
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(theAliases.keySet());
    }

    @Override
    public boolean containsAlias(final String pAlias) {
        return theAliases.containsKey(pAlias);
    }

    @Override
    public int size() {
        return theAliases.size();
    }

    /**
     * Reset the store.
     */
    public void reset() {
        theAliases.clear();
        theSubjectCerts.clear();
        theIssuerCerts.clear();
    }

    @Override
    public void deleteEntry(final String pAlias) {
        /* Remove the existing entry */
        final GordianKeyStoreEntry myEntry = theAliases.remove(pAlias);

        /* Nothing more to do unless we are removing a certificate */
        if (!(myEntry instanceof GordianKeyStoreCertificateHolder myCertHolder)) {
            return;
        }

        /* Access the certificate */
        final GordianCertificate myCert = getCertificate(myCertHolder.getCertificateKey());

        /* If the certificate is not referenced by any other alias */
        if (getCertificateAlias(myCert) == null) {
            /* Remove the certificate from the maps */
            removeCertificate(myCert);
        }
    }

    /**
     * Remove certificate from maps.
     *
     * @param pCertificate the certificate to remove
     */
    private void removeCertificate(final GordianCertificate pCertificate) {
        /* Access the ids of the certificate */
        final GordianCertificateId mySubjectId = pCertificate.getSubject();
        final GordianCertificateId myIssuerId = pCertificate.getIssuer();

        /* If it is not referenced as issuer by any other certificate */
        Map<GordianCertificateId, GordianCertificate> myIssuedMap = theIssuerCerts.get(mySubjectId);
        if (isWithoutIssue(myIssuedMap)) {
            /* If the certificate is not self-signed */
            if (!pCertificate.isSelfSigned()) {
                /* Remove the certificate from the issuer map */
                myIssuedMap = theIssuerCerts.get(myIssuerId);
                myIssuedMap.remove(mySubjectId);

                /* If the issuer now has no issued certificates */
                if (isWithoutIssue(myIssuedMap)) {
                    /* Purge orphan issuers */
                    purgeOrphanIssuers(myIssuerId);
                }
            }

            /* Remove the certificate from the subject map */
            final Map<GordianCertificateId, GordianCertificate> myCertMap = theSubjectCerts.get(mySubjectId);
            myCertMap.remove(myIssuerId);
            if (myCertMap.isEmpty()) {
                theSubjectCerts.remove(mySubjectId);
            }
        }
    }

    /**
     * Determine whether an issuer has no children (other than self).
     *
     * @param pIssuer the issuer Map
     * @return true/false
     */
    private static boolean isWithoutIssue(final Map<GordianCertificateId, GordianCertificate> pIssuer) {
        /* If the map is null or empty then there are no children */
        if (pIssuer == null || pIssuer.isEmpty()) {
            return true;
        }

        /* If there is only one certificate in the map */
        if (pIssuer.size() == 1) {
            /* If the single certificate is self-signed then we are childless */
            final GordianCertificate myCert = pIssuer.values().iterator().next();
            return myCert.isSelfSigned();
        }

        /* Not childless */
        return false;
    }

    /**
     * Purge orphan issuers.
     *
     * @param pIssuerId the issuerId to purge
     */
    private void purgeOrphanIssuers(final GordianCertificateId pIssuerId) {
        /* Remove the entry */
        theIssuerCerts.remove(pIssuerId);

        /* Look for all issuers */
        final Map<GordianCertificateId, GordianCertificate> myCertMap = theSubjectCerts.get(pIssuerId);
        final List<GordianCertificate> myCerts = new ArrayList<>(myCertMap.values());

        /* Loop through all the certificates */
        for (GordianCertificate myCert : myCerts) {
            /* If the certificate is not referenced by any other alias */
            if (getCertificateAlias(myCert) == null) {
                /* Remove the certificate from the maps */
                removeCertificate(myCert);
            }
        }
    }

    @Override
    public void setCertificate(final String pAlias,
                               final GordianCertificate pCertificate) throws GordianException {
        /* Check the alias */
        checkAlias(pAlias);

        /* Check that we are not about to replace a non-certificate */
        final GordianKeyStoreEntry myEntry = theAliases.get(pAlias);
        if (myEntry != null
                && !(myEntry instanceof GordianCoreKeyStoreCertificate)) {
            throw new GordianDataException("Alias already exists for non-certificate");
        }

        /* Remove any existing entry */
        deleteEntry(pAlias);

        /* Set the new value */
        final GordianKeyStoreCertificateElement myCert = new GordianKeyStoreCertificateElement(pCertificate);
        theAliases.put(pAlias, myCert);

        /* Store certificate */
        storeCertificate(pCertificate);
    }

    @Override
    public void setKeyPair(final String pAlias,
                           final GordianKeyPair pKeyPair,
                           final char[] pPassword,
                           final List<GordianCertificate> pCertificateChain) throws GordianException {
        /* Check the alias */
        checkAlias(pAlias);

        /* Make sure that the keyPair has a private key */
        if (pKeyPair.isPublicOnly()) {
            throw new GordianDataException("Private Key missing");
        }

        /* Make sure that we have a valid certificate chain */
        checkChain(pKeyPair, pCertificateChain);

        /* Remove any existing entry */
        deleteEntry(pAlias);

        /* Set the new value */
        final GordianKeyStorePairElement myPair = new GordianKeyStorePairElement(theFactory, thePasswordLockSpec, pKeyPair, pPassword, pCertificateChain);
        theAliases.put(pAlias, myPair);

        /* Store all the certificates in the chain */
        for (GordianCertificate myCert : pCertificateChain) {
            storeCertificate(myCert);
        }
    }

    @Override
    public void updateCertificateChain(final String pAlias,
                                       final List<GordianCertificate> pCertificateChain) throws GordianException {
        /* Obtain the keyStore Entry */
        final GordianKeyStoreEntry myEntry = theAliases.get(pAlias);
        if (!(myEntry instanceof GordianKeyStorePairElement myKeyPairElement)) {
            throw new GordianDataException("Entry not found");
        }

        /* Access old keyPair */
        final List<GordianCertificate> myChain = myKeyPairElement.buildChain(this);
        final GordianKeyPair myKeyPair = myChain.getFirst().getKeyPair();

        /* Make sure that we have a valid certificate chain */
        checkChain(myKeyPair, pCertificateChain);

        /* Access old certificate */
        final GordianCertificate myOldCert = getCertificate(myKeyPairElement.getCertificateKey());

        /* If the certificate is not referenced by any other alias */
        if (getCertificateAlias(myOldCert) == null) {
            /* Remove the certificate from the maps */
            removeCertificate(myOldCert);
        }

        /* Update the chain */
        myKeyPairElement.updateChain(pCertificateChain);

        /* Store all the certificates in the chain */
        for (GordianCertificate myCert : pCertificateChain) {
            storeCertificate(myCert);
        }
    }

    @Override
    public <T extends GordianKeySpec> void setKey(final String pAlias,
                                                  final GordianKey<T> pKey,
                                                  final char[] pPassword) throws GordianException {
        /* Check the alias */
        checkAlias(pAlias);

        /* Remove any existing entry */
        deleteEntry(pAlias);

        /* Set the new value */
        final GordianKeyStoreKeyElement<T> myKey = new GordianKeyStoreKeyElement<>(theFactory, thePasswordLockSpec, pKey, pPassword);
        theAliases.put(pAlias, myKey);
    }

    @Override
    public void setKeySet(final String pAlias,
                          final GordianKeySet pKeySet,
                          final char[] pPassword) throws GordianException {
        /* Check the alias */
        checkAlias(pAlias);

        /* Remove any existing entry */
        deleteEntry(pAlias);

        /* Set the new value */
        final GordianKeyStoreSetElement mySet = new GordianKeyStoreSetElement(theFactory, thePasswordLockSpec, pKeySet, pPassword);
        theAliases.put(pAlias, mySet);
    }

    /**
     * Check that the alias is valid.
     *
     * @param pAlias the alias
     */
    private static void checkAlias(final String pAlias) {
        if (pAlias == null) {
            throw new NullPointerException();
        }
    }

    @Override
    public <T extends GordianKeyStoreEntry> boolean entryInstanceOf(final String pAlias,
                                                                    final Class<T> pClazz) {
        if (pClazz.isAssignableFrom(GordianKeyStoreCertificate.class)) {
            return isCertificateEntry(pAlias);
        }
        if (pClazz.isAssignableFrom(GordianKeyStorePair.class)) {
            return isKeyPairEntry(pAlias);
        }
        if (pClazz.isAssignableFrom(GordianKeyStoreKey.class)) {
            return isKeyEntry(pAlias);
        }
        if (pClazz.isAssignableFrom(GordianKeyStoreSet.class)) {
            return isKeySetEntry(pAlias);
        }
        return false;
    }

    @Override
    public boolean isCertificateEntry(final String pAlias) {
        return theAliases.get(pAlias) instanceof GordianKeyStoreCertificateElement;
    }

    @Override
    public boolean isKeyPairEntry(final String pAlias) {
        return theAliases.get(pAlias) instanceof GordianKeyStorePairElement;
    }

    @Override
    public boolean isKeyEntry(final String pAlias) {
        return theAliases.get(pAlias) instanceof GordianKeyStoreKeyElement;
    }

    @Override
    public boolean isKeySetEntry(final String pAlias) {
        return theAliases.get(pAlias) instanceof GordianKeyStoreSetElement;
    }

    @Override
    public GordianKeyStoreEntry getEntry(final String pAlias,
                                         final char[] pPassword) throws GordianException {
        if (isCertificateEntry(pAlias)) {
            return getKeyStoreCertificate(pAlias);
        }
        if (isKeyPairEntry(pAlias)) {
            return getKeyStorePair(pAlias, pPassword);
        }
        if (isKeyEntry(pAlias)) {
            return getKeyStoreKey(pAlias, pPassword);
        }
        return isKeySetEntry(pAlias)
                ? getKeyStoreSet(pAlias, pPassword)
                : null;
    }

    @Override
    public GordianCertificate getCertificate(final String pAlias) {
        final GordianKeyStoreCertificate myCert = getKeyStoreCertificate(pAlias);
        if (myCert != null) {
            return myCert.getCertificate();
        }
        final List<GordianCertificate> myChain = getCertificateChain(pAlias);
        return myChain == null || myChain.isEmpty() ? null : myChain.getFirst();
    }

    /**
     * Obtain the keyStorePairCertificate.
     *
     * @param pAlias the alias
     * @return the certificate entry (or null)
     */
    private GordianKeyStoreCertificate getKeyStoreCertificate(final String pAlias) {
        final GordianKeyStoreEntry myEntry = theAliases.get(pAlias);
        return myEntry instanceof GordianKeyStoreCertificateElement myElement
                ? myElement.buildEntry(this)
                : null;
    }

    @Override
    public GordianKeyPair getKeyPair(final String pAlias,
                                     final char[] pPassword) throws GordianException {
        final GordianKeyStorePair myPair = getKeyStorePair(pAlias, pPassword);
        return myPair == null ? null : myPair.getKeyPair();
    }

    @Override
    public List<GordianCertificate> getCertificateChain(final String pAlias) {
        final GordianKeyStoreEntry myEntry = theAliases.get(pAlias);
        return myEntry instanceof GordianKeyStorePairElement myElement
                ? myElement.buildChain(this)
                : null;
    }

    /**
     * Obtain the keyStorePair.
     *
     * @param pAlias    the alias
     * @param pPassword the password
     * @return the keyPair entry (or null)
     */
    private GordianKeyStorePair getKeyStorePair(final String pAlias,
                                                final char[] pPassword) throws GordianException {
        final GordianKeyStoreEntry myEntry = theAliases.get(pAlias);
        return myEntry instanceof GordianKeyStorePairElement myElement
                ? myElement.buildEntry(this, pPassword)
                : null;
    }

    @Override
    public <T extends GordianKeySpec> GordianKey<T> getKey(final String pAlias,
                                                           final char[] pPassword) throws GordianException {
        final GordianKeyStoreKey<T> myKey = getKeyStoreKey(pAlias, pPassword);
        return myKey == null ? null : myKey.getKey();
    }

    /**
     * Obtain the keyStoreKey.
     *
     * @param <T>       the keyType
     * @param pAlias    the alias
     * @param pPassword the password
     * @return the key entry (or null)
     */
    @SuppressWarnings("unchecked")
    private <T extends GordianKeySpec> GordianKeyStoreKey<T> getKeyStoreKey(final String pAlias,
                                                                            final char[] pPassword) throws GordianException {
        final GordianKeyStoreEntry myEntry = theAliases.get(pAlias);
        return myEntry instanceof GordianKeyStoreKeyElement
                ? ((GordianKeyStoreKeyElement<T>) myEntry).buildEntry(this, pPassword)
                : null;
    }

    @Override
    public GordianKeySet getKeySet(final String pAlias,
                                   final char[] pPassword) throws GordianException {
        final GordianKeyStoreSet mySet = getKeyStoreSet(pAlias, pPassword);
        return mySet == null ? null : mySet.getKeySet();
    }

    /**
     * Obtain the keyStoreKeySet.
     *
     * @param pAlias    the alias
     * @param pPassword the password
     * @return the keySet entry (or null)
     */
    private GordianKeyStoreSet getKeyStoreSet(final String pAlias,
                                              final char[] pPassword) throws GordianException {
        final GordianKeyStoreEntry myEntry = theAliases.get(pAlias);
        return myEntry instanceof GordianKeyStoreSetElement myElement
                ? myElement.buildEntry(this, pPassword)
                : null;
    }

    @Override
    public LocalDate getCreationDate(final String pAlias) {
        final GordianKeyStoreEntry myEntry = theAliases.get(pAlias);
        return myEntry != null ? myEntry.getCreationDate() : null;
    }

    @Override
    public String getCertificateAlias(final GordianCertificate pCertificate) {
        /* Loop through the alias entries */
        for (Entry<String, GordianKeyStoreEntry> myRecord : theAliases.entrySet()) {
            /* Check for match on certificate entry */
            final GordianKeyStoreEntry myEntry = myRecord.getValue();
            if (myEntry instanceof GordianKeyStoreCertificateHolder myHolder) {
                final GordianKeyStoreCertificateKey myKey = myHolder.getCertificateKey();
                final GordianCertificate myCert = getCertificate(myKey);
                if (pCertificate.equals(myCert)) {
                    return myRecord.getKey();
                }
            }
        }

        /* Not found */
        return null;
    }

    @Override
    public void storeCertificate(final GordianCertificate pCertificate) {
        /* Access the ids */
        final GordianCertificateId mySubjectId = pCertificate.getSubject();
        final GordianCertificateId myIssuerId = pCertificate.getIssuer();

        /* Add the certificate to the list of certificates for this subject */
        Map<GordianCertificateId, GordianCertificate> myMap = theSubjectCerts.computeIfAbsent(mySubjectId, i -> new LinkedHashMap<>());
        myMap.put(myIssuerId, pCertificate);

        /* Add the certificate to the list of certificates for this issuer */
        myMap = theIssuerCerts.computeIfAbsent(myIssuerId, i -> new LinkedHashMap<>());
        myMap.put(mySubjectId, pCertificate);
    }

    /**
     * Check validity of certificate chain.
     *
     * @param pKeyPair the keyPair
     * @param pChain   the certificate chain
     * @throws GordianException on error
     */
    private void checkChain(final GordianKeyPair pKeyPair,
                            final List<GordianCertificate> pChain) throws GordianException {
        /* Make sure that we have a chain */
        if (pChain == null || pChain.isEmpty()) {
            throw new GordianDataException("Empty chain");
        }

        /* Make sure that the keyPair matches end-entity certificate */
        final GordianCoreCertificate myCert = (GordianCoreCertificate) pChain.getFirst();
        if (!myCert.checkMatchingPublicKey(pKeyPair)) {
            throw new GordianDataException("End-entity certificate does not match keyPair");
        }

        /* Loop through the certificates */
        final int mySize = pChain.size();
        for (int i = 0; i < mySize - 1; i++) {
            /* Access the certificate */
            final GordianCoreCertificate myTestCert = (GordianCoreCertificate) pChain.get(i);
            final GordianCoreCertificate mySignerCert = (GordianCoreCertificate) pChain.get(i + 1);

            /* Check the hierarchy */
            if (!myTestCert.validateCertificate(mySignerCert)) {
                throw new GordianDataException("Invalid certificate in path");
            }

            /* Look up any existing certificate for the signer */
            final Map<GordianCertificateId, GordianCertificate> myMap = theSubjectCerts.get(mySignerCert.getSubject());
            if (myMap != null) {
                final GordianCoreCertificate myExisting = (GordianCoreCertificate) myMap.values().iterator().next();
                if (!myExisting.checkMatchingPublicKey(mySignerCert.getKeyPair())) {
                    throw new GordianDataException("Intermediate certificate does not match existing keyPair");
                }
            }
        }

        /* Check that we are anchored by a root certificate */
        final GordianCertificate myRoot = pChain.get(mySize - 1);
        if (!((GordianCoreCertificate) myRoot).validateRootCertificate()) {
            throw new GordianDataException("Invalid root certificate");
        }

        /* Check that the root is known if the depth is greater than 1 */
        if (mySize > 1 && getCertificateAlias(myRoot) == null) {
            throw new GordianDataException("Unknown root certificate");
        }
    }

    @Override
    public String findIssuerCert(final IssuerAndSerialNumber pIssuer) throws GordianException {
        /* Loop through the alias entries */
        final X500Name myIssuer = pIssuer.getName();
        final BigInteger mySerial = pIssuer.getSerialNumber().getValue();
        for (Entry<String, GordianKeyStoreEntry> myEntry : theAliases.entrySet()) {
            /* If this is a keyPair(Set) entry */
            if (myEntry.getValue() instanceof GordianKeyStorePairElement myPair) {
                /* Access details */
                final GordianKeyStoreCertificateKey myCertKey = myPair.getCertificateChain().getFirst();
                final GordianCoreCertificate myCert = (GordianCoreCertificate) getCertificate(myCertKey);

                /* Return alias if we have a match */
                if (myIssuer.equals(myCert.getIssuer().getName())
                        && mySerial.equals(myCert.getSerialNo())) {
                    return myEntry.getKey();
                }
            }
        }

        /* Reject request */
        throw new GordianDataException("Issuer not found");
    }

    @Override
    public void storeToFile(final File pFile,
                            final char[] pPassword) throws GordianException {
        try {
            storeToStream(new FileOutputStream(pFile), pPassword);
        } catch (IOException e) {
            throw new GordianIOException("Failed to store to file", e);
        }
    }

    @Override
    public void storeToStream(final OutputStream pOutputStream,
                              final char[] pPassword) throws GordianException {
        /* Access the Factories */
        final GordianZipFactory myZipFactory = theFactory.getZipFactory();

        /* Create the lock */
        final GordianZipLock myLock = myZipFactory.factoryZipLock(pPassword);

        /* Create the Zip file */
        try (GordianZipWriteFile myZipFile = myZipFactory.createZipFile(myLock, pOutputStream)) {
            /* Create the XML representation */
            final GordianKeyStoreDocument myDocument = new GordianKeyStoreDocument(this);

            /* Write the document to the file */
            myZipFile.writeXMLDocument(new File(ZIPENTRY), myDocument.getDocument());

            /* Catch Exceptions */
        } catch (IOException e) {
            throw new GordianIOException("Failed to store to stream", e);
        }
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial case */
        if (pThat == this) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Ensure object is correct class */
        return pThat instanceof GordianCoreKeyStore myThat
                && theSubjectCerts.equals(myThat.getSubjectMapOfMaps())
                && theIssuerCerts.equals(myThat.getIssuerMapOfMaps())
                && theAliases.equals(myThat.getAliasMap());
    }

    @Override
    public int hashCode() {
        return Objects.hash(theSubjectCerts, theIssuerCerts, theAliases);
    }
}
