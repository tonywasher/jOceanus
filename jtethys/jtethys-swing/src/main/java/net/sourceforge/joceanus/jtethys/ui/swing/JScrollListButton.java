/*******************************************************************************
 * jTethys: Java Utilities
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
 * Code for this module is based on the MenuScroller.java code that is available at
 *
 * under the Apache 2.0 license
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui.swing;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.event.PopupMenuListener;

import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * PopUp menu that displays a list of checkMenu items.
 * @author Tony Washer
 * @param <T> the item type
 * @deprecated as of 1.5.0 use {@link TethysSwingListButtonManager}
 */
@Deprecated
public class JScrollListButton<T>
        extends JButton {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -3192081938066463392L;

    /**
     * Menu Builder.
     */
    private final transient JScrollListMenuBuilder<T> theMenuBuilder;

    /**
     * Constructor.
     */
    public JScrollListButton() {
        /* Set standard setup */
        super(TethysSwingArrowIcon.DOWN);
        setVerticalTextPosition(AbstractButton.CENTER);
        setHorizontalTextPosition(AbstractButton.LEFT);

        /* Create the menu builder */
        theMenuBuilder = new JScrollListMenuBuilder<>(this);
    }

    /**
     * Obtain menuBuilder.
     * @return the menuBuilder.
     */
    public JScrollListMenuBuilder<T> getMenuBuilder() {
        return theMenuBuilder;
    }

    /**
     * MenuBuilder class.
     * @param <X> the object type
     */
    @Deprecated
    public static final class JScrollListMenuBuilder<X>
            implements ActionListener, TethysEventProvider<TethysUIEvent> {
        /**
         * The Event Manager.
         */
        private final TethysEventManager<TethysUIEvent> theEventManager;

        /**
         * The Button.
         */
        private final JScrollListButton<X> theButton;

        /**
         * ScrollList Menu.
         */
        private final JScrollListMenu<X> theMenu;

        /**
         * Constructor.
         * @param pButton the button
         */
        public JScrollListMenuBuilder(final JScrollListButton<X> pButton) {
            /* Store details */
            theButton = pButton;
            theMenu = new JScrollListMenu<>(this);
            theButton.addActionListener(this);

            /* Create event manager */
            theEventManager = new TethysEventManager<>();
        }

        @Override
        public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
            return theEventManager.getEventRegistrar();
        }

        /**
         * Add menu listener.
         * @param pListener the listener
         */
        public void addPopupMenuListener(final PopupMenuListener pListener) {
            theMenu.addPopupMenuListener(pListener);
        }

        /**
         * Remove menu listener.
         * @param pListener the listener
         */
        public void removePopupMenuListener(final PopupMenuListener pListener) {
            theMenu.removePopupMenuListener(pListener);
        }

        /**
         * Clear available items.
         */
        public void clearAvailableItems() {
            theMenu.clearAvailableItems();
        }

        /**
         * Set available item.
         * @param pItem the available item
         */
        public void setAvailableItem(final X pItem) {
            theMenu.setAvailableItem(pItem);
        }

        /**
         * Set available list.
         * @param pList the list of available items
         */
        public void setAvailableItems(final List<X> pList) {
            theMenu.setAvailableItems(pList);
        }

        /**
         * Set available list.
         * @param pArray the array of available items
         */
        public void setAvailableItems(final X[] pArray) {
            theMenu.setAvailableItems(pArray);
        }

        /**
         * clear all selected items.
         */
        public void clearAllSelected() {
            theMenu.clearAllSelected();
        }

        /**
         * Set selected item.
         * @param pItem the item to select
         */
        public void setSelectedItem(final X pItem) {
            theMenu.setSelectedItem(pItem);
        }

        /**
         * Set selected items.
         * @param pIterator the iterator over selected items
         */
        public void setSelectedItems(final Iterator<X> pIterator) {
            /* Loop over the items */
            while (pIterator.hasNext()) {
                X myItem = pIterator.next();

                /* Set the item as selected */
                theMenu.setSelectedItem(myItem);
            }
        }

        /**
         * Clear selected item.
         * @param pItem the item to select
         */
        public void clearSelectedItem(final X pItem) {
            theMenu.clearSelectedItem(pItem);
        }

        /**
         * Set selected items.
         * @param pIterator the iterator over selected items
         */
        public void clearSelectedItems(final Iterator<X> pIterator) {
            /* Loop over the items */
            while (pIterator.hasNext()) {
                X myItem = pIterator.next();

                /* Set the item as cleared */
                theMenu.clearSelectedItem(myItem);
            }
        }

        /**
         * Get selected items.
         * @return iterator of selected items
         */
        public Iterator<X> selectedIterator() {
            return selectedList().iterator();
        }

        /**
         * Get selected items.
         * @return list of selected items
         */
        public List<X> selectedList() {
            return theMenu.getSelectedItems();
        }

        /**
         * Is item selected?
         * @param pItem the item to check
         * @return true/false
         */
        public boolean isSelectedItem(final X pItem) {
            return theMenu.isItemSelected(pItem);
        }

        /**
         * Dispose of the builder.
         */
        public void dispose() {
            theButton.removeActionListener(this);
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            /* Ask listeners to update selection */
            theEventManager.fireEvent(TethysUIEvent.PREPAREDIALOG);

            /* If a menu is provided */
            if ((theMenu != null) && (theMenu.getItemCount() > 0)) {
                /* Show the Select menu in the correct place */
                Rectangle myLoc = theButton.getBounds();
                theMenu.show(theButton, 0, myLoc.height);
            }
        }

        /**
         * fire item state changed.
         * @param pItem the item
         */
        private void fireItemStateChanged(final X pItem) {
            boolean isSelected = isSelectedItem(pItem);
            theEventManager.fireEvent(TethysUIEvent.NEWVALUE, new ToggleState<X>(pItem, isSelected));
        }
    }

    /**
     * Event details.
     * @param <X> the item type
     */
    @Deprecated
    public static final class ToggleState<X> {
        /**
         * The Item.
         */
        private final X theItem;

        /**
         * Is the item selected?
         */
        private final boolean isSelected;

        /**
         * Constructor.
         * @param pItem the item
         * @param pSelected true/false
         */
        private ToggleState(final X pItem,
                            final boolean pSelected) {
            theItem = pItem;
            isSelected = pSelected;
        }

        /**
         * Obtain the item.
         * @return the item
         */
        public X getItem() {
            return theItem;

        }

        /**
         * Is the item selected?
         * @return true/false
         */
        public boolean isSelected() {
            return isSelected;
        }
    }

    /**
     * ScrollList Menu.
     * @param <X> the object type
     */
    @Deprecated
    private static final class JScrollListMenu<X>
            extends JScrollPopupMenu {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -2965593463214916103L;

        /**
         * The Menu Builder.
         */
        private final transient JScrollListMenuBuilder<X> theBuilder;

        /**
         * Map of items.
         */
        private final transient Map<X, JCheckBoxMenuItem> theItemMap;

        /**
         * Constructor.
         * @param pBuilder the builder
         */
        private JScrollListMenu(final JScrollListMenuBuilder<X> pBuilder) {
            /* Allocate the map */
            theBuilder = pBuilder;
            theItemMap = new LinkedHashMap<>();
        }

        /**
         * Clear available items.
         */
        private void clearAvailableItems() {
            /* reset the list of available items */
            removeAll();
            theItemMap.clear();
        }

        /**
         * Set available item.
         * @param pItem the available items
         */
        private void setAvailableItem(final X pItem) {
            /* If the item is not already in the map */
            if (theItemMap.get(pItem) == null) {
                /* Create the action */
                CheckAction myAction = new CheckAction(pItem);
                JCheckBoxMenuItem myMenu = new JCheckBoxMenuItem(myAction);

                /* Add the item */
                addMenuItem(myMenu);
                theItemMap.put(pItem, myMenu);
            }
        }

        /**
         * Set available list.
         * @param pList the list of available items
         */
        private void setAvailableItems(final List<X> pList) {
            /* reset the list of available items */
            clearAvailableItems();

            /* Loop through the items */
            Iterator<X> myIterator = pList.iterator();
            while (myIterator.hasNext()) {
                X myItem = myIterator.next();

                /* Add the available item */
                setAvailableItem(myItem);
            }
        }

        /**
         * Set available array.
         * @param pArray the array of available items
         */
        private void setAvailableItems(final X[] pArray) {
            /* reset the list of available items */
            clearAvailableItems();

            /* Loop through the items */
            for (X myItem : pArray) {
                /* Add the available item */
                setAvailableItem(myItem);
            }
        }

        /**
         * clear all selected items.
         */
        private void clearAllSelected() {
            /* Loop through the menu items */
            for (JCheckBoxMenuItem myItem : theItemMap.values()) {
                /* Clear the item */
                myItem.setSelected(false);
            }
        }

        /**
         * Set selected item.
         * @param pItem the item to select
         */
        private void setSelectedItem(final X pItem) {
            /* Access and select the item */
            JCheckBoxMenuItem myMenu = theItemMap.get(pItem);
            if (myMenu != null) {
                myMenu.setSelected(true);
            }
        }

        /**
         * Clear selected item.
         * @param pItem the item to clear
         */
        private void clearSelectedItem(final X pItem) {
            /* Access and select the item */
            JCheckBoxMenuItem myMenu = theItemMap.get(pItem);
            if (myMenu != null) {
                myMenu.setSelected(false);
            }
        }

        /**
         * Is item selected?
         * @param pItem the item to select
         * @return true/false
         */
        private boolean isItemSelected(final X pItem) {
            /* Access and select the item */
            JCheckBoxMenuItem myMenu = theItemMap.get(pItem);
            return (myMenu == null)
                                    ? false
                                    : myMenu.isSelected();
        }

        /**
         * Get selected items.
         * @return list of selected items
         */
        private List<X> getSelectedItems() {
            /* Create a new list */
            List<X> myList = new ArrayList<>();

            /* Loop through the entries */
            for (Map.Entry<X, JCheckBoxMenuItem> myEntry : theItemMap.entrySet()) {
                /* If the item is selected */
                if (myEntry.getValue().isSelected()) {
                    /* Add to the list */
                    myList.add(myEntry.getKey());
                }
            }

            /* Return the iterator */
            return myList;
        }

        /**
         * Item action class.
         */
        @Deprecated
        private final class CheckAction
                extends AbstractAction {
            /**
             * Serial Id.
             */
            private static final long serialVersionUID = -5525669837228981284L;

            /**
             * The item.
             */
            private final transient X theItem;

            /**
             * Constructor.
             * @param pItem the item
             */
            private CheckAction(final X pItem) {
                super(pItem.toString());
                theItem = pItem;
            }

            @Override
            public void actionPerformed(final ActionEvent pEvent) {
                /* Let the user know that this item has been selected or not */
                Object mySource = pEvent.getSource();
                if (mySource instanceof JCheckBoxMenuItem) {
                    theBuilder.fireItemStateChanged(theItem);
                }

                /* Keep the menu displayed */
                JScrollListMenu.this.setVisible(true);
            }
        }
    }
}
