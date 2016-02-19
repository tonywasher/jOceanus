/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2014 Tony Washer
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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jgordianknot/jgordianknot-core/src/main/java/net/sourceforge/joceanus/jgordianknot/crypto/CipherSetRecipe.java $
 * $Revision: 647 $
 * $Author: Tony $
 * $Date: 2015-11-04 08:58:02 +0000 (Wed, 04 Nov 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.crypto.bc;

import java.math.BigInteger;

import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X9ECParameters;
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

import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyECPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyECPublicKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyRSAPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyRSAPublicKey;

/**
 * BouncyCastle KeyPair generator.
 */
public abstract class BouncyKeyPairGenerator
        extends GordianKeyPairGenerator {
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
            ECDomainParameters myDomain = new ECDomainParameters(x9.getCurve(), x9.getG(), x9.getN(), x9.getH(), x9.getSeed());
            ECKeyGenerationParameters myParams = new ECKeyGenerationParameters(myDomain, getRandom());
            theGenerator.init(myParams);
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            BouncyECPublicKey myPublic = new BouncyECPublicKey(getKeyType(), ECPublicKeyParameters.class.cast(myPair.getPublic()));
            BouncyECPrivateKey myPrivate = new BouncyECPrivateKey(getKeyType(), ECPrivateKeyParameters.class.cast(myPair.getPrivate()));
            return new BouncyKeyPair(myPublic, myPrivate);
        }
    }
}
