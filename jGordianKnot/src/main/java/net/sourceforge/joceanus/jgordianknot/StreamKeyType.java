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
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataException.ExceptionClass;

/**
 * Stream Key Type.
 */
public enum StreamKeyType {
    /**
     * XSalsa20.
     */
    XSALSA20(1),

    /**
     * HC256.
     */
    HC256(2),

    /**
     * ChaCha.
     */
    CHACHA(3),

    /**
     * VMPC.
     */
    VMPC(4);

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(StreamKeyType.class.getName());

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
            theName = NLS_BUNDLE.getString(name());
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
     * @param pRestricted use restricted algorithms
     * @return the algorithm
     */
    public String getAlgorithm(final boolean pRestricted) {
        switch (this) {
            case VMPC:
                return "VMPC-KSA3";
            case HC256:
                return pRestricted
                        ? "HC128"
                        : name();
            case XSALSA20:
                return pRestricted
                        ? "SALSA20"
                        : name();
            default:
                return name();
        }
    }

    /**
     * Constructor.
     * @param id the id
     */
    private StreamKeyType(final int id) {
        theId = id;
    }

    /**
     * get value from id.
     * @param id the id value
     * @return the corresponding enum object
     * @throws JDataException on error
     */
    public static StreamKeyType fromId(final int id) throws JDataException {
        for (StreamKeyType myType : values()) {
            if (myType.getId() == id) {
                return myType;
            }
        }
        throw new JDataException(ExceptionClass.DATA, "Invalid StreamKeyType: "
                                                      + id);
    }

    /**
     * Check the number of types.
     * @param pNumTypes the number of stream keys
     */
    private static void checkNumTypes(final int pNumTypes) {
        /* Access the values */
        StreamKeyType[] myValues = values();
        int myNumTypes = myValues.length;

        /* Validate number of types */
        if ((pNumTypes < 1)
            || (pNumTypes > myNumTypes)) {
            /* Throw exception */
            throw new IllegalArgumentException("Invalid number of stream keys");
        }
    }

    /**
     * Determine bound of random integer for choice of random StreamKeys.
     * @param pNumTypes the number of digests
     * @return the bound of the random integer.
     */
    public static int getRandomBound(final int pNumTypes) {
        /* Validate number of types */
        checkNumTypes(pNumTypes);

        /* Access the values */
        StreamKeyType[] myValues = values();
        int myNumTypes = myValues.length;

        /* Initialise the bounds */
        int myBound = myNumTypes--;

        /* Loop through the types */
        for (int i = 1; i < pNumTypes; i++) {
            /* Factor in additional types */
            myBound *= myNumTypes--;
        }

        /* Return the bound */
        return myBound;
    }

    /**
     * Get random unique set of stream key types.
     * @param pNumTypes the number of types
     * @param pRandom the random generator
     * @return the random set
     * @throws JDataException on error
     */
    public static StreamKeyType[] getRandomTypes(final int pNumTypes,
                                                 final SecureRandom pRandom) throws JDataException {
        /* Access the values */
        StreamKeyType[] myValues = values();
        int iNumValues = myValues.length;
        int iIndex;

        /* Reject call if invalid number of types */
        if ((pNumTypes < 1)
            || (pNumTypes > iNumValues)) {
            throw new JDataException(ExceptionClass.LOGIC, "Invalid number of StreamKeys: "
                                                           + pNumTypes);
        }

        /* Create the result set */
        StreamKeyType[] myTypes = new StreamKeyType[pNumTypes];

        /* Loop through the types */
        for (int i = 0; i < pNumTypes; i++) {
            /* Access the next random index */
            iIndex = pRandom.nextInt(iNumValues);

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
