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
package net.sourceforge.joceanus.gordianknot.impl.bc;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignParams;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyEllipticKeyPair.BouncyECPrivateKey;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyEllipticKeyPair.BouncyECPublicKey;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncySignature.BouncyDSACoder;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncySignature.BouncyDigestSignature;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianDataConverter;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianIOException;
import net.sourceforge.joceanus.gordianknot.impl.core.keypair.GordianKeyPairValidity;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.sec.ECPrivateKey;
import org.bouncycastle.asn1.ua.DSTU4145NamedCurves;
import org.bouncycastle.asn1.ua.DSTU4145Params;
import org.bouncycastle.asn1.ua.DSTU4145PointEncoder;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.GOST3411Digest;
import org.bouncycastle.crypto.generators.DSTU4145KeyPairGenerator;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.DSTU4145Signer;
import org.bouncycastle.jcajce.provider.asymmetric.dstu.BCDSTU4145PrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.dstu.BCDSTU4145PublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

import java.io.IOException;
import java.math.BigInteger;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * DSTU KeyPair classes.
 */
public final class BouncyDSTUKeyPair {
    /**
     * DSTU algorithm.
     */
    private static final String ALGO = "DSTU4145";

    /**
     * Expanded sandBox length.
     */
    private static final int EXPANDED_LEN = 128;

    /**
     * Private constructor.
     */
    private BouncyDSTUKeyPair() {
    }

    /**
     * BouncyCastle DSTU KeyPair generator.
     */
    public static class BouncyDSTUKeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Generator.
         */
        private final ECKeyPairGenerator theGenerator;

        /**
         * Domain.
         */
        private final ECDomainParameters theDomain;

        /**
         * Spec.
         */
        private final ECNamedCurveSpec theSpec;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyDSTUKeyPairGenerator(final GordianBaseFactory pFactory,
                                   final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create the generator */
            theGenerator = new DSTU4145KeyPairGenerator();

            /* Determine domain */
            final String myCurveName = pKeySpec.getElliptic().getCurveName();
            theDomain = DSTU4145NamedCurves.getByOID(new ASN1ObjectIdentifier(myCurveName));
            theSpec = new ECNamedCurveSpec(myCurveName,
                    theDomain.getCurve(),
                    theDomain.getG(),
                    theDomain.getN(),
                    theDomain.getH(),
                    theDomain.getSeed());

            /* Perform conversion */
            final ECCurve myCurve = EC5Util.convertCurve(theSpec.getCurve());
            final ECPoint myG = EC5Util.convertPoint(myCurve, theSpec.getGenerator());

            /* Initialise the generator */
            final ECKeyGenerationParameters myParams = new ECKeyGenerationParameters(
                    new ECDomainParameters(myCurve, myG, theSpec.getOrder(), BigInteger.valueOf(theSpec.getCofactor())), getRandom());
            theGenerator.init(myParams);
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            /* Generate and return the keyPair */
            final AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            final BouncyECPublicKey myPublic = new BouncyECPublicKey(getKeySpec(), (ECPublicKeyParameters) myPair.getPublic());
            final BouncyECPrivateKey myPrivate = new BouncyECPrivateKey(getKeySpec(), (ECPrivateKeyParameters) myPair.getPrivate());
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws GordianException {
            /* Check the keyPair type and keySpecs */
            BouncyKeyPair.checkKeyPair(pKeyPair, getKeySpec());

            /* build and return the encoding */
            final BouncyECPrivateKey myPrivateKey = (BouncyECPrivateKey) getPrivateKey(pKeyPair);
            final ECPrivateKeyParameters myParms = myPrivateKey.getPrivateKey();
            final BouncyECPublicKey myPublicKey = (BouncyECPublicKey) getPublicKey(pKeyPair);
            final ECPublicKeyParameters myPubParms = myPublicKey.getPublicKey();
            final BCDSTU4145PublicKey pubKey = new BCDSTU4145PublicKey(ALGO, myPubParms, theSpec);
            final BCDSTU4145PrivateKey privKey = new BCDSTU4145PrivateKey(ALGO, myParms, pubKey, theSpec);
            return new PKCS8EncodedKeySpec(privKey.getEncoded());
        }

        @Override
        public BouncyKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKey,
                                           final PKCS8EncodedKeySpec pPrivateKey) throws GordianException {
            /* Check the keySpecs */
            checkKeySpec(pPrivateKey);

            /* derive keyPair */
            final BouncyECPublicKey myPublic = derivePublicKey(pPublicKey);
            final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
            final ECPrivateKeyParameters myParms = deriveFromPrivKeyInfo(myInfo);
            final BouncyECPrivateKey myPrivate = new BouncyECPrivateKey(getKeySpec(), myParms);
            final BouncyKeyPair myPair = new BouncyKeyPair(myPublic, myPrivate);

            /* Check that we have a matching pair */
            GordianKeyPairValidity.checkValidity(getFactory(), myPair);

            /* Return the keyPair */
            return myPair;
        }

