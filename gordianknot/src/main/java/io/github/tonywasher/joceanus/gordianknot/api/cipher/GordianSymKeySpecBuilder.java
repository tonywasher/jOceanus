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
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymKeySpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCoreSymKeySpecBuilder;

/**
 * SymKey specification Builder.
 */
public final class GordianSymKeySpecBuilder {
    /**
     * SymKeySpecBuilder.
     */
    private static final GordianNewSymKeySpecBuilder BUILDER = GordianCoreSymKeySpecBuilder.newInstance();

    /**
     * Private constructor.
     */
    private GordianSymKeySpecBuilder() {
    }

    /**
     * Create aesKeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianNewSymKeySpec aes(final GordianLength pKeyLength) {
        return BUILDER.aes(pKeyLength);
    }

    /**
     * Create serpentKeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianNewSymKeySpec serpent(final GordianLength pKeyLength) {
        return BUILDER.serpent(pKeyLength);
    }

    /**
     * Create twoFishKeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianNewSymKeySpec twoFish(final GordianLength pKeyLength) {
        return BUILDER.twoFish(pKeyLength);
    }

    /**
     * Create threeFishKeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianNewSymKeySpec threeFish(final GordianLength pKeyLength) {
        return BUILDER.threeFish(pKeyLength);
    }

    /**
     * Create camelliaKeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianNewSymKeySpec camellia(final GordianLength pKeyLength) {
        return BUILDER.camellia(pKeyLength);
    }

    /**
     * Create rc2KeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianNewSymKeySpec rc2(final GordianLength pKeyLength) {
        return BUILDER.rc2(pKeyLength);
    }

    /**
     * Create rc5KeySpec.
     *
     * @return the keySpec
     */
    public static GordianNewSymKeySpec rc5() {
        return BUILDER.rc5();
    }

    /**
     * Create rc5KeySpec.
     *
     * @param pBlockLength the blockLength
     * @return the keySpec
     */
    public static GordianNewSymKeySpec rc5(final GordianLength pBlockLength) {
        return BUILDER.rc5(pBlockLength);
    }

    /**
     * Create rc6KeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianNewSymKeySpec rc6(final GordianLength pKeyLength) {
        return BUILDER.rc6(pKeyLength);
    }

    /**
     * Create cast5KeySpec.
     *
     * @return the keySpec
     */
    public static GordianNewSymKeySpec cast5() {
        return BUILDER.cast5();
    }

    /**
     * Create cast6KeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianNewSymKeySpec cast6(final GordianLength pKeyLength) {
        return BUILDER.cast6(pKeyLength);
    }

    /**
     * Create ariaKeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianNewSymKeySpec aria(final GordianLength pKeyLength) {
        return BUILDER.aria(pKeyLength);
    }

    /**
     * Create sm4KeySpec.
     *
     * @return the keySpec
     */
    public static GordianNewSymKeySpec sm4() {
        return BUILDER.sm4();
    }

    /**
     * Create noeKeonKeySpec.
     *
     * @return the keySpec
     */
    public static GordianNewSymKeySpec noekeon() {
        return BUILDER.noekeon();
    }

    /**
     * Create seedKeySpec.
     *
     * @return the keySpec
     */
    public static GordianNewSymKeySpec seed() {
        return BUILDER.seed();
    }

    /**
     * Create teaKeySpec.
     *
     * @return the keySpec
     */
    public static GordianNewSymKeySpec tea() {
        return BUILDER.tea();
    }

    /**
     * Create xteaKeySpec.
     *
     * @return the keySpec
     */
    public static GordianNewSymKeySpec xtea() {
        return BUILDER.xtea();
    }

    /**
     * Create ideaKeySpec.
     *
     * @return the keySpec
     */
    public static GordianNewSymKeySpec idea() {
        return BUILDER.idea();
    }

    /**
     * Create skipjackKeySpec.
     *
     * @return the keySpec
     */
    public static GordianNewSymKeySpec skipjack() {
        return BUILDER.skipjack();
    }

    /**
     * Create desedeKeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianNewSymKeySpec desede(final GordianLength pKeyLength) {
        return BUILDER.desede(pKeyLength);
    }

    /**
     * Create blowfishKeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianNewSymKeySpec blowfish(final GordianLength pKeyLength) {
        return BUILDER.blowfish(pKeyLength);
    }

    /**
     * Create kalynaKeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianNewSymKeySpec kalyna(final GordianLength pKeyLength) {
        return BUILDER.kalyna(pKeyLength);
    }

    /**
     * Create kalynaKeySpec.
     *
     * @param pBlockLength the block length
     * @param pKeyLength   the keyLength
     * @return the keySpec
     */
    public static GordianNewSymKeySpec kalyna(final GordianLength pBlockLength,
                                              final GordianLength pKeyLength) {
        return BUILDER.kalyna(pBlockLength, pKeyLength);
    }

    /**
     * Create gost.
     *
     * @return the keySpec
     */
    public static GordianNewSymKeySpec gost() {
        return BUILDER.gost();
    }

    /**
     * Create kuznyechik.
     *
     * @return the keySpec
     */
    public static GordianNewSymKeySpec kuznyechik() {
        return BUILDER.kuznyechik();
    }

    /**
     * Create shacal2.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianNewSymKeySpec shacal2(final GordianLength pKeyLength) {
        return BUILDER.shacal2(pKeyLength);
    }

    /**
     * Create speckKeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianNewSymKeySpec speck(final GordianLength pKeyLength) {
        return BUILDER.speck(pKeyLength);
    }

    /**
     * Create simonKeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianNewSymKeySpec simon(final GordianLength pKeyLength) {
        return BUILDER.simon(pKeyLength);
    }

    /**
     * Create marsKeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianNewSymKeySpec mars(final GordianLength pKeyLength) {
        return BUILDER.mars(pKeyLength);
    }

    /**
     * Create anubisKeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianNewSymKeySpec anubis(final GordianLength pKeyLength) {
        return BUILDER.anubis(pKeyLength);
    }

    /**
     * Create leaKeySpec.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianNewSymKeySpec lea(final GordianLength pKeyLength) {
        return BUILDER.lea(pKeyLength);
    }
}
