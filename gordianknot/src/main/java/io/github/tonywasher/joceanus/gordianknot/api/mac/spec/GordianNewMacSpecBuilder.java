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

package io.github.tonywasher.joceanus.gordianknot.api.mac.spec;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSubSpec.GordianNewDigestState;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestType;

/**
 * Mac Specification Builder.
 */
public interface GordianNewMacSpecBuilder {
    /**
     * Define MacType.
     *
     * @param pType the type
     * @return the Builder
     */
    GordianNewMacSpecBuilder withType(GordianNewMacType pType);

    /**
     * Define keyLength.
     *
     * @param pLength the keyLength
     * @return the Builder
     */
    GordianNewMacSpecBuilder withKeyLength(GordianLength pLength);

    /**
     * Define digestSubSpec.
     *
     * @param pDigestSpec the digest subSpec
     * @return the Builder
     */
    GordianNewMacSpecBuilder withDigestSubSpec(GordianNewDigestSpec pDigestSpec);

    /**
     * Define symKeySubSpec.
     *
     * @param pSymKeySpec the symKeySpec subSpec
     * @return the Builder
     */
    GordianNewMacSpecBuilder withSymKeySubSpec(GordianNewSymKeySpec pSymKeySpec);

    /**
     * Define sipHashSubSpec.
     *
     * @param pSipHashSpec the sipHashSpec subSpec
     * @return the Builder
     */
    GordianNewMacSpecBuilder withSipHashSubSpec(GordianNewSipHashType pSipHashSpec);

    /**
     * Define lengthSubSpec.
     *
     * @param pLength the length subSpec
     * @return the Builder
     */
    GordianNewMacSpecBuilder withLengthSubSpec(GordianLength pLength);

    /**
     * Access digestSpecBuilder.
     *
     * @return the digestSpec builder
     */
    GordianNewDigestSpecBuilder usingDigestSpecBuilder();

    /**
     * Build macSpec.
     *
     * @return the macSpec
     */
    GordianNewMacSpec build();

    /**
     * Create hMacSpec.
     *
     * @param pDigestType the digestType
     * @return the MacSpec
     */
    default GordianNewMacSpec hMac(final GordianNewDigestType pDigestType) {
        return hMac(usingDigestSpecBuilder().withType(pDigestType).build(), GordianLength.LEN_128);
    }

    /**
     * Create hMacSpec.
     *
     * @param pDigest the digestSpec
     * @return the MacSpec
     */
    default GordianNewMacSpec hMac(final GordianNewDigestSpec pDigest) {
        return hMac(pDigest, pDigest.getDigestLength());
    }

    /**
     * Create hMacSpec.
     *
     * @param pDigestSpec the digestSpec
     * @param pKeyLength  the keyLength
     * @return the MacSpec
     */
    default GordianNewMacSpec hMac(final GordianNewDigestSpec pDigestSpec,
                                   final GordianLength pKeyLength) {
        return withType(GordianNewMacType.HMAC).withDigestSubSpec(pDigestSpec).withKeyLength(pKeyLength).build();
    }

    /**
     * Create gMacSpec.
     *
     * @param pSymKeySpec the symKeySpec
     * @return the MacSpec
     */
    default GordianNewMacSpec gMac(final GordianNewSymKeySpec pSymKeySpec) {
        return withType(GordianNewMacType.GMAC).withSymKeySubSpec(pSymKeySpec).build();
    }

    /**
     * Create cMacSpec.
     *
     * @param pSymKeySpec the symKeySpec
     * @return the MacSpec
     */
    default GordianNewMacSpec cMac(final GordianNewSymKeySpec pSymKeySpec) {
        return withType(GordianNewMacType.CMAC).withSymKeySubSpec(pSymKeySpec).build();
    }

    /**
     * Create skeinMacSpec.
     *
     * @param pKeyLength the keyLength
     * @return the MacSpec
     */
    default GordianNewMacSpec kMac(final GordianLength pKeyLength) {
        return kMac(pKeyLength, usingDigestSpecBuilder().shake128());
    }