        @Override
        public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) throws GordianException {
            /* Check the keyPair type and keySpecs */
            BouncyKeyPair.checkKeyPair(pKeyPair, getKeySpec());

            /* build and return the encoding */
            final BouncyECPublicKey myPublicKey = (BouncyECPublicKey) getPublicKey(pKeyPair);
            final ECPublicKeyParameters myParms = myPublicKey.getPublicKey();
            final BCDSTU4145PublicKey pubKey = new BCDSTU4145PublicKey(ALGO, myParms, theSpec);
            return new X509EncodedKeySpec(pubKey.getEncoded());
        }

        @Override
        public BouncyKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pEncodedKey) throws GordianException {
            final BouncyECPublicKey myPublic = derivePublicKey(pEncodedKey);
            return new BouncyKeyPair(myPublic);
        }

        /**
         * Derive public key from encoded.
         * @param pEncodedKey the encoded key
         * @return the public key
         * @throws GordianException on error
         */
        private BouncyECPublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws GordianException {
            /* Check the keySpecs */
            checkKeySpec(pEncodedKey);

            /* derive publicKey */
            final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
            final ECPublicKeyParameters myParms = deriveFromPubKeyInfo(myInfo);
            return new BouncyECPublicKey(getKeySpec(), myParms);
        }

        /**
         * Derive Public Key parameters from SubjectPublicKeyInfo. (extracted from BouncyCastle initialiser)
         * @param pKeyInfo the keyInfo
         * @return the PrivateKeyParameters
         * @throws GordianException on error
         */
        private ECPublicKeyParameters deriveFromPubKeyInfo(final SubjectPublicKeyInfo pKeyInfo) throws GordianException {
            final ASN1BitString bits = pKeyInfo.getPublicKeyData();
            final ASN1OctetString key;

            try {
                key = (ASN1OctetString) ASN1Primitive.fromByteArray(bits.getBytes());
            } catch (IOException ex) {
                throw new GordianIOException("error recovering public key", ex);
            }

            final byte[] keyEnc = key.getOctets();
            final ECCurve curve = theDomain.getCurve();
            return new ECPublicKeyParameters(DSTU4145PointEncoder.decodePoint(curve, keyEnc), theDomain);
        }

        /**
         * Derive Private Key parameters from PrivateKeyInfo. (extracted from BouncyCastle initialiser)
         * @param pKeyInfo the keyInfo
         * @return the PrivateKeyParameters
         * @throws GordianException on error
         */
        private ECPrivateKeyParameters deriveFromPrivKeyInfo(final PrivateKeyInfo pKeyInfo) throws GordianException {
            try {
                final ASN1Encodable privKey = pKeyInfo.parsePrivateKey();
                final BigInteger myD;
                if (privKey instanceof ASN1Integer) {
                    myD = ASN1Integer.getInstance(privKey).getPositiveValue();
                } else {
                    final ECPrivateKey ec = ECPrivateKey.getInstance(privKey);

                    myD = ec.getKey();
                }
                return new ECPrivateKeyParameters(myD, theDomain);

            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }
    }

    /**
     * DSTU encoder.
     */
    static final class BouncyDSTUCoder implements BouncyDSACoder {
        @Override
        public byte[] dsaEncode(final BigInteger r,
                                final BigInteger s) throws GordianException {
            /* Protect against exceptions */
            try {
                /* Access byteArrays */
                final byte[] myFirst = s.toByteArray();
                final byte[] mySecond = r.toByteArray();
                final byte[] myResult = myFirst.length > mySecond.length
                                        ? new byte[myFirst.length * 2]
                                        : new byte[mySecond.length * 2];

                /* Build array and return */
                System.arraycopy(myFirst, 0, myResult, myResult.length / 2 - myFirst.length, myFirst.length);
                System.arraycopy(mySecond, 0, myResult, myResult.length - mySecond.length, mySecond.length);
                return new DEROctetString(myResult).getEncoded();
            } catch (Exception e) {
                throw new GordianCryptoException(BouncySignature.ERROR_SIGGEN, e);
            }
        }

        @Override
        public BigInteger[] dsaDecode(final byte[] pEncoded) throws GordianException {
            /* Protect against exceptions */
            try {
                /* Access the bytes */
                final byte[] bytes = ((ASN1OctetString) ASN1Primitive.fromByteArray(pEncoded)).getOctets();

                /* Build the value arrays */
                final byte[] myFirst = new byte[bytes.length / 2];
                final byte[] mySecond = new byte[bytes.length / 2];
                System.arraycopy(bytes, 0, myFirst, 0, myFirst.length);
                System.arraycopy(bytes, myFirst.length, mySecond, 0, mySecond.length);

                /* Create the signature values and return */
                final BigInteger[] sig = new BigInteger[2];
                sig[1] = new BigInteger(1, myFirst);
                sig[0] = new BigInteger(1, mySecond);
                return sig;
            } catch (Exception e) {
                throw new GordianCryptoException(BouncySignature.ERROR_SIGPARSE, e);
            }
        }
    }

    /**
     * DSTU signer.
     */
    public static class BouncyDSTUSignature
            extends BouncyDigestSignature {
        /**
         * The Signer.
         */
        private final DSTU4145Signer theSigner;

        /**
         * The Coder.
         */
        private final BouncyDSTUCoder theCoder;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSpec the signatureSpec.
         */
        BouncyDSTUSignature(final GordianBaseFactory pFactory,
                            final GordianSignatureSpec pSpec) {
            /* Initialise underlying class */
            super(pFactory, pSpec, newDigest());

            /* Create the signer and Coder */
            theSigner = new DSTU4145Signer();
            theCoder = new BouncyDSTUCoder();
        }

        @Override
        public void initForSigning(final GordianSignParams pParams) throws GordianException {
            /* Initialise detail */
            super.initForSigning(pParams);
            final BouncyKeyPair myPair = getKeyPair();
            BouncyKeyPair.checkKeyPair(myPair);

            /* Initialise and set the signer */
            final BouncyECPrivateKey myPrivate = (BouncyECPrivateKey) myPair.getPrivateKey();
            final ParametersWithRandom myParms = new ParametersWithRandom(myPrivate.getPrivateKey(), getRandom());
            theSigner.init(true, myParms);
        }

        @Override
        public void initForVerify(final GordianSignParams pParams) throws GordianException {
            /* Initialise detail */
            super.initForVerify(pParams);
            final BouncyKeyPair myPair = getKeyPair();
            BouncyKeyPair.checkKeyPair(myPair);

            /* Initialise and set the signer */
            final BouncyECPublicKey myPublic = (BouncyECPublicKey) myPair.getPublicKey();
            theSigner.init(false, myPublic.getPublicKey());
        }

        @Override
        public byte[] sign() throws GordianException {
            /* Check that we are in signing mode */
            checkMode(GordianSignatureMode.SIGN);

            /* Sign the message */
            final BigInteger[] myValues = theSigner.generateSignature(getDigest());
            return theCoder.dsaEncode(myValues[0], myValues[1]);
        }

        @Override
        public boolean verify(final byte[] pSignature) throws GordianException {
            /* Check that we are in verify mode */
            checkMode(GordianSignatureMode.VERIFY);

            /* Verify the message */
            final BigInteger[] myValues = theCoder.dsaDecode(pSignature);
            return theSigner.verifySignature(getDigest(), myValues[0], myValues[1]);
        }

        /**
         * Obtain new digest for DSTU signer.
         * @return the new digest
         */
        private static Digest newDigest() {
            final byte[] myCompressed = DSTU4145Params.getDefaultDKE();
            final byte[] myExpanded = new byte[EXPANDED_LEN];

            for (int i = 0; i < myCompressed.length; i++) {
                myExpanded[i * 2] = (byte) ((myCompressed[i] >> GordianDataConverter.NYBBLE_SHIFT)
                                             & GordianDataConverter.NYBBLE_MASK);
                myExpanded[i * 2 + 1] = (byte) (myCompressed[i] & GordianDataConverter.NYBBLE_MASK);
            }
            return new GOST3411Digest(myExpanded);
        }
    }
}
