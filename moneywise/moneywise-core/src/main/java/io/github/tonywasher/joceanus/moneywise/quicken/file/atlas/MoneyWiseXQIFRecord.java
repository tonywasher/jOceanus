/*
 * MoneyWise: Finance Application
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
package io.github.tonywasher.joceanus.moneywise.quicken.file.atlas;

import io.github.tonywasher.joceanus.moneywise.quicken.definitions.MoneyWiseQLineType;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * XQIF File record representation.
 *
 * @param <T> the line type
 */
public abstract class MoneyWiseXQIFRecord<T extends Enum<T> & MoneyWiseQLineType> {
    /**
     * Quicken Command.
     */
    public static final String XQIF_CMD = "!";

    /**
     * Quicken Item type.
     */
    public static final String XQIF_ITEMTYPE = XQIF_CMD + "Type:";

    /**
     * Set option.
     */
    public static final String XQIF_SETOPT = XQIF_CMD + "Option:";

    /**
     * Clear option.
     */
    public static final String XQIF_CLROPT = XQIF_CMD + "Clear:";

    /**
     * Quicken End of Item indicator.
     */
    public static final String XQIF_EOI = "^";

    /**
     * Quicken New line.
     */
    public static final char XQIF_EOL = '\n';

    /**
     * Class of lines.
     */
    private final Class<T> theClass;

    /**
     * Map of lines.
     */
    private final Map<T, MoneyWiseXQIFLine<T>> theMap;

    /**
     * List of subRecords.
     */
    private List<MoneyWiseXQIFRecord<T>> theSubList;

    /**
     * Constructor.
     *
     * @param pClass the class of the lines
     */
    protected MoneyWiseXQIFRecord(final Class<T> pClass) {
        /* Record the class and file */
        theClass = pClass;

        /* Create the map */
        theMap = new EnumMap<>(pClass);
    }

    /**
     * Obtain line class.
     *
     * @return the line class
     */
    protected Class<T> getLineClass() {
        return theClass;
    }

    /**
     * Obtain line map.
     *
     * @return the line map
     */
    protected Map<T, MoneyWiseXQIFLine<T>> getLineMap() {
        return theMap;
    }

    /**
     * Obtain subList.
     *
     * @return the subList
     */
    protected List<MoneyWiseXQIFRecord<T>> getSubList() {
        return theSubList;
    }

    /**
     * Obtain line for record.
     *
     * @param pLineType the line type
     * @return the record
     */
    protected MoneyWiseXQIFLine<T> getLine(final T pLineType) {
        return theMap.get(pLineType);
    }

    /**
     * Add line to map.
     *
     * @param pLine the Line to add
     */
    protected void addLine(final MoneyWiseXQIFLine<T> pLine) {
        /* Add to the map */
        theMap.put(pLine.getLineType(), pLine);
    }

    /**
     * Add subRecord to list.
     *
     * @param pRecord the record to add
     */
    protected void addRecord(final MoneyWiseXQIFRecord<T> pRecord) {
        /* Allocate list if required */
        if (theSubList == null) {
            theSubList = new ArrayList<>();
        }

        /* Add to the list */
        theSubList.add(pRecord);
    }

    /**
     * Format record.
     *
     * @param pFormatter the data formatter
     * @param pBuilder   the string builder
     */
    public void formatRecord(final OceanusDataFormatter pFormatter,
                             final StringBuilder pBuilder) {
        /* Format the standard lines */
        formatLines(pFormatter, pBuilder);

        /* If we have subLists */
        if (theSubList != null) {
            /* Loop through the subList */
            for (MoneyWiseXQIFRecord<T> myRecord : theSubList) {
                /* Format the lines of the subRecord */
                myRecord.formatLines(pFormatter, pBuilder);
            }
        }

        /* Add the end of record indicator */
        pBuilder.append(XQIF_EOI);
        pBuilder.append(XQIF_EOL);
    }

    /**
     * Format lines.
     *
     * @param pFormatter the data formatter
     * @param pBuilder   the string builder
     */
    private void formatLines(final OceanusDataFormatter pFormatter,
                             final StringBuilder pBuilder) {
        /* Loop through the map in ordinal order */
        for (T myType : theClass.getEnumConstants()) {
            /* Look up value in the map */
            final MoneyWiseXQIFLine<T> myLine = theMap.get(myType);
            if (myLine != null) {
                /* Format the line */
                myLine.formatLine(pFormatter, pBuilder);
                pBuilder.append(XQIF_EOL);
            }
        }
    }

    /**
     * Format item type.
     *
     * @param pItemType the item type
     * @param pBuilder  the string builder
     */
    public static void formatItemType(final String pItemType,
                                      final StringBuilder pBuilder) {
        /* Format the item type */
        pBuilder.append(XQIF_ITEMTYPE);
        pBuilder.append(pItemType);
        pBuilder.append(XQIF_EOL);
    }

    /**
     * Format Header.
     *
     * @param pHdr     the header
     * @param pBuilder the string builder
     */
    public static void formatHeader(final String pHdr,
                                    final StringBuilder pBuilder) {
        /* Format the header */
        pBuilder.append(pHdr);
        pBuilder.append(XQIF_EOL);
    }

    /**
     * Format set switch.
     *
     * @param pSwitch  the switch to set
     * @param pBuilder the string builder
     */
    public static void setSwitch(final String pSwitch,
                                 final StringBuilder pBuilder) {
        /* Format the item type */
        pBuilder.append(XQIF_SETOPT);
        pBuilder.append(pSwitch);
        pBuilder.append(XQIF_EOL);
    }

    /**
     * Format clear switch.
     *
     * @param pSwitch  the switch to clear
     * @param pBuilder the string builder
     */
    public static void clearSwitch(final String pSwitch,
                                   final StringBuilder pBuilder) {
        /* Format the item type */
        pBuilder.append(XQIF_CLROPT);
        pBuilder.append(pSwitch);
        pBuilder.append(XQIF_EOL);
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle trivial case */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Check class */
        if (!getClass().equals(pThat.getClass())) {
            return false;
        }

        /* Cast correctly */
        @SuppressWarnings("unchecked") final MoneyWiseXQIFRecord<T> myThat = (MoneyWiseXQIFRecord<T>) pThat;

        /* Check class */
        if (!theClass.equals(myThat.getLineClass())) {
            return false;
        }

        /* Check map */
        if (!theMap.equals(myThat.getLineMap())) {
            return false;
        }

        /* Check SubLists */
        final List<MoneyWiseXQIFRecord<T>> mySubThat = myThat.getSubList();
        if (theSubList == null) {
            return mySubThat == null;
        }
        if (mySubThat == null) {
            return false;
        }
        return theSubList.equals(mySubThat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(theClass, theMap, theSubList);
    }
}
