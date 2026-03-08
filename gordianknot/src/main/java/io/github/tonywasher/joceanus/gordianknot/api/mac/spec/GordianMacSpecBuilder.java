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
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianSymKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSubSpec.GordianDigestState;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestType;

/**
 * Mac Specification Builder.
 */
public interface GordianMacSpecBuilder {
    /**
     * Define MacType.
     *
     * @param pType the type
     * @return the Builder
     */
    GordianMacSpecBuilder withType(GordianMacType pType);

    /**
     * Define keyLength.
     *
     * @param pLength the keyLength
     * @return the Builder
     */
    GordianMacSpecBuilder withKeyLength(GordianLength pLength);

    /**
     * Define digestSubSpec.
     *
     * @param pDigestSpec the digest subSpec
     * @return the Builder
     */
    GordianMacSpecBuilder withDigestSubSpec(GordianDigestSpec pDigestSpec);

    /**
     * Define symKeySubSpec.
     *
     * @param pSymKeySpec the symKeySpec subSpec
     * @return the Builder
     */
    GordianMacSpecBuilder withSymKeySubSpec(GordianSymKeySpec pSymKeySpec);

    /**
     * Define sipHashSubSpec.
     *
     * @param pSipHashSpec the sipHashSpec subSpec
     * @return the Builder
     */
    GordianMacSpecBuilder withSipHashSubSpec(GordianSipHashType pSipHashSpec);

    /**
     * Define lengthSubSpec.
     *
     * @param pLength the length subSpec
     * @return the Builder
     */
    GordianMacSpecBuilder withLengthSubSpec(GordianLength pLength);

    /**
     * Access digestSpecBuilder.
     *
     * @return the digestSpec builder
     */
    GordianDigestSpecBuilder usingDigestSpecBuilder();

    /**
     * Build macSpec.
     *
     * @return the macSpec
     */
    GordianMacSpec build();

    /**
     * Create generic.
     *
     * @param pMacType   the macType
     * @param pKeyLength the keyLength
     * @return the MacSpec
     */
    default GordianMacSpec mac(final GordianMacType pMacType,
                               final GordianLength pKeyLength) {
        return withType(pMacType).withKeyLength(pKeyLength).build();
    }

    /**
     * Create generic.
     *
     * @param pMacType    the macType
     * @param pSymKeySpec the symKeySpec
     * @return the MacSpec
     */
    default GordianMacSpec mac(final GordianMacType pMacType,
                               final GordianSymKeySpec pSymKeySpec) {
        return withType(pMacType).withSymKeySubSpec(pSymKeySpec).build();
    }

    /**
     * Create hMacSpec.
     *
     * @param pDigestType the digestType
     * @return the MacSpec
     */
    default GordianMacSpec hMac(final GordianDigestType pDigestType) {
        return hMac(pDigestType, GordianLength.LEN_128);
    }

    /**
     * Create hMacSpec.
     *
     * @param pDigestType the digestType
     * @param pKeyLength  the keyLength
     * @return the MacSpec
     */
    default GordianMacSpec hMac(final GordianDigestType pDigestType,
                                final GordianLength pKeyLength) {
        return hMac(usingDigestSpecBuilder().withType(pDigestType).build(), pKeyLength);
    }

    /**
     * Create hMacSpec.
     *
     * @param pDigest the digestSpec
     * @return the MacSpec
     */
    default GordianMacSpec hMac(final GordianDigestSpec pDigest) {
        return hMac(pDigest, pDigest.getDigestLength());
    }

    /**
     * Create hMacSpec.
     *
     * @param pDigestSpec the digestSpec
     * @param pKeyLength  the keyLength
     * @return the MacSpec
     */
    default GordianMacSpec hMac(final GordianDigestSpec pDigestSpec,
                                final GordianLength pKeyLength) {
        return withType(GordianMacType.HMAC).withDigestSubSpec(pDigestSpec).withKeyLength(pKeyLength).build();
    }

