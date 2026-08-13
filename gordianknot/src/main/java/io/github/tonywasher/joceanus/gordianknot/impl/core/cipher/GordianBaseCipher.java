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

package io.github.tonywasher.joceanus.gordianknot.impl.core.cipher;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianCipher;

import java.util.Arrays;

/**
 * Extended cipher interface.
 */
public interface GordianBaseCipher
        extends GordianCipher {
    @Override
    default byte[] update(final byte[] pBytes,
                          final int pOffset,
                          final int pLength) throws GordianException {
        /* Check that we are initialised */
        checkInit();

        /* Create output buffer */
        final int myLen = getOutputLength(pLength);
        final byte[] myOutput = new byte[myLen];

        /* Process the data */
        final int myOut = update(pBytes, pOffset, pLength, myOutput, 0);

        /* Return full buffer if possible */
        if (myOut == myLen) {
            return myOutput;
        }

        /* Cut down buffer */
        final byte[] myReturn = Arrays.copyOf(myOutput, myOut);
        Arrays.fill(myOutput, (byte) 0);
        return myReturn;
    }

    @Override
    default byte[] finish() throws GordianException {
        /* Check that we are initialised */
        checkInit();

        /* Create output buffer */
        final int myLen = getOutputLength(0);
        final byte[] myOutput = new byte[myLen];

        /* Process the data */
        final int myOut = finish(myOutput, 0);

        /* Return full buffer if possible */
        if (myOut == myLen) {
            return myOutput;
        }

        /* Cut down buffer */
        final byte[] myReturn = Arrays.copyOf(myOutput, myOut);
        Arrays.fill(myOutput, (byte) 0);
        return myReturn;
    }

    @Override
    default byte[] finish(final byte[] pBytes,
                          final int pOffset,
                          final int pLength) throws GordianException {
        /* Check that we are initialised */
        checkInit();

        /* Create output buffer */
        final int myLen = getOutputLength(pLength);
        final byte[] myOutput = new byte[myLen];

        /* Process the data */
        final int myOut = finish(pBytes, pOffset, pLength, myOutput, 0);

        /* Return full buffer if possible */
        if (myOut == myLen) {
            return myOutput;
        }

        /* Cut down buffer */
        final byte[] myReturn = Arrays.copyOf(myOutput, myOut);
        Arrays.fill(myOutput, (byte) 0);
        return myReturn;
    }

    /**
     * Check that we are initialised.
     *
     * @throws GordianException on error
     */
    default void checkInit() throws GordianException {
        /* NoOp */
    }
}
