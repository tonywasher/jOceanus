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

import io.github.tonywasher.joceanus.gordianknot.api.factory.GordianFactory;
import io.github.tonywasher.joceanus.gordianknot.api.keyset.GordianKeySet;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusData.PrometheusDataSetCtl;

/**
 * Cryptography Data interfaces.
 */
public interface PrometheusCryptographyData {
    /**
     * PrometheusControlData interface.
     */
    interface PrometheusCryptographicDataSet
            extends PrometheusDataSetCtl {
        /**
         * Get the control key.
         *
         * @return the control key
         */
        PrometheusControlDataCtl getControl();
    }

    /**
     * PrometheusControlData interface.
     */
    interface PrometheusControlDataCtl
            extends PrometheusTableItem {
        /**
         * Get the control key.
         *
         * @return the control key
         */
        PrometheusControlKeyCtl getControlKey();

        /**
         * Set a new ControlKey.
         *
         * @param pControl the new control key
         * @throws OceanusException on error
         */
        void setControlKey(PrometheusControlKeyCtl pControl) throws OceanusException;
    }

    /**
     * PrometheusControlKey interface.
     */
    interface PrometheusControlKeyCtl
            extends PrometheusTableItem {
        /**
         * Get the securityFactory.
         *
         * @return the securityFactory
         */
        GordianFactory getSecurityFactory();

        /**
         * Register ControlKeySet.
         *
         * @param pKeySet the ControlKeySet to register
         */
        void registerControlKeySet(PrometheusControlKeySetCtl pKeySet);
    }

    /**
     * PrometheusControlKeySet interface.
     */
    interface PrometheusControlKeySetCtl
            extends PrometheusTableItem {
        /**
         * Get the KeySet.
         *
         * @return the keySet
         */
        GordianKeySet getKeySet();

        /**
         * Obtain the security factory.
         *
         * @return the security factory
         */
        GordianFactory getSecurityFactory();

        /**
         * Get the ControlKey.
         *
         * @return the controlKey
         */
        PrometheusControlKeyCtl getControlKey();

        /**
         * Register DataKeySet.
         *
         * @param pKeySet the DataKeySet to register
         */
        void registerDataKeySet(PrometheusDataKeySetCtl pKeySet);
    }

    /**
     * PrometheusDataKeySet interface.
     */
    interface PrometheusDataKeySetCtl
            extends PrometheusTableItem {
    }
}
