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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jmetis.field.JFieldElement;
import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisBucket.TaxBasisBucketList;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.TaxBasisFilter;
import net.sourceforge.joceanus.jtethys.event.JEventPanel;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;

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
     * Text for TaxBasis Label.
     */
    private static final String NLS_BASIS = MoneyWiseDataType.TAXBASIS.getItemName();

    /**
     * The active tax basis bucket list.
     */
    private transient TaxBasisBucketList theTaxBases;

    /**
     * The state.
     */
    private transient TaxBasisState theState;

    /**
     * The savePoint.
     */
    private transient TaxBasisState theSavePoint;

    /**
     * The select button.
     */
    private final JScrollButton<TaxBasisBucket> theButton;

    /**
     * Constructor.
     */
    public TaxBasisAnalysisSelect() {
        /* Create the button */
        theButton = new JScrollButton<TaxBasisBucket>();

        /* Create the label */
        JLabel myLabel = new JLabel(NLS_BASIS + JFieldElement.STR_COLON);

        /* Define the layout */
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(Box.createHorizontalGlue());
        add(myLabel);
        add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE, 0)));
        add(theButton);
        add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE, 0)));

        /* Create initial state */
        theState = new TaxBasisState();
        theState.applyState();

        /* Create the listener */
        theButton.addPropertyChangeListener(JScrollButton.PROPERTY_VALUE, new ButtonListener());
    }

    @Override
    public TaxBasisFilter getFilter() {
        TaxBasisBucket myBasis = theState.getTaxBasis();
        return myBasis != null
                              ? new TaxBasisFilter(myBasis)
                              : null;
    }

    @Override
    public boolean isAvailable() {
        return (theTaxBases != null) && !theTaxBases.isEmpty();
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
        theButton.setEnabled(bEnabled && isAvailable());
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
        if ((myBasis == null) && (!theTaxBases.isEmpty())) {
            /* Use the first bucket */
            myBasis = theTaxBases.peekFirst();
        }

        /* Set the basis */
        theState.setTaxBasis(myBasis);
        theState.applyState();
    }

    @Override
    public void setFilter(final AnalysisFilter<?, ?> pFilter) {
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
            implements PropertyChangeListener, ChangeListener {
        /**
         * Tax menu builder.
         */
        private final JScrollMenuBuilder<TaxBasisBucket> theTaxMenuBuilder;

        /**
         * Constructor.
         */
        private ButtonListener() {
            /* Access builders */
            theTaxMenuBuilder = theButton.getMenuBuilder();
            theTaxMenuBuilder.addChangeListener(this);
        }

        @Override
        public void stateChanged(final ChangeEvent pEvent) {
            /* Access source of the event */
            Object o = pEvent.getSource();

            /* Handle builders */
            if (theTaxMenuBuilder.equals(o)) {
                buildBasisMenu();
            }
        }

        /**
         * Build Basis menu.
         */
        private void buildBasisMenu() {
            /* Reset the popUp menu */
            theTaxMenuBuilder.clearMenu();

            /* Record active item */
            JMenuItem myActive = null;
            TaxBasisBucket myCurr = theState.getTaxBasis();

            /* Loop through the available category values */
            Iterator<TaxBasisBucket> myIterator = theTaxBases.iterator();
            while (myIterator.hasNext()) {
                TaxBasisBucket myBucket = myIterator.next();

                /* Create a new JMenuItem and add it to the popUp */
                JMenuItem myItem = theTaxMenuBuilder.addItem(myBucket);

                /* If this is the active bucket */
                if (myBucket.equals(myCurr)) {
                    /* Record it */
                    myActive = myItem;
                }
            }

            /* Ensure active item is visible */
            theTaxMenuBuilder.showItem(myActive);
        }

        @Override
        public void propertyChange(final PropertyChangeEvent pEvent) {
            /* Access the source */
            Object o = pEvent.getSource();

            /* If this is the tax button */
            if (theButton.equals(o)) {
                /* Select the new basis */
                if (theState.setTaxBasis(theButton.getValue())) {
                    theState.applyState();
                    fireStateChanged();
                }
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
         * Obtain the TaxBasis Bucket.
         * @return the Basis
         */
        private TaxBasisBucket getTaxBasis() {
            return theBasis;
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
            theButton.setValue(theBasis);
        }
    }
}
