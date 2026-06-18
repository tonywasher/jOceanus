/*
 * Prometheus: Application Framework
 * Copyright 2026. Tony Washer
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

package io.github.tonywasher.joceanus.prometheus.data;

import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import io.github.tonywasher.joceanus.metis.list.MetisListKey;
import io.github.tonywasher.joceanus.oceanus.resource.OceanusBundleId;

/**
 * Cryptography Data Enum Types.
 */
public enum PrometheusCryptographyDataType
        implements MetisListKey, MetisDataFieldId {
    /**
     * ControlKey.
     */
    CONTROLKEY(1),

    /**
     * ControlKeySet.
     */
    CONTROLKEYSET(2),

    /**
     * DataKeySet.
     */
    DATAKEYSET(3),

    /**
     * ControlData.
     */
    CONTROLDATA(4);

    /**
     * Maximum keyId.
     */
    public static final Integer MAXKEYID = CONTROLDATA.getItemKey();

    /**
     * The list key.
     */
    private final Integer theKey;

    /**
     * The String name.
     */
    private String theName;

    /**
     * The list name.
     */
    private String theListName;

    /**
     * Constructor.
     *
     * @param pKey the key
     */
    PrometheusCryptographyDataType(final Integer pKey) {
        theKey = pKey;
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = bundleIdForCryptoItem(this).getValue();
        }

        /* return the name */
        return theName;
    }

    @Override
    public String getItemName() {
        return toString();
    }

    @Override
    public String getListName() {
        /* If we have not yet loaded the name */
        if (theListName == null) {
            /* Load the name */
            theListName = bundleIdForCryptoList(this).getValue();
        }

        /* return the list name */
        return theListName;
    }

    @Override
    public Integer getItemKey() {
        return theKey;
    }

    @Override
    public String getId() {
        return toString();
    }

    /**
     * Obtain the resource bundleId for the dataType List.
     *
     * @param pType the dataType
     * @return the resource bundleId
     */
    private static OceanusBundleId bundleIdForCryptoItem(final PrometheusCryptographyDataType pType) {
        /* Create the map and return it */
        return switch (pType) {
            case CONTROLKEY -> PrometheusDataResource.CONTROLKEY_NAME;
            case CONTROLKEYSET -> PrometheusDataResource.CONTROLKEYSET_NAME;
            case DATAKEYSET -> PrometheusDataResource.DATAKEYSET_NAME;
            case CONTROLDATA -> PrometheusDataResource.CONTROLDATA_NAME;
            default -> throw new IllegalArgumentException();
        };
    }

    /**
     * Obtain the resource bundleId for the dataType List.
     *
     * @param pType the dataType
     * @return the resource bundleId
     */
    private static OceanusBundleId bundleIdForCryptoList(final PrometheusCryptographyDataType pType) {
        /* Create the map and return it */
        return switch (pType) {
            case CONTROLKEY -> PrometheusDataResource.CONTROLKEY_LIST;
            case CONTROLKEYSET -> PrometheusDataResource.CONTROLKEYSET_LIST;
            case DATAKEYSET -> PrometheusDataResource.DATAKEYSET_LIST;
            case CONTROLDATA -> PrometheusDataResource.CONTROLDATA_LIST;
            default -> throw new IllegalArgumentException();
        };
    }
}
