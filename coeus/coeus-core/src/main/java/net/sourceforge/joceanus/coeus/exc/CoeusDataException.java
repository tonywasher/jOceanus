/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.coeus.exc;

import net.sourceforge.joceanus.oceanus.base.OceanusException;

import java.io.Serial;

/**
 * Coeus Exception class.
 */
public class CoeusDataException
        extends OceanusException {
    /**
     * Serial Id.
     */
    @Serial
    private static final long serialVersionUID = -8749477124141720097L;

    /**
     * Create a new Exception object based on a string.
     * @param s the description of the exception
     */
    public CoeusDataException(final String s) {
        super(s);
    }

    /**
     * Create a new Exception object based on a string and an underlying exception.
     * @param s the description of the exception
     * @param c the underlying exception
     */
    public CoeusDataException(final String s,
                              final Throwable c) {
        super(s, c);
    }

    /**
     * Create a new Exception object based on a string and an object.
     * @param o the associated object
     * @param s the description of the exception
     */
    public CoeusDataException(final Object o,
                              final String s) {
        super(o, s);
    }
}
