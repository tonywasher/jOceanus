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
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSubSpec.GordianNewDigestState;

/**
 * Digest Specification Builder.
 */
public interface GordianNewDigestSpecBuilder {
    /**
     * Define DigestType.
     *
     * @param pType the type
     * @return the Builder
     */
    GordianNewDigestSpecBuilder withType(GordianNewDigestType pType);

    /**
     * Define DigestState.
     *
     * @param pState the state
     * @return the Builder
     */
    GordianNewDigestSpecBuilder withState(GordianNewDigestState pState);

    /**
     * Define DigestLength.
     *
     * @param pLength the length
     * @return the Builder
     */
    GordianNewDigestSpecBuilder withLength(GordianLength pLength);

    /**
     * Use Xof implementation.
     *
     * @return the Builder
     */
    GordianNewDigestSpecBuilder asXof();

    /**
     * Build digestSpec.
     *
     * @return the DigestSpec
     */
    GordianNewDigestSpec build();

    /**
     * Create Md2.
     *
     * @return the digestSpec
     */
    default GordianNewDigestSpec md2() {
        return withType(GordianNewDigestType.MD2).build();
    }

    /**
     * Create Md4.
     *
     * @return the digestSpec
     */
    default GordianNewDigestSpec md4() {
        return withType(GordianNewDigestType.MD4).build();
    }

    /**
     * Create Md5.
     *
     * @return the digestSpec
     */
    default GordianNewDigestSpec md5() {
        return withType(GordianNewDigestType.MD5).build();
    }

    /**
     * Create Sha1.
     *
     * @return the digestSpec
     */
    default GordianNewDigestSpec sha1() {
        return withType(GordianNewDigestType.SHA1).build();
    }

    /**
     * Create sm3.
     *
     * @return the digestSpec
     */
    default GordianNewDigestSpec sm3() {
        return withType(GordianNewDigestType.SM3).build();
    }

    /**
     * Create Whirlpool.
     *
     * @return the digestSpec
     */
    default GordianNewDigestSpec whirlpool() {
        return withType(GordianNewDigestType.WHIRLPOOL).build();
    }

    /**
     * Create Tiger.
     *
     * @return the digestSpec
     */
    default GordianNewDigestSpec tiger() {
        return withType(GordianNewDigestType.TIGER).build();
    }

    /**
     * Create sha2.
     *
     * @param pLength the length
     * @return the digestSpec
     */
    default GordianNewDigestSpec sha2(final GordianLength pLength) {
        return withType(GordianNewDigestType.SHA2).withLength(pLength).build();
    }

    /**
     * Create sha2.
     *
     * @param pState  the state
     * @param pLength the length
     * @return the digestSpec
     */
    default GordianNewDigestSpec sha2(final GordianNewDigestState pState,
                                      final GordianLength pLength) {
        return withType(GordianNewDigestType.SHA2).withState(pState).withLength(pLength).build();
    }

    /**
     * Create sha3.
     *
     * @param pLength the length
     * @return the DigestSpec
     */
    default GordianNewDigestSpec sha3(final GordianLength pLength) {
        return withType(GordianNewDigestType.SHA3).withLength(pLength).build();
    }

    /**
     * Create blake2s.
     *
     * @param pLength the length
     * @return the digestSpec
     */
    default GordianNewDigestSpec blake2s(final GordianLength pLength) {
        return withType(GordianNewDigestType.BLAKE2).withState(GordianNewDigestState.STATE256).withLength(pLength).build();
    }

    /**
     * Create blake2b.
     *
     * @param pLength the length
     * @return the digestSpec
     */
    default GordianNewDigestSpec blake2b(final GordianLength pLength) {
        return withType(GordianNewDigestType.BLAKE2).withState(GordianNewDigestState.STATE512).withLength(pLength).build();
    }

    /**
     * Create blake2.
     *
     * @param pState  the state
     * @param pLength the length
     * @return the digestSpec
     */
    default GordianNewDigestSpec blake2(final GordianNewDigestState pState,
                                        final GordianLength pLength) {
        return withType(GordianNewDigestType.BLAKE2).withState(pState).withLength(pLength).build();
    }

    /**
     * Create blake2Xs.
     *
     * @return the digestSpec
     */
    default GordianNewDigestSpec blake2Xs() {
        return blake2X(GordianNewDigestState.STATE256);
    }

    /**
     * Create blake2Xb.
     *
     * @return the DigestSpec
     */
    default GordianNewDigestSpec blake2Xb() {
        return blake2X(GordianNewDigestState.STATE512);
    }

    /**
     * Create blake2X.
     *
     * @param pState the state
     * @return the digestSpec
     */
    default GordianNewDigestSpec blake2X(final GordianNewDigestState pState) {
        return withType(GordianNewDigestType.BLAKE2).withState(pState).asXof().build();
    }

    /**
     * Create blake3.
     *
     * @param pLength the length
     * @return the digestSpec
     */
    default GordianNewDigestSpec blake3(final GordianLength pLength) {
        return withType(GordianNewDigestType.BLAKE3).withLength(pLength).build();
    }

    /**
     * Create gost.
     *
     * @return the digestSpec
     */
    default GordianNewDigestSpec gost() {
        return withType(GordianNewDigestType.GOST).build();
    }

    /**
     * Create streebog.
     *
     * @param pLength the length
     * @return the digestSpec
     */
    default GordianNewDigestSpec streebog(final GordianLength pLength) {
        return withType(GordianNewDigestType.STREEBOG).withLength(pLength).build();
    }

