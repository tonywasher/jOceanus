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

package io.github.tonywasher.joceanus.gordianknot.api.digest.spec;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSubSpec.GordianDigestState;

/**
 * Digest Specification Builder.
 */
public interface GordianDigestSpecBuilder {
    /**
     * Define DigestType.
     *
     * @param pType the type
     * @return the Builder
     */
    GordianDigestSpecBuilder withType(GordianDigestType pType);

    /**
     * Define DigestState.
     *
     * @param pState the state
     * @return the Builder
     */
    GordianDigestSpecBuilder withState(GordianDigestState pState);

    /**
     * Define DigestLength.
     *
     * @param pLength the length
     * @return the Builder
     */
    GordianDigestSpecBuilder withLength(GordianLength pLength);

    /**
     * Use Xof implementation.
     *
     * @return the Builder
     */
    GordianDigestSpecBuilder asXof();

    /**
     * Build digestSpec.
     *
     * @return the DigestSpec
     */
    GordianDigestSpec build();


    /**
     * Create generic digest.
     *
     * @param pType the digestType
     * @return the digestSpec
     */
    default GordianDigestSpec digest(final GordianDigestType pType) {
        return withType(pType).build();
    }

    /**
     * Create generic digest.
     *
     * @param pType   the digestType
     * @param pLength the length
     * @return the digestSpec
     */
    default GordianDigestSpec digest(final GordianDigestType pType,
                                     final GordianLength pLength) {
        return withType(pType).withLength(pLength).build();
    }

    /**
     * Create generic digest.
     *
     * @param pType    the digestType
     * @param pState   the state
     * @param pLength  the length
     * @param pXofMode asXof
     * @return the digestSpec
     */
    default GordianDigestSpec digest(final GordianDigestType pType,
                                     final GordianDigestState pState,
                                     final GordianLength pLength,
                                     final boolean pXofMode) {
        withType(pType).withState(pState).withLength(pLength);
        if (pXofMode) {
            asXof();
        }
        return build();
    }

    /**
     * Create Md2.
     *
     * @return the digestSpec
     */
    default GordianDigestSpec md2() {
        return withType(GordianDigestType.MD2).build();
    }

    /**
     * Create Md4.
     *
     * @return the digestSpec
     */
    default GordianDigestSpec md4() {
        return withType(GordianDigestType.MD4).build();
    }

    /**
     * Create Md5.
     *
     * @return the digestSpec
     */
    default GordianDigestSpec md5() {
        return withType(GordianDigestType.MD5).build();
    }

    /**
     * Create Sha1.
     *
     * @return the digestSpec
     */
    default GordianDigestSpec sha1() {
        return withType(GordianDigestType.SHA1).build();
    }

    /**
     * Create sm3.
     *
     * @return the digestSpec
     */
    default GordianDigestSpec sm3() {
        return withType(GordianDigestType.SM3).build();
    }

    /**
     * Create Whirlpool.
     *
     * @return the digestSpec
     */
    default GordianDigestSpec whirlpool() {
        return withType(GordianDigestType.WHIRLPOOL).build();
    }

    /**
     * Create Tiger.
     *
     * @return the digestSpec
     */
    default GordianDigestSpec tiger() {
        return withType(GordianDigestType.TIGER).build();
    }

    /**
     * Create sha2.
     *
     * @param pLength the length
     * @return the digestSpec
     */
    default GordianDigestSpec sha2(final GordianLength pLength) {
        return withType(GordianDigestType.SHA2).withLength(pLength).build();
    }

    /**
     * Create sha2.
     *
     * @param pState  the state
     * @param pLength the length
     * @return the digestSpec
     */
    default GordianDigestSpec sha2(final GordianDigestState pState,
                                   final GordianLength pLength) {
        return withType(GordianDigestType.SHA2).withState(pState).withLength(pLength).build();
    }

    /**
     * Create sha3.
     *
     * @param pLength the length
     * @return the DigestSpec
     */
    default GordianDigestSpec sha3(final GordianLength pLength) {
        return withType(GordianDigestType.SHA3).withLength(pLength).build();
    }

    /**
     * Create blake2s.
     *
     * @param pLength the length
     * @return the digestSpec
     */
    default GordianDigestSpec blake2s(final GordianLength pLength) {
        return withType(GordianDigestType.BLAKE2).withState(GordianDigestState.STATE256).withLength(pLength).build();
    }

    /**
     * Create blake2b.
     *
     * @param pLength the length
     * @return the digestSpec
     */
    default GordianDigestSpec blake2b(final GordianLength pLength) {
        return withType(GordianDigestType.BLAKE2).withState(GordianDigestState.STATE512).withLength(pLength).build();
    }

    /**
     * Create blake2.
     *
     * @param pState  the state
     * @param pLength the length
     * @return the digestSpec
     */
    default GordianDigestSpec blake2(final GordianDigestState pState,
                                     final GordianLength pLength) {
        return withType(GordianDigestType.BLAKE2).withState(pState).withLength(pLength).build();
    }

    /**
     * Create blake2Xs.
     *
     * @return the digestSpec
     */
    default GordianDigestSpec blake2Xs() {
        return blake2X(GordianDigestState.STATE256);
    }

    /**
     * Create blake2Xb.
     *
     * @return the DigestSpec
     */
    default GordianDigestSpec blake2Xb() {
        return blake2X(GordianDigestState.STATE512);
    }

    /**
     * Create blake2X.
     *
     * @param pState the state
     * @return the digestSpec
     */
    default GordianDigestSpec blake2X(final GordianDigestState pState) {
        return withType(GordianDigestType.BLAKE2).withState(pState).asXof().build();
    }

