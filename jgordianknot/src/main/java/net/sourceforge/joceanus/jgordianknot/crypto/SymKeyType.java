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

import net.sourceforge.joceanus.jgordianknot.JGordianDataException;
import net.sourceforge.joceanus.jgordianknot.JGordianLogicException;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Symmetric Key Types. Available algorithms.
 */
public enum SymKeyType {
    /**
     * AES.
     */
    AES(1),

    /**
     * TwoFish.
     */
    TWOFISH(2),

    /**
     * Serpent.
     */
    SERPENT(3),

    /**
     * CAMELLIA.
     */
    CAMELLIA(4),

    /**
     * RC6.
     */
    RC6(5),

    /**
     * CAST6.
     */
    CAST6(6),

    /**
     * ThreeFish.
     */
    THREEFISH(7);

    /**
     * The external Id of the algorithm.
     */
    private final int theId;

    /**
     * The String name.
     */
    private String theName;

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = CryptoResource.getKeyForSym(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * Obtain the external Id.
     * @return the external Id
     */
    public int getId() {
        return theId;
    }

    /**
     * Obtain the algorithm.
     * @return the algorithm
     */
    public String getAlgorithm() {
        switch (this) {
            case TWOFISH:
                return "TwoFish";
            case SERPENT:
                return "Serpent";
            case THREEFISH:
                return "ThreeFish-256";
            default:
                return name();
        }
    }

    /**
     * Adjust CipherMode.
     * @param pMode the cipher mode
     * @return the adjusted cipher mode
     */
    public CipherMode adjustCipherMode(final CipherMode pMode) {
        /* If the mode needs a Standard block cipher and this is not one switch to OFB */
        return (pMode.needsStdBlock() && !isStdBlock())
                                                       ? CipherMode.OFB
                                                       : pMode;
    }

    /**
     * Obtain the data cipher name.
     * @param pMode the cipher mode
     * @return the data cipher name
     */
    public String getDataCipher(final CipherMode pMode) {
        /* Build the algorithm */
        return getAlgorithm()
               + "/"
               + pMode.getCipherMode()
               + getPadding(pMode);
    }

    /**
     * Obtain the padding.
     * @param pMode the cipher mode
     * @return the data cipher name
     */
    private String getPadding(final CipherMode pMode) {
        switch (pMode) {
            case CBC:
                return "/PKCS7PADDING";
            default:
                return "/NOPADDING";
        }
    }

    /**
     * Constructor.
     * @param id the id
     */
    private SymKeyType(final int id) {
        theId = id;
    }

    /**
     * Does this KeyType use a standard block size?
     * @return true/false
     */
    public boolean isStdBlock() {
        switch (this) {
            case THREEFISH:
                return false;
            default:
                return true;
        }
    }

    /**
     * get value from id.
     * @param id the id value
     * @return the corresponding enum object
     * @throws JOceanusException on error
     */
    public static SymKeyType fromId(final int id) throws JOceanusException {
        for (SymKeyType myType : values()) {
            if (myType.getId() == id) {
                return myType;
            }
        }
        throw new JGordianDataException("Invalid SymKeyType: "
                                        + id);
    }

    /**
     * Get random unique set of symmetric key types.
     * @param pNumTypes the number of types
     * @param pRandom the random generator
     * @return the random set
     * @throws JOceanusException on error
     */
    public static SymKeyType[] getRandomTypes(final int pNumTypes,
                                              final SecureRandom pRandom) throws JOceanusException {
        /* Use all values */
        return getRandomTypes(pNumTypes, pRandom, false);
    }

    /**
     * Get random unique set of symmetric key types.
     * @param pNumTypes the number of types
     * @param pRandom the random generator
     * @param pStdBlock use only standard block size
     * @return the random set
     * @throws JOceanusException on error
     */
    public static SymKeyType[] getRandomTypes(final int pNumTypes,
                                              final SecureRandom pRandom,
                                              final boolean pStdBlock) throws JOceanusException {
        /* Access the values */
        SymKeyType[] myValues = values();
        int iNumValues = myValues.length;

        /* Reject call if invalid number of types */
        if ((pNumTypes < 1)
            || (pNumTypes > iNumValues)) {
            throw new JGordianLogicException("Invalid number of symmetric keys: "
                                             + pNumTypes);
        }

        /* Create the result set */
        SymKeyType[] myTypes = new SymKeyType[pNumTypes];

        /* Loop through the types */
        int i = 0;
        while (i < pNumTypes) {
            /* Access the next random index */
            int iIndex = pRandom.nextInt(iNumValues);

            /* If we are using all keyTypes or else this is a standard block */
            SymKeyType myType = myValues[iIndex];
            if ((!pStdBlock)
                || myType.isStdBlock()) {
                /* Store the type */
                myTypes[i++] = myValues[iIndex];
            }

            /* Shift last value down in place of the one thats been used */
            myValues[iIndex] = myValues[iNumValues - 1];
            iNumValues--;
        }

        /* Return the types */
        return myTypes;
    }
}
