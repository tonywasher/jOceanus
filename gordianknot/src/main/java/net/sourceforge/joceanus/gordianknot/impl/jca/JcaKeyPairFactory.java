/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.jca;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairType;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianNTRUPrimeSpec.GordianNTRUPrimeType;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianBaseData;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.keypair.GordianCoreKeyPairFactory;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaBIKEKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaCMCEKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaDHKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaDSAKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaECKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaEdKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaElGamalKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaFalconKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaFrodoKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaHQCKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaLMSKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaMLDSAKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaMLKEMKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaMayoKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaNTRUKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaNTRULPrimeKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaNewHopeKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaPicnicKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaRSAKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaSABERKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaSLHDSAKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaSNTRUPrimeKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaSnovaKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaKeyPairGenerator.JcaXMSSKeyPairGenerator;

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
     * @param pKeySpec the keySpec
     * @return the KeyGenerator
     * @throws GordianException on error
     */
    private JcaKeyPairGenerator getJcaKeyPairGenerator(final GordianKeyPairSpec pKeySpec) throws GordianException {
        switch (pKeySpec.getKeyPairType()) {
            case RSA:
                return new JcaRSAKeyPairGenerator(theFactory, pKeySpec);
            case ELGAMAL:
                return new JcaElGamalKeyPairGenerator(theFactory, pKeySpec);
            case EC:
            case SM2:
            case DSTU4145:
            case GOST2012:
                return new JcaECKeyPairGenerator(theFactory, pKeySpec);
            case DSA:
                return new JcaDSAKeyPairGenerator(theFactory, pKeySpec);
            case XDH:
            case EDDSA:
                return new JcaEdKeyPairGenerator(theFactory, pKeySpec);
            case DH:
                return new JcaDHKeyPairGenerator(theFactory, pKeySpec);
            case NEWHOPE:
                return new JcaNewHopeKeyPairGenerator(theFactory, pKeySpec);
            case SLHDSA:
                return new JcaSLHDSAKeyPairGenerator(theFactory, pKeySpec);
            case CMCE:
                return new JcaCMCEKeyPairGenerator(theFactory, pKeySpec);
            case FRODO:
                return new JcaFrodoKeyPairGenerator(theFactory, pKeySpec);
            case SABER:
                return new JcaSABERKeyPairGenerator(theFactory, pKeySpec);
            case MLKEM:
                return new JcaMLKEMKeyPairGenerator(theFactory, pKeySpec);
            case MLDSA:
                return new JcaMLDSAKeyPairGenerator(theFactory, pKeySpec);
            case HQC:
                return new JcaHQCKeyPairGenerator(theFactory, pKeySpec);
            case BIKE:
                return new JcaBIKEKeyPairGenerator(theFactory, pKeySpec);
            case NTRU:
                return new JcaNTRUKeyPairGenerator(theFactory, pKeySpec);
            case NTRUPRIME:
                return pKeySpec.getNTRUPrimeKeySpec().getType() == GordianNTRUPrimeType.NTRUL
                        ? new JcaNTRULPrimeKeyPairGenerator(theFactory, pKeySpec)
                        : new JcaSNTRUPrimeKeyPairGenerator(theFactory, pKeySpec);
            case FALCON:
                return new JcaFalconKeyPairGenerator(theFactory, pKeySpec);
            case MAYO:
                return new JcaMayoKeyPairGenerator(theFactory, pKeySpec);
            case SNOVA:
                return new JcaSnovaKeyPairGenerator(theFactory, pKeySpec);
            case PICNIC:
                return new JcaPicnicKeyPairGenerator(theFactory, pKeySpec);
            case XMSS:
                return new JcaXMSSKeyPairGenerator(theFactory, pKeySpec);
            case LMS:
                return new JcaLMSKeyPairGenerator(theFactory, pKeySpec);
            default:
                throw new GordianDataException(GordianBaseData.getInvalidText(pKeySpec.getKeyPairType()));
        }
    }
}
