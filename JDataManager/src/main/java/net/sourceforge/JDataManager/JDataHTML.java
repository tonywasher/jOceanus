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

import java.awt.Color;
import java.util.Iterator;
import java.util.Map;

import javax.swing.text.html.StyleSheet;

import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataObject.JDataContents;
import net.sourceforge.JDataManager.JDataObject.JDataDifference;
import net.sourceforge.JDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.JDataManager.JDataObject.JDataValues;

/**
 * Data object formatting in HTML.
 * @author Tony Washer
 */
public final class JDataHTML {
    /**
     * Colour for standard elements.
     */
    public static final Color COLOR_STANDARD = Color.black;

    /**
     * Colour for changed elements.
     */
    public static final Color COLOR_CHANGED = Color.magenta;

    /**
     * Colour for standard links.
     */
    public static final Color COLOR_LINK = Color.blue;

    /**
     * Colour for changed link.
     */
    public static final Color COLOR_CHGLINK = Color.green;

    /**
     * Name of odd table row class.
     */
    private static final String CLASS_ODDROW = "oddrow";

    /**
     * Name of even table row class.
     */
    private static final String CLASS_EVENROW = "evenrow";

    /**
     * Name of changed cell class.
     */
    protected static final String CLASS_CHANGED = "changed";

    /**
     * Name of security changed cell class.
     */
    private static final String CLASS_SECCHANGED = "security";

    /**
     * Buffer length.
     */
    private static final int BUFFER_LEN = 1000;

    /**
     * Wrap for hex string.
     */
    private static final int WRAP_HEXSTRING = 60;

    /**
     * Colour for standard elements.
     */
    private final Color theColorStandard;

    /**
     * Colour for changed elements.
     */
    private final Color theColorChanged;

    /**
     * Colour for standard links.
     */
    private final Color theColorLink;

    /**
     * Colour for changed link.
     */
    private final Color theColorChgLink;

    /**
     * Constructor.
     */
    protected JDataHTML() {
        this(COLOR_STANDARD, COLOR_CHANGED, COLOR_LINK, COLOR_CHGLINK);
    }

    /**
     * Constructor.
     * @param pStandard the standard colour
     * @param pChanged the changed colour
     * @param pLink the link colour
     * @param pChgLink the changed link colour
     */
    protected JDataHTML(final Color pStandard,
                        final Color pChanged,
                        final Color pLink,
                        final Color pChgLink) {
        /* Set the colours */
        theColorStandard = pStandard;
        theColorChanged = pChanged;
        theColorLink = pLink;
        theColorChgLink = pChgLink;
    }

