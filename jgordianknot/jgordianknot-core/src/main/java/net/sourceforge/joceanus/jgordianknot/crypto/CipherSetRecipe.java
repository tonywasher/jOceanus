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
package net.sourceforge.joceanus.jgordianknot.crypto;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.function.Predicate;

import net.sourceforge.joceanus.jtethys.DataConverter;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Class for assembling/disassembling data encrypted by a CipherSet.
 */
public class CipherSetRecipe {
    /**
     * Cipher margins.
     */
    private static final int CIPHER_MARGIN = 4;

    /**
     * No IV flag.
     */
    private static final byte NOIV_FLAG = Byte.MIN_VALUE;

    /**
     * The Recipe.
     */
    private final byte[] theRecipe;

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
     * Constructor for random choices.
     * @param pGenerator the security generator
     * @param pCreateIV create an IV
     * @throws JOceanusException on error
     */
    protected CipherSetRecipe(final SecurityGenerator pGenerator,
                              final boolean pCreateIV) throws JOceanusException {
        /* Obtain the secureRandom and the cipher steps */
        SecureRandom myRandom = pGenerator.getRandom();
        int myCipherSteps = pGenerator.getNumCipherSteps();

        /* Create the Recipe Bytes */
        int myLen = 1 + (myCipherSteps >> 1);
        theRecipe = new byte[myLen];

        /* Create the Initialisation vector if required */
        if (pCreateIV) {
            theInitVector = new byte[CipherSet.IVSIZE];
            myRandom.nextBytes(theInitVector);
        } else {
            theInitVector = null;
        }

        /* Allocate new set of parameters */
        theParams = new CipherParameters(pGenerator);
        theParams.buildRecipe(pGenerator, theRecipe);
        theBytes = null;
    }

    /**
     * Constructor for random choices.
     * @param pGenerator the security generator
     * @throws JOceanusException on error
     */
    protected CipherSetRecipe(final SecurityGenerator pGenerator) throws JOceanusException {
        /* Default to creating an IV */
        this(pGenerator, true);
    }

