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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jgordianknot/jgordianknot-core/src/main/java/net/sourceforge/joceanus/jgordianknot/crypto/CipherSetRecipe.java $
 * $Revision: 647 $
 * $Author: Tony $
 * $Date: 2015-11-04 08:58:02 +0000 (Wed, 04 Nov 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.crypto;

import java.security.SecureRandom;
import java.util.Arrays;

import net.sourceforge.joceanus.jtethys.DataConverter;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Class for assembling/disassembling data encrypted by a KeySet.
 */
public final class GordianKeySetRecipe {
    /**
     * Initialisation Vector size (128/8).
     */
    protected static final int IVSIZE = 16;

    /**
     * IV margins.
     */
    private static final int IV_MARGIN = 4;

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
     * The KeySet Parameters.
     */
    private final KeySetParameters theParams;

    /**
     * Constructor for random choices.
     * @param pFactory the factory
     * @param pCreateIV create an IV
     * @throws JOceanusException on error
     */
    protected GordianKeySetRecipe(final GordianFactory pFactory,
                                  final boolean pCreateIV) throws JOceanusException {
        /* Obtain the secureRandom and the cipher steps */
        SecureRandom myRandom = pFactory.getRandom();
        int myCipherSteps = pFactory.getNumCipherSteps();

        /* Create the Recipe Bytes */
        int myLen = 1 + (myCipherSteps >> 1);
        theRecipe = new byte[myLen];

        /* Create the Initialisation vector if required */
        if (pCreateIV) {
            theInitVector = new byte[IVSIZE];
            myRandom.nextBytes(theInitVector);
        } else {
            theInitVector = null;
        }

        /* Allocate new set of parameters */
        theParams = new KeySetParameters(pFactory);
        theParams.buildRecipe(pFactory, theRecipe);
        theBytes = null;
    }

    /**
     * Constructor for external form parse.
     * @param pFactory the factory
     * @param pExternal the external form
     * @throws JOceanusException on error
     */
    protected GordianKeySetRecipe(final GordianFactory pFactory,
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
            myDataLen -= IVSIZE;
            theRecipe = new byte[myRecipeLen];
            theInitVector = new byte[IVSIZE];
            theBytes = new byte[myDataLen];

            /* Determine offset position */
            int myOffSet = getCipherIndentation(pFactory, myDataLen);

            /* Copy Data into buffers */
            System.arraycopy(pExternal, 0, theRecipe, 0, myRecipeLen);
            System.arraycopy(pExternal, myRecipeLen, theBytes, 0, myOffSet);
            System.arraycopy(pExternal, myOffSet
                                        + myRecipeLen, theInitVector, 0, IVSIZE);
            System.arraycopy(pExternal, myOffSet
                                        + myRecipeLen
                                        + IVSIZE, theBytes, myOffSet, myDataLen
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
        theParams = new KeySetParameters(pFactory, theRecipe);
    }

    /**
     * Obtain cipher indentation.
     * @param pFactory the factory
     * @param pDataLen the data length
     * @return the cipher indentation
     */
    private static int getCipherIndentation(final GordianFactory pFactory,
                                            final int pDataLen) {
        GordianIdManager myManager = pFactory.getIdManager();
        int myOffSet = myManager.getCipherIndentation();
        myOffSet += IV_MARGIN;
        return Math.min(myOffSet, pDataLen
                                  - IV_MARGIN);
    }

    /**
     * Obtain the SymKey Types.
     * @return the symKeyTypes
     */
    protected GordianSymKeyType[] getSymKeyTypes() {
        return theParams.getSymKeyTypes();
    }

    /**
     * Obtain the Initialisation vector.
     * @return the initialisation vector
     */
    protected byte[] getInitVector() {
        return (theInitVector == null)
                                       ? null
                                       : Arrays.copyOf(theInitVector, theInitVector.length);
    }

    /**
     * Obtain the bytes.
     * @return the bytes
     */
    protected byte[] getBytes() {
        return (theBytes == null)
                                  ? null
                                  : Arrays.copyOf(theBytes, theBytes.length);
    }

    /**
     * Build External Format for data.
     * @param pFactory the factory
     * @param pData the encrypted data
     * @return the external form
     */
    protected byte[] buildExternal(final GordianFactory pFactory,
                                   final byte[] pData) {
        /* Determine lengths */
        boolean useIV = theInitVector != null;
        int myRecipeLen = theRecipe.length;
        int myDataLen = pData.length;
        int myLen = myRecipeLen
                    + myDataLen;

        /* If we have an IV */
        if (useIV) {
            /* Allocate the new buffer */
            myLen += IVSIZE;
            byte[] myBuffer = new byte[myLen];

            /* Determine offset position */
            int myOffSet = getCipherIndentation(pFactory, myDataLen);

            /* Copy Data into buffer */
            System.arraycopy(theRecipe, 0, myBuffer, 0, myRecipeLen);
            System.arraycopy(pData, 0, myBuffer, myRecipeLen, myOffSet);
            System.arraycopy(theInitVector, 0, myBuffer, myOffSet
                                                         + myRecipeLen, IVSIZE);
            System.arraycopy(pData, myOffSet, myBuffer, myOffSet
                                                        + myRecipeLen
                                                        + IVSIZE, myDataLen
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
    private static final class KeySetParameters {
        /**
         * The CipherSet.
         */
        private final GordianSymKeyType[] theSymKeyTypes;

        /**
         * Construct the parameters from random key.
         * @param pFactory the factory
         * @throws JOceanusException on error
         */
        private KeySetParameters(final GordianFactory pFactory) throws JOceanusException {
            GordianIdManager myManager = pFactory.getIdManager();
            theSymKeyTypes = myManager.generateRandomSymKeyTypes(pFactory.getNumCipherSteps(), pFactory.standardSymKeys());
        }

        /**
         * Construct the parameters from recipe.
         * @param pFactory the factory
         * @param pRecipe the recipe bytes
         * @throws JOceanusException on error
         */
        private KeySetParameters(final GordianFactory pFactory,
                                 final byte[] pRecipe) throws JOceanusException {
            /* Obtain Id manager */
            GordianIdManager myManager = pFactory.getIdManager();

            /* Determine number of cipher steps */
            int myCipherSteps = (pRecipe[0] >> DataConverter.NYBBLE_SHIFT)
                                & DataConverter.NYBBLE_MASK;

            /* Allocate the key types */
            int i = 0, j = 0;
            theSymKeyTypes = new GordianSymKeyType[myCipherSteps];
            int myId = pRecipe[j++]
                       & DataConverter.NYBBLE_MASK;
            theSymKeyTypes[i++] = myManager.deriveSymKeyTypeFromExternalId(myId);
            while (i < myCipherSteps) {
                myId = (pRecipe[j] >> DataConverter.NYBBLE_SHIFT)
                       & DataConverter.NYBBLE_MASK;
                theSymKeyTypes[i++] = myManager.deriveSymKeyTypeFromExternalId(myId);
                if (i < myCipherSteps) {
                    myId = pRecipe[j++]
                           & DataConverter.NYBBLE_MASK;
                    theSymKeyTypes[i++] = myManager.deriveSymKeyTypeFromExternalId(myId);
                }
            }
        }

        /**
         * Obtain the SymKey Types.
         * @return the symKeyTypes
         */
        private GordianSymKeyType[] getSymKeyTypes() {
            return theSymKeyTypes;
        }

        /**
         * Construct the external recipe.
         * @param pFactory the factory
         * @param pRecipe the recipe bytes to build
         * @throws JOceanusException on error
         */
        private void buildRecipe(final GordianFactory pFactory,
                                 final byte[] pRecipe) throws JOceanusException {
            /* Obtain Id manager */
            GordianIdManager myManager = pFactory.getIdManager();

            /* Build the key bytes */
            int myNumCiphers = theSymKeyTypes.length;
            int i = 0, j = 0;
            pRecipe[i++] = (byte) ((myNumCiphers << DataConverter.NYBBLE_SHIFT) + myManager.deriveExternalIdFromSymKeyType(theSymKeyTypes[j++]));
            while (j < myNumCiphers) {
                int myByte = myManager.deriveExternalIdFromSymKeyType(theSymKeyTypes[j++]);
                myByte <<= DataConverter.NYBBLE_SHIFT;
                if (j < myNumCiphers) {
                    myByte += myManager.deriveExternalIdFromSymKeyType(theSymKeyTypes[j++]);
                }
                pRecipe[i++] = (byte) myByte;
            }
        }
    }
}
