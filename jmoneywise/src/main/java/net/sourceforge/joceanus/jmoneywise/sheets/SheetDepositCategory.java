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
import net.sourceforge.joceanus.jmoneywise.data.DepositCategory;
import net.sourceforge.joceanus.jmoneywise.data.DepositCategory.DepositCategoryList;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.sheets.SheetEncrypted;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * SheetDataItem extension for DepositCategory.
 * @author Tony Washer
 */
public class SheetDepositCategory
        extends SheetEncrypted<DepositCategory, MoneyWiseDataType> {
    /**
     * NamedArea for Categories.
     */
    protected static final String AREA_DEPCATEGORIES = DepositCategory.LIST_NAME;

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
     * Category data list.
     */
    private final DepositCategoryList theList;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetDepositCategory(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_DEPCATEGORIES);

        /* Access the Categories list */
        theList = pReader.getData().getDepositCategories();
        setDataList(theList);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetDepositCategory(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_DEPCATEGORIES);

        /* Access the Categories list */
        theList = pWriter.getData().getDepositCategories();
        setDataList(theList);
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadSecureValues() throws JOceanusException {
        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(DepositCategory.OBJECT_NAME);
        myValues.addValue(DepositCategory.FIELD_CATTYPE, loadInteger(COL_TYPE));
        myValues.addValue(DepositCategory.FIELD_PARENT, loadInteger(COL_PARENT));
        myValues.addValue(DepositCategory.FIELD_NAME, loadBytes(COL_NAME));
        myValues.addValue(DepositCategory.FIELD_DESC, loadBytes(COL_DESC));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final DepositCategory pItem) throws JOceanusException {
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

    @Override
    protected void postProcessOnLoad() throws JOceanusException {
        /* Resolve links and reSort */
        theList.resolveDataSetLinks();
        theList.reSort();

        /* Validate the account categories */
        theList.validateOnLoad();
    }
}