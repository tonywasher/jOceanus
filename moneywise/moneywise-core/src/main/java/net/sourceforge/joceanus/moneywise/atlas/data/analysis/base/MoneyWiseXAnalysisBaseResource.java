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
package net.sourceforge.joceanus.moneywise.atlas.data.analysis.base;

import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.oceanus.resource.OceanusBundleId;
import net.sourceforge.joceanus.oceanus.resource.OceanusBundleLoader;

import java.util.EnumMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Resource IDs for MoneyWise Analysis Base Fields.
 */
public enum MoneyWiseXAnalysisBaseResource
        implements OceanusBundleId, MetisDataFieldId {
    /**
     * Bucket Owner.
     */
    BUCKET_OWNER("Bucket.Owner"),

    /**
     * Bucket History.
     */
    BUCKET_HISTORY("Bucket.History"),

    /**
     * Event.
     */
    EVENT("Event.Name"),

    /**
     * Event.
     */
    EVENTLIST("Event.List"),

    /**
     * Event Id.
     */
    EVENT_ID("Event.Id"),

    /**
     * Event Date.
     */
    EVENT_DATE("Event.Date"),

    /**
     * Event.Type.
     */
    EVENT_TYPE("Event.Type"),

    /**
     * Event Transaction.
     */
    EVENT_TRANS("Event.Transaction"),

    /**
     * Event Prices.
     */
    EVENT_PRICES("Event.Prices"),

    /**
     * Event ExchangeRates.
     */
    EVENT_XCHGRATES("Event.XchgRates"),

    /**
     * Event DepositRates.
     */
    EVENT_DEPRATES("Event.DepRates"),

    /**
     * Event OpeningBalances.
     */
    EVENT_BALANCES("Event.Balances"),

    /**
     * SnapShot Event.
     */
    SNAPSHOT_EVENT("SnapShot.Event"),

    /**
     * SnapShot Values.
     */
    SNAPSHOT_VALUES("SnapShot.Values"),

    /**
     * SnapShot Previous.
     */
    SNAPSHOT_PREV("SnapShot.Previous"),

    /**
     * History Range.
     */
    HISTORY_RANGE("History.Range"),

    /**
     * History SnapShot List.
     */
    HISTORY_LIST("History.List"),

    /**
     * History SnapShot Map.
     */
    HISTORY_MAP("History.Map"),

    /**
     * Initial Values.
     */
    HISTORY_INITIAL("History.Initial"),

    /**
     * SnapShot Previous.
     */
    HISTORY_VALUES("History.Values");

    /**
     * The Name Map.
     */
    private static final Map<MoneyWiseXAnalysisDataType, OceanusBundleId> NAME_MAP = buildNameMap();

    /**
     * The List Map.
     */
    private static final Map<MoneyWiseXAnalysisDataType, OceanusBundleId> LIST_MAP = buildListMap();

    /**
     * The Resource Loader.
     */
    private static final OceanusBundleLoader LOADER = OceanusBundleLoader.getLoader(MoneyWiseXAnalysisBaseResource.class.getCanonicalName(),
            ResourceBundle::getBundle);

    /**
     * The Id.
     */
    private final String theKeyName;

    /**
     * The Value.
     */
    private String theValue;

    /**
     * Constructor.
     * @param pKeyName the key name
     */
    MoneyWiseXAnalysisBaseResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "MoneyWise.analysis";
    }

    @Override
    public String getValue() {
        /* If we have not initialised the value */
        if (theValue == null) {
            /* Derive the value */
            theValue = LOADER.getValue(this);
        }

        /* return the value */
        return theValue;
    }

    @Override
    public String getId() {
        return getValue();
    }

    /**
     * Build name map.
     * @return the map
     */
    private static Map<MoneyWiseXAnalysisDataType, OceanusBundleId> buildNameMap() {
        /* Create the map and return it */
        final Map<MoneyWiseXAnalysisDataType, OceanusBundleId> myMap = new EnumMap<>(MoneyWiseXAnalysisDataType.class);
        myMap.put(MoneyWiseXAnalysisDataType.EVENT, EVENT);
        return myMap;
    }

    /**
     * Obtain key for data item.
     * @param pValue the Value
     * @return the resource key
     */
    static OceanusBundleId getKeyForDataType(final MoneyWiseXAnalysisDataType pValue) {
        return OceanusBundleLoader.getKeyForEnum(NAME_MAP, pValue);
    }

    /**
     * Build list map.
     * @return the map
     */
    private static Map<MoneyWiseXAnalysisDataType, OceanusBundleId> buildListMap() {
        /* Create the map and return it */
        final Map<MoneyWiseXAnalysisDataType, OceanusBundleId> myMap = new EnumMap<>(MoneyWiseXAnalysisDataType.class);
        myMap.put(MoneyWiseXAnalysisDataType.EVENT, EVENTLIST);
        return myMap;
    }

    /**
     * Obtain key for data list.
     * @param pValue the Value
     * @return the resource key
     */
    static OceanusBundleId getKeyForDataList(final MoneyWiseXAnalysisDataType pValue) {
        return OceanusBundleLoader.getKeyForEnum(LIST_MAP, pValue);
    }
}
