/*******************************************************************************
 * jMetis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.viewer;

import java.awt.Color;
import java.util.Iterator;
import java.util.Map;

import javax.swing.text.html.StyleSheet;

import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataContents;
import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataDifference;
import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataValues;
import net.sourceforge.joceanus.jtethys.DataConverter;

/**
 * Data object formatting in HTML.
 * @author Tony Washer
 */
public final class JDataHTML {
    /**
     * Maximum Map entry count.
     */
    private static final int MAP_MAXENTRIES = 50;

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
     * Start table html.
     */
    private static final String HTML_TABSTART = "<table><thead>";

    /**
     * Start table body html.
     */
    private static final String HTML_TABBODY = "</thead><tbody>";

    /**
     * End table html.
     */
    private static final String HTML_TABEND = "</tbody></table>";

    /**
     * Font italic css.
     */
    private static final String CSS_ITALIC = " { font-style: italic; color: ";

    /**
     * Start table row html.
     */
    private static final String HTML_ROWSTART = "<tr>";

    /**
     * Start table class row html.
     */
    private static final String HTML_ROWCLSSTART = "<tr class=\"";

    /**
     * End table class row html.
     */
    private static final String HTML_ROWCLSEND = "\">";

    /**
     * End table row html.
     */
    private static final String HTML_ROWEND = "</tr>";

    /**
     * Start table header cell html.
     */
    private static final String HTML_HDRCELLSTART = "<th>";

    /**
     * End table header cell html.
     */
    private static final String HTML_HDRCELLEND = "</th>";

    /**
     * Start table cell html.
     */
    private static final String HTML_CELLSTART = "<td>";

    /**
     * End table cell html.
     */
    private static final String HTML_CELLEND = "</td>";

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
    public static final String CLASS_CHANGED = "changed";

    /**
     * Name of security changed cell class.
     */
    private static final String CLASS_SECCHANGED = "security";

    /**
     * Value header.
     */
    private static final String HDR_VALUE = "Value";

    /**
     * Forward link.
     */
    private static final String LINK_NEXT = "Next";

    /**
     * Backward link.
     */
    private static final String LINK_PREV = "Previous";

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
    private Color theColorStandard;

    /**
     * Colour for changed elements.
     */
    private Color theColorChanged;

    /**
     * Colour for standard links.
     */
    private Color theColorLink;

    /**
     * Colour for changed link.
     */
    private Color theColorChgLink;

    /**
     * Data formatter.
     */
    private final JDataFormatter theFormatter;

    /**
     * Constructor.
     * @param pFormatter the data formatter
     */
    protected JDataHTML(final JDataFormatter pFormatter) {
        /* Record the formatter */
        theFormatter = pFormatter;

        /* Set default colours */
        setColors(COLOR_STANDARD, COLOR_CHANGED, COLOR_LINK, COLOR_CHGLINK);
    }

