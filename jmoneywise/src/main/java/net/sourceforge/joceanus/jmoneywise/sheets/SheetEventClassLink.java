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

import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.EventClass;
import net.sourceforge.joceanus.jmoneywise.data.EventClassLink;
import net.sourceforge.joceanus.jmoneywise.data.EventClassLink.EventClassLinkList;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.sheets.SheetDataItem;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * SheetDataItem extension for EventClass.
 * @author Tony Washer
 */
public class SheetEventClassLink
        extends SheetDataItem<EventClassLink, MoneyWiseDataType> {
    /**
     * NamedArea for Event Classes.
     */
    private static final String AREA_EVENTCLSLINKS = EventClassLink.LIST_NAME;

    /**
     * Event column.
     */
    private static final int COL_EVENT = COL_ID + 1;

    /**
     * Class column.
     */
    private static final int COL_CLASS = COL_EVENT + 1;

    /**
     * Class Link data list.
     */
    private final EventClassLinkList theList;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetEventClassLink(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_EVENTCLSLINKS);

        /* Access the Links list */
        theList = pReader.getData().getEventClassLinks();
        setDataList(theList);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetEventClassLink(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_EVENTCLSLINKS);

        /* Access the Links list */
        theList = pWriter.getData().getEventClassLinks();
        setDataList(theList);
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadSecureValues() throws JOceanusException {
        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(EventClass.OBJECT_NAME);
        myValues.addValue(EventClassLink.FIELD_EVENT, loadInteger(COL_EVENT));
        myValues.addValue(EventClassLink.FIELD_CLASS, loadInteger(COL_CLASS));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final EventClassLink pItem) throws JOceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeInteger(COL_EVENT, pItem.getEventId());
        writeInteger(COL_CLASS, pItem.getEventClassId());
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_CLASS;
    }

    @Override
    protected void postProcessOnLoad() throws JOceanusException {
        /* reSort */
        theList.reSort();

        /* Validate the event tags */
        theList.validateOnLoad();
    }
}
