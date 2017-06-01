/*******************************************************************************
 * jTethys: Java Utilities
 * Copyright 2012,2017 Tony Washer
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
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui;

import java.io.InputStream;
import java.net.URL;

/**
 * IconBuilder.
 */
public abstract class TethysIconBuilder {
    /**
     * Default icon width.
     */
    public static final int DEFAULT_ICONWIDTH = 16;

    /**
     * Interface for eNums.
     */
    @FunctionalInterface
    public interface TethysIconId {
        /**
         * Obtain Icon source.
         * @return the source filename
         */
        String getSourceName();
    }

    /**
     * Private constructor.
     */
    private TethysIconBuilder() {
    }

    /**
     * Obtain resource.
     * @param pId the icon Id
     * @return the URL reference
     */
    public static URL getResource(final TethysIconId pId) {
        return pId.getClass().getResource(pId.getSourceName());
    }

    /**
     * Obtain resource as Stream.
     * @param pId the icon Id
     * @return the inputStream
     */
    public static InputStream getResourceAsStream(final TethysIconId pId) {
        return pId.getClass().getResourceAsStream(pId.getSourceName());
    }
}
