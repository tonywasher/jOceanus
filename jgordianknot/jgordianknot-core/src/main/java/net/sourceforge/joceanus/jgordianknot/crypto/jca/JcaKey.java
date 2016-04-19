/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.crypto.jca;

import javax.crypto.SecretKey;

import org.bouncycastle.util.Arrays;

import net.sourceforge.joceanus.jgordianknot.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKey;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Wrapper for JCA key.
 * @param <T> the Key type
 */
public final class JcaKey<T>
        extends GordianKey<T> {
    /**
     * The secret key.
     */
    private final SecretKey theKey;

    /**
     * Constructor.
     * @param pKeyType the keyType
     * @param pKey the key
     */
    protected JcaKey(final T pKeyType,
                     final SecretKey pKey) {
        /* Initialise underlying class */
        super(pKeyType);

        /* Store parameters */
        theKey = pKey;
    }

    /**
     * Obtain the key.
     * @return the key
     */
    protected SecretKey getKey() {
        return theKey;
    }

    @Override
    public <X> JcaKey<X> convertToKeyType(final X pKeyType) {
        return new JcaKey<>(pKeyType, theKey);
    }

    /**
     * Access Key correctly.
     * @param <X> the key type
     * @param pKey the key to convert
     * @return the converted key
     * @throws OceanusException on error
     */
    protected static <X> JcaKey<X> accessKey(final GordianKey<X> pKey) throws OceanusException {
        /* Check that it is a JcaKey */
        if (pKey instanceof JcaKey) {
            return (JcaKey<X>) pKey;
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
        if (!(pThat instanceof JcaKey)) {
            return false;
        }

        /* Access the target field */
        JcaKey<?> myThat = (JcaKey<?>) pThat;

        /* Check differences */
        if (!getKeyType().equals(myThat.getKeyType())) {
            return false;
        }
        return Arrays.areEqual(getKey().getEncoded(), myThat.getKey().getEncoded());
    }

    @Override
    public int hashCode() {
        return GordianFactory.HASH_PRIME * getKeyType().hashCode()
               + Arrays.hashCode(getKey().getEncoded());
    }
}