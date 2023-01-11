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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.base.MoneyWiseAnalysisValues;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.data.MoneyWiseAnalysis;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.data.MoneyWiseTaxBasisAttr;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.data.MoneyWiseTaxBasisBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * Tax Totals.
 */
public class MoneyWiseTaxTotals
        implements MetisFieldItem {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseTaxTotals> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseTaxTotals.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_TOTAL, MoneyWiseTaxTotals::getTotals);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_INITIAL, MoneyWiseTaxTotals::getInitial);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_PAYEES, MoneyWiseTaxTotals::getTaxBases);
    }

    /**
     * The top-level totals.
     */
    private final MoneyWiseAnalysisValues<MoneyWiseTaxBasisAttr> theTotals;

    /**
     * The initial totals.
     */
    private final MoneyWiseAnalysisValues<MoneyWiseTaxBasisAttr> theInitial;

    /**
     * The payees.
     */
    private final List<MoneyWiseTaxBasisBucket> theTaxBases;

    /**
     * Constructor.
     *
     * @param pAnalysis the analysis
     */
    MoneyWiseTaxTotals(final MoneyWiseAnalysis pAnalysis) {
        /* Create fields */
        final AssetCurrency myCurrency = pAnalysis.getReportingCurrency();
        theTotals = new MoneyWiseAnalysisValues<>(MoneyWiseTaxBasisAttr.class, myCurrency);
        theInitial = new MoneyWiseAnalysisValues<>(MoneyWiseTaxBasisAttr.class, myCurrency);
        theTaxBases = new ArrayList<>();

        /* Loop through the taxBasis buckets */
        for (MoneyWiseTaxBasisBucket myBucket : pAnalysis.getTaxBases().values()) {
            /* Adjust totals */
            addToTotals(theTotals, myBucket.getValues());
            addToTotals(theInitial, myBucket.getInitial());
            theTaxBases.add(myBucket);
        }

        /* Sort the taxBases */
        theTaxBases.sort(Comparator.comparing(MoneyWiseTaxBasisBucket::getOwner));
    }

    /**
     * Obtain the totals.
     *
     * @return the totals
     */
    public MoneyWiseAnalysisValues<MoneyWiseTaxBasisAttr> getTotals() {
        return theTotals;
    }

    /**
     * Obtain the initial totals.
     *
     * @return the initial totals
     */
    public MoneyWiseAnalysisValues<MoneyWiseTaxBasisAttr> getInitial() {
        return theInitial;
    }

    /**
     * Obtain the taxBases.
     *
     * @return the taxBases
     */
    public List<MoneyWiseTaxBasisBucket> getTaxBases() {
        return theTaxBases;
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Add to totals.
     * @param pTotals the totals
     * @param pDelta the delta
     */
    static void addToTotals(final MoneyWiseAnalysisValues<MoneyWiseTaxBasisAttr> pTotals,
                            final MoneyWiseAnalysisValues<MoneyWiseTaxBasisAttr> pDelta) {
        addToTotals(pTotals, pDelta, MoneyWiseTaxBasisAttr.GROSS);
        addToTotals(pTotals, pDelta, MoneyWiseTaxBasisAttr.NETT);
        addToTotals(pTotals, pDelta, MoneyWiseTaxBasisAttr.TAXCREDIT);
    }

    /**
     * Add to totals.
     * @param pTotals the totals
     * @param pDelta the delta
     * @param pAttr the attribute
     */
    private static void addToTotals(final MoneyWiseAnalysisValues<MoneyWiseTaxBasisAttr> pTotals,
                                    final MoneyWiseAnalysisValues<MoneyWiseTaxBasisAttr> pDelta,
                                    final MoneyWiseTaxBasisAttr pAttr) {
        final TethysMoney myTotals = pTotals.getMoneyValue(pAttr);
        final TethysMoney myDelta = pDelta.getMoneyValue(pAttr);
        myTotals.addAmount(myDelta);
    }
}

