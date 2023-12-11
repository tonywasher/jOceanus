/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.sheets;

import net.sourceforge.joceanus.jmoneywise.lethe.data.CashCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.sheets.PrometheusSheetXEncrypted;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * SheetDataItem extension for CashCategory.
 * @author Tony Washer
 */
public class SheetCashCategory
        extends PrometheusSheetXEncrypted<CashCategory> {
    /**
     * NamedArea for Categories.
     */
    protected static final String AREA_CASHCATEGORIES = CashCategory.LIST_NAME;

    /**
     * Name column.
     */
    private static final int COL_NAME = COL_KEYSETID + 1;

    /**
     * Type column.
     */
    private static final int COL_TYPE = COL_NAME + 1;

    /**
     * Parent column.
     */
    private static final int COL_PARENT = COL_TYPE + 1;

    /**
     * Description column.
     */
    private static final int COL_DESC = COL_PARENT + 1;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetCashCategory(final MoneyWiseXReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_CASHCATEGORIES);

        /* Access the Categories list */
        final MoneyWiseData myData = (MoneyWiseData) pReader.getData();
        setDataList(myData.getCashCategories());
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetCashCategory(final MoneyWiseXWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_CASHCATEGORIES);

        /* Access the Categories list */
        final MoneyWiseData myData = (MoneyWiseData) pWriter.getData();
        setDataList(myData.getCashCategories());
    }

    @Override
    protected DataValues loadSecureValues() throws OceanusException {
        /* Build data values */
        final DataValues myValues = getRowValues(CashCategory.OBJECT_NAME);
        myValues.addValue(CashCategory.FIELD_CATTYPE, loadInteger(COL_TYPE));
        myValues.addValue(CashCategory.FIELD_PARENT, loadInteger(COL_PARENT));
        myValues.addValue(CashCategory.FIELD_NAME, loadBytes(COL_NAME));
        myValues.addValue(CashCategory.FIELD_DESC, loadBytes(COL_DESC));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final CashCategory pItem) throws OceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeInteger(COL_TYPE, pItem.getCategoryTypeId());
        writeInteger(COL_PARENT, pItem.getParentCategoryId());
        writeBytes(COL_NAME, pItem.getNameBytes());
        writeBytes(COL_DESC, pItem.getDescBytes());
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_DESC;
    }
}
