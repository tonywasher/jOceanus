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
package io.github.tonywasher.joceanus.gordianknot.api.mac;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSubSpec.GordianNewDigestState;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestType;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianNewMacSpec;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianNewMacSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianNewSipHashType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.mac.GordianCoreMacSpecBuilder;

/**
 * Mac Specification Builder.
 */
public final class GordianMacSpecBuilder {
    /**
     * DigestSpecBuilder.
     */
    private static final GordianNewMacSpecBuilder BUILDER = GordianCoreMacSpecBuilder.newInstance();

    /**
     * Private constructor.
     */
    private GordianMacSpecBuilder() {
    }

    /**
     * Create hMacSpec.
     *
     * @param pDigestType the digestType
     * @return the MacSpec
     */
    public static GordianNewMacSpec hMac(final GordianNewDigestType pDigestType) {
        return hMac(pDigestType, GordianLength.LEN_128);
    }

    /**
     * Create hMacSpec.
     *
     * @param pDigestType the digestType
     * @param pKeyLength  the keyLength
     * @return the MacSpec
     */
    public static GordianNewMacSpec hMac(final GordianNewDigestType pDigestType,
                                         final GordianLength pKeyLength) {
        return BUILDER.hMac(pDigestType, pKeyLength);
    }

    /**
     * Create hMacSpec.
     *
     * @param pDigestSpec the digestSpec
     * @return the MacSpec
     */
    public static GordianNewMacSpec hMac(final GordianNewDigestSpec pDigestSpec) {
        return BUILDER.hMac(pDigestSpec);
    }

    /**
     * Create hMacSpec.
     *
     * @param pDigestSpec the digestSpec
     * @param pKeyLength  the keyLength
     * @return the MacSpec
     */
    public static GordianNewMacSpec hMac(final GordianNewDigestSpec pDigestSpec,
                                         final GordianLength pKeyLength) {
        return BUILDER.hMac(pDigestSpec, pKeyLength);
    }

    /**
     * Create gMacSpec.
     *
     * @param pSymKeySpec the symKeySpec
     * @return the MacSpec
     */
    public static GordianNewMacSpec gMac(final GordianNewSymKeySpec pSymKeySpec) {
        return BUILDER.gMac(pSymKeySpec);
    }

    /**
     * Create cMacSpec.
     *
     * @param pSymKeySpec the symKeySpec
     * @return the MacSpec
     */
    public static GordianNewMacSpec cMac(final GordianNewSymKeySpec pSymKeySpec) {
        return BUILDER.cMac(pSymKeySpec);
    }

    /**
     * Create skeinMacSpec.
     *
     * @param pKeyLength the keyLength
     * @return the MacSpec
     */
    public static GordianNewMacSpec kMac(final GordianLength pKeyLength) {
        return BUILDER.kMac(pKeyLength);
    }

    /**
     * Create KMACSpec.
     *
     * @param pKeyLength the keyLength
     * @param pSpec      the Shake Spec
     * @return the MacSpec
     */
    public static GordianNewMacSpec kMac(final GordianLength pKeyLength,
                                         final GordianNewDigestSpec pSpec) {
        return BUILDER.kMac(pKeyLength, pSpec);
    }

    /**
     * Create poly1305MacSpec.
     *
     * @return the MacSpec
     */
    public static GordianNewMacSpec poly1305Mac() {
        return BUILDER.poly1305Mac();
    }

    /**
     * Create poly1305MacSpec.
     *
     * @param pSymKeySpec the symKeySpec
     * @return the MacSpec
     */
    public static GordianNewMacSpec poly1305Mac(final GordianNewSymKeySpec pSymKeySpec) {
        return BUILDER.poly1305Mac(pSymKeySpec);
    }

    /**
     * Create skeinMacSpec.
     *
     * @param pKeyLength the keyLength
     * @return the MacSpec
     */
    public static GordianNewMacSpec skeinMac(final GordianLength pKeyLength) {
        return BUILDER.skeinMac(pKeyLength);
    }

    /**
     * Create skeinMacSpec.
     *
     * @param pKeyLength the keyLength
     * @param pLength    the length
     * @return the MacSpec
     */
    public static GordianNewMacSpec skeinMac(final GordianLength pKeyLength,
                                             final GordianLength pLength) {
        return BUILDER.skeinMac(pKeyLength, pLength);
    }

    /**
     * Create skeinMacSpec.
     *
     * @param pKeyLength the keyLength
     * @param pState     the digestState
     * @param pLength    the length
     * @return the MacSpec
     */
    public static GordianNewMacSpec skeinMac(final GordianLength pKeyLength,
                                             final GordianNewDigestState pState,
                                             final GordianLength pLength) {
        return BUILDER.skeinMac(pKeyLength, pState, pLength);
    }

