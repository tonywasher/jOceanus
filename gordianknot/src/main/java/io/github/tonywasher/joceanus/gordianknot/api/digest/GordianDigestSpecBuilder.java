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
package io.github.tonywasher.joceanus.gordianknot.api.digest;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSubSpec.GordianNewDigestState;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.digest.GordianCoreDigestSpecBuilder;

/**
 * Digest Specification Builder.
 */
public final class GordianDigestSpecBuilder {
    /**
     * DigestSpecBuilder.
     */
    private static final GordianNewDigestSpecBuilder BUILDER = GordianCoreDigestSpecBuilder.newInstance();

    /**
     * Private constructor.
     */
    private GordianDigestSpecBuilder() {
    }

    /**
     * Create Md2DigestSpec.
     *
     * @return the DigestSpec
     */
    public static GordianNewDigestSpec md2() {
        return BUILDER.md2();
    }

    /**
     * Create Md4DigestSpec.
     *
     * @return the DigestSpec
     */
    public static GordianNewDigestSpec md4() {
        return BUILDER.md4();
    }

    /**
     * Create Md5DigestSpec.
     *
     * @return the DigestSpec
     */
    public static GordianNewDigestSpec md5() {
        return BUILDER.md5();
    }

    /**
     * Create Sha1DigestSpec.
     *
     * @return the DigestSpec
     */
    public static GordianNewDigestSpec sha1() {
        return BUILDER.sha1();
    }

    /**
     * Create sm3DigestSpec.
     *
     * @return the DigestSpec
     */
    public static GordianNewDigestSpec sm3() {
        return BUILDER.sm3();
    }

    /**
     * Create WhirlpoolDigestSpec.
     *
     * @return the DigestSpec
     */
    public static GordianNewDigestSpec whirlpool() {
        return BUILDER.whirlpool();
    }

    /**
     * Create TigerDigestSpec.
     *
     * @return the DigestSpec
     */
    public static GordianNewDigestSpec tiger() {
        return BUILDER.tiger();
    }

    /**
     * Create sha2DigestSpec.
     *
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianNewDigestSpec sha2(final GordianLength pLength) {
        return BUILDER.sha2(pLength);
    }

    /**
     * Create sha2 DigestSpec.
     *
     * @param pState  the state
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianNewDigestSpec sha2(final GordianNewDigestState pState,
                                            final GordianLength pLength) {
        return BUILDER.sha2(pState, pLength);
    }

    /**
     * Create sha3DigestSpec.
     *
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianNewDigestSpec sha3(final GordianLength pLength) {
        return BUILDER.sha3(pLength);
    }

    /**
     * Create blake2sDigestSpec.
     *
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianNewDigestSpec blake2s(final GordianLength pLength) {
        return BUILDER.blake2s(pLength);
    }

    /**
     * Create blake2bDigestSpec.
     *
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianNewDigestSpec blake2b(final GordianLength pLength) {
        return BUILDER.blake2b(pLength);
    }

    /**
     * Create blake2DigestSpec.
     *
     * @param pState  the state
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianNewDigestSpec blake2(final GordianNewDigestState pState,
                                              final GordianLength pLength) {
        return BUILDER.blake2(pState, pLength);
    }

    /**
     * Create blake2XsDigestSpec.
     *
     * @return the DigestSpec
     */
    public static GordianNewDigestSpec blake2Xs() {
        return blake2X(GordianNewDigestState.STATE256);
    }

    /**
     * Create blake2XbDigestSpec.
     *
     * @return the DigestSpec
     */
    public static GordianNewDigestSpec blake2Xb() {
        return blake2X(GordianNewDigestState.STATE512);
    }

    /**
     * Create blake2XDigestSpec.
     *
     * @param pState the state
     * @return the DigestSpec
     */
    public static GordianNewDigestSpec blake2X(final GordianNewDigestState pState) {
        return BUILDER.blake2X(pState);
    }

    /**
     * Create blake3DigestSpec.
     *
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianNewDigestSpec blake3(final GordianLength pLength) {
        return BUILDER.blake3(pLength);
    }

    /**
     * Create gostDigestSpec.
     *
     * @return the DigestSpec
     */
    public static GordianNewDigestSpec gost() {
        return BUILDER.gost();
    }

