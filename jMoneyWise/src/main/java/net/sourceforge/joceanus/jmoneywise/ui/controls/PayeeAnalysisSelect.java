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
package net.sourceforge.joceanus.jmoneywise.ui.controls;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import net.sourceforge.joceanus.jdatamanager.Difference;
import net.sourceforge.joceanus.jeventmanager.JEventPanel;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.PayeeBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.PayeeBucket.PayeeBucketList;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.PayeeFilter;

/**
 * Payee Analysis Selection.
 */
public class PayeeAnalysisSelect
        extends JEventPanel
        implements AnalysisFilterSelection {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -8172530196737018124L;

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(PayeeAnalysisSelect.class.getName());

    /**
     * Text for Payee Label.
     */
    private static final String NLS_PAYEE = NLS_BUNDLE.getString("Payee");

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
     * The select button.
     */
    private final JButton theButton;

    @Override
    public PayeeFilter getFilter() {
        return new PayeeFilter(theState.getPayee());
    }

    @Override
    public boolean isAvailable() {
        return (thePayees != null)
               && !thePayees.isEmpty();
    }

    /**
     * Constructor.
     */
    public PayeeAnalysisSelect() {
        /* Create the button */
        theButton = new JButton();

        /* Create the label */
        JLabel myLabel = new JLabel(NLS_PAYEE);

        /* Define the layout */
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE, 0)));
        add(myLabel);
        add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE, 0)));
        add(theButton);
        add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE, 0)));

        /* Create initial state */
        theState = new PayeeState();
        theState.applyState();

        /* Create the listener */
        theButton.addActionListener(new ButtonListener());
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
        theButton.setEnabled(bEnabled
                             && isAvailable());
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
            myPayee = thePayees.findItemById(myPayee.getOrderedId());
        }

        /* If we do not have an active bucket and the list is non-empty */
        if ((myPayee == null)
            && (!thePayees.isEmpty())) {
            /* Use the first bucket */
            myPayee = thePayees.peekFirst();
        }

        /* Set the payee */
        theState.setPayee(myPayee);
        theState.applyState();
    }

    /**
     * Listener class.
     */
    private final class ButtonListener
            implements ActionListener {
        @Override
        public void actionPerformed(final ActionEvent evt) {
            /* Access source of the event */
            Object o = evt.getSource();

            /* Handle buttons */
            if (theButton.equals(o)) {
                showPayeeMenu();
            }
        }

        /**
         * Show Payee menu.
         */
        private void showPayeeMenu() {
            /* Create a new popUp menu */
            JPopupMenu myPopUp = new JPopupMenu();

            /* Loop through the available payee values */
            Iterator<PayeeBucket> myIterator = thePayees.iterator();
            while (myIterator.hasNext()) {
                PayeeBucket myBucket = myIterator.next();

                /* Create a new JMenuItem and add it to the popUp */
                PayeeAction myAction = new PayeeAction(myBucket);
                JMenuItem myItem = new JMenuItem(myAction);
                myPopUp.add(myItem);
            }

            /* Show the Payee menu in the correct place */
            Rectangle myLoc = theButton.getBounds();
            myPopUp.show(theButton, 0, myLoc.height);
        }
    }

    /**
     * Payee action class.
     */
    private final class PayeeAction
            extends AbstractAction {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -7782620967078519039L;

        /**
         * Payee.
         */
        private final PayeeBucket thePayee;

        /**
         * Constructor.
         * @param pPayee the payee bucket
         */
        private PayeeAction(final PayeeBucket pPayee) {
            super(pPayee.getName());
            thePayee = pPayee;
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            /* Select the new payee */
            if (theState.setPayee(thePayee)) {
                theState.applyState();
                fireStateChanged();
            }
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
         * Obtain the Payee Bucket.
         * @return the Payee
         */
        private PayeeBucket getPayee() {
            return thePayee;
        }

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
         * Set new Payee.
         * @param pPayee the Payee
         * @return true/false did a change occur
         */
        private boolean setPayee(final PayeeBucket pPayee) {
            /* Adjust the selected payee */
            if (!Difference.isEqual(pPayee, thePayee)) {
                thePayee = pPayee;
                return true;
            }
            return false;
        }

        /**
         * Apply the State.
         */
        private void applyState() {
            /* Adjust the lock-down */
            setEnabled(true);
            theButton.setText((thePayee == null)
                    ? null
                    : thePayee.getName());
        }
    }
}