    /**
     * Create KMACSpec.
     *
     * @param pKeyLength the keyLength
     * @param pSpec      the Shake Spec
     * @return the MacSpec
     */
    default GordianNewMacSpec kMac(final GordianLength pKeyLength,
                                   final GordianNewDigestSpec pSpec) {
        return withType(GordianNewMacType.KMAC).withKeyLength(pKeyLength).withDigestSubSpec(pSpec).build();
    }

    /**
     * Create poly1305MacSpec.
     *
     * @return the MacSpec
     */
    default GordianNewMacSpec poly1305Mac() {
        return withType(GordianNewMacType.POLY1305).withKeyLength(GordianLength.LEN_256).build();
    }

    /**
     * Create poly1305MacSpec.
     *
     * @param pSymKeySpec the symKeySpec
     * @return the MacSpec
     */
    default GordianNewMacSpec poly1305Mac(final GordianNewSymKeySpec pSymKeySpec) {
        return withType(GordianNewMacType.POLY1305).withSymKeySubSpec(pSymKeySpec).build();
    }

    /**
     * Create skeinMacSpec.
     *
     * @param pKeyLength the keyLength
     * @return the MacSpec
     */
    default GordianNewMacSpec skeinMac(final GordianLength pKeyLength) {
        return skeinMac(pKeyLength, GordianLength.LEN_256);
    }

    /**
     * Create skeinMacSpec.
     *
     * @param pKeyLength the keyLength
     * @param pLength    the length
     * @return the MacSpec
     */
    default GordianNewMacSpec skeinMac(final GordianLength pKeyLength,
                                       final GordianLength pLength) {
        return skeinMac(pKeyLength, usingDigestSpecBuilder().skein(pLength));
    }

    /**
     * Create skeinMacSpec.
     *
     * @param pKeyLength the keyLength
     * @param pState     the digestState
     * @param pLength    the length
     * @return the MacSpec
     */
    default GordianNewMacSpec skeinMac(final GordianLength pKeyLength,
                                       final GordianNewDigestState pState,
                                       final GordianLength pLength) {
        return skeinMac(pKeyLength, usingDigestSpecBuilder().skein(pState, pLength));
    }

    /**
     * Create skeinMacSpec.
     *
     * @param pKeyLength the keyLength
     * @param pSpec      the skeinDigestSpec
     * @return the MacSpec
     */
    default GordianNewMacSpec skeinMac(final GordianLength pKeyLength,
                                       final GordianNewDigestSpec pSpec) {
        return withType(GordianNewMacType.SKEIN).withKeyLength(pKeyLength).withDigestSubSpec(pSpec).build();
    }

    /**
     * Create skeinXMacSpec.
     *
     * @param pKeyLength the keyLength
     * @param pState     the state
     * @return the MacSpec
     */
    default GordianNewMacSpec skeinXMac(final GordianLength pKeyLength,
                                        final GordianNewDigestState pState) {
        return skeinMac(pKeyLength, usingDigestSpecBuilder().skeinX(pState));
    }

    /**
     * Create blake2sMacSpec.
     *
     * @param pKeyLength the length
     * @param pLength    the length
     * @return the MacSpec
     */
    default GordianNewMacSpec blake2sMac(final GordianLength pKeyLength,
                                         final GordianLength pLength) {
        return blake2Mac(pKeyLength, usingDigestSpecBuilder().blake2s(pLength));
    }

    /**
     * Create blake2bMacSpec.
     *
     * @param pKeyLength the length
     * @param pLength    the length
     * @return the MacSpec
     */
    default GordianNewMacSpec blake2bMac(final GordianLength pKeyLength,
                                         final GordianLength pLength) {
        return blake2Mac(pKeyLength, usingDigestSpecBuilder().blake2b(pLength));
    }

    /**
     * Create blake2MacSpec.
     *
     * @param pKeyLength the keyLength
     * @param pSpec      the blake digestSpec
     * @return the MacSpec
     */
    default GordianNewMacSpec blake2Mac(final GordianLength pKeyLength,
                                        final GordianNewDigestSpec pSpec) {
        return withType(GordianNewMacType.BLAKE2).withKeyLength(pKeyLength).withDigestSubSpec(pSpec).build();
    }

