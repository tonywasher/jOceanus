/*******************************************************************************
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
package uk.co.tolcroft.models.sheets;

import uk.co.tolcroft.models.data.DataKey;
import uk.co.tolcroft.models.data.DataKey.DataKeyList;
import uk.co.tolcroft.models.data.DataSet;

public class SheetDataKey extends SheetDataItem<DataKey> {
    /**
     * SheetName for Keys
     */
    private static final String Keys = DataKey.class.getSimpleName();

    /**
     * DataKey list
     */
    private DataKeyList theList = null;

    /**
     * DataSet
     */
    private DataSet<?> theData = null;

    /**
     * Constructor for loading a spreadsheet
     * @param pReader the spreadsheet reader
     */
    protected SheetDataKey(SheetReader<?> pReader) {
        /* Call super constructor */
        super(pReader, Keys);

        /* Access the Lists */
        theData = pReader.getData();
        theList = theData.getDataKeys();
    }

    /**
     * Constructor for creating a spreadsheet
     * @param pWriter the spreadsheet writer
     */
    protected SheetDataKey(SheetWriter<?> pWriter) {
        /* Call super constructor */
        super(pWriter, Keys);

        /* Access the Control list */
        theList = pWriter.getData().getDataKeys();
        setDataList(theList);
    }

    @Override
    protected void loadItem() throws Throwable {
        /* Access the IDs */
        int myID = loadInteger(0);
        int myControl = loadInteger(1);
        int myKeyType = loadInteger(2);

        /* Access the Binary values */
        byte[] myKey = loadBytes(3);

        /* Add the DataKey */
        theList.addItem(myID, myControl, myKeyType, myKey);
    }

    @Override
    protected void insertItem(DataKey pItem) throws Throwable {
        /* Set the fields */
        writeInteger(0, pItem.getId());
        writeInteger(1, pItem.getControlKey().getId());
        writeInteger(2, pItem.getKeyType().getId());
        writeBytes(3, pItem.getSecuredKeyDef());
    }

    @Override
    protected void preProcessOnWrite() throws Throwable {
    }

    @Override
    protected void postProcessOnWrite() throws Throwable {
        /* Set the four columns as the range */
        nameRange(4);
    }
}
