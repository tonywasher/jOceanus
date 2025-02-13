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
package net.sourceforge.joceanus.gordianknot.impl.jca;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.gordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.key.GordianCoreKey;
import org.bouncycastle.util.Arrays;

import javax.crypto.SecretKey;
import java.util.Objects;

/**
 * Wrapper for JCA key.
 * @param <T> the Key type
 */
public final class JcaKey<T extends GordianKeySpec>
        extends GordianCoreKey<T> {
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
    public byte[] getKeyBytes() {
        return theKey.getEncoded();
    }

    /**
     * Access Key correctly.
     * @param <X> the key type
     * @param pKey the key to convert
     * @return the converted key
     * @throws GordianException on error
     */
    protected static <X extends GordianKeySpec> JcaKey<X> accessKey(final GordianKey<X> pKey) throws GordianException {
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
        final JcaKey<?> myThat = (JcaKey<?>) pThat;

        /* Check differences */
        if (!getKeyType().equals(myThat.getKeyType())) {
            return false;
        }
        return Arrays.areEqual(getKey().getEncoded(), myThat.getKey().getEncoded());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKeyType(),  Arrays.hashCode(getKey().getEncoded()));
    }
}
