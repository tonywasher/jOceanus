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
package net.sourceforge.joceanus.jmetis.viewer.swing;

import java.awt.Color;
import java.util.Iterator;
import java.util.Map;

import javax.swing.text.html.StyleSheet;

import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataValues;
import net.sourceforge.joceanus.jmetis.data.MetisDifference;
import net.sourceforge.joceanus.jmetis.data.MetisExceptionWrapper;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.data.MetisValueSet;
import net.sourceforge.joceanus.jmetis.field.swing.MetisFieldConfig;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerResource;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiUtils;

/**
 * Data object formatting in HTML.
 * @author Tony Washer
 */
public final class MetisViewerHTML {
    /**
     * The field column.
     */
    private static final String COLUMN_FIELD = MetisViewerResource.VIEWER_COLUMN_FIELD.getValue();

    /**
     * The value column.
     */
    private static final String COLUMN_VALUE = MetisViewerResource.VIEWER_COLUMN_VALUE.getValue();

    /**
     * The key column.
     */
    private static final String COLUMN_KEY = MetisViewerResource.VIEWER_COLUMN_KEY.getValue();

    /**
     * Forward link.
     */
    private static final String LINK_NEXT = MetisViewerResource.VIEWER_LINK_NEXT.getValue();

    /**
     * Backward link.
     */
    private static final String LINK_PREV = MetisViewerResource.VIEWER_LINK_PREV.getValue();

    /**
     * Map Table.
     */
    private static final String TABLE_MAP = MetisViewerResource.VIEWER_TABLE_MAP.getValue();

    /**
     * Sections table.
     */
    private static final String TABLE_SECTIONS = MetisViewerResource.VIEWER_TABLE_SECTIONS.getValue();

    /**
     * Stack Trace table.
     */
    private static final String TABLE_STACKTRACE = MetisViewerResource.VIEWER_TABLE_STACKTRACE.getValue();

    /**
     * Maximum Map entry count.
     */
    private static final int MAP_MAXENTRIES = 50;

    /**
     * Start header html.
     */
    private static final String HTML_HDRSTART = "<h2 align=\"center\">";

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
    private final MetisDataFormatter theFormatter;

    /**
     * Constructor.
     * @param pFieldConfig the field configuration
     * @param pFormatter the data formatter
     */
    protected MetisViewerHTML(final MetisFieldConfig pFieldConfig,
                              final MetisDataFormatter pFormatter) {
        /* Record the formatter */
        theFormatter = pFormatter;

        /* process configuration */
        processConfig(pFieldConfig);
    }

    /**
     * Process the configuration.
     * @param pFieldConfig the configuration
     */
    protected void processConfig(final MetisFieldConfig pFieldConfig) {
        /* Set the colours */
        theColorStandard = pFieldConfig.getStandardColor();
        theColorChanged = pFieldConfig.getChangedColor();
        theColorLink = pFieldConfig.getLinkColor();
        theColorChgLink = pFieldConfig.getChangedLinkColor();
    }

