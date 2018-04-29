/*******************************************************************************
 * MoneyWise: Finance Application
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
package net.sourceforge.joceanus.jmoneywise.lethe.quicken.file;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import net.sourceforge.joceanus.jmoneywise.MoneyWiseIOException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Wrapper for stream writer.
 */
public class QIFStreamWriter
        implements AutoCloseable {
    /**
     * Underlying writer.
     */
    private final OutputStreamWriter theWriter;

    /**
     * Constructor.
     * @param pFile the file to write to
     * @throws IOException on error
     */
    public QIFStreamWriter(final File pFile) throws IOException {
        /* Open the file */
        final FileOutputStream myOutput = new FileOutputStream(pFile);
        final BufferedOutputStream myBuffer = new BufferedOutputStream(myOutput);
        theWriter = new OutputStreamWriter(myBuffer, StandardCharsets.ISO_8859_1);
    }

    /**
     * Write a string.
     * @param pValue the string to write
     * @throws OceanusException on error
     */
    protected void write(final String pValue) throws OceanusException {
        try {
            theWriter.write(pValue);
        } catch (IOException e) {
            throw new MoneyWiseIOException("Write failure", e);
        }
    }

    @Override
    public void close() throws IOException {
        theWriter.close();
    }
}
