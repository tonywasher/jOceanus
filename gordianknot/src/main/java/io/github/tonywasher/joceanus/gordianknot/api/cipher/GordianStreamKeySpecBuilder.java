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

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianStreamKeySpec.GordianChaCha20Key;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianStreamKeySpec.GordianElephantKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianStreamKeySpec.GordianISAPKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianStreamKeySpec.GordianRomulusKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianStreamKeySpec.GordianSalsa20Key;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianStreamKeySpec.GordianSkeinXofKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianStreamKeySpec.GordianSparkleKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianStreamKeySpec.GordianVMPCKey;

/**
 * GordianKnot StreamKeySpec Builder.
 */
public final class GordianStreamKeySpecBuilder {
    /**
     * Private constructor.
     */
    private GordianStreamKeySpecBuilder() {
    }

    /**
     * Create hcKeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec hc(final GordianLength pKeyLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.HC, pKeyLength);
    }

    /**
     * Create chachaKeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec chacha(final GordianLength pKeyLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.CHACHA20, pKeyLength, GordianChaCha20Key.STD);
    }

    /**
     * Create chacha7539KeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec chacha7539(final GordianLength pKeyLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.CHACHA20, pKeyLength, GordianChaCha20Key.ISO7539);
    }

    /**
     * Create xchachaKeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec xchacha(final GordianLength pKeyLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.CHACHA20, pKeyLength, GordianChaCha20Key.XCHACHA);
    }

    /**
     * Create salsaKeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec salsa(final GordianLength pKeyLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.SALSA20, pKeyLength, GordianSalsa20Key.STD);
    }

    /**
     * Create xsalsaKeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec xsalsa(final GordianLength pKeyLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.SALSA20, pKeyLength, GordianSalsa20Key.XSALSA);
    }

    /**
     * Create isaacKeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec isaac(final GordianLength pKeyLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.ISAAC, pKeyLength);
    }

    /**
     * Create rc4KeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec rc4(final GordianLength pKeyLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.RC4, pKeyLength);
    }

    /**
     * Create vmpcKeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec vmpc(final GordianLength pKeyLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.VMPC, pKeyLength, GordianVMPCKey.STD);
    }

    /**
     * Create vmpcKSAKeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec vmpcKSA(final GordianLength pKeyLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.VMPC, pKeyLength, GordianVMPCKey.KSA);
    }

    /**
     * Create grainKeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec grain(final GordianLength pKeyLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.GRAIN, pKeyLength);
    }

    /**
     * Create rabbitKeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec rabbit(final GordianLength pKeyLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.RABBIT, pKeyLength);
    }

    /**
     * Create sosemanukKeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec sosemanuk(final GordianLength pKeyLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.SOSEMANUK, pKeyLength);
    }

    /**
     * Create snow3GKeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec snow3G(final GordianLength pKeyLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.SNOW3G, pKeyLength);
    }

    /**
     * Create zucKeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec zuc(final GordianLength pKeyLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.ZUC, pKeyLength);
    }

    /**
     * Create skeinKeySpec.
     *
     * @param pKeyLength   the keyLength
     * @param pStateLength the stateLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec skeinXof(final GordianLength pKeyLength,
                                                final GordianLength pStateLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.SKEINXOF, pKeyLength, GordianSkeinXofKey.getKeyTypeForLength(pStateLength));
    }

    /**
     * Create blake2KeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec blake2Xof(final GordianLength pKeyLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.BLAKE2XOF, pKeyLength);
    }

    /**
     * Create blake3KeySpec.
     *
     * @return the keySpec
     */
    public static GordianStreamKeySpec blake3Xof() {
        return new GordianStreamKeySpec(GordianStreamKeyType.BLAKE3XOF, GordianLength.LEN_256);
    }

    /**
     * Create asconKeySpec.
     *
     * @return the keySpec
     */
    public static GordianStreamKeySpec ascon() {
        return new GordianStreamKeySpec(GordianStreamKeyType.ASCON, GordianLength.LEN_256);
    }

    /**
     * Create elephantKeySpec.
     *
     * @param pSubSpec the subSpec
     * @return the keySpec
     */
    public static GordianStreamKeySpec elephant(final GordianElephantKey pSubSpec) {
        return new GordianStreamKeySpec(GordianStreamKeyType.ELEPHANT, GordianLength.LEN_256, pSubSpec);
    }

    /**
     * Create isapKeySpec.
     *
     * @param pSubSpec the subSpec
     * @return the keySpec
     */
    public static GordianStreamKeySpec isap(final GordianISAPKey pSubSpec) {
        return new GordianStreamKeySpec(GordianStreamKeyType.ISAP, GordianLength.LEN_256, pSubSpec);
    }

    /**
     * Create photonBeetleKeySpec.
     *
     * @return the keySpec
     */
    public static GordianStreamKeySpec photonBeetle() {
        return new GordianStreamKeySpec(GordianStreamKeyType.PHOTONBEETLE, GordianLength.LEN_256);
    }

    /**
     * Create romulusKeySpec.
     *
     * @param pSubSpec the subSpec
     * @return the keySpec
     */
    public static GordianStreamKeySpec romulus(final GordianRomulusKey pSubSpec) {
        return new GordianStreamKeySpec(GordianStreamKeyType.ROMULUS, GordianLength.LEN_256, pSubSpec);
    }

    /**
     * Create sparkleKeySpec.
     *
     * @param pSubSpec the subSpec
     * @return the keySpec
     */
    public static GordianStreamKeySpec sparkle(final GordianSparkleKey pSubSpec) {
        return new GordianStreamKeySpec(GordianStreamKeyType.SPARKLE, pSubSpec.requiredKeyLength(), pSubSpec);
    }

    /**
     * Create xoodyakKeySpec.
     *
     * @return the keySpec
     */
    public static GordianStreamKeySpec xoodyak() {
        return new GordianStreamKeySpec(GordianStreamKeyType.XOODYAK, GordianLength.LEN_256);
    }
}
