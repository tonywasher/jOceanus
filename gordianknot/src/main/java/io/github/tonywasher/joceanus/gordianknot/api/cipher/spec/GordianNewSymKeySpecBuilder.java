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

/**
 * SymKey specification Builder.
 */
public interface GordianNewSymKeySpecBuilder {
    /**
     * Define SymKeyType.
     *
     * @param pType the type
     * @return the Builder
     */
    GordianNewSymKeySpecBuilder withType(GordianNewSymKeyType pType);

    /**
     * Define BlockLength.
     *
     * @param pBlockLength the blockLength
     * @return the Builder
     */
    GordianNewSymKeySpecBuilder withBlockLength(GordianLength pBlockLength);

    /**
     * Define KeyLength.
     *
     * @param pKeyLength the keyLength
     * @return the Builder
     */
    GordianNewSymKeySpecBuilder withKeyLength(GordianLength pKeyLength);

    /**
     * Build symKeySpec.
     *
     * @return the symKeySpec
     */
    GordianNewSymKeySpec build();

    /**
     * Create generic.
     *
     * @param pKeyType   the keyType
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianNewSymKeySpec generic(final GordianNewSymKeyType pKeyType,
                                         final GordianLength pKeyLength) {
        return withType(pKeyType).withKeyLength(pKeyLength).build();
    }
    
    /**
     * Create generic.
     *
     * @param pKeyType     the keyType
     * @param pBlockLength the blockLength
     * @param pKeyLength   the keyLength
     * @return the keySpec
     */
    default GordianNewSymKeySpec generic(final GordianNewSymKeyType pKeyType,
                                         final GordianLength pBlockLength,
                                         final GordianLength pKeyLength) {
        return withType(pKeyType).withBlockLength(pBlockLength).withKeyLength(pKeyLength).build();
    }

    /**
     * Create aes.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianNewSymKeySpec aes(final GordianLength pKeyLength) {
        return withType(GordianNewSymKeyType.AES).withKeyLength(pKeyLength).build();
    }

    /**
     * Create serpent.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianNewSymKeySpec serpent(final GordianLength pKeyLength) {
        return withType(GordianNewSymKeyType.SERPENT).withKeyLength(pKeyLength).build();
    }

    /**
     * Create twoFish.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianNewSymKeySpec twoFish(final GordianLength pKeyLength) {
        return withType(GordianNewSymKeyType.TWOFISH).withKeyLength(pKeyLength).build();
    }

    /**
     * Create threeFish.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianNewSymKeySpec threeFish(final GordianLength pKeyLength) {
        return withType(GordianNewSymKeyType.THREEFISH).withKeyLength(pKeyLength).build();
    }

    /**
     * Create camellia.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianNewSymKeySpec camellia(final GordianLength pKeyLength) {
        return withType(GordianNewSymKeyType.CAMELLIA).withKeyLength(pKeyLength).build();
    }

    /**
     * Create rc2.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianNewSymKeySpec rc2(final GordianLength pKeyLength) {
        return withType(GordianNewSymKeyType.RC2).withKeyLength(pKeyLength).build();
    }

    /**
     * Create rc5.
     *
     * @return the keySpec
     */
    default GordianNewSymKeySpec rc5() {
        return withType(GordianNewSymKeyType.RC5).build();
    }

    /**
     * Create rc5KeySpec.
     *
     * @param pBlockLength the blockLength
     * @return the keySpec
     */
    default GordianNewSymKeySpec rc5(final GordianLength pBlockLength) {
        return withType(GordianNewSymKeyType.RC5).withBlockLength(pBlockLength).build();
    }

    /**
     * Create rc6.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianNewSymKeySpec rc6(final GordianLength pKeyLength) {
        return withType(GordianNewSymKeyType.RC6).withKeyLength(pKeyLength).build();
    }

    /**
     * Create cast5.
     *
     * @return the keySpec
     */
    default GordianNewSymKeySpec cast5() {
        return withType(GordianNewSymKeyType.CAST5).build();
    }

