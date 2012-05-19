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
package net.sourceforge.JDataManager;

import net.sourceforge.JDataManager.ReportFields.ReportField;
import net.sourceforge.JDataManager.ReportObject.ReportDetail;
import net.sourceforge.JSortedList.SortedList;

public abstract class ReportList<T extends ReportItem<T>> extends SortedList<T> implements ReportDetail {
    /**
     * Local Report fields
     */
    protected static final ReportFields theLocalFields = new ReportFields(ReportList.class.getSimpleName());

    /**
     * Instance ReportFields
     */
    private final ReportFields theFields;

    /**
     * Declare fields
     * @return the fields
     */
    public abstract ReportFields declareFields();

    /* Field IDs */
    public static final ReportField FIELD_SIZE = theLocalFields.declareLocalField("ListSize");

    @Override
    public ReportFields getReportFields() {
        return theFields;
    }

    @Override
    public String getObjectSummary() {
        return getReportFields().getName();
    }

    @Override
    public Object getFieldValue(ReportField pField) {
        if (pField == FIELD_SIZE)
            return sizeAll();
        return null;
    }

    /**
     * Construct a top-level List
     * @param pClass the class of the list item
     */
    public ReportList(Class<T> pClass) {
        /* Initialise as a list */
        super(pClass);

        /* Declare fields (allowing for subclasses) */
        theFields = declareFields();
    }

    /**
     * Construct a top-level List
     * @param pClass the class of the list item
     * @param fromStart inserts from start?
     */
    public ReportList(Class<T> pClass,
                      boolean fromStart) {
        /* Initialise as a list */
        super(pClass, fromStart);

        /* Declare fields (allowing for subclasses) */
        theFields = declareFields();
    }
}
