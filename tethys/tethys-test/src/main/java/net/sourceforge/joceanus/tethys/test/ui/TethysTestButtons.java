/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.tethys.test.ui;

import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.tethys.test.ui.TethysTestHelper.TethysIconState;
import net.sourceforge.joceanus.tethys.api.base.TethysUIAlignment;
import net.sourceforge.joceanus.tethys.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.api.button.TethysUIButtonFactory;
import net.sourceforge.joceanus.tethys.api.button.TethysUIDateButtonManager;
import net.sourceforge.joceanus.tethys.api.button.TethysUIIconButtonManager;
import net.sourceforge.joceanus.tethys.api.button.TethysUIListButtonManager;
import net.sourceforge.joceanus.tethys.api.button.TethysUIScrollButtonManager;
import net.sourceforge.joceanus.tethys.api.control.TethysUIControl.TethysUIIconMapSet;
import net.sourceforge.joceanus.tethys.api.control.TethysUIControlFactory;
import net.sourceforge.joceanus.tethys.api.control.TethysUILabel;
import net.sourceforge.joceanus.tethys.api.button.TethysUIColorPicker;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIMenuFactory;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollItem;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIBoxPaneManager;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIGridPaneManager;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIPaneFactory;

import java.util.Map;

/**
 * Buttons Test examples.
 */
public class TethysTestButtons {
    /**
     * Value width.
     */
    private static final int DEFAULT_VALUEWIDTH = 200;

    /**
     * The GuiFactory.
     */
    private final TethysUIFactory<?> theGuiFactory;

    /**
     * The Test helper.
     */
    private final TethysTestHelper theHelper;

    /**
     * The context menu.
     */
    private final TethysUIScrollMenu<String> theContextMenu;

    /**
     * The scroll button manager.
     */
    private final TethysUIScrollButtonManager<String> theScrollButtonMgr;

    /**
     * The simple icon button manager.
     */
    private final TethysUIIconButtonManager<Boolean> theSimpleIconButtonMgr;

    /**
     * The state icon button manager.
     */
    private final TethysUIIconButtonManager<Boolean> theStateIconButtonMgr;

    /**
     * The state scroll manager.
     */
    private final TethysUIScrollButtonManager<TethysIconState> theStateButtonMgr;

    /**
     * The list button manager.
     */
    private final TethysUIListButtonManager<TethysTestListId> theListButtonMgr;

    /**
     * The date button manager.
     */
    private final TethysUIDateButtonManager theDateButtonMgr;

    /**
     * The colour picker.
     */
    private final TethysUIColorPicker<?> theColorPicker;

    /**
     * The selected context value.
     */
    private final TethysUILabel theContextValue;

    /**
     * The selected scroll value.
     */
    private final TethysUILabel theScrollValue;

    /**
     * The selected date value.
     */
    private final TethysUILabel theDateValue;

    /**
     * The selected simple icon value.
     */
    private final TethysUILabel theSimpleIconValue;

    /**
     * The selected state icon values.
     */
    private final TethysUILabel theStateIconValue;

    /**
     * The selected list values.
     */
    private final TethysUILabel theListValues;

    /**
     * The colour value.
     */
    private final TethysUILabel theColorValue;

    /**
     * The panel.
     */
    private final TethysUIComponent thePanel;

