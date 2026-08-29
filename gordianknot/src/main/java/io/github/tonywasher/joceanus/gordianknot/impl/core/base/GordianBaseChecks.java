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

import io.github.tonywasher.joceanus.gordianknot.api.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.api.exc.GordianException;

/**
 * Base Checks.
 */
public final class GordianBaseChecks {
    /**
     * Private constructor.
     */
    private GordianBaseChecks() {
    }

    /**
     * Check that the input buffer is valid.
     *
     * @param pBuffer the buffer
     * @param pOffset the offset
     * @param pLength the length
     * @return non-Zero data true/false
     * @throws GordianException on error
     */
    public static boolean checkInputBuffer(final byte[] pBuffer,
                                           final int pOffset,
                                           final int pLength) throws GordianException {
        /* Null buffer is allowed if length is zero */
        if (pBuffer == null) {
            if (pLength != 0) {
                throw new GordianDataException("Non-zero length for null input buffer");
            }
            return false;

            /* Non-null buffer */
        } else {
            /* Check for negative length/offset */
            if (pLength < 0) {
                throw new GordianDataException("Negative length for input buffer");
            }
            if (pOffset < 0) {
                throw new GordianDataException("Negative offset for input buffer");
            }

            /* Check for short buffer */
            if (pLength + pOffset > pBuffer.length) {
                throw new GordianDataException("Supplied input buffer too short");
            }
        }
        return true;
    }

    /**
     * Check that the output buffer is valid.
     *
     * @param pBuffer the buffer
     * @param pOffset the offset
     * @param pLength the length
     * @throws GordianException on error
     */
    public static void checkOutputBuffer(final byte[] pBuffer,
                                         final int pOffset,
                                         final int pLength) throws GordianException {
        /* Null buffer is not allowed */
        if (pBuffer == null) {
            throw new GordianDataException("Null output buffer");

            /* Non-null buffer */
        } else {
            /* Check for negative length/offset */
            if (pLength < 0) {
                throw new GordianDataException("Negative length for output buffer");
            }
            if (pOffset < 0) {
                throw new GordianDataException("Negative offset for output buffer");
            }

            /* Check for short buffer */
            if (pLength + pOffset > pBuffer.length) {
                throw new GordianDataException("Supplied output buffer too short");
            }
        }
    }
}