    /**
     * Create skeinMacSpec.
     *
     * @param pKeyLength the keyLength
     * @param pSpec      the skeinDigestSpec
     * @return the MacSpec
     */
    public static GordianNewMacSpec skeinMac(final GordianLength pKeyLength,
                                             final GordianNewDigestSpec pSpec) {
        return BUILDER.skeinMac(pKeyLength, pSpec);
    }

    /**
     * Create skeinXMacSpec.
     *
     * @param pKeyLength the keyLength
     * @param pState     the state
     * @return the MacSpec
     */
    public static GordianNewMacSpec skeinXMac(final GordianLength pKeyLength,
                                              final GordianNewDigestState pState) {
        return BUILDER.skeinXMac(pKeyLength, pState);
    }

    /**
     * Create blake2sMacSpec.
     *
     * @param pKeyLength the length
     * @param pLength    the length
     * @return the MacSpec
     */
    public static GordianNewMacSpec blake2sMac(final GordianLength pKeyLength,
                                               final GordianLength pLength) {
        return BUILDER.blake2sMac(pKeyLength, pLength);
    }

    /**
     * Create blake2bMacSpec.
     *
     * @param pKeyLength the length
     * @param pLength    the length
     * @return the MacSpec
     */
    public static GordianNewMacSpec blake2bMac(final GordianLength pKeyLength,
                                               final GordianLength pLength) {
        return BUILDER.blake2bMac(pKeyLength, pLength);
    }

    /**
     * Create blake2MacSpec.
     *
     * @param pKeyLength the keyLength
     * @param pSpec      the blake digestSpec
     * @return the MacSpec
     */
    public static GordianNewMacSpec blake2Mac(final GordianLength pKeyLength,
                                              final GordianNewDigestSpec pSpec) {
        return BUILDER.blake2Mac(pKeyLength, pSpec);
    }

    /**
     * Create blake2MacSpec.
     *
     * @param pKeyLength the keyLength
     * @param pState     the blake state
     * @return the MacSpec
     */
    public static GordianNewMacSpec blake2XMac(final GordianLength pKeyLength,
                                               final GordianNewDigestState pState) {
        return BUILDER.blake2XMac(pKeyLength, pState);
    }

    /**
     * Create blake3MacSpec.
     *
     * @param pMacLength the macLength
     * @return the MacSpec
     */
    public static GordianNewMacSpec blake3Mac(final GordianLength pMacLength) {
        return BUILDER.blake3Mac(pMacLength);
    }

    /**
     * Create kalynaMacSpec.
     *
     * @param pKeySpec the keySpec
     * @return the MacSpec
     */
    public static GordianNewMacSpec kalynaMac(final GordianNewSymKeySpec pKeySpec) {
        return BUILDER.kalynaMac(pKeySpec);
    }

    /**
     * Create kupynaMacSpec.
     *
     * @param pKeyLength the keyLength
     * @return the MacSpec
     */
    public static GordianNewMacSpec kupynaMac(final GordianLength pKeyLength) {
        return BUILDER.kupynaMac(pKeyLength);
    }

    /**
     * Create kupynaMacSpec.
     *
     * @param pKeyLength the keyLength
     * @param pLength    the length
     * @return the MacSpec
     */
    public static GordianNewMacSpec kupynaMac(final GordianLength pKeyLength,
                                              final GordianLength pLength) {
        return BUILDER.kupynaMac(pKeyLength, pLength);
    }

    /**
     * Create vmpcMacSpec.
     *
     * @param pKeyLength the keyLength
     * @return the MacSpec
     */
    public static GordianNewMacSpec vmpcMac(final GordianLength pKeyLength) {
        return BUILDER.vmpcMac(pKeyLength);
    }

    /**
     * Create gostMacSpec.
     *
     * @return the MacSpec
     */
    public static GordianNewMacSpec gostMac() {
        return BUILDER.gostMac();
    }

    /**
     * Create sipHashSpec.
     *
     * @param pType the sipHashType
     * @return the MacSpec
     */
    public static GordianNewMacSpec sipHash(final GordianNewSipHashType pType) {
        return BUILDER.sipHash(pType);
    }

    /**
     * Create cbcMacSpec.
     *
     * @param pSymKeySpec the symKeySpec
     * @return the MacSpec
     */
    public static GordianNewMacSpec cbcMac(final GordianNewSymKeySpec pSymKeySpec) {
        return BUILDER.cbcMac(pSymKeySpec);
    }

    /**
     * Create cfbMacSpec.
     *
     * @param pSymKeySpec the symKeySpec
     * @return the MacSpec
     */
    public static GordianNewMacSpec cfbMac(final GordianNewSymKeySpec pSymKeySpec) {
        return BUILDER.cfbMac(pSymKeySpec);
    }

    /**
     * Create zucMacSpec.
     *
     * @param pKeyLength the keyLength
     * @param pLength    the length
     * @return the MacSpec
     */
    public static GordianNewMacSpec zucMac(final GordianLength pKeyLength,
                                           final GordianLength pLength) {
        return BUILDER.zucMac(pKeyLength, pLength);
    }
}
