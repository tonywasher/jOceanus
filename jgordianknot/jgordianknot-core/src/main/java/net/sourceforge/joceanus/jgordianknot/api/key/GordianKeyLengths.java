/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.api.key;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;

/**
 * Set of supported keyLengths.
 */
public final class GordianKeyLengths {
    /**
     * The Supported keyLengths.
     */
    private static final Set<GordianLength> KEYLENGTHS = EnumSet.of(GordianLength.LEN_128,
            GordianLength.LEN_192,
            GordianLength.LEN_256,
            GordianLength.LEN_512,
            GordianLength.LEN_1024);

    /**
     * Private constructor.
     */
    private GordianKeyLengths() {
    }

    /**
     * Is this length a supported keyLength?
     * @param pKeyLength the length
     * @return true/false
     */
    public static boolean isSupportedLength(final GordianLength pKeyLength) {
        return KEYLENGTHS.contains(pKeyLength);
    }

    /**
     * Obtain iterator for supported lengths.
     * @return the iterator
     */
    public static Iterator<GordianLength> iterator() {
        return KEYLENGTHS.iterator();
    }

    /**
     * Obtain id for keyLength.
     * @param pKeyLength the key length
     * @return the id.
     */
    public static int getIdForKeyLength(final GordianLength pKeyLength) {
        int myId = 1;
        for (GordianLength myLength : KEYLENGTHS) {
            if (pKeyLength == myLength) {
                return myId;
            }
            myId++;
        }
        return -1;
    }

    /**
     * Obtain keyLength for id.
     * @param pId the id
     * @return the keyLength
     */
    public static GordianLength getKeyLengthForId(final int pId) {
        int myId = 1;
        for (GordianLength myLength : KEYLENGTHS) {
            if (myId == pId) {
                return myLength;
            }
            myId++;
        }
        return null;
    }
}
