/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.bc;

import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairType;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianLMSKeySpec.GordianHSSKeySpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianNTRUPrimeSpec.GordianNTRUPrimeType;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyBIKEKeyPair.BouncyBIKEKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyCMCEKeyPair.BouncyCMCEKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyDHKeyPair.BouncyDHKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyMLDSAKeyPair.BouncyMLDSAKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyDSAKeyPair.BouncyDSAKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyDSTUKeyPair.BouncyDSTUKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyEdDSAKeyPair.BouncyEd25519KeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyEdDSAKeyPair.BouncyEd448KeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyElGamalKeyPair.BouncyElGamalKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyEllipticKeyPair.BouncyECKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyFALCONKeyPair.BouncyFALCONKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyFrodoKeyPair.BouncyFrodoKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyGOSTKeyPair.BouncyGOSTKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyHQCKeyPair.BouncyHQCKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyLMSKeyPair.BouncyHSSKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyLMSKeyPair.BouncyLMSKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyMLKEMKeyPair.BouncyMLKEMKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyNTRUKeyPair.BouncyNTRUKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyNTRULPrimeKeyPair.BouncyNTRULPrimeKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyPICNICKeyPair.BouncyPICNICKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyRSAKeyPair.BouncyRSAKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyRainbowKeyPair.BouncyRainbowKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncySABERKeyPair.BouncySABERKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncySNTRUPrimeKeyPair.BouncySNTRUPrimeKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncySLHDSAKeyPair.BouncySLHDSAKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyXDHKeyPair.BouncyX25519KeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyXDHKeyPair.BouncyX448KeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyXMSSKeyPair.BouncyXMSSKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyXMSSKeyPair.BouncyXMSSMTKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.keypair.GordianCoreKeyPairFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.keystore.GordianCoreKeyStoreFactory;
import net.sourceforge.joceanus.tethys.OceanusException;

import java.util.HashMap;
import java.util.Map;

/**
 * BouncyCastle KeyPair Factory.
 */
public class BouncyKeyPairFactory
        extends GordianCoreKeyPairFactory {
    /**
     * KeyPairGenerator Cache.
     */
    private final Map<GordianKeyPairSpec, BouncyKeyPairGenerator> theCache;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    BouncyKeyPairFactory(final BouncyFactory pFactory) {
        /* Initialise underlying class */
        super(pFactory);

        /* Create the cache */
        theCache = new HashMap<>();

        /* Create factories */
        setSignatureFactory(new BouncySignatureFactory(pFactory));
        setAgreementFactory(new BouncyAgreementFactory(pFactory));
        setEncryptorFactory(new BouncyEncryptorFactory(pFactory));
        setKeyStoreFactory(new GordianCoreKeyStoreFactory(this));
    }

    @Override
    public BouncyFactory getFactory() {
        return (BouncyFactory) super.getFactory();
    }

    @Override
    public BouncySignatureFactory getSignatureFactory() {
        return (BouncySignatureFactory) super.getSignatureFactory();
    }

    @Override
    public BouncyAgreementFactory getAgreementFactory() {
        return (BouncyAgreementFactory) super.getAgreementFactory();
    }

    @Override
    public BouncyEncryptorFactory getEncryptorFactory() {
        return (BouncyEncryptorFactory) super.getEncryptorFactory();
    }

    @Override
    public GordianKeyPairGenerator getKeyPairGenerator(final GordianKeyPairSpec pKeySpec) throws OceanusException {
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
     * @throws OceanusException on error
     */
    private BouncyKeyPairGenerator getBCKeyPairGenerator(final GordianKeyPairSpec pKeySpec) throws OceanusException {
        switch (pKeySpec.getKeyPairType()) {
            case RSA:
                return new BouncyRSAKeyPairGenerator(getFactory(), pKeySpec);
            case ELGAMAL:
                return new BouncyElGamalKeyPairGenerator(getFactory(), pKeySpec);
            case EC:
            case SM2:
                return new BouncyECKeyPairGenerator(getFactory(), pKeySpec);
            case DSTU4145:
                return new BouncyDSTUKeyPairGenerator(getFactory(), pKeySpec);
            case GOST2012:
                return new BouncyGOSTKeyPairGenerator(getFactory(), pKeySpec);
            case XDH:
                return pKeySpec.getEdwardsElliptic().is25519()
                       ?  new BouncyX25519KeyPairGenerator(getFactory(), pKeySpec)
                       :  new BouncyX448KeyPairGenerator(getFactory(), pKeySpec);
            case EDDSA:
                return pKeySpec.getEdwardsElliptic().is25519()
                       ? new BouncyEd25519KeyPairGenerator(getFactory(), pKeySpec)
                       : new BouncyEd448KeyPairGenerator(getFactory(), pKeySpec);
            case DSA:
                return new BouncyDSAKeyPairGenerator(getFactory(), pKeySpec);
            case DH:
                return new BouncyDHKeyPairGenerator(getFactory(), pKeySpec);
            case SLHDSA:
                return new BouncySLHDSAKeyPairGenerator(getFactory(), pKeySpec);
            case CMCE:
                return new BouncyCMCEKeyPairGenerator(getFactory(), pKeySpec);
            case FRODO:
                return new BouncyFrodoKeyPairGenerator(getFactory(), pKeySpec);
            case SABER:
                return new BouncySABERKeyPairGenerator(getFactory(), pKeySpec);
            case MLKEM:
                return new BouncyMLKEMKeyPairGenerator(getFactory(), pKeySpec);
            case MLDSA:
                return new BouncyMLDSAKeyPairGenerator(getFactory(), pKeySpec);
            case HQC:
                return new BouncyHQCKeyPairGenerator(getFactory(), pKeySpec);
            case BIKE:
                return new BouncyBIKEKeyPairGenerator(getFactory(), pKeySpec);
            case NTRU:
                return new BouncyNTRUKeyPairGenerator(getFactory(), pKeySpec);
            case NTRUPRIME:
                return pKeySpec.getNTRUPrimeKeySpec().getType() == GordianNTRUPrimeType.NTRUL
                            ? new BouncyNTRULPrimeKeyPairGenerator(getFactory(), pKeySpec)
                            : new BouncySNTRUPrimeKeyPairGenerator(getFactory(), pKeySpec);
            case FALCON:
                return new BouncyFALCONKeyPairGenerator(getFactory(), pKeySpec);
            case PICNIC:
                return new BouncyPICNICKeyPairGenerator(getFactory(), pKeySpec);
            case RAINBOW:
                return new BouncyRainbowKeyPairGenerator(getFactory(), pKeySpec);
            case XMSS:
                return pKeySpec.getXMSSKeySpec().isMT()
                       ? new BouncyXMSSMTKeyPairGenerator(getFactory(), pKeySpec)
                       : new BouncyXMSSKeyPairGenerator(getFactory(), pKeySpec);
            case LMS:
                return pKeySpec.getSubKeyType() instanceof GordianHSSKeySpec
                       ? new BouncyHSSKeyPairGenerator(getFactory(), pKeySpec)
                       : new BouncyLMSKeyPairGenerator(getFactory(), pKeySpec);
            default:
                throw new GordianDataException(GordianCoreFactory.getInvalidText(pKeySpec.getKeyPairType()));
        }
    }
}
