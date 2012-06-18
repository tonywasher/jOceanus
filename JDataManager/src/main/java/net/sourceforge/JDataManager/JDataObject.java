/*******************************************************************************
 * JDataManager: Java Data Manager
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDateDay.DateDay;
import net.sourceforge.JDateDay.DateDayRange;
import net.sourceforge.JDecimal.Decimal;

/**
 * Data object formatting and interfaces.
 * @author Tony Washer
 */
public final class JDataObject {
    /**
     * Private constructor to avoid instantiation.
     */
    private JDataObject() {
    }

    /**
     * Buffer length.
     */
    private static final int BUFFER_LEN = 2000;

    /**
     * Format object interface.
     */
    public interface JDataFormat {
        /**
         * Obtain Object summary.
         * @return the display summary of the object
         */
        String formatObject();
    }

    /**
     * Detail object interface.
     */
    public interface JDataContents extends JDataFormat {
        /**
         * Obtain the Report Fields.
         * @return the report fields
         */
        JDataFields getDataFields();

        /**
         * Obtain Field value.
         * @param pField the field
         * @return the value of the field
         */
        Object getFieldValue(final JDataField pField);
    }

    /**
     * ValueSet object interface.
     */
    public interface JDataValues extends JDataContents {
        /**
         * Obtain Object ValueSet.
         * @return the ValueSet of the object
         */
        ValueSet getValueSet();

        /**
         * Declare the valueSet as active.
         * @param pValues the active values
         */
        void declareValues(final ValueSet pValues);
    }

    /**
     * JDataElement object interface.
     */
    public interface JDataElement {
        /**
         * Obtain Object Name.
         * @return the Name of the object
         */
        String getElementName();

        /**
         * Obtain Object Value.
         * @return the Value of the object
         */
        Object getElementValue();
    }

    /**
     * Difference interface.
     */
    public interface JDataDiffers {
        /**
         * Test for difference with another object.
         * @param pThat the other object
         * @return the difference
         */
        Difference differs(final Object pThat);
    }

    /**
     * Data Object types.
     */
    private enum JDataType {
        /**
         * Contents.
         */
        Contents,

        /**
         * Map.
         */
        Map,

        /**
         * Exception.
         */
        Exception,

        /**
         * StackTrace.
         */
        StackTrace,

        /**
         * None.
         */
        None;
    }

    /**
     * Special values for return by getFieldValue.
     */
    public enum JDataFieldValue {
        /**
         * Field not known.
         */
        UnknownField,

        /**
         * Field to be skipped.
         */
        SkipField;
    }

    /**
     * Format a field value.
     * @param pValue the value of the field
     * @return the formatted value
     */
    public static String formatField(final Object pValue) {
        /* Handle null value */
        if (pValue == null) {
            return null;
        }

        /* Handle ones that we can directly format */
        if (JDataFormat.class.isInstance(pValue)) {
            return ((JDataFormat) pValue).formatObject();
        }

        /* Access the class */
        Class<?> myClass = pValue.getClass();

        /* Handle Native classes */
        if (myClass == String.class) {
            return (String) pValue;
        }
        if (myClass == Boolean.class) {
            return (((Boolean) pValue)) ? "true" : "false";
        }
        if (myClass == Short.class) {
            return ((Short) pValue).toString();
        }
        if (myClass == Integer.class) {
            return ((Integer) pValue).toString();
        }
        if (myClass == Long.class) {
            return ((Long) pValue).toString();
        }
        if (myClass == Float.class) {
            return ((Float) pValue).toString();
        }
        if (myClass == Double.class) {
            return ((Double) pValue).toString();
        }
        if (myClass == BigInteger.class) {
            return ((BigInteger) pValue).toString();
        }
        if (myClass == BigDecimal.class) {
            return ((BigDecimal) pValue).toString();
        }

        /* Handle Enumerated classes */
        if (Enum.class.isInstance(pValue)) {
            return ((Enum<?>) pValue).toString();
        }

        /* Handle Class */
        if (Class.class.isInstance(pValue)) {
            return ((Class<?>) pValue).getCanonicalName();
        }

        /* Handle Native array classes */
        if (myClass == byte[].class) {
            return DataConverter.bytesToHexString((byte[]) pValue);
        }
        if (myClass == char[].class) {
            return new String((char[]) pValue);
        }

        /* Handle date classes */
        if (myClass == DateDay.class) {
            return ((DateDay) pValue).toString();
        }
        if (myClass == DateDayRange.class) {
            return ((DateDayRange) pValue).toString();
        }

        /* Handle decimal classes */
        if (Decimal.class.isInstance(pValue)) {
            return ((Decimal) pValue).format(true);
        }

        /* Standard format option */
        return pValue.getClass().getSimpleName();
    }

