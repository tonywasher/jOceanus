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

import java.util.ResourceBundle;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jtethys.resource.TethysBundleId;
import net.sourceforge.joceanus.jtethys.resource.TethysBundleLoader;

/**
 * Resource IDs for MoneyWise Analysis Data Fields.
 */
public enum MoneyWiseAnalysisTotalsResource
        implements TethysBundleId, MetisDataFieldId {
    /**
     * Totals.
     */
    TOTALS_TOTAL("Totals.Total"),

    /**
     * Initial.
     */
    TOTALS_INITIAL("Totals.Initial"),

    /**
     * Bucket.
     */
    TOTALS_BUCKET("Totals.Bucket"),

    /**
     * Category.
     */
    TOTALS_CATEGORY("Totals.Category"),

    /**
     * Categories.
     */
    TOTALS_CATEGORIES("Totals.Categories"),

    /**
     * Portfolio.
     */
    TOTALS_PORTFOLIO("Totals.Portfolio"),

    /**
     * Deposits.
     */
    TOTALS_DEPOSITS("Totals.Deposits"),

    /**
     * Cash.
     */
    TOTALS_CASH("Totals.Cash"),

    /**
     * Portfolios.
     */
    TOTALS_PORTFOLIOS("Totals.Portfolios"),

    /**
     * Holdings.
     */
    TOTALS_HOLDINGS("Totals.Holdings"),

    /**
     * Loans.
     */
    TOTALS_LOANS("Totals.Loans"),

    /**
     * Assets.
     */
    TOTALS_ASSETS("Totals.Assets"),

    /**
     * Payees.
     */
    TOTALS_PAYEES("Totals.Payees"),

    /**
     * TransactionCategories.
     */
    TOTALS_TRANS("Totals.Trans"),

    /**
     * TaxBases.
     */
    TOTALS_TAX("Totals.Tax");

    /**
     * The Resource Loader.
     */
    private static final TethysBundleLoader LOADER = TethysBundleLoader.getPackageLoader(MoneyWiseAnalysisTotalsResource.class.getCanonicalName(),
            ResourceBundle::getBundle);

    /**
     * The Id.
     */
    private final String theKeyName;

    /**
     * The Value.
     */
    private String theValue;

    /**
     * Constructor.
     * @param pKeyName the key name
     */
    MoneyWiseAnalysisTotalsResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    /**
     * Constructor.
     * @param pResource the underlying resource
     */
    MoneyWiseAnalysisTotalsResource(final TethysBundleId pResource) {
        theKeyName = null;
        theValue = pResource.getValue();
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "MoneyWise.analysis";
    }

    @Override
    public String getValue() {
        /* If we have not initialised the value */
        if (theValue == null) {
            /* Derive the value */
            theValue = LOADER.getValue(this);
        }

        /* return the value */
        return theValue;
    }

    @Override
    public String getId() {
        return getValue();
    }
}
