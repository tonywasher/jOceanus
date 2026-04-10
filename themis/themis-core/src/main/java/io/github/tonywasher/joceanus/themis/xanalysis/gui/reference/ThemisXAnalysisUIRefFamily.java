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
import io.github.tonywasher.joceanus.themis.xanalysis.gui.base.ThemisXAnalysisUIHTMLAttr;
import io.github.tonywasher.joceanus.themis.xanalysis.gui.base.ThemisXAnalysisUIHTMLTag;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverPackage;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverReference;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverReference.ThemisXAnalysisSolverRefClass;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverReference.ThemisXAnalysisSolverRefPackage;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Family table builder.
 */
public class ThemisXAnalysisUIRefFamily {
    /**
     * The builder.
     */
    private final ThemisXAnalysisUIDocBuilder theBuilder;

    /**
     * Does the package have files?
     */
    private boolean hasFiles;

    /**
     * The list of child packages.
     */
    private List<ThemisXAnalysisSolverPackage> theChildren;

    /**
     * Constructor.
     *
     * @param pBuilder the builder
     */
    ThemisXAnalysisUIRefFamily(final ThemisXAnalysisUIDocBuilder pBuilder) {
        theBuilder = pBuilder;
    }

    /**
     * Set details.
     *
     * @param pPackage the package
     */
    private void setDetails(final ThemisXAnalysisSolverPackage pPackage) {
        /* Access children and determine whether we have files */
        theChildren = new ArrayList<>(pPackage.getChildren());
        Collections.sort(theChildren);
        hasFiles = !pPackage.getFiles().isEmpty();
    }

    /**
     * Create document for package family.
     *
     * @param pPackage the package
     * @param pTable   the table
     */
    void formatFamily(final ThemisXAnalysisSolverPackage pPackage,
                      final Element pTable) {
        /* Set the details */
        setDetails(pPackage);

        /* Create a new hdrRow */
        final Element myHdrRow = theBuilder.createElement(ThemisXAnalysisUIHTMLTag.TR);
        pTable.appendChild(myHdrRow);

        /* Create the Local Column header */
        Element myLocalRow = null;
        if (hasFiles) {
            createColLocalHeader(myHdrRow);
            myLocalRow = theBuilder.createElement(ThemisXAnalysisUIHTMLTag.TR);
            pTable.appendChild(myLocalRow);
            createRowLocalHeader(myLocalRow, pPackage);
        }

        /* Loop through the children */
        for (ThemisXAnalysisSolverPackage myChild : theChildren) {
            /* Create a new row */
            final Element myRow = theBuilder.createElement(ThemisXAnalysisUIHTMLTag.TR);
            pTable.appendChild(myRow);

            /* Create a new header for the package short name */
            createColHeader(myHdrRow, myChild);

            /* Create a new cell for the package short name */
            createRowHeader(myRow, myChild);

            /* Create the link row for local to child and vice-versa */
            if (hasFiles) {
                createSelfLocalCell(myLocalRow, pPackage);
                createLinkCell(myLocalRow, myChild, pPackage);
                createLinkCell(myRow, pPackage, myChild);
            }

            /* Loop through the siblings */
            for (ThemisXAnalysisSolverPackage mySibling : theChildren) {
                /* If this is a self-reference */
                if (myChild.equals(mySibling)) {
                    /* Create the self cell */
                    createSelfCell(myRow, myChild);

                    /* else create the standard cell */
                } else {
                    /* Create the link row */
                    createLinkCell(myRow, myChild, mySibling);
                }
            }
        }
    }

    /**
     * Create column header cell.
     *
     * @param pRow    the row
     * @param pSource the source package
     */
    private void createColHeader(final Element pRow,
                                 final ThemisXAnalysisSolverPackage pSource) {
        /* Create the cell */
        final Element myCell = theBuilder.createElement(ThemisXAnalysisUIHTMLTag.TH);
        theBuilder.setAttribute(myCell, ThemisXAnalysisUIHTMLAttr.CLASS, ThemisXAnalysisUIRefConstants.CLASSHDR);
        pRow.appendChild(myCell);

        /* Populate the cell */
        final String myName = pSource.getShortName();
        myCell.setTextContent(myName);
    }

    /**
     * Create column local header cell.
     *
     * @param pRow the row
     */
    private void createColLocalHeader(final Element pRow) {
        /* Create the cell */
        final Element myCell = theBuilder.createElement(ThemisXAnalysisUIHTMLTag.TH);
        theBuilder.setAttribute(myCell, ThemisXAnalysisUIHTMLAttr.CLASS, ThemisXAnalysisUIRefConstants.CLASSHDR);
        pRow.appendChild(myCell);

        /* Populate the cell */
        myCell.setTextContent(ThemisXAnalysisUIRefConstants.LOCALPACKAGE);
    }

