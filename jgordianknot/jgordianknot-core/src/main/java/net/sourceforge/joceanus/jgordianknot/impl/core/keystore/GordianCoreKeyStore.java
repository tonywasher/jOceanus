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

import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSet;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHashSpec;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianCertificate;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianCertificateId;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairCertificate;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairSetCertificate;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStore;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStoreKey;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePair;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePairCertificate;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePairSet;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePairSetCertificate;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStoreSet;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipFactory;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipLock;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipWriteFile;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianCoreKeyStoreEntry.GordianCoreKeyStorePairCertificate;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianCoreKeyStoreEntry.GordianCoreKeyStorePairSetCertificate;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianKeyStoreElement.GordianKeyStoreCertificateHolder;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianKeyStoreElement.GordianKeyStoreCertificateKey;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianKeyStoreElement.GordianKeyStoreKeyElement;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianKeyStoreElement.GordianKeyStorePairCertificateElement;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianKeyStoreElement.GordianKeyStorePairElement;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianKeyStoreElement.GordianKeyStorePairSetCertificateElement;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianKeyStoreElement.GordianKeyStorePairSetElement;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianKeyStoreElement.GordianKeyStoreSetElement;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;

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
    private final Map<GordianCertificateId, Map<GordianCertificateId, GordianCertificate<?>>> theSubjectCerts;

    /**
     * The map of certificates by Issuer.
     */
    private final Map<GordianCertificateId, Map<GordianCertificateId, GordianCertificate<?>>> theIssuerCerts;

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
    Map<GordianCertificateId, Map<GordianCertificateId, GordianCertificate<?>>> getSubjectMapOfMaps() {
        return theSubjectCerts;
    }

    /**
     * Obtain the issuerMapofMaps.
     * @return the map
     */
    private Map<GordianCertificateId, Map<GordianCertificateId, GordianCertificate<?>>> getIssuerMapOfMaps() {
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
    GordianCertificate<?> getCertificate(final GordianKeyStoreCertificateKey pKey) {
        final Map<GordianCertificateId, GordianCertificate<?>> myCertMap = theSubjectCerts.get(pKey.getSubject());
        return myCertMap == null ? null : myCertMap.get(pKey.getIssuer());
    }

    /**
     * Obtain the certificate.
     * @param pKey the key of the certificate
     * @return the certificate
     */
    GordianKeyPairCertificate getKeyPairCertificate(final GordianKeyStoreCertificateKey pKey) {
        final GordianCertificate<?> myCert = getCertificate(pKey);
        return myCert instanceof GordianKeyPairCertificate ? (GordianKeyPairCertificate) myCert : null;
    }

    /**
     * Obtain the certificate.
     * @param pKey the key of the certificate
     * @return the certificate
     */
    GordianKeyPairSetCertificate getKeyPairSetCertificate(final GordianKeyStoreCertificateKey pKey) {
        final GordianCertificate<?> myCert = getCertificate(pKey);
        return myCert instanceof GordianKeyPairSetCertificate ? (GordianKeyPairSetCertificate) myCert : null;
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
        final GordianCertificate<?> myCert = getCertificate(myCertHolder.getCertificateKey());

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
    private void removeCertificate(final GordianCertificate<?> pCertificate) {
        /* Access the ids of the certificate */
        final GordianCertificateId mySubjectId = pCertificate.getSubject();
        final GordianCertificateId myIssuerId = pCertificate.getIssuer();

        /* If it is not referenced as issuer by any other certificate */
        Map<GordianCertificateId, GordianCertificate<?>> myIssuedMap = theIssuerCerts.get(mySubjectId);
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
            final Map<GordianCertificateId, GordianCertificate<?>> myCertMap = theSubjectCerts.get(mySubjectId);
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
    private static boolean isWithoutIssue(final Map<GordianCertificateId, GordianCertificate<?>> pIssuer) {
        /* If the map is null or empty then there are no children */
        if (pIssuer == null || pIssuer.isEmpty()) {
            return true;
        }

        /* If there is only one certificate in the map */
        if (pIssuer.size() == 1) {
            /* If the single certificate is self-signed then we are childless */
            final GordianCertificate<?> myCert = pIssuer.values().iterator().next();
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
        final Map<GordianCertificateId, GordianCertificate<?>> myCertMap = theSubjectCerts.get(pIssuerId);

        /* Loop through all the certificates */
        for (GordianCertificate<?> myCert : myCertMap.values()) {
            /* If the certificate is not referenced by any other alias */
            if (getCertificateAlias(myCert) == null) {
                /* Remove the certificate from the maps */
                removeCertificate(myCert);
            }
        }
    }

    @Override
    public void setKeyPairCertificate(final String pAlias,
                                      final GordianKeyPairCertificate pCertificate) throws OceanusException {
        /* Check the alias */
        checkAlias(pAlias);

        /* Check that we are not about to replace a non-certificate */
        final GordianKeyStoreEntry myEntry = theAliases.get(pAlias);
        if (myEntry != null
                && !(myEntry instanceof GordianCoreKeyStorePairCertificate)) {
            throw new GordianDataException("Alias already exists for non-certificate");
        }

        /* Remove any existing entry */
        deleteEntry(pAlias);

        /* Set the new value */
        final GordianKeyStorePairCertificateElement myCert = new GordianKeyStorePairCertificateElement(pCertificate);
        theAliases.put(pAlias, myCert);

        /* Store certificate */
        storeCertificate(pCertificate);
    }

    @Override
    public void setKeyPairSetCertificate(final String pAlias,
                                         final GordianKeyPairSetCertificate pCertificate) throws OceanusException {
        /* Check the alias */
        checkAlias(pAlias);

        /* Check that we are not about to replace a non-certificate */
        final GordianKeyStoreEntry myEntry = theAliases.get(pAlias);
        if (myEntry != null
                && !(myEntry instanceof GordianCoreKeyStorePairSetCertificate)) {
            throw new GordianDataException("Alias already exists for non-certificate");
        }

        /* Remove any existing entry */
        deleteEntry(pAlias);

        /* Set the new value */
        final GordianKeyStorePairSetCertificateElement myCert = new GordianKeyStorePairSetCertificateElement(pCertificate);
        theAliases.put(pAlias, myCert);

        /* Store certificate */
        storeCertificate(pCertificate);
    }

    @Override
    public void setKeyPair(final String pAlias,
                           final GordianKeyPair pKeyPair,
                           final char[] pPassword,
                           final List<GordianKeyPairCertificate> pCertificateChain) throws OceanusException {
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
    public void setKeyPairSet(final String pAlias,
                              final GordianKeyPairSet pKeyPairSet,
                              final char[] pPassword,
                              final List<GordianKeyPairSetCertificate> pCertificateChain) throws OceanusException {
        /* Check the alias */
        checkAlias(pAlias);

        /* Make sure that we have a valid certificate chain */
        checkChain(pKeyPairSet, pCertificateChain);

        /* Remove any existing entry */
        deleteEntry(pAlias);

        /* Set the new value */
        final GordianKeyStorePairSetElement myPair = new GordianKeyStorePairSetElement(theFactory, theKeySetSpec, pKeyPairSet, pPassword, pCertificateChain);
        theAliases.put(pAlias, myPair);

        /* Store all the certificates in the chain */
        for (GordianKeyPairSetCertificate myCert : pCertificateChain) {
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
        if (pClazz.isAssignableFrom(GordianKeyStorePairCertificate.class)) {
            return isKeyPairCertificateEntry(pAlias);
        }
        if (pClazz.isAssignableFrom(GordianKeyStorePairSetCertificate.class)) {
            return isKeyPairSetCertificateEntry(pAlias);
        }
        if (pClazz.isAssignableFrom(GordianKeyStorePair.class)) {
            return isKeyPairEntry(pAlias);
        }
        if (pClazz.isAssignableFrom(GordianKeyStorePairSet.class)) {
            return isKeyPairSetEntry(pAlias);
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
    public boolean isKeyPairCertificateEntry(final String pAlias) {
        return theAliases.get(pAlias) instanceof GordianKeyStorePairCertificateElement;
    }

    @Override
    public boolean isKeyPairSetCertificateEntry(final String pAlias) {
        return theAliases.get(pAlias) instanceof GordianKeyStorePairSetCertificateElement;
    }

    @Override
    public boolean isKeyPairEntry(final String pAlias) {
        return theAliases.get(pAlias) instanceof GordianKeyStorePairElement;
    }

    @Override
    public boolean isKeyPairSetEntry(final String pAlias) {
        return theAliases.get(pAlias) instanceof GordianKeyStorePairSetElement;
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
                                         final char[] pPassword) throws OceanusException {
        if (isKeyPairCertificateEntry(pAlias)) {
            return getKeyStorePairCertificate(pAlias);
        }
        if (isKeyPairSetCertificateEntry(pAlias)) {
            return getKeyStorePairSetCertificate(pAlias);
        }
        if (isKeyPairEntry(pAlias)) {
            return getKeyStorePair(pAlias, pPassword);
        }
        if (isKeyPairSetEntry(pAlias)) {
            return getKeyStorePairSet(pAlias, pPassword);
        }
        if (isKeyEntry(pAlias)) {
            return getKeyStoreKey(pAlias, pPassword);
        }
        return isKeySetEntry(pAlias)
                ? getKeyStoreSet(pAlias, pPassword)
                : null;
    }

    @Override
    public GordianKeyPairCertificate getKeyPairCertificate(final String pAlias) {
        final GordianKeyStorePairCertificate myCert = getKeyStorePairCertificate(pAlias);
        if (myCert != null) {
            return myCert.getCertificate();
        }
        final List<GordianKeyPairCertificate> myChain = getKeyPairCertificateChain(pAlias);
        return myChain == null || myChain.isEmpty() ? null : myChain.get(0);
    }

    /**
     * Obtain the keyStorePairCertificate.
     * @param pAlias the alias
     * @return the certificate entry (or null)
     */
    private GordianKeyStorePairCertificate getKeyStorePairCertificate(final String pAlias) {
        final GordianCoreKeyStoreEntry myEntry = theAliases.get(pAlias);
        return myEntry instanceof GordianKeyStorePairCertificateElement
                ? ((GordianKeyStorePairCertificateElement) myEntry).buildEntry(this)
                : null;
    }

    @Override
    public GordianKeyPairSetCertificate getKeyPairSetCertificate(final String pAlias) {
        final GordianKeyStorePairSetCertificate myCert = getKeyStorePairSetCertificate(pAlias);
        if (myCert != null) {
            return myCert.getCertificate();
        }
        final List<GordianKeyPairSetCertificate> myChain = getKeyPairSetCertificateChain(pAlias);
        return myChain == null || myChain.isEmpty() ? null : myChain.get(0);
    }

    /**
     * Obtain the keyStorePairSetCertificate.
     * @param pAlias the alias
     * @return the certificate entry (or null)
     */
    private GordianKeyStorePairSetCertificate getKeyStorePairSetCertificate(final String pAlias) {
        final GordianCoreKeyStoreEntry myEntry = theAliases.get(pAlias);
        return myEntry instanceof GordianKeyStorePairSetCertificateElement
                ? ((GordianKeyStorePairSetCertificateElement) myEntry).buildEntry(this)
                : null;
    }

    @Override
    public GordianKeyPair getKeyPair(final String pAlias,
                                     final char[] pPassword) throws OceanusException {
        final GordianKeyStorePair myPair = getKeyStorePair(pAlias, pPassword);
        return myPair == null ? null : myPair.getKeyPair();
    }

    @Override
    public List<GordianKeyPairCertificate> getKeyPairCertificateChain(final String pAlias) {
        final GordianCoreKeyStoreEntry myEntry = theAliases.get(pAlias);
        return myEntry instanceof GordianKeyStorePairElement
                ? ((GordianKeyStorePairElement) myEntry).buildChain(this)
                : null;
    }

    /**
     * Obtain the keyStorePair.
     * @param pAlias the alias
     * @param pPassword the password
     * @return the keyPair entry (or null)
     */
    private GordianKeyStorePair getKeyStorePair(final String pAlias,
                                                final char[] pPassword) throws OceanusException {
        final GordianCoreKeyStoreEntry myEntry = theAliases.get(pAlias);
        return myEntry instanceof GordianKeyStorePairElement
                ? ((GordianKeyStorePairElement) myEntry).buildEntry(this, pPassword)
                : null;
    }

    @Override
    public List<GordianKeyPairSetCertificate> getKeyPairSetCertificateChain(final String pAlias) {
        final GordianCoreKeyStoreEntry myEntry = theAliases.get(pAlias);
        return myEntry instanceof GordianKeyStorePairSetElement
                ? ((GordianKeyStorePairSetElement) myEntry).buildChain(this)
                : null;
    }

    @Override
    public GordianKeyPairSet getKeyPairSet(final String pAlias,
                                           final char[] pPassword) throws OceanusException {
        final GordianKeyStorePairSet myPairSet = getKeyStorePairSet(pAlias, pPassword);
        return myPairSet == null ? null : myPairSet.getKeyPairSet();
    }

    /**
     * Obtain the keyStorePairSet.
     * @param pAlias the alias
     * @param pPassword the password
     * @return the keyPairSet entry (or null)
     */
    private GordianKeyStorePairSet getKeyStorePairSet(final String pAlias,
                                                      final char[] pPassword) throws OceanusException {
        final GordianCoreKeyStoreEntry myEntry = theAliases.get(pAlias);
        return myEntry instanceof GordianKeyStorePairSetElement
                ? ((GordianKeyStorePairSetElement) myEntry).buildEntry(this, pPassword)
                : null;
    }

    @Override
    public <T extends GordianKeySpec> GordianKey<T> getKey(final String pAlias,
                                                           final char[] pPassword) throws OceanusException {
        final GordianKeyStoreKey<T> myKey = getKeyStoreKey(pAlias, pPassword);
        return myKey == null ? null : myKey.getKey();
    }

    /**
     * Obtain the keyStoreKey.
     * @param <T> the keyType
     * @param pAlias the alias
     * @param pPassword the password
     * @return the key entry (or null)
     */
    @SuppressWarnings("unchecked")
    private <T extends GordianKeySpec> GordianKeyStoreKey<T> getKeyStoreKey(final String pAlias,
                                                                            final char[] pPassword) throws OceanusException {
        final GordianCoreKeyStoreEntry myEntry = theAliases.get(pAlias);
        return myEntry instanceof GordianKeyStoreKeyElement
                ? ((GordianKeyStoreKeyElement<T>) myEntry).buildEntry(this, pPassword)
                : null;
    }

    @Override
    public GordianKeySet getKeySet(final String pAlias,
                                   final char[] pPassword) throws OceanusException {
        final GordianKeyStoreSet mySet = getKeyStoreSet(pAlias, pPassword);
        return mySet == null ? null : mySet.getKeySet();
    }

    /**
     * Obtain the keyStoreKeySet.
     * @param pAlias the alias
     * @param pPassword the password
     * @return the keySet entry (or null)
     */
    private GordianKeyStoreSet getKeyStoreSet(final String pAlias,
                                              final char[] pPassword) throws OceanusException {
        final GordianCoreKeyStoreEntry myEntry = theAliases.get(pAlias);
        return myEntry instanceof GordianKeyStoreSetElement
                ? ((GordianKeyStoreSetElement) myEntry).buildEntry(this, pPassword)
                : null;
    }

    @Override
    public TethysDate getCreationDate(final String pAlias) {
        final GordianCoreKeyStoreEntry myEntry = theAliases.get(pAlias);
        return myEntry != null ? myEntry.getCreationDate() : null;
    }

    @Override
    public String getCertificateAlias(final GordianCertificate<?> pCertificate) {
        /* Loop through the alias entries */
        for (Entry<String, GordianCoreKeyStoreEntry> myRecord : theAliases.entrySet()) {
            /* Check for match on certificate entry */
            final GordianKeyStoreEntry myEntry = myRecord.getValue();
            if (myEntry instanceof GordianKeyStoreCertificateHolder) {
                final GordianKeyStoreCertificateKey myKey = ((GordianKeyStoreCertificateHolder) myEntry).getCertificateKey();
                final GordianCertificate<?> myCert = getCertificate(myKey);
                if (pCertificate.equals(myCert)) {
                    return myRecord.getKey();
                }
            }
        }

        /* Not found */
        return null;
    }

    /**
     * Store certificate.
     * @param pCertificate the certificate
     */
    void storeCertificate(final GordianCertificate<?> pCertificate) {
        /* Access the ids */
        final GordianCertificateId mySubjectId = pCertificate.getSubject();
        final GordianCertificateId myIssuerId = pCertificate.getIssuer();

        /* Add the certificate to the list of certificates for this subject */
        Map<GordianCertificateId, GordianCertificate<?>> myMap = theSubjectCerts.computeIfAbsent(mySubjectId, i -> new LinkedHashMap<>());
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
                            final List<GordianKeyPairCertificate> pChain) throws OceanusException {
        /* Make sure that we have a chain */
        if (pChain == null || pChain.isEmpty()) {
            throw new GordianDataException("Empty chain");
        }

        /* Make sure that the keyPair has a private key */
        if (pKeyPair.isPublicOnly()) {
            throw new GordianDataException("Private Key missing");
        }

        /* Make sure that the keyPair matches end-entity certificate */
        final GordianCoreKeyPairCertificate myCert = (GordianCoreKeyPairCertificate) pChain.get(0);
        if (!myCert.checkMatchingPublicKey(pKeyPair)) {
            throw new GordianDataException("End-entity certificate does not match keyPair");
        }

        /* Loop through the certificates */
        final int mySize = pChain.size();
        for (int i = 0; i < mySize - 1; i++) {
            /* Access the certificate */
            final GordianCoreKeyPairCertificate myTestCert = (GordianCoreKeyPairCertificate) pChain.get(i);
            final GordianCoreKeyPairCertificate mySignerCert = (GordianCoreKeyPairCertificate) pChain.get(i + 1);

            /* Check the hierarchy */
            if (!myTestCert.validateCertificate(mySignerCert)) {
                throw new GordianDataException("Invalid certificate in path");
            }

            /* Look up any existing certificate for the signer */
            final Map<GordianCertificateId, GordianCertificate<?>> myMap = theSubjectCerts.get(mySignerCert.getSubject());
            if (myMap != null) {
                final GordianCoreKeyPairCertificate myExisting = (GordianCoreKeyPairCertificate) myMap.values().iterator().next();
                if (!myExisting.checkMatchingPublicKey(mySignerCert.getKeyPair())) {
                    throw new GordianDataException("Intermediate certificate does not match existing keyPair");
                }
            }
        }

        /* Check that we are anchored by a root certificate */
        final GordianKeyPairCertificate myRoot = pChain.get(mySize - 1);
        if (!((GordianCoreKeyPairCertificate) myRoot).validateRootCertificate()) {
            throw new GordianDataException("Invalid root certificate");
        }

        /* Check that the root is known if the depth is greater than 1 */
        if (mySize > 1 && getCertificateAlias(myRoot) == null) {
            throw new GordianDataException("Unknown root certificate");
        }
    }

    /**
     * Check validity of certificate chain.
     * @param pKeyPairSet the keyPairSet
     * @param pChain the certificate chain
     * @throws OceanusException on error
     */
    private void checkChain(final GordianKeyPairSet pKeyPairSet,
                            final List<GordianKeyPairSetCertificate> pChain) throws OceanusException {
        /* Make sure that we have a chain */
        if (pChain == null || pChain.isEmpty()) {
            throw new GordianDataException("Empty chain");
        }

        /* Make sure that the keyPair has a private key */
        if (pKeyPairSet.isPublicOnly()) {
            throw new GordianDataException("Private Key missing");
        }

        /* Make sure that the keyPair matches end-entity certificate */
        final GordianCoreKeyPairSetCertificate myCert = (GordianCoreKeyPairSetCertificate) pChain.get(0);
        if (!myCert.checkMatchingPublicKey(pKeyPairSet)) {
            throw new GordianDataException("End-entity certificate does not match keyPair");
        }

        /* Loop through the certificates */
        final int mySize = pChain.size();
        for (int i = 0; i < mySize - 1; i++) {
            /* Access the certificate */
            final GordianCoreKeyPairSetCertificate myTestCert = (GordianCoreKeyPairSetCertificate) pChain.get(i);
            final GordianCoreKeyPairSetCertificate mySignerCert = (GordianCoreKeyPairSetCertificate) pChain.get(i + 1);

            /* Check the hierarchy */
            if (!myTestCert.validateCertificate(mySignerCert)) {
                throw new GordianDataException("Invalid certificate in path");
            }

            /* Look up any existing certificate for the signer */
            final Map<GordianCertificateId, GordianCertificate<?>> myMap = theSubjectCerts.get(mySignerCert.getSubject());
            if (myMap != null) {
                final GordianCoreKeyPairSetCertificate myExisting = (GordianCoreKeyPairSetCertificate) myMap.values().iterator().next();
                if (!myExisting.checkMatchingPublicKey(mySignerCert.getKeyPair())) {
                    throw new GordianDataException("Intermediate certificate does not match existing keyPair");
                }
            }
        }

        /* Check that we are anchored by a root certificate */
        final GordianKeyPairSetCertificate myRoot = pChain.get(mySize - 1);
        if (!((GordianCoreKeyPairSetCertificate) myRoot).validateRootCertificate()) {
            throw new GordianDataException("Invalid root certificate");
        }

        /* Check that the root is known if the depth is greater than 1 */
        if (mySize > 1 && getCertificateAlias(myRoot) == null) {
            throw new GordianDataException("Unknown root certificate");
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
