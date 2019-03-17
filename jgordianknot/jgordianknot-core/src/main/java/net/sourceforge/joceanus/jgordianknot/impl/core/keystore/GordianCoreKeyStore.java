/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bouncycastle.asn1.x500.X500Name;

import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianAsymFactory;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianCertificate;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianCertificateId;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairUsage;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStore;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStoreCertificate;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStoreHash;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStoreKey;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePair;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStoreSet;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
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
 *     issued by different authorities must describe the same publicKey. It is expected that different publicKeys for the same subjectName WILL have different subjectIDs</li>
 *     <li>An issuerCertificate will only issue a single certificate at a time for a subjectName/subjectID combination.
 *     If two such certificates are received, the later one will overwrite the first one, (assuming the publicKey is the same)</li>
 * </ol>
 * </p>
 */
public class GordianCoreKeyStore
        implements GordianKeyStore {
    /**
     * The factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * The map of certificates by Subject.
     */
    private final Map<GordianCertificateId, Map<GordianCertificateId, GordianCoreCertificate>> theSubjectCerts;

    /**
     * The map of certificates by Issuer.
     */
    private final Map<GordianCertificateId, Map<GordianCertificateId, GordianCoreCertificate>> theIssuerCerts;

    /**
     * The aliases.
     */
    private final Map<String, GordianCoreKeyStoreEntry> theAliases;

    /**
     * Constructor.
     * @param pFactory the factory
     */
    GordianCoreKeyStore(final GordianCoreFactory pFactory) {
        /* Store parameters */
        theFactory = pFactory;

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
     * Obtain the certificate.
     * @param pKey the key of the certificate
     * @return the certificate
     */
    GordianCoreCertificate getCertificate(final GordianKeyStoreCertificateKey pKey) {
        final Map<GordianCertificateId, GordianCoreCertificate> myCertMap = theSubjectCerts.get(pKey.getSubject());
        return myCertMap == null ? null : myCertMap.get(pKey.getIssuer());
    }

    /**
     * Create a new keyPair with root certificate.
     *
     * @param pKeySpec the spec of the new keyPair
     * @param pSubject the name of the entity
     * @param pAlias the alias
     * @param pPassword the password
     * @return the new keyPair entry
     * @throws OceanusException on error
     */
    public GordianKeyStorePair createRootKeyPair(final GordianAsymKeySpec pKeySpec,
                                                 final X500Name pSubject,
                                                 final String pAlias,
                                                 final char[] pPassword) throws OceanusException {
        /* Create the new keyPair */
        final GordianAsymFactory myAsym = theFactory.getAsymmetricFactory();
        final GordianKeyPairGenerator myGenerator = myAsym.getKeyPairGenerator(pKeySpec);
        final GordianCoreKeyPair myKeyPair = (GordianCoreKeyPair) myGenerator.generateKeyPair();

        /* Create the certificate */
        final GordianCoreCertificate myCert = new GordianCoreCertificate(theFactory, myKeyPair, pSubject);
        final GordianCertificate[] myChain = new GordianCertificate[] { myCert };

        /* Record into keyStore */
        setKeyPair(pAlias, myKeyPair, pPassword, myChain);
        return getKeyPair(pAlias, pPassword);
    }


    /**
     * Create a new keyPair with certificate.
     *
     * @param pKeySpec the spec of the new keyPair
     * @param pSubject the name of the entity
     * @param pUsage   the key usage
     * @param pSigner the signer
     * @param pAlias the alias
     * @param pPassword the password
     * @return the new keyPair entry
     * @throws OceanusException on error
     */
    public GordianKeyStorePair createKeyPair(final GordianAsymKeySpec pKeySpec,
                                             final X500Name pSubject,
                                             final GordianKeyPairUsage pUsage,
                                             final GordianKeyStorePair pSigner,
                                             final String pAlias,
                                             final char[] pPassword) throws OceanusException {
        /* Create the new keyPair */
        final GordianAsymFactory myAsym = theFactory.getAsymmetricFactory();
        final GordianKeyPairGenerator myGenerator = myAsym.getKeyPairGenerator(pKeySpec);
        final GordianCoreKeyPair myKeyPair = (GordianCoreKeyPair) myGenerator.generateKeyPair();

        /* Create the certificate */
        final GordianCoreCertificate myCert = new GordianCoreCertificate(theFactory, pSigner, myKeyPair, pSubject, pUsage);

        /* Create the new chain */
        final GordianCertificate[] myParentChain = pSigner.getCertificateChain();
        final GordianCertificate[] myChain = new GordianCertificate[myParentChain.length + 1];
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
        final GordianCoreCertificate myCert = getCertificate(myCertHolder.getCertificateKey());

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
    private void removeCertificate(final GordianCoreCertificate pCertificate) {
        /* Access the ids of the certificate */
        final GordianCertificateId mySubjectId = pCertificate.getSubject();
        final GordianCertificateId myIssuerId = pCertificate.getIssuer();

        /* If it is not referenced as issuer by any other certificate */
        Map<GordianCertificateId, GordianCoreCertificate> myIssuedMap = theIssuerCerts.get(mySubjectId);
        if (myIssuedMap == null) {
            /* Remove the certificate from the issuer map */
            myIssuedMap = theIssuerCerts.get(myIssuerId);
            myIssuedMap.remove(mySubjectId);

            /* If the issuer now has no issued certificates */
            if (myIssuedMap.isEmpty()) {
                /* Purge orphan issuers */
                purgeOrphanIssuers(myIssuerId);
            }

            /* Remove the certificate from the subject map */
            final Map<GordianCertificateId, GordianCoreCertificate> myCertMap = theSubjectCerts.get(mySubjectId);
            myCertMap.remove(myIssuerId);
            if (myCertMap.isEmpty()) {
                theSubjectCerts.remove(mySubjectId);
            }
        }
    }

    /**
     * Purge orphan issuers.
     * @param pIssuerId the issuerId to purge
     */
    private void purgeOrphanIssuers(final GordianCertificateId pIssuerId) {
        /* Remove the entry */
        theIssuerCerts.remove(pIssuerId);

        /* If it is not referenced as issuer by any other certificate */
        final Map<GordianCertificateId, GordianCoreCertificate> myCertMap = theSubjectCerts.get(pIssuerId);

        /* Loop through all the certificates TODO not sure whether this will work when we are deleting */
        for (GordianCoreCertificate myCert : myCertMap.values()) {
            /* If the certificate is not referenced by any other alias */
            if (getCertificateAlias(myCert) == null) {
                /* Remove the certificate from the maps */
                removeCertificate(myCert);
            }
        }
    }

    @Override
    public void setCertificate(final String pAlias,
                               final GordianCertificate pCertificate) throws OceanusException {
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
                           final GordianCertificate[] pCertificateChain) throws OceanusException {
        /* Check the alias */
        checkAlias(pAlias);

        /* Make sure that we have a valid certificate chain */
        checkChain(pKeyPair, pCertificateChain);

        /* Remove any existing entry */
        deleteEntry(pAlias);

        /* Set the new value */
        final GordianKeyStorePairElement myPair = new GordianKeyStorePairElement(theFactory, pKeyPair, pPassword, pCertificateChain);
        theAliases.put(pAlias, myPair);

        /* Store all the certificates in the chain */
        for (GordianCertificate myCert : pCertificateChain) {
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
        final GordianKeyStoreKeyElement<T> myKey = new GordianKeyStoreKeyElement<>(theFactory, pKey, pPassword);
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
        final GordianKeyStoreSetElement mySet = new GordianKeyStoreSetElement(theFactory, pKeySet, pPassword);
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
    public GordianKeyStoreCertificate getCertificate(final String pAlias) {
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
    public String getCertificateAlias(final GordianCertificate pCertificate) {
        /* Loop through the alias entries */
        for (Map.Entry<String, GordianCoreKeyStoreEntry> myRecord : theAliases.entrySet()) {
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
    private void storeCertificate(final GordianCertificate pCertificate) {
        /* Access certificate */
        final GordianCoreCertificate myCert = (GordianCoreCertificate) pCertificate;

        /* Access the ids */
        final GordianCertificateId mySubjectId = myCert.getSubject();
        final GordianCertificateId myIssuerId = myCert.getIssuer();

        /* Add the certificate to the list of certificates for this subject */
        Map<GordianCertificateId, GordianCoreCertificate> myMap = theSubjectCerts.computeIfAbsent(mySubjectId, i -> new LinkedHashMap<>());
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
                            final GordianCertificate[] pChain) throws OceanusException {
        /* Make sure that we have a chain */
        if (pChain == null || pChain.length == 0) {
            throw new GordianDataException("Empty chain");
        }

        /* Make sure that the keyPair has a private key */
        if (pKeyPair.isPublicOnly()) {
            throw new GordianDataException("Private Key missing");
        }

        /* Make sure that the keyPair matches end-entity certificate */
        final GordianCoreCertificate myCert = (GordianCoreCertificate) pChain[0];
        if (!myCert.checkMatchingPublicKey(pKeyPair)) {
            throw new GordianDataException("End-entity certificate does not match keyPair");
        }

        /* Loop through the certificates */
        for (int i = 0; i < pChain.length - 1; i++) {
            /* Access the certificate */
            final GordianCoreCertificate myTestCert = (GordianCoreCertificate) pChain[i];
            final GordianCoreCertificate mySignerCert = (GordianCoreCertificate) pChain[i + 1];

            /* Check the hierarchy */
            if (!myTestCert.validateCertificate(mySignerCert)) {
                throw new GordianDataException("Invalid certificate in path");
            }

            /* Look up any existing certificate for the signer */
            final Map<GordianCertificateId, GordianCoreCertificate> myMap = theSubjectCerts.get(mySignerCert.getSubject());
            if (myMap != null) {
                final GordianCoreCertificate myExisting = myMap.values().iterator().next();
                if (!myExisting.checkMatchingPublicKey(mySignerCert.getKeyPair())) {
                    throw new GordianDataException("Intermediate certificate does not match existing keyPair");
                }
            }
        }

        /* Check that we are anchored by a root certificate */
        if (!((GordianCoreCertificate) pChain[pChain.length - 1]).validateRootCertificate()) {
            throw new GordianDataException("Invalid root certificate");
        }
    }
}
