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
package net.sourceforge.JDataWalker;

import java.util.Iterator;
import java.util.Map;

import net.sourceforge.JDataWalker.ReportFields.ReportField;
import net.sourceforge.JDateDay.DateDay;
import net.sourceforge.JDateDay.DateDayRange;
import net.sourceforge.JDecimal.Decimal;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.EncryptedData.EncryptedField;
import uk.co.tolcroft.models.data.ValueSet;

public class ReportObject {
    /**
     * Summary object interface
     */
    public interface ReportSummary {
        /**
         * Obtain Object summary
         * @return the display summary of the object
         */
        public String getObjectSummary();
    }

    /**
     * Detail object interface
     */
    public interface ReportDetail extends ReportSummary {
        /**
         * Obtain the Report Fields
         * @return the report fields
         */
        public ReportFields getReportFields();

        /**
         * Obtain Field value
         * @param pField the field
         * @return the value of the field
         */
        public Object getFieldValue(ReportField pField);
    }

    /**
     * ValueSet object interface
     * @param <T> the item type
     */
    public interface ReportValues<T extends DataItem<T>> extends ReportDetail {
        /**
         * Obtain Object ValueSet
         * @return the ValueSet of the object
         */
        public ValueSet<T> getValueSet();
    }

    /**
     * ReportElement object interface
     */
    public interface ReportElement {
        /**
         * Obtain Object Name
         * @return the Name of the object
         */
        public String getElementName();

        /**
         * Obtain Object Value
         * @return the Value of the object
         */
        public Object getElementValue();
    }

    /**
     * Report Object types
     */
    private enum ReportType {
        Detail, Map, Exception, StackTrace, None;
    }

    /**
     * The value to return if the field is to be skipped
     */
    public final static String skipField = "SkipField";

    /**
     * Format a field value
     * @param pValue the value of the field
     * @return the formatted value
     */
    public static String formatField(Object pValue) {
        /* Handle null value */
        if (pValue == null)
            return null;

        /* Handle encrypted classes */
        if (EncryptedField.class.isInstance(pValue))
            return formatField(((EncryptedField<?>) pValue).getValue());

        /* Access the class */
        Class<?> myClass = pValue.getClass();

        /* Handle Native classes */
        if (myClass == String.class)
            return (String) pValue;
        if (myClass == Boolean.class)
            return (((Boolean) pValue)) ? "true" : "false";
        if (myClass == Short.class)
            return ((Short) pValue).toString();
        if (myClass == Integer.class)
            return ((Integer) pValue).toString();
        if (myClass == Long.class)
            return ((Long) pValue).toString();

        /* Handle Enumerated classes */
        if (Enum.class.isInstance(pValue))
            return ((Enum<?>) pValue).toString();

        /* Handle Class */
        if (Class.class.isInstance(pValue))
            return ((Class<?>) pValue).getCanonicalName();

        /* Handle Native array classes */
        if (myClass == byte[].class)
            return DataConverter.bytesToHexString((byte[]) pValue);
        if (myClass == char[].class)
            return new String((char[]) pValue);

        /* Handle date classes */
        if (myClass == DateDay.class)
            return ((DateDay) pValue).toString();
        if (myClass == DateDayRange.class)
            return ((DateDayRange) pValue).toString();

        /* Handle decimal classes */
        if (Decimal.class.isInstance(pValue))
            return ((Decimal) pValue).format(true);

        /* Handle report object classes */
        if (ReportSummary.class.isInstance(pValue))
            return ((ReportSummary) pValue).getObjectSummary();

        /* Standard format option */
        return pValue.getClass().getSimpleName();
    }

    /**
     * Is the object link-able
     * @param pObject the object to get the type for
     * @return true/false
     */
    private static ReportType getReportType(Object pObject) {
        /* Determine which objects are supported */
        if (pObject == null)
            return ReportType.None;
        if (ReportDetail.class.isInstance(pObject))
            return ReportType.Detail;
        if (Map.class.isInstance(pObject))
            return ReportType.Map;
        if (Throwable.class.isInstance(pObject))
            return ReportType.Exception;
        if (StackTraceElement[].class.isInstance(pObject))
            return ReportType.StackTrace;
        return ReportType.None;
    }

    /**
     * Build HTML table describing object
     * @param pDetail linking detail
     * @param pObject the object to describe
     * @return the HTML table
     */
    public static StringBuilder formatHTMLObject(DebugDetail pDetail,
                                                 Object pObject) {
        Object o = pObject;

        /* Switch on object type */
        switch (getReportType(o)) {
            case Exception:
                o = new ModelException((Throwable) o);
            case Detail:
                return formatHTMLDetail(pDetail, o);
            case Map:
                return formatHTMLMap(pDetail, o);
            case StackTrace:
                return formatHTMLStack(pDetail, o);
            case None:
            default:
                return null;
        }
    }

