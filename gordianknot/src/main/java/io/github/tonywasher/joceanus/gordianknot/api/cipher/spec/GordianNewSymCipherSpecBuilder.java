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

package io.github.tonywasher.joceanus.gordianknot.api.cipher.spec;

/**
 * The SymCipherSpec Builder class.
 */
public interface GordianNewSymCipherSpecBuilder {
    /**
     * Define StreamKeySpec.
     *
     * @param pSpec the spec
     * @return the Builder
     */
    GordianNewSymCipherSpecBuilder withKeySpec(GordianNewSymKeySpec pSpec);

    /**
     * Define cipher mode.
     *
     * @param pMode the cipher mode
     * @return the Builder
     */
    GordianNewSymCipherSpecBuilder withMode(GordianNewCipherMode pMode);

    /**
     * Define cipher padding.
     *
     * @param pPadding the padding
     * @return the Builder
     */
    GordianNewSymCipherSpecBuilder withPadding(GordianNewPadding pPadding);

    /**
     * Build symCipherSpec.
     *
     * @return the symCipherSpec
     */
    GordianNewSymCipherSpec build();

    /**
     * Create an ECB symKey cipherSpec.
     *
     * @param pKeySpec the keySpec
     * @param pPadding the padding
     * @return the cipherSpec
     */
    default GordianNewSymCipherSpec ecb(final GordianNewSymKeySpec pKeySpec,
                                        final GordianNewPadding pPadding) {
        return withKeySpec(pKeySpec).withMode(GordianNewCipherMode.ECB).withPadding(pPadding).build();
    }

    /**
     * Create a CBC symKey cipherSpec.
     *
     * @param pKeySpec the keySpec
     * @param pPadding the padding
     * @return the cipherSpec
     */
    default GordianNewSymCipherSpec cbc(final GordianNewSymKeySpec pKeySpec,
                                        final GordianNewPadding pPadding) {
        return withKeySpec(pKeySpec).withMode(GordianNewCipherMode.CBC).withPadding(pPadding).build();
    }

    /**
     * Create a CFB symKey cipherSpec.
     *
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    default GordianNewSymCipherSpec cfb(final GordianNewSymKeySpec pKeySpec) {
        return withKeySpec(pKeySpec).withMode(GordianNewCipherMode.CFB).build();
    }

    /**
     * Create a GCFB symKey cipherSpec.
     *
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    default GordianNewSymCipherSpec gcfb(final GordianNewSymKeySpec pKeySpec) {
        return withKeySpec(pKeySpec).withMode(GordianNewCipherMode.GCFB).build();
    }

    /**
     * Create a OFB symKey cipherSpec.
     *
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    default GordianNewSymCipherSpec ofb(final GordianNewSymKeySpec pKeySpec) {
        return withKeySpec(pKeySpec).withMode(GordianNewCipherMode.OFB).build();
    }

    /**
     * Create a GOFB symKey cipherSpec.
     *
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    default GordianNewSymCipherSpec gofb(final GordianNewSymKeySpec pKeySpec) {
        return withKeySpec(pKeySpec).withMode(GordianNewCipherMode.GOFB).build();
    }

    /**
     * Create a SIC symKey cipherSpec.
     *
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    default GordianNewSymCipherSpec sic(final GordianNewSymKeySpec pKeySpec) {
        return withKeySpec(pKeySpec).withMode(GordianNewCipherMode.SIC).build();
    }

    /**
     * Create a KCTR symKey cipherSpec.
     *
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    default GordianNewSymCipherSpec kctr(final GordianNewSymKeySpec pKeySpec) {
        return withKeySpec(pKeySpec).withMode(GordianNewCipherMode.KCTR).build();
    }

    /**
     * Create a CCM symKey cipherSpec.
     *
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    default GordianNewSymCipherSpec ccm(final GordianNewSymKeySpec pKeySpec) {
        return withKeySpec(pKeySpec).withMode(GordianNewCipherMode.CCM).build();
    }

    /**
     * Create a KCCM symKey cipherSpec.
     *
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    default GordianNewSymCipherSpec kccm(final GordianNewSymKeySpec pKeySpec) {
        return withKeySpec(pKeySpec).withMode(GordianNewCipherMode.KCCM).build();
    }

    /**
     * Create a GCM symKey cipherSpec.
     *
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    default GordianNewSymCipherSpec gcm(final GordianNewSymKeySpec pKeySpec) {
        return withKeySpec(pKeySpec).withMode(GordianNewCipherMode.GCM).build();
    }

    /**
     * Create a KGCM symKey cipherSpec.
     *
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    default GordianNewSymCipherSpec kgcm(final GordianNewSymKeySpec pKeySpec) {
        return withKeySpec(pKeySpec).withMode(GordianNewCipherMode.KGCM).build();
    }

    /**
     * Create an EAX symKey cipherSpec.
     *
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    default GordianNewSymCipherSpec eax(final GordianNewSymKeySpec pKeySpec) {
        return withKeySpec(pKeySpec).withMode(GordianNewCipherMode.EAX).build();
    }

    /**
     * Create an OCB symKey cipherSpec.
     *
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    default GordianNewSymCipherSpec ocb(final GordianNewSymKeySpec pKeySpec) {
        return withKeySpec(pKeySpec).withMode(GordianNewCipherMode.OCB).build();
    }
}
