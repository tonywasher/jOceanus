/*******************************************************************************
 * Tethys: GUI Utilities
 * Copyright 2012,2025 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.tethys.swing.menu;

import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.tethys.api.base.TethysUIArrowIconId;
import net.sourceforge.joceanus.tethys.api.base.TethysUIConstant;
import net.sourceforge.joceanus.tethys.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.api.base.TethysUIIcon;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollIcon;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollItem;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollSubMenu;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollToggle;
import net.sourceforge.joceanus.tethys.core.menu.TethysUICoreScrollMenu;
import net.sourceforge.joceanus.tethys.swing.base.TethysUISwingArrowIcon;
import net.sourceforge.joceanus.tethys.swing.base.TethysUISwingIcon;
import net.sourceforge.joceanus.tethys.swing.base.TethysUISwingUtils;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
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

/**
 * Scroll-able version of ContextMenu.
 * <p>
 * Implemented as Stage since ContextMenu does not allow control of individual elements.
 * @param <T> the value type
 */
public class TethysUISwingScrollMenu<T>
        implements TethysUIScrollMenu<T> {
    /**
     * Background active colour.
     */
    private static final Color COLOR_BACKGROUND = Color.decode("#add8e6");

    /**
     * Timer.
     */
    private Timer theTimer;

    /**
     * List of menu items.
     */
    private final List<TethysUISwingScrollElement> theMenuItems;

    /**
     * The items panel.
     */
    private final JPanel theActiveItems;

    /**
     * First item to show in list.
     */
    private int theFirstIndex;

    /**
     * Max number of items to display in popUp.
     */
    private int theMaxDisplayItems;

    /**
     * The Event Manager.
     */
    private final OceanusEventManager<TethysUIEvent> theEventManager;

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
    private final TethysUISwingScrollSubMenu<T> theParentMenu;

    /**
     * The Parent contextMenu.
     */
    private final TethysUISwingScrollMenu<T> theParentContext;

    /**
     * The dialog.
     */
    private JDialog theDialog;

    /**
     * The Active subMenu.
     */
    private TethysUISwingScrollSubMenu<T> theActiveMenu;

    /**
     * The Active item.
     */
    private TethysUISwingScrollItem<T> theActiveItem;

    /**
     * The selected value.
     */
    private TethysUISwingScrollItem<T> theSelectedItem;

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
    TethysUISwingScrollMenu() {
        this(TethysUICoreScrollMenu.DEFAULT_ITEMCOUNT);
    }

    /**
     * Constructor.
     * @param pMaxDisplayItems the maximum number of items to display
     */
    TethysUISwingScrollMenu(final int pMaxDisplayItems) {
        this(null, pMaxDisplayItems);
    }

    /**
     * Constructor.
     * @param pParent the parent scroll menu
     */
    TethysUISwingScrollMenu(final TethysUISwingScrollSubMenu<T> pParent) {
        this(pParent, pParent.getContext().getMaxDisplayItems());
    }

    /**
     * Constructor.
     * @param pParent the parent scroll menu
     * @param pMaxDisplayItems the maximum number of items to display
     */
    TethysUISwingScrollMenu(final TethysUISwingScrollSubMenu<T> pParent,
                            final int pMaxDisplayItems) {
        /* Check parameters */
        if (pMaxDisplayItems <= 0) {
            throw new IllegalArgumentException(TethysUICoreScrollMenu.ERROR_MAXITEMS);
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
        theEventManager = new OceanusEventManager<>();

        /* Create the scroll items */
        theUpItem = new ScrollControl(TethysUISwingArrowIcon.getIconForId(TethysUIArrowIconId.UP), -1);
        theDownItem = new ScrollControl(TethysUISwingArrowIcon.getIconForId(TethysUIArrowIconId.DOWN), 1);

        /* Allocate the list */
        theMenuItems = new ArrayList<>();
        theActiveItems = new JPanel();
        theActiveItems.setLayout(new BoxLayout(theActiveItems, BoxLayout.Y_AXIS));
        theActiveItems.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        /* Create the container */
        theContainer = new JPanel();
        theContainer.setLayout(new BorderLayout());
        theContainer.setBorder(BorderFactory.createLineBorder(Color.lightGray));
    }

    @Override
    public OceanusEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public TethysUIScrollItem<T> getSelectedItem() {
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
    Timer getTimer() {
        return theTimer;
    }

    /**
     * Obtain the dialog.
     * @return the dialog
     */
    JDialog getDialog() {
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
            throw new IllegalArgumentException(TethysUICoreScrollMenu.ERROR_MAXITEMS);
        }

        /* Check state */
        if (theDialog != null
                && theDialog.isVisible()) {
            throw new IllegalStateException();
        }

        /* Store parameters */
        theMaxDisplayItems = pMaxDisplayItems;

        /* Loop through the children */
        final Iterator<TethysUISwingScrollElement> myIterator = theMenuItems.iterator();
        while (myIterator.hasNext()) {
            final TethysUISwingScrollElement myChild = myIterator.next();

            /* If this is a subMenu */
            if (myChild instanceof TethysUISwingScrollSubMenu) {
                /* Pass call on */
                final TethysUISwingScrollSubMenu<?> mySubMenu = (TethysUISwingScrollSubMenu<?>) myChild;
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

        /* If we have elements */
        if (theMenuSize != null) {
            /* determine location to display */
            final Point myLocation = TethysUISwingUtils.obtainDisplayPoint(pAnchor, pSide, theMenuSize);

            /* Show menu */
            showMenuAtLocation(myLocation);
        }
    }

    /**
     * Show the menu at position.
     * @param pAnchor the anchor node
     * @param pSide the side of the anchor node
     */
    public void showMenuAtPosition(final Rectangle pAnchor,
                                   final int pSide) {
        /* Create the dialog */
        if (theDialog == null) {
            createDialog(null);
        }

        /* determine the size of the menu */
        determineSize();

        /* If we have elements */
        if (theMenuSize != null) {
            /* determine location to display */
            final Point myLocation = TethysUISwingUtils.obtainDisplayPoint(pAnchor, pSide, theMenuSize);

            /* Show menu */
            showMenuAtLocation(myLocation);
        }
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

        /* If we have elements */
        if (theMenuSize != null) {
            /* determine location to display */
            final Point myRequest = new Point((int) pX, (int) pY);
            final Point myLocation = TethysUISwingUtils.obtainDisplayPoint(pAnchor, myRequest, theMenuSize);

            /* Show menu */
            showMenuAtLocation(myLocation);
        }
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
    void closeMenu() {
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
     * Create subDialog.
     * @param pOwner the owner component
     */
    void ensureSubDialog(final TethysUISwingScrollSubMenu<T> pOwner) {
        /* If dialog does not exist */
        if (theDialog == null) {
            /* Create the new dialog */
            theDialog = new JDialog(pOwner.getDialog(), false);
            initDialog();
        }
    }

    /**
     * Create dialog.
     * @param pOwner the owner component
     */
    private void createDialog(final Component pOwner) {
        /* Create the new dialog */
        theDialog = new JDialog(JOptionPane.getFrameForComponent(pOwner), false);
        initDialog();
    }

    /**
     * Initialise dialog.
     */
    private void initDialog() {
        /* Create the new dialog */
        theDialog.setUndecorated(true);
        theDialog.getContentPane().add(theContainer);
        needReBuild = true;

        /* Add listeners */
        final ScrollListener myListener = new ScrollListener();
        theDialog.addFocusListener(myListener);
        theDialog.addKeyListener(myListener);
        theDialog.addMouseWheelListener(myListener);
    }

    /**
     * ScrollListener.
     */
    private final class ScrollListener
            implements FocusListener, KeyListener, MouseWheelListener {
        @Override
        public void focusGained(final FocusEvent e) {
            /* NoOp */
        }

        @Override
        public void focusLost(final FocusEvent e) {
            handleFocusLost();
        }

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

        @Override
        public void mouseWheelMoved(final MouseWheelEvent e) {
            /* Request the scroll */
            requestScroll(e.getWheelRotation());
            e.consume();
        }
    }

    /**
     * Handle focus lost event.
     */
    void handleFocusLost() {
        /* If we've lost focus to other than the active subMenu */
        if (theActiveMenu == null) {
            /* fire cancellation event */
            if (theParentMenu == null) {
                theEventManager.fireEvent(TethysUIEvent.WINDOWCLOSED);
            }

            /* Close the menu hierarchy if we are currently showing */
            if (theDialog != null
                    && theDialog.isShowing()) {
                closeOnFocusLoss();
            }
        }
    }

    /**
     * Set the selected item.
     * @param pItem the selected item
     */
    void setSelectedItem(final TethysUISwingScrollItem<T> pItem) {
        /* If we are a child menu */
        if (theParentContext != null) {
            /* pass call on to parent */
            theParentContext.setSelectedItem(pItem);

            /* else we are top-level */
        } else {
            /* We assume that we will close the menu */
            boolean doCloseMenu = true;

            /* record selection */
            theSelectedItem = pItem;
            if (theSelectedItem instanceof TethysUIScrollToggle) {
                final TethysUIScrollToggle<?> myItem = (TethysUIScrollToggle<?>) theSelectedItem;
                myItem.toggleSelected();
                doCloseMenu = closeOnToggle;
            }

            /* Close the menu if requested */
            if (doCloseMenu) {
                /* Close the menu */
                closeMenu();
            }

            /* fire selection event */
            theEventManager.fireEvent(TethysUIEvent.NEWVALUE, theSelectedItem);
        }
    }

    /**
     * Handle escapeKey.
     */
    void handleEscapeKey() {
        /* If we are a child menu */
        if (theParentContext != null) {
            /* pass call on to parent */
            theParentContext.handleEscapeKey();

            /* else we are top-level */
        } else {
            /* fire cancellation event */
            theEventManager.fireEvent(TethysUIEvent.WINDOWCLOSED);

            /* Notify the cancel */
            closeMenu();
        }
    }

    /**
     * Handle enterKey.
     */
    void handleEnterKey() {
        /* If we are a child menu */
        if (theActiveItem != null) {
            /* assume item is selected */
            setSelectedItem(theActiveItem);
        }
    }

    /**
     * Clear active Item.
     */
    void clearActiveItem() {
        theActiveItem = null;
    }

    /**
     * Handle activeItem.
     * @param pItem the item
     */
    void handleActiveItem(final TethysUISwingScrollItem<T> pItem) {
        /* Close any children */
        closeChildren();

        /* Record that we are the active item */
        theActiveItem = pItem;
    }

    /**
     * Handle activeMenu.
     * @param pMenu the menu
     */
    void handleActiveMenu(final TethysUISwingScrollSubMenu<T> pMenu) {
        /* Reset existing item */
        theActiveItem = null;

        /* Hide any existing menu that is not us */
        if (theActiveMenu != null
                && theActiveMenu.getIndex() != pMenu.getIndex()) {
            theActiveMenu.hideMenu();
        }

        /* Record active menu */
        theActiveMenu = pMenu;
    }

    @Override
    public void removeAllItems() {
        /* Check state */
        if (theDialog != null
                && theDialog.isVisible()) {
            theDialog.setVisible(false);
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
    public TethysUIScrollItem<T> addItem(final T pValue) {
        /* Use standard name */
        return addItem(pValue, pValue.toString(), null);
    }

    @Override
    public TethysUIScrollItem<T> addItem(final T pValue,
                                         final String pName) {
        /* Use standard name */
        return addItem(pValue, pName, null);
    }

    @Override
    public TethysUIScrollItem<T> addItem(final T pValue,
                                         final TethysUIIcon pGraphic) {
        /* Use standard name */
        return addItem(pValue, pValue.toString(), pGraphic);
    }

    @Override
    public TethysUIScrollItem<T> addNullItem(final String pName) {
        /* Use given name */
        return addItem(null, pName, null);
    }

    @Override
    public TethysUIScrollItem<T> addNullItem(final String pName,
                                             final TethysUIIcon pGraphic) {
        /* Use given name */
        return addItem(null, pName, pGraphic);
    }

    @Override
    public TethysUIScrollItem<T> addItem(final T pValue,
                                         final String pName,
                                         final TethysUIIcon pGraphic) {
        /* Check state */
        if (theDialog != null
                && theDialog.isVisible()) {
            throw new IllegalStateException();
        }

        /* Create element */
        final TethysUISwingScrollItem<T> myItem = new TethysUISwingScrollItem<>(this, pValue, pName, pGraphic);

        /* Add to the list of menuItems */
        theMenuItems.add(myItem);
        needReBuild = true;
        return myItem;
    }

    @Override
    public TethysUIScrollSubMenu<T> addSubMenu(final String pName) {
        /* Use given name */
        return addSubMenu(pName, null);
    }

    @Override
    public TethysUIScrollSubMenu<T> addSubMenu(final String pName,
                                               final TethysUIIcon pGraphic) {
        /* Check state */
        if (theDialog != null
                && theDialog.isVisible()) {
            throw new IllegalStateException();
        }

        /* Create menu */
        final TethysUISwingScrollSubMenu<T> myMenu = new TethysUISwingScrollSubMenu<>(this, pName, pGraphic);

        /* Add to the list of menuItems */
        theMenuItems.add(myMenu);
        needReBuild = true;
        return myMenu;
    }

    @Override
    public TethysUIScrollToggle<T> addToggleItem(final T pValue) {
        /* Use standard name */
        return addToggleItem(pValue, pValue.toString());
    }

    @Override
    public TethysUIScrollToggle<T> addToggleItem(final T pValue,
                                                 final String pName) {
        /* Check state */
        if (theDialog != null
                && theDialog.isVisible()) {
            throw new IllegalStateException();
        }

        /* Create element */
        final TethysUISwingScrollToggle<T> myItem = new TethysUISwingScrollToggle<>(this, pValue, pName);

        /* Add to the list of menuItems */
        theMenuItems.add(myItem);
        needReBuild = true;
        return myItem;
    }

    /**
     * close child menus.
     */
    void closeChildren() {
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
    void scrollToIndex(final int pIndex) {
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
        final int myCount = theMenuItems.size();
        if (pIndex < 0
                || pIndex >= myCount) {
            return;
        }

        /* If index is above window */
        if (pIndex < theFirstIndex) {
            /* Scroll window upwards and return */
            requestScroll(pIndex - theFirstIndex);
            return;
        }

        /* If index is beyond last visible index */
        final int myLastIndex = theFirstIndex
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
        /* NoOp if we do not need to reBuild the menu */
        if (!needReBuild) {
            return;
        }

        /* If we have items */
        if (!theMenuItems.isEmpty()) {
            /* Access the number of entries and the scroll count */
            final int myCount = theMenuItems.size();
            final int myScroll = Math.min(theMaxDisplayItems, myCount);

            /* Remove all items */
            theActiveItems.removeAll();
            theContainer.removeAll();
            theContainer.add(theActiveItems, BorderLayout.CENTER);

            /* If we do not need to scroll */
            if (myScroll == myCount) {
                /* Loop through the items to add */
                for (int i = 0; i < myCount; i++) {
                    /* Add the items */
                    theActiveItems.add(theMenuItems.get(i).getPanel());
                }

                /* Calculate size of menu */
                theDialog.pack();

                /* Determine the size */
                theMenuSize = new Dimension(theDialog.getWidth(), theDialog.getHeight());

                /* else need to set up scroll */
            } else {
                /* Add the scrolling items */
                theContainer.add(theUpItem.getPanel(), BorderLayout.PAGE_START);
                theContainer.add(theDownItem.getPanel(), BorderLayout.PAGE_END);
                theUpItem.getPanel().setEnabled(true);
                theDownItem.getPanel().setEnabled(true);

                /* Add ALL items */
                for (final TethysUISwingScrollElement myItem : theMenuItems) {
                    /* Add the items */
                    theActiveItems.add(myItem.getPanel());
                }

                /* Calculate size of menu */
                theDialog.pack();
                final int myWidth = theDialog.getWidth();

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
                    theActiveItems.add(theMenuItems.get(i).getPanel());
                }

                /* Calculate size of menu */
                theDialog.pack();
                final int myHeight = theDialog.getHeight();

                /* Set visibility of scroll items */
                theUpItem.getPanel().setEnabled(theFirstIndex > 0);
                theDownItem.getPanel().setEnabled(myMaxIndex < myCount);

                /* Determine the size */
                theMenuSize = new Dimension(myWidth, myHeight);

                /* Fix the width */
                theDialog.setPreferredSize(theMenuSize);
                theDialog.pack();
            }

            /* else reset the menuSize */
        } else {
            theMenuSize = null;
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
            final int myCount = theMenuItems.size();

            /* Reset the children */
            closeChildren();

            /* Ensure Up item is enabled */
            theUpItem.getPanel().setEnabled(true);

            /* Remove the first item */
            theActiveItems.remove(0);

            /* Add the final item */
            final int myLast = theFirstIndex + theMaxDisplayItems;
            final TethysUISwingScrollElement myItem = theMenuItems.get(myLast);
            myItem.setActive(false);
            theActiveItems.add(myItem.getPanel());

            /* Adjust down item */
            theDownItem.getPanel().setEnabled(myLast + 1 < myCount);

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
            theDownItem.getPanel().setEnabled(true);

            /* Remove the last item */
            theActiveItems.remove(theMaxDisplayItems - 1);

            /* Add the initial item */
            final TethysUISwingScrollElement myItem = theMenuItems.get(theFirstIndex - 1);
            myItem.setActive(false);
            theActiveItems.add(myItem.getPanel(), 0);

            /* Adjust up item */
            theUpItem.getPanel().setEnabled(theFirstIndex > 1);

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
    void requestScroll(final int pDelta) {
        /* If this is a scroll downwards */
        if (pDelta > 0) {
            /* If we can scroll downwards */
            final int myCount = theMenuItems.size();
            final int mySpace = myCount - theFirstIndex - theMaxDisplayItems;
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
    public abstract static class TethysUISwingScrollElement {
        /**
         * Default row size.
         */
        private static final int DEFAULT_ROWHEIGHT = 16;

        /**
         * The panel.
         */
        private final JPanel thePanel;

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
        private TethysUISwingScrollElement(final String pName,
                                           final TethysUIIcon pGraphic) {
            /* Create the panel */
            thePanel = new JPanel();

            /* Set border layout */
            thePanel.setLayout(new BorderLayout());

            /* Create a Label for the name */
            theLabel = new JLabel();
            theLabel.setHorizontalAlignment(SwingConstants.LEFT);
            theLabel.setText(pName);
            theLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, DEFAULT_ROWHEIGHT));

            /* Create a Label for the graphic */
            theIcon = new JLabel();
            theIcon.setIcon(TethysUISwingIcon.getIcon(pGraphic));
            final Dimension myDim = new Dimension(TethysUIConstant.DEFAULT_ICONSIZE, DEFAULT_ROWHEIGHT);
            theIcon.setMinimumSize(myDim);
            theIcon.setPreferredSize(myDim);
            theIcon.setMaximumSize(myDim);

            /* Add the children */
            thePanel.add(theIcon, BorderLayout.LINE_START);
            thePanel.add(theLabel, BorderLayout.CENTER);
        }

        /**
         * Constructor.
         * @param pGraphic the icon for the item
         */
        private TethysUISwingScrollElement(final TethysUIIcon pGraphic) {
            /* Create the panel */
            thePanel = new JPanel();

            /* No label required */
            theLabel = null;

            /* Create a Label for the graphic */
            theIcon = new JLabel();
            theIcon.setIcon(TethysUISwingIcon.getIcon(pGraphic));
            theIcon.setHorizontalAlignment(SwingConstants.CENTER);

            /* Add the children */
            thePanel.add(theIcon);
        }

        /**
         * Obtain the panel.
         * @return the panel
         */
        protected JPanel getPanel() {
            return thePanel;
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
        protected void setIcon(final TethysUIIcon pGraphic) {
            theIcon.setIcon(TethysUISwingIcon.getIcon(pGraphic));
        }

        /**
         * Set the active indication.
         * @param pActive true/false
         */
        protected void setActive(final boolean pActive) {
            if (pActive) {
                if (theBaseColor == null) {
                    theBaseColor = thePanel.getBackground();
                    thePanel.setBackground(COLOR_BACKGROUND);
                }
            } else {
                if (theBaseColor != null) {
                    thePanel.setBackground(theBaseColor);
                    theBaseColor = null;
                }
            }
        }

        /**
         * Add Menu icon.
         */
        protected void addMenuIcon() {
            final JLabel myLabel = new JLabel(TethysUISwingArrowIcon.RIGHT);
            thePanel.add(myLabel, BorderLayout.LINE_END);
        }
    }

    /**
     * Scroll item.
     * @param <T> the value type
     */
    protected static class TethysUISwingScrollItem<T>
            extends TethysUISwingScrollElement
            implements TethysUIScrollItem<T> {
        /**
         * Parent context menu.
         */
        private final TethysUISwingScrollMenu<T> theContext;

        /**
         * The index.
         */
        private final int theIndex;

        /**
         * Associated value.
         */
        private final T theValue;

        /**
         * Constructor.
         * @param pContext the parent context menu
         * @param pValue the value
         * @param pName the display name
         * @param pGraphic the icon for the item
         */
        protected TethysUISwingScrollItem(final TethysUISwingScrollMenu<T> pContext,
                                          final T pValue,
                                          final String pName,
                                          final TethysUIIcon pGraphic) {
            /* Call super-constructor */
            super(pName, pGraphic);

            /* Record parameters */
            theContext = pContext;
            theValue = pValue;

            /* Determine the index */
            theIndex = theContext.getItemCount();

            /* Handle removal of subMenus */
            getPanel().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(final MouseEvent e) {
                    handleMouseEntered();
                    setActive(true);
                }

                @Override
                public void mouseExited(final MouseEvent e) {
                    setActive(false);
                }

                @Override
                public void mouseClicked(final MouseEvent e) {
                    setActive(false);
                    handleMouseClicked();
                }
            });
        }

        /**
         * handle mouseClicked.
         */
        void handleMouseClicked() {
            theContext.setSelectedItem(this);
        }

        /**
         * handle mouseEntered.
         */
        void handleMouseEntered() {
            theContext.handleActiveItem(this);
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
    private static final class TethysUISwingScrollToggle<T>
            extends TethysUISwingScrollItem<T>
            implements TethysUIScrollToggle<T> {
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
        TethysUISwingScrollToggle(final TethysUISwingScrollMenu<T> pContext,
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
                    ? TethysUISwingUtils.getIconAtSize(TethysUIScrollIcon.CHECKMARK, TethysUIConstant.DEFAULT_ICONSIZE)
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
    public static final class TethysUISwingScrollSubMenu<T>
            extends TethysUISwingScrollElement
            implements TethysUIScrollSubMenu<T> {
        /**
         * Parent contextMenu.
         */
        private final TethysUISwingScrollMenu<T> theContext;

        /**
         * The index.
         */
        private final int theIndex;

        /**
         * Associated value.
         */
        private final TethysUISwingScrollMenu<T> theSubMenu;

        /**
         * Constructor.
         * @param pContext the parent context menu
         * @param pName the name
         * @param pGraphic the icon for the menu
         */
        TethysUISwingScrollSubMenu(final TethysUISwingScrollMenu<T> pContext,
                                   final String pName,
                                   final TethysUIIcon pGraphic) {
            /* Call super-constructor */
            super(pName, pGraphic);

            /* Record parameters */
            theContext = pContext;

            /* Create the subMenu */
            theSubMenu = new TethysUISwingScrollMenu<>(this);

            /* Determine the index */
            theIndex = theContext.getItemCount();

            /* Set menu icon */
            addMenuIcon();

            /* Handle show menu */
            getPanel().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(final MouseEvent e) {
                    handleMouseEntered();
                }

                @Override
                public void mouseExited(final MouseEvent e) {
                    setActive(false);
                }
            });
        }

        /**
         * handle MouseEntered.
         */
        void handleMouseEntered() {
            /* handle the active menu */
            theContext.handleActiveMenu(this);
            setActive(true);

            /* Show the menu */
            theSubMenu.ensureSubDialog(this);
            theSubMenu.showMenuAtPosition(getPanel(), SwingConstants.RIGHT);
        }

        /**
         * Obtain the parent.
         * @return the parent
         */
        TethysUISwingScrollMenu<T> getContext() {
            return theContext;
        }

        @Override
        public TethysUISwingScrollMenu<T> getSubMenu() {
            return theSubMenu;
        }

        /**
         * Obtain the index.
         * @return the index
         */
        int getIndex() {
            return theIndex;
        }

        /**
         * Hide the subMenu.
         */
        void hideMenu() {
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
        void scrollToMenu() {
            theContext.scrollToIndex(theIndex);
        }
    }

    /**
     * Scroll control class.
     */
    private final class ScrollControl
            extends TethysUISwingScrollElement {
        /**
         * Increment.
         */
        private final int theIncrement;

        /**
         * Timer.
         */
        private TimerTask theTimerTask;

        /**
         * Constructor.
         * @param pIcon the icon
         * @param pIncrement the increment
         */
        ScrollControl(final TethysUIIcon pIcon,
                      final int pIncrement) {
            /* Set the icon for the item */
            super(pIcon);

            /* Store parameters */
            theIncrement = pIncrement;

            /* Handle show menu */
            getPanel().addMouseListener(new MouseAdapter() {
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
        void processScroll() {
            /* Request the scroll */
            requestScroll(theIncrement);
        }

        /**
         * Process mouseEnter event.
         */
        void processMouseEnter() {
            /* cancel any existing task */
            if (theTimerTask != null) {
                theTimerTask.cancel();
            }

            /* Close any children */
            closeChildren();

            /* Set no active Item */
            clearActiveItem();
            setActive(true);

            /* Create new timer task */
            theTimerTask = new TimerTask() {
                @Override
                public void run() {
                    SwingUtilities.invokeLater(ScrollControl.this::processScroll);
                }
            };

            /* Schedule the task */
            getTimer().schedule(theTimerTask, TethysUICoreScrollMenu.INITIAL_SCROLLDELAY, TethysUICoreScrollMenu.REPEAT_SCROLLDELAY);
        }

        /**
         * Process mouseExit event.
         */
        void processMouseExit() {
            /* If the timer is stopped */
            if (theTimerTask != null) {
                theTimerTask.cancel();
                theTimerTask = null;
            }
            setActive(false);
        }
    }
}
