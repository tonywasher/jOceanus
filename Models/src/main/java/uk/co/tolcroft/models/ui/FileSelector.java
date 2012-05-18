/*******************************************************************************
 * Copyright 2012 Tony Washer
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
package uk.co.tolcroft.models.ui;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

public class FileSelector extends JFileChooser {
    /* Serial Id */
    private static final long serialVersionUID = -7626176642686030118L;

    /**
     * The frame that is the parent to this fileSelector
     */
    private Component theParent = null;

    /**
     * The resulting file/directory that has been chosen
     */
    private File theResult = null;

    /**
     * Filter Prefix
     */
    private String thePrefix = null;

    /**
     * Filter Suffix
     */
    private String theSuffix = null;

    @Override
    public File getSelectedFile() {
        return theResult;
    }

    /**
     * Constructor
     * @param pParent the parent component
     * @param pTitle the title
     * @param pSelected the currently selected file
     * @param pPrefix the filter prefix
     * @param pSuffix the filter suffix
     */
    public FileSelector(Component pParent,
                        String pTitle,
                        File pSelected,
                        String pPrefix,
                        String pSuffix) {
        /* Record the parent and the filter criteria */
        theParent = pParent;
        thePrefix = pPrefix;
        theSuffix = pSuffix;

        /* Note the the chooser can only select a directory */
        setFileSelectionMode(FILES_ONLY);

        /* Initialise it to the selected file/directory */
        if (pSelected.isDirectory())
            setCurrentDirectory(pSelected);
        else
            setSelectedFile(pSelected);

        /* Set the file filter */
        setFileFilter(new fileFilter());

        /* Set the title */
        setDialogTitle(pTitle);
    }

    /**
     * Constructor
     * @param pParent the parent component
     * @param pTitle the title
     * @param pSelected the currently selected directory
     */
    public FileSelector(Component pParent,
                        String pTitle,
                        File pSelected) {
        /* Record the parent */
        theParent = pParent;

        /* Note the the chooser can only select a directory */
        setFileSelectionMode(DIRECTORIES_ONLY);

        /* Initialise it to the selected file */
        setSelectedFile(pSelected);

        /* Set the title */
        setDialogTitle(pTitle);
    }

    /**
     * Show the dialog to select a file using an invokeAndWait clause if necessary
     */
    public void showDialog() {
        /* If this is the event dispatcher thread */
        if (SwingUtilities.isEventDispatchThread()) {
            /* invoke the dialog directly */
            showTheDialog();
        }

        /* else we must use invokeAndWait */
        else {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        /* invoke the dialog */
                        showTheDialog();
                    }
                });
            }

            catch (Exception e) {
            }
        }
    }

    /**
     * Show the dialog to select the file
     */
    private void showTheDialog() {
        /* Show the dialog and select the file */
        int iRet = showOpenDialog(theParent);

        /* If we selected a file */
        if (iRet == JFileChooser.APPROVE_OPTION)
            theResult = super.getSelectedFile();

        /* else set no selection */
        else
            theResult = null;
    }

    /**
     * FileFilter class
     */
    private class fileFilter extends FileFilter {
        @Override
        public boolean accept(File pFile) {
            /* Always accept directories */
            if (pFile.isDirectory())
                return true;

            /* Access the file name */
            String myName = pFile.getName();

            /* Check the prefix */
            if ((thePrefix != null) && (!myName.startsWith(thePrefix)))
                return false;

            /* Check the suffix */
            if ((theSuffix != null) && (!myName.endsWith(theSuffix)))
                return false;

            /* accept the file */
            return true;
        }

        @Override
        public String getDescription() {
            return "Filter selection";
        }
    }
}
