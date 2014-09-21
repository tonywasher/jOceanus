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
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.data.Deposit.DepositList;
import net.sourceforge.joceanus.jmoneywise.data.DepositInfo.DepositInfoList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.sheets.SheetEncrypted;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;

/**
 * SheetDataItem extension for Deposit.
 * @author Tony Washer
 */
public class SheetDeposit
        extends SheetEncrypted<Deposit, MoneyWiseDataType> {
    /**
     * NamedArea for Deposits.
     */
    private static final String AREA_DEPOSITS = Deposit.LIST_NAME;

    /**
     * Name column.
     */
    private static final int COL_NAME = COL_KEYSETID + 1;

    /**
     * Category column.
     */
    private static final int COL_CATEGORY = COL_NAME + 1;

    /**
     * Parent column.
     */
    private static final int COL_PARENT = COL_CATEGORY + 1;

    /**
     * Description column.
     */
    private static final int COL_DESC = COL_PARENT + 1;

    /**
     * Currency column.
     */
    private static final int COL_CURRENCY = COL_DESC + 1;

    /**
     * Gross column.
     */
    private static final int COL_GROSS = COL_CURRENCY + 1;

    /**
     * TaxFree column.
     */
    private static final int COL_TAXFREE = COL_GROSS + 1;

    /**
     * Closed column.
     */
    private static final int COL_CLOSED = COL_TAXFREE + 1;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetDeposit(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_DEPOSITS);

        /* Access the Deposits list */
        MoneyWiseData myData = pReader.getData();
        setDataList(myData.getDeposits());
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetDeposit(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_DEPOSITS);

        /* Access the Deposits list */
        MoneyWiseData myData = pWriter.getData();
        setDataList(myData.getDeposits());
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadSecureValues() throws JOceanusException {
        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(Deposit.OBJECT_NAME);
        myValues.addValue(Deposit.FIELD_CATEGORY, loadInteger(COL_CATEGORY));
        myValues.addValue(Deposit.FIELD_PARENT, loadInteger(COL_PARENT));
        myValues.addValue(Deposit.FIELD_CURRENCY, loadInteger(COL_CURRENCY));
        myValues.addValue(Deposit.FIELD_NAME, loadBytes(COL_NAME));
        myValues.addValue(Deposit.FIELD_DESC, loadBytes(COL_DESC));
        myValues.addValue(Deposit.FIELD_GROSS, loadBoolean(COL_GROSS));
        myValues.addValue(Deposit.FIELD_TAXFREE, loadBoolean(COL_TAXFREE));
        myValues.addValue(Deposit.FIELD_CLOSED, loadBoolean(COL_CLOSED));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final Deposit pItem) throws JOceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeInteger(COL_CATEGORY, pItem.getCategoryId());
        writeInteger(COL_PARENT, pItem.getParentId());
        writeInteger(COL_CURRENCY, pItem.getDepositCurrencyId());
        writeBytes(COL_NAME, pItem.getNameBytes());
        writeBytes(COL_DESC, pItem.getDescBytes());
        writeBoolean(COL_GROSS, pItem.isGross());
        writeBoolean(COL_TAXFREE, pItem.isTaxFree());
        writeBoolean(COL_CLOSED, pItem.isClosed());
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_CLOSED;
    }

    /**
     * Process deposit row from archive.
     * @param pLoader the archive loader
     * @param pData the DataSet
     * @param pView the spreadsheet view
     * @param pRow the spreadsheet row
     * @throws JOceanusException on error
     */
    protected static void processDeposit(final ArchiveLoader pLoader,
                                         final MoneyWiseData pData,
                                         final DataView pView,
                                         final DataRow pRow) throws JOceanusException {
        /* Access name and type */
        int iAdjust = 0;
        String myName = pView.getRowCellByIndex(pRow, iAdjust++).getStringValue();
        String myType = pView.getRowCellByIndex(pRow, iAdjust++).getStringValue();

        /* Skip class */
        iAdjust++;

        /* Handle taxFree which may be missing */
        DataCell myCell = pView.getRowCellByIndex(pRow, iAdjust++);
        Boolean isTaxFree = Boolean.FALSE;
        if (myCell != null) {
            isTaxFree = myCell.getBooleanValue();
        }

        /* Handle gross which may be missing */
        myCell = pView.getRowCellByIndex(pRow, iAdjust++);
        Boolean isGross = Boolean.FALSE;
        if (myCell != null) {
            isGross = myCell.getBooleanValue();
        }

        /* Handle closed which may be missing */
        myCell = pView.getRowCellByIndex(pRow, iAdjust++);
        Boolean isClosed = Boolean.FALSE;
        if (myCell != null) {
            isClosed = myCell.getBooleanValue();
        }

        /* Access Parent account */
        String myParent = pView.getRowCellByIndex(pRow, iAdjust++).getStringValue();

        /* Skip alias, portfolio and holding columns */
        iAdjust++;
        iAdjust++;
        iAdjust++;

        /* Handle maturity which may be missing */
        myCell = pView.getRowCellByIndex(pRow, iAdjust++);
        JDateDay myMaturity = null;
        if (myCell != null) {
            myMaturity = myCell.getDateValue();
        }

        /* Handle opening balance which may be missing */
        myCell = pView.getRowCellByIndex(pRow, iAdjust++);
        String myBalance = null;
        if (myCell != null) {
            myBalance = myCell.getStringValue();
        }

        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = new DataValues<MoneyWiseDataType>(Deposit.OBJECT_NAME);
        myValues.addValue(Deposit.FIELD_NAME, myName);
        myValues.addValue(Deposit.FIELD_CATEGORY, myType);
        myValues.addValue(Deposit.FIELD_CURRENCY, pData.getDefaultCurrency());
        myValues.addValue(Deposit.FIELD_PARENT, myParent);
        myValues.addValue(Deposit.FIELD_GROSS, isGross);
        myValues.addValue(Deposit.FIELD_TAXFREE, isTaxFree);
        myValues.addValue(Deposit.FIELD_CLOSED, isClosed);

        /* Add the value into the list */
        DepositList myList = pData.getDeposits();
        Deposit myDeposit = myList.addValuesItem(myValues);

        /* Add information relating to the deposit */
        DepositInfoList myInfoList = pData.getDepositInfo();
        myInfoList.addInfoItem(null, myDeposit, AccountInfoClass.MATURITY, myMaturity);
        myInfoList.addInfoItem(null, myDeposit, AccountInfoClass.OPENINGBALANCE, myBalance);

        /* Declare the deposit */
        pLoader.declareAsset(myDeposit);
    }
}
