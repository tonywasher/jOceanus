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
package net.sourceforge.joceanus.jtethys.help.javafx;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.sourceforge.joceanus.jtethys.help.TethysHelpEntry;
import net.sourceforge.joceanus.jtethys.help.TethysHelpResource;
import net.sourceforge.joceanus.jtethys.help.TethysHelpWindow;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXHTMLManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXSplitTreeManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTreeManager;

/**
 * Help Window class, responsible for displaying the help.
 */
public class TethysFXHelpWindow
        extends TethysHelpWindow<Node, Node> {
    /**
     * The stage.
     */
    private Stage theStage;

    /**
     * The help dialog.
     */
    private HelpDialog theDialog;

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     */
    public TethysFXHelpWindow(final TethysFXGuiFactory pFactory) {
        /* Initialise underlying class */
        super(pFactory);
    }

    @Override
    public TethysFXSplitTreeManager<TethysHelpEntry> getSplitTreeManager() {
        return (TethysFXSplitTreeManager<TethysHelpEntry>) super.getSplitTreeManager();
    }

    @Override
    public TethysFXTreeManager<TethysHelpEntry> getTreeManager() {
        return (TethysFXTreeManager<TethysHelpEntry>) super.getTreeManager();
    }

    @Override
    public TethysFXHTMLManager getHTMLManager() {
        return (TethysFXHTMLManager) super.getHTMLManager();
    }

    @Override
    public void showDialog() {
        /* If the dialog does not exist */
        if (theDialog == null) {
            /* Create a new dialog */
            theDialog = new HelpDialog();

            /* Change visibility of tree when hiding */
            theDialog.setOnHiding(e -> handleDialogClosing());
        }

        /* If the dialog is not visible */
        if (!theDialog.isShowing()) {
            /* Show it */
            theDialog.showDialog();
        }
    }

    /**
     * Handle dialog closing.
     */
    private void handleDialogClosing() {
        getTreeManager().setVisible(false);
        fireEvent(TethysUIEvent.WINDOWCLOSED, null);
    }

    @Override
    public void hideDialog() {
        /* If the dialog is visible */
        if (theDialog.isShowing()) {
            /* hide it */
            theDialog.hide();
        }
    }

    /**
     * Set the stage.
     * @param pStage the stage
     */
    public void setStage(final Stage pStage) {
        theStage = pStage;
    }

    /**
     * Dialog class.
     */
    private final class HelpDialog
            extends Stage {
        /**
         * Constructor.
         */
        private HelpDialog() {
            /* Set the title */
            setTitle(TethysHelpResource.TITLE.getValue());

            /* Initialise the dialog */
            initModality(Modality.NONE);
            initOwner(theStage);

            /* Create the scene */
            BorderPane myContainer = new BorderPane();
            myContainer.setCenter(getSplitTreeManager().getNode());
            Scene myScene = new Scene(myContainer);
            setScene(myScene);
        }

        /**
         * show the dialog.
         */
        private void showDialog() {
            /* Centre on parent */
            Window myParent = getOwner();
            if (myParent != null) {
                double myX = (myParent.getWidth() - WINDOW_WIDTH) / 2;
                double myY = (myParent.getHeight() - WINDOW_HEIGHT) / 2;
                setX(myParent.getX() + myX);
                setY(myParent.getY() + myY);
            }

            /* Set the tree as visible */
            getTreeManager().setVisible(true);

            /* Show the dialog */
            show();
        }
    }
}
