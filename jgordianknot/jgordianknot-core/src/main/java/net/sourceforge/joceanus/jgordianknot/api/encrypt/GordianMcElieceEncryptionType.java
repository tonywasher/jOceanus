/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.api.encrypt;

import net.sourceforge.joceanus.jgordianknot.api.asym.GordianMcElieceKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianMcElieceKeySpec.GordianMcElieceKeyType;

/**
 * McEliece encryptionType.
 */
public enum GordianMcElieceEncryptionType {
    /**
     * Standard.
     */
    STANDARD("Standard"),

    /**
     * KobaraImai.
     */
    KOBARAIMAI("KobaraImai"),

    /**
     * Fujisaki.
     */
    FUJISAKI("Fujisaki"),

    /**
     * Pointcheval.
     */
    POINTCHEVAL("Pointcheval");

    /**
     * The name.
     */
    private final String theName;

    /**
     * Constructor.
     * @param pName the name of the encryption
     */
    GordianMcElieceEncryptionType(final String pName) {
        theName = pName;
    }

    /**
     * Check valid encryption type.
     * @param pKeySpec the McElieceKeySpec
     * @param pEncryptorType the encryptorType
     * @return true/false
     */
    public static boolean checkValidEncryptionType(final GordianMcElieceKeySpec pKeySpec,
                                                   final GordianMcElieceEncryptionType pEncryptorType) {
        switch (pEncryptorType) {
            case STANDARD:
                return GordianMcElieceKeyType.STANDARD.equals(pKeySpec.getKeyType());
            case FUJISAKI:
            case KOBARAIMAI:
            case POINTCHEVAL:
                return !GordianMcElieceKeyType.STANDARD.equals(pKeySpec.getKeyType());
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public String toString() {
        return theName;
    }
}
