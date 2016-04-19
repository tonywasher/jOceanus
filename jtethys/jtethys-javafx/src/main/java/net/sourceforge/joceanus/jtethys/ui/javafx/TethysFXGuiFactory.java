/*******************************************************************************
; * jTethys: Java Utilities
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

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.sourceforge.joceanus.jtethys.ui.TethysDataFormatter;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataButtonField.TethysFXDateButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataButtonField.TethysFXIconButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataButtonField.TethysFXListButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataButtonField.TethysFXScrollButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataButtonField.TethysFXStateIconButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXDilutedPriceTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXDilutionTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXIntegerTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXLongTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXMoneyTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXPriceTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXRateTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXRatioTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXShortTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXStringTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXUnitsTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXIconButtonManager.TethysFXSimpleIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXIconButtonManager.TethysFXStateIconButtonManager;

/**
 * Tethys GUI Factory.
 */
public class TethysFXGuiFactory
        extends TethysGuiFactory<Node, Node> {
    /**
     * Base StyleSheet Class.
     */
    protected static final String CSS_STYLE_BASE = "-jtethys";

    /**
     * StyleSheet Name.
     */
    private static final String CSS_STYLE_NAME = "jtethys-javafx-gui.css";

    /**
     * PreLoad StyleSheet.
     */
    private static final String CSS_STYLE = TethysFXGuiFactory.class.getResource(CSS_STYLE_NAME).toExternalForm();

    /**
     * Stage.
     */
    private Stage theStage;

    /**
     * Constructor.
     */
    public TethysFXGuiFactory() {
        this(new TethysDataFormatter());
    }

    /**
     * Constructor.
     * @param pFormatter the formatter
     */
    public TethysFXGuiFactory(final TethysDataFormatter pFormatter) {
        super(pFormatter);
    }

    /**
     * Apply stylesheets.
     * @param pScene the scene
     */
    public void applyStyleSheets(final Scene pScene) {
        pScene.getStylesheets().add(CSS_STYLE);
        pScene.getRoot().getStyleClass().add(CSS_STYLE_BASE);
    }

    /**
     * Set the stage.
     * @param pStage the stage
     */
    public void setStage(final Stage pStage) {
        theStage = pStage;
    }

    @Override
    public TethysFXLabel newLabel() {
        return new TethysFXLabel(this);
    }

    @Override
    public TethysFXLabel newLabel(final String pText) {
        TethysFXLabel myLabel = newLabel();
        myLabel.setText(pText);
        return myLabel;
    }

    @Override
    public TethysFXCheckBox newCheckBox() {
        return new TethysFXCheckBox(this);
    }

    @Override
    public TethysFXCheckBox newCheckBox(final String pText) {
        TethysFXCheckBox myBox = newCheckBox();
        myBox.setText(pText);
        return myBox;
    }

    @Override
    public TethysFXButton newButton() {
        return new TethysFXButton(this);
    }

    @Override
    public <T> TethysFXScrollContextMenu<T> newContextMenu() {
        return new TethysFXScrollContextMenu<>();
    }

    @Override
    public TethysFXDateButtonManager newDateButton() {
        return new TethysFXDateButtonManager(this);
    }

    @Override
    public <T> TethysFXScrollButtonManager<T> newScrollButton() {
        return new TethysFXScrollButtonManager<>(this);
    }

    @Override
    public <T> TethysFXListButtonManager<T> newListButton() {
        return new TethysFXListButtonManager<>(this);
    }

    @Override
    public <T> TethysFXSimpleIconButtonManager<T> newSimpleIconButton() {
        return new TethysFXSimpleIconButtonManager<>(this);
    }

    @Override
    public <T, S> TethysFXStateIconButtonManager<T, S> newStateIconButton() {
        return new TethysFXStateIconButtonManager<>(this);
    }

    @Override
    public TethysFXDateRangeSelector newDateRangeSelector() {
        return newDateRangeSelector(false);
    }

    @Override
    public TethysFXDateRangeSelector newDateRangeSelector(final boolean pBaseIsStart) {
        return new TethysFXDateRangeSelector(this, pBaseIsStart);
    }

    @Override
    public TethysFXHTMLManager newHTMLManager() {
        return new TethysFXHTMLManager(this);
    }

    @Override
    public <T> TethysFXTreeManager<T> newTreeManager() {
        return new TethysFXTreeManager<>();
    }

    @Override
    public <T> TethysFXSplitTreeManager<T> newSplitTreeManager() {
        return new TethysFXSplitTreeManager<>(this);
    }

    @Override
    public TethysFXFileSelector newFileSelector() {
        return new TethysFXFileSelector(theStage);
    }

    @Override
    public TethysFXDirectorySelector newDirectorySelector() {
        return new TethysFXDirectorySelector(theStage);
    }

    @Override
    public TethysFXProgressBar newProgressBar() {
        return new TethysFXProgressBar(this);
    }

    @Override
    public TethysFXSlider newSlider() {
        return new TethysFXSlider(this);
    }

    @Override
    public TethysFXTabPaneManager newTabPane() {
        return new TethysFXTabPaneManager(this);
    }

    @Override
    public <P extends TethysNode<Node>> TethysFXCardPaneManager<P> newCardPane() {
        return new TethysFXCardPaneManager<>(this);
    }

    @Override
    public TethysFXBorderPaneManager newBorderPane() {
        return new TethysFXBorderPaneManager(this);
    }

    @Override
    public TethysFXFlowPaneManager newFlowPane() {
        return new TethysFXFlowPaneManager(this);
    }

    @Override
    public TethysFXBoxPaneManager newVBoxPane() {
        return new TethysFXBoxPaneManager(this, false);
    }

    @Override
    public TethysFXBoxPaneManager newHBoxPane() {
        return new TethysFXBoxPaneManager(this, true);
    }

    @Override
    public TethysFXGridPaneManager newGridPane() {
        return new TethysFXGridPaneManager(this);
    }

    @Override
    public TethysFXStringTextField newStringField() {
        return new TethysFXStringTextField(this);
    }

    @Override
    public TethysFXShortTextField newShortField() {
        return new TethysFXShortTextField(this);
    }

    @Override
    public TethysFXIntegerTextField newIntegerField() {
        return new TethysFXIntegerTextField(this);
    }

    @Override
    public TethysFXLongTextField newLongField() {
        return new TethysFXLongTextField(this);
    }

    @Override
    public TethysFXMoneyTextField newMoneyField() {
        return new TethysFXMoneyTextField(this);
    }

    @Override
    public TethysFXPriceTextField newPriceField() {
        return new TethysFXPriceTextField(this);
    }

    @Override
    public TethysFXDilutedPriceTextField newDilutedPriceField() {
        return new TethysFXDilutedPriceTextField(this);
    }

    @Override
    public TethysFXRateTextField newRateField() {
        return new TethysFXRateTextField(this);
    }

    @Override
    public TethysFXUnitsTextField newUnitsField() {
        return new TethysFXUnitsTextField(this);
    }

    @Override
    public TethysFXDilutionTextField newDilutionField() {
        return new TethysFXDilutionTextField(this);
    }

    @Override
    public TethysFXRatioTextField newRatioField() {
        return new TethysFXRatioTextField(this);
    }

    @Override
    public TethysFXDateButtonField newDateField() {
        return new TethysFXDateButtonField(this);
    }

    @Override
    public <T> TethysFXScrollButtonField<T> newScrollField() {
        return new TethysFXScrollButtonField<>(this);
    }

    @Override
    public <T> TethysFXListButtonField<T> newListField() {
        return new TethysFXListButtonField<>(this);
    }

    @Override
    public <T> TethysFXIconButtonField<T> newSimpleIconField() {
        return new TethysFXIconButtonField<>(this);
    }

    @Override
    public <T, S> TethysFXStateIconButtonField<T, S> newStateIconField() {
        return new TethysFXStateIconButtonField<>(this);
    }

    @Override
    public <C, R> TethysFXTableManager<C, R> newTable() {
        return new TethysFXTableManager<>(this);
    }

}