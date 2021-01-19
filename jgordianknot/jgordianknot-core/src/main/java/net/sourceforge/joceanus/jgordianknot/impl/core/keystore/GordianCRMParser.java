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
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.cms.EnvelopedData;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.KeyTransRecipientInfo;
import org.bouncycastle.asn1.cms.RecipientInfo;
import org.bouncycastle.asn1.crmf.EncKeyWithID;
import org.bouncycastle.asn1.crmf.POPOPrivKey;
import org.bouncycastle.asn1.crmf.ProofOfPossession;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.GeneralName;

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKeyPairAnonymousAgreement;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKeyPairSetAnonymousAgreement;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptorSpec;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianKeyPairEncryptor;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianKeyPairSetEncryptor;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianKeyPairFactory;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSet;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetSpec;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairCertificate;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairSetCertificate;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairUsage;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairUse;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePair;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePairSet;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianCoreAgreementFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jgordianknot.impl.core.encrypt.GordianCoreEncryptorFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * KeyPair Certificate Request Message Parser.
 */
public abstract class GordianCRMParser {
    /**
     * The factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * The keyStore.
     */
    private final GordianCoreKeyStore theKeyStore;

    /**
     * The keyStoreManager.
     */
    private final GordianCoreKeyStoreManager theKeyStoreMgr;

    /**
     * The password resolver.
     */
    private final Function<String, char[]> theResolver;

    /**
     * Constructor.
     * @param pKeyStoreMgr the keyStoreManager
     * @param pResolver the password resolver
     */
    public GordianCRMParser(final GordianCoreKeyStoreManager pKeyStoreMgr,
                            final Function<String, char[]> pResolver) {
        /* Store parameters */
        theKeyStoreMgr = pKeyStoreMgr;
        theKeyStore = pKeyStoreMgr.getKeyStore();
        theResolver = pResolver;
        theFactory = theKeyStore.getFactory();
    }

    /**
     * Obtain the factory.
     * @return the factory
     */
    GordianCoreFactory getFactory() {
        return theFactory;
    }

    /**
     * Obtain the keyStoreManager.
     * @return the keyStoreManager
     */
    GordianCoreKeyStoreManager getKeyStoreMgr() {
        return theKeyStoreMgr;
    }

    /**
     * Decode a certificate request.
     * @param pObject the PEM object
     * @return the PEM certificate chain
     * @throws OceanusException on error
     */
    public abstract List<GordianPEMObject> decodeCertificateRequest(GordianPEMObject pObject) throws OceanusException;

    /**
     * Derive an encrypted keySet.
     * @param pKeyPair the keyPair
     * @param pAlgId the algorithm Identifier
     * @param pEncryptedKey the encrypted key
     * @return the derived keySet
     * @throws OceanusException on error
     */
    GordianKeySet deriveEncryptedKeySet(final GordianKeyPair pKeyPair,
                                        final AlgorithmIdentifier pAlgId,
                                        final byte[] pEncryptedKey) throws OceanusException {
        /* Handle decryption */
        final GordianKeyPairFactory myKPFactory = theFactory.getKeyPairFactory();
        final GordianCoreEncryptorFactory myEncFactory = (GordianCoreEncryptorFactory) myKPFactory.getEncryptorFactory();
        final GordianEncryptorSpec myEncSpec = myEncFactory.getSpecForIdentifier(pAlgId);
        final GordianKeyPairEncryptor myEncryptor = myEncFactory.createKeyPairEncryptor(myEncSpec);
        myEncryptor.initForDecrypt(pKeyPair);
        final byte[] myKey = myEncryptor.decrypt(pEncryptedKey);
        return GordianCRMBuilder.deriveKeySetFromKey(theFactory, myKey);
    }


    /**
     * Derive the privateKey.
     * @param pProof the proof of possession
     * @param pSubject the subject name
     * @return the PKCS8Encoded privateKey
     * @throws OceanusException on error
     */
    PKCS8EncodedKeySpec derivePrivateKey(final ProofOfPossession pProof,
                                         final X500Name pSubject) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Extract details */
            final POPOPrivKey myEncrypt = (POPOPrivKey) pProof.getObject();
            final EnvelopedData myData = (EnvelopedData) myEncrypt.getValue();
            final RecipientInfo myRecipient = RecipientInfo.getInstance(myData.getRecipientInfos().getObjectAt(0));
            final EncryptedContentInfo myContent = myData.getEncryptedContentInfo();
            final byte[] myEncryptedPrivKey = myContent.getEncryptedContent().getOctets();
            final KeyTransRecipientInfo myRecInfo = (KeyTransRecipientInfo) myRecipient.getInfo();

            /* Derive the keySet */
            final GordianKeySet myKeySet = deriveKeySetFromRecInfo(myRecInfo);

            /* Decrypt the privateKey/ID */
            final EncKeyWithID myKeyWithId = EncKeyWithID.getInstance(myKeySet.decryptBytes(myEncryptedPrivKey));

            /* Check that the ID matches */
            final X500Name myName = X500Name.getInstance(GeneralName.getInstance(myKeyWithId.getIdentifier()).getName());
            if (!myName.equals(pSubject)) {
                throw new GordianDataException("Mismatch on subjectID");
            }

