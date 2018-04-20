/*******************************************************************************
 * jTethys: Java Utilities
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jtethys.test.ui.swing;

import net.sourceforge.joceanus.jtethys.TethysLogConfig;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.test.ui.TethysHelperIcon;
import net.sourceforge.joceanus.jtethys.test.ui.TethysListId;
import net.sourceforge.joceanus.jtethys.test.ui.TethysScrollUITestHelper;
import net.sourceforge.joceanus.jtethys.test.ui.TethysScrollUITestHelper.IconState;
import net.sourceforge.joceanus.jtethys.ui.TethysAlignment;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingColorPicker;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDateButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGridPaneManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingLabel;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingListButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollContextMenu;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.HeadlessException;
import java.util.Map;

/**
 * Scroll utilities examples.
 */
public class TethysSwingScrollUIExample {
    /**
     * The default width.
     */
    private static final int DEFAULT_VALUEWIDTH = 200;

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(TethysSwingScrollUIExample.class);

    /**
     * The GuiFactory.
     */
    private final TethysSwingGuiFactory theGuiFactory;

    /**
     * The Test helper.
     */
    private final TethysScrollUITestHelper<JComponent, Icon> theHelper;

    /**
     * The popUp menu.
     */
    private final TethysSwingScrollContextMenu<String> theScrollMenu;

    /**
     * The scroll button manager.
     */
    private final TethysSwingScrollButtonManager<String> theScrollButtonMgr;

    /**
     * The simple icon button manager.
     */
    private final TethysSwingIconButtonManager<Boolean> theSimpleIconButtonMgr;

    /**
     * The state icon button manager.
     */
    private final TethysSwingIconButtonManager<Boolean> theStateIconButtonMgr;

    /**
     * The state scroll manager.
     */
    private final TethysSwingScrollButtonManager<IconState> theStateButtonMgr;

    /**
     * The list button manager.
     */
    private final TethysSwingListButtonManager<TethysListId> theListButtonMgr;

    /**
     * The date button manager.
     */
    private final TethysSwingDateButtonManager theDateButtonMgr;

    /**
     * The colour picker.
     */
    private final TethysSwingColorPicker theColorPicker;

    /**
     * The selected context value.
     */
    private final TethysSwingLabel theContextValue;

    /**
     * The selected scroll value.
     */
    private final TethysSwingLabel theScrollValue;

    /**
     * The selected date value.
     */
    private final TethysSwingLabel theDateValue;

    /**
     * The selected simple icon value.
     */
    private final TethysSwingLabel theSimpleIconValue;

    /**
     * The selected state icon values.
     */
    private final TethysSwingLabel theStateIconValue;

    /**
     * The selected list values.
     */
    private final TethysSwingLabel theListValues;

    /**
     * The colour value.
     */
    private final TethysSwingLabel theColorValue;

    /**
     * Constructor.
     */
    public TethysSwingScrollUIExample() {
        /* Create helper */
        theHelper = new TethysScrollUITestHelper<>();

        /* Create GUI Factory */
        theGuiFactory = new TethysSwingGuiFactory();

        /* Create resources */
        theScrollMenu = theGuiFactory.newContextMenu();
        theScrollButtonMgr = theGuiFactory.newScrollButton();
        theSimpleIconButtonMgr = theGuiFactory.newIconButton();
        theStateIconButtonMgr = theGuiFactory.newIconButton();
        theStateButtonMgr = theGuiFactory.newScrollButton();
        theListButtonMgr = theGuiFactory.newListButton();
        theDateButtonMgr = theGuiFactory.newDateButton();
        theColorPicker = theGuiFactory.newColorPicker();
        theContextValue = theGuiFactory.newLabel();
        theScrollValue = theGuiFactory.newLabel();
        theDateValue = theGuiFactory.newLabel();
        theSimpleIconValue = theGuiFactory.newLabel();
        theStateIconValue = theGuiFactory.newLabel();
        theListValues = theGuiFactory.newLabel();
        theColorValue = theGuiFactory.newLabel();
    }

