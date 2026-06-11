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
package io.github.tonywasher.joceanus.tethys.swing.dialog;

import io.github.tonywasher.joceanus.tethys.core.dialog.TethysUICoreFileSelector;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Component;
import java.io.File;
import java.util.List;

/**
 * Swing File Selector.
 */
public class TethysUISwingFileSelector
        extends TethysUICoreFileSelector {
    /**
     * Parent frame.
     */
    private final Component theParent;

    /**
     * File Chooser.
     */
    private final JFileChooser theChooser;

    /**
     * The selected file.
     */
    private File theSelectedFile;

    /**
     * Constructor.
     *
     * @param pParent the parent
     */
    TethysUISwingFileSelector(final Component pParent) {
        if (pParent == null) {
            throw new IllegalArgumentException("Cannot create Dialog during initialisation");
        }
        theParent = pParent;
        theChooser = new JFileChooser();
        theChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    }

    /**
     * select the file.
     */
    public void showDialog() {
        /* Initialise selection */
        theSelectedFile = null;

        /* Set values */
        theChooser.setDialogTitle(getTitle());
        theChooser.setCurrentDirectory(getInitialDirectory());
        final String myName = getInitialFileName();
        if (myName != null) {
            final File myFile = new File(theChooser.getCurrentDirectory(), myName);
            theChooser.setSelectedFile(myFile);
        }

        /* Set the extension filter list */
        theChooser.resetChoosableFileFilters();
        final List<String> myExt = getExtensions();
        if (!myExt.isEmpty()) {
            final String[] myExtArray = myExt.toArray(new String[0]);
            theChooser.setFileFilter(new FileNameExtensionFilter(getFilterName(), myExtArray));
        }

        /* Show the dialog */
        final int myResult = useSave()
                ? theChooser.showSaveDialog(theParent)
                : theChooser.showOpenDialog(theParent);

        /* If we selected a file */
        if (myResult == JFileChooser.APPROVE_OPTION) {
            theSelectedFile = theChooser.getSelectedFile();
        }
    }

    @Override
    public File selectFile() {
        TethysUISwingDialog.runInSwingThread(this::showDialog);
        return theSelectedFile;
    }
}
