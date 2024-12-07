/*******************************************************************************
 * Oceanus: Java Utilities
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
package net.sourceforge.joceanus.oceanus.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.exc.OceanusDataException;

/**
 * Resource Loader.
 */
public final class OceanusResourceLoader {
    /**
     * Private constructor.
     */
    private OceanusResourceLoader() {
    }

    /**
     * Error for missing resource.
     * @param pId the missing id
     * @return the error message
     */
    public static String getErrorNoResource(final Enum<?> pId) {
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append("Missing Resource: ");
        myBuilder.append(pId.getClass().getName());
        myBuilder.append(':');
        myBuilder.append(pId.name());
        return myBuilder.toString();
    }

    /**
     * Obtain Icon for enum.
     * @param <E> the enum type
     * @param <I> the icon type
     * @param pMap the map
     * @param pValue the enum value
     * @return the icon
     */
    public static <E extends Enum<E>, I> I getIconForEnum(final Map<E, I> pMap,
                                                          final E pValue) {
        final I myIcon = pMap.get(pValue);
        if (myIcon == null) {
            throw new IllegalArgumentException(getErrorNoResource(pValue));
        }
        return myIcon;
    }

    /**
     * Load resource file to String.
     * @param pKey the resource Key
     * @return the loaded resource
     * @throws OceanusException on error
     */
    public static String loadResourceToString(final OceanusResourceId pKey) throws OceanusException {
        /* Reset the builder */
        final StringBuilder myBuilder = new StringBuilder();

        /* Protect against exceptions */
        try (InputStream myStream = pKey.getInputStream();
             InputStreamReader myInputReader = new InputStreamReader(myStream, StandardCharsets.UTF_8);
             BufferedReader myReader = new BufferedReader(myInputReader)) {

            /* Read the header entry */
            for (;;) {
                /* Read next line */
                final String myLine = myReader.readLine();
                if (myLine == null) {
                    break;
                }

                /* Add to the string buffer */
                myBuilder.append(myLine);
                myBuilder.append('\n');
            }

            /* Build the string */
            return myBuilder.toString();

            /* Catch exceptions */
        } catch (IOException e) {
            /* Throw an exception */
            throw new OceanusDataException("Failed to load resource "
                                          + pKey.getSourceName(), e);
        }
    }
}
