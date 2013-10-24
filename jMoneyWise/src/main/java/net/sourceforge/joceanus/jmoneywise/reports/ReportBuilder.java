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

import java.util.EnumMap;
import java.util.Map;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jmoneywise.views.Analysis;

import org.w3c.dom.Document;

/**
 * Report Classes.
 * @author Tony Washer
 */
public class ReportBuilder {
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(ReportBuilder.class.getName());

    /**
     * The Total text.
     */
    protected static final String TEXT_TOTAL = NLS_BUNDLE.getString("ReportTotal");

    /**
     * The Profit text.
     */
    protected static final String TEXT_PROFIT = NLS_BUNDLE.getString("ReportProfit");

    /**
     * The Income text.
     */
    protected static final String TEXT_INCOME = NLS_BUNDLE.getString("ReportIncome");

    /**
     * The Expense text.
     */
    protected static final String TEXT_EXPENSE = NLS_BUNDLE.getString("ReportExpense");

    /**
     * The Report Manager.
     */
    private final ReportManager theManager;

    /**
     * Map of allocated reports.
     */
    private final Map<ReportType, BasicReport<?, ?>> theReportMap;

    /**
     * Constructor.
     * @param pManager the report manager
     * @throws JDataException on error
     */
    public ReportBuilder(final ReportManager pManager) throws JDataException {
        /* Record the details */
        theManager = pManager;

        /* Allocate map */
        theReportMap = new EnumMap<ReportType, BasicReport<?, ?>>(ReportType.class);
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
        BasicReport<?, ?> myReport = theReportMap.get(pType);

        /* If we have not previously allocated this report */
        if (myReport == null) {
            /* Switch on the report type */
            switch (pType) {
                case NetWorth:
                    myReport = new NetWorth(theManager);
                    break;
                case BalanceSheet:
                    myReport = new BalanceSheet(theManager);
                    break;
                case CashFlow:
                    myReport = new CashFlow(theManager);
                    break;
                case IncomeExpense:
                    myReport = new IncomeExpense(theManager);
                    break;
                case TaxationBasis:
                    myReport = new TaxationBasis(theManager);
                    break;
                case TaxCalculation:
                    myReport = new TaxCalculation(theManager);
                    break;
                case Portfolio:
                    myReport = new Portfolio(theManager);
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
