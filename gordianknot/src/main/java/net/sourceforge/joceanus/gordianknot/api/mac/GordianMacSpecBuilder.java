/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012-2026 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.gordianknot.api.mac;

import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSpecBuilder;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSubSpec.GordianDigestState;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestType;

/**
 * Mac Specification Builder.
 */
public final class GordianMacSpecBuilder {
    /**
     * Private constructor.
     */
    private GordianMacSpecBuilder() {
    }

    /**
     * Create hMacSpec.
     * @param pDigestType the digestType
     * @return the MacSpec
     */
    public static GordianMacSpec hMac(final GordianDigestType pDigestType) {
        return hMac(new GordianDigestSpec(pDigestType), GordianLength.LEN_128);
    }

    /**
     * Create hMacSpec.
     * @param pDigestType the digestType
     * @param pKeyLength the keyLength
     * @return the MacSpec
     */
    public static GordianMacSpec hMac(final GordianDigestType pDigestType,
                                      final GordianLength pKeyLength) {
        return hMac(new GordianDigestSpec(pDigestType), pKeyLength);
    }

    /**
     * Create hMacSpec.
     * @param pDigestSpec the digestSpec
     * @return the MacSpec
     */
    public static GordianMacSpec hMac(final GordianDigestSpec pDigestSpec) {
        return new GordianMacSpec(GordianMacType.HMAC, pDigestSpec.getDigestLength(), pDigestSpec);
    }

    /**
     * Create hMacSpec.
     * @param pDigestSpec the digestSpec
     * @param pKeyLength the keyLength
     * @return the MacSpec
     */
    public static GordianMacSpec hMac(final GordianDigestSpec pDigestSpec,
                                      final GordianLength pKeyLength) {
        return new GordianMacSpec(GordianMacType.HMAC, pKeyLength, pDigestSpec);
    }

    /**
     * Create gMacSpec.
     * @param pSymKeySpec the symKeySpec
     * @return the MacSpec
     */
    public static GordianMacSpec gMac(final GordianSymKeySpec pSymKeySpec) {
        return new GordianMacSpec(GordianMacType.GMAC, pSymKeySpec);
    }

    /**
     * Create cMacSpec.
     * @param pSymKeySpec the symKeySpec
     * @return the MacSpec
     */
    public static GordianMacSpec cMac(final GordianSymKeySpec pSymKeySpec) {
        return new GordianMacSpec(GordianMacType.CMAC, pSymKeySpec);
    }

    /**
     * Create skeinMacSpec.
     * @param pKeyLength the keyLength
     * @return the MacSpec
     */
    public static GordianMacSpec kMac(final GordianLength pKeyLength) {
        return kMac(pKeyLength, GordianDigestSpecBuilder.shake128());
    }

    /**
     * Create KMACSpec.
     * @param pKeyLength the keyLength
     * @param pSpec the Shake Spec
     * @return the MacSpec
     */
    public static GordianMacSpec kMac(final GordianLength pKeyLength,
                                      final GordianDigestSpec pSpec) {
        return new GordianMacSpec(GordianMacType.KMAC, pKeyLength, pSpec);
    }

    /**
     * Create poly1305MacSpec.
     * @return the MacSpec
     */
    public static GordianMacSpec poly1305Mac() {
        return new GordianMacSpec(GordianMacType.POLY1305, GordianLength.LEN_256);
    }

    /**
     * Create poly1305MacSpec.
     * @param pSymKeySpec the symKeySpec
     * @return the MacSpec
     */
    public static GordianMacSpec poly1305Mac(final GordianSymKeySpec pSymKeySpec) {
        return new GordianMacSpec(GordianMacType.POLY1305, pSymKeySpec);
    }

    /**
     * Create skeinMacSpec.
     * @param pKeyLength the keyLength
     * @return the MacSpec
     */
    public static GordianMacSpec skeinMac(final GordianLength pKeyLength) {
        return skeinMac(pKeyLength, GordianDigestType.SKEIN.getDefaultLength());
    }

    /**
     * Create skeinMacSpec.
     * @param pKeyLength the keyLength
     * @param pLength the length
     * @return the MacSpec
     */
    public static GordianMacSpec skeinMac(final GordianLength pKeyLength,
                                          final GordianLength pLength) {
        final GordianDigestSpec mySpec = GordianDigestSpecBuilder.skein(pLength);
        return skeinMac(pKeyLength, mySpec);
    }

    /**
     * Create skeinMacSpec.
     * @param pKeyLength the keyLength
     * @param pState the digestState
     * @param pLength the length
     * @return the MacSpec
     */
    public static GordianMacSpec skeinMac(final GordianLength pKeyLength,
                                          final GordianDigestState pState,
                                          final GordianLength pLength) {
        final GordianDigestSpec mySpec = GordianDigestSpecBuilder.skein(pState, pLength);
        return skeinMac(pKeyLength, mySpec);
    }

    /**
     * Create skeinMacSpec.
     * @param pKeyLength the keyLength
     * @param pSpec the skeinDigestSpec
     * @return the MacSpec
     */
    public static GordianMacSpec skeinMac(final GordianLength pKeyLength,
                                          final GordianDigestSpec pSpec) {
        return new GordianMacSpec(GordianMacType.SKEIN, pKeyLength, pSpec);
    }

