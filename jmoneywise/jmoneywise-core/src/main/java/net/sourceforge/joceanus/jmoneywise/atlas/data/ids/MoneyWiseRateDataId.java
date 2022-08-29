/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2022 Tony Washer
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
import net.sourceforge.joceanus.jmoneywise.lethe.data.DepositRate;
import net.sourceforge.joceanus.jmoneywise.lethe.data.ExchangeRate;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseDataResource;
import net.sourceforge.joceanus.jmoneywise.lethe.views.MoneyWiseViewResource;
import net.sourceforge.joceanus.jmoneywise.lethe.views.SpotExchangeRate;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataFieldId;

/**
 * Rate DataIds.
 */
public enum MoneyWiseRateDataId
        implements PrometheusDataFieldId {
    /**
     * Deposit.
     */
    DEPOSIT(MoneyWiseDataType.DEPOSIT, DepositRate.FIELD_DEPOSIT),

    /**
     * Rate.
     */
    RATE(MoneyWiseDataResource.MONEYWISEDATA_FIELD_RATE, DepositRate.FIELD_RATE),

    /**
     * Bonus.
     */
    BONUS(MoneyWiseDataResource.DEPOSITRATE_BONUS, DepositRate.FIELD_BONUS),

    /**
     * EndDate.
     */
    ENDDATE(MoneyWiseDataResource.DEPOSITRATE_ENDDATE, DepositRate.FIELD_ENDDATE),

    /**
     * Date.
     */
    DATE(MoneyWiseDataResource.MONEYWISEDATA_FIELD_DATE, ExchangeRate.FIELD_DATE),

    /**
     * From.
     */
    FROM(MoneyWiseDataResource.XCHGRATE_FROM, ExchangeRate.FIELD_FROM),

    /**
     * To.
     */
    TO(MoneyWiseDataResource.XCHGRATE_TO, ExchangeRate.FIELD_TO),

    /**
     * ExchangeRate.
     */
    XCHGRATE(MoneyWiseDataResource.XCHGRATE_RATE, ExchangeRate.FIELD_RATE),

    /**
     * PreviousDate.
     */
    PREVDATE(MoneyWiseViewResource.SPOTEVENT_PREVDATE, SpotExchangeRate.FIELD_PREVDATE),

    /**
     * PreviousRate.
     */
    PREVRATE(MoneyWiseViewResource.SPOTPRICE_PREVPRICE, SpotExchangeRate.FIELD_PREVRATE);

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
    MoneyWiseRateDataId(final MetisDataFieldId pKeyName,
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

