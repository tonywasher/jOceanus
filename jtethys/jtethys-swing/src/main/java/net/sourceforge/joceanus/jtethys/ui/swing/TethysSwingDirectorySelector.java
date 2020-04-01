/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2020 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.swing;

import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;
import net.sourceforge.joceanus.jtethys.ui.TethysDirectorySelector;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

/**
 * Swing Directory Selector.
 */
public class TethysSwingDirectorySelector
        extends TethysDirectorySelector {
    /**
     * Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(TethysSwingDirectorySelector.class);

    /**
     * Parent stage.
     */
    private final Component theParent;

    /**
     * Directory Chooser.
     */
    private final JFileChooser theChooser;

    /**
     * The selected directory.
     */
    private File theSelectedDir;

    /**
     * Constructor.
     * @param pParent the parent
     */
    public TethysSwingDirectorySelector(final Component pParent) {
        theParent = pParent;
        theChooser = new JFileChooser();
        theChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    }

    /**
     * select the file.
     */
    public void showDialog() {
        /* Initialise selection */
        theSelectedDir = null;

        /* Set values */
        theChooser.setDialogTitle(getTitle());
        theChooser.setCurrentDirectory(getInitialDirectory());

        /* Show the dialog */
        final int myResult = theChooser.showOpenDialog(theParent);

        /* If we selected a directory */
        if (myResult == JFileChooser.APPROVE_OPTION) {
            theSelectedDir = theChooser.getSelectedFile();
        }
    }

    @Override
    public File selectDirectory() {
        /* If this is the event dispatcher thread */
        if (SwingUtilities.isEventDispatchThread()) {
            /* invoke the dialog directly */
            showDialog();

            /* else we must use invokeAndWait */
        } else {
            try {
                SwingUtilities.invokeAndWait(this::showDialog);
            } catch (InvocationTargetException e) {
                LOGGER.error("Failed to display dialog", e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        /* Return to caller */
        return theSelectedDir;
    }
}
