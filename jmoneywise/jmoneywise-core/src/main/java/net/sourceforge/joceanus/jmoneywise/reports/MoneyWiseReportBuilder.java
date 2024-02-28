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
package net.sourceforge.joceanus.jmoneywise.reports;

import java.util.EnumMap;
import java.util.Map;

import org.w3c.dom.Document;

import net.sourceforge.joceanus.jmetis.report.MetisReportBase;
import net.sourceforge.joceanus.jmetis.report.MetisReportManager;
import net.sourceforge.joceanus.jmoneywise.data.analysis.data.MoneyWiseAnalysis;
import net.sourceforge.joceanus.jmoneywise.data.analysis.data.MoneyWiseAnalysisDataResource;
import net.sourceforge.joceanus.jmoneywise.data.analysis.data.MoneyWiseAnalysisSecurityBucket;
import net.sourceforge.joceanus.jmoneywise.views.MoneyWiseAnalysisFilter;

/**
 * Report Classes.
 * @author Tony Washer
 */
public class MoneyWiseReportBuilder {
    /**
     * The Total text.
     */
    protected static final String TEXT_TOTAL = MoneyWiseAnalysisDataResource.ANALYSIS_TOTALS.getValue();

    /**
     * The Profit text.
     */
    protected static final String TEXT_PROFIT = MoneyWiseAnalysisDataResource.SECURITYATTR_PROFIT.getValue();

    /**
     * The Income text.
     */
    protected static final String TEXT_INCOME = MoneyWiseAnalysisDataResource.PAYEEATTR_INCOME.getValue();

    /**
     * The Expense text.
     */
    protected static final String TEXT_EXPENSE = MoneyWiseAnalysisDataResource.PAYEEATTR_EXPENSE.getValue();

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
     * @param pAnalysis the analysis
     * @param pType the report type
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
            switch (pType) {
                case NETWORTH:
                    myReport = new MoneyWiseReportNetWorth(theManager);
                    break;
                case BALANCESHEET:
                    myReport = new MoneyWiseReportBalanceSheet(theManager);
                    break;
                case CASHFLOW:
                    myReport = new MoneyWiseReportCashFlow(theManager);
                    break;
                case INCOMEEXPENSE:
                    myReport = new MoneyWiseReportIncomeExpense(theManager);
                    break;
                case PORTFOLIO:
                    myReport = new MoneyWiseReportPortfolioView(theManager);
                    break;
                case MARKETGROWTH:
                    myReport = new MoneyWiseReportMarketGrowth(theManager);
                    break;
                case TAXBASIS:
                    myReport = new MoneyWiseReportTaxationBasis(theManager);
                    break;
                case TAXCALC:
                    myReport = new MoneyWiseReportTaxCalculation(theManager);
                    break;
                case ASSETGAINS:
                    myReport = new MoneyWiseReportAssetGains(theManager);
                    break;
                case CAPITALGAINS:
                    myReport = new MoneyWiseReportCapitalGains(theManager);
                    break;
                default:
                    return null;
            }

            /* Store allocated report */
            theReportMap.put(pType, myReport);
        }

        /* If the report requires the security */
        if (myReport instanceof MoneyWiseReportCapitalGains) {
            ((MoneyWiseReportCapitalGains) myReport).setSecurity(pSecurity);
        }

        /* Set up the report */
        theManager.setReport(myReport);

        /* Create the report */
        return myReport.createReport(pAnalysis);
    }
}
