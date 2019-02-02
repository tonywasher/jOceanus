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
package net.sourceforge.joceanus.jgordianknot.impl.jca;

import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.KeyGenerator;

import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypair.GordianCoreAsymFactory;
import net.sourceforge.joceanus.jgordianknot.impl.jca.JcaKeyPairGenerator.JcaDHKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.jca.JcaKeyPairGenerator.JcaDSAKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.jca.JcaKeyPairGenerator.JcaECKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.jca.JcaKeyPairGenerator.JcaEdKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.jca.JcaKeyPairGenerator.JcaMcElieceKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.jca.JcaKeyPairGenerator.JcaNewHopeKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.jca.JcaKeyPairGenerator.JcaQTESLAKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.jca.JcaKeyPairGenerator.JcaRSAKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.jca.JcaKeyPairGenerator.JcaRainbowKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.jca.JcaKeyPairGenerator.JcaSPHINCSKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.jca.JcaKeyPairGenerator.JcaXMSSKeyPairGenerator;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Jca Asymmetric Factory.
 */
public class JcaAsymFactory
    extends GordianCoreAsymFactory {
    /**
     * KeyPairGenerator Cache.
     */
    private final Map<GordianAsymKeySpec, JcaKeyPairGenerator> theCache;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    JcaAsymFactory(final JcaFactory pFactory) {
        /* Initialise underlying class */
        super(pFactory);

        /* Create the cache */
        theCache = new HashMap<>();

        /* Create factories */
        setSignatureFactory(new JcaSignatureFactory(pFactory));
        setAgreementFactory(new JcaAgreementFactory(pFactory));
        setEncryptorFactory(new JcaEncryptorFactory(pFactory));
    }

    @Override
    public JcaFactory getFactory() { return (JcaFactory) super.getFactory(); }

    @Override
    public JcaSignatureFactory getSignatureFactory() { return (JcaSignatureFactory) super.getSignatureFactory(); }

    @Override
    public JcaAgreementFactory getAgreementFactory() { return (JcaAgreementFactory) super.getAgreementFactory(); }

    @Override
    public JcaEncryptorFactory getEncryptorFactory() { return (JcaEncryptorFactory) super.getEncryptorFactory(); }

    @Override
    public JcaKeyPairGenerator getKeyPairGenerator(final GordianAsymKeySpec pKeySpec) throws OceanusException {
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
     * @throws OceanusException on error
     */
    private JcaKeyPairGenerator getJcaKeyPairGenerator(final GordianAsymKeySpec pKeySpec) throws OceanusException {
        switch (pKeySpec.getKeyType()) {
            case RSA:
                return new JcaRSAKeyPairGenerator(getFactory(), pKeySpec);
            case EC:
            case SM2:
            case DSTU4145:
            case GOST2012:
                return new JcaECKeyPairGenerator(getFactory(), pKeySpec);
            case DSA:
                return new JcaDSAKeyPairGenerator(getFactory(), pKeySpec);
            case X25519:
            case X448:
            case ED25519:
            case ED448:
                return new JcaEdKeyPairGenerator(getFactory(), pKeySpec);
            case DIFFIEHELLMAN:
                return new JcaDHKeyPairGenerator(getFactory(), pKeySpec);
            case SPHINCS:
                return new JcaSPHINCSKeyPairGenerator(getFactory(), pKeySpec);
            case RAINBOW:
                return new JcaRainbowKeyPairGenerator(getFactory(), pKeySpec);
            case MCELIECE:
                return new JcaMcElieceKeyPairGenerator(getFactory(), pKeySpec);
            case NEWHOPE:
                return new JcaNewHopeKeyPairGenerator(getFactory(), pKeySpec);
            case XMSS:
            case XMSSMT:
                return new JcaXMSSKeyPairGenerator(getFactory(), pKeySpec);
            case QTESLA:
                return new JcaQTESLAKeyPairGenerator(getFactory(), pKeySpec);
            default:
                throw new GordianDataException(JcaFactory.getInvalidText(pKeySpec.getKeyType()));
        }
    }

    /**
     * Create the BouncyCastle KeyFactory via JCA.
     * @param pAlgorithm the Algorithm
     * @param postQuantum is this a postQuantum algorithm?
     * @return the KeyFactory
     * @throws OceanusException on error
     */
    static KeyFactory getJavaKeyFactory(final String pAlgorithm,
                                        final boolean postQuantum) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Return a KeyFactory for the algorithm */
            return KeyFactory.getInstance(pAlgorithm, postQuantum
                                                      ? JcaFactory.BCPQPROV
                                                      : JcaFactory.BCPROV);

            /* Catch exceptions */
        } catch (NoSuchAlgorithmException e) {
            /* Throw the exception */
            throw new GordianCryptoException("Failed to create KeyFactory", e);
        }
    }

    /**
     * Create the BouncyCastle KeyPairGenerator via JCA.
     * @param pAlgorithm the Algorithm
     * @param postQuantum is this a postQuantum algorithm?
     * @return the KeyPairGenerator
     * @throws OceanusException on error
     */
    static KeyPairGenerator getJavaKeyPairGenerator(final String pAlgorithm,
                                                    final boolean postQuantum) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Return a KeyPairGenerator for the algorithm */
            return KeyPairGenerator.getInstance(pAlgorithm, postQuantum
                                                            ? JcaFactory.BCPQPROV
                                                            : JcaFactory.BCPROV);

            /* Catch exceptions */
        } catch (NoSuchAlgorithmException e) {
            /* Throw the exception */
            throw new GordianCryptoException("Failed to create KeyPairGenerator", e);
        }
    }
}
