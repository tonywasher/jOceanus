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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jmetis/jmetis-core/src/main/java/net/sourceforge/joceanus/jmetis/service/sheet/jopen/MetisJOpenWorkBook.java $
 * $Revision: 923 $
 * $Author: Tony $
 * $Date: 2018-03-22 09:07:36 +0000 (Thu, 22 Mar 2018) $
 ******************************************************************************/
package net.sourceforge.joceanus.jmetis.service.sheet.jopen;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.jopendocument.dom.ODPackage;
import org.jopendocument.dom.spreadsheet.Range;
import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetCellPosition;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetException;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetSheet;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetView;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetWorkBook;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysDataFormatter;

/**
 * JOpenDocument WorkBook.
 */
public class MetisJOpenWorkBook
        implements MetisSheetWorkBook {
    /**
     * The load failure error text.
     */
    private static final String ERROR_LOAD = "Failed to load workbook";

    /**
     * Oasis package.
     */
    private final ODPackage thePackage;

    /**
     * Oasis WorkBook.
     */
    private final SpreadSheet theBook;

    /**
     * Data formatter.
     */
    private final TethysDataFormatter theDataFormatter;

    /**
     * Map of Sheets.
     */
    private final Map<String, MetisJOpenSheet> theSheetMap;

    /**
     * Map of Ranges.
     */
    private final Map<String, Range> theRangeMap;

    /**
     * Is the workBook readOnly?
     */
    private final boolean isReadOnly;

    /**
     * Constructor.
     * @param pInput the input stream
     * @throws OceanusException on error
     */
    public MetisJOpenWorkBook(final InputStream pInput) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Access book and contents */
            thePackage = ODPackage.createFromStream(pInput, "Input");
            theBook = thePackage.getSpreadSheet();

            /* Allocate the formatter */
            theDataFormatter = createFormatter();

            /* Allocate the maps */
            theSheetMap = new HashMap<>();
            theRangeMap = new HashMap<>();

            /* Build the maps */
            buildSheetMap();
            buildRangeMap();

            /* Note readOnly */
            isReadOnly = true;

            /* Handle Exceptions */
        } catch (Exception e) {
            throw new MetisSheetException(ERROR_LOAD, e);
        }
    }

    /**
     * Constructor.
     * @throws OceanusException on error
     */
    public MetisJOpenWorkBook() throws OceanusException {
        /* Create empty workBook */
        theBook = SpreadSheet.create(0, 0, 0);
        thePackage = theBook.getPackage();

        /* Allocate the formatter */
        theDataFormatter = createFormatter();

        /* Allocate the maps */
        theSheetMap = new HashMap<>();
        theRangeMap = new HashMap<>();

        /* Note writable */
        isReadOnly = false;
    }

    @Override
    public boolean isReadOnly() {
        return isReadOnly;
    }

    /**
     * Obtain the data formatter.
     * @return the formatter
     */
    protected TethysDataFormatter getDataFormatter() {
        return theDataFormatter;
    }

    /**
     * Save the workBook to output stream.
     * @param pOutput the output stream
     * @throws OceanusException on error
     */
    public void saveToStream(final OutputStream pOutput) throws OceanusException {
        try {
            thePackage.save(pOutput);
        } catch (IOException e) {
            throw new MetisSheetException("Failed to save workbook", e);
        }
    }

    @Override
    public MetisSheetSheet newSheet(final String pName,
                                    final int pNumRows,
                                    final int pNumCols) {
        /* Create the new Sheet */
        final int myIndex = theBook.getSheetCount();
        final Sheet myBaseSheet = theBook.addSheet(pName);
        myBaseSheet.ensureColumnCount(pNumCols);
        myBaseSheet.ensureRowCount(pNumRows);

        /* Add it to the map */
        final MetisJOpenSheet mySheet = new MetisJOpenSheet(this, myBaseSheet, myIndex);
        theSheetMap.put(pName, mySheet);

        /* Return the sheet */
        return mySheet;
    }

    @Override
    public MetisSheetSheet newSheet(final String pName) {
        return newSheet(pName, 1, 1);
    }

    @Override
    public MetisSheetView getRangeView(final String pName) throws OceanusException {
        /* Locate the named range in the map */
        final Range myRange = theRangeMap.get(pName);
        if (myRange == null) {
            return null;
        }

        /* Obtain the address */
        final MetisSheetCellPosition myFirstCell = new MetisSheetCellPosition(myRange.getStartPoint().x, myRange.getStartPoint().y);
        final MetisSheetCellPosition myLastCell = new MetisSheetCellPosition(myRange.getEndPoint().x, myRange.getEndPoint().y);

        /* Obtain the sheet and reject if missing */
        final MetisSheetSheet mySheet = getSheet(myRange.getStartSheet());
        if (mySheet == null) {
            throw new MetisSheetException("Sheet for "
                                          + pName
                                          + " not found in workbook");
        }

        /* Return the view */
        return new MetisSheetView(mySheet, myFirstCell, myLastCell);
    }

    /**
     * Build sheet map.
     */
    private void buildSheetMap() {
        /* Loop through the sheets */
        for (int i = 0; i < theBook.getSheetCount(); i++) {
            /* Access sheet and add to the map */
            final Sheet myBaseSheet = theBook.getSheet(i);
            final MetisJOpenSheet mySheet = new MetisJOpenSheet(this, myBaseSheet, i);
            theSheetMap.put(mySheet.getName(), mySheet);
        }
    }

    @Override
    public MetisSheetSheet getSheet(final String pName) {
        return theSheetMap.get(pName);
    }

    /**
     * Build range map.
     */
    private void buildRangeMap() {
        /* Loop through the ranges */
        for (String myName : theBook.getRangesNames()) {
            /* Access range and add to the map */
            final Range myRange = theBook.getRange(myName);
            theRangeMap.put(myName, myRange);
        }
    }
}
