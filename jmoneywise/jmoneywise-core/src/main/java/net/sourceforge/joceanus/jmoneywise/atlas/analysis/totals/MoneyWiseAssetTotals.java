/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.atlas.analysis.totals;

import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.base.MoneyWiseAnalysisValues;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.data.MoneyWiseAccountAttr;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.data.MoneyWiseAnalysis;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * Asset Totals.
 */
public class MoneyWiseAssetTotals
        implements MetisFieldItem {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseAssetTotals> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAssetTotals.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_TOTAL, MoneyWiseAssetTotals::getTotals);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_INITIAL, MoneyWiseAssetTotals::getInitial);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_DEPOSITS, MoneyWiseAssetTotals::getDeposits);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_CASH, MoneyWiseAssetTotals::getCash);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_PORTFOLIOS, MoneyWiseAssetTotals::getPortfolios);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_LOANS, MoneyWiseAssetTotals::getLoans);
    }

    /**
     * The top-level totals.
     */
    private final MoneyWiseAnalysisValues<MoneyWiseAccountAttr> theTotals;

    /**
     * The initial totals.
     */
    private final MoneyWiseAnalysisValues<MoneyWiseAccountAttr> theInitial;

    /**
     * The deposit totals.
     */
    private final MoneyWiseDepositTotals theDeposits;

    /**
     * The cash totals.
     */
    private final MoneyWiseCashTotals theCash;

    /**
     * The portfolio totals.
     */
    private final MoneyWisePortfolioTotals thePortfolios;

    /**
     * The loan totals.
     */
    private final MoneyWiseLoanTotals theLoans;

    /**
     * Constructor.
     * @param pAnalysis the analysis
     */
    MoneyWiseAssetTotals(final MoneyWiseAnalysis pAnalysis) {
        /* Create fields */
        final AssetCurrency myCurrency = pAnalysis.getReportingCurrency();
        theTotals = new MoneyWiseAnalysisValues<>(MoneyWiseAccountAttr.class, myCurrency);
        theInitial = new MoneyWiseAnalysisValues<>(MoneyWiseAccountAttr.class, myCurrency);
        theDeposits = new MoneyWiseDepositTotals(pAnalysis);
        theCash = new MoneyWiseCashTotals(pAnalysis);
        thePortfolios = new MoneyWisePortfolioTotals(pAnalysis);
        theLoans = new MoneyWiseLoanTotals(pAnalysis);

        /* Build the totals */
        buildTotals();
    }

    /**
     * Obtain the totals.
     * @return the totals
     */
    public MoneyWiseAnalysisValues<MoneyWiseAccountAttr> getTotals() {
        return theTotals;
    }

    /**
     * Obtain the initial totals.
     * @return the initial totals
     */
    public MoneyWiseAnalysisValues<MoneyWiseAccountAttr> getInitial() {
        return theInitial;
    }

    /**
     * Obtain the deposit totals.
     * @return the totals
     */
    public MoneyWiseDepositTotals  getDeposits() {
        return theDeposits;
    }

    /**
     * Obtain the cash totals.
     * @return the totals
     */
    public MoneyWiseCashTotals  getCash() {
        return theCash;
    }

    /**
     * Obtain the portfolio totals.
     * @return the totals
     */
    public MoneyWisePortfolioTotals  getPortfolios() {
        return thePortfolios;
    }

    /**
     * Obtain the loan totals.
     * @return the totals
     */
    public MoneyWiseLoanTotals  getLoans() {
        return theLoans;
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Build totals.
     */
    private void buildTotals() {
        /* Build totals */
        addToTotals(theTotals, theDeposits.getTotals());
        addToTotals(theTotals, theCash.getTotals());
        addToTotals(theTotals, thePortfolios.getTotals());
        addToTotals(theTotals, theLoans.getTotals());

        /* Build initial */
        addToTotals(theInitial, theDeposits.getInitial());
        addToTotals(theInitial, theCash.getInitial());
        addToTotals(theInitial, thePortfolios.getInitial());
        addToTotals(theInitial, theLoans.getInitial());
    }

    /**
     * Add to totals.
     * @param pTotals the totals
     * @param pDelta the delta
     */
    static void addToTotals(final MoneyWiseAnalysisValues<MoneyWiseAccountAttr> pTotals,
                            final MoneyWiseAnalysisValues<MoneyWiseAccountAttr> pDelta) {
        final TethysMoney myTotals = pTotals.getMoneyValue(MoneyWiseAccountAttr.VALUATION);
        final TethysMoney myDelta = pDelta.getMoneyValue(MoneyWiseAccountAttr.VALUATION);
        myTotals.addAmount(myDelta);
    }
}
