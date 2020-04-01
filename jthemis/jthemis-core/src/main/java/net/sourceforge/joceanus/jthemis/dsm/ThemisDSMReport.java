/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2019 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jthemis.dsm;

import java.util.Iterator;
import java.util.List;
import javax.swing.text.html.HTML.Tag;

/**
 * DSM report.
 */
public final class ThemisDSMReport {
    /**
     * The reference separator.
     */
    public static final String SEP_REF = "-";

    /**
     * Private constructor.
     */
    private ThemisDSMReport() {
    }

    /**
     * report on a module.
     * @param pModule the module to report on
     * @return the report
     */
    public static String reportOnModule(final ThemisDSMModule pModule) {
        /* Create a stringBuilder */
        final StringBuilder myBuilder = new StringBuilder();

        /* Start document */
        addStartElement(myBuilder, Tag.HTML);
        addStartElement(myBuilder, Tag.BODY);

        /* Build table */
        addStartElement(myBuilder, Tag.TABLE);
        buildTableHeader(myBuilder, pModule);

        /* Loop through the packages */
        int myKey = 0;
        int myRowNo = 0;
        final Iterator<ThemisDSMPackage> myIterator = pModule.packageIterator();
        while (myIterator.hasNext()) {
            final ThemisDSMPackage myPackage = myIterator.next();
            buildTableRow(myBuilder, pModule, myPackage, myRowNo++, myKey++);
        }

        /* Finish the table */
        addEndElement(myBuilder, Tag.TABLE);

        /* Finish document */
        addEndElement(myBuilder, Tag.BODY);
        addEndElement(myBuilder, Tag.HTML);
        return myBuilder.toString();
    }

    /**
     * build the table header.
     * @param pBuilder the builder
     * @param pModule the module to report on
     */
    private static void buildTableHeader(final StringBuilder pBuilder,
                                        final ThemisDSMModule pModule) {
        /* Start the row */
        addStartElementWithClass(pBuilder, Tag.TR, "dsm-row-header");

        /* Build the initial headers */
        addTextElement(pBuilder, Tag.TH, "Package");
        addTextElement(pBuilder, Tag.TH, "Key");

        /* Loop through the packages */
        int myKey = 0;
        final int myCount = pModule.getPackageCount();
        for (int i = 0; i < myCount; i++) {
            /* Add the key */
            addTextElementWithClass(pBuilder, Tag.TH, "dsm-col-count", getKeyForIndex(myKey++));
        }

        /* Complete the row */
        addEndElement(pBuilder, Tag.TR);
    }

    /**
     * build a row for the table.
     * @param pBuilder the builder
     * @param pModule the module to report on
     * @param pPackage the package to report on
     * @param pRowNo the row number
     * @param pKey the package key
     */
    private static void buildTableRow(final StringBuilder pBuilder,
                                      final ThemisDSMModule pModule,
                                      final ThemisDSMPackage pPackage,
                                      final int pRowNo,
                                      final int pKey) {
        /* Start the row */
        final String myClass = (pRowNo % 2 == 0) ? "dsm-row-even" : "dsm-row-odd";
        addStartElementWithClass(pBuilder, Tag.TR, myClass);

        /* Build the initial cells */
        addTextElementWithClass(pBuilder, Tag.TD, "dsm-cell-name-left", pPackage.getPackageName());
        final String myKeyTo = getKeyForIndex(pKey);
        addTextElement(pBuilder, Tag.TD, myKeyTo);

        /* Loop through the packages */
        int myKey = 0;
        final Iterator<ThemisDSMPackage> myIterator = pModule.packageIterator();
        while (myIterator.hasNext()) {
            final ThemisDSMPackage myPackage = myIterator.next();

            /* Add the key */
            final boolean isSelf = pPackage.equals(myPackage);
            final boolean isCircular = myPackage.isCircular();
            final int myCount = myPackage.getReferencesTo(pPackage).size();
            if (isSelf) {
                final String myCellClass = isCircular ? "dsm-cell-circular" : "dsm-cell-self";
                addTextElementWithClass(pBuilder, Tag.TD, myCellClass, "");
            } else if (myCount == 0) {
                addTextElement(pBuilder, Tag.TD, "");
            } else {
                final String myKeyFrom = getKeyForIndex(myKey);
                addTextElementWithLink(pBuilder, Tag.TD, Integer.toString(myCount),
                        myKeyFrom + SEP_REF + myKeyTo);
            }
            myKey++;
        }

        /* Complete the row */
        addEndElement(pBuilder, Tag.TR);
    }

