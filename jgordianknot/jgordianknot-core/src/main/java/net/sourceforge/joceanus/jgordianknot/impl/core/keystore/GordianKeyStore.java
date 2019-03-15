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

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.x500.X500Name;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;

/**
 * KeyStore implementation.
 */
public class GordianKeyStore {
    /**
     * The map of certificates by Subject.
     */
    private final Map<GordianKeyStoreId, Map<GordianKeyStoreId, GordianCertificate>> theSubjectCerts;

    /**
     * The map of certificates by Certificate.
     */
    private final Map<GordianKeyStoreId, Map<GordianKeyStoreId, GordianCertificate>> theIssuerCerts;

    /**
     * The aliases.
     */
    private final Map<String, GordianKeyStoreEntry> theAliases;

    /**
     * Constructor.
     */
    GordianKeyStore() {
        /* Create the maps */
        theSubjectCerts = new LinkedHashMap<>();
        theIssuerCerts = new LinkedHashMap<>();
        theAliases = new LinkedHashMap<>();
    }

    /**
     * Obtain a list of all aliases.
     * @return the list
     */
    public List<String> getAliases() {
        return new ArrayList<>(theAliases.keySet());
    }

    /**
     * Does the store contain this alias?
     * @param pAlias the alias
     * @return true/false
     */
    public boolean containsAlias(final String pAlias) {
        return theAliases.containsKey(pAlias);
    }

    /**
     * Obtain the number of entries in this keyStore.
     * @return the # of entries
     */
    public int size() {
        return theAliases.size();
    }