    /**
     * Main function.
     * @param args the arguments
     */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    /**
     * Create and show the GUI.
     */
    static void createAndShowGUI() {
        try {
            /* Create the frame */
            final JFrame myFrame = new JFrame("SwingScroll Demo");

            /* Configure log4j */
            TethysLogConfig.configureLog4j();

            /* Create the UI */
            final TethysSwingScrollUIExample myExample = new TethysSwingScrollUIExample();

            /* Build the panel */
            final TethysSwingGridPaneManager myPanel = myExample.buildPanel();

            /* Attach the panel to the frame */
            myPanel.getNode().setOpaque(true);
            myFrame.setContentPane(myPanel.getNode());
            myFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            /* Show the frame */
            myFrame.pack();
            myFrame.setLocationRelativeTo(null);
            myFrame.setVisible(true);
        } catch (HeadlessException e) {
            LOGGER.error("createGUI didn't complete successfully", e);
        }
    }

    /**
     * Build the panel.
     */
    private TethysSwingGridPaneManager buildPanel() {
        /* Create a panel */
        final TethysSwingGridPaneManager myGrid = theGuiFactory.newGridPane();

        /* Create context menu line */
        final TethysSwingLabel myContextArea = theGuiFactory.newLabel("Right-click for Menu");
        myContextArea.setBorderTitle("ContextArea");
        myContextArea.setAlignment(TethysAlignment.CENTRE);
        theContextValue.setBorderTitle("ContextValue");
        theContextValue.setAlignment(TethysAlignment.CENTRE);
        theContextValue.setPreferredWidth(DEFAULT_VALUEWIDTH);
        myGrid.addCell(myContextArea);
        myGrid.addCell(theContextValue);
        myGrid.allowCellGrowth(theContextValue);
        myGrid.newRow();
        setContextValue(null);

        /* Build the context menu */
        theHelper.buildContextMenu(theScrollMenu);

        /* Create the menu hook */
        myContextArea.setContextMenu(theScrollMenu);

        /* Add listener */
        theScrollMenu.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> {
            /* If we selected a value */
            final TethysScrollMenuItem<String> mySelected = theScrollMenu.getSelectedItem();
            if (mySelected != null) {
                setContextValue(mySelected.getValue());
            }
        });

        /* Create scroll button line */
        theScrollButtonMgr.setBorderTitle("ScrollButton");
        theScrollValue.setBorderTitle("ScrollValue");
        theScrollValue.setAlignment(TethysAlignment.CENTRE);
        theScrollValue.setPreferredWidth(DEFAULT_VALUEWIDTH);
        myGrid.addCell(theScrollButtonMgr);
        myGrid.addCell(theScrollValue);
        myGrid.allowCellGrowth(theScrollValue);
        myGrid.newRow();
        setScrollValue(null);

