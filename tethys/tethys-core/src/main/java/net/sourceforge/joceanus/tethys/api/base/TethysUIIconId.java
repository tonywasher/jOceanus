/*******************************************************************************
 * Tethys: GUI Utilities
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.tethys.api.base;

import java.io.IOException;
import java.io.InputStream;

import net.sourceforge.joceanus.oceanus.resource.OceanusResourceId;

/**
 * IconId.
 */
public interface TethysUIIconId extends OceanusResourceId {
    /**
     * Obtain resource as bytes.
     * @return the bytes
     * @throws IOException on error
     */
    default byte[] loadResourceToBytes() throws IOException {
        try (InputStream myInput = getInputStream()) {
            return myInput.readAllBytes();
        }
    }
}
