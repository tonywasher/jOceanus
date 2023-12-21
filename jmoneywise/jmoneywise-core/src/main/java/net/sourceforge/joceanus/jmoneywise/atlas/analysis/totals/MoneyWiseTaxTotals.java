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
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.base.MoneyWiseXAnalysisValues;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.data.MoneyWiseXAnalysis;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.data.MoneyWiseXTaxBasisAttr;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.data.MoneyWiseXTaxBasisBucket;
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
    private final MoneyWiseXAnalysisValues<MoneyWiseXTaxBasisAttr> theTotals;

    /**
     * The initial totals.
     */
    private final MoneyWiseXAnalysisValues<MoneyWiseXTaxBasisAttr> theInitial;

    /**
     * The payees.
     */
    private final List<MoneyWiseXTaxBasisBucket> theTaxBases;

    /**
     * Constructor.
     *
     * @param pAnalysis the analysis
     */
    MoneyWiseTaxTotals(final MoneyWiseXAnalysis pAnalysis) {
        /* Create fields */
        final AssetCurrency myCurrency = pAnalysis.getReportingCurrency();
        theTotals = new MoneyWiseXAnalysisValues<>(MoneyWiseXTaxBasisAttr.class, myCurrency);
        theInitial = new MoneyWiseXAnalysisValues<>(MoneyWiseXTaxBasisAttr.class, myCurrency);
        theTaxBases = new ArrayList<>();

        /* Loop through the taxBasis buckets */
        for (MoneyWiseXTaxBasisBucket myBucket : pAnalysis.getTaxBases().values()) {
            /* Adjust totals */
            addToTotals(theTotals, myBucket.getValues());
            addToTotals(theInitial, myBucket.getInitial());
            theTaxBases.add(myBucket);
        }

        /* Sort the taxBases */
        theTaxBases.sort(Comparator.comparing(MoneyWiseXTaxBasisBucket::getOwner));
    }

    /**
     * Obtain the totals.
     *
     * @return the totals
     */
    public MoneyWiseXAnalysisValues<MoneyWiseXTaxBasisAttr> getTotals() {
        return theTotals;
    }

    /**
     * Obtain the initial totals.
     *
     * @return the initial totals
     */
    public MoneyWiseXAnalysisValues<MoneyWiseXTaxBasisAttr> getInitial() {
        return theInitial;
    }

    /**
     * Obtain the taxBases.
     *
     * @return the taxBases
     */
    public List<MoneyWiseXTaxBasisBucket> getTaxBases() {
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
    static void addToTotals(final MoneyWiseXAnalysisValues<MoneyWiseXTaxBasisAttr> pTotals,
                            final MoneyWiseXAnalysisValues<MoneyWiseXTaxBasisAttr> pDelta) {
        addToTotals(pTotals, pDelta, MoneyWiseXTaxBasisAttr.GROSS);
        addToTotals(pTotals, pDelta, MoneyWiseXTaxBasisAttr.NETT);
        addToTotals(pTotals, pDelta, MoneyWiseXTaxBasisAttr.TAXCREDIT);
    }

    /**
     * Add to totals.
     * @param pTotals the totals
     * @param pDelta the delta
     * @param pAttr the attribute
     */
    private static void addToTotals(final MoneyWiseXAnalysisValues<MoneyWiseXTaxBasisAttr> pTotals,
                                    final MoneyWiseXAnalysisValues<MoneyWiseXTaxBasisAttr> pDelta,
                                    final MoneyWiseXTaxBasisAttr pAttr) {
        final TethysMoney myTotals = pTotals.getMoneyValue(pAttr);
        final TethysMoney myDelta = pDelta.getMoneyValue(pAttr);
        myTotals.addAmount(myDelta);
    }
}

