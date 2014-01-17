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
package net.sourceforge.joceanus.jgordianknot;

import java.nio.ByteBuffer;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Encapsulation of a MessageDigest.
 */
public class DataDigest {
    /**
     * The MessageDigest.
     */
    private final MessageDigest theDigest;

    /**
     * The DigestType.
     */
    private final DigestType theDigestType;

    /**
     * Obtain the digest type.
     * @return the digest type
     */
    public DigestType getDigestType() {
        return theDigestType;
    }

    /**
     * Obtain the digest length.
     * @return the digest length
     */
    public int getDigestLength() {
        return theDigest.getDigestLength();
    }

    /**
     * DataDigest Generator.
     * @param pGenerator the security generator
     * @return the new Digest
     * @throws JOceanusException on error
     */
    protected static DataDigest generateRandomDigest(final SecurityGenerator pGenerator) throws JOceanusException {
        /* Access random generator */
        SecureRandom myRandom = pGenerator.getRandom();
        DigestType[] myType = DigestType.getRandomTypes(1, myRandom);

        /* Generate a Digest for the Digest type */
        return new DataDigest(pGenerator, myType[0]);
    }

    /**
     * Constructor for a new message digest.
     * @param pGenerator the security generator
     * @param pDigestType DigestType
     * @throws JOceanusException on error
     */
    protected DataDigest(final SecurityGenerator pGenerator,
                         final DigestType pDigestType) throws JOceanusException {
        /* Store the DigestType */
        theDigestType = pDigestType;

        /* Protect against exceptions */
        try {
            /* Return a digest for the algorithm */
            boolean useLongHash = pGenerator.useLongHash();
            String myProviderName = pGenerator.getProviderName();
            theDigest = MessageDigest.getInstance(theDigestType.getAlgorithm(useLongHash), myProviderName);

            /* Catch exceptions */
        } catch (NoSuchProviderException | NoSuchAlgorithmException e) {
            /* Throw the exception */
            throw new JOceanusException("Failed to create Digest", e);
        }
    }

    /**
     * Constructor for a new message digest of random type.
     * @param pGenerator the security generator
     * @throws JOceanusException on error
     */
    protected DataDigest(final SecurityGenerator pGenerator) throws JOceanusException {
        /* Create digest for random digest type */
        this(pGenerator, DigestType.getRandomTypes(1, pGenerator.getRandom())[0]);
    }

    /**
     * Update the digest with a portion of a byte array.
     * @param pBytes the bytes to update with.
     * @param pOffset the offset of the data within the byte array
     * @param pLength the length of the data to use
     */
    public void update(final byte[] pBytes,
                       final int pOffset,
                       final int pLength) {
        /* Update the digest */
        theDigest.update(pBytes, pOffset, pLength);
    }

    /**
     * Update the digest with a byte array.
     * @param pBytes the bytes to update with.
     */
    public void update(final byte[] pBytes) {
        /* Update the digest */
        theDigest.update(pBytes);
    }

    /**
     * Update the digest with a single byte.
     * @param pByte the byte to update with.
     */
    public void update(final byte pByte) {
        /* Update the digest */
        theDigest.update(pByte);
    }

    /**
     * Update the digest with a byteBuffer.
     * @param pBuffer the buffer to update with.
     */
    public void update(final ByteBuffer pBuffer) {
        /* Update the digest */
        theDigest.update(pBuffer);
    }

    /**
     * Reset the digest.
     */
    public void reset() {
        /* Reset the digest */
        theDigest.reset();
    }

    /**
     * Calculate the digest and reset it.
     * @return the digest
     */
    public byte[] finish() {
        /* Calculate the digest */
        return theDigest.digest();
    }

    /**
     * Update the digest, calculate and reset it.
     * @param pBytes the bytes to update with.
     * @return the digest
     */
    public byte[] finish(final byte[] pBytes) {
        /* Calculate the digest */
        return theDigest.digest(pBytes);
    }

    /**
     * Calculate the Digest, and return it in the buffer provided.
     * @param pBuffer the buffer to return the digest in.
     * @param pOffset the offset in the buffer to store the digest.
     * @param pLen the number of bytes in the buffer available for the digest.
     * @return the number of bytes placed into buffer
     * @throws JOceanusException if buffer too short
     */
    public int finish(final byte[] pBuffer,
                      final int pOffset,
                      final int pLen) throws JOceanusException {
        /* Calculate the digest */
        try {
            return theDigest.digest(pBuffer, pOffset, pLen);
        } catch (DigestException e) {
            /* Throw the exception */
            throw new JOceanusException("Failed to calculate Digest", e);
        }
    }
}
