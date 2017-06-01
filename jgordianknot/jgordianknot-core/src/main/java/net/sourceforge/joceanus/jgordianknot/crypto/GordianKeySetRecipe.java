/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2017 Tony Washer
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
     * @throws OceanusException on error
     */
    protected GordianKeySetRecipe(final GordianFactory pFactory) throws OceanusException {
        /* Allocate new set of parameters */
        theParams = new GordianKeySetParameters(pFactory);
        theRecipe = theParams.getRecipe();
        theBytes = null;
    }

    /**
     * Constructor for external form parse.
     * @param pFactory the factory
     * @param pExternal the external form
     * @throws OceanusException on error
     */
    protected GordianKeySetRecipe(final GordianFactory pFactory,
                                  final byte[] pExternal) throws OceanusException {
        /* Determine data length */
        int myRecipeLen = RECIPELEN;
        int myLen = pExternal.length;
        int myDataLen = myLen
                        - myRecipeLen
                        - IVSIZE;

        /* Allocate buffers */
        theRecipe = new byte[myRecipeLen];
        byte[] myInitVector = new byte[IVSIZE];
        theBytes = new byte[myDataLen];

        /* Determine offset position */
        int myOffSet = getCipherIndentation(pFactory, myDataLen);

        /* Copy Data into buffers */
        System.arraycopy(pExternal, 0, theBytes, 0, myOffSet);
        System.arraycopy(pExternal, myOffSet, theRecipe, 0, myRecipeLen);
        System.arraycopy(pExternal, myOffSet
                                    + myRecipeLen, myInitVector, 0, IVSIZE);
        myRecipeLen += IVSIZE;
        System.arraycopy(pExternal, myOffSet
                                    + myRecipeLen, theBytes, myOffSet, myDataLen
                                                                       - myOffSet);

        /* Allocate new set of parameters */
        theParams = new GordianKeySetParameters(pFactory, theRecipe, myInitVector);
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
     * Obtain the keySet parameters.
     * @return the parameters
     */
    protected GordianKeySetParameters getParameters() {
        return theParams;
    }

    /**
     * Obtain the bytes.
     * @return the bytes
     */
    protected byte[] getBytes() {
        return theBytes;
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
        int myRecipeLen = RECIPELEN;
        int myDataLen = pData.length;
        int myLen = myRecipeLen
                    + myDataLen + IVSIZE;

        /* Allocate the buffer */
        byte[] myBuffer = new byte[myLen];

        /* Determine offset position */
        int myOffSet = getCipherIndentation(pFactory, myDataLen);

        /* Copy Data into buffer */
        System.arraycopy(pData, 0, myBuffer, 0, myOffSet);
        System.arraycopy(theRecipe, 0, myBuffer, myOffSet, myRecipeLen);
        System.arraycopy(theParams.getInitVector(), 0, myBuffer, myOffSet
                                                                 + myRecipeLen, IVSIZE);
        myRecipeLen += IVSIZE;
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
         * The hMac.
         */
        private final GordianDigestType[] theHMacType;

        /**
         * The StreamKey.
         */
        private final GordianStreamKeyType[] theStreamKeyType;

        /**
         * The SymKeySet.
         */
        private final GordianSymKeyType[] theSymKeyTypes;

        /**
         * The Initialisation Vector.
         */
        private final byte[] theInitVector;

        /**
         * Construct the parameters from random.
         * @param pFactory the factory
         * @throws OceanusException on error
         */
        private GordianKeySetParameters(final GordianFactory pFactory) throws OceanusException {
            /* Obtain Id manager and random */
            GordianIdManager myManager = pFactory.getIdManager();
            SecureRandom myRandom = pFactory.getRandom();

            /* Allocate the initVector */
            theInitVector = new byte[IVSIZE];
            myRandom.nextBytes(theInitVector);

            /* Allocate the arrays */
            theHMacType = new GordianDigestType[1];
            theStreamKeyType = new GordianStreamKeyType[1];
            theSymKeyTypes = new GordianSymKeyType[pFactory.getNumCipherSteps()];

            /* Generate recipe and derive parameters */
            int mySeed = myRandom.nextInt();
            theRecipe = TethysDataConverter.integerToByteArray(mySeed);
            mySeed = myManager.deriveSymKeyTypesFromSeed(mySeed, theSymKeyTypes);
            mySeed = myManager.deriveStreamKeyTypesFromSeed(mySeed, theStreamKeyType);
            myManager.deriveKeyHashDigestTypesFromSeed(mySeed, theHMacType);
        }

        /**
         * Construct the parameters from recipe.
         * @param pFactory the factory
         * @param pRecipe the recipe bytes
         * @param pInitVector the initVector
         * @throws OceanusException on error
         */
        private GordianKeySetParameters(final GordianFactory pFactory,
                                        final byte[] pRecipe,
                                        final byte[] pInitVector) throws OceanusException {
            /* Obtain Id manager */
            GordianIdManager myManager = pFactory.getIdManager();

            /* Store recipe and initVector */
            theRecipe = pRecipe;
            theInitVector = pInitVector;

            /* Allocate the arrays */
            theHMacType = new GordianDigestType[1];
            theStreamKeyType = new GordianStreamKeyType[1];
            theSymKeyTypes = new GordianSymKeyType[pFactory.getNumCipherSteps()];

            /* derive parameters */
            int mySeed = TethysDataConverter.byteArrayToInteger(theRecipe);
            mySeed = myManager.deriveSymKeyTypesFromSeed(mySeed, theSymKeyTypes);
            mySeed = myManager.deriveStreamKeyTypesFromSeed(mySeed, theStreamKeyType);
            myManager.deriveKeyHashDigestTypesFromSeed(mySeed, theHMacType);
        }

        /**
         * Obtain the hMac Type.
         * @return the hMacType
         */
        protected GordianDigestType getHMacType() {
            return theHMacType[0];
        }

        /**
         * Obtain the streamKey Type.
         * @return the streamKeyType
         */
        protected GordianStreamKeyType getStreamKeyType() {
            return theStreamKeyType[0];
        }

        /**
         * Obtain the SymKey Types.
         * @return the symKeyTypes
         */
        protected GordianSymKeyType[] getSymKeyTypes() {
            return theSymKeyTypes;
        }

        /**
         * Obtain the Initialisation vector.
         * @return the initialisation vector
         */
        protected byte[] getInitVector() {
            return theInitVector;
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
