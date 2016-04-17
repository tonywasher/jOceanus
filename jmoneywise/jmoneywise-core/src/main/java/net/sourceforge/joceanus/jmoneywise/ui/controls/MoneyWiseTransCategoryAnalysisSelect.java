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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDifference;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.TransactionCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.TransactionCategoryBucket.TransactionCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.TransactionCategoryFilter;
import net.sourceforge.joceanus.jprometheus.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysLabel;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollSubMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * Transaction Category Analysis Selection.
 * @param <N> the node type
 * @param <I> the Icon Type
 */
public class MoneyWiseTransCategoryAnalysisSelect<N, I>
        implements MoneyWiseAnalysisFilterSelection<N>, TethysEventProvider<PrometheusDataEvent> {
    /**
     * Text for TransCategory Label.
     */
    private static final String NLS_CATEGORY = MoneyWiseDataType.TRANSCATEGORY.getItemName();

    /**
     * The Event Manager.
     */
    private final TethysEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The panel.
     */
    private final TethysBoxPaneManager<N, I> thePanel;

    /**
     * The select button.
     */
    private final TethysScrollButtonManager<TransactionCategoryBucket, N, I> theButton;

    /**
     * Category menu.
     */
    private final TethysScrollMenu<TransactionCategoryBucket, I> theCategoryMenu;

    /**
     * The active transaction categories bucket list.
     */
    private TransactionCategoryBucketList theCategories;

    /**
     * The state.
     */
    private EventState theState;

    /**
     * The savePoint.
     */
    private EventState theSavePoint;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected MoneyWiseTransCategoryAnalysisSelect(final TethysGuiFactory<N, I> pFactory) {
        /* Create the button */
        theButton = pFactory.newScrollButton();

        /* Create the label */
        TethysLabel<N, I> myLabel = pFactory.newLabel(NLS_CATEGORY + TethysLabel.STR_COLON);

        /* Create Event Manager */
        theEventManager = new TethysEventManager<>();

        /* Define the layout */
        thePanel = pFactory.newHBoxPane();
        thePanel.addSpacer();
        thePanel.addNode(myLabel);
        thePanel.addNode(theButton);

        /* Create initial state */
        theState = new EventState();
        theState.applyState();

        /* Access the menus */
        theCategoryMenu = theButton.getMenu();

        /* Create the listeners */
        TethysEventRegistrar<TethysUIEvent> myRegistrar = theButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewCategory());
        myRegistrar.addEventListener(TethysUIEvent.PREPAREDIALOG, e -> buildCategoryMenu());
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
    public TransactionCategoryFilter getFilter() {
        TransactionCategoryBucket myCategory = theState.getEventCategory();
        return myCategory != null
                                  ? new TransactionCategoryFilter(myCategory)
                                  : null;
    }

    @Override
    public boolean isAvailable() {
        return (theCategories != null) && !theCategories.isEmpty();
    }

    /**
     * Create SavePoint.
     */
    public void createSavePoint() {
        /* Create the savePoint */
        theSavePoint = new EventState(theState);
    }

    /**
     * Restore SavePoint.
     */
    public void restoreSavePoint() {
        /* Restore the savePoint */
        theState = new EventState(theSavePoint);

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
        theCategories = pAnalysis.getTransCategories();

        /* Obtain the current category */
        TransactionCategoryBucket myCategory = theState.getEventCategory();

        /* If we have a selected Category */
        if (myCategory != null) {
            /* Look for the equivalent bucket */
            myCategory = getMatchingBucket(myCategory);
        }

        /* If we do not have an active bucket and the list is non-empty */
        if ((myCategory == null) && (!theCategories.isEmpty())) {
            /* Use the first non-parent bucket */
            myCategory = getFirstCategory();
        }

        /* Set the category */
        theState.setTheCategory(myCategory);
        theState.applyState();
    }

    @Override
    public void setFilter(final AnalysisFilter<?, ?> pFilter) {
        /* If this is the correct filter type */
        if (pFilter instanceof TransactionCategoryFilter) {
            /* Access filter */
            TransactionCategoryFilter myFilter = (TransactionCategoryFilter) pFilter;

            /* Obtain the filter bucket */
            TransactionCategoryBucket myCategory = myFilter.getBucket();

            /* Obtain equivalent bucket */
            myCategory = getMatchingBucket(myCategory);

            /* Set the category */
            theState.setTheCategory(myCategory);
            theState.applyState();
        }
    }

    /**
     * Obtain first non-parent event category.
     * @return the first event category
     */
    private TransactionCategoryBucket getFirstCategory() {
        /* Loop through the available account values */
        Iterator<TransactionCategoryBucket> myIterator = theCategories.iterator();
        while (myIterator.hasNext()) {
            TransactionCategoryBucket myBucket = myIterator.next();

            /* Return if non-parent */
            if (!myBucket.getTransactionCategoryType().getCategoryClass().canParentCategory()) {
                return myBucket;
            }
        }

        /* No such account */
        return null;
    }

    /**
     * Obtain matching bucket.
     * @param pBucket the original bucket
     * @return the matching bucket
     */
    private TransactionCategoryBucket getMatchingBucket(final TransactionCategoryBucket pBucket) {
        /* Look up the matching CategoryBucket */
        TransactionCategory myCategory = pBucket.getTransactionCategory();
        TransactionCategoryBucket myBucket = theCategories.findItemById(myCategory.getOrderedId());

        /* If there is no such bucket in the analysis */
        if (myBucket == null) {
            /* Allocate an orphan bucket */
            myBucket = theCategories.getOrphanBucket(myCategory);
        }

        /* return the bucket */
        return myBucket;
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
        Map<String, TethysScrollSubMenu<TransactionCategoryBucket, ?>> myMap = new HashMap<>();

        /* Record active item */
        TransactionCategoryBucket myCurrent = theState.getEventCategory();
        TethysScrollMenuItem<TransactionCategoryBucket> myActive = null;

        /* Loop through the available category values */
        Iterator<TransactionCategoryBucket> myIterator = theCategories.iterator();
        while (myIterator.hasNext()) {
            TransactionCategoryBucket myBucket = myIterator.next();

            /* Only process low-level items */
            TransactionCategoryClass myClass = myBucket.getTransactionCategoryType().getCategoryClass();
            if (myClass.canParentCategory()) {
                continue;
            }

            /* Determine menu to add to */
            TransactionCategory myCategory = myBucket.getTransactionCategory();
            TransactionCategory myParent = myCategory.getParentCategory();
            String myParentName = myParent.getName();
            TethysScrollSubMenu<TransactionCategoryBucket, ?> myMenu = myMap.get(myParentName);

            /* If this is a new menu */
            if (myMenu == null) {
                /* Create a new JMenu and add it to the popUp */
                myMenu = theCategoryMenu.addSubMenu(myParentName);
                myMap.put(myParentName, myMenu);
            }

            /* Create a new MenuItem and add it to the popUp */
            TethysScrollMenuItem<TransactionCategoryBucket> myItem = myMenu.getSubMenu().addItem(myBucket, myCategory.getSubCategory());

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
    private final class EventState {
        /**
         * The active EventCategoryBucket.
         */
        private TransactionCategoryBucket theCategory;

        /**
         * Constructor.
         */
        private EventState() {
            /* Initialise the category */
            theCategory = null;
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private EventState(final EventState pState) {
            /* Initialise state */
            theCategory = pState.getEventCategory();
        }

        /**
         * Obtain the EventCategory Bucket.
         * @return the EventCategory
         */
        private TransactionCategoryBucket getEventCategory() {
            return theCategory;
        }

        /**
         * Set new Category.
         * @param pCategory the Category
         * @return true/false did a change occur
         */
        private boolean setCategory(final TransactionCategoryBucket pCategory) {
            /* Adjust the selected category */
            if (!MetisDifference.isEqual(pCategory, theCategory)) {
                setTheCategory(pCategory);
                return true;
            }
            return false;
        }

        /**
         * Set the Category.
         * @param pCategory the Category
         */
        private void setTheCategory(final TransactionCategoryBucket pCategory) {
            /* Store the selected category */
            theCategory = pCategory;
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
