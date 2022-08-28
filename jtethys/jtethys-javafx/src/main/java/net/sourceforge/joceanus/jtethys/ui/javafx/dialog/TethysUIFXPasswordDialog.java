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
package net.sourceforge.joceanus.jtethys.ui.javafx.dialog;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;
import net.sourceforge.joceanus.jtethys.ui.core.dialog.TethysUICorePasswordDialog;
import net.sourceforge.joceanus.jtethys.ui.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.jtethys.ui.javafx.base.TethysUIFXNode;
import net.sourceforge.joceanus.jtethys.ui.javafx.pane.TethysUIFXBorderPaneManager;

/**
 * Dialog to request a password. Will also ask for password confirmation if required.
 */
public class TethysUIFXPasswordDialog
        extends TethysUICorePasswordDialog {
    /**
     * approximate width.
     */
    private static final int APPROX_WIDTH = 400;

    /**
     * approximate height.
     */
    private static final int APPROX_HEIGHT = 100;

    /**
     * Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(TethysUIFXPasswordDialog.class);

    /**
     * The stage.
     */
    private final Stage theStage;

    /**
     * Constructor.
     *
     * @param pFactory     the GUI Factory
     * @param pStage       the stage
     * @param pTitle       the title
     * @param pNeedConfirm true/false
     */
    TethysUIFXPasswordDialog(final TethysUICoreFactory<?> pFactory,
                             final Stage pStage,
                             final String pTitle,
                             final boolean pNeedConfirm) {
        /* Initialise underlying class */
        super(pFactory, pNeedConfirm);
        final TethysUIFXSceneRegister mySceneRegister = (TethysUIFXSceneRegister) pFactory;
        if (pStage == null) {
            throw new IllegalArgumentException("Cannot create Dialog during initialisation");
        }

        /* Determine title */
        final String myTitle = pNeedConfirm
                ? NLS_TITLENEWPASS + " " + pTitle
                : NLS_TITLEPASS + " " + pTitle;

        /* Initialise the stage */
        theStage = new Stage();
        theStage.initStyle(StageStyle.UTILITY);
        theStage.initModality(Modality.WINDOW_MODAL);
        theStage.initOwner(pStage);
        theStage.setTitle(myTitle);

        /* Create the scene */
        final Scene myScene = new Scene((Region) TethysUIFXNode.getNode(getContainer()));
        theStage.setScene(myScene);

        /* Sort out factory */
        mySceneRegister.registerScene(myScene);
    }

    @Override
    protected TethysUIFXBorderPaneManager getContainer() {
        return (TethysUIFXBorderPaneManager) super.getContainer();
    }

    @Override
    protected void closeDialog() {
        theStage.close();
    }

    @Override
    protected void reSizeDialog() {
        theStage.sizeToScene();
    }

    /**
     * show the dialog.
     */
    private void showTheDialog() {
        /* Centre on parent */
        final Window myParent = theStage.getOwner();
        if (myParent != null) {
            final double myX = (myParent.getWidth() - APPROX_WIDTH) / 2;
            final double myY = (myParent.getHeight() - APPROX_HEIGHT) / 2;
            theStage.setX(myParent.getX() + myX);
            theStage.setY(myParent.getY() + myY);
        }

        /* Show the dialog */
        reSizeDialog();
        theStage.toFront();
        theStage.showAndWait();
    }

    /**
     * Create the dialog under an invokeAndWait clause.
     *
     * @param pFactory     the GUI Factory
     * @param pTitle       the title
     * @param pNeedConfirm true/false
     * @return the new dialog
     */
    static TethysUIFXPasswordDialog createTheDialog(final TethysUICoreFactory<?> pFactory,
                                                    final Stage pStage,
                                                    final String pTitle,
                                                    final boolean pNeedConfirm) {
        /* If this is the event dispatcher thread */
        if (Platform.isFxApplicationThread()) {
            /* invoke the dialog directly */
            return new TethysUIFXPasswordDialog(pFactory, pStage, pTitle, pNeedConfirm);

            /* else we must use invokeAndWait */
        } else {
            /* Create a FutureTask so that we will wait */
            final FutureTask<TethysUIFXPasswordDialog> myTask = new FutureTask<>(() -> new TethysUIFXPasswordDialog(pFactory, pStage, pTitle, pNeedConfirm));

            /* Protect against exceptions */
            try {
                /* Run on Application thread and wait for completion */
                Platform.runLater(myTask);
                return myTask.get();
            } catch (IllegalStateException
                     | ExecutionException e) {
                LOGGER.error("Failed to create dialog", e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return null;
        }
    }

    @Override
    public boolean showDialog() {
        /* If this is the event dispatcher thread */
        if (Platform.isFxApplicationThread()) {
            /* invoke the dialog directly */
            showTheDialog();

            /* else we must use invokeAndWait */
        } else {
            /* Create a FutureTask so that we will wait */
            final FutureTask<Void> myTask = new FutureTask<>(() -> {
                showTheDialog();
                return null;
            });

            /* Protect against exceptions */
            try {
                /* Run on Application thread and wait for completion */
                Platform.runLater(myTask);
                myTask.get();
            } catch (IllegalStateException
                     | ExecutionException e) {
                LOGGER.error("Failed to display dialog", e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        /* Return to caller */
        return isPasswordSet();
    }
}