    /**
     * Create skein.
     *
     * @param pLength the length
     * @return the digestSpec
     */
    default GordianNewDigestSpec skein(final GordianLength pLength) {
        return withType(GordianNewDigestType.SKEIN).withLength(pLength).build();
    }

    /**
     * Create skein.
     *
     * @param pState  the state
     * @param pLength the length
     * @return the digestSpec
     */
    default GordianNewDigestSpec skein(final GordianNewDigestState pState,
                                       final GordianLength pLength) {
        return withType(GordianNewDigestType.SKEIN).withState(pState).withLength(pLength).build();
    }

    /**
     * Create skeinX.
     *
     * @param pState the state
     * @return the digestSpec
     */
    default GordianNewDigestSpec skeinX(final GordianNewDigestState pState) {
        return withType(GordianNewDigestType.SKEIN).withState(pState).asXof().build();
    }

    /**
     * Create ripemd.
     *
     * @param pLength the length
     * @return the digestSpec
     */
    default GordianNewDigestSpec ripemd(final GordianLength pLength) {
        return withType(GordianNewDigestType.RIPEMD).withLength(pLength).build();
    }

    /**
     * Create shake128.
     *
     * @return the digestSpec
     */
    default GordianNewDigestSpec shake128() {
        return withType(GordianNewDigestType.SHAKE).withState(GordianNewDigestState.STATE128).withLength(GordianLength.LEN_256).build();
    }

    /**
     * Create shake256.
     *
     * @return the digestSpec
     */
    default GordianNewDigestSpec shake256() {
        return withType(GordianNewDigestType.SHAKE).withState(GordianNewDigestState.STATE256).withLength(GordianLength.LEN_512).build();
    }

    /**
     * Create kupyna.
     *
     * @param pLength the length
     * @return the digestSpec
     */
    default GordianNewDigestSpec kupyna(final GordianLength pLength) {
        return withType(GordianNewDigestType.KUPYNA).withLength(pLength).build();
    }

    /**
     * Create jh.
     *
     * @param pLength the length
     * @return the digestSpec
     */
    default GordianNewDigestSpec jh(final GordianLength pLength) {
        return withType(GordianNewDigestType.JH).withLength(pLength).build();
    }

    /**
     * Create groestl.
     *
     * @param pLength the length
     * @return the digestSpec
     */
    default GordianNewDigestSpec groestl(final GordianLength pLength) {
        return withType(GordianNewDigestType.GROESTL).withLength(pLength).build();
    }

    /**
     * Create cubeHash.
     *
     * @param pLength the length
     * @return the digestSpec
     */
    default GordianNewDigestSpec cubeHash(final GordianLength pLength) {
        return withType(GordianNewDigestType.CUBEHASH).withLength(pLength).build();
    }

    /**
     * Create kangaroo.
     *
     * @param pLength the length
     * @return the digestSpec
     */
    default GordianNewDigestSpec kangaroo(final GordianLength pLength) {
        return withType(GordianNewDigestType.KANGAROO).withState(GordianNewDigestState.STATE128).withLength(pLength).build();
    }

    /**
     * Create marsupimal.
     *
     * @param pLength the length
     * @return the digestSpec
     */
    default GordianNewDigestSpec marsupimal(final GordianLength pLength) {
        return withType(GordianNewDigestType.KANGAROO).withState(GordianNewDigestState.STATE256).withLength(pLength).build();
    }

    /**
     * Create haraka256.
     *
     * @return the digestSpec
     */
    default GordianNewDigestSpec haraka256() {
        return withType(GordianNewDigestType.HARAKA).withState(GordianNewDigestState.STATE256).withLength(GordianLength.LEN_256).build();
    }

    /**
     * Create haraka512.
     *
     * @return the digestSpec
     */
    default GordianNewDigestSpec haraka512() {
        return withType(GordianNewDigestType.HARAKA).withState(GordianNewDigestState.STATE512).withLength(GordianLength.LEN_256).build();
    }

    /**
     * Create ascon.
     *
     * @return the digestSpec
     */
    default GordianNewDigestSpec ascon() {
        return withType(GordianNewDigestType.ASCON).build();
    }

    /**
     * Create asconDigestSpec.
     *
     * @return the DigestSpec
     */
    default GordianNewDigestSpec asconX() {
        return withType(GordianNewDigestType.ASCON).asXof().build();
    }

    /**
     * Create ISAP.
     *
     * @return the digestSpec
     */
    default GordianNewDigestSpec isap() {
        return withType(GordianNewDigestType.ISAP).build();
    }

    /**
     * Create photonBeetle.
     *
     * @return the digestSpec
     */
    default GordianNewDigestSpec photonBeetle() {
        return withType(GordianNewDigestType.PHOTONBEETLE).build();
    }

    /**
     * Create romulus.
     *
     * @return the digestSpec
     */
    default GordianNewDigestSpec romulus() {
        return withType(GordianNewDigestType.ROMULUS).build();
    }

    /**
     * Create sparkle.
     *
     * @param pLength the digest length
     * @return the digestSpec
     */
    default GordianNewDigestSpec sparkle(final GordianLength pLength) {
        return withType(GordianNewDigestType.SPARKLE).withLength(pLength).build();
    }

    /**
     * Create xoodyak.
     *
     * @return the digestSpec
     */
    default GordianNewDigestSpec xoodyak() {
        return withType(GordianNewDigestType.XOODYAK).build();
    }
}