    /**
     * Create blake3.
     *
     * @param pLength the length
     * @return the digestSpec
     */
    default GordianDigestSpec blake3(final GordianLength pLength) {
        return withType(GordianDigestType.BLAKE3).withLength(pLength).build();
    }

    /**
     * Create gost.
     *
     * @return the digestSpec
     */
    default GordianDigestSpec gost() {
        return withType(GordianDigestType.GOST).build();
    }

    /**
     * Create streebog.
     *
     * @param pLength the length
     * @return the digestSpec
     */
    default GordianDigestSpec streebog(final GordianLength pLength) {
        return withType(GordianDigestType.STREEBOG).withLength(pLength).build();
    }

    /**
     * Create skein.
     *
     * @param pLength the length
     * @return the digestSpec
     */
    default GordianDigestSpec skein(final GordianLength pLength) {
        return withType(GordianDigestType.SKEIN).withLength(pLength).build();
    }

    /**
     * Create skein.
     *
     * @param pState  the state
     * @param pLength the length
     * @return the digestSpec
     */
    default GordianDigestSpec skein(final GordianDigestState pState,
                                    final GordianLength pLength) {
        return withType(GordianDigestType.SKEIN).withState(pState).withLength(pLength).build();
    }

    /**
     * Create skeinX.
     *
     * @param pState the state
     * @return the digestSpec
     */
    default GordianDigestSpec skeinX(final GordianDigestState pState) {
        return withType(GordianDigestType.SKEIN).withState(pState).asXof().build();
    }

    /**
     * Create ripemd.
     *
     * @param pLength the length
     * @return the digestSpec
     */
    default GordianDigestSpec ripemd(final GordianLength pLength) {
        return withType(GordianDigestType.RIPEMD).withLength(pLength).build();
    }

    /**
     * Create shake128.
     *
     * @return the digestSpec
     */
    default GordianDigestSpec shake128() {
        return withType(GordianDigestType.SHAKE).withState(GordianDigestState.STATE128).withLength(GordianLength.LEN_256).build();
    }

    /**
     * Create shake256.
     *
     * @return the digestSpec
     */
    default GordianDigestSpec shake256() {
        return withType(GordianDigestType.SHAKE).withState(GordianDigestState.STATE256).withLength(GordianLength.LEN_512).build();
    }

    /**
     * Create kupyna.
     *
     * @param pLength the length
     * @return the digestSpec
     */
    default GordianDigestSpec kupyna(final GordianLength pLength) {
        return withType(GordianDigestType.KUPYNA).withLength(pLength).build();
    }

    /**
     * Create jh.
     *
     * @param pLength the length
     * @return the digestSpec
     */
    default GordianDigestSpec jh(final GordianLength pLength) {
        return withType(GordianDigestType.JH).withLength(pLength).build();
    }

    /**
     * Create groestl.
     *
     * @param pLength the length
     * @return the digestSpec
     */
    default GordianDigestSpec groestl(final GordianLength pLength) {
        return withType(GordianDigestType.GROESTL).withLength(pLength).build();
    }

    /**
     * Create cubeHash.
     *
     * @param pLength the length
     * @return the digestSpec
     */
    default GordianDigestSpec cubeHash(final GordianLength pLength) {
        return withType(GordianDigestType.CUBEHASH).withLength(pLength).build();
    }

    /**
     * Create kangaroo.
     *
     * @param pLength the length
     * @return the digestSpec
     */
    default GordianDigestSpec kangaroo(final GordianLength pLength) {
        return withType(GordianDigestType.KANGAROO).withState(GordianDigestState.STATE128).withLength(pLength).build();
    }

    /**
     * Create marsupimal.
     *
     * @param pLength the length
     * @return the digestSpec
     */
    default GordianDigestSpec marsupimal(final GordianLength pLength) {
        return withType(GordianDigestType.KANGAROO).withState(GordianDigestState.STATE256).withLength(pLength).build();
    }

    /**
     * Create haraka256.
     *
     * @return the digestSpec
     */
    default GordianDigestSpec haraka256() {
        return withType(GordianDigestType.HARAKA).withState(GordianDigestState.STATE256).withLength(GordianLength.LEN_256).build();
    }

    /**
     * Create haraka512.
     *
     * @return the digestSpec
     */
    default GordianDigestSpec haraka512() {
        return withType(GordianDigestType.HARAKA).withState(GordianDigestState.STATE512).withLength(GordianLength.LEN_256).build();
    }

    /**
     * Create ascon.
     *
     * @return the digestSpec
     */
    default GordianDigestSpec ascon() {
        return withType(GordianDigestType.ASCON).build();
    }

    /**
     * Create asconDigestSpec.
     *
     * @return the DigestSpec
     */
    default GordianDigestSpec asconX() {
        return withType(GordianDigestType.ASCON).asXof().build();
    }

    /**
     * Create ISAP.
     *
     * @return the digestSpec
     */
    default GordianDigestSpec isap() {
        return withType(GordianDigestType.ISAP).build();
    }

    /**
     * Create photonBeetle.
     *
     * @return the digestSpec
     */
    default GordianDigestSpec photonBeetle() {
        return withType(GordianDigestType.PHOTONBEETLE).build();
    }

    /**
     * Create romulus.
     *
     * @return the digestSpec
     */
    default GordianDigestSpec romulus() {
        return withType(GordianDigestType.ROMULUS).build();
    }

    /**
     * Create sparkle.
     *
     * @param pLength the digest length
     * @return the digestSpec
     */
    default GordianDigestSpec sparkle(final GordianLength pLength) {
        return withType(GordianDigestType.SPARKLE).withLength(pLength).build();
    }

    /**
     * Create xoodyak.
     *
     * @return the digestSpec
     */
    default GordianDigestSpec xoodyak() {
        return withType(GordianDigestType.XOODYAK).build();
    }
}
