/*
 * Prometheus: Application Framework
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
package net.sourceforge.joceanus.prometheus.service.sheet;

import io.github.tonywasher.joceanus.oceanus.resource.OceanusBundleId;
import io.github.tonywasher.joceanus.oceanus.resource.OceanusBundleLoader;

import java.util.EnumMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Resource IDs for Sheet.
 */
public enum PrometheusSheetResource
        implements OceanusBundleId {
    /**
     * WorkBook ExcelXLS.
     */
    WORKBOOK_EXCELXLS("workBook.EXCELXLS"),

    /**
     * WorkBook ExcelXLSX.
     */
    WORKBOOK_EXCELXLSX("workBook.EXCELXLSX"),

    /**
     * WorkBook Oasis.
     */
    WORKBOOK_OASIS("workBook.OASIS");

    /**
     * The WorkBook Map.
     */
    private static final Map<PrometheusSheetWorkBookType, OceanusBundleId> WORKBOOK_MAP = buildWorkBookMap();

    /**
     * The Resource Loader.
     */
    private static final OceanusBundleLoader LOADER = OceanusBundleLoader.getLoader(PrometheusSheetResource.class.getCanonicalName(),
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
     *
     * @param pKeyName the key name
     */
    PrometheusSheetResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "Prometheus.sheet";
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

    /**
     * Build workBook map.
     *
     * @return the map
     */
    private static Map<PrometheusSheetWorkBookType, OceanusBundleId> buildWorkBookMap() {
        /* Create the map and return it */
        final Map<PrometheusSheetWorkBookType, OceanusBundleId> myMap = new EnumMap<>(PrometheusSheetWorkBookType.class);
        myMap.put(PrometheusSheetWorkBookType.EXCELXLS, WORKBOOK_EXCELXLS);
        myMap.put(PrometheusSheetWorkBookType.EXCELXLSX, WORKBOOK_EXCELXLSX);
        myMap.put(PrometheusSheetWorkBookType.OASIS, WORKBOOK_OASIS);
        return myMap;
    }

    /**
     * Obtain key for workBookType.
     *
     * @param pType the Type
     * @return the resource key
     */
    protected static OceanusBundleId getKeyForWorkBook(final PrometheusSheetWorkBookType pType) {
        return OceanusBundleLoader.getKeyForEnum(WORKBOOK_MAP, pType);
    }
}
