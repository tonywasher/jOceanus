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

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySubType.GordianNewChaCha20Key;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySubType.GordianNewElephantKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySubType.GordianNewISAPKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySubType.GordianNewRomulusKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySubType.GordianNewSalsa20Key;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySubType.GordianNewSkeinXofKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySubType.GordianNewSparkleKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySubType.GordianNewVMPCKey;

/**
 * GordianKnot StreamKeySpec Builder.
 */
public interface GordianNewStreamKeySpecBuilder {
    /**
     * Define StreamKeyType.
     *
     * @param pType the type
     * @return the Builder
     */
    GordianNewStreamKeySpecBuilder withType(GordianNewStreamKeyType pType);

    /**
     * Define subType.
     *
     * @param pSubType the subType
     * @return the Builder
     */
    GordianNewStreamKeySpecBuilder withSubType(GordianNewStreamKeySubType pSubType);

    /**
     * Define KeyLength.
     *
     * @param pKeyLength the keyLength
     * @return the Builder
     */
    GordianNewStreamKeySpecBuilder withKeyLength(GordianLength pKeyLength);

    /**
     * Build streamKeySpec.
     *
     * @return the streamKeySpec
     */
    GordianNewStreamKeySpec build();

    /**
     * Create generic.
     *
     * @param pKeyType   the keyType
     * @param pKeyLength the keyLength
     * @param pSubType   the subType
     * @return the keySpec
     */
    default GordianNewStreamKeySpec streamKey(final GordianNewStreamKeyType pKeyType,
                                              final GordianLength pKeyLength,
                                              final GordianNewStreamKeySubType pSubType) {
        return withType(pKeyType).withKeyLength(pKeyLength).withSubType(pSubType).build();
    }

    /**
     * Create hc.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianNewStreamKeySpec hc(final GordianLength pKeyLength) {
        return withType(GordianNewStreamKeyType.HC).withKeyLength(pKeyLength).build();
    }

    /**
     * Create chacha`.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianNewStreamKeySpec chacha(final GordianLength pKeyLength) {
        return withType(GordianNewStreamKeyType.CHACHA20).withKeyLength(pKeyLength).build();
    }

    /**
     * Create chacha7539.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianNewStreamKeySpec chacha7539(final GordianLength pKeyLength) {
        return withType(GordianNewStreamKeyType.CHACHA20).withSubType(GordianNewChaCha20Key.ISO7539).withKeyLength(pKeyLength).build();
    }

    /**
     * Create xchacha.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianNewStreamKeySpec xchacha(final GordianLength pKeyLength) {
        return withType(GordianNewStreamKeyType.CHACHA20).withSubType(GordianNewChaCha20Key.XCHACHA).withKeyLength(pKeyLength).build();
    }

    /**
     * Create salsa.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianNewStreamKeySpec salsa(final GordianLength pKeyLength) {
        return withType(GordianNewStreamKeyType.SALSA20).withKeyLength(pKeyLength).build();
    }

    /**
     * Create xsalsa.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianNewStreamKeySpec xsalsa(final GordianLength pKeyLength) {
        return withType(GordianNewStreamKeyType.SALSA20).withSubType(GordianNewSalsa20Key.XSALSA).withKeyLength(pKeyLength).build();
    }

    /**
     * Create isaac.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianNewStreamKeySpec isaac(final GordianLength pKeyLength) {
        return withType(GordianNewStreamKeyType.ISAAC).withKeyLength(pKeyLength).build();
    }

    /**
     * Create rc4c.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianNewStreamKeySpec rc4(final GordianLength pKeyLength) {
        return withType(GordianNewStreamKeyType.RC4).withKeyLength(pKeyLength).build();
    }

    /**
     * Create vmpc.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianNewStreamKeySpec vmpc(final GordianLength pKeyLength) {
        return withType(GordianNewStreamKeyType.VMPC).withKeyLength(pKeyLength).build();
    }

    /**
     * Create vmpcKSA.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianNewStreamKeySpec vmpcKSA(final GordianLength pKeyLength) {
        return withType(GordianNewStreamKeyType.VMPC).withSubType(GordianNewVMPCKey.KSA).withKeyLength(pKeyLength).build();
    }

    /**
     * Create grain.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianNewStreamKeySpec grain(final GordianLength pKeyLength) {
        return withType(GordianNewStreamKeyType.GRAIN).withKeyLength(pKeyLength).build();
    }

    /**
     * Create rabbit.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianNewStreamKeySpec rabbit(final GordianLength pKeyLength) {
        return withType(GordianNewStreamKeyType.RABBIT).withKeyLength(pKeyLength).build();
    }

    /**
     * Create sosemanuk.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianNewStreamKeySpec sosemanuk(final GordianLength pKeyLength) {
        return withType(GordianNewStreamKeyType.SOSEMANUK).withKeyLength(pKeyLength).build();
    }

    /**
     * Create snow3G.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianNewStreamKeySpec snow3G(final GordianLength pKeyLength) {
        return withType(GordianNewStreamKeyType.SNOW3G).withKeyLength(pKeyLength).build();
    }

    /**
     * Create zuc.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianNewStreamKeySpec zuc(final GordianLength pKeyLength) {
        return withType(GordianNewStreamKeyType.ZUC).withKeyLength(pKeyLength).build();
    }

    /**
     * Create skein.
     *
     * @param pKeyLength   the keyLength
     * @param pStateLength the stateLength
     * @return the keySpec
     */
    default GordianNewStreamKeySpec skeinXof(final GordianLength pKeyLength,
                                             final GordianNewSkeinXofKey pStateLength) {
        return withType(GordianNewStreamKeyType.SKEINXOF).withSubType(pStateLength).withKeyLength(pKeyLength).build();
    }

