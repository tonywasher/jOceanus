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
package net.sourceforge.joceanus.jtethys.ui.swing;

import java.awt.Component;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.joceanus.jtethys.ui.TethysFileSelector;

/**
 * Swing File Selector.
 */
public class TethysSwingFileSelector
        extends TethysFileSelector {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TethysSwingFileSelector.class);

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
     * @param pParent the parent
     */
    public TethysSwingFileSelector(final Component pParent) {
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
        String myName = getInitialFileName();
        if (myName != null) {
            File myFile = new File(theChooser.getCurrentDirectory(), myName);
            theChooser.setSelectedFile(myFile);
        }

        /* Set the extension filter list */
        theChooser.resetChoosableFileFilters();
        String myExt = getExtension();
        if (myExt != null) {
            theChooser.setFileFilter(new FileNameExtensionFilter("Filter", myExt));
        }

        /* Show the dialog */
        int myResult = useSave()
                                 ? theChooser.showSaveDialog(theParent)
                                 : theChooser.showOpenDialog(theParent);

        /* If we selected a file */
        if (myResult == JFileChooser.APPROVE_OPTION) {
            theSelectedFile = theChooser.getSelectedFile();
        }
    }

    @Override
    public File selectFile() {
        /* If this is the event dispatcher thread */
        if (SwingUtilities.isEventDispatchThread()) {
            /* invoke the dialog directly */
            showDialog();

            /* else we must use invokeAndWait */
        } else {
            try {
                SwingUtilities.invokeAndWait(() -> showDialog());
            } catch (InvocationTargetException e) {
                LOGGER.error("Failed to display dialog", e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        /* Return to caller */
        return theSelectedFile;
    }
}
