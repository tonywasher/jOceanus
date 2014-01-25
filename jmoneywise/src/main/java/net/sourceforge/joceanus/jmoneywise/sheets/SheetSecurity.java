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
import net.sourceforge.joceanus.jmetis.sheet.DataWorkBook;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseIOException;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.Security;
import net.sourceforge.joceanus.jmoneywise.data.Security.SecurityList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency;
import net.sourceforge.joceanus.jprometheus.data.TaskControl;
import net.sourceforge.joceanus.jprometheus.sheets.SheetDataItem;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * SheetDataItem extension for Security.
 * @author Tony Washer
 */
public class SheetSecurity
        extends SheetDataItem<Security, MoneyWiseDataType> {
    /**
     * NamedArea for Securities.
     */
    private static final String AREA_SECURITIES = Security.LIST_NAME;

    /**
     * NameList for Securities.
     */
    protected static final String AREA_SECURITYNAMES = Security.OBJECT_NAME + "Names";

    /**
     * Name column.
     */
    private static final int COL_NAME = COL_CONTROLID + 1;

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
    protected void loadSecureItem(final Integer pId) throws JOceanusException {
        /* Access the IDs */
        Integer myControlId = loadInteger(COL_CONTROLID);
        Integer myTypeId = loadInteger(COL_TYPE);
        Integer myParentId = loadInteger(COL_PARENT);
        Integer myCurrencyId = loadInteger(COL_CURRENCY);

        /* Access the Name and description */
        byte[] myNameBytes = loadBytes(COL_NAME);
        byte[] myDescBytes = loadBytes(COL_DESC);
        byte[] mySymbol = loadBytes(COL_SYMBOL);

        /* Access the Flags */
        Boolean isClosed = loadBoolean(COL_CLOSED);

        /* Load the item */
        theList.addSecureItem(pId, myControlId, myNameBytes, myDescBytes, myTypeId, myParentId, mySymbol, myCurrencyId, isClosed);
    }

    @Override
    protected void loadOpenItem(final Integer pId) throws JOceanusException {
        /* Access the name */
        String myType = loadString(COL_TYPE);
        String myParent = loadString(COL_PARENT);
        String myCurrency = loadString(COL_CURRENCY);

        /* Access the name and description bytes */
        String myName = loadString(COL_NAME);
        String myDesc = loadString(COL_DESC);
        String mySymbol = loadString(COL_SYMBOL);

        /* Access the Flags */
        Boolean isClosed = loadBoolean(COL_CLOSED);

        /* Load the item */
        theList.addOpenItem(pId, myName, myDesc, myType, myParent, mySymbol, myCurrency, isClosed);
    }

    @Override
    protected void insertSecureItem(final Security pItem) throws JOceanusException {
        /* Set the fields */
        writeInteger(COL_CONTROLID, pItem.getControlKeyId());
        writeInteger(COL_TYPE, pItem.getSecurityTypeId());
        writeInteger(COL_PARENT, pItem.getParentId());
        writeInteger(COL_CURRENCY, pItem.getSecurityCurrencyId());
        writeBytes(COL_NAME, pItem.getNameBytes());
        writeBytes(COL_DESC, pItem.getDescBytes());
        writeBytes(COL_SYMBOL, pItem.getSymbolBytes());
        writeBoolean(COL_CLOSED, pItem.isClosed());
    }

    @Override
    protected void insertOpenItem(final Security pItem) throws JOceanusException {
        /* Set the fields */
        writeString(COL_TYPE, pItem.getSecurityTypeName());
        writeString(COL_PARENT, pItem.getParentName());
        writeString(COL_CURRENCY, pItem.getSecurityCurrencyName());
        writeString(COL_NAME, pItem.getName());
        writeString(COL_DESC, pItem.getDesc());
        writeString(COL_SYMBOL, pItem.getSymbol());
        writeBoolean(COL_CLOSED, pItem.isClosed());
    }

    @Override
    protected void prepareSheet() throws JOceanusException {
        /* Write titles */
        writeHeader(COL_TYPE, Security.FIELD_SECTYPE.getName());
        writeHeader(COL_PARENT, Security.FIELD_PARENT.getName());
        writeHeader(COL_CURRENCY, Security.FIELD_CURRENCY.getName());
        writeHeader(COL_NAME, Security.FIELD_NAME.getName());
        writeHeader(COL_DESC, Security.FIELD_DESC.getName());
        writeHeader(COL_SYMBOL, Security.FIELD_SYMBOL.getName());
        writeHeader(COL_CLOSED, Security.FIELD_CLOSED.getName());
    }

    @Override
    protected void formatSheet() throws JOceanusException {
        /* Set the column types */
        setStringColumn(COL_NAME);
        setStringColumn(COL_DESC);
        setStringColumn(COL_TYPE);
        setStringColumn(COL_PARENT);
        setStringColumn(COL_SYMBOL);
        setStringColumn(COL_CURRENCY);
        setBooleanColumn(COL_CLOSED);

        /* Set the name column range */
        nameColumnRange(COL_NAME, AREA_SECURITYNAMES);

        /* Set validation */
        applyDataValidation(COL_TYPE, SheetSecurityType.AREA_SECURITYTYPENAMES);
        applyDataValidation(COL_CURRENCY, SheetAccountCurrency.AREA_ACCOUNTCURRNAMES);
        applyDataValidation(COL_PARENT, SheetPayee.AREA_PAYEENAMES);
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_CLOSED;
    }

    @Override
    protected void postProcessOnLoad() throws JOceanusException {
        /* Resolve links and reSort */
        theList.resolveDataSetLinks();
        theList.reSort();

        /* Touch underlying items */
        theList.touchUnderlyingItems();

        /* Validate the securities */
        theList.validateOnLoad();
    }

    /**
     * Load the Securities from an archive.
     * @param pTask the task control
     * @param pWorkBook the workbook
     * @param pData the data set to load into
     * @return continue to load <code>true/false</code>
     * @throws JOceanusException on error
     */
    protected static boolean loadArchive(final TaskControl<MoneyWiseData> pTask,
                                         final DataWorkBook pWorkBook,
                                         final MoneyWiseData pData) throws JOceanusException {
        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            DataView myView = pWorkBook.getRangeView(AREA_SECURITIES);

            /* Access the number of reporting steps */
            int mySteps = pTask.getReportingSteps();
            int myCount = 0;

            /* Declare the new stage */
            if (!pTask.setNewStage(Security.LIST_NAME)) {
                return false;
            }

            /* Count the number of securities */
            int myTotal = myView.getRowCount();

            /* Access the list of securities */
            SecurityList myList = pData.getSecurities();

            /* Declare the number of steps */
            if (!pTask.setNumSteps(myTotal)) {
                return false;
            }

            /* Access default currency */
            AccountCurrency myCurrency = pData.getDefaultCurrency();

            /* Loop through the rows of the table */
            for (int i = 0; i < myTotal; i++) {
                /* Access the cell by reference */
                DataRow myRow = myView.getRowByIndex(i);
                int iAdjust = 0;

                /* Access name */
                DataCell myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                String myName = myCell.getStringValue();

                /* Access Type */
                myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                String myType = myCell.getStringValue();

                /* Access Symbol */
                myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                String mySymbol = myCell.getStringValue();

                /* Access Parent */
                myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                String myParent = myCell.getStringValue();

                /* Access Closed Flag */
                myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                Boolean isClosed = myCell.getBooleanValue();

                /* Add the value into the finance tables */
                myList.addOpenItem(0, myName, null, myType, myParent, mySymbol, myCurrency.getName(), isClosed);

                /* Report the progress */
                myCount++;
                if (((myCount % mySteps) == 0) && (!pTask.setStepsDone(myCount))) {
                    return false;
                }
            }

            /* Resolve links and reSort */
            myList.resolveDataSetLinks();
            myList.reSort();

            /* Touch underlying items */
            myList.touchUnderlyingItems();

            /* Validate the event categories */
            myList.validateOnLoad();

            /* Handle exceptions */
        } catch (JOceanusException e) {
            throw new JMoneyWiseIOException("Failed to Load Securities", e);
        }

        /* Return to caller */
        return true;
    }
}
