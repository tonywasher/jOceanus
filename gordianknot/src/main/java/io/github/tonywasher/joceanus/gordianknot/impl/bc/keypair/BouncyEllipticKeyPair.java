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
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair.BouncyPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair.BouncyPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianLogicException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreElliptic;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECNamedDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;

/**
 * EllipticCurve KeyPair classes.
 */
public final class BouncyEllipticKeyPair {
    /**
     * Private constructor.
     */
    private BouncyEllipticKeyPair() {
    }

    /**
     * Bouncy Elliptic PublicKey.
     */
    public static class BouncyECPublicKey
            extends BouncyPublicKey<ECPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncyECPublicKey(final GordianKeyPairSpec pKeySpec,
                          final ECPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final ECPublicKeyParameters myThis = getPublicKey();
            final ECPublicKeyParameters myThat = (ECPublicKeyParameters) pThat;

            /* Compare keys */
            return compareKeys(myThis, myThat);
        }

        /**
         * CompareKeys.
         *
         * @param pFirst  the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final ECPublicKeyParameters pFirst,
                                           final ECPublicKeyParameters pSecond) {
            return pFirst.getQ().equals(pSecond.getQ())
                    && pFirst.getParameters().equals(pSecond.getParameters());
        }
    }

    /**
     * Bouncy Elliptic PrivateKey.
     */
    public static class BouncyECPrivateKey
            extends BouncyPrivateKey<ECPrivateKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncyECPrivateKey(final GordianKeyPairSpec pKeySpec,
                           final ECPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final ECPrivateKeyParameters myThis = getPrivateKey();
            final ECPrivateKeyParameters myThat = (ECPrivateKeyParameters) pThat;

            /* Compare keys */
            return compareKeys(myThis, myThat);
        }

        /**
         * CompareKeys.
         *
         * @param pFirst  the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final ECPrivateKeyParameters pFirst,
                                           final ECPrivateKeyParameters pSecond) {
            return pFirst.getD().equals(pSecond.getD())
                    && pFirst.getParameters().equals(pSecond.getParameters());
        }
    }

    /**
     * BouncyCastle Elliptic KeyPair generator.
     */
    public static class BouncyECKeyPairGenerator
            extends BouncyKeyPairGenerator<ECPrivateKeyParameters, ECPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         * @throws GordianException on error
         */
        BouncyECKeyPairGenerator(final GordianBaseFactory pFactory,
                                 final GordianKeyPairSpec pKeySpec) throws GordianException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Lookup the parameters */
            final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeySpec;
            final GordianCoreElliptic myElliptic = myKeySpec.getElliptic();
            final String myCurve = myElliptic.getCurveName();
            final X9ECParameters x9 = myElliptic.hasCustomCurve()
                    ? ECUtil.getNamedCurveByName(myCurve)
                    : ECNamedCurveTable.getByName(myCurve);
            if (x9 == null) {
                throw new GordianLogicException("Invalid KeySpec - " + pKeySpec);
            }

            /* Initialise the generator */
            final ASN1ObjectIdentifier myOid = ECUtil.getNamedCurveOid(myCurve);
            final ECNamedDomainParameters myDomain = new ECNamedDomainParameters(myOid, x9.getCurve(), x9.getG(), x9.getN(), x9.getH(), x9.getSeed());
            final ECKeyGenerationParameters myParams = new ECKeyGenerationParameters(myDomain, getRandom());

            /* Create and initialise the generator */
            setGenerator(new ECKeyPairGenerator(), myParams);
            setFactorySet(BouncyStdKeyFactorySet.INSTANCE);
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
