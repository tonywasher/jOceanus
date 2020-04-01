/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2020 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import net.sourceforge.joceanus.jtethys.decimal.TethysDilutedPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysCurrencyEditField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysDateButtonField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysIconButtonField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysListButtonField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysRawDecimalEditField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysScrollButtonField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysValidatedEditField;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;

/**
 * Tethys GUI Factory.
 */
public abstract class TethysGuiFactory {
    /**
     * The Next nodeId.
     */
    private final AtomicInteger theNextNodeId = new AtomicInteger(1);

    /**
     * Data Formatter.
     */
    private final TethysDataFormatter theFormatter;

    /**
     * ValueSet.
     */
    private final TethysValueSet theValueSet;

    /**
     * The node Map.
     */
    private final Map<Integer, TethysParentComponent> theParentMap;

    /**
     * Program Definition.
     */
    private final TethysProgram theProgram;

    /**
     * ParentComponent.
     */
    @FunctionalInterface
    public interface TethysParentComponent {
        /**
         * Set child visibility.
         * @param pChild the child
         * @param pVisible the visibility
         */
        void setChildVisible(TethysComponent pChild,
                             boolean pVisible);
    }

    /**
     * Constructor.
     * @param pFormatter the formatter
     * @param pApp the program definition
     */
    protected TethysGuiFactory(final TethysDataFormatter pFormatter,
                               final TethysProgram pApp) {
        theFormatter = pFormatter;
        theParentMap = new HashMap<>();
        theValueSet = new TethysValueSet();
        theProgram = pApp;
    }

    /**
     * Obtain the formatter.
     * @return the formatter
     */
    public TethysDataFormatter getDataFormatter() {
        return theFormatter;
    }

    /**
     * Obtain the valueSet.
     * @return the valueSet
     */
    public TethysValueSet getValueSet() {
        return theValueSet;
    }

    /**
     * Obtain the next id.
     * @return the next id
     */
    public Integer getNextId() {
        return theNextNodeId.getAndIncrement();
    }

    /**
     * Set visibility of node.
     * @param pNode the node
     * @param pVisible true/false
     */
    public void setNodeVisible(final TethysComponent pNode,
                               final boolean pVisible) {
        /* Lookup parent */
        final TethysParentComponent myParent = theParentMap.get(pNode.getId());
        if (myParent != null) {
            myParent.setChildVisible(pNode, pVisible);
        }
    }

    /**
     * Register Child.
     * @param pParent the parent
     * @param pChild the child node
     */
    protected void registerChild(final TethysParentComponent pParent,
                                 final TethysComponent pChild) {
        theParentMap.put(pChild.getId(), pParent);
    }

    /**
     * DeRegister Child.
     * @param pChild the child node
     */
    protected void deRegisterChild(final TethysComponent pChild) {
        theParentMap.remove(pChild.getId());
    }

    /**
     * Obtain the program definition.
     * @return the definition
     */
    public TethysProgram getProgramDefinitions() {
        return theProgram;
    }

    /**
     * Resolve Icon.
     * @param pIconId the mapped IconId
     * @param pWidth the icon width
     * @return the icon
     */
    public abstract TethysIcon resolveIcon(TethysIconId pIconId, int pWidth);

    /**
     * Obtain a new label.
     * @return the new label
     */
    public abstract TethysLabel newLabel();

    /**
     * Obtain a new label.
     * @param pText the label text
     * @return the new label
     */
    public abstract TethysLabel newLabel(String pText);

    /**
     * Obtain a check box.
     * @return the new check box
     */
    public abstract TethysCheckBox newCheckBox();

    /**
     * Obtain a check box.
     * @param pText the checkBox text
     * @return the new check box
     */
    public abstract TethysCheckBox newCheckBox(String pText);

    /**
     * Obtain a new button.
     * @return the new button
     */
    public abstract TethysButton newButton();

    /**
     * Obtain a new textArea.
     * @return the new textArea
     */
    public abstract TethysTextArea newTextArea();

    /**
     * Obtain a context menu.
     * @param <T> the item type
     * @return the new menu
     */
    public abstract <T> TethysScrollMenu<T> newContextMenu();

    /**
     * Obtain a new date button manager.
     * @return the new manager
     */
    public abstract TethysDateButtonManager newDateButton();

    /**
     * Obtain a new scroll button manager.
     * @param <T> the item type
     * @return the new manager
     */
    public abstract <T> TethysScrollButtonManager<T> newScrollButton();

    /**
     * Obtain a new list button manager.
     * @param <T> the item type
     * @return the new manager
     */
    public abstract <T extends Comparable<T>> TethysListButtonManager<T> newListButton();

    /**
     * Obtain a new icon button manager.
     * @param <T> the item type
     * @return the new manager
     */
    public abstract <T> TethysIconButtonManager<T> newIconButton();

    /**
     * Obtain a new dateRange selector.
     * @return the new selector
     */
    public abstract TethysDateRangeSelector newDateRangeSelector();

    /**
     * Obtain a new dateRange selector.
     * @param pBaseIsStart is the baseDate the start of the period? (true/false)
     * @return the new selector
     */
    public abstract TethysDateRangeSelector newDateRangeSelector(boolean pBaseIsStart);

    /**
     * Obtain a new passwordField.
     * @return the new field
     */
    public abstract TethysPasswordField newPasswordField();

    /**
     * Obtain a new colorPicker.
     * @return the new picker
     */
    public abstract TethysColorPicker newColorPicker();

