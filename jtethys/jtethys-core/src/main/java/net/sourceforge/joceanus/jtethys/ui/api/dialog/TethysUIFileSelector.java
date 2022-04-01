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
package net.sourceforge.joceanus.jtethys.ui.api.dialog;

import java.io.File;

/**
 * File Selector.
 */
public interface TethysUIFileSelector {
    /**
     * Set the title.
     * @param pTitle the title
     */
    void setTitle(String pTitle);

    /**
     * Set the initial directory.
     * @param pDir the directory
     */
    void setInitialDirectory(File pDir);

    /**
     * Set the initial file name.
     * @param pName the name
     */
    void setInitialFileName(String pName);

    /**
     * Set the initial file.
     * @param pFile the file
     */
    void setInitialFile(File pFile);

    /**
     * Set the extension.
     * @param pExt the extension
     */
    void setExtension(String pExt);

    /**
     * Use save rather than open.
     * @param pSave true/false
     */
    void setUseSave(boolean pSave);

    /**
     * Select file via dialog.
     * @return the selected file or null.
     */
    File selectFile();
}
