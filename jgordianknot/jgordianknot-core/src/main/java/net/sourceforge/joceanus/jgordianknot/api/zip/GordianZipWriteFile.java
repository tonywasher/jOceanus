/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2022 Tony Washer
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
import java.io.IOException;
import java.io.OutputStream;

import org.w3c.dom.Document;

import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * GordianKnot Zip WriteFile API.
 */
public interface GordianZipWriteFile extends AutoCloseable {
    /**
     * Obtain an output stream for an entry in the zip file.
     * @param pFile the file details for the new zip entry
     * @param pCompress should we compress this file?
     * @return the output stream
     * @throws OceanusException on error
     */
    OutputStream createOutputStream(File pFile,
                                    boolean pCompress) throws OceanusException;

    /**
     * Write an XML Document as a compressed in the Zip file.
     * @param pFile the file details for the new zip entry
     * @param pDocument the XML document.
     * @throws OceanusException on error
     */
    void writeXMLDocument(File pFile,
                          Document pDocument) throws OceanusException;

    /**
     * Obtain the contents.
     * @return the ZipFile Contents
     */
    GordianZipFileContents getContents();

    /**
     * Obtain the currently active ZipFileEntry.
     * @return the ZipFile Entry
     */
    GordianZipFileEntry getCurrentEntry();

    @Override
    void close() throws IOException;
}
