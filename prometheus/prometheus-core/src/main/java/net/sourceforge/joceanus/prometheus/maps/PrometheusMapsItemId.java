/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.prometheus.maps;

import net.sourceforge.joceanus.metis.data.MetisDataResource;
import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.metis.list.MetisListKey;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;

import java.util.Objects;

/**
 * DataTouchMap itemId.
 */
public class PrometheusMapsItemId
        implements MetisFieldItem {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<PrometheusMapsItemId> FIELD_DEFS
            = MetisFieldSet.newFieldSet(PrometheusMapsItemId.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAITEM_TYPE, PrometheusMapsItemId::getListKey);
        FIELD_DEFS.declareLocalField(MetisDataResource.DATA_ID, PrometheusMapsItemId::getItemId);
    }

    /**
     * The listKey.
     */
    private final MetisListKey theListKey;

    /**
     * The itemId.
     */
    private final Integer theItemId;

    /**
     * Constructor.
     * @param pItem the item
     */
    PrometheusMapsItemId(final PrometheusDataItem pItem) {
        theListKey = pItem.getItemType();
        theItemId = pItem.getIndexedId();
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
            return FIELD_DEFS;
        }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
        return pFormatter.formatObject(theListKey) + ":" + theItemId;
    }

    /**
     * Obtain the listKey.
     * @return the listKey
     */
    private MetisListKey getListKey() {
        return theListKey;
    }

    /**
     * Obtain the itemId.
     * @return the id
     */
    private Integer getItemId() {
        return theItemId;
    }

    @Override
    public boolean equals(final Object pThat) {
        if (!(pThat instanceof PrometheusMapsItemId)) {
            return false;
        }
        final PrometheusMapsItemId myThat = (PrometheusMapsItemId) pThat;
        return theItemId.equals(myThat.getItemId())
                && theListKey.equals(myThat.getListKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(theListKey, theItemId);
    }
}
