/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.base;

import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import io.github.tonywasher.joceanus.metis.field.MetisFieldSimpleId;
import io.github.tonywasher.joceanus.metis.list.MetisListKey;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import io.github.tonywasher.joceanus.oceanus.resource.OceanusBundleId;

/**
 * MoneyWise Analysis Item Types.
 */
public enum MoneyWiseXAnalysisDataType
        implements MetisListKey, MetisDataFieldId {
    /**
     * Event.
     */
    EVENT(MoneyWiseBasicDataType.MAXKEYID + 1);

    /**
     * The list key.
     */
    private final Integer theKey;

    /**
     * The String id.
     */
    private MetisDataFieldId theNameId;

    /**
     * The List id.
     */
    private MetisDataFieldId theListId;

    /**
     * Constructor.
     *
     * @param pKey the keyId
     */
    MoneyWiseXAnalysisDataType(final Integer pKey) {
        theKey = pKey;
    }

    @Override
    public String toString() {
        /* return the name */
        return getItemId().getId();
    }

    /**
     * Obtain Id of item.
     *
     * @return the item name
     */
    public MetisDataFieldId getItemId() {
        /* If we have not yet loaded the id */
        if (theNameId == null) {
            /* Load the id */
            theNameId = MetisFieldSimpleId.convertResource(bundleIdForDataType(this));
        }

        /* Return the name id */
        return theNameId;
    }

    /**
     * Obtain Name of item.
     *
     * @return the item name
     */
    @Override
    public String getItemName() {
        return toString();
    }

    /**
     * Obtain Id of associated list.
     *
     * @return the list name
     */
    public MetisDataFieldId getListId() {
        /* If we have not yet loaded the id */
        if (theListId == null) {
            /* Load the id */
            theListId = MetisFieldSimpleId.convertResource(bundleIdForDataList(this));
        }

        /* return the list id */
        return theListId;
    }

    @Override
    public String getListName() {
        return getFieldName();
    }

    /**
     * Obtain field name.
     *
     * @return the field name
     */
    public String getFieldName() {
        return getListId().getId();
    }

    @Override
    public String getId() {
        return getItemName();
    }

    @Override
    public Integer getItemKey() {
        return theKey;
    }

    /**
     * Obtain the resource bundleId for the dataTypeList.
     *
     * @param pType the dataType
     * @return the resource bundleId
     */
    private static OceanusBundleId bundleIdForDataType(final MoneyWiseXAnalysisDataType pType) {
        if (pType == EVENT) {
            return MoneyWiseXAnalysisBaseResource.EVENT;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Obtain the resource bundleId for the dataTypeList.
     *
     * @param pType the dataType
     * @return the resource bundleId
     */
    private static OceanusBundleId bundleIdForDataList(final MoneyWiseXAnalysisDataType pType) {
        if (pType == EVENT) {
            return MoneyWiseXAnalysisBaseResource.EVENTLIST;
        }
        throw new IllegalArgumentException();
    }
}
