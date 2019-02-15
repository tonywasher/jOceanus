/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2018 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.impl.core.keyset;

import java.security.SecureRandom;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeyType;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Class for assembling/disassembling data encrypted by a KeySet.
 */
public final class GordianKeySetRecipe {
    /**
     * Recipe length (Integer).
     */
    static final int RECIPELEN = Integer.BYTES;

    /**
     * Salt length.
     */
    static final int SALTLEN = GordianLength.LEN_128.getByteLength();

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
     */
    GordianKeySetRecipe(final GordianCoreFactory pFactory) {
        /* Allocate new set of parameters */
        theParams = new GordianKeySetParameters(pFactory);
        theRecipe = theParams.getRecipe();
        theBytes = null;
    }

    /**
     * Constructor for external form parse.
     * @param pFactory the factory
     * @param pExternal the external form
     */
    GordianKeySetRecipe(final GordianCoreFactory pFactory,
                        final byte[] pExternal) {
        /* Determine data length */
        final int myLen = pExternal.length;
        final int myDataLen = myLen
                - RECIPELEN
                - SALTLEN;

        /* Allocate buffers */
        theRecipe = new byte[RECIPELEN];
        final byte[] mySalt = new byte[SALTLEN];
        theBytes = new byte[myDataLen];

        /* Copy Data into buffers */
        System.arraycopy(pExternal, 0, theRecipe, 0, RECIPELEN);
        System.arraycopy(pExternal, RECIPELEN, mySalt, 0, SALTLEN);
        System.arraycopy(pExternal, RECIPELEN
                + SALTLEN, theBytes, 0, myDataLen);

        /* Allocate new set of parameters */
        theParams = new GordianKeySetParameters(pFactory, theRecipe, mySalt);
    }

    /**
     * Obtain the keySet parameters.
     * @return the parameters
     */
    GordianKeySetParameters getParameters() {
        return theParams;
    }

    /**
     * Obtain the bytes.
     * @return the bytes
     */
    byte[] getBytes() {
        return theBytes;
    }

    /**
     * Build External Format for data.
     * @param pData the encrypted data
     * @return the external form
     */
    byte[] buildExternal(final byte[] pData) {
        /* Determine lengths */
        final int myDataLen = pData.length;
        final int myLen = RECIPELEN
                + myDataLen + SALTLEN;

        /* Allocate the buffer */
        final byte[] myBuffer = new byte[myLen];

        /* Copy Data into buffer */
        System.arraycopy(theRecipe, 0, myBuffer, 0, RECIPELEN);
        System.arraycopy(theParams.getSalt(), 0, myBuffer, RECIPELEN, SALTLEN);
        System.arraycopy(pData, 0, myBuffer, RECIPELEN + SALTLEN, myDataLen);

        /* return the external format */
        return myBuffer;
    }

    /**
     * The parameters class.
     */
    static final class GordianKeySetParameters {
        /**
         * The Recipe.
         */
        private final byte[] theRecipe;

        /**
         * The SymKeySet.
         */
        private final GordianSymKeyType[] theSymKeyTypes;

        /**
         * The Salt.
         */
        private final byte[] theSalt;

        /**
         * The Initialisation Vector.
         */
        private final byte[] theInitVector;

        /**
         * Construct the parameters from random.
         * @param pFactory the factory
         */
        GordianKeySetParameters(final GordianCoreFactory pFactory) {
            /* Obtain Id manager and random */
            final GordianCoreKeySetFactory myFactory = (GordianCoreKeySetFactory) pFactory.getKeySetFactory();
            final GordianIdManager myManager = myFactory.getIdManager();
            final GordianPersonalisation myPersonal = myFactory.getPersonalisation();
            final SecureRandom myRandom = pFactory.getRandomSource().getRandom();

            /* Allocate the initVector */
            theSalt = new byte[SALTLEN];
            myRandom.nextBytes(theSalt);

            /* Calculate the initVector */
            theInitVector = myPersonal.adjustIV(theSalt);

            /* Allocate the arrays */
            theSymKeyTypes = new GordianSymKeyType[pFactory.getNumCipherSteps()];

            /* Generate recipe and derive parameters */
            int mySeed = myRandom.nextInt();
            theRecipe = TethysDataConverter.integerToByteArray(mySeed);
            mySeed = myPersonal.convertRecipe(mySeed);
            myManager.deriveKeySetSymKeyTypesFromSeed(mySeed, theSymKeyTypes);
        }

        /**
         * Construct the parameters from recipe.
         * @param pFactory the factory
         * @param pRecipe the recipe bytes
         * @param pSalt the salt
         */
        GordianKeySetParameters(final GordianCoreFactory pFactory,
                                final byte[] pRecipe,
                                final byte[] pSalt) {
            /* Obtain Id manager */
            final GordianCoreKeySetFactory myFactory = (GordianCoreKeySetFactory) pFactory.getKeySetFactory();
            final GordianIdManager myManager = myFactory.getIdManager();
            final GordianPersonalisation myPersonal = myFactory.getPersonalisation();

            /* Store recipe and salt */
            theRecipe = pRecipe;
            theSalt = pSalt;

            /* Calculate the initVector */
            theInitVector = myPersonal.adjustIV(theSalt);

            /* Allocate the arrays */
            theSymKeyTypes = new GordianSymKeyType[pFactory.getNumCipherSteps()];

            /* derive parameters */
            int mySeed = TethysDataConverter.byteArrayToInteger(theRecipe);
            mySeed = myPersonal.convertRecipe(mySeed);
            myManager.deriveKeySetSymKeyTypesFromSeed(mySeed, theSymKeyTypes);
        }

        /**
         * Obtain the salt.
         * @return the salt
         */
        byte[] getSalt() {
            return theSalt;
        }

        /**
         * Obtain the SymKey Types.
         * @return the symKeyTypes
         */
        GordianSymKeyType[] getSymKeyTypes() {
            return theSymKeyTypes;
        }

        /**
         * Obtain the Initialisation vector.
         * @return the initialisation vector
         */
        byte[] getInitVector() {
            return theInitVector;
        }

        /**
         * Obtain the Recipe.
         * @return the recipe
         */
        byte[] getRecipe() {
            return theRecipe;
        }
    }
}
