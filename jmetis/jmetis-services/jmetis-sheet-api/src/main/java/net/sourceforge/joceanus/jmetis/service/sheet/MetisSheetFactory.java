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

import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * WorkBook Factory.
 */
public interface MetisSheetFactory {
    /**
     * Load readOnly workBook from inputStream.
     * @param pInput the input stream
     * @return the loaded workBook
     * @throws OceanusException on error
     */
    MetisSheetWorkBook loadFromStream(InputStream pInput) throws OceanusException;

    /**
     * Create empty workBook.
     * @return the new workBook
     * @throws OceanusException on error
     */
    MetisSheetWorkBook newWorkBook() throws OceanusException;
}
