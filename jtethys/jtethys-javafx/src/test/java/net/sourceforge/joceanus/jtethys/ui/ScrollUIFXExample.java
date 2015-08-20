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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jtethys/jtethys-swing/src/test/java/net/sourceforge/joceanus/jtethys/dateday/JDateDayExample.java $
 * $Revision: 580 $
 * $Author: Tony $
 * $Date: 2015-03-25 14:52:24 +0000 (Wed, 25 Mar 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusActionEvent;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusActionEventListener;
import net.sourceforge.joceanus.jtethys.ui.ScrollMenuContent.ScrollMenuItem;
import net.sourceforge.joceanus.jtethys.ui.ScrollMenuContent.ScrollMenuToggleItem;
import net.sourceforge.joceanus.jtethys.ui.ScrollUITestHelper.IconState;
import net.sourceforge.joceanus.jtethys.ui.javafx.IconFXButton;
import net.sourceforge.joceanus.jtethys.ui.javafx.IconFXButton.SimpleFXIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.IconFXButton.StateFXIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.JTitledPane;
import net.sourceforge.joceanus.jtethys.ui.javafx.ListFXButton;
import net.sourceforge.joceanus.jtethys.ui.javafx.ListFXButton.ListFXButtonManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.ScrollFXButton;
import net.sourceforge.joceanus.jtethys.ui.javafx.ScrollFXButton.ScrollFXButtonManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.ScrollFXContextMenu;
import net.sourceforge.joceanus.jtethys.ui.javafx.ScrollFXContextMenu.ContextEvent;

/**
 * Scroll utilities examples.
 */
public class ScrollUIFXExample
        extends Application {
    /**
     * Open True icon.
     */
    private static final Image OPEN_TRUE_ICON = new Image(ScrollUITestHelper.class.getResourceAsStream("GreenJellyOpenTrue.png"));

    /**
     * Open False icon.
     */
    private static final Image OPEN_FALSE_ICON = new Image(ScrollUITestHelper.class.getResourceAsStream("GreenJellyOpenFalse.png"));

    /**
     * Closed True icon.
     */
    private static final Image CLOSED_TRUE_ICON = new Image(ScrollUITestHelper.class.getResourceAsStream("BlueJellyClosedTrue.png"));

    /**
     * The padding.
     */
    private static final int PADDING = 5;

    /**
     * The value width.
     */
    private static final int VALUE_WIDTH = 200;

    /**
     * Default icon width.
     */
    private static final int DEFAULT_ICONWIDTH = 24;

    /**
     * The Test helper.
     */
    private final ScrollUITestHelper<Node> theHelper;

    /**
     * The context menu.
     */
    private final ScrollFXContextMenu<String> theContextMenu;

    /**
     * The scroll button manager.
     */
    private final ScrollFXButtonManager<String> theScrollButtonMgr;

    /**
     * The simple icon button manager.
     */
    private final SimpleFXIconButtonManager<Boolean> theSimpleIconButtonMgr;

    /**
     * The state icon button manager.
     */
    private final StateFXIconButtonManager<Boolean, IconState> theStateIconButtonMgr;

    /**
     * The state scroll manager.
     */
    private final ScrollFXButtonManager<IconState> theStateButtonMgr;

    /**
     * The list button manager.
     */
    private final ListFXButtonManager<String> theListButtonMgr;

    /**
     * The selected context value.
     */
    private final Label theContextValue;

    /**
     * The selected scroll value.
     */
    private final Label theScrollValue;

    /**
     * The selected simple icon value.
     */
    private final Label theSimpleIconValue;

    /**
     * The selected state icon values.
     */
    private final Label theStateIconValue;

    /**
     * The selected list values.
     */
    private final Label theListValues;

    /**
     * The selected list values.
     */
    private final List<String> theSelectedValues;

    /**
     * Constructor.
     */
    public ScrollUIFXExample() {
        /* Create helper */
        theHelper = new ScrollUITestHelper<Node>();

        /* Create resources */
        theContextMenu = new ScrollFXContextMenu<String>();
        theScrollButtonMgr = new ScrollFXButtonManager<String>();
        theSimpleIconButtonMgr = new SimpleFXIconButtonManager<Boolean>();
        theStateIconButtonMgr = new StateFXIconButtonManager<Boolean, IconState>();
        theStateButtonMgr = new ScrollFXButtonManager<IconState>();
        theListButtonMgr = new ListFXButtonManager<String>();
        theContextValue = new Label();
        theScrollValue = new Label();
        theSimpleIconValue = new Label();
        theStateIconValue = new Label();
        theListValues = new Label();
        theSelectedValues = new ArrayList<String>();
    }

    /**
     * Main entry point.
     * @param args the parameters
     */
    public static void main(final String[] args) {
        launch(args);
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
    public void start(final Stage pStage) {
        /* Create a GridPane */
        GridPane myPane = new GridPane();
        int myRowNo = 0;
        myPane.setHgap(PADDING);
        myPane.setVgap(PADDING << 1);
        myPane.setPadding(new Insets(PADDING, PADDING, PADDING, PADDING));
        myPane.getColumnConstraints().addAll(new ColumnConstraints(), new ColumnConstraints(VALUE_WIDTH));

        /* Create context menu line */
        Label myContextArea = new Label("Right-click for Menu");
        StackPane myControl = JTitledPane.getTitledPane("ContextArea", myContextArea);
        myControl.setAlignment(Pos.CENTER);
        myControl.setMaxWidth(Double.MAX_VALUE);
        StackPane myResult = JTitledPane.getTitledPane("ContextValue", theContextValue);
        theContextValue.setAlignment(Pos.CENTER);
        myPane.addRow(myRowNo++, myControl, myResult);
        setContextValue(null);

        /* Build the context menu */
        theHelper.buildContextMenu(theContextMenu);

        /* Create the menu hook */
        myControl.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            @Override
            public void handle(final ContextMenuEvent e) {
                theContextMenu.showMenuAtPosition(myPane, e.getSceneX(), e.getSceneY());
            }
        });

        /* Add listener */
        theContextMenu.addEventHandler(ContextEvent.MENU_SELECT, new EventHandler<ContextEvent<?>>() {
            @Override
            public void handle(final ContextEvent<?> e) {
                /* If we selected a value */
                ScrollMenuItem<String> mySelected = theContextMenu.getSelectedItem();
                if (mySelected != null) {
                    setContextValue(mySelected.getValue());
                    pStage.sizeToScene();
                }
            }
        });

        /* Create scroll button line */
        ScrollFXButton myScrollButton = theScrollButtonMgr.getButton();
        myControl = JTitledPane.getTitledPane("ScrollButton", myScrollButton);
        myControl.setAlignment(Pos.CENTER);
        myControl.setMaxWidth(Double.MAX_VALUE);
        myResult = JTitledPane.getTitledPane("ScrollValue", theScrollValue);
        theScrollValue.setAlignment(Pos.CENTER);
        myPane.addRow(myRowNo++, myControl, myResult);
        setScrollValue(null);

        /* Add listener */
        theScrollButtonMgr.getEventRegistrar().addActionListener(new JOceanusActionEventListener() {
            @Override
            public void processActionEvent(final JOceanusActionEvent pEvent) {
                switch (pEvent.getActionId()) {
                    case ScrollButtonManager.ACTION_NEW_VALUE:
                        setScrollValue(pEvent.getDetails(String.class));
                        pStage.sizeToScene();
                        break;
                    case ScrollButtonManager.ACTION_MENU_BUILD:
                        theHelper.buildContextMenu(theScrollButtonMgr.getMenu());
                        break;
                    case ScrollButtonManager.ACTION_MENU_CANCELLED:
                    default:
                        break;
                }
            }
        });

        /* Create list button line */
        ListFXButton myListButton = theListButtonMgr
                .getButton();
        myControl = JTitledPane.getTitledPane("ListButton", myListButton);
        myControl.setAlignment(Pos.CENTER);
        myControl.setMaxWidth(Double.MAX_VALUE);
        myResult = JTitledPane.getTitledPane("ListValues", theListValues);
        theListValues.setAlignment(Pos.CENTER);
        myPane.addRow(myRowNo++, myControl, myResult);
        setListValue(null);
        theListButtonMgr.getButton().setButtonText("Tag");
        theListButtonMgr.getMenu().setCloseOnToggle(false);

        /* Add listener */
        theListButtonMgr.getEventRegistrar().addActionListener(new JOceanusActionEventListener() {
            @Override
            public void processActionEvent(final JOceanusActionEvent pEvent) {
                switch (pEvent.getActionId()) {
                    case ListButtonManager.ACTION_TOGGLED:
                        setListValue(pEvent.getDetails(ScrollMenuToggleItem.class));
                        pStage.sizeToScene();
                        break;
                    case ScrollButtonManager.ACTION_MENU_BUILD:
                        theHelper.buildAvailableItems(theListButtonMgr.getMenu(), theSelectedValues);
                        break;
                    case ScrollButtonManager.ACTION_MENU_CANCELLED:
                    default:
                        break;
                }
            }
        });

        /* Create simple icon button line */
        IconFXButton myIconButton = theSimpleIconButtonMgr.getButton();
        myControl = JTitledPane.getTitledPane("SimpleIconButton", myIconButton);
        myControl.setAlignment(Pos.CENTER);
        myControl.setMaxWidth(Double.MAX_VALUE);
        myResult = JTitledPane.getTitledPane("IconValue", theSimpleIconValue);
        theSimpleIconValue.setAlignment(Pos.CENTER);
        myPane.addRow(myRowNo++, myControl, myResult);
        theHelper.buildSimpleIconState(theSimpleIconButtonMgr,
                resizeImage(OPEN_FALSE_ICON, DEFAULT_ICONWIDTH),
                resizeImage(OPEN_TRUE_ICON, DEFAULT_ICONWIDTH));

        /* Add listener */
        theSimpleIconButtonMgr.getEventRegistrar().addActionListener(new JOceanusActionEventListener() {
            @Override
            public void processActionEvent(final JOceanusActionEvent pEvent) {
                switch (pEvent.getActionId()) {
                    case IconButtonManager.ACTION_NEW_VALUE:
                        setSimpleIconValue(pEvent.getDetails(Boolean.class));
                        pStage.sizeToScene();
                        break;
                    default:
                        break;
                }
            }
        });

        /* Create state icon button line */
        myIconButton = theStateIconButtonMgr.getButton();
        HBox myBox = new HBox();
        myBox.getChildren().addAll(theStateButtonMgr.getButton(), myIconButton);
        myControl = JTitledPane.getTitledPane("StateIconButton", myBox);
        myControl.setAlignment(Pos.CENTER);
        myControl.setMaxWidth(Double.MAX_VALUE);
        myResult = JTitledPane.getTitledPane("StateIconValue", theStateIconValue);
        theStateIconValue.setAlignment(Pos.CENTER);
        myPane.addRow(myRowNo++, myControl, myResult);
        theHelper.buildStateButton(theStateButtonMgr);
        theHelper.buildStateIconState(theStateIconButtonMgr,
                resizeImage(OPEN_FALSE_ICON, DEFAULT_ICONWIDTH),
                resizeImage(OPEN_TRUE_ICON, DEFAULT_ICONWIDTH),
                resizeImage(CLOSED_TRUE_ICON, DEFAULT_ICONWIDTH));

        /* Add listener */
        theStateIconButtonMgr.getEventRegistrar().addActionListener(new JOceanusActionEventListener() {
            @Override
            public void processActionEvent(final JOceanusActionEvent pEvent) {
                switch (pEvent.getActionId()) {
                    case IconButtonManager.ACTION_NEW_VALUE:
                        setStateIconValue(pEvent.getDetails(Boolean.class));
                        pStage.sizeToScene();
                        break;
                    default:
                        break;
                }
            }
        });

        /* Add listener */
        theStateButtonMgr.getEventRegistrar().addActionListener(new JOceanusActionEventListener() {
            @Override
            public void processActionEvent(final JOceanusActionEvent pEvent) {
                switch (pEvent.getActionId()) {
                    case IconButtonManager.ACTION_NEW_VALUE:
                        theStateIconButtonMgr.setMachineState(pEvent.getDetails(IconState.class));
                        setStateIconValue(theStateIconButtonMgr.getValue());
                        pStage.sizeToScene();
                        break;
                    default:
                        break;
                }
            }
        });
        /* Create scene */
        Scene myScene = new Scene(new Group());
        ((Group) myScene.getRoot()).getChildren().addAll(myPane);
        pStage.setTitle("JavaFXScroll Demo");
        JTitledPane.addStyleSheet(myScene);
        pStage.setScene(myScene);
        pStage.show();
    }

    /**
     * Set the active value.
     * @param pValue the value to set
     */
    private void setContextValue(final String pValue) {
        /* Record the value */
        theContextValue.setText(pValue);
    }

    /**
     * Set the scroll value.
     * @param pValue the value to set
     */
    private void setScrollValue(final String pValue) {
        /* Record the value */
        theScrollValue.setText(pValue);
    }

    /**
     * Set the list value.
     * @param pValue the value to set
     */
    private void setListValue(final ScrollMenuToggleItem<?> pValue) {
        /* Record the value */
        if (pValue != null) {
            String myValue = (String) pValue.getValue();
            theHelper.adjustSelected(myValue, theSelectedValues);
        }
        theListValues.setText(theHelper.formatSelected(theSelectedValues));
    }

    /**
     * Set the simple icon value.
     * @param pValue the value to set
     */
    private void setSimpleIconValue(final Boolean pValue) {
        /* Record the value */
        theSimpleIconValue.setText(Boolean.toString(pValue));
    }

    /**
     * Set the state icon value.
     * @param pValue the value to set
     */
    private void setStateIconValue(final Boolean pValue) {
        /* Record the value */
        theStateIconValue.setText(theStateButtonMgr.getValue().toString() + ":" + Boolean.toString(pValue));
    }
}
