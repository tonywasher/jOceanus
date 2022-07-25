/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.javafx.menu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
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

import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIIcon;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollIcon;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollItem;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollSubMenu;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollToggle;
import net.sourceforge.joceanus.jtethys.ui.core.base.TethysUICoreComponent;
import net.sourceforge.joceanus.jtethys.ui.core.menu.TethysUICoreScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.javafx.base.TethysUIFXArrowIcon;
import net.sourceforge.joceanus.jtethys.ui.javafx.base.TethysUIFXIcon;
import net.sourceforge.joceanus.jtethys.ui.javafx.base.TethysUIFXUtils;

/**
 * Scroll-able version of ContextMenu.
 * <p>
 * Implemented as Stage since ContextMenu does not allow control of individual elements.
 *
 * @param <T> the value type
 */
public class TethysUIFXScrollMenu<T>
        implements TethysUIScrollMenu<T> {
    /**
     * StyleSheet Name.
     */
    private static final String CSS_STYLE_NAME = "jtethys-javafx-contextmenu.css";

    /**
     * StyleSheet.
     */
    private static final String CSS_STYLE = TethysUIFXScrollMenu.class.getResource(CSS_STYLE_NAME).toExternalForm();

    /**
     * The menu style.
     */
    private static final String STYLE_MENU = TethysUIFXUtils.CSS_STYLE_BASE + "-context";

    /**
     * The item style.
     */
    static final String STYLE_ITEM = STYLE_MENU + "-item";

    /**
     * The Event Manager.
     */
    private final TethysEventManager<TethysUIEvent> theEventManager;

    /**
     * List of menu items.
     */
    private final List<TethysUIFXScrollElement> theMenuItems;

    /**
     * List of active menu items.
     */
    private final ObservableList<Node> theActiveItems;

    /**
     * First item to show in list.
     */
    private int theFirstIndex;

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
    private final TethysUIFXScrollSubMenu<T> theParentMenu;

    /**
     * The Parent contextMenu.
     */
    private final TethysUIFXScrollMenu<T> theParentContext;

    /**
     * The Stage.
     */
    private Stage theStage;

    /**
     * The Active subMenu.
     */
    private TethysUIFXScrollSubMenu<T> theActiveMenu;

    /**
     * The Active item.
     */
    private TethysUIFXScrollItem<T> theActiveItem;

    /**
     * The selected value.
     */
    private TethysUIFXScrollItem<T> theSelectedItem;

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
    TethysUIFXScrollMenu() {
        this(TethysUICoreScrollMenu.DEFAULT_ITEMCOUNT);
    }

    /**
     * Constructor.
     *
     * @param pMaxDisplayItems the maximum number of items to display
     */
    TethysUIFXScrollMenu(final int pMaxDisplayItems) {
        this(null, pMaxDisplayItems);
    }

    /**
     * Constructor.
     *
     * @param pParent the parent scroll menu
     */
    TethysUIFXScrollMenu(final TethysUIFXScrollSubMenu<T> pParent) {
        this(pParent, pParent.getContext().getMaxDisplayItems());
    }

    /**
     * Constructor.
     *
     * @param pParent          the parent scroll menu
     * @param pMaxDisplayItems the maximum number of items to display
     */
    private TethysUIFXScrollMenu(final TethysUIFXScrollSubMenu<T> pParent,
                                 final int pMaxDisplayItems) {
        /* Check parameters */
        if (pMaxDisplayItems <= 0) {
            throw new IllegalArgumentException(TethysUICoreScrollMenu.ERROR_MAXITEMS);
        }

        /* Create event manager */
        theEventManager = new TethysEventManager<>();

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
        theUpItem = new ScrollControl(TethysUIFXArrowIcon.UP.getArrow(), -1);
        theDownItem = new ScrollControl(TethysUIFXArrowIcon.DOWN.getArrow(), 1);

        /* Allocate the list */
        theMenuItems = new ArrayList<>();
        final VBox myBox = new VBox();
        myBox.setSpacing(2);
        myBox.setPadding(new Insets(2, 2, 2, 2));
        theActiveItems = myBox.getChildren();

        /* Create the scene */
        theContainer = new BorderPane();
        theContainer.setCenter(myBox);
        theContainer.getStyleClass().add(STYLE_MENU);
    }

    @Override
    public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Ensure the Stage.
     */
    private void ensureStage() {
        if (theStage == null) {
            createStage();
        }
    }

    /**
     * Create the Stage.
     */
    private void createStage() {
        /* Non-Modal and undecorated */
        theStage = new Stage(StageStyle.UNDECORATED);
        theStage.initModality(Modality.NONE);

        /* Create the scene */
        final Scene myScene = new Scene(theContainer);
        final ObservableList<String> mySheets = myScene.getStylesheets();
        mySheets.add(CSS_STYLE);
        theStage.setScene(myScene);

        /* Add listener to shut dialog on loss of focus */
        theStage.focusedProperty().addListener((v, o, n) -> handleFocusChange(n));

        /* ensure that escape closes menu */
        myScene.setOnKeyPressed(this::handleKeyPress);

        /* Handle scroll events */
        theStage.addEventFilter(ScrollEvent.SCROLL, this::handleScroll);
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

    @Override
    public TethysUIScrollItem<T> getSelectedItem() {
        return theSelectedItem;
    }

    /**
     * Obtain the maximum # of items in the displayed PopUp window.
     *
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

    /**
     * Is the menu showing?
     *
     * @return true/false
     */
    public boolean isShowing() {
        return theStage != null
                && theStage.isShowing();
    }

    @Override
    public void setMaxDisplayItems(final int pMaxDisplayItems) {
        /* Check parameters */
        if (pMaxDisplayItems <= 0) {
            throw new IllegalArgumentException(TethysUICoreScrollMenu.ERROR_MAXITEMS);
        }

        /* Check state */
        if (isShowing()) {
            throw new IllegalStateException();
        }

        /* Store parameters */
        theMaxDisplayItems = pMaxDisplayItems;

        /* Loop through the children */
        final Iterator<TethysUIFXScrollElement> myIterator = theMenuItems.iterator();
        while (myIterator.hasNext()) {
            final TethysUIFXScrollElement myChild = myIterator.next();

            /* If this is a subMenu */
            if (myChild instanceof TethysUIFXScrollSubMenu) {
                /* Pass call on */
                final TethysUIFXScrollSubMenu<?> mySubMenu = (TethysUIFXScrollSubMenu<?>) myChild;
                mySubMenu.setMaxDisplayItems(pMaxDisplayItems);
            }
        }

        /* Request reBuild */
        needReBuild = true;
    }

    /**
     * Show the menu at position.
     *
     * @param pAnchor the anchor node
     * @param pSide   the side of the anchor node
     */
    public void showMenuAtPosition(final Node pAnchor,
                                   final Side pSide) {
        /* Ensure the stage */
        ensureStage();

        /* determine the size of the menu */
        determineSize();

        /* If we have elements */
        if (theMenuSize != null) {
            /* determine location to display */
            final Point2D myLocation = TethysUIFXUtils.obtainDisplayPoint(pAnchor, pSide, theMenuSize);

            /* Show menu */
            showMenuAtLocation(myLocation);
        }
    }

    /**
     * Show the menu at position.
     *
     * @param pAnchor the anchor node
     * @param pX      the relative X position
     * @param pY      the relative Y position
     */
    public void showMenuAtPosition(final Node pAnchor,
                                   final double pX,
                                   final double pY) {
        /* Ensure the stage */
        ensureStage();

        /* determine the size of the menu */
        determineSize();

        /* If we have elements */
        if (theMenuSize != null) {
            /* determine location to display */
            final Point2D myRequest = new Point2D(pX, pY);
            final Point2D myLocation = TethysUIFXUtils.obtainDisplayPoint(pAnchor, myRequest, theMenuSize);

            /* Show menu */
            showMenuAtLocation(myLocation);
        }
    }

    /**
     * Show the menu at location.
     *
     * @param pLocation the location
     */
    private void showMenuAtLocation(final Point2D pLocation) {
        /* Record position */
        theStage.setX(pLocation.getX());
        theStage.setY(pLocation.getY());

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
        theStage.show();
    }

    /**
     * Close non-Modal.
     */
    void closeMenu() {
        /* Close any children */
        closeChildren();

        /* Close the menu */
        theStage.close();
    }

    /**
     * Clear active Item.
     */
    void clearActiveItem() {
        theActiveItem = null;
    }

    /**
     * Set the selected item.
     *
     * @param pItem the selected item
     */
    void setSelectedItem(final TethysUIFXScrollItem<T> pItem) {
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
            if (theSelectedItem instanceof TethysUIFXScrollToggle) {
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
    private void handleEscapeKey() {
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
    private void handleEnterKey() {
        /* If we are a child menu */
        if (theActiveItem != null) {
            /* assume item is selected */
            setSelectedItem(theActiveItem);
        }
    }

    /**
     * Handle focusChange.
     *
     * @param pState the focus state
     */
    private void handleFocusChange(final Boolean pState) {
        /* If we've lost focus to other than the active subMenu */
        if (!pState
                && theActiveMenu == null) {
            /* fire cancellation event */
            if (theParentMenu == null) {
                theEventManager.fireEvent(TethysUIEvent.WINDOWCLOSED);
            }

            /* Close the menu hierarchy if we are currently showing */
            if (isShowing()) {
                closeOnFocusLoss();
            }
        }
    }

    /**
     * Handle keyPress.
     *
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
     *
     * @param pEvent the event
     */
    private void handleScroll(final ScrollEvent pEvent) {
        /* request the scroll */
        final double myDelta = pEvent.getDeltaY() / pEvent.getMultiplierY();
        requestScroll((int) -myDelta);

        /* Consume the event */
        pEvent.consume();
    }

    /**
     * Handle activeItem.
     *
     * @param pItem the item
     */
    void handleActiveItem(final TethysUIFXScrollItem<T> pItem) {
        /* Close any children */
        closeChildren();

        /* Record that we are the active item */
        theActiveItem = pItem;
    }

    /**
     * Handle activeMenu.
     *
     * @param pMenu the menu
     */
    void handleActiveMenu(final TethysUIFXScrollSubMenu<T> pMenu) {
        /* Hide any existing menu that is not us */
        if (theActiveMenu != null
                && theActiveMenu.getIndex() != pMenu.getIndex()) {
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
            closeMenu();
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
     *
     * @return the count
     */
    protected int getItemCount() {
        /* Obtain count */
        return theMenuItems.size();
    }

    /**
     * Ensure item at index will be visible when displayed.
     *
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
     *
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
        if (isShowing()) {
            throw new IllegalStateException();
        }

        /* Create element */
        final TethysUIFXScrollItem<T> myItem = new TethysUIFXScrollItem<>(this, pValue, pName, pGraphic);

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
        if (isShowing()) {
            throw new IllegalStateException();
        }

        /* Create menu */
        final TethysUIFXScrollSubMenu<T> myMenu = new TethysUIFXScrollSubMenu<>(this, pName, pGraphic);

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
        if (isShowing()) {
            throw new IllegalStateException();
        }

        /* Create element */
        final TethysUIFXScrollToggle<T> myItem = new TethysUIFXScrollToggle<>(this, pValue, pName);

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
            theActiveMenu.hide();
        }
        theActiveMenu = null;
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
            theActiveItems.clear();

            /* If we do not need to scroll */
            if (myScroll == myCount) {
                /* Ensure we have no arrows */
                theContainer.setTop(null);
                theContainer.setBottom(null);

                /* Loop through the items to add */
                for (int i = 0; i < myCount; i++) {
                    /* Add the items */
                    theActiveItems.add(theMenuItems.get(i).getBorderPane());
                }

                /* Calculate size of menu */
                theStage.show();
                theStage.close();

                /* Determine the size */
                theMenuSize = new Dimension2D(theStage.getWidth(), theStage.getHeight());

                /* else need to set up scroll */
            } else {
                /* Add the scrolling items */
                theContainer.setTop(theUpItem.getLabel());
                theContainer.setBottom(theDownItem.getLabel());
                theUpItem.setVisible(true);
                theDownItem.setVisible(true);

                /* Add ALL items */
                for (final TethysUIFXScrollElement myItem : theMenuItems) {
                    /* Add the items */
                    theActiveItems.add(myItem.getBorderPane());
                }

                /* Calculate size of menu */
                theStage.show();
                theStage.close();
                final double myWidth = theStage.getWidth();

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
                    theActiveItems.add(theMenuItems.get(i).getBorderPane());
                }

                /* Calculate size of menu */
                theStage.show();
                theStage.close();
                final double myHeight = theStage.getHeight();

                /* Set visibility of scroll items */
                theUpItem.setVisible(theFirstIndex > 0);
                theDownItem.setVisible(myMaxIndex < myCount);

                /* Determine the size */
                theMenuSize = new Dimension2D(myWidth, myHeight);

                /* Fix the width */
                theStage.setMinWidth(myWidth);
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
            theUpItem.setVisible(true);

            /* Remove the first item */
            theActiveItems.remove(0);

            /* Add the final item */
            final int myLast = theFirstIndex + theMaxDisplayItems;
            final TethysUIFXScrollElement myItem = theMenuItems.get(myLast);
            theActiveItems.add(myItem.getBorderPane());

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
            final TethysUIFXScrollElement myItem = theMenuItems.get(theFirstIndex - 1);
            theActiveItems.add(0, myItem.getBorderPane());

            /* Adjust up item */
            theUpItem.setVisible(theFirstIndex > 1);
        }

        /* Adjust first index */
        theFirstIndex--;
    }

    /**
     * request scroll.
     *
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
     * Scroll element.
     */
    public abstract static class TethysUIFXScrollElement {
        /**
         * BorderPane.
         */
        private final BorderPane theBorderPane;

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
         *
         * @param pName    the display name
         * @param pGraphic the icon for the item
         */
        private TethysUIFXScrollElement(final String pName,
                                        final TethysUIIcon pGraphic) {
            /* Create the borderPane */
            theBorderPane = new BorderPane();

            /* Create a Label for the name */
            theLabel = new Label();
            theLabel.setText(pName);
            theLabel.setMaxWidth(Double.MAX_VALUE);

            /* Create a Label for the graphic */
            theIcon = new Label();
            theIcon.setGraphic(TethysUIFXIcon.getIcon(pGraphic));
            theIcon.setMinWidth(TethysUICoreComponent.DEFAULT_ICONWIDTH);

            /* Add the children */
            theBorderPane.setLeft(theIcon);
            theBorderPane.setCenter(theLabel);

            /* Set style of item */
            theBorderPane.getStyleClass().add(STYLE_ITEM);
        }

        /**
         * Obtain the label.
         *
         * @return the label
         */
        BorderPane getBorderPane() {
            return theBorderPane;
        }

        /**
         * Add event filter.
         *
         * @param <T>     the event type
         * @param pEvent  the event
         * @param pFilter the filter
         */
        <T extends Event> void addEventFilter(final EventType<T> pEvent,
                                              final EventHandler<? super T> pFilter) {
            theBorderPane.addEventFilter(pEvent, pFilter);
        }

        /**
         * Obtain the text.
         *
         * @return the text
         */
        public String getText() {
            return theLabel.getText();
        }

        /**
         * Set the graphic.
         *
         * @param pGraphic the graphic
         */
        protected void setIcon(final TethysUIFXIcon pGraphic) {
            theIcon.setGraphic(TethysUIFXIcon.getIcon(pGraphic));
        }

        /**
         * Add Menu icon.
         */
        protected void addMenuIcon() {
            final Label myLabel = new Label();
            myLabel.setGraphic(TethysUIFXArrowIcon.RIGHT.getArrow());
            theBorderPane.setRight(myLabel);
        }
    }

    /**
     * Scroll item.
     *
     * @param <T> the value type
     */
    protected static class TethysUIFXScrollItem<T>
            extends TethysUIFXScrollElement
            implements TethysUIScrollItem<T> {
        /**
         * Parent context menu.
         */
        private final TethysUIFXScrollMenu<T> theContext;

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
         *
         * @param pContext the parent context menu
         * @param pValue   the value
         * @param pName    the display name
         * @param pGraphic the icon for the item
         */
        protected TethysUIFXScrollItem(final TethysUIFXScrollMenu<T> pContext,
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
            addEventFilter(MouseEvent.MOUSE_ENTERED, e -> theContext.handleActiveItem(this));

            /* Handle selection */
            addEventFilter(MouseEvent.MOUSE_CLICKED, e -> theContext.setSelectedItem(this));
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
     *
     * @param <T> the value type
     */
    private static final class TethysUIFXScrollToggle<T>
            extends TethysUIFXScrollItem<T>
            implements TethysUIScrollToggle<T> {
        /**
         * Selected state.
         */
        private boolean isSelected;

        /**
         * Constructor.
         *
         * @param pContext the parent context menu
         * @param pValue   the value
         * @param pName    the display name
         */
        TethysUIFXScrollToggle(final TethysUIFXScrollMenu<T> pContext,
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
                    ? TethysUIFXUtils.getIconAtSize(TethysUIScrollIcon.CHECKMARK, TethysUICoreComponent.DEFAULT_ICONWIDTH)
                    : null);
        }

        @Override
        public void toggleSelected() {
            setSelected(!isSelected);
        }
    }

    /**
     * Scroll menu.
     *
     * @param <T> the value type
     */
    public static final class TethysUIFXScrollSubMenu<T>
            extends TethysUIFXScrollElement
            implements TethysUIScrollSubMenu<T> {
        /**
         * Parent contextMenu.
         */
        private final TethysUIFXScrollMenu<T> theContext;

        /**
         * The index.
         */
        private final int theIndex;

        /**
         * Associated value.
         */
        private final TethysUIFXScrollMenu<T> theSubMenu;

        /**
         * Constructor.
         *
         * @param pContext the parent context menu
         * @param pName    the name
         * @param pGraphic the icon for the menu
         */
        TethysUIFXScrollSubMenu(final TethysUIFXScrollMenu<T> pContext,
                                final String pName,
                                final TethysUIIcon pGraphic) {
            /* Call super-constructor */
            super(pName, pGraphic);

            /* Record parameters */
            theContext = pContext;

            /* Create the subMenu */
            theSubMenu = new TethysUIFXScrollMenu<>(this);

            /* Determine the index */
            theIndex = theContext.getItemCount();

            /* Set menu icon */
            addMenuIcon();

            /* Handle show menu */
            addEventFilter(MouseEvent.MOUSE_ENTERED, e -> {
                theContext.handleActiveMenu(this);
                theSubMenu.showMenuAtPosition(getBorderPane(), Side.RIGHT);
            });
        }

        /**
         * Obtain the parent.
         *
         * @return the parent
         */
        TethysUIFXScrollMenu<T> getContext() {
            return theContext;
        }

        @Override
        public TethysUIFXScrollMenu<T> getSubMenu() {
            return theSubMenu;
        }

        /**
         * Obtain the index.
         *
         * @return the index
         */
        int getIndex() {
            return theIndex;
        }

        /**
         * Hide the subMenu.
         */
        void hide() {
            theSubMenu.closeMenu();
        }

        /**
         * Set the number of items in the scrolling portion of the menu.
         *
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
    private final class ScrollControl {
        /**
         * Label.
         */
        private final Label theLabel;

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
         *
         * @param pIcon      the icon
         * @param pIncrement the increment
         */
        ScrollControl(final Polygon pIcon,
                      final int pIncrement) {
            /* Create the label */
            theLabel = new Label();

            /* Set the icon for the item */
            theLabel.setGraphic(pIcon);
            theLabel.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            theLabel.setAlignment(Pos.CENTER);
            theLabel.setMaxWidth(Double.MAX_VALUE);
            theLabel.getStyleClass().add(STYLE_ITEM);

            /* Store parameters */
            theIncrement = pIncrement;

            /* Handle selection */
            theLabel.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> processScroll());

            /* Handle mouse enters */
            theLabel.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> processMouseEnter());

            /* Handle mouse exits */
            theLabel.addEventHandler(MouseEvent.MOUSE_EXITED, e -> processMouseExit());

            /* Create the timers */
            theKickStartTimer = new Timeline(new KeyFrame(Duration.millis(TethysUICoreScrollMenu.INITIAL_SCROLLDELAY),
                    e -> processScroll()));
            theRepeatTimer = new Timeline(new KeyFrame(Duration.millis(TethysUICoreScrollMenu.REPEAT_SCROLLDELAY),
                    e -> processScroll()));
        }

        /**
         * Obtain the label.
         *
         * @return the label
         */
        Label getLabel() {
            return theLabel;
        }

        /**
         * Set visibility.
         *
         * @param pVisible true/false
         */
        void setVisible(final boolean pVisible) {
            theLabel.setVisible(pVisible);
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
            clearActiveItem();

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
}
