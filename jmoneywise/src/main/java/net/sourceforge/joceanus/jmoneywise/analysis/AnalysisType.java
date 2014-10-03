/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.analysis;

/**
 * Analysis Types.
 */
public enum AnalysisType {
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
            theName = AnalysisResource.getKeyForAnalysisType(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * Obtain default value.
     * @return the default
     */
    public BucketAttribute getDefaultValue() {
        BucketAttribute[] myValues = getValues();
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
    public BucketAttribute[] getValues() {
        switch (this) {
            case DEPOSIT:
            case CASH:
            case LOAN:
            case TRANSTAG:
            case ALL:
                return AccountAttribute.values();
            case SECURITY:
                return SecurityAttribute.values();
            case PAYEE:
                return PayeeAttribute.values();
            case CATEGORY:
                return EventAttribute.values();
            case TAXBASIS:
                return TaxBasisAttribute.values();
            default:
                throw new IllegalArgumentException("Invalid Attribute type " + toString());
        }
    }
}
