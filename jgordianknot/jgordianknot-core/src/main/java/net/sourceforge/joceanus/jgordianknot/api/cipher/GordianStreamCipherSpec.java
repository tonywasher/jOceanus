/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2018 Tony Washer
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

/**
 * The StreamCipherSpec class.
 */
public class GordianStreamCipherSpec
        extends GordianCipherSpec<GordianStreamKeyType> {
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
     * @param pKeyType the keyType
     */
    protected GordianStreamCipherSpec(final GordianStreamKeyType pKeyType) {
        super(pKeyType);
        isValid = checkValidity();
    }

    /**
     * Create a streamKey cipherSpec.
     * @param pKeyType the keyType
     * @return the cipherSpec
     */
    public static GordianStreamCipherSpec stream(final GordianStreamKeyType pKeyType) {
        return new GordianStreamCipherSpec(pKeyType);
    }

    @Override
    public boolean needsIV() {
        return getKeyType().getIVLength(false) > 0;
    }

    @Override
    public int getIVLength(final boolean pRestricted) {
        return getKeyType().getIVLength(pRestricted);
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
        return getKeyType() != null;
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
}
