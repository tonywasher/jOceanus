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
package net.sourceforge.joceanus.jmoneywise.ui.controls;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import net.sourceforge.joceanus.jdatamanager.Difference;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisBucket.TaxBasisBucketList;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.TaxBasisFilter;
import net.sourceforge.joceanus.jtethys.event.JEventPanel;
import net.sourceforge.joceanus.jtethys.swing.ArrowIcon;

/**
 * TaxBasisAnalysis Selection.
 */
public class TaxBasisAnalysisSelect
        extends JEventPanel
        implements AnalysisFilterSelection {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 2653125674925955281L;

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(TaxBasisAnalysisSelect.class.getName());

    /**
     * Text for TaxBasis Label.
     */
    private static final String NLS_BASIS = NLS_BUNDLE.getString("TaxBasis");

    /**
     * The active tax basis bucket list.
     */
    private TaxBasisBucketList theTaxBases;

    /**
     * The state.
     */
    private TaxBasisState theState;

    /**
     * The savePoint.
     */
    private TaxBasisState theSavePoint;

    /**
     * The select button.
     */
    private final JButton theButton;

    @Override
    public TaxBasisFilter getFilter() {
        return new TaxBasisFilter(theState.getTaxBasis());
    }

    @Override
    public boolean isAvailable() {
        return (theTaxBases != null)
               && !theTaxBases.isEmpty();
    }

    /**
     * Constructor.
     */
    public TaxBasisAnalysisSelect() {
        /* Create the button */
        theButton = new JButton(ArrowIcon.DOWN);
        theButton.setVerticalTextPosition(AbstractButton.CENTER);
        theButton.setHorizontalTextPosition(AbstractButton.LEFT);

        /* Create the label */
        JLabel myLabel = new JLabel(NLS_BASIS);

        /* Define the layout */
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE, 0)));
        add(myLabel);
        add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE, 0)));
        add(theButton);
        add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE, 0)));

        /* Create initial state */
        theState = new TaxBasisState();
        theState.applyState();

        /* Create the listener */
        theButton.addActionListener(new ButtonListener());
    }

    /**
     * Create SavePoint.
     */
    public void createSavePoint() {
        /* Create the savePoint */
        theSavePoint = new TaxBasisState(theState);
    }

    /**
     * Restore SavePoint.
     */
    public void restoreSavePoint() {
        /* Restore the savePoint */
        theState = new TaxBasisState(theSavePoint);

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
        theTaxBases = pAnalysis.getTaxBasis();

        /* Obtain the current basis */
        TaxBasisBucket myBasis = theState.getTaxBasis();

        /* If we have a selected TaxBasis */
        if (myBasis != null) {
            /* Look for the equivalent bucket */
            myBasis = theTaxBases.findItemById(myBasis.getOrderedId());
        }

        /* If we do not have an active bucket and the list is non-empty */
        if ((myBasis == null)
            && (!theTaxBases.isEmpty())) {
            /* Use the first bucket */
            myBasis = theTaxBases.peekFirst();
        }

        /* Set the basis */
        theState.setTaxBasis(myBasis);
        theState.applyState();
    }

    @Override
    public void setFilter(final AnalysisFilter<?> pFilter) {
        /* If this is the correct filter type */
        if (pFilter instanceof TaxBasisFilter) {
            /* Access filter */
            TaxBasisFilter myFilter = (TaxBasisFilter) pFilter;

            /* Obtain the filter bucket */
            TaxBasisBucket myTaxBasis = myFilter.getBucket();

            /* Obtain equivalent bucket */
            myTaxBasis = theTaxBases.findItemById(myTaxBasis.getOrderedId());

            /* Set the taxBasis */
            theState.setTaxBasis(myTaxBasis);
            theState.applyState();
        }
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
                showBasisMenu();
            }
        }

        /**
         * Show Basis menu.
         */
        private void showBasisMenu() {
            /* Create a new popUp menu */
            JPopupMenu myPopUp = new JPopupMenu();

            /* Loop through the available category values */
            Iterator<TaxBasisBucket> myIterator = theTaxBases.iterator();
            while (myIterator.hasNext()) {
                TaxBasisBucket myBucket = myIterator.next();

                /* Create a new JMenuItem and add it to the popUp */
                TaxBasisAction myAction = new TaxBasisAction(myBucket);
                JMenuItem myItem = new JMenuItem(myAction);
                myPopUp.add(myItem);
            }

            /* Show the Category menu in the correct place */
            Rectangle myLoc = theButton.getBounds();
            myPopUp.show(theButton, 0, myLoc.height);
        }
    }

    /**
     * TaxBasis Action class.
     */
    private final class TaxBasisAction
            extends AbstractAction {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -8236563867539368503L;

        /**
         * Tax Basis.
         */
        private final TaxBasisBucket theBasis;

        /**
         * Constructor.
         * @param pTaxBasis the tax basis bucket
         */
        private TaxBasisAction(final TaxBasisBucket pTaxBasis) {
            super(pTaxBasis.getName());
            theBasis = pTaxBasis;
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            /* Select the new tax basis */
            if (theState.setTaxBasis(theBasis)) {
                theState.applyState();
                fireStateChanged();
            }
        }
    }

    /**
     * SavePoint values.
     */
    private final class TaxBasisState {
        /**
         * The active TaxBasisBucket.
         */
        private TaxBasisBucket theBasis;

        /**
         * Obtain the TaxBasis Bucket.
         * @return the Basis
         */
        private TaxBasisBucket getTaxBasis() {
            return theBasis;
        }

        /**
         * Constructor.
         */
        private TaxBasisState() {
            /* Initialise the basis */
            theBasis = null;
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private TaxBasisState(final TaxBasisState pState) {
            /* Initialise state */
            theBasis = pState.getTaxBasis();
        }

        /**
         * Set new TaxBasis.
         * @param pTaxBasis the TaxBasis
         * @return true/false did a change occur
         */
        private boolean setTaxBasis(final TaxBasisBucket pTaxBasis) {
            /* Adjust the selected taxBasis */
            if (!Difference.isEqual(pTaxBasis, theBasis)) {
                theBasis = pTaxBasis;
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
            theButton.setText((theBasis == null)
                    ? null
                    : theBasis.getName());
        }
    }
}
