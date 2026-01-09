/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.core.lock;

import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;

/**
 * Lock data.
 */
public final class GordianLockData {
    /**
     * Number of digests.
     */
    static final int NUM_DIGESTS = 3;

    /**
     * Recipe length (Integer).
     */
    static final int RECIPELEN = Integer.BYTES;

    /**
     * Salt length.
     */
    static final int SALTLEN = GordianLength.LEN_256.getByteLength();

    /**
     * Hash Length.
     */
    static final int HASHLEN = GordianLength.LEN_512.getByteLength();

    /**
     * HashSize.
     */
    static final int HASHSIZE = RECIPELEN + SALTLEN + HASHLEN;

    /**
     * Private constructor.
     */
    private GordianLockData() {
    }
}
