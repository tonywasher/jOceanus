/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2016 Tony Washer
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

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.bouncycastle.asn1.sec.ECPrivateKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ECPoint;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.math.ec.ECCurve;

import net.sourceforge.joceanus.jgordianknot.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianPublicKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyECPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyECPublicKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyRSAPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyRSAPublicKey;
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
     * Constructor.
     * @param pFactory the Security Factory
     * @param pKeyType the keyType
     */
    protected BouncyKeyPairGenerator(final BouncyFactory pFactory,
                                     final GordianAsymKeyType pKeyType) {
        super(pFactory, pKeyType);
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
         * RSA strength.
         */
        private static final int RSA_STRENGTH = 2048;

        /**
         * RSA certainty.
         */
        private static final int RSA_CERTAINTY = 128;

        /**
         * Generator.
         */
        private RSAKeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         */
        protected BouncyRSAKeyPairGenerator(final BouncyFactory pFactory) {
            /* Initialise underlying class */
            super(pFactory, GordianAsymKeyType.RSA);

            /* Create and initialise the generator */
            theGenerator = new RSAKeyPairGenerator();
            RSAKeyGenerationParameters myParams = new RSAKeyGenerationParameters(RSA_EXPONENT, getRandom(), RSA_STRENGTH, RSA_CERTAINTY);
            theGenerator.init(myParams);
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            BouncyRSAPublicKey myPublic = new BouncyRSAPublicKey(RSAKeyParameters.class.cast(myPair.getPublic()));
            BouncyRSAPrivateKey myPrivate = new BouncyRSAPrivateKey(RSAPrivateCrtKeyParameters.class.cast(myPair.getPrivate()));
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        protected PKCS8EncodedKeySpec getPKCS8Encoding(final GordianPrivateKey pPrivateKey) throws OceanusException {
            BouncyRSAPrivateKey myPrivateKey = BouncyRSAPrivateKey.class.cast(pPrivateKey);
            RSAPrivateCrtKeyParameters myParms = myPrivateKey.getPrivateKey();
            byte[] myBytes = KeyUtil.getEncodedPrivateKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE),
                    new RSAPrivateKey(myParms.getModulus(), myParms.getPublicExponent(), myParms.getExponent(),
                            myParms.getP(), myParms.getQ(), myParms.getDP(), myParms.getDQ(), myParms.getQInv()));
            return new PKCS8EncodedKeySpec(myBytes);
        }

        @Override
        protected BouncyRSAPrivateKey derivePrivateKey(final PKCS8EncodedKeySpec pEncodedKey) throws OceanusException {
            try {
                PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pEncodedKey.getEncoded());
                RSAPrivateKey myKey;
                myKey = RSAPrivateKey.getInstance(myInfo.parsePrivateKey());
                RSAPrivateCrtKeyParameters myParms = new RSAPrivateCrtKeyParameters(myKey.getModulus(), myKey.getPublicExponent(), myKey.getPrivateExponent(),
                        myKey.getPrime1(), myKey.getPrime2(), myKey.getExponent1(), myKey.getExponent2(), myKey.getCoefficient());
                return new BouncyRSAPrivateKey(myParms);
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public X509EncodedKeySpec getX509Encoding(final GordianPublicKey pPublicKey) throws OceanusException {
            BouncyRSAPublicKey myPublicKey = BouncyRSAPublicKey.class.cast(pPublicKey);
            RSAKeyParameters myParms = myPublicKey.getPublicKey();
            byte[] myBytes = KeyUtil.getEncodedSubjectPublicKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE),
                    new RSAPublicKey(myParms.getModulus(), myParms.getExponent()));
            return new X509EncodedKeySpec(myBytes);
        }

        @Override
        public BouncyRSAPublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            try {
                SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
                RSAPublicKey myKey = RSAPublicKey.getInstance(myInfo.parsePublicKey());
                RSAKeyParameters myParms = new RSAKeyParameters(false, myKey.getModulus(), myKey.getPublicExponent());
                return new BouncyRSAPublicKey(myParms);
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
         * Generator.
         */
        private ECKeyPairGenerator theGenerator;

        /**
         * Domain.
         */
        private ECDomainParameters theDomain;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeyType the keyType
         */
        protected BouncyECKeyPairGenerator(final BouncyFactory pFactory,
                                           final GordianAsymKeyType pKeyType) {
            /* Initialise underlying class */
            super(pFactory, pKeyType);

            /* Create and initialise the generator */
            theGenerator = new ECKeyPairGenerator();
            X9ECParameters x9 = ECNamedCurveTable.getByName(pKeyType.getCurve());
            theDomain = new ECDomainParameters(x9.getCurve(), x9.getG(), x9.getN(), x9.getH(), x9.getSeed());
            ECKeyGenerationParameters myParams = new ECKeyGenerationParameters(theDomain, getRandom());
            theGenerator.init(myParams);
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            BouncyECPublicKey myPublic = new BouncyECPublicKey(getKeyType(), ECPublicKeyParameters.class.cast(myPair.getPublic()));
            BouncyECPrivateKey myPrivate = new BouncyECPrivateKey(getKeyType(), ECPrivateKeyParameters.class.cast(myPair.getPrivate()));
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        protected PKCS8EncodedKeySpec getPKCS8Encoding(final GordianPrivateKey pPrivateKey) throws OceanusException {
            BouncyECPrivateKey myPrivateKey = BouncyECPrivateKey.class.cast(pPrivateKey);
            ECPrivateKeyParameters myParms = myPrivateKey.getPrivateKey();
            X962Parameters myX962Parms = new X962Parameters(ECUtil.getNamedCurveOid(getKeyType().getCurve()));
            BigInteger myOrder = myParms.getParameters().getCurve().getOrder();
            ECPrivateKey myKey = new ECPrivateKey(myOrder.bitLength(), myParms.getD(), myX962Parms);
            byte[] myBytes = KeyUtil.getEncodedPrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, myX962Parms.toASN1Primitive()),
                    myKey.toASN1Primitive());
            return new PKCS8EncodedKeySpec(myBytes);
        }

        @Override
        protected BouncyECPrivateKey derivePrivateKey(final PKCS8EncodedKeySpec pEncodedKey) throws OceanusException {
            try {
                PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pEncodedKey.getEncoded());
                ECPrivateKey myKey = ECPrivateKey.getInstance(myInfo.parsePrivateKey());
                ECPrivateKeyParameters myParms = new ECPrivateKeyParameters(myKey.getKey(), theDomain);
                return new BouncyECPrivateKey(getKeyType(), myParms);
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public X509EncodedKeySpec getX509Encoding(final GordianPublicKey pPublicKey) throws OceanusException {
            BouncyECPublicKey myPublicKey = BouncyECPublicKey.class.cast(pPublicKey);
            ECPublicKeyParameters myParms = myPublicKey.getPublicKey();
            X962Parameters myX962Parms = new X962Parameters(ECUtil.getNamedCurveOid(getKeyType().getCurve()));
            ECCurve myCurve = theDomain.getCurve();
            ASN1OctetString p = (ASN1OctetString) new X9ECPoint(myCurve.createPoint(myParms.getQ().getAffineXCoord().toBigInteger(),
                    myParms.getQ().getAffineYCoord().toBigInteger())).toASN1Primitive();
            SubjectPublicKeyInfo myInfo = new SubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, myX962Parms), p.getOctets());
            byte[] myBytes = KeyUtil.getEncodedSubjectPublicKeyInfo(myInfo);
            return new X509EncodedKeySpec(myBytes);
        }

        @Override
        public BouncyECPublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
            X9ECPoint myKey = new X9ECPoint(theDomain.getCurve(), new DEROctetString(myInfo.getPublicKeyData().getBytes()));
            ECPublicKeyParameters myParms = new ECPublicKeyParameters(myKey.getPoint(), theDomain);
            return new BouncyECPublicKey(getKeyType(), myParms);
        }
    }
}
