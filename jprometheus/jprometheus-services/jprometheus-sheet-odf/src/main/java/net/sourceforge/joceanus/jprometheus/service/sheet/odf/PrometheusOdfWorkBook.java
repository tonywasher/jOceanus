/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.service.sheet.odf;

import java.io.InputStream;
import java.io.OutputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetSheet;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetView;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetWorkBook;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;

/**
 * Odf WorkBook.
 */
public class PrometheusOdfWorkBook
        implements PrometheusSheetWorkBook {
    /**
     * The Contents Document.
     */
    private final Document theContents;

    /**
     * The Parser.
     */
    private final PrometheusOdfParser theParser;

    /**
     * The Styler.
     */
    private final PrometheusOdfStyler theStyler;

    /**
     * Data formatter.
     */
    private final TethysUIDataFormatter theDataFormatter;

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
    private final PrometheusOdfTableStore theTableStore;

    /**
     * Constructor.
     * @param pFactory the gui factory
     * @param pInput the input stream
     * @throws OceanusException on error
     */
    PrometheusOdfWorkBook(final TethysUIFactory<?> pFactory,
                          final InputStream pInput) throws OceanusException {
        /* Load the contents of the spreadSheet */
        theContents = PrometheusOdfLoader.loadNewSpreadSheet(pInput);
        theParser = new PrometheusOdfParser(theContents);
        theStyler = null;

        /* Allocate the formatter */
        theDataFormatter = createFormatter(pFactory);

        /* Note readOnly */
        isReadOnly = true;

        /* Access the list of tables */
        final Element myMain = theContents.getDocumentElement();
        final Element myBody = theParser.getFirstNamedChild(myMain, PrometheusOdfOfficeItem.BODY);
        theSpreadSheet = theParser.getFirstNamedChild(myBody, PrometheusOdfOfficeItem.SPREADSHEET);

        /* Build tableStore */
        theTableStore = new PrometheusOdfTableStore(this, theSpreadSheet);
        theTableStore.loadMaps();
    }

    /**
     * Constructor.
     * @param pFactory the gui factory
     * @throws OceanusException on error
     */
    PrometheusOdfWorkBook(final TethysUIFactory<?> pFactory) throws OceanusException {
        /* Create empty workBook */
        theContents = PrometheusOdfLoader.loadInitialSpreadSheet();
        theParser = new PrometheusOdfParser(theContents);
        theStyler = new PrometheusOdfStyler(theParser);

        /* Allocate the formatter */
        theDataFormatter = createFormatter(pFactory);

        /* Clear out all existing tables */
        final Element myMain = theContents.getDocumentElement();
        final Element myBody = theParser.getFirstNamedChild(myMain, PrometheusOdfOfficeItem.BODY);
        theSpreadSheet = theParser.getFirstNamedChild(myBody, PrometheusOdfOfficeItem.SPREADSHEET);
        for (Element myTable : theParser.getAllNamedChildren(theSpreadSheet, PrometheusOdfTableItem.TABLE)) {
            theSpreadSheet.removeChild(myTable);
        }

        /* Build tableStore */
        theTableStore = new PrometheusOdfTableStore(this, theSpreadSheet);

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
    PrometheusOdfParser getParser() {
        return theParser;
    }

    /**
     * Obtain the styler.
     * @return the styler
     */
    PrometheusOdfStyler getStyler() {
        return theStyler;
    }

    /**
     * Obtain the data formatter.
     * @return the formatter
     */
    TethysUIDataFormatter getFormatter() {
        return theDataFormatter;
    }

    @Override
    public void saveToStream(final OutputStream pOutput) throws OceanusException {
        /* build the elements */
        theTableStore.buildSheetXML();

        /* Write to a completely new spreadSheet */
        PrometheusOdfLoader.writeNewSpreadSheet(theContents, pOutput);
    }

    @Override
    public PrometheusSheetSheet newSheet(final String pName,
                                         final int pNumRows,
                                         final int pNumCols) throws OceanusException {
        return theTableStore.newSheet(pName, pNumRows, pNumCols);
    }

    @Override
    public PrometheusSheetSheet newSheet(final String pName) throws OceanusException {
        return newSheet(pName, 1, 1);
    }

    @Override
    public PrometheusSheetSheet getSheet(final String pName) throws OceanusException {
        return theTableStore.getSheet(pName);
    }

    @Override
    public PrometheusSheetView getRangeView(final String pName) throws OceanusException {
        return theTableStore.getRangeView(pName);
    }
}
