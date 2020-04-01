/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2020 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jmetis.viewer.javafx;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerResource;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerWindow;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXHTMLManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXNode;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXSplitTreeManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTreeManager;

/**
 * JavaFX Data Viewer Manager.
 */
public class MetisFXViewerWindow
        extends MetisViewerWindow {
    /**
     * The help dialog.
     */
    private ViewerDialog theDialog;

    /**
     * Constructor.
     *
     * @param pFactory     the GUI factory
     * @param pDataManager the viewer data manager
     * @throws OceanusException on error
     */
    public MetisFXViewerWindow(final TethysFXGuiFactory pFactory,
                               final MetisViewerManager pDataManager) throws OceanusException {
        /* Initialise underlying class */
        super(pFactory, pDataManager);
    }

    @Override
    public TethysFXSplitTreeManager<MetisViewerEntry> getSplitTreeManager() {
        return (TethysFXSplitTreeManager<MetisViewerEntry>) super.getSplitTreeManager();
    }

    @Override
    public TethysFXTreeManager<MetisViewerEntry> getTreeManager() {
        return (TethysFXTreeManager<MetisViewerEntry>) super.getTreeManager();
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
            theDialog = new ViewerDialog();
        }

        /* Show it */
        theDialog.showDialog();
    }

    @Override
    public void hideDialog() {
        if (theDialog != null) {
            theDialog.hideDialog();
        }
    }

    @Override
    public void closeWindow() {
        if (theDialog != null) {
            theDialog.closeDialog();
        }
    }

    /**
     * Dialog class.
     */
    private final class ViewerDialog {
        /**
         * The Stage.
         */
        private Stage theStage;

        /**
         * Constructor.
         */
        private ViewerDialog() {
            /* Create the stage */
            theStage = new Stage();

            /* Set the title */
            theStage.setTitle(MetisViewerResource.VIEWER_TITLE.getValue());

            /* Initialise the dialog */
            theStage.initModality(Modality.NONE);
            theStage.initStyle(StageStyle.DECORATED);

            /* Create the scene */
            final BorderPane myContainer = new BorderPane();
            myContainer.setCenter(TethysFXNode.getNode(getSplitTreeManager()));
            final Scene myScene = new Scene(myContainer);
            theStage.setScene(myScene);

            /* Change visibility of tree when hiding */
            theStage.setOnHiding(e -> handleDialogClosing());
        }

        /**
         * show the dialog.
         */
        private void showDialog() {
            if (!theStage.isShowing()) {
                /* Centre on parent */
                final Window myParent = theStage.getOwner();
                if (myParent != null) {
                    final double myX = (myParent.getWidth() - WINDOW_WIDTH) / 2;
                    final double myY = (myParent.getHeight() - WINDOW_HEIGHT) / 2;
                    theStage.setX(myParent.getX() + myX);
                    theStage.setY(myParent.getY() + myY);
                }

                /* Set the tree as visible */
                getTreeManager().setVisible(true);

                /* Show the dialog */
                theStage.show();
            }
        }

        /**
         * Hide the dialog.
         */
        public void hideDialog() {
            /* If the dialog is visible */
            if (theStage.isShowing()) {
                /* hide it */
                theStage.hide();

                /* Terminate the tree */
                terminateTree();
            }
        }

        /**
         * Close the dialog.
         */
        public void closeDialog() {
            /* close the dialog */
            theStage.close();
        }

        /**
         * Handle dialog closing.
         */
        private void handleDialogClosing() {
            getTreeManager().setVisible(false);
            fireEvent(TethysUIEvent.WINDOWCLOSED, null);
        }
    }
}
