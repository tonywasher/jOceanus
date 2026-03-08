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
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianChaCha20Key;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianElephantKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianISAPKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianRomulusKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianSalsa20Key;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianSkeinXofKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianSparkleKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianVMPCKey;

/**
 * GordianKnot StreamKeySpec Builder.
 */
public interface GordianStreamKeySpecBuilder {
    /**
     * Define StreamKeyType.
     *
     * @param pType the type
     * @return the Builder
     */
    GordianStreamKeySpecBuilder withType(GordianStreamKeyType pType);

    /**
     * Define subType.
     *
     * @param pSubType the subType
     * @return the Builder
     */
    GordianStreamKeySpecBuilder withSubType(GordianStreamKeySubType pSubType);

    /**
     * Define KeyLength.
     *
     * @param pKeyLength the keyLength
     * @return the Builder
     */
    GordianStreamKeySpecBuilder withKeyLength(GordianLength pKeyLength);

    /**
     * Build streamKeySpec.
     *
     * @return the streamKeySpec
     */
    GordianStreamKeySpec build();

    /**
     * Create generic.
     *
     * @param pKeyType   the keyType
     * @param pKeyLength the keyLength
     * @param pSubType   the subType
     * @return the keySpec
     */
    default GordianStreamKeySpec streamKey(final GordianStreamKeyType pKeyType,
                                           final GordianLength pKeyLength,
                                           final GordianStreamKeySubType pSubType) {
        return withType(pKeyType).withKeyLength(pKeyLength).withSubType(pSubType).build();
    }

    /**
     * Create hc.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianStreamKeySpec hc(final GordianLength pKeyLength) {
        return withType(GordianStreamKeyType.HC).withKeyLength(pKeyLength).build();
    }

    /**
     * Create chacha`.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianStreamKeySpec chacha(final GordianLength pKeyLength) {
        return withType(GordianStreamKeyType.CHACHA20).withKeyLength(pKeyLength).build();
    }

    /**
     * Create chacha7539.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianStreamKeySpec chacha7539(final GordianLength pKeyLength) {
        return withType(GordianStreamKeyType.CHACHA20).withSubType(GordianChaCha20Key.ISO7539).withKeyLength(pKeyLength).build();
    }

    /**
     * Create xchacha.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianStreamKeySpec xchacha(final GordianLength pKeyLength) {
        return withType(GordianStreamKeyType.CHACHA20).withSubType(GordianChaCha20Key.XCHACHA).withKeyLength(pKeyLength).build();
    }

    /**
     * Create salsa.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianStreamKeySpec salsa(final GordianLength pKeyLength) {
        return withType(GordianStreamKeyType.SALSA20).withKeyLength(pKeyLength).build();
    }

    /**
     * Create xsalsa.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianStreamKeySpec xsalsa(final GordianLength pKeyLength) {
        return withType(GordianStreamKeyType.SALSA20).withSubType(GordianSalsa20Key.XSALSA).withKeyLength(pKeyLength).build();
    }

    /**
     * Create isaac.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianStreamKeySpec isaac(final GordianLength pKeyLength) {
        return withType(GordianStreamKeyType.ISAAC).withKeyLength(pKeyLength).build();
    }

    /**
     * Create rc4c.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianStreamKeySpec rc4(final GordianLength pKeyLength) {
        return withType(GordianStreamKeyType.RC4).withKeyLength(pKeyLength).build();
    }

    /**
     * Create vmpc.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianStreamKeySpec vmpc(final GordianLength pKeyLength) {
        return withType(GordianStreamKeyType.VMPC).withKeyLength(pKeyLength).build();
    }

    /**
     * Create vmpcKSA.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianStreamKeySpec vmpcKSA(final GordianLength pKeyLength) {
        return withType(GordianStreamKeyType.VMPC).withSubType(GordianVMPCKey.KSA).withKeyLength(pKeyLength).build();
    }

    /**
     * Create grain.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianStreamKeySpec grain(final GordianLength pKeyLength) {
        return withType(GordianStreamKeyType.GRAIN).withKeyLength(pKeyLength).build();
    }

    /**
     * Create rabbit.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianStreamKeySpec rabbit(final GordianLength pKeyLength) {
        return withType(GordianStreamKeyType.RABBIT).withKeyLength(pKeyLength).build();
    }

    /**
     * Create sosemanuk.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianStreamKeySpec sosemanuk(final GordianLength pKeyLength) {
        return withType(GordianStreamKeyType.SOSEMANUK).withKeyLength(pKeyLength).build();
    }

    /**
     * Create snow3G.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianStreamKeySpec snow3G(final GordianLength pKeyLength) {
        return withType(GordianStreamKeyType.SNOW3G).withKeyLength(pKeyLength).build();
    }

    /**
     * Create zuc.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianStreamKeySpec zuc(final GordianLength pKeyLength) {
        return withType(GordianStreamKeyType.ZUC).withKeyLength(pKeyLength).build();
    }

    /**
     * Create skein.
     *
     * @param pKeyLength   the keyLength
     * @param pStateLength the stateLength
     * @return the keySpec
     */
    default GordianStreamKeySpec skeinXof(final GordianLength pKeyLength,
                                          final GordianSkeinXofKey pStateLength) {
        return withType(GordianStreamKeyType.SKEINXOF).withSubType(pStateLength).withKeyLength(pKeyLength).build();
    }

    /**
     * Create blake2X.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianStreamKeySpec blake2Xof(final GordianLength pKeyLength) {
        return withType(GordianStreamKeyType.BLAKE2XOF).withKeyLength(pKeyLength).build();
    }

    /**
     * Create blake3.
     *
     * @return the keySpec
     */
    default GordianStreamKeySpec blake3Xof() {
        return withType(GordianStreamKeyType.BLAKE3XOF).build();
    }

    /**
     * Create ascon.
     *
     * @return the keySpec
     */
    default GordianStreamKeySpec ascon() {
        return withType(GordianStreamKeyType.ASCON).build();
    }

    /**
     * Create elephant.
     *
     * @param pSubSpec the subSpec
     * @return the keySpec
     */
    default GordianStreamKeySpec elephant(final GordianElephantKey pSubSpec) {
        return withType(GordianStreamKeyType.ELEPHANT).withSubType(pSubSpec).build();
    }

    /**
     * Create isap.
     *
     * @param pSubSpec the subSpec
     * @return the keySpec
     */
    default GordianStreamKeySpec isap(final GordianISAPKey pSubSpec) {
        return withType(GordianStreamKeyType.ISAP).withSubType(pSubSpec).build();
    }

    /**
     * Create photonBeetle.
     *
     * @return the keySpec
     */
    default GordianStreamKeySpec photonBeetle() {
        return withType(GordianStreamKeyType.PHOTONBEETLE).build();
    }

    /**
     * Create romulus.
     *
     * @param pSubSpec the subSpec
     * @return the keySpec
     */
    default GordianStreamKeySpec romulus(final GordianRomulusKey pSubSpec) {
        return withType(GordianStreamKeyType.ROMULUS).withSubType(pSubSpec).build();
    }

    /**
     * Create sparkle.
     *
     * @param pSubSpec the subSpec
     * @return the keySpec
     */
    default GordianStreamKeySpec sparkle(final GordianSparkleKey pSubSpec) {
        return withType(GordianStreamKeyType.SPARKLE).withSubType(pSubSpec).build();
    }

    /**
     * Create xoodyak.
     *
     * @return the keySpec
     */
    default GordianStreamKeySpec xoodyak() {
        return withType(GordianStreamKeyType.XOODYAK).build();
    }
}