    /**
     * Create gMacSpec.
     *
     * @param pSymKeySpec the symKeySpec
     * @return the MacSpec
     */
    default GordianMacSpec gMac(final GordianSymKeySpec pSymKeySpec) {
        return withType(GordianMacType.GMAC).withSymKeySubSpec(pSymKeySpec).build();
    }

    /**
     * Create cMacSpec.
     *
     * @param pSymKeySpec the symKeySpec
     * @return the MacSpec
     */
    default GordianMacSpec cMac(final GordianSymKeySpec pSymKeySpec) {
        return withType(GordianMacType.CMAC).withSymKeySubSpec(pSymKeySpec).build();
    }

    /**
     * Create skeinMacSpec.
     *
     * @param pKeyLength the keyLength
     * @return the MacSpec
     */
    default GordianMacSpec kMac(final GordianLength pKeyLength) {
        return kMac(pKeyLength, usingDigestSpecBuilder().shake128());
    }

    /**
     * Create KMACSpec.
     *
     * @param pKeyLength the keyLength
     * @param pSpec      the Shake Spec
     * @return the MacSpec
     */
    default GordianMacSpec kMac(final GordianLength pKeyLength,
                                final GordianDigestSpec pSpec) {
        return withType(GordianMacType.KMAC).withKeyLength(pKeyLength).withDigestSubSpec(pSpec).build();
    }

    /**
     * Create poly1305MacSpec.
     *
     * @return the MacSpec
     */
    default GordianMacSpec poly1305Mac() {
        return withType(GordianMacType.POLY1305).withKeyLength(GordianLength.LEN_256).build();
    }

    /**
     * Create poly1305MacSpec.
     *
     * @param pSymKeySpec the symKeySpec
     * @return the MacSpec
     */
    default GordianMacSpec poly1305Mac(final GordianSymKeySpec pSymKeySpec) {
        return withType(GordianMacType.POLY1305).withSymKeySubSpec(pSymKeySpec).build();
    }

    /**
     * Create skeinMacSpec.
     *
     * @param pKeyLength the keyLength
     * @return the MacSpec
     */
    default GordianMacSpec skeinMac(final GordianLength pKeyLength) {
        return skeinMac(pKeyLength, GordianLength.LEN_256);
    }

