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
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverClass;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverFile;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverPackage;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Local table builder.
 */
public class ThemisXAnalysisUIRefLocal {
    /**
     * The builder.
     */
    private final ThemisXAnalysisUIDocBuilder theBuilder;

    /**
     * Does the package have children?
     */
    private boolean hasChildren;

    /**
     * The list of package files.
     */
    private List<ThemisXAnalysisSolverFile> theFiles;

    /**
     * Constructor.
     *
     * @param pBuilder the builder
     */
    ThemisXAnalysisUIRefLocal(final ThemisXAnalysisUIDocBuilder pBuilder) {
        theBuilder = pBuilder;
    }

    /**
     * Set details.
     *
     * @param pPackage the package
     */
    private void setDetails(final ThemisXAnalysisSolverPackage pPackage) {
        /* Access files and determine whether we have children */
        theFiles = new ArrayList<>(pPackage.getFiles());
        Collections.sort(theFiles);
        hasChildren = !pPackage.getChildren().isEmpty();
    }

    /**
     * Create document for local package.
     *
     * @param pPackage the package
     * @param pTable   the table
     */
    void formatLocal(final ThemisXAnalysisSolverPackage pPackage,
                     final Element pTable) {
        /* Set the details */
        setDetails(pPackage);

        /* Create a new hdrRow */
        final Element myHdrRow = theBuilder.createElement(ThemisXAnalysisUIHTMLTag.TR);
        pTable.appendChild(myHdrRow);

        /* Loop through the files */
        for (ThemisXAnalysisSolverFile myFile : theFiles) {
            /* Create a new row */
            final Element myRow = theBuilder.createElement(ThemisXAnalysisUIHTMLTag.TR);
            pTable.appendChild(myRow);

            /* Create a new header for the file class name */
            createColHeader(myHdrRow, myFile);

            /* Create a new cell for the package class name */
            createRowHeader(myRow, myFile);

            /* Loop through the siblings */
            for (ThemisXAnalysisSolverFile mySibling : theFiles) {
                /* If this is a self-reference */
                if (myFile.equals(mySibling)) {
                    /* Create the self cell */
                    createSelfCell(myRow, myFile);

                    /* else create the standard cell */
                } else {
                    /* Create the link row */
                    createLinkCell(myRow, myFile, mySibling);
                }
            }
        }
    }

    /**
     * Create column header cell.
     *
     * @param pRow    the row
     * @param pSource the source file
     */
    private void createColHeader(final Element pRow,
                                 final ThemisXAnalysisSolverFile pSource) {
        /* Create the cell */
        final Element myCell = theBuilder.createElement(ThemisXAnalysisUIHTMLTag.TH);
        theBuilder.setAttribute(myCell, ThemisXAnalysisUIHTMLAttr.CLASS, ThemisXAnalysisUIRefConstants.CLASSHDR);
        pRow.appendChild(myCell);

        /* Populate the cell */
        final String myName = pSource.getTopLevel().getName();
        myCell.setTextContent(myName);
    }

    /**
     * Create row header cell.
     *
     * @param pRow    the row
     * @param pTarget the target package
     */
    private void createRowHeader(final Element pRow,
                                 final ThemisXAnalysisSolverFile pTarget) {
        /* Create the cell */
        final Element myCell = theBuilder.createElement(ThemisXAnalysisUIHTMLTag.TD);
        theBuilder.setAttribute(myCell, ThemisXAnalysisUIHTMLAttr.CLASS, ThemisXAnalysisUIRefConstants.CLASSLINK);
        pRow.appendChild(myCell);

        /* Populate the cell */
        final String myName = pTarget.getTopLevel().getName();
        myCell.setTextContent(myName);
    }

    /**
     * Create self-link cell.
     *
     * @param pRow  the row
     * @param pFile the file
     */
    private void createSelfCell(final Element pRow,
                                final ThemisXAnalysisSolverFile pFile) {
        /* Determine whether we are circular */
        final boolean isCircular = pFile.isCircular();
        final String myClass = isCircular
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
                                final ThemisXAnalysisSolverFile pSource,
                                final ThemisXAnalysisSolverFile pTarget) {
        /* Access links between from the sibling to the child */
        final List<ThemisXAnalysisSolverClass> myList = pSource.getLocalReferences();
        final Element myCell = theBuilder.createElement(ThemisXAnalysisUIHTMLTag.TD);
        pRow.appendChild(myCell);

        /* If we have a link between the classes */
        if (myList.contains(pTarget.getTopLevel())) {
            /* Show the link */
            theBuilder.setAttribute(myCell, ThemisXAnalysisUIHTMLAttr.CLASS, ThemisXAnalysisUIRefConstants.CLASSLINKPRESENT);
        }
    }
}
