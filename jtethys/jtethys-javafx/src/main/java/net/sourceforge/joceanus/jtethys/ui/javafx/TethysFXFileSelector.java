/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2021 Tony Washer
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

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;
import net.sourceforge.joceanus.jtethys.ui.TethysFileSelector;

/**
 * JavaFX File Selector.
 */
public class TethysFXFileSelector
        extends TethysFileSelector {
    /**
     * Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(TethysFXFileSelector.class);

    /**
     * Parent stage.
     */
    private final Stage theStage;

    /**
     * File Chooser.
     */
    private final FileChooser theChooser;

    /**
     * The selected file.
     */
    private File theSelectedFile;

    /**
     * Constructor.
     * @param pParent the parent
     */
    public TethysFXFileSelector(final Stage pParent) {
        theStage = pParent;
        theChooser = new FileChooser();
    }

    /**
     * select the file.
     */
    public void showDialog() {
        /* Initialise selection */
        theSelectedFile = null;

        /* Set values */
        theChooser.setTitle(getTitle());
        theChooser.setInitialDirectory(getInitialDirectory());
        theChooser.setInitialFileName(getInitialFileName());

        /* Set the extension filter list */
        theChooser.getExtensionFilters().clear();
        final String myExt = getExtension();
        if (myExt != null) {
            theChooser.getExtensionFilters().add(new ExtensionFilter("Filter", myExt));
        }

        /* Show the dialog */
        theSelectedFile = useSave()
                                    ? theChooser.showSaveDialog(theStage)
                                    : theChooser.showOpenDialog(theStage);
    }

    @Override
    public File selectFile() {
        /* If this is the event dispatcher thread */
        if (Platform.isFxApplicationThread()) {
            /* invoke the dialog directly */
            showDialog();

            /* else we must use invokeAndWait */
        } else {
            /* Create a FutureTask so that we will wait */
            final FutureTask<Void> myTask = new FutureTask<>(() -> {
                showDialog();
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
        return theSelectedFile;
    }
}
