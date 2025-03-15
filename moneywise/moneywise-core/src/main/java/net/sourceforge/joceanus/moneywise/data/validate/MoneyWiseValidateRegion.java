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
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataValidator.MoneyWiseDataValidatorDefaults;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseRegion;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseRegion.MoneyWiseRegionDataMap;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseRegion.MoneyWiseRegionList;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataInfoLinkSet;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;

/**
 * Validator for region.
 */
public class MoneyWiseValidateRegion
    implements MoneyWiseDataValidatorDefaults<MoneyWiseRegion> {
    /**
     * New Region name.
     */
    private static final String NAME_NEWREGION = MoneyWiseBasicResource.REGION_NEWREGION.getValue();

    @Override
    public void setEditSet(final PrometheusEditSet pEditSet) {
        /* NoOp */
    }

    @Override
    public void validate(final PrometheusDataItem pRegion) {
        final MoneyWiseRegion myRegion = (MoneyWiseRegion) pRegion;
        final MoneyWiseRegionList myList = myRegion.getList();
        final String myName = myRegion.getName();
        final String myDesc = myRegion.getDesc();
        final MoneyWiseRegionDataMap myMap = myList.getDataMap();

        /* Name must be non-null */
        if (myName == null) {
            pRegion.addError(PrometheusDataItem.ERROR_MISSING, PrometheusDataResource.DATAITEM_FIELD_NAME);

            /* Else check the name */
        } else {
            /* The description must not be too long */
            if (myName.length() > PrometheusDataItem.NAMELEN) {
                pRegion.addError(PrometheusDataItem.ERROR_LENGTH, PrometheusDataResource.DATAITEM_FIELD_NAME);
            }

            /* Check that the name is unique */
            if (!myMap.validNameCount(myName)) {
                pRegion.addError(PrometheusDataItem.ERROR_DUPLICATE, PrometheusDataResource.DATAITEM_FIELD_NAME);
            }

            /* Check that the name does not contain invalid characters */
            if (myName.contains(PrometheusDataInfoLinkSet.ITEM_SEP)) {
                pRegion.addError(PrometheusDataItem.ERROR_INVALIDCHAR, PrometheusDataResource.DATAITEM_FIELD_NAME);
            }
        }

        /* Check description length */
        if (myDesc != null
                && myDesc.length() > PrometheusDataItem.DESCLEN) {
            pRegion.addError(PrometheusDataItem.ERROR_LENGTH, PrometheusDataResource.DATAITEM_FIELD_DESC);
        }

        /* Set validation flag */
        if (!pRegion.hasErrors()) {
            pRegion.setValidEdit();
        }
    }

    @Override
    public void setDefaults(final MoneyWiseRegion pRegion) throws OceanusException {
        /* Set values */
        final MoneyWiseRegionList myList = pRegion.getList();
        pRegion.setName(getUniqueName(myList));
    }

    /**
     * Obtain unique name for new tag.
     * @param pList the region list
     * @return The new name
     */
    private String getUniqueName(final MoneyWiseRegionList pList) {
        /* Set up base constraints */
        final String myBase = NAME_NEWREGION;
        int iNextId = 1;

        /* Loop until we found a name */
        String myName = myBase;
        for (;;) {
            /* try out the name */
            if (pList.findItemByName(myName) == null) {
                return myName;
            }

            /* Build next name */
            myName = myBase.concat(Integer.toString(iNextId++));
        }
    }
}
