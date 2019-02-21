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
package net.sourceforge.joceanus.jgordianknot.api.asym;

import org.bouncycastle.pqc.crypto.qtesla.QTESLASecurityCategory;

/**
 * qTESLA keyType.
 */
public enum GordianQTESLAKeyType {
    /**
     * Heuristic Category I.
     */
    HEURISTIC_I(QTESLASecurityCategory.HEURISTIC_I),

    /**
     * Provably Secure Category I.
     */
    PROVABLY_SECURE_I(QTESLASecurityCategory.PROVABLY_SECURE_I),

    /**
     * Heuristic Category III (Size).
     */
    HEURISTIC_III_SIZE(QTESLASecurityCategory.HEURISTIC_III_SIZE),

    /**
     * Heuristic Category III (Speed).
     */
    HEURISTIC_III_SPEED(QTESLASecurityCategory.HEURISTIC_III_SPEED),

    /**
     * Provably Secure Category III.
     */
    PROVABLY_SECURE_III(QTESLASecurityCategory.PROVABLY_SECURE_III);

    /**
     * Security Category.
     */
    private final int theCategory;

    /**
     * Constructor.
     * @param pCategory the category
     */
    GordianQTESLAKeyType(final int pCategory) {
        theCategory = pCategory;
    }

    /**
     * Obtain the Category.
     * @return the category
     */
    public int getCategory() {
        return theCategory;
    }
}
