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
package io.github.tonywasher.joceanus.moneywise.lethe.reports;

import io.github.tonywasher.joceanus.metis.report.MetisReportBase;
import io.github.tonywasher.joceanus.metis.report.MetisReportManager;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysis;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisSecurityBucket;
import io.github.tonywasher.joceanus.moneywise.lethe.views.MoneyWiseAnalysisFilter;
import org.w3c.dom.Document;

import java.util.EnumMap;
import java.util.Map;

/**
 * Report Classes.
 *
 * @author Tony Washer
 */
public class MoneyWiseReportBuilder {
    /**
     * The Report Manager.
     */
    private final MetisReportManager<MoneyWiseAnalysisFilter<?, ?>> theManager;

    /**
     * Map of allocated reports.
     */
    private final Map<MoneyWiseReportType, MetisReportBase<MoneyWiseAnalysis, MoneyWiseAnalysisFilter<?, ?>>> theReportMap;

    /**
     * Constructor.
     *
     * @param pManager the report manager
     */
    public MoneyWiseReportBuilder(final MetisReportManager<MoneyWiseAnalysisFilter<?, ?>> pManager) {
        /* Record the details */
        theManager = pManager;

        /* Allocate map */
        theReportMap = new EnumMap<>(MoneyWiseReportType.class);
    }

    /**
     * Build a report of the appropriate type.
     *
     * @param pAnalysis the analysis
     * @param pType     the report type
     * @param pSecurity the security
     * @return the Web document
     */
    public Document createReport(final MoneyWiseAnalysis pAnalysis,
                                 final MoneyWiseReportType pType,
                                 final MoneyWiseAnalysisSecurityBucket pSecurity) {
        /* Access existing report */
        MetisReportBase<MoneyWiseAnalysis, MoneyWiseAnalysisFilter<?, ?>> myReport = theReportMap.get(pType);

        /* If we have not previously allocated this report */
        if (myReport == null) {
            /* Switch on the report type */
            myReport = switch (pType) {
                case NETWORTH -> new MoneyWiseReportNetWorth(theManager);
                case BALANCESHEET -> new MoneyWiseReportBalanceSheet(theManager);
                case CASHFLOW -> new MoneyWiseReportCashFlow(theManager);
                case INCOMEEXPENSE -> new MoneyWiseReportIncomeExpense(theManager);
                case PORTFOLIO -> new MoneyWiseReportPortfolioView(theManager);
                case MARKETGROWTH -> new MoneyWiseReportMarketGrowth(theManager);
                case TAXBASIS -> new MoneyWiseReportTaxationBasis(theManager);
                case TAXCALC -> new MoneyWiseReportTaxCalculation(theManager);
                case ASSETGAINS -> new MoneyWiseReportAssetGains(theManager);
                case CAPITALGAINS -> new MoneyWiseReportCapitalGains(theManager);
                default -> null;
            };

            /* Return null if no report found */
            if (myReport == null) {
                return null;
            }

            /* Store allocated report */
            theReportMap.put(pType, myReport);
        }

        /* If the report requires the security */
        if (myReport instanceof MoneyWiseReportCapitalGains myCapGains) {
            myCapGains.setSecurity(pSecurity);
        }

        /* Set up the report */
        theManager.setReport(myReport);

        /* Create the report */
        return myReport.createReport(pAnalysis);
    }
}
