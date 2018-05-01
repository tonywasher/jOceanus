/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot;

import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Data Exception.
 */
public class GordianDataException
        extends OceanusException {
    /**
     * SerialId.
     */
    private static final long serialVersionUID = 7219582735082613667L;

    /**
     * Create a new GordianKnot Exception object based on a string.
     * @param s the description of the exception
     */
    public GordianDataException(final String s) {
        super(s);
    }
}
