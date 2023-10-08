/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.atlas.data;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticDataItem;

/**
 * Prometheus DataIds.
 */
public enum PrometheusDataId
    implements PrometheusDataFieldId {
    /**
     * Id.
     */
    ID(PrometheusDataResource.DATAITEM_ID, DataItem.FIELD_ID),

    /**
     * Type.
     */
    TYPE(PrometheusDataResource.DATAITEM_TYPE, DataItem.FIELD_DATATYPE),

    /**
     * List.
     */
    LIST(PrometheusDataResource.DATALIST_NAME, DataItem.FIELD_LIST),

    /**
     * Base.
     */
    BASE(PrometheusDataResource.DATAITEM_BASE, DataItem.FIELD_BASE),

    /**
     * Touch.
     */
    TOUCH(PrometheusDataResource.DATAITEM_TOUCH, DataItem.FIELD_TOUCH),

    /**
     * Deleted.
     */
    DELETED(PrometheusDataResource.DATAITEM_DELETED, DataItem.FIELD_DELETED),

    /**
     * State.
     */
    STATE(PrometheusDataResource.DATAITEM_STATE, DataItem.FIELD_STATE),

    /**
     * EditState.
     */
    EDITSTATE(PrometheusDataResource.DATAITEM_EDITSTATE, DataItem.FIELD_EDITSTATE),

    /**
     * Version.
     */
    VERSION(PrometheusDataResource.DATASET_VERSION, DataItem.FIELD_VERSION),

    /**
     * Header.
     */
    HEADER(PrometheusDataResource.DATAITEM_HEADER, DataItem.FIELD_HEADER),

    /**
     * History.
     */
    HISTORY(PrometheusDataResource.DATAITEM_HISTORY, DataItem.FIELD_HISTORY),

    /**
     * Errors.
     */
    ERRORS(PrometheusDataResource.DATAITEM_ERRORS, DataItem.FIELD_ERRORS),

    /**
     * Name.
     */
    NAME(PrometheusDataResource.DATAITEM_FIELD_NAME, StaticDataItem.FIELD_NAME),

    /**
     * Description.
     */
    DESC(PrometheusDataResource.DATAITEM_FIELD_DESC, StaticDataItem.FIELD_DESC),

    /**
     * Enabled.
     */
    ENABLED(PrometheusDataResource.STATICDATA_ENABLED, StaticDataItem.FIELD_ENABLED),

    /**
     * Order.
     */
    ORDER(PrometheusDataResource.STATICDATA_SORT, StaticDataItem.FIELD_ORDER),

    /**
     * Class.
     */
    CLASS(PrometheusDataResource.STATICDATA_CLASS, StaticDataItem.FIELD_CLASS);

    /**
     * The Value.
     */
    private final String theValue;

    /**
     * The Lethe Field.
     */
    private final MetisLetheField theField;

    /**
     * Constructor.
     * @param pKeyName the key name
     * @param pField the lethe field
     */
    PrometheusDataId(final MetisDataFieldId pKeyName,
                     final MetisLetheField pField) {
        theValue = pKeyName.getId();
        theField = pField;
    }

    @Override
    public String getId() {
        return theValue;
    }

    @Override
    public String toString() {
        return getId();
    }

    @Override
    public MetisLetheField getLetheField() {
        return theField;
    }
}
