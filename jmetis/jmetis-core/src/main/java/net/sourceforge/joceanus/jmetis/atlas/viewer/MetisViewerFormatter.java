/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmetis.atlas.viewer;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataField;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet.MetisDataFieldItem;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet.MetisFieldStorage;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataMap;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataVersionControl;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataVersionDelta.MetisDataDelta;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataVersionValues;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataVersionValues.MetisDataVersionedItem;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysDataFormatter;

/**
 * Data Viewer Formatter.
 */
public class MetisViewerFormatter {
    /**
     * Items per page.
     */
    protected static final int ITEMS_PER_PAGE = 50;

    /**
     * The index column.
     */
    private static final String COLUMN_INDEX = "Index";

    /**
     * The field column.
     */
    private static final String COLUMN_FIELD = MetisViewerResource.VIEWER_COLUMN_FIELD.getValue();

    /**
     * The key column.
     */
    private static final String COLUMN_KEY = MetisViewerResource.VIEWER_COLUMN_KEY.getValue();

    /**
     * The value column.
     */
    private static final String COLUMN_VALUE = MetisViewerResource.VIEWER_COLUMN_VALUE.getValue();

    /**
     * List Table.
     */
    private static final String TABLE_LIST = "List";

    /**
     * Map Table.
     */
    private static final String TABLE_MAP = MetisViewerResource.VIEWER_TABLE_MAP.getValue();

    /**
     * Stack Trace table.
     */
    private static final String TABLE_STACKTRACE = MetisViewerResource.VIEWER_TABLE_STACKTRACE.getValue();

    /**
     * The HTML Builder.
     */
    private final MetisViewerBuilder theBuilder;

    /**
     * Constructor.
     * @param pFormatter the data formatter
     * @throws OceanusException on error
     */
    protected MetisViewerFormatter(final TethysDataFormatter pFormatter) throws OceanusException {
        theBuilder = new MetisViewerBuilder(pFormatter);
    }

    /**
     * Build HTML table describing Object.
     * @param pPage the viewer page
     */
    protected void formatPage(final MetisViewerPage pPage) {
        /* Reset the document */
        theBuilder.resetDocument(pPage);

        /* Switch on the Mode */
        switch (pPage.getMode()) {
            case CONTENTS:
                formatHTMLObject(pPage.getObject());
                break;
            case SUMMARY:
                formatHTMLCollection(pPage.getObject(), pPage.getItemNo());
                break;
            case ITEMS:
                formatHTMLItem(pPage.getObject(), pPage.getItemNo());
                break;
            default:
                break;
        }

        /* Format the document */
        theBuilder.formatDocument();
    }

    /**
     * Build HTML table.
     * @param pObject the object
     */
    private void formatHTMLObject(final Object pObject) {
        /* handle DataDifference */
        Object myObject = pObject instanceof MetisDataDelta
                                                            ? ((MetisDataDelta) pObject).getObject()
                                                            : pObject;

        /* If we are DataContents */
        if (myObject instanceof MetisDataVersionedItem) {
            formatHTMLVersionedItem((MetisDataVersionedItem) myObject);

            /* If we are DataContents */
        } else if (myObject instanceof MetisDataFieldItem) {
            formatHTMLFieldItem((MetisDataFieldItem) myObject);

            /* If we are Stack Trace */
        } else if (myObject instanceof StackTraceElement[]) {
            formatHTMLStackTrace((StackTraceElement[]) myObject);

            /* If we are Throwable */
        } else if (myObject instanceof Throwable) {
            formatHTMLFieldItem(new MetisViewerExceptionWrapper((Throwable) myObject));

            /* else handle unsupported list item */
        } else {
            formatHTMLUnknown(myObject);
        }
    }

    /**
     * Build HTML collection.
     * @param pObject the object
     * @param pStart the start index for the section
     */
    private void formatHTMLCollection(final Object pObject,
                                      final int pStart) {
        /* handle DataDifference */
        Object myObject = pObject instanceof MetisDataDelta
                                                            ? ((MetisDataDelta) pObject).getObject()
                                                            : pObject;

        /* handle embedded objects */
        if (myObject instanceof MetisDataList) {
            myObject = ((MetisDataList<?>) myObject).getUnderlyingList();
        } else if (myObject instanceof MetisDataMap) {
            myObject = ((MetisDataMap<?, ?>) myObject).getUnderlyingMap();
        }

        /* If we are List */
        if (myObject instanceof List) {
            formatHTMLListSection((List<?>) myObject, pStart);

            /* If we are Map */
        } else if (myObject instanceof Map) {
            formatHTMLMapSection((Map<?, ?>) myObject, pStart);
        }
    }

