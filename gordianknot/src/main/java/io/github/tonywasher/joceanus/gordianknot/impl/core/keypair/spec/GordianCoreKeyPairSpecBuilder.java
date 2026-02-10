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

package io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.spec;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewBIKESpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewCMCESpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewDHSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewDSASpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewDSTUSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewECSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewFRODOSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewFalconSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewGOSTSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewHQCSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewKeyPairSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewKeyPairType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewLMSSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewLMSSpec.GordianNewLMSHash;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewLMSSpec.GordianNewLMSHeight;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewLMSSpec.GordianNewLMSWidth;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewMLDSASpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewMLKEMSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewMayoSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewNTRUPrimeSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewNTRUPrimeSpec.GordianNewNTRUPrimeParams;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewNTRUPrimeSpec.GordianNewNTRUPrimeType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewNTRUSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewPicnicSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewRSASpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewSABERSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewSLHDSASpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewSM2Spec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewSnovaSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewXMSSSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewXMSSSpec.GordianNewXMSSDigestType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewXMSSSpec.GordianNewXMSSHeight;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewXMSSSpec.GordianNewXMSSMTLayers;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.spec.GordianCoreLMSSpec.GordianCoreHSSSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.spec.GordianCoreXMSSSpec.GordianCoreXMSSMTSpec;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Asymmetric KeyPair Specification Builder.
 */
