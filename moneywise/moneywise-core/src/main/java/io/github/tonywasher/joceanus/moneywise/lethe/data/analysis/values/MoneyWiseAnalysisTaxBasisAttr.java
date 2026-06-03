/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.values;

import io.github.tonywasher.joceanus.metis.data.MetisDataType;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.base.MoneyWiseAnalysisAttribute;
import io.github.tonywasher.joceanus.oceanus.resource.OceanusBundleId;

/**
 * TaxBasisAttribute enumeration.
 */
public enum MoneyWiseAnalysisTaxBasisAttr implements MoneyWiseAnalysisAttribute {
    /**
     * Gross Amount.
     */
    GROSS,

    /**
     * Nett Amount.
     */
    NETT,

    /**
     * TaxCredit.
     */
    TAXCREDIT;

    /**
     * The String name.
     */
    private String theName;

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = bundleIdForTaxBasisAttr(this).getValue();
        }

        /* return the name */
        return theName;
    }

    @Override
    public boolean isCounter() {
        return true;
    }

    @Override
    public MetisDataType getDataType() {
        return MetisDataType.MONEY;
    }

    /**
     * Obtain the resource bundleId for the attribute.
     *
     * @param pAttr the attribute
     * @return the resource bundleId
     */
    private static OceanusBundleId bundleIdForTaxBasisAttr(final MoneyWiseAnalysisTaxBasisAttr pAttr) {
        /* Create the map and return it */
        return switch (pAttr) {
            case GROSS -> MoneyWiseAnalysisValuesResource.TAXATTR_GROSS;
            case NETT -> MoneyWiseAnalysisValuesResource.TAXATTR_NETT;
            case TAXCREDIT -> MoneyWiseAnalysisValuesResource.TAXATTR_TAX;
            default -> throw new IllegalArgumentException();
        };
    }
}
