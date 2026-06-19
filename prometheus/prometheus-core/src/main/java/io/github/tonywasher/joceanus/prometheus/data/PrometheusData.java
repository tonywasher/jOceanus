/*
 * Prometheus: Application Framework
 * Copyright 2026. Tony Washer
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

package io.github.tonywasher.joceanus.prometheus.data;

import io.github.tonywasher.joceanus.gordianknot.api.keyset.spec.GordianKeySetSpec;
import io.github.tonywasher.joceanus.metis.data.MetisDataEditState;
import io.github.tonywasher.joceanus.metis.list.MetisListKey;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusTableItem.PrometheusTableList;
import io.github.tonywasher.joceanus.prometheus.security.PrometheusSecurityPasswordManager;

/**
 * Data Interfaces.
 */

public interface PrometheusData {
    /**
     * DataSet interface.
     */
    interface PrometheusDataSetListCtl {
        /**
         * Obtain the list for a class.
         *
         * @param <L>       the list type
         * @param pDataType the data type
         * @param pClass    the list class
         * @return the list
         */
        <L extends PrometheusDataListCtl<?>> L getDataList(MetisListKey pDataType,
                                                           Class<L> pClass);

        /**
         * Does this list have the dataType?
         *
         * @param pDataType the dataType
         * @return true/false
         */
        boolean hasDataType(MetisListKey pDataType);
    }

    /**
     * DataSet interface.
     */
    interface PrometheusDataSetCtl
            extends PrometheusDataSetListCtl {
        /**
         * Obtain a validator fot=r the itemType.
         *
         * @param pItemType the itemType
         * @return the validator.
         */
        PrometheusDataValidator getValidator(MetisListKey pItemType);

        /**
         * Get Password Manager.
         *
         * @return the password manager
         */
        PrometheusSecurityPasswordManager getPasswordMgr();

        /**
         * Obtain the data formatter.
         *
         * @return the formatter
         */
        OceanusDataFormatter getDataFormatter();

        /**
         * Get KeySetSpec.
         *
         * @return the keySetSpec
         */
        GordianKeySetSpec getKeySetSpec();

        /**
         * Get Number of activeKeySets.
         *
         * @return the # active KeySets
         */
        int getNumActiveKeySets();

        /**
         * Get Version.
         *
         * @return the version
         */
        int getVersion();

        /**
         * Set Version.
         *
         * @param pVersion the version
         */
        void setVersion(int pVersion);
    }

    /**
     * DataList interface.
     *
     * @param <T> the dataType
     */
    interface PrometheusDataListCtl<T extends PrometheusDataItemCtl>
            extends PrometheusTableList<T> {
        /**
         * Obtain the dataSet.
         *
         * @return the dataSet
         */
        PrometheusDataSetCtl getDataSet();

        /**
         * Obtain the validator.
         *
         * @return the validator
         */
        PrometheusDataValidator getValidator();

        /**
         * Get the style of the list.
         *
         * @return the list style
         */
        PrometheusListStyle getStyle();

        /**
         * Get the type of the list.
         *
         * @return the item type
         */
        MetisListKey getItemType();

        /**
         * Get the Version of the list.
         *
         * @return the Version
         */
        int getVersion();

        /**
         * Generate/Record new id for the item.
         *
         * @param pItem the new item
         */
        void setNewId(PrometheusDataItemCtl pItem);

        /**
         * Set the EditState for the list (forcible on error/change).
         *
         * @param pState the new {@link MetisDataEditState} (only ERROR/DIRTY)
         */
        void setEditState(MetisDataEditState pState);

        /**
         * Locate an item by name (if possible).
         *
         * @param pName the name of the item
         * @return the matching item
         */
        T findItemByName(final String pName);

        /**
         * Obtain item by id.
         *
         * @param pId the id to lookup
         * @return the item (or null if not present)
         */
        T findItemById(Integer pId);
    }

    /**
     * DataSet interface.
     */
    interface PrometheusDataItemCtl
            extends PrometheusTableItem {
        /**
         * Obtain the dataSet.
         *
         * @return the dataSet
         */
        PrometheusDataSetCtl getDataSet();

        /**
         * Set Id.
         *
         * @param pId the Id
         */
        void setIndexedId(Integer pId);
    }
}
