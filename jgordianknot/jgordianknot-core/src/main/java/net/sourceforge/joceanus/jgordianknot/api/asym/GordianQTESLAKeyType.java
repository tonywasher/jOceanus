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
     * Heuristic Category II.
     */
    HEURISTIC_II(QTESLASecurityCategory.HEURISTIC_II),

    /**
     * Heuristic Category III.
     */
    HEURISTIC_III(QTESLASecurityCategory.HEURISTIC_III),

    /**
     * Heuristic Category P I.
     */
    HEURISTIC_P_I(QTESLASecurityCategory.HEURISTIC_P_I),

    /**
     * Heuristic Category P III.
     */
    HEURISTIC_P_III(QTESLASecurityCategory.HEURISTIC_P_III),

    /**
     * Heuristic Category V SIZE.
     */
    HEURISTIC_V(QTESLASecurityCategory.HEURISTIC_V),

    /**
     * Heuristic Category V SIZE.
     */
    HEURISTIC_V_SIZE(QTESLASecurityCategory.HEURISTIC_V_SIZE);

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

