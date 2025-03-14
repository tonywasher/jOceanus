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
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDeposit;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDeposit.MoneyWiseDepositList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDepositCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDepositCategory.MoneyWiseDepositCategoryList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee.MoneyWisePayeeList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseDepositCategoryClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValidator.PrometheusDataValidatorAutoCorrect;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;

import java.util.Iterator;

/**
 * Validator for Deposit.
 */
public class MoneyWiseValidateDeposit
        extends MoneyWiseValidateAccount<MoneyWiseDeposit>
        implements PrometheusDataValidatorAutoCorrect<MoneyWiseDeposit> {
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
    public void setEditSet(final PrometheusEditSet pEditSet) {
        super.setEditSet(pEditSet);
        theInfoSet.storeEditSet(pEditSet);
    }

    @Override
    public void validate(final PrometheusDataItem pDeposit) {
        final MoneyWiseDeposit myDeposit = (MoneyWiseDeposit) pDeposit;
        final MoneyWisePayee myParent = myDeposit.getParent();
        final MoneyWiseDepositCategory myCategory = myDeposit.getCategory();
        final MoneyWiseCurrency myCurrency = myDeposit.getAssetCurrency();
        final MoneyWiseDepositCategoryClass myClass = myDeposit.getCategoryClass();

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
            if (!myDeposit.isClosed() && Boolean.TRUE.equals(myParent.isClosed())) {
                pDeposit.addError(MoneyWiseAssetBase.ERROR_PARCLOSED, MoneyWiseBasicResource.ASSET_CLOSED);
            }
        }

        /* If we have an infoSet */
        if (myDeposit.getInfoSet() != null) {
            /* Validate the InfoSet */
            theInfoSet.validate(myDeposit.getInfoSet());
        }

        /* Set validation flag */
        if (!pDeposit.hasErrors()) {
            pDeposit.setValidEdit();
        }
    }

    @Override
    public void setDefaults(final MoneyWiseDeposit pDeposit) throws OceanusException {
        /* Set values */
        final MoneyWiseDepositList myList = pDeposit.getList();
        pDeposit.setName(myList.getUniqueName(MoneyWiseDeposit.NAME_NEWACCOUNT));
        pDeposit.setCategory(getDefaultCategory());
        pDeposit.setAssetCurrency(getReportingCurrency());
        pDeposit.setClosed(Boolean.FALSE);
        autoCorrect(pDeposit);
    }

    @Override
    public void autoCorrect(final MoneyWiseDeposit pDeposit) throws OceanusException {
        /* Ensure that we have a valid parent */
        final MoneyWiseDepositCategoryClass myClass = pDeposit.getCategoryClass();
        final MoneyWisePayee myParent = pDeposit.getParent();
        if (myParent == null
                || !myParent.getCategoryClass().canParentDeposit(myClass)) {
            pDeposit.setParent(getDefaultParent(pDeposit));
        }

        /* autoCorrect the infoSet */
        theInfoSet.autoCorrect(pDeposit.getInfoSet());
    }

    /**
     * Obtain default category for new deposit account.
     * @return the default category
     */
    private MoneyWiseDepositCategory getDefaultCategory() {
        /* loop through the categories */
        final MoneyWiseDepositCategoryList myCategories
                = getEditSet().getDataList(MoneyWiseBasicDataType.DEPOSITCATEGORY, MoneyWiseDepositCategoryList.class);
        final Iterator<MoneyWiseDepositCategory> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseDepositCategory myCategory = myIterator.next();

            /* Ignore deleted categories */
            if (myCategory.isDeleted()) {
                continue;
            }

            /* If the category is not a parent */
            if (!myCategory.isCategoryClass(MoneyWiseDepositCategoryClass.PARENT)) {
                return myCategory;
            }
        }

        /* Return no category */
        return null;
    }

    /**
     * Obtain default parent for new deposit.
     * @param pDeposit the deposit
     * @return the default parent
     */
    private MoneyWisePayee getDefaultParent(final MoneyWiseDeposit pDeposit) {
        /* Access details */
        final MoneyWisePayeeList myPayees = getEditSet().getDataList(MoneyWiseBasicDataType.PAYEE, MoneyWisePayeeList.class);
        final MoneyWiseDepositCategoryClass myClass = pDeposit.getCategoryClass();

        /* loop through the payees */
        final Iterator<MoneyWisePayee> myIterator = myPayees.iterator();
        while (myIterator.hasNext()) {
            final MoneyWisePayee myPayee = myIterator.next();

            /* Ignore deleted and closed payees */
            if (myPayee.isDeleted() || Boolean.TRUE.equals(myPayee.isClosed())) {
                continue;
            }

            /* If the payee can parent */
            if (myPayee.getCategoryClass().canParentDeposit(myClass)) {
                return myPayee;
            }
        }

        /* Return no payee */
        return null;
    }
}
