/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.sheets;

import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear.TaxYearList;
import net.sourceforge.joceanus.jmoneywise.data.TaxYearInfo;
import net.sourceforge.joceanus.jmoneywise.data.TaxYearInfo.TaxInfoList;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.sheets.SheetDataInfo;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * SheetDataInfo extension for TaxYearInfo.
 * @author Tony Washer
 */
public class SheetTaxYearInfo
        extends SheetDataInfo<TaxYearInfo, MoneyWiseDataType> {
    /**
     * NamedArea for TaxYearInfo.
     */
    private static final String AREA_TAXYEARINFO = TaxYearInfo.LIST_NAME;

    /**
     * TaxYear data list.
     */
    private final TaxYearList theTaxYears;

    /**
     * TaxYearInfo data list.
     */
    private final TaxInfoList theList;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetTaxYearInfo(final MoneyWiseReader pReader) {
        /* Call super-constructor */
        super(pReader, AREA_TAXYEARINFO);

        /* Access the InfoType list */
        MoneyWiseData myData = pReader.getData();
        theTaxYears = myData.getTaxYears();
        theList = myData.getTaxInfo();
        setDataList(theList);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetTaxYearInfo(final MoneyWiseWriter pWriter) {
        /* Call super-constructor */
        super(pWriter, AREA_TAXYEARINFO);

        /* Access the InfoType list */
        theTaxYears = null;
        theList = pWriter.getData().getTaxInfo();
        setDataList(theList);
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadSecureValues() throws JOceanusException {
        /* Build data values */
        return getRowValues(TaxYearInfo.OBJECT_NAME);
    }

    @Override
    protected void postProcessOnLoad() throws JOceanusException {
        /* Validate info */
        theList.validateOnLoad();

        /* Validate the tax years */
        theTaxYears.validateOnLoad();
    }
}
