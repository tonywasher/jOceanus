/*
 * GordianKnot: Security Suite
 * Copyright 2026. Tony Washer
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
 */

package io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher;

import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamCipherSpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySpec;

import java.util.Objects;

/**
 * The StreamCipherSpec class.
 */
public class GordianCoreStreamCipherSpec
        implements GordianNewStreamCipherSpec {
    /**
     * The keySpec.
     */
    private final GordianCoreStreamKeySpec theKeySpec;

    /**
     * Is this an AEAD variant.
     */
    private final boolean asAEAD;

    /**
     * The Validity.
     */
    private final boolean isValid;

    /**
     * The String name.
     */
    private String theName;

    /**
     * Constructor.
     *
     * @param pKeySpec the keySpec
     * @param pAEAD    is this an AAD variant?
     */
    public GordianCoreStreamCipherSpec(final GordianCoreStreamKeySpec pKeySpec,
                                       final boolean pAEAD) {
        theKeySpec = pKeySpec;
        asAEAD = pAEAD;
        isValid = checkValidity();
    }

    /**
     * Obtain the Core cipherMode.
     *
     * @return the keyType
     */
    public GordianCoreStreamKeySpec getCoreKeySpec() {
        return theKeySpec;
    }

    @Override
    public GordianNewStreamKeySpec getKeySpec() {
        return theKeySpec;
    }

    @Override
    public boolean needsIV() {
        return theKeySpec.needsIV();
    }

    @Override
    public int getIVLength() {
        return theKeySpec.getIVLength();
    }

    @Override
    public boolean asAEAD() {
        return asAEAD;
    }

    @Override
    public boolean isAEAD() {
        return asAEAD || theKeySpec.isAEAD();
    }

    @Override
    public boolean isValid() {
        return isValid;
    }

    /**
     * Check spec validity.
     *
     * @return valid true/false
     */
    private boolean checkValidity() {
        /* KeyType must be non-null and valid */
        if (theKeySpec == null
                || !theKeySpec.isValid()) {
            return false;
        }

        /* KeySpec must support AEAD if requested */
        return !asAEAD || theKeySpec.supportsAEAD();
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* If the keySpec is valid */
            if (isValid) {
                /* Load the name */
                theName = super.toString();
                if (asAEAD) {
                    theName += "Poly1305";
                }
            } else {
                /* Report invalid spec */
                theName = "InvalidStreamCipherSpec: " + theKeySpec;
            }
        }

        /* return the name */
        return theName;
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Check KeyType */
        return pThat instanceof GordianCoreStreamCipherSpec myThat
                && theKeySpec.equals(myThat.getKeySpec())
                && asAEAD == myThat.asAEAD();
    }

    @Override
    public int hashCode() {
        return Objects.hash(theKeySpec, asAEAD);
    }
}
