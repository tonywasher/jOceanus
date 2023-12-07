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

import net.sourceforge.joceanus.jmoneywise.lethe.data.LoanCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.sheets.PrometheusSheetXEncrypted;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * SheetDataItem extension for LoanCategory.
 * @author Tony Washer
 */
public class SheetLoanCategory
        extends PrometheusSheetXEncrypted<LoanCategory> {
    /**
     * NamedArea for Categories.
     */
    protected static final String AREA_LOANCATEGORIES = LoanCategory.LIST_NAME;

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
    protected SheetLoanCategory(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_LOANCATEGORIES);

        /* Access the Categories list */
        final MoneyWiseData myData = (MoneyWiseData) pReader.getData();
        setDataList(myData.getLoanCategories());
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetLoanCategory(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_LOANCATEGORIES);

        /* Access the Categories list */
        final MoneyWiseData myData = (MoneyWiseData) pWriter.getData();
        setDataList(myData.getLoanCategories());
    }

    @Override
    protected DataValues loadSecureValues() throws OceanusException {
        /* Build data values */
        final DataValues myValues = getRowValues(LoanCategory.OBJECT_NAME);
        myValues.addValue(LoanCategory.FIELD_CATTYPE, loadInteger(COL_TYPE));
        myValues.addValue(LoanCategory.FIELD_PARENT, loadInteger(COL_PARENT));
        myValues.addValue(LoanCategory.FIELD_NAME, loadBytes(COL_NAME));
        myValues.addValue(LoanCategory.FIELD_DESC, loadBytes(COL_DESC));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final LoanCategory pItem) throws OceanusException {
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
