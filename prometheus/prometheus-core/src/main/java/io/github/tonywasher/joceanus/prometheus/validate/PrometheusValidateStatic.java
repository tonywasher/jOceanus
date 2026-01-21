/*
 * Prometheus: Application Framework
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
package io.github.tonywasher.joceanus.prometheus.validate;

import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataItem;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataResource;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataValidator;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusStaticDataItem;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusStaticDataItem.PrometheusStaticDataMap;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusStaticDataItem.PrometheusStaticList;

/**
 * Validator for static.
 */
public class PrometheusValidateStatic
        implements PrometheusDataValidator {
    /**
     * Constructor.
     */
    public PrometheusValidateStatic() {
    }

    @Override
    public void validate(final PrometheusDataItem pStatic) {
        final PrometheusStaticDataItem myStatic = (PrometheusStaticDataItem) pStatic;
        final PrometheusStaticList<?> myList = myStatic.getList();
        final String myName = myStatic.getName();
        final String myDesc = myStatic.getDesc();
        final PrometheusStaticDataMap<?> myMap = myList.getDataMap();

        /* Name must be non-null */
        if (myName == null) {
            pStatic.addError(PrometheusDataItem.ERROR_MISSING, PrometheusDataResource.DATAITEM_FIELD_NAME);

            /* Else check the name */
        } else {
            /* The name must not be too long */
            if (PrometheusDataItem.byteLength(myName) > PrometheusDataItem.NAMELEN) {
                pStatic.addError(PrometheusDataItem.ERROR_LENGTH, PrometheusDataResource.DATAITEM_FIELD_NAME);
            }

            /* The name must only contain valid characters */
            if (!PrometheusDataItem.validString(myName, null)) {
                pStatic.addError(PrometheusStaticDataItem.ERROR_BADNAME, PrometheusDataResource.DATAITEM_FIELD_NAME);
            }

            /* Check that the name is unique */
            if (!myMap.validNameCount(myName)) {
                pStatic.addError(PrometheusDataItem.ERROR_DUPLICATE, PrometheusDataResource.DATAITEM_FIELD_NAME);
            }
        }

        /* Check description length */
        if (myDesc != null
                && PrometheusDataItem.byteLength(myDesc) > PrometheusDataItem.DESCLEN) {
            pStatic.addError(PrometheusDataItem.ERROR_LENGTH, PrometheusDataResource.DATAITEM_FIELD_DESC);
        }

        /* The order must not be negative */
        if (myStatic.getOrder() < 0) {
            pStatic.addError(PrometheusDataItem.ERROR_NEGATIVE, PrometheusDataResource.STATICDATA_SORT);
        }

        /* Cannot have duplicate order */
        if (!myMap.validOrderCount(myStatic.getOrder())) {
            pStatic.addError(PrometheusDataItem.ERROR_DUPLICATE, PrometheusDataResource.STATICDATA_SORT);
        }

        /* Set validation flag */
        if (!pStatic.hasErrors()) {
            pStatic.setValidEdit();
        }
    }
}
