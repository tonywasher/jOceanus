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
package net.sourceforge.joceanus.jgordianknot.api.digest;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;

/**
 * Digest Specification Builder.
 */
public final class GordianDigestSpecBuilder {
    /**
     * Private constructor.
     */
    private GordianDigestSpecBuilder() {
    }

    /**
     * Create Md2DigestSpec.
     * @return the DigestSpec
     */
    public static GordianDigestSpec md2() {
        return new GordianDigestSpec(GordianDigestType.MD2);
    }

    /**
     * Create Md4DigestSpec.
     * @return the DigestSpec
     */
    public static GordianDigestSpec md4() {
        return new GordianDigestSpec(GordianDigestType.MD4);
    }

    /**
     * Create Md5DigestSpec.
     * @return the DigestSpec
     */
    public static GordianDigestSpec md5() {
        return new GordianDigestSpec(GordianDigestType.MD5);
    }

    /**
     * Create Sha1DigestSpec.
     * @return the DigestSpec
     */
    public static GordianDigestSpec sha1() {
        return new GordianDigestSpec(GordianDigestType.SHA1);
    }

    /**
     * Create sm3DigestSpec.
     * @return the DigestSpec
     */
    public static GordianDigestSpec sm3() {
        return new GordianDigestSpec(GordianDigestType.SM3);
    }

    /**
     * Create WhirlpoolDigestSpec.
     * @return the DigestSpec
     */
    public static GordianDigestSpec whirlpool() {
        return new GordianDigestSpec(GordianDigestType.WHIRLPOOL);
    }

    /**
     * Create TigerDigestSpec.
     * @return the DigestSpec
     */
    public static GordianDigestSpec tiger() {
        return new GordianDigestSpec(GordianDigestType.TIGER);
    }

    /**
     * Create sha2DigestSpec.
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianDigestSpec sha2(final GordianLength pLength) {
        return new GordianDigestSpec(GordianDigestType.SHA2, null, pLength);
    }

    /**
     * Create sha2 alternate DigestSpec.
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianDigestSpec sha2Alt(final GordianLength pLength) {
        return new GordianDigestSpec(GordianDigestType.SHA2, GordianDigestType.SHA2.getAlternateStateForLength(pLength), pLength);
    }

    /**
     * Create sha3DigestSpec.
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianDigestSpec sha3(final GordianLength pLength) {
        return new GordianDigestSpec(GordianDigestType.SHA3, pLength);
    }

    /**
     * Create blake2bDigestSpec.
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianDigestSpec blake2(final GordianLength pLength) {
        return new GordianDigestSpec(GordianDigestType.BLAKE2, GordianDigestType.BLAKE2.getStateForLength(pLength), pLength);
    }

    /**
     * Create blake2sDigestSpec.
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianDigestSpec blake2Alt(final GordianLength pLength) {
        return new GordianDigestSpec(GordianDigestType.BLAKE2, GordianDigestType.BLAKE2.getAlternateStateForLength(pLength), pLength);
    }

    /**
     * Create blake2DigestSpec.
     * @param pStateLength the state length
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianDigestSpec blake2(final GordianLength pStateLength,
                                           final GordianLength pLength) {
        return new GordianDigestSpec(GordianDigestType.BLAKE2, pStateLength, pLength);
    }

    /**
     * Create blake3DigestSpec.
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianDigestSpec blake3(final GordianLength pLength) {
        return new GordianDigestSpec(GordianDigestType.BLAKE3, pLength);
    }

    /**
     * Create gostDigestSpec.
     * @return the DigestSpec
     */
    public static GordianDigestSpec gost() {
        return new GordianDigestSpec(GordianDigestType.GOST);
    }

    /**
     * Create streebogDigestSpec.
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianDigestSpec streebog(final GordianLength pLength) {
        return new GordianDigestSpec(GordianDigestType.STREEBOG, pLength);
    }

    /**
     * Create skeinDigestSpec.
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianDigestSpec skein(final GordianLength pLength) {
        return new GordianDigestSpec(GordianDigestType.SKEIN, GordianDigestType.SKEIN.getStateForLength(pLength), pLength);
    }

    /**
     * Create skeinDigestSpec.
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianDigestSpec skeinAlt(final GordianLength pLength) {
        return new GordianDigestSpec(GordianDigestType.SKEIN, GordianDigestType.SKEIN.getAlternateStateForLength(pLength), pLength);
    }

    /**
     * Create skeinDigestSpec.
     * @param pStateLength the state length
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianDigestSpec skein(final GordianLength pStateLength,
                                          final GordianLength pLength) {
        return new GordianDigestSpec(GordianDigestType.SKEIN, pStateLength, pLength);
    }

    /**
     * Create sha2DigestSpec.
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianDigestSpec ripemd(final GordianLength pLength) {
        return new GordianDigestSpec(GordianDigestType.RIPEMD, pLength);
    }

    /**
     * Create shake128DigestSpec.
     * @return the DigestSpec
     */
    public static GordianDigestSpec shake128() {
        return new GordianDigestSpec(GordianDigestType.SHAKE, GordianLength.LEN_128, GordianLength.LEN_256);
    }

    /**
     * Create shake256DigestSpec.
     * @return the DigestSpec
     */
    public static GordianDigestSpec shake256() {
        return new GordianDigestSpec(GordianDigestType.SHAKE, GordianLength.LEN_256, GordianLength.LEN_512);
    }

    /**
     * Create kupynaDigestSpec.
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianDigestSpec kupyna(final GordianLength pLength) {
        return new GordianDigestSpec(GordianDigestType.KUPYNA, pLength);
    }

    /**
     * Create jhDigestSpec.
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianDigestSpec jh(final GordianLength pLength) {
        return new GordianDigestSpec(GordianDigestType.JH, pLength);
    }

    /**
     * Create groestlDigestSpec.
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianDigestSpec groestl(final GordianLength pLength) {
        return new GordianDigestSpec(GordianDigestType.GROESTL, pLength);
    }

    /**
     * Create cubeHashDigestSpec.
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianDigestSpec cubeHash(final GordianLength pLength) {
        return new GordianDigestSpec(GordianDigestType.CUBEHASH, pLength);
    }

    /**
     * Create kangarooDigestSpec.
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianDigestSpec kangaroo(final GordianLength pLength) {
        return new GordianDigestSpec(GordianDigestType.KANGAROO, GordianLength.LEN_128, pLength);
    }

    /**
     * Create marsupimalDigestSpec.
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianDigestSpec marsupimal(final GordianLength pLength) {
        return new GordianDigestSpec(GordianDigestType.KANGAROO, GordianLength.LEN_256, pLength);
    }

    /**
     * Create haraka256DigestSpec.
     * @return the DigestSpec
     */
    public static GordianDigestSpec haraka256() {
        return new GordianDigestSpec(GordianDigestType.HARAKA, GordianLength.LEN_256);
    }

    /**
     * Create haraka512DigestSpec.
     * @return the DigestSpec
     */
    public static GordianDigestSpec haraka512() {
        return new GordianDigestSpec(GordianDigestType.HARAKA, GordianLength.LEN_512, GordianLength.LEN_256);
    }
}
