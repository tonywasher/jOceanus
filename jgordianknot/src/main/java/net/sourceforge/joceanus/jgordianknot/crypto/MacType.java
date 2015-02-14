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
 * Mac types. Available algorithms.
 */
public enum MacType {
    /**
     * HMAC.
     */
    HMAC(1),

    /**
     * GMAC.
     */
    GMAC(2),

    /**
     * Poly1305.
     */
    POLY1305(3),

    /**
     * Skein.
     */
    SKEIN(4),

    /**
     * VMPC.
     */
    VMPC(5);

    /**
     * The external Id of the algorithm.
     */
    private int theId = 0;

    /**
     * The String name.
     */
    private String theName;

    /**
     * Constructor.
     * @param id the id
     */
    private MacType(final int id) {
        theId = id;
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = CryptoResource.getKeyForHMac(this).getValue();
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
     * get value from id.
     * @param id the id value
     * @return the corresponding enumeration object
     * @throws JOceanusException on error
     */
    public static MacType fromId(final int id) throws JOceanusException {
        for (MacType myType : values()) {
            if (myType.getId() == id) {
                return myType;
            }
        }
        throw new JGordianDataException("Invalid MacType: "
                                        + id);
    }

    /**
     * Obtain the algorithm.
     * @param pKeyType the symmetric key type (or null)
     * @return the algorithm
     */
    public String getAlgorithm(final SymKeyType pKeyType) {
        switch (this) {
            case GMAC:
                return (pKeyType.isStdBlock())
                                              ? pKeyType.name()
                                                + "-GMAC"
                                              : null;
            case POLY1305:
                return (pKeyType.isStdBlock())
                                              ? "POLY1305-"
                                                + pKeyType.name()
                                              : null;
            default:
                return null;
        }
    }

    /**
     * Obtain the Key generation algorithm.
     * @param bLong use long hashes?
     * @return the algorithm
     */
    public String getAlgorithm(final boolean bLong) {
        switch (this) {
            case VMPC:
                return "VMPC-MAC";
            case SKEIN:
                return "SKEIN-MAC-"
                       + ((bLong)
                                 ? "512-512"
                                 : "256-256");
            default:
                return null;
        }
    }

    /**
     * Obtain the IV length.
     * @return the IV Length
     */
    public int getIVLen() {
        switch (this) {
            case VMPC:
            case GMAC:
                return CipherSet.IVSIZE;
            case POLY1305:
                return CipherSet.IVSIZE >> 1;
            default:
                return 0;
        }
    }

    /**
     * Obtain the Key generation algorithm.
     * @param bLong use long hashes?
     * @return the algorithm
     */
    public String getKeyAlgorithm(final boolean bLong) {
        switch (this) {
            case VMPC:
                return "VMPC-KSA3";
            case SKEIN:
                return getAlgorithm(bLong);
            default:
                return null;
        }
    }

    /**
     * Get random unique set of Mac types.
     * @param pNumTypes the number of types
     * @param pRandom the random generator
     * @return the random set
     * @throws JOceanusException on error
     */
    public static MacType[] getRandomTypes(final int pNumTypes,
                                           final SecureRandom pRandom) throws JOceanusException {
        /* Access the values */
        MacType[] myValues = values();
        int iNumValues = myValues.length;

        /* Reject call if invalid number of types */
        if ((pNumTypes < 1)
            || (pNumTypes > iNumValues)) {
            throw new JGordianLogicException("Invalid number of Macs: "
                                             + pNumTypes);
        }

        /* Create the result set */
        MacType[] myTypes = new MacType[pNumTypes];

        /* Loop through the types */
        for (int i = 0; i < pNumTypes; i++) {
            /* Access the next random index */
            int iIndex = pRandom.nextInt(iNumValues);

            /* Store the type */
            myTypes[i] = myValues[iIndex];

            /* Shift last value down in place of the one thats been used */
            myValues[iIndex] = myValues[iNumValues - 1];
            iNumValues--;
        }

        /* Return the types */
        return myTypes;
    }
}