    /**
     * Constructor for external form parse.
     * @param pGenerator the security generator
     * @param pExternal the external form
     * @throws JOceanusException on error
     */
    protected CipherSetRecipe(final SecurityGenerator pGenerator,
                              final byte[] pExternal) throws JOceanusException {
        /* Determine whether we have an IV */
        byte myStart = pExternal[0];
        boolean hasIV = (myStart & NOIV_FLAG) == 0;
        myStart &= ~NOIV_FLAG;

        /* Determine number of cipher steps */
        int myCipherSteps = (myStart >> DataConverter.NYBBLE_SHIFT)
                            & DataConverter.NYBBLE_MASK;

        /* Determine data length */
        int myRecipeLen = 1 + (myCipherSteps >> 1);
        int myLen = pExternal.length;
        int myDataLen = myLen
                        - myRecipeLen;

        /* If we are using an IV */
        if (hasIV) {
            /* Allocate buffers */
            myDataLen -= CipherSet.IVSIZE;
            theRecipe = new byte[myRecipeLen];
            theInitVector = new byte[CipherSet.IVSIZE];
            theBytes = new byte[myDataLen];

            /* Determine offset position */
            int myOffSet = pExternal[myRecipeLen]
                           & DataConverter.NYBBLE_MASK;
            myOffSet += CIPHER_MARGIN;
            myOffSet = Math.min(myOffSet, myDataLen
                                          - CIPHER_MARGIN);

            /* Copy Data into buffers */
            System.arraycopy(pExternal, 0, theRecipe, 0, myRecipeLen);
            System.arraycopy(pExternal, myRecipeLen, theBytes, 0, myOffSet);
            System.arraycopy(pExternal, myOffSet
                                        + myRecipeLen, theInitVector, 0, CipherSet.IVSIZE);
            System.arraycopy(pExternal, myOffSet
                                        + myRecipeLen
                                        + CipherSet.IVSIZE, theBytes, myOffSet, myDataLen
                                                                                - myOffSet);

            /* else there is no IV */
        } else {
            /* Allocate buffers */
            theRecipe = new byte[myRecipeLen];
            theInitVector = null;
            theBytes = new byte[myDataLen];

            /* Copy Data into buffers */
            System.arraycopy(pExternal, 0, theRecipe, 0, myRecipeLen);
            System.arraycopy(pExternal, myRecipeLen, theBytes, 0, myDataLen);

            /* Clear the flag */
            theRecipe[0] &= ~NOIV_FLAG;
        }

        /* Allocate new set of parameters */
        theParams = new CipherParameters(pGenerator, theRecipe);
    }

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
     * Build External Format for data.
     * @param pData the encrypted data
     * @return the external form
     */
    public byte[] buildExternal(final byte[] pData) {
        /* Determine lengths */
        boolean useIV = theInitVector != null;
        int myRecipeLen = theRecipe.length;
        int myDataLen = pData.length;
        int myLen = myRecipeLen
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
            myOffSet = Math.min(myOffSet, myDataLen
                                          - CIPHER_MARGIN);

            /* Copy Data into buffer */
            System.arraycopy(theRecipe, 0, myBuffer, 0, myRecipeLen);
            System.arraycopy(pData, 0, myBuffer, myRecipeLen, myOffSet);
            System.arraycopy(theInitVector, 0, myBuffer, myOffSet
                                                         + myRecipeLen, CipherSet.IVSIZE);
            System.arraycopy(pData, myOffSet, myBuffer, myOffSet
                                                        + myRecipeLen
                                                        + CipherSet.IVSIZE, myDataLen
                                                                            - myOffSet);
            /* return the external format */
            return myBuffer;

            /* Else we have No IV */
        } else {
            /* Allocate the buffer */
            byte[] myBuffer = new byte[myLen];

            /* Build the buffer */
            System.arraycopy(theRecipe, 0, myBuffer, 0, myRecipeLen);
            System.arraycopy(pData, 0, myBuffer, myRecipeLen, myDataLen);

            /* Mark the buffer */
            myBuffer[0] |= NOIV_FLAG;

            /* return the external format */
            return myBuffer;
        }
    }

    /**
     * The parameters class.
     */
    private static final class CipherParameters {
        /**
         * The CipherSet.
         */
        private final SymKeyType[] theSymKeyTypes;

        /**
         * Construct the parameters from random key.
         * @param pGenerator the security generator
         * @throws JOceanusException on error
         */
        private CipherParameters(final SecurityGenerator pGenerator) throws JOceanusException {
            theSymKeyTypes = pGenerator.generateSymKeyTypes();
        }

        /**
         * Construct the parameters from recipe.
         * @param pGenerator the security generator
         * @param pRecipe the recipe bytes
         * @throws JOceanusException on error
         */
        private CipherParameters(final SecurityGenerator pGenerator,
                                 final byte[] pRecipe) throws JOceanusException {
            /* Obtain Id manager */
            SecurityIdManager myManager = pGenerator.getIdManager();

            /* Determine number of cipher steps */
            int myCipherSteps = (pRecipe[0] >> DataConverter.NYBBLE_SHIFT)
                                & DataConverter.NYBBLE_MASK;

            /* Determine the SymKey predicate */
            Predicate<SymKeyType> myPredicate = pGenerator.getSymKeyPredicate();

            /* Allocate the key types */
            int i = 0, j = 0;
            theSymKeyTypes = new SymKeyType[myCipherSteps];
            int myId = pRecipe[j++]
                       & DataConverter.NYBBLE_MASK;
            theSymKeyTypes[i++] = myManager.deriveSymKeyTypeFromExternalId(myId, myPredicate);
            while (i < myCipherSteps) {
                myId = (pRecipe[j] >> DataConverter.NYBBLE_SHIFT)
                       & DataConverter.NYBBLE_MASK;
                theSymKeyTypes[i++] = myManager.deriveSymKeyTypeFromExternalId(myId, myPredicate);
                if (i < myCipherSteps) {
                    myId = pRecipe[j++]
                           & DataConverter.NYBBLE_MASK;
                    theSymKeyTypes[i++] = myManager.deriveSymKeyTypeFromExternalId(myId, myPredicate);
                }
            }
        }

        /**
         * Obtain the SymKey Types.
         * @return the symKeyTypes
         */
        public SymKeyType[] getSymKeyTypes() {
            return theSymKeyTypes;
        }

        /**
         * Construct the external recipe.
         * @param pGenerator the security generator
         * @param pRecipe the recipe bytes to build
         * @throws JOceanusException on error
         */
        private void buildRecipe(final SecurityGenerator pGenerator,
                                 final byte[] pRecipe) throws JOceanusException {
            /* Obtain Id manager */
            SecurityIdManager myManager = pGenerator.getIdManager();

            /* Build the key bytes */
            int myNumCiphers = theSymKeyTypes.length;
            int i = 0, j = 0;
            pRecipe[i++] = (byte) ((myNumCiphers << DataConverter.NYBBLE_SHIFT) + myManager.getExternalId(theSymKeyTypes[j++]));
            while (j < myNumCiphers) {
                int myByte = myManager.getExternalId(theSymKeyTypes[j++]);
                myByte <<= DataConverter.NYBBLE_SHIFT;
                if (j < myNumCiphers) {
                    myByte += myManager.getExternalId(theSymKeyTypes[j++]);
                }
                pRecipe[i++] = (byte) myByte;
            }
        }
    }
}
