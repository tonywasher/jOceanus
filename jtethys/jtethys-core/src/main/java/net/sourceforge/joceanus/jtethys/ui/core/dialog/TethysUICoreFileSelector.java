/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.core.dialog;

import java.io.File;

import net.sourceforge.joceanus.jtethys.ui.api.dialog.TethysUIFileSelector;

/**
 * File Selector.
 */
public abstract class TethysUICoreFileSelector
        implements TethysUIFileSelector {
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

    @Override
    public void setTitle(final String pTitle) {
        theTitle = pTitle;
    }

    @Override
    public void setInitialDirectory(final File pDir) {
        theInitialDir = pDir;
    }

    @Override
    public void setInitialFileName(final String pName) {
        theInitialFileName = pName;
    }

    @Override
    public void setInitialFile(final File pFile) {
        theInitialDir = pFile == null
                ? null
                : pFile.getParentFile();
        theInitialFileName = pFile == null
                ? null
                : pFile.getName();
    }

    @Override
    public void setExtension(final String pExt) {
        theExtension = pExt;
    }

    @Override
    public void setUseSave(final boolean pSave) {
        useSave = pSave;
    }
}