    /**
     * Create cast6.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianNewSymKeySpec cast6(final GordianLength pKeyLength) {
        return withType(GordianNewSymKeyType.CAST6).withKeyLength(pKeyLength).build();
    }

    /**
     * Create aria.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianNewSymKeySpec aria(final GordianLength pKeyLength) {
        return withType(GordianNewSymKeyType.ARIA).withKeyLength(pKeyLength).build();
    }

    /**
     * Create sm4.
     *
     * @return the keySpec
     */
    default GordianNewSymKeySpec sm4() {
        return withType(GordianNewSymKeyType.SM4).build();
    }

    /**
     * Create noeKeon.
     *
     * @return the keySpec
     */
    default GordianNewSymKeySpec noekeon() {
        return withType(GordianNewSymKeyType.NOEKEON).build();
    }

    /**
     * Create seed.
     *
     * @return the keySpec
     */
    default GordianNewSymKeySpec seed() {
        return withType(GordianNewSymKeyType.SEED).build();
    }

    /**
     * Create tea.
     *
     * @return the keySpec
     */
    default GordianNewSymKeySpec tea() {
        return withType(GordianNewSymKeyType.TEA).build();
    }

    /**
     * Create xtea.
     *
     * @return the keySpec
     */
    default GordianNewSymKeySpec xtea() {
        return withType(GordianNewSymKeyType.XTEA).build();
    }

    /**
     * Create idea.
     *
     * @return the keySpec
     */
    default GordianNewSymKeySpec idea() {
        return withType(GordianNewSymKeyType.IDEA).build();
    }

    /**
     * Create skipjack.
     *
     * @return the keySpec
     */
    default GordianNewSymKeySpec skipjack() {
        return withType(GordianNewSymKeyType.SKIPJACK).build();
    }

    /**
     * Create desede.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianNewSymKeySpec desede(final GordianLength pKeyLength) {
        return withType(GordianNewSymKeyType.DESEDE).build();
    }

    /**
     * Create blowfish.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianNewSymKeySpec blowfish(final GordianLength pKeyLength) {
        return withType(GordianNewSymKeyType.BLOWFISH).build();
    }

    /**
     * Create kalyna.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianNewSymKeySpec kalyna(final GordianLength pKeyLength) {
        return withType(GordianNewSymKeyType.KALYNA).withKeyLength(pKeyLength).build();
    }

    /**
     * Create kalyna.
     *
     * @param pBlockLength the block length
     * @param pKeyLength   the keyLength
     * @return the keySpec
     */
    default GordianNewSymKeySpec kalyna(final GordianLength pBlockLength,
                                        final GordianLength pKeyLength) {
        return withType(GordianNewSymKeyType.KALYNA).withBlockLength(pBlockLength).withKeyLength(pKeyLength).build();
    }

    /**
     * Create gost.
     *
     * @return the keySpec
     */
    default GordianNewSymKeySpec gost() {
        return withType(GordianNewSymKeyType.GOST).build();
    }

    /**
     * Create kuznyechik.
     *
     * @return the keySpec
     */
    default GordianNewSymKeySpec kuznyechik() {
        return withType(GordianNewSymKeyType.KUZNYECHIK).build();
    }

    /**
     * Create shacal2.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianNewSymKeySpec shacal2(final GordianLength pKeyLength) {
        return withType(GordianNewSymKeyType.SHACAL2).withKeyLength(pKeyLength).build();
    }

    /**
     * Create speck.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianNewSymKeySpec speck(final GordianLength pKeyLength) {
        return withType(GordianNewSymKeyType.SPECK).withKeyLength(pKeyLength).build();
    }

    /**
     * Create simon.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianNewSymKeySpec simon(final GordianLength pKeyLength) {
        return withType(GordianNewSymKeyType.SIMON).withKeyLength(pKeyLength).build();
    }

    /**
     * Create mars.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianNewSymKeySpec mars(final GordianLength pKeyLength) {
        return withType(GordianNewSymKeyType.MARS).withKeyLength(pKeyLength).build();
    }

    /**
     * Create anubis.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianNewSymKeySpec anubis(final GordianLength pKeyLength) {
        return withType(GordianNewSymKeyType.ANUBIS).withKeyLength(pKeyLength).build();
    }

    /**
     * Create lea.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianNewSymKeySpec lea(final GordianLength pKeyLength) {
        return withType(GordianNewSymKeyType.LEA).withKeyLength(pKeyLength).build();
    }
}
