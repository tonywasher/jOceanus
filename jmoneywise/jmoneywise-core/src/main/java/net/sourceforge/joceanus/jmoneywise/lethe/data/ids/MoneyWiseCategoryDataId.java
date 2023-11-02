/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.data.ids;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.CashCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.CategoryBase;
import net.sourceforge.joceanus.jmoneywise.lethe.data.DepositCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.LoanCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseDataResource;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory;
import net.sourceforge.joceanus.jprometheus.lethe.data.ids.PrometheusDataFieldId;
import net.sourceforge.joceanus.jprometheus.lethe.data.PrometheusDataResourceX;

/**
 * Category DataIds.
 */
public enum MoneyWiseCategoryDataId
        implements PrometheusDataFieldId {
    /**
     * Name.
     */
    NAME(PrometheusDataResourceX.DATAITEM_FIELD_NAME, CategoryBase.FIELD_NAME),

    /**
     * Description.
     */
    DESC(PrometheusDataResourceX.DATAITEM_FIELD_DESC, CategoryBase.FIELD_DESC),

    /**
     * Parent.
     */
    PARENT(PrometheusDataResourceX.DATAGROUP_PARENT, CategoryBase.FIELD_PARENT),

    /**
     * SubCategory.
     */
    SUBCAT(MoneyWiseDataResource.CATEGORY_SUBCAT, CategoryBase.FIELD_SUBCAT),

    /**
     * DepositCategoryType.
     */
    DEPOSITCATTYPE(MoneyWiseDataType.DEPOSITTYPE, DepositCategory.FIELD_CATTYPE),

    /**
     * CashCategoryType.
     */
    CASHCATTYPE(MoneyWiseDataType.CASHTYPE, CashCategory.FIELD_CATTYPE),

    /**
     * LoanCategoryType.
     */
    LOANCATTYPE(MoneyWiseDataType.LOANTYPE, LoanCategory.FIELD_CATTYPE),

    /**
     * TransactionCategoryType.
     */
    TRANSCATTYPE(MoneyWiseDataType.TRANSTYPE, TransactionCategory.FIELD_CATTYPE);

    /**
     * The Value.
     */
    private final String theValue;

    /**
     * The Lethe Field.
     */
    private final MetisLetheField theField;

    /**
     * Constructor.
     * @param pKeyName the key name
     * @param pField the lethe field
     */
    MoneyWiseCategoryDataId(final MetisDataFieldId pKeyName,
                            final MetisLetheField pField) {
        theValue = pKeyName.getId();
        theField = pField;
    }

    @Override
    public String getId() {
        return theValue;
    }

    @Override
    public String toString() {
        return getId();
    }

    @Override
    public MetisLetheField getLetheField() {
        return theField;
    }
}
