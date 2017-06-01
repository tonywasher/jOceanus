/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2017 Tony Washer
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

import java.util.EnumMap;
import java.util.Map;

import org.w3c.dom.Document;

import net.sourceforge.joceanus.jmetis.lethe.report.MetisReportBase;
import net.sourceforge.joceanus.jmetis.lethe.report.MetisReportManager;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.AnalysisResource;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Report Classes.
 * @author Tony Washer
 */
public class MoneyWiseReportBuilder {
    /**
     * The Total text.
     */
    protected static final String TEXT_TOTAL = AnalysisResource.ANALYSIS_TOTALS.getValue();

    /**
     * The Profit text.
     */
    protected static final String TEXT_PROFIT = AnalysisResource.SECURITYATTR_PROFIT.getValue();

    /**
     * The Income text.
     */
    protected static final String TEXT_INCOME = AnalysisResource.PAYEEATTR_INCOME.getValue();

    /**
     * The Expense text.
     */
    protected static final String TEXT_EXPENSE = AnalysisResource.PAYEEATTR_EXPENSE.getValue();

    /**
     * The Report Manager.
     */
    private final MetisReportManager<AnalysisFilter<?, ?>> theManager;

    /**
     * Map of allocated reports.
     */
    private final Map<MoneyWiseReportType, MetisReportBase<Analysis, AnalysisFilter<?, ?>>> theReportMap;

    /**
     * Constructor.
     * @param pManager the report manager
     * @throws OceanusException on error
     */
    public MoneyWiseReportBuilder(final MetisReportManager<AnalysisFilter<?, ?>> pManager) throws OceanusException {
        /* Record the details */
        theManager = pManager;

        /* Allocate map */
        theReportMap = new EnumMap<>(MoneyWiseReportType.class);
    }

    /**
     * Build a report of the appropriate type.
     * @param pAnalysis the analysis
     * @param pType the report type
     * @return the Web document
     */
    public Document createReport(final Analysis pAnalysis,
                                 final MoneyWiseReportType pType) {
        /* Access existing report */
        MetisReportBase<Analysis, AnalysisFilter<?, ?>> myReport = theReportMap.get(pType);

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
                case CAPITALGAINS:
                    myReport = new MoneyWiseReportCapitalGains(theManager);
                    break;
                default:
                    return null;
            }

            /* Store allocated report */
            theReportMap.put(pType, myReport);
        }

        /* Set up the report */
        theManager.setReport(myReport);

        /* Create the report */
        return myReport.createReport(pAnalysis);
    }
}
