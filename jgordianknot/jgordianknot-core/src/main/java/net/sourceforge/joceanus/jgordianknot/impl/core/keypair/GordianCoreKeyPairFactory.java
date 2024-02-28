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
package net.sourceforge.joceanus.jgordianknot.impl.core.keypair;

import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementFactory;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptorFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianKeyPairFactory;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianBIKESpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianCMCESpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianDHGroup;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianDILITHIUMSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianDSAElliptic;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianDSAKeyType;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianDSTU4145Elliptic;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianFALCONSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianFRODOSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianGOSTElliptic;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianHQCSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKYBERSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairType;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianLMSKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianLMSKeySpec.GordianHSSKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianNTRUPrimeSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianNTRUSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianPICNICSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianRSAModulus;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianRainbowSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianSABERSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianSM2Elliptic;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianSPHINCSPlusSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianXMSSKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreFactory;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignatureFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * GordianKnot Core KeyPairFactory.
 */
public abstract class GordianCoreKeyPairFactory
        implements GordianKeyPairFactory {
    /**
     * KeyPairAlgId.
     */
    private static final GordianKeyPairAlgId KEYPAIR_ALG_ID = new GordianKeyPairAlgId();

    /**
     * KeyPairGenerator Cache.
     */
    private final Map<GordianKeyPairSpec, GordianCompositeKeyPairGenerator> theCache;

    /**
     * The factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * The signature factory.
     */
    private GordianSignatureFactory theSignatureFactory;

    /**
     * The agreement factory.
     */
    private GordianAgreementFactory theAgreementFactory;

    /**
     * The encryptor factory.
     */
    private GordianEncryptorFactory theEncryptorFactory;

    /**
     * The keyStore factory.
     */
    private GordianKeyStoreFactory theKeyStoreFactory;

    /**
     * Constructor.
     * @param pFactory the factory
     */
    protected GordianCoreKeyPairFactory(final GordianCoreFactory pFactory) {
        theFactory = pFactory;
        theCache = new HashMap<>();
    }

    @Override
    public GordianCoreFactory getFactory() {
        return theFactory;
    }

    @Override
    public GordianSignatureFactory getSignatureFactory() {
        return theSignatureFactory;
    }

    /**
     * Set the signature factory.
     * @param pFactory the factory
     */
    protected void setSignatureFactory(final GordianSignatureFactory pFactory) {
        theSignatureFactory = pFactory;
    }

    @Override
    public GordianAgreementFactory getAgreementFactory() {
        return theAgreementFactory;
    }

    /**
     * Set the agreement factory.
     * @param pFactory the factory
     */
    protected void setAgreementFactory(final GordianAgreementFactory pFactory) {
        theAgreementFactory = pFactory;
    }

    @Override
    public GordianEncryptorFactory getEncryptorFactory() {
        return theEncryptorFactory;
    }

    /**
     * Set the encryptor factory.
     * @param pFactory the factory
     */
    protected void setEncryptorFactory(final GordianEncryptorFactory pFactory) {
        theEncryptorFactory = pFactory;
    }

    @Override
    public GordianKeyStoreFactory getKeyStoreFactory() {
        return theKeyStoreFactory;
    }

    /**
     * Set the keyStore factory.
     * @param pFactory the factory
     */
    protected void setKeyStoreFactory(final GordianKeyStoreFactory pFactory) {
        theKeyStoreFactory = pFactory;
    }

    @Override
    public GordianKeyPairGenerator getKeyPairGenerator(final GordianKeyPairSpec pKeySpec) throws OceanusException {
        /* Look up in the cache */
        GordianCompositeKeyPairGenerator myGenerator = theCache.get(pKeySpec);
        if (myGenerator == null) {
            /* Check the keySpec */
            checkAsymKeySpec(pKeySpec);

            /* Create the new generator */
            myGenerator = new GordianCompositeKeyPairGenerator(this, pKeySpec);

            /* Add to cache */
            theCache.put(pKeySpec, myGenerator);
        }
        return myGenerator;
    }

    @Override
    public GordianKeyPairSpec determineKeyPairSpec(final PKCS8EncodedKeySpec pEncoded) throws OceanusException {
        return KEYPAIR_ALG_ID.determineKeyPairSpec(pEncoded);
    }

    @Override
    public GordianKeyPairSpec determineKeyPairSpec(final X509EncodedKeySpec pEncoded) throws OceanusException {
        return KEYPAIR_ALG_ID.determineKeyPairSpec(pEncoded);
    }

    /**
     * Check the asymKeySpec.
     * @param pKeySpec the asymKeySpec
     * @throws OceanusException on error
     */
    protected void checkAsymKeySpec(final GordianKeyPairSpec pKeySpec) throws OceanusException {
        /* Check validity of keySpec */
        if (pKeySpec == null || !pKeySpec.isValid()) {
            throw new GordianDataException(GordianCoreFactory.getInvalidText(pKeySpec));
        }
    }

    @Override
    public Predicate<GordianKeyPairSpec> supportedKeyPairSpecs() {
        return this::validAsymKeySpec;
    }

    /**
     * Valid keySpec.
     * @param pKeySpec the asymKeySpec
     * @return true/false
     */
    public boolean validAsymKeySpec(final GordianKeyPairSpec pKeySpec) {
        return pKeySpec != null && pKeySpec.isValid();
    }

    @Override
    public List<GordianKeyPairSpec> listAllSupportedKeyPairSpecs() {
        return listPossibleKeySpecs()
                .stream()
                .filter(supportedKeyPairSpecs())
                .collect(Collectors.toList());
    }

    @Override
    public List<GordianKeyPairSpec> listAllSupportedKeyPairSpecs(final GordianKeyPairType pKeyPairType) {
        return listPossibleKeySpecs()
                .stream()
                .filter(s -> pKeyPairType.equals(s.getKeyPairType()))
                .filter(supportedKeyPairSpecs())
                .collect(Collectors.toList());
    }

    @Override
    public List<GordianKeyPairSpec> listPossibleKeySpecs() {
        /* Create the list */
        final List<GordianKeyPairSpec> mySpecs = new ArrayList<>();

        /* Add RSA */
        EnumSet.allOf(GordianRSAModulus.class).forEach(m -> mySpecs.add(GordianKeyPairSpec.rsa(m)));

        /* Add DSA */
        EnumSet.allOf(GordianDSAKeyType.class).forEach(t -> mySpecs.add(GordianKeyPairSpec.dsa(t)));

        /* Add DH  */
        EnumSet.allOf(GordianDHGroup.class).forEach(g -> mySpecs.add(GordianKeyPairSpec.dh(g)));

        /* Add ElGamal  */
        EnumSet.allOf(GordianDHGroup.class).forEach(g -> mySpecs.add(GordianKeyPairSpec.elGamal(g)));

        /* Add EC */
        EnumSet.allOf(GordianDSAElliptic.class).forEach(c -> mySpecs.add(GordianKeyPairSpec.ec(c)));

        /* Add SM2 */
        EnumSet.allOf(GordianSM2Elliptic.class).forEach(c -> mySpecs.add(GordianKeyPairSpec.sm2(c)));

        /* Add GOST2012 */
        EnumSet.allOf(GordianGOSTElliptic.class).forEach(c -> mySpecs.add(GordianKeyPairSpec.gost2012(c)));

        /* Add DSTU4145 */
        EnumSet.allOf(GordianDSTU4145Elliptic.class).forEach(c -> mySpecs.add(GordianKeyPairSpec.dstu4145(c)));

        /* Add Ed25519/Ed448 */
        mySpecs.add(GordianKeyPairSpec.ed448());
        mySpecs.add(GordianKeyPairSpec.ed25519());

        /* Add X25519/X448 */
        mySpecs.add(GordianKeyPairSpec.x448());
        mySpecs.add(GordianKeyPairSpec.x25519());

        /* Add XMSS/XMSSMT */
        GordianXMSSKeySpec.listPossibleKeySpecs().forEach(t -> mySpecs.add(new GordianKeyPairSpec(GordianKeyPairType.XMSS, t)));

        /* Add LMS */
        GordianLMSKeySpec.listPossibleKeySpecs().forEach(t -> {
            mySpecs.add(GordianKeyPairSpec.lms(t));
            for (int i = 2; i < GordianHSSKeySpec.MAX_DEPTH; i++) {
                mySpecs.add(GordianKeyPairSpec.hss(t, i));
            }
        });

        /* Add SPHINCSPlus/CMCE/Frodo/Saber */
        EnumSet.allOf(GordianSPHINCSPlusSpec.class).forEach(t -> mySpecs.add(GordianKeyPairSpec.sphincsPlus(t)));
        EnumSet.allOf(GordianCMCESpec.class).forEach(t -> mySpecs.add(GordianKeyPairSpec.cmce(t)));
        EnumSet.allOf(GordianFRODOSpec.class).forEach(t -> mySpecs.add(GordianKeyPairSpec.frodo(t)));
        EnumSet.allOf(GordianSABERSpec.class).forEach(t -> mySpecs.add(GordianKeyPairSpec.saber(t)));
        EnumSet.allOf(GordianKYBERSpec.class).forEach(t -> mySpecs.add(GordianKeyPairSpec.kyber(t)));
        EnumSet.allOf(GordianDILITHIUMSpec.class).forEach(t -> mySpecs.add(GordianKeyPairSpec.dilithium(t)));
        EnumSet.allOf(GordianHQCSpec.class).forEach(t -> mySpecs.add(GordianKeyPairSpec.hqc(t)));
        EnumSet.allOf(GordianBIKESpec.class).forEach(t -> mySpecs.add(GordianKeyPairSpec.bike(t)));
        EnumSet.allOf(GordianNTRUSpec.class).forEach(t -> mySpecs.add(GordianKeyPairSpec.ntru(t)));
        EnumSet.allOf(GordianFALCONSpec.class).forEach(t -> mySpecs.add(GordianKeyPairSpec.falcon(t)));
        EnumSet.allOf(GordianPICNICSpec.class).forEach(t -> mySpecs.add(GordianKeyPairSpec.picnic(t)));
        EnumSet.allOf(GordianRainbowSpec.class).forEach(t -> mySpecs.add(GordianKeyPairSpec.rainbow(t)));

        /* Add NTRUPrime */
        GordianNTRUPrimeSpec.listPossibleKeySpecs().forEach(t -> mySpecs.add(GordianKeyPairSpec.ntruprime(t)));

        /* Return the list */
        return mySpecs;
    }
}