    /**
     * Build CSS styleSheet.
     * @param pSheet the styleSheet
     */
    protected void buildStylesheet(final StyleSheet pSheet) {
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Define standard font for body and table contents */
        myBuilder.append("body { font-family: Verdana, sans-serif; font-size: 1em; color: ");
        myBuilder.append(TethysSwingGuiUtils.colorToHexString(theColorStandard));
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
        myBuilder.append(TethysSwingGuiUtils.colorToHexString(theColorChanged));
        myBuilder.append(";}");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define alternate colour and style for security changed elements */
        myBuilder.append(".");
        myBuilder.append(CLASS_SECCHANGED);
        myBuilder.append(" { color: ");
        myBuilder.append(TethysSwingGuiUtils.colorToHexString(theColorChanged));
        myBuilder.append(";}");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Set standard link definition */
        myBuilder.append("a { text-decoration: none; color: ");
        myBuilder.append(TethysSwingGuiUtils.colorToHexString(theColorLink));
        myBuilder.append(";}");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Set hover colour for standard link */
        myBuilder.append("a:hover { color: ");
        myBuilder.append(TethysSwingGuiUtils.colorToHexString(theColorLink.brighter()));
        myBuilder.append(";}");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Set changed link definition */
        myBuilder.append("a.");
        myBuilder.append(CLASS_CHANGED);
        myBuilder.append(CSS_ITALIC);
        myBuilder.append(TethysSwingGuiUtils.colorToHexString(theColorChgLink));
        myBuilder.append(";}");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Set hover colour for changed link */
        myBuilder.append("a.");
        myBuilder.append(CLASS_CHANGED);
        myBuilder.append(":hover { font-style: italic; color: ");
        myBuilder.append(TethysSwingGuiUtils.colorToHexString(theColorChgLink.brighter()));
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
    public StringBuilder formatHTMLObject(final MetisViewerDetail pDetail,
                                          final Object pObject) {
        /* Switch on object type */
        Object o = pObject;
        switch (getDataType(o)) {
            case EXCEPTION:
                o = new MetisExceptionWrapper((Throwable) o);
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
    private static ViewerDataType getDataType(final Object pObject) {
        /* Determine which objects are supported */
        if (pObject == null) {
            return ViewerDataType.NONE;
        }
        if (MetisDataContents.class.isInstance(pObject)) {
            return ViewerDataType.CONTENTS;
        }
        if (Map.class.isInstance(pObject)) {
            return ViewerDataType.MAP;
        }
        if (ViewerMapSection.class.isInstance(pObject)) {
            return ViewerDataType.MAPSECTION;
        }
        if (Throwable.class.isInstance(pObject)) {
            return ViewerDataType.EXCEPTION;
        }
        if (StackTraceElement[].class.isInstance(pObject)) {
            return ViewerDataType.STACKTRACE;
        }
        if (MetisDataDifference.class.isInstance(pObject)) {
            return getDataType(((MetisDataDifference) pObject).getObject());
        }
        return ViewerDataType.NONE;
    }

    /**
     * does the object format need wrapping?
     * @param pObject the object
     * @return true/false
     */
    private static boolean needsWrapping(final Object pObject) {
        /* Determine whether we need wrapping */
        Object myObject = pObject;
        if (MetisDataDifference.class.isInstance(myObject)) {
            myObject = ((MetisDataDifference) pObject).getObject();
        }
        return byte[].class.isInstance(myObject);
    }

    /**
     * Build HTML table describing ReportObject.
     * @param pDetail linking detail
     * @param pObject the ReportObject to describe
     * @return the HTML table
     */
    private StringBuilder formatHTMLDetail(final MetisViewerDetail pDetail,
                                           final Object pObject) {
        MetisDataContents myDetail = MetisDataContents.class.cast(pObject);
        MetisDataValues myValueCtl = null;
        StringBuilder myResults = new StringBuilder(BUFFER_LEN);
        StringBuilder myEntries = new StringBuilder(BUFFER_LEN);
        MetisFields myFields = myDetail.getDataFields();
        MetisValueSet myValues = null;
        Object myValue;

        /* Access valueSet if it exists */
        if (MetisDataValues.class.isInstance(pObject)) {
            myValueCtl = (MetisDataValues) pObject;
            myValues = myValueCtl.getValueSet();
        }

        /* Loop through the fields */
        Iterator<MetisField> myIterator = myFields.fieldIterator();
        boolean isOdd = true;
        while (myIterator.hasNext()) {
            /* Access Field */
            MetisField myField = myIterator.next();

            /* Access the value */
            if ((myField.isValueSetField())
                && (myValues != null)) {
                myValue = myValueCtl.skipField(myField)
                                                        ? MetisFieldValue.SKIP
                                                        : myValues.getValue(myField);
            } else {
                myValue = myDetail.getFieldValue(myField);
            }

            /* Skip value if required */
            if (MetisFieldValue.SKIP.equals(myValue)) {
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
        myResults.append(HTML_HDRSTART);
        myResults.append(myFields.getName());
        myResults.append(HTML_TABSTART);
        myResults.append(HTML_ROWSTART);
        myResults.append(HTML_HDRCELLSTART);
        myResults.append(COLUMN_FIELD);
        myResults.append(HTML_HDRCELLEND);
        myResults.append(HTML_HDRCELLSTART);
        myResults.append(COLUMN_VALUE);
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
    private String formatHTMLValue(final MetisViewerDetail pDetail,
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
        if (getDataType(pValue) != ViewerDataType.NONE) {
            /* Adjust for linkage */
            myFormat = pDetail.addDataLink(pValue, myFormat);

            /* else if it is a JDataDifference */
        } else if (pValue instanceof MetisDataDifference) {
            /* Access the difference */
            MetisDifference myDifference = ((MetisDataDifference) pValue).getDifference();

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
                                             final MetisDifference pDifference) {
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
    private StringBuilder formatHTMLMap(final MetisViewerDetail pDetail,
                                        final Object pMap) {
        /* Access the map */
        Map<?, ?> myMap = Map.class.cast(pMap);

        /* If we have too many entries */
        if (myMap.size() > (MAP_MAXENTRIES << 1)) {
            /* Build as first section of multiMap */
            return formatHTMLMapSection(pDetail, new ViewerMapSection(myMap, 0));
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
    private static StringBuilder buildHTMLMap(final StringBuilder pEntries) {
        /* Initialise the string with an item name */
        StringBuilder myResults = new StringBuilder(BUFFER_LEN);
        myResults.append(HTML_HDRSTART);
        myResults.append(TABLE_MAP);
        myResults.append(HTML_TABSTART);
        myResults.append(HTML_ROWSTART);
        myResults.append(HTML_HDRCELLSTART);
        myResults.append(COLUMN_KEY);
        myResults.append(HTML_HDRCELLEND);
        myResults.append(HTML_HDRCELLSTART);
        myResults.append(COLUMN_VALUE);
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
    private StringBuilder formatHTMLMapEntry(final MetisViewerDetail pDetail,
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
        if (getDataType(myKey) != ViewerDataType.NONE) {
            myFormat = pDetail.addDataLink(myKey, myFormat);
        }
        myBuild.append(HTML_CELLSTART);
        myBuild.append(myFormat);
        myBuild.append(HTML_CELLEND);
        myBuild.append(HTML_CELLSTART);

        /* Format the value */
        myFormat = theFormatter.formatObject(myValue);
        if (getDataType(myValue) != ViewerDataType.NONE) {
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
    private StringBuilder formatHTMLMapSection(final MetisViewerDetail pDetail,
                                               final Object pSection) {
        StringBuilder myEntries = new StringBuilder(BUFFER_LEN);

        /* Access iterator for map */
        ViewerMapSection mySection = ViewerMapSection.class.cast(pSection);
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
        myResults.append("<thead><th>");
        myResults.append(TABLE_SECTIONS);
        myResults.append(HTML_HDRCELLEND);

        /* Handle Backward Link */
        if (hasPrev) {
            myResults.append(HTML_HDRCELLSTART);
            myResults.append(pDetail.addDataLink(new ViewerMapSection(myMap, myPart - 1), LINK_PREV));
            myResults.append(HTML_HDRCELLEND);
        }

        /* Handle Forward Link */
        if (hasMore) {
            myResults.append(HTML_HDRCELLSTART);
            myResults.append(pDetail.addDataLink(new ViewerMapSection(myMap, myPart + 1), LINK_NEXT));
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
        myResults.append(HTML_HDRSTART);
        myResults.append(TABLE_STACKTRACE);
        myResults.append(HTML_TABSTART);
        myResults.append(HTML_ROWSTART);
        myResults.append(HTML_HDRCELLSTART);
        myResults.append(TABLE_STACKTRACE);
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
    private static final class ViewerMapSection {
        /**
         * The map.
         */
        private final Map<?, ?> theMap;

        /**
         * The Part.
         */
        private final int thePart;

        /**
         * Constructor.
         * @param pMap the Map
         * @param pPart the part#
         */
        private ViewerMapSection(final Map<?, ?> pMap,
                                 final int pPart) {
            theMap = pMap;
            thePart = pPart;
        }

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
    }

    /**
     * Data Object types.
     */
    private enum ViewerDataType {
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
