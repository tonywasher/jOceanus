/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jmetis.service.sheet.odf;

import java.io.InputStream;
import java.io.OutputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetSheet;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetView;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetWorkBook;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysDataFormatter;

/**
 * Odf WorkBook.
 */
public class MetisOdfWorkBook
        implements MetisSheetWorkBook {
    /**
     * The Contents Document.
     */
    private final Document theContents;

    /**
     * The Parser.
     */
    private final MetisOdfParser theParser;

    /**
     * The Styler.
     */
    private final MetisOdfStyler theStyler;

    /**
     * Data formatter.
     */
    private final TethysDataFormatter theDataFormatter;

    /**
     * Is the workBook readOnly?
     */
    private final boolean isReadOnly;

    /**
     * The officeSpreadSheet element.
     */
    private final Element theSpreadSheet;

    /**
     * The tableStore.
     */
    private final MetisOdfTableStore theTableStore;

    /**
     * Constructor.
     * @param pInput the input stream
     * @throws OceanusException on error
     */
    MetisOdfWorkBook(final InputStream pInput) throws OceanusException {
        /* Load the contents of the spreadSheet */
        theContents = MetisOdfLoader.loadNewSpreadSheet(pInput);
        theParser = new MetisOdfParser(theContents);
        theStyler = null;

        /* Allocate the formatter */
        theDataFormatter = createFormatter();

        /* Note readOnly */
        isReadOnly = true;

        /* Access the list of tables */
        final Element myMain = theContents.getDocumentElement();
        final Element myBody = theParser.getFirstNamedChild(myMain, MetisOdfOfficeItem.BODY);
        theSpreadSheet = theParser.getFirstNamedChild(myBody, MetisOdfOfficeItem.SPREADSHEET);

        /* Build tableStore */
        theTableStore = new MetisOdfTableStore(this, theSpreadSheet);
        theTableStore.loadMaps();
    }

    /**
     * Constructor.
     * @throws OceanusException on error
     */
    MetisOdfWorkBook() throws OceanusException {
        /* Create empty workBook */
        theContents = MetisOdfLoader.loadInitialSpreadSheet();
        theParser = new MetisOdfParser(theContents);
        theStyler = new MetisOdfStyler(theParser);

        /* Allocate the formatter */
        theDataFormatter = createFormatter();

        /* Clear out all existing tables */
        final Element myMain = theContents.getDocumentElement();
        final Element myBody = theParser.getFirstNamedChild(myMain, MetisOdfOfficeItem.BODY);
        theSpreadSheet = theParser.getFirstNamedChild(myBody, MetisOdfOfficeItem.SPREADSHEET);
        for (Element myTable : theParser.getAllNamedChildren(theSpreadSheet, MetisOdfTableItem.TABLE)) {
            theSpreadSheet.removeChild(myTable);
        }

        /* Build tableStore */
        theTableStore = new MetisOdfTableStore(this, theSpreadSheet);

        /* Note writable */
        isReadOnly = false;
    }

    @Override
    public boolean isReadOnly() {
        return isReadOnly;
    }

    /**
     * Obtain the parser.
     * @return the parser
     */
    MetisOdfParser getParser() {
        return theParser;
    }

    /**
     * Obtain the styler.
     * @return the styler
     */
    MetisOdfStyler getStyler() {
        return theStyler;
    }

    /**
     * Obtain the data formatter.
     * @return the formatter
     */
    TethysDataFormatter getFormatter() {
        return theDataFormatter;
    }

    @Override
    public void saveToStream(final OutputStream pOutput) throws OceanusException {
        /* build the elements */
        theTableStore.buildSheetXML();

        /* Write to a completely new spreadSheet */
        MetisOdfLoader.writeNewSpreadSheet(theContents, pOutput);
    }

    @Override
    public MetisSheetSheet newSheet(final String pName,
                                    final int pNumRows,
                                    final int pNumCols) throws OceanusException {
        return theTableStore.newSheet(pName, pNumRows, pNumCols);
    }

    @Override
    public MetisSheetSheet newSheet(final String pName) throws OceanusException {
        return newSheet(pName, 1, 1);
    }

    @Override
    public MetisSheetSheet getSheet(final String pName) throws OceanusException {
        return theTableStore.getSheet(pName);
    }

    @Override
    public MetisSheetView getRangeView(final String pName) throws OceanusException {
        return theTableStore.getRangeView(pName);
    }
}
