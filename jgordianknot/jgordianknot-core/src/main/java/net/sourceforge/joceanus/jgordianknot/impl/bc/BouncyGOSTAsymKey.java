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
package net.sourceforge.joceanus.jgordianknot.impl.bc;

import java.io.IOException;
import java.math.BigInteger;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.ECGOST3410_2012Signer;
import org.bouncycastle.jcajce.provider.asymmetric.ecgost12.BCECGOST3410_2012PrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ecgost12.BCECGOST3410_2012PublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyEllipticAsymKey.BouncyECPrivateKey;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyEllipticAsymKey.BouncyECPublicKey;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncySignature.BouncyDSACoder;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncySignature.BouncyDigestSignature;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * GOST AsymKey classes.
 */
public final class BouncyGOSTAsymKey {
    /**
     * GOST algorithm.
     */
    private static final String ALGO = "ECGOST3410-2012";

    /**
     * Length 32.
     */
    private static final int LEN32 = 32;

    /**
     * Length 32.
     */
    private static final int LEN64 = 64;

    /**
     * Encoding id.
     */
    private static final byte ENCODING_ID = 0x04;

    /**
     * Private constructor.
     */
    private BouncyGOSTAsymKey() {
    }

    /**
     * BouncyCastle GOST KeyPair generator.
     */
    public static class BouncyGOSTKeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Curve.
         */
        private final String theCurve;

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
         * @throws OceanusException on error
         */
        BouncyGOSTKeyPairGenerator(final BouncyFactory pFactory,
                                   final GordianAsymKeySpec pKeySpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create the generator */
            theGenerator = new ECKeyPairGenerator();

            /* Determine domain */
            theCurve = pKeySpec.getElliptic().getCurveName();
            theDomain = ECGOST3410NamedCurves.getByName(theCurve);
            theSpec = new ECNamedCurveSpec(theCurve,
                    theDomain.getCurve(),
                    theDomain.getG(),
                    theDomain.getN(),
                    theDomain.getH(),
                    theDomain.getSeed());

            /* Perform conversion */
            final ECCurve myCurve = EC5Util.convertCurve(theSpec.getCurve());
            final ECPoint myG = EC5Util.convertPoint(myCurve, theSpec.getGenerator(), false);

            /* Initialise the generator */
            final ECKeyGenerationParameters myParams = new ECKeyGenerationParameters(
                    new ECDomainParameters(myCurve, myG, theSpec.getOrder(), BigInteger.valueOf(theSpec.getCofactor())), getRandom());
            theGenerator.init(myParams);
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            final AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            final BouncyECPublicKey myPublic = new BouncyECPublicKey(getKeySpec(), (ECPublicKeyParameters) myPair.getPublic());
            final BouncyECPrivateKey myPrivate = new BouncyECPrivateKey(getKeySpec(), (ECPrivateKeyParameters) myPair.getPrivate());
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            final BouncyECPrivateKey myPrivateKey = (BouncyECPrivateKey) getPrivateKey(pKeyPair);
            final ECPrivateKeyParameters myParms = myPrivateKey.getPrivateKey();
            final BouncyECPublicKey myPublicKey = (BouncyECPublicKey) getPublicKey(pKeyPair);
            final ECPublicKeyParameters myPubParms = myPublicKey.getPublicKey();
            final BCECGOST3410_2012PublicKey pubKey = new BCECGOST3410_2012PublicKey(ALGO, myPubParms, theSpec);
            final BCECGOST3410_2012PrivateKey privKey = new BCECGOST3410_2012PrivateKey(ALGO, myParms, pubKey, theSpec);
            return new PKCS8EncodedKeySpec(privKey.getEncoded());
        }

