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
 * http://svn.apache.org/repos/asf/jmeter/trunk/src/jorphan/org/apache/jorphan/gui/MenuScroller.java
 * under the Apache 2.0 license
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.swing;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * Scroll-able extension to JPopUpMenu.
 */
public class JScrollPopupMenu
        extends JPopupMenu {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 3147284603144729076L;

    /**
     * MaxDisplayItems error.
     */
    protected static final String ERROR_MAXITEMS = "Maximum Display items must be greater than 0";

    /**
     * Scroll delay error.
     */
    protected static final String ERROR_DELAY = "Scroll delay must be greater than 0";

    /**
     * Item index error.
     */
    private static final String ERROR_INDEX = "Index is out of range";

    /**
     * Default number of items for scroll window.
     */
    protected static final int DEFAULT_ITEMCOUNT = 15;

    /**
     * Default scroll delay when hovering over icon.
     */
    protected static final int DEFAULT_SCROLLDELAY = 150;

    /**
     * List of menu items.
     */
    private final transient List<JMenuItem> theMenuItems;

    /**
     * First item to show in list.
     */
    private int theFirstIndex = 0;

    /**
     * Max number of items to display in popUp.
     */
    private int theMaxDisplayItems;

    /**
     * Scroll delay in milliseconds.
     * <p>
     * This is the delay between successive scrolls when the mouse hovers over the scrolling item.
     */
    private int theScrollDelay;

    /**
     * The ScrollUp Item.
     */
    private final ScrollItem theUpItem;

    /**
     * The ScrollDown Item.
     */
    private final ScrollItem theDownItem;

    /**
     * Constructor.
     */
    public JScrollPopupMenu() {
        this(DEFAULT_ITEMCOUNT);
    }

    /**
     * Constructor.
     * @param pMaxDisplayItems the maximum number of items to display
     */
    public JScrollPopupMenu(final int pMaxDisplayItems) {
        this(pMaxDisplayItems, DEFAULT_SCROLLDELAY);
    }

    /**
     * Constructor.
     * @param pMaxDisplayItems the maximum number of items to display
     * @param pScrollDelay the scroll delay
     */
    public JScrollPopupMenu(final int pMaxDisplayItems,
                            final int pScrollDelay) {
        /* Check parameters */
        if (pMaxDisplayItems <= 0) {
            throw new IllegalArgumentException(ERROR_MAXITEMS);
        }
        if (pScrollDelay <= 0) {
            throw new IllegalArgumentException(ERROR_DELAY);
        }

        /* Record parameters */
        theMaxDisplayItems = pMaxDisplayItems;
        theScrollDelay = pScrollDelay;

        /* Create the scroll items */
        theUpItem = new ScrollItem(ArrowIcon.UP, -1);
        theDownItem = new ScrollItem(ArrowIcon.DOWN, 1);

        /* Allocate the list */
        theMenuItems = new ArrayList<JMenuItem>();

        /* Create listener and add it */
        ScrollListener myListener = new ScrollListener();
        addMouseWheelListener(myListener);
        addPopupMenuListener(myListener);
        addMenuKeyListener(new ScrollKeyListener());
    }

    /**
     * Obtain the maximum # of items in the displayed PopUp window.
     * @return the # of items
     */
    public int getMaxDisplayItems() {
        return theMaxDisplayItems;
    }

    /**
     * Obtain the scroll delay.
     * @return the scroll delay
     */
    public int getScrollDelay() {
        return theScrollDelay;
    }

    /**
     * Set the number of items in the scrolling portion of the menu.
     * @param pMaxDisplayItems the maximum number of items to display
     * @throws IllegalArgumentException if pMaxDisplayItems is 0 or negative
     */
    public void setMaxDisplayItems(final int pMaxDisplayItems) {
        /* Check parameters */
        if (pMaxDisplayItems <= 0) {
            throw new IllegalArgumentException(ERROR_MAXITEMS);
        }

        /* Store parameters */
        theMaxDisplayItems = pMaxDisplayItems;
    }

    /**
     * Set the scroll delay.
     * @param pScrollDelay the scroll delay in milliseconds
     * @throws IllegalArgumentException if interval is 0 or negative
     */
    public void setScrollDelay(final int pScrollDelay) {
        /* Check parameter */
        if (pScrollDelay <= 0) {
            throw new IllegalArgumentException(ERROR_DELAY);
        }

        theUpItem.setScrollDelay(pScrollDelay);
        theDownItem.setScrollDelay(pScrollDelay);
        theScrollDelay = pScrollDelay;
    }

    @Override
    public JMenuItem add(final Action pAction) {
        /* Throw exception */
        throw new UnsupportedOperationException();
    }

    @Override
    public JMenuItem add(final JMenuItem pComponent) {
        /* Throw exception */
        throw new UnsupportedOperationException();
    }

    @Override
    public JMenuItem add(final String pComponent) {
        /* Throw exception */
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(final Action pAction,
                       final int pIndex) {
        /* Throw exception */
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(final Component pComponent,
                       final int pIndex) {
        /* Throw exception */
        throw new UnsupportedOperationException();
    }

    @Override
    public void addSeparator() {
        /* Throw exception */
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(final int pIndex) {
        /* Throw exception */
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeAll() {
        /* Clear menuItems */
        theMenuItems.clear();
        theFirstIndex = 0;
    }

    /**
     * Add a menu Item.
     * @param pItem the item to add
     * @return the item
     */
    public JMenuItem addMenuItem(final JMenuItem pItem) {
        /* Add to the list of Menu Items */
        theMenuItems.add(pItem);

        /* Add to the popUp menu so that initial size can be calculated */
        super.add(pItem);

        /* Return the item */
        return pItem;
    }

    /**
     * Insert a menu Item.
     * @param pItem the item to add
     * @param pIndex the index to add at
     * @throws IllegalArgumentException if index is invalid
     */
    public void insertMenuItem(final JMenuItem pItem,
                               final int pIndex) {
        /* Check for valid index */
        if ((pIndex < 0)
            || (pIndex >= theMenuItems.size())) {
            throw new IllegalArgumentException(ERROR_INDEX);
        }

        /* Add to the list of Menu Items in the correct position */
        theMenuItems.add(pIndex, pItem);

        /* Add to the popUp menu so that initial size can be calculated */
        super.insert(pItem, pIndex);
    }

    /**
     * Remove menu Item at specified index.
     * @param pIndex the index of the item to remove
     * @throws IllegalArgumentException if index is invalid
     */
    public void removeMenuItem(final int pIndex) {
        /* Check for valid index */
        if ((pIndex < 0)
            || (pIndex >= theMenuItems.size())) {
            throw new IllegalArgumentException(ERROR_INDEX);
        }

        /* Remove the item */
        theMenuItems.remove(pIndex);

        /* Remove from the popUp menu so that initial size can be calculated */
        super.remove(pIndex);
    }

    /**
     * Obtain array of menu Items.
     * @return the array
     */
    public JMenuItem[] getMenuItems() {
        /* Allocate array */
        JMenuItem[] myArray = new JMenuItem[theMenuItems.size()];

        /* Obtain array of items */
        return theMenuItems.toArray(myArray);
    }

    /**
     * Obtain count of menu Items.
     * @return the array
     */
    public int getItemCount() {
        /* Obtain count */
        return theMenuItems.size();
    }

    /**
     * Ensure item shown.
     * @param pItem the item to show
     */
    public void showItem(final JMenuItem pItem) {
        /* Obtain index from list */
        int myIndex = theMenuItems.indexOf(pItem);

        /* Show the index */
        showIndex(myIndex);
    }

    /**
     * Ensure index shown.
     * @param pIndex the index to show
     */
    public void showIndex(final int pIndex) {
        /* Ignore if index is out of range */
        int myCount = theMenuItems.size();
        if ((pIndex < 0)
            || (pIndex >= myCount)) {
            return;
        }

        /* If index is above window */
        if (pIndex < theFirstIndex) {
            /* Move window upwards and return */
            theFirstIndex = pIndex;
            return;
        }

        /* If index is beyond last visible index */
        int myLastIndex = theFirstIndex
                          + theMaxDisplayItems
                          - 1;
        if (myLastIndex < pIndex) {
            /* Move window downwards */
            theFirstIndex += pIndex
                             - myLastIndex;
        }
    }

    /**
     * Refresh the menu.
     */
    protected void refreshMenu() {
        /* If we have items to display */
        if (!theMenuItems.isEmpty()) {
            /* Access the number of entries and the scroll count */
            int myCount = theMenuItems.size();
            int myScroll = Math.min(theMaxDisplayItems, myCount);

            /* Remove all items */
            super.removeAll();

            /* If we do not need to scroll */
            if (myScroll == myCount) {
                /* Loop through the items to add */
                for (int i = 0; i < myCount; i++) {
                    /* Add the items */
                    super.add(theMenuItems.get(i));
                }

                /* else need to set up scroll */
            } else {
                /* Ensure that the starting index is positive */
                if (theFirstIndex < 0) {
                    theFirstIndex = 0;
                }

                /* Ensure that the starting point is not too late */
                int myMaxIndex = theFirstIndex
                                 + myScroll;
                if (myMaxIndex > myCount) {
                    /* Adjust the first index */
                    theFirstIndex = myCount
                                    - myScroll;
                    myMaxIndex = myCount;
                }

                /* Add the top level item */
                if (theFirstIndex > 0) {
                    super.add(theUpItem);
                }

                /* Loop through the items to add */
                for (int i = theFirstIndex; i < myMaxIndex; i++) {
                    /* Add the items */
                    super.add(theMenuItems.get(i));
                }

                /* Add the down item */
                if (myMaxIndex < myCount) {
                    super.add(theDownItem);
                }
            }

            /* Re-pack */
            pack();
        }
    }

    /**
     * Listener class.
     */
    private class ScrollListener
            implements PopupMenuListener, MouseWheelListener {
        @Override
        public void mouseWheelMoved(final MouseWheelEvent mwe) {
            /* Adjust index and refresh the menu */
            theFirstIndex += mwe.getWheelRotation();
            refreshMenu();

            /* Consume the event */
            mwe.consume();
        }

        @Override
        public void popupMenuWillBecomeVisible(final PopupMenuEvent e) {
            refreshMenu();
        }

        @Override
        public void popupMenuCanceled(final PopupMenuEvent e) {
            /* Ignore */
        }

        @Override
        public void popupMenuWillBecomeInvisible(final PopupMenuEvent e) {
            /* Ignore */
        }
    }

    /**
     * Listener class.
     */
    private class ScrollKeyListener
            implements MenuKeyListener {
        @Override
        public void menuKeyPressed(final MenuKeyEvent pEvent) {
            /* Switch on KeyCode */
            switch (pEvent.getKeyCode()) {
                case KeyEvent.VK_UP:
                    checkItem(pEvent, theUpItem);
                    break;
                case KeyEvent.VK_DOWN:
                    checkItem(pEvent, theDownItem);
                    break;
                default:
                    break;
            }
        }

        /**
         * Check whether to ignore a keyStroke.
         * @param pEvent the keyEvent
         * @param pItem the MenuItem to check
         */
        private void checkItem(final MenuKeyEvent pEvent,
                               final JMenuItem pItem) {
            MenuElement[] myElements = pEvent.getMenuSelectionManager().getSelectedPath();
            MenuElement myActual = myElements[myElements.length - 1];
            if (myActual.equals(pItem)) {
                pEvent.consume();
            }
        }

        @Override
        public void menuKeyReleased(final MenuKeyEvent e) {
            /* No action */
        }

        @Override
        public void menuKeyTyped(final MenuKeyEvent e) {
            /* No action */
        }
    }

    /**
     * Scroll action class.
     */
    private final class ScrollItem
            extends JMenuItem
            implements ActionListener, ChangeListener {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -7639838040457426994L;

        /**
         * Increment.
         */
        private final int theIncrement;

        /**
         * Timer.
         */
        private final Timer theTimer;

        /**
         * Constructor.
         * @param pIcon the icon
         * @param pIncrement the increment
         */
        private ScrollItem(final Icon pIcon,
                           final int pIncrement) {
            /* Set the icon for the item */
            super(pIcon);
            setHorizontalAlignment(JMenuItem.CENTER);

            /* Store parameters */
            theIncrement = pIncrement;

            /* Add as listener */
            addActionListener(this);
            addChangeListener(this);

            /* Create timer */
            theTimer = new Timer(theScrollDelay, this);
        }

        /**
         * Set the scroll delay.
         * @param pScrollDelay the scroll delay in milliseconds
         */
        private void setScrollDelay(final int pScrollDelay) {
            /* Adjust the timer delay */
            theTimer.setDelay(pScrollDelay);
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            /* Access the source of the event */
            Object src = e.getSource();

            /* If this comes from us */
            if (this.equals(src)) {
                /* Adjust index and refresh menu */
                theFirstIndex += theIncrement;
                refreshMenu();

                /* Set the menu to visible again */
                JScrollPopupMenu.this.setVisible(true);
            }

            /* If this comes from the timer */
            if (theTimer.equals(src)) {
                /* Adjust index and refresh menu */
                theFirstIndex += theIncrement;
                refreshMenu();
            }
        }

        @Override
        public void stateChanged(final ChangeEvent e) {
            /* If we are armed then make sure that the timer is running */
            if (isArmed()
                && !theTimer.isRunning()) {
                theTimer.start();
            }

            /* If we are not armed then make sure that the timer is stopped */
            if (!isArmed()
                && theTimer.isRunning()) {
                theTimer.stop();
            }
        }
    }
}
