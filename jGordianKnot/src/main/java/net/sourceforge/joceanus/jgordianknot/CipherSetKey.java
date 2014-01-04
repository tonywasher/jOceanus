/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2013 Tony Washer
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

import java.security.SecureRandom;
import java.util.Arrays;

import net.sourceforge.joceanus.jdatamanager.DataConverter;
import net.sourceforge.joceanus.jdatamanager.JDataException;

/**
 * Class for assembling/disassembling data encrypted by a CipherSet.
 */
public class CipherSetKey {
    /**
     * Cipher margins.
     */
    private static final int CIPHER_MARGIN = 4;

    /**
     * No IV flag.
     */
    private static final byte NOIV_FLAG = Byte.MIN_VALUE;

    /**
     * The KeyBytes.
     */
    private final byte[] theKeyBytes;

    /**
     * The Initialisation Vector.
     */
    private final byte[] theInitVector;

    /**
     * The Data bytes.
     */
    private final byte[] theBytes;

    /**
     * The Cipher Parameters.
     */
    private final CipherParameters theParams;

    /**
     * Obtain the SymKey Types.
     * @return the symKeyTypes
     */
    public SymKeyType[] getSymKeyTypes() {
        return theParams.getSymKeyTypes();
    }

    /**
     * Obtain the Initialisation vector.
     * @return the initialisation vector
     */
    public byte[] getInitVector() {
        return (theInitVector == null)
                ? null
                : Arrays.copyOf(theInitVector, theInitVector.length);
    }

    /**
     * Obtain the bytes.
     * @return the bytes
     */
    public byte[] getBytes() {
        return (theBytes == null)
                ? null
                : Arrays.copyOf(theBytes, theBytes.length);
    }

    /**
     * Constructor for random choices.
     * @param pGenerator the security generator
     * @param pCreateIV create an IV
     * @throws JDataException on error
     */
    protected CipherSetKey(final SecurityGenerator pGenerator,
                           final boolean pCreateIV) throws JDataException {
        /* Obtain the secureRandom and the cipher steps */
        SecureRandom myRandom = pGenerator.getRandom();
        int myCipherSteps = pGenerator.getNumCipherSteps();

        /* Create the KeyBytes */
        int myLen = 1 + (myCipherSteps >> 1);
        theKeyBytes = new byte[myLen];

        /* Create the Initialisation vector if required */
        if (pCreateIV) {
            theInitVector = new byte[CipherSet.IVSIZE];
            myRandom.nextBytes(theInitVector);
        } else {
            theInitVector = null;
        }

        /* Allocate new set of parameters */
        theParams = new CipherParameters(pGenerator);
        theBytes = null;
    }

    /**
     * Constructor for random choices.
     * @param pGenerator the security generator
     * @throws JDataException on error
     */
    protected CipherSetKey(final SecurityGenerator pGenerator) throws JDataException {
        /* Default to creating an IV */
        this(pGenerator, true);
    }

    /**
     * Constructor for external form parse.
     * @param pExternal the external form
     * @throws JDataException on error
     */
    protected CipherSetKey(final byte[] pExternal) throws JDataException {
        /* Determine whether we have an IV */
        byte myStart = pExternal[0];
        boolean hasIV = (myStart & NOIV_FLAG) == 0;
        myStart &= ~NOIV_FLAG;

        /* Determine number of cipher steps */
        int myCipherSteps = (myStart >> DataConverter.NYBBLE_SHIFT)
                            & DataConverter.NYBBLE_MASK;

        /* Determine data length */
        int myKeyLen = 1 + (myCipherSteps >> 1);
        int myLen = pExternal.length;
        int myDataLen = myLen
                        - myKeyLen;

        /* If we are using an IV */
        if (hasIV) {
            /* Allocate buffers */
            myDataLen -= CipherSet.IVSIZE;
            theKeyBytes = new byte[myKeyLen];
            theInitVector = new byte[CipherSet.IVSIZE];
            theBytes = new byte[myDataLen];

            /* Determine offset position */
            int myOffSet = pExternal[myKeyLen]
                           & DataConverter.NYBBLE_MASK;
            myOffSet += CIPHER_MARGIN;
            myOffSet = Math.max(myOffSet, myDataLen
                                          - CIPHER_MARGIN);

            /* Copy Data into buffers */
            System.arraycopy(pExternal, 0, theKeyBytes, 0, myKeyLen);
            System.arraycopy(pExternal, myKeyLen, theBytes, 0, myOffSet);
            System.arraycopy(pExternal, myOffSet
                                        + myKeyLen, theInitVector, 0, CipherSet.IVSIZE);
            System.arraycopy(pExternal, myOffSet
                                        + myKeyLen
                                        + CipherSet.IVSIZE, theBytes, myOffSet, myDataLen
                                                                                - myOffSet);

            /* else there is no IV */
        } else {
            /* Allocate buffers */
            theKeyBytes = new byte[myKeyLen];
            theInitVector = null;
            theBytes = new byte[myDataLen];

            /* Copy Data into buffers */
            System.arraycopy(pExternal, 0, theKeyBytes, 0, myKeyLen);
            System.arraycopy(pExternal, myKeyLen, theBytes, 0, myDataLen);

            /* Clear the flag */
            theKeyBytes[0] &= ~NOIV_FLAG;
        }

        /* Allocate new set of parameters */
        theParams = new CipherParameters();
    }

