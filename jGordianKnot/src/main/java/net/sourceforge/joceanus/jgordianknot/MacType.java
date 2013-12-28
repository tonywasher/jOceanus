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

import java.util.ResourceBundle;

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataException.ExceptionClass;

/**
 * DataDigest types. Available algorithms.
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
    SKEIN(4);

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(MacType.class.getName());

    /**
     * The external Id of the algorithm.
     */
    private int theId = 0;

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
     * Constructor.
     * @param id the id
     */
    private MacType(final int id) {
        theId = id;
    }

    /**
     * get value from id.
     * @param id the id value
     * @return the corresponding enumeration object
     * @throws JDataException on error
     */
    public static MacType fromId(final int id) throws JDataException {
        for (MacType myType : values()) {
            if (myType.getId() == id) {
                return myType;
            }
        }
        throw new JDataException(ExceptionClass.DATA, "Invalid MacType: "
                                                      + id);
    }
}
