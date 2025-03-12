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
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoan;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoanCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseLoanCategoryClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;

/**
 * Validator for Loan.
 */
public class MoneyWiseValidateLoan
        extends MoneyWiseValidateAccount<MoneyWiseLoan> {
    /**
     * The infoSet validator.
     */
    private final MoneyWiseValidateLoanInfoSet theInfoSet;

    /**
     * Constructor.
     */
    MoneyWiseValidateLoan() {
        theInfoSet = new MoneyWiseValidateLoanInfoSet();
    }

    @Override
    public void validate(final MoneyWiseLoan pLoan) {
        final MoneyWisePayee myParent = pLoan.getParent();
        final MoneyWiseLoanCategory myCategory = pLoan.getCategory();
        final MoneyWiseCurrency myCurrency = pLoan.getAssetCurrency();
        final MoneyWiseLoanCategoryClass myClass = pLoan.getCategoryClass();

        /* Validate base components */
        super.validate(pLoan);

        /* Category must be non-null */
        if (myCategory == null) {
            pLoan.addError(PrometheusDataItem.ERROR_MISSING, MoneyWiseBasicResource.CATEGORY_NAME);
        } else if (myCategory.getCategoryTypeClass().isParentCategory()) {
            pLoan.addError(MoneyWiseAssetBase.ERROR_BADCATEGORY, MoneyWiseBasicResource.CATEGORY_NAME);
        }

        /* Currency must be non-null and enabled */
        if (myCurrency == null) {
            pLoan.addError(PrometheusDataItem.ERROR_MISSING, MoneyWiseStaticDataType.CURRENCY);
        } else if (!myCurrency.getEnabled()) {
            pLoan.addError(PrometheusDataItem.ERROR_DISABLED, MoneyWiseStaticDataType.CURRENCY);
        }

        /* Loan must be a child */
        if (!myClass.isChild()) {
            pLoan.addError(PrometheusDataItem.ERROR_EXIST, MoneyWiseBasicResource.ASSET_PARENT);

            /* Must have parent */
        } else if (myParent == null) {
            pLoan.addError(PrometheusDataItem.ERROR_MISSING, MoneyWiseBasicResource.ASSET_PARENT);
        } else {
            /* Parent must be suitable */
            if (!myParent.getCategoryClass().canParentLoan(myClass)) {
                pLoan.addError(MoneyWiseAssetBase.ERROR_BADPARENT, MoneyWiseBasicResource.ASSET_PARENT);
            }

            /* If we are open then parent must be open */
            if (!pLoan.isClosed() && Boolean.TRUE.equals(myParent.isClosed())) {
                pLoan.addError(MoneyWiseAssetBase.ERROR_PARCLOSED, MoneyWiseBasicResource.ASSET_CLOSED);
            }
        }

        /* If we have an infoSet */
        if (pLoan.getInfoSet() != null) {
            /* Validate the InfoSet */
            theInfoSet.validate(pLoan.getInfoSet());
        }

        /* Set validation flag */
        if (!pLoan.hasErrors()) {
            pLoan.setValidEdit();
        }
    }
}
