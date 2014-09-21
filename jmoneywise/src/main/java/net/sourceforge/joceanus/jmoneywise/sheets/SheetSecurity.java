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

import net.sourceforge.joceanus.jmetis.sheet.DataCell;
import net.sourceforge.joceanus.jmetis.sheet.DataRow;
import net.sourceforge.joceanus.jmetis.sheet.DataView;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseLogicException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Security;
import net.sourceforge.joceanus.jmoneywise.data.Security.SecurityList;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.sheets.SheetEncrypted;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * SheetDataItem extension for Security.
 * @author Tony Washer
 */
public class SheetSecurity
        extends SheetEncrypted<Security, MoneyWiseDataType> {
    /**
     * NamedArea for Securities.
     */
    private static final String AREA_SECURITIES = Security.LIST_NAME;

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
     * Symbol column.
     */
    private static final int COL_SYMBOL = COL_PARENT + 1;

    /**
     * Description column.
     */
    private static final int COL_DESC = COL_SYMBOL + 1;

    /**
     * Currency column.
     */
    private static final int COL_CURRENCY = COL_DESC + 1;

    /**
     * Closed column.
     */
    private static final int COL_CLOSED = COL_CURRENCY + 1;

    /**
     * Security data list.
     */
    private final SecurityList theList;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetSecurity(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_SECURITIES);

        /* Access the Securities list */
        theList = pReader.getData().getSecurities();
        setDataList(theList);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetSecurity(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_SECURITIES);

        /* Access the Securities list */
        theList = pWriter.getData().getSecurities();
        setDataList(theList);
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadSecureValues() throws JOceanusException {
        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(Security.OBJECT_NAME);
        myValues.addValue(Security.FIELD_SECTYPE, loadInteger(COL_TYPE));
        myValues.addValue(Security.FIELD_PARENT, loadInteger(COL_PARENT));
        myValues.addValue(Security.FIELD_CURRENCY, loadInteger(COL_CURRENCY));
        myValues.addValue(Security.FIELD_NAME, loadBytes(COL_NAME));
        myValues.addValue(Security.FIELD_DESC, loadBytes(COL_DESC));
        myValues.addValue(Security.FIELD_SYMBOL, loadBytes(COL_SYMBOL));
        myValues.addValue(Security.FIELD_CLOSED, loadBoolean(COL_CLOSED));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final Security pItem) throws JOceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeInteger(COL_TYPE, pItem.getSecurityTypeId());
        writeInteger(COL_PARENT, pItem.getParentId());
        writeInteger(COL_CURRENCY, pItem.getSecurityCurrencyId());
        writeBytes(COL_NAME, pItem.getNameBytes());
        writeBytes(COL_DESC, pItem.getDescBytes());
        writeBytes(COL_SYMBOL, pItem.getSymbolBytes());
        writeBoolean(COL_CLOSED, pItem.isClosed());
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_CLOSED;
    }

    /**
     * Process security row from archive.
     * @param pLoader the archive loader
     * @param pData the DataSet
     * @param pView the spreadsheet view
     * @param pRow the spreadsheet row
     * @throws JOceanusException on error
     */
    protected static void processSecurity(final ArchiveLoader pLoader,
                                          final MoneyWiseData pData,
                                          final DataView pView,
                                          final DataRow pRow) throws JOceanusException {
        /* Access name and type */
        int iAdjust = 0;
        String myName = pView.getRowCellByIndex(pRow, iAdjust++).getStringValue();
        String myType = pView.getRowCellByIndex(pRow, iAdjust++).getStringValue();

        /* Look for separator in category */
        int iIndex = myType.indexOf(TransactionCategory.STR_SEP);
        if (iIndex == -1) {
            throw new JMoneyWiseLogicException("Unexpected Security Class " + myType);
        }

        /* Access subCategory as security type */
        String mySecType = myType.substring(iIndex + 1);

        /* Skip class, taxFree and gross */
        iAdjust++;
        iAdjust++;
        iAdjust++;

        /* Handle closed which may be missing */
        DataCell myCell = pView.getRowCellByIndex(pRow, iAdjust++);
        Boolean isClosed = Boolean.FALSE;
        if (myCell != null) {
            isClosed = myCell.getBooleanValue();
        }

        /* Access the list */
        SecurityList myList = pData.getSecurities();

        /* Access Parent account */
        String myParent = pView.getRowCellByIndex(pRow, iAdjust++).getStringValue();

        /* Access the alias account */
        myCell = pView.getRowCellByIndex(pRow, iAdjust++);
        String myAlias = null;
        if (myCell != null) {
            myAlias = myCell.getStringValue();
        }

        /* Access Portfolio */
        String myPortfolio = pView.getRowCellByIndex(pRow, iAdjust++).getStringValue();

        /* If we have an alias */
        if (myAlias != null) {
            /* Locate the target security */
            Security myTarget = myList.findItemByName(myAlias);

            /* Declare the security alias */
            pLoader.declareSecurityHolding(myName, myTarget, myPortfolio);

            /* return */
            return;
        }

        /* Skip holding,maturity and opening columns */
        iAdjust++;
        iAdjust++;
        iAdjust++;

        /* Access Symbol */
        String mySymbol = pView.getRowCellByIndex(pRow, iAdjust++).getStringValue();

        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = new DataValues<MoneyWiseDataType>(Security.OBJECT_NAME);
        myValues.addValue(Security.FIELD_NAME, myName);
        myValues.addValue(Security.FIELD_SECTYPE, mySecType);
        myValues.addValue(Security.FIELD_CURRENCY, pData.getDefaultCurrency());
        myValues.addValue(Security.FIELD_PARENT, myParent);
        myValues.addValue(Security.FIELD_SYMBOL, mySymbol);
        myValues.addValue(Security.FIELD_CLOSED, isClosed);

        /* Add the value into the list */
        Security mySecurity = myList.addValuesItem(myValues);

        /* Declare the security holding */
        pLoader.declareSecurityHolding(mySecurity, myPortfolio);
    }
}
