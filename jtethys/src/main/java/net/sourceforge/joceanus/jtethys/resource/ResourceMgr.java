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

import java.util.ResourceBundle;

/**
 * Resource Bundle Manager.
 */
public final class ResourceMgr {
    /**
     * Private constructor.
     */
    private ResourceMgr() {
    }

    /**
     * Obtain string from resource bundle.
     * @param pKey the resource Key
     * @return the string
     */
    public static <K extends Enum<K> & ResourceId> String getString(final K pKey) {
        /* Access the correct bundle */
        ResourceBundle myBundle = ResourceBundle.getBundle(pKey.getBundleName());

        /* Build the required name */
        StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(pKey.getNameSpace());
        myBuilder.append('.');
        myBuilder.append(pKey.getKeyName());

        /* Access the required resource */
        return myBundle.getString(myBuilder.toString());
    }

    /**
     * The Resource Id interface.
     */
    public interface ResourceId {
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
         * Get Bundle name.
         * @return the bundle name.
         */
        String getBundleName();
    }
}
