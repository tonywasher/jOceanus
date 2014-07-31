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
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.swing;

import java.awt.Container;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.event.PopupMenuListener;

import net.sourceforge.joceanus.jtethys.event.JEventObject;

/**
 * Swing Button which provides a PopUpMenu selection.
 * @param <T> the object type
 */
public class JScrollButton<T>
        extends JButton {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 7301178805823932380L;

    /**
     * Name of the Value property.
     */
    public static final String PROPERTY_VALUE = "Value";

    /**
     * Menu Builder.
     */
    private JScrollMenuBuilder<T> theMenuBuilder;

    /**
     * Value.
     */
    private T theValue;

    /**
     * Name.
     */
    private String theName;

    /**
     * Text.
     */
    private final String theText;

    /**
     * Fire on menu close.
     */
    private boolean fireOnClose;

    /**
     * Set the value.
     * @param pValue the value to set.
     */
    public void setValue(final T pValue) {
        setValue(pValue, pValue == null
                                       ? null
                                       : pValue.toString());
    }

    /**
     * Fire state change on menu cancel (either menu close or no-change selection).
     */
    public void fireOnClose() {
        fireOnClose = true;
    }

    /**
     * Refresh Text from item.
     */
    public void refreshText() {
        storeValue(theValue);
    }

    /**
     * Set the value.
     * @param pValue the value to set.
     * @param pName the display name
     */
    public void setValue(final T pValue,
                         final String pName) {
        /* Access current value */
        T myOld = theValue;

        /* Store new values */
        storeTheValue(pValue, pName);

        /* If we are firing all values */
        if (theText != null) {
            /* Fire the property change */
            firePropertyChange(PROPERTY_VALUE, null, theValue);

            /* If the value has changed */
        } else if (isValueChanged(myOld, theValue)) {
            /* Fire the property change */
            firePropertyChange(PROPERTY_VALUE, myOld, theValue);

            /* else note that the menu has been cancelled */
        } else if (fireOnClose) {
            theMenuBuilder.notifyClosed();
        }
    }

    /**
     * Store the value without firing events.
     * @param pValue the value to set.
     */
    public void storeValue(final T pValue) {
        storeValue(pValue, pValue == null
                                         ? null
                                         : pValue.toString());
    }

    /**
     * Store the value without firing events.
     * @param pValue the value to set.
     * @param pName the display name
     */
    public void storeValue(final T pValue,
                           final String pName) {
        /* Store new values */
        storeTheValue(pValue, pName);
    }

    /**
     * Has the value changed?
     * @param pFirst the first value
     * @param pSecond the second value
     * @param <T> the object type
     * @return <code>true/false</code>
     */
    protected static <T> boolean isValueChanged(final T pFirst,
                                                final T pSecond) {
        if (pFirst == null) {
            return pSecond != null;
        } else {
            return !pFirst.equals(pSecond);
        }
    }

    /**
     * Set the value.
     * @param pValue the value to set.
     * @param pName the display name
     */
    private void storeTheValue(final T pValue,
                               final String pName) {
        theValue = pValue;
        theName = pName;
        if (theText == null) {
            setText(theName);
        }
    }

    /**
     * Obtain menuBuilder.
     * @return the menuBuilder.
     */
    public JScrollMenuBuilder<T> getMenuBuilder() {
        return theMenuBuilder;
    }

    /**
     * Obtain value.
     * @return the value.
     */
    public T getValue() {
        return theValue;
    }

    /**
     * Obtain name.
     * @return the name.
     */
    public String getDisplayName() {
        return theName;
    }

    /**
     * Constructor.
     */
    public JScrollButton() {
        this(null);
    }

    /**
     * Constructor.
     * @param pText the fixed text.
     */
    public JScrollButton(final String pText) {
        super(ArrowIcon.DOWN);
        setVerticalTextPosition(AbstractButton.CENTER);
        setHorizontalTextPosition(AbstractButton.LEFT);
        theMenuBuilder = new JScrollMenuBuilder<T>(this);
        theText = pText;
        fireOnClose = false;
        setText(theText);
    }

    /**
     * MenuBuilder class.
     * @param <T> the object type
     */
    public static final class JScrollMenuBuilder<T>
            extends JEventObject
            implements ActionListener {
        /**
         * The Button.
         */
        private final JScrollButton<T> theButton;

        /**
         * Building menu.
         */
        private boolean buildingMenu;

        /**
         * The PopUpMenu.
         */
        private JScrollPopupMenu theMenu;

        /**
         * Are we building the menu?
         * @return true/false
         */
        public boolean buildingMenu() {
            return buildingMenu;
        }

        /**
         * Constructor.
         * @param pButton the button
         */
        private JScrollMenuBuilder(final JScrollButton<T> pButton) {
            theButton = pButton;
            theButton.addActionListener(this);
            theMenu = new JScrollPopupMenu();
        }

        /**
         * Clear menu.
         */
        public void clearMenu() {
            theMenu.removeAll();
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
         * Dispose of the builder.
         */
        public void dispose() {
            theButton.removeActionListener(this);
        }

        /**
         * Add item for value.
         * @param pValue the value
         * @return the menu item
         */
        public JMenuItem addItem(final T pValue) {
            return addItem(pValue, pValue.toString());
        }

        /**
         * Add null item.
         * @param pName the display name
         * @return the menu item
         */
        public JMenuItem addNullItem(final String pName) {
            return addItem(null, pName);
        }

        /**
         * Add item for value.
         * @param pValue the value
         * @param pName the display name
         * @return the menu item
         */
        public JMenuItem addItem(final T pValue,
                                 final String pName) {
            /* Create the action */
            ItemAction<T> myAction = new ItemAction<T>(theButton, pValue, pName);
            JMenuItem myItem = new JMenuItem(myAction);

            /* Add the item */
            theMenu.addMenuItem(myItem);
            return myItem;
        }

        /**
         * Add subMenu.
         * @param pName the display name
         * @return the subMenu
         */
        public JScrollMenu addSubMenu(final String pName) {
            /* Create the action */
            JScrollMenu myMenu = new JScrollMenu(pName);

            /* Add the menu */
            theMenu.addMenuItem(myMenu);
            return myMenu;
        }

        /**
         * Add item for value to subMenu.
         * @param pMenu the menu
         * @param pValue the value
         * @return the menu item
         */
        public JMenuItem addItem(final JScrollMenu pMenu,
                                 final T pValue) {
            return addItem(pMenu, pValue, pValue.toString());
        }

        /**
         * Add null item to menu.
         * @param pMenu the menu
         * @param pName the display name
         * @return the menu item
         */
        public JMenuItem addNullItem(final JScrollMenu pMenu,
                                     final String pName) {
            return addItem(pMenu, null, pName);
        }

        /**
         * Add item for value to Menu.
         * @param pMenu the menu
         * @param pValue the value
         * @param pName the display name
         * @return the menu item
         */
        public JMenuItem addItem(final JScrollMenu pMenu,
                                 final T pValue,
                                 final String pName) {
            /* Create the action */
            ItemAction<T> myAction = new ItemAction<T>(theButton, pValue, pName);
            JMenuItem myItem = new JMenuItem(myAction);

            /* Add the item */
            pMenu.addMenuItem(myItem);
            return myItem;
        }

        /**
         * Add subMenu to Menu.
         * @param pMenu the menu
         * @param pName the display name
         * @return the subMenu
         */
        public JScrollMenu addSubMenu(final JScrollMenu pMenu,
                                      final String pName) {
            /* Create the action */
            JScrollMenu myMenu = new JScrollMenu(pName);

            /* Add the menu */
            pMenu.addMenuItem(myMenu);
            return myMenu;
        }

        /**
         * Ensure that the item is scrolled into place.
         * @param pItem the item to scroll
         */
        public void showItem(final JMenuItem pItem) {
            /* Ignore null item */
            if (pItem == null) {
                return;
            }

            /* Obtain the parent of the item */
            Container myParent = pItem.getParent();
            if (myParent instanceof JScrollMenu) {
                /* Access parent menu */
                JScrollMenu myMenu = (JScrollMenu) myParent;
                myMenu.showItem(pItem);

                /* Ensure that parent is visible */
                showItem(myMenu);
            } else if (myParent instanceof JScrollPopupMenu) {
                /* Access parent menu */
                JScrollPopupMenu myMenu = (JScrollPopupMenu) myParent;
                myMenu.showItem(pItem);
            }
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            /* Ask listeners to provide the menu */
            buildingMenu = true;
            fireStateChanged();

            /* If a menu is provided */
            if ((theMenu != null) && (theMenu.getItemCount() > 0)) {
                /* Show the Select menu in the correct place */
                Rectangle myLoc = theButton.getBounds();
                theMenu.show(theButton, 0, myLoc.height);
            }
        }

        /**
         * Notify that we have closed the menu without a choice.
         */
        private void notifyClosed() {
            /* Ask listeners to provide the menu */
            buildingMenu = false;
            fireStateChanged();
        }
    }

    /**
     * Item action class.
     * @param <T> the object type
     */
    private static final class ItemAction<T>
            extends AbstractAction {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -8505257662931197112L;

        /**
         * Item.
         */
        private final JScrollButton<T> theButton;

        /**
         * Item.
         */
        private final T theItem;

        /**
         * Name.
         */
        private final String theName;

        /**
         * Constructor.
         * @param pButton the button
         * @param pItem the item
         */
        private ItemAction(final JScrollButton<T> pButton,
                           final T pItem) {
            this(pButton, pItem, pItem.toString());
        }

        /**
         * Constructor.
         * @param pButton the button
         * @param pItem the item
         * @param pName the name
         */
        private ItemAction(final JScrollButton<T> pButton,
                           final T pItem,
                           final String pName) {
            super(pName);
            theButton = pButton;
            theItem = pItem;
            theName = pName;
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            /* Set the item */
            theButton.setValue(theItem, theName);
        }
    }
}
