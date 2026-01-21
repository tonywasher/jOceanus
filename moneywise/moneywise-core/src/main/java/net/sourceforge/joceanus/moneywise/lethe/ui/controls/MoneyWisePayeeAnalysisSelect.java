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
package net.sourceforge.joceanus.moneywise.lethe.ui.controls;

import io.github.tonywasher.joceanus.oceanus.date.OceanusDateRange;
import io.github.tonywasher.joceanus.oceanus.event.OceanusEventManager;
import io.github.tonywasher.joceanus.oceanus.event.OceanusEventRegistrar;
import io.github.tonywasher.joceanus.oceanus.event.OceanusEventRegistrar.OceanusEventProvider;
import io.github.tonywasher.joceanus.metis.data.MetisDataDifference;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysis;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisPayeeBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisPayeeBucket.MoneyWiseAnalysisPayeeBucketList;
import net.sourceforge.joceanus.moneywise.lethe.views.MoneyWiseAnalysisFilter;
import net.sourceforge.joceanus.moneywise.lethe.views.MoneyWiseAnalysisFilter.MoneyWiseAnalysisPayeeFilter;
import net.sourceforge.joceanus.prometheus.views.PrometheusDataEvent;
import net.sourceforge.joceanus.tethys.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.api.base.TethysUIConstant;
import net.sourceforge.joceanus.tethys.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.api.button.TethysUIScrollButtonManager;
import net.sourceforge.joceanus.tethys.api.control.TethysUILabel;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollItem;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIBoxPaneManager;

import java.util.Iterator;

/**
 * Payee Analysis Selection.
 */
