/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012, 2018 Tony Washer
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
package net.sourceforge.joceanus.jmetis.service.sheet;

import java.io.InputStream;
import java.util.ServiceLoader;

import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * SpreadSheet Factory Provider.
 */
public final class MetisSheetProvider {
    /**
     * Obtain a cache of the factories.
     */
    private static final ServiceLoader<MetisSheetFactory> LOADER = ServiceLoader.load(MetisSheetFactory.class);

    /**
     * Private constructor.
     */
    private MetisSheetProvider() {
    }

    /**
     * Instantiate the required factory.
     * @param pType the workBook type
     * @return the factory
     * @throws OceanusException on error
     */
    private static MetisSheetFactory newFactory(final MetisSheetWorkBookType pType) throws OceanusException {
        /* Locate the required factory */
        for (MetisSheetFactory myFactory : LOADER) {
            final MetisSheetService myService = myFactory.getClass().getAnnotation(MetisSheetService.class);
            if (myService != null && pType.equals(myService.value())) {
                return myFactory;
            }
        }
        throw new MetisSheetException("Unknown WorkBookType - " + pType);
    }

    /**
     * Load readOnly workBook from inputStream.
     * @param pType the workBook type
     * @param pInput the input stream
     * @return the loaded workBook
     * @throws OceanusException on error
     */
    public static MetisSheetWorkBook loadFromStream(final MetisSheetWorkBookType pType,
                                                    final InputStream pInput) throws OceanusException {
        final MetisSheetFactory myFactory = newFactory(pType);
        return myFactory.loadFromStream(pInput);
    }

    /**
     * Create empty workBook.
     * @param pType the workBook type
     * @return the new workBook
     * @throws OceanusException on error
     */
    public static MetisSheetWorkBook newWorkBook(final MetisSheetWorkBookType pType) throws OceanusException {
        final MetisSheetFactory myFactory = newFactory(pType);
        return myFactory.newWorkBook();

    }
}
