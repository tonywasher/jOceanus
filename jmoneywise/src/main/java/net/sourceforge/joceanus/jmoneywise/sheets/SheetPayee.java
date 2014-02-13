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
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Payee;
import net.sourceforge.joceanus.jmoneywise.data.Payee.PayeeList;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.TaskControl;
import net.sourceforge.joceanus.jprometheus.sheets.SheetEncrypted;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * SheetDataItem extension for Payee.
 * @author Tony Washer
 */
public class SheetPayee
        extends SheetEncrypted<Payee, MoneyWiseDataType> {
    /**
     * NamedArea for Payees.
     */
    private static final String AREA_PAYEES = Payee.LIST_NAME;

    /**
     * NameList for Payees.
     */
    protected static final String AREA_PAYEENAMES = Payee.OBJECT_NAME + "Names";

    /**
     * Name column.
     */
    private static final int COL_NAME = COL_CONTROLID + 1;

    /**
     * Type column.
     */
    private static final int COL_TYPE = COL_NAME + 1;

    /**
     * Description column.
     */
    private static final int COL_DESC = COL_TYPE + 1;

    /**
     * Closed column.
     */
    private static final int COL_CLOSED = COL_DESC + 1;

    /**
     * Payee data list.
     */
    private final PayeeList theList;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetPayee(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_PAYEES);

        /* Access the Payees list */
        theList = pReader.getData().getPayees();
        setDataList(theList);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetPayee(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_PAYEES);

        /* Access the Payees list */
        theList = pWriter.getData().getPayees();
        setDataList(theList);
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadSecureValues() throws JOceanusException {
        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getSecureRowValues(Payee.OBJECT_NAME);
        myValues.addValue(Payee.FIELD_PAYEETYPE, loadInteger(COL_TYPE));
        myValues.addValue(Payee.FIELD_NAME, loadBytes(COL_NAME));
        myValues.addValue(Payee.FIELD_DESC, loadBytes(COL_DESC));
        myValues.addValue(Payee.FIELD_CLOSED, loadBoolean(COL_CLOSED));

        /* Return the values */
        return myValues;
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadOpenValues() throws JOceanusException {
        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(Payee.OBJECT_NAME);
        myValues.addValue(Payee.FIELD_PAYEETYPE, loadString(COL_TYPE));
        myValues.addValue(Payee.FIELD_NAME, loadString(COL_NAME));
        myValues.addValue(Payee.FIELD_DESC, loadString(COL_DESC));
        myValues.addValue(Payee.FIELD_CLOSED, loadBoolean(COL_CLOSED));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final Payee pItem) throws JOceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeInteger(COL_TYPE, pItem.getPayeeTypeId());
        writeBytes(COL_NAME, pItem.getNameBytes());
        writeBytes(COL_DESC, pItem.getDescBytes());
        writeBoolean(COL_CLOSED, pItem.isClosed());
    }

    @Override
    protected void insertOpenItem(final Payee pItem) throws JOceanusException {
        /* Set the fields */
        super.insertOpenItem(pItem);
        writeString(COL_TYPE, pItem.getPayeeTypeName());
        writeString(COL_NAME, pItem.getName());
        writeString(COL_DESC, pItem.getDesc());
        writeBoolean(COL_CLOSED, pItem.isClosed());
    }

    @Override
    protected void prepareSheet() throws JOceanusException {
        /* Write titles */
        writeHeader(COL_TYPE, Payee.FIELD_PAYEETYPE.getName());
        writeHeader(COL_NAME, Payee.FIELD_NAME.getName());
        writeHeader(COL_DESC, Payee.FIELD_DESC.getName());
        writeHeader(COL_CLOSED, Payee.FIELD_CLOSED.getName());
    }

    @Override
    protected void formatSheet() throws JOceanusException {
        /* Set the column types */
        setStringColumn(COL_NAME);
        setStringColumn(COL_DESC);
        setStringColumn(COL_TYPE);
        setBooleanColumn(COL_CLOSED);

        /* Set the name column range */
        nameColumnRange(COL_NAME, AREA_PAYEENAMES);

        /* Set validation */
        applyDataValidation(COL_TYPE, SheetPayeeType.AREA_PAYEETYPENAMES);
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

        /* Validate the payees */
        theList.validateOnLoad();
    }

    /**
     * Load the Payees from an archive.
     * @param pTask the task control
     * @param pWorkBook the workbook
     * @param pData the data set to load into
     * @return continue to load <code>true/false</code>
     * @throws JOceanusException on error
     */
    protected static boolean loadArchive(final TaskControl<MoneyWiseData> pTask,
                                         final DataWorkBook pWorkBook,
                                         final MoneyWiseData pData) throws JOceanusException {
        /* Access the list of payees */
        PayeeList myList = pData.getPayees();

        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            DataView myView = pWorkBook.getRangeView(AREA_PAYEES);

            /* Access the number of reporting steps */
            int mySteps = pTask.getReportingSteps();
            int myCount = 0;

            /* Declare the new stage */
            if (!pTask.setNewStage(Payee.LIST_NAME)) {
                return false;
            }

            /* Count the number of Payees */
            int myTotal = myView.getRowCount();

            /* Declare the number of steps */
            if (!pTask.setNumSteps(myTotal)) {
                return false;
            }

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

                /* Access Closed Flag */
                myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                Boolean isClosed = myCell.getBooleanValue();

                /* Build data values */
                DataValues<MoneyWiseDataType> myValues = new DataValues<MoneyWiseDataType>(Payee.OBJECT_NAME);
                myValues.addValue(Payee.FIELD_PAYEETYPE, myType);
                myValues.addValue(Payee.FIELD_NAME, myName);
                myValues.addValue(Payee.FIELD_CLOSED, isClosed);

                /* Add the value into the list */
                myList.addValuesItem(myValues);

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
            throw new JMoneyWiseIOException("Failed to Load " + myList.getItemType().getListName(), e);
        }

        /* Return to caller */
        return true;
    }
}
