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
package net.sourceforge.joceanus.gordianknot.impl.jca;

import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSubSpec.GordianDigestState;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestType;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.digest.GordianCoreDigest;
import net.sourceforge.joceanus.oceanus.base.OceanusException;

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
     * @param pDigestSpec the digestSpec
     * @param pDigest the digest
     */
    protected JcaDigest(final GordianDigestSpec pDigestSpec,
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
                        final int pOffset) throws OceanusException {
        try {
            return theDigest.digest(pBuffer, pOffset, getDigestSize());
        } catch (DigestException e) {
            throw new GordianCryptoException("Failed to calculate Digest", e);
        }
    }

    /**
     * Obtain the sha2 signature algorithm.
     * @param pDigestSpec the digestSpec
     * @return the algorithm
     * @throws OceanusException on error
     */
    static String getSignAlgorithm(final GordianDigestSpec pDigestSpec) throws OceanusException {
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
     * @param pDigestSpec the digestSpec
     * @return the algorithm
     * @throws OceanusException on error
     */
    static String getHMacAlgorithm(final GordianDigestSpec pDigestSpec) throws OceanusException {
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
     * @param pDigestSpec the digestSpec
     * @return the name
     * @throws OceanusException on error
     */
    static String getFullAlgorithm(final GordianDigestSpec pDigestSpec) throws OceanusException {
        /* Access standard name */
        final String myAlgorithm = getAlgorithm(pDigestSpec);

        switch (pDigestSpec.getDigestType()) {
            case SHAKE:
            case BLAKE3:
                return myAlgorithm + "-" + pDigestSpec.getDigestLength();
            default:
                return myAlgorithm;
        }
    }

    /**
     * Obtain the algorithm name.
     * @param pDigestSpec the digestSpec
     * @return the name
     * @throws OceanusException on error
     */
    static String getAlgorithm(final GordianDigestSpec pDigestSpec) throws OceanusException {
        /* Access digest details */
        final GordianDigestType myType = pDigestSpec.getDigestType();
        final GordianLength myLen = pDigestSpec.getDigestLength();

        /* Switch on digestType */
        switch (myType) {
            case SHA2:
                return getSHA2Algorithm(pDigestSpec);
            case STREEBOG:
                return getStreebogAlgorithm(myLen);
            case RIPEMD:
                return getRIPEMDAlgorithm(myLen);
            case SKEIN:
                return getSkeinAlgorithm(pDigestSpec);
            case SHA3:
                return getSHA3Algorithm(myLen);
            case BLAKE2:
                return getBlake2Algorithm(pDigestSpec);
            case KUPYNA:
                return getKupynaAlgorithm(myLen);
            case SHAKE:
                return getSHAKEAlgorithm(pDigestSpec.getDigestState());
            case HARAKA:
                return pDigestSpec.toString();
            case GOST:
                return "GOST3411";
            case WHIRLPOOL:
            case TIGER:
            case SHA1:
            case MD5:
            case MD4:
            case MD2:
            case SM3:
                return myType.name();
            case BLAKE3:
                return pDigestSpec.toString();
            default:
                throw new GordianDataException("Invalid DigestSpec :- " + pDigestSpec);
        }
    }

    /**
     * Determine the RIPEMD algorithm.
     * @param pLength the digest length
     * @return the name
     */
    private static String getRIPEMDAlgorithm(final GordianLength pLength) {
        switch (pLength) {
            case LEN_128:
                return "RIPEMD128";
            case LEN_160:
                return "RIPEMD160";
            case LEN_256:
                return "RIPEMD256";
            case LEN_320:
            default:
                return "RIPEMD320";
        }
    }

    /**
     * Determine the Blake2 algorithm.
     * @param pSpec the digestSpec
     * @return the name
     */
    private static String getBlake2Algorithm(final GordianDigestSpec pSpec) {
        return pSpec.toString();
    }

    /**
     * Determine the Kupyna algorithm.
     * @param pLength the digest length
     * @return the name
     */
    private static String getKupynaAlgorithm(final GordianLength pLength) {
        switch (pLength) {
            case LEN_256:
                return "DSTU7564-256";
            case LEN_384:
                return "DSTU7564-384";
            case LEN_512:
            default:
                return "DSTU7564-512";
        }
    }

    /**
     * Determine the SHA2 algorithm.
     * @param pSpec the digestSpec
     * @return the name
     */
    private static String getSHA2Algorithm(final GordianDigestSpec pSpec) {
        /* Access lengths */
        final GordianDigestState myState = pSpec.getDigestState();
        final GordianLength myLen = pSpec.getDigestLength();

        /* Switch on length */
        switch (myLen) {
            case LEN_224:
                return GordianDigestState.STATE256.equals(myState)
                       ? "SHA224"
                       : "SHA-512/224";
            case LEN_256:
                return GordianDigestState.STATE256.equals(myState)
                       ? "SHA256"
                       : "SHA-512/256";
            case LEN_384:
                return "SHA384";
            case LEN_512:
            default:
                return "SHA512";
        }
    }

    /**
     * Determine the SHA3 algorithm.
     * @param pLength the digest length
     * @return the name
     */
    private static String getSHA3Algorithm(final GordianLength pLength) {
        switch (pLength) {
            case LEN_224:
                return "SHA3-224";
            case LEN_256:
                return "SHA3-256";
            case LEN_384:
                return "SHA3-384";
            case LEN_512:
            default:
                return "SHA3-512";
        }
    }

    /**
     * Determine the SHAKE algorithm.
     * @param pState the stateLength
     * @return the name
     */
    private static String getSHAKEAlgorithm(final GordianDigestState pState) {
        /* Determine SHAKE digest */
        return "SHAKE" + pState;
    }

    /**
     * Determine the Skein algorithm.
     * @param pSpec the digestSpec
     * @return the name
     */
    private static String getSkeinAlgorithm(final GordianDigestSpec pSpec) {
        return "Skein-"
                + pSpec.getDigestState()
                + '-'
                + pSpec.getDigestLength();
    }

    /**
     * Determine the Streebog algorithm.
     * @param pLength the digest length
     * @return the name
     */
    private static String getStreebogAlgorithm(final GordianLength pLength) {
        return "GOST3411-2012-" + pLength;
    }

    /**
     * Determine whether the algorithm is supported.
     * @param pDigestType the digest type
     * @return true/false
     */
    static boolean isHMacSupported(final GordianDigestType pDigestType) {
        switch (pDigestType) {
            case BLAKE2:
            case BLAKE3:
            case KUPYNA:
            case SHAKE:
                return false;
            default:
                return true;
        }
    }
}
