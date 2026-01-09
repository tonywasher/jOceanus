/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012-2026 Tony Washer
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

import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCategoryBase;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoanCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoanCategory.MoneyWiseLoanCategoryList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseLoanCategoryClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseLoanCategoryType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseLoanCategoryType.MoneyWiseLoanCategoryTypeList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;

/**
 * Validator for LoanCategory.
 */
public class MoneyWiseValidateLoanCategory
        extends MoneyWiseValidateCategory<MoneyWiseLoanCategory> {

    @Override
    public void validate(final PrometheusDataItem pCategory) {
        /* Validate the base */
        super.validate(pCategory);

        /* Access details */
        final MoneyWiseLoanCategory myCategory = (MoneyWiseLoanCategory) pCategory;
        final MoneyWiseLoanCategoryType myCatType = myCategory.getCategoryType();
        final MoneyWiseLoanCategory myParent = myCategory.getParentCategory();

        /* LoanCategoryType must be non-null */
        if (myCatType == null) {
            pCategory.addError(PrometheusDataItem.ERROR_MISSING, MoneyWiseStaticDataType.DEPOSITTYPE);
        } else {
            /* Access the class */
            final MoneyWiseLoanCategoryClass myClass = myCatType.getLoanClass();

            /* LoanCategoryType must be enabled */
            if (!myCatType.getEnabled()) {
                pCategory.addError(PrometheusDataItem.ERROR_DISABLED, MoneyWiseStaticDataType.DEPOSITTYPE);
            }

            /* Switch on the account class */
            if (MoneyWiseLoanCategoryClass.PARENT.equals(myClass)) {
                /* If parent exists */
                if (myParent != null) {
                    pCategory.addError(PrometheusDataItem.ERROR_EXIST, PrometheusDataResource.DATAGROUP_PARENT);
                }
            } else {
                /* Check parent */
                if (myParent == null) {
                    pCategory.addError(PrometheusDataItem.ERROR_MISSING, PrometheusDataResource.DATAGROUP_PARENT);
                } else if (!myParent.isCategoryClass(MoneyWiseLoanCategoryClass.PARENT)) {
                    pCategory.addError(ERROR_BADPARENT, PrometheusDataResource.DATAGROUP_PARENT);
                } else {
                    final String myName = myCategory.getName();

                    /* Check validity of parent */
                    final MoneyWiseLoanCategoryClass myParentClass = myParent.getCategoryTypeClass();
                    if (!MoneyWiseLoanCategoryClass.PARENT.equals(myParentClass)) {
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
    public void setDefaults(final MoneyWiseLoanCategory pParent,
                            final MoneyWiseLoanCategory pCategory) throws OceanusException {
        /* Set values */
        final MoneyWiseLoanCategoryList myList = pCategory.getList();
        final MoneyWiseLoanCategoryTypeList myTypes
                = getEditSet().getDataList(MoneyWiseStaticDataType.LOANTYPE, MoneyWiseLoanCategoryTypeList.class);
        pCategory.setCategoryType(myTypes.findItemByClass(pParent == null
                ? MoneyWiseLoanCategoryClass.PARENT
                : MoneyWiseLoanCategoryClass.LOAN));
        pCategory.setParentCategory(pParent);
        pCategory.setSubCategoryName(getUniqueName(myList, pParent));
    }
}
