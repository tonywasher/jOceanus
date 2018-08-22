/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import net.sourceforge.joceanus.jtethys.resource.TethysResourceId;

/**
 * IconId.
 */
public interface TethysIconId extends TethysResourceId {
    /**
     * Default icon width.
     */
    int DEFAULT_ICONWIDTH = 16;

    /**
     * BUFFER Length.
     */
    int BUFFERLEN = 8192;

    /**
     * Obtain resource as bytes.
     * @return the bytes
     * @throws IOException on error
     */
    default byte[] loadResourceToBytes() throws IOException {
        try (InputStream myInput = getInputStream();
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            int nRead;
            final byte[] data = new byte[BUFFERLEN];
            while ((nRead = myInput.read(data, 0, BUFFERLEN)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            return buffer.toByteArray();
        }
    }
}
