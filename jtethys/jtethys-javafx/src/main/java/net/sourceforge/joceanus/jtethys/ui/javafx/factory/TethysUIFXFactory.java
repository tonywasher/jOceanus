/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.javafx.factory;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import net.sourceforge.joceanus.jtethys.ui.TethysValueSet;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIIcon;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIIconId;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIProgram;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIButtonFactory;
import net.sourceforge.joceanus.jtethys.ui.api.chart.TethysUIChartFactory;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControlFactory;
import net.sourceforge.joceanus.jtethys.ui.api.dialog.TethysUIDialogFactory;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIFieldFactory;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIMenuFactory;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIPaneFactory;
import net.sourceforge.joceanus.jtethys.ui.api.table.TethysUITableManager;
import net.sourceforge.joceanus.jtethys.ui.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.base.TethysUIFXUtils;
import net.sourceforge.joceanus.jtethys.ui.javafx.button.TethysUIFXButtonFactory;
import net.sourceforge.joceanus.jtethys.ui.javafx.chart.TethysUIFXChartFactory;
import net.sourceforge.joceanus.jtethys.ui.javafx.control.TethysUIFXControlFactory;
import net.sourceforge.joceanus.jtethys.ui.javafx.dialog.TethysUIFXDialogFactory;
import net.sourceforge.joceanus.jtethys.ui.javafx.dialog.TethysUIFXSceneRegister;
import net.sourceforge.joceanus.jtethys.ui.javafx.field.TethysUIFXFieldFactory;
import net.sourceforge.joceanus.jtethys.ui.javafx.menu.TethysUIFXMenuFactory;
import net.sourceforge.joceanus.jtethys.ui.javafx.pane.TethysUIFXPaneFactory;
import net.sourceforge.joceanus.jtethys.ui.javafx.table.TethysUIFXTableManager;

/**
 * javafx Factory.
 */
public class TethysUIFXFactory
        extends TethysUICoreFactory<Color>
        implements TethysUIFXSceneRegister {
    /**
     * StyleSheet Name.
     */
    private static final String CSS_STYLE_NAME = "jtethys-javafx-gui.css";

    /**
     * PreLoad StyleSheet.
     */
    private static final String CSS_STYLE = TethysUIFXFactory.class.getResource(CSS_STYLE_NAME).toExternalForm();

    /**
     * The pane factory.
     */
    private final TethysUIFXPaneFactory thePaneFactory;

    /**
     * The button factory.
     */
    private final TethysUIFXButtonFactory theButtonFactory;

    /**
     * The chart factory.
     */
    private final TethysUIFXChartFactory theChartFactory;

    /**
     * The control factory.
     */
    private final TethysUIFXControlFactory theControlFactory;

    /**
     * The dialog factory.
     */
    private final TethysUIFXDialogFactory theDialogFactory;

    /**
     * The field factory.
     */
    private final TethysUIFXFieldFactory theFieldFactory;

    /**
     * The menu factory.
     */
    private final TethysUIFXMenuFactory theMenuFactory;

    /**
     * Scenes.
     */
    private final List<Scene> theScenes;

    /**
     * Constructor.
     * @param pProgram the program definitions
     */
    TethysUIFXFactory(final TethysUIProgram pProgram) {
        super(pProgram);
        thePaneFactory = new TethysUIFXPaneFactory(this);
        theButtonFactory = new TethysUIFXButtonFactory(this);
        theChartFactory = new TethysUIFXChartFactory(this);
        theControlFactory = new TethysUIFXControlFactory(this);
        theDialogFactory = new TethysUIFXDialogFactory(this);
        theFieldFactory = new TethysUIFXFieldFactory(this);
        theMenuFactory = new TethysUIFXMenuFactory(this);

        /* Handle scenes */
        theScenes = new ArrayList<>();
        getValueSet().getEventRegistrar().addEventListener(e -> applyValuesToScenes());
    }

    @Override
    public TethysUIPaneFactory paneFactory() {
        return thePaneFactory;
    }

    @Override
    public TethysUIButtonFactory buttonFactory() {
        return theButtonFactory;
    }

    @Override
    public TethysUIChartFactory chartFactory() {
        return theChartFactory;
    }

    @Override
    public TethysUIControlFactory controlFactory() {
        return theControlFactory;
    }

    @Override
    public TethysUIDialogFactory<Color> dialogFactory() {
        return theDialogFactory;
    }

    @Override
    public TethysUIFieldFactory fieldFactory() {
        return theFieldFactory;
    }

    @Override
    public TethysUIMenuFactory menuFactory() {
        return theMenuFactory;
    }

    @Override
    public TethysUIIcon resolveIcon(final TethysUIIconId pIconId,
                                    final int pWidth) {
        return pIconId == null
                ? null
                : TethysUIFXUtils.getIconAtSize(pIconId, pWidth);
    }

    /**
     * Set the stage.
     * @param pStage the stage
     */
    public void setStage(final Stage pStage) {
        theControlFactory.setStage(pStage);
        theDialogFactory.setStage(pStage);
        fireEvent(TethysUIEvent.NEWSTAGE);
    }

    @Override
    public void registerScene(final Scene pScene) {
        /* Configure the scene */
        pScene.getStylesheets().add(CSS_STYLE);
        pScene.getRoot().getStyleClass().add(TethysUIFXUtils.CSS_STYLE_BASE);

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
        final StringBuilder myBuilder = new StringBuilder();

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

    @Override
    public <C, R> TethysUITableManager<C, R> newTable() {
        return new TethysUIFXTableManager<>(this);
    }

    /**
     * Add value to buffer.
     * @param pBuffer the buffer
     * @param pName the value name
     */
    private void addValueToBuffer(final StringBuilder pBuffer,
                                  final String pName) {
        /* Add the name */
        pBuffer.append(pName).append(':').append(getValueSet().getValueForKey(pName)).append(';');
    }

    /**
     * Register scene.
     */
    private void applyValuesToScenes() {
        /* Loop through the scenes */
        final String myValues = buildStandardColors();
        for (Scene myScene : theScenes) {
            myScene.getRoot().setStyle(myValues);
        }
    }
}