    /**
     * Build External Format for data.
     * @param pData the encrypted data
     * @return the external form
     */
    public byte[] buildExternal(final byte[] pData) {
        /* Determine lengths */
        boolean useIV = theInitVector != null;
        int myKeyLen = theKeyBytes.length;
        int myDataLen = pData.length;
        int myLen = myKeyLen
                    + myDataLen;

        /* If we have an IV */
        if (useIV) {
            /* Allocate the new buffer */
            myLen += CipherSet.IVSIZE;
            byte[] myBuffer = new byte[myLen];

            /* Determine offset position */
            int myOffSet = pData[0]
                           & DataConverter.NYBBLE_MASK;
            myOffSet += CIPHER_MARGIN;
            myOffSet = Math.max(myOffSet, myDataLen
                                          - CIPHER_MARGIN);

            /* Copy Data into buffer */
            System.arraycopy(theKeyBytes, 0, myBuffer, 0, myKeyLen);
            System.arraycopy(pData, 0, myBuffer, myKeyLen, myOffSet);
            System.arraycopy(theInitVector, 0, myBuffer, myOffSet
                                                         + myKeyLen, CipherSet.IVSIZE);
            System.arraycopy(pData, myOffSet, myBuffer, myOffSet
                                                        + myKeyLen
                                                        + CipherSet.IVSIZE, myDataLen
                                                                            - myOffSet);
            /* return the external format */
            return myBuffer;

            /* Else we have No IV */
        } else {
            /* Allocate the buffer */
            byte[] myBuffer = new byte[myLen];

            /* Build the buffer */
            System.arraycopy(theKeyBytes, 0, myBuffer, 0, myKeyLen);
            System.arraycopy(pData, 0, myBuffer, myKeyLen, myDataLen);

            /* Mark the buffer */
            myBuffer[0] |= NOIV_FLAG;

            /* return the external format */
            return myBuffer;
        }
    }

    /**
     * The parameters class.
     */
    private final class CipherParameters {
        /**
         * The CipherSet.
         */
        private final SymKeyType[] theSymKeyTypes;

        /**
         * Obtain the SymKey Types.
         * @return the symKeyTypes
         */
        public SymKeyType[] getSymKeyTypes() {
            return theSymKeyTypes;
        }

        /**
         * Construct the parameters from random key.
         * @param pGenerator the security generator
         * @throws JDataException on error
         */
        private CipherParameters(final SecurityGenerator pGenerator) throws JDataException {
            /* Obtain Digest list */
            int myNumCiphers = pGenerator.getNumCipherSteps();
            theSymKeyTypes = SymKeyType.getRandomTypes(pGenerator.getNumCipherSteps(), pGenerator.getRandom());

            /* Build the key bytes */
            int i = 0, j = 0;
            theKeyBytes[i++] = (byte) ((myNumCiphers << DataConverter.NYBBLE_SHIFT) + theSymKeyTypes[j++].getId());
            while (j < myNumCiphers) {
                int myByte = theSymKeyTypes[j++].getId();
                myByte <<= DataConverter.NYBBLE_SHIFT;
                if (j < myNumCiphers) {
                    myByte += theSymKeyTypes[j++].getId();
                }
                theKeyBytes[i++] = (byte) myByte;
            }
        }

        /**
         * Construct the parameters from key bytes.
         * @throws JDataException on error
         */
        private CipherParameters() throws JDataException {
            /* Determine number of cipher steps */
            int myCipherSteps = (theKeyBytes[0] >> DataConverter.NYBBLE_SHIFT)
                                & DataConverter.NYBBLE_MASK;

            /* Allocate the key types */
            int i = 0, j = 0;
            theSymKeyTypes = new SymKeyType[myCipherSteps];
            int myId = theKeyBytes[j++]
                       & DataConverter.NYBBLE_MASK;
            theSymKeyTypes[i++] = SymKeyType.fromId(myId);
            while (i < myCipherSteps) {
                myId = (theKeyBytes[j] >> DataConverter.NYBBLE_SHIFT)
                       & DataConverter.NYBBLE_MASK;
                theSymKeyTypes[i++] = SymKeyType.fromId(myId);
                if (i < myCipherSteps) {
                    myId = theKeyBytes[j++]
                           & DataConverter.NYBBLE_MASK;
                    theSymKeyTypes[i++] = SymKeyType.fromId(myId);
                }
            }
        }
    }
}
