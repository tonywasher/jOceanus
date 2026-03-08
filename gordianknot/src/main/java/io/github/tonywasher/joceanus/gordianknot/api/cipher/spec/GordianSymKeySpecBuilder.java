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
public interface GordianSymKeySpecBuilder {
    /**
     * Define SymKeyType.
     *
     * @param pType the type
     * @return the Builder
     */
    GordianSymKeySpecBuilder withType(GordianSymKeyType pType);

    /**
     * Define BlockLength.
     *
     * @param pBlockLength the blockLength
     * @return the Builder
     */
    GordianSymKeySpecBuilder withBlockLength(GordianLength pBlockLength);

    /**
     * Define KeyLength.
     *
     * @param pKeyLength the keyLength
     * @return the Builder
     */
    GordianSymKeySpecBuilder withKeyLength(GordianLength pKeyLength);

    /**
     * Build symKeySpec.
     *
     * @return the symKeySpec
     */
    GordianSymKeySpec build();

    /**
     * Create generic.
     *
     * @param pKeyType   the keyType
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianSymKeySpec symKey(final GordianSymKeyType pKeyType,
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
    default GordianSymKeySpec symKey(final GordianSymKeyType pKeyType,
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
    default GordianSymKeySpec aes(final GordianLength pKeyLength) {
        return withType(GordianSymKeyType.AES).withKeyLength(pKeyLength).build();
    }

    /**
     * Create serpent.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianSymKeySpec serpent(final GordianLength pKeyLength) {
        return withType(GordianSymKeyType.SERPENT).withKeyLength(pKeyLength).build();
    }

    /**
     * Create twoFish.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianSymKeySpec twoFish(final GordianLength pKeyLength) {
        return withType(GordianSymKeyType.TWOFISH).withKeyLength(pKeyLength).build();
    }

    /**
     * Create threeFish.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianSymKeySpec threeFish(final GordianLength pKeyLength) {
        return withType(GordianSymKeyType.THREEFISH).withKeyLength(pKeyLength).build();
    }

    /**
     * Create camellia.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianSymKeySpec camellia(final GordianLength pKeyLength) {
        return withType(GordianSymKeyType.CAMELLIA).withKeyLength(pKeyLength).build();
    }

    /**
     * Create rc2.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianSymKeySpec rc2(final GordianLength pKeyLength) {
        return withType(GordianSymKeyType.RC2).withKeyLength(pKeyLength).build();
    }

    /**
     * Create rc5.
     *
     * @return the keySpec
     */
    default GordianSymKeySpec rc5() {
        return withType(GordianSymKeyType.RC5).build();
    }

    /**
     * Create rc5KeySpec.
     *
     * @param pBlockLength the blockLength
     * @return the keySpec
     */
    default GordianSymKeySpec rc5(final GordianLength pBlockLength) {
        return withType(GordianSymKeyType.RC5).withBlockLength(pBlockLength).build();
    }

    /**
     * Create rc6.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianSymKeySpec rc6(final GordianLength pKeyLength) {
        return withType(GordianSymKeyType.RC6).withKeyLength(pKeyLength).build();
    }

    /**
     * Create cast5.
     *
     * @return the keySpec
     */
    default GordianSymKeySpec cast5() {
        return withType(GordianSymKeyType.CAST5).build();
    }

    /**
     * Create cast6.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianSymKeySpec cast6(final GordianLength pKeyLength) {
        return withType(GordianSymKeyType.CAST6).withKeyLength(pKeyLength).build();
    }

    /**
     * Create aria.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianSymKeySpec aria(final GordianLength pKeyLength) {
        return withType(GordianSymKeyType.ARIA).withKeyLength(pKeyLength).build();
    }

    /**
     * Create sm4.
     *
     * @return the keySpec
     */
    default GordianSymKeySpec sm4() {
        return withType(GordianSymKeyType.SM4).build();
    }

    /**
     * Create noeKeon.
     *
     * @return the keySpec
     */
    default GordianSymKeySpec noekeon() {
        return withType(GordianSymKeyType.NOEKEON).build();
    }

    /**
     * Create seed.
     *
     * @return the keySpec
     */
    default GordianSymKeySpec seed() {
        return withType(GordianSymKeyType.SEED).build();
    }

    /**
     * Create tea.
     *
     * @return the keySpec
     */
    default GordianSymKeySpec tea() {
        return withType(GordianSymKeyType.TEA).build();
    }

    /**
     * Create xtea.
     *
     * @return the keySpec
     */
    default GordianSymKeySpec xtea() {
        return withType(GordianSymKeyType.XTEA).build();
    }

    /**
     * Create idea.
     *
     * @return the keySpec
     */
    default GordianSymKeySpec idea() {
        return withType(GordianSymKeyType.IDEA).build();
    }

    /**
     * Create skipjack.
     *
     * @return the keySpec
     */
    default GordianSymKeySpec skipjack() {
        return withType(GordianSymKeyType.SKIPJACK).build();
    }

    /**
     * Create desede.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianSymKeySpec desede(final GordianLength pKeyLength) {
        return withType(GordianSymKeyType.DESEDE).build();
    }

    /**
     * Create blowfish.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianSymKeySpec blowfish(final GordianLength pKeyLength) {
        return withType(GordianSymKeyType.BLOWFISH).build();
    }

    /**
     * Create kalyna.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianSymKeySpec kalyna(final GordianLength pKeyLength) {
        return withType(GordianSymKeyType.KALYNA).withKeyLength(pKeyLength).build();
    }

    /**
     * Create kalyna.
     *
     * @param pBlockLength the block length
     * @param pKeyLength   the keyLength
     * @return the keySpec
     */
    default GordianSymKeySpec kalyna(final GordianLength pBlockLength,
                                     final GordianLength pKeyLength) {
        return withType(GordianSymKeyType.KALYNA).withBlockLength(pBlockLength).withKeyLength(pKeyLength).build();
    }

    /**
     * Create gost.
     *
     * @return the keySpec
     */
    default GordianSymKeySpec gost() {
        return withType(GordianSymKeyType.GOST).build();
    }

    /**
     * Create kuznyechik.
     *
     * @return the keySpec
     */
    default GordianSymKeySpec kuznyechik() {
        return withType(GordianSymKeyType.KUZNYECHIK).build();
    }

    /**
     * Create shacal2.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianSymKeySpec shacal2(final GordianLength pKeyLength) {
        return withType(GordianSymKeyType.SHACAL2).withKeyLength(pKeyLength).build();
    }

    /**
     * Create speck.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianSymKeySpec speck(final GordianLength pKeyLength) {
        return withType(GordianSymKeyType.SPECK).withKeyLength(pKeyLength).build();
    }

    /**
     * Create simon.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianSymKeySpec simon(final GordianLength pKeyLength) {
        return withType(GordianSymKeyType.SIMON).withKeyLength(pKeyLength).build();
    }

    /**
     * Create mars.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianSymKeySpec mars(final GordianLength pKeyLength) {
        return withType(GordianSymKeyType.MARS).withKeyLength(pKeyLength).build();
    }

    /**
     * Create anubis.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianSymKeySpec anubis(final GordianLength pKeyLength) {
        return withType(GordianSymKeyType.ANUBIS).withKeyLength(pKeyLength).build();
    }

    /**
     * Create lea.
     *
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    default GordianSymKeySpec lea(final GordianLength pKeyLength) {
        return withType(GordianSymKeyType.LEA).withKeyLength(pKeyLength).build();
    }
}
