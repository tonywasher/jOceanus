/*******************************************************************************
 * jCoeus: Peer2Peer Analysis
 * Copyright 2012,2017 Tony Washer
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
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jcoeus.ui.report;

import java.util.EnumMap;
import java.util.Map;

import net.sourceforge.joceanus.jcoeus.CoeusDataException;
import net.sourceforge.joceanus.jtethys.resource.TethysResourceBuilder;
import net.sourceforge.joceanus.jtethys.resource.TethysResourceId;

/**
 * Resource IDs for Coeus Reports.
 */
public enum CoeusReportResource
        implements TethysResourceId {
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
    private static final Map<CoeusReportType, TethysResourceId> REPORT_MAP = buildReportMap();

    /**
     * The Resource Builder.
     */
    private static final TethysResourceBuilder BUILDER = TethysResourceBuilder.getPackageResourceBuilder(CoeusDataException.class.getCanonicalName());

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
            theValue = BUILDER.getValue(this);
        }

        /* return the value */
        return theValue;
    }

    /**
     * Build report map.
     * @return the map
     */
    private static Map<CoeusReportType, TethysResourceId> buildReportMap() {
        /* Create the map and return it */
        final Map<CoeusReportType, TethysResourceId> myMap = new EnumMap<>(CoeusReportType.class);
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
    protected static TethysResourceId getKeyForReportType(final CoeusReportType pValue) {
        return TethysResourceBuilder.getKeyForEnum(REPORT_MAP, pValue);
    }
}
