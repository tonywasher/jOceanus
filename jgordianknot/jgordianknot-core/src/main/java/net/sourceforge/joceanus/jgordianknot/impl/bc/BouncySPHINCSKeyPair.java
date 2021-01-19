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
package net.sourceforge.joceanus.jgordianknot.impl.bc;

import java.io.IOException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.digests.SHA512tDigest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCS256KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCS256KeyPairGenerator;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCS256Signer;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.util.PrivateKeyFactory;
import org.bouncycastle.pqc.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.pqc.crypto.util.PublicKeyFactory;
import org.bouncycastle.pqc.crypto.util.SubjectPublicKeyInfoFactory;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianSPHINCSDigestType;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyKeyPair.BouncyPrivateKey;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyKeyPair.BouncyPublicKey;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncySignature.BouncyDigestSignature;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypair.GordianKeyPairValidity;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * SPHINCS KeyPair classes.
 */
public final class BouncySPHINCSKeyPair {
    /**
     * Private constructor.
     */
    private BouncySPHINCSKeyPair() {
    }

    /**
     * Bouncy SPHINCS PublicKey.
     */
    public static class BouncySPHINCSPublicKey
            extends BouncyPublicKey<SPHINCSPublicKeyParameters> {
        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPublicKey the public key
         */
        BouncySPHINCSPublicKey(final GordianKeyPairSpec pKeySpec,
                               final SPHINCSPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final SPHINCSPublicKeyParameters myThis = getPublicKey();
            final SPHINCSPublicKeyParameters myThat = (SPHINCSPublicKeyParameters) pThat;

            /* Compare keys */
            return compareKeys(myThis, myThat);
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
            extends BouncyPrivateKey<SPHINCSPrivateKeyParameters> {
        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPrivateKey the private key
         */
        BouncySPHINCSPrivateKey(final GordianKeyPairSpec pKeySpec,
                                final SPHINCSPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }


        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final SPHINCSPrivateKeyParameters myThis = getPrivateKey();
            final SPHINCSPrivateKeyParameters myThat = (SPHINCSPrivateKeyParameters) pThat;

            /* Compare keys */
            return compareKeys(myThis, myThat);
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
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncySPHINCSKeyPairGenerator(final BouncyFactory pFactory,
                                      final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Determine the algorithm Id */
            final GordianSPHINCSDigestType myDigestType = pKeySpec.getSPHINCSDigestType();

            /* Determine the digest */
            final Digest myDigest = GordianSPHINCSDigestType.SHA3.equals(myDigestType)
                                    ? new SHA3Digest(GordianLength.LEN_256.getLength())
                                    : new SHA512tDigest(GordianLength.LEN_256.getLength());

            /* Create and initialise the generator */
            theGenerator = new SPHINCS256KeyPairGenerator();
            final SPHINCS256KeyGenerationParameters myParams = new SPHINCS256KeyGenerationParameters(getRandom(), myDigest);
            theGenerator.init(myParams);
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            /* Generate and return the keyPair */
            final AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            final BouncySPHINCSPublicKey myPublic = new BouncySPHINCSPublicKey(getKeySpec(), (SPHINCSPublicKeyParameters) myPair.getPublic());
            final BouncySPHINCSPrivateKey myPrivate = new BouncySPHINCSPrivateKey(getKeySpec(), (SPHINCSPrivateKeyParameters) myPair.getPrivate());
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Check the keyPair type and keySpecs */
                BouncyKeyPair.checkKeyPair(pKeyPair, getKeySpec());

                /* build and return the encoding */
                final BouncySPHINCSPrivateKey myPrivateKey = (BouncySPHINCSPrivateKey) getPrivateKey(pKeyPair);
                final SPHINCSPrivateKeyParameters myParms = myPrivateKey.getPrivateKey();
                final PrivateKeyInfo myInfo = PrivateKeyInfoFactory.createPrivateKeyInfo(myParms, null);
                return new PKCS8EncodedKeySpec(myInfo.getEncoded());

            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public BouncyKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKey,
                                           final PKCS8EncodedKeySpec pPrivateKey) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Check the keySpecs */
                checkKeySpec(pPrivateKey);

                /* derive keyPair */
                final BouncySPHINCSPublicKey myPublic = derivePublicKey(pPublicKey);
                final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                final SPHINCSPrivateKeyParameters myParms = (SPHINCSPrivateKeyParameters) PrivateKeyFactory.createKey(myInfo);
                final BouncySPHINCSPrivateKey myPrivate = new BouncySPHINCSPrivateKey(getKeySpec(), myParms);
                final BouncyKeyPair myPair = new BouncyKeyPair(myPublic, myPrivate);

                /* Check that we have a matching pair */
                GordianKeyPairValidity.checkValidity(getFactory(), myPair);

                /* Return the keyPair */
                return myPair;

            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Check the keyPair type and keySpecs */
                BouncyKeyPair.checkKeyPair(pKeyPair, getKeySpec());

                /* build and return the encoding */
                final BouncySPHINCSPublicKey myPublicKey = (BouncySPHINCSPublicKey) getPublicKey(pKeyPair);
                final SPHINCSPublicKeyParameters myParms = myPublicKey.getPublicKey();
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(myParms);
                return new X509EncodedKeySpec(myInfo.getEncoded());

            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
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
         * @throws OceanusException on error
         */
        private BouncySPHINCSPublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Check the keySpecs */
                checkKeySpec(pEncodedKey);

                /* derive publicKey */
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
                final SPHINCSPublicKeyParameters myParms = (SPHINCSPublicKeyParameters) PublicKeyFactory.createKey(myInfo);
                return new BouncySPHINCSPublicKey(getKeySpec(), myParms);

            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }
    }

    /**
     * SPHINCS signer.
     */
    public static class BouncySPHINCSSignature
            extends BouncyDigestSignature {
        /**
         * The SPHINCS Signer.
         */
        private SPHINCS256Signer theSigner;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSpec the signatureSpec.
         * @throws OceanusException on error
         */
        BouncySPHINCSSignature(final BouncyFactory pFactory,
                               final GordianSignatureSpec pSpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pSpec);
        }

        /**
         * Set the signer according to the keyPair.
         * @param pKeyPair the keyPair
         * @throws OceanusException on error
         */
        private void setSigner(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Access the SPHINCS keyType */
            final GordianSPHINCSDigestType myKeyType = pKeyPair.getKeyPairSpec().getSPHINCSDigestType();

            /* Create the internal digests */
            final BouncyDigestFactory myDigests = getFactory().getDigestFactory();
            final BouncyDigest myTreeDigest = myDigests.createDigest(GordianSPHINCSDigestType.SHA3.equals(myKeyType)
                                                                        ? GordianDigestSpec.sha3(GordianLength.LEN_256)
                                                                        : GordianDigestSpec.sha2Alt(GordianLength.LEN_256));
            final BouncyDigest myMsgDigest = myDigests.createDigest(GordianSPHINCSDigestType.SHA3.equals(myKeyType)
                                                                       ? GordianDigestSpec.sha3(GordianLength.LEN_512)
                                                                       : GordianDigestSpec.sha2(GordianLength.LEN_512));

            /* Create the signer */
            theSigner = new SPHINCS256Signer(myTreeDigest.getDigest(), myMsgDigest.getDigest());
        }

        @Override
        public void initForSigning(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Initialise detail */
            BouncyKeyPair.checkKeyPair(pKeyPair);
            super.initForSigning(pKeyPair);

            /* Set the digest */
            final GordianDigestSpec myDigestSpec = pKeyPair.getKeyPairSpec().getSPHINCSDigestType().getDigestSpec();
            setDigest(myDigestSpec);

            /* Initialise and set the signer */
            setSigner(pKeyPair);
            final BouncySPHINCSPrivateKey myPrivate = (BouncySPHINCSPrivateKey) getKeyPair().getPrivateKey();
            theSigner.init(true, myPrivate.getPrivateKey());
        }

        @Override
        public void initForVerify(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Initialise detail */
            BouncyKeyPair.checkKeyPair(pKeyPair);
            super.initForVerify(pKeyPair);

            /* Set the digest */
            final GordianDigestSpec myDigestSpec = pKeyPair.getKeyPairSpec().getSPHINCSDigestType().getDigestSpec();
            setDigest(myDigestSpec);

            /* Initialise and set the signer */
            setSigner(pKeyPair);
            final BouncySPHINCSPublicKey myPublic = (BouncySPHINCSPublicKey) getKeyPair().getPublicKey();
            theSigner.init(false, myPublic.getPublicKey());
        }

        @Override
        public byte[] sign() throws OceanusException {
            /* Check that we are in signing mode */
            checkMode(GordianSignatureMode.SIGN);

            /* Sign the message */
            return theSigner.generateSignature(getDigest());
        }

        @Override
        public boolean verify(final byte[] pSignature) throws OceanusException {
            /* Check that we are in verify mode */
            checkMode(GordianSignatureMode.VERIFY);

            /* Verify the message */
            return theSigner.verifySignature(getDigest(), pSignature);
        }
    }
}
