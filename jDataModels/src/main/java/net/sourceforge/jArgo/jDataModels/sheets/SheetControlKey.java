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
package net.sourceforge.jArgo.jDataModels.sheets;

import net.sourceforge.jArgo.jDataManager.JDataException;
import net.sourceforge.jArgo.jDataModels.data.ControlKey;
import net.sourceforge.jArgo.jDataModels.data.ControlKey.ControlKeyList;
import net.sourceforge.jArgo.jDataModels.data.DataSet;

/**
 * SheetDataItem extension for ControlKey.
 * @author Tony Washer
 */
public class SheetControlKey extends SheetDataItem<ControlKey> {
    /**
     * SheetName for Keys.
     */
    private static final String SHEET_NAME = ControlKey.class.getSimpleName();

    /**
     * KeyData column.
     */
    private static final int COL_KEYDATA = COL_ID + 1;

    /**
     * ControlKey data list.
     */
    private ControlKeyList theList = null;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetControlKey(final SheetReader<?> pReader) {
        /* Call super constructor */
        super(pReader, SHEET_NAME);

        /* Access the Lists */
        DataSet<?> myData = pReader.getData();
        theList = myData.getControlKeys();
        setDataList(theList);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the Spreadsheet writer
     */
    protected SheetControlKey(final SheetWriter<?> pWriter) {
        /* Call super constructor */
        super(pWriter, SHEET_NAME);

        /* Access the Control list */
        theList = pWriter.getData().getControlKeys();
        setDataList(theList);
    }

    @Override
    protected void loadSecureItem() throws JDataException {
        /* Access the IDs */
        Integer myID = loadInteger(COL_ID);

        /* Access the binary values */
        byte[] myHash = loadBytes(COL_KEYDATA);

        /* Add the Control */
        theList.addSecureItem(myID, myHash);
    }

    @Override
    protected void insertSecureItem(final ControlKey pItem) throws JDataException {
        /* Set the fields */
        writeInteger(COL_ID, pItem.getId());
        writeBytes(COL_KEYDATA, pItem.getHashBytes());
    }

    @Override
    protected void postProcessOnWrite() throws JDataException {
        /* Set the range */
        nameRange(COL_KEYDATA);
    }
}
