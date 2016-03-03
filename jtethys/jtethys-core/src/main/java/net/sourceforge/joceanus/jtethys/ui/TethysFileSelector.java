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
package net.sourceforge.joceanus.jtethys.ui;

import java.io.File;

/**
 * File Selector.
 */
public abstract class TethysFileSelector {
    /**
     * The title.
     */
    private String theTitle;

    /**
     * The initial directory.
     */
    private File theInitialDir;

    /**
     * The initial file.
     */
    private String theInitialFileName;

    /**
     * The extension.
     */
    private String theExtension;

    /**
     * The useSave.
     */
    private boolean useSave;

    /**
     * Obtain the title.
     * @return the title
     */
    protected String getTitle() {
        return theTitle;
    }

    /**
     * Obtain the initial directory.
     * @return the directory
     */
    protected File getInitialDirectory() {
        return theInitialDir;
    }

    /**
     * Obtain the initial file name.
     * @return the name
     */
    protected String getInitialFileName() {
        return theInitialFileName;
    }

    /**
     * Obtain the extension.
     * @return the extension
     */
    protected String getExtension() {
        return theExtension;
    }

    /**
     * Use save rather than open.
     * @return the file
     */
    protected boolean useSave() {
        return useSave;
    }

    /**
     * Set the title.
     * @param pTitle the title
     */
    public void setTitle(final String pTitle) {
        theTitle = pTitle;
    }

    /**
     * Set the initial directory.
     * @param pDir the directory
     */
    public void setInitialDirectory(final File pDir) {
        theInitialDir = pDir;
    }

    /**
     * Set the initial file name.
     * @param pName the name
     */
    public void setInitialFileName(final String pName) {
        theInitialFileName = pName;
    }

    /**
     * Set the extension.
     * @param pExt the extension
     */
    public void setExtension(final String pExt) {
        theExtension = pExt;
    }

    /**
     * Use save rather than open.
     * @param pSave true/false
     */
    public void setUseSave(final boolean pSave) {
        useSave = pSave;
    }

    /**
     * Select file via dialog.
     * @return the selected file or null.
     */
    public abstract File selectFile();
}
