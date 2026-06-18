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

/**
 * Cryptography Data interfaces.
 */
public interface PrometheusCryptographyData {
    /**
     * PrometheusControlData interface.
     */
    interface PrometheusControlDataCtl
            extends PrometheusTableItem {
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
