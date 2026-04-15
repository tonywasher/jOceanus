/*
 * GordianKnot: Security Suite
 * Copyright 2026. Tony Washer
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

package io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianBIKESpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianCMCESpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianDHSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianDSASpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianDSTUSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianECSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianFRODOSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianFalconSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianGOSTSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianHQCSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianLMSSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianLMSSpec.GordianLMSHash;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianLMSSpec.GordianLMSHeight;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianLMSSpec.GordianLMSWidth;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianMLDSASpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianMLKEMSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianMayoSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNTRUPlusSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNTRUPrimeSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNTRUPrimeSpec.GordianNTRUPrimeParams;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNTRUPrimeSpec.GordianNTRUPrimeType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNTRUSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianPicnicSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianRSASpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianSABERSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianSLHDSASpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianSM2Spec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianSnovaSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianXMSSSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianXMSSSpec.GordianXMSSDigestType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianXMSSSpec.GordianXMSSHeight;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianXMSSSpec.GordianXMSSMTLayers;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Asymmetric KeyPair Specification Builder.
 */
public final class GordianCoreKeyPairSpecBuilder
        implements GordianKeyPairSpecBuilder {
    /**
     * The keyPairType.
     */
    private GordianKeyPairType theKeyPairType;

    /**
     * The subSpec.
     */
    private Object theSubSpec;

    /**
     * Private constructor.
     */
    private GordianCoreKeyPairSpecBuilder() {
    }

    /**
     * Obtain new instance.
     *
     * @return the new instance
     */
    public static GordianCoreKeyPairSpecBuilder newInstance() {
        return new GordianCoreKeyPairSpecBuilder();
    }

    @Override
    public GordianKeyPairSpecBuilder withKeyPairType(final GordianKeyPairType pType) {
        theKeyPairType = pType;
        return this;
    }

    @Override
    public GordianKeyPairSpecBuilder withEnumSubSpec(final Enum<?> pSubSpec) {
        theSubSpec = pSubSpec;
        return this;
    }

    @Override
    public GordianKeyPairSpecBuilder withXMSSSubSpec(final GordianXMSSDigestType pDigestType,
                                                     final GordianXMSSHeight pHeight) {
        theSubSpec = new GordianCoreXMSSSpec(pDigestType, pHeight);
        return this;
    }

    @Override
    public GordianKeyPairSpecBuilder withXMSSMTSubSpec(final GordianXMSSDigestType pDigestType,
                                                       final GordianXMSSHeight pHeight,
                                                       final GordianXMSSMTLayers pLayers) {
        theSubSpec = new GordianCoreXMSSSpec(pDigestType, pHeight, pLayers);
        return this;
    }

    @Override
    public GordianKeyPairSpecBuilder withLMSSubSpec(final GordianLMSHash pHashType,
                                                    final GordianLMSHeight pHeight,
                                                    final GordianLMSWidth pWidth,
                                                    final GordianLength pLength,
                                                    final int pDepth) {
        theSubSpec = new GordianCoreLMSSpec(pHashType, pHeight, pWidth, pLength, pDepth);
        return this;
    }

    @Override
    public GordianKeyPairSpecBuilder withNTRUPrimeSubSpec(final GordianNTRUPrimeType pType,
                                                          final GordianNTRUPrimeParams pParams) {
        theSubSpec = new GordianCoreNTRUPrimeSpec(pType, pParams);
        return this;
    }

    @Override
    public GordianKeyPairSpecBuilder withKeyPairSpecs(final List<GordianKeyPairSpec> pSpecs) {
        theSubSpec = pSpecs;
        return this;
    }

    @Override
    public GordianKeyPairSpec build() {
        final GordianCoreKeyPairSpec mySpec = new GordianCoreKeyPairSpec(theKeyPairType, theSubSpec);
        reset();
        return mySpec;
    }

    /**
     * Reset state.
     */
    private void reset() {
        theKeyPairType = null;
        theSubSpec = null;
    }

    /**
     * Create xmssKey.
     *
     * @param pSpec the xmssSpec
     * @return the KeySpec
     */
    public GordianKeyPairSpec xmss(final GordianXMSSSpec pSpec) {
        return new GordianCoreKeyPairSpec(GordianKeyPairType.XMSS, pSpec);
    }

    /**
     * Create hssKey.
     *
     * @param pSpec the lmsSpec
     * @return the KeySpec
     */
    public GordianKeyPairSpec lms(final GordianLMSSpec pSpec) {
        return new GordianCoreKeyPairSpec(GordianKeyPairType.LMS, pSpec);
    }

    /**
     * Create ntruPrimeKey.
     *
     * @param pSpec the ntruPrimeSpec
     * @return the KeySpec
     */
    public GordianKeyPairSpec ntruprime(final GordianNTRUPrimeSpec pSpec) {
        return new GordianCoreKeyPairSpec(GordianKeyPairType.NTRUPRIME, pSpec);
    }

    /**
     * List all possible keyPairSpecs.
     *
     * @return the list
     */
    public static List<GordianKeyPairSpec> listPossibleKeySpecs() {
        /* Create the list */
        final List<GordianKeyPairSpec> mySpecs = new ArrayList<>();
        final GordianCoreKeyPairSpecBuilder myBuilder = new GordianCoreKeyPairSpecBuilder();

        /* Add RSA */
        EnumSet.allOf(GordianRSASpec.class).forEach(m -> mySpecs.add(myBuilder.rsa(m)));

        /* Add DSA */
        EnumSet.allOf(GordianDSASpec.class).forEach(t -> mySpecs.add(myBuilder.dsa(t)));

        /* Add DH  */
        EnumSet.allOf(GordianDHSpec.class).forEach(g -> mySpecs.add(myBuilder.dh(g)));

        /* Add ElGamal  */
        EnumSet.allOf(GordianDHSpec.class).forEach(g -> mySpecs.add(myBuilder.elGamal(g)));

        /* Add EC */
        EnumSet.allOf(GordianECSpec.class).forEach(c -> mySpecs.add(myBuilder.ec(c)));

        /* Add SM2 */
        EnumSet.allOf(GordianSM2Spec.class).forEach(c -> mySpecs.add(myBuilder.sm2(c)));

        /* Add GOST2012 */
        EnumSet.allOf(GordianGOSTSpec.class).forEach(c -> mySpecs.add(myBuilder.gost2012(c)));

        /* Add DSTU4145 */
        EnumSet.allOf(GordianDSTUSpec.class).forEach(c -> mySpecs.add(myBuilder.dstu4145(c)));

        /* Add Ed25519/Ed448 */
        mySpecs.add(myBuilder.ed448());
        mySpecs.add(myBuilder.ed25519());

        /* Add X25519/X448 */
        mySpecs.add(myBuilder.x448());
        mySpecs.add(myBuilder.x25519());

        /* Add NewHope */
        mySpecs.add(myBuilder.newHope());

        /* Add XMSS/XMSSMT */
        GordianCoreXMSSSpec.listAllPossibleSpecs().forEach(t -> mySpecs.add(myBuilder.xmss(t)));

        /* Add LMS */
        GordianCoreLMSSpec.listAllPossibleSpecs().forEach(t -> mySpecs.add(myBuilder.lms(t)));

        /* Add SPHINCSPlus/CMCE/Frodo/Saber */
        EnumSet.allOf(GordianSLHDSASpec.class).forEach(t -> mySpecs.add(myBuilder.slhdsa(t)));
        EnumSet.allOf(GordianCMCESpec.class).forEach(t -> mySpecs.add(myBuilder.cmce(t)));
        EnumSet.allOf(GordianFRODOSpec.class).forEach(t -> mySpecs.add(myBuilder.frodo(t)));
        EnumSet.allOf(GordianSABERSpec.class).forEach(t -> mySpecs.add(myBuilder.saber(t)));
        EnumSet.allOf(GordianMLKEMSpec.class).forEach(t -> mySpecs.add(myBuilder.mlkem(t)));
        EnumSet.allOf(GordianMLDSASpec.class).forEach(t -> mySpecs.add(myBuilder.mldsa(t)));
        EnumSet.allOf(GordianHQCSpec.class).forEach(t -> mySpecs.add(myBuilder.hqc(t)));
        EnumSet.allOf(GordianBIKESpec.class).forEach(t -> mySpecs.add(myBuilder.bike(t)));
        EnumSet.allOf(GordianNTRUSpec.class).forEach(t -> mySpecs.add(myBuilder.ntru(t)));
        EnumSet.allOf(GordianNTRUPlusSpec.class).forEach(t -> mySpecs.add(myBuilder.ntruPlus(t)));
        EnumSet.allOf(GordianFalconSpec.class).forEach(t -> mySpecs.add(myBuilder.falcon(t)));
        EnumSet.allOf(GordianMayoSpec.class).forEach(t -> mySpecs.add(myBuilder.mayo(t)));
        EnumSet.allOf(GordianSnovaSpec.class).forEach(t -> mySpecs.add(myBuilder.snova(t)));
        EnumSet.allOf(GordianPicnicSpec.class).forEach(t -> mySpecs.add(myBuilder.picnic(t)));

        /* Add NTRUPrime */
        GordianCoreNTRUPrimeSpec.listAllPossibleSpecs().forEach(t -> mySpecs.add(myBuilder.ntruprime(t)));

        /* Return the list */
        return mySpecs;
    }
}
