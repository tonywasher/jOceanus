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
package io.github.tonywasher.joceanus.prometheus.service.sheet;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;

/**
 * Sheet Service interface.
 */
public interface PrometheusSheetWorkBook
        extends PrometheusSheetWorkBookCtl {
    @Override
    PrometheusSheetSheet newSheet(String pName) throws OceanusException;

    @Override
    PrometheusSheetSheet newSheet(String pName,
                                  int pNumRows,
                                  int pNumCols) throws OceanusException;

    @Override
    PrometheusSheetSheet getSheet(String pName) throws OceanusException;

    @Override
    PrometheusSheetView getRangeView(String pName) throws OceanusException;
}
