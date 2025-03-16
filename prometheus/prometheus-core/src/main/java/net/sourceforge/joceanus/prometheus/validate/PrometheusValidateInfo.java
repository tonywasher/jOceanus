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
package net.sourceforge.joceanus.prometheus.validate;

import net.sourceforge.joceanus.prometheus.data.PrometheusDataInfoItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValidator;
import net.sourceforge.joceanus.prometheus.data.PrometheusStaticDataItem;

/**
 * Validator for infoItem.
 * @param <T> the InfoItem type
 */
public class PrometheusValidateInfo<T extends PrometheusDataInfoItem>
        implements PrometheusDataValidator<T> {

    @Override
    public void validate(final PrometheusDataItem pInfo) {
        final PrometheusDataInfoItem myInfo = (PrometheusDataInfoItem) pInfo;
        final PrometheusStaticDataItem myType = myInfo.getInfoType();
        final PrometheusDataItem myOwner = myInfo.getOwner();
        final Object myValue = myInfo.getValue(Object.class);

        /* InfoType must be non-null */
        if (myType == null) {
            pInfo.addError(PrometheusDataItem.ERROR_MISSING, PrometheusDataResource.DATAINFO_TYPE);
        }

        /* Owner must be non-null */
        if (myOwner == null) {
            pInfo.addError(PrometheusDataItem.ERROR_MISSING, PrometheusDataResource.DATAINFO_OWNER);
        }

        /* Value must be non-null */
        if (myValue == null) {
            pInfo.addError(PrometheusDataItem.ERROR_MISSING, PrometheusDataResource.DATAINFO_VALUE);
        }

        /* Set validation flag */
        if (!pInfo.hasErrors()) {
            pInfo.setValidEdit();
        }
    }
}
