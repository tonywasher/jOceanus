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
package net.sourceforge.joceanus.jgordianknot.api.cipher;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianIdSpec;

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
     * Is this an AAD Spec.
     */
    private final boolean isAAD;

    /**
     * The String name.
     */
    private String theName;

    /**
     * Constructor.
     * @param pKeySpec the keySpec
     */
    private GordianStreamCipherSpec(final GordianStreamKeySpec pKeySpec) {
        this(pKeySpec, false);
    }

    /**
     * Constructor.
     * @param pKeySpec the keySpec
     * @param pAAD is this an AAD cipher?
     */
    private GordianStreamCipherSpec(final GordianStreamKeySpec pKeySpec,
                                    final boolean pAAD) {
        super(pKeySpec);
        isAAD = pAAD;
        isValid = checkValidity();
    }

    /**
     * Create a streamCipherSpec.
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    public static GordianStreamCipherSpec stream(final GordianStreamKeySpec pKeySpec) {
        return new GordianStreamCipherSpec(pKeySpec);
    }

    /**
     * Create a streamCipherSpec.
     * @param pKeySpec the keySpec
     * @param pAAD is this an AAD cipher?
     * @return the cipherSpec
     */
    public static GordianStreamCipherSpec stream(final GordianStreamKeySpec pKeySpec,
                                                 final boolean pAAD) {
        return new GordianStreamCipherSpec(pKeySpec, pAAD);
    }

    @Override
    public boolean needsIV() {
        return getKeyType().needsIV();
    }

    @Override
    public int getIVLength() {
        return getKeyType().getIVLength(isAAD);
    }

    /**
     * Is the keySpec an AAD cipher?
     * @return true/false.
     */
    public boolean isAAD() {
        return isAAD;
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

        /* KeySpec must support AAD if requested */
        return !isAAD || mySpec.supportsAAD();
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* If the keySpec is valid */
            if (isValid) {
                /* Load the name */
                theName = super.toString();
                if (isAAD) {
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
                && isAAD == myThat.isAAD();
    }

    @Override
    public int hashCode() {
       return getKeyType().hashCode() + (isAAD() ? 1 : 0);
    }
}
