/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.moneywise.atlas.data.analysis.values;

import net.sourceforge.joceanus.metis.data.MetisDataType;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisAttribute;

/**
 * TransactionAttribute enumeration.
 */
public enum MoneyWiseXAnalysisTransAttr
        implements MoneyWiseXAnalysisAttribute {
    /**
     * Income.
     */
    INCOME,

    /**
     * Expense.
     */
    EXPENSE,

    /**
     * Profit (NetIncome).
     */
    PROFIT;

    /**
     * The String name.
     */
    private String theName;

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = MoneyWiseXAnalysisValuesResource.getKeyForTransactionAttr(this).getValue();
        }

        /* return the name */
        return theName;
    }

    @Override
    public boolean isPreserved() {
        return true;
    }

    @Override
    public MetisDataType getDataType() {
        return MetisDataType.MONEY;
    }
}
