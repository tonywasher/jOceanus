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
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sourceforge.joceanus.jmetis.data.MetisDifference;
import net.sourceforge.joceanus.jmetis.field.swing.MetisFieldElement;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.TransactionTagBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.TransactionTagBucket.TransactionTagBucketList;
import net.sourceforge.joceanus.jmoneywise.data.TransactionTag;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.TagFilter;
import net.sourceforge.joceanus.jprometheus.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollButton.TethysSwingScrollButtonManager;

/**
 * TransactionTag Selection.
 */
public class TransactionTagSelect
        implements AnalysisFilterSelection<JComponent>, TethysEventProvider<PrometheusDataEvent> {
    /**
     * Text for TransactionTag Label.
     */
    private static final String NLS_TAG = MoneyWiseDataType.TRANSTAG.getItemName();

    /**
     * The Event Manager.
     */
    private final TethysEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The panel.
     */
    private final JPanel thePanel;

    /**
     * The active transaction tag list.
     */
    private TransactionTagBucketList theTags;

    /**
     * The state.
     */
    private TagState theState;

    /**
     * The savePoint.
     */
    private TagState theSavePoint;

    /**
     * The tag button.
     */
    private final TethysSwingScrollButtonManager<TransactionTagBucket> theTagButton;

    /**
     * Tag menu.
     */
    private final TethysScrollMenu<TransactionTagBucket, ?> theTagMenu;

    /**
     * Constructor.
     */
    public TransactionTagSelect() {
        /* Create the tags button */
        theTagButton = new TethysSwingScrollButtonManager<>();

        /* Create Event Manager */
        theEventManager = new TethysEventManager<>();

        /* Create the label */
        JLabel myTagLabel = new JLabel(NLS_TAG + MetisFieldElement.STR_COLON);

        /* Define the layout */
        thePanel = new JPanel();
        thePanel.setLayout(new BoxLayout(thePanel, BoxLayout.X_AXIS));
        thePanel.add(Box.createHorizontalGlue());
        thePanel.add(myTagLabel);
        thePanel.add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE, 0)));
        thePanel.add(theTagButton.getNode());
        thePanel.add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE, 0)));

        /* Create initial state */
        theState = new TagState();
        theState.applyState();

        /* Create the listener */
        TethysEventRegistrar<TethysUIEvent> myRegistrar = theTagButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewTag());
        myRegistrar.addEventListener(TethysUIEvent.PREPAREDIALOG, e -> buildTagMenu());
        theTagMenu = theTagButton.getMenu();
    }

    @Override
    public JComponent getNode() {
        return thePanel;
    }

    @Override
    public TethysEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public TagFilter getFilter() {
        TransactionTagBucket myTag = theState.getTag();
        return myTag != null
                             ? new TagFilter(myTag)
                             : null;
    }

    @Override
    public boolean isAvailable() {
        return (theTags != null) && !theTags.isEmpty();
    }

    /**
     * Create SavePoint.
     */
    protected void createSavePoint() {
        /* Create the savePoint */
        theSavePoint = new TagState(theState);
    }

    /**
     * Restore SavePoint.
     */
    protected void restoreSavePoint() {
        /* Restore the savePoint */
        theState = new TagState(theSavePoint);

        /* Apply the state */
        theState.applyState();
    }

    @Override
    public void setEnabled(final boolean bEnabled) {
        /* Determine whether there are any Accounts to select */
        boolean csAvailable = bEnabled && isAvailable();

        /* Pass call on to buttons */
        theTagButton.setEnabled(csAvailable);
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
        theTags = pAnalysis.getTransactionTags();

        /* Obtain the current tag */
        TransactionTagBucket myTag = theState.getTag();

        /* If we have a selected Tag */
        if (myTag != null) {
            /* Look for the equivalent tag */
            myTag = getMatchingBucket(myTag);
        }

        /* If we do not have an active tag and the list is non-empty */
        if ((myTag == null) && (!theTags.isEmpty())) {
            /* Determine the next tag */
            myTag = theTags.peekFirst();
        }

        /* Set the tag */
        theState.setTheTag(myTag);
        theState.applyState();
    }

    @Override
    public void setFilter(final AnalysisFilter<?, ?> pFilter) {
        /* If this is the correct filter type */
        if (pFilter instanceof TagFilter) {
            /* Access filter */
            TagFilter myFilter = (TagFilter) pFilter;

            /* Obtain the tag */
            TransactionTagBucket myTag = myFilter.getBucket();

            /* Obtain equivalent bucket */
            myTag = getMatchingBucket(myTag);

            /* Set the tag */
            theState.setTheTag(myTag);
            theState.applyState();
        }
    }

    /**
     * Obtain matching bucket.
     * @param pBucket the original bucket
     * @return the matching bucket
     */
    private TransactionTagBucket getMatchingBucket(final TransactionTagBucket pBucket) {
        /* Look up the matching TagBucket */
        TransactionTag myTag = pBucket.getTransTag();
        TransactionTagBucket myBucket = theTags.findItemById(myTag.getOrderedId());

        /* If there is no such bucket in the analysis */
        if (myBucket == null) {
            /* Allocate an orphan bucket */
            myBucket = theTags.getOrphanBucket(myTag);
        }

        /* return the bucket */
        return myBucket;
    }

    /**
     * Build Tag menu.
     */
    private void buildTagMenu() {
        /* Reset the popUp menu */
        theTagMenu.removeAllItems();

        /* Record active item */
        TransactionTagBucket myCurrent = theState.getTag();
        TethysScrollMenuItem<TransactionTagBucket> myActive = null;

        /* Loop through the available tag values */
        Iterator<TransactionTagBucket> myIterator = theTags.iterator();
        while (myIterator.hasNext()) {
            TransactionTagBucket myTag = myIterator.next();

            /* Create a new JMenuItem and add it to the popUp */
            TethysScrollMenuItem<TransactionTagBucket> myItem = theTagMenu.addItem(myTag);

            /* If this is the active category */
            if (myTag.equals(myCurrent)) {
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
     * Handle new Tag.
     */
    private void handleNewTag() {
        /* Select the new tag */
        if (theState.setTag(theTagButton.getValue())) {
            theState.applyState();
            theEventManager.fireEvent(PrometheusDataEvent.SELECTIONCHANGED);
        }
    }

    /**
     * SavePoint values.
     */
    private final class TagState {
        /**
         * The active Tag.
         */
        private TransactionTagBucket theTransTag;

        /**
         * Constructor.
         */
        private TagState() {
            /* Initialise the tag */
            theTransTag = null;
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private TagState(final TagState pState) {
            /* Initialise state */
            theTransTag = pState.getTag();
        }

        /**
         * Obtain the TransactionTag.
         * @return the Tag
         */
        private TransactionTagBucket getTag() {
            return theTransTag;
        }

        /**
         * Set new Tag.
         * @param pTag the Transaction Tag
         * @return true/false did a change occur
         */
        private boolean setTag(final TransactionTagBucket pTag) {
            /* Adjust the selected tag */
            if (!MetisDifference.isEqual(pTag, theTransTag)) {
                /* Store the tag */
                setTheTag(pTag);
                return true;
            }
            return false;
        }

        /**
         * Set the Tag.
         * @param pTag the Tag
         */
        private void setTheTag(final TransactionTagBucket pTag) {
            /* Store the tag */
            theTransTag = pTag;
        }

        /**
         * Apply the State.
         */
        private void applyState() {
            /* Adjust the lock-down */
            setEnabled(true);
            theTagButton.setValue(theTransTag);
        }
    }
}
