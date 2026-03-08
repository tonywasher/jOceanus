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

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianCipherMode;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianSymCipherSpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianSymKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianPadding;

import java.util.Objects;

/**
 * The SymCipherSpec class.
 */

public class GordianCoreSymCipherSpec
        implements GordianSymCipherSpec {
    /**
     * The AAD IV length.
     */
    public static final int AADIVLEN = 12;

    /**
     * The Separator.
     */
    private static final String SEP = "-";

    /**
     * The keySpec.
     */
    private final GordianCoreSymKeySpec theKeySpec;

    /**
     * The mode.
     */
    private final GordianCoreCipherMode theMode;

    /**
     * The padding.
     */
    private final GordianPadding thePadding;

    /**
     * is the spec valid?.
     */
    private final boolean isValid;

    /**
     * the name.
     */
    private String theName;

    /**
     * Constructor.
     *
     * @param pKeySpec the keySpec
     * @param pMode    the mode
     * @param pPadding the padding
     */
    GordianCoreSymCipherSpec(final GordianCoreSymKeySpec pKeySpec,
                             final GordianCipherMode pMode,
                             final GordianPadding pPadding) {
        theKeySpec = pKeySpec;
        theMode = GordianCoreCipherMode.mapCoreMode(pMode);
        thePadding = pPadding;
        isValid = checkValidity();
    }

    /**
     * Obtain the Core cipherMode.
     *
     * @return the keyType
     */
    public GordianCoreSymKeySpec getCoreKeySpec() {
        return theKeySpec;
    }

    @Override
    public GordianSymKeySpec getKeySpec() {
        return theKeySpec;
    }

    /**
     * Obtain the Core cipherMode.
     *
     * @return the keyType
     */
    public GordianCoreCipherMode getCoreCipherMode() {
        return theMode;
    }

    @Override
    public GordianCipherMode getCipherMode() {
        return theMode.getMode();
    }

    @Override
    public GordianPadding getPadding() {
        return thePadding;
    }

    @Override
    public boolean isValid() {
        return isValid;
    }

    @Override
    public boolean needsIV() {
        return theMode.needsIV();
    }

    /**
     * Obtain the blockLength.
     *
     * @return the blockLength
     */
    public GordianLength getBlockLength() {
        return theKeySpec.getBlockLength();
    }

    @Override
    public int getIVLength() {
        if (theMode.isAAD()) {
            return AADIVLEN;
        }
        final int myBlockLen = theKeySpec.getBlockLength().getByteLength();
        return GordianCipherMode.G3413CTR.equals(theMode.getMode())
                ? myBlockLen >> 1
                : myBlockLen;
    }

    /**
     * Is this an AAD mode?
     *
     * @return true/false
     */
    public boolean isAAD() {
        return theMode != null && theMode.isAAD();
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* If the keySpec is valid */
            if (isValid) {
                /* Load the name */
                theName = theKeySpec.toString();
                theName += SEP + theMode;
                if (!GordianPadding.NONE.equals(thePadding)) {
                    theName += SEP + thePadding;
                }
            } else {
                /* Report invalid spec */
                theName = "InvalidSymCipherSpec: " + theKeySpec + ":" + theMode + ":" + thePadding;
            }
        }

        /* return the name */
        return theName;
    }

    /**
     * Check spec validity.
     *
     * @return valid true/false
     */
    private boolean checkValidity() {
        return theKeySpec != null && theKeySpec.isValid()
                && theMode != null && thePadding != null;
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

        /* Check subFields */
        return pThat instanceof GordianCoreSymCipherSpec myThat
                && Objects.equals(theKeySpec, myThat.getKeySpec())
                && Objects.equals(theMode, myThat.getCoreCipherMode())
                && thePadding == myThat.getPadding();
    }

    @Override
    public int hashCode() {
        return Objects.hash(theKeySpec, theMode, thePadding);
    }
}