public class MoneyWisePayeeAnalysisSelect
        implements MoneyWiseAnalysisFilterSelection, OceanusEventProvider<PrometheusDataEvent> {
    /**
     * Text for Payee Label.
     */
    private static final String NLS_PAYEE = MoneyWiseBasicDataType.PAYEE.getItemName();

    /**
     * The Event Manager.
     */
    private final OceanusEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The panel.
     */
    private final TethysUIBoxPaneManager thePanel;

    /**
     * The select button.
     */
    private final TethysUIScrollButtonManager<MoneyWiseAnalysisPayeeBucket> theButton;

    /**
     * Payee menu.
     */
    private final TethysUIScrollMenu<MoneyWiseAnalysisPayeeBucket> thePayeeMenu;

    /**
     * The active payee bucket list.
     */
    private MoneyWiseAnalysisPayeeBucketList thePayees;

    /**
     * The state.
     */
    private MoneyWisePayeeState theState;

    /**
     * The savePoint.
     */
    private MoneyWisePayeeState theSavePoint;

    /**
     * Constructor.
     *
     * @param pFactory the GUI factory
     */
    protected MoneyWisePayeeAnalysisSelect(final TethysUIFactory<?> pFactory) {
        /* Create the button */
        theButton = pFactory.buttonFactory().newScrollButton(MoneyWiseAnalysisPayeeBucket.class);

        /* Create Event Manager */
        theEventManager = new OceanusEventManager<>();

        /* Create the label */
        final TethysUILabel myLabel = pFactory.controlFactory().newLabel(NLS_PAYEE + TethysUIConstant.STR_COLON);

        /* Define the layout */
        thePanel = pFactory.paneFactory().newHBoxPane();
        thePanel.addSpacer();
        thePanel.addNode(myLabel);
        thePanel.addNode(theButton);

        /* Create initial state */
        theState = new MoneyWisePayeeState();
        theState.applyState();

        /* Access the menus */
        thePayeeMenu = theButton.getMenu();

        /* Create the listeners */
        final OceanusEventRegistrar<TethysUIEvent> myRegistrar = theButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewPayee());
        theButton.setMenuConfigurator(e -> buildPayeeMenu());
    }

    @Override
    public TethysUIComponent getUnderlying() {
        return thePanel;
    }

    @Override
    public OceanusEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public MoneyWiseAnalysisPayeeFilter getFilter() {
        return theState.getFilter();
    }

    @Override
    public boolean isAvailable() {
        return thePayees != null
                && !thePayees.isEmpty();
    }

    /**
     * Create SavePoint.
     */
    public void createSavePoint() {
        /* Create the savePoint */
        theSavePoint = new MoneyWisePayeeState(theState);
    }

    /**
     * Restore SavePoint.
     */
    public void restoreSavePoint() {
        /* Restore the savePoint */
        theState = new MoneyWisePayeeState(theSavePoint);

        /* Apply the state */
        theState.applyState();
    }

    @Override
    public void setEnabled(final boolean bEnabled) {
        /* Pass call on to button */
        theButton.setEnabled(bEnabled && isAvailable());
    }

    @Override
    public void setVisible(final boolean pVisible) {
        thePanel.setVisible(pVisible);
    }

    /**
     * Set analysis.
     *
     * @param pAnalysis the analysis.
     */
    public void setAnalysis(final MoneyWiseAnalysis pAnalysis) {
        /* Access buckets */
        thePayees = pAnalysis.getPayees();

        /* Obtain the current payee */
        MoneyWiseAnalysisPayeeBucket myPayee = theState.getPayee();

        /* Switch to versions from the analysis */
        myPayee = myPayee != null
                ? thePayees.getMatchingPayee(myPayee.getPayee())
                : thePayees.getDefaultPayee();

        /* Set the payee */
        theState.setThePayee(myPayee);
        theState.setDateRange(pAnalysis.getDateRange());
        theState.applyState();
    }

    @Override
    public void setFilter(final MoneyWiseAnalysisFilter<?, ?> pFilter) {
        /* If this is the correct filter type */
        if (pFilter instanceof MoneyWiseAnalysisPayeeFilter) {
            /* Access filter */
            final MoneyWiseAnalysisPayeeFilter myFilter = (MoneyWiseAnalysisPayeeFilter) pFilter;

            /* Obtain the filter bucket */
            MoneyWiseAnalysisPayeeBucket myPayee = myFilter.getBucket();

            /* Obtain equivalent bucket */
            myPayee = thePayees.getMatchingPayee(myPayee.getPayee());

            /* Set the payee */
            theState.setThePayee(myPayee);
            theState.setDateRange(myFilter.getDateRange());
            theState.applyState();
        }
    }

    /**
     * Handle new Payee.
     */
    private void handleNewPayee() {
        /* Select the new Payee */
        if (theState.setPayee(theButton.getValue())) {
            theState.applyState();
            theEventManager.fireEvent(PrometheusDataEvent.SELECTIONCHANGED);
        }
    }

    /**
     * Build Payee menu.
     */
    private void buildPayeeMenu() {
        /* Reset the popUp menu */
        thePayeeMenu.removeAllItems();

        /* Record active item */
        TethysUIScrollItem<MoneyWiseAnalysisPayeeBucket> myActive = null;
        final MoneyWiseAnalysisPayeeBucket myCurr = theState.getPayee();

        /* Loop through the available payee values */
        final Iterator<MoneyWiseAnalysisPayeeBucket> myIterator = thePayees.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisPayeeBucket myBucket = myIterator.next();

            /* Create a new MenuItem and add it to the popUp */
            final TethysUIScrollItem<MoneyWiseAnalysisPayeeBucket> myItem = thePayeeMenu.addItem(myBucket);

            /* If this is the active bucket */
            if (myBucket.equals(myCurr)) {
                /* Record it */
                myActive = myItem;
            }
        }

        /* Ensure active item is visible */
        if (myActive != null) {
            myActive.scrollToItem();
        }
    }

    /**
     * SavePoint values.
     */
    private final class MoneyWisePayeeState {
        /**
         * The active PayeeBucket.
         */
        private MoneyWiseAnalysisPayeeBucket thePayee;

        /**
         * The dateRange.
         */
        private OceanusDateRange theDateRange;

        /**
         * The active filter.
         */
        private MoneyWiseAnalysisPayeeFilter theFilter;

        /**
         * Constructor.
         */
        private MoneyWisePayeeState() {
        }

        /**
         * Constructor.
         *
         * @param pState state to copy from
         */
        private MoneyWisePayeeState(final MoneyWisePayeeState pState) {
            /* Initialise state */
            thePayee = pState.getPayee();
            theDateRange = pState.getDateRange();
            theFilter = pState.getFilter();
        }

        /**
         * Obtain the Payee Bucket.
         *
         * @return the Payee
         */
        private MoneyWiseAnalysisPayeeBucket getPayee() {
            return thePayee;
        }

        /**
         * Obtain the dateRange.
         *
         * @return the dateRange
         */
        private OceanusDateRange getDateRange() {
            return theDateRange;
        }

        /**
         * Obtain the Filter.
         *
         * @return the Filter
         */
        private MoneyWiseAnalysisPayeeFilter getFilter() {
            return theFilter;
        }

        /**
         * Set new Payee.
         *
         * @param pPayee the Payee
         * @return true/false did a change occur
         */
        private boolean setPayee(final MoneyWiseAnalysisPayeeBucket pPayee) {
            /* Adjust the selected payee */
            if (!MetisDataDifference.isEqual(pPayee, thePayee)) {
                setThePayee(pPayee);
                return true;
            }
            return false;
        }

        /**
         * Set the Payee.
         *
         * @param pPayee the Payee
         */
        private void setThePayee(final MoneyWiseAnalysisPayeeBucket pPayee) {
            /* Store the payee */
            thePayee = pPayee;
            if (thePayee != null) {
                theFilter = new MoneyWiseAnalysisPayeeFilter(thePayee);
                theFilter.setDateRange(theDateRange);
            } else {
                theFilter = null;
            }
        }

        /**
         * Set the dateRange.
         *
         * @param pRange the dateRange
         */
        private void setDateRange(final OceanusDateRange pRange) {
            /* Store the dateRange */
            theDateRange = pRange;
            if (theFilter != null) {
                theFilter.setDateRange(theDateRange);
            }
        }

        /**
         * Apply the State.
         */
        private void applyState() {
            /* Adjust the lock-down */
            setEnabled(true);
            theButton.setValue(thePayee);
        }
    }
}
