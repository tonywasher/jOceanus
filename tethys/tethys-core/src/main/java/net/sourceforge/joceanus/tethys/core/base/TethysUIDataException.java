/*
 * Tethys: GUI Utilities
 * Copyright 2012-2026. Tony Washer
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
 */
package net.sourceforge.joceanus.tethys.core.base;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;

import java.io.Serial;

/**
 * Data Exception.
 */
public class TethysUIDataException
        extends OceanusException {
    /**
     * SerialId.
     */
    @Serial
    private static final long serialVersionUID = 4031981620731173721L;

    /**
     * Create a new Exception object based on a string.
     *
     * @param s the description of the exception
     */
    public TethysUIDataException(final String s) {
        super(s);
    }

    /**
     * Create a new Exception object based on a string and an underlying exception.
     *
     * @param s the description of the exception
     * @param c the underlying exception
     */
    public TethysUIDataException(final String s,
                                 final Throwable c) {
        super(s, c);
    }

    /**
     * Create a new Exception object based on a string and an object.
     *
     * @param o the associated object
     * @param s the description of the exception
     */
    public TethysUIDataException(final Object o,
                                 final String s) {
        super(o, s);
    }
}
