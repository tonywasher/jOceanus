/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.jOceanus.jMoneyWise.database;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataModels.data.DataErrorList;
import net.sourceforge.jOceanus.jDataModels.data.DataItem;
import net.sourceforge.jOceanus.jDataModels.data.DataSet;
import net.sourceforge.jOceanus.jDataModels.database.Database;
import net.sourceforge.jOceanus.jDataModels.database.TableDefinition;
import net.sourceforge.jOceanus.jDataModels.database.TableEncrypted;
import net.sourceforge.jOceanus.jMoneyWise.data.EventClass;
import net.sourceforge.jOceanus.jMoneyWise.data.EventClass.EventClassList;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;

/**
 * TableEncrypted extension EventClass.
 * @author Tony Washer
 */
public class TableEventClass
        extends TableEncrypted<EventClass> {
    /**
     * The name of the EventClass table.
     */
    protected static final String TABLE_NAME = EventClass.LIST_NAME;

    /**
     * The tag list.
     */
    private EventClassList theList = null;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableEventClass(final Database<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        TableDefinition myTableDef = getTableDef();

        /* Declare the columns */
        myTableDef.addEncryptedColumn(EventClass.FIELD_NAME, EventClass.NAMELEN);
        myTableDef.addNullEncryptedColumn(EventClass.FIELD_DESC, EventClass.DESCLEN);
    }

    @Override
    protected void declareData(final DataSet<?> pData) {
        FinanceData myData = (FinanceData) pData;
        theList = myData.getEventClasses();
        setList(theList);
    }

    @Override
    protected void loadItem(final Integer pId,
                            final Integer pControlId) throws JDataException {
        /* Get the various fields */
        TableDefinition myTableDef = getTableDef();
        byte[] myName = myTableDef.getBinaryValue(EventClass.FIELD_NAME);
        byte[] myDesc = myTableDef.getBinaryValue(EventClass.FIELD_DESC);

        /* Add into the list */
        theList.addSecureItem(pId, pControlId, myName, myDesc);
    }

    @Override
    protected void setFieldValue(final EventClass pItem,
                                 final JDataField iField) throws JDataException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
        if (EventClass.FIELD_NAME.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getNameBytes());
        } else if (EventClass.FIELD_DESC.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getDescBytes());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }

    @Override
    protected void postProcessOnLoad() throws JDataException {
        /* Sort the data */
        theList.reSort();

        /* Validate the event tags */
        DataErrorList<DataItem> myErrors = theList.validate();
        if (myErrors != null) {
            throw new JDataException(ExceptionClass.VALIDATE, myErrors, DataItem.ERROR_VALIDATION);
        }
    }
}
