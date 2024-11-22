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

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Function;

/**
 * Bundle Loader.
 */
public final class OceanusBundleLoader {
    /**
     * String Builder.
     */
    private final StringBuilder theBuilder = new StringBuilder();

    /**
     * BundleName.
     */
    private final String theBundleName;

    /**
     * BundleLoader.
     */
    private final Function<String, ResourceBundle> theLoader;

    /**
     * ResourceBundle.
     */
    private WeakReference<ResourceBundle> theBundle;

    /**
     * Standard constructor.
     * @param pBundleName the Bundle Name
     * @param pLoader the loader
     */
    private OceanusBundleLoader(final String pBundleName,
                                final Function<String, ResourceBundle> pLoader) {
        theBundleName = pBundleName;
        theLoader = pLoader;
    }

    /**
     * Obtain standard loader.
     * @param pBundleName the Bundle Name
     * @param pLoader the bundle loader
     * @return the loader
     */
    public static OceanusBundleLoader getLoader(final String pBundleName,
                                                final Function<String, ResourceBundle> pLoader) {
        return new OceanusBundleLoader(pBundleName, pLoader);
    }

    /**
     * Obtain package loader.
     * @param pBundleName the Bundle Name
     * @param pLoader the bundle loader
     * @return the loader
     */
    public static OceanusBundleLoader getPackageLoader(final String pBundleName,
                                                       final Function<String, ResourceBundle> pLoader) {
        final String myName = getPackageBundle(pBundleName);
        return new OceanusBundleLoader(myName, pLoader);
    }

    /**
     * Build name of resource for Key.
     * @param <K> the the resource key data type
     * @param pKey the resource Key
     * @return the resourceName
     */
    public <K extends Enum<K> & OceanusBundleId> String getValue(final K pKey) {
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
            myBundle = theLoader.apply(theBundleName);
            theBundle = new WeakReference<>(myBundle);
        }

        /* Access the required resource */
        return myBundle.getString(theBuilder.toString());
    }

    /**
     * Obtain bundle name based on package name.
     * @param pClazz the class name.
     * @return the bundle.
     */
    private static String getPackageBundle(final String pClazz) {
        /* Check that we have separators */
        int myIndex = pClazz.lastIndexOf('.');
        if (myIndex != -1) {
            /* Strip the final class name off */
            final String myResult = pClazz.substring(0, myIndex);

            /* Locate the package name */
            myIndex = myResult.lastIndexOf('.');

            /* If we are OK */
            if (myIndex != -1) {
                /* Access the package name */
                final String myPackage = myResult.substring(myIndex + 1);

                /* Build a new package name */
                final StringBuilder myBuilder = new StringBuilder();
                myBuilder.append(myResult);
                myBuilder.append('.');
                myBuilder.append(myPackage);
                return myBuilder.toString();
            }
        }

        /* No change */
        return pClazz;
    }

    /**
     * Obtain key for enum.
     * @param <E> the enum type
     * @param pMap the map
     * @param pValue the enum value
     * @return the resource key
     */
    public static <E extends Enum<E>> OceanusBundleId getKeyForEnum(final Map<E, OceanusBundleId> pMap,
                                                                    final E pValue) {
        final OceanusBundleId myId = pMap.get(pValue);
        if (myId == null) {
            throw new IllegalArgumentException(OceanusResourceLoader.getErrorNoResource(pValue));
        }
        return myId;
    }
}
