/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.service.sheet;

import java.io.OutputStream;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysDataFormatter;

/**
 * Metis Sheet Service interface.
 */
public interface MetisSheetWorkBook {
    /**
     * Is the WorkBook readOnly?
     * @return true/false
     */
    boolean isReadOnly();

    /**
     * Save the workBook to output stream.
     * @param pOutput the output stream
     * @throws OceanusException on error
     */
    void saveToStream(OutputStream pOutput) throws OceanusException;

    /**
     * Create a new Sheet with the given name.
     * @param pName the name of the new sheet
     * @return the new sheet
     * @throws OceanusException on error
     */
    MetisSheetSheet newSheet(String pName) throws OceanusException;

    /**
     * Create a new Sheet with the given name.
     * @param pName the name of the new sheet
     * @param pNumRows the number of rows to allocate
     * @param pNumCols the number of columns to allocate
     * @return the new sheet
     * @throws OceanusException on error
     */
    MetisSheetSheet newSheet(String pName,
                             int pNumRows,
                             int pNumCols) throws OceanusException;

    /**
     * Access an existing Sheet with the given name.
     * @param pName the name of the sheet
     * @return the sheet (or null if no such sheet)
     * @throws OceanusException on error
     */
    MetisSheetSheet getSheet(String pName) throws OceanusException;

    /**
     * Obtain a view of the named range.
     * @param pName the name of the range
     * @return the view of the range
     * @throws OceanusException on error
     */
    MetisSheetView getRangeView(String pName) throws OceanusException;

    /**
     * Create data formatter.
     * @return the new formatter
     */
    default TethysDataFormatter createFormatter() {
        /* Allocate the formatter and set date format */
        final TethysDataFormatter myFormatter = new TethysDataFormatter();
        myFormatter.setFormat(MetisSheetFormats.OASIS_DATE);
        myFormatter.setAccountingWidth(MetisSheetFormats.ACCOUNTING_WIDTH);

        /* return the formatter */
        return myFormatter;
    }
}
