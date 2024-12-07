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
package net.sourceforge.joceanus.prometheus.service.sheet.odf;

import java.io.InputStream;

import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetFactory;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetService;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetWorkBook;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetWorkBookType;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.tethys.ui.api.factory.TethysUIFactory;

/**
 * Factory to load/initialise an Oasis ODF WorkBook.
 */
@PrometheusSheetService(PrometheusSheetWorkBookType.OASIS)
public class PrometheusOdfFactory
        implements PrometheusSheetFactory {
    @Override
    public PrometheusSheetWorkBook loadFromStream(final TethysUIFactory<?> pFactory,
                                                  final InputStream pInput) throws OceanusException {
        return new PrometheusOdfWorkBook(pFactory, pInput);
    }

    @Override
    public PrometheusSheetWorkBook newWorkBook(final TethysUIFactory<?> pFactory) throws OceanusException {
        return new PrometheusOdfWorkBook(pFactory);
    }
}
