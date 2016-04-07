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
package net.sourceforge.joceanus.jtethys.ui.javafx;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.ui.TethysHelperIcon;
import net.sourceforge.joceanus.jtethys.ui.TethysLabel.TethysAlignment;
import net.sourceforge.joceanus.jtethys.ui.TethysListId;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollUITestHelper;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollUITestHelper.IconState;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXIconButtonManager.TethysFXSimpleIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXIconButtonManager.TethysFXStateIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXScrollContextMenu.TethysFXContextEvent;

/**
 * Scroll utilities examples.
 */
public class TethysFXScrollUIExample
        extends Application {
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
     * The GuiFactory.
     */
    private final TethysFXGuiFactory theGuiFactory;

    /**
     * The Test helper.
     */
    private final TethysScrollUITestHelper<Node, Node> theHelper;

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
    private final TethysFXListButtonManager<TethysListId> theListButtonMgr;

    /**
     * The date button manager.
     */
    private final TethysFXDateButtonManager theDateButtonMgr;

    /**
     * The selected context value.
     */
    private final TethysFXLabel theContextValue;

    /**
     * The selected scroll value.
     */
    private final TethysFXLabel theScrollValue;

    /**
     * The selected date value.
     */
    private final TethysFXLabel theDateValue;

    /**
     * The selected simple icon value.
     */
    private final TethysFXLabel theSimpleIconValue;

    /**
     * The selected state icon values.
     */
    private final TethysFXLabel theStateIconValue;

    /**
     * The selected list values.
     */
    private final TethysFXLabel theListValues;

    /**
     * Constructor.
     */
    public TethysFXScrollUIExample() {
        /* Create helper */
        theHelper = new TethysScrollUITestHelper<>();

        /* Create GUI Factory */
        theGuiFactory = new TethysFXGuiFactory();

        /* Create resources */
        theContextMenu = theGuiFactory.newContextMenu();
        theScrollButtonMgr = theGuiFactory.newScrollButton();
        theSimpleIconButtonMgr = theGuiFactory.newSimpleIconButton();
        theStateIconButtonMgr = theGuiFactory.newStateIconButton();
        theStateButtonMgr = theGuiFactory.newScrollButton();
        theListButtonMgr = theGuiFactory.newListButton();
        theDateButtonMgr = theGuiFactory.newDateButton();
        theContextValue = theGuiFactory.newLabel();
        theScrollValue = theGuiFactory.newLabel();
        theDateValue = theGuiFactory.newLabel();
        theSimpleIconValue = theGuiFactory.newLabel();
        theStateIconValue = theGuiFactory.newLabel();
        theListValues = theGuiFactory.newLabel();
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
        GridPane myGrid = new GridPane();
        int myRowNo = 0;
        myGrid.setHgap(PADDING);
        myGrid.setVgap(PADDING << 1);
        myGrid.setPadding(new Insets(PADDING, PADDING, PADDING, PADDING));
        myGrid.getColumnConstraints().addAll(new ColumnConstraints(), new ColumnConstraints(VALUE_WIDTH));

        /* Create context menu line */
        TethysFXLabel myContextArea = theGuiFactory.newLabel("Right-click for Menu");
        myContextArea.setBorderTitle("ContextArea");
        myContextArea.setAlignment(TethysAlignment.CENTRE);
        theContextValue.setBorderTitle("ContextValue");
        theContextValue.setAlignment(TethysAlignment.CENTRE);
        myGrid.addRow(myRowNo++, myContextArea.getNode(), theContextValue.getNode());
        setContextValue(null);

        /* Build the context menu */
        theHelper.buildContextMenu(theContextMenu);

        /* Create the menu hook */
        // myContext.setOnContextMenuRequested(e -> theContextMenu.showMenuAtPosition(myContext,
        // e.getX(), e.getY()));

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
        theScrollButtonMgr.setBorderTitle("ScrollButton");
        theScrollValue.setBorderTitle("ScrollValue");
        theScrollValue.setAlignment(TethysAlignment.CENTRE);
        myGrid.addRow(myRowNo++, theScrollButtonMgr.getNode(), theScrollValue.getNode());
        setScrollValue(null);

        /* Add listener */
        theScrollButtonMgr.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> {
            setScrollValue(e.getDetails(String.class));
            pStage.sizeToScene();
        });
        theScrollButtonMgr.getEventRegistrar().addEventListener(TethysUIEvent.PREPAREDIALOG,
                e -> theHelper.buildContextMenu(theScrollButtonMgr.getMenu()));

        /* Create list button line */
        theListButtonMgr.setBorderTitle("ListButton");
        theListValues.setBorderTitle("ListValues");
        theListValues.setAlignment(TethysAlignment.CENTRE);
        myGrid.addRow(myRowNo++, theListButtonMgr.getNode(), theListValues.getNode());

        theListButtonMgr.setValue(theHelper.buildToggleList(theListButtonMgr));
        theListButtonMgr.setText("Tag");

        /* Add listener */
        theListButtonMgr.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> {
            setListValue();
            pStage.sizeToScene();
        });

        /* Create date button line */
        theDateButtonMgr.setBorderTitle("DateButton");
        theDateValue.setBorderTitle("DateValue");
        theDateValue.setAlignment(TethysAlignment.CENTRE);
        myGrid.addRow(myRowNo++, theDateButtonMgr.getNode(), theDateValue.getNode());

        /* Add listener */
        theDateButtonMgr.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> {
            setDateValue(e.getDetails(TethysDate.class));
            pStage.sizeToScene();
        });

        /* Create simple icon button line */
        theSimpleIconButtonMgr.setBorderTitle("SimpleIconButton");
        theSimpleIconValue.setBorderTitle("IconValue");
        theSimpleIconValue.setAlignment(TethysAlignment.CENTRE);
        myGrid.addRow(myRowNo++, theSimpleIconButtonMgr.getNode(), theSimpleIconValue.getNode());
        theSimpleIconButtonMgr.setWidth(DEFAULT_ICONWIDTH);
        theHelper.buildSimpleIconState(theSimpleIconButtonMgr, TethysHelperIcon.OPENFALSE, TethysHelperIcon.OPENTRUE);

        /* Add listener */
        theSimpleIconButtonMgr.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> {
            setSimpleIconValue(e.getDetails(Boolean.class));
            pStage.sizeToScene();
        });

        /* Create state icon button line */
        TethysFXBoxPaneManager myBox = theGuiFactory.newHBoxPane();
        myBox.addNode(theStateButtonMgr);
        myBox.addNode(theStateIconButtonMgr);
        myBox.setBorderTitle("StateIconButton");
        theStateIconValue.setBorderTitle("StateIconValue");
        theStateIconValue.setAlignment(TethysAlignment.CENTRE);
        myGrid.addRow(myRowNo++, myBox.getNode(), theStateIconValue.getNode());
        theHelper.buildStateButton(theStateButtonMgr);
        theStateIconButtonMgr.setWidth(DEFAULT_ICONWIDTH);
        theHelper.buildStateIconState(theStateIconButtonMgr,
                TethysHelperIcon.OPENFALSE,
                TethysHelperIcon.OPENTRUE,
                TethysHelperIcon.CLOSEDTRUE);

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
        BorderPane myPane = new BorderPane();
        Scene myScene = new Scene(myPane);
        theGuiFactory.applyStyleSheets(myScene);
        myPane.setCenter(myGrid);
        pStage.setTitle("JavaFXScroll Demo");
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
     */
    private void setListValue() {
        /* Record the value */
        theListValues.setText(theListButtonMgr.getText());
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