    /**
     * Create skeinMacSpec.
     *
     * @param pKeyLength the keyLength
     * @param pLength    the length
     * @return the MacSpec
     */
    default GordianMacSpec skeinMac(final GordianLength pKeyLength,
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
    default GordianMacSpec skeinMac(final GordianLength pKeyLength,
                                    final GordianDigestState pState,
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
    default GordianMacSpec skeinMac(final GordianLength pKeyLength,
                                    final GordianDigestSpec pSpec) {
        return withType(GordianMacType.SKEIN).withKeyLength(pKeyLength).withDigestSubSpec(pSpec).build();
    }

    /**
     * Create skeinXMacSpec.
     *
     * @param pKeyLength the keyLength
     * @param pState     the state
     * @return the MacSpec
     */
    default GordianMacSpec skeinXMac(final GordianLength pKeyLength,
                                     final GordianDigestState pState) {
        return skeinMac(pKeyLength, usingDigestSpecBuilder().skeinX(pState));
    }

    /**
     * Create blake2sMacSpec.
     *
     * @param pKeyLength the length
     * @param pLength    the length
     * @return the MacSpec
     */
    default GordianMacSpec blake2sMac(final GordianLength pKeyLength,
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
    default GordianMacSpec blake2bMac(final GordianLength pKeyLength,
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
    default GordianMacSpec blake2Mac(final GordianLength pKeyLength,
                                     final GordianDigestSpec pSpec) {
        return withType(GordianMacType.BLAKE2).withKeyLength(pKeyLength).withDigestSubSpec(pSpec).build();
    }

    /**
     * Create blake2MacSpec.
     *
     * @param pKeyLength the keyLength
     * @param pState     the blake state
     * @return the MacSpec
     */
    default GordianMacSpec blake2XMac(final GordianLength pKeyLength,
                                      final GordianDigestState pState) {
        return withType(GordianMacType.BLAKE2).withKeyLength(pKeyLength)
                .withDigestSubSpec(usingDigestSpecBuilder().blake2X(pState)).build();
    }

    /**
     * Create blake3MacSpec.
     *
     * @param pMacLength the macLength
     * @return the MacSpec
     */
    default GordianMacSpec blake3Mac(final GordianLength pMacLength) {
        return withType(GordianMacType.BLAKE3).withKeyLength(GordianLength.LEN_256)
                .withDigestSubSpec(usingDigestSpecBuilder().blake3(pMacLength)).build();
    }

    /**
     * Create kalynaMacSpec.
     *
     * @param pKeySpec the keySpec
     * @return the MacSpec
     */
    default GordianMacSpec kalynaMac(final GordianSymKeySpec pKeySpec) {
        return withType(GordianMacType.KALYNA).withSymKeySubSpec(pKeySpec).build();
    }

    /**
     * Create kupynaMacSpec.
     *
     * @param pKeyLength the keyLength
     * @return the MacSpec
     */
    default GordianMacSpec kupynaMac(final GordianLength pKeyLength) {
        return kupynaMac(pKeyLength, GordianLength.LEN_256);
    }

    /**
     * Create kupynaMacSpec.
     *
     * @param pKeyLength the keyLength
     * @param pLength    the length
     * @return the MacSpec
     */
    default GordianMacSpec kupynaMac(final GordianLength pKeyLength,
                                     final GordianLength pLength) {
        return withType(GordianMacType.KUPYNA).withKeyLength(pKeyLength)
                .withDigestSubSpec(usingDigestSpecBuilder().kupyna(pLength)).build();
    }

    /**
     * Create vmpcMacSpec.
     *
     * @param pKeyLength the keyLength
     * @return the MacSpec
     */
    default GordianMacSpec vmpcMac(final GordianLength pKeyLength) {
        return withType(GordianMacType.VMPC).withKeyLength(pKeyLength).build();
    }

    /**
     * Create gostMacSpec.
     *
     * @return the MacSpec
     */
    default GordianMacSpec gostMac() {
        return withType(GordianMacType.GOST).withKeyLength(GordianLength.LEN_256).build();
    }

    /**
     * Create sipHashSpec.
     *
     * @param pSpec the sipHashSpec
     * @return the MacSpec
     */
    default GordianMacSpec sipHash(final GordianSipHashType pSpec) {
        return withType(GordianMacType.SIPHASH).withSipHashSubSpec(pSpec).build();
    }

    /**
     * Create cbcMacSpec.
     *
     * @param pSymKeySpec the symKeySpec
     * @return the MacSpec
     */
    default GordianMacSpec cbcMac(final GordianSymKeySpec pSymKeySpec) {
        return withType(GordianMacType.CBCMAC).withSymKeySubSpec(pSymKeySpec).build();
    }

    /**
     * Create cfbMacSpec.
     *
     * @param pSymKeySpec the symKeySpec
     * @return the MacSpec
     */
    default GordianMacSpec cfbMac(final GordianSymKeySpec pSymKeySpec) {
        return withType(GordianMacType.CFBMAC).withSymKeySubSpec(pSymKeySpec).build();
    }

    /**
     * Create zucMacSpec.
     *
     * @param pKeyLength the keyLength
     * @param pLength    the length
     * @return the MacSpec
     */
    default GordianMacSpec zucMac(final GordianLength pKeyLength,
                                  final GordianLength pLength) {
        return withType(GordianMacType.ZUC).withKeyLength(pKeyLength).withLengthSubSpec(pLength).build();
    }
}
