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
package net.sourceforge.joceanus.jgordianknot.impl.bc;

import java.io.IOException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.digests.SHA512tDigest;
import org.bouncycastle.crypto.newutils.PqcPrivateKeyFactory;
import org.bouncycastle.crypto.newutils.PqcPrivateKeyInfoFactory;
import org.bouncycastle.crypto.newutils.PqcPublicKeyFactory;
import org.bouncycastle.crypto.newutils.PqcSubjectPublicKeyInfoFactory;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCS256KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCS256KeyPairGenerator;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCS256Signer;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCSPublicKeyParameters;

import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianSPHINCSKeyType;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyKeyPair.BouncyPrivateKey;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyKeyPair.BouncyPublicKey;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncySignature.BouncyDigestSignature;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCryptoException;
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
            extends BouncyPublicKey<SPHINCSPublicKeyParameters> {
        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPublicKey the public key
         */
        BouncySPHINCSPublicKey(final GordianAsymKeySpec pKeySpec,
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
        BouncySPHINCSPrivateKey(final GordianAsymKeySpec pKeySpec,
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
         * TreeDigest.
         */
        private final ASN1ObjectIdentifier theTreeDigest;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncySPHINCSKeyPairGenerator(final BouncyFactory pFactory,
                                      final GordianAsymKeySpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Determine the algorithm Id */
            final GordianSPHINCSKeyType myKeyType = pKeySpec.getSPHINCSType();
            theTreeDigest = GordianSPHINCSKeyType.SHA3.equals(myKeyType)
                            ? NISTObjectIdentifiers.id_sha3_256
                            : NISTObjectIdentifiers.id_sha512_256;

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
            final BouncySPHINCSPublicKey myPublic = new BouncySPHINCSPublicKey(getKeySpec(), (SPHINCSPublicKeyParameters) myPair.getPublic());
            final BouncySPHINCSPrivateKey myPrivate = new BouncySPHINCSPrivateKey(getKeySpec(), (SPHINCSPrivateKeyParameters) myPair.getPrivate());
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            try {
                final BouncySPHINCSPrivateKey myPrivateKey = (BouncySPHINCSPrivateKey) getPrivateKey(pKeyPair);
                final SPHINCSPrivateKeyParameters myParms = myPrivateKey.getPrivateKey();
                final PrivateKeyInfo myInfo = PqcPrivateKeyInfoFactory.createSPHINCSPrivateKeyInfo(myParms, theTreeDigest);
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
                final SPHINCSPrivateKeyParameters myParms = (SPHINCSPrivateKeyParameters) PqcPrivateKeyFactory.createKey(myInfo);
                final BouncySPHINCSPrivateKey myPrivate = new BouncySPHINCSPrivateKey(getKeySpec(), myParms);
                final BouncySPHINCSPublicKey myPublic = derivePublicKey(pPublicKey);
                return new BouncyKeyPair(myPublic, myPrivate);
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            try {
                final BouncySPHINCSPublicKey myPublicKey = (BouncySPHINCSPublicKey) getPublicKey(pKeyPair);
                final SPHINCSPublicKeyParameters myParms = myPublicKey.getPublicKey();
                final SubjectPublicKeyInfo myInfo = PqcSubjectPublicKeyInfoFactory.createSPHINCSPublicKeyInfo(myParms, theTreeDigest);
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
            try {
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
                final SPHINCSPublicKeyParameters myParms = (SPHINCSPublicKeyParameters) PqcPublicKeyFactory.createKey(myInfo);
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
         * Constructor.
         * @param pKeyPair the keyPair
         * @throws OceanusException on error
         */
        private void setSigner(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Access the SPHINCS keyType */
            final GordianSPHINCSKeyType myKeyType = pKeyPair.getKeySpec().getSPHINCSType();

            /* Create the internal digests */
            final BouncyDigestFactory myDigests = getFactory().getDigestFactory();
            final BouncyDigest myTreeDigest = myDigests.createDigest(GordianSPHINCSKeyType.SHA3.equals(myKeyType)
                                                                        ? GordianDigestSpec.sha3(GordianLength.LEN_256)
                                                                        : GordianDigestSpec.sha2Alt(GordianLength.LEN_256));
            final BouncyDigest myMsgDigest = myDigests.createDigest(GordianSPHINCSKeyType.SHA3.equals(myKeyType)
                                                                       ? GordianDigestSpec.sha3(GordianLength.LEN_512)
                                                                       : GordianDigestSpec.sha2(GordianLength.LEN_512));

            /* Create the signer */
            theSigner = new SPHINCS256Signer(myTreeDigest.getDigest(), myMsgDigest.getDigest());
        }

        @Override
        public void initForSigning(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Initialise detail */
            super.initForSigning(pKeyPair);

            /* Set the digest */
            final GordianDigestSpec myDigestSpec = pKeyPair.getKeySpec().getSPHINCSType().getDigestSpec();
            setDigest(myDigestSpec);

            /* Initialise and set the signer */
            setSigner(pKeyPair);
            final BouncySPHINCSPrivateKey myPrivate = (BouncySPHINCSPrivateKey) getKeyPair().getPrivateKey();
            theSigner.init(true, myPrivate.getPrivateKey());
        }

        @Override
        public void initForVerify(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Initialise detail */
            super.initForVerify(pKeyPair);

            /* Set the digest */
            final GordianDigestSpec myDigestSpec = pKeyPair.getKeySpec().getSPHINCSType().getDigestSpec();
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