    /**
     * Create blake2MacSpec.
     *
     * @param pKeyLength the keyLength
     * @param pState     the blake state
     * @return the MacSpec
     */
    default GordianNewMacSpec blake2XMac(final GordianLength pKeyLength,
                                         final GordianNewDigestState pState) {
        return withType(GordianNewMacType.BLAKE2).withKeyLength(pKeyLength)
                .withDigestSubSpec(usingDigestSpecBuilder().blake2X(pState)).build();
    }

    /**
     * Create blake3MacSpec.
     *
     * @param pMacLength the macLength
     * @return the MacSpec
     */
    default GordianNewMacSpec blake3Mac(final GordianLength pMacLength) {
        return withType(GordianNewMacType.BLAKE3).withKeyLength(GordianLength.LEN_256)
                .withDigestSubSpec(usingDigestSpecBuilder().blake3(pMacLength)).build();
    }

    /**
     * Create kalynaMacSpec.
     *
     * @param pKeySpec the keySpec
     * @return the MacSpec
     */
    default GordianNewMacSpec kalynaMac(final GordianNewSymKeySpec pKeySpec) {
        return withType(GordianNewMacType.KALYNA).withSymKeySubSpec(pKeySpec).build();
    }

    /**
     * Create kupynaMacSpec.
     *
     * @param pKeyLength the keyLength
     * @return the MacSpec
     */
    default GordianNewMacSpec kupynaMac(final GordianLength pKeyLength) {
        return kupynaMac(pKeyLength, GordianLength.LEN_256);
    }

    /**
     * Create kupynaMacSpec.
     *
     * @param pKeyLength the keyLength
     * @param pLength    the length
     * @return the MacSpec
     */
    default GordianNewMacSpec kupynaMac(final GordianLength pKeyLength,
                                        final GordianLength pLength) {
        return withType(GordianNewMacType.KUPYNA).withKeyLength(pKeyLength)
                .withDigestSubSpec(usingDigestSpecBuilder().kupyna(pLength)).build();
    }

    /**
     * Create vmpcMacSpec.
     *
     * @param pKeyLength the keyLength
     * @return the MacSpec
     */
    default GordianNewMacSpec vmpcMac(final GordianLength pKeyLength) {
        return withType(GordianNewMacType.VMPC).withKeyLength(pKeyLength).build();
    }

    /**
     * Create gostMacSpec.
     *
     * @return the MacSpec
     */
    default GordianNewMacSpec gostMac() {
        return withType(GordianNewMacType.GOST).withKeyLength(GordianLength.LEN_256).build();
    }

    /**
     * Create sipHashSpec.
     *
     * @param pSpec the sipHashSpec
     * @return the MacSpec
     */
    default GordianNewMacSpec sipHash(final GordianNewSipHashType pSpec) {
        return withType(GordianNewMacType.SIPHASH).withSipHashSubSpec(pSpec).build();
    }

    /**
     * Create cbcMacSpec.
     *
     * @param pSymKeySpec the symKeySpec
     * @return the MacSpec
     */
    default GordianNewMacSpec cbcMac(final GordianNewSymKeySpec pSymKeySpec) {
        return withType(GordianNewMacType.CBCMAC).withSymKeySubSpec(pSymKeySpec).build();
    }

    /**
     * Create cfbMacSpec.
     *
     * @param pSymKeySpec the symKeySpec
     * @return the MacSpec
     */
    default GordianNewMacSpec cfbMac(final GordianNewSymKeySpec pSymKeySpec) {
        return withType(GordianNewMacType.CFBMAC).withSymKeySubSpec(pSymKeySpec).build();
    }

    /**
     * Create zucMacSpec.
     *
     * @param pKeyLength the keyLength
     * @param pLength    the length
     * @return the MacSpec
     */
    default GordianNewMacSpec zucMac(final GordianLength pKeyLength,
                                     final GordianLength pLength) {
        return withType(GordianNewMacType.ZUC).withKeyLength(pKeyLength).withLengthSubSpec(pLength).build();
    }
}
