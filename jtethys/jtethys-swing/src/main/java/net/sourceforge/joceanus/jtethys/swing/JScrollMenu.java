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
package net.sourceforge.joceanus.jtethys.swing;

import java.awt.Point;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Scroll-able extension to JMenu.
 */
public class JScrollMenu
        extends JMenu
        implements ChangeListener {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 8771491962847538149L;

    /**
     * PopupMenu.
     */
    private final JScrollPopupMenu thePopUp;

    /**
     * Constructor.
     * @param pTitle the title of the menu.
     */
    public JScrollMenu(final String pTitle) {
        this(pTitle, JScrollPopupMenu.DEFAULT_ITEMCOUNT);
    }

    /**
     * Constructor.
     * @param pTitle the title of the menu.
     * @param pMaxDisplayItems the maximum number of items to display
     */
    public JScrollMenu(final String pTitle,
                       final int pMaxDisplayItems) {
        this(pTitle, pMaxDisplayItems, JScrollPopupMenu.DEFAULT_SCROLLDELAY);
    }

    /**
     * Constructor.
     * @param pTitle the title of the menu.
     * @param pMaxDisplayItems the maximum number of items to display
     * @param pScrollDelay the scroll delay
     */
    public JScrollMenu(final String pTitle,
                       final int pMaxDisplayItems,
                       final int pScrollDelay) {
        /* Pass call on */
        super(pTitle);

        /* Check parameters */
        if (pMaxDisplayItems <= 0) {
            throw new IllegalArgumentException(JScrollPopupMenu.ERROR_MAXITEMS);
        }
        if (pScrollDelay <= 0) {
            throw new IllegalArgumentException(JScrollPopupMenu.ERROR_DELAY);
        }

        /* Record parameters */
        thePopUp = new JScrollPopupMenu(pMaxDisplayItems, pScrollDelay);

        /* Add listener */
        addChangeListener(this);
    }

    @Override
    public JScrollPopupMenu getPopupMenu() {
        return thePopUp;
    }

    @Override
    public void addSeparator() {
        /* Throw exception */
        throw new UnsupportedOperationException();
    }

    @Override
    public JMenuItem add(final String pComponent) {
        /* Throw exception */
        throw new UnsupportedOperationException();
    }

    @Override
    public JMenuItem add(final JMenuItem pComponent) {
        /* Throw exception */
        throw new UnsupportedOperationException();
    }

    @Override
    public JMenuItem add(final Action pAction) {
        /* Throw exception */
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(final int pIndex) {
        /* Throw exception */
        throw new UnsupportedOperationException();
    }

    @Override
    public JMenuItem insert(final Action pAction,
                            final int pIndex) {
        /* Throw exception */
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeAll() {
        /* Remove all items */
        thePopUp.removeAll();
    }

    /**
     * Add a menu Item.
     * @param pItem the item to add
     * @return the item
     */
    public JMenuItem addMenuItem(final JMenuItem pItem) {
        /* Pass call to popUp */
        return thePopUp.addMenuItem(pItem);
    }

    /**
     * Insert a menu Item.
     * @param pItem the item to add
     * @param pIndex the index to add at
     * @throws IllegalArgumentException if index is invalid
     */
    public void insertMenuItem(final JMenuItem pItem,
                               final int pIndex) {
        /* Pass call to popUp */
        thePopUp.insertMenuItem(pItem, pIndex);
    }

    /**
     * Remove menu Item at specified index.
     * @param pIndex the index of the item to remove
     * @throws IllegalArgumentException if index is invalid
     */
    public void removeMenuItem(final int pIndex) {
        /* Pass call to popUp */
        thePopUp.removeMenuItem(pIndex);
    }

    /**
     * Obtain array of menu Items.
     * @return the array
     */
    public JMenuItem[] getMenuItems() {
        /* Pass call to PopUp */
        return thePopUp.getMenuItems();
    }

    @Override
    public int getItemCount() {
        /* Pass call to popUp */
        return thePopUp.getItemCount();
    }

    /**
     * Ensure item shown.
     * @param pItem the item to show
     */
    public void showItem(final JMenuItem pItem) {
        /* Pass call to popUp */
        thePopUp.showItem(pItem);
    }

    /**
     * Ensure index shown.
     * @param pIndex the index to show
     */
    public void showIndex(final int pIndex) {
        /* Pass call to popUp */
        thePopUp.showIndex(pIndex);
    }

    @Override
    public void stateChanged(final ChangeEvent e) {
        /* If the popUp is not currently visible */
        if (!thePopUp.isVisible()) {
            /* Display at desired location */
            Point myPoint = getPopupMenuOrigin();
            thePopUp.show(this, myPoint.x, myPoint.y);

            /* else hide the popUp */
        } else {
            thePopUp.setVisible(false);
        }
    }
}
