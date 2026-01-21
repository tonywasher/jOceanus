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
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCategoryBase;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransCategory.MoneyWiseTransCategoryDataMap;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransCategory.MoneyWiseTransCategoryList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransCategoryClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransCategoryType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransCategoryType.MoneyWiseTransCategoryTypeList;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;

/**
 * Validator for TransCategory.
 */
public class MoneyWiseValidateTransCategory
        extends MoneyWiseValidateCategory<MoneyWiseTransCategory> {
    /**
     * Different Parent Error.
     */
    private static final String ERROR_DIFFPARENT = MoneyWiseBasicResource.TRANSCATEGORY_ERROR_DIFFPARENT.getValue();

    @Override
    public void validate(final PrometheusDataItem pCategory) {
        /* Validate the base */
        super.validate(pCategory);

        /* Access details */
        final MoneyWiseTransCategory myCategory = (MoneyWiseTransCategory) pCategory;
        final MoneyWiseTransCategoryList myList = myCategory.getList();
        final MoneyWiseTransCategoryType myCatType = myCategory.getCategoryType();
        final MoneyWiseTransCategory myParent = myCategory.getParentCategory();
        final String myName = myCategory.getName();

        /* EventCategoryType must be non-null */
        if (myCatType == null) {
            pCategory.addError(PrometheusDataItem.ERROR_MISSING, MoneyWiseStaticDataType.TRANSTYPE);
        } else {
            /* Access the class */
            final MoneyWiseTransCategoryClass myClass = myCatType.getCategoryClass();

            /* EventCategoryType must be enabled */
            if (!myCatType.getEnabled()) {
                pCategory.addError(PrometheusDataItem.ERROR_DISABLED, MoneyWiseStaticDataType.TRANSTYPE);
            }

            /* If the CategoryType is singular */
            if (myClass.isSingular()) {
                /* Count the elements of this class */
                final MoneyWiseTransCategoryDataMap myMap = myList.getDataMap();
                if (!myMap.validSingularCount(myClass)) {
                    pCategory.addError(PrometheusDataItem.ERROR_MULT, MoneyWiseStaticDataType.TRANSTYPE);
                }
            }

            /* Switch on the category class */
            switch (myClass) {
                case TOTALS:
                    /* If parent exists */
                    if (myParent != null) {
                        pCategory.addError(PrometheusDataItem.ERROR_EXIST, PrometheusDataResource.DATAGROUP_PARENT);
                    }
                    break;
                case INCOMETOTALS:
                case EXPENSETOTALS:
                case SECURITYPARENT:
                    /* Check parent */
                    if (myParent == null) {
                        pCategory.addError(PrometheusDataItem.ERROR_MISSING, PrometheusDataResource.DATAGROUP_PARENT);
                    } else if (!myParent.isCategoryClass(MoneyWiseTransCategoryClass.TOTALS)) {
                        pCategory.addError(ERROR_BADPARENT, PrometheusDataResource.DATAGROUP_PARENT);
                    }
                    break;
                default:
                    /* Check parent requirement */
                    final boolean isTransfer = myClass == MoneyWiseTransCategoryClass.TRANSFER;
                    final boolean hasParent = myParent != null;
                    if (hasParent == isTransfer) {
                        if (isTransfer) {
                            pCategory.addError(PrometheusDataItem.ERROR_EXIST, PrometheusDataResource.DATAGROUP_PARENT);
                        } else {
                            pCategory.addError(PrometheusDataItem.ERROR_MISSING, PrometheusDataResource.DATAGROUP_PARENT);
                        }
                    } else if (hasParent) {
                        /* Check validity of parent */
                        final MoneyWiseTransCategoryClass myParentClass = myParent.getCategoryTypeClass();
                        if (!myParentClass.canParentCategory()) {
                            pCategory.addError(ERROR_BADPARENT, PrometheusDataResource.DATAGROUP_PARENT);
                        }
                        if (myParentClass.isIncome() != myClass.isIncome()
                                || myParentClass.isSecurityTransfer() != myClass.isSecurityTransfer()) {
                            pCategory.addError(ERROR_DIFFPARENT, PrometheusDataResource.DATAGROUP_PARENT);
                        }

                        /* Check that name reflects parent */
                        if (myName != null && !myName.startsWith(myParent.getName() + MoneyWiseCategoryBase.STR_SEP)) {
                            pCategory.addError(ERROR_MATCHPARENT, PrometheusDataResource.DATAGROUP_PARENT);
                        }
                    }
                    break;
            }
        }

        /* Set validation flag */
        if (!pCategory.hasErrors()) {
            pCategory.setValidEdit();
        }
    }

    @Override
    public void setDefaults(final MoneyWiseTransCategory pParent,
                            final MoneyWiseTransCategory pCategory) throws OceanusException {
        /* Set values */
        final MoneyWiseTransCategoryList myList = pCategory.getList();
        final MoneyWiseTransCategoryTypeList myTypes
                = getEditSet().getDataList(MoneyWiseStaticDataType.TRANSTYPE, MoneyWiseTransCategoryTypeList.class);
        final MoneyWiseTransCategoryClass myParentClass = pParent == null ? null : pParent.getCategoryTypeClass();
        final MoneyWiseTransCategoryClass myNewClass;
        if (myParentClass == null || myParentClass.isTotals()) {
            myNewClass = MoneyWiseTransCategoryClass.EXPENSETOTALS;
        } else if (myParentClass.isIncome()) {
            myNewClass = MoneyWiseTransCategoryClass.TAXEDINCOME;
        } else if (myParentClass.isTransfer()) {
            myNewClass = MoneyWiseTransCategoryClass.STOCKSPLIT;
        } else {
            myNewClass = MoneyWiseTransCategoryClass.EXPENSE;
        }
        pCategory.setCategoryType(myTypes.findItemByClass(myNewClass));
        pCategory.setParentCategory(pParent);
        pCategory.setSubCategoryName(getUniqueName(myList, pParent));
    }
}
