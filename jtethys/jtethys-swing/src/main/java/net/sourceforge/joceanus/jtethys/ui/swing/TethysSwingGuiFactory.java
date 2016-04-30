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
package net.sourceforge.joceanus.jtethys.ui.swing;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;

import net.sourceforge.joceanus.jtethys.ui.TethysDataFormatter;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataButtonField.TethysSwingDateButtonField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataButtonField.TethysSwingIconButtonField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataButtonField.TethysSwingListButtonField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataButtonField.TethysSwingScrollButtonField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataButtonField.TethysSwingStateIconButtonField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingCharArrayTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingDilutedPriceTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingDilutionTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingIntegerTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingLongTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingMoneyTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingPriceTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingRateTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingRatioTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingShortTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingStringTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingUnitsTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingIconButtonManager.TethysSwingSimpleIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingIconButtonManager.TethysSwingStateIconButtonManager;

/**
 * Tethys GUI Manager.
 */
public class TethysSwingGuiFactory
        extends TethysGuiFactory<JComponent, Icon> {
    /**
     * The Field Adjuster.
     */
    private final TethysSwingDataFieldAdjust theAdjuster;

    /**
     * Frame.
     */
    private JFrame theFrame;

    /**
     * Constructor.
     */
    public TethysSwingGuiFactory() {
        this(new TethysDataFormatter());
    }

    /**
     * Constructor.
     * @param pFormatter the formatter
     */
    public TethysSwingGuiFactory(final TethysDataFormatter pFormatter) {
        super(pFormatter);
        theAdjuster = new TethysSwingDataFieldAdjust(this);
    }

    /**
     * Set the frame.
     * @param pFrame the frame
     */
    public void setFrame(final JFrame pFrame) {
        theFrame = pFrame;
    }

    /**
     * Obtain the field adjuster.
     * @return the field adjuster
     */
    protected TethysSwingDataFieldAdjust getFieldAdjuster() {
        return theAdjuster;
    }

    @Override
    public TethysSwingLabel newLabel() {
        return new TethysSwingLabel(this);
    }

    @Override
    public TethysSwingLabel newLabel(final String pText) {
        TethysSwingLabel myLabel = newLabel();
        myLabel.setText(pText);
        return myLabel;
    }

    @Override
    public TethysSwingCheckBox newCheckBox() {
        return new TethysSwingCheckBox(this);
    }

    @Override
    public TethysSwingCheckBox newCheckBox(final String pText) {
        TethysSwingCheckBox myBox = newCheckBox();
        myBox.setText(pText);
        return myBox;
    }

    @Override
    public TethysSwingButton newButton() {
        return new TethysSwingButton(this);
    }

    @Override
    public <T> TethysSwingScrollContextMenu<T> newContextMenu() {
        return new TethysSwingScrollContextMenu<>();
    }

    @Override
    public TethysSwingDateButtonManager newDateButton() {
        return new TethysSwingDateButtonManager(this);
    }

    @Override
    public <T> TethysSwingScrollButtonManager<T> newScrollButton() {
        return new TethysSwingScrollButtonManager<>(this);
    }

    @Override
    public <T> TethysSwingListButtonManager<T> newListButton() {
        return new TethysSwingListButtonManager<>(this);
    }

    @Override
    public <T> TethysSwingSimpleIconButtonManager<T> newSimpleIconButton() {
        return new TethysSwingSimpleIconButtonManager<>(this);
    }

    @Override
    public <T, S> TethysSwingStateIconButtonManager<T, S> newStateIconButton() {
        return new TethysSwingStateIconButtonManager<>(this);
    }

    @Override
    public TethysSwingDateRangeSelector newDateRangeSelector() {
        return newDateRangeSelector(false);
    }

    @Override
    public TethysSwingDateRangeSelector newDateRangeSelector(final boolean pBaseIsStart) {
        return new TethysSwingDateRangeSelector(this, pBaseIsStart);
    }

    @Override
    public TethysSwingPasswordField newPasswordField() {
        return new TethysSwingPasswordField(this);
    }

    @Override
    public TethysSwingColorPicker newColorPicker() {
        return new TethysSwingColorPicker(this);
    }

    @Override
    public TethysSwingHTMLManager newHTMLManager() {
        return new TethysSwingHTMLManager(this);
    }

    @Override
    public <T> TethysSwingTreeManager<T> newTreeManager() {
        return new TethysSwingTreeManager<>();
    }

    @Override
    public <T> TethysSwingSplitTreeManager<T> newSplitTreeManager() {
        return new TethysSwingSplitTreeManager<>(this);
    }

    @Override
    public TethysSwingFileSelector newFileSelector() {
        return new TethysSwingFileSelector(theFrame);
    }

    @Override
    public TethysSwingDirectorySelector newDirectorySelector() {
        return new TethysSwingDirectorySelector(theFrame);
    }

    @Override
    public TethysSwingProgressBar newProgressBar() {
        return new TethysSwingProgressBar(this);
    }

    @Override
    public TethysSwingSlider newSlider() {
        return new TethysSwingSlider(this);
    }

    @Override
    public <T> TethysSwingMenuBarManager<T> newMenuBar() {
        return new TethysSwingMenuBarManager<>();
    }

    @Override
    public TethysSwingTabPaneManager newTabPane() {
        return new TethysSwingTabPaneManager(this);
    }

    @Override
    public <P extends TethysNode<JComponent>> TethysSwingCardPaneManager<P> newCardPane() {
        return new TethysSwingCardPaneManager<>(this);
    }

    @Override
    public TethysSwingBorderPaneManager newBorderPane() {
        return new TethysSwingBorderPaneManager(this);
    }

    @Override
    public TethysSwingFlowPaneManager newFlowPane() {
        return new TethysSwingFlowPaneManager(this);
    }

    @Override
    public TethysSwingBoxPaneManager newVBoxPane() {
        return new TethysSwingBoxPaneManager(this, false);
    }

    @Override
    public TethysSwingBoxPaneManager newHBoxPane() {
        return new TethysSwingBoxPaneManager(this, true);
    }

    @Override
    public TethysSwingGridPaneManager newGridPane() {
        return new TethysSwingGridPaneManager(this);
    }

    @Override
    public TethysSwingStringTextField newStringField() {
        return new TethysSwingStringTextField(this);
    }

    @Override
    public TethysSwingCharArrayTextField newCharArrayField() {
        return new TethysSwingCharArrayTextField(this);
    }

    @Override
    public TethysSwingShortTextField newShortField() {
        return new TethysSwingShortTextField(this);
    }

    @Override
    public TethysSwingIntegerTextField newIntegerField() {
        return new TethysSwingIntegerTextField(this);
    }

    @Override
    public TethysSwingLongTextField newLongField() {
        return new TethysSwingLongTextField(this);
    }

    @Override
    public TethysSwingMoneyTextField newMoneyField() {
        return new TethysSwingMoneyTextField(this);
    }

    @Override
    public TethysSwingPriceTextField newPriceField() {
        return new TethysSwingPriceTextField(this);
    }

    @Override
    public TethysSwingDilutedPriceTextField newDilutedPriceField() {
        return new TethysSwingDilutedPriceTextField(this);
    }

    @Override
    public TethysSwingRateTextField newRateField() {
        return new TethysSwingRateTextField(this);
    }

    @Override
    public TethysSwingUnitsTextField newUnitsField() {
        return new TethysSwingUnitsTextField(this);
    }

    @Override
    public TethysSwingDilutionTextField newDilutionField() {
        return new TethysSwingDilutionTextField(this);
    }

    @Override
    public TethysSwingRatioTextField newRatioField() {
        return new TethysSwingRatioTextField(this);
    }

    @Override
    public TethysSwingDateButtonField newDateField() {
        return new TethysSwingDateButtonField(this);
    }

    @Override
    public <T> TethysSwingScrollButtonField<T> newScrollField() {
        return new TethysSwingScrollButtonField<>(this);
    }

    @Override
    public <T> TethysSwingListButtonField<T> newListField() {
        return new TethysSwingListButtonField<>(this);
    }

    @Override
    public <T> TethysSwingIconButtonField<T> newSimpleIconField() {
        return new TethysSwingIconButtonField<>(this);
    }

    @Override
    public <T, S> TethysSwingStateIconButtonField<T, S> newStateIconField() {
        return new TethysSwingStateIconButtonField<>(this);
    }

    @Override
    public <C, R> TethysSwingTableManager<C, R> newTable() {
        return new TethysSwingTableManager<>(this);
    }
}
