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
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDeposit;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDepositCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseDepositCategoryClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;

/**
 * Validator for Deposit.
 */
public class MoneyWiseValidateDeposit
        extends MoneyWiseValidateAccount<MoneyWiseDeposit> {
    /**
     * The infoSet validator.
     */
    private final MoneyWiseValidateDepositInfoSet theInfoSet;

    /**
     * Constructor.
     */
    MoneyWiseValidateDeposit() {
        theInfoSet = new MoneyWiseValidateDepositInfoSet();
    }

    @Override
    public void validate(final MoneyWiseDeposit pDeposit) {
        final MoneyWisePayee myParent = pDeposit.getParent();
        final MoneyWiseDepositCategory myCategory = pDeposit.getCategory();
        final MoneyWiseCurrency myCurrency = pDeposit.getAssetCurrency();
        final MoneyWiseDepositCategoryClass myClass = pDeposit.getCategoryClass();

        /* Validate base components */
        super.validate(pDeposit);

        /* Category must be non-null */
        if (myCategory == null) {
            pDeposit.addError(PrometheusDataItem.ERROR_MISSING, MoneyWiseBasicResource.CATEGORY_NAME);
        } else if (myCategory.getCategoryTypeClass().isParentCategory()) {
            pDeposit.addError(MoneyWiseAssetBase.ERROR_BADCATEGORY, MoneyWiseBasicResource.CATEGORY_NAME);
        }

        /* Currency must be non-null and enabled */
        if (myCurrency == null) {
            pDeposit.addError(PrometheusDataItem.ERROR_MISSING, MoneyWiseStaticDataType.CURRENCY);
        } else if (!myCurrency.getEnabled()) {
            pDeposit.addError(PrometheusDataItem.ERROR_DISABLED, MoneyWiseStaticDataType.CURRENCY);
        }

        /* Deposit must be a child */
        if (!myClass.isChild()) {
            pDeposit.addError(PrometheusDataItem.ERROR_EXIST, MoneyWiseBasicResource.ASSET_PARENT);

            /* Must have parent */
        } else if (myParent == null) {
            pDeposit.addError(PrometheusDataItem.ERROR_MISSING, MoneyWiseBasicResource.ASSET_PARENT);
        } else {
            /* Parent must be suitable */
            if (!myParent.getCategoryClass().canParentDeposit(myClass)) {
                pDeposit.addError(MoneyWiseAssetBase.ERROR_BADPARENT, MoneyWiseBasicResource.ASSET_PARENT);
            }

            /* If we are open then parent must be open */
            if (!pDeposit.isClosed() && Boolean.TRUE.equals(myParent.isClosed())) {
                pDeposit.addError(MoneyWiseAssetBase.ERROR_PARCLOSED, MoneyWiseBasicResource.ASSET_CLOSED);
            }
        }

        /* If we have an infoSet */
        if (pDeposit.getInfoSet() != null) {
            /* Validate the InfoSet */
            theInfoSet.validate(pDeposit.getInfoSet());
        }

        /* Set validation flag */
        if (!pDeposit.hasErrors()) {
            pDeposit.setValidEdit();
        }
    }
}
