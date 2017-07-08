/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2017 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.crypto.bc;

import java.io.IOException;
import java.math.BigInteger;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.spec.DHParameterSpec;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.DHParameter;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.bouncycastle.asn1.sec.ECPrivateKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DSAParameter;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ECPoint;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.digests.SHA512tDigest;
import org.bouncycastle.crypto.generators.DHKeyPairGenerator;
import org.bouncycastle.crypto.generators.DSAKeyPairGenerator;
import org.bouncycastle.crypto.generators.DSAParametersGenerator;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.DHKeyGenerationParameters;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.params.DHPrivateKeyParameters;
import org.bouncycastle.crypto.params.DHPublicKeyParameters;
import org.bouncycastle.crypto.params.DSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.DSAParameterGenerationParameters;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.crypto.params.DSAPrivateKeyParameters;
import org.bouncycastle.crypto.params.DSAPublicKeyParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.dh.BCDHPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.dh.BCDHPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.pqc.asn1.McElieceCCA2PrivateKey;
import org.bouncycastle.pqc.asn1.McElieceCCA2PublicKey;
import org.bouncycastle.pqc.asn1.McEliecePrivateKey;
import org.bouncycastle.pqc.asn1.McEliecePublicKey;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.asn1.RainbowPrivateKey;
import org.bouncycastle.pqc.asn1.RainbowPublicKey;
import org.bouncycastle.pqc.asn1.SPHINCS256KeyParams;
import org.bouncycastle.pqc.crypto.mceliece.McElieceCCA2KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.mceliece.McElieceCCA2KeyPairGenerator;
import org.bouncycastle.pqc.crypto.mceliece.McElieceCCA2Parameters;
import org.bouncycastle.pqc.crypto.mceliece.McElieceCCA2PrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mceliece.McElieceCCA2PublicKeyParameters;
import org.bouncycastle.pqc.crypto.mceliece.McElieceKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.mceliece.McElieceKeyPairGenerator;
import org.bouncycastle.pqc.crypto.mceliece.McElieceParameters;
import org.bouncycastle.pqc.crypto.mceliece.McEliecePrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mceliece.McEliecePublicKeyParameters;
import org.bouncycastle.pqc.crypto.newhope.NHKeyPairGenerator;
import org.bouncycastle.pqc.crypto.newhope.NHPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.newhope.NHPublicKeyParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowKeyPairGenerator;
import org.bouncycastle.pqc.crypto.rainbow.RainbowParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowPublicKeyParameters;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCS256KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCS256KeyPairGenerator;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCSPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.provider.mceliece.BCMcElieceCCA2PrivateKey;
import org.bouncycastle.pqc.jcajce.provider.mceliece.BCMcElieceCCA2PublicKey;
import org.bouncycastle.pqc.jcajce.provider.mceliece.BCMcEliecePrivateKey;
import org.bouncycastle.pqc.jcajce.provider.mceliece.BCMcEliecePublicKey;
import org.bouncycastle.pqc.jcajce.provider.newhope.BCNHPrivateKey;
import org.bouncycastle.pqc.jcajce.provider.newhope.BCNHPublicKey;
import org.bouncycastle.pqc.jcajce.provider.rainbow.BCRainbowPrivateKey;
import org.bouncycastle.pqc.jcajce.provider.rainbow.BCRainbowPublicKey;
import org.bouncycastle.pqc.jcajce.spec.McElieceCCA2KeyGenParameterSpec;

import net.sourceforge.joceanus.jgordianknot.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDSAKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianLength;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianModulus;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSPHINCSKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyDSAPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyDSAPublicKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyDiffieHellmanPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyDiffieHellmanPublicKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyECPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyECPublicKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyMcEliecePrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyMcEliecePublicKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyNewHopePrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyNewHopePublicKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyRSAPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyRSAPublicKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyRainbowPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyRainbowPublicKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncySPHINCSPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncySPHINCSPublicKey;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * BouncyCastle KeyPair generator.
 */
