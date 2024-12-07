/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.prometheus.service.sheet;

import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.tethys.ui.api.factory.TethysUIFactory;

import java.io.InputStream;

/**
 * WorkBook Factory.
 */
public interface PrometheusSheetFactory {
    /**
     * Load readOnly workBook from inputStream.
     * @param pFactory the gui factory
     * @param pInput the input stream
     * @return the loaded workBook
     * @throws OceanusException on error
     */
    PrometheusSheetWorkBook loadFromStream(TethysUIFactory<?> pFactory,
                                           InputStream pInput) throws OceanusException;

    /**
     * Create empty workBook.
     * @param pFactory the gui factory
     * @return the new workBook
     * @throws OceanusException on error
     */
    PrometheusSheetWorkBook newWorkBook(TethysUIFactory<?> pFactory) throws OceanusException;
}
