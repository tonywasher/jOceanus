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
 * Asymmetric Key Mode. Encapsulates Asymmetric Key options
 * @author Tony Washer
 */
public class AsymKeyMode
        extends SecurityMode {
    /**
     * The locations of the ASYMTYPE (in nybbles).
     */
    private static final int PLACE_ASYMTYPE = 0;

    /**
     * The locations of the DIGESTTYPE (in nybbles).
     */
    private static final int PLACE_DIGESTTYPE = 1;

    /**
     * The version.
     */
    public static final short VERSION_CURRENT = 1;

    /**
     * The Asymmetric KeyType.
     */
    private final AsymKeyType theAsymKeyType;

    /**
     * The Cipher Digest type.
     */
    private final DigestType theCipherDigest;

    /**
     * Obtain the Asymmetric Key Type.
     * @return the key type
     */
    public AsymKeyType getAsymKeyType() {
        return theAsymKeyType;
    }

    /**
     * Obtain the Cipher Digest type.
     * @return the digest type
     */
    public DigestType getCipherDigest() {
        return theCipherDigest;
    }

    /**
     * Constructor at random.
     * @param useRestricted use restricted keys
     * @param pRandom the random generator
     * @throws JDataException on error
     */
    protected AsymKeyMode(final boolean useRestricted,
                          final SecureRandom pRandom) throws JDataException {
        /* Access a random set of Key/DigestTypes */
        AsymKeyType[] myKeyType = AsymKeyType.getRandomTypes(1, pRandom);
        DigestType[] myDigest = DigestType.getRandomTypes(1, pRandom);

        /* Store Key type and digest */
        theAsymKeyType = myKeyType[0];
        theCipherDigest = myDigest[0];

        /* Set the version */
        setVersion(VERSION_CURRENT);

        /* Set restricted flags */
        setRestricted(useRestricted);

        /* encode the keyMode */
        encodeKeyMode();
    }

    /**
     * Constructor from a partner KeyMode.
     * @param useRestricted use restricted keys
     * @param pSource the key mode to initialise from
     */
    protected AsymKeyMode(final boolean useRestricted,
                          final AsymKeyMode pSource) {
        /* Store Key type and digest */
        theAsymKeyType = pSource.getAsymKeyType();
        theCipherDigest = pSource.getCipherDigest();

        /* Set the version */
        setVersion(VERSION_CURRENT);

        /* Use restricted if local or source requires it */
        setRestricted(useRestricted
                      || pSource.useRestricted());

        /* encode the keyMode */
        encodeKeyMode();
    }

    /**
     * Constructor from encoded format.
     * @param pEncoded the encoded format
     * @throws JDataException if the encoded mode is invalid
     */
    protected AsymKeyMode(final byte[] pEncoded) throws JDataException {
        /* Set the initial encoded version */
        setEncoded(pEncoded);

        /* Not allowed unless version is current */
        if (getVersion() != VERSION_CURRENT) {
            throw new JDataException(ExceptionClass.LOGIC, "Invalid mode version: "
                                                           + getVersion());
        }

        /* Store Key type and digest */
        theAsymKeyType = AsymKeyType.fromId(getDataValue(PLACE_ASYMTYPE));
        theCipherDigest = DigestType.fromId(getDataValue(PLACE_DIGESTTYPE));

        /* Re-encode the key mode */
        encodeKeyMode();
    }

    /**
     * Encode the key mode.
     */
    private void encodeKeyMode() {
        /* Allocate the encoded array */
        allocateEncoded(PLACE_DIGESTTYPE);

        /* Set the values */
        setDataValue(PLACE_ASYMTYPE, theAsymKeyType.getId());
        setDataValue(PLACE_DIGESTTYPE, theCipherDigest.getId());
    }
}
