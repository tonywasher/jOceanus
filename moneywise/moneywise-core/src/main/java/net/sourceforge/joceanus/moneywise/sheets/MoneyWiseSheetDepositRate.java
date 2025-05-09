/*******************************************************************************
 * MoneyWise: Finance Application
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
package net.sourceforge.joceanus.moneywise.sheets;

import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDepositRate;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.prometheus.sheets.PrometheusSheetEncrypted;

/**
 * SheetDataItem extension for DepositRate.
 * @author Tony Washer
 */
public final class MoneyWiseSheetDepositRate
        extends PrometheusSheetEncrypted<MoneyWiseDepositRate> {
    /**
     * NamedArea for Rates.
     */
    private static final String AREA_RATES = MoneyWiseDepositRate.LIST_NAME;

    /**
     * Deposit column.
     */
    private static final int COL_DEPOSIT = COL_KEYSETID + 1;

    /**
     * Rate column.
     */
    private static final int COL_RATE = COL_DEPOSIT + 1;

    /**
     * Bonus column.
     */
    private static final int COL_BONUS = COL_RATE + 1;

    /**
     * EndDate column.
     */
    private static final int COL_ENDDATE = COL_BONUS + 1;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    MoneyWiseSheetDepositRate(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_RATES);

        /* Access the Rates list */
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pReader.getData();
        setDataList(myData.getDepositRates());
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    MoneyWiseSheetDepositRate(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_RATES);

        /* Access the Rates list */
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pWriter.getData();
        setDataList(myData.getDepositRates());
    }

    @Override
    protected PrometheusDataValues loadSecureValues() throws OceanusException {
        /* Build data values */
        final PrometheusDataValues myValues = getRowValues(MoneyWiseDepositRate.OBJECT_NAME);
        myValues.addValue(MoneyWiseBasicDataType.DEPOSIT, loadInteger(COL_DEPOSIT));
        myValues.addValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_RATE, loadBytes(COL_RATE));
        myValues.addValue(MoneyWiseBasicResource.DEPOSITRATE_BONUS, loadBytes(COL_BONUS));
        myValues.addValue(MoneyWiseBasicResource.DEPOSITRATE_ENDDATE, loadDate(COL_ENDDATE));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final MoneyWiseDepositRate pItem) throws OceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeInteger(COL_DEPOSIT, pItem.getDepositId());
        writeBytes(COL_RATE, pItem.getRateBytes());
        writeBytes(COL_BONUS, pItem.getBonusBytes());
        writeDate(COL_ENDDATE, pItem.getEndDate());
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_ENDDATE;
    }
}
