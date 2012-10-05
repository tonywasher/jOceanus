/*******************************************************************************
 * Copyright 2012 Tony Washer
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
package net.sourceforge.JFinanceApp.sheets;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataModels.sheets.SheetDataInfo;
import net.sourceforge.JFinanceApp.data.TaxYearInfo;
import net.sourceforge.JFinanceApp.data.TaxYearInfo.TaxInfoList;

/**
 * SheetDataInfo extension for TaxYearInfo.
 * @author Tony Washer
 */
public class SheetTaxYearInfo extends SheetDataInfo<TaxYearInfo> {
    /**
     * NamedArea for TaxYearInfo.
     */
    private static final String AREA_TAXYEARINFO = TaxYearInfo.LIST_NAME;

    /**
     * TaxYearInfo data list.
     */
    private final TaxInfoList theList;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetTaxYearInfo(final FinanceReader pReader) {
        /* Call super-constructor */
        super(pReader, AREA_TAXYEARINFO);

        /* Access the InfoType list */
        theList = pReader.getData().getTaxInfo();
        setDataList(theList);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetTaxYearInfo(final FinanceWriter pWriter) {
        /* Call super-constructor */
        super(pWriter, AREA_TAXYEARINFO);

        /* Access the InfoType list */
        theList = pWriter.getData().getTaxInfo();
        setDataList(theList);
    }

    @Override
    protected void loadEncryptedItem(final int pId,
                                     final int pControlId,
                                     final int pInfoTypeId,
                                     final int pOwnerId,
                                     final byte[] pValue) throws JDataException {
        /* Create the item */
        theList.addSecureItem(pId, pControlId, pInfoTypeId, pOwnerId, pValue);
    }
}
