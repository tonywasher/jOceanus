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

import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCash;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCash.MoneyWiseCashList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCashCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCashCategory.MoneyWiseCashCategoryList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataValidator.MoneyWiseDataValidatorAutoCorrect;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCashCategoryClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;

import java.util.Iterator;

/**
 * Validator for Cash.
 */
public class MoneyWiseValidateCash
        extends MoneyWiseValidateAccount<MoneyWiseCash>
        implements MoneyWiseDataValidatorAutoCorrect<MoneyWiseCash> {
    /**
     * New Account name.
     */
    private static final String NAME_NEWACCOUNT = MoneyWiseBasicResource.CASH_NEWACCOUNT.getValue();

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
    public void setEditSet(final PrometheusEditSet pEditSet) {
        super.setEditSet(pEditSet);
        theInfoSet.storeEditSet(pEditSet);
    }

    @Override
    public void validate(final PrometheusDataItem pCash) {
        final MoneyWiseCash myCash = (MoneyWiseCash) pCash;
        final MoneyWisePayee myParent = myCash.getParent();
        final MoneyWiseCashCategory myCategory = myCash.getCategory();
        final MoneyWiseCurrency myCurrency = myCash.getAssetCurrency();

        /* Validate base components */
        super.validate(pCash);

        /* Category must be non-null */
        if (myCategory == null) {
            pCash.addError(PrometheusDataItem.ERROR_MISSING, MoneyWiseBasicResource.CATEGORY_NAME);
        } else if (myCategory.getCategoryTypeClass().isParentCategory()) {
            pCash.addError(ERROR_BADCATEGORY, MoneyWiseBasicResource.CATEGORY_NAME);
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
        if (myCash.getInfoSet() != null) {
            /* Validate the InfoSet */
            theInfoSet.validate(myCash.getInfoSet());
        }

        /* Set validation flag */
        if (!pCash.hasErrors()) {
            pCash.setValidEdit();
        }
    }

    @Override
    public void setDefaults(final MoneyWiseCash pCash) throws OceanusException {
        /* Set values */
        final MoneyWiseCashList myList = pCash.getList();
        pCash.setName(getUniqueName(myList, NAME_NEWACCOUNT));
        pCash.setCategory(getDefaultCategory());
        pCash.setAssetCurrency(getReportingCurrency());
        pCash.setClosed(Boolean.FALSE);
        autoCorrect(pCash);
    }

    @Override
    public void autoCorrect(final MoneyWiseCash pCash) throws OceanusException {
        /* autoCorrect the infoSet */
        theInfoSet.autoCorrect(pCash.getInfoSet());
    }

    /**
     * Obtain default category for new cash account.
     * @return the default category
     */
    private MoneyWiseCashCategory getDefaultCategory() {
        /* loop through the categories */
        final MoneyWiseCashCategoryList myCategories
                = getEditSet().getDataList(MoneyWiseBasicDataType.CASHCATEGORY, MoneyWiseCashCategoryList.class);
        final Iterator<MoneyWiseCashCategory> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseCashCategory myCategory = myIterator.next();

            /* Ignore deleted categories */
            if (myCategory.isDeleted()) {
                continue;
            }

            /* If the category is not a parent */
            if (!myCategory.isCategoryClass(MoneyWiseCashCategoryClass.PARENT)) {
                return myCategory;
            }
        }

        /* Return no category */
        return null;
    }
}
