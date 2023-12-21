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
package net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.base;

import java.util.ResourceBundle;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jtethys.resource.TethysBundleId;
import net.sourceforge.joceanus.jtethys.resource.TethysBundleLoader;

/**
 * Resource IDs for MoneyWise Analysis Base Fields.
 */
public enum MoneyWiseAnalysisBaseResource
        implements TethysBundleId, MetisDataFieldId {
    /**
     * Bucket Owner.
     */
    BUCKET_OWNER("Bucket.Owner"),

    /**
     * Bucket History.
     */
    BUCKET_HISTORY("Bucket.History"),

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
     * Event Rates.
     */
    EVENT_RATES("Event.Rates"),

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
     * The Resource Loader.
     */
    private static final TethysBundleLoader LOADER = TethysBundleLoader.getPackageLoader(MoneyWiseAnalysisBaseResource.class.getCanonicalName(),
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
    MoneyWiseAnalysisBaseResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    /**
     * Constructor.
     * @param pResource the underlying resource
     */
    MoneyWiseAnalysisBaseResource(final TethysBundleId pResource) {
        theKeyName = null;
        theValue = pResource.getValue();
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
}
