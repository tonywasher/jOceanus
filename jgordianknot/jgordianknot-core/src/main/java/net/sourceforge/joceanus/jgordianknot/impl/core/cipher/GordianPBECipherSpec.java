/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.core.cipher;

import java.util.Objects;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianPBESpec;

/**
 * PBE Cipher Specification.
 * @param <T> the cipher type
 */
public class GordianPBECipherSpec<T extends GordianKeySpec> {
    /**
     * The PBESpec.
     */
    private final GordianPBESpec thePBESpec;

    /**
     * The CipherSpec.
     */
    private final GordianCipherSpec<T> theCipherSpec;

    /**
     * Is the Spec valid?
     */
    private boolean isValid;

    /**
     * Constructor.
     * @param pPBESpec the PBE Spec
     * @param pCipherSpec the CipherSpec
     */
    public GordianPBECipherSpec(final GordianPBESpec pPBESpec,
                                final GordianCipherSpec<T> pCipherSpec) {
        thePBESpec = pPBESpec;
        theCipherSpec = pCipherSpec;
        isValid = pPBESpec != null && pPBESpec.isValid()
                && pCipherSpec != null && pCipherSpec.isValid();
    }

    /**
     * Obtain the pbeSpec.
     * @return the pbeSpec
     */
    public GordianPBESpec getPBESpec() {
        return thePBESpec;
    }

    /**
     * Obtain the CipherSpec.
     * @return the cipherSpec
     */
    public GordianCipherSpec<T> getCipherSpec() {
        return theCipherSpec;
    }

    /**
     * is the pbeCipherSpec valid?
     * @return true/false
     */
    public boolean isValid() {
        return isValid;
    }

    @Override
    public String toString() {
        return "" + theCipherSpec + ":" + thePBESpec;
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Make sure that the classes are the same */
        if (!(pThat instanceof GordianPBECipherSpec)) {
            return false;
        }
        final GordianPBECipherSpec<?> myThat = (GordianPBECipherSpec<?>) pThat;

        /* Check that the fields are equal */
        return Objects.equals(theCipherSpec, myThat.getCipherSpec())
                && Objects.equals(thePBESpec, myThat.getPBESpec());
    }

    @Override
    public int hashCode() {
        return theCipherSpec.hashCode() + thePBESpec.hashCode();
    }
}
