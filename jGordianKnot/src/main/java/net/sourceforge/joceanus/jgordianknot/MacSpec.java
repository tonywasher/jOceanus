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

import net.sourceforge.joceanus.jdatamanager.DataConverter;
import net.sourceforge.joceanus.jdatamanager.JDataException;

/**
 * Mac Specification.
 */
public class MacSpec {
    /**
     * The Mac Type.
     */
    private final MacType theMacType;

    /**
     * The Digest Type.
     */
    private final DigestType theDigestType;

    /**
     * The SymKey Type.
     */
    private final SymKeyType theKeyType;

    /**
     * Obtain Mac Type.
     * @return the MacType
     */
    public MacType getMacType() {
        return theMacType;
    }

    /**
     * Obtain Digest Type.
     * @return the DigestType
     */
    public DigestType getDigestType() {
        return theDigestType;
    }

    /**
     * Obtain SymKey Type.
     * @return the KeyType
     */
    public SymKeyType getKeyType() {
        return theKeyType;
    }

    /**
     * Constructor from Mac.
     * @param pMac the source mac
     */
    protected MacSpec(final DataMac pMac) {
        /* Store parameters */
        theMacType = pMac.getMacType();
        theDigestType = pMac.getDigestType();
        theKeyType = pMac.getSymKeyType();
    }

    /**
     * Constructor from Code.
     * @param pCode the encoded specification
     * @throws JDataException on error
     */
    protected MacSpec(final int pCode) throws JDataException {
        /* Determine MacType */
        int myId = pCode
                   & DataConverter.NYBBLE_MASK;
        theMacType = MacType.fromId(myId);

        /* Switch on the MacType */
        switch (theMacType) {
            case HMAC:
                theDigestType = DigestType.fromId(myId >> DataConverter.NYBBLE_SHIFT);
                theKeyType = null;
                break;
            case GMAC:
            case POLY1305:
                theDigestType = null;
                theKeyType = SymKeyType.fromId(myId >> DataConverter.NYBBLE_SHIFT);
                break;
            default:
                theDigestType = null;
                theKeyType = null;
                break;
        }
    }

    /**
     * Encode the MacSpec.
     * @return the encoded specification
     */
    public int getEncoded() {
        /* Determine base code */
        int myCode = theMacType.getId();

        /* Switch on Mac Type */
        switch (theMacType) {
            case HMAC:
                myCode += theDigestType.getId() << DataConverter.NYBBLE_SHIFT;
                break;
            case GMAC:
            case POLY1305:
                myCode += theKeyType.getId() << DataConverter.NYBBLE_SHIFT;
                break;
            default:
                break;
        }

        /* Return the code */
        return myCode;
    }
}
