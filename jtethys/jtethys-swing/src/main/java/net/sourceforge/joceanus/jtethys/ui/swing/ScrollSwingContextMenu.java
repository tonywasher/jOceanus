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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jtethys/src/main/java/net/sourceforge/joceanus/jtethys/swing/JScrollPopupMenu.java $
 * $Revision: 570 $
 * $Author: Tony $
 * $Date: 2015-02-14 06:54:38 +0000 (Sat, 14 Feb 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import net.sourceforge.joceanus.jtethys.event.JOceanusEventManager;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistrar.JOceanusEventProvider;
import net.sourceforge.joceanus.jtethys.swing.GuiUtils;
import net.sourceforge.joceanus.jtethys.ui.ScrollMenuContent;
import net.sourceforge.joceanus.jtethys.ui.ScrollMenuContent.ScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.ScrollMenuContent.ScrollMenuItem;
import net.sourceforge.joceanus.jtethys.ui.ScrollMenuContent.ScrollMenuToggleItem;
import net.sourceforge.joceanus.jtethys.ui.ScrollMenuContent.ScrollSubMenu;
import net.sourceforge.joceanus.jtethys.ui.swing.JEnableWrapper.JEnablePanel;

/**
 * Scroll-able version of ContextMenu.
 * <p>
 * Implemented as Stage since ContextMenu does not allow control of individual elements.
 * @param <T> the value type
 */
public class ScrollSwingContextMenu<T>
        implements ScrollMenu<T, Icon>, JOceanusEventProvider {
    /**
     * Default row size.
     */
    private static final int DEFAULT_ROWHEIGHT = 16;

    /**
     * CheckMark icon.
     */
    private static final Icon CHECK_ICON = GuiUtils.resizeImage(new ImageIcon(ScrollMenuContent.class.getResource("BlueJellyCheckMark.png")), ScrollMenuContent.DEFAULT_ICONWIDTH);

    /**
     * Background active colour.
     */
    protected static final Color COLOR_BACKGROUND = Color.decode("#add8e6");

    /**
     * Value updated.
     */
    public static final int ACTION_SELECTED = 100;

    /**
     * Value toggled.
     */
    public static final int ACTION_TOGGLED = 101;

    /**
     * Menu cancelled.
     */
    public static final int ACTION_MENU_CANCELLED = 102;

    /**
     * Timer.
     */
    private Timer theTimer;

    /**
     * List of menu items.
     */
    private final List<ScrollElement> theMenuItems;

    /**
     * The items panel.
     */
    private final JPanel theActiveItems;

    /**
     * First item to show in list.
     */
    private int theFirstIndex = 0;

    /**
     * Max number of items to display in popUp.
     */
    private int theMaxDisplayItems;

    /**
     * The Event Manager.
     */
    private final JOceanusEventManager theEventManager;

    /**
     * The ScrollUp Item.
     */
    private final ScrollControl theUpItem;

    /**
     * The ScrollDown Item.
     */
    private final ScrollControl theDownItem;

    /**
     * The container panel.
     */
    private final JPanel theContainer;

    /**
     * The Parent scrollMenu.
     */
    private final ScrollSwingSubMenu<T> theParentMenu;

    /**
     * The Parent contextMenu.
     */
    private final ScrollSwingContextMenu<T> theParentContext;

    /**
     * The dialog.
     */
    private JDialog theDialog;

    /**
     * The Active subMenu.
     */
    private ScrollSwingSubMenu<T> theActiveMenu;

    /**
     * The Active item.
     */
    private ScrollSwingMenuItem<T> theActiveItem;

    /**
     * The selected value.
     */
    private ScrollSwingMenuItem<T> theSelectedItem;

    /**
     * Do we need to close menu on toggle?
     */
    private boolean closeOnToggle;

    /**
     * Do we need to reBuild the menu?
     */
    private boolean needReBuild;

    /**
     * The size of the menu.
     */
    private Dimension theMenuSize;

    /**
     * Constructor.
     */
    public ScrollSwingContextMenu() {
        this(ScrollMenuContent.DEFAULT_ITEMCOUNT);
    }

    /**
     * Constructor.
     * @param pMaxDisplayItems the maximum number of items to display
     */
    public ScrollSwingContextMenu(final int pMaxDisplayItems) {
        this(null, pMaxDisplayItems);
    }

    /**
     * Constructor.
     * @param pParent the parent scroll menu
     */
    private ScrollSwingContextMenu(final ScrollSwingSubMenu<T> pParent) {
        this(pParent, pParent.getContext().getMaxDisplayItems());
    }

    /**
     * Constructor.
     * @param pParent the parent scroll menu
     * @param pMaxDisplayItems the maximum number of items to display
     */
    private ScrollSwingContextMenu(final ScrollSwingSubMenu<T> pParent,
                                   final int pMaxDisplayItems) {
        /* Check parameters */
        if (pMaxDisplayItems <= 0) {
            throw new IllegalArgumentException(ScrollMenuContent.ERROR_MAXITEMS);
        }

        /* Record parameters */
        theMaxDisplayItems = pMaxDisplayItems;
        theParentMenu = pParent;
        theParentContext = theParentMenu == null
                                                 ? null
                                                 : theParentMenu.getContext();

        /* Initially need reBuild */
        needReBuild = true;

        /* Initially close on toggle */
        closeOnToggle = true;

        /* Create event manager */
        theEventManager = new JOceanusEventManager();

        /* Create the scroll items */
        theUpItem = new ScrollControl(ArrowIcon.UP, -1);
        theDownItem = new ScrollControl(ArrowIcon.DOWN, 1);

        /* Allocate the list */
        theMenuItems = new ArrayList<ScrollElement>();
        theActiveItems = new JPanel();
        theActiveItems.setLayout(new BoxLayout(theActiveItems, BoxLayout.Y_AXIS));
        theActiveItems.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        /* Create the container */
        theContainer = new JPanel();
        theContainer.setLayout(new BorderLayout());
        theContainer.setBorder(BorderFactory.createLineBorder(Color.lightGray));
    }

    @Override
    public JOceanusEventRegistrar getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public ScrollMenuItem<T> getSelectedItem() {
        return theSelectedItem;
    }

    /**
     * Obtain the maximum # of items in the displayed PopUp window.
     * @return the # of items
     */
    public int getMaxDisplayItems() {
        return theMaxDisplayItems;
    }

    /**
     * Obtain the timer.
     * @return the timer
     */
    private Timer getTimer() {
        return theTimer;
    }

    /**
     * Obtain the dialog.
     * @return the dialog
     */
    private JDialog getDialog() {
        return theDialog;
    }

    @Override
    public void setCloseOnToggle(final boolean pCloseOnToggle) {
        /* Set value */
        closeOnToggle = pCloseOnToggle;
    }

    @Override
    public void setMaxDisplayItems(final int pMaxDisplayItems) {
        /* Check parameters */
        if (pMaxDisplayItems <= 0) {
            throw new IllegalArgumentException(ScrollMenuContent.ERROR_MAXITEMS);
        }

        /* Check state */
        if ((theDialog != null)
            && theDialog.isVisible()) {
            throw new IllegalStateException();
        }

        /* Store parameters */
        theMaxDisplayItems = pMaxDisplayItems;

        /* Loop through the children */
        Iterator<ScrollElement> myIterator = theMenuItems.iterator();
        while (myIterator.hasNext()) {
            ScrollElement myChild = myIterator.next();

            /* If this is a subMenu */
            if (myChild instanceof ScrollSwingSubMenu) {
                /* Pass call on */
                ScrollSwingSubMenu<?> mySubMenu = (ScrollSwingSubMenu<?>) myChild;
                mySubMenu.setMaxDisplayItems(pMaxDisplayItems);
            }
        }

        /* Request reBuild */
        needReBuild = true;
    }

    /**
     * Show the menu at position.
     * @param pAnchor the anchor node
     * @param pSide the side of the anchor node
     */
    public void showMenuAtPosition(final Component pAnchor,
                                   final int pSide) {
        /* Create the dialog */
        if (theDialog == null) {
            createDialog(pAnchor);
        }

        /* determine the size of the menu */
        determineSize();

        /* determine location to display */
        Point myLocation = GuiUtils.obtainDisplayPoint(pAnchor, pSide, theMenuSize);

        /* Show menu */
        showMenuAtLocation(myLocation);
    }

    /**
     * Show the menu at position.
     * @param pAnchor the anchor node
     * @param pX the relative X position
     * @param pY the relative Y position
     */
    public void showMenuAtPosition(final Component pAnchor,
                                   final double pX,
                                   final double pY) {
        /* Create the dialog */
        if (theDialog == null) {
            createDialog(pAnchor);
        }

        /* determine the size of the menu */
        determineSize();

        /* determine location to display */
        Point myRequest = new Point((int) pX, (int) pY);
        Point myLocation = GuiUtils.obtainDisplayPoint(pAnchor, myRequest, theMenuSize);

        /* Show menu */
        showMenuAtLocation(myLocation);
    }

    /**
     * Show the menu at location.
     * @param pLocation the location
     */
    private void showMenuAtLocation(final Point pLocation) {
        /* Record position */
        theDialog.setLocation(pLocation);

        /* Show menu */
        showMenu();
    }

    /**
     * Show the menu.
     */
    private void showMenu() {
        /* Clear any timer */
        if (theParentMenu == null
            && theTimer != null) {
            theTimer.cancel();
        }

        /* Initialise the values */
        theTimer = theParentMenu != null
                                         ? theParentContext.getTimer()
                                         : new Timer();
        theSelectedItem = null;
        theActiveMenu = null;
        theActiveItem = null;

        /* show the dialog */
        theDialog.setVisible(true);
    }

    /**
     * CloseOnFocusLoss.
     */
    private void closeOnFocusLoss() {
        /* Pass call on to parent if it exists */
        if (theParentContext != null) {
            theParentContext.closeOnFocusLoss();
        }

        /* Close the menu */
        closeMenu();
    }

    /**
     * Close non-Modal.
     */
    private void closeMenu() {
        /* Close any children */
        closeChildren();

        /* Close the menu */
        if (theDialog != null) {
            /* Close and throw away the dialog */
            theDialog.setVisible(false);
            theDialog.getContentPane().removeAll();
            theDialog = null;
        }

        /* Clear any timer */
        if (theParentMenu == null
            && theTimer != null) {
            theTimer.cancel();
        }
        theTimer = null;
    }

    /**
     * Create dialog.
     * @param pOwner the owner component
     */
    private void createDialog(final Component pOwner) {
        /* Create the new dialog */
        theDialog = pOwner instanceof ScrollSwingSubMenu
                                                         ? new JDialog(((ScrollSwingSubMenu<?>) pOwner).getDialog(), false)
                                                         : new JDialog(JOptionPane.getFrameForComponent(pOwner), false);
        theDialog.setUndecorated(true);
        theDialog.getContentPane().add(theContainer);
        needReBuild = true;

        /* Add focus listener */
        theDialog.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(final FocusEvent e) {
                /* NoOp */
            }

            @Override
            public void focusLost(final FocusEvent e) {
                /* If we've lost focus to other than the active subMenu */
                if (theActiveMenu == null) {
                    /* fire cancellation event */
                    if (theParentMenu == null) {
                        theEventManager.fireActionEvent(ACTION_MENU_CANCELLED);
                    }

                    /* Close the menu hierarchy if we are currently showing */
                    if ((theDialog != null)
                        && theDialog.isShowing()) {
                        closeOnFocusLoss();
                    }
                }
            }
        });

        theDialog.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(final KeyEvent e) {
                /* NoOp */
            }

            @Override
            public void keyPressed(final KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ESCAPE:
                        handleEscapeKey();
                        break;
                    case KeyEvent.VK_ENTER:
                        handleEnterKey();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void keyReleased(final KeyEvent e) {
                /* NoOp */
            }
        });

        /* Add mouse wheel listener */
        theDialog.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(final MouseWheelEvent e) {
                /* Request the scroll */
                requestScroll(e.getWheelRotation());
                e.consume();
            }
        });
    }

    /**
     * Set the selected item.
     * @param pItem the selected item
     */
    private void setSelectedItem(final ScrollSwingMenuItem<T> pItem) {
        /* If we are a child menu */
        if (theParentContext != null) {
            /* pass call on to parent */
            theParentContext.setSelectedItem(pItem);

            /* else we are top-level */
        } else {
            /* We assume that we will close the menu */
            boolean doCloseMenu = true;
            int myAction = ACTION_SELECTED;

            /* record selection */
            theSelectedItem = pItem;
            if (theSelectedItem instanceof ScrollMenuToggleItem) {
                ScrollMenuToggleItem<?> myItem = (ScrollMenuToggleItem<?>) theSelectedItem;
                myItem.toggleSelected();
                doCloseMenu = closeOnToggle;
                myAction = ACTION_TOGGLED;
            }

            /* fire selection event */
            theEventManager.fireActionEvent(myAction, theSelectedItem);

            /* Close the menu if requested */
            if (doCloseMenu) {
                /* Close the menu */
                closeMenu();
            }
        }
    }

    /**
     * Handle escapeKey.
     */
    private void handleEscapeKey() {
        /* If we are a child menu */
        if (theParentContext != null) {
            /* pass call on to parent */
            theParentContext.handleEscapeKey();

            /* else we are top-level */
        } else {
            /* fire cancellation event */
            theEventManager.fireActionEvent(ACTION_MENU_CANCELLED);

            /* Notify the cancel */
            closeMenu();
        }
    }

    /**
     * Handle enterKey.
     */
    private void handleEnterKey() {
        /* If we are a child menu */
        if (theActiveItem != null) {
            /* assume item is selected */
            setSelectedItem(theActiveItem);
        }
    }

    /**
     * Handle activeItem.
     * @param pItem the item
     */
    private void handleActiveItem(final ScrollSwingMenuItem<T> pItem) {
        /* Close any children */
        closeChildren();

        /* Record that we are the active item */
        theActiveItem = pItem;
    }

    /**
     * Handle activeMenu.
     * @param pMenu the menu
     */
    private void handleActiveMenu(final ScrollSwingSubMenu<T> pMenu) {
        /* Reset existing item */
        theActiveItem = null;

        /* Hide any existing menu that is not us */
        if ((theActiveMenu != null)
            && (theActiveMenu.getIndex() != pMenu.getIndex())) {
            theActiveMenu.hideMenu();
        }

        /* Record active menu */
        theActiveMenu = pMenu;
    }

    @Override
    public void removeAllItems() {
        /* Check state */
        if ((theDialog != null)
            && theDialog.isVisible()) {
            throw new IllegalStateException();
        }

        /* Clear menuItems */
        theMenuItems.clear();
        theFirstIndex = 0;
        needReBuild = true;

        /* Clear state */
        theSelectedItem = null;
    }

    @Override
    public boolean isEmpty() {
        /* Obtain count */
        return theMenuItems.isEmpty();
    }

    /**
     * Obtain count of menu Items.
     * @return the count
     */
    protected int getItemCount() {
        /* Obtain count */
        return theMenuItems.size();
    }

    @Override
    public ScrollMenuItem<T> addItem(final T pValue) {
        /* Use standard name */
        return addItem(pValue, pValue.toString(), null);
    }

    @Override
    public ScrollMenuItem<T> addItem(final T pValue,
                                     final String pName) {
        /* Use standard name */
        return addItem(pValue, pName, null);
    }

    @Override
    public ScrollMenuItem<T> addItem(final T pValue,
                                     final Icon pGraphic) {
        /* Use standard name */
        return addItem(pValue, pValue.toString(), pGraphic);
    }

    @Override
    public ScrollMenuItem<T> addNullItem(final String pName) {
        /* Use given name */
        return addItem(null, pName, null);
    }

    @Override
    public ScrollMenuItem<T> addNullItem(final String pName,
                                         final Icon pGraphic) {
        /* Use given name */
        return addItem(null, pName, pGraphic);
    }

    @Override
    public ScrollMenuItem<T> addItem(final T pValue,
                                     final String pName,
                                     final Icon pGraphic) {
        /* Check state */
        if ((theDialog != null)
            && theDialog.isVisible()) {
            throw new IllegalStateException();
        }

        /* Create element */
        ScrollSwingMenuItem<T> myItem = new ScrollSwingMenuItem<T>(this, pValue, pName, pGraphic);

        /* Add to the list of menuItems */
        theMenuItems.add(myItem);
        needReBuild = true;
        return myItem;
    }

    @Override
    public ScrollSubMenu<T, Icon> addSubMenu(final String pName) {
        /* Use given name */
        return addSubMenu(pName, null);
    }

    @Override
    public ScrollSubMenu<T, Icon> addSubMenu(final String pName,
                                             final Icon pGraphic) {
        /* Check state */
        if ((theDialog != null)
            && theDialog.isVisible()) {
            throw new IllegalStateException();
        }

        /* Create menu */
        ScrollSwingSubMenu<T> myMenu = new ScrollSwingSubMenu<T>(this, pName, pGraphic);

        /* Add to the list of menuItems */
        theMenuItems.add(myMenu);
        needReBuild = true;
        return myMenu;
    }

    @Override
    public ScrollMenuToggleItem<T> addToggleItem(final T pValue) {
        /* Use standard name */
        return addToggleItem(pValue, pValue.toString());
    }

    @Override
    public ScrollMenuToggleItem<T> addToggleItem(final T pValue,
                                                 final String pName) {
        /* Check state */
        if ((theDialog != null)
            && theDialog.isVisible()) {
            throw new IllegalStateException();
        }

        /* Create element */
        ScrollSwingToggleItem<T> myItem = new ScrollSwingToggleItem<T>(this, pValue, pName);

        /* Add to the list of menuItems */
        theMenuItems.add(myItem);
        needReBuild = true;
        return myItem;
    }

    /**
     * close child menus.
     */
    private void closeChildren() {
        /* Close any active subMenu */
        if (theActiveMenu != null) {
            theActiveMenu.hideMenu();
        }
        theActiveMenu = null;
    }

    /**
     * Ensure item at index will be visible when displayed.
     * @param pIndex the index to show
     */
    private void scrollToIndex(final int pIndex) {
        /* Show the index */
        showIndex(pIndex);

        /* cascade call upwards */
        if (theParentMenu != null) {
            theParentMenu.scrollToMenu();
        }
    }

    /**
     * Ensure index shown.
     * @param pIndex the index to show
     */
    private void showIndex(final int pIndex) {
        /* Ignore if index is out of range */
        int myCount = theMenuItems.size();
        if ((pIndex < 0)
            || (pIndex >= myCount)) {
            return;
        }

        /* If index is above window */
        if (pIndex < theFirstIndex) {
            /* Scroll window upwards and return */
            requestScroll(pIndex - theFirstIndex);
            return;
        }

        /* If index is beyond last visible index */
        int myLastIndex = theFirstIndex
                          + theMaxDisplayItems
                          - 1;
        if (myLastIndex < pIndex) {
            /* Scroll window downwards */
            requestScroll(pIndex - myLastIndex);
        }
    }

    /**
     * Determine size of menu.
     */
    private void determineSize() {
        /* If we need to rebuild the menu */
        if (needReBuild
            && !theMenuItems.isEmpty()) {
            /* Access the number of entries and the scroll count */
            int myCount = theMenuItems.size();
            int myScroll = Math.min(theMaxDisplayItems, myCount);

            /* Remove all items */
            theActiveItems.removeAll();
            theContainer.removeAll();
            theContainer.add(theActiveItems, BorderLayout.CENTER);

            /* If we do not need to scroll */
            if (myScroll == myCount) {
                /* Loop through the items to add */
                for (int i = 0; i < myCount; i++) {
                    /* Add the items */
                    theActiveItems.add(theMenuItems.get(i));
                }

                /* Calculate size of menu */
                theDialog.pack();

                /* Determine the size */
                theMenuSize = new Dimension(theDialog.getWidth(), theDialog.getHeight());

                /* else need to set up scroll */
            } else {
                /* Add the scrolling items */
                theContainer.add(theUpItem, BorderLayout.PAGE_START);
                theContainer.add(theDownItem, BorderLayout.PAGE_END);
                theUpItem.setEnabled(true);
                theDownItem.setEnabled(true);

                /* Add ALL items */
                for (ScrollElement myItem : theMenuItems) {
                    /* Add the items */
                    theActiveItems.add(myItem);
                }

                /* Calculate size of menu */
                theDialog.pack();
                int myWidth = theDialog.getWidth();

                /* Remove all items */
                theActiveItems.removeAll();

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

                /* Loop through the items to add */
                for (int i = theFirstIndex; i < myMaxIndex; i++) {
                    /* Add the items */
                    theActiveItems.add(theMenuItems.get(i));
                }

                /* Calculate size of menu */
                theDialog.pack();
                int myHeight = theDialog.getHeight();

                /* Set visibility of scroll items */
                theUpItem.setEnabled(theFirstIndex > 0);
                theDownItem.setEnabled(myMaxIndex < myCount);

                /* Determine the size */
                theMenuSize = new Dimension(myWidth, myHeight);

                /* Fix the width */
                theDialog.setPreferredSize(theMenuSize);
                theDialog.pack();
            }
        }

        /* Reset flag */
        needReBuild = false;
    }

    /**
     * ScrollPlusOne.
     */
    protected void scrollPlusOne() {
        /* If we are already built */
        if (!needReBuild) {
            /* Access the number of entries */
            int myCount = theMenuItems.size();

            /* Reset the children */
            closeChildren();

            /* Ensure Up item is enabled */
            theUpItem.setEnabled(true);

            /* Remove the first item */
            theActiveItems.remove(0);

            /* Add the final item */
            int myLast = theFirstIndex + theMaxDisplayItems;
            ScrollElement myItem = theMenuItems.get(myLast);
            myItem.setActive(false);
            theActiveItems.add(myItem);

            /* Adjust down item */
            theDownItem.setEnabled(myLast + 1 < myCount);

            /* Make sure that the menu is sized correctly */
            theDialog.pack();
        }

        /* Adjust first index */
        theFirstIndex++;
    }

    /**
     * ScrollMinusOne.
     */
    protected void scrollMinusOne() {
        /* If we are already built */
        if (!needReBuild) {
            /* Reset the children */
            closeChildren();

            /* Ensure Down item is enabled */
            theDownItem.setEnabled(true);

            /* Remove the last item */
            theActiveItems.remove(theMaxDisplayItems - 1);

            /* Add the initial item */
            ScrollElement myItem = theMenuItems.get(theFirstIndex - 1);
            myItem.setActive(false);
            theActiveItems.add(myItem, 0);

            /* Adjust up item */
            theUpItem.setEnabled(theFirstIndex > 1);

            /* Make sure that the menu is sized correctly */
            theDialog.pack();
        }

        /* Adjust first index */
        theFirstIndex--;
    }

    /**
     * request scroll.
     * @param pDelta the delta to scroll.
     */
    private void requestScroll(final int pDelta) {
        /* If this is a scroll downwards */
        if (pDelta > 0) {
            /* If we can scroll downwards */
            int myCount = theMenuItems.size();
            int mySpace = myCount - theFirstIndex - theMaxDisplayItems;
            int myScroll = Math.min(mySpace, pDelta);

            /* While we have space */
            while (myScroll-- > 0) {
                /* Scroll downwards */
                scrollPlusOne();
            }

            /* else scroll upwards if we can */
        } else if (theFirstIndex > 0) {
            /* Determine space */
            int myScroll = Math.min(theFirstIndex, -pDelta);

            /* While we have space */
            while (myScroll-- > 0) {
                /* Scroll upwards */
                scrollMinusOne();
            }
        }
    }

    /**
     * Scroll item.
     */
    public abstract static class ScrollElement
            extends JEnablePanel {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 6724073032653057103L;

        /**
         * The label.
         */
        private final JLabel theLabel;

        /**
         * The icon label.
         */
        private final JLabel theIcon;

        /**
         * The base colour.
         */
        private Color theBaseColor;

        /**
         * Constructor.
         * @param pName the display name
         * @param pGraphic the icon for the item
         */
        private ScrollElement(final String pName,
                              final Icon pGraphic) {
            /* Set border layout */
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

            /* Create a Label for the name */
            theLabel = new JLabel();
            theLabel.setHorizontalAlignment(SwingConstants.LEFT);
            theLabel.setText(pName);
            theLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, DEFAULT_ROWHEIGHT));

            /* Create a Label for the graphic */
            theIcon = new JLabel();
            theIcon.setIcon(pGraphic);
            Dimension myDim = new Dimension(ScrollMenuContent.DEFAULT_ICONWIDTH, DEFAULT_ROWHEIGHT);
            theIcon.setMinimumSize(myDim);
            theIcon.setPreferredSize(myDim);
            theIcon.setMaximumSize(myDim);

            /* Add the children */
            add(theIcon);
            add(theLabel);
        }

        /**
         * Constructor.
         * @param pGraphic the icon for the item
         */
        private ScrollElement(final Icon pGraphic) {
            /* No label required */
            theLabel = null;

            /* Create a Label for the graphic */
            theIcon = new JLabel();
            theIcon.setIcon(pGraphic);
            theIcon.setHorizontalAlignment(SwingConstants.CENTER);

            /* Add the children */
            add(theIcon);
        }

        /**
         * Obtain the label.
         * @return the label
         */
        protected JLabel getLabel() {
            return theLabel;
        }

        /**
         * Obtain the text.
         * @return the text
         */
        public String getText() {
            return theLabel.getText();
        }

        /**
         * Set the graphic.
         * @param pGraphic the graphic
         */
        protected void setIcon(final Icon pGraphic) {
            theIcon.setIcon(pGraphic);
        }

        /**
         * Set the active indication.
         * @param pActive true/false
         */
        protected void setActive(final boolean pActive) {
            if (pActive) {
                if (theBaseColor == null) {
                    theBaseColor = getBackground();
                    setBackground(COLOR_BACKGROUND);
                }
            } else {
                if (theBaseColor != null) {
                    setBackground(theBaseColor);
                    theBaseColor = null;
                }
            }
        }
    }

    /**
     * Scroll item.
     * @param <T> the value type
     */
    protected static class ScrollSwingMenuItem<T>
            extends ScrollElement
            implements ScrollMenuItem<T> {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -5789994956291385390L;

        /**
         * Parent context menu.
         */
        private final transient ScrollSwingContextMenu<T> theContext;

        /**
         * The index.
         */
        private final int theIndex;

        /**
         * Associated value.
         */
        private final transient T theValue;

        /**
         * Constructor.
         * @param pContext the parent context menu
         * @param pValue the value
         * @param pName the display name
         * @param pGraphic the icon for the item
         */
        protected ScrollSwingMenuItem(final ScrollSwingContextMenu<T> pContext,
                                      final T pValue,
                                      final String pName,
                                      final Icon pGraphic) {
            /* Call super-constructor */
            super(pName, pGraphic);

            /* Record parameters */
            theContext = pContext;
            theValue = pValue;

            /* Determine the index */
            theIndex = theContext.getItemCount();

            /* Handle removal of subMenus */
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(final MouseEvent e) {
                    theContext.handleActiveItem(ScrollSwingMenuItem.this);
                    setActive(true);
                }

                @Override
                public void mouseExited(final MouseEvent e) {
                    setActive(false);
                }

                @Override
                public void mouseClicked(final MouseEvent e) {
                    theContext.setSelectedItem(ScrollSwingMenuItem.this);
                }
            });
        }

        @Override
        public T getValue() {
            return theValue;
        }

        @Override
        public void scrollToItem() {
            theContext.scrollToIndex(theIndex);
        }
    }

    /**
     * Scroll item.
     * @param <T> the value type
     */
    private static final class ScrollSwingToggleItem<T>
            extends ScrollSwingMenuItem<T>
            implements ScrollMenuToggleItem<T> {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -4663974362053903008L;

        /**
         * Selected state.
         */
        private boolean isSelected;

        /**
         * Constructor.
         * @param pContext the parent context menu
         * @param pValue the value
         * @param pName the display name
         */
        private ScrollSwingToggleItem(final ScrollSwingContextMenu<T> pContext,
                                      final T pValue,
                                      final String pName) {
            /* Call super-constructor */
            super(pContext, pValue, pName, null);
        }

        @Override
        public boolean isSelected() {
            return isSelected;
        }

        @Override
        public void setSelected(final boolean pSelected) {
            isSelected = pSelected;
            setIcon(isSelected
                               ? CHECK_ICON
                               : null);
        }

        @Override
        public void toggleSelected() {
            setSelected(!isSelected);
        }
    }

    /**
     * Scroll menu.
     * @param <T> the value type
     */
    public static final class ScrollSwingSubMenu<T>
            extends ScrollElement
            implements ScrollSubMenu<T, Icon> {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 2927699457612602250L;

        /**
         * Parent contextMenu.
         */
        private final transient ScrollSwingContextMenu<T> theContext;

        /**
         * The index.
         */
        private final int theIndex;

        /**
         * Associated value.
         */
        private final transient ScrollSwingContextMenu<T> theSubMenu;

        /**
         * Constructor.
         * @param pContext the parent context menu
         * @param pName the name
         * @param pGraphic the icon for the menu
         */
        private ScrollSwingSubMenu(final ScrollSwingContextMenu<T> pContext,
                                   final String pName,
                                   final Icon pGraphic) {
            /* Call super-constructor */
            super(pName, pGraphic);

            /* Record parameters */
            theContext = pContext;

            /* Create the subMenu */
            theSubMenu = new ScrollSwingContextMenu<T>(this);

            /* Determine the index */
            theIndex = theContext.getItemCount();

            /* Set text */
            JLabel myLabel = getLabel();
            myLabel.setIcon(ArrowIcon.RIGHT);
            myLabel.setHorizontalTextPosition(SwingConstants.LEFT);

            /* Handle show menu */
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(final MouseEvent e) {
                    /* handle the active menu */
                    theContext.handleActiveMenu(ScrollSwingSubMenu.this);
                    setActive(true);

                    /* Show the menu */
                    theSubMenu.showMenuAtPosition(ScrollSwingSubMenu.this, SwingConstants.RIGHT);
                }

                @Override
                public void mouseExited(final MouseEvent e) {
                    setActive(false);
                }
            });
        }

        /**
         * Obtain the parent.
         * @return the parent
         */
        private ScrollSwingContextMenu<T> getContext() {
            return theContext;
        }

        @Override
        public ScrollSwingContextMenu<T> getSubMenu() {
            return theSubMenu;
        }

        /**
         * Obtain the index.
         * @return the index
         */
        private int getIndex() {
            return theIndex;
        }

        /**
         * Hide the subMenu.
         */
        private void hideMenu() {
            theSubMenu.closeMenu();
        }

        /**
         * Obtain the dialog.
         * @return the dialog
         */
        private JDialog getDialog() {
            return theContext.getDialog();
        }

        /**
         * Set the number of items in the scrolling portion of the menu.
         * @param pMaxDisplayItems the maximum number of items to display
         * @throws IllegalArgumentException if pMaxDisplayItems is 0 or negative
         */
        private void setMaxDisplayItems(final int pMaxDisplayItems) {
            /* Pass call to subMenu */
            theSubMenu.setMaxDisplayItems(pMaxDisplayItems);
        }

        /**
         * Ensure that this menu is visible immediately the context is displayed.
         */
        private void scrollToMenu() {
            theContext.scrollToIndex(theIndex);
        }
    }

    /**
     * Scroll control class.
     */
    private final class ScrollControl
            extends ScrollElement {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 6027843917697048465L;

        /**
         * Increment.
         */
        private final int theIncrement;

        /**
         * Timer.
         */
        private transient TimerTask theTimerTask;

        /**
         * Constructor.
         * @param pIcon the icon
         * @param pIncrement the increment
         */
        private ScrollControl(final Icon pIcon,
                              final int pIncrement) {
            /* Set the icon for the item */
            super(pIcon);

            /* Store parameters */
            theIncrement = pIncrement;

            /* Handle show menu */
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                    processScroll();
                }

                @Override
                public void mouseEntered(final MouseEvent e) {
                    processMouseEnter();
                }

                @Override
                public void mouseExited(final MouseEvent e) {
                    processMouseExit();
                }
            });
        }

        /**
         * Process scroll event.
         */
        private void processScroll() {
            /* Request the scroll */
            requestScroll(theIncrement);
        }

        /**
         * Process mouseEnter event.
         */
        private void processMouseEnter() {
            /* cancel any existing task */
            if (theTimerTask != null) {
                theTimerTask.cancel();
            }

            /* Close any children */
            closeChildren();

            /* Set no active Item */
            theActiveItem = null;
            setActive(true);

            /* Create new timer task */
            theTimerTask = new TimerTask() {
                @Override
                public void run() {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            processScroll();
                        }
                    });
                }
            };

            /* Schedule the task */
            theTimer.schedule(theTimerTask, ScrollMenuContent.INITIAL_SCROLLDELAY, ScrollMenuContent.REPEAT_SCROLLDELAY);
        }

        /**
         * Process mouseExit event.
         */
        private void processMouseExit() {
            /* If the timer is stopped */
            if (theTimerTask != null) {
                theTimerTask.cancel();
                theTimerTask = null;
            }
            setActive(false);
        }
    }
}
