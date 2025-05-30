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
package net.sourceforge.joceanus.gordianknot.api.base;

import java.io.Serial;

/**
 * Exception extension class.
 */
public abstract class GordianException
        extends Exception {
    /**
     * Required serialisation field.
     */
    @Serial
    private static final long serialVersionUID = -8980967515725110116L;

    /**
     * The associated object.
     */
    private final transient Object theObject;

    /**
     * Create a wrapped Exception object based on an underlying exception.
     * @param c the underlying exception
     */
    protected GordianException(final Throwable c) {
        super(c);
        theObject = null;
    }

    /**
     * Create a new Exception object based on a string and class.
     * @param s the description of the exception
     */
    protected GordianException(final String s) {
        super(s);
        theObject = null;
        fillInStackTrace();
    }

    /**
     * Create a new Exception object based on a string and an underlying exception.
     * @param s the description of the exception
     * @param c the underlying exception
     */
    protected GordianException(final String s,
                               final Throwable c) {
        super(s, c);
        theObject = null;
    }

    /**
     * Create a new Exception object based on a string and an object.
     * @param o the associated object
     * @param s the description of the exception
     */
    protected GordianException(final Object o,
                               final String s) {
        super(s);
        theObject = o;
        fillInStackTrace();
    }

    /**
     * Create a new Exception object based on a string, an object and an underlying exception.
     * @param o the associated object
     * @param s the description of the exception
     * @param c the underlying exception
     */
    protected GordianException(final Object o,
                               final String s,
                               final Throwable c) {
        super(s, c);
        theObject = o;
    }

    /**
     * Get the associated object.
     * @return the associated object
     */
    public Object getObject() {
        return theObject;
    }
}
