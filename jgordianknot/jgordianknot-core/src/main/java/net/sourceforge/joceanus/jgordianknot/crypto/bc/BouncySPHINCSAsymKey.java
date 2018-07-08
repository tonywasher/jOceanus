/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.crypto.bc;

import java.io.IOException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.digests.SHA512tDigest;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.asn1.SPHINCS256KeyParams;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCS256KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCS256KeyPairGenerator;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCS256Signer;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCSPublicKeyParameters;

import net.sourceforge.joceanus.jgordianknot.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianLength;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSPHINCSKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSignatureSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSigner;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianValidator;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyPublicKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncySignature.BouncyDigestSignature;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * SPHINCS AsymKey classes.
 */
public final class BouncySPHINCSAsymKey {
    /**
     * Private constructor.
     */
    private BouncySPHINCSAsymKey() {
    }

    /**
     * Bouncy SPHINCS PublicKey.
     */
    public static class BouncySPHINCSPublicKey
            extends BouncyPublicKey {
        /**
         * Public Key details.
         */
        private final SPHINCSPublicKeyParameters theKey;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPublicKey the public key
         */
        protected BouncySPHINCSPublicKey(final GordianAsymKeySpec pKeySpec,
                                         final SPHINCSPublicKeyParameters pPublicKey) {
            super(pKeySpec);
            theKey = pPublicKey;
        }

        /**
         * Obtain the public key.
         * @return the key
         */
        protected SPHINCSPublicKeyParameters getPublicKey() {
            return theKey;
        }

        @Override
        public boolean equals(final Object pThat) {
            /* Handle the trivial cases */
            if (pThat == this) {
                return true;
            }
            if (pThat == null) {
                return false;
            }

            /* Make sure that the object is the same class */
            if (!(pThat instanceof BouncySPHINCSPublicKey)) {
                return false;
            }

            /* Access the target field */
            final BouncySPHINCSPublicKey myThat = (BouncySPHINCSPublicKey) pThat;

            /* Check differences */
            return getKeySpec().equals(myThat.getKeySpec())
                   && compareKeys(theKey, myThat.getPublicKey());
        }

        @Override
        public int hashCode() {
            return GordianFactory.HASH_PRIME * getKeySpec().hashCode()
                   + theKey.hashCode();
        }

        /**
         * CompareKeys.
         * @param pFirst the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final SPHINCSPublicKeyParameters pFirst,
                                           final SPHINCSPublicKeyParameters pSecond) {
            return Arrays.equals(pFirst.getKeyData(), pSecond.getKeyData());
        }
    }

    /**
     * Bouncy SPHINCS PrivateKey.
     */
    public static class BouncySPHINCSPrivateKey
            extends BouncyPrivateKey {
        /**
         * Private Key details.
         */
        private final SPHINCSPrivateKeyParameters theKey;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPrivateKey the private key
         */
        protected BouncySPHINCSPrivateKey(final GordianAsymKeySpec pKeySpec,
                                          final SPHINCSPrivateKeyParameters pPrivateKey) {
            super(pKeySpec);
            theKey = pPrivateKey;
        }

        /**
         * Obtain the private key.
         * @return the key
         */
        protected SPHINCSPrivateKeyParameters getPrivateKey() {
            return theKey;
        }

        @Override
        public boolean equals(final Object pThat) {
            /* Handle the trivial cases */
            if (pThat == this) {
                return true;
            }
            if (pThat == null) {
                return false;
            }

            /* Make sure that the object is the same class */
            if (!(pThat instanceof BouncySPHINCSPrivateKey)) {
                return false;
            }

            /* Access the target field */
            final BouncySPHINCSPrivateKey myThat = (BouncySPHINCSPrivateKey) pThat;

            /* Check differences */
            return getKeySpec().equals(myThat.getKeySpec())
                   && compareKeys(theKey, myThat.getPrivateKey());
        }

        @Override
        public int hashCode() {
            return GordianFactory.HASH_PRIME * getKeySpec().hashCode()
                   + theKey.hashCode();
        }

        /**
         * CompareKeys.
         * @param pFirst the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final SPHINCSPrivateKeyParameters pFirst,
                                           final SPHINCSPrivateKeyParameters pSecond) {
            return Arrays.equals(pFirst.getKeyData(), pSecond.getKeyData());
        }
    }

    /**
     * BouncyCastle SPHINCS256 KeyPair generator.
     */
    public static class BouncySPHINCSKeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Generator.
         */
        private final SPHINCS256KeyPairGenerator theGenerator;

        /**
         * AlgorithmId.
         */
        private final AlgorithmIdentifier theAlgorithmId;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        protected BouncySPHINCSKeyPairGenerator(final BouncyFactory pFactory,
                                                final GordianAsymKeySpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Determine the algorithm Id */
            final GordianSPHINCSKeyType myKeyType = pKeySpec.getSPHINCSType();
            final ASN1ObjectIdentifier myId = GordianSPHINCSKeyType.SHA3.equals(myKeyType)
                                                                                           ? NISTObjectIdentifiers.id_sha3_256
                                                                                           : NISTObjectIdentifiers.id_sha512_256;
            theAlgorithmId = new AlgorithmIdentifier(PQCObjectIdentifiers.sphincs256,
                    new SPHINCS256KeyParams(new AlgorithmIdentifier(myId)));

            /* Determine the digest */
            final Digest myDigest = GordianSPHINCSKeyType.SHA3.equals(myKeyType)
                                                                                 ? new SHA3Digest(GordianLength.LEN_256.getLength())
                                                                                 : new SHA512tDigest(GordianLength.LEN_256.getLength());

            /* Create and initialise the generator */
            theGenerator = new SPHINCS256KeyPairGenerator();
            final SPHINCS256KeyGenerationParameters myParams = new SPHINCS256KeyGenerationParameters(getRandom(), myDigest);
            theGenerator.init(myParams);
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            final AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            final BouncySPHINCSPublicKey myPublic = new BouncySPHINCSPublicKey(getKeySpec(), SPHINCSPublicKeyParameters.class.cast(myPair.getPublic()));
            final BouncySPHINCSPrivateKey myPrivate = new BouncySPHINCSPrivateKey(getKeySpec(), SPHINCSPrivateKeyParameters.class.cast(myPair.getPrivate()));
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            try {
                final BouncySPHINCSPrivateKey myPrivateKey = BouncySPHINCSPrivateKey.class.cast(getPrivateKey(pKeyPair));
                final SPHINCSPrivateKeyParameters myParms = myPrivateKey.getPrivateKey();
                final PrivateKeyInfo myInfo = new PrivateKeyInfo(theAlgorithmId, new DEROctetString(myParms.getKeyData()));
                return new PKCS8EncodedKeySpec(myInfo.getEncoded());
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public BouncyKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKey,
                                           final PKCS8EncodedKeySpec pPrivateKey) throws OceanusException {
            try {
                final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                final SPHINCSPrivateKeyParameters myParms = new SPHINCSPrivateKeyParameters(ASN1OctetString.getInstance(myInfo.parsePrivateKey()).getOctets());
                final BouncySPHINCSPrivateKey myPrivate = new BouncySPHINCSPrivateKey(getKeySpec(), myParms);
                final BouncySPHINCSPublicKey myPublic = derivePublicKey(pPublicKey);
                return new BouncyKeyPair(myPublic, myPrivate);
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            final BouncySPHINCSPublicKey myPublicKey = BouncySPHINCSPublicKey.class.cast(getPublicKey(pKeyPair));
            final SPHINCSPublicKeyParameters myParms = myPublicKey.getPublicKey();
            final SubjectPublicKeyInfo myInfo = new SubjectPublicKeyInfo(theAlgorithmId, myParms.getKeyData());
            final byte[] myBytes = KeyUtil.getEncodedSubjectPublicKeyInfo(myInfo);
            return new X509EncodedKeySpec(myBytes);
        }

        @Override
        public BouncyKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            final BouncySPHINCSPublicKey myPublic = derivePublicKey(pEncodedKey);
            return new BouncyKeyPair(myPublic);
        }

        /**
         * Derive public key from encoded.
         * @param pEncodedKey the encoded key
         * @return the public key
         */
        private BouncySPHINCSPublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) {
            final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
            final SPHINCSPublicKeyParameters myParms = new SPHINCSPublicKeyParameters(myInfo.getPublicKeyData().getBytes());
            return new BouncySPHINCSPublicKey(getKeySpec(), myParms);
        }
    }

    /**
     * SPHINCS signer.
     */
    public static class BouncySPHINCSSigner
            extends BouncyDigestSignature
            implements GordianSigner {
        /**
         * The SPHINCS Signer.
         */
        private final SPHINCS256Signer theSigner;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pPrivateKey the private key
         * @param pSpec the signatureSpec.
         * @throws OceanusException on error
         */
        protected BouncySPHINCSSigner(final BouncyFactory pFactory,
                                      final BouncySPHINCSPrivateKey pPrivateKey,
                                      final GordianSignatureSpec pSpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Access the SPHINCS keyType */
            final GordianSPHINCSKeyType myKeyType = pPrivateKey.getKeySpec().getSPHINCSType();

            /* Create the internal digests */
            final BouncyDigest myTreeDigest = pFactory.createDigest(GordianSPHINCSKeyType.SHA3.equals(myKeyType)
                                                                                                                 ? GordianDigestSpec.sha3(GordianLength.LEN_256)
                                                                                                                 : GordianDigestSpec.sha2Alt(GordianLength.LEN_256));
            final BouncyDigest myMsgDigest = pFactory.createDigest(GordianSPHINCSKeyType.SHA3.equals(myKeyType)
                                                                                                                ? GordianDigestSpec.sha3(GordianLength.LEN_512)
                                                                                                                : GordianDigestSpec.sha2(GordianLength.LEN_512));

            /* Create the signer */
            theSigner = new SPHINCS256Signer(myTreeDigest.getDigest(), myMsgDigest.getDigest());

            /* Initialise and set the signer */
            theSigner.init(true, pPrivateKey.getPrivateKey());
        }

        @Override
        public byte[] sign() throws OceanusException {
            /* Sign the message */
            return theSigner.generateSignature(getDigest());
        }
    }

    /**
     * SPHINCS validator.
     */
    public static class BouncySPHINCSValidator
            extends BouncyDigestSignature
            implements GordianValidator {
        /**
         * The SPHINCS Signer.
         */
        private final SPHINCS256Signer theSigner;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pPublicKey the public key
         * @param pSpec the signatureSpec.
         * @throws OceanusException on error
         */
        protected BouncySPHINCSValidator(final BouncyFactory pFactory,
                                         final BouncySPHINCSPublicKey pPublicKey,
                                         final GordianSignatureSpec pSpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Access the SPHINCS keyType */
            final GordianSPHINCSKeyType myKeyType = pPublicKey.getKeySpec().getSPHINCSType();

            /* Create the internal digests */
            final BouncyDigest myTreeDigest = pFactory.createDigest(GordianSPHINCSKeyType.SHA3.equals(myKeyType)
                                                                                                                 ? GordianDigestSpec.sha3(GordianLength.LEN_256)
                                                                                                                 : GordianDigestSpec.sha2Alt(GordianLength.LEN_256));
            final BouncyDigest myMsgDigest = pFactory.createDigest(GordianSPHINCSKeyType.SHA3.equals(myKeyType)
                                                                                                                ? GordianDigestSpec.sha3(GordianLength.LEN_512)
                                                                                                                : GordianDigestSpec.sha2(GordianLength.LEN_512));

            /* Create the signer */
            theSigner = new SPHINCS256Signer(myTreeDigest.getDigest(), myMsgDigest.getDigest());

            /* Initialise and set the signer */
            theSigner.init(false, pPublicKey.getPublicKey());
        }

        @Override
        public boolean verify(final byte[] pSignature) throws OceanusException {
            return theSigner.verifySignature(getDigest(), pSignature);
        }
    }
}
