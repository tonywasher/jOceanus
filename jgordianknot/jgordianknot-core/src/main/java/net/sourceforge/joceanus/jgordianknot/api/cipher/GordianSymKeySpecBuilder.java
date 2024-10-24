/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.api.cipher;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;

/**
 * SymKey specification Builder.
 */
public final class GordianSymKeySpecBuilder {
    /**
     * Private constructor.
     */
    private GordianSymKeySpecBuilder() {
    }

    /**
     * Create aesKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianSymKeySpec aes(final GordianLength pKeyLength) {
        return new GordianSymKeySpec(GordianSymKeyType.AES, pKeyLength);
    }

    /**
     * Create serpentKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianSymKeySpec serpent(final GordianLength pKeyLength) {
        return new GordianSymKeySpec(GordianSymKeyType.SERPENT, pKeyLength);
    }

    /**
     * Create twoFishKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianSymKeySpec twoFish(final GordianLength pKeyLength) {
        return new GordianSymKeySpec(GordianSymKeyType.TWOFISH, pKeyLength);
    }

    /**
     * Create threeFishKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianSymKeySpec threeFish(final GordianLength pKeyLength) {
        return new GordianSymKeySpec(GordianSymKeyType.THREEFISH, pKeyLength);
    }

    /**
     * Create camelliaKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianSymKeySpec camellia(final GordianLength pKeyLength) {
        return new GordianSymKeySpec(GordianSymKeyType.CAMELLIA, pKeyLength);
    }

    /**
     * Create rc2KeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianSymKeySpec rc2(final GordianLength pKeyLength) {
        return new GordianSymKeySpec(GordianSymKeyType.RC2, pKeyLength);
    }

    /**
     * Create rc5KeySpec.
     * @return the keySpec
     */
    public static GordianSymKeySpec rc5() {
        return new GordianSymKeySpec(GordianSymKeyType.RC5, GordianLength.LEN_128);
    }

    /**
     * Create rc5KeySpec.
     * @param pBlockLength the blockLength
     * @return the keySpec
     */
    public static GordianSymKeySpec rc5(final GordianLength pBlockLength) {
        return new GordianSymKeySpec(GordianSymKeyType.RC5, pBlockLength, GordianLength.LEN_128);
    }

    /**
     * Create rc6KeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianSymKeySpec rc6(final GordianLength pKeyLength) {
        return new GordianSymKeySpec(GordianSymKeyType.RC6, pKeyLength);
    }

    /**
     * Create cast5KeySpec.
     * @return the keySpec
     */
    public static GordianSymKeySpec cast5() {
        return new GordianSymKeySpec(GordianSymKeyType.CAST5, GordianLength.LEN_128);
    }

    /**
     * Create cast6KeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianSymKeySpec cast6(final GordianLength pKeyLength) {
        return new GordianSymKeySpec(GordianSymKeyType.CAST6, pKeyLength);
    }

    /**
     * Create ariaKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianSymKeySpec aria(final GordianLength pKeyLength) {
        return new GordianSymKeySpec(GordianSymKeyType.ARIA, pKeyLength);
    }

    /**
     * Create sm4KeySpec.
     * @return the keySpec
     */
    public static GordianSymKeySpec sm4() {
        return new GordianSymKeySpec(GordianSymKeyType.SM4, GordianLength.LEN_128);
    }

    /**
     * Create noeKeonKeySpec.
     * @return the keySpec
     */
    public static GordianSymKeySpec noekeon() {
        return new GordianSymKeySpec(GordianSymKeyType.NOEKEON, GordianLength.LEN_128);
    }

    /**
     * Create seedKeySpec.
     * @return the keySpec
     */
    public static GordianSymKeySpec seed() {
        return new GordianSymKeySpec(GordianSymKeyType.SEED, GordianLength.LEN_128);
    }

    /**
     * Create teaKeySpec.
     * @return the keySpec
     */
    public static GordianSymKeySpec tea() {
        return new GordianSymKeySpec(GordianSymKeyType.TEA, GordianLength.LEN_128);
    }

    /**
     * Create xteaKeySpec.
     * @return the keySpec
     */
    public static GordianSymKeySpec xtea() {
        return new GordianSymKeySpec(GordianSymKeyType.XTEA, GordianLength.LEN_128);
    }

    /**
     * Create ideaKeySpec.
     * @return the keySpec
     */
    public static GordianSymKeySpec idea() {
        return new GordianSymKeySpec(GordianSymKeyType.IDEA, GordianLength.LEN_128);
    }

    /**
     * Create skipjackKeySpec.
     * @return the keySpec
     */
    public static GordianSymKeySpec skipjack() {
        return new GordianSymKeySpec(GordianSymKeyType.SKIPJACK, GordianLength.LEN_128);
    }

    /**
     * Create desedeKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianSymKeySpec desede(final GordianLength pKeyLength) {
        return new GordianSymKeySpec(GordianSymKeyType.DESEDE, pKeyLength);
    }

    /**
     * Create blowfishKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianSymKeySpec blowfish(final GordianLength pKeyLength) {
        return new GordianSymKeySpec(GordianSymKeyType.BLOWFISH, pKeyLength);
    }

    /**
     * Create kalynaKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianSymKeySpec kalyna(final GordianLength pKeyLength) {
        return new GordianSymKeySpec(GordianSymKeyType.KALYNA, pKeyLength);
    }

    /**
     * Create kalynaKeySpec.
     * @param pBlockLength the block length
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianSymKeySpec kalyna(final GordianLength pBlockLength,
                                           final GordianLength pKeyLength) {
        return new GordianSymKeySpec(GordianSymKeyType.KALYNA, pBlockLength, pKeyLength);
    }

    /**
     * Create speckKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianSymKeySpec speck(final GordianLength pKeyLength) {
        return new GordianSymKeySpec(GordianSymKeyType.SPECK, pKeyLength);
    }

    /**
     * Create simonKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianSymKeySpec simon(final GordianLength pKeyLength) {
        return new GordianSymKeySpec(GordianSymKeyType.SIMON, pKeyLength);
    }

    /**
     * Create marsKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianSymKeySpec mars(final GordianLength pKeyLength) {
        return new GordianSymKeySpec(GordianSymKeyType.MARS, pKeyLength);
    }

    /**
     * Create anubisKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianSymKeySpec anubis(final GordianLength pKeyLength) {
        return new GordianSymKeySpec(GordianSymKeyType.ANUBIS, pKeyLength);
    }

    /**
     * Create leaKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianSymKeySpec lea(final GordianLength pKeyLength) {
        return new GordianSymKeySpec(GordianSymKeyType.LEA, pKeyLength);
    }
}
