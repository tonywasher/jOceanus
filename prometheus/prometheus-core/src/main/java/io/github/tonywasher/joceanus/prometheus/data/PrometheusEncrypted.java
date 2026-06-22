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

import io.github.tonywasher.joceanus.metis.data.MetisDataDifference;
import io.github.tonywasher.joceanus.metis.field.MetisFieldVersion.MetisFieldVersionedItemCtl;
import io.github.tonywasher.joceanus.metis.field.MetisFieldVersion.MetisFieldVersionedSetCtl;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusData.PrometheusDataItemCtl;

/**
 * Encrypted Data Interfaces.
 */
public interface PrometheusEncrypted {
    /**
     * Encrypted FieldSet.
     *
     * @param <T> the itemType
     */
    interface PrometheusEncryptedFieldSetCtl<T extends PrometheusEncryptedDataItemCtl>
            extends MetisFieldVersionedSetCtl<T> {
    }

    /**
     * InfoSet List.
     */
    interface PrometheusDataInfoListCtl {
    }

    /**
     * Interface for an infoSet item.
     *
     * @param <T> the infoType
     */
    interface PrometheusDataInfoSetItemCtl<T extends PrometheusDataInfoItemCtl> {
        /**
         * Obtain infoSet.
         *
         * @return the infoSet
         */
        PrometheusDataInfoSetCtl<T> getInfoSet();
    }

    /**
     * Interface for an infoSet.
     *
     * @param <T> the infoType
     */
    interface PrometheusDataInfoSetCtl<T extends PrometheusDataInfoItemCtl>
            extends Iterable<T> {
        /**
         * Is the infoSet empty?
         *
         * @return true/false.
         */
        boolean isEmpty();

        /**
         * Determine whether a particular field has changed in this edit view.
         *
         * @param pInfoClass the class to test
         * @return <code>true/false</code>
         */
        MetisDataDifference fieldChanged(PrometheusDataInfoClass pInfoClass);
    }

    /**
     * DataInfoItem.
     */
    interface PrometheusDataInfoItemCtl
            extends PrometheusEncryptedDataItemCtl {
        /**
         * Obtain InfoClass.
         *
         * @return the InfoClass
         */
        PrometheusDataInfoClass getInfoClass();

        /**
         * Obtain Link.
         *
         * @return the Link
         */
        PrometheusDataItemCtl getLink();

        /**
         * Obtain Value as object.
         *
         * @param <X>    the object type
         * @param pClass the object class
         * @return the Value
         */
        <X> X getValue(Class<X> pClass);
    }

    /**
     * Encrypted Data Item.
     */
    interface PrometheusEncryptedDataItemCtl
            extends PrometheusDataItemCtl, MetisFieldVersionedItemCtl {
        /**
         * Get the Encryptor.
         *
         * @return the encryptor
         */
        PrometheusEncryptor getEncryptor();
    }
}
