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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import net.sourceforge.joceanus.jmetis.data.Difference;
import net.sourceforge.joceanus.jmetis.field.swing.JFieldElement;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.TransactionCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.TransactionCategoryBucket.TransactionCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.TransactionCategoryFilter;
import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysChangeEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysChangeEventListener;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistration.TethysChangeRegistration;
import net.sourceforge.joceanus.jtethys.ui.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.ui.swing.JScrollButton.JScrollMenuBuilder;
import net.sourceforge.joceanus.jtethys.ui.swing.JScrollMenu;

/**
 * EventCategory Analysis Selection.
 */
public class TransCategoryAnalysisSelect
        extends JPanel
        implements AnalysisFilterSelection, TethysEventProvider {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -5828172857155415661L;

    /**
     * Text for TransCategory Label.
     */
    private static final String NLS_CATEGORY = MoneyWiseDataType.TRANSCATEGORY.getItemName();

    /**
     * The Event Manager.
     */
    private final transient TethysEventManager theEventManager;

    /**
     * The active transaction categories bucket list.
     */
    private transient TransactionCategoryBucketList theCategories;

    /**
     * The state.
     */
    private transient EventState theState;

    /**
     * The savePoint.
     */
    private transient EventState theSavePoint;

    /**
     * The select button.
     */
    private final JScrollButton<TransactionCategoryBucket> theButton;

    /**
     * Constructor.
     */
    public TransCategoryAnalysisSelect() {
        /* Create the button */
        theButton = new JScrollButton<TransactionCategoryBucket>();

        /* Create the label */
        JLabel myLabel = new JLabel(NLS_CATEGORY + JFieldElement.STR_COLON);

        /* Create Event Manager */
        theEventManager = new TethysEventManager();

        /* Define the layout */
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(Box.createHorizontalGlue());
        add(myLabel);
        add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE, 0)));
        add(theButton);
        add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE, 0)));

        /* Create initial state */
        theState = new EventState();
        theState.applyState();

        /* Create the listener */
        new CategoryListener();
    }

    @Override
    public TethysEventRegistrar getEventRegistrar() {
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
     * Listener class.
     */
    private final class CategoryListener
            implements PropertyChangeListener, TethysChangeEventListener {
        /**
         * Category menu builder.
         */
        private final JScrollMenuBuilder<TransactionCategoryBucket> theCategoryMenuBuilder;

        /**
         * CategoryMenu Registration.
         */
        private final TethysChangeRegistration theCategoryMenuReg;

        /**
         * Constructor.
         */
        private CategoryListener() {
            /* Access builders */
            theCategoryMenuBuilder = theButton.getMenuBuilder();
            theCategoryMenuReg = theCategoryMenuBuilder.getEventRegistrar().addChangeListener(this);

            /* Add swing listener */
            theButton.addPropertyChangeListener(JScrollButton.PROPERTY_VALUE, this);
        }

        @Override
        public void processChangeEvent(final TethysChangeEvent pEvent) {
            /* If this is the CategoryMenu */
            if (theCategoryMenuReg.isRelevant(pEvent)) {
                buildCategoryMenu();
            }
        }

        /**
         * Build Category menu.
         */
        private void buildCategoryMenu() {
            /* Reset the popUp menu */
            theCategoryMenuBuilder.clearMenu();

            /* Create a simple map for top-level categories */
            Map<String, JScrollMenu> myMap = new HashMap<String, JScrollMenu>();

            /* Record active item */
            TransactionCategoryBucket myCurrent = theState.getEventCategory();
            JMenuItem myActive = null;

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
                JScrollMenu myMenu = myMap.get(myParentName);

                /* If this is a new menu */
                if (myMenu == null) {
                    /* Create a new JMenu and add it to the popUp */
                    myMenu = theCategoryMenuBuilder.addSubMenu(myParentName);
                    myMap.put(myParentName, myMenu);
                }

                /* Create a new JMenuItem and add it to the popUp */
                JMenuItem myItem = theCategoryMenuBuilder.addItem(myMenu, myBucket, myCategory.getSubCategory());

                /* If this is the active category */
                if (myBucket.equals(myCurrent)) {
                    /* Record it */
                    myActive = myItem;
                }
            }

            /* Ensure active item is visible */
            theCategoryMenuBuilder.showItem(myActive);
        }

        @Override
        public void propertyChange(final PropertyChangeEvent pEvent) {
            /* Access the source */
            Object o = pEvent.getSource();

            /* If this is the category button */
            if (theButton.equals(o)) {
                /* Select the new category */
                if (theState.setCategory(theButton.getValue())) {
                    theState.applyState();
                    theEventManager.fireStateChanged();
                }
            }
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
            if (!Difference.isEqual(pCategory, theCategory)) {
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
