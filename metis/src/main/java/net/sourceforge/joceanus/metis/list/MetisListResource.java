/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.metis.list;

import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.metis.data.MetisDataResource;
import net.sourceforge.joceanus.oceanus.resource.OceanusBundleId;
import net.sourceforge.joceanus.oceanus.resource.OceanusBundleLoader;

import java.util.ResourceBundle;

/**
 * ResourcesIds for List.
 */
public enum MetisListResource
        implements OceanusBundleId, MetisDataFieldId {
    /**
     * List Size.
     */
    FIELD_SIZE("field.Size"),

    /**
     * List Version.
     */
    FIELD_VERSION("field.Version"),

    /**
     * List EditVersion.
     */
    FIELD_EDITVERSION("field.EditVersion"),

    /**
     * List Base.
     */
    FIELD_BASE("field.Base"),

    /**
     * ItemType.
     */
    FIELD_ITEMTYPE("field.ItemType"),

    /**
     * ListSet.
     */
    FIELD_LISTSET("field.listSet"),

    /**
     * ListType.
     */
    FIELD_TYPE("field.Type"),

    /**
     * List Class.
     */
    FIELD_CLASS("field.Class"),

    /**
     * List Error.
     */
    FIELD_ERROR("field.Error"),

    /**
     * List PairedItems.
     */
    FIELD_PAIREDITEMS("field.PairedItems"),

    /**
     * List PairedReferences.
     */
    FIELD_PAIREDREFERENCES("field.PairedReferences");

    /**
     * The Resource Loader.
     */
    private static final OceanusBundleLoader LOADER = OceanusBundleLoader.getLoader(MetisDataResource.class.getCanonicalName(),
            ResourceBundle::getBundle);

    /**
     * The Id.
     */
    private final String theKeyName;

    /**
     * The Value.
     */
    private String theValue;

    /**
     * Constructor.
     * @param pKeyName the key name
     */
    MetisListResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "jMetis.list";
    }

    @Override
    public String getValue() {
        /* If we have not initialised the value */
        if (theValue == null) {
            /* Derive the value */
            theValue = LOADER.getValue(this);
        }

        /* return the value */
        return theValue;
    }

    @Override
    public String getId() {
        return getValue();
    }
}
