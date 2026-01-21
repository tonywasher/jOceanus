/*
 * Tethys: GUI Utilities
 * Copyright 2012-2026. Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.tonywasher.joceanus.tethys.javafx.dialog;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import io.github.tonywasher.joceanus.tethys.api.base.TethysUIComponent;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIEvent;
import io.github.tonywasher.joceanus.tethys.core.dialog.TethysUICoreChildDialog;
import io.github.tonywasher.joceanus.tethys.core.factory.TethysUICoreFactory;
import io.github.tonywasher.joceanus.tethys.javafx.base.TethysUIFXNode;

/**
 * javaFX Child Dialog.
 */
public class TethysUIFXChildDialog
        extends TethysUICoreChildDialog {
    /**
     * The Stage.
     */
    private final Stage theParent;

    /**
     * The Stage.
     */
    private final Stage theStage;

    /**
     * The Container.
     */
    private final BorderPane theContainer;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     * @param pParent  the parent stage
     */
    TethysUIFXChildDialog(final TethysUICoreFactory<?> pFactory,
                          final Stage pParent) {
        /* Store parameter */
        if (pParent == null) {
            throw new IllegalArgumentException("Cannot create Dialog during initialisation");
        }
        theParent = pParent;

        /* Create the stage */
        theStage = new Stage();

        /* Initialise the dialog */
        theStage.initModality(Modality.NONE);
        theStage.initStyle(StageStyle.DECORATED);

        /* Create the scene */
        theContainer = new BorderPane();
        final Scene myScene = new Scene(theContainer);
        ((TethysUIFXSceneRegister) pFactory).registerScene(myScene);
        theStage.setScene(myScene);

        /* Change visibility of tree when hiding */
        theStage.setOnHiding(e -> handleDialogClosing());
    }

    @Override
    public void setTitle(final String pTitle) {
        theStage.setTitle(pTitle);
    }

    @Override
    public void setContent(final TethysUIComponent pContent) {
        theContainer.setCenter(TethysUIFXNode.getNode(pContent));
    }

    @Override
    public void showDialog() {
        if (!isShowing()) {
            /* Centre on parent */
            if (theParent != null) {
                final double myX = (theParent.getWidth() - theContainer.getPrefWidth()) / 2;
                final double myY = (theParent.getHeight() - theContainer.getPrefHeight()) / 2;
                theStage.setX(theParent.getX() + myX);
                theStage.setY(theParent.getY() + myY);
            }

            /* Set the tree as visible */
            theContainer.setVisible(true);

            /* Show the dialog */
            theStage.show();
        }
    }

    @Override
    public boolean isShowing() {
        return theStage.isShowing();
    }

    @Override
    public void hideDialog() {
        /* If the dialog is visible */
        if (isShowing()) {
            /* hide it */
            theStage.hide();
        }
    }

    @Override
    public void closeDialog() {
        /* close the dialog */
        theStage.close();
    }

    /**
     * Handle dialog closing.
     */
    private void handleDialogClosing() {
        theContainer.setVisible(false);
        fireEvent(TethysUIEvent.WINDOWCLOSED, null);
    }
}
