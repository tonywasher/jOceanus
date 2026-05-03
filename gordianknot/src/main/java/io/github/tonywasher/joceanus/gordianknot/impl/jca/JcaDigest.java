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
package io.github.tonywasher.joceanus.gordianknot.impl.jca;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSubSpec.GordianDigestState;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.digest.GordianCoreDigest;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.digest.GordianCoreDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.digest.GordianCoreDigestSubSpec.GordianCoreDigestState;

import java.security.DigestException;
import java.security.MessageDigest;

/**
 * Jca Digest.
 */
public final class JcaDigest
        extends GordianCoreDigest {
    /**
     * The MessageDigest.
     */
    private final MessageDigest theDigest;

    /**
     * Constructor.
     *
     * @param pDigestSpec the digestSpec
     * @param pDigest     the digest
     */
    JcaDigest(final GordianCoreDigestSpec pDigestSpec,
              final MessageDigest pDigest) {
        super(pDigestSpec);
        theDigest = pDigest;
    }

    @Override
    public int getDigestSize() {
        return theDigest.getDigestLength();
    }

    @Override
    public void doUpdate(final byte[] pBytes,
                         final int pOffset,
                         final int pLength) {
        theDigest.update(pBytes, pOffset, pLength);
    }

    @Override
    public void update(final byte pByte) {
        theDigest.update(pByte);
    }

    @Override
    public void reset() {
        theDigest.reset();
    }

    @Override
    public byte[] finish() {
        return theDigest.digest();
    }

    @Override
    public byte[] finish(final byte[] pBytes) {
        return theDigest.digest(pBytes);
    }

    @Override
    public int doFinish(final byte[] pBuffer,
                        final int pOffset) throws GordianException {
        try {
            return theDigest.digest(pBuffer, pOffset, getDigestSize());
        } catch (DigestException e) {
            throw new GordianCryptoException("Failed to calculate Digest", e);
        }
    }

    /**
     * Obtain the sha2 signature algorithm.
     *
     * @param pDigestSpec the digestSpec
     * @return the algorithm
     * @throws GordianException on error
     */
    static String getSignAlgorithm(final GordianCoreDigestSpec pDigestSpec) throws GordianException {
        /* If this is a sha2 extended algorithm */
        if (GordianDigestType.SHA2.equals(pDigestSpec.getDigestType())
                && pDigestSpec.isSha2Hybrid()) {
            return GordianLength.LEN_256.equals(pDigestSpec.getDigestLength())
                    ? "SHA512(256)"
                    : "SHA512(224)";
        }

        /* Access digest details */
        return getAlgorithm(pDigestSpec);
    }

    /**
     * Create the sha2 hMac algorithm.
     *
     * @param pDigestSpec the digestSpec
     * @return the algorithm
     * @throws GordianException on error
     */
    static String getHMacAlgorithm(final GordianCoreDigestSpec pDigestSpec) throws GordianException {
        /* If this is a sha2 extended algorithm */
        if (GordianDigestType.SHA2.equals(pDigestSpec.getDigestType())
                && pDigestSpec.isSha2Hybrid()) {
            return GordianLength.LEN_256.equals(pDigestSpec.getDigestLength())
                    ? "SHA512/256"
                    : "SHA512/224";
        }

        /* Access digest details */
        return getAlgorithm(pDigestSpec);
    }

    /**
     * Obtain the full algorithm name.
     *
     * @param pDigestSpec the digestSpec
     * @return the name
     * @throws GordianException on error
     */
    static String getFullAlgorithm(final GordianCoreDigestSpec pDigestSpec) throws GordianException {
        /* Access standard name */
        final String myAlgorithm = getAlgorithm(pDigestSpec);

        return switch (pDigestSpec.getDigestType()) {
            case SHAKE, BLAKE3 -> myAlgorithm + "-" + pDigestSpec.getDigestLength();
            default -> myAlgorithm;
        };
    }

    /**
     * Obtain the algorithm name.
     *
     * @param pDigestSpec the digestSpec
     * @return the name
     * @throws GordianException on error
     */
    static String getAlgorithm(final GordianCoreDigestSpec pDigestSpec) throws GordianException {
        /* Access digest details */
        final GordianDigestType myType = pDigestSpec.getDigestType();
        final GordianLength myLen = pDigestSpec.getDigestLength();

        /* Switch on digestType */
        return switch (myType) {
            case SHA2 -> getSHA2Algorithm(pDigestSpec);
            case STREEBOG -> getStreebogAlgorithm(myLen);
            case RIPEMD -> getRIPEMDAlgorithm(myLen);
            case SKEIN -> getSkeinAlgorithm(pDigestSpec);
            case SHA3 -> getSHA3Algorithm(myLen);
            case BLAKE2 -> getBlake2Algorithm(pDigestSpec);
            case KUPYNA -> getKupynaAlgorithm(myLen);
            case SHAKE -> getSHAKEAlgorithm(pDigestSpec.getCoreDigestState());
            case HARAKA -> pDigestSpec.toString();
            case GOST -> "GOST3411";
            case WHIRLPOOL, TIGER, SHA1, MD5, MD4, MD2, SM3 -> myType.name();
            case BLAKE3 -> pDigestSpec.toString();
            default -> throw new GordianDataException("Invalid DigestSpec :- " + pDigestSpec);
        };
    }

    /**
     * Determine the RIPEMD algorithm.
     *
     * @param pLength the digest length
     * @return the name
     */
    private static String getRIPEMDAlgorithm(final GordianLength pLength) {
        return switch (pLength) {
            case LEN_128 -> "RIPEMD128";
            case LEN_160 -> "RIPEMD160";
            case LEN_256 -> "RIPEMD256";
            default -> "RIPEMD320";
        };
    }

    /**
     * Determine the Blake2 algorithm.
     *
     * @param pSpec the digestSpec
     * @return the name
     */
    private static String getBlake2Algorithm(final GordianCoreDigestSpec pSpec) {
        return pSpec.toString();
    }

    /**
     * Determine the Kupyna algorithm.
     *
     * @param pLength the digest length
     * @return the name
     */
    private static String getKupynaAlgorithm(final GordianLength pLength) {
        return switch (pLength) {
            case LEN_256 -> "DSTU7564-256";
            case LEN_384 -> "DSTU7564-384";
            default -> "DSTU7564-512";
        };
    }

    /**
     * Determine the SHA2 algorithm.
     *
     * @param pSpec the digestSpec
     * @return the name
     */
    private static String getSHA2Algorithm(final GordianCoreDigestSpec pSpec) {
        /* Access lengths */
        final GordianDigestState myState = pSpec.getDigestState();
        final GordianLength myLen = pSpec.getDigestLength();

        /* Switch on length */
        return switch (myLen) {
            case LEN_224 -> GordianDigestState.STATE256.equals(myState)
                    ? "SHA224"
                    : "SHA-512/224";
            case LEN_256 -> GordianDigestState.STATE256.equals(myState)
                    ? "SHA256"
                    : "SHA-512/256";
            case LEN_384 -> "SHA384";
            default -> "SHA512";
        };
    }

    /**
     * Determine the SHA3 algorithm.
     *
     * @param pLength the digest length
     * @return the name
     */
    private static String getSHA3Algorithm(final GordianLength pLength) {
        return switch (pLength) {
            case LEN_224 -> "SHA3-224";
            case LEN_256 -> "SHA3-256";
            case LEN_384 -> "SHA3-384";
            default -> "SHA3-512";
        };
    }

    /**
     * Determine the SHAKE algorithm.
     *
     * @param pState the stateLength
     * @return the name
     */
    private static String getSHAKEAlgorithm(final GordianCoreDigestState pState) {
        /* Determine SHAKE digest */
        return "SHAKE" + pState;
    }

    /**
     * Determine the Skein algorithm.
     *
     * @param pSpec the digestSpec
     * @return the name
     */
    private static String getSkeinAlgorithm(final GordianCoreDigestSpec pSpec) {
        return "Skein-"
                + pSpec.getCoreDigestState()
                + '-'
                + pSpec.getDigestLength();
    }

    /**
     * Determine the Streebog algorithm.
     *
     * @param pLength the digest length
     * @return the name
     */
    private static String getStreebogAlgorithm(final GordianLength pLength) {
        return "GOST3411-2012-" + pLength;
    }

    /**
     * Determine whether the algorithm is supported.
     *
     * @param pDigestType the digest type
     * @return true/false
     */
    static boolean isHMacSupported(final GordianDigestType pDigestType) {
        return switch (pDigestType) {
            case BLAKE2, BLAKE3, KUPYNA, SHAKE -> false;
            default -> true;
        };
    }
}
