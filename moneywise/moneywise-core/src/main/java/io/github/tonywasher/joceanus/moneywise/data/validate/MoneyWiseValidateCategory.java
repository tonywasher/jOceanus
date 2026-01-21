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
package io.github.tonywasher.joceanus.moneywise.data.validate;

import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseCategoryBase;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseCategoryBase.MoneyWiseCategoryBaseList;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseCategoryBase.MoneyWiseCategoryDataMap;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseDataValidator.MoneyWiseDataValidatorCategory;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataItem;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataResource;
import io.github.tonywasher.joceanus.prometheus.views.PrometheusEditSet;

/**
 * Validator for categoryBase.
 *
 * @param <T> the category type
 */
public abstract class MoneyWiseValidateCategory<T extends MoneyWiseCategoryBase>
        implements MoneyWiseDataValidatorCategory<T> {
    /**
     * Invalid Parent Error.
     */
    static final String ERROR_BADPARENT = MoneyWiseBasicResource.CATEGORY_ERROR_BADPARENT.getValue();

    /**
     * NonMatching Parent Error.
     */
    static final String ERROR_MATCHPARENT = MoneyWiseBasicResource.CATEGORY_ERROR_MATCHPARENT.getValue();

    /**
     * New parent name.
     */
    private static final String NAME_NEWPARENT = MoneyWiseBasicResource.CATEGORY_NEWPARENT.getValue();

    /**
     * New Category name.
     */
    private static final String NAME_NEWCATEGORY = MoneyWiseBasicResource.CATEGORY_NEWCAT.getValue();

    /**
     * Set the editSet.
     */
    private PrometheusEditSet theEditSet;

    @Override
    public void setEditSet(final PrometheusEditSet pEditSet) {
        theEditSet = pEditSet;
    }

    /**
     * Obtain the editSet.
     *
     * @return the editSet
     */
    PrometheusEditSet getEditSet() {
        return theEditSet;
    }

    @Override
    public void validate(final PrometheusDataItem pCategory) {
        final MoneyWiseCategoryBase myCategory = (MoneyWiseCategoryBase) pCategory;
        final MoneyWiseCategoryBaseList<?> myList = myCategory.getList();
        final String myName = myCategory.getName();
        final String myDesc = myCategory.getDesc();
        final MoneyWiseCategoryDataMap<?> myMap = myList.getDataMap();

        /* Name must be non-null */
        if (myName == null) {
            pCategory.addError(PrometheusDataItem.ERROR_MISSING, PrometheusDataResource.DATAITEM_FIELD_NAME);

            /* Check that the name is valid */
        } else {
            /* The name must not be too long */
            if (myName.length() > PrometheusDataItem.NAMELEN) {
                pCategory.addError(PrometheusDataItem.ERROR_LENGTH, PrometheusDataResource.DATAITEM_FIELD_NAME);
            }

            /* The name must be unique */
            if (!myMap.validNameCount(myName)) {
                final String mySubName = myCategory.getSubCategory();
                pCategory.addError(PrometheusDataItem.ERROR_DUPLICATE, (mySubName == null)
                        ? PrometheusDataResource.DATAITEM_FIELD_NAME
                        : MoneyWiseBasicResource.CATEGORY_SUBCAT);
            }
        }

        /* Check description length */
        if (myDesc != null
                && myDesc.length() > PrometheusDataItem.DESCLEN) {
            pCategory.addError(PrometheusDataItem.ERROR_LENGTH, PrometheusDataResource.DATAITEM_FIELD_DESC);
        }
    }

    /**
     * Obtain unique name for new category.
     *
     * @param pList   the owning list
     * @param pParent the parent category
     * @return The new name
     */
    String getUniqueName(final MoneyWiseCategoryBaseList<T> pList,
                         final T pParent) {
        /* Set up base constraints */
        final String myBase = pParent == null
                ? ""
                : pParent.getName() + MoneyWiseCategoryBase.STR_SEP;
        final String myCore = pParent == null
                ? NAME_NEWPARENT
                : NAME_NEWCATEGORY;
        int iNextId = 1;

        /* Loop until we found a name */
        String myName = myCore;
        for (; ; ) {
            /* try out the name */
            if (pList.findItemByName(myBase + myName) == null) {
                return myName;
            }

            /* Build next name */
            myName = myCore.concat(Integer.toString(iNextId++));
        }
    }
}
