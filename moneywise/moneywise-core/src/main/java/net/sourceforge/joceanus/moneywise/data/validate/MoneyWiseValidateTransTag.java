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

import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransTag;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransTag.MoneyWiseTagDataMap;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransTag.MoneyWiseTransTagList;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataInfoLinkSet;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValidator;

/**
 * Validator for transTag.
 */
public class MoneyWiseValidateTransTag
        implements PrometheusDataValidator<MoneyWiseTransTag> {

    @Override
    public void validate(final MoneyWiseTransTag pTag) {
        final MoneyWiseTransTagList myList = pTag.getList();
        final String myName = pTag.getName();
        final String myDesc = pTag.getDesc();
        final MoneyWiseTagDataMap myMap = myList.getDataMap();

        /* Name must be non-null */
        if (myName == null) {
            pTag.addError(PrometheusDataItem.ERROR_MISSING, PrometheusDataResource.DATAITEM_FIELD_NAME);

            /* Else check the name */
        } else {
            /* The description must not be too long */
            if (myName.length() > PrometheusDataItem.NAMELEN) {
                pTag.addError(PrometheusDataItem.ERROR_LENGTH, PrometheusDataResource.DATAITEM_FIELD_NAME);
            }

            /* Check that the name is unique */
            if (!myMap.validNameCount(myName)) {
                pTag.addError(PrometheusDataItem.ERROR_DUPLICATE, PrometheusDataResource.DATAITEM_FIELD_NAME);
            }

            /* Check that the name does not contain invalid characters */
            if (myName.contains(PrometheusDataInfoLinkSet.ITEM_SEP)) {
                pTag.addError(PrometheusDataItem.ERROR_INVALIDCHAR, PrometheusDataResource.DATAITEM_FIELD_NAME);
            }
        }

        /* Check description length */
        if (myDesc != null
                && myDesc.length() > PrometheusDataItem.DESCLEN) {
            pTag.addError(PrometheusDataItem.ERROR_LENGTH, PrometheusDataResource.DATAITEM_FIELD_DESC);
        }

        /* Set validation flag */
        if (!pTag.hasErrors()) {
            pTag.setValidEdit();
        }
    }
}
