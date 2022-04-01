/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.util;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianCoreKeySet;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianKeySetHashASN1;

/**
 * Utilities.
 */
public final class GordianUtilities {
    /**
     * The ZipFile extension.
     */
    public static final String ZIPFILE_EXT = ".zip";

    /**
     * The Encrypted ZipFile extension.
     */
    public static final String SECUREZIPFILE_EXT = ".gkzip";

    /**
     * Private constructor.
     */
    private GordianUtilities() {
    }

    /**
     * Obtain Maximum KeyWrapLength.
     * @return the maximum keyWrap size
     */
    public static int getMaximumKeyWrapLength() {
        return GordianCoreKeySet.getDataWrapLength(GordianLength.LEN_256.getByteLength(),
                GordianKeySetSpec.MAXIMUM_CIPHER_STEPS);
    }

    /**
     * Obtain Maximum KeyWrapLength.
     * @return the maximum keyWrap size
     */
    public static int getMaximumKeySetWrapLength() {
        final int my128 = GordianCoreKeySet.getKeySetWrapLength(GordianLength.LEN_128,
                GordianKeySetSpec.MAXIMUM_CIPHER_STEPS);
        final int my256 = GordianCoreKeySet.getKeySetWrapLength(GordianLength.LEN_256,
                GordianKeySetSpec.MAXIMUM_CIPHER_STEPS);
        return Math.max(my128, my256);
    }

    /**
     * Obtain HashLength.
     * @return the maximum keyWrap size
     */
    public static int getKeySetHashLen() {
        return GordianKeySetHashASN1.getEncodedLength();
    }

    /**
     * Obtain Encryption length.
     *
     * @param pDataLength the length of data to be encrypted
     * @return the length of encrypted data
     */
    public static int getKeySetEncryptionLength(final int pDataLength) {
        return GordianCoreKeySet.getEncryptionLength(pDataLength);
    }
}
