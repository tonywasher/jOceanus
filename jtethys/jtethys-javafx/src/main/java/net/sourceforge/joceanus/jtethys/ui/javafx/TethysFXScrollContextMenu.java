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
package net.sourceforge.joceanus.jtethys.ui.javafx;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Polygon;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import net.sourceforge.joceanus.jtethys.ui.TethysIconBuilder;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollIcon;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuToggleItem;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollSubMenu;

/**
 * Scroll-able version of ContextMenu.
 * <p>
 * Implemented as Stage since ContextMenu does not allow control of individual elements.
 * @param <T> the value type
 */
public class TethysFXScrollContextMenu<T>
        extends Stage
        implements TethysScrollMenu<T, Node> {
    /**
     * StyleSheet Name.
     */
    private static final String CSS_STYLE_NAME = "jtethys-javafx-contextmenu.css";

    /**
     * StyleSheet.
     */
    private static final String CSS_STYLE = TethysFXScrollContextMenu.class.getResource(CSS_STYLE_NAME).toExternalForm();

    /**
     * The menu style.
     */
    private static final String STYLE_MENU = TethysFXGuiFactory.CSS_STYLE_BASE + "-context";

    /**
     * The item style.
     */
    private static final String STYLE_ITEM = STYLE_MENU + "-item";

    /**
     * List of menu items.
     */
    private final List<TethysFXScrollElement> theMenuItems;

    /**
     * List of active menu items.
     */
    private final ObservableList<Node> theActiveItems;

    /**
     * First item to show in list.
     */
    private int theFirstIndex = 0;

    /**
     * Max number of items to display in popUp.
     */
    private int theMaxDisplayItems;

    /**
     * The ScrollUp Item.
     */
    private final ScrollControl theUpItem;

    /**
     * The ScrollDown Item.
     */
    private final ScrollControl theDownItem;

    /**
     * The container box.
     */
    private final BorderPane theContainer;

    /**
     * The Parent scrollMenu.
     */
    private final TethysFXScrollSubMenu<T> theParentMenu;

    /**
     * The Parent contextMenu.
     */
    private final TethysFXScrollContextMenu<T> theParentContext;

    /**
     * The Active subMenu.
     */
    private TethysFXScrollSubMenu<T> theActiveMenu;

    /**
     * The Active item.
     */
    private TethysFXScrollMenuItem<T> theActiveItem;

    /**
     * The selected value.
     */
    private TethysFXScrollMenuItem<T> theSelectedItem;

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
    private Dimension2D theMenuSize;

    /**
     * Constructor.
     */
    protected TethysFXScrollContextMenu() {
        this(TethysScrollMenuContent.DEFAULT_ITEMCOUNT);
    }

    /**
     * Constructor.
     * @param pMaxDisplayItems the maximum number of items to display
     */
    private TethysFXScrollContextMenu(final int pMaxDisplayItems) {
        this(null, pMaxDisplayItems);
    }

    /**
     * Constructor.
     * @param pParent the parent scroll menu
     */
    private TethysFXScrollContextMenu(final TethysFXScrollSubMenu<T> pParent) {
        this(pParent, pParent.getContext().getMaxDisplayItems());
    }

    /**
     * Constructor.
     * @param pParent the parent scroll menu
     * @param pMaxDisplayItems the maximum number of items to display
     */
    private TethysFXScrollContextMenu(final TethysFXScrollSubMenu<T> pParent,
                                      final int pMaxDisplayItems) {
        /* Non-Modal and undecorated */
        super(StageStyle.UNDECORATED);
        initModality(Modality.NONE);

        /* Check parameters */
        if (pMaxDisplayItems <= 0) {
            throw new IllegalArgumentException(TethysScrollMenuContent.ERROR_MAXITEMS);
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

        /* Create the scroll items */
        theUpItem = new ScrollControl(TethysFXArrowIcon.UP.getArrow(), -1);
        theDownItem = new ScrollControl(TethysFXArrowIcon.DOWN.getArrow(), 1);

        /* Allocate the list */
        theMenuItems = new ArrayList<>();
        VBox myBox = new VBox();
        myBox.setSpacing(2);
        myBox.setPadding(new Insets(2, 2, 2, 2));
        theActiveItems = myBox.getChildren();

        /* Create the scene */
        theContainer = new BorderPane();
        Scene myScene = new Scene(theContainer);
        theContainer.setCenter(myBox);
        ObservableList<String> mySheets = myScene.getStylesheets();
        mySheets.add(CSS_STYLE);
        theContainer.getStyleClass().add(STYLE_MENU);
        setScene(myScene);

        /* Add listener to shut dialog on loss of focus */
        focusedProperty().addListener((v, o, n) -> handleFocusChange(n));

        /* ensure that escape closes menu */
        myScene.setOnKeyPressed(this::handleKeyPress);

        /* Handle scroll events */
        addEventFilter(ScrollEvent.SCROLL, this::handleScroll);
    }

    /**
     * CloseOnFocusLoss.
     */
    private void closeOnFocusLoss() {
        /* If we have lost focus */
        /* Pass call on to parent if it exists */
        if (theParentContext != null) {
            theParentContext.closeOnFocusLoss();
        }

        /* Close the menu */
        closeMenu();
    }

    @Override
    public TethysScrollMenuItem<T> getSelectedItem() {
        return theSelectedItem;
    }

    /**
     * Obtain the maximum # of items in the displayed PopUp window.
     * @return the # of items
     */
    public int getMaxDisplayItems() {
        return theMaxDisplayItems;
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
            throw new IllegalArgumentException(TethysScrollMenuContent.ERROR_MAXITEMS);
        }

        /* Check state */
        if (isShowing()) {
            throw new IllegalStateException();
        }

        /* Store parameters */
        theMaxDisplayItems = pMaxDisplayItems;

        /* Loop through the children */
        Iterator<TethysFXScrollElement> myIterator = theMenuItems.iterator();
        while (myIterator.hasNext()) {
            TethysFXScrollElement myChild = myIterator.next();

            /* If this is a subMenu */
            if (myChild instanceof TethysFXScrollSubMenu) {
                /* Pass call on */
                TethysFXScrollSubMenu<?> mySubMenu = (TethysFXScrollSubMenu<?>) myChild;
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
    public void showMenuAtPosition(final Node pAnchor,
                                   final Side pSide) {
        /* determine the size of the menu */
        determineSize();

        /* determine location to display */
        Point2D myLocation = TethysFXGuiUtils.obtainDisplayPoint(pAnchor, pSide, theMenuSize);

        /* Show menu */
        showMenuAtLocation(myLocation);
    }

    /**
     * Show the menu at position.
     * @param pAnchor the anchor node
     * @param pX the relative X position
     * @param pY the relative Y position
     */
    public void showMenuAtPosition(final Node pAnchor,
                                   final double pX,
                                   final double pY) {
        /* determine the size of the menu */
        determineSize();

        /* determine location to display */
        Point2D myRequest = new Point2D(pX, pY);
        Point2D myLocation = TethysFXGuiUtils.obtainDisplayPoint(pAnchor, myRequest, theMenuSize);

        /* Show menu */
        showMenuAtLocation(myLocation);
    }

    /**
     * Show the menu at location.
     * @param pLocation the location
     */
    private void showMenuAtLocation(final Point2D pLocation) {
        /* Record position */
        setX(pLocation.getX());
        setY(pLocation.getY());

        /* Show menu */
        showMenu();
    }

    /**
     * Show the menu.
     */
    private void showMenu() {
        /* Initialise the values */
        theSelectedItem = null;
        theActiveMenu = null;
        theActiveItem = null;

        /* show the menu */
        show();
    }

    /**
     * Close non-Modal.
     */
    private void closeMenu() {
        /* Close any children */
        closeChildren();

        /* Close the menu */
        close();
    }

    /**
     * Set the selected item.
     * @param pItem the selected item
     */
    private void setSelectedItem(final TethysFXScrollMenuItem<T> pItem) {
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
            if (theSelectedItem instanceof TethysScrollMenuToggleItem) {
                TethysScrollMenuToggleItem<?> myItem = (TethysScrollMenuToggleItem<?>) theSelectedItem;
                myItem.toggleSelected();
                doCloseMenu = closeOnToggle;
            }

            /* fire selection event */
            fireEvent(new TethysFXContextEvent<T>(theSelectedItem));

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
            fireEvent(new TethysFXContextEvent<T>());

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
     * Handle focusChange.
     * @param pState the focus state
     */
    private void handleFocusChange(final Boolean pState) {
        /* If we've lost focus to other than the active subMenu */
        if ((!pState)
            && (theActiveMenu == null)) {
            /* fire cancellation event */
            if (theParentMenu == null) {
                fireEvent(new TethysFXContextEvent<T>());
            }

            /* Close the menu hierarchy if we are currently showing */
            if (isShowing()) {
                closeOnFocusLoss();
            }
        }
    }

    /**
     * Handle keyPress.
     * @param pEvent the event
     */
    private void handleKeyPress(final KeyEvent pEvent) {
        switch (pEvent.getCode()) {
            case ESCAPE:
                handleEscapeKey();
                break;
            case ENTER:
                handleEnterKey();
                break;
            default:
                break;
        }
    }

    /**
     * Handle scroll.
     * @param pEvent the event
     */
    private void handleScroll(final ScrollEvent pEvent) {
        /* request the scroll */
        double myDelta = pEvent.getDeltaY() / pEvent.getMultiplierY();
        requestScroll((int) -myDelta);

        /* Consume the event */
        pEvent.consume();
    }

    /**
     * Handle activeItem.
     * @param pItem the item
     */
    private void handleActiveItem(final TethysFXScrollMenuItem<T> pItem) {
        /* Close any children */
        closeChildren();

        /* Record that we are the active item */
        theActiveItem = pItem;
    }

    /**
     * Handle activeMenu.
     * @param pMenu the menu
     */
    private void handleActiveMenu(final TethysFXScrollSubMenu<T> pMenu) {
        /* Hide any existing menu that is not us */
        if ((theActiveMenu != null)
            && (theActiveMenu.getIndex() != pMenu.getIndex())) {
            theActiveMenu.hide();
        }

        /* Set no active Item */
        theActiveItem = null;

        /* Record active menu */
        theActiveMenu = pMenu;
    }

    @Override
    public void removeAllItems() {
        /* Check state */
        if (isShowing()) {
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

    @Override
    public TethysScrollMenuItem<T> addItem(final T pValue) {
        /* Use standard name */
        return addItem(pValue, pValue.toString(), null);
    }

    @Override
    public TethysScrollMenuItem<T> addItem(final T pValue,
                                           final String pName) {
        /* Use standard name */
        return addItem(pValue, pName, null);
    }

    @Override
    public TethysScrollMenuItem<T> addItem(final T pValue,
                                           final Node pGraphic) {
        /* Use standard name */
        return addItem(pValue, pValue.toString(), pGraphic);
    }

    @Override
    public TethysScrollMenuItem<T> addNullItem(final String pName) {
        /* Use given name */
        return addItem(null, pName, null);
    }

    @Override
    public TethysScrollMenuItem<T> addNullItem(final String pName,
                                               final Node pGraphic) {
        /* Use given name */
        return addItem(null, pName, pGraphic);
    }

    @Override
    public TethysScrollMenuItem<T> addItem(final T pValue,
                                           final String pName,
                                           final Node pGraphic) {
        /* Check state */
        if (isShowing()) {
            throw new IllegalStateException();
        }

        /* Create element */
        TethysFXScrollMenuItem<T> myItem = new TethysFXScrollMenuItem<>(this, pValue, pName, pGraphic);

        /* Add to the list of menuItems */
        theMenuItems.add(myItem);
        needReBuild = true;
        return myItem;
    }

    @Override
    public TethysScrollSubMenu<T, Node> addSubMenu(final String pName) {
        /* Use given name */
        return addSubMenu(pName, null);
    }

    @Override
    public TethysScrollSubMenu<T, Node> addSubMenu(final String pName,
                                                   final Node pGraphic) {
        /* Check state */
        if (isShowing()) {
            throw new IllegalStateException();
        }

        /* Create menu */
        TethysFXScrollSubMenu<T> myMenu = new TethysFXScrollSubMenu<>(this, pName, pGraphic);

        /* Add to the list of menuItems */
        theMenuItems.add(myMenu);
        needReBuild = true;
        return myMenu;
    }

    @Override
    public TethysScrollMenuToggleItem<T> addToggleItem(final T pValue) {
        /* Use standard name */
        return addToggleItem(pValue, pValue.toString());
    }

    @Override
    public TethysScrollMenuToggleItem<T> addToggleItem(final T pValue,
                                                       final String pName) {
        /* Check state */
        if (isShowing()) {
            throw new IllegalStateException();
        }

        /* Create element */
        TethysFXScrollToggleItem<T> myItem = new TethysFXScrollToggleItem<>(this, pValue, pName);

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
            theActiveMenu.hide();
        }
        theActiveMenu = null;
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
            theActiveItems.clear();

            /* If we do not need to scroll */
            if (myScroll == myCount) {
                /* Ensure we have no arrows */
                theContainer.setTop(null);
                theContainer.setBottom(null);

                /* Loop through the items to add */
                for (int i = 0; i < myCount; i++) {
                    /* Add the items */
                    theActiveItems.add(theMenuItems.get(i));
                }

                /* Calculate size of menu */
                show();
                close();

                /* Determine the size */
                theMenuSize = new Dimension2D(getWidth(), getHeight());

                /* else need to set up scroll */
            } else {
                /* Add the scrolling items */
                theContainer.setTop(theUpItem);
                theContainer.setBottom(theDownItem);
                theUpItem.setVisible(true);
                theDownItem.setVisible(true);

                /* Add ALL items */
                for (TethysFXScrollElement myItem : theMenuItems) {
                    /* Add the items */
                    theActiveItems.add(myItem);
                }

                /* Calculate size of menu */
                show();
                close();
                double myWidth = getWidth();

                /* Remove all items */
                theActiveItems.clear();

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
                show();
                close();
                double myHeight = getHeight();

                /* Set visibility of scroll items */
                theUpItem.setVisible(theFirstIndex > 0);
                theDownItem.setVisible(myMaxIndex < myCount);

                /* Determine the size */
                theMenuSize = new Dimension2D(myWidth, myHeight);

                /* Fix the width */
                setMinWidth(myWidth);
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
            theUpItem.setVisible(true);

            /* Remove the first item */
            theActiveItems.remove(0);

            /* Add the final item */
            int myLast = theFirstIndex + theMaxDisplayItems;
            TethysFXScrollElement myItem = theMenuItems.get(myLast);
            theActiveItems.add(myItem);

            /* Adjust down item */
            theDownItem.setVisible(myLast + 1 < myCount);
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
            theDownItem.setVisible(true);

            /* Remove the last item */
            theActiveItems.remove(theMaxDisplayItems - 1);

            /* Add the initial item */
            TethysFXScrollElement myItem = theMenuItems.get(theFirstIndex - 1);
            theActiveItems.add(0, myItem);

            /* Adjust up item */
            theUpItem.setVisible(theFirstIndex > 1);
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
    public abstract static class TethysFXScrollElement
            extends BorderPane {
        /**
         * The label.
         */
        private final Label theLabel;

        /**
         * The icon label.
         */
        private final Label theIcon;

        /**
         * Constructor.
         * @param pName the display name
         * @param pGraphic the icon for the item
         */
        private TethysFXScrollElement(final String pName,
                                      final Node pGraphic) {
            /* Create a Label for the name */
            theLabel = new Label();
            theLabel.setText(pName);
            theLabel.setMaxWidth(Double.MAX_VALUE);

            /* Create a Label for the graphic */
            theIcon = new Label();
            theIcon.setGraphic(pGraphic);
            theIcon.setMinWidth(TethysIconBuilder.DEFAULT_ICONWIDTH);

            /* Add the children */
            setLeft(theIcon);
            setCenter(theLabel);

            /* Set style of item */
            getStyleClass().add(STYLE_ITEM);
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
        protected void setIcon(final Node pGraphic) {
            theIcon.setGraphic(pGraphic);
        }

        /**
         * Add Menu icon.
         */
        protected void addMenuIcon() {
            Label myLabel = new Label();
            myLabel.setGraphic(TethysFXArrowIcon.RIGHT.getArrow());
            setRight(myLabel);
        }
    }

    /**
     * Scroll item.
     * @param <T> the value type
     */
    protected static class TethysFXScrollMenuItem<T>
            extends TethysFXScrollElement
            implements TethysScrollMenuItem<T> {
        /**
         * Parent context menu.
         */
        private final TethysFXScrollContextMenu<T> theContext;

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
        protected TethysFXScrollMenuItem(final TethysFXScrollContextMenu<T> pContext,
                                         final T pValue,
                                         final String pName,
                                         final Node pGraphic) {
            /* Call super-constructor */
            super(pName, pGraphic);

            /* Record parameters */
            theContext = pContext;
            theValue = pValue;

            /* Determine the index */
            theIndex = theContext.getItemCount();

            /* Handle removal of subMenus */
            addEventFilter(MouseEvent.MOUSE_ENTERED, e -> theContext.handleActiveItem(TethysFXScrollMenuItem.this));

            /* Handle selection */
            addEventFilter(MouseEvent.MOUSE_CLICKED, e -> theContext.setSelectedItem(TethysFXScrollMenuItem.this));
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
    private static final class TethysFXScrollToggleItem<T>
            extends TethysFXScrollMenuItem<T>
            implements TethysScrollMenuToggleItem<T> {
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
        private TethysFXScrollToggleItem(final TethysFXScrollContextMenu<T> pContext,
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
                               ? TethysFXGuiUtils.getIconAtSize(TethysScrollIcon.CHECKMARK, TethysIconBuilder.DEFAULT_ICONWIDTH)
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
    public static final class TethysFXScrollSubMenu<T>
            extends TethysFXScrollElement
            implements TethysScrollSubMenu<T, Node> {
        /**
         * Parent contextMenu.
         */
        private final TethysFXScrollContextMenu<T> theContext;

        /**
         * The index.
         */
        private final int theIndex;

        /**
         * Associated value.
         */
        private final TethysFXScrollContextMenu<T> theSubMenu;

        /**
         * Constructor.
         * @param pContext the parent context menu
         * @param pName the name
         * @param pGraphic the icon for the menu
         */
        private TethysFXScrollSubMenu(final TethysFXScrollContextMenu<T> pContext,
                                      final String pName,
                                      final Node pGraphic) {
            /* Call super-constructor */
            super(pName, pGraphic);

            /* Record parameters */
            theContext = pContext;

            /* Create the subMenu */
            theSubMenu = new TethysFXScrollContextMenu<>(this);

            /* Determine the index */
            theIndex = theContext.getItemCount();

            /* Set menu icon */
            addMenuIcon();

            /* Handle show menu */
            addEventFilter(MouseEvent.MOUSE_ENTERED, e -> {
                theContext.handleActiveMenu(TethysFXScrollSubMenu.this);
                theSubMenu.showMenuAtPosition(TethysFXScrollSubMenu.this, Side.RIGHT);
            });
        }

        /**
         * Obtain the parent.
         * @return the parent
         */
        private TethysFXScrollContextMenu<T> getContext() {
            return theContext;
        }

        @Override
        public TethysFXScrollContextMenu<T> getSubMenu() {
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
        private void hide() {
            theSubMenu.hide();
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
            extends Label {
        /**
         * Increment.
         */
        private final int theIncrement;

        /**
         * KickStart Timer.
         */
        private final Timeline theKickStartTimer;

        /**
         * Repeat Timer.
         */
        private final Timeline theRepeatTimer;

        /**
         * Constructor.
         * @param pIcon the icon
         * @param pIncrement the increment
         */
        private ScrollControl(final Polygon pIcon,
                              final int pIncrement) {
            /* Set the icon for the item */
            setGraphic(pIcon);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            setAlignment(Pos.CENTER);
            setMaxWidth(Double.MAX_VALUE);
            getStyleClass().add(STYLE_ITEM);

            /* Store parameters */
            theIncrement = pIncrement;

            /* Handle selection */
            addEventFilter(MouseEvent.MOUSE_CLICKED, e -> processScroll());

            /* Handle mouse enters */
            addEventHandler(MouseEvent.MOUSE_ENTERED, e -> processMouseEnter());

            /* Handle mouse exits */
            addEventHandler(MouseEvent.MOUSE_EXITED, e -> processMouseExit());

            /* Create the timers */
            theKickStartTimer = new Timeline(new KeyFrame(Duration.millis(TethysScrollMenuContent.INITIAL_SCROLLDELAY),
                    e -> processScroll()));
            theRepeatTimer = new Timeline(new KeyFrame(Duration.millis(TethysScrollMenuContent.REPEAT_SCROLLDELAY),
                    e -> processScroll()));
        }

        /**
         * Process scroll event.
         */
        private void processScroll() {
            /* Request the scroll */
            requestScroll(theIncrement);
            theRepeatTimer.play();
        }

        /**
         * Process mouseEnter event.
         */
        private void processMouseEnter() {
            /* Close any children */
            closeChildren();

            /* Set no active Item */
            theActiveItem = null;

            /* Schedule the task */
            theKickStartTimer.play();
        }

        /**
         * Process mouseExit event.
         */
        private void processMouseExit() {
            /* Cancel any timer tasks */
            theKickStartTimer.stop();
            theRepeatTimer.stop();
        }
    }

    /**
     * ContextEvent.
     * @param <T> the value type
     */
    public static final class TethysFXContextEvent<T>
            extends Event {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -2289427191547125044L;

        /**
         * An item has been selected.
         */
        public static final EventType<TethysFXContextEvent<?>> MENU_SELECT = new EventType<>(Event.ANY, "MENU_SELECT");

        /**
         * An item has been selected.
         */
        public static final EventType<TethysFXContextEvent<?>> MENU_TOGGLE = new EventType<>(Event.ANY, "MENU_TOGGLE");

        /**
         * The menu has been cancelled.
         */
        public static final EventType<TethysFXContextEvent<?>> MENU_CANCEL = new EventType<>(Event.ANY, "MENU_CANCEL");

        /**
         * The item.
         */
        private final TethysFXScrollMenuItem<T> theItem;

        /**
         * Constructor.
         * @param pItem the selected item
         */
        public TethysFXContextEvent(final TethysFXScrollMenuItem<T> pItem) {
            super(pItem instanceof TethysFXScrollToggleItem
                                                            ? MENU_TOGGLE
                                                            : MENU_SELECT);
            theItem = pItem;
        }

        /**
         * Constructor.
         */
        public TethysFXContextEvent() {
            super(MENU_CANCEL);
            theItem = null;
        }

        /**
         * Obtain the toggled item.
         * @return the item
         */
        public TethysFXScrollToggleItem<T> getToggledItem() {
            return theItem instanceof TethysFXScrollToggleItem
                                                               ? (TethysFXScrollToggleItem<T>) theItem
                                                               : null;
        }

        /**
         * Obtain the selected item.
         * @return the item
         */
        public TethysFXScrollMenuItem<T> getSelectedItem() {
            return theItem instanceof TethysFXScrollToggleItem
                                                               ? null
                                                               : theItem;
        }
    }
}