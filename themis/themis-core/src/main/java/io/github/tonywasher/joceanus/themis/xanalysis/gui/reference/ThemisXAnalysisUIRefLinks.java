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

package io.github.tonywasher.joceanus.themis.xanalysis.gui.reference;

import io.github.tonywasher.joceanus.themis.xanalysis.gui.base.ThemisXAnalysisUIDocBuilder;
import io.github.tonywasher.joceanus.themis.xanalysis.gui.base.ThemisXAnalysisUIHTMLTag;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisChar;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverClass;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverPackage;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverReference;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverReference.ThemisXAnalysisSolverRefClass;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverReference.ThemisXAnalysisSolverRefPackage;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Links table builder.
 */
public class ThemisXAnalysisUIRefLinks {
    /**
     * The builder.
     */
    private final ThemisXAnalysisUIDocBuilder theBuilder;

    /**
     * Constructor.
     *
     * @param pBuilder the builder
     */
    ThemisXAnalysisUIRefLinks(final ThemisXAnalysisUIDocBuilder pBuilder) {
        theBuilder = pBuilder;
    }

    /**
     * Create document for package links.
     *
     * @param pSource the source package
     * @param pTarget the target package
     * @param pTable  the table
     */
    void formatLinks(final ThemisXAnalysisSolverPackage pSource,
                     final ThemisXAnalysisSolverPackage pTarget,
                     final Element pTable) {
        /* Access link map */
        final ThemisXAnalysisSolverReference myMap = pSource.getReferenceMap();
        final ThemisXAnalysisSolverRefPackage myLinkMap = myMap.getReferences(pTarget);

        /* If we have links */
        if (myLinkMap != null) {
            /* Creat the header */
            formatHeader(pSource, pTarget, pTable);

            /* Loop through the references */
            for (ThemisXAnalysisSolverRefClass myLink : myLinkMap.getReferences()) {
                /* Create table row */
                final Element myRow = theBuilder.createElement(ThemisXAnalysisUIHTMLTag.TR);
                pTable.appendChild(myRow);

                /* Add row elements */
                formatFromClass(pSource, myLink, myRow);
                formatToClasses(pTarget, myLink, myRow);
            }
        }
    }

    /**
     * Format Header.
     * @param pSource the source package
     * @param pTarget the target package
     * @param pTable the table
     */
    private void formatHeader(final ThemisXAnalysisSolverPackage pSource,
                              final ThemisXAnalysisSolverPackage pTarget,
                              final Element pTable) {
        /* Create the table row */
        final Element myRow = theBuilder.createElement(ThemisXAnalysisUIHTMLTag.TR);
        pTable.appendChild(myRow);

        /* Create the source cell */
        final Element mySource = theBuilder.createElement(ThemisXAnalysisUIHTMLTag.TH);
        mySource.setTextContent(pSource.getPackageName());
        myRow.appendChild(mySource);

        /* Create the source cell */
        final Element myTarget = theBuilder.createElement(ThemisXAnalysisUIHTMLTag.TH);
        myTarget.setTextContent(pTarget.getPackageName());
        myRow.appendChild(myTarget);
    }

    /**
     * Format from link.
     *
     * @param pSource the source package
     * @param pLink the link
     * @param pTable  the table row
     */
    private void formatFromClass(final ThemisXAnalysisSolverPackage pSource,
                                 final ThemisXAnalysisSolverRefClass pLink,
                                 final Element pTable) {
        /* Create table cell */
        final Element myCell = theBuilder.createElement(ThemisXAnalysisUIHTMLTag.TD);
        pTable.appendChild(myCell);

        /* Set the text */
        final String myName = pLink.getSubject().getFullName();
        final String myPrefix = pSource.getPackageName() + ThemisXAnalysisChar.PERIOD;
        myCell.setTextContent(myName.substring(myPrefix.length()));
    }

    /**
     * Format to link.
     *
     * @param pTarget the Target package
     * @param pLink the link
     * @param pTable  the table row
     */
    private void formatToClasses(final ThemisXAnalysisSolverPackage pTarget,
                                 final ThemisXAnalysisSolverRefClass pLink,
                                 final Element pTable) {
        /* Create table cell */
        final Element myCell = theBuilder.createElement(ThemisXAnalysisUIHTMLTag.TD);
        pTable.appendChild(myCell);

        /* Loop through the classes */
        boolean myXtra = false;
        final String myPrefix = pTarget.getPackageName() + ThemisXAnalysisChar.PERIOD;
        for (ThemisXAnalysisSolverClass myClass : pLink.getReferences()) {
            /* Add a text element */
            final String myName = myClass.getFullName();
            final Node myText = theBuilder.createTextNode(myName.substring(myPrefix.length()));
            myCell.appendChild(myText);

            /* If this is an extra class */
            if (myXtra) {
                /* Add a text element */
                final Node mySpace = theBuilder.createTextNode(ThemisXAnalysisChar.BLANK);
                myCell.appendChild(mySpace);
            }

            /* Note xtra classes */
            myXtra = true;
        }
    }
}
