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

import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCashCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCashCategory.MoneyWiseCashCategoryList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCategoryBase;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCashCategoryClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCashCategoryType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCashCategoryType.MoneyWiseCashCategoryTypeList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;

/**
 * Validator for CashCategory.
 */
public class MoneyWiseValidateCashCategory
        extends MoneyWiseValidateCategory<MoneyWiseCashCategory> {
    @Override
    public void validate(final PrometheusDataItem pCategory) {
        /* Validate the base */
        super.validate(pCategory);

        /* Access details */
        final MoneyWiseCashCategory myCategory = (MoneyWiseCashCategory) pCategory;
        final MoneyWiseCashCategoryType myCatType = myCategory.getCategoryType();
        final MoneyWiseCashCategory myParent = myCategory.getParentCategory();

        /* CashCategoryType must be non-null */
        if (myCatType == null) {
            pCategory.addError(PrometheusDataItem.ERROR_MISSING, MoneyWiseStaticDataType.CASHTYPE);
        } else {
            /* Access the class */
            final MoneyWiseCashCategoryClass myClass = myCatType.getCashClass();

            /* CashCategoryType must be enabled */
            if (!myCatType.getEnabled()) {
                pCategory.addError(PrometheusDataItem.ERROR_DISABLED, MoneyWiseStaticDataType.CASHTYPE);
            }

            /* Switch on the account class */
            if (MoneyWiseCashCategoryClass.PARENT.equals(myClass)) {
                /* If parent exists */
                if (myParent != null) {
                    pCategory.addError(PrometheusDataItem.ERROR_EXIST, PrometheusDataResource.DATAGROUP_PARENT);
                }
            } else {
                /* Check parent */
                if (myParent == null) {
                    pCategory.addError(PrometheusDataItem.ERROR_MISSING, PrometheusDataResource.DATAGROUP_PARENT);
                } else if (!myParent.isCategoryClass(MoneyWiseCashCategoryClass.PARENT)) {
                    pCategory.addError(MoneyWiseCategoryBase.ERROR_BADPARENT, PrometheusDataResource.DATAGROUP_PARENT);
                } else {
                    final String myName = myCategory.getName();

                    /* Check validity of parent */
                    final MoneyWiseCashCategoryClass myParentClass = myParent.getCategoryTypeClass();
                    if (!MoneyWiseCashCategoryClass.PARENT.equals(myParentClass)) {
                        pCategory.addError(MoneyWiseCategoryBase.ERROR_BADPARENT, PrometheusDataResource.DATAGROUP_PARENT);
                    }
                    /* Check that name reflects parent */
                    if ((myName != null) && !myName.startsWith(myParent.getName() + MoneyWiseCategoryBase.STR_SEP)) {
                        pCategory.addError(MoneyWiseCategoryBase.ERROR_MATCHPARENT, PrometheusDataResource.DATAGROUP_PARENT);
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
    public void setDefaults(final MoneyWiseCashCategory pParent,
                            final MoneyWiseCashCategory pCategory) throws OceanusException {
        /* Set values */
        final MoneyWiseCashCategoryList myList = pCategory.getList();
        final MoneyWiseCashCategoryTypeList myTypes
                = getEditSet().getDataList(MoneyWiseStaticDataType.CASHTYPE, MoneyWiseCashCategoryTypeList.class);
        pCategory.setCategoryType(myTypes.findItemByClass(pParent == null
                ? MoneyWiseCashCategoryClass.PARENT
                : MoneyWiseCashCategoryClass.CASH));
        pCategory.setParentCategory(pParent);
        pCategory.setSubCategoryName(myList.getUniqueName(pParent));
    }
}