public class GordianCoreKeyPairSpecBuilder
        implements GordianNewKeyPairSpecBuilder {
    /**
     * The keyPairType.
     */
    private GordianNewKeyPairType theKeyPairType;

    /**
     * The subSpec.
     */
    private Object theSubSpec;

    @Override
    public GordianNewKeyPairSpecBuilder withKeyPairType(final GordianNewKeyPairType pType) {
        theKeyPairType = pType;
        return this;
    }

    @Override
    public GordianNewKeyPairSpecBuilder withEnumSubSpec(final Enum<?> pSubSpec) {
        theSubSpec = pSubSpec;
        return this;
    }

    @Override
    public GordianNewKeyPairSpecBuilder withXMSSSubSpec(final GordianNewXMSSDigestType pDigestType,
                                                        final GordianNewXMSSHeight pHeight) {
        theSubSpec = new GordianCoreXMSSSpec(pDigestType, pHeight);
        return this;
    }

    @Override
    public GordianNewKeyPairSpecBuilder withXMSSMTSubSpec(final GordianNewXMSSDigestType pDigestType,
                                                          final GordianNewXMSSHeight pHeight,
                                                          final GordianNewXMSSMTLayers pLayers) {
        theSubSpec = new GordianCoreXMSSMTSpec(pDigestType, pHeight, pLayers);
        return this;
    }

    @Override
    public GordianNewKeyPairSpecBuilder withHSSSubSpec(final GordianNewLMSHash pHashType,
                                                       final GordianNewLMSHeight pHeight,
                                                       final GordianNewLMSWidth pWidth,
                                                       final GordianLength pLength,
                                                       final int pDepth) {
        final GordianCoreLMSSpec myLMS = new GordianCoreLMSSpec(pHashType, pHeight, pWidth, pLength);
        theSubSpec = new GordianCoreHSSSpec(myLMS, pDepth);
        return this;
    }

    @Override
    public GordianNewKeyPairSpecBuilder withNTRUPrimeSubSpec(final GordianNewNTRUPrimeType pType,
                                                             final GordianNewNTRUPrimeParams pParams) {
        theSubSpec = new GordianCoreNTRUPrimeSpec(pType, pParams);
        return this;
    }

    @Override
    public GordianNewKeyPairSpecBuilder withKeyPairSpecs(final List<GordianNewKeyPairSpec> pSpecs) {
        theSubSpec = pSpecs;
        return this;
    }

    @Override
    public GordianNewKeyPairSpec build() {
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
    public GordianNewKeyPairSpec xmss(final GordianNewXMSSSpec pSpec) {
        return new GordianCoreKeyPairSpec(GordianNewKeyPairType.XMSS, pSpec);
    }

    /**
     * Create hssKey.
     *
     * @param pSpec the lmsSpec
     * @return the KeySpec
     */
    public GordianNewKeyPairSpec lms(final GordianNewLMSSpec pSpec) {
        return new GordianCoreKeyPairSpec(GordianNewKeyPairType.LMS, pSpec);
    }

    /**
     * Create ntruPrimeKey.
     *
     * @param pSpec the ntruPrimeSpec
     * @return the KeySpec
     */
    public GordianNewKeyPairSpec ntruprime(final GordianNewNTRUPrimeSpec pSpec) {
        return new GordianCoreKeyPairSpec(GordianNewKeyPairType.NTRUPRIME, pSpec);
    }

    /**
     * List all possible keyPairSpecs.
     *
     * @return the list
     */
    public static List<GordianNewKeyPairSpec> listPossibleKeySpecs() {
        /* Create the list */
        final List<GordianNewKeyPairSpec> mySpecs = new ArrayList<>();
        final GordianCoreKeyPairSpecBuilder myBuilder = new GordianCoreKeyPairSpecBuilder();

        /* Add RSA */
        EnumSet.allOf(GordianNewRSASpec.class).forEach(m -> mySpecs.add(myBuilder.rsa(m)));

        /* Add DSA */
        EnumSet.allOf(GordianNewDSASpec.class).forEach(t -> mySpecs.add(myBuilder.dsa(t)));

        /* Add DH  */
        EnumSet.allOf(GordianNewDHSpec.class).forEach(g -> mySpecs.add(myBuilder.dh(g)));

        /* Add ElGamal  */
        EnumSet.allOf(GordianNewDHSpec.class).forEach(g -> mySpecs.add(myBuilder.elGamal(g)));

        /* Add EC */
        EnumSet.allOf(GordianNewECSpec.class).forEach(c -> mySpecs.add(myBuilder.ec(c)));

        /* Add SM2 */
        EnumSet.allOf(GordianNewSM2Spec.class).forEach(c -> mySpecs.add(myBuilder.sm2(c)));

        /* Add GOST2012 */
        EnumSet.allOf(GordianNewGOSTSpec.class).forEach(c -> mySpecs.add(myBuilder.gost2012(c)));

        /* Add DSTU4145 */
        EnumSet.allOf(GordianNewDSTUSpec.class).forEach(c -> mySpecs.add(myBuilder.dstu4145(c)));

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
        EnumSet.allOf(GordianNewSLHDSASpec.class).forEach(t -> mySpecs.add(myBuilder.slhdsa(t)));
        EnumSet.allOf(GordianNewCMCESpec.class).forEach(t -> mySpecs.add(myBuilder.cmce(t)));
        EnumSet.allOf(GordianNewFRODOSpec.class).forEach(t -> mySpecs.add(myBuilder.frodo(t)));
        EnumSet.allOf(GordianNewSABERSpec.class).forEach(t -> mySpecs.add(myBuilder.saber(t)));
        EnumSet.allOf(GordianNewMLKEMSpec.class).forEach(t -> mySpecs.add(myBuilder.mlkem(t)));
        EnumSet.allOf(GordianNewMLDSASpec.class).forEach(t -> mySpecs.add(myBuilder.mldsa(t)));
        EnumSet.allOf(GordianNewHQCSpec.class).forEach(t -> mySpecs.add(myBuilder.hqc(t)));
        EnumSet.allOf(GordianNewBIKESpec.class).forEach(t -> mySpecs.add(myBuilder.bike(t)));
        EnumSet.allOf(GordianNewNTRUSpec.class).forEach(t -> mySpecs.add(myBuilder.ntru(t)));
        EnumSet.allOf(GordianNewFalconSpec.class).forEach(t -> mySpecs.add(myBuilder.falcon(t)));
        EnumSet.allOf(GordianNewMayoSpec.class).forEach(t -> mySpecs.add(myBuilder.mayo(t)));
        EnumSet.allOf(GordianNewSnovaSpec.class).forEach(t -> mySpecs.add(myBuilder.snova(t)));
        EnumSet.allOf(GordianNewPicnicSpec.class).forEach(t -> mySpecs.add(myBuilder.picnic(t)));

        /* Add NTRUPrime */
        GordianCoreNTRUPrimeSpec.listAllPossibleSpecs().forEach(t -> mySpecs.add(myBuilder.ntruprime(t)));

        /* Return the list */
        return mySpecs;
    }
}