    /**
     * Create skeinXMacSpec.
     * @param pKeyLength the keyLength
     * @param pState the state
     * @return the MacSpec
     */
    public static GordianMacSpec skeinXMac(final GordianLength pKeyLength,
                                           final GordianDigestState pState) {
        final GordianDigestSpec mySpec = GordianDigestSpecBuilder.skeinX(pState);
        return skeinMac(pKeyLength, mySpec);
    }

    /**
     * Create blake2sMacSpec.
     * @param pKeyLength the length
     * @param pLength the length
     * @return the MacSpec
     */
    public static GordianMacSpec blake2sMac(final GordianLength pKeyLength,
                                            final GordianLength pLength) {
        final GordianDigestSpec mySpec = GordianDigestSpecBuilder.blake2s(pLength);
        return blake2Mac(pKeyLength, mySpec);
    }

    /**
     * Create blake2bMacSpec.
     * @param pKeyLength the length
     * @param pLength the length
     * @return the MacSpec
     */
    public static GordianMacSpec blake2bMac(final GordianLength pKeyLength,
                                            final GordianLength pLength) {
        final GordianDigestSpec mySpec = GordianDigestSpecBuilder.blake2b(pLength);
        return blake2Mac(pKeyLength, mySpec);
    }

    /**
     * Create blake2MacSpec.
     * @param pKeyLength the keyLength
     * @param pSpec the blake digestSpec
     * @return the MacSpec
     */
    public static GordianMacSpec blake2Mac(final GordianLength pKeyLength,
                                           final GordianDigestSpec pSpec) {
        return new GordianMacSpec(GordianMacType.BLAKE2, pKeyLength, pSpec);
    }

    /**
     * Create blake2MacSpec.
     * @param pKeyLength the keyLength
     * @param pState the blake state
     * @return the MacSpec
     */
    public static GordianMacSpec blake2XMac(final GordianLength pKeyLength,
                                            final GordianDigestState pState) {
        return new GordianMacSpec(GordianMacType.BLAKE2, pKeyLength, GordianDigestSpecBuilder.blake2X(pState));
    }

    /**
     * Create blake3MacSpec.
     * @param pMacLength the macLength
     * @return the MacSpec
     */
    public static GordianMacSpec blake3Mac(final GordianLength pMacLength) {
        return new GordianMacSpec(GordianMacType.BLAKE3, GordianLength.LEN_256, GordianDigestSpecBuilder.blake3(pMacLength));
    }

    /**
     * Create kalynaMacSpec.
     * @param pKeySpec the keySpec
     * @return the MacSpec
     */
    public static GordianMacSpec kalynaMac(final GordianSymKeySpec pKeySpec) {
        return new GordianMacSpec(GordianMacType.KALYNA, pKeySpec);
    }

    /**
     * Create kupynaMacSpec.
     * @param pKeyLength the keyLength
     * @return the MacSpec
     */
    public static GordianMacSpec kupynaMac(final GordianLength pKeyLength) {
        return kupynaMac(pKeyLength, GordianDigestType.KUPYNA.getDefaultLength());
    }

    /**
     * Create kupynaMacSpec.
     * @param pKeyLength the keyLength
     * @param pLength the length
     * @return the MacSpec
     */
    public static GordianMacSpec kupynaMac(final GordianLength pKeyLength,
                                           final GordianLength pLength) {
        final GordianDigestSpec mySpec = GordianDigestSpecBuilder.kupyna(pLength);
        return new GordianMacSpec(GordianMacType.KUPYNA, pKeyLength, mySpec);
    }

    /**
     * Create vmpcMacSpec.
     * @param pKeyLength the keyLength
     * @return the MacSpec
     */
    public static GordianMacSpec vmpcMac(final GordianLength pKeyLength) {
        return new GordianMacSpec(GordianMacType.VMPC, pKeyLength);
    }

    /**
     * Create gostMacSpec.
     * @return the MacSpec
     */
    public static GordianMacSpec gostMac() {
        return new GordianMacSpec(GordianMacType.GOST, GordianLength.LEN_256);
    }

    /**
     * Create sipHashSpec.
     * @param pSpec the sipHashSpec
     * @return the MacSpec
     */
    public static GordianMacSpec sipHash(final GordianSipHashSpec pSpec) {
        return new GordianMacSpec(GordianMacType.SIPHASH, pSpec);
    }

    /**
     * Create cbcMacSpec.
     * @param pSymKeySpec the symKeySpec
     * @return the MacSpec
     */
    public static GordianMacSpec cbcMac(final GordianSymKeySpec pSymKeySpec) {
        return new GordianMacSpec(GordianMacType.CBCMAC, pSymKeySpec);
    }

    /**
     * Create cfbMacSpec.
     * @param pSymKeySpec the symKeySpec
     * @return the MacSpec
     */
    public static GordianMacSpec cfbMac(final GordianSymKeySpec pSymKeySpec) {
        return new GordianMacSpec(GordianMacType.CFBMAC, pSymKeySpec);
    }

    /**
     * Create zucMacSpec.
     * @param pKeyLength the keyLength
     * @param pLength the length
     * @return the MacSpec
     */
    public static GordianMacSpec zucMac(final GordianLength pKeyLength,
                                        final GordianLength pLength) {
        return new GordianMacSpec(GordianMacType.ZUC, pKeyLength, pLength);
    }
}
