/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2018 Tony Washer
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

import java.io.File;

import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * GordianKnot Zip Factory API.
 */
public interface GordianZipFactory {
    /**
     * Create a secure zipFile.
     * @param pHash the password hash to use
     * @param pFile the file details for the new zip file
     * @throws OceanusException on error
     */
    GordianZipWriteFile createZipFile(GordianKeySetHash pHash,
                                      File pFile) throws OceanusException;

    /**
     * Create a standard zipFile with no security.
     * @param pFile the file details for the new zip file
     * @throws OceanusException on error
     */
    GordianZipWriteFile createZipFile(File pFile) throws OceanusException;

    /**
     * Open an existing zipFile.
     * @param pFile the file to read
     * @throws OceanusException on error
     */
    GordianZipReadFile openZipFile(File pFile) throws OceanusException;
}
