/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2020 Tony Washer
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
import java.util.Random;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestType;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIdManager;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianPersonalisation;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianPersonalisation.GordianPersonalId;
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
     * Mac length.
     */
    static final int MACLEN = GordianLength.LEN_128.getByteLength();

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
     * Is this recipe for AEAD?
     */
    private final boolean forAEAD;

    /**
     * Constructor for new recipe.
     * @param pFactory the factory
     * @param pSpec the keySetSpec
     * @param pAEAD true/false is AEAD in use?
     */
    private GordianKeySetRecipe(final GordianCoreFactory pFactory,
                                final GordianKeySetSpec pSpec,
                                final boolean pAEAD) {
        /* Allocate new set of parameters */
        theParams = new GordianKeySetParameters(pFactory, pSpec, pAEAD);
        theRecipe = theParams.getRecipe();
        forAEAD = pAEAD;
        theBytes = null;
    }

    /**
     * Constructor for external form parse.
     * @param pFactory the factory
     * @param pSpec the keySetSpec
     * @param pExternal the external form
     * @param pAEAD true/false is AEAD in use?
     */
    private GordianKeySetRecipe(final GordianCoreFactory pFactory,
                                final GordianKeySetSpec pSpec,
                                final byte[] pExternal,
                                final boolean pAEAD) {
        /* Determine data length */
        final int myLen = pExternal.length;
        int myDataLen = myLen
                - RECIPELEN
                - SALTLEN;
        if (pAEAD) {
            myDataLen -= MACLEN;
        }

        /* Allocate buffers */
        forAEAD = pAEAD;
        theRecipe = new byte[RECIPELEN];
        final byte[] mySalt = new byte[SALTLEN];
        theBytes = new byte[myDataLen];
        final byte[] myMac = pAEAD ? new byte[MACLEN] : null;

        /* Copy Data into buffers */
        System.arraycopy(pExternal, 0, theRecipe, 0, RECIPELEN);
        System.arraycopy(pExternal, RECIPELEN, mySalt, 0, SALTLEN);
        System.arraycopy(pExternal, RECIPELEN
                + SALTLEN, theBytes, 0, myDataLen);
        if (pAEAD) {
            System.arraycopy(pExternal, RECIPELEN
                    + SALTLEN + myDataLen, myMac, 0, MACLEN);
        }

        /* Allocate new set of parameters */
        theParams = new GordianKeySetParameters(pFactory, pSpec, theRecipe, mySalt, myMac);
    }

    /**
     * Create a new recipe.
     * @param pFactory the factory
     * @param pSpec the keySetSpec
     * @param pAEAD true/false is AEAD in use?
     * @return the recipe
     */
    static GordianKeySetRecipe newRecipe(final GordianCoreFactory pFactory,
                                         final GordianKeySetSpec pSpec,
                                         final boolean pAEAD) {
        return new GordianKeySetRecipe(pFactory, pSpec, pAEAD);
    }

    /**
     * parse the encryption recipe.
     * @param pFactory the factory
     * @param pSpec the keySetSpec
     * @param pExternal the external form
     * @param pAEAD true/false is AEAD in use?
     * @return the recipe
     */
    static GordianKeySetRecipe parseRecipe(final GordianCoreFactory pFactory,
                                           final GordianKeySetSpec pSpec,
                                           final byte[] pExternal,
                                           final boolean pAEAD) {
        return new GordianKeySetRecipe(pFactory, pSpec, pExternal, pAEAD);
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
        int myLen = RECIPELEN
                + myDataLen + SALTLEN;
        if (forAEAD) {
            myLen += MACLEN;
        }

        /* Allocate the buffer */
        final byte[] myBuffer = new byte[myLen];

        /* Copy Data into buffer */
        System.arraycopy(theRecipe, 0, myBuffer, 0, RECIPELEN);
        System.arraycopy(theParams.getSalt(), 0, myBuffer, RECIPELEN, SALTLEN);
        System.arraycopy(pData, 0, myBuffer, RECIPELEN + SALTLEN, myDataLen);
        if (forAEAD) {
            System.arraycopy(theParams.getMac(), 0, myBuffer, RECIPELEN + SALTLEN + myDataLen, MACLEN);
        }

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
         * The DigestType.
         */
        private final GordianDigestType[] theDigestType;

        /**
         * The Salt.
         */
        private final byte[] theSalt;

        /**
         * The Mac.
         */
        private final byte[] theMac;

        /**
         * The Initialisation Vector.
         */
        private final byte[] theInitVector;

        /**
         * Construct the parameters from random.
         * @param pFactory the factory
         * @param pSpec the keySetSpec
         * @param pAEAD true/false is AEAD in use?
         */
        GordianKeySetParameters(final GordianCoreFactory pFactory,
                                final GordianKeySetSpec pSpec,
                                final boolean pAEAD) {
            /* Obtain Id manager and random */
            final GordianIdManager myManager = pFactory.getIdManager();
            final GordianPersonalisation myPersonal = pFactory.getPersonalisation();
            final SecureRandom myRandom = pFactory.getRandomSource().getRandom();

            /* Allocate the initVector */
            theSalt = new byte[SALTLEN];
            myRandom.nextBytes(theSalt);
            theMac = pAEAD ? new byte[MACLEN] : null;

            /* Calculate the initVector */
            theInitVector = myPersonal.adjustIV(theSalt);

            /* Generate recipe and derive parameters */
            final int mySeed = myRandom.nextInt();
            theRecipe = TethysDataConverter.integerToByteArray(mySeed);
            final Random mySeededRandom = myPersonal.getSeededRandom(GordianPersonalId.KEYSETRANDOM, theRecipe);
            theSymKeyTypes = myManager.deriveKeySetSymKeyTypesFromSeed(mySeededRandom, pSpec.getKeyLength(), pSpec.getCipherSteps());
            theDigestType = pAEAD
                            ? myManager.deriveKeyHashDigestTypesFromSeed(mySeededRandom, 1)
                            : null;
        }

        /**
         * Construct the parameters from recipe.
         * @param pFactory the factory
         * @param pSpec the keySetSpec
         * @param pRecipe the recipe bytes
         * @param pSalt the salt
         * @param pMac the Mac
         */
        GordianKeySetParameters(final GordianCoreFactory pFactory,
                                final GordianKeySetSpec pSpec,
                                final byte[] pRecipe,
                                final byte[] pSalt,
                                final byte[] pMac) {
            /* Obtain Id manager */
            final GordianIdManager myManager = pFactory.getIdManager();
            final GordianPersonalisation myPersonal = pFactory.getPersonalisation();

            /* Store recipe, salt and Mac */
            theRecipe = pRecipe;
            theSalt = pSalt;
            theMac = pMac;
            final boolean forAEAD = pMac != null;

            /* Calculate the initVector */
            theInitVector = myPersonal.adjustIV(theSalt);

            /* derive parameters */
            final Random mySeededRandom = myPersonal.getSeededRandom(GordianPersonalId.KEYSETRANDOM, theRecipe);
            theSymKeyTypes = myManager.deriveKeySetSymKeyTypesFromSeed(mySeededRandom, pSpec.getKeyLength(), pSpec.getCipherSteps());
            theDigestType = forAEAD
                            ? myManager.deriveKeyHashDigestTypesFromSeed(mySeededRandom, 1)
                            : null;
        }

        /**
         * Obtain the salt.
         * @return the salt
         */
        byte[] getSalt() {
            return theSalt;
        }

        /**
         * Obtain the Mac.
         * @return the mac
         */
        byte[] getMac() {
            return theMac;
        }

        /**
         * Obtain the SymKey Types.
         * @return the symKeyTypes
         */
        GordianSymKeyType[] getSymKeyTypes() {
            return theSymKeyTypes;
        }

        /**
         * Obtain the Digest Type.
         * @return the digestType
         */
        GordianDigestType getDigestType() {
            return theDigestType != null ? theDigestType[0] : null;
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
