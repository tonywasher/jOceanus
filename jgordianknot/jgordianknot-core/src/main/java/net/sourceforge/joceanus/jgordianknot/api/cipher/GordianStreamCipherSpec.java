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
package net.sourceforge.joceanus.jgordianknot.api.cipher;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianIdSpec;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;

/**
 * The StreamCipherSpec class.
 */
public class GordianStreamCipherSpec
        extends GordianCipherSpec<GordianStreamKeySpec>
        implements GordianIdSpec {
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
     * @param pKeySpec the keySpec
     */
    GordianStreamCipherSpec(final GordianStreamKeySpec pKeySpec) {
        super(pKeySpec);
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

    @Override
    public boolean needsIV() {
        return getKeyType().needsIV();
    }

    @Override
    public int getIVLength(final GordianLength pKeyLen) {
        return getKeyType().getIVLength();
    }

    /**
     * Is the keySpec valid?
     * @return true/false.
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * Check spec validity.
     * @return valid true/false
     */
    private boolean checkValidity() {
        return getKeyType() != null
                && getKeyType().isValid();
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* If the keySpec is valid */
            if (isValid) {
                /* Load the name */
                theName = super.toString();
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
        return getKeyType().equals(myThat.getKeyType());
    }

    @Override
    public int hashCode() {
       return getKeyType().hashCode();
    }
}
