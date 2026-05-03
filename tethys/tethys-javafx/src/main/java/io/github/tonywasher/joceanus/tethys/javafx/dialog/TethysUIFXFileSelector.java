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

import io.github.tonywasher.joceanus.tethys.core.dialog.TethysUICoreFileSelector;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import java.io.File;

/**
 * JavaFX File Selector.
 */
public class TethysUIFXFileSelector
        extends TethysUICoreFileSelector {
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
     *
     * @param pParent the parent
     */
    TethysUIFXFileSelector(final Stage pParent) {
        if (pParent == null) {
            throw new IllegalArgumentException("Cannot create Dialog during initialisation");
        }
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
        TethysUIFXDialog.runInFXThread(this::showDialog);
        return theSelectedFile;
    }
}
