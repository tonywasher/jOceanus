/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.jOceanus.jMoneyWise.quicken.file;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.jOceanus.jDataManager.JDataFormatter;
import net.sourceforge.jOceanus.jMoneyWise.quicken.definitions.QLineType;

/**
 * QIF File record representation.
 * @param <T> the line type
 */
public abstract class QIFRecord<T extends Enum<T> & QLineType> {
    /**
     * Quicken Item type.
     */
    private static final String QIF_ITEMTYPE = "!Type:";

    /**
     * Set option.
     */
    private static final String QIF_SETOPT = "!Option:";

    /**
     * Clear option.
     */
    private static final String QIF_CLROPT = "!Clear:";

    /**
     * Quicken End of Item indicator.
     */
    private static final String QIF_EOI = "^";

    /**
     * Quicken New line.
     */
    protected static final char QIF_EOL = '\n';

    /**
     * The QIF File.
     */
    private final QIFFile theFile;

    /**
     * Class of lines.
     */
    private final Class<T> theClass;

    /**
     * Map of lines.
     */
    private final Map<T, QIFLine<T>> theMap;

    /**
     * List of subRecords.
     */
    private final List<QIFRecord<T>> theSubList;

    /**
     * Obtain file.
     * @return the file
     */
    protected QIFFile getFile() {
        return theFile;
    }

    /**
     * Constructor.
     * @param pFile the QIF File
     * @param pClass the class of the lines
     * @param hasSubRecords does the record have subRecords?
     */
    protected QIFRecord(final QIFFile pFile,
                        final Class<T> pClass,
                        final boolean hasSubRecords) {
        /* Record the class and file */
        theClass = pClass;
        theFile = pFile;

        /* Create the map */
        theMap = new HashMap<T, QIFLine<T>>();

        /* Allocate list if required */
        theSubList = (hasSubRecords)
                ? new ArrayList<QIFRecord<T>>()
                : null;
    }

    /**
     * Constructor.
     * @param pFile the QIF File
     * @param pClass the class of the lines
     */
    protected QIFRecord(final QIFFile pFile,
                        final Class<T> pClass) {
        /* Record the class */
        this(pFile, pClass, false);
    }

    /**
     * Add line to map.
     * @param pLine the Line to add
     */
    protected void addLine(final QIFLine<T> pLine) {
        /* Add to the map */
        theMap.put(pLine.getLineType(), pLine);
    }

    /**
     * Add subRecord to list.
     * @param pRecord the record to add
     */
    protected void addRecord(final QIFRecord<T> pRecord) {
        /* Add to the list */
        theSubList.add(pRecord);
    }

    /**
     * Format record.
     * @param pFormatter the data formatter
     * @param pBuilder the string builder
     */
    public void formatRecord(final JDataFormatter pFormatter,
                             final StringBuilder pBuilder) {
        /* Format the standard lines */
        formatLines(pFormatter, pBuilder);

        /* If we have subLists */
        if (theSubList != null) {
            /* Loop through the subList */
            Iterator<QIFRecord<T>> myIterator = theSubList.iterator();
            while (myIterator.hasNext()) {
                QIFRecord<T> myRecord = myIterator.next();

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
    private void formatLines(final JDataFormatter pFormatter,
                             final StringBuilder pBuilder) {
        /* Loop through the map in ordinal order */
        for (T myType : theClass.getEnumConstants()) {
            /* Look up value in the map */
            QIFLine<T> myLine = theMap.get(myType);
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
}
