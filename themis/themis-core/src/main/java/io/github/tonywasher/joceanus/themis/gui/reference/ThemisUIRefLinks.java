/*
 * Themis: Java Project Framework
 * Copyright 2026. Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.github.tonywasher.joceanus.themis.gui.reference;

import io.github.tonywasher.joceanus.themis.gui.base.ThemisUIBaseDocument;
import io.github.tonywasher.joceanus.themis.gui.base.ThemisUIDocBuilder;
import io.github.tonywasher.joceanus.themis.gui.base.ThemisUIHTMLTag;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisChar;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverClass;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverPackage;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverReference;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverReference.ThemisSolverRefClass;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverReference.ThemisSolverRefPackage;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Links table builder.
 */
public class ThemisUIRefLinks {
    /**
     * The builder.
     */
    private final ThemisUIDocBuilder theBuilder;

    /**
     * Constructor.
     *
     * @param pBuilder the builder
     */
    ThemisUIRefLinks(final ThemisUIDocBuilder pBuilder) {
        theBuilder = pBuilder;
    }

    /**
     * Create document for package links.
     *
     * @param pSource the source package
     * @param pTarget the target package
     * @param pTable  the table
     */
    void formatLinks(final ThemisSolverPackage pSource,
                     final ThemisSolverPackage pTarget,
                     final Element pTable) {
        /* Access link map */
        final ThemisSolverReference myMap = pSource.getReferenceMap();
        final ThemisSolverRefPackage myLinkMap = myMap.getReferences(pTarget);

        /* If we have links */
        if (myLinkMap != null) {
            /* Create table body */
            theBuilder.addClassToElement(pTable, ThemisUIBaseDocument.CLASSTBLSTD);
            final Element myBody = theBuilder.createElement(ThemisUIHTMLTag.TBODY);
            pTable.appendChild(myBody);
            theBuilder.addClassToElement(myBody, ThemisUIBaseDocument.CLASSTBLZEBRA);

            /* Create the header */
            formatHeader(pSource, pTarget, myBody);

            /* Loop through the references */
            for (ThemisSolverRefClass myLink : myLinkMap.getReferences()) {
                /* Create table row */
                final Element myRow = theBuilder.createElement(ThemisUIHTMLTag.TR);
                myBody.appendChild(myRow);

                /* Add row elements */
                formatFromClass(pSource, myLink, myRow);
                formatArrow(myRow);
                formatToClasses(pTarget, myLink, myRow);
            }
        }
    }

    /**
     * Format Header.
     *
     * @param pSource the source package
     * @param pTarget the target package
     * @param pTable  the table
     */
    private void formatHeader(final ThemisSolverPackage pSource,
                              final ThemisSolverPackage pTarget,
                              final Element pTable) {
        /* Create the table row */
        final Element myRow = theBuilder.createElement(ThemisUIHTMLTag.TR);
        pTable.appendChild(myRow);

        /* Create the source header */
        final Element mySource = theBuilder.createElement(ThemisUIHTMLTag.TH);
        mySource.setTextContent(pSource.getPackageName());
        myRow.appendChild(mySource);

        /* Create the null header */
        final Element myLink = theBuilder.createElement(ThemisUIHTMLTag.TH);
        myRow.appendChild(myLink);

        /* Create the source header */
        final Element myTarget = theBuilder.createElement(ThemisUIHTMLTag.TH);
        myTarget.setTextContent(pTarget.getPackageName());
        myRow.appendChild(myTarget);
    }

    /**
     * Format from link.
     *
     * @param pSource the source package
     * @param pLink   the link
     * @param pTable  the table row
     */
    private void formatFromClass(final ThemisSolverPackage pSource,
                                 final ThemisSolverRefClass pLink,
                                 final Element pTable) {
        /* Create table cell */
        final Element myCell = theBuilder.createElement(ThemisUIHTMLTag.TD);
        pTable.appendChild(myCell);

        /* Set the text */
        final String myName = pLink.getSubject().getFullName();
        final String myPrefix = pSource.getPackageName() + ThemisChar.PERIOD;
        myCell.setTextContent(myName.substring(myPrefix.length()));
    }

    /**
     * Format arrow.
     *
     * @param pTable the table row
     */
    private void formatArrow(final Element pTable) {
        /* Create table cell */
        final Element myCell = theBuilder.createElement(ThemisUIHTMLTag.TD);
        pTable.appendChild(myCell);

        /* Set the text */
        myCell.setTextContent("➤");
    }

    /**
     * Format to link.
     *
     * @param pTarget the Target package
     * @param pLink   the link
     * @param pTable  the table row
     */
    private void formatToClasses(final ThemisSolverPackage pTarget,
                                 final ThemisSolverRefClass pLink,
                                 final Element pTable) {
        /* Create table cell */
        final Element myCell = theBuilder.createElement(ThemisUIHTMLTag.TD);
        pTable.appendChild(myCell);

        /* Loop through the classes */
        boolean myXtra = false;
        final String myPrefix = pTarget.getPackageName() + ThemisChar.PERIOD;
        for (ThemisSolverClass myClass : pLink.getReferences()) {
            /* If this is an extra class */
            if (myXtra) {
                /* Add a text element */
                final Element myBreak = theBuilder.createElement(ThemisUIHTMLTag.BR);
                myCell.appendChild(myBreak);
            }

            /* Add a text element */
            final String myName = myClass.getFullName();
            final Node myText = theBuilder.createTextNode(myName.substring(myPrefix.length()));
            myCell.appendChild(myText);

            /* Note xtra classes */
            myXtra = true;
        }
    }
}
