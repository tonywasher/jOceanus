/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jcoeus.ui.panels;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import net.sourceforge.joceanus.jcoeus.data.CoeusLoan;
import net.sourceforge.joceanus.jcoeus.data.CoeusTotals;
import net.sourceforge.joceanus.jcoeus.data.CoeusTotalsField;
import net.sourceforge.joceanus.jcoeus.data.CoeusTransactionType;
import net.sourceforge.joceanus.jcoeus.ui.CoeusDataEvent;
import net.sourceforge.joceanus.jcoeus.ui.CoeusFilter;
import net.sourceforge.joceanus.jcoeus.ui.CoeusFilter.CoeusSnapShotFilter;
import net.sourceforge.joceanus.jcoeus.ui.CoeusMarketCache;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmetis.list.MetisListBaseManager;
import net.sourceforge.joceanus.jmetis.list.MetisListIndexed;
import net.sourceforge.joceanus.jmetis.launch.MetisToolkit;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataException;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;
import net.sourceforge.joceanus.jtethys.ui.TethysBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysComponent;
import net.sourceforge.joceanus.jtethys.ui.TethysFileSelector;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTableScrollColumn;

/**
 * Statement Panel.
 */
public class CoeusStatementTable
        implements TethysComponent {
    /**
     * The logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(CoeusStatementTable.class);

    /**
     * The logger.
     */
    private static final char COMMA = ',';

    /**
     * The Gui Factory.
     */
    private final TethysGuiFactory theFactory;

    /**
     * The List.
     */
    private final MetisListIndexed<CoeusTotals> theList;

    /**
     * The Table.
     */
    private final TethysTableManager<MetisDataFieldId, CoeusTotals> theTable;

    /**
     * The Selector.
     */
    private final CoeusStatementSelect theSelector;

    /**
     * The BorderPane.
     */
    private final TethysBorderPaneManager thePane;

    /**
     * The Loan Column.
     */
    private final TethysTableScrollColumn<CoeusLoan, MetisDataFieldId, CoeusTotals> theLoanColumn;

    /**
     * The Statement Calculator.
     */
    private final CoeusStatementCalculator theCalculator;

    /**
     * The File Selector.
     */
    private TethysFileSelector theFileSelector;

    /**
     * Constructor.
     * @param pToolkit the Toolkit
     * @param pCache the market cache
     */
    public CoeusStatementTable(final MetisToolkit pToolkit,
                               final CoeusMarketCache pCache) {
        /* Access the GUI factory */
        theFactory = pToolkit.getGuiFactory();

         /* Create the table */
        theTable = theFactory.newTable();
        theTable.setEditable(false);

        /* Create the list */
        theList = new MetisListIndexed<>();
        theTable.setItems(theList.getUnderlyingList());

        /* Create the date column */
        theTable.declareDateColumn(CoeusTotalsField.DATE)
                .setCellValueFactory(CoeusTotals::getDate);

        /* Create the transactionType column */
        theTable.declareScrollColumn(CoeusTotalsField.TRANSTYPE, CoeusTransactionType.class)
                .setCellValueFactory(CoeusTotals::getTransType);

        /* Create the description column */
        theTable.declareStringColumn(CoeusTotalsField.DESC)
                .setCellValueFactory(CoeusTotals::getDescription);

        /* Create the loan column */
        theLoanColumn = theTable.declareScrollColumn(CoeusTotalsField.LOAN, CoeusLoan.class);
        theLoanColumn.setCellValueFactory(CoeusTotals::getLoan);

        /* Create the delta column */
        theTable.declareRawDecimalColumn(CoeusTotalsField.DELTA)
                .setCellValueFactory(this::getFilteredDelta);

        /* Create the balance column */
        theTable.declareRawDecimalColumn(CoeusTotalsField.BALANCE)
                .setCellValueFactory(this::getFilteredBalance);

        /* Create the selector */
        theSelector = new CoeusStatementSelect(theFactory, pCache);
        theSelector.getEventRegistrar().addEventListener(CoeusDataEvent.SELECTIONCHANGED, e -> updateStatement(theSelector.getFilter()));
        theSelector.getEventRegistrar().addEventListener(CoeusDataEvent.FILTERCHANGED, e -> filterChanged());
        theSelector.getEventRegistrar().addEventListener(CoeusDataEvent.SAVETOFILE, e -> saveToFile());

        /* Create and configure the Pane */
        thePane = theFactory.newBorderPane();
        thePane.setNorth(theSelector);
        thePane.setCentre(theTable);

        /* Create the calculator */
        theCalculator = new CoeusStatementCalculator(theList);
    }

    @Override
    public TethysNode getNode() {
        return thePane.getNode();
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        thePane.setEnabled(pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        thePane.setVisible(pVisible);
    }

    @Override
    public Integer getId() {
        return thePane.getId();
    }

    /**
     * Obtain filtered delta for transaction.
     * @param pTotals the totals
     * @return the delta
     */
    private TethysDecimal getFilteredDelta(final CoeusTotals pTotals) {
        return theCalculator.calculateValue(pTotals, CoeusTotalsField.DELTA);
    }

    /**
     * Obtain filtered balance for transaction.
     * @param pTotals the totals
     * @return the balance
     */
    private TethysDecimal getFilteredBalance(final CoeusTotals pTotals) {
        return theCalculator.calculateValue(pTotals, CoeusTotalsField.BALANCE);
    }

    /**
     * Process the filter.
     * @param pFilter the filter
     */
    public void processFilter(final CoeusFilter pFilter) {
        theSelector.setFilter(pFilter);
        updateStatement(pFilter);
    }

    /**
     * Update Statement.
     * @param pFilter the filter
     */
    public void updateStatement(final CoeusFilter pFilter) {
        MetisListBaseManager.resetContent(theList, pFilter.getHistory().historyIterator());
        theTable.setItems(theList.getUnderlyingList());
        filterChanged();
    }

    /**
     * Handle a change in filter.
     */
    private void filterChanged() {
        /* Adjust the calculator */
        final CoeusFilter myFilter = theSelector.getFilter();
        theCalculator.setInitial(myFilter.getHistory().getInitial());
        theCalculator.setTotalSet(myFilter.getTotalSet());

        /* Show or hide the loan column */
        final boolean showLoan = !(myFilter instanceof CoeusSnapShotFilter)
                                 || ((CoeusSnapShotFilter) myFilter).getLoan() == null;
        theLoanColumn.setVisible(showLoan);

        /* Reset the filter */
        theTable.setFilter(theCalculator.getFilter());
    }

    /**
     * Handle a saveToFile request.
     */
    private void saveToFile() {
        /* Create the string Builder */
        final StringBuilder myBuilder = new StringBuilder();

        /* Loop through the view iterator */
        final Iterator<CoeusTotals> myIterator = theTable.viewIterator();
        while (myIterator.hasNext()) {
            final CoeusTotals myTotals = myIterator.next();

            /* Build the line */
            myBuilder.append(myTotals.getDate());
            myBuilder.append(COMMA);
            myBuilder.append(myTotals.getTransType());
            myBuilder.append(COMMA);
            myBuilder.append(myTotals.getDescription());
            myBuilder.append(COMMA);
            myBuilder.append(myTotals.getDelta());
            myBuilder.append(COMMA);
            myBuilder.append(myTotals.getBalance());
            myBuilder.append('\n');
        }

        /* Write the result to the file */
        writeToFile(myBuilder.toString());
    }

    /**
     * Initialise file selector.
     * @return the file selector
     */
    private TethysFileSelector initFileSelector() {
        final TethysFileSelector myFileSelector = theFactory.newFileSelector();
        myFileSelector.setUseSave(true);
        myFileSelector.setExtension(".csv");
        return myFileSelector;
    }

    /**
     * Write data to file.
     * @param pData the string to write
     */
    private void writeToFile(final String pData) {
        try {
            /* Make sure that the file Selector is initialised */
            if (theFileSelector == null) {
                theFileSelector = initFileSelector();
            }

            /* Select File */
            final File myFile = theFileSelector.selectFile();
            if (myFile != null) {
                writeDataToFile(pData, myFile);
            }
        } catch (OceanusException e) {
            LOGGER.error("Failed to write to file", e);
        }
    }

    /**
     * Write String to file.
     * @param pData the string to write
     * @param pFile the file to write to
     * @throws OceanusException on error
     */
    private static void writeDataToFile(final String pData,
                                        final File pFile) throws OceanusException {
        /* Protect the write */
        try (PrintWriter myWriter = new PrintWriter(pFile, StandardCharsets.UTF_8)) {
            /* Write to stream */
            myWriter.print(pData);

        } catch (IOException e) {
            throw new TethysDataException("Failed to output XML", e);
        }
    }
}
