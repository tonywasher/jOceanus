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
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigestType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianLength;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Wrapper for JCA Digest.
 */
public final class JcaDigest
        implements GordianDigest {
    /**
     * The DigestType.
     */
    private final GordianDigestType theDigestType;

    /**
     * The MessageDigest.
     */
    private final MessageDigest theDigest;

    /**
     * Constructor.
     * @param pDigestType the digest type
     * @param pDigest the digest
     * @throws OceanusException on error
     */
    protected JcaDigest(final GordianDigestType pDigestType,
                        final MessageDigest pDigest) throws OceanusException {
        theDigestType = pDigestType;
        theDigest = pDigest;
    }

    @Override
    public GordianDigestType getDigestType() {
        return theDigestType;
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
     * Return the associated algorithm.
     * @param pDigestType the digest type
     * @return the algorithm
     * @throws OceanusException on error
     */
    protected static String getAlgorithm(final GordianDigestType pDigestType) throws OceanusException {
        switch (pDigestType) {
            case SKEIN:
                return getSkeinAlgorithm(GordianDigestType.SKEIN.getDefaultLength());
            case SHA3:
                return getSHA3Algorithm(GordianDigestType.SHA3.getDefaultLength());
            case SHA2:
                return getSHA2Algorithm(GordianDigestType.SHA2.getDefaultLength());
            case RIPEMD:
                return getRIPEMDAlgorithm(GordianDigestType.RIPEMD.getDefaultLength());
            case GOST:
                return getGOSTAlgorithm(GordianDigestType.GOST.getDefaultLength());
            case BLAKE:
                return getBlake2bAlgorithm(GordianDigestType.BLAKE.getDefaultLength());
            case WHIRLPOOL:
            case TIGER:
            case SHA1:
            case MD5:
            case SM3:
                return pDigestType.name();
            default:
                throw new GordianDataException("Invalid DigestType :- " + pDigestType);
        }
    }

    /**
     * Create the BouncyCastle digest.
     * @param pDigestType the digest type
     * @param pLength the digest length
     * @return the digest
     * @throws OceanusException on error
     */
    protected static String getAlgorithm(final GordianDigestType pDigestType,
                                         final GordianLength pLength) throws OceanusException {
        switch (pDigestType) {
            case SHA2:
                return getSHA2Algorithm(pLength);
            case GOST:
                return getGOSTAlgorithm(pLength);
            case RIPEMD:
                return getRIPEMDAlgorithm(pLength);
            case SKEIN:
                return getSkeinAlgorithm(pLength);
            case SHA3:
                return getSHA3Algorithm(pLength);
            case BLAKE:
                return getBlake2bAlgorithm(pLength);
            default:
                return getAlgorithm(pDigestType);
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
                return "RIPEMD128";
            case LEN_256:
                return "RIPEMD160";
            case LEN_384:
                return "RIPEMD256";
            case LEN_512:
            default:
                return "RIPEMD320";
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
        StringBuilder myBuilder = new StringBuilder();
        myBuilder.append("SKEIN-");
        myBuilder.append(myLen);
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