    /**
     * Constructor.
     * @param pFactory the factory
     */
    public TethysTestButtons(final TethysUIFactory<?> pFactory) {
        /* Store GUI Factory */
        theGuiFactory = pFactory;

        /* Create helper */
        theHelper = new TethysTestHelper(theGuiFactory);

        /* Create resources */
        final TethysUIButtonFactory<?> myButtons = theGuiFactory.buttonFactory();
        final TethysUIMenuFactory myMenus = theGuiFactory.menuFactory();
        final TethysUIControlFactory myControls = theGuiFactory.controlFactory();
        theContextMenu = myMenus.newContextMenu();
        theScrollButtonMgr = myButtons.newScrollButton(String.class);
        theSimpleIconButtonMgr = myButtons.newIconButton(Boolean.class);
        theStateIconButtonMgr = myButtons.newIconButton(Boolean.class);
        theStateButtonMgr = myButtons.newScrollButton(TethysIconState.class);
        theListButtonMgr = myButtons.newListButton();
        theDateButtonMgr = myButtons.newDateButton();
        theColorPicker = myButtons.newColorPicker();
        theContextValue = myControls.newLabel();
        theScrollValue = myControls.newLabel();
        theDateValue = myControls.newLabel();
        theSimpleIconValue = myControls.newLabel();
        theStateIconValue = myControls.newLabel();
        theListValues = myControls.newLabel();
        theColorValue = myControls.newLabel();

        /* Create the panel */
        thePanel = buildPanel();
    }

    /**
     * Obtain the component.
     * @return the component
     */
    TethysUIComponent getComponent() {
        return thePanel;
    }

    /**
     * Build panel.
     */
    private TethysUIComponent buildPanel() {
        /* Create a GridPane */
        final TethysUIPaneFactory myPanes = theGuiFactory.paneFactory();
        final TethysUIGridPaneManager myGrid = myPanes.newGridPane();

        /* Create context menu line */
        final TethysUIControlFactory myControls = theGuiFactory.controlFactory();
        final TethysUILabel myContextArea = myControls.newLabel("Right-click for Menu");
        myContextArea.setBorderTitle("ContextArea");
        myContextArea.setAlignment(TethysUIAlignment.CENTRE);
        theContextValue.setBorderTitle("ContextValue");
        theContextValue.setAlignment(TethysUIAlignment.CENTRE);
        theContextValue.setPreferredWidth(DEFAULT_VALUEWIDTH);
        myGrid.addCell(myContextArea);
        myGrid.addCell(theContextValue);
        myGrid.allowCellGrowth(theContextValue);
        myGrid.newRow();
        setContextValue(null);

        /* Build the context menu */
        theHelper.buildContextMenu(theContextMenu);

        /* Create the menu hook */
        myContextArea.setContextMenu(theContextMenu);

        /* Add listener */
        theContextMenu.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> {
            /* If we selected a value */
            final TethysUIScrollItem<String> mySelected = theContextMenu.getSelectedItem();
            if (mySelected != null) {
                setContextValue(mySelected.getValue());
            }
        });

        /* Create scroll button line */
        theScrollButtonMgr.setBorderTitle("ScrollButton");
        theScrollValue.setBorderTitle("ScrollValue");
        theScrollValue.setAlignment(TethysUIAlignment.CENTRE);
        theScrollValue.setPreferredWidth(DEFAULT_VALUEWIDTH);
        myGrid.addCell(theScrollButtonMgr);
        myGrid.addCell(theScrollValue);
        myGrid.allowCellGrowth(theScrollValue);
        myGrid.newRow();

