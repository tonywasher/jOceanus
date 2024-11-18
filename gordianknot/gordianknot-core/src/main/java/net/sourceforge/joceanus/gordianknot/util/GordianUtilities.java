/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.util;

import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.impl.core.keyset.GordianCoreKeySet;
import net.sourceforge.joceanus.gordianknot.impl.core.lock.GordianFactoryLockImpl;

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
        return GordianCoreKeySet.getDataWrapLength(GordianLength.LEN_256.getByteLength());
    }

    /**
     * Obtain Maximum KeyWrapLength.
     * @return the maximum keyWrap size
     */
    public static int getMaximumKeySetWrapLength() {
        final int my128 = GordianCoreKeySet.getKeySetWrapLength(GordianLength.LEN_128);
        final int my256 = GordianCoreKeySet.getKeySetWrapLength(GordianLength.LEN_256);
        return Math.max(my128, my256);
    }

    /**
     * Obtain FactoryLockLen.
     * @return the factoryLock length
     */
    public static int getFactoryLockLen() {
        return GordianFactoryLockImpl.getEncodedLength();
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