    /**
     * Obtain key for index.
     * @param pIndex the index
     * @return the key
     */
    static String getKeyForIndex(final int pIndex) {
        /* Handle simple index */
        final int myRadix = 'Z' - 'A' + 1;
        if (pIndex < myRadix) {
            return Character.toString('A' + pIndex);
        }

        /* Handle double index */
        final int myFirst = pIndex / myRadix - 1;
        final int mySecond = pIndex % myRadix;
        return new String(new char[] { (char) ('A' + myFirst), (char) ('A' + mySecond) });
    }

    /**
     * Obtain index for key.
     * @param pKey the key
     * @return the index
     */
    public static int getIndexForKey(final String pKey) {
        /* Handle simple key */
        if (pKey.length() == 1) {
            return pKey.charAt(0) - 'A';
        }

        /* Handle double index */
        final int myRadix = 'Z' - 'A' + 1;
        final int myFirst = pKey.charAt(0) - 'A' + 1;
        final int mySecond = pKey.charAt(1) - 'A';
        return myFirst * myRadix + mySecond;
    }

    /**
     * report on package links.
     * @param pSource the source package to report on
     * @param pTarget the target package to report on
     * @return the report
     */
    public static String reportOnPackageLinks(final ThemisDSMPackage pSource,
                                              final ThemisDSMPackage pTarget) {
        /* Create a stringBuilder */
        final StringBuilder myBuilder = new StringBuilder();

        /* Build table */
        addStartElement(myBuilder, Tag.TABLE);

        /* Table header */
        addStartElementWithClass(myBuilder, Tag.TR, "dsm-row-header");
        addTextElement(myBuilder, Tag.TH, "Class");
        addTextElement(myBuilder, Tag.TH, "References");
        addEndElement(myBuilder, Tag.TR);

        /* Loop through the packages */
        int myRowNo = 0;
        for (ThemisDSMClass myClass : pSource.getReferencesTo(pTarget)) {
            /* Access list of references */
            final List<ThemisDSMClass> myClasses = myClass.getReferencesTo(pTarget);

            /* Table header */
            final String myRowClass = (myRowNo++ % 2 == 0) ? "dsm-row-even" : "dsm-row-odd";
            addStartElementWithClass(myBuilder, Tag.TR, myRowClass);
            addTextElementWithSpan(myBuilder, Tag.TD, myClass.getClassName(), myClasses.size());
            boolean bFirst = true;
            for (ThemisDSMClass myRef : myClasses) {
                if (!bFirst) {
                    addStartElementWithClass(myBuilder, Tag.TR, myRowClass);
                }
                bFirst = false;
                addTextElement(myBuilder, Tag.TD, myRef.getClassName());
                addEndElement(myBuilder, Tag.TR);
            }
        }

        /* Finish the table */
        addEndElement(myBuilder, Tag.TABLE);

        /* Finish document */
        addEndElement(myBuilder, Tag.BODY);
        addEndElement(myBuilder, Tag.HTML);
        return myBuilder.toString();
    }

