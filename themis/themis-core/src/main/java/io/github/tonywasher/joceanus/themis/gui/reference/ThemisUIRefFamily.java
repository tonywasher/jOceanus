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

import io.github.tonywasher.joceanus.themis.gui.base.ThemisUIDocBuilder;
import io.github.tonywasher.joceanus.themis.gui.base.ThemisUIHTMLAttr;
import io.github.tonywasher.joceanus.themis.gui.base.ThemisUIHTMLTag;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverPackage;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverReference;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverReference.ThemisSolverRefClass;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverReference.ThemisSolverRefPackage;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Family table builder.
 */
public class ThemisUIRefFamily {
    /**
     * The builder.
     */
    private final ThemisUIDocBuilder theBuilder;

    /**
     * Does the package have files?
     */
    private boolean hasFiles;

    /**
     * The list of child packages.
     */
    private List<ThemisSolverPackage> theChildren;

    /**
     * Constructor.
     *
     * @param pBuilder the builder
     */
    ThemisUIRefFamily(final ThemisUIDocBuilder pBuilder) {
        theBuilder = pBuilder;
    }

    /**
     * Set details.
     *
     * @param pPackage the package
     */
    private void setDetails(final ThemisSolverPackage pPackage) {
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
    void formatFamily(final ThemisSolverPackage pPackage,
                      final Element pTable) {
        /* Set the details */
        setDetails(pPackage);

        /* Create a new hdrRow */
        final Element myHdrRow = theBuilder.createElement(ThemisUIHTMLTag.TR);
        pTable.appendChild(myHdrRow);

        /* Create a blank cell for top left */
        final Element myHdrBlank = theBuilder.createElement(ThemisUIHTMLTag.TH);
        myHdrRow.appendChild(myHdrBlank);

        /* Create the Local Column header */
        Element myLocalRow = null;
        if (hasFiles) {
            createColLocalHeader(myHdrRow);
            myLocalRow = theBuilder.createElement(ThemisUIHTMLTag.TR);
            pTable.appendChild(myLocalRow);
            createRowLocalHeader(myLocalRow, pPackage);
        }

        /* Loop through the children */
        for (ThemisSolverPackage myChild : theChildren) {
            /* Create a new row */
            final Element myRow = theBuilder.createElement(ThemisUIHTMLTag.TR);
            pTable.appendChild(myRow);

            /* Create a new header for the package short name */
            createColHeader(myHdrRow, myChild);

            /* Create a new cell for the package short name */
            createRowHeader(myRow, myChild);

            /* Create the link row for local to child and vice-versa */
            if (myLocalRow != null) {
                createSelfLocalCell(myLocalRow, pPackage);
                createLinkCell(myLocalRow, myChild, pPackage);
                createLinkCell(myRow, pPackage, myChild);
            }

            /* Loop through the siblings */
            for (ThemisSolverPackage mySibling : theChildren) {
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
                                 final ThemisSolverPackage pSource) {
        /* Create the cell */
        final Element myCell = theBuilder.createElement(ThemisUIHTMLTag.TH);
        theBuilder.setAttribute(myCell, ThemisUIHTMLAttr.CLASS, ThemisUIRefConstants.CLASSHDR);
        pRow.appendChild(myCell);

        /* Create the div and span */
        final Element myDiv = theBuilder.createElement(ThemisUIHTMLTag.DIV);
        myCell.appendChild(myDiv);
        final Element mySpan = theBuilder.createElement(ThemisUIHTMLTag.SPAN);
        myDiv.appendChild(mySpan);

        /* Populate the cell */
        final String myName = pSource.getShortName();
        mySpan.setTextContent(myName);
    }

    /**
     * Create column local header cell.
     *
     * @param pRow the row
     */
    private void createColLocalHeader(final Element pRow) {
        /* Create the cell */
        final Element myCell = theBuilder.createElement(ThemisUIHTMLTag.TH);
        theBuilder.setAttribute(myCell, ThemisUIHTMLAttr.CLASS, ThemisUIRefConstants.CLASSHDR);
        pRow.appendChild(myCell);

        /* Populate the cell */
        myCell.setTextContent(ThemisUIRefConstants.LOCALPACKAGE);
    }

    /**
     * Create row header cell.
     *
     * @param pRow    the row
     * @param pTarget the target package
     */
    private void createRowHeader(final Element pRow,
                                 final ThemisSolverPackage pTarget) {
        /* Create the cell */
        final Element myCell = theBuilder.createElement(ThemisUIHTMLTag.TD);
        theBuilder.setAttribute(myCell, ThemisUIHTMLAttr.CLASS, ThemisUIRefConstants.CLASSLINK);
        pRow.appendChild(myCell);

        /* Create the link */
        final Element myLink = theBuilder.createElement(ThemisUIHTMLTag.A);
        myCell.appendChild(myLink);

        /* Determine the linkReference */
        final String myLinkRef = ThemisUIRefConstants.LINKPACKAGE + pTarget.getPackageName();
        theBuilder.setAttribute(myLink, ThemisUIHTMLAttr.HREF, myLinkRef);

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
                                      final ThemisSolverPackage pTarget) {
        /* Create the cell */
        final Element myCell = theBuilder.createElement(ThemisUIHTMLTag.TD);
        theBuilder.setAttribute(myCell, ThemisUIHTMLAttr.CLASS, ThemisUIRefConstants.CLASSLINK);
        pRow.appendChild(myCell);

        /* Create the link */
        final Element myLink = theBuilder.createElement(ThemisUIHTMLTag.A);
        myCell.appendChild(myLink);

        /* Determine the linkReference */
        final String myLinkRef = ThemisUIRefConstants.LINKLOCAL + pTarget.getPackageName();
        theBuilder.setAttribute(myLink, ThemisUIHTMLAttr.HREF, myLinkRef);

        /* Populate the cell */
        myLink.setTextContent(ThemisUIRefConstants.LOCALPACKAGE);
    }

    /**
     * Create self-link cell.
     *
     * @param pRow     the row
     * @param pPackage the package
     */
    private void createSelfCell(final Element pRow,
                                final ThemisSolverPackage pPackage) {
        /* Determine whether we are circular */
        final boolean isCircular = pPackage.isCircular();
        final String myClass = isCircular
                ? ThemisUIRefConstants.CLASSSELFERROR
                : ThemisUIRefConstants.CLASSSELFOK;

        /* Create the cell */
        final Element myCell = theBuilder.createElement(ThemisUIHTMLTag.TD);
        theBuilder.setAttribute(myCell, ThemisUIHTMLAttr.CLASS, myClass);
        pRow.appendChild(myCell);
    }

    /**
     * Create self-link cell.
     *
     * @param pRow     the row
     * @param pPackage the package
     */
    private void createSelfLocalCell(final Element pRow,
                                     final ThemisSolverPackage pPackage) {
        /* Determine whether we are incestuous */
        final boolean isIncestuous = pPackage.isIncestuous();
        final String myClass = isIncestuous
                ? ThemisUIRefConstants.CLASSSELFERROR
                : ThemisUIRefConstants.CLASSSELFOK;

        /* Create the cell */
        final Element myCell = theBuilder.createElement(ThemisUIHTMLTag.TD);
        theBuilder.setAttribute(myCell, ThemisUIHTMLAttr.CLASS, myClass);
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
                                final ThemisSolverPackage pSource,
                                final ThemisSolverPackage pTarget) {
        /* Access links between from the sibling to the child */
        final ThemisSolverReference myMap = pSource.getReferenceMap();
        final ThemisSolverRefPackage myLinkMap = myMap.getReferences(pTarget);
        final Element myCell = theBuilder.createElement(ThemisUIHTMLTag.TD);
        theBuilder.setAttribute(myCell, ThemisUIHTMLAttr.CLASS, ThemisUIRefConstants.CLASSLINKLIST);
        pRow.appendChild(myCell);

        /* If we have links */
        if (myLinkMap != null) {
            /* Create the link */
            final Element myLink = theBuilder.createElement(ThemisUIHTMLTag.A);
            myCell.appendChild(myLink);

            /* Determine the linkReference */
            final String myLinkRef = ThemisUIRefConstants.LINKLIST + pSource.getPackageName()
                    + ThemisUIRefConstants.SEPCHAR + pTarget.getPackageName();
            theBuilder.setAttribute(myLink, ThemisUIHTMLAttr.HREF, myLinkRef);

            /* Populate the cell */
            final List<ThemisSolverRefClass> myReferences = myLinkMap.getReferences();
            final String myText = Integer.toString(myReferences.size());
            myLink.setTextContent(myText);
        }
    }
}
