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

import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCategoryBase;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCategoryBase.MoneyWiseCategoryBaseList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCategoryBase.MoneyWiseCategoryDataMap;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValidator;

/**
 * Validator for categoryBase.
 * @param <T> the category type
 */
public abstract class MoneyWiseValidateCategory<T extends MoneyWiseCategoryBase>
        implements PrometheusDataValidator<T> {

    @Override
    public void validate(final T pCategory) {
        final MoneyWiseCategoryBaseList<?> myList = pCategory.getList();
        final String myName = pCategory.getName();
        final String myDesc = pCategory.getDesc();
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
                final String mySubName = pCategory.getSubCategory();
                pCategory.addError(PrometheusDataItem.ERROR_DUPLICATE, (mySubName == null)
                        ? PrometheusDataResource.DATAITEM_FIELD_NAME
                        : MoneyWiseBasicResource.CATEGORY_SUBCAT);
            }
        }

        /* Check description length */
        if ((myDesc != null) && (myDesc.length() > PrometheusDataItem.DESCLEN)) {
            pCategory.addError(PrometheusDataItem.ERROR_LENGTH, PrometheusDataResource.DATAITEM_FIELD_DESC);
        }
    }
}
