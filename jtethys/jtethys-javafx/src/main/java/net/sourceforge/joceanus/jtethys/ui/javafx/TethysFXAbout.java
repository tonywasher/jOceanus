/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.javafx;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.sourceforge.joceanus.jtethys.ui.TethysAbout;

/**
 * javaFX About Box.
 */
public class TethysFXAbout
        extends TethysAbout<Node, Node> {
    /**
     * The GuiFactory.
     */
    private final TethysFXGuiFactory theGuiFactory;

    /**
     * The dialog stage.
     */
    private Stage theDialog;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    public TethysFXAbout(final TethysFXGuiFactory pFactory) {
        /* Initialise underlying class */
        super(pFactory);

        /* Store parameters */
        theGuiFactory = pFactory;
    }

    @Override
    public Region getNode() {
        return (Region) super.getNode();
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
        theDialog.initOwner(theGuiFactory.getStage());
        theDialog.initModality(Modality.WINDOW_MODAL);

        /* Define style of Box */
        final Region myPanel = getNode();
        myPanel.getStyleClass().add("-jtethys-about");

        /* Create the scene and attach it */
        final Scene myScene = new Scene(myPanel);
        theGuiFactory.registerScene(myScene);
        theDialog.setScene(myScene);
    }

    @Override
    protected void closeDialog() {
        theDialog.close();
    }
}
