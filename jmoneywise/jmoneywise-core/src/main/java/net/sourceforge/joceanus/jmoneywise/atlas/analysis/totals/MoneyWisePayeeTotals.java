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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.base.MoneyWiseAnalysisValues;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.data.MoneyWiseAnalysis;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.data.MoneyWiseIncomeAttr;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.data.MoneyWisePayeeBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * Payee Totals.
 */
public class MoneyWisePayeeTotals
        implements MetisFieldItem {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWisePayeeTotals> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWisePayeeTotals.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_TOTAL, MoneyWisePayeeTotals::getTotals);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_INITIAL, MoneyWisePayeeTotals::getInitial);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_PAYEES, MoneyWisePayeeTotals::getPayees);
    }

    /**
     * The top-level totals.
     */
    private final MoneyWiseAnalysisValues<MoneyWiseIncomeAttr> theTotals;

    /**
     * The initial totals.
     */
    private final MoneyWiseAnalysisValues<MoneyWiseIncomeAttr> theInitial;

    /**
     * The payees.
     */
    private final List<MoneyWisePayeeBucket> thePayees;

    /**
     * Constructor.
     *
     * @param pAnalysis the analysis
     */
    MoneyWisePayeeTotals(final MoneyWiseAnalysis pAnalysis) {
        /* Create fields */
        final AssetCurrency myCurrency = pAnalysis.getReportingCurrency();
        theTotals = new MoneyWiseAnalysisValues<>(MoneyWiseIncomeAttr.class, myCurrency);
        theInitial = new MoneyWiseAnalysisValues<>(MoneyWiseIncomeAttr.class, myCurrency);
        thePayees = new ArrayList<>();

        /* Loop through the payee buckets */
        for (MoneyWisePayeeBucket myBucket : pAnalysis.getPayees().values()) {
            /* Adjust totals */
            addToTotals(theTotals, myBucket.getValues());
            addToTotals(theInitial, myBucket.getInitial());
            thePayees.add(myBucket);
        }

        /* Sort the payees */
        thePayees.sort(Comparator.comparing(MoneyWisePayeeBucket::getOwner));
    }

    /**
     * Obtain the totals.
     *
     * @return the totals
     */
    public MoneyWiseAnalysisValues<MoneyWiseIncomeAttr> getTotals() {
        return theTotals;
    }

    /**
     * Obtain the initial totals.
     *
     * @return the initial totals
     */
    public MoneyWiseAnalysisValues<MoneyWiseIncomeAttr> getInitial() {
        return theInitial;
    }

    /**
     * Obtain the payees.
     *
     * @return the payees
     */
    public List<MoneyWisePayeeBucket> getPayees() {
        return thePayees;
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
    static void addToTotals(final MoneyWiseAnalysisValues<MoneyWiseIncomeAttr> pTotals,
                            final MoneyWiseAnalysisValues<MoneyWiseIncomeAttr> pDelta) {
        addToTotals(pTotals, pDelta, MoneyWiseIncomeAttr.INCOME);
        addToTotals(pTotals, pDelta, MoneyWiseIncomeAttr.EXPENSE);
        addToTotals(pTotals, pDelta, MoneyWiseIncomeAttr.PROFIT);
    }

    /**
     * Add to totals.
     * @param pTotals the totals
     * @param pDelta the delta
     * @param pAttr the attribute
     */
    private static void addToTotals(final MoneyWiseAnalysisValues<MoneyWiseIncomeAttr> pTotals,
                                    final MoneyWiseAnalysisValues<MoneyWiseIncomeAttr> pDelta,
                                    final MoneyWiseIncomeAttr pAttr) {
        final TethysMoney myTotals = pTotals.getMoneyValue(pAttr);
        final TethysMoney myDelta = pDelta.getMoneyValue(pAttr);
        myTotals.addAmount(myDelta);
    }
}
