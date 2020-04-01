/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2020 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.core.base;

import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Cryptography Exception.
 */
public class GordianCryptoException
        extends OceanusException {
    /**
     * SerialId.
     */
    private static final long serialVersionUID = 3549487220672166829L;

    /**
     * Create a new GordianKnot Exception object based on a string and an underlying exception.
     * @param s the description of the exception
     * @param e the underlying exception
     */
    public GordianCryptoException(final String s,
                                  final Throwable e) {
        super(s, e);
    }
}
