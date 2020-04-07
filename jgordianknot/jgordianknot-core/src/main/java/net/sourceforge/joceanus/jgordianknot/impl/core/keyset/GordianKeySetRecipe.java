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
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIdManager;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianPersonalisation;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianPersonalisation.GordianPersonalId;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Class for assembling/disassembling data encrypted by a KeySet.
 */
public final class GordianKeySetRecipe {
    /**
     * Recipe length (Integer).
     */
    private static final int RECIPELEN = Integer.BYTES;

    /**
     * Salt length.
     */
    private static final int SALTLEN = GordianLength.LEN_128.getByteLength();

    /**
     * Salt length.
     */
    static final int HDRLEN = SALTLEN + RECIPELEN;

    /**
     * The Recipe.
     */
    private final byte[] theRecipe;

    /**
     * The KeySet Parameters.
     */
    private final GordianKeySetParameters theParams;

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
    }

    /**
     * Constructor for external form parse.
     * @param pFactory the factory
     * @param pSpec the keySetSpec
     * @param pHeader the header
     * @param pAEAD true/false is AEAD in use?
     */
    private GordianKeySetRecipe(final GordianCoreFactory pFactory,
                                final GordianKeySetSpec pSpec,
                                final byte[] pHeader,
                                final boolean pAEAD) {
         /* Allocate buffers */
        theRecipe = new byte[RECIPELEN];
        final byte[] mySalt = new byte[SALTLEN];

        /* Copy Data into buffers */
        System.arraycopy(pHeader, 0, theRecipe, 0, RECIPELEN);
        System.arraycopy(pHeader, RECIPELEN, mySalt, 0, SALTLEN);

        /* Allocate new set of parameters */
        theParams = new GordianKeySetParameters(pFactory, pSpec, theRecipe, mySalt, pAEAD);
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
     * @param pHeader the header
     * @param pAEAD true/false is AEAD in use?
     * @return the recipe
     * @throws OceanusException on error
     */
    static GordianKeySetRecipe parseRecipe(final GordianCoreFactory pFactory,
                                           final GordianKeySetSpec pSpec,
                                           final byte[] pHeader,
                                           final boolean pAEAD) throws OceanusException {
        /* Check that the input data is long enough */
        if (pHeader.length < HDRLEN) {
            throw new GordianDataException("Header too short");
        }

        /* Process the recipe */
        return new GordianKeySetRecipe(pFactory, pSpec, pHeader, pAEAD);
    }

    /**
     * Obtain the keySet parameters.
     * @return the parameters
     */
    GordianKeySetParameters getParameters() {
        return theParams;
    }

    /**
     * Build Header.
     * @param pHeader the header
     */
    void buildHeader(final byte[] pHeader) {
        /* Copy Data into buffer */
        System.arraycopy(theRecipe, 0, pHeader, 0, RECIPELEN);
        System.arraycopy(theParams.getSalt(), 0, pHeader, RECIPELEN, SALTLEN);
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
         * The Poly1305 SymKeyType.
         */
        private final GordianSymKeyType thePoly1305SymKeyType;

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

            /* Calculate the initVector */
            theInitVector = myPersonal.adjustIV(theSalt);

            /* Generate recipe and derive parameters */
            final int mySeed = myRandom.nextInt();
            theRecipe = TethysDataConverter.integerToByteArray(mySeed);
            final Random mySeededRandom = myPersonal.getSeededRandom(GordianPersonalId.KEYSETRANDOM, theRecipe);
            theSymKeyTypes = myManager.deriveKeySetSymKeyTypesFromSeed(mySeededRandom, pSpec.getKeyLength(), pSpec.getCipherSteps());
            thePoly1305SymKeyType = pAEAD
                            ? myManager.deriveKeySetSymKeyTypesFromSeed(mySeededRandom, pSpec.getKeyLength(), 1)[0]
                            : null;
        }

        /**
         * Construct the parameters from recipe.
         * @param pFactory the factory
         * @param pSpec the keySetSpec
         * @param pRecipe the recipe bytes
         * @param pSalt the salt
         * @param pAEAD true/false is AEAD in use?
         */
        GordianKeySetParameters(final GordianCoreFactory pFactory,
                                final GordianKeySetSpec pSpec,
                                final byte[] pRecipe,
                                final byte[] pSalt,
                                final boolean pAEAD) {
            /* Obtain Id manager */
            final GordianIdManager myManager = pFactory.getIdManager();
            final GordianPersonalisation myPersonal = pFactory.getPersonalisation();

            /* Store recipe, salt and Mac */
            theRecipe = pRecipe;
            theSalt = pSalt;

            /* Calculate the initVector */
            theInitVector = myPersonal.adjustIV(theSalt);

            /* derive parameters */
            final Random mySeededRandom = myPersonal.getSeededRandom(GordianPersonalId.KEYSETRANDOM, theRecipe);
            theSymKeyTypes = myManager.deriveKeySetSymKeyTypesFromSeed(mySeededRandom, pSpec.getKeyLength(), pSpec.getCipherSteps());
            thePoly1305SymKeyType = pAEAD
                            ? myManager.deriveKeySetSymKeyTypesFromSeed(mySeededRandom, pSpec.getKeyLength(), 1)[0]
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
         * Obtain the SymKey Types.
         * @return the symKeyTypes
         */
        GordianSymKeyType[] getSymKeyTypes() {
            return theSymKeyTypes;
        }

        /**
         * Obtain the Poly1305 symKeyType.
         * @return the symKeyType
         */
        GordianSymKeyType getPoly1305SymKeyType() {
            return thePoly1305SymKeyType;
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
