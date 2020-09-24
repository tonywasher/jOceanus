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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bouncycastle.asn1.x500.X500Name;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianKeyPairFactory;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHashSpec;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianCertificateId;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairCertificate;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairUsage;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStore;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStoreCertificate;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStoreHash;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStoreKey;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePair;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePairCertificate;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStoreSet;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipFactory;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipLock;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipWriteFile;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypair.GordianCoreKeyPair;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianKeyStoreElement.GordianKeyStoreCertificateElement;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianKeyStoreElement.GordianKeyStoreCertificateHolder;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianKeyStoreElement.GordianKeyStoreCertificateKey;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianKeyStoreElement.GordianKeyStoreHashElement;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianKeyStoreElement.GordianKeyStoreKeyElement;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianKeyStoreElement.GordianKeyStorePairElement;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianKeyStoreElement.GordianKeyStoreSetElement;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;

/**
 * KeyStore implementation.
 * <p>
 * It should be noted that this implementation is based upon a number of assumptions.
 * <ol>
 *     <li>The combination of Subject Name/Subject Id is viewed as a unique identifier for a public key. Multiple Certificates for the same subject,
 *     issued by different authorities must describe the same publicKey. It is expected that different publicKeys for the same subjectName
 *     WILL have different subjectIDs.</li>
 *     <li>An issuerCertificate will only issue a single certificate at a time for a subjectName/subjectID combination.
 *     If two such certificates are received, the later one will overwrite the first one, (assuming the publicKey is the same).
 *     No attempt will be made to determine a BETTER certificate (e.g. longer validity)</li>
 * </ol>
 * </p>
 */
