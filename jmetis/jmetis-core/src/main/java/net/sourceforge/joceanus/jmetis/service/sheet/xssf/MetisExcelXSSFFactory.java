/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2017 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jmetis.service.sheet.xssf;

import java.io.InputStream;

import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetFactory;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetService;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetWorkBook;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetWorkBookType;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Factory to load/initialise an XSSF WorkBook.
 */
@MetisSheetService(MetisSheetWorkBookType.EXCELXLSX)
public class MetisExcelXSSFFactory
        implements MetisSheetFactory {
    @Override
    public MetisSheetWorkBook loadFromStream(final InputStream pInput) throws OceanusException {
        return new MetisExcelXSSFWorkBook(pInput);
    }

    @Override
    public MetisSheetWorkBook newWorkBook() throws OceanusException {
        return new MetisExcelXSSFWorkBook();
    }
}
