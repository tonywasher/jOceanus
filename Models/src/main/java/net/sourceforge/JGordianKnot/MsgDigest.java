/*******************************************************************************
 * Copyright 2012 Tony Washer
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
package net.sourceforge.JGordianKnot;

import java.security.MessageDigest;
import java.util.Arrays;

import net.sourceforge.JDataWalker.ModelException;
import net.sourceforge.JDataWalker.ModelException.ExceptionClass;
import net.sourceforge.JGordianKnot.DataHayStack.DigestNeedle;

public class MsgDigest {
    /**
     * The message digest
     */
    private final DigestType theType;

    /**
     * The message digest
     */
    private final MessageDigest theDigest;

    /**
     * The length of the data digested
     */
    private long theDataLen = 0;

    /**
     * The expected data length
     */
    private final long theExpectedDataLen;

    /**
     * The expected digest length
     */
    private final byte[] theExpectedDigest;

    /**
     * The digest name
     */
    private final String theName;

    /**
     * Obtain the digest type
     * @return the digest type
     */
    public DigestType getDigestType() {
        return theType;
    }

    /**
     * Obtain the data length
     * @return the data length
     */
    public long getDataLength() {
        return theDataLen;
    }

    /**
     * Constructor for random type
     * @param pGenerator the security generator
     * @throws ModelException
     */
    public MsgDigest(SecurityGenerator pGenerator) throws ModelException {
        /* Store the type */
        DigestType[] myType = DigestType.getRandomTypes(1, pGenerator.getRandom());
        theType = myType[0];

        /* Create the digest */
        theDigest = pGenerator.accessDigest(theType);

        /* No expected details */
        theName = null;
        theExpectedDataLen = -1;
        theExpectedDigest = null;
    }

    /**
     * Constructor
     * @param pGenerator the security generator
     * @param pType the digest type
     * @throws ModelException
     */
    public MsgDigest(SecurityGenerator pGenerator,
                     DigestType pType) throws ModelException {
        /* Store the type */
        theType = pType;

        /* Create the digest */
        theDigest = pGenerator.accessDigest(pType);

        /* No expected details */
        theName = null;
        theExpectedDataLen = -1;
        theExpectedDigest = null;
    }

    /**
     * Constructor from external format
     * @param pGenerator the security generator
     * @param pExternal the external format
     * @param pDataLen the expected data length
     * @param pName the name of the digest
     * @throws ModelException
     */
    public MsgDigest(SecurityGenerator pGenerator,
                     byte[] pExternal,
                     long pDataLen,
                     String pName) throws ModelException {
        /* Parse the External form */
        DigestNeedle myNeedle = new DigestNeedle(pExternal);

        /* Store the type */
        theType = myNeedle.getDigestType();

        /* Create the digest */
        theDigest = pGenerator.accessDigest(theType);

        /* Store the name of the digest */
        theName = pName;

        /* Store the expected details */
        theExpectedDataLen = pDataLen;
        theExpectedDigest = myNeedle.getDigest();
    }

    /**
     * Build the external format
     * @return the external format
     */
    public byte[] buildExternal() {
        /* Determine the external definition */
        DigestNeedle myNeedle = new DigestNeedle(theType, theDigest.digest());
        return myNeedle.getExternal();
    }

    /**
     * Validate digest
     * @throws ModelException
     */
    public void validateDigest() throws ModelException {
        /* If the data lengths do not match */
        if (theDataLen != theExpectedDataLen) {
            /* Throw an exception */
            throw new ModelException(ExceptionClass.DATA, "Mismatch on Data lengths for " + theName);
        }

        /* If the digest does not match */
        if (!Arrays.equals(theDigest.digest(), theExpectedDigest)) {
            /* Throw an exception */
            throw new ModelException(ExceptionClass.DATA, "Mismatch on Digest for " + theName);
        }
    }

    /**
     * Update the digest
     * @param pBytes the bytes to add
     */
    public void update(byte[] pBytes) {
        /* Update the digest */
        theDigest.update(pBytes);

        /* Adjust the data length */
        theDataLen += pBytes.length;
    }

    /**
     * Update the digest
     * @param pByte the byte to add
     */
    public void update(byte pByte) {
        /* Update the digest */
        theDigest.update(pByte);

        /* Adjust the data length */
        theDataLen++;
    }

    /**
     * Update the digest
     * @param pBytes the bytes to add
     * @param pOffset the offset in the buffer to start
     * @param pLength the length of data to add
     */
    public void update(byte[] pBytes,
                       int pOffset,
                       int pLength) {
        /* Update the digest */
        theDigest.update(pBytes, pOffset, pLength);

        /* Adjust the data length */
        theDataLen += pLength;
    }
}