public class GordianCoreKeyStore
        implements GordianKeyStore {
    /**
     * The ZipFile entry name.
     */
    static final String ZIPENTRY = "KeyStore";

    /**
     * The factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * The keySetSpec.
     */
    private final GordianKeySetHashSpec theKeySetSpec;

    /**
     * The map of certificates by Subject.
     */
    private final Map<GordianCertificateId, Map<GordianCertificateId, GordianCoreKeyPairCertificate>> theSubjectCerts;

    /**
     * The map of certificates by Issuer.
     */
    private final Map<GordianCertificateId, Map<GordianCertificateId, GordianCoreKeyPairCertificate>> theIssuerCerts;

    /**
     * The aliases.
     */
    private final Map<String, GordianCoreKeyStoreEntry> theAliases;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pSpec the keySetSpec
     */
    GordianCoreKeyStore(final GordianCoreFactory pFactory,
                        final GordianKeySetHashSpec pSpec) {
        /* Store parameters */
        theFactory = pFactory;
        theKeySetSpec = pSpec;

        /* Create the maps */
        theSubjectCerts = new LinkedHashMap<>();
        theIssuerCerts = new LinkedHashMap<>();
        theAliases = new LinkedHashMap<>();
    }

    /**
     * Obtain the factory.
     * @return the factory
     */
    GordianCoreFactory getFactory() {
        return theFactory;
    }

    /**
     * Obtain the keySetHashSpec.
     * @return the keySetHashSpec
     */
    GordianKeySetHashSpec getKeySetSpec() {
        return theKeySetSpec;
    }

    /**
     * Obtain the subjectMapOfMaps.
     * @return the map
     */
    Map<GordianCertificateId, Map<GordianCertificateId, GordianCoreKeyPairCertificate>> getSubjectMapOfMaps() {
        return theSubjectCerts;
    }

    /**
     * Obtain the issuerMapofMaps.
     * @return the map
     */
    private Map<GordianCertificateId, Map<GordianCertificateId, GordianCoreKeyPairCertificate>> getIssuerMapOfMaps() {
        return theIssuerCerts;
    }

    /**
     * Obtain the issuerMapofMaps.
     * @return the map
     */
    Map<String, GordianCoreKeyStoreEntry> getAliasMap() {
        return theAliases;
    }

    /**
     * Obtain the certificate.
     * @param pKey the key of the certificate
     * @return the certificate
     */
    GordianCoreKeyPairCertificate getCertificate(final GordianKeyStoreCertificateKey pKey) {
        final Map<GordianCertificateId, GordianCoreKeyPairCertificate> myCertMap = theSubjectCerts.get(pKey.getSubject());
        return myCertMap == null ? null : myCertMap.get(pKey.getIssuer());
    }

    @Override
    public GordianKeyStorePair createRootKeyPair(final GordianKeyPairSpec pKeySpec,
                                                 final X500Name pSubject,
                                                 final String pAlias,
                                                 final char[] pPassword) throws OceanusException {
        /* Create the new keyPair */
        final GordianKeyPairFactory myFactory = theFactory.getKeyPairFactory();
        final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(pKeySpec);
        final GordianCoreKeyPair myKeyPair = (GordianCoreKeyPair) myGenerator.generateKeyPair();

        /* Create the certificate */
        final GordianCoreKeyPairCertificate myCert = new GordianCoreKeyPairCertificate(theFactory, myKeyPair, pSubject);
        final GordianKeyPairCertificate[] myChain = new GordianKeyPairCertificate[] { myCert };

        /* Record into keyStore */
        setKeyPair(pAlias, myKeyPair, pPassword, myChain);
        return getKeyPair(pAlias, pPassword);
    }


    @Override
    public GordianKeyStorePair createKeyPair(final GordianKeyPairSpec pKeySpec,
                                             final X500Name pSubject,
                                             final GordianKeyPairUsage pUsage,
                                             final GordianKeyStorePair pSigner,
                                             final String pAlias,
                                             final char[] pPassword) throws OceanusException {
        /* Create the new keyPair */
        final GordianKeyPairFactory myFactory = theFactory.getKeyPairFactory();
        final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(pKeySpec);
        final GordianCoreKeyPair myKeyPair = (GordianCoreKeyPair) myGenerator.generateKeyPair();

        /* Create the certificate */
        final GordianCoreKeyPairCertificate myCert = new GordianCoreKeyPairCertificate(theFactory, pSigner, myKeyPair, pSubject, pUsage);

        /* Create the new chain */
        final GordianKeyPairCertificate[] myParentChain = (GordianKeyPairCertificate[]) pSigner.getCertificateChain();
        final GordianKeyPairCertificate[] myChain = new GordianKeyPairCertificate[myParentChain.length + 1];
        myChain[0] = myCert;
        System.arraycopy(myParentChain, 0, myChain, 1, myParentChain.length);

        /* Record into keyStore */
        setKeyPair(pAlias, myKeyPair, pPassword, myChain);
        return getKeyPair(pAlias, pPassword);
    }

    @Override
    public GordianKeyStorePair createAlternate(final GordianKeyStorePair pKeyPair,
                                               final GordianKeyPairUsage pUsage,
                                               final GordianKeyStorePair pSigner,
                                               final String pAlias,
                                               final char[] pPassword) throws OceanusException {
        /* Access the keyPair and subject */
        final GordianCoreKeyPair myKeyPair = (GordianCoreKeyPair) pKeyPair.getKeyPair();
        final X500Name mySubject = pKeyPair.getCertificateChain()[0].getSubject().getName();

        /* Create the certificate */
        final GordianCoreKeyPairCertificate myCert = new GordianCoreKeyPairCertificate(theFactory, pSigner, myKeyPair, mySubject, pUsage);

        /* Create the new chain */
        final GordianKeyPairCertificate[] myParentChain = (GordianKeyPairCertificate[]) pSigner.getCertificateChain();
        final GordianKeyPairCertificate[] myChain = new GordianKeyPairCertificate[myParentChain.length + 1];
        myChain[0] = myCert;
        System.arraycopy(myParentChain, 0, myChain, 1, myParentChain.length);

        /* Record into keyStore */
        setKeyPair(pAlias, myKeyPair, pPassword, myChain);
        return getKeyPair(pAlias, pPassword);
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

    @Override
    public void deleteEntry(final String pAlias) {
        /* Remove the existing entry */
        final GordianCoreKeyStoreEntry myEntry = theAliases.remove(pAlias);

        /* Nothing more to do unless we are removing a certificate */
        if (!(myEntry instanceof GordianKeyStoreCertificateHolder)) {
            return;
        }

        /* Access the certificate */
        final GordianKeyStoreCertificateHolder myCertHolder = (GordianKeyStoreCertificateHolder) myEntry;
        final GordianCoreKeyPairCertificate myCert = getCertificate(myCertHolder.getCertificateKey());

        /* If the certificate is not referenced by any other alias */
        if (getCertificateAlias(myCert) == null) {
            /* Remove the certificate from the maps */
            removeCertificate(myCert);
        }
    }

    /**
     * Remove certificate from maps.
     * @param pCertificate the certificate to remove
     */
    private void removeCertificate(final GordianCoreKeyPairCertificate pCertificate) {
        /* Access the ids of the certificate */
        final GordianCertificateId mySubjectId = pCertificate.getSubject();
        final GordianCertificateId myIssuerId = pCertificate.getIssuer();

        /* If it is not referenced as issuer by any other certificate */
        Map<GordianCertificateId, GordianCoreKeyPairCertificate> myIssuedMap = theIssuerCerts.get(mySubjectId);
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
            final Map<GordianCertificateId, GordianCoreKeyPairCertificate> myCertMap = theSubjectCerts.get(mySubjectId);
            myCertMap.remove(myIssuerId);
            if (myCertMap.isEmpty()) {
                theSubjectCerts.remove(mySubjectId);
            }
        }
    }

    /**
     * Determine whether an issuer has no children (other than self).
     * @param pIssuer the issuer Map
     * @return true/false
     */
    private static boolean isWithoutIssue(final Map<GordianCertificateId, GordianCoreKeyPairCertificate> pIssuer) {
        /* If the map is null or empty then there are no children */
        if (pIssuer == null || pIssuer.isEmpty()) {
            return true;
        }

        /* If there is only one certificate in the map */
        if (pIssuer.size() == 1) {
            /* If the single certificate is self-signed then we are childless */
            final GordianCoreKeyPairCertificate myCert = pIssuer.values().iterator().next();
            return myCert.isSelfSigned();
        }

        /* Not childless */
        return false;
    }

    /**
     * Purge orphan issuers.
     * @param pIssuerId the issuerId to purge
     */
    private void purgeOrphanIssuers(final GordianCertificateId pIssuerId) {
        /* Remove the entry */
        theIssuerCerts.remove(pIssuerId);

        /* Look for all issuers */
        final Map<GordianCertificateId, GordianCoreKeyPairCertificate> myCertMap = theSubjectCerts.get(pIssuerId);

        /* Loop through all the certificates */
        for (GordianCoreKeyPairCertificate myCert : myCertMap.values()) {
            /* If the certificate is not referenced by any other alias */
            if (getCertificateAlias(myCert) == null) {
                /* Remove the certificate from the maps */
                removeCertificate(myCert);
            }
        }
    }

    @Override
    public void setCertificate(final String pAlias,
                               final GordianKeyPairCertificate pCertificate) throws OceanusException {
        /* Check the alias */
        checkAlias(pAlias);

        /* Check that we are not about to replace a non-certificate */
        final GordianKeyStoreEntry myEntry = theAliases.get(pAlias);
        if (myEntry != null
                && !(myEntry instanceof GordianKeyStoreCertificate)) {
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
                           final GordianKeyPairCertificate[] pCertificateChain) throws OceanusException {
        /* Check the alias */
        checkAlias(pAlias);

        /* Make sure that we have a valid certificate chain */
        checkChain(pKeyPair, pCertificateChain);

        /* Remove any existing entry */
        deleteEntry(pAlias);

        /* Set the new value */
        final GordianKeyStorePairElement myPair = new GordianKeyStorePairElement(theFactory, theKeySetSpec, pKeyPair, pPassword, pCertificateChain);
        theAliases.put(pAlias, myPair);

        /* Store all the certificates in the chain */
        for (GordianKeyPairCertificate myCert : pCertificateChain) {
            storeCertificate(myCert);
        }
    }

    @Override
    public <T extends GordianKeySpec> void setKey(final String pAlias,
                                                  final GordianKey<T> pKey,
                                                  final char[] pPassword) throws OceanusException {
        /* Check the alias */
        checkAlias(pAlias);

        /* Remove any existing entry */
        deleteEntry(pAlias);

        /* Set the new value */
        final GordianKeyStoreKeyElement<T> myKey = new GordianKeyStoreKeyElement<>(theFactory, theKeySetSpec, pKey, pPassword);
        theAliases.put(pAlias, myKey);
    }

    @Override
    public void setKeySet(final String pAlias,
                          final GordianKeySet pKeySet,
                          final char[] pPassword) throws OceanusException {
        /* Check the alias */
        checkAlias(pAlias);

        /* Remove any existing entry */
        deleteEntry(pAlias);

        /* Set the new value */
        final GordianKeyStoreSetElement mySet = new GordianKeyStoreSetElement(theFactory, theKeySetSpec, pKeySet, pPassword);
        theAliases.put(pAlias, mySet);
    }

    @Override
    public void setKeySetHash(final String pAlias,
                              final GordianKeySetHash pHash) {
        /* Check the alias */
        checkAlias(pAlias);

        /* Set the new value */
        final GordianKeyStoreHashElement myHash = new GordianKeyStoreHashElement(pHash);
        theAliases.put(pAlias, myHash);
    }

    /**
     * Check that the alias is valid.
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
        if (pClazz.isAssignableFrom(GordianKeyStoreHash.class)) {
            return isKeySetHashEntry(pAlias);
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
    public boolean isKeySetHashEntry(final String pAlias) {
        return theAliases.get(pAlias) instanceof GordianKeyStoreHashElement;
    }

    @Override
    public GordianKeyStoreEntry getEntry(final String pAlias,
                                         final char[] pPassword) throws OceanusException {
        if (isCertificateEntry(pAlias)) {
            return getCertificate(pAlias);
        }
        if (isKeyPairEntry(pAlias)) {
            return getKeyPair(pAlias, pPassword);
        }
        if (isKeyEntry(pAlias)) {
            return getKey(pAlias, pPassword);
        }
        if (isKeySetHashEntry(pAlias)) {
            return getKeySetHash(pAlias, pPassword);
        }
        return isKeySetEntry(pAlias)
                ? getKeySet(pAlias, pPassword)
                : null;
    }

    @Override
    public GordianKeyStorePairCertificate getCertificate(final String pAlias) {
        final GordianCoreKeyStoreEntry myEntry = theAliases.get(pAlias);
        return myEntry instanceof GordianKeyStoreCertificateElement
                ? ((GordianKeyStoreCertificateElement) myEntry).buildEntry(this)
                : null;
    }

    @Override
    public GordianKeyStorePair getKeyPair(final String pAlias,
                                          final char[] pPassword) throws OceanusException {
        final GordianCoreKeyStoreEntry myEntry = theAliases.get(pAlias);
        return myEntry instanceof GordianKeyStorePairElement
               ? ((GordianKeyStorePairElement) myEntry).buildEntry(this, pPassword)
               : null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends GordianKeySpec> GordianKeyStoreKey<T> getKey(final String pAlias,
                                                                   final char[] pPassword) throws OceanusException {
        final GordianCoreKeyStoreEntry myEntry = theAliases.get(pAlias);
        return myEntry instanceof GordianKeyStoreKeyElement
               ? ((GordianKeyStoreKeyElement<T>) myEntry).buildEntry(this, pPassword)
               : null;
    }

    @Override
    public GordianKeyStoreSet getKeySet(final String pAlias,
                                        final char[] pPassword) throws OceanusException {
        final GordianCoreKeyStoreEntry myEntry = theAliases.get(pAlias);
        return myEntry instanceof GordianKeyStoreSetElement
               ? ((GordianKeyStoreSetElement) myEntry).buildEntry(this, pPassword)
               : null;
    }

    @Override
    public GordianKeyStoreHash getKeySetHash(final String pAlias,
                                             final char[] pPassword) throws OceanusException {
        final GordianCoreKeyStoreEntry myEntry = theAliases.get(pAlias);
        return myEntry instanceof GordianKeyStoreHashElement
               ? ((GordianKeyStoreHashElement) myEntry).buildEntry(this, pPassword)
               : null;
    }

    @Override
    public TethysDate getCreationDate(final String pAlias) {
        final GordianCoreKeyStoreEntry myEntry = theAliases.get(pAlias);
        return myEntry != null ? myEntry.getCreationDate() : null;
    }

    @Override
    public String getCertificateAlias(final GordianKeyPairCertificate pCertificate) {
        /* Loop through the alias entries */
        for (Entry<String, GordianCoreKeyStoreEntry> myRecord : theAliases.entrySet()) {
            /* Check for match on certificate entry */
            final GordianKeyStoreEntry myEntry = myRecord.getValue();
            if (myEntry instanceof GordianKeyStoreCertificateHolder
                    && pCertificate.equals(getCertificate(((GordianKeyStoreCertificateHolder) myEntry).getCertificateKey()))) {
                return myRecord.getKey();
            }
        }

        /* Not found */
        return null;
    }

    /**
     * Store certificate.
     * @param pCertificate the certificate
     */
    void storeCertificate(final GordianKeyPairCertificate pCertificate) {
        /* Access certificate */
        final GordianCoreKeyPairCertificate myCert = (GordianCoreKeyPairCertificate) pCertificate;

        /* Access the ids */
        final GordianCertificateId mySubjectId = myCert.getSubject();
        final GordianCertificateId myIssuerId = myCert.getIssuer();

        /* Add the certificate to the list of certificates for this subject */
        Map<GordianCertificateId, GordianCoreKeyPairCertificate> myMap = theSubjectCerts.computeIfAbsent(mySubjectId, i -> new LinkedHashMap<>());
        myMap.put(myIssuerId, myCert);

        /* Add the certificate to the list of certificates for this issuer */
        myMap = theIssuerCerts.computeIfAbsent(myIssuerId, i -> new LinkedHashMap<>());
        myMap.put(mySubjectId, myCert);
    }

    /**
     * Check validity of certificate chain.
     * @param pKeyPair the keyPair
     * @param pChain the certificate chain
     * @throws OceanusException on error
     */
    private void checkChain(final GordianKeyPair pKeyPair,
                            final GordianKeyPairCertificate[] pChain) throws OceanusException {
        /* Make sure that we have a chain */
        if (pChain == null || pChain.length == 0) {
            throw new GordianDataException("Empty chain");
        }

        /* Make sure that the keyPair has a private key */
        if (pKeyPair.isPublicOnly()) {
            throw new GordianDataException("Private Key missing");
        }

        /* Make sure that the keyPair matches end-entity certificate */
        final GordianCoreKeyPairCertificate myCert = (GordianCoreKeyPairCertificate) pChain[0];
        if (!myCert.checkMatchingPublicKey(pKeyPair)) {
            throw new GordianDataException("End-entity certificate does not match keyPair");
        }

        /* Loop through the certificates */
        for (int i = 0; i < pChain.length - 1; i++) {
            /* Access the certificate */
            final GordianCoreKeyPairCertificate myTestCert = (GordianCoreKeyPairCertificate) pChain[i];
            final GordianCoreKeyPairCertificate mySignerCert = (GordianCoreKeyPairCertificate) pChain[i + 1];

            /* Check the hierarchy */
            if (!myTestCert.validateCertificate(mySignerCert)) {
                throw new GordianDataException("Invalid certificate in path");
            }

            /* Look up any existing certificate for the signer */
            final Map<GordianCertificateId, GordianCoreKeyPairCertificate> myMap = theSubjectCerts.get(mySignerCert.getSubject());
            if (myMap != null) {
                final GordianCoreKeyPairCertificate myExisting = myMap.values().iterator().next();
                if (!myExisting.checkMatchingPublicKey(mySignerCert.getKeyPair())) {
                    throw new GordianDataException("Intermediate certificate does not match existing keyPair");
                }
            }
        }

        /* Check that we are anchored by a root certificate */
        if (!((GordianCoreKeyPairCertificate) pChain[pChain.length - 1]).validateRootCertificate()) {
            throw new GordianDataException("Invalid root certificate");
        }
    }

    @Override
    public void storeToFile(final File pFile,
                            final char[] pPassword) throws OceanusException {
        try {
            storeToStream(new FileOutputStream(pFile), pPassword);
        } catch (IOException e) {
            throw new GordianIOException("Failed to store to file", e);
        }
    }

    @Override
    public void storeToStream(final OutputStream pOutputStream,
                              final char[] pPassword) throws OceanusException {
        /* Access the Factories */
        final GordianZipFactory myZipFactory = theFactory.getZipFactory();

        /* Create the lock */
        final GordianZipLock myLock = myZipFactory.createZipLock(theKeySetSpec, pPassword);

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
        if (!(pThat instanceof GordianCoreKeyStore)) {
            return false;
        }
        final GordianCoreKeyStore myThat = (GordianCoreKeyStore) pThat;

        /* Check that the certificate maps are is identical */
        if (!theSubjectCerts.equals(myThat.getSubjectMapOfMaps())
            || !theIssuerCerts.equals(myThat.getIssuerMapOfMaps())) {
            return false;
        }

        /* Compare alias maps */
        return theAliases.equals(myThat.getAliasMap());
    }

    @Override
    public int hashCode() {
        return theSubjectCerts.hashCode()
                + theIssuerCerts.hashCode()
                + theAliases.hashCode();
    }
}
