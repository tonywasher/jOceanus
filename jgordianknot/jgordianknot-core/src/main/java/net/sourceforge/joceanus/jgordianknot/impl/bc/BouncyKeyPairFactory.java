/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2020 Tony Washer
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

import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianLMSKeySpec.GordianHSSKeySpec;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyDHKeyPair.BouncyDHKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyDSAKeyPair.BouncyDSAKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyDSTUKeyPair.BouncyDSTUKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyEdDSAKeyPair.BouncyEd25519KeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyEdDSAKeyPair.BouncyEd448KeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyElGamalKeyPair.BouncyElGamalKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyEllipticKeyPair.BouncyECKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyGOSTKeyPair.BouncyGOSTKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyLMSKeyPair.BouncyHSSKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyLMSKeyPair.BouncyLMSKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyMcElieceKeyPair.BouncyMcElieceCCA2KeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyMcElieceKeyPair.BouncyMcElieceKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyNewHopeKeyPair.BouncyNewHopeKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyQTESLAKeyPair.BouncyQTESLAKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyRSAKeyPair.BouncyRSAKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyRainbowKeyPair.BouncyRainbowKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncySPHINCSKeyPair.BouncySPHINCSKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyXDHKeyPair.BouncyX25519KeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyXDHKeyPair.BouncyX448KeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyXMSSKeyPair.BouncyXMSSKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyXMSSKeyPair.BouncyXMSSMTKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypair.GordianCoreKeyPairFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypairset.GordianCoreKeyPairSetFactory;
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
        setKeyPairSetFactory(new GordianCoreKeyPairSetFactory(this));
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
    public BouncyKeyPairGenerator getKeyPairGenerator(final GordianKeyPairSpec pKeySpec) throws OceanusException {
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
            case SPHINCS:
                return new BouncySPHINCSKeyPairGenerator(getFactory(), pKeySpec);
            case RAINBOW:
                return new BouncyRainbowKeyPairGenerator(getFactory(), pKeySpec);
            case MCELIECE:
                return pKeySpec.getMcElieceKeySpec().isCCA2()
                       ? new BouncyMcElieceCCA2KeyPairGenerator(getFactory(), pKeySpec)
                       : new BouncyMcElieceKeyPairGenerator(getFactory(), pKeySpec);
            case NEWHOPE:
                return new BouncyNewHopeKeyPairGenerator(getFactory(), pKeySpec);
            case XMSS:
                return pKeySpec.getXMSSKeySpec().isMT()
                       ? new BouncyXMSSMTKeyPairGenerator(getFactory(), pKeySpec)
                       : new BouncyXMSSKeyPairGenerator(getFactory(), pKeySpec);
            case QTESLA:
                return new BouncyQTESLAKeyPairGenerator(getFactory(), pKeySpec);
            case LMS:
                return pKeySpec.getSubKeyType() instanceof GordianHSSKeySpec
                       ? new BouncyHSSKeyPairGenerator(getFactory(), pKeySpec)
                       : new BouncyLMSKeyPairGenerator(getFactory(), pKeySpec);
            default:
                throw new GordianDataException(GordianCoreFactory.getInvalidText(pKeySpec.getKeyPairType()));
        }
    }

}
