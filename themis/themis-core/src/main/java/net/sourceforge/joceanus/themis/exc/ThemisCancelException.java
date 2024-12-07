/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.themis.exc;

import net.sourceforge.joceanus.oceanus.base.OceanusException;

/**
 * Themis Cancel Exception.
 */
public class ThemisCancelException
        extends OceanusException {
    /**
     * SerialId.
     */
    private static final long serialVersionUID = 8610621822129819107L;

    /**
     * Create a new Themis Exception object based on a string.
     * @param s the description of the exception
     */
    public ThemisCancelException(final String s) {
        super(s);
    }
}
