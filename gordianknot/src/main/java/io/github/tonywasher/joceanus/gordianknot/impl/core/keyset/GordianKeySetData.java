/*
 * GordianKnot: Security Suite
 * Copyright 2012-2026. Tony Washer
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
package io.github.tonywasher.joceanus.gordianknot.impl.core.keyset;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianASN1Util;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public final class GordianKeySetData {
    /**
     * Private constructor.
     */
    private GordianKeySetData() {
    }

    /**
     * KeySetOID branch.
     */
    static final ASN1ObjectIdentifier KEYSETOID = GordianASN1Util.EXTOID.branch("1");

    /**
     * Initialisation Vector size.
     */
    static final GordianLength BLOCKLEN = GordianLength.LEN_128;

    /**
     * Obtain the encryption length for a length of data.
     *
     * @param pDataLength the dataLength
     * @return the encryption length
     */
    public static int getEncryptionLength(final int pDataLength) {
        final int iBlocks = 1 + (pDataLength / BLOCKLEN.getByteLength());
        return iBlocks * BLOCKLEN.getByteLength()
                + getEncryptionOverhead();
    }

    /**
     * Encryption overhead.
     *
     * @return the encryption overhead
     */
    static int getEncryptionOverhead() {
        return GordianKeySetRecipe.HDRLEN;
    }
}
