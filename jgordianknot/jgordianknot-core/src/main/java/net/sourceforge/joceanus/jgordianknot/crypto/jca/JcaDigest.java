/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2016 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.crypto.jca;

import java.security.DigestException;
import java.security.MessageDigest;

import net.sourceforge.joceanus.jgordianknot.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigest;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigestType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianLength;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Wrapper for JCA Digest.
 */
public final class JcaDigest
        implements GordianDigest {
    /**
     * The DigestSpec.
     */
    private final GordianDigestSpec theDigestSpec;

    /**
     * The MessageDigest.
     */
    private final MessageDigest theDigest;

    /**
     * Constructor.
     * @param pDigestSpec the digestSpec
     * @param pDigest the digest
     * @throws OceanusException on error
     */
    protected JcaDigest(final GordianDigestSpec pDigestSpec,
                        final MessageDigest pDigest) throws OceanusException {
        theDigestSpec = pDigestSpec;
        theDigest = pDigest;
    }

    @Override
    public GordianDigestSpec getDigestSpec() {
        return theDigestSpec;
    }

    @Override
    public int getDigestSize() {
        return theDigest.getDigestLength();
    }

    @Override
    public void update(final byte[] pBytes,
                       final int pOffset,
                       final int pLength) {
        theDigest.update(pBytes, pOffset, pLength);
    }

    @Override
    public void update(final byte[] pBytes) {
        theDigest.update(pBytes);
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
    public int finish(final byte[] pBuffer,
                      final int pOffset) throws OceanusException {
        try {
            return theDigest.digest(pBuffer, pOffset, getDigestSize());
        } catch (DigestException e) {
            throw new GordianCryptoException("Failed to calculate Digest", e);
        }
    }

    /**
     * Create the BouncyCastle digest.
     * @param pDigestSpec the digestSpec
     * @return the digest
     * @throws OceanusException on error
     */
    protected static String getAlgorithm(final GordianDigestSpec pDigestSpec) throws OceanusException {
        /* Access digest details */
        GordianDigestType myType = pDigestSpec.getDigestType();
        GordianLength myLen = pDigestSpec.getDigestLength();

        /* Switch on digestType */
        switch (myType) {
            case SHA2:
                return getSHA2Algorithm(myLen);
            case GOST:
                return getGOSTAlgorithm(myLen);
            case RIPEMD:
                return getRIPEMDAlgorithm(myLen);
            case SKEIN:
                return getSkeinAlgorithm(myLen);
            case SHA3:
                return getSHA3Algorithm(myLen);
            case BLAKE:
                return getBlake2bAlgorithm(myLen);
            case WHIRLPOOL:
            case TIGER:
            case SHA1:
            case MD5:
            case SM3:
                return myType.name();
            default:
                throw new GordianDataException("Invalid DigestSpec :- " + pDigestSpec);
        }
    }

    /**
     * Determine the RIPEMD algorithm.
     * @param pLength the digest length
     * @return the digest
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
     * Determine the Blake2b algorithm.
     * @param pLength the digest length
     * @return the digest
     */
    private static String getBlake2bAlgorithm(final GordianLength pLength) {
        switch (pLength) {
            case LEN_160:
                return "BLAKE2B-160";
            case LEN_256:
                return "BLAKE2B-256";
            case LEN_384:
                return "BLAKE2B-384";
            case LEN_512:
            default:
                return "BLAKE2B-512";
        }
    }

    /**
     * Determine the SHA2 algorithm.
     * @param pLength the digest length
     * @return the digest
     */
    private static String getSHA2Algorithm(final GordianLength pLength) {
        switch (pLength) {
            case LEN_224:
                return "SHA224";
            case LEN_256:
                return "SHA256";
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
     * @return the digest
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
     * Determine the Skein algorithm.
     * @param pLength the digest length
     * @return the digest
     */
    private static String getSkeinAlgorithm(final GordianLength pLength) {
        String myLen = Integer.toString(pLength.getLength());
        String myState = Integer.toString(pLength.getSkeinState().getLength());
        StringBuilder myBuilder = new StringBuilder();
        myBuilder.append("Skein-");
        myBuilder.append(myState);
        myBuilder.append("-");
        myBuilder.append(myLen);
        return myBuilder.toString();
    }

    /**
     * Determine the GOST algorithm.
     * @param pLength the digest length
     * @return the digest
     */
    private static String getGOSTAlgorithm(final GordianLength pLength) {
        StringBuilder myBuilder = new StringBuilder();
        myBuilder.append("GOST3411-2012-");
        myBuilder.append(pLength.getLength());
        return myBuilder.toString();
    }

    /**
     * Determine whether the algorithm is supported.
     * @param pDigestType the digest type
     * @return true/false
     */
    protected static boolean isHMacSupported(final GordianDigestType pDigestType) {
        switch (pDigestType) {
            case SM3:
            case BLAKE:
                return false;
            default:
                return true;
        }
    }
}
