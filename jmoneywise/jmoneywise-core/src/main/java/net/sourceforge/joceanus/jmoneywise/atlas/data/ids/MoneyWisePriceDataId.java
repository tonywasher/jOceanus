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
package net.sourceforge.joceanus.jmoneywise.atlas.data.ids;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseDataResource;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityPrice;
import net.sourceforge.joceanus.jmoneywise.lethe.views.MoneyWiseViewResource;
import net.sourceforge.joceanus.jmoneywise.lethe.views.SpotSecurityPrice;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataFieldId;

/**
 * Price DataIds.
 */
public enum MoneyWisePriceDataId
        implements PrometheusDataFieldId {
    /**
     * Security.
     */
    SECURITY(MoneyWiseDataType.SECURITY, SecurityPrice.FIELD_SECURITY),

    /**
     * Date.
     */
    DATE(MoneyWiseDataResource.MONEYWISEDATA_FIELD_DATE, SecurityPrice.FIELD_DATE),

    /**
     * Price.
     */
    PRICE(MoneyWiseDataResource.MONEYWISEDATA_FIELD_PRICE, SecurityPrice.FIELD_PRICE),

    /**
     * PreviousDate.
     */
    PREVDATE(MoneyWiseViewResource.SPOTEVENT_PREVDATE, SpotSecurityPrice.FIELD_PREVDATE),

    /**
     * PreviousPrice.
     */
    PREVPRICE(MoneyWiseViewResource.SPOTPRICE_PREVPRICE, SpotSecurityPrice.FIELD_PREVPRICE);

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
    MoneyWisePriceDataId(final MetisDataFieldId pKeyName,
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
