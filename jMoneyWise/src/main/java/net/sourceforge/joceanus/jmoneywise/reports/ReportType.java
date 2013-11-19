/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.reports;

/**
 * Report Types.
 */
public enum ReportType {
    /**
     * Net Worth Report.
     */
    NetWorth,

    /**
     * BalanceSheet Report.
     */
    BalanceSheet,

    /**
     * CashFlow Report.
     */
    CashFlow,

    /**
     * Income/Expense Report.
     */
    IncomeExpense("Income/Expense"),

    /**
     * Taxation Basis Report.
     */
    TaxationBasis,

    /**
     * Tax Calculation Report.
     */
    TaxCalculation,

    /**
     * Market Growth.
     */
    MarketGrowth,

    /**
     * Market Report.
     */
    Portfolio("Market");

    /**
     * Report Name.
     */
    private final String theName;

    @Override
    public String toString() {
        return theName;
    }

    /**
     * Constructor.
     */
    private ReportType() {
        theName = name();
    }

    /**
     * Constructor.
     * @param pName the report name
     */
    private ReportType(final String pName) {
        theName = pName;
    }

    /**
     * is this a Point in time report?
     * @return true/false
     */
    public boolean isPointInTime() {
        switch (this) {
            case NetWorth:
            case Portfolio:
                return true;
            default:
                return false;
        }
    }
}