    /**
     * Build CSS styleSheet.
     * @param pSheet the styleSheet
     */
    protected void buildStylesheet(final StyleSheet pSheet) {
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Define standard font for body and table contents */
        myBuilder.append("body { font-family: Verdana, sans-serif; font-size: 1em; color: ");
        myBuilder.append(DataConverter.colorToHexString(theColorStandard));
        myBuilder.append(";}");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define tables */
        myBuilder.append("table { width: 90%; margin-left:5%; margin-right:5%; ");
        myBuilder.append("text-align: center; border-spacing: 1px; border-collapse: collapse; }");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define background colour for oddRow */
        myBuilder.append(".");
        myBuilder.append(CLASS_ODDROW);
        myBuilder.append(" td { background-color: #dddddd; text-align: center; border: 1px solid white; }");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define background colour for evenRow */
        myBuilder.append(".");
        myBuilder.append(CLASS_EVENROW);
        myBuilder.append(" td { background-color: #eeeeee; text-align: center; border: 1px solid white; }");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define table headers */
        myBuilder.append("th { background-color: #bbbbbb; font-weight:bold; color: white;}");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define alternate colour and style for changed elements */
        myBuilder.append(".");
        myBuilder.append(CLASS_CHANGED);
        myBuilder.append(" { font-style: italic; color: ");
        myBuilder.append(DataConverter.colorToHexString(theColorChanged));
        myBuilder.append(";}");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define alternate colour and style for security changed elements */
        myBuilder.append(".");
        myBuilder.append(CLASS_SECCHANGED);
        myBuilder.append(" { color: ");
        myBuilder.append(DataConverter.colorToHexString(theColorChanged));
        myBuilder.append(";}");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Set standard link definition */
        myBuilder.append("a { text-decoration: none; color: ");
        myBuilder.append(DataConverter.colorToHexString(theColorLink));
        myBuilder.append(";}");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Set hover colour for standard link */
        myBuilder.append("a:hover { color: ");
        myBuilder.append(DataConverter.colorToHexString(theColorLink.brighter()));
        myBuilder.append(";}");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Set changed link definition */
        myBuilder.append("a.");
        myBuilder.append(CLASS_CHANGED);
        myBuilder.append(" { font-style: italic; color: ");
        myBuilder.append(DataConverter.colorToHexString(theColorChgLink));
        myBuilder.append(";}");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Set hover colour for changed link */
        myBuilder.append("a.");
        myBuilder.append(CLASS_CHANGED);
        myBuilder.append(":hover { font-style: italic; color: ");
        myBuilder.append(DataConverter.colorToHexString(theColorChgLink.brighter()));
        myBuilder.append(";}");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);
    }

    /**
     * Build HTML table describing object.
     * @param pDetail linking detail
     * @param pObject the object to describe
     * @return the HTML table
     */
    public static StringBuilder formatHTMLObject(final JDataDetail pDetail,
                                                 final Object pObject) {
        /* Switch on object type */
        Object o = pObject;
        switch (getDataType(o)) {
            case Exception:
                o = new JDataException((Throwable) o);
                return formatHTMLDetail(pDetail, o);
            case Contents:
                return formatHTMLDetail(pDetail, o);
            case Map:
                return formatHTMLMap(pDetail, o);
            case StackTrace:
                return formatHTMLStack(o);
            case None:
            default:
                return null;
        }
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
        if (JDataDifference.class.isInstance(pObject)) {
            return getDataType(((JDataDifference) pObject).getObject());
        }
        return JDataType.None;
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
        Object myValue;

        /* Access valueSet if it exists */
        if (JDataValues.class.isInstance(pObject)) {
            myValues = ((JDataValues) pObject).getValueSet();
        }

        /* Loop through the fields */
        Iterator<JDataField> myIterator = myFields.fieldIterator();
        boolean isOdd = true;
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

            /* Start the field */
            myEntries.append("<tr class=\"");
            myEntries.append(isOdd ? CLASS_ODDROW : CLASS_EVENROW);
            myEntries.append("\"><td>");
            myEntries.append(myField.getName());
            myEntries.append("</td><td>");

            /* Format the value */
            String myFormat = formatHTMLValue(pDetail, myValue);
            myEntries.append(myFormat);
            myEntries.append("</td></tr>");

            /* Flip row type */
            isOdd = !isOdd;
        }

        /* Initialise results */
        myResults.setLength(0);
        myResults.append("<h2 align=\"center\">");
        myResults.append(myFields.getName());
        myResults.append("<table><thead>");
        myResults.append("<tr><th>Field</th><th>Value</th></tr>");
        myResults.append("</thead><tbody>");

        /* Add in the entries */
        myResults.append(myEntries);

        /* Terminate the table */
        myResults.append("</tbody></table>");

        /* Return the formatted item */
        return myResults;
    }

    /**
     * Format an HTML value
     * @param pDetail linking detail
     * @param pValue the value to format
     * @return the formatted value
     */
    private static String formatHTMLValue(final JDataDetail pDetail,
                                          final Object pValue) {
        /* Format the value */
        String myFormat = JDataObject.formatField(pValue);

        /* Perform special formatting for a long byte[] */
        if ((pValue instanceof byte[]) && (myFormat.length() > WRAP_HEXSTRING)) {
            StringBuilder myBuffer = new StringBuilder(BUFFER_LEN);

            /* Format the buffer */
            myBuffer.append(myFormat);

            /* Insert new lines */
            int iCount = myFormat.length() / WRAP_HEXSTRING;
            while (iCount > 0) {
                myBuffer.insert(WRAP_HEXSTRING * iCount--, '\n');
            }

            /* Obtain new format */
            myFormat = myBuffer.toString();
        }

        /* If this needs a linkage */
        if (getDataType(pValue) != JDataType.None) {
            /* Adjust for linkage */
            myFormat = pDetail.addDataLink(pValue, myFormat);

            /* else if it is a JDataDifference */
        } else if (pValue instanceof JDataDifference) {
            /* Access the difference */
            Difference myDifference = ((JDataDifference) pValue).getDifference();

            /* Format it */
            myFormat = formatHTMLChange(myFormat, myDifference);
        }

        /* Return the formatted value */
        return myFormat;
    }

    /**
     * Format an HTML changed value
     * @param pText the value to format
     * @param pDifference the difference
     * @return the formatted value
     */
    protected static String formatHTMLChange(final String pText,
                                             final Difference pDifference) {
        /* If there is no difference */
        if (pDifference.isIdentical()) {
            /* Just return the text */
            return pText;
        }

        StringBuilder myBuffer = new StringBuilder(BUFFER_LEN);

        /* Build a div section */
        myBuffer.append("<div class=\"");
        myBuffer.append(pDifference.isValueChanged() ? CLASS_CHANGED : CLASS_SECCHANGED);
        myBuffer.append("\">");

        /* Add value and reformat */
        myBuffer.append(pText);
        myBuffer.append("</div>");
        return myBuffer.toString();
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
        String myFormat;

        /* Loop through the fields */
        boolean isOdd = true;
        for (Map.Entry<?, ?> myEntry : myMap.entrySet()) {
            /* Access the key and value */
            Object myKey = myEntry.getKey();
            Object myValue = myEntry.getValue();

            /* Format the row */
            myEntries.append("<tr class=\"");
            myEntries.append(isOdd ? CLASS_ODDROW : CLASS_EVENROW);
            myEntries.append("\">");

            /* Format the key */
            myFormat = JDataObject.formatField(myKey);
            if (getDataType(myKey) != JDataType.None) {
                myFormat = pDetail.addDataLink(myKey, myFormat);
            }
            myEntries.append("<td>");
            myEntries.append(myFormat);
            myEntries.append("</td><td>");

            /* Format the value */
            myFormat = JDataObject.formatField(myValue);
            if (getDataType(myValue) != JDataType.None) {
                myFormat = pDetail.addDataLink(myValue, myFormat);
            }
            myEntries.append(myFormat);
            myEntries.append("</td></tr>");

            /* Flip row type */
            isOdd = !isOdd;
        }

        /* Initialise the string with an item name */
        myResults.append("<h2 align=\"center\">Map Elements");
        myResults.append("<table><thead>");
        myResults.append("<tr><th>Key</th><th>Value</th></tr></thead><tbody>");

        /* Add in the entries */
        myResults.append(myEntries);

        /* Terminate the table */
        myResults.append("</tbody></table>");

        /* Return the formatted item */
        return myResults;
    }

    /**
     * Build HTML table describing stack.
     * @param pStack the stack to describe
     * @return the HTML table
     */
    private static StringBuilder formatHTMLStack(final Object pStack) {
        StackTraceElement[] myArray = StackTraceElement[].class.cast(pStack);
        StringBuilder myResults = new StringBuilder(BUFFER_LEN);

        /* Add the stack trace */
        myResults.append("<h2 align=\"center\">Stack Trace");
        myResults.append("<table><thead>");
        myResults.append("<tr><th>Stack Trace</th></tr></thead><tbody>");

        /* Loop through the elements */
        boolean isOdd = true;
        for (StackTraceElement st : myArray) {
            /* Format the row */
            myResults.append("<tr class=\"");
            myResults.append(isOdd ? CLASS_ODDROW : CLASS_EVENROW);
            myResults.append("\">");

            /* Add the stack trace */
            myResults.append("<td>");
            myResults.append(st.toString());
            myResults.append("</td></tr>");

            /* Flip row type */
            isOdd = !isOdd;
        }

        /* Terminate the table */
        myResults.append("</tbody></table>");

        /* Return the formatted item */
        return myResults;
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
}
