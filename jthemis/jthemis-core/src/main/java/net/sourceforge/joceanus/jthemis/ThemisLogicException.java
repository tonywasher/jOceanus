/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2018 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jthemis;

import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Themis Logic Exception.
 */
public class ThemisLogicException
        extends OceanusException {
    /**
     * SerialId.
     */
    private static final long serialVersionUID = -8168556703703668204L;

    /**
     * Create a new Themis Exception object based on an object and a string.
     * @param o the object
     * @param s the description of the exception
     */
    public ThemisLogicException(final Object o,
                                final String s) {
        super(o, s);
    }

    /**
     * Create a new Themis Exception object based on a string.
     * @param s the description of the exception
     */
    public ThemisLogicException(final String s) {
        super(s);
    }
}