    /**
     * Build HTML item.
     * @param pObject the object
     * @param pIndex index of the item
     */
    private void formatHTMLItem(final Object pObject,
                                final int pIndex) {
        /* handle DataDifference */
        Object myObject = pObject instanceof MetisDataDelta
                                                            ? ((MetisDataDelta) pObject).getObject()
                                                            : pObject;

        /* handle embedded objects */
        if (myObject instanceof MetisDataList) {
            myObject = ((MetisDataList<?>) myObject).getUnderlyingList();
        }

        /* If we are List */
        if (myObject instanceof List) {
            formatHTMLListItem((List<?>) myObject, pIndex);
        }
    }

    /**
     * Build HTML table describing DataFieldItem.
     * @param pItem the item
     */
    private void formatHTMLFieldItem(final MetisDataFieldItem pItem) {
        /* Access details */
        MetisDataFieldSet myFields = pItem.getDataFieldSet();

        /* Initialise the document */
        theBuilder.newTitle(myFields.getName());
        theBuilder.newTable();
        theBuilder.newTitleCell(COLUMN_FIELD);
        theBuilder.newTitleCell(COLUMN_VALUE);

        /* Loop through the fields */
        Iterator<MetisDataField> myIterator = myFields.fieldIterator();
        while (myIterator.hasNext()) {
            /* Access Field */
            MetisDataField myField = myIterator.next();
            MetisFieldStorage myStorage = myField.getStorage();

            /* Access the value */
            Object myValue = myStorage.isCalculated()
                                                      ? MetisDataFieldValue.SKIP
                                                      : pItem.getFieldValue(myField);

            /* Skip value if required */
            if (MetisDataFieldValue.SKIP.equals(myValue)) {
                continue;
            }

            /* Start the field */
            theBuilder.newTableRow();
            theBuilder.newDataCell(myField.getName());
            theBuilder.newDataCell(myValue);
        }
    }

    /**
     * Build HTML table describing DataVersionedItem.
     * @param pItem the item
     */
    private void formatHTMLVersionedItem(final MetisDataVersionedItem pItem) {
        /* Access details */
        MetisDataFieldSet myFields = pItem.getDataFieldSet();
        MetisDataVersionControl myControl = pItem.getVersionControl();
        MetisDataVersionValues myValues = myControl == null
                                                            ? null
                                                            : myControl.getValueSet();

        /* Initialise the document */
        theBuilder.newTitle(myFields.getName());
        theBuilder.newTable();
        theBuilder.newTitleCell(COLUMN_FIELD);
        theBuilder.newTitleCell(COLUMN_VALUE);

        /* Loop through the fields */
        Iterator<MetisDataField> myIterator = myFields.fieldIterator();
        while (myIterator.hasNext()) {
            /* Access Field */
            MetisDataField myField = myIterator.next();
            MetisFieldStorage myStorage = myField.getStorage();
            Object myValue = MetisDataFieldValue.SKIP;

            /* Access the value */
            if (myStorage.isVersioned()) {
                myValue = myValues.getValue(myField);
            } else if (!myStorage.isCalculated()) {
                myValue = pItem.getFieldValue(myField);
            }

            /* Skip value if required */
            if (MetisDataFieldValue.SKIP.equals(myValue)) {
                continue;
            }

            /* Start the field */
            theBuilder.newTableRow();
            theBuilder.newDataCell(myField.getName());
            if (myControl != null
                && myControl.fieldChanged(myField).isDifferent()) {
                theBuilder.newDataCell(myValue, true);
            } else {
                theBuilder.newDataCell(myValue);
            }
        }
    }

