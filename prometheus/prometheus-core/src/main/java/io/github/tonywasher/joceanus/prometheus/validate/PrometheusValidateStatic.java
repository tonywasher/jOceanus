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

import io.github.tonywasher.joceanus.prometheus.data.PrometheusData.PrometheusDataItemCtl;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusData.PrometheusDataValidator;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataItem;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataResource;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusStaticDataItem;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusStaticDataItem.PrometheusStaticDataMap;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusStaticDataItem.PrometheusStaticList;

/**
 * Validator for static.
 */
public class PrometheusValidateStatic
        implements PrometheusDataValidator {
    /**
     * Default constructor.
     */
    public PrometheusValidateStatic() {
        /* NoOp */
    }

    @Override
    public void validate(final PrometheusDataItemCtl pStatic) {
        final PrometheusStaticDataItem myStatic = (PrometheusStaticDataItem) pStatic;
        final PrometheusStaticList<?> myList = myStatic.getList();
        final String myName = myStatic.getName();
        final String myDesc = myStatic.getDesc();
        final PrometheusStaticDataMap<?> myMap = myList.getDataMap();

        /* Name must be non-null */
        if (myName == null) {
            myStatic.addError(PrometheusDataItem.ERROR_MISSING, PrometheusDataResource.DATAITEM_FIELD_NAME);

            /* Else check the name */
        } else {
            /* The name must not be too long */
            if (PrometheusDataItem.byteLength(myName) > PrometheusDataItem.NAMELEN) {
                myStatic.addError(PrometheusDataItem.ERROR_LENGTH, PrometheusDataResource.DATAITEM_FIELD_NAME);
            }

            /* The name must only contain valid characters */
            if (!PrometheusDataItem.validString(myName, null)) {
                myStatic.addError(PrometheusStaticDataItem.ERROR_BADNAME, PrometheusDataResource.DATAITEM_FIELD_NAME);
            }

            /* Check that the name is unique */
            if (!myMap.validNameCount(myName)) {
                myStatic.addError(PrometheusDataItem.ERROR_DUPLICATE, PrometheusDataResource.DATAITEM_FIELD_NAME);
            }
        }

        /* Check description length */
        if (myDesc != null
                && PrometheusDataItem.byteLength(myDesc) > PrometheusDataItem.DESCLEN) {
            myStatic.addError(PrometheusDataItem.ERROR_LENGTH, PrometheusDataResource.DATAITEM_FIELD_DESC);
        }

        /* The order must not be negative */
        if (myStatic.getOrder() < 0) {
            myStatic.addError(PrometheusDataItem.ERROR_NEGATIVE, PrometheusDataResource.STATICDATA_SORT);
        }

        /* Cannot have duplicate order */
        if (!myMap.validOrderCount(myStatic.getOrder())) {
            myStatic.addError(PrometheusDataItem.ERROR_DUPLICATE, PrometheusDataResource.STATICDATA_SORT);
        }

        /* Set validation flag */
        if (!myStatic.hasErrors()) {
            myStatic.setValidEdit();
        }
    }
}
