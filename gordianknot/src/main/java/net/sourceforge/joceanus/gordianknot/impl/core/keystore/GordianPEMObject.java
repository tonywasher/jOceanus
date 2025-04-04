/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.core.keystore;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;

/**
 * PEM Object.
 */
public class GordianPEMObject {
    /**
     * The bracket sequence.
     */
    static final String BRACKET = "-----";

    /**
     * Object type.
     */
    private final GordianPEMObjectType theType;

    /**
     * Encoded object.
     */
    private final byte[] theEncoded;

    /**
     * Constructor.
     * @param pObjectType the objectType
     * @param pEncoded the encoded bytes
     */
    GordianPEMObject(final GordianPEMObjectType pObjectType,
                     final byte[] pEncoded) {
        theType = pObjectType;
        theEncoded = pEncoded;
    }

    /**
     * Object the objectType.
     * @return the objectType
     */
    GordianPEMObjectType getObjectType() {
        return theType;
    }

    /**
     * Object the encoded.
     * @return the encoded
     */
    byte[] getEncoded() {
        return theEncoded;
    }

    /**
     * The PEM Object types.
     */
    enum GordianPEMObjectType {
        /**
         * Certificate.
         */
        CERT("CERTIFICATE"),

        /**
         * privateKey.
         */
        PRIVATEKEY("ENCRYPTED PRIVATE KEY"),

        /**
         * keySet.
         */
        KEYSET("ENCRYPTED KEYSET"),

        /**
         * key.
         */
        KEY("ENCRYPTED KEY"),

        /**
         * certificate request.
         */
        CERTREQ("CERTIFICATE REQUEST"),

        /**
         * certificate response.
         */
        CERTRESP("CERTIFICATE RESPONSE"),

        /**
         * certificate acknowledgement.
         */
        CERTACK("CERTIFICATE ACK");

        /**
         * The header.
         */
        private final String theId;

        /**
         * Constructor.
         * @param pId the Id
         */
        GordianPEMObjectType(final String pId) {
            theId = pId;
        }

        /**
         * Obtain the id.
         * @return the id
         */
        String getId() {
            return theId;
        }

        /**
         * Determine the ObjectType.
         * @param pId the id
         * @return the objectType
         * @throws GordianException on error
         */
        static GordianPEMObjectType getObjectType(final String pId) throws GordianException {
            for (GordianPEMObjectType myType : values()) {
                final String myTest = myType.getId() + BRACKET;
                if (myTest.equals(pId)) {
                    return myType;
                }
            }
            throw new GordianDataException("Unsupported id: " + pId);
        }
    }
}
