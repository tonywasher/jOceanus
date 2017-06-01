/*******************************************************************************
 * jTethys: Java Utilities
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.javafx;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import net.sourceforge.joceanus.jtethys.ui.TethysDirectorySelector;

/**
 * JavaFX Directory Selector.
 */
public class TethysFXDirectorySelector
        extends TethysDirectorySelector {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TethysFXDirectorySelector.class);

    /**
     * Parent stage.
     */
    private final Stage theStage;

    /**
     * Directory Chooser.
     */
    private final DirectoryChooser theChooser;

    /**
     * The selected directory.
     */
    private File theSelectedDir;

    /**
     * Constructor.
     * @param pParent the parent
     */
    public TethysFXDirectorySelector(final Stage pParent) {
        theStage = pParent;
        theChooser = new DirectoryChooser();
    }

    /**
     * select the file.
     */
    public void showDialog() {
        /* Initialise selection */
        theSelectedDir = null;

        /* Set values */
        theChooser.setTitle(getTitle());
        theChooser.setInitialDirectory(getInitialDirectory());

        /* Show the dialog */
        theSelectedDir = theChooser.showDialog(theStage);
    }

    @Override
    public File selectDirectory() {
        /* If this is the event dispatcher thread */
        if (Platform.isFxApplicationThread()) {
            /* invoke the dialog directly */
            showDialog();

            /* else we must use invokeAndWait */
        } else {
            /* Create a FutureTask so that we will wait */
            FutureTask<Void> myTask = new FutureTask<>(() -> {
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
        return theSelectedDir;
    }
}