public abstract class BouncyKeyPairGenerator
        extends GordianKeyPairGenerator {
    /**
     * Parsing error.
     */
    private static final String ERROR_PARSE = "Failed to parse encoding";

    /**
     * Prime certainty.
     */
    private static final int PRIME_CERTAINTY = 128;

    /**
     * Constructor.
     * @param pFactory the Security Factory
     * @param pKeySpec the keySpec
     */
    protected BouncyKeyPairGenerator(final BouncyFactory pFactory,
                                     final GordianAsymKeySpec pKeySpec) {
        super(pFactory, pKeySpec);
    }

    /**
     * BouncyCastle RSA KeyPair generator.
     */
    public static class BouncyRSAKeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * RSA exponent.
         */
        private static final BigInteger RSA_EXPONENT = new BigInteger("10001", 16);

        /**
         * Generator.
         */
        private RSAKeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        protected BouncyRSAKeyPairGenerator(final BouncyFactory pFactory,
                                            final GordianAsymKeySpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            theGenerator = new RSAKeyPairGenerator();
            RSAKeyGenerationParameters myParams = new RSAKeyGenerationParameters(RSA_EXPONENT, getRandom(), pKeySpec.getModulus().getModulus(), PRIME_CERTAINTY);
            theGenerator.init(myParams);
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            BouncyRSAPublicKey myPublic = new BouncyRSAPublicKey(getKeySpec(), RSAKeyParameters.class.cast(myPair.getPublic()));
            BouncyRSAPrivateKey myPrivate = new BouncyRSAPrivateKey(getKeySpec(), RSAPrivateCrtKeyParameters.class.cast(myPair.getPrivate()));
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        protected PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            BouncyRSAPrivateKey myPrivateKey = BouncyRSAPrivateKey.class.cast(getPrivateKey(pKeyPair));
            RSAPrivateCrtKeyParameters myParms = myPrivateKey.getPrivateKey();
            byte[] myBytes = KeyUtil.getEncodedPrivateKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE),
                    new RSAPrivateKey(myParms.getModulus(), myParms.getPublicExponent(), myParms.getExponent(),
                            myParms.getP(), myParms.getQ(), myParms.getDP(), myParms.getDQ(), myParms.getQInv()));
            return new PKCS8EncodedKeySpec(myBytes);
        }

        @Override
        protected BouncyKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKey,
                                              final PKCS8EncodedKeySpec pPrivateKey) throws OceanusException {
            try {
                PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                RSAPrivateKey myKey = RSAPrivateKey.getInstance(myInfo.parsePrivateKey());
                RSAPrivateCrtKeyParameters myParms = new RSAPrivateCrtKeyParameters(myKey.getModulus(), myKey.getPublicExponent(), myKey.getPrivateExponent(),
                        myKey.getPrime1(), myKey.getPrime2(), myKey.getExponent1(), myKey.getExponent2(), myKey.getCoefficient());
                BouncyRSAPrivateKey myPrivate = new BouncyRSAPrivateKey(getKeySpec(), myParms);
                BouncyRSAPublicKey myPublic = derivePublicKey(pPublicKey);
                return new BouncyKeyPair(myPublic, myPrivate);
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            BouncyRSAPublicKey myPublicKey = BouncyRSAPublicKey.class.cast(getPublicKey(pKeyPair));
            RSAKeyParameters myParms = myPublicKey.getPublicKey();
            byte[] myBytes = KeyUtil.getEncodedSubjectPublicKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE),
                    new RSAPublicKey(myParms.getModulus(), myParms.getExponent()));
            return new X509EncodedKeySpec(myBytes);
        }

        @Override
        public BouncyKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            BouncyRSAPublicKey myPublic = derivePublicKey(pEncodedKey);
            return new BouncyKeyPair(myPublic);
        }

        /**
         * Derive public key from encoded.
         * @param pEncodedKey the encoded key
         * @return the public key
         * @throws OceanusException on error
         */
        private BouncyRSAPublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            try {
                SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
                RSAPublicKey myKey = RSAPublicKey.getInstance(myInfo.parsePublicKey());
                RSAKeyParameters myParms = new RSAKeyParameters(false, myKey.getModulus(), myKey.getPublicExponent());
                return new BouncyRSAPublicKey(getKeySpec(), myParms);
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }
    }

    /**
     * BouncyCastle Elliptic KeyPair generator.
     */
    public static class BouncyECKeyPairGenerator
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
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        protected BouncyECKeyPairGenerator(final BouncyFactory pFactory,
                                           final GordianAsymKeySpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create the generator */
            GordianAsymKeyType myKeyType = pKeySpec.getKeyType();
            theGenerator = new ECKeyPairGenerator();
            theCurve = GordianAsymKeyType.EC.equals(myKeyType)
                                                               ? pKeySpec.getElliptic().getCurveName()
                                                               : pKeySpec.getSM2Elliptic().getCurveName();

            /* Lookup the parameters */
            X9ECParameters x9 = GordianAsymKeyType.EC.equals(myKeyType)
                                                                        ? ECNamedCurveTable.getByName(theCurve)
                                                                        : GMNamedCurves.getByName(theCurve);

            /* Initialise the generator */
            theDomain = new ECDomainParameters(x9.getCurve(), x9.getG(), x9.getN(), x9.getH(), x9.getSeed());
            ECKeyGenerationParameters myParams = new ECKeyGenerationParameters(theDomain, getRandom());
            theGenerator.init(myParams);
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            BouncyECPublicKey myPublic = new BouncyECPublicKey(getKeySpec(), ECPublicKeyParameters.class.cast(myPair.getPublic()));
            BouncyECPrivateKey myPrivate = new BouncyECPrivateKey(getKeySpec(), ECPrivateKeyParameters.class.cast(myPair.getPrivate()));
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        protected PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            BouncyECPrivateKey myPrivateKey = BouncyECPrivateKey.class.cast(getPrivateKey(pKeyPair));
            ECPrivateKeyParameters myParms = myPrivateKey.getPrivateKey();
            X962Parameters myX962Parms = new X962Parameters(ECUtil.getNamedCurveOid(theCurve));
            BigInteger myOrder = myParms.getParameters().getCurve().getOrder();
            ECPrivateKey myKey = new ECPrivateKey(myOrder.bitLength(), myParms.getD(), myX962Parms);
            byte[] myBytes = KeyUtil.getEncodedPrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, myX962Parms.toASN1Primitive()),
                    myKey.toASN1Primitive());
            return new PKCS8EncodedKeySpec(myBytes);
        }

        @Override
        protected BouncyKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKey,
                                              final PKCS8EncodedKeySpec pPrivateKey) throws OceanusException {
            try {
                PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                ECPrivateKey myKey = ECPrivateKey.getInstance(myInfo.parsePrivateKey());
                ECPrivateKeyParameters myParms = new ECPrivateKeyParameters(myKey.getKey(), theDomain);
                BouncyECPrivateKey myPrivate = new BouncyECPrivateKey(getKeySpec(), myParms);
                BouncyECPublicKey myPublic = derivePublicKey(pPublicKey);
                return new BouncyKeyPair(myPublic, myPrivate);
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            BouncyECPublicKey myPublicKey = BouncyECPublicKey.class.cast(getPublicKey(pKeyPair));
            ECPublicKeyParameters myParms = myPublicKey.getPublicKey();
            X962Parameters myX962Parms = new X962Parameters(ECUtil.getNamedCurveOid(theCurve));
            ECCurve myCurve = theDomain.getCurve();
            ASN1OctetString p = (ASN1OctetString) new X9ECPoint(myCurve.createPoint(myParms.getQ().getAffineXCoord().toBigInteger(),
                    myParms.getQ().getAffineYCoord().toBigInteger())).toASN1Primitive();
            SubjectPublicKeyInfo myInfo = new SubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, myX962Parms), p.getOctets());
            byte[] myBytes = KeyUtil.getEncodedSubjectPublicKeyInfo(myInfo);
            return new X509EncodedKeySpec(myBytes);
        }

        @Override
        public BouncyKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            BouncyECPublicKey myPublic = derivePublicKey(pEncodedKey);
            return new BouncyKeyPair(myPublic);
        }

        /**
         * Derive public key from encoded.
         * @param pEncodedKey the encoded key
         * @return the public key
         * @throws OceanusException on error
         */
        private BouncyECPublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
            X9ECPoint myKey = new X9ECPoint(theDomain.getCurve(), new DEROctetString(myInfo.getPublicKeyData().getBytes()));
            ECPublicKeyParameters myParms = new ECPublicKeyParameters(myKey.getPoint(), theDomain);
            return new BouncyECPublicKey(getKeySpec(), myParms);
        }
    }

    /**
     * BouncyCastle DSA KeyPair generator.
     */
    public static class BouncyDSAKeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Generator.
         */
        private final DSAKeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        protected BouncyDSAKeyPairGenerator(final BouncyFactory pFactory,
                                            final GordianAsymKeySpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create the parameter generator */
            GordianDSAKeyType myKeyType = pKeySpec.getDSAKeyType();
            DSAParameterGenerationParameters myGenParms = new DSAParameterGenerationParameters(myKeyType.getKeySize(),
                    myKeyType.getHashSize(), PRIME_CERTAINTY, getRandom());
            DSAParametersGenerator myParmGenerator = new DSAParametersGenerator(new SHA256Digest());
            myParmGenerator.init(myGenParms);

            /* Create and initialise the generator */
            theGenerator = new DSAKeyPairGenerator();
            DSAKeyGenerationParameters myParams = new DSAKeyGenerationParameters(getRandom(), myParmGenerator.generateParameters());
            theGenerator.init(myParams);
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            BouncyDSAPublicKey myPublic = new BouncyDSAPublicKey(getKeySpec(), DSAPublicKeyParameters.class.cast(myPair.getPublic()));
            BouncyDSAPrivateKey myPrivate = new BouncyDSAPrivateKey(getKeySpec(), DSAPrivateKeyParameters.class.cast(myPair.getPrivate()));
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        protected PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws GordianCryptoException {
            try {
                BouncyDSAPrivateKey myPrivateKey = BouncyDSAPrivateKey.class.cast(getPrivateKey(pKeyPair));
                DSAPrivateKeyParameters myKey = myPrivateKey.getPrivateKey();
                DSAParameters myParms = myKey.getParameters();
                PrivateKeyInfo myInfo = new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa, new DSAParameter(myParms.getP(), myParms.getQ(), myParms.getG())
                        .toASN1Primitive()), new ASN1Integer(myKey.getX()));
                return new PKCS8EncodedKeySpec(myInfo.getEncoded());
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        protected BouncyKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKey,
                                              final PKCS8EncodedKeySpec pPrivateKey) throws OceanusException {
            try {
                PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                DSAParameter myParams = DSAParameter.getInstance(myInfo.getPrivateKeyAlgorithm().getParameters());
                ASN1Integer myX = ASN1Integer.getInstance(myInfo.parsePrivateKey());
                DSAParameters myParms = new DSAParameters(myParams.getP(), myParams.getQ(), myParams.getG());
                BouncyDSAPrivateKey myPrivate = new BouncyDSAPrivateKey(getKeySpec(), new DSAPrivateKeyParameters(myX.getValue(), myParms));
                BouncyDSAPublicKey myPublic = derivePublicKey(pPublicKey);
                return new BouncyKeyPair(myPublic, myPrivate);
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) {
            BouncyDSAPublicKey myPublicKey = BouncyDSAPublicKey.class.cast(getPublicKey(pKeyPair));
            DSAPublicKeyParameters myKey = myPublicKey.getPublicKey();
            DSAParameters myParms = myKey.getParameters();
            byte[] myBytes = KeyUtil.getEncodedSubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa,
                    new DSAParameter(myParms.getP(), myParms.getQ(), myParms.getG()).toASN1Primitive()), new ASN1Integer(myKey.getY()));
            return new X509EncodedKeySpec(myBytes);
        }

        @Override
        public BouncyKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            BouncyDSAPublicKey myPublic = derivePublicKey(pEncodedKey);
            return new BouncyKeyPair(myPublic);
        }

        /**
         * Derive public key from encoded.
         * @param pEncodedKey the encoded key
         * @return the public key
         * @throws OceanusException on error
         */
        private BouncyDSAPublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            try {
                SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
                DSAParameter myParams = DSAParameter.getInstance(myInfo.getAlgorithm().getParameters());
                ASN1Integer myY = ASN1Integer.getInstance(myInfo.parsePublicKey());
                DSAParameters myParms = new DSAParameters(myParams.getP(), myParams.getQ(), myParams.getG());
                return new BouncyDSAPublicKey(getKeySpec(), new DSAPublicKeyParameters(myY.getValue(), myParms));
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }
    }

    /**
     * BouncyCastle DiffieHellman KeyPair generator.
     */
    public static class BouncyDiffieHellmanKeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Generator.
         */
        private final DHKeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        protected BouncyDiffieHellmanKeyPairGenerator(final BouncyFactory pFactory,
                                                      final GordianAsymKeySpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create the parameter generator */
            GordianModulus myModulus = pKeySpec.getModulus();
            DHParameters myParms = myModulus.getDHParameters();
            DHKeyGenerationParameters myParams = new DHKeyGenerationParameters(getRandom(), myParms);

            /* Create and initialise the generator */
            theGenerator = new DHKeyPairGenerator();
            theGenerator.init(myParams);
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            BouncyDiffieHellmanPublicKey myPublic = new BouncyDiffieHellmanPublicKey(getKeySpec(), DHPublicKeyParameters.class.cast(myPair.getPublic()));
            BouncyDiffieHellmanPrivateKey myPrivate = new BouncyDiffieHellmanPrivateKey(getKeySpec(), DHPrivateKeyParameters.class.cast(myPair.getPrivate()));
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        protected PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws GordianCryptoException {
            try {
                BouncyDiffieHellmanPrivateKey myPrivateKey = BouncyDiffieHellmanPrivateKey.class.cast(getPrivateKey(pKeyPair));
                DHPrivateKeyParameters myKey = myPrivateKey.getPrivateKey();
                DHParameters myParms = myKey.getParameters();
                PrivateKeyInfo myInfo = new PrivateKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.dhKeyAgreement,
                        new DHParameter(myParms.getP(), myParms.getG(), myParms.getL()).toASN1Primitive()), new ASN1Integer(myKey.getX()));
                return new PKCS8EncodedKeySpec(myInfo.getEncoded());
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        protected BouncyKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKey,
                                              final PKCS8EncodedKeySpec pPrivateKey) throws OceanusException {
            try {
                PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                BCDHPrivateKey myKey = new BCDHPrivateKey(myInfo);
                DHParameterSpec mySpec = myKey.getParams();
                DHParameters myParms = new DHParameters(mySpec.getP(), mySpec.getG());
                BouncyDiffieHellmanPrivateKey myPrivate = new BouncyDiffieHellmanPrivateKey(getKeySpec(), new DHPrivateKeyParameters(myKey.getX(), myParms));
                BouncyDiffieHellmanPublicKey myPublic = derivePublicKey(pPublicKey);
                return new BouncyKeyPair(myPublic, myPrivate);
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) {
            BouncyDiffieHellmanPublicKey myPublicKey = BouncyDiffieHellmanPublicKey.class.cast(getPublicKey(pKeyPair));
            DHPublicKeyParameters myKey = myPublicKey.getPublicKey();
            DHParameters myParms = myKey.getParameters();
            byte[] myBytes = KeyUtil.getEncodedSubjectPublicKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.dhKeyAgreement,
                    new DHParameter(myParms.getP(), myParms.getG(), myParms.getL()).toASN1Primitive()), new ASN1Integer(myKey.getY()));
            return new X509EncodedKeySpec(myBytes);
        }

        @Override
        public BouncyKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pEncodedKey) {
            BouncyDiffieHellmanPublicKey myPublic = derivePublicKey(pEncodedKey);
            return new BouncyKeyPair(myPublic);
        }

        /**
         * Derive public key from encoded.
         * @param pEncodedKey the encoded key
         * @return the public key
         */
        private BouncyDiffieHellmanPublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) {
            SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
            BCDHPublicKey myKey = new BCDHPublicKey(myInfo);
            return new BouncyDiffieHellmanPublicKey(getKeySpec(), myKey.engineGetKeyParameters());
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
            GordianSPHINCSKeyType myKeyType = pKeySpec.getSPHINCSType();
            ASN1ObjectIdentifier myId = GordianSPHINCSKeyType.SHA3.equals(myKeyType)
                                                                                     ? NISTObjectIdentifiers.id_sha3_256
                                                                                     : NISTObjectIdentifiers.id_sha256;
            theAlgorithmId = new AlgorithmIdentifier(PQCObjectIdentifiers.sphincs256,
                    new SPHINCS256KeyParams(new AlgorithmIdentifier(myId)));

            /* Determine the digest */
            Digest myDigest = GordianSPHINCSKeyType.SHA3.equals(myKeyType)
                                                                           ? new SHA3Digest(GordianLength.LEN_256.getLength())
                                                                           : new SHA512tDigest(GordianLength.LEN_256.getLength());

            /* Create and initialise the generator */
            theGenerator = new SPHINCS256KeyPairGenerator();
            SPHINCS256KeyGenerationParameters myParams = new SPHINCS256KeyGenerationParameters(getRandom(), myDigest);
            theGenerator.init(myParams);
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            BouncySPHINCSPublicKey myPublic = new BouncySPHINCSPublicKey(getKeySpec(), SPHINCSPublicKeyParameters.class.cast(myPair.getPublic()));
            BouncySPHINCSPrivateKey myPrivate = new BouncySPHINCSPrivateKey(getKeySpec(), SPHINCSPrivateKeyParameters.class.cast(myPair.getPrivate()));
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        protected PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            try {
                BouncySPHINCSPrivateKey myPrivateKey = BouncySPHINCSPrivateKey.class.cast(getPrivateKey(pKeyPair));
                SPHINCSPrivateKeyParameters myParms = myPrivateKey.getPrivateKey();
                PrivateKeyInfo myInfo = new PrivateKeyInfo(theAlgorithmId, new DEROctetString(myParms.getKeyData()));
                return new PKCS8EncodedKeySpec(myInfo.getEncoded());
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        protected BouncyKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKey,
                                              final PKCS8EncodedKeySpec pPrivateKey) throws OceanusException {
            try {
                PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                SPHINCSPrivateKeyParameters myParms = new SPHINCSPrivateKeyParameters(ASN1OctetString.getInstance(myInfo.parsePrivateKey()).getOctets());
                BouncySPHINCSPrivateKey myPrivate = new BouncySPHINCSPrivateKey(getKeySpec(), myParms);
                BouncySPHINCSPublicKey myPublic = derivePublicKey(pPublicKey);
                return new BouncyKeyPair(myPublic, myPrivate);
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            BouncySPHINCSPublicKey myPublicKey = BouncySPHINCSPublicKey.class.cast(getPublicKey(pKeyPair));
            SPHINCSPublicKeyParameters myParms = myPublicKey.getPublicKey();
            SubjectPublicKeyInfo myInfo = new SubjectPublicKeyInfo(theAlgorithmId, myParms.getKeyData());
            byte[] myBytes = KeyUtil.getEncodedSubjectPublicKeyInfo(myInfo);
            return new X509EncodedKeySpec(myBytes);
        }

        @Override
        public BouncyKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            BouncySPHINCSPublicKey myPublic = derivePublicKey(pEncodedKey);
            return new BouncyKeyPair(myPublic);
        }

        /**
         * Derive public key from encoded.
         * @param pEncodedKey the encoded key
         * @return the public key
         * @throws OceanusException on error
         */
        private BouncySPHINCSPublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
            SPHINCSPublicKeyParameters myParms = new SPHINCSPublicKeyParameters(myInfo.getPublicKeyData().getBytes());
            return new BouncySPHINCSPublicKey(getKeySpec(), myParms);
        }
    }

    /**
     * BouncyCastle Rainbow KeyPair generator.
     */
    public static class BouncyRainbowKeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Generator.
         */
        private final RainbowKeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        protected BouncyRainbowKeyPairGenerator(final BouncyFactory pFactory,
                                                final GordianAsymKeySpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            theGenerator = new RainbowKeyPairGenerator();
            KeyGenerationParameters myParams = new RainbowKeyGenerationParameters(getRandom(), new RainbowParameters());
            theGenerator.init(myParams);
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            BouncyRainbowPublicKey myPublic = new BouncyRainbowPublicKey(getKeySpec(), RainbowPublicKeyParameters.class.cast(myPair.getPublic()));
            BouncyRainbowPrivateKey myPrivate = new BouncyRainbowPrivateKey(getKeySpec(), RainbowPrivateKeyParameters.class.cast(myPair.getPrivate()));
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        protected PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) {
            BouncyRainbowPrivateKey myPrivateKey = BouncyRainbowPrivateKey.class.cast(getPrivateKey(pKeyPair));
            RainbowPrivateKeyParameters myParms = myPrivateKey.getPrivateKey();
            BCRainbowPrivateKey myKey = new BCRainbowPrivateKey(myParms);
            return new PKCS8EncodedKeySpec(myKey.getEncoded());
        }

        @Override
        protected BouncyKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKey,
                                              final PKCS8EncodedKeySpec pPrivateKey) throws OceanusException {
            try {
                PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                RainbowPrivateKey myKey = RainbowPrivateKey.getInstance(myInfo.parsePrivateKey());
                BouncyRainbowPrivateKey myPrivate = new BouncyRainbowPrivateKey(getKeySpec(),
                        new RainbowPrivateKeyParameters(myKey.getInvA1(), myKey.getB1(), myKey.getInvA2(), myKey.getB2(), myKey.getVi(), myKey.getLayers()));
                BouncyRainbowPublicKey myPublic = derivePublicKey(pPublicKey);
                return new BouncyKeyPair(myPublic, myPrivate);
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) {
            BouncyRainbowPublicKey myPublicKey = BouncyRainbowPublicKey.class.cast(getPublicKey(pKeyPair));
            RainbowPublicKeyParameters myParms = myPublicKey.getPublicKey();
            BCRainbowPublicKey myKey = new BCRainbowPublicKey(myParms);
            return new X509EncodedKeySpec(myKey.getEncoded());
        }

        @Override
        public BouncyKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            BouncyRainbowPublicKey myPublic = derivePublicKey(pEncodedKey);
            return new BouncyKeyPair(myPublic);
        }

        /**
         * Derive public key from encoded.
         * @param pEncodedKey the encoded key
         * @return the public key
         * @throws OceanusException on error
         */
        private BouncyRainbowPublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            try {
                SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
                RainbowPublicKey myKey = RainbowPublicKey.getInstance(myInfo.parsePublicKey());
                RainbowPublicKeyParameters myParms = new RainbowPublicKeyParameters(myKey.getDocLength(),
                        myKey.getCoeffQuadratic(), myKey.getCoeffSingular(), myKey.getCoeffScalar());
                return new BouncyRainbowPublicKey(getKeySpec(), myParms);
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }
    }

    /**
     * BouncyCastle McEliece KeyPair generator.
     */
    public static class BouncyMcElieceKeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Digest String.
         */
        private static final String MCELIECE_DIGEST = McElieceCCA2KeyGenParameterSpec.SHA256;

        /**
         * Generator.
         */
        private final AsymmetricCipherKeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        protected BouncyMcElieceKeyPairGenerator(final BouncyFactory pFactory,
                                                 final GordianAsymKeySpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            theGenerator = isCCA2()
                                    ? new McElieceCCA2KeyPairGenerator()
                                    : new McElieceKeyPairGenerator();
            KeyGenerationParameters myParams = isCCA2()
                                                        ? new McElieceCCA2KeyGenerationParameters(getRandom(), new McElieceCCA2Parameters(MCELIECE_DIGEST))
                                                        : new McElieceKeyGenerationParameters(getRandom(), new McElieceParameters(new SHA256Digest()));
            theGenerator.init(myParams);
        }

        /**
         * Is this a CCA2 keyType?
         * @return true/false
         */
        private boolean isCCA2() {
            return getKeySpec().getMcElieceType().isCCA2();
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            BouncyMcEliecePublicKey myPublic = new BouncyMcEliecePublicKey(getKeySpec(), myPair.getPublic());
            BouncyMcEliecePrivateKey myPrivate = new BouncyMcEliecePrivateKey(getKeySpec(), myPair.getPrivate());
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        protected PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) {
            BouncyMcEliecePrivateKey myPrivateKey = BouncyMcEliecePrivateKey.class.cast(getPrivateKey(pKeyPair));
            if (isCCA2()) {
                McElieceCCA2PrivateKeyParameters myParms = myPrivateKey.getPrivateCCA2Key();
                BCMcElieceCCA2PrivateKey myKey = new BCMcElieceCCA2PrivateKey(myParms);
                return new PKCS8EncodedKeySpec(myKey.getEncoded());
            } else {
                McEliecePrivateKeyParameters myParms = myPrivateKey.getPrivateStdKey();
                BCMcEliecePrivateKey myKey = new BCMcEliecePrivateKey(myParms);
                return new PKCS8EncodedKeySpec(myKey.getEncoded());
            }
        }

        @Override
        protected BouncyKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKey,
                                              final PKCS8EncodedKeySpec pPrivateKey) throws OceanusException {
            try {
                PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                if (isCCA2()) {
                    McElieceCCA2PrivateKey myKey = McElieceCCA2PrivateKey.getInstance(myInfo.parsePrivateKey());
                    BouncyMcEliecePrivateKey myPrivate = new BouncyMcEliecePrivateKey(getKeySpec(),
                            new McElieceCCA2PrivateKeyParameters(myKey.getN(), myKey.getK(), myKey.getField(), myKey.getGoppaPoly(),
                                    myKey.getP(), MCELIECE_DIGEST));
                    BouncyMcEliecePublicKey myPublic = derivePublicKey(pPublicKey);
                    return new BouncyKeyPair(myPublic, myPrivate);
                } else {
                    McEliecePrivateKey myKey = McEliecePrivateKey.getInstance(myInfo.parsePrivateKey());
                    BouncyMcEliecePrivateKey myPrivate = new BouncyMcEliecePrivateKey(getKeySpec(),
                            new McEliecePrivateKeyParameters(myKey.getN(), myKey.getK(), myKey.getField(), myKey.getGoppaPoly(),
                                    myKey.getP1(), myKey.getP2(), myKey.getSInv()));
                    BouncyMcEliecePublicKey myPublic = derivePublicKey(pPublicKey);
                    return new BouncyKeyPair(myPublic, myPrivate);
                }
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) {
            BouncyMcEliecePublicKey myPublicKey = BouncyMcEliecePublicKey.class.cast(getPublicKey(pKeyPair));
            if (isCCA2()) {
                McElieceCCA2PublicKeyParameters myParms = myPublicKey.getPublicCCA2Key();
                BCMcElieceCCA2PublicKey myKey = new BCMcElieceCCA2PublicKey(myParms);
                return new X509EncodedKeySpec(myKey.getEncoded());
            } else {
                McEliecePublicKeyParameters myParms = myPublicKey.getPublicStdKey();
                BCMcEliecePublicKey myKey = new BCMcEliecePublicKey(myParms);
                return new X509EncodedKeySpec(myKey.getEncoded());
            }
        }

        @Override
        public BouncyKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            BouncyMcEliecePublicKey myPublic = derivePublicKey(pEncodedKey);
            return new BouncyKeyPair(myPublic);
        }

        /**
         * Derive public key from encoded.
         * @param pEncodedKey the encoded key
         * @return the public key
         * @throws OceanusException on error
         */
        private BouncyMcEliecePublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            try {
                SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
                if (isCCA2()) {
                    McElieceCCA2PublicKey myKey = McElieceCCA2PublicKey.getInstance(myInfo.parsePublicKey());
                    McElieceCCA2PublicKeyParameters myParms = new McElieceCCA2PublicKeyParameters(myKey.getN(),
                            myKey.getT(), myKey.getG(), MCELIECE_DIGEST);
                    return new BouncyMcEliecePublicKey(getKeySpec(), myParms);
                } else {
                    McEliecePublicKey myKey = McEliecePublicKey.getInstance(myInfo.parsePublicKey());
                    McEliecePublicKeyParameters myParms = new McEliecePublicKeyParameters(myKey.getN(),
                            myKey.getT(), myKey.getG());
                    return new BouncyMcEliecePublicKey(getKeySpec(), myParms);
                }
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }
    }

    /**
     * BouncyCastle NewHope KeyPair generator.
     */
    public static class BouncyNewHopeKeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Generator.
         */
        private final NHKeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        protected BouncyNewHopeKeyPairGenerator(final BouncyFactory pFactory,
                                                final GordianAsymKeySpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            theGenerator = new NHKeyPairGenerator();
            KeyGenerationParameters myParams = new KeyGenerationParameters(getRandom(), GordianModulus.MOD1024.getModulus());
            theGenerator.init(myParams);
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            BouncyNewHopePublicKey myPublic = new BouncyNewHopePublicKey(getKeySpec(), NHPublicKeyParameters.class.cast(myPair.getPublic()));
            BouncyNewHopePrivateKey myPrivate = new BouncyNewHopePrivateKey(getKeySpec(), NHPrivateKeyParameters.class.cast(myPair.getPrivate()));
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        protected PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) {
            BouncyNewHopePrivateKey myPrivateKey = BouncyNewHopePrivateKey.class.cast(getPrivateKey(pKeyPair));
            NHPrivateKeyParameters myParms = myPrivateKey.getPrivateKey();
            BCNHPrivateKey myKey = new BCNHPrivateKey(myParms);
            return new PKCS8EncodedKeySpec(myKey.getEncoded());
        }

        @Override
        protected BouncyKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKey,
                                              final PKCS8EncodedKeySpec pPrivateKey) throws OceanusException {
            try {
                PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                BCNHPrivateKey myKey = new BCNHPrivateKey(myInfo);
                BouncyNewHopePrivateKey myPrivate = new BouncyNewHopePrivateKey(getKeySpec(), new NHPrivateKeyParameters(myKey.getSecretData()));
                BouncyNewHopePublicKey myPublic = derivePublicKey(pPublicKey);
                return new BouncyKeyPair(myPublic, myPrivate);
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) {
            BouncyNewHopePublicKey myPublicKey = BouncyNewHopePublicKey.class.cast(getPublicKey(pKeyPair));
            NHPublicKeyParameters myParms = myPublicKey.getPublicKey();
            BCNHPublicKey myKey = new BCNHPublicKey(myParms);
            return new X509EncodedKeySpec(myKey.getEncoded());
        }

        @Override
        public BouncyKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pEncodedKey) {
            BouncyNewHopePublicKey myPublic = derivePublicKey(pEncodedKey);
            return new BouncyKeyPair(myPublic);
        }

        /**
         * Derive public key from encoded.
         * @param pEncodedKey the encoded key
         * @return the public key
         */
        private BouncyNewHopePublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) {
            SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
            NHPublicKeyParameters myParms = new NHPublicKeyParameters(myInfo.getPublicKeyData().getBytes());
            return new BouncyNewHopePublicKey(getKeySpec(), myParms);
        }
    }
}
