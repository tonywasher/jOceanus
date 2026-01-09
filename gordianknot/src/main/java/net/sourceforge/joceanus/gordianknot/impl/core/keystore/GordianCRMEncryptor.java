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

import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.gordianknot.api.agree.GordianAnonymousAgreement;
import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.cert.GordianCertificate;
import net.sourceforge.joceanus.gordianknot.api.cert.GordianKeyPairUsage;
import net.sourceforge.joceanus.gordianknot.api.cert.GordianKeyPairUse;
import net.sourceforge.joceanus.gordianknot.api.encrypt.GordianEncryptor;
import net.sourceforge.joceanus.gordianknot.api.encrypt.GordianEncryptorSpec;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianAsyncFactory;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.gordianknot.impl.core.agree.GordianCoreAgreementFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianParameters;
import net.sourceforge.joceanus.gordianknot.impl.core.cert.GordianCoreCertificate;
import net.sourceforge.joceanus.gordianknot.impl.core.encrypt.GordianCoreEncryptorFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianIOException;
import net.sourceforge.joceanus.gordianknot.impl.core.keyset.GordianKeySetSpecASN1;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.KeyTransRecipientInfo;
import org.bouncycastle.asn1.cms.RecipientIdentifier;
import org.bouncycastle.asn1.cms.RecipientInfo;
import org.bouncycastle.asn1.crmf.CRMFObjectIdentifiers;
import org.bouncycastle.asn1.crmf.EncKeyWithID;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.GeneralName;

import java.io.IOException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;

/**
 * Encryptor.
 */
public class GordianCRMEncryptor {
    /**
     * The factory.
     */
    private final GordianBaseFactory theFactory;

    /**
     * Constructor.
     * @param pFactory the factory
     */
    GordianCRMEncryptor(final GordianBaseFactory pFactory) {
        theFactory = pFactory;
    }

    /**
     * convert a certificate.
     * @param pCertificate the certificate
     * @return the converted certificate
     * @throws GordianException on error
     */
    GordianCoreCertificate convertCertificate(final Certificate pCertificate) throws GordianException {
        return new GordianCoreCertificate(theFactory, pCertificate);
    }

    /**
     * Prepare for encryption.
     * @param pCertificate the target certificate
     * @return the CRM result
     * @throws GordianException on error
     */
    GordianCRMResult prepareForEncryption(final GordianCoreCertificate pCertificate) throws GordianException {
        /* Try to send an encrypted proof */
        final GordianKeyPair myKeyPair = pCertificate.getKeyPair();
        final GordianKeyPairSpec mySpec = myKeyPair.getKeyPairSpec();
        final GordianEncryptorSpec myEncSpec = theFactory.getAsyncFactory().getEncryptorFactory().defaultForKeyPair(mySpec);
        if (myEncSpec != null) {
            return prepareForEncryption(myEncSpec, pCertificate);
        }

        /* Try to send an agreed proof */
        final GordianAgreementSpec myAgreeSpec = theFactory.getAsyncFactory().getAgreementFactory().defaultForKeyPair(mySpec);
        if (myAgreeSpec != null) {
            return prepareAgreedEncryption(myAgreeSpec, pCertificate);
        }

        /* Reject the request */
        throw new GordianDataException("Unable to prepare for encryption");
    }

    /**
     * Prepare an agreed encryption.
     * @param pAgreeSpec the agreementSpec
     * @param pCertificate the target certificate
     * @return the CRM result
     * @throws GordianException on error
     */
    private GordianCRMResult prepareAgreedEncryption(final GordianAgreementSpec pAgreeSpec,
                                                     final GordianCoreCertificate pCertificate) throws GordianException {
        /* Create the agreement */
        final GordianAsyncFactory myFactory = theFactory.getAsyncFactory();
        final GordianCoreAgreementFactory myAgreeFactory = (GordianCoreAgreementFactory) myFactory.getAgreementFactory();
        final GordianAnonymousAgreement myAgree = (GordianAnonymousAgreement) myAgreeFactory.createAgreement(pAgreeSpec);
        myAgree.setResultType(new GordianKeySetSpec());
        final byte[] myHello = myAgree.createClientHello(pCertificate.getKeyPair());
        final GordianKeySet myKeySet = (GordianKeySet) myAgree.getResult();

        /* Create the recipient info */
        final AlgorithmIdentifier myAlgId = myAgreeFactory.getIdentifierForSpec(pAgreeSpec);
        final IssuerAndSerialNumber myId = new IssuerAndSerialNumber(pCertificate.getIssuer().getName(), pCertificate.getSerialNo());
        final KeyTransRecipientInfo myKTInfo = new KeyTransRecipientInfo(new RecipientIdentifier(myId), myAlgId, new BEROctetString(myHello));
        final RecipientInfo myInfo = new RecipientInfo(myKTInfo);

        /* Return the result */
        return new GordianCRMResult(myInfo, myKeySet);
    }

