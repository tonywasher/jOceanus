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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.TransactionCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.TransactionCategoryBucket.TransactionCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter.TransactionCategoryFilter;
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
        final TethysLabel<N, I> myLabel = pFactory.newLabel(NLS_CATEGORY + TethysLabel.STR_COLON);

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
        final TethysEventRegistrar<TethysUIEvent> myRegistrar = theButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewCategory());
        theButton.setMenuConfigurator(e -> buildCategoryMenu());
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

        /* Switch to versions from the analysis */
        myCategory = myCategory != null
                                        ? theCategories.getMatchingCategory(myCategory.getTransactionCategory())
                                        : theCategories.getDefaultCategory();

        /* Set the category */
        theState.setTheCategory(myCategory);
        theState.applyState();
    }

    @Override
    public void setFilter(final AnalysisFilter<?, ?> pFilter) {
        /* If this is the correct filter type */
        if (pFilter instanceof TransactionCategoryFilter) {
            /* Access filter */
            final TransactionCategoryFilter myFilter = (TransactionCategoryFilter) pFilter;

            /* Obtain the filter bucket */
            TransactionCategoryBucket myCategory = myFilter.getBucket();

            /* Obtain equivalent bucket */
            myCategory = theCategories.getMatchingCategory(myCategory.getTransactionCategory());

            /* Set the category */
            theState.setTheCategory(myCategory);
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
        final Map<String, TethysScrollSubMenu<TransactionCategoryBucket, ?>> myMap = new HashMap<>();

        /* Record active item */
        final TransactionCategoryBucket myCurrent = theState.getEventCategory();
        TethysScrollMenuItem<TransactionCategoryBucket> myActive = null;

        /* Loop through the available category values */
        final Iterator<TransactionCategoryBucket> myIterator = theCategories.iterator();
        while (myIterator.hasNext()) {
            final TransactionCategoryBucket myBucket = myIterator.next();

            /* Only process low-level items */
            final TransactionCategoryClass myClass = myBucket.getTransactionCategoryType().getCategoryClass();
            if (myClass.canParentCategory()) {
                continue;
            }

            /* Determine menu to add to */
            final TransactionCategory myCategory = myBucket.getTransactionCategory();
            final TransactionCategory myParent = myCategory.getParentCategory();
            final String myParentName = myParent.getName();
            TethysScrollSubMenu<TransactionCategoryBucket, ?> myMenu = myMap.get(myParentName);

            /* If this is a new menu */
            if (myMenu == null) {
                /* Create a new JMenu and add it to the popUp */
                myMenu = theCategoryMenu.addSubMenu(myParentName);
                myMap.put(myParentName, myMenu);
            }

            /* Create a new MenuItem and add it to the popUp */
            final TethysScrollMenuItem<TransactionCategoryBucket> myItem = myMenu.getSubMenu().addItem(myBucket, myCategory.getSubCategory());

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
         * The active Filter.
         */
        private TransactionCategoryFilter theFilter;

        /**
         * Constructor.
         */
        private EventState() {
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private EventState(final EventState pState) {
            /* Initialise state */
            theCategory = pState.getEventCategory();
            theFilter = pState.getFilter();
        }

        /**
         * Obtain the EventCategory Bucket.
         * @return the EventCategory
         */
        private TransactionCategoryBucket getEventCategory() {
            return theCategory;
        }

        /**
         * Obtain the EventCategory Filter.
         * @return the EventCategory
         */
        private TransactionCategoryFilter getFilter() {
            return theFilter;
        }

        /**
         * Set new Category.
         * @param pCategory the Category
         * @return true/false did a change occur
         */
        private boolean setCategory(final TransactionCategoryBucket pCategory) {
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
        private void setTheCategory(final TransactionCategoryBucket pCategory) {
            /* Store the selected category */
            theCategory = pCategory;
            theFilter = theCategory != null
                                            ? new TransactionCategoryFilter(theCategory)
                                            : null;
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