    /**
     * Create streebogDigestSpec.
     *
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianNewDigestSpec streebog(final GordianLength pLength) {
        return BUILDER.streebog(pLength);
    }

    /**
     * Create skeinDigestSpec.
     *
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianNewDigestSpec skein(final GordianLength pLength) {
        return BUILDER.skein(pLength);
    }

    /**
     * Create skeinDigestSpec.
     *
     * @param pState  the state
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianNewDigestSpec skein(final GordianNewDigestState pState,
                                             final GordianLength pLength) {
        return BUILDER.skein(pState, pLength);
    }

    /**
     * Create skeinXDigestSpec.
     *
     * @param pState the state
     * @return the DigestSpec
     */
    public static GordianNewDigestSpec skeinX(final GordianNewDigestState pState) {
        return BUILDER.skeinX(pState);
    }

    /**
     * Create sha2DigestSpec.
     *
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianNewDigestSpec ripemd(final GordianLength pLength) {
        return BUILDER.ripemd(pLength);
    }

    /**
     * Create shake128DigestSpec.
     *
     * @return the DigestSpec
     */
    public static GordianNewDigestSpec shake128() {
        return BUILDER.shake128();
    }

    /**
     * Create shake256DigestSpec.
     *
     * @return the DigestSpec
     */
    public static GordianNewDigestSpec shake256() {
        return BUILDER.shake256();
    }

    /**
     * Create kupynaDigestSpec.
     *
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianNewDigestSpec kupyna(final GordianLength pLength) {
        return BUILDER.kupyna(pLength);
    }

    /**
     * Create jhDigestSpec.
     *
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianNewDigestSpec jh(final GordianLength pLength) {
        return BUILDER.jh(pLength);
    }

    /**
     * Create groestlDigestSpec.
     *
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianNewDigestSpec groestl(final GordianLength pLength) {
        return BUILDER.groestl(pLength);
    }

    /**
     * Create cubeHashDigestSpec.
     *
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianNewDigestSpec cubeHash(final GordianLength pLength) {
        return BUILDER.cubeHash(pLength);
    }

    /**
     * Create kangarooDigestSpec.
     *
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianNewDigestSpec kangaroo(final GordianLength pLength) {
        return BUILDER.kangaroo(pLength);
    }

    /**
     * Create marsupimalDigestSpec.
     *
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianNewDigestSpec marsupimal(final GordianLength pLength) {
        return BUILDER.marsupimal(pLength);
    }

    /**
     * Create haraka256DigestSpec.
     *
     * @return the DigestSpec
     */
    public static GordianNewDigestSpec haraka256() {
        return BUILDER.haraka256();
    }

    /**
     * Create haraka512DigestSpec.
     *
     * @return the DigestSpec
     */
    public static GordianNewDigestSpec haraka512() {
        return BUILDER.haraka512();
    }

    /**
     * Create asconDigestSpec.
     *
     * @return the DigestSpec
     */
    public static GordianNewDigestSpec ascon() {
        return BUILDER.ascon();
    }

    /**
     * Create asconDigestSpec.
     *
     * @return the DigestSpec
     */
    public static GordianNewDigestSpec asconX() {
        return BUILDER.asconX();
    }

    /**
     * Create ISAPDigestSpec.
     *
     * @return the DigestSpec
     */
    public static GordianNewDigestSpec isap() {
        return BUILDER.isap();
    }

    /**
     * Create photonBeetleDigestSpec.
     *
     * @return the DigestSpec
     */
    public static GordianNewDigestSpec photonBeetle() {
        return BUILDER.photonBeetle();
    }

    /**
     * Create romulusDigestSpec.
     *
     * @return the DigestSpec
     */
    public static GordianNewDigestSpec romulus() {
        return BUILDER.romulus();
    }

    /**
     * Create sparkleDigestSpec.
     *
     * @param pLength the digest length
     * @return the DigestSpec
     */
    public static GordianNewDigestSpec sparkle(final GordianLength pLength) {
        return BUILDER.sparkle(pLength);
    }

    /**
     * Create xoodyakDigestSpec.
     *
     * @return the DigestSpec
     */
    public static GordianNewDigestSpec xoodyak() {
        return BUILDER.xoodyak();
    }
}
