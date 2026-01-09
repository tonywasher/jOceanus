/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.api.base;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Bundle Loader. (Cloned from OceanusBundleLoader)
 */
public final class GordianBundleLoader {
    /**
     * Interface for bundleIds.
     */
    public interface GordianBundleId {
        /**
         * Get Key Name.
         * @return the key name.
         */
        String getKeyName();

        /**
         * Get NameSpace.
         * @return the nameSpace.
         */
        String getNameSpace();

        /**
         * Get Value.
         * @return the value.
         */
        String getValue();
    }

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
    private GordianBundleLoader(final String pBundleName) {
        theBundleName = pBundleName;
    }

    /**
     * Obtain standard loader.
     * @param pBundleName the Bundle Name
     * @return the loader
     */
    public static GordianBundleLoader getLoader(final String pBundleName) {
        return new GordianBundleLoader(pBundleName);
    }

    /**
     * Build name of resource for Key.
     * @param <K> the resource key data type
     * @param pKey the resource Key
     * @return the resourceName
     */
    public <K extends Enum<K> & GordianBundleId> String getValue(final K pKey) {
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
     * Obtain key for enum.
     * @param <E> the enum type
     * @param pMap the map
     * @param pValue the enum value
     * @return the resource key
     */
    public static <E extends Enum<E>> GordianBundleId getKeyForEnum(final Map<E, GordianBundleId> pMap,
                                                                    final E pValue) {
        final GordianBundleId myId = pMap.get(pValue);
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
    private static String getErrorNoResource(final Enum<?> pId) {
        return "Missing Resource: "
                + pId.getClass().getName()
                + ':'
                + pId.name();
    }
}