        @Override
        public BouncyKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKey,
                                           final PKCS8EncodedKeySpec pPrivateKey) throws OceanusException {
            final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
            final ECPrivateKeyParameters myParms = deriveFromPrivKeyInfo(myInfo);
            final BouncyECPrivateKey myPrivate = new BouncyECPrivateKey(getKeySpec(), myParms);
            final BouncyECPublicKey myPublic = derivePublicKey(pPublicKey);
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            final BouncyECPublicKey myPublicKey = (BouncyECPublicKey) getPublicKey(pKeyPair);
            final ECPublicKeyParameters myParms = myPublicKey.getPublicKey();
            final BCECGOST3410_2012PublicKey pubKey = new BCECGOST3410_2012PublicKey(ALGO, myParms, theSpec);
            return new X509EncodedKeySpec(pubKey.getEncoded());
        }

        @Override
        public BouncyKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            final BouncyECPublicKey myPublic = derivePublicKey(pEncodedKey);
            return new BouncyKeyPair(myPublic);
        }

        /**
         * Derive public key from encoded.
         * @param pEncodedKey the encoded key
         * @return the public key
         * @throws OceanusException on error
         */
        private BouncyECPublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
            final ECPublicKeyParameters myParms = deriveFromPubKeyInfo(myInfo);
            return new BouncyECPublicKey(getKeySpec(), myParms);
        }

        /**
         * Derive Public Key parameters from SubjectPublicKeyInfo. (extracted from BouncyCastle initialiser)
         * @param pKeyInfo the keyInfo
         * @return the PrivateKeyParameters
         * @throws OceanusException on error
         */
        private ECPublicKeyParameters deriveFromPubKeyInfo(final SubjectPublicKeyInfo pKeyInfo) throws OceanusException {
            final ASN1ObjectIdentifier algOid = pKeyInfo.getAlgorithm().getAlgorithm();
            final DERBitString bits = pKeyInfo.getPublicKeyData();
            final ASN1OctetString key;

            try {
                key = (ASN1OctetString) ASN1Primitive.fromByteArray(bits.getBytes());
            } catch (IOException ex) {
                throw new GordianDataException("error recovering public key");
            }

            final byte[] keyEnc = key.getOctets();
            int fieldSize = LEN32;
            if (algOid.equals(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512)) {
                fieldSize = LEN64;
            }

            final int keySize = 2 * fieldSize;
            final byte[] x9Encoding = new byte[1 + keySize];
            x9Encoding[0] = ENCODING_ID;
            for (int i = 1; i <= fieldSize; ++i) {
                x9Encoding[i            ] = keyEnc[fieldSize - i];
                x9Encoding[i + fieldSize] = keyEnc[keySize - i];
            }

            final ECCurve curve = theDomain.getCurve();
            return new ECPublicKeyParameters(curve.decodePoint(x9Encoding), theDomain);
        }

        /**
         * Derive Private Key parameters from PrivateKeyInfo. (extracted from BouncyCastle initialiser)
         * @param pKeyInfo the keyInfo
         * @return the PrivateKeyParameters
         * @throws OceanusException on error
         */
        private ECPrivateKeyParameters deriveFromPrivKeyInfo(final PrivateKeyInfo pKeyInfo) throws OceanusException {
            try {
                final ASN1Encodable privKey = pKeyInfo.parsePrivateKey();
                final BigInteger myD;
                if (privKey instanceof ASN1Integer) {
                    myD = ASN1Integer.getInstance(privKey).getPositiveValue();
                } else {
                    final byte[] encVal = ASN1OctetString.getInstance(privKey).getOctets();
                    final byte[] dVal = new byte[encVal.length];

                    for (int i = 0; i != encVal.length; i++) {
                        dVal[i] = encVal[encVal.length - 1 - i];
                    }

                    myD = new BigInteger(1, dVal);
                }
                return new ECPrivateKeyParameters(myD, theDomain);

            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }
    }

    /**
     * GOST encoder.
     */
    static final class BouncyGOSTCoder
            implements BouncyDSACoder {
        /**
         * The fixed length (if any).
         */
        private final Integer theLen;

        /**
         * Constructor.
         * @param pLen the fixed length (or null)
         */
        BouncyGOSTCoder(final Integer pLen) {
            theLen = pLen;
        }

        @Override
        public byte[] dsaEncode(final BigInteger r,
                                final BigInteger s) throws OceanusException {
            /* Access byteArrays */
            final byte[] myFirst = makeUnsigned(s);
            final byte[] mySecond = makeUnsigned(r);
            final byte[] myResult = new byte[theLen];

            /* Build array and return */
            System.arraycopy(myFirst, 0, myResult, theLen / 2 - myFirst.length, myFirst.length);
            System.arraycopy(mySecond, 0, myResult, theLen - mySecond.length, mySecond.length);
            return myResult;
        }

        /**
         * Make the value unsigned.
         * @param pValue the value
         * @return the unsigned value
         */
        private static byte[] makeUnsigned(final BigInteger pValue) {
            /* Convert to byteArray and return if OK */
            final byte[] myResult = pValue.toByteArray();
            if (myResult[0] != 0) {
                return myResult;
            }

            /* Shorten the array */
            final byte[] myTmp = new byte[myResult.length - 1];
            System.arraycopy(myResult, 1, myTmp, 0, myTmp.length);
            return myTmp;
        }

        @Override
        public BigInteger[] dsaDecode(final byte[] pEncoded) throws OceanusException {
            /* Build the value arrays */
            final byte[] myFirst = new byte[pEncoded.length / 2];
            final byte[] mySecond = new byte[pEncoded.length / 2];
            System.arraycopy(pEncoded, 0, myFirst, 0, myFirst.length);
            System.arraycopy(pEncoded, myFirst.length, mySecond, 0, mySecond.length);

            /* Create the signature values and return */
            final BigInteger[] sig = new BigInteger[2];
            sig[1] = new BigInteger(1, myFirst);
            sig[0] = new BigInteger(1, mySecond);
            return sig;
        }
    }

    /**
     * GOST signer.
     */
    public static class BouncyGOSTSignature
            extends BouncyDigestSignature {
        /**
         * The Signer.
         */
        private final ECGOST3410_2012Signer theSigner;

        /**
         * The Coder.
         */
        private final BouncyGOSTCoder theCoder;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSpec the signatureSpec.
         * @throws OceanusException on error
         */
        BouncyGOSTSignature(final BouncyFactory pFactory,
                            final GordianSignatureSpec pSpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Create the signer and Coder */
            theSigner = new ECGOST3410_2012Signer();
            theCoder = new BouncyGOSTCoder(pSpec.getDigestSpec().getDigestLength().getByteLength() << 1);
        }

        @Override
        public void initForSigning(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Initialise detail */
            super.initForSigning(pKeyPair);

            /* Initialise and set the signer */
            final BouncyECPrivateKey myPrivate = (BouncyECPrivateKey) getKeyPair().getPrivateKey();
            final ParametersWithRandom myParms = new ParametersWithRandom(myPrivate.getPrivateKey(), getRandom());
            theSigner.init(true, myParms);
        }

        @Override
        public void initForVerify(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Initialise detail */
            super.initForVerify(pKeyPair);

            /* Initialise and set the signer */
            final BouncyECPublicKey myPublic = (BouncyECPublicKey) getKeyPair().getPublicKey();
            theSigner.init(false, myPublic.getPublicKey());
        }

        @Override
        public byte[] sign() throws OceanusException {
            /* Check that we are in signing mode */
            checkMode(GordianSignatureMode.SIGN);

            /* Sign the message */
            final BigInteger[] myValues = theSigner.generateSignature(getDigest());
            return theCoder.dsaEncode(myValues[0], myValues[1]);
        }

        @Override
        public boolean verify(final byte[] pSignature) throws OceanusException {
            /* Check that we are in verify mode */
            checkMode(GordianSignatureMode.VERIFY);

            /* Verify the message */
            final BigInteger[] myValues = theCoder.dsaDecode(pSignature);
            return theSigner.verifySignature(getDigest(), myValues[0], myValues[1]);
        }
    }
}
