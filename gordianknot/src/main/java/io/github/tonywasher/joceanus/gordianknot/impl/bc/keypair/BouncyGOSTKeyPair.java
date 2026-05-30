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

import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyEllipticKeyPair.BouncyECPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyEllipticKeyPair.BouncyECPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpec;
import org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECGOST3410Parameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECNamedDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jcajce.spec.GOST3410ParameterSpec;

/**
 * GOST KeyPair classes.
 */
public final class BouncyGOSTKeyPair {
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
    private BouncyGOSTKeyPair() {
    }

    /**
     * BouncyCastle GOST KeyPair generator.
     */
    public static class BouncyGOSTKeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyGOSTKeyPairGenerator(final GordianBaseFactory pFactory,
                                   final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Determine domain */
            final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeySpec;
            final String myCurve = myKeySpec.getElliptic().getCurveName();
            final GOST3410ParameterSpec mySpec = new GOST3410ParameterSpec(myCurve);
            final X9ECParameters x9 = ECGOST3410NamedCurves.getByNameX9(myCurve);
            final ECDomainParameters myDomain = new ECNamedDomainParameters(mySpec.getPublicKeyParamSet(), x9);

            /* initialise */
            final ECKeyGenerationParameters myParams = new ECKeyGenerationParameters(
                    new ECGOST3410Parameters(myDomain, mySpec.getPublicKeyParamSet(), mySpec.getDigestParamSet(),
                            mySpec.getEncryptionParamSet()), getRandom());

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
