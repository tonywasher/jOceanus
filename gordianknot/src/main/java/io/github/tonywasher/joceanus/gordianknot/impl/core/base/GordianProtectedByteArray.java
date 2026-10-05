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

package io.github.tonywasher.joceanus.gordianknot.impl.core.base;

import java.util.Arrays;

/**
 * A protected byteArray suitable for use as a hashMap key.
 */
public class GordianProtectedByteArray {
    /**
     * The value.
     */
    private final byte[] theValue;

    /**
     * Constructor.
     *
     * @param pValue the value
     */
    public GordianProtectedByteArray(final byte[] pValue) {
        theValue = pValue == null ? null : pValue.clone();
    }

    /**
     * Obtain the value.
     *
     * @return the value
     */
    public byte[] getValue() {
        return theValue == null ? null : theValue.clone();
    }

    @Override
    public boolean equals(final Object pThat) {
        return this == pThat || pThat instanceof GordianProtectedByteArray myValue
                && Arrays.equals(theValue, myValue.theValue);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(theValue);
    }
}
