/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2024 Tony Washer
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

import net.sourceforge.joceanus.jtethys.ui.api.dialog.TethysUIDirectorySelector;

/**
 * Directory Selector.
 */
public abstract class TethysUICoreDirectorySelector
        implements TethysUIDirectorySelector {
    /**
     * The title.
     */
    private String theTitle;

    /**
     * The initial directory.
     */
    private File theInitialDir;

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

    @Override
    public void setTitle(final String pTitle) {
        theTitle = pTitle;
    }

    @Override
    public void setInitialDirectory(final File pDir) {
        theInitialDir = pDir;
    }
}
