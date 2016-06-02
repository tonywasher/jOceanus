/*******************************************************************************
 * jTethys: Java Utilities
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jtethys.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataException;

/**
 * Resource Builder.
 */
public final class TethysResourceBuilder {
    /**
     * String Builder.
     */
    private final StringBuilder theBuilder = new StringBuilder();

    /**
     * BundleName.
     */
    private final String theBundleName;

    /**
     * ResourceBundle.
     */
    private WeakReference<ResourceBundle> theBundle;

    /**
     * Standard constructor.
     * @param pBundleName the Bundle Name
     */
    private TethysResourceBuilder(final String pBundleName) {
        theBundleName = pBundleName;
    }

    /**
     * Obtain standard builder.
     * @param pBundleName the Bundle Name
     * @return the builder
     */
    public static TethysResourceBuilder getResourceBuilder(final String pBundleName) {
        return new TethysResourceBuilder(pBundleName);
    }

    /**
     * Obtain package builder.
     * @param pBundleName the Bundle Name
     * @return the builder
     */
    public static TethysResourceBuilder getPackageResourceBuilder(final String pBundleName) {
        String myName = getPackageBundle(pBundleName);
        return new TethysResourceBuilder(myName);
    }

    /**
     * Build name of resource for Key.
     * @param <K> the the resource key data type
     * @param pKey the resource Key
     * @return the resourceName
     */
    public <K extends Enum<K> & TethysResourceId> String getValue(final K pKey) {
        /* Build the required name */
        theBuilder.setLength(0);
        theBuilder.append(pKey.getNameSpace());
        theBuilder.append('.');
        theBuilder.append(pKey.getKeyName());

        /* Access the cached bundle */
        ResourceBundle myBundle = theBundle == null
                                                    ? null
                                                    : theBundle.get();

        /* If the bundle is not cached */
        if (myBundle == null) {
            /* Access and cache the bundle */
            myBundle = ResourceBundle.getBundle(theBundleName);
            theBundle = new WeakReference<>(myBundle);
        }

        /* Access the required resource */
        return myBundle.getString(theBuilder.toString());
    }

    /**
     * Obtain bundle name based on package name.
     * @param pClass the class name.
     * @return the bundle.
     */
    private static String getPackageBundle(final String pClass) {
        /* Check that we have separators */
        int myIndex = pClass.lastIndexOf('.');
        if (myIndex != -1) {
            /* Strip the final class name off */
            String myResult = pClass.substring(0, myIndex);

            /* Locate the package name */
            myIndex = myResult.lastIndexOf('.');

            /* If we are OK */
            if (myIndex != -1) {
                /* Access the package name */
                String myPackage = myResult.substring(myIndex + 1);

                /* Build a new package name */
                StringBuilder myBuilder = new StringBuilder();
                myBuilder.append(myResult);
                myBuilder.append('.');
                myBuilder.append(myPackage);
                return myBuilder.toString();
            }
        }

        /* No change */
        return pClass;
    }

    /**
     * Obtain key for enum.
     * @param <E> the enum type
     * @param pMap the map
     * @param pValue the enum value
     * @return the resource key
     */
    public static <E extends Enum<E>> TethysResourceId getKeyForEnum(final Map<E, TethysResourceId> pMap,
                                                                     final E pValue) {
        TethysResourceId myId = pMap.get(pValue);
        if (myId == null) {
            throw new IllegalArgumentException(getErrorNoResource(pValue));
        }
        return myId;
    }

    /**
     * Error for missing resource.
     * @param pId the missing id
     * @return the error message
     */
    public static String getErrorNoResource(final Enum<?> pId) {
        StringBuilder myBuilder = new StringBuilder();
        myBuilder.append("Missing Resource: ");
        myBuilder.append(pId.getClass().getName());
        myBuilder.append(':');
        myBuilder.append(pId.name());
        return myBuilder.toString();
    }

    /**
     * Load resource file to String.
     * @param pClass the class to use to load the resource
     * @param pName the name of the resource relative to the class
     * @return the loaded resource
     * @throws OceanusException on error
     */
    public static String loadResourceToString(final Class<?> pClass,
                                              final String pName) throws OceanusException {
        /* Reset the builder */
        StringBuilder myBuilder = new StringBuilder();

        /* Protect against exceptions */
        try (InputStream myStream = pClass.getResourceAsStream(pName);
             InputStreamReader myInputReader = new InputStreamReader(myStream, StandardCharsets.UTF_8);
             BufferedReader myReader = new BufferedReader(myInputReader)) {

            /* Read the header entry */
            for (;;) {
                /* Read next line */
                String myLine = myReader.readLine();
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
            throw new TethysDataException("Failed to load resource "
                                          + pName, e);
        }
    }
}
