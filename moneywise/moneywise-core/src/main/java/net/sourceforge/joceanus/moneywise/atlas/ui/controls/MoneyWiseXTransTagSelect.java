/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.moneywise.atlas.ui.controls;

import net.sourceforge.joceanus.metis.data.MetisDataDifference;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTransTagBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTransTagBucket.MoneyWiseXAnalysisTransTagBucketList;
import net.sourceforge.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter;
import net.sourceforge.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter.MoneyWiseXAnalysisTagFilter;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.oceanus.date.OceanusDateRange;
import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar.OceanusEventProvider;
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
 * TransactionTag Selection.
 */
public class MoneyWiseXTransTagSelect
        implements MoneyWiseXAnalysisFilterSelection, OceanusEventProvider<PrometheusDataEvent> {
    /**
     * Text for TransactionTag Label.
     */
    private static final String NLS_TAG = MoneyWiseBasicDataType.TRANSTAG.getItemName();

    /**
     * The Event Manager.
     */
    private final OceanusEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The panel.
     */
    private final TethysUIBoxPaneManager thePanel;

    /**
     * The tag button.
     */
    private final TethysUIScrollButtonManager<MoneyWiseXAnalysisTransTagBucket> theTagButton;

    /**
     * Tag menu.
     */
    private final TethysUIScrollMenu<MoneyWiseXAnalysisTransTagBucket> theTagMenu;

    /**
     * The active transaction tag list.
     */
    private MoneyWiseXAnalysisTransTagBucketList theTags;

    /**
     * The state.
     */
    private MoneyWiseTagState theState;

    /**
     * The savePoint.
     */
    private MoneyWiseTagState theSavePoint;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected MoneyWiseXTransTagSelect(final TethysUIFactory<?> pFactory) {
        /* Create the tags button */
        theTagButton = pFactory.buttonFactory().newScrollButton(MoneyWiseXAnalysisTransTagBucket.class);

        /* Create Event Manager */
        theEventManager = new OceanusEventManager<>();

        /* Create the label */
        final TethysUILabel myTagLabel = pFactory.controlFactory().newLabel(NLS_TAG + TethysUIConstant.STR_COLON);

        /* Define the layout */
        thePanel = pFactory.paneFactory().newHBoxPane();
        thePanel.addSpacer();
        thePanel.addNode(myTagLabel);
        thePanel.addNode(theTagButton);

        /* Create initial state */
        theState = new MoneyWiseTagState();
        theState.applyState();

        /* Create the listener */
        final OceanusEventRegistrar<TethysUIEvent> myRegistrar = theTagButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewTag());
        theTagButton.setMenuConfigurator(e -> buildTagMenu());
        theTagMenu = theTagButton.getMenu();
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
    public MoneyWiseXAnalysisTagFilter getFilter() {
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
        theSavePoint = new MoneyWiseTagState(theState);
    }

    /**
     * Restore SavePoint.
     */
    protected void restoreSavePoint() {
        /* Restore the savePoint */
        theState = new MoneyWiseTagState(theSavePoint);

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
    public void setAnalysis(final MoneyWiseXAnalysis pAnalysis) {
        /* Access buckets */
        theTags = pAnalysis.getTransactionTags();

        /* Obtain the current tag */
        MoneyWiseXAnalysisTransTagBucket myTag = theState.getTag();

        /* Switch to versions from the analysis */
        myTag = myTag != null
                ? theTags.getMatchingTag(myTag.getTransTag())
                : theTags.getDefaultTag();

        /* Set the tag */
        theState.setTheTag(myTag);
        theState.setDateRange(pAnalysis.getDateRange());
        theState.applyState();
    }

    @Override
    public void setFilter(final MoneyWiseXAnalysisFilter<?, ?> pFilter) {
        /* If this is the correct filter type */
        if (pFilter instanceof MoneyWiseXAnalysisTagFilter myFilter) {
            /* Obtain the tag */
            MoneyWiseXAnalysisTransTagBucket myTag = myFilter.getBucket();

            /* Obtain equivalent bucket */
            myTag = theTags.getMatchingTag(myTag.getTransTag());

            /* Set the tag */
            theState.setTheTag(myTag);
            theState.setDateRange(myFilter.getDateRange());
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
        final MoneyWiseXAnalysisTransTagBucket myCurrent = theState.getTag();
        TethysUIScrollItem<MoneyWiseXAnalysisTransTagBucket> myActive = null;

        /* Loop through the available tag values */
        final Iterator<MoneyWiseXAnalysisTransTagBucket> myIterator = theTags.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisTransTagBucket myTag = myIterator.next();

            /* Create a new MenuItem and add it to the popUp */
            final TethysUIScrollItem<MoneyWiseXAnalysisTransTagBucket> myItem = theTagMenu.addItem(myTag);

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
    private final class MoneyWiseTagState {
        /**
         * The active Tag.
         */
        private MoneyWiseXAnalysisTransTagBucket theTransTag;

        /**
         * The dateRange.
         */
        private OceanusDateRange theDateRange;

        /**
         * The active filter.
         */
        private MoneyWiseXAnalysisTagFilter theFilter;

        /**
         * Constructor.
         */
        private MoneyWiseTagState() {
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private MoneyWiseTagState(final MoneyWiseTagState pState) {
            /* Initialise state */
            theTransTag = pState.getTag();
            theDateRange = pState.getDateRange();
            theFilter = pState.getFilter();
        }

        /**
         * Obtain the TransactionTag.
         * @return the Tag
         */
        private MoneyWiseXAnalysisTransTagBucket getTag() {
            return theTransTag;
        }

        /**
         * Obtain the dateRange.
         * @return the dateRange
         */
        private OceanusDateRange getDateRange() {
            return theDateRange;
        }

        /**
         * Obtain the Filter.
         * @return the Filter
         */
        private MoneyWiseXAnalysisTagFilter getFilter() {
            return theFilter;
        }

        /**
         * Set new Tag.
         * @param pTag the Transaction Tag
         * @return true/false did a change occur
         */
        private boolean setTag(final MoneyWiseXAnalysisTransTagBucket pTag) {
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
        private void setTheTag(final MoneyWiseXAnalysisTransTagBucket pTag) {
            /* Store the tag */
            theTransTag = pTag;
            if (theTransTag != null) {
                theFilter = new MoneyWiseXAnalysisTagFilter(theTransTag);
                theFilter.setDateRange(theDateRange);
            } else {
                theFilter = null;
            }
        }

        /**
         * Set the dateRange.
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
            theTagButton.setValue(theTransTag);
        }
    }
}