    /**
     * Build HTML table describing list item.
     * @param pList the list
     * @param pIndex the index of the item
     */
    private void formatHTMLListItem(final List<?> pList,
                                    final int pIndex) {
        /* Obtain the object */
        Object myObject = pList.get(pIndex - 1);

        /* Format the object */
        formatHTMLObject(myObject);
    }

    /**
     * Build HTML table describing list section.
     * @param pList the list
     * @param pStart the start index for the section
     */
    private void formatHTMLListSection(final List<?> pList,
                                       final int pStart) {
        /* Initialise the document */
        theBuilder.newTitle(TABLE_LIST);
        theBuilder.newTable();
        theBuilder.newTitleCell(COLUMN_INDEX);
        theBuilder.newTitleCell(COLUMN_VALUE);

        /* If there are items in the list */
        if (!pList.isEmpty()) {
            /* Calculate start point */
            int myStart = (pStart - 1) * ITEMS_PER_PAGE;

            /* Create iterator at start */
            Iterator<?> myIterator = pList.listIterator(myStart);

            /* Loop up to the limit */
            int myCount = ITEMS_PER_PAGE;
            int myIndex = myStart + 1;
            while (myIterator.hasNext()
                   && (myCount-- > 0)) {
                /* Access the key and value */
                Object myObject = myIterator.next();

                /* Format the row */
                theBuilder.newTableRow();
                theBuilder.newDataCell(myIndex++);
                theBuilder.newDataCell(myObject);
            }
        }
    }

    /**
     * Build HTML table describing map section.
     * @param pMap the map
     * @param pStart the start index for the section
     */
    private void formatHTMLMapSection(final Map<?, ?> pMap,
                                      final int pStart) {

        /* Initialise the document */
        theBuilder.newTitle(TABLE_MAP);
        theBuilder.newTable();
        theBuilder.newTitleCell(COLUMN_INDEX);
        theBuilder.newTitleCell(COLUMN_KEY);
        theBuilder.newTitleCell(COLUMN_VALUE);

        /* If there are items in the list */
        if (!pMap.isEmpty()) {
            /* Calculate start point */
            int myCount = (pStart - 1) * ITEMS_PER_PAGE;
            int myIndex = myCount + 1;

            /* Create iterator and shift to start */
            Iterator<?> myIterator = pMap.entrySet().iterator();
            if (myCount > 0) {
                /* Skip leading entries */
                while (myIterator.hasNext()
                       && (myCount-- > 0)) {
                    myIterator.next();
                }
            }

            /* Loop up to the limit */
            myCount = ITEMS_PER_PAGE;
            while (myIterator.hasNext()
                   && (myCount-- > 0)) {
                /* Access the key and value */
                Map.Entry<?, ?> myEntry = Map.Entry.class.cast(myIterator.next());

                /* Format the row */
                theBuilder.newTableRow();
                theBuilder.newDataCell(myIndex++);
                theBuilder.newDataCell(myEntry.getKey());
                theBuilder.newDataCell(myEntry.getValue());
            }
        }
    }

    /**
     * Build HTML table describing stack.
     * @param pStack the stack to describe
     */
    private void formatHTMLStackTrace(final StackTraceElement[] pStack) {
        /* Initialise the document */
        theBuilder.newTitle(TABLE_STACKTRACE);
        theBuilder.newTable();
        theBuilder.newTitleCell(TABLE_STACKTRACE);

        /* Loop through the elements */
        for (StackTraceElement st : pStack) {
            /* Format the row */
            theBuilder.newTableRow();
            theBuilder.newDataCell(st.toString());
        }
    }

    /**
     * Build HTML table describing unknown object.
     * @param pObject the object to describe
     */
    private void formatHTMLUnknown(final Object pObject) {
        /* Initialise the document */
        theBuilder.newTitle("Unknown");
        theBuilder.newTable();
        theBuilder.newTitleCell(COLUMN_FIELD);
        theBuilder.newTitleCell(COLUMN_VALUE);

        /* Describe the class */
        theBuilder.newTableRow();
        theBuilder.newDataCell("Class");
        theBuilder.newDataCell(pObject != null
                                               ? pObject.getClass()
                                               : null);

        /* Describe the object */
        theBuilder.newTableRow();
        theBuilder.newDataCell("Value");
        theBuilder.newDataCell(pObject);
    }
}