            /* myName and Subject should be identical */
            return new PKCS8EncodedKeySpec(myKeyWithId.getPrivateKey().getEncoded());

        } catch (IOException e) {
            throw new GordianIOException("Failed to derive encrypted privateKey", e);
        }
    }

    /**
     * Derive the keySet via a keyPairSet issuer.
     * @param pRecInfo the recipient info
     * @return the keySet
     * @throws OceanusException on error
     */
    private GordianKeySet deriveKeySetFromRecInfo(final KeyTransRecipientInfo pRecInfo) throws OceanusException {
        /* Extract details */
        final AlgorithmIdentifier myAlgId = pRecInfo.getKeyEncryptionAlgorithm();
        final byte[] myEncryptedKey = pRecInfo.getEncryptedKey().getOctets();

        /* Access issuer details */
        final IssuerAndSerialNumber myIssId = (IssuerAndSerialNumber) pRecInfo.getRecipientIdentifier().getId();

        /* Locate issuer */
        final String myAlias = theKeyStore.findIssuerCert(myIssId);
        final char[] myPassword = theResolver.apply(myAlias);
        if (myPassword == null) {
            throw new GordianDataException("No password available for issuer");
        }
        final GordianKeyStoreEntry myIssuerEntry = theKeyStore.getEntry(myAlias, myPassword);
        Arrays.fill(myPassword, (char) 0);

        /* If the issuer is a keyPair */
        if (myIssuerEntry instanceof GordianKeyStorePair) {
            /* Access details */
            final GordianKeyStorePair myIssuer = (GordianKeyStorePair) myIssuerEntry;
            final GordianKeyPairCertificate myCert = myIssuer.getCertificateChain().get(0);
            final GordianKeyPair myKeyPair = myIssuer.getKeyPair();

            /* Derive the keySet appropriately */
            final GordianKeyPairUsage myUsage = myCert.getUsage();
            return myUsage.hasUse(GordianKeyPairUse.KEYENCRYPT)
                    ? deriveEncryptedKeySet(myKeyPair, myAlgId, myEncryptedKey)
                    : deriveAgreedKeySet(myKeyPair, myEncryptedKey);

            /* else must be a keyPairSet */
        } else {
            /* Access details */
            final GordianKeyStorePairSet myIssuer = (GordianKeyStorePairSet) myIssuerEntry;
            final GordianKeyPairSetCertificate myCert = myIssuer.getCertificateChain().get(0);
            final GordianKeyPairSet myKeyPairSet = myIssuer.getKeyPairSet();

            /* Derive the keySet appropriately */
            final GordianKeyPairUsage myUsage = myCert.getUsage();
            return myUsage.hasUse(GordianKeyPairUse.KEYENCRYPT)
                    ? deriveEncryptedKeySet(myKeyPairSet, myEncryptedKey)
                    : deriveAgreedKeySet(myKeyPairSet, myEncryptedKey);
        }
    }

    /**
     * Derive an encrypted keySet.
     * @param pKeyPairSet the keyPairSet
     * @param pEncryptedKey the encrypted key
     * @return the derived keySet
     * @throws OceanusException on error
     */
    GordianKeySet deriveEncryptedKeySet(final GordianKeyPairSet pKeyPairSet,
                                        final byte[] pEncryptedKey) throws OceanusException {
        /* Handle decryption */
        final GordianKeyPairFactory myKPFactory = theFactory.getKeyPairFactory();
        final GordianCoreEncryptorFactory myEncFactory = (GordianCoreEncryptorFactory) myKPFactory.getEncryptorFactory();
        final GordianKeyPairSetSpec myKeySpec = pKeyPairSet.getKeyPairSetSpec();
        final GordianKeyPairSetEncryptor myEncryptor = myEncFactory.createKeyPairSetEncryptor(myKeySpec);
        myEncryptor.initForDecrypt(pKeyPairSet);
        final byte[] myKey = myEncryptor.decrypt(pEncryptedKey);
        return GordianCRMBuilder.deriveKeySetFromKey(theFactory, myKey);
    }

    /**
     * Derive an agreed keySet.
     * @param pKeyPair the keyPair
     * @param pHello the clientHello
     * @return the derived keySet
     * @throws OceanusException on error
     */
    GordianKeySet deriveAgreedKeySet(final GordianKeyPair pKeyPair,
                                     final byte[] pHello) throws OceanusException {
        /* Handle agreement */
        final GordianKeyPairFactory myKPFactory = theFactory.getKeyPairFactory();
        final GordianCoreAgreementFactory myAgreeFactory = (GordianCoreAgreementFactory) myKPFactory.getAgreementFactory();
        final GordianKeyPairAnonymousAgreement myAgree = (GordianKeyPairAnonymousAgreement) myAgreeFactory.createKeyPairAgreement(pHello);
        myAgree.acceptClientHello(pKeyPair, pHello);
        return (GordianKeySet) myAgree.getResult();
    }

    /**
     * Derive an agreed keySet.
     * @param pKeyPairSet the keyPairSet
     * @param pHello the clientHello
     * @return the derived keySet
     * @throws OceanusException on error
     */
    GordianKeySet deriveAgreedKeySet(final GordianKeyPairSet pKeyPairSet,
                                     final byte[] pHello) throws OceanusException {
        /* Handle agreement */
        final GordianKeyPairFactory myKPFactory = theFactory.getKeyPairFactory();
        final GordianCoreAgreementFactory myAgreeFactory = (GordianCoreAgreementFactory) myKPFactory.getAgreementFactory();
        final GordianKeyPairSetAnonymousAgreement myAgree = (GordianKeyPairSetAnonymousAgreement) myAgreeFactory.createKeyPairSetAgreement(pHello);
        myAgree.acceptClientHello(pKeyPairSet, pHello);
        return (GordianKeySet) myAgree.getResult();
    }
}
