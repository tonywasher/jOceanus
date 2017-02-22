/*******************************************************************************
 * jCoeus: Peer2Peer Analysis
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jcoeus.ui.panels;

import net.sourceforge.joceanus.jcoeus.data.CoeusLoan;
import net.sourceforge.joceanus.jcoeus.data.CoeusTotals;
import net.sourceforge.joceanus.jcoeus.data.CoeusTransactionType;
import net.sourceforge.joceanus.jcoeus.ui.CoeusDataEvent;
import net.sourceforge.joceanus.jcoeus.ui.CoeusFilter;
import net.sourceforge.joceanus.jcoeus.ui.CoeusFilter.CoeusSnapShotFilter;
import net.sourceforge.joceanus.jcoeus.ui.CoeusMarketCache;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.newlist.MetisBaseList;
import net.sourceforge.joceanus.jmetis.threads.MetisToolkit;
import net.sourceforge.joceanus.jmetis.ui.MetisTableManager;
import net.sourceforge.joceanus.jtethys.ui.TethysBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTableColumn;

/**
 * Statement Panel.
 * @param <N> Node type
 * @param <I> Icon type
 */
public class CoeusStatementTable<N, I>
        implements TethysNode<N> {
    /**
     * The List.
     */
    private final MetisBaseList<CoeusTotals> theList;

    /**
     * The Table.
     */
    private final MetisTableManager<CoeusTotals, N, I> theTable;

    /**
     * The Selector.
     */
    private final CoeusStatementSelect<N, I> theSelector;

    /**
     * The BorderPane.
     */
    private final TethysBorderPaneManager<N, I> thePane;

    /**
     * The Loan Column.
     */
    private final TethysTableColumn<MetisField, CoeusTotals, N, I> theLoanColumn;

    /**
     * The Statement Calculator.
     */
    private final CoeusStatementCalculator theCalculator;

    /**
     * Constructor.
     * @param pToolkit the Toolkit
     * @param pCache the market cache
     */
    public CoeusStatementTable(final MetisToolkit<N, I> pToolkit,
                               final CoeusMarketCache pCache) {
        /* Access the GUI factory */
        TethysGuiFactory<N, I> myFactory = pToolkit.getGuiFactory();

        /* Create the list */
        theList = new MetisBaseList<>(CoeusTotals.class, CoeusTotals.getBaseFields());

        /* Create the table */
        theTable = pToolkit.newTableManager(theList);
        theTable.declareDateColumn(CoeusTotals.FIELD_DATE);
        theTable.declareScrollColumn(CoeusTotals.FIELD_TYPE, CoeusTransactionType.class);
        theTable.declareStringColumn(CoeusTotals.FIELD_DESC);
        theLoanColumn = theTable.declareScrollColumn(CoeusTotals.FIELD_LOAN, CoeusLoan.class);
        theTable.declareRawDecimalColumn(CoeusTotals.FIELD_DELTA);
        theTable.declareRawDecimalColumn(CoeusTotals.FIELD_BALANCE);

        /* Create the selector */
        theSelector = new CoeusStatementSelect<>(myFactory, pCache);
        theSelector.getEventRegistrar().addEventListener(CoeusDataEvent.SELECTIONCHANGED, e -> updateStatement(theSelector.getFilter()));
        theSelector.getEventRegistrar().addEventListener(CoeusDataEvent.FILTERCHANGED, e -> filterChanged());

        /* Create and configure the Pane */
        thePane = myFactory.newBorderPane();
        thePane.setNorth(theSelector);
        thePane.setCentre(theTable);

        /* Create the calculator */
        theCalculator = new CoeusStatementCalculator();
    }

    @Override
    public N getNode() {
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
        theList.resetContent(pFilter.getHistory().historyIterator());
        filterChanged();
    }

    /**
     * Handle a change in filter.
     */
    private void filterChanged() {
        /* Adjust the calculator */
        CoeusFilter myFilter = theSelector.getFilter();
        theCalculator.setTotalSet(myFilter.getTotalSet());

        /* Show or hide the loan column */
        boolean showLoan = (!(myFilter instanceof CoeusSnapShotFilter))
                           || ((CoeusSnapShotFilter) myFilter).getLoan() == null;

        theLoanColumn.setVisible(showLoan);

        /* Reset the filter */
        theTable.setFilter(theCalculator.getFilter());

        /* Declare the calculator */
        theTable.setCalculator(theCalculator);
    }
}