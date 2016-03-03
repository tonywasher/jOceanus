/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2014 Tony Washer
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
                return "SKEIN-512-512";
            case KECCAK:
                return "KECCAK-512";
            case SHA2:
                return "SHA512";
            case RIPEMD:
                return "RIPEMD320";
            case GOST:
                return "GOST3411";
            case WHIRLPOOL:
            case TIGER:
                return pDigestType.name();
            case SM3:
            case BLAKE:
            default:
                throw new GordianDataException("Invalid DigestType :- " + pDigestType);
        }
    }

    /**
     * Determine whether the algorithm is supported.
     * @param pDigestType the digest type
     * @return true/false
     */
    protected static boolean isSupported(final GordianDigestType pDigestType) {
        switch (pDigestType) {
            case SM3:
            case BLAKE:
                return false;
            default:
                return true;
        }
    }
}