    /**
     * Prepare for encryption.
     * @param pEncryptSpec the encryptionSpec
     * @param pCertificate the target certificate
     * @return the CRM result
     * @throws GordianException on error
     */
    private GordianCRMResult prepareForEncryption(final GordianEncryptorSpec pEncryptSpec,
                                                  final GordianCoreCertificate pCertificate) throws GordianException {
        /* Create the random key */
        final byte[] myKey = createKeyForKeySet();

        /* Derive the keySet from the key */
        final GordianKeySet myKeySet = deriveKeySetFromKey(myKey.clone());

        /* Create recipientInfo */
        final RecipientInfo myInfo = createRecipientInfo(myKey, pCertificate, pEncryptSpec);

        /* Return the result */
        return new GordianCRMResult(myInfo, myKeySet);
    }

    /**
     * Create a random key for KeySet.
     * @return the new key
     */
    private byte[] createKeyForKeySet() {
        final byte[] myKey = new byte[GordianLength.LEN_1024.getByteLength()];
        theFactory.getRandomSource().getRandom().nextBytes(myKey);
        return myKey;
    }

    /**
     * Derive a keySet from a key.
     * @param pKey the key
     * @return the keySet
     * @throws GordianException on error
     */
    private GordianKeySet deriveKeySetFromKey(final byte[] pKey) throws GordianException {
        /* Create a new Factory using the key */
        final GordianParameters myParams = new GordianParameters(pKey);
        final GordianFactory myFactory = theFactory.newFactory(myParams);
        return myFactory.getEmbeddedKeySet();
    }

    /**
     * Encrypt the key with a keyPair.
     * @param pKey the key to encrypt
     * @param pCertificate the target certificate
     * @param pSpec the encryptorSpec
     * @return the encrypted key
     * @throws GordianException on error
     */
    private RecipientInfo createRecipientInfo(final byte[] pKey,
                                              final GordianCoreCertificate pCertificate,
                                              final GordianEncryptorSpec pSpec) throws GordianException {
        /* Create the encrypted key */
        final GordianAsyncFactory myFactory = theFactory.getAsyncFactory();
        final GordianCoreEncryptorFactory myEncFactory = (GordianCoreEncryptorFactory) myFactory.getEncryptorFactory();
        final GordianEncryptor myEncryptor = myEncFactory.createEncryptor(pSpec);

        /* Create the encrypted key */
        myEncryptor.initForEncrypt(pCertificate.getKeyPair());
        final byte[] myEncryptedKey = myEncryptor.encrypt(pKey);
        Arrays.fill(pKey, (byte) 0);

        /* Build recipientInfo */
        final AlgorithmIdentifier myAlgId = myEncFactory.getIdentifierForSpec(pSpec);
        final IssuerAndSerialNumber myId = new IssuerAndSerialNumber(pCertificate.getIssuer().getName(), pCertificate.getSerialNo());
        final KeyTransRecipientInfo myKTInfo = new KeyTransRecipientInfo(new RecipientIdentifier(myId), myAlgId, new BEROctetString(myEncryptedKey));
        return new RecipientInfo(myKTInfo);
    }

