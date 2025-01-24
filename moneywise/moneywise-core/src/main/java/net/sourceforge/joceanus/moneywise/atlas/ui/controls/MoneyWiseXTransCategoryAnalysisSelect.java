/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTransCategoryBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTransCategoryBucket.MoneyWiseXAnalysisTransCategoryBucketList;
import net.sourceforge.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter;
import net.sourceforge.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter.MoneyWiseXAnalysisTransCategoryFilter;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransCategory;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransCategoryClass;
import net.sourceforge.joceanus.oceanus.date.OceanusDateRange;
import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.prometheus.views.PrometheusDataEvent;
import net.sourceforge.joceanus.tethys.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.api.base.TethysUIConstant;
import net.sourceforge.joceanus.tethys.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.api.button.TethysUIScrollButtonManager;
import net.sourceforge.joceanus.tethys.api.control.TethysUILabel;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollItem;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollSubMenu;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIBoxPaneManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Transaction Category Analysis Selection.
 */
public class MoneyWiseXTransCategoryAnalysisSelect
        implements MoneyWiseXAnalysisFilterSelection, TethysEventProvider<PrometheusDataEvent> {
    /**
     * Text for TransCategory Label.
     */
    private static final String NLS_CATEGORY = MoneyWiseBasicDataType.TRANSCATEGORY.getItemName();

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
    private final TethysUIScrollButtonManager<MoneyWiseXAnalysisTransCategoryBucket> theButton;

    /**
     * Category menu.
     */
    private final TethysUIScrollMenu<MoneyWiseXAnalysisTransCategoryBucket> theCategoryMenu;

    /**
     * The active transaction categories bucket list.
     */
    private MoneyWiseXAnalysisTransCategoryBucketList theCategories;

    /**
     * The state.
     */
    private MoneyWiseEventState theState;

    /**
     * The savePoint.
     */
    private MoneyWiseEventState theSavePoint;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected MoneyWiseXTransCategoryAnalysisSelect(final TethysUIFactory<?> pFactory) {
        /* Create the button */
        theButton = pFactory.buttonFactory().newScrollButton(MoneyWiseXAnalysisTransCategoryBucket.class);

        /* Create the label */
        final TethysUILabel myLabel = pFactory.controlFactory().newLabel(NLS_CATEGORY + TethysUIConstant.STR_COLON);

        /* Create Event Manager */
        theEventManager = new OceanusEventManager<>();

        /* Define the layout */
        thePanel = pFactory.paneFactory().newHBoxPane();
        thePanel.addSpacer();
        thePanel.addNode(myLabel);
        thePanel.addNode(theButton);

        /* Create initial state */
        theState = new MoneyWiseEventState();
        theState.applyState();

        /* Access the menus */
        theCategoryMenu = theButton.getMenu();

        /* Create the listeners */
        final OceanusEventRegistrar<TethysUIEvent> myRegistrar = theButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewCategory());
        theButton.setMenuConfigurator(e -> buildCategoryMenu());
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
    public MoneyWiseXAnalysisTransCategoryFilter getFilter() {
        return theState.getFilter();
    }

    @Override
    public boolean isAvailable() {
        return theCategories != null
                && !theCategories.isEmpty();
    }

    /**
     * Create SavePoint.
     */
    public void createSavePoint() {
        /* Create the savePoint */
        theSavePoint = new MoneyWiseEventState(theState);
    }

    /**
     * Restore SavePoint.
     */
    public void restoreSavePoint() {
        /* Restore the savePoint */
        theState = new MoneyWiseEventState(theSavePoint);

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
    public void setAnalysis(final MoneyWiseXAnalysis pAnalysis) {
        /* Access buckets */
        theCategories = pAnalysis.getTransCategories();

        /* Obtain the current category */
        MoneyWiseXAnalysisTransCategoryBucket myCategory = theState.getEventCategory();

        /* Switch to versions from the analysis */
        myCategory = myCategory != null
                ? theCategories.getMatchingCategory(myCategory.getTransactionCategory())
                : theCategories.getDefaultCategory();

        /* Set the category */
        theState.setTheCategory(myCategory);
        theState.setDateRange(pAnalysis.getDateRange());
        theState.applyState();
    }

    @Override
    public void setFilter(final MoneyWiseXAnalysisFilter<?, ?> pFilter) {
        /* If this is the correct filter type */
        if (pFilter instanceof MoneyWiseXAnalysisTransCategoryFilter) {
            /* Access filter */
            final MoneyWiseXAnalysisTransCategoryFilter myFilter = (MoneyWiseXAnalysisTransCategoryFilter) pFilter;

            /* Obtain the filter bucket */
            MoneyWiseXAnalysisTransCategoryBucket myCategory = myFilter.getBucket();

            /* Obtain equivalent bucket */
            myCategory = theCategories.getMatchingCategory(myCategory.getTransactionCategory());

            /* Set the category */
            theState.setTheCategory(myCategory);
            theState.setDateRange(myFilter.getDateRange());
            theState.applyState();
        }
    }

    /**
     * Handle new Category.
     */
    private void handleNewCategory() {
        /* Select the new category */
        if (theState.setCategory(theButton.getValue())) {
            theState.applyState();
            theEventManager.fireEvent(PrometheusDataEvent.SELECTIONCHANGED);
        }
    }

    /**
     * Build Category menu.
     */
    private void buildCategoryMenu() {
        /* Reset the popUp menu */
        theCategoryMenu.removeAllItems();

        /* Create a simple map for top-level categories */
        final Map<String, TethysUIScrollSubMenu<MoneyWiseXAnalysisTransCategoryBucket>> myMap = new HashMap<>();

        /* Record active item */
        final MoneyWiseXAnalysisTransCategoryBucket myCurrent = theState.getEventCategory();
        TethysUIScrollItem<MoneyWiseXAnalysisTransCategoryBucket> myActive = null;

        /* Loop through the available category values */
        final Iterator<MoneyWiseXAnalysisTransCategoryBucket> myIterator = theCategories.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisTransCategoryBucket myBucket = myIterator.next();

            /* Only process low-level items */
            final MoneyWiseTransCategoryClass myClass = myBucket.getTransactionCategoryType().getCategoryClass();
            if (myClass.canParentCategory()) {
                continue;
            }

            /* Determine menu to add to */
            final MoneyWiseTransCategory myCategory = myBucket.getTransactionCategory();
            final MoneyWiseTransCategory myParent = myCategory.getParentCategory();
            final String myParentName = myParent.getName();
            final TethysUIScrollSubMenu<MoneyWiseXAnalysisTransCategoryBucket> myMenu = myMap.computeIfAbsent(myParentName, theCategoryMenu::addSubMenu);

            /* Create a new MenuItem and add it to the popUp */
            final TethysUIScrollItem<MoneyWiseXAnalysisTransCategoryBucket> myItem = myMenu.getSubMenu().addItem(myBucket, myCategory.getSubCategory());

            /* If this is the active category */
            if (myBucket.equals(myCurrent)) {
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
    private final class MoneyWiseEventState {
        /**
         * The active EventCategoryBucket.
         */
        private MoneyWiseXAnalysisTransCategoryBucket theCategory;

        /**
         * The dateRange.
         */
        private OceanusDateRange theDateRange;

        /**
         * The active Filter.
         */
        private MoneyWiseXAnalysisTransCategoryFilter theFilter;

        /**
         * Constructor.
         */
        private MoneyWiseEventState() {
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private MoneyWiseEventState(final MoneyWiseEventState pState) {
            /* Initialise state */
            theCategory = pState.getEventCategory();
            theDateRange = pState.getDateRange();
            theFilter = pState.getFilter();
        }

        /**
         * Obtain the EventCategory Bucket.
         * @return the EventCategory
         */
        private MoneyWiseXAnalysisTransCategoryBucket getEventCategory() {
            return theCategory;
        }

        /**
         * Obtain the dateRange.
         * @return the dateRange
         */
        private OceanusDateRange getDateRange() {
            return theDateRange;
        }

        /**
         * Obtain the EventCategory Filter.
         * @return the EventCategory
         */
        private MoneyWiseXAnalysisTransCategoryFilter getFilter() {
            return theFilter;
        }

        /**
         * Set new Category.
         * @param pCategory the Category
         * @return true/false did a change occur
         */
        private boolean setCategory(final MoneyWiseXAnalysisTransCategoryBucket pCategory) {
            /* Adjust the selected category */
            if (!MetisDataDifference.isEqual(pCategory, theCategory)) {
                setTheCategory(pCategory);
                return true;
            }
            return false;
        }

        /**
         * Set the Category.
         * @param pCategory the Category
         */
        private void setTheCategory(final MoneyWiseXAnalysisTransCategoryBucket pCategory) {
            /* Store the selected category */
            theCategory = pCategory;
            if (theCategory != null) {
                theFilter = new MoneyWiseXAnalysisTransCategoryFilter(theCategory);
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
            theButton.setValue(theCategory);
        }
    }
}
