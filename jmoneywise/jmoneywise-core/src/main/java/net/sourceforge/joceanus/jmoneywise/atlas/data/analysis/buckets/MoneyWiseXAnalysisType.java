/*******************************************************************************
 * MoneyWise: Finance Application
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
package net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets;

import net.sourceforge.joceanus.jmoneywise.data.analysis.base.MoneyWiseAnalysisAttribute;
import net.sourceforge.joceanus.jmoneywise.data.analysis.values.MoneyWiseAnalysisAccountAttr;
import net.sourceforge.joceanus.jmoneywise.data.analysis.values.MoneyWiseAnalysisPayeeAttr;
import net.sourceforge.joceanus.jmoneywise.data.analysis.values.MoneyWiseAnalysisSecurityAttr;
import net.sourceforge.joceanus.jmoneywise.data.analysis.values.MoneyWiseAnalysisTaxBasisAttr;
import net.sourceforge.joceanus.jmoneywise.data.analysis.values.MoneyWiseAnalysisTransAttr;

/**
 * Analysis Types.
 */
public enum MoneyWiseXAnalysisType {
    /**
     * Deposit.
     */
    DEPOSIT,

    /**
     * Cash.
     */
    CASH,

    /**
     * Loan.
     */
    LOAN,

    /**
     * Security.
     */
    SECURITY,

    /**
     * Portfolio.
     */
    PORTFOLIO,

    /**
     * Payee.
     */
    PAYEE,

    /**
     * EventCategory.
     */
    CATEGORY,

    /**
     * TaxBasis.
     */
    TAXBASIS,

    /**
     * TransactionTag.
     */
    TRANSTAG,

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
            theName = MoneyWiseXAnalysisBucketResource.getKeyForAnalysisType(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * Obtain default value.
     * @return the default
     */
    public MoneyWiseAnalysisAttribute getDefaultValue() {
        final MoneyWiseAnalysisAttribute[] myValues = getValues();
        return myValues != null && myValues.length > 0
                ? myValues[0]
                : null;
    }

    /**
     * Does the analysis have balances.
     * @return true/false
     */
    public boolean hasBalances() {
        switch (this) {
            case DEPOSIT:
            case CASH:
            case LOAN:
            case PAYEE:
            case PORTFOLIO:
            case SECURITY:
            case CATEGORY:
            case TAXBASIS:
                return true;
            case TRANSTAG:
            case ALL:
            default:
                return false;
        }
    }

    /**
     * Obtain values.
     * @return values
     */
    public MoneyWiseAnalysisAttribute[] getValues() {
        switch (this) {
            case DEPOSIT:
            case CASH:
            case LOAN:
            case PORTFOLIO:
            case TRANSTAG:
            case ALL:
                return MoneyWiseAnalysisAccountAttr.values();
            case SECURITY:
                return MoneyWiseAnalysisSecurityAttr.values();
            case PAYEE:
                return MoneyWiseAnalysisPayeeAttr.values();
            case CATEGORY:
                return MoneyWiseAnalysisTransAttr.values();
            case TAXBASIS:
                return MoneyWiseAnalysisTaxBasisAttr.values();
            default:
                throw new IllegalArgumentException("Invalid Attribute type " + toString());
        }
    }
}
