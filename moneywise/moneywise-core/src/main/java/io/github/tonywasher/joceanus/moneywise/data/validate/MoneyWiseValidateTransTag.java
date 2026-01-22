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
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseDataValidator.MoneyWiseDataValidatorDefaults;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransTag;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransTag.MoneyWiseTagDataMap;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransTag.MoneyWiseTransTagList;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataInfoLinkSet;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataItem;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataResource;
import io.github.tonywasher.joceanus.prometheus.views.PrometheusEditSet;

/**
 * Validator for transTag.
 */
public class MoneyWiseValidateTransTag
        implements MoneyWiseDataValidatorDefaults<MoneyWiseTransTag> {
    /**
     * New Tag name.
     */
    private static final String NAME_NEWTAG = MoneyWiseBasicResource.TRANSTAG_NEWTAG.getValue();

    @Override
    public void setEditSet(final PrometheusEditSet pEditSet) {
        /* NoOp */
    }

    @Override
    public void validate(final PrometheusDataItem pTag) {
        final MoneyWiseTransTag myTag = (MoneyWiseTransTag) pTag;
        final MoneyWiseTransTagList myList = myTag.getList();
        final String myName = myTag.getName();
        final String myDesc = myTag.getDesc();
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

    @Override
    public void setDefaults(final MoneyWiseTransTag pTag) throws OceanusException {
        /* Set values */
        final MoneyWiseTransTagList myList = pTag.getList();
        pTag.setName(getUniqueName(myList));
    }

    /**
     * Obtain unique name for new tag.
     *
     * @param pList the tagList
     * @return The new name
     */
    private String getUniqueName(final MoneyWiseTransTagList pList) {
        /* Set up base constraints */
        final String myBase = NAME_NEWTAG;
        int iNextId = 1;

        /* Loop until we found a name */
        String myName = myBase;
        while (true) {
            /* try out the name */
            if (pList.findItemByName(myName) == null) {
                return myName;
            }

            /* Build next name */
            myName = myBase.concat(Integer.toString(iNextId++));
        }
    }
}
