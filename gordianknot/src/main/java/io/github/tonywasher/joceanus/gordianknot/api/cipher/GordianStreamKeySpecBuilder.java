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
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySubType.GordianNewElephantKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySubType.GordianNewISAPKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySubType.GordianNewRomulusKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySubType.GordianNewSkeinXofKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySubType.GordianNewSparkleKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCoreStreamKeySpecBuilder;

/**
 * GordianKnot StreamKeySpec Builder.
 */
public final class GordianStreamKeySpecBuilder {
    /**
     * StreamKeySpecBuilder.
     */
    private static final GordianNewStreamKeySpecBuilder BUILDER = GordianCoreStreamKeySpecBuilder.newInstance();

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
    public static GordianNewStreamKeySpec hc(final GordianLength pKeyLength) {
        return BUILDER.hc(pKeyLength);
    }

    /**
     * Create chachaKeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianNewStreamKeySpec chacha(final GordianLength pKeyLength) {
        return BUILDER.chacha(pKeyLength);
    }

    /**
     * Create chacha7539KeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianNewStreamKeySpec chacha7539(final GordianLength pKeyLength) {
        return BUILDER.chacha7539(pKeyLength);
    }

    /**
     * Create xchachaKeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianNewStreamKeySpec xchacha(final GordianLength pKeyLength) {
        return BUILDER.xchacha(pKeyLength);
    }

    /**
     * Create salsaKeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianNewStreamKeySpec salsa(final GordianLength pKeyLength) {
        return BUILDER.salsa(pKeyLength);
    }

    /**
     * Create xsalsaKeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianNewStreamKeySpec xsalsa(final GordianLength pKeyLength) {
        return BUILDER.xsalsa(pKeyLength);
    }

    /**
     * Create isaacKeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianNewStreamKeySpec isaac(final GordianLength pKeyLength) {
        return BUILDER.isaac(pKeyLength);
    }

    /**
     * Create rc4KeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianNewStreamKeySpec rc4(final GordianLength pKeyLength) {
        return BUILDER.rc4(pKeyLength);
    }

    /**
     * Create vmpcKeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianNewStreamKeySpec vmpc(final GordianLength pKeyLength) {
        return BUILDER.vmpc(pKeyLength);
    }

    /**
     * Create vmpcKSAKeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianNewStreamKeySpec vmpcKSA(final GordianLength pKeyLength) {
        return BUILDER.vmpcKSA(pKeyLength);
    }

    /**
     * Create grainKeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianNewStreamKeySpec grain(final GordianLength pKeyLength) {
        return BUILDER.grain(pKeyLength);
    }

    /**
     * Create rabbitKeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianNewStreamKeySpec rabbit(final GordianLength pKeyLength) {
        return BUILDER.rabbit(pKeyLength);
    }

    /**
     * Create sosemanukKeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianNewStreamKeySpec sosemanuk(final GordianLength pKeyLength) {
        return BUILDER.sosemanuk(pKeyLength);
    }

    /**
     * Create snow3GKeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianNewStreamKeySpec snow3G(final GordianLength pKeyLength) {
        return BUILDER.snow3G(pKeyLength);
    }

    /**
     * Create zucKeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianNewStreamKeySpec zuc(final GordianLength pKeyLength) {
        return BUILDER.zuc(pKeyLength);
    }

    /**
     * Create skeinKeySpec.
     *
     * @param pKeyLength the keyLength
     * @param pSubSpec   the subSpec
     * @return the keySpec
     */
    public static GordianNewStreamKeySpec skeinXof(final GordianLength pKeyLength,
                                                   final GordianNewSkeinXofKey pSubSpec) {
        return BUILDER.skeinXof(pKeyLength, pSubSpec);
    }

    /**
     * Create blake2KeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianNewStreamKeySpec blake2Xof(final GordianLength pKeyLength) {
        return BUILDER.blake2Xof(pKeyLength);
    }

    /**
     * Create blake3KeySpec.
     *
     * @return the keySpec
     */
    public static GordianNewStreamKeySpec blake3Xof() {
        return BUILDER.blake3Xof();
    }

    /**
     * Create asconKeySpec.
     *
     * @return the keySpec
     */
    public static GordianNewStreamKeySpec ascon() {
        return BUILDER.ascon();
    }

    /**
     * Create elephantKeySpec.
     *
     * @param pSubSpec the subSpec
     * @return the keySpec
     */
    public static GordianNewStreamKeySpec elephant(final GordianNewElephantKey pSubSpec) {
        return BUILDER.elephant(pSubSpec);
    }

    /**
     * Create isapKeySpec.
     *
     * @param pSubSpec the subSpec
     * @return the keySpec
     */
    public static GordianNewStreamKeySpec isap(final GordianNewISAPKey pSubSpec) {
        return BUILDER.isap(pSubSpec);
    }

    /**
     * Create photonBeetleKeySpec.
     *
     * @return the keySpec
     */
    public static GordianNewStreamKeySpec photonBeetle() {
        return BUILDER.photonBeetle();
    }

    /**
     * Create romulusKeySpec.
     *
     * @param pSubSpec the subSpec
     * @return the keySpec
     */
    public static GordianNewStreamKeySpec romulus(final GordianNewRomulusKey pSubSpec) {
        return BUILDER.romulus(pSubSpec);
    }

    /**
     * Create sparkleKeySpec.
     *
     * @param pSubSpec the subSpec
     * @return the keySpec
     */
    public static GordianNewStreamKeySpec sparkle(final GordianNewSparkleKey pSubSpec) {
        return BUILDER.sparkle(pSubSpec);
    }

    /**
     * Create xoodyakKeySpec.
     *
     * @return the keySpec
     */
    public static GordianNewStreamKeySpec xoodyak() {
        return BUILDER.xoodyak();
    }
}
