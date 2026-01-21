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
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCategoryBase;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDepositCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDepositCategory.MoneyWiseDepositCategoryList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseDepositCategoryClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseDepositCategoryType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseDepositCategoryType.MoneyWiseDepositCategoryTypeList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataItem;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataResource;

/**
 * Validator for DepositCategory.
 */
public class MoneyWiseValidateDepositCategory
        extends MoneyWiseValidateCategory<MoneyWiseDepositCategory> {

    @Override
    public void validate(final PrometheusDataItem pCategory) {
        /* Validate the base */
        super.validate(pCategory);

        /* Access details */
        final MoneyWiseDepositCategory myCategory = (MoneyWiseDepositCategory) pCategory;
        final MoneyWiseDepositCategoryType myCatType = myCategory.getCategoryType();
        final MoneyWiseDepositCategory myParent = myCategory.getParentCategory();

        /* DepositCategoryType must be non-null */
        if (myCatType == null) {
            pCategory.addError(PrometheusDataItem.ERROR_MISSING, MoneyWiseStaticDataType.DEPOSITTYPE);
        } else {
            /* Access the class */
            final MoneyWiseDepositCategoryClass myClass = myCatType.getDepositClass();

            /* DepositCategoryType must be enabled */
            if (!myCatType.getEnabled()) {
                pCategory.addError(PrometheusDataItem.ERROR_DISABLED, MoneyWiseStaticDataType.DEPOSITTYPE);
            }

            /* Switch on the account class */
            if (MoneyWiseDepositCategoryClass.PARENT.equals(myClass)) {
                /* If parent exists */
                if (myParent != null) {
                    pCategory.addError(PrometheusDataItem.ERROR_EXIST, PrometheusDataResource.DATAGROUP_PARENT);
                }
            } else {
                /* Check parent */
                if (myParent == null) {
                    pCategory.addError(PrometheusDataItem.ERROR_MISSING, PrometheusDataResource.DATAGROUP_PARENT);
                } else if (!myParent.isCategoryClass(MoneyWiseDepositCategoryClass.PARENT)) {
                    pCategory.addError(ERROR_BADPARENT, PrometheusDataResource.DATAGROUP_PARENT);
                } else {
                    final String myName = myCategory.getName();

                    /* Check validity of parent */
                    final MoneyWiseDepositCategoryClass myParentClass = myParent.getCategoryTypeClass();
                    if (!MoneyWiseDepositCategoryClass.PARENT.equals(myParentClass)) {
                        pCategory.addError(ERROR_BADPARENT, PrometheusDataResource.DATAGROUP_PARENT);
                    }
                    /* Check that name reflects parent */
                    if ((myName != null) && !myName.startsWith(myParent.getName() + MoneyWiseCategoryBase.STR_SEP)) {
                        pCategory.addError(ERROR_MATCHPARENT, PrometheusDataResource.DATAGROUP_PARENT);
                    }
                }
            }
        }

        /* Set validation flag */
        if (!pCategory.hasErrors()) {
            pCategory.setValidEdit();
        }
    }

    @Override
    public void setDefaults(final MoneyWiseDepositCategory pParent,
                            final MoneyWiseDepositCategory pCategory) throws OceanusException {
        /* Set values */
        final MoneyWiseDepositCategoryList myList = pCategory.getList();
        final MoneyWiseDepositCategoryTypeList myTypes
                = getEditSet().getDataList(MoneyWiseStaticDataType.DEPOSITTYPE, MoneyWiseDepositCategoryTypeList.class);
        pCategory.setCategoryType(myTypes.findItemByClass(pParent == null
                ? MoneyWiseDepositCategoryClass.PARENT
                : MoneyWiseDepositCategoryClass.SAVINGS));
        pCategory.setParentCategory(pParent);
        pCategory.setSubCategoryName(getUniqueName(myList, pParent));
    }
}
