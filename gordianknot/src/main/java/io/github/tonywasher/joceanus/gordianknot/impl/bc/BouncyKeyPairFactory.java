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
package io.github.tonywasher.joceanus.gordianknot.impl.bc;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianLMSKeySpec.GordianHSSKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianNTRUPrimeSpec.GordianNTRUPrimeType;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyBIKEKeyPair.BouncyBIKEKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyCMCEKeyPair.BouncyCMCEKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyDHKeyPair.BouncyDHKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyDSAKeyPair.BouncyDSAKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyDSTUKeyPair.BouncyDSTUKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyEdDSAKeyPair.BouncyEd25519KeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyEdDSAKeyPair.BouncyEd448KeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyElGamalKeyPair.BouncyElGamalKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyEllipticKeyPair.BouncyECKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyFalconKeyPair.BouncyFalconKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyFrodoKeyPair.BouncyFrodoKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyGOSTKeyPair.BouncyGOSTKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyHQCKeyPair.BouncyHQCKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyLMSKeyPair.BouncyHSSKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyLMSKeyPair.BouncyLMSKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyMLDSAKeyPair.BouncyMLDSAKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyMLKEMKeyPair.BouncyMLKEMKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyMayoKeyPair.BouncyMayoKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyNTRUKeyPair.BouncyNTRUKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyNTRULPrimeKeyPair.BouncyNTRULPrimeKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyNewHopeKeyPair.BouncyNewHopeKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyPicnicKeyPair.BouncyPicnicKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyRSAKeyPair.BouncyRSAKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncySABERKeyPair.BouncySABERKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncySLHDSAKeyPair.BouncySLHDSAKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncySNTRUPrimeKeyPair.BouncySNTRUPrimeKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncySnovaKeyPair.BouncySnovaKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyXDHKeyPair.BouncyX25519KeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyXDHKeyPair.BouncyX448KeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyXMSSKeyPair.BouncyXMSSKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyXMSSKeyPair.BouncyXMSSMTKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseData;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.GordianCoreKeyPairFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * BouncyCastle KeyPair Factory.
 */
public class BouncyKeyPairFactory
        extends GordianCoreKeyPairFactory {
    /**
     * Factory.
     */
    private final GordianBaseFactory theFactory;

    /**
     * KeyPairGenerator Cache.
     */
    private final Map<GordianKeyPairSpec, BouncyKeyPairGenerator> theCache;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    BouncyKeyPairFactory(final GordianBaseFactory pFactory) {
        /* Initialise underlying class */
        super(pFactory);
        theFactory = pFactory;

        /* Create the cache */
        theCache = new HashMap<>();
    }

    @Override
    public GordianKeyPairGenerator getKeyPairGenerator(final GordianKeyPairSpec pKeySpec) throws GordianException {
        /* Handle composite keyPairGenerator */
        if (GordianKeyPairType.COMPOSITE.equals(pKeySpec.getKeyPairType())) {
            return super.getKeyPairGenerator(pKeySpec);
        }

        /* Look up in the cache */
        BouncyKeyPairGenerator myGenerator = theCache.get(pKeySpec);
        if (myGenerator == null) {
            /* Check the keySpec */
            checkAsymKeySpec(pKeySpec);

            /* Create the new generator */
            myGenerator = getBCKeyPairGenerator(pKeySpec);

            /* Add to cache */
            theCache.put(pKeySpec, myGenerator);
        }
        return myGenerator;
    }

    /**
     * Create the BouncyCastle KeyPairGenerator.
     *
     * @param pKeySpec the keySpec
     * @return the KeyGenerator
     * @throws GordianException on error
     */
    private BouncyKeyPairGenerator getBCKeyPairGenerator(final GordianKeyPairSpec pKeySpec) throws GordianException {
        switch (pKeySpec.getKeyPairType()) {
            case RSA:
                return new BouncyRSAKeyPairGenerator(theFactory, pKeySpec);
            case ELGAMAL:
                return new BouncyElGamalKeyPairGenerator(theFactory, pKeySpec);
            case EC:
            case SM2:
                return new BouncyECKeyPairGenerator(theFactory, pKeySpec);
            case DSTU4145:
                return new BouncyDSTUKeyPairGenerator(theFactory, pKeySpec);
            case GOST2012:
                return new BouncyGOSTKeyPairGenerator(theFactory, pKeySpec);
            case XDH:
                return pKeySpec.getEdwardsElliptic().is25519()
                        ? new BouncyX25519KeyPairGenerator(theFactory, pKeySpec)
                        : new BouncyX448KeyPairGenerator(theFactory, pKeySpec);
            case EDDSA:
                return pKeySpec.getEdwardsElliptic().is25519()
                        ? new BouncyEd25519KeyPairGenerator(theFactory, pKeySpec)
                        : new BouncyEd448KeyPairGenerator(theFactory, pKeySpec);
            case DSA:
                return new BouncyDSAKeyPairGenerator(theFactory, pKeySpec);
            case DH:
                return new BouncyDHKeyPairGenerator(theFactory, pKeySpec);
            case SLHDSA:
                return new BouncySLHDSAKeyPairGenerator(theFactory, pKeySpec);
            case CMCE:
                return new BouncyCMCEKeyPairGenerator(theFactory, pKeySpec);
            case FRODO:
                return new BouncyFrodoKeyPairGenerator(theFactory, pKeySpec);
            case SABER:
                return new BouncySABERKeyPairGenerator(theFactory, pKeySpec);
            case MLKEM:
                return new BouncyMLKEMKeyPairGenerator(theFactory, pKeySpec);
            case MLDSA:
                return new BouncyMLDSAKeyPairGenerator(theFactory, pKeySpec);
            case HQC:
                return new BouncyHQCKeyPairGenerator(theFactory, pKeySpec);
            case BIKE:
                return new BouncyBIKEKeyPairGenerator(theFactory, pKeySpec);
            case NTRU:
                return new BouncyNTRUKeyPairGenerator(theFactory, pKeySpec);
            case NTRUPRIME:
                return pKeySpec.getNTRUPrimeKeySpec().getType() == GordianNTRUPrimeType.NTRUL
                        ? new BouncyNTRULPrimeKeyPairGenerator(theFactory, pKeySpec)
                        : new BouncySNTRUPrimeKeyPairGenerator(theFactory, pKeySpec);
            case FALCON:
                return new BouncyFalconKeyPairGenerator(theFactory, pKeySpec);
            case MAYO:
                return new BouncyMayoKeyPairGenerator(theFactory, pKeySpec);
            case SNOVA:
                return new BouncySnovaKeyPairGenerator(theFactory, pKeySpec);
            case PICNIC:
                return new BouncyPicnicKeyPairGenerator(theFactory, pKeySpec);
            case NEWHOPE:
                return new BouncyNewHopeKeyPairGenerator(theFactory, pKeySpec);
            case XMSS:
                return pKeySpec.getXMSSKeySpec().isMT()
                        ? new BouncyXMSSMTKeyPairGenerator(theFactory, pKeySpec)
                        : new BouncyXMSSKeyPairGenerator(theFactory, pKeySpec);
            case LMS:
                return pKeySpec.getSubKeyType() instanceof GordianHSSKeySpec
                        ? new BouncyHSSKeyPairGenerator(theFactory, pKeySpec)
                        : new BouncyLMSKeyPairGenerator(theFactory, pKeySpec);
            default:
                throw new GordianDataException(GordianBaseData.getInvalidText(pKeySpec.getKeyPairType()));
        }
    }
}