    /**
     * Build HTML table describing ReportObject
     * @param pDetail linking detail
     * @param pObject the ReportObject to describe
     * @return the HTML table
     */
    private static StringBuilder formatHTMLDetail(DebugDetail pDetail,
                                                  Object pObject) {
        ReportDetail myDetail = ReportDetail.class.cast(pObject);
        StringBuilder myResults = new StringBuilder(2000);
        StringBuilder myEntries = new StringBuilder(2000);
        ReportFields myFields = myDetail.getReportFields();
        ValueSet<?> myValues = null;
        int iNumEntries = 0;
        Object myValue;

        /* Access valueSet if it exists */
        if (ReportValues.class.isInstance(pObject)) {
            myValues = ((ReportValues<?>) pObject).getValueSet();
        }

        /* Loop through the fields */
        Iterator<ReportField> myIterator = myFields.fieldIterator();
        while (myIterator.hasNext()) {
            /* Access Field */
            ReportField myField = myIterator.next();

            /* Access the value */
            if ((myField.isValueSetField()) && (myValues != null))
                myValue = myValues.getValue(myField);
            else
                myValue = myDetail.getFieldValue(myField);

            /* Skip value if required */
            if (myValue == skipField)
                continue;

            /* Increment number of entries and start row if needed */
            if (iNumEntries++ > 0)
                myEntries.append("<tr>");

            /* Start the field */
            myEntries.append("<td>");
            myEntries.append(myField.getName());
            myEntries.append("</td><td>");

            /* Format the value */
            String myFormat = ReportObject.formatField(myValue);
            if (getReportType(myValue) != ReportType.None)
                myFormat = pDetail.addDebugLink(myValue, myFormat);
            myEntries.append(myFormat);
            myEntries.append("</td></tr>");
        }

        /* Initialise the string with an item name */
        myResults.append("<table border=\"1\" width=\"90%\" align=\"center\">");
        myResults.append("<thead><th>");
        myResults.append(myFields.getName());
        myResults.append("</th>");
        myResults.append("<th>Field</th><th>Value</th></thead><tbody>");

        /* Start the entries section */
        myResults.append("<tr><th rowspan=\"");
        myResults.append(iNumEntries + 1);
        myResults.append("\">Values</th></tr>");

        /* Add in the entries */
        myResults.append(myEntries);

        /* Terminate the table */
        myResults.append("</tbody></table>");

        /* Return the formatted item */
        return myResults;
    }

    /**
     * Build HTML table describing map
     * @param pDetail linking detail
     * @param pMap the map to describe
     * @return the HTML table
     */
    private static StringBuilder formatHTMLMap(DebugDetail pDetail,
                                               Object pMap) {
        Map<?, ?> myMap = Map.class.cast(pMap);
        StringBuilder myResults = new StringBuilder(2000);
        StringBuilder myEntries = new StringBuilder(2000);
        int iNumEntries = 0;
        String myFormat;

        /* Loop through the fields */
        for (Map.Entry<?, ?> myEntry : myMap.entrySet()) {
            /* Access the key and value */
            Object myKey = myEntry.getKey();
            Object myValue = myEntry.getValue();

            /* Increment number of entries and start row if needed */
            if (iNumEntries++ > 0)
                myEntries.append("<tr>");

            /* Format the key */
            myFormat = ReportObject.formatField(myKey);
            if (getReportType(myKey) != ReportType.None)
                myFormat = pDetail.addDebugLink(myKey, myFormat);
            myEntries.append("<td>");
            myEntries.append(myFormat);
            myEntries.append("</td><td>");

            /* Format the value */
            myFormat = ReportObject.formatField(myValue);
            if (getReportType(myValue) != ReportType.None)
                myFormat = pDetail.addDebugLink(myValue, myFormat);
            myEntries.append(myFormat);
            myEntries.append("</td></tr>");
        }

        /* Initialise the string with an item name */
        myResults.append("<table border=\"1\" width=\"90%\" align=\"center\">");
        myResults.append("<thead><th>Map</th><th>Key</th><th>Value</th></thead><tbody>");

        /* Start the entries section */
        myResults.append("<tr><th rowspan=\"");
        myResults.append(iNumEntries + 1);
        myResults.append("\">Values</th></tr>");

        /* Add in the entries */
        myResults.append(myEntries);

        /* Terminate the table */
        myResults.append("</tbody></table>");

        /* Return the formatted item */
        return myResults;
    }

    /**
     * Build HTML table describing stack
     * @param pDetail linking detail
     * @param pStack the stack to describe
     * @return the HTML table
     */
    private static StringBuilder formatHTMLStack(DebugDetail pDetail,
                                                 Object pStack) {
        StackTraceElement[] myArray = StackTraceElement[].class.cast(pStack);
        StringBuilder myResults = new StringBuilder(2000);

        /* Add the stack trace */
        myResults.append("<table border=\"1\" width=\"90%\" align=\"center\">");
        myResults.append("<thead><th>Stack Trace</th></thead><tbody>");

        /* Loop through the elements */
        for (StackTraceElement st : myArray) {
            /* Add the stack trace */
            myResults.append("<tr><td>");
            myResults.append(st.toString());
            myResults.append("</td></tr>");
        }

        /* Terminate the table */
        myResults.append("</tbody></table>");

        /* Return the formatted item */
        return myResults;
    }
}
