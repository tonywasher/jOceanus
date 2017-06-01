/*******************************************************************************
; * jTethys: Java Utilities
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
package net.sourceforge.joceanus.jtethys.ui.javafx;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysDataFormatter;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysProgram;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.TethysValueSet;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataButtonField.TethysFXColorButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataButtonField.TethysFXDateButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataButtonField.TethysFXIconButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataButtonField.TethysFXListButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataButtonField.TethysFXScrollButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataButtonField.TethysFXStateIconButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXCharArrayTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXDilutedPriceTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXDilutionTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXIntegerTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXLongTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXMoneyTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXPriceTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXRateTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXRatioTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXRawDecimalTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXShortTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXStringTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXUnitsTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXIconButtonManager.TethysFXSimpleIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXIconButtonManager.TethysFXStateIconButtonManager;

/**
 * Tethys GUI Factory.
 */
public class TethysFXGuiFactory
        extends TethysGuiFactory<Node, Node>
        implements TethysEventProvider<TethysUIEvent> {
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
     * Event Manager.
     */
    private final TethysEventManager<TethysUIEvent> theEventManager;

    /**
     * Scenes.
     */
    private final List<Scene> theScenes;

    /**
     * Stage.
     */
    private Stage theStage;

    /**
     * Constructor.
     */
    public TethysFXGuiFactory() {
        this(null);
    }

    /**
     * Constructor.
     * @param pApp the program definition
     */
    public TethysFXGuiFactory(final TethysProgram pApp) {
        this(new TethysDataFormatter(), pApp);
    }

    /**
     * Constructor.
     * @param pFormatter the formatter
     * @param pApp the program definition
     */
    public TethysFXGuiFactory(final TethysDataFormatter pFormatter,
                              final TethysProgram pApp) {
        /* Initialise class */
        super(pFormatter, pApp);
        theScenes = new ArrayList<>();

        /* Create event manager */
        theEventManager = new TethysEventManager<>();

        /* Add value listener */
        getValueSet().getEventRegistrar().addEventListener(e -> applyValuesToScenes());
    }

    @Override
    public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Register scene.
     * @param pScene the scene
     */
    public void registerScene(final Scene pScene) {
        /* Configure the scene */
        pScene.getStylesheets().add(CSS_STYLE);
        pScene.getRoot().getStyleClass().add(CSS_STYLE_BASE);

        /* Add the scene to the list */
        theScenes.add(pScene);

        /* Apply the colourSet */
        pScene.getRoot().setStyle(buildStandardColors());
    }

    /**
     * Build standard colours.
     * @return the standard colours
     */
    private String buildStandardColors() {
        /* Allocate a string builder */
        StringBuilder myBuilder = new StringBuilder();

        /* Create the default colour values */
        addValueToBuffer(myBuilder, TethysValueSet.TETHYS_COLOR_STANDARD);
        addValueToBuffer(myBuilder, TethysValueSet.TETHYS_COLOR_ERROR);
        addValueToBuffer(myBuilder, TethysValueSet.TETHYS_COLOR_BACKGROUND);
        addValueToBuffer(myBuilder, TethysValueSet.TETHYS_COLOR_DISABLED);
        addValueToBuffer(myBuilder, TethysValueSet.TETHYS_COLOR_ZEBRA);
        addValueToBuffer(myBuilder, TethysValueSet.TETHYS_COLOR_CHANGED);
        addValueToBuffer(myBuilder, TethysValueSet.TETHYS_COLOR_PROGRESS);

        /* Return the string */
        return myBuilder.toString();
    }

    /**
     * Add value to buffer.
     * @param pBuffer the buffer
     * @param pName the value name
     */
    private void addValueToBuffer(final StringBuilder pBuffer,
                                  final String pName) {
        /* Add the name */
        pBuffer.append(pName);
        pBuffer.append(":");
        pBuffer.append(getValueSet().getValueForKey(pName));
        pBuffer.append(";");
    }

    /**
     * Register scene.
     */
    private void applyValuesToScenes() {
        /* Loop through the scenes */
        String myValues = buildStandardColors();
        Iterator<Scene> myIterator = theScenes.iterator();
        while (myIterator.hasNext()) {
            Scene myScene = myIterator.next();
            myScene.getRoot().setStyle(myValues);
        }
    }

    /**
     * Set the stage.
     * @param pStage the stage
     */
    public void setStage(final Stage pStage) {
        theStage = pStage;
        theEventManager.fireEvent(TethysUIEvent.NEWSTAGE);
    }

    /**
     * Obtain the stage.
     * @return the stage
     */
    public Stage getStage() {
        return theStage;
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
    public TethysFXTextArea newTextArea() {
        return new TethysFXTextArea(this);
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
    public TethysFXPasswordField newPasswordField() {
        return new TethysFXPasswordField(this);
    }

    @Override
    public TethysFXColorPicker newColorPicker() {
        return new TethysFXColorPicker(this);
    }

    @Override
    public TethysFXHTMLManager newHTMLManager() {
        return new TethysFXHTMLManager(this);
    }

    @Override
    public <T> TethysFXTreeManager<T> newTreeManager() {
        return new TethysFXTreeManager<>(this);
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
    public TethysFXMenuBarManager newMenuBar() {
        return new TethysFXMenuBarManager();
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
    public TethysFXScrollPaneManager newScrollPane() {
        return new TethysFXScrollPaneManager(this);
    }

    @Override
    public TethysFXStringTextField newStringField() {
        return new TethysFXStringTextField(this);
    }

    @Override
    public TethysFXCharArrayTextField newCharArrayField() {
        return new TethysFXCharArrayTextField(this);
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
    public TethysFXRawDecimalTextField newRawDecimalField() {
        return new TethysFXRawDecimalTextField(this);
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
    public TethysFXColorButtonField newColorField() {
        return new TethysFXColorButtonField(this);
    }

    @Override
    public <C, R> TethysFXTableManager<C, R> newTable() {
        return new TethysFXTableManager<>(this);
    }

    @Override
    public TethysFXAbout newAboutBox() {
        return new TethysFXAbout(this);
    }
}
