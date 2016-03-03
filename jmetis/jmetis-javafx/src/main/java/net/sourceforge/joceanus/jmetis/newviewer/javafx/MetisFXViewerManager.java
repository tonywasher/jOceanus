/*******************************************************************************
 * jMetis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.newviewer.javafx;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.sourceforge.joceanus.jmetis.newviewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmetis.newviewer.MetisViewerManager;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXHTMLManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXSplitTreeManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTreeManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTreeManager.TethysFXTreeItem;

/**
 * JavaFX Data Viewer Manager.
 */
public class MetisFXViewerManager
        extends MetisViewerManager<MetisFXViewerEntry, Node> {
    /**
     * The stage.
     */
    private Stage theStage;

    /**
     * The help dialog.
     */
    private ViewerDialog theDialog;

    /**
     * Constructor.
     */
    public MetisFXViewerManager() {
        /* Initialise underlying class */
        super(new TethysFXSplitTreeManager<>());
    }

    @Override
    public TethysFXSplitTreeManager<MetisFXViewerEntry> getSplitTreeManager() {
        return (TethysFXSplitTreeManager<MetisFXViewerEntry>) super.getSplitTreeManager();
    }

    @Override
    public TethysFXTreeManager<MetisFXViewerEntry> getTreeManager() {
        return (TethysFXTreeManager<MetisFXViewerEntry>) super.getTreeManager();
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

    @Override
    public MetisFXViewerEntry newEntry(final String pName) throws OceanusException {
        /* Create the entry */
        MetisFXViewerEntry myEntry = new MetisFXViewerEntry(this, pName);

        /* Define and set the tree entry */
        TethysFXTreeManager<MetisFXViewerEntry> myManager = getTreeManager();
        TethysFXTreeItem<MetisFXViewerEntry> myTreeItem = new TethysFXTreeItem<>(myManager,
                myManager.getRoot(), pName, myEntry);
        myEntry.setTreeItem(myTreeItem);

        /* Return the new entry */
        return myEntry;
    }

    @Override
    public MetisFXViewerEntry newEntry(final MetisViewerEntry<MetisFXViewerEntry, Node> pParent,
                                       final String pName) throws OceanusException {
        /* Access parent and create the entry */
        MetisFXViewerEntry myParent = MetisFXViewerEntry.class.cast(pParent);
        MetisFXViewerEntry myEntry = new MetisFXViewerEntry(this, pName);

        /* Build the new name */
        StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(myParent.getName());
        myBuilder.append('.');
        myBuilder.append(pName);
        String myName = myBuilder.toString();

        /* Define and set the tree entry */
        TethysFXTreeItem<MetisFXViewerEntry> myTreeItem = new TethysFXTreeItem<>(getTreeManager(),
                myParent.getTreeItem(), myName, myEntry);
        myEntry.setTreeItem(myTreeItem);

        /* Return the new entry */
        return myEntry;
    }

    /**
     * Dialog class.
     */
    private final class ViewerDialog
            extends Stage {
        /**
         * Constructor.
         */
        private ViewerDialog() {
            /* Set the title */
            setTitle("Data Manager");

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
