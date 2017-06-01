/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.crypto.bc;

import org.bouncycastle.util.Arrays;

import net.sourceforge.joceanus.jgordianknot.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKey;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * BC GordianKnot Key implementation.
 * @param <T> the Key type
 */
public final class BouncyKey<T>
        extends GordianKey<T> {
    /**
     * The key bytes.
     */
    private final byte[] theKey;

    /**
     * Constructor.
     * @param pKeyType the keyType
     * @param pKeyBytes the key bytes
     */
    protected BouncyKey(final T pKeyType,
                        final byte[] pKeyBytes) {
        /* Initialise underlying class */
        super(pKeyType);

        /* Store parameters */
        theKey = pKeyBytes;
    }

    /**
     * Obtain the key.
     * @return the key
     */
    protected byte[] getKey() {
        return theKey;
    }

    @Override
    protected byte[] getKeyBytes() {
        return theKey;
    }

    @Override
    public <X> BouncyKey<X> convertToKeyType(final X pKeyType) {
        return new BouncyKey<>(pKeyType, theKey);
    }

    /**
     * Access Key correctly.
     * @param <X> the key type
     * @param pKey the key to convert
     * @return the converted key
     * @throws OceanusException on error
     */
    protected static <X> BouncyKey<X> accessKey(final GordianKey<X> pKey) throws OceanusException {
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
        BouncyKey<?> myThat = (BouncyKey<?>) pThat;

        /* Check differences */
        if (!getKeyType().equals(myThat.getKeyType())) {
            return false;
        }
        return Arrays.areEqual(getKey(), myThat.getKey());
    }

    @Override
    public int hashCode() {
        return GordianFactory.HASH_PRIME * getKeyType().hashCode()
               + Arrays.hashCode(getKey());
    }
}
