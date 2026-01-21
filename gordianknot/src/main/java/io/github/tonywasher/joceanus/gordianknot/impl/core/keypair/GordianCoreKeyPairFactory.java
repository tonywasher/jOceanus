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
package io.github.tonywasher.joceanus.gordianknot.impl.core.keypair;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianBIKESpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianCMCESpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianDHGroup;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianDSAElliptic;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianDSAKeyType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianDSTU4145Elliptic;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianFRODOSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianFalconSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianGOSTElliptic;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianHQCSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairFactory;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianLMSKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianLMSKeySpec.GordianHSSKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianMLDSASpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianMLKEMSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianMayoSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianNTRUPrimeSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianNTRUSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianPicnicSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianRSAModulus;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianSABERSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianSLHDSASpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianSM2Elliptic;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianSnovaSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianXMSSKeySpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseData;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;

import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

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
     * Constructor.
     *
     * @param pFactory the factory
     */
    protected GordianCoreKeyPairFactory(final GordianBaseFactory pFactory) {
        theCache = new HashMap<>();
    }

    @Override
    public GordianKeyPairGenerator getKeyPairGenerator(final GordianKeyPairSpec pKeySpec) throws GordianException {
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
    public GordianKeyPairSpec determineKeyPairSpec(final PKCS8EncodedKeySpec pEncoded) throws GordianException {
        return KEYPAIR_ALG_ID.determineKeyPairSpec(pEncoded);
    }

    @Override
    public GordianKeyPairSpec determineKeyPairSpec(final X509EncodedKeySpec pEncoded) throws GordianException {
        return KEYPAIR_ALG_ID.determineKeyPairSpec(pEncoded);
    }

    /**
     * Check the asymKeySpec.
     *
     * @param pKeySpec the asymKeySpec
     * @throws GordianException on error
     */
    protected void checkAsymKeySpec(final GordianKeyPairSpec pKeySpec) throws GordianException {
        /* Check validity of keySpec */
        if (pKeySpec == null || !pKeySpec.isValid()) {
            throw new GordianDataException(GordianBaseData.getInvalidText(pKeySpec));
        }
    }

    @Override
    public Predicate<GordianKeyPairSpec> supportedKeyPairSpecs() {
        return this::validAsymKeySpec;
    }

    /**
     * Valid keySpec.
     *
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
                .toList();
    }

    @Override
    public List<GordianKeyPairSpec> listAllSupportedKeyPairSpecs(final GordianKeyPairType pKeyPairType) {
        return listPossibleKeySpecs()
                .stream()
                .filter(s -> pKeyPairType.equals(s.getKeyPairType()))
                .filter(supportedKeyPairSpecs())
                .toList();
    }

    @Override
    public List<GordianKeyPairSpec> listPossibleKeySpecs() {
        /* Create the list */
        final List<GordianKeyPairSpec> mySpecs = new ArrayList<>();

        /* Add RSA */
        EnumSet.allOf(GordianRSAModulus.class).forEach(m -> mySpecs.add(GordianKeyPairSpecBuilder.rsa(m)));

        /* Add DSA */
        EnumSet.allOf(GordianDSAKeyType.class).forEach(t -> mySpecs.add(GordianKeyPairSpecBuilder.dsa(t)));

        /* Add DH  */
        EnumSet.allOf(GordianDHGroup.class).forEach(g -> mySpecs.add(GordianKeyPairSpecBuilder.dh(g)));

        /* Add ElGamal  */
        EnumSet.allOf(GordianDHGroup.class).forEach(g -> mySpecs.add(GordianKeyPairSpecBuilder.elGamal(g)));

        /* Add EC */
        EnumSet.allOf(GordianDSAElliptic.class).forEach(c -> mySpecs.add(GordianKeyPairSpecBuilder.ec(c)));

        /* Add SM2 */
        EnumSet.allOf(GordianSM2Elliptic.class).forEach(c -> mySpecs.add(GordianKeyPairSpecBuilder.sm2(c)));

        /* Add GOST2012 */
        EnumSet.allOf(GordianGOSTElliptic.class).forEach(c -> mySpecs.add(GordianKeyPairSpecBuilder.gost2012(c)));

        /* Add DSTU4145 */
        EnumSet.allOf(GordianDSTU4145Elliptic.class).forEach(c -> mySpecs.add(GordianKeyPairSpecBuilder.dstu4145(c)));

        /* Add Ed25519/Ed448 */
        mySpecs.add(GordianKeyPairSpecBuilder.ed448());
        mySpecs.add(GordianKeyPairSpecBuilder.ed25519());

        /* Add X25519/X448 */
        mySpecs.add(GordianKeyPairSpecBuilder.x448());
        mySpecs.add(GordianKeyPairSpecBuilder.x25519());

        /* Add NewHope */
        mySpecs.add(GordianKeyPairSpecBuilder.newHope());

        /* Add XMSS/XMSSMT */
        GordianXMSSKeySpec.listPossibleKeySpecs().forEach(t -> mySpecs.add(new GordianKeyPairSpec(GordianKeyPairType.XMSS, t)));

        /* Add LMS */
        GordianLMSKeySpec.listPossibleKeySpecs().forEach(t -> {
            mySpecs.add(GordianKeyPairSpecBuilder.lms(t));
            for (int i = 2; i < GordianHSSKeySpec.MAX_DEPTH; i++) {
                mySpecs.add(GordianKeyPairSpecBuilder.hss(t, i));
            }
        });

        /* Add SPHINCSPlus/CMCE/Frodo/Saber */
        EnumSet.allOf(GordianSLHDSASpec.class).forEach(t -> mySpecs.add(GordianKeyPairSpecBuilder.slhdsa(t)));
        EnumSet.allOf(GordianCMCESpec.class).forEach(t -> mySpecs.add(GordianKeyPairSpecBuilder.cmce(t)));
        EnumSet.allOf(GordianFRODOSpec.class).forEach(t -> mySpecs.add(GordianKeyPairSpecBuilder.frodo(t)));
        EnumSet.allOf(GordianSABERSpec.class).forEach(t -> mySpecs.add(GordianKeyPairSpecBuilder.saber(t)));
        EnumSet.allOf(GordianMLKEMSpec.class).forEach(t -> mySpecs.add(GordianKeyPairSpecBuilder.mlkem(t)));
        EnumSet.allOf(GordianMLDSASpec.class).forEach(t -> mySpecs.add(GordianKeyPairSpecBuilder.mldsa(t)));
        EnumSet.allOf(GordianHQCSpec.class).forEach(t -> mySpecs.add(GordianKeyPairSpecBuilder.hqc(t)));
        EnumSet.allOf(GordianBIKESpec.class).forEach(t -> mySpecs.add(GordianKeyPairSpecBuilder.bike(t)));
        EnumSet.allOf(GordianNTRUSpec.class).forEach(t -> mySpecs.add(GordianKeyPairSpecBuilder.ntru(t)));
        EnumSet.allOf(GordianFalconSpec.class).forEach(t -> mySpecs.add(GordianKeyPairSpecBuilder.falcon(t)));
        EnumSet.allOf(GordianMayoSpec.class).forEach(t -> mySpecs.add(GordianKeyPairSpecBuilder.mayo(t)));
        EnumSet.allOf(GordianSnovaSpec.class).forEach(t -> mySpecs.add(GordianKeyPairSpecBuilder.snova(t)));
        EnumSet.allOf(GordianPicnicSpec.class).forEach(t -> mySpecs.add(GordianKeyPairSpecBuilder.picnic(t)));

        /* Add NTRUPrime */
        GordianNTRUPrimeSpec.listPossibleKeySpecs().forEach(t -> mySpecs.add(GordianKeyPairSpecBuilder.ntruprime(t)));

        /* Return the list */
        return mySpecs;
    }
}

