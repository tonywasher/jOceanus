/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.bc;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairType;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianLMSKeySpec.GordianHSSKeySpec;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyBIKEKeyPair.BouncyBIKEKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyCMCEKeyPair.BouncyCMCEKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyDHKeyPair.BouncyDHKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyDILITHIUMKeyPair.BouncyDILITHIUMKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyDSAKeyPair.BouncyDSAKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyDSTUKeyPair.BouncyDSTUKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyEdDSAKeyPair.BouncyEd25519KeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyEdDSAKeyPair.BouncyEd448KeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyElGamalKeyPair.BouncyElGamalKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyEllipticKeyPair.BouncyECKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyFALCONKeyPair.BouncyFALCONKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyFrodoKeyPair.BouncyFrodoKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyGOSTKeyPair.BouncyGOSTKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyKYBERKeyPair.BouncyKYBERKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyLMSKeyPair.BouncyHSSKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyLMSKeyPair.BouncyLMSKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyNTRUKeyPair.BouncyNTRUKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyNTRULPrimeKeyPair.BouncyNTRULPrimeKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyPICNICKeyPair.BouncyPICNICKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyRSAKeyPair.BouncyRSAKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncySABERKeyPair.BouncySABERKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncySNTRUPrimeKeyPair.BouncySNTRUPrimeKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncySPHINCSPlusKeyPair.BouncySPHINCSPlusKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyXDHKeyPair.BouncyX25519KeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyXDHKeyPair.BouncyX448KeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyXMSSKeyPair.BouncyXMSSKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyXMSSKeyPair.BouncyXMSSMTKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypair.GordianCoreKeyPairFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianCoreKeyStoreFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;

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
            case SPHINCSPLUS:
                return new BouncySPHINCSPlusKeyPairGenerator(getFactory(), pKeySpec);
            case CMCE:
                return new BouncyCMCEKeyPairGenerator(getFactory(), pKeySpec);
            case FRODO:
                return new BouncyFrodoKeyPairGenerator(getFactory(), pKeySpec);
            case SABER:
                return new BouncySABERKeyPairGenerator(getFactory(), pKeySpec);
            case KYBER:
                return new BouncyKYBERKeyPairGenerator(getFactory(), pKeySpec);
            case DILITHIUM:
                return new BouncyDILITHIUMKeyPairGenerator(getFactory(), pKeySpec);
            case BIKE:
                return new BouncyBIKEKeyPairGenerator(getFactory(), pKeySpec);
            case NTRU:
                return new BouncyNTRUKeyPairGenerator(getFactory(), pKeySpec);
            case NTRULPRIME:
                return new BouncyNTRULPrimeKeyPairGenerator(getFactory(), pKeySpec);
            case SNTRUPRIME:
                return new BouncySNTRUPrimeKeyPairGenerator(getFactory(), pKeySpec);
            case FALCON:
                return new BouncyFALCONKeyPairGenerator(getFactory(), pKeySpec);
            case PICNIC:
                return new BouncyPICNICKeyPairGenerator(getFactory(), pKeySpec);
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