    /**
     * Add text element.
     * @param pBuilder the builder
     * @param pElement the element
     * @param pText the text
     */
    private static void addTextElement(final StringBuilder pBuilder,
                                       final Tag pElement,
                                       final String pText) {
        /* Add the text element */
        addStartElement(pBuilder, pElement);
        pBuilder.append(pText);
        addEndElement(pBuilder, pElement);
    }

    /**
     * Add text element.
     * @param pBuilder the builder
     * @param pElement the element
     * @param pText the text
     * @param pLink the link
     */
    private static void addTextElementWithLink(final StringBuilder pBuilder,
                                               final Tag pElement,
                                               final String pText,
                                               final String pLink) {
        /* Add the text element */
        addStartElement(pBuilder, pElement);
        addStartElementWithLink(pBuilder, Tag.A, pLink);
        pBuilder.append(pText);
        addEndElement(pBuilder, Tag.A);
        addEndElement(pBuilder, pElement);
    }
    /**
     * Add text element with Clas.
     * @param pBuilder the builder
     * @param pElement the element
     * @param pClass the class of the element
     * @param pText the text
     */
    private static void addTextElementWithClass(final StringBuilder pBuilder,
                                                final Tag pElement,
                                                final String pClass,
                                                final String pText) {
        /* Add the text element */
        addStartElementWithClass(pBuilder, pElement, pClass);
        pBuilder.append(pText);
        addEndElement(pBuilder, pElement);
    }

    /**
     * Add text element with span.
     * @param pBuilder the builder
     * @param pElement the element
     * @param pText the text
     * @param pSpan the span count
     */
    private static void addTextElementWithSpan(final StringBuilder pBuilder,
                                               final Tag pElement,
                                               final String pText,
                                               final int pSpan) {
        /* Add the text element */
        addStartElementWithSpan(pBuilder, pElement, pSpan);
        pBuilder.append(pText);
        addEndElement(pBuilder, pElement);
    }

    /**
     * Add start element with class.
     * @param pBuilder the builder
     * @param pElement the element
     * @param pClass the class of the element
     */
    private static void addStartElementWithClass(final StringBuilder pBuilder,
                                                 final Tag pElement,
                                                 final String pClass) {
        /* Start the element */
        pBuilder.append("<");
        pBuilder.append(pElement);
        pBuilder.append(" class=\"");
        pBuilder.append(pClass);
        pBuilder.append("\">");
    }

    /**
     * Add start element with link.
     * @param pBuilder the builder
     * @param pElement the element
     * @param pLink the link
     */
    private static void addStartElementWithLink(final StringBuilder pBuilder,
                                                final Tag pElement,
                                                final String pLink) {
        /* Start the element */
        pBuilder.append("<");
        pBuilder.append(pElement);
        pBuilder.append(" href=\"");
        pBuilder.append(pLink);
        pBuilder.append("\">");
    }

    /**
     * Add start element with span.
     * @param pBuilder the builder
     * @param pElement the element
     * @param pSpan the span count
     */
    private static void addStartElementWithSpan(final StringBuilder pBuilder,
                                                final Tag pElement,
                                                final int pSpan) {
        /* Start the element */
        pBuilder.append("<");
        pBuilder.append(pElement);
        if (pSpan > 1) {
            pBuilder.append(" rowspan=\"");
            pBuilder.append(pSpan);
            pBuilder.append("\"");
        }
        pBuilder.append(">");
    }

    /**
     * Add start element.
     * @param pBuilder the builder
     * @param pElement the element
     */
    private static void addStartElement(final StringBuilder pBuilder,
                                        final Tag pElement) {
        /* Start the element */
        pBuilder.append("<");
        pBuilder.append(pElement);
        pBuilder.append(">");
    }

    /**
     * Add end element.
     * @param pBuilder the builder
     * @param pElement the element
     */
    private static void addEndElement(final StringBuilder pBuilder,
                                      final Tag pElement) {
        /* Start the element */
        pBuilder.append("</");
        pBuilder.append(pElement);
        pBuilder.append(">");
    }
}
