/*******************************************************************************
 * jMetis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.sheet;

import java.util.EnumMap;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.MetisDataException;
import net.sourceforge.joceanus.jtethys.resource.TethysResourceBuilder;
import net.sourceforge.joceanus.jtethys.resource.TethysResourceId;

/**
 * Resource IDs for JMetis Sheet.
 */
public enum MetisSheetResource implements TethysResourceId {
    /**
     * WorkBook ExcelXLS.
     */
    WORKBOOK_EXCELXLS("workBook.EXCELXLS"),

    /**
     * WorkBook OasisODS.
     */
    WORKBOOK_OASISODS("workBook.OASISODS");

    /**
     * The WorkBook Map.
     */
    private static final Map<MetisWorkBookType, TethysResourceId> WORKBOOK_MAP = buildWorkBookMap();

    /**
     * The Resource Builder.
     */
    private static final TethysResourceBuilder BUILDER = TethysResourceBuilder.getPackageResourceBuilder(MetisDataException.class.getCanonicalName());

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
    MetisSheetResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "jMetis.sheet";
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
     * Build workBook map.
     * @return the map
     */
    private static Map<MetisWorkBookType, TethysResourceId> buildWorkBookMap() {
        /* Create the map and return it */
        final Map<MetisWorkBookType, TethysResourceId> myMap = new EnumMap<>(MetisWorkBookType.class);
        myMap.put(MetisWorkBookType.EXCELXLS, WORKBOOK_EXCELXLS);
        myMap.put(MetisWorkBookType.OASISODS, WORKBOOK_OASISODS);
        return myMap;
    }

    /**
     * Obtain key for workBookType.
     * @param pType the Type
     * @return the resource key
     */
    protected static TethysResourceId getKeyForWorkBook(final MetisWorkBookType pType) {
        return TethysResourceBuilder.getKeyForEnum(WORKBOOK_MAP, pType);
    }
}
