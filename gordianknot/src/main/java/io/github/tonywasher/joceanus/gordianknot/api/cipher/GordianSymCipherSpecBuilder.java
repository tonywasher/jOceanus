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
package io.github.tonywasher.joceanus.gordianknot.api.cipher;

import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewPadding;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymCipherSpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymCipherSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymKeySpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCoreSymCipherSpecBuilder;

/**
 * The SymCipherSpec Builder class.
 */
public final class GordianSymCipherSpecBuilder {
    /**
     * SymCipherSpecBuilder.
     */
    private static final GordianNewSymCipherSpecBuilder BUILDER = GordianCoreSymCipherSpecBuilder.newInstance();

    /**
     * Private constructor.
     */
    private GordianSymCipherSpecBuilder() {
    }

    /**
     * Create an ECB symKey cipherSpec.
     *
     * @param pKeySpec the keySpec
     * @param pPadding the padding
     * @return the cipherSpec
     */
    public static GordianNewSymCipherSpec ecb(final GordianNewSymKeySpec pKeySpec,
                                              final GordianNewPadding pPadding) {
        return BUILDER.ecb(pKeySpec, pPadding);
    }

    /**
     * Create a CBC symKey cipherSpec.
     *
     * @param pKeySpec the keySpec
     * @param pPadding the padding
     * @return the cipherSpec
     */
    public static GordianNewSymCipherSpec cbc(final GordianNewSymKeySpec pKeySpec,
                                              final GordianNewPadding pPadding) {
        return BUILDER.cbc(pKeySpec, pPadding);
    }

    /**
     * Create a CFB symKey cipherSpec.
     *
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    public static GordianNewSymCipherSpec cfb(final GordianNewSymKeySpec pKeySpec) {
        return BUILDER.cfb(pKeySpec);
    }

    /**
     * Create a GCFB symKey cipherSpec.
     *
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    public static GordianNewSymCipherSpec gcfb(final GordianNewSymKeySpec pKeySpec) {
        return BUILDER.gcfb(pKeySpec);
    }

    /**
     * Create a OFB symKey cipherSpec.
     *
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    public static GordianNewSymCipherSpec ofb(final GordianNewSymKeySpec pKeySpec) {
        return BUILDER.ofb(pKeySpec);
    }

    /**
     * Create a GOFB symKey cipherSpec.
     *
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    public static GordianNewSymCipherSpec gofb(final GordianNewSymKeySpec pKeySpec) {
        return BUILDER.gofb(pKeySpec);
    }

    /**
     * Create a SIC symKey cipherSpec.
     *
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    public static GordianNewSymCipherSpec sic(final GordianNewSymKeySpec pKeySpec) {
        return BUILDER.sic(pKeySpec);
    }

    /**
     * Create a KCTR symKey cipherSpec.
     *
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    public static GordianNewSymCipherSpec kctr(final GordianNewSymKeySpec pKeySpec) {
        return BUILDER.kctr(pKeySpec);
    }

    /**
     * Create a CCM symKey cipherSpec.
     *
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    public static GordianNewSymCipherSpec ccm(final GordianNewSymKeySpec pKeySpec) {
        return BUILDER.ccm(pKeySpec);
    }

    /**
     * Create a KCCM symKey cipherSpec.
     *
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    public static GordianNewSymCipherSpec kccm(final GordianNewSymKeySpec pKeySpec) {
        return BUILDER.kccm(pKeySpec);
    }

    /**
     * Create a GCM symKey cipherSpec.
     *
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    public static GordianNewSymCipherSpec gcm(final GordianNewSymKeySpec pKeySpec) {
        return BUILDER.gcm(pKeySpec);
    }

    /**
     * Create a KGCM symKey cipherSpec.
     *
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    public static GordianNewSymCipherSpec kgcm(final GordianNewSymKeySpec pKeySpec) {
        return BUILDER.kgcm(pKeySpec);
    }

    /**
     * Create an EAX symKey cipherSpec.
     *
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    public static GordianNewSymCipherSpec eax(final GordianNewSymKeySpec pKeySpec) {
        return BUILDER.eax(pKeySpec);
    }

    /**
     * Create an OCB symKey cipherSpec.
     *
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    public static GordianNewSymCipherSpec ocb(final GordianNewSymKeySpec pKeySpec) {
        return BUILDER.ocb(pKeySpec);
    }
}
