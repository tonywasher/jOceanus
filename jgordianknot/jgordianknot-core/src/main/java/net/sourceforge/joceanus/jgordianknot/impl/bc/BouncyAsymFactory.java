/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2018 Tony Washer
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

import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyDHAsymKey.BouncyDHKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyDSAAsymKey.BouncyDSAKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyDSTUAsymKey.BouncyDSTUKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyEdDSAAsymKey.BouncyEd25519KeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyEdDSAAsymKey.BouncyEd448KeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyEllipticAsymKey.BouncyECKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyGOSTAsymKey.BouncyGOSTKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyMcElieceAsymKey.BouncyMcElieceCCA2KeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyMcElieceAsymKey.BouncyMcElieceKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyNewHopeAsymKey.BouncyNewHopeKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyQTESLAAsymKey.BouncyQTESLAKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyRSAAsymKey.BouncyRSAKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyRainbowAsymKey.BouncyRainbowKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncySPHINCSAsymKey.BouncySPHINCSKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyXDHAsymKey.BouncyX25519KeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyXDHAsymKey.BouncyX448KeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyXMSSAsymKey.BouncyXMSSKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyXMSSAsymKey.BouncyXMSSMTKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypair.GordianCoreAsymFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Jca Asymmetric Factory.
 */
public class BouncyAsymFactory
        extends GordianCoreAsymFactory {
    /**
     * KeyPairGenerator Cache.
     */
    final Map<GordianAsymKeySpec, BouncyKeyPairGenerator> theCache;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    public BouncyAsymFactory(final BouncyFactory pFactory) {
        /* Initialise underlying class */
        super(pFactory);

        /* Create the cache */
        theCache = new HashMap<>();

        /* Create factories */
        setSignatureFactory(new BouncySignatureFactory(pFactory));
        setAgreementFactory(new BouncyAgreementFactory(pFactory));
        setEncryptorFactory(new BouncyEncryptorFactory(pFactory));
    }

    @Override
    public BouncyFactory getFactory() { return (BouncyFactory) super.getFactory(); }

    @Override
    public BouncySignatureFactory getSignatureFactory() { return (BouncySignatureFactory) super.getSignatureFactory(); }

    @Override
    public BouncyAgreementFactory getAgreementFactory() { return (BouncyAgreementFactory) super.getAgreementFactory(); }

    @Override
    public BouncyEncryptorFactory getEncryptorFactory() { return (BouncyEncryptorFactory) super.getEncryptorFactory(); }

    @Override
    public BouncyKeyPairGenerator getKeyPairGenerator(final GordianAsymKeySpec pKeySpec) throws OceanusException {
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
    private BouncyKeyPairGenerator getBCKeyPairGenerator(final GordianAsymKeySpec pKeySpec) throws OceanusException {
        switch (pKeySpec.getKeyType()) {
            case RSA:
                return new BouncyRSAKeyPairGenerator(getFactory(), pKeySpec);
            case EC:
            case SM2:
                return new BouncyECKeyPairGenerator(getFactory(), pKeySpec);
            case DSTU4145:
                return new BouncyDSTUKeyPairGenerator(getFactory(), pKeySpec);
            case GOST2012:
                return new BouncyGOSTKeyPairGenerator(getFactory(), pKeySpec);
            case X25519:
                return new BouncyX25519KeyPairGenerator(getFactory(), pKeySpec);
            case X448:
                return new BouncyX448KeyPairGenerator(getFactory(), pKeySpec);
            case ED25519:
                return new BouncyEd25519KeyPairGenerator(getFactory(), pKeySpec);
            case ED448:
                return new BouncyEd448KeyPairGenerator(getFactory(), pKeySpec);
            case DSA:
                return new BouncyDSAKeyPairGenerator(getFactory(), pKeySpec);
            case DIFFIEHELLMAN:
                return new BouncyDHKeyPairGenerator(getFactory(), pKeySpec);
            case SPHINCS:
                return new BouncySPHINCSKeyPairGenerator(getFactory(), pKeySpec);
            case RAINBOW:
                return new BouncyRainbowKeyPairGenerator(getFactory(), pKeySpec);
            case MCELIECE:
                return pKeySpec.getMcElieceSpec().isCCA2()
                       ? new BouncyMcElieceCCA2KeyPairGenerator(getFactory(), pKeySpec)
                       : new BouncyMcElieceKeyPairGenerator(getFactory(), pKeySpec);
            case NEWHOPE:
                return new BouncyNewHopeKeyPairGenerator(getFactory(), pKeySpec);
            case XMSS:
                return new BouncyXMSSKeyPairGenerator(getFactory(), pKeySpec);
            case XMSSMT:
                return new BouncyXMSSMTKeyPairGenerator(getFactory(), pKeySpec);
            case QTESLA:
                return new BouncyQTESLAKeyPairGenerator(getFactory(), pKeySpec);
            default:
                throw new GordianDataException(BouncyFactory.getInvalidText(pKeySpec.getKeyType()));
        }
    }

}
