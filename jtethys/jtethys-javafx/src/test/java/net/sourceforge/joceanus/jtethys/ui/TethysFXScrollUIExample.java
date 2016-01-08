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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import net.sourceforge.jdatebutton.javafx.JDateButton;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.javafx.TethysFXGuiUtils;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuToggleItem;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollUITestHelper.IconState;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDateButtonManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXIconButton;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXIconButton.TethysFXSimpleIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXIconButton.TethysFXStateIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXListButton;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXListButton.TethysFXListButtonManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXScrollButton;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXScrollButton.TethysFXScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXScrollContextMenu;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXScrollContextMenu.TethysFXContextEvent;

/**
 * Scroll utilities examples.
 */
public class TethysFXScrollUIExample
        extends Application {
    /**
     * Open True icon.
     */
    private static final Image OPEN_TRUE_ICON = new Image(TethysScrollUITestHelper.class.getResourceAsStream("GreenJellyOpenTrue.png"));

    /**
     * Open False icon.
     */
    private static final Image OPEN_FALSE_ICON = new Image(TethysScrollUITestHelper.class.getResourceAsStream("GreenJellyOpenFalse.png"));

    /**
     * Closed True icon.
     */
    private static final Image CLOSED_TRUE_ICON = new Image(TethysScrollUITestHelper.class.getResourceAsStream("BlueJellyClosedTrue.png"));

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
    private final TethysScrollUITestHelper<Node> theHelper;

    /**
     * The context menu.
     */
    private final TethysFXScrollContextMenu<String> theContextMenu;

    /**
     * The scroll button manager.
     */
    private final TethysFXScrollButtonManager<String> theScrollButtonMgr;

    /**
     * The simple icon button manager.
     */
    private final TethysFXSimpleIconButtonManager<Boolean> theSimpleIconButtonMgr;

    /**
     * The state icon button manager.
     */
    private final TethysFXStateIconButtonManager<Boolean, IconState> theStateIconButtonMgr;

    /**
     * The state scroll manager.
     */
    private final TethysFXScrollButtonManager<IconState> theStateButtonMgr;

    /**
     * The list button manager.
     */
    private final TethysFXListButtonManager<String> theListButtonMgr;

    /**
     * The date button manager.
     */
    private final TethysFXDateButtonManager theDateButtonMgr;

    /**
     * The selected context value.
     */
    private final Label theContextValue;

    /**
     * The selected scroll value.
     */
    private final Label theScrollValue;

    /**
     * The selected date value.
     */
    private final Label theDateValue;

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
    public TethysFXScrollUIExample() {
        /* Create helper */
        theHelper = new TethysScrollUITestHelper<Node>();

        /* Create resources */
        theContextMenu = new TethysFXScrollContextMenu<String>();
        theScrollButtonMgr = new TethysFXScrollButtonManager<String>();
        theSimpleIconButtonMgr = new TethysFXSimpleIconButtonManager<Boolean>();
        theStateIconButtonMgr = new TethysFXStateIconButtonManager<Boolean, IconState>();
        theStateButtonMgr = new TethysFXScrollButtonManager<IconState>();
        theListButtonMgr = new TethysFXListButtonManager<String>();
        theDateButtonMgr = new TethysFXDateButtonManager();
        theContextValue = new Label();
        theScrollValue = new Label();
        theDateValue = new Label();
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
        StackPane myContext = TethysFXGuiUtils.getTitledPane("ContextArea", myContextArea);
        myContext.setAlignment(Pos.CENTER);
        myContext.setMaxWidth(Double.MAX_VALUE);
        StackPane myResult = TethysFXGuiUtils.getTitledPane("ContextValue", theContextValue);
        theContextValue.setAlignment(Pos.CENTER);
        myPane.addRow(myRowNo++, myContext, myResult);
        setContextValue(null);

        /* Build the context menu */
        theHelper.buildContextMenu(theContextMenu);

        /* Create the menu hook */
        myContext.setOnContextMenuRequested(e -> theContextMenu.showMenuAtPosition(myContext, e.getX(), e.getY()));

        /* Add listener */
        theContextMenu.addEventHandler(TethysFXContextEvent.MENU_SELECT, e -> {
            /* If we selected a value */
            TethysScrollMenuItem<String> mySelected = theContextMenu.getSelectedItem();
            if (mySelected != null) {
                setContextValue(mySelected.getValue());
                pStage.sizeToScene();
            }
        });

        /* Create scroll button line */
        TethysFXScrollButton myScrollButton = theScrollButtonMgr.getButton();
        StackPane myControl = TethysFXGuiUtils.getTitledPane("ScrollButton", myScrollButton);
        myControl.setAlignment(Pos.CENTER);
        myControl.setMaxWidth(Double.MAX_VALUE);
        myResult = TethysFXGuiUtils.getTitledPane("ScrollValue", theScrollValue);
        theScrollValue.setAlignment(Pos.CENTER);
        myPane.addRow(myRowNo++, myControl, myResult);
        setScrollValue(null);

        /* Add listener */
        theScrollButtonMgr.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> {
            setScrollValue(e.getDetails(String.class));
            pStage.sizeToScene();
        });
        theScrollButtonMgr.getEventRegistrar().addEventListener(TethysUIEvent.PREPAREDIALOG,
                e -> theHelper.buildContextMenu(theScrollButtonMgr.getMenu()));

        /* Create list button line */
        TethysFXListButton myListButton = theListButtonMgr.getButton();
        myControl = TethysFXGuiUtils.getTitledPane("ListButton", myListButton);
        myControl.setAlignment(Pos.CENTER);
        myControl.setMaxWidth(Double.MAX_VALUE);
        myResult = TethysFXGuiUtils.getTitledPane("ListValues", theListValues);
        theListValues.setAlignment(Pos.CENTER);
        myPane.addRow(myRowNo++, myControl, myResult);

        setListValue(null);
        theListButtonMgr.getButton().setButtonText("Tag");
        theListButtonMgr.getMenu().setCloseOnToggle(false);

        /* Add listener */
        theListButtonMgr.getEventRegistrar().addEventListener(TethysUIEvent.TOGGLEITEM, e -> {
            setListValue(e.getDetails(TethysScrollMenuToggleItem.class));
            pStage.sizeToScene();
        });
        theListButtonMgr.getEventRegistrar().addEventListener(TethysUIEvent.PREPAREDIALOG,
                e -> theHelper.buildAvailableItems(theListButtonMgr, theSelectedValues));

        /* Create date button line */
        JDateButton myDateButton = theDateButtonMgr.getButton();
        myControl = TethysFXGuiUtils.getTitledPane("DateButton", myDateButton);
        myControl.setAlignment(Pos.CENTER);
        myControl.setMaxWidth(Double.MAX_VALUE);
        myResult = TethysFXGuiUtils.getTitledPane("DateValue", theDateValue);
        theDateValue.setAlignment(Pos.CENTER);
        myPane.addRow(myRowNo++, myControl, myResult);

        /* Add listener */
        theDateButtonMgr.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> {
            setDateValue(e.getDetails(TethysDate.class));
            pStage.sizeToScene();
        });

        /* Create simple icon button line */
        TethysFXIconButton myIconButton = theSimpleIconButtonMgr.getButton();
        myControl = TethysFXGuiUtils.getTitledPane("SimpleIconButton", myIconButton);
        myControl.setAlignment(Pos.CENTER);
        myControl.setMaxWidth(Double.MAX_VALUE);
        myResult = TethysFXGuiUtils.getTitledPane("IconValue", theSimpleIconValue);
        theSimpleIconValue.setAlignment(Pos.CENTER);
        myPane.addRow(myRowNo++, myControl, myResult);
        theHelper.buildSimpleIconState(theSimpleIconButtonMgr,
                TethysFXGuiUtils.resizeImage(OPEN_FALSE_ICON, DEFAULT_ICONWIDTH),
                TethysFXGuiUtils.resizeImage(OPEN_TRUE_ICON, DEFAULT_ICONWIDTH));

        /* Add listener */
        theSimpleIconButtonMgr.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> {
            setSimpleIconValue(e.getDetails(Boolean.class));
            pStage.sizeToScene();
        });

        /* Create state icon button line */
        myIconButton = theStateIconButtonMgr.getButton();
        HBox myBox = new HBox();
        myBox.getChildren().addAll(theStateButtonMgr.getButton(), myIconButton);
        myControl = TethysFXGuiUtils.getTitledPane("StateIconButton", myBox);
        myControl.setAlignment(Pos.CENTER);
        myControl.setMaxWidth(Double.MAX_VALUE);
        myResult = TethysFXGuiUtils.getTitledPane("StateIconValue", theStateIconValue);
        theStateIconValue.setAlignment(Pos.CENTER);
        myPane.addRow(myRowNo++, myControl, myResult);
        theHelper.buildStateButton(theStateButtonMgr);
        theHelper.buildStateIconState(theStateIconButtonMgr,
                TethysFXGuiUtils.resizeImage(OPEN_FALSE_ICON, DEFAULT_ICONWIDTH),
                TethysFXGuiUtils.resizeImage(OPEN_TRUE_ICON, DEFAULT_ICONWIDTH),
                TethysFXGuiUtils.resizeImage(CLOSED_TRUE_ICON, DEFAULT_ICONWIDTH));

        /* Add listener */
        theStateIconButtonMgr.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> {
            setStateIconValue(e.getDetails(Boolean.class));
            pStage.sizeToScene();
        });

        /* Add listener */
        theStateButtonMgr.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> {
            theStateIconButtonMgr.setMachineState(e.getDetails(IconState.class));
            setStateIconValue(theStateIconButtonMgr.getValue());
            pStage.sizeToScene();
        });

        /* Create scene */
        Scene myScene = new Scene(new Group());
        ((Group) myScene.getRoot()).getChildren().addAll(myPane);
        pStage.setTitle("JavaFXScroll Demo");
        TethysFXGuiUtils.addStyleSheet(myScene);
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
     * Set the date value.
     * @param pValue the value to set
     */
    private void setDateValue(final TethysDate pValue) {
        /* Record the value */
        theDateValue.setText(pValue == null
                                            ? null
                                            : pValue.toString());
    }

    /**
     * Set the list value.
     * @param pValue the value to set
     */
    private void setListValue(final TethysScrollMenuToggleItem<?> pValue) {
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
