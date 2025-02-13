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
package net.sourceforge.joceanus.gordianknot.impl.bc;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.gordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.key.GordianCoreKey;
import org.bouncycastle.util.Arrays;

import java.util.Objects;

/**
 * BC GordianKnot Key implementation.
 * @param <T> the Key type
 */
public final class BouncyKey<T extends GordianKeySpec>
        extends GordianCoreKey<T> {
    /**
     * The key bytes.
     */
    private final byte[] theKey;

    /**
     * Constructor.
     * @param pKeyType the keyType
     * @param pKeyBytes the key bytes
     */
    BouncyKey(final T pKeyType,
              final byte[] pKeyBytes) {
        /* Initialise underlying class */
        super(pKeyType);

        /* Store parameters */
        theKey = Arrays.clone(pKeyBytes);
    }

    /**
     * Obtain the key.
     * @return the key
     */
    protected byte[] getKey() {
        return theKey;
    }

    @Override
    public byte[] getKeyBytes() {
        return getKey();
    }

    /**
     * Access Key correctly.
     * @param <X> the key type
     * @param pKey the key to convert
     * @return the converted key
     * @throws GordianException on error
     */
    protected static <X extends GordianKeySpec> BouncyKey<X> accessKey(final GordianKey<X> pKey) throws GordianException {
        /* Check that it is a BouncyKey */
        if (pKey instanceof BouncyKey) {
            return (BouncyKey<X>) pKey;
        }

        /* Reject key */
        throw new GordianDataException("Invalid Key");
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (pThat == this) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Make sure that the object is the same class */
        if (!(pThat instanceof BouncyKey)) {
            return false;
        }

        /* Access the target field */
        final BouncyKey<?> myThat = (BouncyKey<?>) pThat;

        /* Check differences */
        if (!getKeyType().equals(myThat.getKeyType())) {
            return false;
        }
        return Arrays.areEqual(getKey(), myThat.getKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKeyType(), Arrays.hashCode(getKey()));
    }
}
