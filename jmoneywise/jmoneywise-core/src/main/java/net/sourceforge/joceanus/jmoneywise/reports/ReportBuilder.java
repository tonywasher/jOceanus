/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2016 Tony Washer
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

import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.AnalysisResource;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Report Classes.
 * @author Tony Washer
 */
public class ReportBuilder {
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
    private final ReportManager theManager;

    /**
     * Map of allocated reports.
     */
    private final Map<ReportType, BasicReport> theReportMap;

    /**
     * Constructor.
     * @param pManager the report manager
     * @throws OceanusException on error
     */
    public ReportBuilder(final ReportManager pManager) throws OceanusException {
        /* Record the details */
        theManager = pManager;

        /* Allocate map */
        theReportMap = new EnumMap<>(ReportType.class);
    }

    /**
     * Build a report of the appropriate type.
     * @param pAnalysis the analysis
     * @param pType the report type
     * @return the Web document
     */
    public Document createReport(final Analysis pAnalysis,
                                 final ReportType pType) {
        /* Access existing report */
        BasicReport myReport = theReportMap.get(pType);

        /* If we have not previously allocated this report */
        if (myReport == null) {
            /* Switch on the report type */
            switch (pType) {
                case NETWORTH:
                    myReport = new NetWorth(theManager);
                    break;
                case BALANCESHEET:
                    myReport = new BalanceSheet(theManager);
                    break;
                case CASHFLOW:
                    myReport = new CashFlow(theManager);
                    break;
                case INCOMEEXPENSE:
                    myReport = new IncomeExpense(theManager);
                    break;
                case PORTFOLIO:
                    myReport = new PortfolioView(theManager);
                    break;
                case MARKETGROWTH:
                    myReport = new MarketGrowth(theManager);
                    break;
                case TAXBASIS:
                    myReport = new TaxationBasis(theManager);
                    break;
                case TAXCALC:
                    myReport = new TaxCalculation(theManager);
                    break;
                default:
                    return null;
            }

            /* Store allocated report */
            theReportMap.put(pType, myReport);
        }

        /* Set up the report */
        myReport.clearMaps();
        theManager.setReport(myReport);

        /* Create the report */
        return myReport.createReport(pAnalysis);
    }
}
