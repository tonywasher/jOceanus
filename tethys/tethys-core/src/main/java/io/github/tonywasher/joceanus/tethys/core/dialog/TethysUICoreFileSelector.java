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
package io.github.tonywasher.joceanus.tethys.core.dialog;

import io.github.tonywasher.joceanus.tethys.api.dialog.TethysUIFileSelector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
     * The filter name.
     */
    private String theFilterName = "Filter";

    /**
     * The extension.
     */
    private final List<String> theExtensions;

    /**
     * The useSave.
     */
    private boolean useSave;

    /**
     * Constructor.
     */
    protected TethysUICoreFileSelector() {
        theExtensions = new ArrayList<>();
    }

    /**
     * Obtain the title.
     *
     * @return the title
     */
    protected String getTitle() {
        return theTitle;
    }

    /**
     * Obtain the initial directory.
     *
     * @return the directory
     */
    protected File getInitialDirectory() {
        return theInitialDir;
    }

    /**
     * Obtain the initial file name.
     *
     * @return the name
     */
    protected String getInitialFileName() {
        return theInitialFileName;
    }

    /**
     * Obtain the filter name.
     *
     * @return the extensions
     */
    protected String getFilterName() {
        return theFilterName;
    }

    /**
     * Obtain the extensions.
     *
     * @return the extensions
     */
    protected List<String> getExtensions() {
        return theExtensions;
    }

    /**
     * Use save rather than open.
     *
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
    public void setFilterName(final String pName) {
        theFilterName = pName;
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
    public void addExtension(final String pExt) {
        theExtensions.add(pExt);
    }

    @Override
    public void setUseSave(final boolean pSave) {
        useSave = pSave;
    }
}
