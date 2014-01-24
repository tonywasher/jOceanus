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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;

import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.EventCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.EventCategoryBucket.EventCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.data.EventCategory;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventCategoryClass;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.EventCategoryFilter;
import net.sourceforge.joceanus.jtethys.event.JEventPanel;
import net.sourceforge.joceanus.jtethys.swing.ArrowIcon;
import net.sourceforge.joceanus.jtethys.swing.JScrollMenu;
import net.sourceforge.joceanus.jtethys.swing.JScrollPopupMenu;

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
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(EventCategoryAnalysisSelect.class.getName());

    /**
     * Text for EventCategory Label.
     */
    private static final String NLS_CATEGORY = NLS_BUNDLE.getString("EventCategory");

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
    private final JButton theButton;

    @Override
    public EventCategoryFilter getFilter() {
        return new EventCategoryFilter(theState.getEventCategory());
    }

    @Override
    public boolean isAvailable() {
        return (theCategories != null)
               && !theCategories.isEmpty();
    }

    /**
     * Constructor.
     */
    public EventCategoryAnalysisSelect() {
        /* Create the button */
        theButton = new JButton(ArrowIcon.DOWN);
        theButton.setVerticalTextPosition(AbstractButton.CENTER);
        theButton.setHorizontalTextPosition(AbstractButton.LEFT);

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
        theButton.addActionListener(new ButtonListener());
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
        theButton.setEnabled(bEnabled
                             && isAvailable());
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
        if ((myCategory == null)
            && (!theCategories.isEmpty())) {
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
            implements ActionListener {
        @Override
        public void actionPerformed(final ActionEvent evt) {
            /* Access source of the event */
            Object o = evt.getSource();

            /* Handle buttons */
            if (theButton.equals(o)) {
                showCategoryMenu();
            }
        }

        /**
         * Show Category menu.
         */
        private void showCategoryMenu() {
            /* Create a new popUp menu */
            JScrollPopupMenu myPopUp = new JScrollPopupMenu();

            /* Create a simple map for top-level categories */
            Map<String, JScrollMenu> myMap = new HashMap<String, JScrollMenu>();

            /* Loop through the available category values */
            Iterator<EventCategoryBucket> myIterator = theCategories.iterator();
            while (myIterator.hasNext()) {
                EventCategoryBucket myBucket = myIterator.next();

                /* Only process subTotal items */
                EventCategoryClass myClass = myBucket.getEventCategoryType().getCategoryClass();
                if (!myClass.isSubTotal()) {
                    continue;
                }

                /* Create a new JMenu and add it to the popUp */
                String myName = myBucket.getName();
                JScrollMenu myMenu = new JScrollMenu(myName);
                myMap.put(myName, myMenu);
                myPopUp.addMenuItem(myMenu);
            }

            /* Re-Loop through the available category values */
            myIterator = theCategories.iterator();
            while (myIterator.hasNext()) {
                EventCategoryBucket myBucket = myIterator.next();

                /* Only process low-level items */
                EventCategoryClass myClass = myBucket.getEventCategoryType().getCategoryClass();
                if (myClass.canParentCategory()) {
                    continue;
                }

                /* Determine menu to add to */
                EventCategory myParent = myBucket.getEventCategory().getParentCategory();
                JScrollMenu myMenu = myMap.get(myParent.getName());

                /* Create a new JMenuItem and add it to the menu */
                EventAction myAction = new EventAction(myBucket);
                JMenuItem myItem = new JMenuItem(myAction);
                myMenu.addMenuItem(myItem);
            }

            /* Show the Category menu in the correct place */
            Rectangle myLoc = theButton.getBounds();
            myPopUp.show(theButton, 0, myLoc.height);
        }
    }

    /**
     * EventCategory action class.
     */
    private final class EventAction
            extends AbstractAction {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -6988674038012323118L;

        /**
         * EventCategory.
         */
        private final EventCategoryBucket theCategory;

        /**
         * Constructor.
         * @param pCategory the eventCategory bucket
         */
        private EventAction(final EventCategoryBucket pCategory) {
            super(pCategory.getEventCategory().getSubCategory());
            theCategory = pCategory;
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            /* Select the new category */
            if (theState.setEventCategory(theCategory)) {
                theState.applyState();
                fireStateChanged();
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
            theButton.setText((theCategory == null)
                    ? null
                    : theCategory.getName());
        }
    }
}
