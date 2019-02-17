/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.core.key;

import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;

/**
 * GordianKnot Key abstraction.
 * @param <T> the Key type
 */
public abstract class GordianCoreKey<T extends GordianKeySpec>
    implements GordianKey<T> {
    /**
     * The Key Type.
     */
    private final T theKeyType;

    /**
     * Constructor.
     * @param pKeyType the keyType
     */
    protected GordianCoreKey(final T pKeyType) {
        theKeyType = pKeyType;
    }

    @Override
    public T getKeyType() {
        return theKeyType;
    }

    /**
     * Obtain the underlying keyBytes.
     * @return the keyBytes
     */
    public abstract byte[] getKeyBytes();
}
