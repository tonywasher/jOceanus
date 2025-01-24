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
package net.sourceforge.joceanus.gordianknot.api.cipher;

import net.sourceforge.joceanus.gordianknot.api.base.GordianIdSpec;

import java.util.Objects;

/**
 * The StreamCipherSpec class.
 */
public final class GordianStreamCipherSpec
        extends GordianCipherSpec<GordianStreamKeySpec>
        implements GordianIdSpec {
    /**
     * The Validity.
     */
    private final boolean isValid;

    /**
     * Is this an AEAD Spec.
     */
    private final boolean isAEAD;

    /**
     * The String name.
     */
    private String theName;

    /**
     * Constructor.
     * @param pKeySpec the keySpec
     */
    public GordianStreamCipherSpec(final GordianStreamKeySpec pKeySpec) {
        this(pKeySpec, false);
    }

    /**
     * Constructor.
     * @param pKeySpec the keySpec
     * @param pAEAD is this an AAD cipher?
     */
    public GordianStreamCipherSpec(final GordianStreamKeySpec pKeySpec,
                                   final boolean pAEAD) {
        super(pKeySpec);
        isAEAD = pAEAD;
        isValid = checkValidity();
    }

    @Override
    public boolean needsIV() {
        return getKeyType().needsIV();
    }

    @Override
    public int getIVLength() {
        return getKeyType().getIVLength();
    }

    /**
     * Is the keySpec an AEAD cipher mode?
     * @return true/false.
     */
    public boolean isAEADMode() {
        return isAEAD;
    }

    /**
     * Is the keySpec an AEAD cipher?
     * @return true/false.
     */
    public boolean isAEAD() {
        return isAEAD || getKeyType().isAEAD();
    }

    @Override
    public boolean isValid() {
        return isValid;
    }

    /**
     * Check spec validity.
     * @return valid true/false
     */
    private boolean checkValidity() {
        /* KeyType must be non-null and valid */
        final GordianStreamKeySpec mySpec = getKeyType();
        if (mySpec == null
                || !mySpec.isValid()) {
            return false;
        }

        /* KeySpec must support AEAD if requested */
        return !isAEAD || mySpec.supportsAEAD();
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* If the keySpec is valid */
            if (isValid) {
                /* Load the name */
                theName = super.toString();
                if (isAEAD) {
                    theName += "Poly1305";
                }
            }  else {
                /* Report invalid spec */
                theName = "InvalidStreamCipherSpec: " + getKeyType();
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

        /* Make sure that the object is a StreamCipherSpec */
        if (pThat.getClass() != this.getClass()) {
            return false;
        }

        /* Access the target StreamKeySpec */
        final GordianStreamCipherSpec myThat = (GordianStreamCipherSpec) pThat;

        /* Check KeyType */
        return getKeyType().equals(myThat.getKeyType())
                && isAEAD == myThat.isAEADMode();
    }

    @Override
    public int hashCode() {
       return Objects.hash(getKeyType(), isAEADMode());
    }
}
