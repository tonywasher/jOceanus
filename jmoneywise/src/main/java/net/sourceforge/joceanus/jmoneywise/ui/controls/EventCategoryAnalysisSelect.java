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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.EventCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.EventCategoryBucket.EventCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.EventCategoryFilter;
import net.sourceforge.joceanus.jtethys.event.JEventPanel;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;
import net.sourceforge.joceanus.jtethys.swing.JScrollMenu;

/**
 * EventCategory Analysis Selection.
 */
public class EventCategoryAnalysisSelect
        extends JEventPanel
        implements AnalysisFilterSelection {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -5828172857155415661L;

    /**
     * Text for EventCategory Label.
     */
    private static final String NLS_CATEGORY = MoneyWiseDataType.TRANSCATEGORY.getItemName();

    /**
     * The active event categories bucket list.
     */
    private EventCategoryBucketList theCategories;

    /**
     * The state.
     */
    private EventState theState;

    /**
     * The savePoint.
     */
    private EventState theSavePoint;

    /**
     * The select button.
     */
    private final JScrollButton<EventCategoryBucket> theButton;

    @Override
    public EventCategoryFilter getFilter() {
        return new EventCategoryFilter(theState.getEventCategory());
    }

    @Override
    public boolean isAvailable() {
        return (theCategories != null) && !theCategories.isEmpty();
    }

    /**
     * Constructor.
     */
    public EventCategoryAnalysisSelect() {
        /* Create the button */
        theButton = new JScrollButton<EventCategoryBucket>();

        /* Create the label */
        JLabel myLabel = new JLabel(NLS_CATEGORY);

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
        theButton.addPropertyChangeListener(JScrollButton.PROPERTY_VALUE, new ButtonListener());
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
        theCategories = pAnalysis.getEventCategories();

        /* Obtain the current category */
        EventCategoryBucket myCategory = theState.getEventCategory();

        /* If we have a selected Category */
        if (myCategory != null) {
            /* Look for the equivalent bucket */
            myCategory = theCategories.findItemById(myCategory.getOrderedId());
        }

        /* If we do not have an active bucket and the list is non-empty */
        if ((myCategory == null) && (!theCategories.isEmpty())) {
            /* Use the first bucket */
            myCategory = theCategories.peekFirst();
        }

        /* Set the payee */
        theState.setEventCategory(myCategory);
        theState.applyState();
    }

    @Override
    public void setFilter(final AnalysisFilter<?> pFilter) {
        /* If this is the correct filter type */
        if (pFilter instanceof EventCategoryFilter) {
            /* Access filter */
            EventCategoryFilter myFilter = (EventCategoryFilter) pFilter;

            /* Obtain the filter bucket */
            EventCategoryBucket myCategory = myFilter.getBucket();

            /* Obtain equivalent bucket */
            myCategory = theCategories.findItemById(myCategory.getOrderedId());

            /* Set the category */
            theState.setEventCategory(myCategory);
            theState.applyState();
        }
    }

    /**
     * Listener class.
     */
    private final class ButtonListener
            implements PropertyChangeListener, ChangeListener {
        /**
         * Category menu builder.
         */
        private final JScrollMenuBuilder<EventCategoryBucket> theCategoryMenuBuilder;

        /**
         * Constructor.
         */
        private ButtonListener() {
            /* Access builders */
            theCategoryMenuBuilder = theButton.getMenuBuilder();
            theCategoryMenuBuilder.addChangeListener(this);
        }

        @Override
        public void stateChanged(final ChangeEvent pEvent) {
            /* Access source of the event */
            Object o = pEvent.getSource();

            /* Handle builders */
            if (theCategoryMenuBuilder.equals(o)) {
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
            EventCategoryBucket myCurrent = theState.getEventCategory();
            JMenuItem myActive = null;

            /* Loop through the available category values */
            Iterator<EventCategoryBucket> myIterator = theCategories.iterator();
            while (myIterator.hasNext()) {
                EventCategoryBucket myBucket = myIterator.next();

                /* Only process subTotal items */
                TransactionCategoryClass myClass = myBucket.getEventCategoryType().getCategoryClass();
                if (!myClass.isSubTotal()) {
                    continue;
                }

                /* Create a new JMenu and add it to the popUp */
                String myName = myBucket.getName();
                JScrollMenu myMenu = theCategoryMenuBuilder.addSubMenu(myName);
                myMap.put(myName, myMenu);
            }

            /* Re-Loop through the available category values */
            myIterator = theCategories.iterator();
            while (myIterator.hasNext()) {
                EventCategoryBucket myBucket = myIterator.next();

                /* Only process low-level items */
                TransactionCategoryClass myClass = myBucket.getEventCategoryType().getCategoryClass();
                if (myClass.canParentCategory()) {
                    continue;
                }

                /* Determine menu to add to */
                TransactionCategory myCategory = myBucket.getEventCategory();
                TransactionCategory myParent = myCategory.getParentCategory();
                JScrollMenu myMenu = myMap.get(myParent.getName());

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
                if (theState.setEventCategory(theButton.getValue())) {
                    theState.applyState();
                    fireStateChanged();
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
        private EventCategoryBucket theCategory;

        /**
         * Obtain the EventCategory Bucket.
         * @return the EventCategory
         */
        private EventCategoryBucket getEventCategory() {
            return theCategory;
        }

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
         * Set new Category.
         * @param pCategory the Event Category
         * @return true/false did a change occur
         */
        private boolean setEventCategory(final EventCategoryBucket pCategory) {
            /* Adjust the selected category */
            if (!Difference.isEqual(pCategory, theCategory)) {
                theCategory = pCategory;
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
            theButton.setValue(theCategory);
        }
    }
}
