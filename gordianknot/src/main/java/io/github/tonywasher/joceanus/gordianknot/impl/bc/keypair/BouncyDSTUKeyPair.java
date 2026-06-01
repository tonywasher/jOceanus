/*
 * GordianKnot: Security Suite
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPair;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyEllipticKeyPair.BouncyECPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyEllipticKeyPair.BouncyECPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianIOException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.GordianKeyPairValidity;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpec;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.sec.ECPrivateKey;
import org.bouncycastle.asn1.ua.DSTU4145NamedCurves;
import org.bouncycastle.asn1.ua.DSTU4145PointEncoder;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.generators.DSTU4145KeyPairGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
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
     * Private constructor.
     */
    private BouncyDSTUKeyPair() {
    }

    /**
     * BouncyCastle DSTU KeyPair generator.
     */
    public static class BouncyDSTUKeyPairGenerator
            extends BouncyKeyPairGenerator<ECPrivateKeyParameters, ECPublicKeyParameters> {
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
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyDSTUKeyPairGenerator(final GordianBaseFactory pFactory,
                                   final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Determine domain */
            final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeySpec;
            final String myCurveName = myKeySpec.getElliptic().getCurveName();
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

            /* Create and initialise the generator */
            setGenerator(new DSTU4145KeyPairGenerator(), myParams);
            setFactorySet(BouncyStdKeyFactorySet.INSTANCE);
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
            final BouncyECPublicKey myPublic = deriveThePublicKey(pPublicKey);
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
            final BouncyECPublicKey myPublic = deriveThePublicKey(pEncodedKey);
            return new BouncyKeyPair(myPublic);
        }

        /**
         * Derive public key from encoded.
         *
         * @param pEncodedKey the encoded key
         * @return the public key
         * @throws GordianException on error
         */
        private BouncyECPublicKey deriveThePublicKey(final X509EncodedKeySpec pEncodedKey) throws GordianException {
            /* Check the keySpecs */
            checkKeySpec(pEncodedKey);

            /* derive publicKey */
            final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
            final ECPublicKeyParameters myParms = deriveFromPubKeyInfo(myInfo);
            return new BouncyECPublicKey(getKeySpec(), myParms);
        }

        /**
         * Derive Public Key parameters from SubjectPublicKeyInfo. (extracted from BouncyCastle initialiser)
         *
         * @param pKeyInfo the keyInfo
         * @return the PrivateKeyParameters
         * @throws GordianException on error
         */
        private ECPublicKeyParameters deriveFromPubKeyInfo(final SubjectPublicKeyInfo pKeyInfo) throws GordianException {
            try {
                final ASN1BitString bits = pKeyInfo.getPublicKeyData();
                final ASN1OctetString key = (ASN1OctetString) ASN1Primitive.fromByteArray(bits.getBytes());
                final byte[] keyEnc = key.getOctets();
                final ECCurve curve = theDomain.getCurve();
                return new ECPublicKeyParameters(DSTU4145PointEncoder.decodePoint(curve, keyEnc), theDomain);
            } catch (IOException ex) {
                throw new GordianIOException("error recovering public key", ex);
            }
        }

        /**
         * Derive Private Key parameters from PrivateKeyInfo. (extracted from BouncyCastle initialiser)
         *
         * @param pKeyInfo the keyInfo
         * @return the PrivateKeyParameters
         * @throws GordianException on error
         */
        private ECPrivateKeyParameters deriveFromPrivKeyInfo(final PrivateKeyInfo pKeyInfo) throws GordianException {
            try {
                final ASN1Encodable privKey = pKeyInfo.parsePrivateKey();
                final BigInteger myD = privKey instanceof ASN1Integer myInt
                        ? myInt.getPositiveValue()
                        : ECPrivateKey.getInstance(privKey).getKey();
                return new ECPrivateKeyParameters(myD, theDomain);
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        BouncyECPrivateKey newPrivateKey(final AsymmetricKeyParameter pThat) {
            return new BouncyECPrivateKey(getKeySpec(), (ECPrivateKeyParameters) pThat);
        }

        @Override
        BouncyECPublicKey newPublicKey(final AsymmetricKeyParameter pThat) {
            return new BouncyECPublicKey(getKeySpec(), (ECPublicKeyParameters) pThat);
        }
    }
}
