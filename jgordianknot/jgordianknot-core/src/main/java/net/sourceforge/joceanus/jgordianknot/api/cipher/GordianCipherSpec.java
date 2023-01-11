/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.api.cipher;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianIdSpec;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;

/**
 * Cipher Specification.
 * @param <T> the keyType
 */
public abstract class GordianCipherSpec<T extends GordianKeySpec>
        implements GordianIdSpec {
    /**
     * KeyType.
     */
    private final T theKeyType;

    /**
     * Constructor.
     * @param pKeyType the keyType
     */
    GordianCipherSpec(final T pKeyType) {
        theKeyType = pKeyType;
    }

    /**
     * Obtain the keyType.
     * @return the keyType
     */
    public T getKeyType() {
        return theKeyType;
    }

    @Override
    public String toString() {
        return theKeyType.toString();
    }

    /**
     * Obtain the cipherMode.
     * @return the mode
     */
    public abstract boolean needsIV();

    /**
     * Obtain the IV length for the cipher.
     * @return the IV length
     */
    public abstract int getIVLength();

    /**
     * Is the keySpec valid?
     * @return true/false.
     */
    public abstract boolean isValid();

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Make sure that the object is a CipherSpec */
        if (!(pThat instanceof GordianCipherSpec)) {
            return false;
        }

        /* Access the target cipherSpec */
        final GordianCipherSpec<?> myThat = (GordianCipherSpec<?>) pThat;

        /* Check KeyType */
        return theKeyType == myThat.getKeyType();
    }

    @Override
    public int hashCode() {
        return theKeyType.hashCode();
    }
}
