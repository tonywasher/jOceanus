/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2018 Tony Washer
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
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.TransactionTagBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.TransactionTagBucket.TransactionTagBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter.TagFilter;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysLabel;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * TransactionTag Selection.
 * @param <N> the node type
 * @param <I> the Icon Type
 */
public class MoneyWiseTransactionTagSelect<N, I>
        implements MoneyWiseAnalysisFilterSelection<N>, TethysEventProvider<PrometheusDataEvent> {
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
    private final TethysBoxPaneManager<N, I> thePanel;

    /**
     * The tag button.
     */
    private final TethysScrollButtonManager<TransactionTagBucket, N, I> theTagButton;

    /**
     * Tag menu.
     */
    private final TethysScrollMenu<TransactionTagBucket, I> theTagMenu;

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
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected MoneyWiseTransactionTagSelect(final TethysGuiFactory<N, I> pFactory) {
        /* Create the tags button */
        theTagButton = pFactory.newScrollButton();

        /* Create Event Manager */
        theEventManager = new TethysEventManager<>();

        /* Create the label */
        final TethysLabel<N, I> myTagLabel = pFactory.newLabel(NLS_TAG + TethysLabel.STR_COLON);

        /* Define the layout */
        thePanel = pFactory.newHBoxPane();
        thePanel.addSpacer();
        thePanel.addNode(myTagLabel);
        thePanel.addNode(theTagButton);

        /* Create initial state */
        theState = new TagState();
        theState.applyState();

        /* Create the listener */
        final TethysEventRegistrar<TethysUIEvent> myRegistrar = theTagButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewTag());
        theTagButton.setMenuConfigurator(e -> buildTagMenu());
        theTagMenu = theTagButton.getMenu();
    }

    @Override
    public Integer getId() {
        return thePanel.getId();
    }

    @Override
    public N getNode() {
        return thePanel.getNode();
    }

    @Override
    public TethysEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public TagFilter getFilter() {
        return theState.getFilter();
    }

    @Override
    public boolean isAvailable() {
        return theTags != null
               && !theTags.isEmpty();
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
        final boolean csAvailable = bEnabled && isAvailable();

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

        /* Switch to versions from the analysis */
        myTag = myTag != null
                              ? theTags.getMatchingTag(myTag.getTransTag())
                              : theTags.getDefaultTag();

        /* Set the tag */
        theState.setTheTag(myTag);
        theState.applyState();
    }

    @Override
    public void setFilter(final AnalysisFilter<?, ?> pFilter) {
        /* If this is the correct filter type */
        if (pFilter instanceof TagFilter) {
            /* Access filter */
            final TagFilter myFilter = (TagFilter) pFilter;

            /* Obtain the tag */
            TransactionTagBucket myTag = myFilter.getBucket();

            /* Obtain equivalent bucket */
            myTag = theTags.getMatchingTag(myTag.getTransTag());

            /* Set the tag */
            theState.setTheTag(myTag);
            theState.applyState();
        }
    }

    /**
     * Build Tag menu.
     */
    private void buildTagMenu() {
        /* Reset the popUp menu */
        theTagMenu.removeAllItems();

        /* Record active item */
        final TransactionTagBucket myCurrent = theState.getTag();
        TethysScrollMenuItem<TransactionTagBucket> myActive = null;

        /* Loop through the available tag values */
        final Iterator<TransactionTagBucket> myIterator = theTags.iterator();
        while (myIterator.hasNext()) {
            final TransactionTagBucket myTag = myIterator.next();

            /* Create a new JMenuItem and add it to the popUp */
            final TethysScrollMenuItem<TransactionTagBucket> myItem = theTagMenu.addItem(myTag);

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
         * The active filter.
         */
        private TagFilter theFilter;

        /**
         * Constructor.
         */
        private TagState() {
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private TagState(final TagState pState) {
            /* Initialise state */
            theTransTag = pState.getTag();
            theFilter = pState.getFilter();
        }

        /**
         * Obtain the TransactionTag.
         * @return the Tag
         */
        private TransactionTagBucket getTag() {
            return theTransTag;
        }

        /**
         * Obtain the Filter.
         * @return the Filter
         */
        private TagFilter getFilter() {
            return theFilter;
        }

        /**
         * Set new Tag.
         * @param pTag the Transaction Tag
         * @return true/false did a change occur
         */
        private boolean setTag(final TransactionTagBucket pTag) {
            /* Adjust the selected tag */
            if (!MetisDataDifference.isEqual(pTag, theTransTag)) {
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
            theFilter = theTransTag != null
                                            ? new TagFilter(theTransTag)
                                            : null;
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
