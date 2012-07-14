/*******************************************************************************
 * JDataModels: Data models
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
package net.sourceforge.JDataModels.sheets;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataModels.data.DataKey;
import net.sourceforge.JDataModels.data.DataKey.DataKeyList;
import net.sourceforge.JDataModels.data.DataSet;

/**
 * SheetDataItem extension for DataKey.
 * @author Tony Washer
 */
public class SheetDataKey extends SheetDataItem<DataKey> {
    /**
     * SheetName for Keys.
     */
    private static final String SHEET_NAME = DataKey.class.getSimpleName();

    /**
     * Number of columns.
     */
    private static final int NUM_COLS = 4;

    /**
     * KeyData column.
     */
    private static final int COL_CONTROL = 1;

    /**
     * KeyType column.
     */
    private static final int COL_KEYTYPE = 2;

    /**
     * KeyData column.
     */
    private static final int COL_KEYDATA = 3;

    /**
     * DataKey list.
     */
    private DataKeyList theList = null;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetDataKey(final SheetReader<?> pReader) {
        /* Call super constructor */
        super(pReader, SHEET_NAME);

        /* Access the Lists */
        DataSet<?> myData = pReader.getData();
        theList = myData.getDataKeys();
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetDataKey(final SheetWriter<?> pWriter) {
        /* Call super constructor */
        super(pWriter, SHEET_NAME);

        /* Access the Control list */
        theList = pWriter.getData().getDataKeys();
        setDataList(theList);
    }

    @Override
    protected void loadItem() throws JDataException {
        /* Access the IDs */
        int myID = loadInteger(COL_ID);
        int myControl = loadInteger(COL_CONTROL);
        int myKeyType = loadInteger(COL_KEYTYPE);

        /* Access the Binary values */
        byte[] myKey = loadBytes(COL_KEYDATA);

        /* Add the DataKey */
        theList.addItem(myID, myControl, myKeyType, myKey);
    }

    @Override
    protected void insertItem(final DataKey pItem) throws JDataException {
        /* Set the fields */
        writeInteger(COL_ID, pItem.getId());
        writeInteger(COL_CONTROL, pItem.getControlKey().getId());
        writeInteger(COL_KEYTYPE, pItem.getKeyType().getId());
        writeBytes(COL_KEYDATA, pItem.getSecuredKeyDef());
    }

    @Override
    protected void preProcessOnWrite() throws JDataException {
    }

    @Override
    protected void postProcessOnWrite() throws JDataException {
        /* Set the four columns as the range */
        nameRange(NUM_COLS);
    }
}