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
     * Private constructor.
     */
    private ThemisDSMReport() {
    }

    /**
     * report on a project.
     * @param pProject the project to report on
     * @return the report
     */
    static String reportOnProject(final ThemisDSMProject pProject) {
        /* Build title */
        final StringBuilder myBuilder = new StringBuilder();
        addStartElement(myBuilder, Tag.HTML);
        addStartElement(myBuilder, Tag.HEAD);
        myBuilder.append("<link REL=\"StyleSheet\" HREF=\"dsm.css\" TYPE=\"text/css\" MEDIA=\"screen, print\">");
        addEndElement(myBuilder, Tag.HEAD);
        addStartElement(myBuilder, Tag.BODY);
        addElement(myBuilder, Tag.H1, pProject.getProjectName());

        /* Loop through the modules */
        final Iterator<ThemisDSMModule> myIterator = pProject.moduleIterator();
        while (myIterator.hasNext()) {
            final ThemisDSMModule myModule = myIterator.next();

            /* Process the module */
            reportOnModule(myBuilder, myModule);
        }

        /* Finish document */
        addEndElement(myBuilder, Tag.BODY);
        addEndElement(myBuilder, Tag.HTML);

        /* Return the report */
        return myBuilder.toString();
    }

    /**
     * report on a module.
     * @param pBuilder the builder
     * @param pModule the module to report on
     */
    private static void reportOnModule(final StringBuilder pBuilder,
                                       final ThemisDSMModule pModule) {
        /* If the Module has packages */
        if (pModule.hasPackages()) {
            /* Create a stringBuilder */
            final StringBuilder myBuilder = new StringBuilder();

            /* Build title */
            addElement(pBuilder, Tag.H2, pModule.getModuleName());

            /* Build table */
            addStartElement(pBuilder, Tag.TABLE);
            buildTableHeader(pBuilder, pModule);

            /* Loop through the packages */
            int myKey = 0;
            int myRowNo = 0;
            final Iterator<ThemisDSMPackage> myIterator = pModule.packageIterator();
            while (myIterator.hasNext()) {
                final ThemisDSMPackage myPackage = myIterator.next();
                myBuilder.append(buildTableRow(pBuilder, pModule, myPackage, myRowNo++, myKey++));
            }

            /* Finish the table */
            addEndElement(pBuilder, Tag.TABLE);
            pBuilder.append(myBuilder);
        }

        /* Loop through the submodules */
        final Iterator<ThemisDSMModule> myIterator = pModule.moduleIterator();
        while (myIterator.hasNext()) {
            final ThemisDSMModule myModule = myIterator.next();

            /* Process the module */
            reportOnModule(pBuilder, myModule);
        }
    }

    /**
     * build the table header.
     * @param pBuilder the builder
     * @param pModule the module to report on
     */
    private static void buildTableHeader(final StringBuilder pBuilder,
                                        final ThemisDSMModule pModule) {
        /* Start the row */
        addStartElement(pBuilder, Tag.TR, "dsm-row-header");

        /* Build the initial headers */
        addElement(pBuilder, Tag.TH, "Package");
        addElement(pBuilder, Tag.TH, "Key");

        /* Loop through the packages */
        int myKey = 0;
        final int myCount = pModule.getPackageCount();
        for (int i = 0; i < myCount; i++) {
            /* Add the key */
            addElement(pBuilder, Tag.TH, "dsm-col-count", getKeyForIndex(myKey++));
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
    private static StringBuilder buildTableRow(final StringBuilder pBuilder,
                                               final ThemisDSMModule pModule,
                                               final ThemisDSMPackage pPackage,
                                               final int pRowNo,
                                               final int pKey) {
        /* Create a stringBuilder */
        final StringBuilder myBuilder = new StringBuilder();

        /* Start the row */
        final String myClass = (pRowNo % 2 == 0) ? "dsm-row-even" : "dsm-row-odd";
        addStartElement(pBuilder, Tag.TR, myClass);

        /* Build the initial cells */
        addElement(pBuilder, Tag.TD, "dsm-cell-name-left", pPackage.getPackageName());
        addElement(pBuilder, Tag.TD, getKeyForIndex(pKey));

        /* Loop through the packages */
        final Iterator<ThemisDSMPackage> myIterator = pModule.packageIterator();
        while (myIterator.hasNext()) {
            final ThemisDSMPackage myPackage = myIterator.next();

            /* Add the key */
            final boolean isSelf = pPackage.equals(myPackage);
            final boolean isCircular = myPackage.isCircular();
            final int myCount = myPackage.getReferencesTo(pPackage).size();
            if (isSelf) {
                final String myCellClass = isCircular ? "dsm-cell-circular" : "dsm-cell-self";
                addElement(pBuilder, Tag.TD, myCellClass, "");
            } else if (myCount == 0) {
                addElement(pBuilder, Tag.TD, "");
            } else {
                addElement(pBuilder, Tag.TD, Integer.toString(myCount));
                reportOnPackageLinks(myBuilder, myPackage, pPackage);
            }
        }

        /* Complete the row */
        addEndElement(pBuilder, Tag.TR);
        return myBuilder;
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
     * report on a module.
     * @param pBuilder the builder
     * @param pSource the source package to report on
     * @param pTarget the target package to report on
     */
    private static void reportOnPackageLinks(final StringBuilder pBuilder,
                                             final ThemisDSMPackage pSource,
                                             final ThemisDSMPackage pTarget) {
        /* Build table */
        addStartElement(pBuilder, Tag.BR);
        addEndElement(pBuilder, Tag.BR);
        addStartElement(pBuilder, Tag.TABLE);

        /* Table header */
        addStartElement(pBuilder, Tag.TR, "dsm-row-header");
        addElement(pBuilder, Tag.TH, pSource.getPackageName());
        addElement(pBuilder, Tag.TH, pTarget.getPackageName());
        addEndElement(pBuilder, Tag.TR);

        /* Loop through the packages */
        int myRowNo = 0;
        for (ThemisDSMClass myClass : pSource.getReferencesTo(pTarget)) {
            /* Access list of references */
            final List<ThemisDSMClass> myClasses = myClass.getReferencesTo(pTarget);

            /* Table header */
            final String myRowClass = (myRowNo++ % 2 == 0) ? "dsm-row-even" : "dsm-row-odd";
            addStartElement(pBuilder, Tag.TR, myRowClass);
            addElement(pBuilder, Tag.TD, myClass.getClassName(), myClasses.size());
            boolean bFirst = true;
            for (ThemisDSMClass myRef : myClasses) {
                if (!bFirst) {
                    addStartElement(pBuilder, Tag.TR, myRowClass);
                }
                bFirst = false;
                addElement(pBuilder, Tag.TD, myRef.getClassName());
                addEndElement(pBuilder, Tag.TR);
            }
        }

        /* Finish the table */
        addEndElement(pBuilder, Tag.TABLE);
    }

    /**
     * Add text element.
     * @param pBuilder the builder
     * @param pElement the element
     * @param pText the text
     */
    private static void addElement(final StringBuilder pBuilder,
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
     * @param pClass the class of the element
     * @param pText the text
     */
    private static void addElement(final StringBuilder pBuilder,
                                   final Tag pElement,
                                   final String pClass,
                                   final String pText) {
        /* Add the text element */
        addStartElement(pBuilder, pElement, pClass);
        pBuilder.append(pText);
        addEndElement(pBuilder, pElement);
    }

    /**
     * Add spanning text element.
     * @param pBuilder the builder
     * @param pElement the element
     * @param pText the text
     * @param pSpan the span count
     */
    private static void addElement(final StringBuilder pBuilder,
                                   final Tag pElement,
                                   final String pText,
                                   final int pSpan) {
        /* Add the text element */
        addStartElement(pBuilder, pElement, pSpan);
        pBuilder.append(pText);
        addEndElement(pBuilder, pElement);
    }

    /**
     * Add start element.
     * @param pBuilder the builder
     * @param pElement the element
     * @param pClass the class of the element
     */
    private static void addStartElement(final StringBuilder pBuilder,
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
     * Add start element.
     * @param pBuilder the builder
     * @param pElement the element
     * @param pSpan the span count
     */
    private static void addStartElement(final StringBuilder pBuilder,
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
