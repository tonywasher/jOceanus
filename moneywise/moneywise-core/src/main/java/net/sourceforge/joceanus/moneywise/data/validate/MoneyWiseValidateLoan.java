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
package net.sourceforge.joceanus.moneywise.data.validate;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataValidator.MoneyWiseDataValidatorAutoCorrect;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoan;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoan.MoneyWiseLoanList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoanCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoanCategory.MoneyWiseLoanCategoryList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee.MoneyWisePayeeList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseLoanCategoryClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataItem;
import io.github.tonywasher.joceanus.prometheus.views.PrometheusEditSet;

import java.util.Iterator;

/**
 * Validator for Loan.
 */
public class MoneyWiseValidateLoan
        extends MoneyWiseValidateAccount<MoneyWiseLoan>
        implements MoneyWiseDataValidatorAutoCorrect<MoneyWiseLoan> {
    /**
     * New Account name.
     */
    private static final String NAME_NEWACCOUNT = MoneyWiseBasicResource.LOAN_NEWACCOUNT.getValue();

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
    public void setEditSet(final PrometheusEditSet pEditSet) {
        super.setEditSet(pEditSet);
        theInfoSet.storeEditSet(pEditSet);
    }

    @Override
    public void validate(final PrometheusDataItem pLoan) {
        final MoneyWiseLoan myLoan = (MoneyWiseLoan) pLoan;
        final MoneyWisePayee myParent = myLoan.getParent();
        final MoneyWiseLoanCategory myCategory = myLoan.getCategory();
        final MoneyWiseCurrency myCurrency = myLoan.getAssetCurrency();
        final MoneyWiseLoanCategoryClass myClass = myLoan.getCategoryClass();

        /* Validate base components */
        super.validate(pLoan);

        /* Category must be non-null */
        if (myCategory == null) {
            pLoan.addError(PrometheusDataItem.ERROR_MISSING, MoneyWiseBasicResource.CATEGORY_NAME);
        } else if (myCategory.getCategoryTypeClass().isParentCategory()) {
            pLoan.addError(ERROR_BADCATEGORY, MoneyWiseBasicResource.CATEGORY_NAME);
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
                pLoan.addError(ERROR_BADPARENT, MoneyWiseBasicResource.ASSET_PARENT);
            }

            /* If we are open then parent must be open */
            if (!myLoan.isClosed() && Boolean.TRUE.equals(myParent.isClosed())) {
                pLoan.addError(ERROR_PARCLOSED, MoneyWiseBasicResource.ASSET_CLOSED);
            }
        }

        /* If we have an infoSet */
        if (myLoan.getInfoSet() != null) {
            /* Validate the InfoSet */
            theInfoSet.validate(myLoan.getInfoSet());
        }

        /* Set validation flag */
        if (!pLoan.hasErrors()) {
            pLoan.setValidEdit();
        }
    }

    @Override
    public void setDefaults(final MoneyWiseLoan pLoan) throws OceanusException {
        /* Set values */
        final MoneyWiseLoanList myList = pLoan.getList();
        pLoan.setName(getUniqueName(myList, NAME_NEWACCOUNT));
        pLoan.setCategory(getDefaultCategory());
        pLoan.setAssetCurrency(getReportingCurrency());
        pLoan.setClosed(Boolean.FALSE);
        autoCorrect(pLoan);
    }

    @Override
    public void autoCorrect(final MoneyWiseLoan pLoan) throws OceanusException {
        /* Access category class and parent */
        final MoneyWiseLoanCategoryClass myClass = pLoan.getCategoryClass();
        final MoneyWisePayee myParent = pLoan.getParent();

        /* Ensure that we have valid parent */
        if (myParent == null
                || !myParent.getCategoryClass().canParentLoan(myClass)) {
            pLoan.setParent(getDefaultParent(pLoan));
        }

        /* autoCorrect the infoSet */
        theInfoSet.autoCorrect(pLoan.getInfoSet());
    }

    /**
     * Obtain default category for new loan account.
     *
     * @return the default category
     */
    private MoneyWiseLoanCategory getDefaultCategory() {
        /* loop through the categories */
        final MoneyWiseLoanCategoryList myCategories
                = getEditSet().getDataList(MoneyWiseBasicDataType.LOANCATEGORY, MoneyWiseLoanCategoryList.class);
        final Iterator<MoneyWiseLoanCategory> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseLoanCategory myCategory = myIterator.next();

            /* Ignore deleted categories */
            if (myCategory.isDeleted()) {
                continue;
            }

            /* If the category is not a parent */
            if (!myCategory.isCategoryClass(MoneyWiseLoanCategoryClass.PARENT)) {
                return myCategory;
            }
        }

        /* Return no category */
        return null;
    }

    /**
     * Obtain default parent for new loan.
     *
     * @param pLoan the loan
     * @return the default parent
     */
    private MoneyWisePayee getDefaultParent(final MoneyWiseLoan pLoan) {
        /* Access details */
        final MoneyWisePayeeList myPayees = getEditSet().getDataList(MoneyWiseBasicDataType.PAYEE, MoneyWisePayeeList.class);
        final MoneyWiseLoanCategoryClass myClass = pLoan.getCategoryClass();

        /* loop through the payees */
        final Iterator<MoneyWisePayee> myIterator = myPayees.iterator();
        while (myIterator.hasNext()) {
            final MoneyWisePayee myPayee = myIterator.next();

            /* Ignore deleted and closed payees */
            if (myPayee.isDeleted() || Boolean.TRUE.equals(myPayee.isClosed())) {
                continue;
            }

            /* If the payee can parent */
            if (myPayee.getCategoryClass().canParentLoan(myClass)) {
                return myPayee;
            }
        }

        /* Return no payee */
        return null;
    }
}
