/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.api.zip;

import java.io.InputStream;

import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * GordianKnot Zip ReadFile API.
 */
public interface GordianZipReadFile {
    /**
     * Obtain the contents.
     * @return the contents
     */
    GordianZipFileContents getContents();

    /**
     * Obtain the hash bytes for the file.
     * @return the hash bytes
     */
    byte[] getHashBytes();

    /**
     * Set the keySet hash.
     * @param pHash the keySet hash
     * @throws OceanusException on error
     */
    void setKeySetHash(GordianKeySetHash pHash) throws OceanusException;

    /**
     * Obtain an input stream for an entry in the zip file.
     * @param pFile the file details for the new zip entry
     * @return the input stream
     * @throws OceanusException on error
     */
    InputStream createInputStream(GordianZipFileEntry pFile) throws OceanusException;
}