    /**
     * Build the encryptedContentInfo for a PrivateKey.
     * @param pKeySet the keySet to encrypt with
     * @param pPKCS8Encoding the PKCS8Encoded privateKey
     * @param pCertificate the local certificate
     * @return the encryptedContentInfo
     * @throws GordianException on error
     */
    public static EncryptedContentInfo buildEncryptedContentInfo(final GordianKeySet pKeySet,
                                                                 final PKCS8EncodedKeySpec pPKCS8Encoding,
                                                                 final GordianCertificate pCertificate) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Obtain the PrivateKeyInfo */
            final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPKCS8Encoding.getEncoded());
            final EncKeyWithID myKeyWithId = new EncKeyWithID(myInfo, new GeneralName(pCertificate.getSubject().getName()));
            final byte[] myData = pKeySet.encryptBytes(myKeyWithId.getEncoded());
            final GordianKeySetSpecASN1 myASN1 = new GordianKeySetSpecASN1(pKeySet.getKeySetSpec());
            final AlgorithmIdentifier myAlgId = myASN1.getAlgorithmId();
            return new EncryptedContentInfo(CRMFObjectIdentifiers.id_ct_encKeyWithID, myAlgId, new BEROctetString(myData));

        } catch (IOException e) {
            throw new GordianIOException("Failed to create EncryptedContentInfo", e);
        }
    }

    /**
     * Build the encryptedContentInfo.
     * @param pKeySet the keySet to encrypt with
     * @param pCertificate the certificate to encrypt
     * @return the encryptedContentInfo
     * @throws GordianException on error
     */
    public static EncryptedContentInfo buildEncryptedContentInfo(final GordianKeySet pKeySet,
                                                                 final GordianCertificate pCertificate) throws GordianException {
        /* Obtain the encrypted certificate */
        final byte[] myData = pKeySet.encryptBytes(pCertificate.getEncoded());
        final GordianKeySetSpecASN1 myASN1 = new GordianKeySetSpecASN1(pKeySet.getKeySetSpec());
        final AlgorithmIdentifier myAlgId = myASN1.getAlgorithmId();
        return new EncryptedContentInfo(PKCSObjectIdentifiers.x509Certificate, myAlgId, new BEROctetString(myData));
    }

    /**
     * Derive the keySet via a keyPairSet issuer.
     * @param pRecInfo the recipient info
     * @param pCertificate the receiving certificate
     * @param pKeyPair the keyPair
     * @return the keySet
     * @throws GordianException on error
     */
    public GordianKeySet deriveKeySetFromRecInfo(final KeyTransRecipientInfo pRecInfo,
                                                 final GordianCertificate pCertificate,
                                                 final GordianKeyPair pKeyPair) throws GordianException {
        /* Extract details */
        final AlgorithmIdentifier myAlgId = pRecInfo.getKeyEncryptionAlgorithm();
        final byte[] myEncryptedKey = pRecInfo.getEncryptedKey().getOctets();

        /* Derive the keySet appropriately */
        final GordianKeyPairUsage myUsage = pCertificate.getUsage();
        return myUsage.hasUse(GordianKeyPairUse.KEYENCRYPT)
                ? deriveEncryptedKeySet(pKeyPair, myAlgId, myEncryptedKey)
                : deriveAgreedKeySet(pKeyPair, myEncryptedKey);
    }

    /**
     * Derive an encrypted keySet.
     * @param pKeyPair the keyPair
     * @param pAlgId the algorithm Identifier
     * @param pEncryptedKey the encrypted key
     * @return the derived keySet
     * @throws GordianException on error
     */
    private GordianKeySet deriveEncryptedKeySet(final GordianKeyPair pKeyPair,
                                                final AlgorithmIdentifier pAlgId,
                                                final byte[] pEncryptedKey) throws GordianException {
        /* Handle decryption */
        final GordianAsyncFactory myFactory = theFactory.getAsyncFactory();
        final GordianCoreEncryptorFactory myEncFactory = (GordianCoreEncryptorFactory) myFactory.getEncryptorFactory();
        final GordianEncryptorSpec myEncSpec = myEncFactory.getSpecForIdentifier(pAlgId);
        final GordianEncryptor myEncryptor = myEncFactory.createEncryptor(myEncSpec);
        myEncryptor.initForDecrypt(pKeyPair);
        final byte[] myKey = myEncryptor.decrypt(pEncryptedKey);
        final GordianKeySet myKeySet = deriveKeySetFromKey(myKey);
        Arrays.fill(myKey, (byte) 0);
        return myKeySet;
    }

    /**
     * Derive an agreed keySet.
     * @param pKeyPair the keyPair
     * @param pHello the clientHello
     * @return the derived keySet
     * @throws GordianException on error
     */
   private GordianKeySet deriveAgreedKeySet(final GordianKeyPair pKeyPair,
                                            final byte[] pHello) throws GordianException {
        /* Handle agreement */
        final GordianAsyncFactory myFactory = theFactory.getAsyncFactory();
        final GordianCoreAgreementFactory myAgreeFactory = (GordianCoreAgreementFactory) myFactory.getAgreementFactory();
        final GordianAnonymousAgreement myAgree = (GordianAnonymousAgreement) myAgreeFactory.createAgreement(pHello);
        myAgree.acceptClientHello(pKeyPair, pHello);
        return (GordianKeySet) myAgree.getResult();
    }

    /**
     * Result class.
     */
    public static class GordianCRMResult {
        /**
         * Recipient info.
         */
        private final RecipientInfo theRecipient;

        /**
         * The keySet.
         */
        private final GordianKeySet theKeySet;

        /**
         * Constructor.
         * @param pKeySet the keySet
         * @param pRecipient the recipient.
         */
        GordianCRMResult(final RecipientInfo pRecipient,
                         final GordianKeySet pKeySet) {
            theRecipient = pRecipient;
            theKeySet = pKeySet;
        }

        /**
         * Obtain the recipient.
         * @return the recipient
         */
        public RecipientInfo getRecipient() {
            return theRecipient;
        }

        /**
         * Obtain the keySet.
         * @return the keySet
         */
        public GordianKeySet getKeySet() {
            return theKeySet;
        }
    }
}
