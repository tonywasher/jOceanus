/*
 * MoneyWise: Finance Application
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
package io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.data;

import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseStaticResource;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.base.MoneyWiseAnalysisAttribute;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisAccountAttr;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisPayeeAttr;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisSecurityAttr;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisTaxBasisAttr;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisTransAttr;
import io.github.tonywasher.joceanus.oceanus.resource.OceanusBundleId;

/**
 * Analysis Types.
 */
public enum MoneyWiseAnalysisType {
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
            theName = bundleIdForType(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * Obtain default value.
     *
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
     *
     * @return true/false
     */
    public boolean hasBalances() {
        return switch (this) {
            case DEPOSIT, CASH, LOAN, PAYEE, PORTFOLIO, SECURITY, CATEGORY, TAXBASIS -> true;
            default -> false;
        };
    }

    /**
     * Obtain values.
     *
     * @return values
     */
    public MoneyWiseAnalysisAttribute[] getValues() {
        return switch (this) {
            case DEPOSIT, CASH, LOAN, PORTFOLIO, TRANSTAG, ALL -> MoneyWiseAnalysisAccountAttr.values();
            case SECURITY -> MoneyWiseAnalysisSecurityAttr.values();
            case PAYEE -> MoneyWiseAnalysisPayeeAttr.values();
            case CATEGORY -> MoneyWiseAnalysisTransAttr.values();
            case TAXBASIS -> MoneyWiseAnalysisTaxBasisAttr.values();
            default -> throw new IllegalArgumentException("Invalid Attribute type " + toString());
        };
    }

    /**
     * Obtain the resource bundleId for the type.
     *
     * @param pType the type
     * @return the resource bundleId
     */
    private static OceanusBundleId bundleIdForType(final MoneyWiseAnalysisType pType) {
        return switch (pType) {
            case DEPOSIT -> MoneyWiseBasicResource.DEPOSIT_NAME;
            case CASH -> MoneyWiseBasicResource.CASH_NAME;
            case LOAN -> MoneyWiseBasicResource.LOAN_NAME;
            case PAYEE -> MoneyWiseBasicResource.PAYEE_NAME;
            case SECURITY -> MoneyWiseBasicResource.SECURITY_NAME;
            case PORTFOLIO -> MoneyWiseBasicResource.PORTFOLIO_NAME;
            case CATEGORY -> MoneyWiseBasicResource.TRANSCAT_NAME;
            case TAXBASIS -> MoneyWiseStaticResource.TAXBASIS_NAME;
            case TRANSTAG -> MoneyWiseBasicResource.TRANSTAG_NAME;
            case ALL -> MoneyWiseAnalysisDataResource.FILTER_ALL;
            default -> throw new IllegalArgumentException();
        };
    }
}
