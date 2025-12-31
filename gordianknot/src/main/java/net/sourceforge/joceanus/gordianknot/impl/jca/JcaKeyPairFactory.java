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
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.keypair.GordianCoreKeyPairFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.keystore.GordianCoreKeyStoreFactory;
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

import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Jca KeyPair Factory.
 */
public class JcaKeyPairFactory
    extends GordianCoreKeyPairFactory {
    /**
     * KeyPairGenerator Cache.
     */
    private final Map<GordianKeyPairSpec, JcaKeyPairGenerator> theCache;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    JcaKeyPairFactory(final JcaFactory pFactory) {
        /* Initialise underlying class */
        super(pFactory);

        /* Create the cache */
        theCache = new HashMap<>();

        /* Create factories */
        setSignatureFactory(new JcaSignatureFactory(pFactory));
        setAgreementFactory(new JcaAgreementFactory(pFactory));
        setXAgreementFactory(new JcaXAgreementFactory(pFactory));
        setEncryptorFactory(new JcaEncryptorFactory(pFactory));
        setKeyStoreFactory(new GordianCoreKeyStoreFactory(this));
    }

    @Override
    public JcaFactory getFactory() {
        return (JcaFactory) super.getFactory();
    }

    @Override
    public JcaSignatureFactory getSignatureFactory() {
        return (JcaSignatureFactory) super.getSignatureFactory();
    }

    @Override
    public JcaAgreementFactory getAgreementFactory() {
        return (JcaAgreementFactory) super.getAgreementFactory();
    }

    @Override
    public JcaEncryptorFactory getEncryptorFactory() {
        return (JcaEncryptorFactory) super.getEncryptorFactory();
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
                return new JcaRSAKeyPairGenerator(getFactory(), pKeySpec);
            case ELGAMAL:
                return new JcaElGamalKeyPairGenerator(getFactory(), pKeySpec);
            case EC:
            case SM2:
            case DSTU4145:
            case GOST2012:
                return new JcaECKeyPairGenerator(getFactory(), pKeySpec);
            case DSA:
                return new JcaDSAKeyPairGenerator(getFactory(), pKeySpec);
            case XDH:
            case EDDSA:
                return new JcaEdKeyPairGenerator(getFactory(), pKeySpec);
            case DH:
                return new JcaDHKeyPairGenerator(getFactory(), pKeySpec);
            case NEWHOPE:
                return new JcaNewHopeKeyPairGenerator(getFactory(), pKeySpec);
            case SLHDSA:
                return new JcaSLHDSAKeyPairGenerator(getFactory(), pKeySpec);
            case CMCE:
                return new JcaCMCEKeyPairGenerator(getFactory(), pKeySpec);
            case FRODO:
                return new JcaFrodoKeyPairGenerator(getFactory(), pKeySpec);
            case SABER:
                return new JcaSABERKeyPairGenerator(getFactory(), pKeySpec);
            case MLKEM:
                return new JcaMLKEMKeyPairGenerator(getFactory(), pKeySpec);
            case MLDSA:
                return new JcaMLDSAKeyPairGenerator(getFactory(), pKeySpec);
            case HQC:
                return new JcaHQCKeyPairGenerator(getFactory(), pKeySpec);
            case BIKE:
                return new JcaBIKEKeyPairGenerator(getFactory(), pKeySpec);
            case NTRU:
                return new JcaNTRUKeyPairGenerator(getFactory(), pKeySpec);
            case NTRUPRIME:
                return pKeySpec.getNTRUPrimeKeySpec().getType() == GordianNTRUPrimeType.NTRUL
                        ? new JcaNTRULPrimeKeyPairGenerator(getFactory(), pKeySpec)
                        : new JcaSNTRUPrimeKeyPairGenerator(getFactory(), pKeySpec);
            case FALCON:
                return new JcaFalconKeyPairGenerator(getFactory(), pKeySpec);
            case MAYO:
                return new JcaMayoKeyPairGenerator(getFactory(), pKeySpec);
            case SNOVA:
                return new JcaSnovaKeyPairGenerator(getFactory(), pKeySpec);
            case PICNIC:
                return new JcaPicnicKeyPairGenerator(getFactory(), pKeySpec);
            case XMSS:
                return new JcaXMSSKeyPairGenerator(getFactory(), pKeySpec);
            case LMS:
                return new JcaLMSKeyPairGenerator(getFactory(), pKeySpec);
            default:
                throw new GordianDataException(GordianCoreFactory.getInvalidText(pKeySpec.getKeyPairType()));
        }
    }

    /**
     * Create the BouncyCastle KeyFactory via JCA.
     * @param pAlgorithm the Algorithm
     * @param postQuantum is this a postQuantum algorithm?
     * @return the KeyFactory
     * @throws GordianException on error
     */
    static KeyFactory getJavaKeyFactory(final String pAlgorithm,
                                        final boolean postQuantum) throws GordianException {
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
     * @throws GordianException on error
     */
    static KeyPairGenerator getJavaKeyPairGenerator(final String pAlgorithm,
                                                    final boolean postQuantum) throws GordianException {
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
