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
package io.github.tonywasher.joceanus.gordianknot.impl.jca;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNTRUPrimeSpec.GordianNTRUPrimeType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseData;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.GordianCoreKeyPairFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaBIKEKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaCMCEKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaDHKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaDSAKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaECKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaEdKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaElGamalKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaFalconKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaFrodoKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaHQCKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaLMSKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaMLDSAKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaMLKEMKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaMayoKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaNTRUKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaNTRULPrimeKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaNTRUPlusKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaNewHopeKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaPicnicKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaRSAKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaSABERKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaSLHDSAKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaSNTRUPrimeKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaSnovaKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaXMSSKeyPairGenerator;

import java.util.HashMap;
import java.util.Map;

/**
 * Jca KeyPair Factory.
 */
public class JcaKeyPairFactory
        extends GordianCoreKeyPairFactory {
    /**
     * Factory.
     */
    private final GordianBaseFactory theFactory;

    /**
     * KeyPairGenerator Cache.
     */
    private final Map<GordianKeyPairSpec, JcaKeyPairGenerator> theCache;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    JcaKeyPairFactory(final GordianBaseFactory pFactory) {
        /* Initialize underlying class */
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
        JcaKeyPairGenerator myGenerator = theCache.get(pKeySpec);
        if (myGenerator == null) {
            /* Check the keySpec */
            checkAsymKeySpec(pKeySpec);

            /* Create the new generator */
            myGenerator = getJcaKeyPairGenerator(pKeySpec);

            /* Add to cache */
            theCache.put(pKeySpec, myGenerator);
        }
        return myGenerator;
    }

    /**
     * Create the Jca KeyPairGenerator.
     *
     * @param pKeySpec the keySpec
     * @return the KeyGenerator
     * @throws GordianException on error
     */
    private JcaKeyPairGenerator getJcaKeyPairGenerator(final GordianKeyPairSpec pKeySpec) throws GordianException {
        final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeySpec;
        return switch (pKeySpec.getKeyPairType()) {
            case RSA -> new JcaRSAKeyPairGenerator(theFactory, pKeySpec);
            case ELGAMAL -> new JcaElGamalKeyPairGenerator(theFactory, pKeySpec);
            case EC, SM2, DSTU, GOST -> new JcaECKeyPairGenerator(theFactory, pKeySpec);
            case DSA -> new JcaDSAKeyPairGenerator(theFactory, pKeySpec);
            case XDH, EDDSA -> new JcaEdKeyPairGenerator(theFactory, pKeySpec);
            case DH -> new JcaDHKeyPairGenerator(theFactory, pKeySpec);
            case NEWHOPE -> new JcaNewHopeKeyPairGenerator(theFactory, pKeySpec);
            case SLHDSA -> new JcaSLHDSAKeyPairGenerator(theFactory, pKeySpec);
            case CMCE -> new JcaCMCEKeyPairGenerator(theFactory, pKeySpec);
            case FRODO -> new JcaFrodoKeyPairGenerator(theFactory, pKeySpec);
            case SABER -> new JcaSABERKeyPairGenerator(theFactory, pKeySpec);
            case MLKEM -> new JcaMLKEMKeyPairGenerator(theFactory, pKeySpec);
            case MLDSA -> new JcaMLDSAKeyPairGenerator(theFactory, pKeySpec);
            case HQC -> new JcaHQCKeyPairGenerator(theFactory, pKeySpec);
            case BIKE -> new JcaBIKEKeyPairGenerator(theFactory, pKeySpec);
            case NTRU -> new JcaNTRUKeyPairGenerator(theFactory, pKeySpec);
            case NTRUPLUS -> new JcaNTRUPlusKeyPairGenerator(theFactory, pKeySpec);
            case NTRUPRIME -> myKeySpec.getNTRUPrimeSpec().getType() == GordianNTRUPrimeType.NTRUL
                    ? new JcaNTRULPrimeKeyPairGenerator(theFactory, pKeySpec)
                    : new JcaSNTRUPrimeKeyPairGenerator(theFactory, pKeySpec);
            case FALCON -> new JcaFalconKeyPairGenerator(theFactory, pKeySpec);
            case MAYO -> new JcaMayoKeyPairGenerator(theFactory, pKeySpec);
            case SNOVA -> new JcaSnovaKeyPairGenerator(theFactory, pKeySpec);
            case PICNIC -> new JcaPicnicKeyPairGenerator(theFactory, pKeySpec);
            case XMSS -> new JcaXMSSKeyPairGenerator(theFactory, pKeySpec);
            case LMS -> new JcaLMSKeyPairGenerator(theFactory, pKeySpec);
            default -> throw new GordianDataException(GordianBaseData.getInvalidText(pKeySpec.getKeyPairType()));
        };
    }
}
