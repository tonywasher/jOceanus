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
package io.github.tonywasher.joceanus.moneywise.atlas.reports;

import io.github.tonywasher.joceanus.oceanus.resource.OceanusBundleId;

/**
 * Report Types.
 */
public enum MoneyWiseXReportType {
    /**
     * Net Worth Report.
     */
    NETWORTH,

    /**
     * BalanceSheet Report.
     */
    BALANCESHEET,

    /**
     * CashFlow Report.
     */
    CASHFLOW,

    /**
     * Income/Expense Report.
     */
    INCOMEEXPENSE,

    /**
     * Taxation Basis Report.
     */
    TAXBASIS,

    /**
     * Tax Calculation Report.
     */
    TAXCALC,

    /**
     * Market Growth.
     */
    MARKETGROWTH,

    /**
     * Portfolio Report.
     */
    PORTFOLIO,

    /**
     * Asset Gains.
     */
    ASSETGAINS,

    /**
     * Capital Gains.
     */
    CAPITALGAINS;

    /**
     * Report Name.
     */
    private String theName;

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = bundleIdForReportType(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * is this a Point in time report?
     *
     * @return true/false
     */
    public boolean isPointInTime() {
        return switch (this) {
            case NETWORTH, PORTFOLIO, ASSETGAINS, CAPITALGAINS -> true;
            default -> false;
        };
    }

    /**
     * do we need to have securities for this report?
     *
     * @return true/false
     */
    public boolean needSecurities() {
        return switch (this) {
            case MARKETGROWTH, PORTFOLIO, ASSETGAINS, CAPITALGAINS -> true;
            default -> false;
        };
    }

    /**
     * Obtain the default report.
     *
     * @return the default
     */
    public static MoneyWiseXReportType getDefault() {
        return NETWORTH;
    }

    /**
     * Obtain the resource bundleId for the reportType.
     *
     * @param pType the reportType
     * @return the resource bundleId
     */
    private static OceanusBundleId bundleIdForReportType(final MoneyWiseXReportType pType) {
        return switch (pType) {
            case NETWORTH -> MoneyWiseXReportResource.TYPE_NETWORTH;
            case BALANCESHEET -> MoneyWiseXReportResource.TYPE_BALANCESHEET;
            case CASHFLOW -> MoneyWiseXReportResource.TYPE_CASHFLOW;
            case INCOMEEXPENSE -> MoneyWiseXReportResource.TYPE_INCEXP;
            case PORTFOLIO -> MoneyWiseXReportResource.TYPE_PORTFOLIO;
            case MARKETGROWTH -> MoneyWiseXReportResource.TYPE_MARKET;
            case TAXBASIS -> MoneyWiseXReportResource.TYPE_TAXBASIS;
            case TAXCALC -> MoneyWiseXReportResource.TYPE_TAXCALC;
            case ASSETGAINS -> MoneyWiseXReportResource.TYPE_ASSETGAINS;
            case CAPITALGAINS -> MoneyWiseXReportResource.TYPE_CAPITALGAINS;
            default -> throw new IllegalArgumentException();
        };
    }
}