    /**
     * Obtain a new HTML manager.
     * @return the new manager
     */
    public abstract TethysHTMLManager newHTMLManager();

    /**
     * Obtain a new Tree manager.
     * @param <T> the item type
     * @return the new manager
     */
    public abstract <T> TethysTreeManager<T> newTreeManager();

    /**
     * Obtain a new splitTree manager.
     * @param <T> the item type
     * @return the new manager
     */
    public abstract <T> TethysSplitTreeManager<T> newSplitTreeManager();

    /**
     * Obtain a new fileSelector.
     * @return the new selector
     */
    public abstract TethysFileSelector newFileSelector();

    /**
     * Obtain a new directorySelector.
     * @return the new selector
     */
    public abstract TethysDirectorySelector newDirectorySelector();

    /**
     * Obtain a new progressBar.
     * @return the new progressBar
     */
    public abstract TethysProgressBar newProgressBar();

    /**
     * Obtain a new slider.
     * @return the new slider
     */
    public abstract TethysSlider newSlider();

    /**
     * Obtain a new menuBar manager.
     * @return the new manager
     */
    public abstract TethysMenuBarManager newMenuBar();

    /**
     * Obtain a new toolBar manager.
     * @return the new manager
     */
    public abstract TethysToolBarManager newToolBar();

    /**
     * Obtain a new tabPane manager.
     * @return the new manager
     */
    public abstract TethysTabPaneManager newTabPane();

    /**
     * Obtain a new cardPane manager.
     * @param <P> the card panel type
     * @return the new manager
     */
    public abstract <P extends TethysComponent> TethysCardPaneManager<P> newCardPane();

    /**
     * Obtain a new borderPane manager.
     * @return the new manager
     */
    public abstract TethysBorderPaneManager newBorderPane();

    /**
     * Obtain a new flowPane manager.
     * @return the new manager
     */
    public abstract TethysFlowPaneManager newFlowPane();

    /**
     * Obtain a new vertical boxPane manager.
     * @return the new manager
     */
    public abstract TethysBoxPaneManager newVBoxPane();

    /**
     * Obtain a new horizontal boxPane manager.
     * @return the new manager
     */
    public abstract TethysBoxPaneManager newHBoxPane();

    /**
     * Obtain a new gridPane manager.
     * @return the new manager
     */
    public abstract TethysGridPaneManager newGridPane();

    /**
     * Obtain a new scrollPane manager.
     * @return the new manager
     */
    public abstract TethysScrollPaneManager newScrollPane();

    /**
     * Obtain a new string data field.
     * @return the new field
     */
    public abstract TethysValidatedEditField<String> newStringField();

    /**
     * Obtain a new charArray data field.
     * @return the new field
     */
    public abstract TethysValidatedEditField<char[]> newCharArrayField();

    /**
     * Obtain a new short data field.
     * @return the new field
     */
    public abstract TethysValidatedEditField<Short> newShortField();

    /**
     * Obtain a new integer data field.
     * @return the new field
     */
    public abstract TethysValidatedEditField<Integer> newIntegerField();

    /**
     * Obtain a new long data field.
     * @return the new field
     */
    public abstract TethysValidatedEditField<Long> newLongField();

    /**
     * Obtain a new raw decimal data field.
     * @return the new field
     */
    public abstract TethysRawDecimalEditField newRawDecimalField();

    /**
     * Obtain a new money data field.
     * @return the new field
     */
    public abstract TethysCurrencyEditField<TethysMoney> newMoneyField();

    /**
     * Obtain a new price data field.
     * @return the new field
     */
    public abstract TethysCurrencyEditField<TethysPrice> newPriceField();

    /**
     * Obtain a new dilutedPrice data field.
     * @return the new field
     */
    public abstract TethysCurrencyEditField<TethysDilutedPrice> newDilutedPriceField();

    /**
     * Obtain a new rate data field.
     * @return the new field
     */
    public abstract TethysValidatedEditField<TethysRate> newRateField();

    /**
     * Obtain a new units data field.
     * @return the new field
     */
    public abstract TethysValidatedEditField<TethysUnits> newUnitsField();

    /**
     * Obtain a new dilution data field.
     * @return the new field
     */
    public abstract TethysValidatedEditField<TethysDilution> newDilutionField();

    /**
     * Obtain a new ratio data field.
     * @return the new field
     */
    public abstract TethysValidatedEditField<TethysRatio> newRatioField();

    /**
     * Obtain a new date data field.
     * @return the new field
     */
    public abstract TethysDateButtonField newDateField();

    /**
     * Obtain a new scroll data field.
     * @param <T> the item type
     * @return the new field
     */
    public abstract <T> TethysScrollButtonField<T> newScrollField();

    /**
     * Obtain a new list data field.
     * @param <T> the item type
     * @return the new field
     */
    public abstract <T extends Comparable<T>> TethysListButtonField<T> newListField();

    /**
     * Obtain a new simple icon data field.
     * @param <T> the item type
     * @return the new field
     */
    public abstract <T> TethysIconButtonField<T> newIconField();

    /**
     * Obtain a new colour data field.
     * @return the new field
     */
    public abstract TethysDataEditField<String> newColorField();

    /**
     * Obtain a new table.
     * @param <C> the column id type
     * @param <R> the row type
     * @return the new field
     */
    public abstract <C, R> TethysTableManager<C, R> newTable();

    /**
     * Obtain a new aboutBox.
     * @return the new box
     */
    public abstract TethysAbout newAboutBox();
}
