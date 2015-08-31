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
package net.sourceforge.joceanus.jtethys.ui.javafx;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Polygon;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.sourceforge.joceanus.jtethys.ui.ScrollMenuContent;
import net.sourceforge.joceanus.jtethys.ui.ScrollMenuContent.ScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.ScrollMenuContent.ScrollMenuItem;
import net.sourceforge.joceanus.jtethys.ui.ScrollMenuContent.ScrollMenuToggleItem;
import net.sourceforge.joceanus.jtethys.ui.ScrollMenuContent.ScrollSubMenu;

/**
 * Scroll-able version of ContextMenu.
 * <p>
 * Implemented as Stage since ContextMenu does not allow control of individual elements.
 * @param <T> the value type
 */
public class ScrollFXContextMenu<T>
        extends Stage
        implements ScrollMenu<T, Node> {
    /**
     * StyleSheet.
     */
    private static final String CSS_STYLE = ScrollFXContextMenu.class.getResource("jtethys-javafx-contextmenu.css").toExternalForm();

    /**
     * CheckMark icon.
     */
    private static final Image CHECK_ICON = new Image(ScrollMenuContent.class.getResourceAsStream("BlueJellyCheckMark.png"));

    /**
     * The menu style.
     */
    private static final String STYLE_MENU = "-jtethys-context";

    /**
     * The item style.
     */
    private static final String STYLE_ITEM = "-jtethys-context-item";

    /**
     * Timer.
     */
    private Timer theTimer;

    /**
     * List of menu items.
     */
    private final List<ScrollElement<T>> theMenuItems;

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
    private final ScrollFXSubMenu<T> theParentMenu;

    /**
     * The Parent contextMenu.
     */
    private final ScrollFXContextMenu<T> theParentContext;

    /**
     * The Active subMenu.
     */
    private ScrollFXSubMenu<T> theActiveMenu;

    /**
     * The Active item.
     */
    private ScrollFXMenuItem<T> theActiveItem;

    /**
     * The selected value.
     */
    private ScrollFXMenuItem<T> theSelectedItem;

    /**
     * Do we need to close menu on toggle?
     */
    private boolean closeOnToggle;

    /**
     * Do we need to reBuild the menu?
     */
    private boolean needReBuild;

    /**
     * Constructor.
     */
    public ScrollFXContextMenu() {
        this(ScrollMenuContent.DEFAULT_ITEMCOUNT);
    }

    /**
     * Constructor.
     * @param pMaxDisplayItems the maximum number of items to display
     */
    public ScrollFXContextMenu(final int pMaxDisplayItems) {
        this(null, pMaxDisplayItems);
    }

    /**
     * Constructor.
     * @param pParent the parent scroll menu
     */
    private ScrollFXContextMenu(final ScrollFXSubMenu<T> pParent) {
        this(pParent, pParent.getContext().getMaxDisplayItems());
    }

    /**
     * Constructor.
     * @param pParent the parent scroll menu
     * @param pMaxDisplayItems the maximum number of items to display
     */
    private ScrollFXContextMenu(final ScrollFXSubMenu<T> pParent,
                                final int pMaxDisplayItems) {
        /* Non-Modal and undecorated */
        super(StageStyle.UNDECORATED);
        initModality(Modality.NONE);

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

        /* Create the scroll items */
        theUpItem = new ScrollControl(ArrowIcon.UP.getArrow(), -1);
        theDownItem = new ScrollControl(ArrowIcon.DOWN.getArrow(), 1);

        /* Allocate the list */
        theMenuItems = new ArrayList<ScrollElement<T>>();
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
        focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(final ObservableValue<? extends Boolean> observable,
                                final Boolean oldValue,
                                final Boolean newValue) {
                /* If we've lost focus to other than the active subMenu */
                if ((!newValue)
                    && ((theActiveMenu == null)
                        || theActiveMenu.isFocused())) {
                    /* fire cancellation event */
                    if (theParentMenu == null) {
                        fireEvent(new ContextEvent<T>());
                    }

                    /* Close the menu */
                    closeMenu();
                }
            }
        });

        /* ensure that escape closes menu */
        myScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(final KeyEvent t) {
                switch (t.getCode()) {
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
        });

        /* Handle scroll events */
        addEventFilter(ScrollEvent.SCROLL, new EventHandler<ScrollEvent>() {
            @Override
            public void handle(final ScrollEvent e) {
                /* Adjust index and refresh the menu */
                theFirstIndex -= e.getDeltaY() / e.getMultiplierY();
                needReBuild = true;
                refreshMenu();

                /* Consume the event */
                e.consume();
            }
        });
    }

    /**
     * Resize an icon to the width.
     * @param pSource the source icon
     * @param pWidth the width
     * @return the resized icon
     */
    private static ImageView resizeImage(final Image pSource,
                                         final int pWidth) {
        ImageView myNewImage = new ImageView();
        myNewImage.setImage(pSource);
        myNewImage.setFitWidth(pWidth);
        myNewImage.setPreserveRatio(true);
        myNewImage.setSmooth(true);
        myNewImage.setCache(true);
        return myNewImage;
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
        if (isShowing()) {
            throw new IllegalStateException();
        }

        /* Store parameters */
        theMaxDisplayItems = pMaxDisplayItems;

        /* Loop through the children */
        Iterator<ScrollElement<T>> myIterator = theMenuItems.iterator();
        while (myIterator.hasNext()) {
            ScrollElement<T> myChild = myIterator.next();

            /* If this is a subMenu */
            if (myChild instanceof ScrollFXSubMenu) {
                /* Pass call on */
                ScrollFXSubMenu<?> mySubMenu = (ScrollFXSubMenu<?>) myChild;
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
        /* Obtain location of anchor node */
        Bounds myLocal = pAnchor.getLayoutBounds();
        Bounds myBounds = pAnchor.localToScreen(myLocal);

        /* Obtain default location */
        double myX = myBounds.getMinX();
        double myY = myBounds.getMinY();

        switch (pSide) {
            case RIGHT:
                myX = myBounds.getMaxX();
                break;
            case BOTTOM:
                myY = myBounds.getMaxY();
                break;
            case TOP:
                refreshMenu();
                myY -= getHeight();
                break;
            case LEFT:
            default:
                refreshMenu();
                myX -= getWidth();
                break;
        }

        /* Show menu */
        showMenuAtPosition(myX, myY);
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
        /* Obtain location of anchor node */
        Bounds myLocal = pAnchor.getLayoutBounds();
        Bounds myBounds = pAnchor.localToScreen(myLocal);

        /* Obtain default location */
        double myX = myBounds.getMinX() + pX;
        double myY = myBounds.getMinY() + pY;

        /* Show menu */
        showMenuAtPosition(myX, myY);
    }

    /**
     * Show the menu at position.
     * @param pX the X position
     * @param pY the Y position
     */
    private void showMenuAtPosition(final double pX,
                                    final double pY) {
        /* Record position */
        setX(pX);
        setY(pY);

        /* Show menu */
        showMenu();
    }

    /**
     * Show the menu.
     */
    private void showMenu() {
        /* Ensure that the menu is built */
        refreshMenu();

        /* Initialise the values */
        theTimer = theParentMenu != null
                                         ? theParentContext.getTimer()
                                         : new Timer();
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

        /* Clear any timer */
        if (theParentMenu == null
            && theTimer != null) {
            theTimer.cancel();
        }
        theTimer = null;
    }

    /**
     * Set the selected item.
     * @param pItem the selected item
     */
    private void setSelectedItem(final ScrollFXMenuItem<T> pItem) {
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
            if (theSelectedItem instanceof ScrollMenuToggleItem) {
                ScrollMenuToggleItem<?> myItem = (ScrollMenuToggleItem<?>) theSelectedItem;
                myItem.toggleSelected();
                doCloseMenu = closeOnToggle;
            }

            /* fire selection event */
            fireEvent(new ContextEvent<T>(theSelectedItem));

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
            fireEvent(new ContextEvent<T>());

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
    private void handleActiveItem(final ScrollFXMenuItem<T> pItem) {
        /* Close any children */
        closeChildren();

        /* Record that we are the active item */
        theActiveItem = pItem;
    }

    /**
     * Handle activeMenu.
     * @param pMenu the menu
     */
    private void handleActiveMenu(final ScrollFXSubMenu<T> pMenu) {
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

        /* Request reBuild */
        needReBuild = true;
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
                theContainer.setTop(theUpItem);
                theUpItem.setVisible(theFirstIndex > 0);

                /* Loop through the items to add */
                for (int i = theFirstIndex; i < myMaxIndex; i++) {
                    /* Add the items */
                    theActiveItems.add(theMenuItems.get(i));
                }

                /* Add the down item */
                theContainer.setBottom(theDownItem);
                theDownItem.setVisible(myMaxIndex < myCount);
            }
        }

        /* Make sure that the menu is sized correctly */
        sizeToScene();
        needReBuild = false;
    }

    @Override
    public ScrollMenuItem<T> addItem(final T pValue) {
        /* Use standard name */
        return addItem(pValue, pValue.toString(), null);
    }

    @Override
    public ScrollMenuItem<T> addItem(final T pValue,
                                     final Node pGraphic) {
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
                                         final Node pGraphic) {
        /* Use given name */
        return addItem(null, pName, pGraphic);
    }

    @Override
    public ScrollMenuItem<T> addItem(final T pValue,
                                     final String pName,
                                     final Node pGraphic) {
        /* Create element */
        ScrollFXMenuItem<T> myItem = new ScrollFXMenuItem<T>(this, pValue, pName, pGraphic);

        /* Add to the list of menuItems */
        theMenuItems.add(myItem);
        needReBuild = true;
        return myItem;
    }

    @Override
    public ScrollSubMenu<T, Node> addSubMenu(final String pName) {
        /* Use given name */
        return addSubMenu(pName, null);
    }

    @Override
    public ScrollSubMenu<T, Node> addSubMenu(final String pName,
                                             final Node pGraphic) {
        /* Create menu */
        ScrollFXSubMenu<T> myMenu = new ScrollFXSubMenu<T>(this, pName, pGraphic);

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
        /* Create element */
        ScrollFXToggleItem<T> myItem = new ScrollFXToggleItem<T>(this, pValue, pName);

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
     * Scroll item.
     * @param <T> the value type
     */
    public abstract static class ScrollElement<T>
            extends HBox {
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
        private ScrollElement(final String pName,
                              final Node pGraphic) {
            /* Create a Label for the name */
            theLabel = new Label();
            theLabel.setText(pName);
            theLabel.setMaxWidth(Double.MAX_VALUE);

            /* Create a Label for the graphic */
            theIcon = new Label();
            theIcon.setGraphic(pGraphic);
            theIcon.setMinWidth(ScrollMenuContent.DEFAULT_ICONWIDTH);

            /* Add the children */
            getChildren().addAll(theIcon, theLabel);

            /* Set style of item */
            getStyleClass().add(STYLE_ITEM);
        }

        /**
         * Obtain the label.
         * @return the label
         */
        protected Label getLabel() {
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
        protected void setIcon(final Node pGraphic) {
            theIcon.setGraphic(pGraphic);
        }
    }

    /**
     * Scroll item.
     * @param <T> the value type
     */
    protected static class ScrollFXMenuItem<T>
            extends ScrollElement<T>
            implements ScrollMenuItem<T> {
        /**
         * Parent context menu.
         */
        private final ScrollFXContextMenu<T> theContext;

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
        protected ScrollFXMenuItem(final ScrollFXContextMenu<T> pContext,
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
            addEventFilter(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(final MouseEvent event) {
                    /* handle the active item */
                    theContext.handleActiveItem(ScrollFXMenuItem.this);
                }
            });

            /* Handle selection */
            addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(final MouseEvent event) {
                    theContext.setSelectedItem(ScrollFXMenuItem.this);
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
    private static final class ScrollFXToggleItem<T>
            extends ScrollFXMenuItem<T>
            implements ScrollMenuToggleItem<T> {
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
        private ScrollFXToggleItem(final ScrollFXContextMenu<T> pContext,
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
                               ? resizeImage(CHECK_ICON, ScrollMenuContent.DEFAULT_ICONWIDTH)
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
    public static final class ScrollFXSubMenu<T>
            extends ScrollElement<T>
            implements ScrollSubMenu<T, Node> {
        /**
         * Parent contextMenu.
         */
        private final ScrollFXContextMenu<T> theContext;

        /**
         * The index.
         */
        private final int theIndex;

        /**
         * Associated value.
         */
        private final ScrollFXContextMenu<T> theSubMenu;

        /**
         * Constructor.
         * @param pContext the parent context menu
         * @param pName the name
         * @param pGraphic the icon for the menu
         */
        private ScrollFXSubMenu(final ScrollFXContextMenu<T> pContext,
                                final String pName,
                                final Node pGraphic) {
            /* Call super-constructor */
            super(pName, pGraphic);

            /* Record parameters */
            theContext = pContext;

            /* Create the subMenu */
            theSubMenu = new ScrollFXContextMenu<T>(this);

            /* Determine the index */
            theIndex = theContext.getItemCount();

            /* Set text */
            Label myLabel = getLabel();
            myLabel.setGraphic(ArrowIcon.RIGHT.getArrow());
            myLabel.setContentDisplay(ContentDisplay.RIGHT);

            /* Handle show menu */
            addEventFilter(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(final MouseEvent event) {
                    /* handle the active menu */
                    theContext.handleActiveMenu(ScrollFXSubMenu.this);

                    /* Show the menu */
                    theSubMenu.showMenuAtPosition(ScrollFXSubMenu.this, Side.RIGHT);
                }
            });
        }

        /**
         * Obtain the parent.
         * @return the parent
         */
        private ScrollFXContextMenu<T> getContext() {
            return theContext;
        }

        @Override
        public ScrollFXContextMenu<T> getSubMenu() {
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
         * Timer.
         */
        private TimerTask theTimerTask;

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
            addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(final MouseEvent e) {
                    processScroll();
                }
            });

            /* Handle mouse enters */
            addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(final MouseEvent e) {
                    processMouseEnter();
                }
            });

            /* Handle mouse exits */
            addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(final MouseEvent e) {
                    processMouseExit();
                }
            });
        }

        /**
         * Process scroll event.
         */
        private void processScroll() {
            /* Adjust index and refresh menu */
            theFirstIndex += theIncrement;
            needReBuild = true;
            refreshMenu();
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

            /* Create new timer task */
            theTimerTask = new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(new Runnable() {
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
        }
    }

    /**
     * ContextEvent.
     * @param <T> the value type
     */
    public static final class ContextEvent<T>
            extends Event {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -2289427191547125044L;

        /**
         * An item has been selected.
         */
        public static final EventType<ContextEvent<?>> MENU_SELECT = new EventType<>(Event.ANY, "MENU_SELECT");

        /**
         * An item has been selected.
         */
        public static final EventType<ContextEvent<?>> MENU_TOGGLE = new EventType<>(Event.ANY, "MENU_TOGGLE");

        /**
         * The menu has been cancelled.
         */
        public static final EventType<ContextEvent<?>> MENU_CANCEL = new EventType<>(Event.ANY, "MENU_CANCEL");

        /**
         * The item.
         */
        private final ScrollFXMenuItem<T> theItem;

        /**
         * Constructor.
         * @param pItem the selected item
         */
        public ContextEvent(final ScrollFXMenuItem<T> pItem) {
            super(pItem instanceof ScrollFXToggleItem
                                                      ? MENU_TOGGLE
                                                      : MENU_SELECT);
            theItem = pItem;
        }

        /**
         * Constructor.
         * @param pItem the selected item
         */
        public ContextEvent() {
            super(MENU_CANCEL);
            theItem = null;
        }

        /**
         * Obtain the toggled item.
         * @return the item
         */
        public ScrollFXToggleItem<T> getToggledItem() {
            return theItem instanceof ScrollFXToggleItem
                                                         ? (ScrollFXToggleItem<T>) theItem
                                                         : null;
        }

        /**
         * Obtain the selected item.
         * @return the item
         */
        public ScrollFXMenuItem<T> getSelectedItem() {
            return theItem instanceof ScrollFXToggleItem
                                                         ? null
                                                         : theItem;
        }
    }
}