        /* Add listener */
        theScrollButtonMgr.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> setScrollValue(e.getDetails(String.class)));
        theScrollButtonMgr.setMenuConfigurator(theHelper::buildContextMenu);
        theScrollButtonMgr.setValue("First");

        /* Create list button line */
        theListButtonMgr.setBorderTitle("ListButton");
        theListValues.setBorderTitle("ListValues");
        theListValues.setAlignment(TethysUIAlignment.CENTRE);
        theListValues.setPreferredWidth(DEFAULT_VALUEWIDTH);
        myGrid.addCell(theListButtonMgr);
        myGrid.addCell(theListValues);
        myGrid.allowCellGrowth(theListValues);
        myGrid.newRow();

        /* Add listener */
        theListButtonMgr.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> setListValue());
        theListButtonMgr.setSelectables(theHelper::buildSelectableList);
        theListButtonMgr.setValue(theHelper.buildSelectedList());

        /* Create date button line */
        theDateButtonMgr.setBorderTitle("DateButton");
        theDateValue.setBorderTitle("DateValue");
        theDateValue.setAlignment(TethysUIAlignment.CENTRE);
        theDateValue.setPreferredWidth(DEFAULT_VALUEWIDTH);
        myGrid.addCell(theDateButtonMgr);
        myGrid.addCell(theDateValue);
        myGrid.allowCellGrowth(theDateValue);
        myGrid.newRow();

        /* Add listener */
        theDateButtonMgr.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> setDateValue(e.getDetails(OceanusDate.class)));
        theDateButtonMgr.setSelectedDate(new OceanusDate());

        /* Create simple icon button line */
        theSimpleIconButtonMgr.setBorderTitle("SimpleIconButton");
        theSimpleIconValue.setBorderTitle("IconValue");
        theSimpleIconValue.setAlignment(TethysUIAlignment.CENTRE);
        theSimpleIconValue.setPreferredWidth(DEFAULT_VALUEWIDTH);
        myGrid.addCell(theSimpleIconButtonMgr);
        myGrid.addCell(theSimpleIconValue);
        myGrid.allowCellGrowth(theSimpleIconValue);
        myGrid.newRow();
        final TethysUIIconMapSet<Boolean> myMapSet = theHelper.buildSimpleIconState(TethysTestIcon.OPENFALSE, TethysTestIcon.OPENTRUE);
        theSimpleIconButtonMgr.setIconMapSet(() -> myMapSet);
        theSimpleIconButtonMgr.setValue(Boolean.FALSE);

        /* Add listener */
        theSimpleIconButtonMgr.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> setSimpleIconValue(e.getDetails(Boolean.class)));

        /* Create state icon button line */
        final TethysUIBoxPaneManager myBox = myPanes.newHBoxPane();
        myBox.addNode(theStateButtonMgr);
        myBox.addNode(theStateIconButtonMgr);
        myBox.setBorderTitle("StateIconButton");
        theStateIconValue.setBorderTitle("StateIconValue");
        theStateIconValue.setAlignment(TethysUIAlignment.CENTRE);
        theStateIconValue.setPreferredWidth(DEFAULT_VALUEWIDTH);
        myGrid.addCell(myBox);
        myGrid.addCell(theStateIconValue);
        myGrid.allowCellGrowth(theStateIconValue);
        myGrid.newRow();
        theHelper.buildStateButton(theStateButtonMgr);
        final Map<TethysIconState, TethysUIIconMapSet<Boolean>> myMap = theHelper.buildStateIconState(TethysTestIcon.OPENFALSE, TethysTestIcon.OPENTRUE, TethysTestIcon.CLOSEDTRUE);
        theStateIconButtonMgr.setIconMapSet(() -> myMap.get(theStateButtonMgr.getValue()));
        theStateIconButtonMgr.setNullMargins();
        theStateIconButtonMgr.setValue(Boolean.FALSE);
        theStateIconButtonMgr.setVisible(false);

        /* Add listener */
        theStateIconButtonMgr.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> setStateIconValue(e.getDetails(Boolean.class)));

        /* Add listener */
        theStateButtonMgr.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> {
            setStateIconValue(theStateIconButtonMgr.getValue());
            theStateIconButtonMgr.applyButtonState();
            theStateIconButtonMgr.setVisible(theStateButtonMgr.getValue() == TethysIconState.OPEN);
        });

        /* Create colour picker line */
        theColorPicker.setBorderTitle("ColorPicker");
        theColorPicker.setValue("#000000");
        theColorValue.setBorderTitle("ColorValue");
        theColorValue.setAlignment(TethysUIAlignment.CENTRE);
        theColorValue.setPreferredWidth(DEFAULT_VALUEWIDTH);
        myGrid.addCell(theColorPicker);
        myGrid.addCell(theColorValue);
        myGrid.allowCellGrowth(theColorValue);
        myGrid.newRow();

        /* Add listener */
        theColorPicker.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> setColorValue(e.getDetails(String.class)));

        /* Configure the grid */
        myGrid.setBorderPadding(2);
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
    private void setDateValue(final OceanusDate pValue) {
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
        theStateIconValue.setText(theStateButtonMgr.getValue().toString() + ":" + pValue);
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
