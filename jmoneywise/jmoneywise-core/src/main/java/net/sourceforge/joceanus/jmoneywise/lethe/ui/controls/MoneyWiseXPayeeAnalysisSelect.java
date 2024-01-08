/*******************************************************************************
 * MoneyWise: Finance Application
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
package net.sourceforge.joceanus.jmoneywise.lethe.ui.controls;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.PayeeBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.PayeeBucket.PayeeBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter.PayeeFilter;
import net.sourceforge.joceanus.jprometheus.atlas.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIConstant;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUILabel;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollItem;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIBoxPaneManager;

/**
 * Payee Analysis Selection.
 */
public class MoneyWiseXPayeeAnalysisSelect
        implements MoneyWiseXAnalysisFilterSelection, TethysEventProvider<PrometheusDataEvent> {
    /**
     * Text for Payee Label.
     */
    private static final String NLS_PAYEE = MoneyWiseDataType.PAYEE.getItemName();

    /**
     * The Event Manager.
     */
    private final TethysEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The panel.
     */
    private final TethysUIBoxPaneManager thePanel;

    /**
     * The select button.
     */
    private final TethysUIScrollButtonManager<PayeeBucket> theButton;

    /**
     * Payee menu.
     */
    private final TethysUIScrollMenu<PayeeBucket> thePayeeMenu;

    /**
     * The active payee bucket list.
     */
    private PayeeBucketList thePayees;

    /**
     * The state.
     */
    private PayeeState theState;

    /**
     * The savePoint.
     */
    private PayeeState theSavePoint;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected MoneyWiseXPayeeAnalysisSelect(final TethysUIFactory<?> pFactory) {
        /* Create the button */
        theButton = pFactory.buttonFactory().newScrollButton(PayeeBucket.class);

        /* Create Event Manager */
        theEventManager = new TethysEventManager<>();

        /* Create the label */
        final TethysUILabel myLabel = pFactory.controlFactory().newLabel(NLS_PAYEE + TethysUIConstant.STR_COLON);

        /* Define the layout */
        thePanel = pFactory.paneFactory().newHBoxPane();
        thePanel.addSpacer();
        thePanel.addNode(myLabel);
        thePanel.addNode(theButton);

        /* Create initial state */
        theState = new PayeeState();
        theState.applyState();

        /* Access the menus */
        thePayeeMenu = theButton.getMenu();

        /* Create the listeners */
        final TethysEventRegistrar<TethysUIEvent> myRegistrar = theButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewPayee());
        theButton.setMenuConfigurator(e -> buildPayeeMenu());
    }

    @Override
    public TethysUIComponent getUnderlying() {
        return thePanel;
    }

    @Override
    public TethysEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public PayeeFilter getFilter() {
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
        theSavePoint = new PayeeState(theState);
    }

    /**
     * Restore SavePoint.
     */
    public void restoreSavePoint() {
        /* Restore the savePoint */
        theState = new PayeeState(theSavePoint);

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
     * @param pAnalysis the analysis.
     */
    public void setAnalysis(final Analysis pAnalysis) {
        /* Access buckets */
        thePayees = pAnalysis.getPayees();

        /* Obtain the current payee */
        PayeeBucket myPayee = theState.getPayee();

        /* Switch to versions from the analysis */
        myPayee = myPayee != null
                                  ? thePayees.getMatchingPayee(myPayee.getPayee())
                                  : thePayees.getDefaultPayee();

        /* Set the payee */
        theState.setThePayee(myPayee);
        theState.applyState();
    }

    @Override
    public void setFilter(final AnalysisFilter<?, ?> pFilter) {
        /* If this is the correct filter type */
        if (pFilter instanceof PayeeFilter) {
            /* Access filter */
            final PayeeFilter myFilter = (PayeeFilter) pFilter;

            /* Obtain the filter bucket */
            PayeeBucket myPayee = myFilter.getBucket();

            /* Obtain equivalent bucket */
            myPayee = thePayees.getMatchingPayee(myPayee.getPayee());

            /* Set the payee */
            theState.setThePayee(myPayee);
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
        TethysUIScrollItem<PayeeBucket> myActive = null;
        final PayeeBucket myCurr = theState.getPayee();

        /* Loop through the available payee values */
        final Iterator<PayeeBucket> myIterator = thePayees.iterator();
        while (myIterator.hasNext()) {
            final PayeeBucket myBucket = myIterator.next();

            /* Create a new MenuItem and add it to the popUp */
            final TethysUIScrollItem<PayeeBucket> myItem = thePayeeMenu.addItem(myBucket);

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
    private final class PayeeState {
        /**
         * The active PayeeBucket.
         */
        private PayeeBucket thePayee;

        /**
         * The active filter.
         */
        private PayeeFilter theFilter;

        /**
         * Constructor.
         */
        private PayeeState() {
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private PayeeState(final PayeeState pState) {
            /* Initialise state */
            thePayee = pState.getPayee();
            theFilter = pState.getFilter();
        }

        /**
         * Obtain the Payee Bucket.
         * @return the Payee
         */
        private PayeeBucket getPayee() {
            return thePayee;
        }

        /**
         * Obtain the Filter.
         * @return the Filter
         */
        private PayeeFilter getFilter() {
            return theFilter;
        }

        /**
         * Set new Payee.
         * @param pPayee the Payee
         * @return true/false did a change occur
         */
        private boolean setPayee(final PayeeBucket pPayee) {
            /* Adjust the selected payee */
            if (!MetisDataDifference.isEqual(pPayee, thePayee)) {
                setThePayee(pPayee);
                return true;
            }
            return false;
        }

        /**
         * Set the Payee.
         * @param pPayee the Payee
         */
        private void setThePayee(final PayeeBucket pPayee) {
            /* Store the payee */
            thePayee = pPayee;
            theFilter = thePayee != null
                                         ? new PayeeFilter(thePayee)
                                         : null;
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