    /**
     * Create blake2X.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianNewStreamKeySpec blake2Xof(final GordianLength pKeyLength) {
        return withType(GordianNewStreamKeyType.BLAKE2XOF).withKeyLength(pKeyLength).build();
    }

    /**
     * Create blake3.
     *
     * @return the keySpec
     */
    default GordianNewStreamKeySpec blake3Xof() {
        return withType(GordianNewStreamKeyType.BLAKE3XOF).build();
    }

    /**
     * Create ascon.
     *
     * @return the keySpec
     */
    default GordianNewStreamKeySpec ascon() {
        return withType(GordianNewStreamKeyType.ASCON).build();
    }

    /**
     * Create elephant.
     *
     * @param pSubSpec the subSpec
     * @return the keySpec
     */
    default GordianNewStreamKeySpec elephant(final GordianNewElephantKey pSubSpec) {
        return withType(GordianNewStreamKeyType.ELEPHANT).withSubType(pSubSpec).build();
    }

    /**
     * Create isap.
     *
     * @param pSubSpec the subSpec
     * @return the keySpec
     */
    default GordianNewStreamKeySpec isap(final GordianNewISAPKey pSubSpec) {
        return withType(GordianNewStreamKeyType.ISAP).withSubType(pSubSpec).build();
    }

    /**
     * Create photonBeetle.
     *
     * @return the keySpec
     */
    default GordianNewStreamKeySpec photonBeetle() {
        return withType(GordianNewStreamKeyType.PHOTONBEETLE).build();
    }

    /**
     * Create romulus.
     *
     * @param pSubSpec the subSpec
     * @return the keySpec
     */
    default GordianNewStreamKeySpec romulus(final GordianNewRomulusKey pSubSpec) {
        return withType(GordianNewStreamKeyType.ROMULUS).withSubType(pSubSpec).build();
    }

    /**
     * Create sparkle.
     *
     * @param pSubSpec the subSpec
     * @return the keySpec
     */
    default GordianNewStreamKeySpec sparkle(final GordianNewSparkleKey pSubSpec) {
        return withType(GordianNewStreamKeyType.SPARKLE).withSubType(pSubSpec).build();
    }

    /**
     * Create xoodyak.
     *
     * @return the keySpec
     */
    default GordianNewStreamKeySpec xoodyak() {
        return withType(GordianNewStreamKeyType.XOODYAK).build();
    }
}
