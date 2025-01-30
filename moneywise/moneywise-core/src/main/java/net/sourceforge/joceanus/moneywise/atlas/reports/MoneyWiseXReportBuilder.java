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
package net.sourceforge.joceanus.moneywise.atlas.reports;

import java.util.EnumMap;
import java.util.Map;

import org.w3c.dom.Document;

import net.sourceforge.joceanus.metis.report.MetisReportBase;
import net.sourceforge.joceanus.metis.report.MetisReportManager;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisBucketResource;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisSecurityBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisValuesResource;
import net.sourceforge.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter;

/**
 * Report Classes.
 * @author Tony Washer
 */
public class MoneyWiseXReportBuilder {
    /**
     * The Total text.
     */
    protected static final String TEXT_TOTAL = MoneyWiseXAnalysisBucketResource.ANALYSIS_TOTALS.getValue();

    /**
     * The Profit text.
     */
    protected static final String TEXT_PROFIT = MoneyWiseXAnalysisValuesResource.SECURITYATTR_PROFIT.getValue();

    /**
     * The Income text.
     */
    protected static final String TEXT_INCOME = MoneyWiseXAnalysisValuesResource.PAYEEATTR_INCOME.getValue();

    /**
     * The Expense text.
     */
    protected static final String TEXT_EXPENSE = MoneyWiseXAnalysisValuesResource.PAYEEATTR_EXPENSE.getValue();

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
     * @param pAnalysis the analysis
     * @param pType the report type
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
            switch (pType) {
                case NETWORTH:
                    myReport = new MoneyWiseXReportNetWorth(theManager);
                    break;
                case BALANCESHEET:
                    myReport = new MoneyWiseXReportBalanceSheet(theManager);
                    break;
                case CASHFLOW:
                    myReport = new MoneyWiseXReportCashFlow(theManager);
                    break;
                case INCOMEEXPENSE:
                    myReport = new MoneyWiseXReportIncomeExpense(theManager);
                    break;
                case PORTFOLIO:
                    myReport = new MoneyWiseXReportPortfolioView(theManager);
                    break;
                case MARKETGROWTH:
                    myReport = new MoneyWiseXReportMarketGrowth(theManager);
                    break;
                case TAXBASIS:
                    myReport = new MoneyWiseXReportTaxationBasis(theManager);
                    break;
                case TAXCALC:
                    myReport = new MoneyWiseXReportTaxCalculation(theManager);
                    break;
                case ASSETGAINS:
                    myReport = new MoneyWiseXReportAssetGains(theManager);
                    break;
                case CAPITALGAINS:
                    myReport = new MoneyWiseXReportCapitalGains(theManager);
                    break;
                default:
                    return null;
            }

            /* Store allocated report */
            theReportMap.put(pType, myReport);
        }

        /* If the report requires the security */
        if (myReport instanceof MoneyWiseXReportCapitalGains) {
            ((MoneyWiseXReportCapitalGains) myReport).setSecurity(pSecurity);
        }

        /* Set up the report */
        theManager.setReport(myReport);

        /* Create the report */
        return myReport.createReport(pAnalysis);
    }
}