    /**
     * Obtain the type of object.
     * @param pObject the object to get the type for
     * @return true/false
     */
    private static JDataType getDataType(final Object pObject) {
        /* Determine which objects are supported */
        if (pObject == null) {
            return JDataType.None;
        }
        if (JDataContents.class.isInstance(pObject)) {
            return JDataType.Contents;
        }
        if (Map.class.isInstance(pObject)) {
            return JDataType.Map;
        }
        if (Throwable.class.isInstance(pObject)) {
            return JDataType.Exception;
        }
        if (StackTraceElement[].class.isInstance(pObject)) {
            return JDataType.StackTrace;
        }
        return JDataType.None;
    }

    /**
     * Build HTML table describing object.
     * @param pDetail linking detail
     * @param pObject the object to describe
     * @return the HTML table
     */
    public static StringBuilder formatHTMLObject(final JDataDetail pDetail,
                                                 final Object pObject) {
        Object o = pObject;

        /* Switch on object type */
        switch (getDataType(o)) {
            case Exception:
                o = new JDataException((Throwable) o);
            case Contents:
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
     * Build HTML table describing ReportObject.
     * @param pDetail linking detail
     * @param pObject the ReportObject to describe
     * @return the HTML table
     */
    private static StringBuilder formatHTMLDetail(final JDataDetail pDetail,
                                                  final Object pObject) {
        JDataContents myDetail = JDataContents.class.cast(pObject);
        StringBuilder myResults = new StringBuilder(BUFFER_LEN);
        StringBuilder myEntries = new StringBuilder(BUFFER_LEN);
        JDataFields myFields = myDetail.getDataFields();
        ValueSet myValues = null;
        int iNumEntries = 0;
        Object myValue;

        /* Access valueSet if it exists */
        if (JDataValues.class.isInstance(pObject)) {
            myValues = ((JDataValues) pObject).getValueSet();
        }

        /* Loop through the fields */
        Iterator<JDataField> myIterator = myFields.fieldIterator();
        while (myIterator.hasNext()) {
            /* Access Field */
            JDataField myField = myIterator.next();

            /* Access the value */
            if ((myField.isValueSetField()) && (myValues != null)) {
                myValue = myValues.getValue(myField);
            } else {
                myValue = myDetail.getFieldValue(myField);
            }

            /* Skip value if required */
            if (myValue == JDataFieldValue.SkipField) {
                continue;
            }

            /* Increment number of entries and start row if needed */
            if (iNumEntries++ > 0) {
                myEntries.append("<tr>");
            }

            /* Start the field */
            myEntries.append("<td>");
            myEntries.append(myField.getName());
            myEntries.append("</td><td>");

            /* Format the value */
            String myFormat = formatField(myValue);
            if (getDataType(myValue) != JDataType.None) {
                myFormat = pDetail.addDataLink(myValue, myFormat);
            }
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
     * Build HTML table describing map.
     * @param pDetail linking detail
     * @param pMap the map to describe
     * @return the HTML table
     */
    private static StringBuilder formatHTMLMap(final JDataDetail pDetail,
                                               final Object pMap) {
        Map<?, ?> myMap = Map.class.cast(pMap);
        StringBuilder myResults = new StringBuilder(BUFFER_LEN);
        StringBuilder myEntries = new StringBuilder(BUFFER_LEN);
        int iNumEntries = 0;
        String myFormat;

        /* Loop through the fields */
        for (Map.Entry<?, ?> myEntry : myMap.entrySet()) {
            /* Access the key and value */
            Object myKey = myEntry.getKey();
            Object myValue = myEntry.getValue();

            /* Increment number of entries and start row if needed */
            if (iNumEntries++ > 0) {
                myEntries.append("<tr>");
            }

            /* Format the key */
            myFormat = formatField(myKey);
            if (getDataType(myKey) != JDataType.None) {
                myFormat = pDetail.addDataLink(myKey, myFormat);
            }
            myEntries.append("<td>");
            myEntries.append(myFormat);
            myEntries.append("</td><td>");

            /* Format the value */
            myFormat = formatField(myValue);
            if (getDataType(myValue) != JDataType.None) {
                myFormat = pDetail.addDataLink(myValue, myFormat);
            }
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
     * Build HTML table describing stack.
     * @param pDetail linking detail
     * @param pStack the stack to describe
     * @return the HTML table
     */
    private static StringBuilder formatHTMLStack(final JDataDetail pDetail,
                                                 final Object pStack) {
        StackTraceElement[] myArray = StackTraceElement[].class.cast(pStack);
        StringBuilder myResults = new StringBuilder(BUFFER_LEN);

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
