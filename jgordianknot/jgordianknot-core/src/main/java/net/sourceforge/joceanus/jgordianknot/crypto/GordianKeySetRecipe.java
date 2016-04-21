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

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Class for assembling/disassembling data encrypted by a KeySet.
 */
public final class GordianKeySetRecipe {
    /**
     * Recipe length (Integer).
     */
    protected static final int RECIPELEN = Integer.BYTES;

    /**
     * Initialisation Vector size (128/8).
     */
    private static final int IVSIZE = GordianFactory.IVSIZE;

    /**
     * Margins.
     */
    private static final int IMBED_MARGIN = 4;

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
    private final GordianKeySetParameters theParams;

    /**
     * Constructor for random choices.
     * @param pFactory the factory
     * @param pCreateIV create an IV
     * @throws OceanusException on error
     */
    protected GordianKeySetRecipe(final GordianFactory pFactory,
                                  final boolean pCreateIV) throws OceanusException {
        /* Allocate new set of parameters */
        theParams = new GordianKeySetParameters(pFactory);
        theRecipe = theParams.getRecipe();
        theBytes = null;

        /* Create the Initialisation vector if required */
        if (pCreateIV) {
            /* Obtain the secureRandom and the cipher steps */
            SecureRandom myRandom = pFactory.getRandom();

            theInitVector = new byte[IVSIZE];
            myRandom.nextBytes(theInitVector);
        } else {
            theInitVector = null;
        }
    }

    /**
     * Constructor for external form parse.
     * @param pFactory the factory
     * @param pExternal the external form
     * @param pHasIV derive an IV
     * @throws OceanusException on error
     */
    protected GordianKeySetRecipe(final GordianFactory pFactory,
                                  final byte[] pExternal,
                                  final boolean pHasIV) throws OceanusException {
        /* Determine data length */
        int myRecipeLen = RECIPELEN;
        int myLen = pExternal.length;
        int myDataLen = myLen
                        - myRecipeLen;

        /* If we are using an IV */
        if (pHasIV) {
            /* Adjust DataLen */
            myDataLen -= IVSIZE;
        }

        /* Allocate buffers */
        theRecipe = new byte[myRecipeLen];
        theInitVector = pHasIV
                               ? new byte[IVSIZE]
                               : null;
        theBytes = new byte[myDataLen];

        /* Determine offset position */
        int myOffSet = getCipherIndentation(pFactory, myDataLen);

        /* Copy Data into buffers */
        System.arraycopy(pExternal, 0, theBytes, 0, myOffSet);
        System.arraycopy(pExternal, myOffSet, theRecipe, 0, myRecipeLen);
        if (pHasIV) {
            System.arraycopy(pExternal, myOffSet
                                        + myRecipeLen, theInitVector, 0, IVSIZE);
            myRecipeLen += IVSIZE;
        }
        System.arraycopy(pExternal, myOffSet
                                    + myRecipeLen, theBytes, myOffSet, myDataLen
                                                                       - myOffSet);

        /* Allocate new set of parameters */
        theParams = new GordianKeySetParameters(pFactory, theRecipe);
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
        myOffSet += IMBED_MARGIN;
        return Math.min(myOffSet, pDataLen
                                  - IMBED_MARGIN);
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
        int myRecipeLen = RECIPELEN;
        int myDataLen = pData.length;
        int myLen = myRecipeLen
                    + myDataLen;

        /* If we have an IV */
        if (useIV) {
            /* Increase the buffer size */
            myLen += IVSIZE;
        }

        /* Allocate the buffer */
        byte[] myBuffer = new byte[myLen];

        /* Determine offset position */
        int myOffSet = getCipherIndentation(pFactory, myDataLen);

        /* Copy Data into buffer */
        System.arraycopy(pData, 0, myBuffer, 0, myOffSet);
        System.arraycopy(theRecipe, 0, myBuffer, myOffSet, myRecipeLen);
        if (useIV) {
            System.arraycopy(theInitVector, 0, myBuffer, myOffSet
                                                         + myRecipeLen, IVSIZE);
            myRecipeLen += IVSIZE;
        }
        System.arraycopy(pData, myOffSet, myBuffer, myOffSet
                                                    + myRecipeLen, myDataLen
                                                                   - myOffSet);

        /* return the external format */
        return myBuffer;
    }

    /**
     * The parameters class.
     */
    protected static final class GordianKeySetParameters {
        /**
         * The Recipe.
         */
        private final byte[] theRecipe;

        /**
         * The CipherSet.
         */
        private final GordianSymKeyType[] theSymKeyTypes;

        /**
         * Construct the parameters from random.
         * @param pFactory the factory
         * @throws OceanusException on error
         */
        private GordianKeySetParameters(final GordianFactory pFactory) throws OceanusException {
            /* Obtain Id manager and random */
            GordianIdManager myManager = pFactory.getIdManager();
            SecureRandom myRandom = pFactory.getRandom();

            /* Generate recipe and derive symKeyTypes */
            int mySeed = myRandom.nextInt();
            theRecipe = TethysDataConverter.integerToByteArray(mySeed);
            theSymKeyTypes = myManager.deriveSymKeyTypesFromSeed(mySeed, pFactory.getNumCipherSteps());
        }

        /**
         * Construct the parameters from recipe.
         * @param pFactory the factory
         * @param pRecipe the recipe bytes
         * @throws OceanusException on error
         */
        private GordianKeySetParameters(final GordianFactory pFactory,
                                        final byte[] pRecipe) throws OceanusException {
            /* Obtain Id manager */
            GordianIdManager myManager = pFactory.getIdManager();

            /* Store recipe and derive symKeyTypes */
            theRecipe = pRecipe;
            int mySeed = TethysDataConverter.byteArrayToInteger(theRecipe);
            theSymKeyTypes = myManager.deriveSymKeyTypesFromSeed(mySeed, pFactory.getNumCipherSteps());
        }

        /**
         * Obtain the SymKey Types.
         * @return the symKeyTypes
         */
        private GordianSymKeyType[] getSymKeyTypes() {
            return theSymKeyTypes;
        }

        /**
         * Obtain the Recipe.
         * @return the recipe
         */
        private byte[] getRecipe() {
            return theRecipe;
        }
    }
}
