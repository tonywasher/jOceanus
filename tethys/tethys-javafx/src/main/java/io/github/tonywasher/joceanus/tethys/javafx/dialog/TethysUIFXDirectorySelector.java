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

import io.github.tonywasher.joceanus.tethys.core.dialog.TethysUICoreDirectorySelector;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * JavaFX Directory Selector.
 */
public class TethysUIFXDirectorySelector
        extends TethysUICoreDirectorySelector {
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
     *
     * @param pParent the parent
     */
    TethysUIFXDirectorySelector(final Stage pParent) {
        if (pParent == null) {
            throw new IllegalArgumentException("Cannot create Dialog during initialisation");
        }
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
        TethysUIFXDialog.runInFXThread(this::showDialog);
        return theSelectedDir;
    }
}