    /**
     * Delete the entry relating to this alias (if it exists).
     * @param pAlias the alias to remove
     */
    public void deleteEntry(final String pAlias) {
        /* Remove the existing entry */
        final GordianKeyStoreEntry myEntry = theAliases.remove(pAlias);

        /* Nothing more to do unless we are removing a certificate */
        if (!(myEntry instanceof GordianKeyStoreCertificate)) {
            return;
        }

        /* Access the certificate */
        final GordianKeyStoreCertificate myCertEntry = (GordianKeyStoreCertificate) myEntry;
        final GordianCertificate myCert = myCertEntry.getCertificate();

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
    private void removeCertificate(final GordianCertificate pCertificate) {
        /* Access the ids of the certificate */
        final GordianKeyStoreId mySubjectId = new GordianKeyStoreId(pCertificate);
        final GordianKeyStoreId myIssuerId = GordianKeyStoreId.getIssuerId(pCertificate);

        /* If it is not referenced as issuer by any other certificate */
        Map<GordianKeyStoreId, GordianCertificate> myIssued = theIssuerCerts.get(mySubjectId);
        if (myIssued == null) {
            /* Remove the certificate from the issuer map */
            myIssued = theIssuerCerts.get(myIssuerId);
            myIssued.remove(mySubjectId);

            /* If the issuer now has no issued certificates */
            if (myIssued.isEmpty()) {
                /* Purge orphan issuers */
                purgeOrphanIssuers(myIssuerId);
            }

            /* Remove the certificate from the subject map */
            final Map<GordianKeyStoreId, GordianCertificate> myCerts = theSubjectCerts.get(mySubjectId);
            myCerts.remove(myIssuerId);
            if (myCerts.isEmpty()) {
                theSubjectCerts.remove(mySubjectId);
            }
        }
    }

    /**
     * Purge orphan issuers.
     * @param pIssuerId the issuerId to purge
     */
    private void purgeOrphanIssuers(final GordianKeyStoreId pIssuerId) {
        /* Remove the entry */
        theIssuerCerts.remove(pIssuerId);

        /* If it is not referenced as issuer by any other certificate */
        final Map<GordianKeyStoreId, GordianCertificate> myCerts = theSubjectCerts.get(pIssuerId);

        /* Loop through all the certificates TODO not sure whether this will work when we are deleting */
        for (GordianCertificate myCert : myCerts.values()) {
            /* If the certificate is not referenced by any other alias */
            if (getCertificateAlias(myCert) == null) {
                /* Remove the certificate from the maps */
                removeCertificate(myCert);
            }
        }
    }

    /**
     * Set certificate entry.
     * @param pAlias the alias
     * @param pCertificate the certificate
     * @throws OceanusException on error
     */
    public void setCertificate(final String pAlias,
                               final GordianCertificate pCertificate) throws OceanusException {
        /* Check the alias */
        checkAlias(pAlias);

        /* Check that we are not about to replace a non-certificate */
        final GordianKeyStoreEntry myEntry = theAliases.get(pAlias);
        if (myEntry != null && !(myEntry instanceof GordianKeyStoreCertificate)) {
            throw new GordianDataException("Alias already exists for non-certificate");
        }

        /* Remove any existing entry */
        deleteEntry(pAlias);

        /* Set the new value */
        final GordianKeyStoreCertificate myCert = new GordianKeyStoreCertificate(pCertificate);
        theAliases.put(pAlias, myCert);

        /* Store certificate */
        storeCertificate(pCertificate);
    }

    /**
     * Set keyPair entry.
     * @param pAlias the alias
     * @param pKeyPair the keyPair
     * @param pCertificateChain the certificateChain
     * @throws OceanusException on error
     */
    public void setKeyPair(final String pAlias,
                           final GordianKeyPair pKeyPair,
                           final GordianCertificate[] pCertificateChain) throws OceanusException {
        /* Check the alias */
        checkAlias(pAlias);

        /* Make sure that we have a valid certificate chain */
        checkChain(pKeyPair, pCertificateChain);

        /* Remove any existing entry */
        deleteEntry(pAlias);

        /* Set the new value */
        final GordianKeyStorePair myPair = new GordianKeyStorePair(pKeyPair, pCertificateChain);
        theAliases.put(pAlias, myPair);

        /* Store all the certificates in the chain */
        for (GordianCertificate myCert : pCertificateChain) {
            storeCertificate(myCert);
        }
    }

    /**
     * Set key entry.
     * @param <T> the key type
     * @param pAlias the alias
     * @param pKey the key
     */
    public <T extends GordianKeySpec> void setKey(final String pAlias,
                                                  final GordianKey<T> pKey) {
        /* Check the alias */
        checkAlias(pAlias);

        /* Remove any existing entry */
        deleteEntry(pAlias);

        /* Set the new value */
        final GordianKeyStoreKey myKey = new GordianKeyStoreKey<>(pKey);
        theAliases.put(pAlias, myKey);
    }

    /**
     * Set keySet entry.
     * @param pAlias the alias
     * @param pKeySet the keySet
     */
    public void setKeySet(final String pAlias,
                          final GordianKeySet pKeySet) {
        /* Check the alias */
        checkAlias(pAlias);

        /* Remove any existing entry */
        deleteEntry(pAlias);

        /* Set the new value */
        final GordianKeyStoreSet mySet = new GordianKeyStoreSet(pKeySet);
        theAliases.put(pAlias, mySet);
    }

    /**
     * Set keySetHash entry.
     * @param pAlias the alias
     * @param pHash the keySetHash
     */
    public void setKeySetHash(final String pAlias,
                              final GordianKeySetHash pHash) {
        /* Check the alias */
        checkAlias(pAlias);

        /* Set the new value */
        final GordianKeyStoreHash myHash = new GordianKeyStoreHash(pHash);
        theAliases.put(pAlias, myHash);
    }

    /**
     * Check that the alias is valid.
     * @param pAlias the alias
     */
    private void checkAlias(final String pAlias) {
        if (pAlias == null) {
            throw new NullPointerException();
        }
    }

    /**
     * Determine whether the alias is a specified entry type.
     * @param <T> the entry class
     * @param pAlias the alias
     * @param pClazz the entry class
     * @return true/false
     */
    public <T extends GordianKeyStoreEntry> boolean entryInstanceOf(final String pAlias,
                                                                    final Class<T> pClazz) {
        return pClazz.isInstance(theAliases.get(pAlias));
    }

    /**
     * Determine whether the alias is a certificate entry.
     * @param pAlias the alias
     * @return true/false
     */
    public boolean isCertificateEntry(final String pAlias) {
        return getCertificate(pAlias) != null;
    }

    /**
     * Determine whether the alias is a keyPair entry.
     * @param pAlias the alias
     * @return true/false
     */
    public boolean isKeyPairEntry(final String pAlias) {
        return getKeyPair(pAlias) != null;
    }

    /**
     * Determine whether the alias is a key entry.
     * @param pAlias the alias
     * @return true/false
     */
    public boolean isKeyEntry(final String pAlias) {
        return getKey(pAlias) != null;
    }

    /**
     * Determine whether the alias is a keySet entry.
     * @param pAlias the alias
     * @return true/false
     */
    public boolean isKeySetEntry(final String pAlias) {
        return getKeySet(pAlias) != null;
    }

    /**
     * Determine whether the alias is a keyHash entry.
     * @param pAlias the alias
     * @return true/false
     */
    public boolean isKeySetHashEntry(final String pAlias) {
        return getKeySetHash(pAlias) != null;
    }

    /**
     * Obtain the Entry for the alias.
     * @param pAlias the alias
     * @return the entry
     */
    public GordianKeyStoreEntry getEntry(final String pAlias) {
        return theAliases.get(pAlias);
    }

    /**
     * Obtain the certificate for the alias.
     * @param pAlias the alias
     * @return the keyPair
     */
    public GordianKeyStoreCertificate getCertificate(final String pAlias) {
        final GordianKeyStoreEntry myEntry = getEntry(pAlias);
        return myEntry instanceof GordianKeyStoreCertificate
                ? (GordianKeyStoreCertificate) myEntry
                : null;
    }

    /**
     * Obtain the keyPair for the alias.
     * @param pAlias the alias
     * @return the keyPair
     */
    public GordianKeyStorePair getKeyPair(final String pAlias) {
        final GordianKeyStoreEntry myEntry = getEntry(pAlias);
        return myEntry instanceof GordianKeyStorePair
               ? (GordianKeyStorePair) myEntry
               : null;
    }

    /**
     * Obtain the key for the alias.
     * @param <T> the keyType
     * @param pAlias the alias
     * @return the key
     */
    @SuppressWarnings("unchecked")
    public <T extends GordianKeySpec> GordianKeyStoreKey<T> getKey(final String pAlias) {
        final GordianKeyStoreEntry myEntry = getEntry(pAlias);
        return myEntry instanceof GordianKeyStoreKey
               ? (GordianKeyStoreKey<T>) myEntry
               : null;
    }

    /**
     * Obtain the keySetHash for the alias.
     * @param pAlias the alias
     * @return the keySetHash
     */
    public GordianKeyStoreHash getKeySetHash(final String pAlias) {
        final GordianKeyStoreEntry myEntry = getEntry(pAlias);
        return myEntry instanceof GordianKeyStoreHash
               ? (GordianKeyStoreHash) myEntry
               : null;
    }

    /**
     * Obtain the keySet for the alias.
     * @param pAlias the alias
     * @return the keySet
     */
    public GordianKeyStoreSet getKeySet(final String pAlias) {
        final GordianKeyStoreEntry myEntry = getEntry(pAlias);
        return myEntry instanceof GordianKeyStoreSet
               ? (GordianKeyStoreSet) myEntry
               : null;
    }

    /**
     * Obtain the creationDate of the alias.
     * @param pAlias the alias
     * @return the creation date
     */
    public TethysDate getCreationDate(final String pAlias) {
        final GordianKeyStoreEntry myEntry = theAliases.get(pAlias);
        return myEntry != null ? myEntry.getCreationDate() : null;
    }

    /**
     * Obtain the alias for this certificate.
     * @param pCertificate the certificate
     * @return the Alias if it exists
     */
    public String getCertificateAlias(final GordianCertificate pCertificate) {
        /* Loop through the alias entries */
        for (Map.Entry<String, GordianKeyStoreEntry> myRecord : theAliases.entrySet()) {
            /* Check for match on certificate entry */
            final GordianKeyStoreEntry myEntry = myRecord.getValue();
            if (myEntry instanceof GordianKeyStoreCertificate
                    && pCertificate.equals(((GordianKeyStoreCertificate) myEntry).getCertificate())) {
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
        /* Access the ids */
        final GordianKeyStoreId mySubjectId = new GordianKeyStoreId(pCertificate);
        final GordianKeyStoreId myIssuerId = GordianKeyStoreId.getIssuerId(pCertificate);

        /* Add the certificate to the list of certificates for this subject */
        Map<GordianKeyStoreId, GordianCertificate> myMap = theSubjectCerts.computeIfAbsent(mySubjectId, i -> new LinkedHashMap<>());
        myMap.put(myIssuerId, pCertificate);

        /* Add the certificate to the list of certificates for this issuer */
        myMap = theIssuerCerts.computeIfAbsent(myIssuerId, i -> new LinkedHashMap<>());
        myMap.put(mySubjectId, pCertificate);
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

        /* Make sure that the keyPair ihas a private key */
        if (pKeyPair.isPublicOnly()) {
            throw new GordianDataException("Private Key missing");
        }

        /* Make sure that the keyPair matches */
        if (!pKeyPair.equals(pChain[0].getKeyPair())) {
            throw new GordianDataException("End-entity certificate does not match keyPair");
        }

        /* Loop through the certificates */
        for (int i = 0; i < pChain.length - 1; i++) {
            /* Check the hierarchy */
            if (!pChain[i].validateCertificate(pChain[i])) {
                throw new GordianDataException("Invalid certificate in path");
            }
        }

        /* Check that we are anchored by a root certificate */
        if (!pChain[pChain.length - 1].validateRootCertificate()) {
            throw new GordianDataException("Invalid root certificate");
        }
    }

    /**
     * KeyStore KeyId.
     */
    public static class GordianKeyStoreId {
        /**
         * The Name.
         */
        private final X500Name theName;

        /**
         * The ID.
         */
        private final DERBitString theId;

        /**
         * Constructor.
         * @param pName the name
         * @param pId the id
         */
        GordianKeyStoreId(final X500Name pName,
                          final DERBitString pId) {
            theName = pName;
            theId = pId;
        }

        /**
         * Constructor.
         * @param pCertificate the certificate
         */
        public GordianKeyStoreId(final GordianCertificate pCertificate) {
            this(pCertificate.getSubject(), pCertificate.getSubjectId());
        }

        /**
         * Obtain the issuer Id for a certificate.
         * @param pCertificate the certificate
         * @return get the issuer id
         */
        public static GordianKeyStoreId getIssuerId(final GordianCertificate pCertificate) {
            return new GordianKeyStoreId(pCertificate.getIssuer(), pCertificate.getIssuerId());
        }

        /**
         * Obtain the name.
         * @return the name
         */
        public X500Name getName() {
            return theName;
        }

        /**
         * Obtain the id.
         * @return the id
         */
        public DERBitString getId() {
            return theId;
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
            if (!(pThat instanceof GordianKeyStoreId)) {
                return false;
            }
            final GordianKeyStoreId myThat = (GordianKeyStoreId) pThat;

            /* Compare fields */
            return theName.equals(myThat.getName())
                    && Objects.equals(theId, myThat.getId());
        }

        @Override
        public int hashCode() {
            return theName.hashCode()
                    + (theId == null
                                    ? 0
                                    : theId.hashCode());
        }
    }

    /**
     * KeyStore Entry.
     */
    public abstract static class GordianKeyStoreEntry
            implements KeyStore.Entry {
        /**
         * The Creation Date.
         */
        private final TethysDate theDate;

        /**
         * Constructor.
         */
        GordianKeyStoreEntry() {

            this(new TethysDate());
        }

        /**
         * Constructor.
         * @param pDate the creation date
         */
        GordianKeyStoreEntry(final TethysDate pDate) {
            theDate = pDate;
        }

        /**
         * Obtain the creation date.
         * @return the creation date
         */
        public TethysDate getCreationDate() {
            return theDate;
        }
    }

    /**
     * KeyStore Certificate.
     */
    public static class GordianKeyStoreCertificate
            extends GordianKeyStoreEntry {
        /**
         * The Certificate.
         */
        private final GordianCertificate theCertificate;

        /**
         * Constructor.
         * @param pCertificate the certificate
         */
        GordianKeyStoreCertificate(final GordianCertificate pCertificate) {
            theCertificate = pCertificate;
        }

        /**
         * Obtain the certificate.
         * @return the certificate
         */
        public GordianCertificate getCertificate() {
            return theCertificate;
        }
    }

    /**
     * KeyStore KeyPair.
     */
    public static class GordianKeyStorePair
        extends GordianKeyStoreCertificate {
        /**
         * The KeyPair.
         */
        private final GordianKeyPair theKeyPair;

        /**
         * Constructor.
         * @param pKeyPair the keyPair.
         * @param pCertificateChain the matching certificateChain
         */
        GordianKeyStorePair(final GordianKeyPair pKeyPair,
                            final GordianCertificate[] pCertificateChain) {
            super(pCertificateChain[0]);
            theKeyPair = pKeyPair;
        }

        /**
         * Obtain the keyPair.
         * @return the keyPair
         */
        public GordianKeyPair getKeyPair() {
            return theKeyPair;
        }
    }

    /**
     * KeyStore KeyEntry.
     * @param <T> the key type
     */
    public static class GordianKeyStoreKey<T extends GordianKeySpec>
            extends GordianKeyStoreEntry {
        /**
         * The Key.
         */
        private final GordianKey<T> theKey;

        /**
         * Constructor.
         * @param pKey the key
         */
        GordianKeyStoreKey(final GordianKey<T> pKey) {
            theKey = pKey;
        }

        /**
         * Obtain the key.
         * @return the key
         */
        public GordianKey<T> getKey() {
            return theKey;
        }
    }


    /**
     * KeyStore KeySet.
     */
    public static class GordianKeyStoreSet
            extends GordianKeyStoreEntry {
        /**
         * The KeySet.
         */
        private final GordianKeySet theKeySet;

        /**
         * Constructor.
         * @param pKeySet the keySet
         */
        GordianKeyStoreSet(final GordianKeySet pKeySet) {
            theKeySet = pKeySet;
        }

        /**
         * Obtain the keySetHash.
         * @return the keySetHash
         */
        public GordianKeySet getKeySet() {
            return theKeySet;
        }
    }

    /**
     * KeyStore KeySetHash.
     */
    public static class GordianKeyStoreHash
            extends GordianKeyStoreEntry {
        /**
         * The KeySetHash.
         */
        private final GordianKeySetHash theHash;

        /**
         * Constructor.
         * @param pHash the keySetHash
         */
        GordianKeyStoreHash(final GordianKeySetHash pHash) {
            theHash = pHash;
        }

        /**
         * Obtain the keySetHash.
         * @return the keySetHash
         */
        public GordianKeySetHash getKeySetHash() {
            return theHash;
        }
    }
}
