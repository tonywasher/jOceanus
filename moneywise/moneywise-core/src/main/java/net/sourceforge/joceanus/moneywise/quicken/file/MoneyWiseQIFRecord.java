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
package net.sourceforge.joceanus.moneywise.quicken.file;

import net.sourceforge.joceanus.moneywise.quicken.definitions.MoneyWiseQLineType;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * QIF File record representation.
 * @param <T> the line type
 */
public abstract class MoneyWiseQIFRecord<T extends Enum<T> & MoneyWiseQLineType> {
    /**
     * Quicken Command.
     */
    protected static final String QIF_CMD = "!";

    /**
     * Quicken Item type.
     */
    protected static final String QIF_ITEMTYPE = QIF_CMD + "Type:";

    /**
     * Set option.
     */
    protected static final String QIF_SETOPT = QIF_CMD + "Option:";

    /**
     * Clear option.
     */
    protected static final String QIF_CLROPT = QIF_CMD + "Clear:";

    /**
     * Quicken End of Item indicator.
     */
    protected static final String QIF_EOI = "^";

    /**
     * Quicken New line.
     */
    protected static final char QIF_EOL = '\n';

    /**
     * The QIF File.
     */
    private final MoneyWiseQIFFile theFile;

    /**
     * Class of lines.
     */
    private final Class<T> theClass;

    /**
     * Map of lines.
     */
    private final Map<T, MoneyWiseQIFLine<T>> theMap;

    /**
     * List of subRecords.
     */
    private List<MoneyWiseQIFRecord<T>> theSubList;

    /**
     * Constructor.
     * @param pFile the QIF File
     * @param pClass the class of the lines
     */
    protected MoneyWiseQIFRecord(final MoneyWiseQIFFile pFile,
                                 final Class<T> pClass) {
        /* Record the class and file */
        theClass = pClass;
        theFile = pFile;

        /* Create the map */
        theMap = new EnumMap<>(pClass);
    }

    /**
     * Obtain file.
     * @return the file
     */
    protected MoneyWiseQIFFile getFile() {
        return theFile;
    }

    /**
     * Obtain line class.
     * @return the line class
     */
    protected Class<T> getLineClass() {
        return theClass;
    }

    /**
     * Obtain line map.
     * @return the line map
     */
    protected Map<T, MoneyWiseQIFLine<T>> getLineMap() {
        return theMap;
    }

    /**
     * Obtain subList.
     * @return the subList
     */
    protected List<MoneyWiseQIFRecord<T>> getSubList() {
        return theSubList;
    }

    /**
     * Obtain line for record.
     * @param pLineType the line type
     * @return the record
     */
    protected MoneyWiseQIFLine<T> getLine(final T pLineType) {
        return theMap.get(pLineType);
    }

    /**
     * Add line to map.
     * @param pLine the Line to add
     */
    protected void addLine(final MoneyWiseQIFLine<T> pLine) {
        /* Add to the map */
        theMap.put(pLine.getLineType(), pLine);
    }

    /**
     * Add subRecord to list.
     * @param pRecord the record to add
     */
    protected void addRecord(final MoneyWiseQIFRecord<T> pRecord) {
        /* Allocate list if required */
        if (theSubList == null) {
            theSubList = new ArrayList<>();
        }

        /* Add to the list */
        theSubList.add(pRecord);
    }

    /**
     * Format record.
     * @param pFormatter the data formatter
     * @param pBuilder the string builder
     */
    public void formatRecord(final OceanusDataFormatter pFormatter,
                             final StringBuilder pBuilder) {
        /* Format the standard lines */
        formatLines(pFormatter, pBuilder);

        /* If we have subLists */
        if (theSubList != null) {
            /* Loop through the subList */
            for (MoneyWiseQIFRecord<T> myRecord : theSubList) {
                /* Format the lines of the subRecord */
                myRecord.formatLines(pFormatter, pBuilder);
            }
        }

        /* Add the end of record indicator */
        pBuilder.append(QIF_EOI);
        pBuilder.append(QIF_EOL);
    }

    /**
     * Format lines.
     * @param pFormatter the data formatter
     * @param pBuilder the string builder
     */
    private void formatLines(final OceanusDataFormatter pFormatter,
                             final StringBuilder pBuilder) {
        /* Loop through the map in ordinal order */
        for (T myType : theClass.getEnumConstants()) {
            /* Look up value in the map */
            final MoneyWiseQIFLine<T> myLine = theMap.get(myType);
            if (myLine != null) {
                /* Format the line */
                myLine.formatLine(pFormatter, pBuilder);
                pBuilder.append(QIF_EOL);
            }
        }
    }

    /**
     * Format item type.
     * @param pItemType the item type
     * @param pBuilder the string builder
     */
    protected static void formatItemType(final String pItemType,
                                         final StringBuilder pBuilder) {
        /* Format the item type */
        pBuilder.append(QIF_ITEMTYPE);
        pBuilder.append(pItemType);
        pBuilder.append(QIF_EOL);
    }

    /**
     * Format Header.
     * @param pHdr the header
     * @param pBuilder the string builder
     */
    protected static void formatHeader(final String pHdr,
                                       final StringBuilder pBuilder) {
        /* Format the header */
        pBuilder.append(pHdr);
        pBuilder.append(QIF_EOL);
    }

    /**
     * Format set switch.
     * @param pSwitch the switch to set
     * @param pBuilder the string builder
     */
    protected static void setSwitch(final String pSwitch,
                                    final StringBuilder pBuilder) {
        /* Format the item type */
        pBuilder.append(QIF_SETOPT);
        pBuilder.append(pSwitch);
        pBuilder.append(QIF_EOL);
    }

    /**
     * Format clear switch.
     * @param pSwitch the switch to clear
     * @param pBuilder the string builder
     */
    protected static void clearSwitch(final String pSwitch,
                                      final StringBuilder pBuilder) {
        /* Format the item type */
        pBuilder.append(QIF_CLROPT);
        pBuilder.append(pSwitch);
        pBuilder.append(QIF_EOL);
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
        @SuppressWarnings("unchecked")
        final MoneyWiseQIFRecord<T> myThat = (MoneyWiseQIFRecord<T>) pThat;

        /* Check class */
        if (!theClass.equals(myThat.getLineClass())) {
            return false;
        }

        /* Check map */
        if (!theMap.equals(myThat.getLineMap())) {
            return false;
        }

        /* Check SubLists */
        final List<MoneyWiseQIFRecord<T>> mySubThat = myThat.getSubList();
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
        int myResult = MoneyWiseQIFFile.HASH_BASE * theClass.hashCode();
        myResult += theMap.hashCode();
        if (theSubList != null) {
            myResult *= MoneyWiseQIFFile.HASH_BASE;
            myResult += theSubList.hashCode();
        }
        return myResult;
    }
}
