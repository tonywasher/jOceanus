/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.ui.controls.swing;

import java.awt.Dimension;
import java.util.Iterator;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import net.sourceforge.joceanus.jmetis.data.MetisDifference;
import net.sourceforge.joceanus.jmetis.field.swing.MetisFieldElement;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.PayeeBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.PayeeBucket.PayeeBucketList;
import net.sourceforge.joceanus.jmoneywise.data.Payee;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.PayeeFilter;
import net.sourceforge.joceanus.jprometheus.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.ui.swing.JScrollButton.JScrollMenuBuilder;

/**
 * Payee Analysis Selection.
 */
public class PayeeAnalysisSelect
        extends JPanel
        implements AnalysisFilterSelection, TethysEventProvider<PrometheusDataEvent> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -8172530196737018124L;

    /**
     * Text for Payee Label.
     */
    private static final String NLS_PAYEE = MoneyWiseDataType.PAYEE.getItemName();

    /**
     * The Event Manager.
     */
    private final transient TethysEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The active payee bucket list.
     */
    private transient PayeeBucketList thePayees;

    /**
     * The state.
     */
    private transient PayeeState theState;

    /**
     * The savePoint.
     */
    private transient PayeeState theSavePoint;

    /**
     * The select button.
     */
    private final JScrollButton<PayeeBucket> theButton;

    /**
     * Payee menu builder.
     */
    private final JScrollMenuBuilder<PayeeBucket> thePayeeMenuBuilder;

    /**
     * Constructor.
     */
    public PayeeAnalysisSelect() {
        /* Create the button */
        theButton = new JScrollButton<>();

        /* Create Event Manager */
        theEventManager = new TethysEventManager<>();

        /* Create the label */
        JLabel myLabel = new JLabel(NLS_PAYEE + MetisFieldElement.STR_COLON);

        /* Define the layout */
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(Box.createHorizontalGlue());
        add(myLabel);
        add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE, 0)));
        add(theButton);
        add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE, 0)));

        /* Create initial state */
        theState = new PayeeState();
        theState.applyState();

        /* Create the listeners */
        thePayeeMenuBuilder = theButton.getMenuBuilder();
        thePayeeMenuBuilder.getEventRegistrar().addEventListener(e -> buildPayeeMenu());
        theButton.addPropertyChangeListener(JScrollButton.PROPERTY_VALUE, e -> handleNewPayee());
    }

    @Override
    public TethysEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public PayeeFilter getFilter() {
        PayeeBucket myPayee = theState.getPayee();
        return myPayee != null
                               ? new PayeeFilter(myPayee)
                               : null;
    }

    @Override
    public boolean isAvailable() {
        return (thePayees != null) && !thePayees.isEmpty();
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

    /**
     * Set analysis.
     * @param pAnalysis the analysis.
     */
    public void setAnalysis(final Analysis pAnalysis) {
        /* Access buckets */
        thePayees = pAnalysis.getPayees();

        /* Obtain the current payee */
        PayeeBucket myPayee = theState.getPayee();

        /* If we have a selected Payee */
        if (myPayee != null) {
            /* Look for the equivalent bucket */
            myPayee = getMatchingBucket(myPayee);
        }

        /* If we do not have an active bucket and the list is non-empty */
        if ((myPayee == null) && (!thePayees.isEmpty())) {
            /* Use the first bucket */
            myPayee = thePayees.peekFirst();
        }

        /* Set the payee */
        theState.setThePayee(myPayee);
        theState.applyState();
    }

    @Override
    public void setFilter(final AnalysisFilter<?, ?> pFilter) {
        /* If this is the correct filter type */
        if (pFilter instanceof PayeeFilter) {
            /* Access filter */
            PayeeFilter myFilter = (PayeeFilter) pFilter;

            /* Obtain the filter bucket */
            PayeeBucket myPayee = myFilter.getBucket();

            /* Obtain equivalent bucket */
            myPayee = getMatchingBucket(myPayee);

            /* Set the payee */
            theState.setThePayee(myPayee);
            theState.applyState();
        }
    }

    /**
     * Obtain matching bucket.
     * @param pBucket the original bucket
     * @return the matching bucket
     */
    private PayeeBucket getMatchingBucket(final PayeeBucket pBucket) {
        /* Look up the matching PayeeBucket */
        Payee myPayee = pBucket.getPayee();
        PayeeBucket myBucket = thePayees.findItemById(myPayee.getOrderedId());

        /* If there is no such bucket in the analysis */
        if (myBucket == null) {
            /* Allocate an orphan bucket */
            myBucket = thePayees.getOrphanBucket(myPayee);
        }

        /* return the bucket */
        return myBucket;
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
        thePayeeMenuBuilder.clearMenu();

        /* Record active item */
        JMenuItem myActive = null;
        PayeeBucket myCurr = theState.getPayee();

        /* Loop through the available payee values */
        Iterator<PayeeBucket> myIterator = thePayees.iterator();
        while (myIterator.hasNext()) {
            PayeeBucket myBucket = myIterator.next();

            /* Create a new JMenuItem and add it to the popUp */
            JMenuItem myItem = thePayeeMenuBuilder.addItem(myBucket);

            /* If this is the active bucket */
            if (myBucket.equals(myCurr)) {
                /* Record it */
                myActive = myItem;
            }
        }

        /* Ensure active item is visible */
        thePayeeMenuBuilder.showItem(myActive);
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
         * Constructor.
         */
        private PayeeState() {
            /* Initialise the payee */
            thePayee = null;
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private PayeeState(final PayeeState pState) {
            /* Initialise state */
            thePayee = pState.getPayee();
        }

        /**
         * Obtain the Payee Bucket.
         * @return the Payee
         */
        private PayeeBucket getPayee() {
            return thePayee;
        }

        /**
         * Set new Payee.
         * @param pPayee the Payee
         * @return true/false did a change occur
         */
        private boolean setPayee(final PayeeBucket pPayee) {
            /* Adjust the selected payee */
            if (!MetisDifference.isEqual(pPayee, thePayee)) {
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
