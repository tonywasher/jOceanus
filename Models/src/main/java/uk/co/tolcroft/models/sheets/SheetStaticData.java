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

import net.sourceforge.JDataWalker.ModelException;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.StaticData;
import uk.co.tolcroft.models.sheets.SpreadSheet.SheetType;

public abstract class SheetStaticData<T extends StaticData<T, ?>> extends SheetDataItem<T> {

    /* Load the Static Data */
    protected abstract void loadEncryptedItem(int pId,
                                              int pControlId,
                                              boolean isEnabled,
                                              int iOrder,
                                              byte[] pName,
                                              byte[] pDesc) throws ModelException;

    protected abstract void loadClearTextItem(int pId,
                                              boolean isEnabled,
                                              int iOrder,
                                              String pName,
                                              String pDesc) throws ModelException;

    /**
     * Is the spreadsheet a backup spreadsheet or an edit-able one
     */
    private boolean isBackup = false;

    /**
     * The name of the items
     */
    private String theNames = null;

    /**
     * Constructor for loading a spreadsheet
     * @param pReader the spreadsheet reader
     * @param pRange the range to load
     */
    protected SheetStaticData(SheetReader<?> pReader, String pRange) {
        /* Call super constructor */
        super(pReader, pRange);

        /* Note whether this is a backup */
        isBackup = (pReader.getType() == SheetType.BACKUP);
    }

    /**
     * Constructor for creating a spreadsheet
     * @param pWriter the spreadsheet writer
     * @param pRange the range to create
     * @param pNames the name range to create
     */
    protected SheetStaticData(SheetWriter<?> pWriter, String pRange, String pNames) {
        /* Call super constructor */
        super(pWriter, pRange);

        /* Record the names */
        theNames = pNames;

        /* Note whether this is a backup */
        isBackup = (pWriter.getType() == SheetType.BACKUP);
    }

    @Override
    protected void loadItem() throws Throwable {
        /* If this is a backup load */
        if (isBackup) {
            /* Access the IDs */
            int myID = loadInteger(0);
            int myControlId = loadInteger(1);
            int myOrder = loadInteger(2);
            boolean myEnabled = loadBoolean(3);

            /* Access the name and description bytes */
            byte[] myNameBytes = loadBytes(4);
            byte[] myDescBytes = loadBytes(5);

            /* Load the item */
            loadEncryptedItem(myID, myControlId, myEnabled, myOrder, myNameBytes, myDescBytes);
        }

        /* else this is a load from an edit-able spreadsheet */
        else {
            /* Access the IDs */
            int myID = loadInteger(0);
            int myOrder = loadInteger(1);
            boolean myEnabled = loadBoolean(2);

            /* Access the name and description bytes */
            String myName = loadString(3);
            String myDesc = loadString(4);

            /* Load the item */
            loadClearTextItem(myID, myEnabled, myOrder, myName, myDesc);
        }
    }

    @Override
    protected void insertItem(T pItem) throws Throwable {
        /* If we are creating a backup */
        if (isBackup) {
            /* Set the fields */
            writeInteger(0, pItem.getId());
            writeInteger(1, pItem.getControlKey().getId());
            writeInteger(2, pItem.getOrder());
            writeBoolean(3, pItem.getEnabled());
            writeBytes(4, pItem.getNameBytes());
            writeBytes(5, pItem.getDescBytes());
        }

        /* else we are creating an edit-able spreadsheet */
        else {
            /* Set the fields */
            writeInteger(0, pItem.getId());
            writeInteger(1, pItem.getOrder());
            writeBoolean(2, pItem.getEnabled());
            writeString(3, pItem.getName());
            writeString(4, pItem.getDesc());
        }
    }

    @Override
    protected void preProcessOnWrite() throws Throwable {
        /* Ignore if this is a backup */
        if (isBackup)
            return;

        /* Create a new row */
        newRow();

        /* Write titles */
        writeHeader(0, DataItem.FIELD_ID.getName());
        writeHeader(1, StaticData.FIELD_ORDER.getName());
        writeHeader(2, StaticData.FIELD_ENABLED.getName());
        writeHeader(3, StaticData.FIELD_NAME.getName());
        writeHeader(4, StaticData.FIELD_DESC.getName());

        /* Adjust for Header */
        adjustForHeader();
    }

    @Override
    protected void postProcessOnWrite() throws Throwable {
        /* If we are creating a backup */
        if (isBackup) {
            /* Set the six columns as the range */
            nameRange(6);
        }

        /* else this is an edit-able spreadsheet */
        else {
            /* Set the five columns as the range */
            nameRange(5);

            /* Set the Id column as hidden */
            setHiddenColumn(0);

            /* Set default column types */
            setIntegerColumn(0);
            setIntegerColumn(1);
            setBooleanColumn(2);

            /* Set the name column width and range */
            nameColumnRange(3, theNames);
            setColumnWidth(3, StaticData.NAMELEN);

            /* Set description column width */
            setColumnWidth(4, StaticData.DESCLEN);
        }
    }
}
