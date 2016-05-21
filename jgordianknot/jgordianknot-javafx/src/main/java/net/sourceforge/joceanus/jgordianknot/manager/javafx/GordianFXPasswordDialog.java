/*******************************************************************************
 * jGordianKnot: Security Suite
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
package net.sourceforge.joceanus.jgordianknot.manager.javafx;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.sourceforge.joceanus.jgordianknot.manager.GordianPasswordDialog;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXGuiFactory;

/**
 * Dialog to request a password. Will also ask for password confirmation if required.
 */
public class GordianFXPasswordDialog
        extends GordianPasswordDialog<Node, Node> {
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
    private static final Logger LOGGER = LoggerFactory.getLogger(GordianFXPasswordDialog.class);

    /**
     * The stage.
     */
    private final Stage theStage;

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     * @param pTitle the title
     * @param pNeedConfirm true/false
     */
    protected GordianFXPasswordDialog(final TethysFXGuiFactory pFactory,
                                      final String pTitle,
                                      final boolean pNeedConfirm) {
        /* Initialise underlying class */
        super(pFactory, pNeedConfirm);

        /* Initialise the stage */
        theStage = new Stage();
        theStage.initModality(Modality.WINDOW_MODAL);
        theStage.initOwner(pFactory.getStage());
        theStage.setTitle(pTitle);

        /* Create the scene */
        Scene myScene = new Scene(getContainer().getNode());
        theStage.setScene(myScene);

        /* Sort out factory */
        pFactory.registerScene(myScene);
    }

    @Override
    protected TethysFXGuiFactory getFactory() {
        return (TethysFXGuiFactory) super.getFactory();
    }

    @Override
    protected TethysFXBorderPaneManager getContainer() {
        return (TethysFXBorderPaneManager) super.getContainer();
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
    public void showDialog() {
        /* Centre on parent */
        Window myParent = theStage.getOwner();
        if (myParent != null) {
            double myX = (myParent.getWidth() - APPROX_WIDTH) / 2;
            double myY = (myParent.getHeight() - APPROX_HEIGHT) / 2;
            theStage.setX(myParent.getX() + myX);
            theStage.setY(myParent.getY() + myY);
        }

        /* Show the dialog */
        theStage.showAndWait();
    }

    /**
     * Show the dialog under an invokeAndWait clause.
     * @param pDialog the dialog to show
     * @return successful dialog usage true/false
     */
    protected static boolean showTheDialog(final GordianFXPasswordDialog pDialog) {
        /* If this is the event dispatcher thread */
        if (Platform.isFxApplicationThread()) {
            /* invoke the dialog directly */
            pDialog.showDialog();

            /* else we must use invokeAndWait */
        } else {
            /* Create a FutureTask so that we will wait */
            FutureTask<Void> myTask = new FutureTask<>(() -> {
                pDialog.showDialog();
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
        return pDialog.isPasswordSet();
    }
}
