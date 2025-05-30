/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2025 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.sourceforge.joceanus.coeus.ui.report;

import net.sourceforge.joceanus.coeus.data.CoeusResource;
import net.sourceforge.joceanus.oceanus.resource.OceanusBundleId;
import net.sourceforge.joceanus.oceanus.resource.OceanusBundleLoader;

import java.util.EnumMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Resource IDs for Coeus Reports.
 */
public enum CoeusReportResource
        implements OceanusBundleId {
    /**
     * BalanceSheet ReportType.
     */
    REPORTTYPE_BALANCESHEET("BalanceSheet"),

    /**
     * LoanBook ReportType.
     */
    REPORTTYPE_LOANBOOK("LoanBook"),

    /**
     * Annual ReportType.
     */
    REPORTTYPE_ANNUAL("Annual");

    /**
     * The Report Map.
     */
    private static final Map<CoeusReportType, OceanusBundleId> REPORT_MAP = buildReportMap();

    /**
     * The Resource Loader.
     */
    private static final OceanusBundleLoader LOADER = OceanusBundleLoader.getLoader(CoeusResource.class.getCanonicalName(),
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
    CoeusReportResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "coeus.report";
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
     * Build report map.
     * @return the map
     */
    private static Map<CoeusReportType, OceanusBundleId> buildReportMap() {
        /* Create the map and return it */
        final Map<CoeusReportType, OceanusBundleId> myMap = new EnumMap<>(CoeusReportType.class);
        myMap.put(CoeusReportType.BALANCESHEET, REPORTTYPE_BALANCESHEET);
        myMap.put(CoeusReportType.LOANBOOK, REPORTTYPE_LOANBOOK);
        myMap.put(CoeusReportType.ANNUAL, REPORTTYPE_ANNUAL);
        return myMap;
    }

    /**
     * Obtain key for report type.
     * @param pValue the Value
     * @return the resource key
     */
    static OceanusBundleId getKeyForReportType(final CoeusReportType pValue) {
        return OceanusBundleLoader.getKeyForEnum(REPORT_MAP, pValue);
    }
}
