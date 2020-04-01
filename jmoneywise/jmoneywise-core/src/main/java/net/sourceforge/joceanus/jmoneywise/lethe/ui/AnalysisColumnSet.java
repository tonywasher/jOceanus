/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2020 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.ui;

/**
 * Analysis Column Sets.
 */
public enum AnalysisColumnSet {
    /**
     * Balance.
     */
    BALANCE,

    /**
     * Standard.
     */
    STANDARD,

    /**
     * Salary.
     */
    SALARY,

    /**
     * Interest.
     */
    INTEREST,

    /**
     * Dividend.
     */
    DIVIDEND,

    /**
     * Security.
     */
    SECURITY,

    /**
     * All.
     */
    ALL;

    /**
     * Report Name.
     */
    private String theName;

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = MoneyWiseUIResource.getKeyForColumnSet(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * Is this the balance set?
     * @return true/false
     */
    public boolean isBalance() {
        return BALANCE.equals(this);
    }
}
