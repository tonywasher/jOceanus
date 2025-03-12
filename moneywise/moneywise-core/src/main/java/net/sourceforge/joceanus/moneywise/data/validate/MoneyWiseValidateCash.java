/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.moneywise.data.validate;

import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetBase;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCash;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCashCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;

/**
 * Validator for Cash.
 */
public class MoneyWiseValidateCash
        extends MoneyWiseValidateAccount<MoneyWiseCash> {
    /**
     * The infoSet validator.
     */
    private final MoneyWiseValidateCashInfoSet theInfoSet;

    /**
     * Constructor.
     */
    MoneyWiseValidateCash() {
        theInfoSet = new MoneyWiseValidateCashInfoSet();
    }

    @Override
    public void validate(final MoneyWiseCash pCash) {
        final MoneyWisePayee myParent = pCash.getParent();
        final MoneyWiseCashCategory myCategory = pCash.getCategory();
        final MoneyWiseCurrency myCurrency = pCash.getAssetCurrency();

        /* Validate base components */
        super.validate(pCash);

        /* Category must be non-null */
        if (myCategory == null) {
            pCash.addError(PrometheusDataItem.ERROR_MISSING, MoneyWiseBasicResource.CATEGORY_NAME);
        } else if (myCategory.getCategoryTypeClass().isParentCategory()) {
            pCash.addError(MoneyWiseAssetBase.ERROR_BADCATEGORY, MoneyWiseBasicResource.CATEGORY_NAME);
        }

        /* Parent must be null */
        if (myParent != null) {
            pCash.addError(PrometheusDataItem.ERROR_EXIST, MoneyWiseBasicResource.ASSET_PARENT);
        }

        /* Currency must be non-null and enabled */
        if (myCurrency == null) {
            pCash.addError(PrometheusDataItem.ERROR_MISSING, MoneyWiseStaticDataType.CURRENCY);
        } else if (!myCurrency.getEnabled()) {
            pCash.addError(PrometheusDataItem.ERROR_DISABLED, MoneyWiseStaticDataType.CURRENCY);
        }

        /* If we have an infoSet */
        if (pCash.getInfoSet() != null) {
            /* Validate the InfoSet */
            theInfoSet.validate(pCash.getInfoSet());
        }

        /* Set validation flag */
        if (!pCash.hasErrors()) {
            pCash.setValidEdit();
        }
    }
}
