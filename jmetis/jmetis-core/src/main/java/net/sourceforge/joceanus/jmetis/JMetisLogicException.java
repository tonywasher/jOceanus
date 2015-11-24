/*******************************************************************************
 * jMetis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis;

import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * JMetis Logic Exception.
 */
public class JMetisLogicException
        extends OceanusException {
    /**
     * SerialId.
     */
    private static final long serialVersionUID = 864554925186516604L;

    /**
     * Create a new Metis Exception object based on a string.
     * @param s the description of the exception
     */
    public JMetisLogicException(final String s) {
        super(s);
    }
}