    /**
     * Create row header cell.
     *
     * @param pRow    the row
     * @param pTarget the target package
     */
    private void createRowHeader(final Element pRow,
                                 final ThemisXAnalysisSolverPackage pTarget) {
        /* Create the cell */
        final Element myCell = theBuilder.createElement(ThemisXAnalysisUIHTMLTag.TD);
        theBuilder.setAttribute(myCell, ThemisXAnalysisUIHTMLAttr.CLASS, ThemisXAnalysisUIRefConstants.CLASSLINK);
        pRow.appendChild(myCell);

        /* Create the link */
        final Element myLink = theBuilder.createElement(ThemisXAnalysisUIHTMLTag.A);
        myCell.appendChild(myLink);

        /* Determine the linkReference */
        final String myLinkRef = ThemisXAnalysisUIRefConstants.LINKPACKAGE + pTarget.getPackageName();
        theBuilder.setAttribute(myLink, ThemisXAnalysisUIHTMLAttr.HREF, myLinkRef);

        /* Populate the cell */
        myLink.setTextContent(pTarget.getShortName());
    }

    /**
     * Create local row header cell.
     *
     * @param pRow    the row
     * @param pTarget the target package
     */
    private void createRowLocalHeader(final Element pRow,
                                      final ThemisXAnalysisSolverPackage pTarget) {
        /* Create the cell */
        final Element myCell = theBuilder.createElement(ThemisXAnalysisUIHTMLTag.TD);
        theBuilder.setAttribute(myCell, ThemisXAnalysisUIHTMLAttr.CLASS, ThemisXAnalysisUIRefConstants.CLASSLINK);
        pRow.appendChild(myCell);

        /* Create the link */
        final Element myLink = theBuilder.createElement(ThemisXAnalysisUIHTMLTag.A);
        myCell.appendChild(myLink);

        /* Determine the linkReference */
        final String myLinkRef = ThemisXAnalysisUIRefConstants.LINKLOCAL + pTarget.getPackageName();
        theBuilder.setAttribute(myLink, ThemisXAnalysisUIHTMLAttr.HREF, myLinkRef);

        /* Populate the cell */
        myLink.setTextContent(ThemisXAnalysisUIRefConstants.LOCALPACKAGE);
    }

    /**
     * Create self-link cell.
     *
     * @param pRow     the row
     * @param pPackage the package
     */
    private void createSelfCell(final Element pRow,
                                final ThemisXAnalysisSolverPackage pPackage) {
        /* Determine whether we are circular */
        final boolean isCircular = pPackage.isCircular();
        final String myClass = isCircular
                ? ThemisXAnalysisUIRefConstants.CLASSSELFERROR
                : ThemisXAnalysisUIRefConstants.CLASSSELFOK;

        /* Create the cell */
        final Element myCell = theBuilder.createElement(ThemisXAnalysisUIHTMLTag.TD);
        theBuilder.setAttribute(myCell, ThemisXAnalysisUIHTMLAttr.CLASS, myClass);
        pRow.appendChild(myCell);
    }

    /**
     * Create self-link cell.
     *
     * @param pRow     the row
     * @param pPackage the package
     */
    private void createSelfLocalCell(final Element pRow,
                                     final ThemisXAnalysisSolverPackage pPackage) {
        /* Determine whether we are incestuous */
        final boolean isIncestuous = pPackage.isIncestuous();
        final String myClass = isIncestuous
                ? ThemisXAnalysisUIRefConstants.CLASSSELFERROR
                : ThemisXAnalysisUIRefConstants.CLASSSELFOK;

        /* Create the cell */
        final Element myCell = theBuilder.createElement(ThemisXAnalysisUIHTMLTag.TD);
        theBuilder.setAttribute(myCell, ThemisXAnalysisUIHTMLAttr.CLASS, myClass);
        pRow.appendChild(myCell);
    }

    /**
     * Create linking cell.
     *
     * @param pRow    the row
     * @param pSource the source package
     * @param pTarget the target package
     */
    private void createLinkCell(final Element pRow,
                                final ThemisXAnalysisSolverPackage pSource,
                                final ThemisXAnalysisSolverPackage pTarget) {
        /* Access links between from the sibling to the child */
        final ThemisXAnalysisSolverReference myMap = pSource.getReferenceMap();
        final ThemisXAnalysisSolverRefPackage myLinkMap = myMap.getReferences(pTarget);
        final Element myCell = theBuilder.createElement(ThemisXAnalysisUIHTMLTag.TD);
        theBuilder.setAttribute(myCell, ThemisXAnalysisUIHTMLAttr.CLASS, ThemisXAnalysisUIRefConstants.CLASSLINKLIST);
        pRow.appendChild(myCell);

        /* If we have links */
        if (myLinkMap != null) {
            /* Create the link */
            final Element myLink = theBuilder.createElement(ThemisXAnalysisUIHTMLTag.A);
            myCell.appendChild(myLink);

            /* Determine the linkReference */
            final String myLinkRef = ThemisXAnalysisUIRefConstants.LINKLIST + pSource.getPackageName()
                    + ThemisXAnalysisUIRefConstants.SEPCHAR + pTarget.getPackageName();
            theBuilder.setAttribute(myLink, ThemisXAnalysisUIHTMLAttr.HREF, myLinkRef);

            /* Populate the cell */
            final List<ThemisXAnalysisSolverRefClass> myReferences = myLinkMap.getReferences();
            final String myText = Integer.toString(myReferences.size());
            myLink.setTextContent(myText);
        }
    }
}
