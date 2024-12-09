/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.tethys.javafx.dialog;

import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.sourceforge.joceanus.tethys.core.dialog.TethysUICoreAboutBox;
import net.sourceforge.joceanus.tethys.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.tethys.javafx.base.TethysUIFXNode;

/**
 * javaFX About Box.
 */
public class TethysUIFXAboutBox
        extends TethysUICoreAboutBox {
    /**
     * The Scene register.
     */
    private final TethysUIFXSceneRegister theSceneRegister;

    /**
     * The underlying stage.
     */
    private final Stage theStage;

    /**
     * The dialog panel.
     */
    private final Region thePanel;

    /**
     * The dialog stage.
     */
    private Stage theDialog;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pStage the stage
     */
    TethysUIFXAboutBox(final TethysUICoreFactory<?> pFactory,
                       final Stage pStage) {
        /* Initialise underlying class */
        super(pFactory);
        if (pStage == null) {
            throw new IllegalArgumentException("Cannot create Dialog during initialisation");
        }

        /* Store parameters */
        theSceneRegister = (TethysUIFXSceneRegister) pFactory;
        theStage = pStage;
        thePanel = (Region) getNode().getNode();
    }

    @Override
    public TethysUIFXNode getNode() {
        return (TethysUIFXNode) super.getNode();
    }

    @Override
    public void showDialog() {
        /* If we have not made the dialog yet */
        if (theDialog == null) {
            makeDialog();
        }

        /* Show the dialog */
        theDialog.show();
    }

    /**
     * Make the dialog.
     */
    private void makeDialog() {
        /* Create the dialog */
        theDialog = new Stage(StageStyle.UNDECORATED);
        theDialog.initOwner(theStage);
        theDialog.initModality(Modality.WINDOW_MODAL);

        /* Define style of Box */
        thePanel.getStyleClass().add("-jtethys-about");

        /* Create the scene and attach it */
        final Scene myScene = new Scene(thePanel);
        theSceneRegister.registerScene(myScene);
        theDialog.setScene(myScene);
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        thePanel.setPrefWidth(pWidth);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        thePanel.setPrefHeight(pHeight);
    }

    @Override
    protected void closeDialog() {
        theDialog.close();
    }
}
