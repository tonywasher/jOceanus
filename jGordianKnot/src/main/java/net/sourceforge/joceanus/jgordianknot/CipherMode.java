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
package net.sourceforge.joceanus.jgordianknot;

import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Cipher Modes. Available algorithms.
 */
public enum CipherMode {
    /**
     * CBC Mode.
     */
    CBC(1),

    /**
     * OFB Mode.
     */
    OFB(2),

    /**
     * CFB Mode.
     */
    CFB(3),

    /**
     * SIC(CTR) Mode.
     */
    SIC(4),

    /**
     * GCM Mode.
     */
    GCM(5),

    /**
     * EAX Mode.
     */
    EAX(6);

    /**
     * The external Id of the cipherMode.
     */
    private final int theId;

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
    private CipherMode(final int id) {
        theId = id;
    }

    /**
     * Obtain cipher mode.
     * @return the cipher mode
     */
    public String getCipherMode() {
        switch (this) {
            case CFB:
            case OFB:
                return name()
                       + "8";
            default:
                return name();
        }
    }

    /**
     * Does this cipher mode need a StdBlock.
     * @return true/false
     */
    public boolean needsStdBlock() {
        switch (this) {
            case GCM:
            case EAX:
                return true;
            default:
                return false;
        }
    }

    /**
     * get value from id.
     * @param id the id value
     * @return the corresponding enumeration object
     * @throws JOceanusException on error
     */
    public static CipherMode fromId(final int id) throws JOceanusException {
        for (CipherMode myType : values()) {
            if (myType.getId() == id) {
                return myType;
            }
        }
        throw new JOceanusException("Invalid CipherMode: "
                                    + id);
    }
}
