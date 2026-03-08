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
public interface GordianSymCipherSpecBuilder {
    /**
     * Define SymKeySpec.
     *
     * @param pSpec the spec
     * @return the Builder
     */
    GordianSymCipherSpecBuilder withKeySpec(GordianSymKeySpec pSpec);

    /**
     * Define cipher mode.
     *
     * @param pMode the cipher mode
     * @return the Builder
     */
    GordianSymCipherSpecBuilder withMode(GordianCipherMode pMode);

    /**
     * Define cipher padding.
     *
     * @param pPadding the padding
     * @return the Builder
     */
    GordianSymCipherSpecBuilder withPadding(GordianPadding pPadding);

    /**
     * Build symCipherSpec.
     *
     * @return the symCipherSpec
     */
    GordianSymCipherSpec build();

    /**
     * Create a generic symKey cipherSpec.
     *
     * @param pKeySpec the keySpec
     * @param pMode    the mode
     * @param pPadding the padding
     * @return the cipherSpec
     */
    default GordianSymCipherSpec symCipher(final GordianSymKeySpec pKeySpec,
                                           final GordianCipherMode pMode,
                                           final GordianPadding pPadding) {
        return withKeySpec(pKeySpec).withMode(pMode).withPadding(pPadding).build();
    }

    /**
     * Create an ECB symKey cipherSpec.
     *
     * @param pKeySpec the keySpec
     * @param pPadding the padding
     * @return the cipherSpec
     */
    default GordianSymCipherSpec ecb(final GordianSymKeySpec pKeySpec,
                                     final GordianPadding pPadding) {
        return withKeySpec(pKeySpec).withMode(GordianCipherMode.ECB).withPadding(pPadding).build();
    }

    /**
     * Create a CBC symKey cipherSpec.
     *
     * @param pKeySpec the keySpec
     * @param pPadding the padding
     * @return the cipherSpec
     */
    default GordianSymCipherSpec cbc(final GordianSymKeySpec pKeySpec,
                                     final GordianPadding pPadding) {
        return withKeySpec(pKeySpec).withMode(GordianCipherMode.CBC).withPadding(pPadding).build();
    }

    /**
     * Create a CFB symKey cipherSpec.
     *
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    default GordianSymCipherSpec cfb(final GordianSymKeySpec pKeySpec) {
        return withKeySpec(pKeySpec).withMode(GordianCipherMode.CFB).build();
    }

    /**
     * Create a GCFB symKey cipherSpec.
     *
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    default GordianSymCipherSpec gcfb(final GordianSymKeySpec pKeySpec) {
        return withKeySpec(pKeySpec).withMode(GordianCipherMode.GCFB).build();
    }

    /**
     * Create a OFB symKey cipherSpec.
     *
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    default GordianSymCipherSpec ofb(final GordianSymKeySpec pKeySpec) {
        return withKeySpec(pKeySpec).withMode(GordianCipherMode.OFB).build();
    }

    /**
     * Create a GOFB symKey cipherSpec.
     *
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    default GordianSymCipherSpec gofb(final GordianSymKeySpec pKeySpec) {
        return withKeySpec(pKeySpec).withMode(GordianCipherMode.GOFB).build();
    }

    /**
     * Create a SIC symKey cipherSpec.
     *
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    default GordianSymCipherSpec sic(final GordianSymKeySpec pKeySpec) {
        return withKeySpec(pKeySpec).withMode(GordianCipherMode.SIC).build();
    }

    /**
     * Create a KCTR symKey cipherSpec.
     *
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    default GordianSymCipherSpec kctr(final GordianSymKeySpec pKeySpec) {
        return withKeySpec(pKeySpec).withMode(GordianCipherMode.KCTR).build();
    }

    /**
     * Create a CCM symKey cipherSpec.
     *
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    default GordianSymCipherSpec ccm(final GordianSymKeySpec pKeySpec) {
        return withKeySpec(pKeySpec).withMode(GordianCipherMode.CCM).build();
    }

    /**
     * Create a KCCM symKey cipherSpec.
     *
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    default GordianSymCipherSpec kccm(final GordianSymKeySpec pKeySpec) {
        return withKeySpec(pKeySpec).withMode(GordianCipherMode.KCCM).build();
    }

    /**
     * Create a GCM symKey cipherSpec.
     *
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    default GordianSymCipherSpec gcm(final GordianSymKeySpec pKeySpec) {
        return withKeySpec(pKeySpec).withMode(GordianCipherMode.GCM).build();
    }

    /**
     * Create a KGCM symKey cipherSpec.
     *
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    default GordianSymCipherSpec kgcm(final GordianSymKeySpec pKeySpec) {
        return withKeySpec(pKeySpec).withMode(GordianCipherMode.KGCM).build();
    }

    /**
     * Create an EAX symKey cipherSpec.
     *
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    default GordianSymCipherSpec eax(final GordianSymKeySpec pKeySpec) {
        return withKeySpec(pKeySpec).withMode(GordianCipherMode.EAX).build();
    }

    /**
     * Create an OCB symKey cipherSpec.
     *
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    default GordianSymCipherSpec ocb(final GordianSymKeySpec pKeySpec) {
        return withKeySpec(pKeySpec).withMode(GordianCipherMode.OCB).build();
    }
}
