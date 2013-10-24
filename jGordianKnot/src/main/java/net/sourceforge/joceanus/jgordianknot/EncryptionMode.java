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

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataException.ExceptionClass;

/**
 * Encryption Mode. Encapsulates Encryption options.
 * @author Tony Washer
 */
public class EncryptionMode
        extends NybbleArray {
    /**
     * The # types location (in nybbles).
     */
    private static final int PLACE_NUMTYPES = 0;

    /**
     * The SymKey location (in nybbles).
     */
    private static final int PLACE_SYMKEY = 1;

    /**
     * The SymKeyTypes.
     */
    private final SymKeyType[] theKeyTypes;

    /**
     * Obtain the SymKeyTypes.
     * @return the SymKeyTypes
     */
    protected SymKeyType[] getSymKeyTypes() {
        return theKeyTypes;
    }

    /**
     * Constructor at random.
     * @param pNumEncrypts the number of keys
     * @param pRandom the random generator
     * @throws JDataException on error
     */
    protected EncryptionMode(final int pNumEncrypts,
                             final SecureRandom pRandom) throws JDataException {
        /* Access a random set of Key/DigestTypes */
        theKeyTypes = SymKeyType.getRandomTypes(pNumEncrypts, pRandom);

        /* encode the keyMode */
        encodeKeyMode();
    }

    /**
     * Constructor from encoded format.
     * @param pEncoded the encoded format
     * @throws JDataException on error
     */
    protected EncryptionMode(final byte[] pEncoded) throws JDataException {
        /* Set the initial encoded version */
        setEncoded(pEncoded);

        /* Obtain number of key types */
        int iNumEncrypts = getValue(PLACE_NUMTYPES);
        if (iNumEncrypts > SymKeyType.values().length) {
            throw new JDataException(ExceptionClass.DATA, "Invalid number of encryption steps: "
                                                          + iNumEncrypts);
        }

        /* Allocate the array */
        theKeyTypes = new SymKeyType[iNumEncrypts];

        /* Loop through the key types */
        for (int i = 0; i < iNumEncrypts; i++) {
            /* Pick up the key type */
            theKeyTypes[i] = SymKeyType.fromId(getValue(PLACE_SYMKEY
                                                        + i));
        }

        /* Re-encode the key mode */
        encodeKeyMode();
    }

    /**
     * Encode the key mode.
     */
    private void encodeKeyMode() {
        int iNumEncrypts = theKeyTypes.length;

        /* Allocate the encoded array */
        allocateEncoded(PLACE_SYMKEY
                        + iNumEncrypts
                        - 1);

        /* Set the number of keys */
        setValue(PLACE_NUMTYPES, iNumEncrypts);

        /* Loop through the key types */
        for (int i = 0; i < iNumEncrypts; i++) {
            /* Set the key type */
            setValue(PLACE_SYMKEY
                     + i, theKeyTypes[i].getId());
        }
    }
}