        /* Add listener */
        theScrollButtonMgr.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE,
                e -> setScrollValue(e.getDetails(String.class)));
        theScrollButtonMgr.setMenuConfigurator(theHelper::buildContextMenu);

        /* Create list button line */
        theListButtonMgr.setBorderTitle("ListButton");
        theListValues.setBorderTitle("ListValues");
        theListValues.setAlignment(TethysAlignment.CENTRE);
        theListValues.setPreferredWidth(DEFAULT_VALUEWIDTH);
        myGrid.addCell(theListButtonMgr);
        myGrid.addCell(theListValues);
        myGrid.allowCellGrowth(theListValues);
        myGrid.newRow();

        theListButtonMgr.setValue(theHelper.buildSelectedList());
        theListButtonMgr.setSelectables(theHelper::buildSelectableList);
        theListButtonMgr.setText("Tag");

        /* Add listener */
        theListButtonMgr.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> setListValue());

        /* Create date button line */
        theDateButtonMgr.setBorderTitle("DateButton");
        theDateValue.setBorderTitle("DateValue");
        theDateValue.setAlignment(TethysAlignment.CENTRE);
        theDateValue.setPreferredWidth(DEFAULT_VALUEWIDTH);
        myGrid.addCell(theDateButtonMgr);
        myGrid.addCell(theDateValue);
        myGrid.allowCellGrowth(theDateValue);
        myGrid.newRow();

        /* Add listener */
        theDateButtonMgr.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> setDateValue(e.getDetails(TethysDate.class)));

        /* Create simple icon button line */
        theSimpleIconButtonMgr.setBorderTitle("SimpleIconButton");
        theSimpleIconValue.setBorderTitle("IconValue");
        theSimpleIconValue.setAlignment(TethysAlignment.CENTRE);
        theSimpleIconValue.setPreferredWidth(DEFAULT_VALUEWIDTH);
        myGrid.addCell(theSimpleIconButtonMgr);
        myGrid.addCell(theSimpleIconValue);
        myGrid.allowCellGrowth(theSimpleIconValue);
        myGrid.newRow();
        final TethysIconMapSet<Boolean> myMapSet = theHelper.buildSimpleIconState(TethysHelperIcon.OPENFALSE, TethysHelperIcon.OPENTRUE);
        theSimpleIconButtonMgr.setIconMapSet(() -> myMapSet);
        theSimpleIconButtonMgr.setValue(Boolean.FALSE);

        /* Add listener */
        theSimpleIconButtonMgr.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE,
                e -> setSimpleIconValue(e.getDetails(Boolean.class)));

        /* Create state icon button line */
        final TethysSwingBoxPaneManager myBox = theGuiFactory.newHBoxPane();
        myBox.addNode(theStateButtonMgr);
        myBox.addNode(theStateIconButtonMgr);
        myBox.setBorderTitle("StateIconButton");
        theStateIconValue.setBorderTitle("StateIconValue");
        theStateIconValue.setAlignment(TethysAlignment.CENTRE);
        theStateIconValue.setPreferredWidth(DEFAULT_VALUEWIDTH);
        myGrid.addCell(myBox);
        myGrid.addCell(theStateIconValue);
        myGrid.allowCellGrowth(theStateIconValue);
        myGrid.newRow();

        theHelper.buildStateButton(theStateButtonMgr);
        final Map<IconState, TethysIconMapSet<Boolean>> myMap = theHelper.buildStateIconState(TethysHelperIcon.OPENFALSE, TethysHelperIcon.OPENTRUE, TethysHelperIcon.CLOSEDTRUE);
        theStateIconButtonMgr.setIconMapSet(() -> myMap.get(theStateButtonMgr.getValue()));
        theStateIconButtonMgr.setNullMargins();
        theStateIconButtonMgr.setValue(Boolean.FALSE);

        /* Add listener */
        theStateIconButtonMgr.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> setStateIconValue(e.getDetails(Boolean.class)));

        /* Add listener */
        theStateButtonMgr.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> {
            setStateIconValue(theStateIconButtonMgr.getValue());
            theStateIconButtonMgr.applyButtonState();
        });

        /* Create colour picker line */
        theColorPicker.setBorderTitle("ColorPicker");
        theColorValue.setBorderTitle("ColorValue");
        theColorValue.setAlignment(TethysAlignment.CENTRE);
        theColorValue.setPreferredWidth(DEFAULT_VALUEWIDTH);
        myGrid.addCell(theColorPicker);
        myGrid.addCell(theColorValue);
        myGrid.allowCellGrowth(theColorValue);
        myGrid.newRow();

        /* Add listener */
        theColorPicker.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> setColorValue(e.getDetails(String.class)));

        /* Configure the grid */
        myGrid.setBorderPadding(3);

        /* Return the panel */
        return myGrid;
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

    /**
     * Set the colour value.
     * @param pValue the value to set
     */
    private void setColorValue(final String pValue) {
        /* Record the value */
        theColorValue.setText(pValue);
    }
}
