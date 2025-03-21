/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2025 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.gordianknot.api.zip;

import java.util.Iterator;

/**
 * GordianKnot ZipFile contents API.
 */
public interface GordianZipFileContents {
    /**
     * Obtain an iterator for the fileEntries.
     * @return the iterator
     */
    Iterator<GordianZipFileEntry> iterator();

    /**
     * Locate the fileEntry by name.
     * @param pName the name of the fileEntry
     * @return the fileEntry or null if not found
     */
    GordianZipFileEntry findFileEntry(String pName);
}
