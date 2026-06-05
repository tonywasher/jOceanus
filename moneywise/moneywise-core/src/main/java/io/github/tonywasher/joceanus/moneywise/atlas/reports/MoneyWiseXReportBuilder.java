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

import io.github.tonywasher.joceanus.metis.report.MetisReportBase;
import io.github.tonywasher.joceanus.metis.report.MetisReportManager;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisSecurityBucket;
import io.github.tonywasher.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter;
import org.w3c.dom.Document;

import java.util.EnumMap;
import java.util.Map;

/**
 * Report Classes.
 *
 * @author Tony Washer
 */
public class MoneyWiseXReportBuilder {
    /**
     * The Report Manager.
     */
    private final MetisReportManager<MoneyWiseXAnalysisFilter<?, ?>> theManager;

    /**
     * Map of allocated reports.
     */
    private final Map<MoneyWiseXReportType, MetisReportBase<MoneyWiseXAnalysis, MoneyWiseXAnalysisFilter<?, ?>>> theReportMap;

    /**
     * Constructor.
     *
     * @param pManager the report manager
     */
    public MoneyWiseXReportBuilder(final MetisReportManager<MoneyWiseXAnalysisFilter<?, ?>> pManager) {
        /* Record the details */
        theManager = pManager;

        /* Allocate map */
        theReportMap = new EnumMap<>(MoneyWiseXReportType.class);
    }

    /**
     * Build a report of the appropriate type.
     *
     * @param pAnalysis the analysis
     * @param pType     the report type
     * @param pSecurity the security
     * @return the Web document
     */
    public Document createReport(final MoneyWiseXAnalysis pAnalysis,
                                 final MoneyWiseXReportType pType,
                                 final MoneyWiseXAnalysisSecurityBucket pSecurity) {
        /* Access existing report */
        MetisReportBase<MoneyWiseXAnalysis, MoneyWiseXAnalysisFilter<?, ?>> myReport = theReportMap.get(pType);

        /* If we have not previously allocated this report */
        if (myReport == null) {
            /* Switch on the report type */
            myReport = switch (pType) {
                case NETWORTH -> new MoneyWiseXReportNetWorth(theManager);
                case BALANCESHEET -> new MoneyWiseXReportBalanceSheet(theManager);
                case CASHFLOW -> new MoneyWiseXReportCashFlow(theManager);
                case INCOMEEXPENSE -> new MoneyWiseXReportIncomeExpense(theManager);
                case PORTFOLIO -> new MoneyWiseXReportPortfolioView(theManager);
                case MARKETGROWTH -> new MoneyWiseXReportMarketGrowth(theManager);
                case TAXBASIS -> new MoneyWiseXReportTaxationBasis(theManager);
                case TAXCALC -> new MoneyWiseXReportTaxCalculation(theManager);
                case ASSETGAINS -> new MoneyWiseXReportAssetGains(theManager);
                case CAPITALGAINS -> new MoneyWiseXReportCapitalGains(theManager);
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
        if (myReport instanceof MoneyWiseXReportCapitalGains myCapGains) {
            myCapGains.setSecurity(pSecurity);
        }

        /* Set up the report */
        theManager.setReport(myReport);

        /* Create the report */
        return myReport.createReport(pAnalysis);
    }
}
