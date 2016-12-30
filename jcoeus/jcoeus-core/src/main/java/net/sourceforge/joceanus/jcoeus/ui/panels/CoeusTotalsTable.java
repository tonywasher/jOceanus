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

import net.sourceforge.joceanus.jcoeus.data.CoeusTotals;
import net.sourceforge.joceanus.jcoeus.data.CoeusTransactionType;
import net.sourceforge.joceanus.jcoeus.ui.CoeusFilter;
import net.sourceforge.joceanus.jcoeus.ui.CoeusMarketCache;
import net.sourceforge.joceanus.jmetis.newlist.MetisBaseList;
import net.sourceforge.joceanus.jmetis.threads.MetisToolkit;
import net.sourceforge.joceanus.jmetis.ui.MetisTableManager;
import net.sourceforge.joceanus.jtethys.ui.TethysBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;

/**
 * Statement Panel.
 * @param <N> Node type
 * @param <I> Icon type
 */
public class CoeusTotalsTable<N, I>
        implements TethysNode<N> {
    /**
     * The MarketCache.
     */
    private final CoeusMarketCache theCache;

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
    private final CoeusFilterSelect<N, I> theSelector;

    /**
     * The BorderPane.
     */
    private final TethysBorderPaneManager<N, I> thePane;

    /**
     * Constructor.
     * @param pToolkit the Toolkit
     * @param pCache the market cache
     */
    public CoeusTotalsTable(final MetisToolkit<N, I> pToolkit,
                            final CoeusMarketCache pCache) {
        /* Store the cache */
        theCache = pCache;

        /* Access the GUI factory */
        TethysGuiFactory<N, I> myFactory = pToolkit.getGuiFactory();

        /* Create the list */
        theList = new MetisBaseList<>(CoeusTotals.class, CoeusTotals.getBaseFields());

        /* Create the table */
        theTable = pToolkit.newTableManager(theList);
        theTable.declareDateColumn(CoeusTotals.FIELD_DATE);
        theTable.declareScrollColumn(CoeusTotals.FIELD_TYPE, CoeusTransactionType.class);
        theTable.declareStringColumn(CoeusTotals.FIELD_DESC);
        theTable.declareStringColumn(CoeusTotals.FIELD_LOAN);

        /* Create the selector */
        theSelector = new CoeusFilterSelect<>(myFactory);

        /* Create and configure the Pane */
        thePane = myFactory.newBorderPane();
        thePane.setNorth(theSelector);
        thePane.setCentre(theTable);
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

    }
}
