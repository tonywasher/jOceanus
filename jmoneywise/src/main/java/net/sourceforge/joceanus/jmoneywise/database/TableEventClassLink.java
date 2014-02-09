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
package net.sourceforge.joceanus.jmoneywise.database;

import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.EventClassLink;
import net.sourceforge.joceanus.jmoneywise.data.EventClassLink.EventClassLinkList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.database.Database;
import net.sourceforge.joceanus.jprometheus.database.DatabaseTable;
import net.sourceforge.joceanus.jprometheus.database.TableDefinition;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * DatabaseTable extension for EventClassLink.
 * @author Tony Washer
 */
public class TableEventClassLink extends DatabaseTable<EventClassLink, MoneyWiseDataType> {
    /**
     * The name of the EventClassLink table.
     */
    protected static final String TABLE_NAME = EventClassLink.LIST_NAME;

    /**
     * The tag link list.
     */
    private EventClassLinkList theList = null;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableEventClassLink(final Database<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        TableDefinition myTableDef = getTableDef();

        /* Declare the columns */
        myTableDef.addReferenceColumn(EventClassLink.FIELD_EVENT, TableEvent.TABLE_NAME);
        myTableDef.addReferenceColumn(EventClassLink.FIELD_CLASS, TableEventClass.TABLE_NAME);
    }

    @Override
    protected void declareData(final DataSet<?, ?> pData) {
        MoneyWiseData myData = (MoneyWiseData) pData;
        theList = myData.getEventClassLinks();
        setList(theList);
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadValues() throws JOceanusException {
        /* Access the table definition */
        TableDefinition myTableDef = getTableDef();

        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(EventClassLink.OBJECT_NAME);
        myValues.addValue(EventClassLink.FIELD_EVENT, myTableDef.getIntegerValue(EventClassLink.FIELD_EVENT));
        myValues.addValue(EventClassLink.FIELD_CLASS, myTableDef.getIntegerValue(EventClassLink.FIELD_CLASS));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final EventClassLink pItem,
                                 final JDataField iField) throws JOceanusException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
        if (EventClassLink.FIELD_EVENT.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getEventId());
        } else if (EventClassLink.FIELD_CLASS.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getEventClassId());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }

    @Override
    protected void postProcessOnLoad() throws JOceanusException {
        /* Resolve links and sort the data */
        theList.resolveDataSetLinks();
        theList.reSort();

        /* Touch underlying items */
        theList.touchUnderlyingItems();

        /* Validate the class links */
        theList.validateOnLoad();
    }
}
