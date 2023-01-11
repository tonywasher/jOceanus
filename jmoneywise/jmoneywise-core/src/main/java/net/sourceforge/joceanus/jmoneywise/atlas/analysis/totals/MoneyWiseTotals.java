/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.base.MoneyWiseAnalysisBaseResource;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.data.MoneyWiseAccountAttr;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.data.MoneyWiseAnalysis;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.data.MoneyWiseIncomeAttr;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.data.MoneyWiseTaxBasisAttr;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;

/**
 * Totals.
 */
public class MoneyWiseTotals
        implements MetisFieldItem {
    /**
     * Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(MoneyWiseTotals.class);

    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseTotals> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseTotals.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisBaseResource.HISTORY_RANGE, MoneyWiseTotals::getRange);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_ASSETS, MoneyWiseTotals::getAssets);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_PAYEES, MoneyWiseTotals::getPayees);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_TRANS, MoneyWiseTotals::getTrans);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_TAX, MoneyWiseTotals::getTax);
    }

    /**
     * The range.
     */
    private final TethysDateRange theRange;

    /**
     * The asset totals.
     */
    private final MoneyWiseAssetTotals theAssets;

    /**
     * The payee totals.
     */
    private final MoneyWisePayeeTotals thePayees;

    /**
     * The transaction totals.
     */
    private final MoneyWiseTransTotals theTrans;

    /**
     * The tax totals.
     */
    private final MoneyWiseTaxTotals theTax;

    /**
     * Constructor.
     * @param pAnalysis the analysis
     */
    MoneyWiseTotals(final MoneyWiseAnalysis pAnalysis) {
        /* Create fields */
        theRange = pAnalysis.getRange();
        theAssets = new MoneyWiseAssetTotals(pAnalysis);
        thePayees = new MoneyWisePayeeTotals(pAnalysis);
        theTrans = new MoneyWiseTransTotals(pAnalysis);
        theTax = new MoneyWiseTaxTotals(pAnalysis);

        /* Check the totals */
        checkTotals();
    }

    /**
     * Get the dateRange.
     * @return the dateRange
     */
    public TethysDateRange getRange() {
        return theRange;
    }

    /**
     * Obtain the asset totals.
     * @return the totals
     */
    public MoneyWiseAssetTotals getAssets() {
        return theAssets;
    }

    /**
     * Obtain the payee totals.
     * @return the totals
     */
    public MoneyWisePayeeTotals getPayees() {
        return thePayees;
    }

    /**
     * Obtain the transaction totals.
     * @return the totals
     */
    public MoneyWiseTransTotals getTrans() {
        return theTrans;
    }

    /**
     * Obtain the tax totals.
     * @return the totals
     */
    public MoneyWiseTaxTotals getTax() {
        return theTax;
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Check totals.
     */
    private void checkTotals() {
        /* Access asset totals */
        final TethysMoney myAssets = new TethysMoney(theAssets.getTotals().getMoneyValue(MoneyWiseAccountAttr.VALUATION));
        myAssets.subtractAmount(theAssets.getInitial().getMoneyValue(MoneyWiseAccountAttr.VALUATION));

        /* Check payees */
        final TethysMoney myPayees = new TethysMoney(thePayees.getTotals().getMoneyValue(MoneyWiseIncomeAttr.PROFIT));
        myPayees.subtractAmount(thePayees.getInitial().getMoneyValue(MoneyWiseIncomeAttr.PROFIT));
        if (!myAssets.equals(myPayees)) {
            LOGGER.error("Payee total mismatch");
        }

        /* Check transactions */
        final TethysMoney myTrans = new TethysMoney(theTrans.getTotals().getMoneyValue(MoneyWiseIncomeAttr.PROFIT));
        myPayees.subtractAmount(theTrans.getInitial().getMoneyValue(MoneyWiseIncomeAttr.PROFIT));
        if (!myAssets.equals(myTrans)) {
            LOGGER.error("TransactionCategory total mismatch");
        }

        /* Check tax */
        final TethysMoney myTax = new TethysMoney(theTax.getTotals().getMoneyValue(MoneyWiseTaxBasisAttr.NETT));
        myPayees.subtractAmount(theTax.getInitial().getMoneyValue(MoneyWiseTaxBasisAttr.NETT));
        if (!myAssets.equals(myTax)) {
            LOGGER.error("TaxBasis total mismatch");
        }
    }
}