    /**
     * Constructor.
     * @param pStandard the standard colour
     * @param pChanged the changed colour
     * @param pLink the link colour
     * @param pChgLink the changed link colour
     */
    protected void setColors(final Color pStandard,
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
        myBuilder.append("th { background-color: #bbbbbb; font-weight:bold; color: white; border: 1px solid white; }");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define alternate colour and style for changed elements */
        myBuilder.append(".");
        myBuilder.append(CLASS_CHANGED);
        myBuilder.append(CSS_ITALIC);
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
        myBuilder.append(CSS_ITALIC);
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
    public StringBuilder formatHTMLObject(final JDataDetail pDetail,
                                          final Object pObject) {
        /* Switch on object type */
        Object o = pObject;
        switch (getDataType(o)) {
            case EXCEPTION:
                o = new JMetisExceptionWrapper((Throwable) o);
                return formatHTMLDetail(pDetail, o);
            case CONTENTS:
                return formatHTMLDetail(pDetail, o);
            case MAP:
                return formatHTMLMap(pDetail, o);
            case MAPSECTION:
                return formatHTMLMapSection(pDetail, o);
            case STACKTRACE:
                return formatHTMLStack(o);
            case NONE:
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
            return JDataType.NONE;
        }
        if (JDataContents.class.isInstance(pObject)) {
            return JDataType.CONTENTS;
        }
        if (Map.class.isInstance(pObject)) {
            return JDataType.MAP;
        }
        if (MapSection.class.isInstance(pObject)) {
            return JDataType.MAPSECTION;
        }
        if (Throwable.class.isInstance(pObject)) {
            return JDataType.EXCEPTION;
        }
        if (StackTraceElement[].class.isInstance(pObject)) {
            return JDataType.STACKTRACE;
        }
        if (JDataDifference.class.isInstance(pObject)) {
            return getDataType(((JDataDifference) pObject).getObject());
        }
        return JDataType.NONE;
    }

    /**
     * does the object format need wrapping?
     * @param pObject the object
     * @return true/false
     */
    private static boolean needsWrapping(final Object pObject) {
        /* Determine whether we need wrapping */
        Object myObject = pObject;
        if (JDataDifference.class.isInstance(myObject)) {
            myObject = ((JDataDifference) pObject).getObject();
        }
        return byte[].class.isInstance(myObject);
    }

    /**
     * Build HTML table describing ReportObject.
     * @param pDetail linking detail
     * @param pObject the ReportObject to describe
     * @return the HTML table
     */
    private StringBuilder formatHTMLDetail(final JDataDetail pDetail,
                                           final Object pObject) {
        JDataContents myDetail = JDataContents.class.cast(pObject);
        JDataValues myValueCtl = null;
        StringBuilder myResults = new StringBuilder(BUFFER_LEN);
        StringBuilder myEntries = new StringBuilder(BUFFER_LEN);
        JDataFields myFields = myDetail.getDataFields();
        ValueSet myValues = null;
        Object myValue;

        /* Access valueSet if it exists */
        if (JDataValues.class.isInstance(pObject)) {
            myValueCtl = (JDataValues) pObject;
            myValues = myValueCtl.getValueSet();
        }

        /* Loop through the fields */
        Iterator<JDataField> myIterator = myFields.fieldIterator();
        boolean isOdd = true;
        while (myIterator.hasNext()) {
            /* Access Field */
            JDataField myField = myIterator.next();

            /* Access the value */
            if ((myField.isValueSetField())
                && (myValues != null)) {
                myValue = myValueCtl.skipField(myField)
                                                       ? JDataFieldValue.SKIP
                                                       : myValues.getValue(myField);
            } else {
                myValue = myDetail.getFieldValue(myField);
            }

            /* Skip value if required */
            if (myValue.equals(JDataFieldValue.SKIP)) {
                continue;
            }

            /* Start the field */
            myEntries.append(HTML_ROWCLSSTART);
            myEntries.append(isOdd
                                  ? CLASS_ODDROW
                                  : CLASS_EVENROW);
            myEntries.append(HTML_ROWCLSEND);
            myEntries.append(HTML_CELLSTART);
            myEntries.append(myField.getName());
            myEntries.append(HTML_CELLEND);
            myEntries.append(HTML_CELLSTART);

            /* Format the value */
            String myFormat = formatHTMLValue(pDetail, myValue);
            myEntries.append(myFormat);
            myEntries.append(HTML_CELLEND);
            myEntries.append(HTML_ROWEND);

            /* Flip row type */
            isOdd = !isOdd;
        }

        /* Initialise results */
        myResults.setLength(0);
        myResults.append("<h2 align=\"center\">");
        myResults.append(myFields.getName());
        myResults.append(HTML_TABSTART);
        myResults.append(HTML_ROWSTART);
        myResults.append(HTML_HDRCELLSTART);
        myResults.append("Field");
        myResults.append(HTML_HDRCELLEND);
        myResults.append(HTML_HDRCELLSTART);
        myResults.append(HDR_VALUE);
        myResults.append(HTML_HDRCELLEND);
        myResults.append(HTML_ROWEND);
        myResults.append(HTML_TABBODY);

        /* Add in the entries */
        myResults.append(myEntries);

        /* Terminate the table */
        myResults.append(HTML_TABEND);

        /* Return the formatted item */
        return myResults;
    }

    /**
     * Format an HTML value.
     * @param pDetail linking detail
     * @param pValue the value to format
     * @return the formatted value
     */
    private String formatHTMLValue(final JDataDetail pDetail,
                                   final Object pValue) {
        /* Format the value */
        String myFormat = theFormatter.formatObject(pValue);

        /* Perform special formatting for a long byte[] */
        if (needsWrapping(pValue)
            && (myFormat.length() > WRAP_HEXSTRING)) {
            StringBuilder myBuffer = new StringBuilder(BUFFER_LEN);

            /* Format the buffer */
            myBuffer.append(myFormat);

            /* Insert new lines */
            int iCount = myFormat.length()
                         / WRAP_HEXSTRING;
            while (iCount > 0) {
                myBuffer.insert(WRAP_HEXSTRING
                                * iCount--, '\n');
            }

            /* Obtain new format */
            myFormat = myBuffer.toString();
        }

        /* If this needs a linkage */
        if (getDataType(pValue) != JDataType.NONE) {
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
     * Format an HTML changed value.
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
        myBuffer.append(pDifference.isValueChanged()
                                                    ? CLASS_CHANGED
                                                    : CLASS_SECCHANGED);
        myBuffer.append(HTML_ROWCLSEND);

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
    private StringBuilder formatHTMLMap(final JDataDetail pDetail,
                                        final Object pMap) {
        /* Access the map */
        Map<?, ?> myMap = Map.class.cast(pMap);

        /* If we have too many entries */
        if (myMap.size() > (MAP_MAXENTRIES << 1)) {
            /* Build as first section of multiMap */
            return formatHTMLMapSection(pDetail, new MapSection(myMap, 0));
        }

        /* Loop through the fields */
        StringBuilder myEntries = new StringBuilder(BUFFER_LEN);
        boolean isOdd = true;
        for (Map.Entry<?, ?> myEntry : myMap.entrySet()) {
            /* Access the key and value */
            StringBuilder myBuild = formatHTMLMapEntry(pDetail, myEntry, isOdd);
            myEntries.append(myBuild);

            /* Flip row type */
            isOdd = !isOdd;
        }

        /* Return the formatted item */
        return buildHTMLMap(myEntries);
    }

    /**
     * Build HTML table describing map.
     * @param pEntries the map entries
     * @return the HTML table
     */
    private StringBuilder buildHTMLMap(final StringBuilder pEntries) {

        /* Initialise the string with an item name */
        StringBuilder myResults = new StringBuilder(BUFFER_LEN);
        myResults.append("<h2 align=\"center\">Map Elements");
        myResults.append(HTML_TABSTART);
        myResults.append(HTML_ROWSTART);
        myResults.append(HTML_HDRCELLSTART);
        myResults.append("Key");
        myResults.append(HTML_HDRCELLEND);
        myResults.append(HTML_HDRCELLSTART);
        myResults.append(HDR_VALUE);
        myResults.append(HTML_HDRCELLEND);
        myResults.append(HTML_ROWEND);
        myResults.append(HTML_TABBODY);

        /* Add in the entries */
        myResults.append(pEntries);

        /* Terminate the table */
        myResults.append(HTML_TABEND);

        /* Return the formatted item */
        return myResults;
    }

    /**
     * Format map entry.
     * @param pDetail linking detail
     * @param pEntry the map entry to describe
     * @param isOdd is this and odd row true/false
     * @return the HTML table
     */
    private StringBuilder formatHTMLMapEntry(final JDataDetail pDetail,
                                             final Object pEntry,
                                             final boolean isOdd) {
        Map.Entry<?, ?> myEntry = Map.Entry.class.cast(pEntry);
        StringBuilder myBuild = new StringBuilder(BUFFER_LEN);

        /* Access the key and value */
        Object myKey = myEntry.getKey();
        Object myValue = myEntry.getValue();

        /* Format the row */
        myBuild.append(HTML_ROWCLSSTART);
        myBuild.append(isOdd
                            ? CLASS_ODDROW
                            : CLASS_EVENROW);
        myBuild.append(HTML_ROWCLSEND);

        /* Format the key */
        String myFormat = theFormatter.formatObject(myKey);
        if (getDataType(myKey) != JDataType.NONE) {
            myFormat = pDetail.addDataLink(myKey, myFormat);
        }
        myBuild.append(HTML_CELLSTART);
        myBuild.append(myFormat);
        myBuild.append(HTML_CELLEND);
        myBuild.append(HTML_CELLSTART);

        /* Format the value */
        myFormat = theFormatter.formatObject(myValue);
        if (getDataType(myValue) != JDataType.NONE) {
            myFormat = pDetail.addDataLink(myValue, myFormat);
        }
        myBuild.append(myFormat);
        myBuild.append(HTML_CELLEND);
        myBuild.append(HTML_ROWEND);

        /* Return the formatted item */
        return myBuild;
    }

    /**
     * Build HTML table describing map.
     * @param pDetail linking detail
     * @param pSection the map section to describe
     * @return the HTML table
     */
    private StringBuilder formatHTMLMapSection(final JDataDetail pDetail,
                                               final Object pSection) {
        StringBuilder myEntries = new StringBuilder(BUFFER_LEN);

        /* Access iterator for map */
        MapSection mySection = MapSection.class.cast(pSection);
        Map<?, ?> myMap = mySection.getMap();
        int myPart = mySection.getPart();
        Iterator<?> myIterator = myMap.entrySet().iterator();

        /* If the section is not the first */
        boolean hasPrev = myPart > 0;
        if (hasPrev) {
            /* Skip leading entries */
            int myStart = myPart
                          * MAP_MAXENTRIES;
            while (myIterator.hasNext()
                   && (myStart-- > 0)) {
                myIterator.next();
            }
        }

        /* Loop up to the limit */
        boolean isOdd = true;
        int myCount = MAP_MAXENTRIES;
        while (myIterator.hasNext()
               && (myCount-- > 0)) {
            /* Access the key and value */
            Map.Entry<?, ?> myEntry = Map.Entry.class.cast(myIterator.next());

            /* Access the key and value */
            StringBuilder myBuild = formatHTMLMapEntry(pDetail, myEntry, isOdd);
            myEntries.append(myBuild);

            /* Flip row type */
            isOdd = !isOdd;
        }

        /* Determine whether there are further parts */
        boolean hasMore = myIterator.hasNext();

        /* Create the StringBuilder */
        StringBuilder myResults = new StringBuilder(BUFFER_LEN);

        /* Build the links */
        myResults.append("<table border=\"1\" width=\"90%\" align=\"center\">");
        myResults.append("<thead><th>Sections</th>");

        /* Handle Backward Link */
        if (hasPrev) {
            myResults.append(HTML_HDRCELLSTART);
            myResults.append(pDetail.addDataLink(new MapSection(myMap, myPart - 1), LINK_PREV));
            myResults.append(HTML_HDRCELLEND);
        }

        /* Handle Forward Link */
        if (hasMore) {
            myResults.append(HTML_HDRCELLSTART);
            myResults.append(pDetail.addDataLink(new MapSection(myMap, myPart + 1), LINK_NEXT));
            myResults.append(HTML_HDRCELLEND);
        }

        /* Complete the table */
        myResults.append(HTML_TABBODY);
        myResults.append(HTML_TABEND);

        /* Return the formatted item */
        myResults.append(buildHTMLMap(myEntries));
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
        myResults.append(HTML_TABSTART);
        myResults.append(HTML_ROWSTART);
        myResults.append(HTML_HDRCELLSTART);
        myResults.append("Stack Trace");
        myResults.append(HTML_HDRCELLEND);
        myResults.append(HTML_ROWEND);
        myResults.append(HTML_TABBODY);

        /* Loop through the elements */
        boolean isOdd = true;
        for (StackTraceElement st : myArray) {
            /* Format the row */
            myResults.append(HTML_ROWCLSSTART);
            myResults.append(isOdd
                                  ? CLASS_ODDROW
                                  : CLASS_EVENROW);
            myResults.append(HTML_ROWCLSEND);

            /* Add the stack trace */
            myResults.append(HTML_CELLSTART);
            myResults.append(st.toString());
            myResults.append(HTML_CELLEND);
            myResults.append(HTML_ROWEND);

            /* Flip row type */
            isOdd = !isOdd;
        }

        /* Terminate the table */
        myResults.append(HTML_TABEND);

        /* Return the formatted item */
        return myResults;
    }

    /**
     * Map section class.
     */
    private static final class MapSection {
        /**
         * The map.
         */
        private final Map<?, ?> theMap;

        /**
         * The Part.
         */
        private final int thePart;

        /**
         * Obtain the map.
         * @return the map
         */
        private Map<?, ?> getMap() {
            return theMap;
        }

        /**
         * Obtain the part.
         * @return the part#
         */
        private int getPart() {
            return thePart;
        }

        /**
         * Constructor.
         * @param pMap the Map
         * @param pPart the part#
         */
        private MapSection(final Map<?, ?> pMap,
                           final int pPart) {
            theMap = pMap;
            thePart = pPart;
        }
    }

    /**
     * Data Object types.
     */
    private enum JDataType {
        /**
         * Contents.
         */
        CONTENTS,

        /**
         * Map.
         */
        MAP,

        /**
         * Map Section.
         */
        MAPSECTION,

        /**
         * Exception.
         */
        EXCEPTION,

        /**
         * StackTrace.
         */
        STACKTRACE,

        /**
         * None.
         */
        NONE;
    }
}
