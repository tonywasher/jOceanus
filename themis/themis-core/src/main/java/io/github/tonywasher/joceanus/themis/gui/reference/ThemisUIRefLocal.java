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
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverClass;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverFile;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverPackage;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Local table builder.
 */
public class ThemisUIRefLocal {
    /**
     * The builder.
     */
    private final ThemisUIDocBuilder theBuilder;

    /**
     * Does the package have children?
     */
    private boolean hasChildren;

    /**
     * The list of package files.
     */
    private List<ThemisSolverFile> theFiles;

    /**
     * Constructor.
     *
     * @param pBuilder the builder
     */
    ThemisUIRefLocal(final ThemisUIDocBuilder pBuilder) {
        theBuilder = pBuilder;
    }

    /**
     * Set details.
     *
     * @param pPackage the package
     */
    private void setDetails(final ThemisSolverPackage pPackage) {
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
    void formatLocal(final ThemisSolverPackage pPackage,
                     final Element pTable) {
        /* Set the details */
        setDetails(pPackage);

        /* Create table body */
        final Element myTblBody = theBuilder.createElement(ThemisUIHTMLTag.TBODY);
        pTable.appendChild(myTblBody);
        theBuilder.addClassToElement(myTblBody, ThemisUIBaseDocument.CLASSTBLZEBRA);

        /* Loop through the files */
        for (ThemisSolverFile myFile : theFiles) {
            /* Create a new row */
            final Element myRow = theBuilder.createElement(ThemisUIHTMLTag.TR);
            myTblBody.appendChild(myRow);

            /* Create a new cell for the package class name */
            createRowHeader(myRow, myFile);

            /* Loop through the siblings */
            for (ThemisSolverFile mySibling : theFiles) {
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
     * Create row header cell.
     *
     * @param pRow    the row
     * @param pTarget the target package
     */
    private void createRowHeader(final Element pRow,
                                 final ThemisSolverFile pTarget) {
        /* Create the cell */
        final Element myCell = theBuilder.createElement(ThemisUIHTMLTag.TD);
        theBuilder.addClassToElement(myCell, ThemisUIRefConstants.CLASSLINK);
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
                                final ThemisSolverFile pFile) {
        /* Determine whether we are circular */
        final boolean isCircular = pFile.isCircular();
        final String myClass = isCircular
                ? ThemisUIRefConstants.CLASSSELFERROR
                : ThemisUIRefConstants.CLASSSELFOK;

        /* Create the cell */
        final Element myCell = theBuilder.createElement(ThemisUIHTMLTag.TD);
        theBuilder.addClassToElement(myCell, myClass);
        theBuilder.addClassToElement(myCell, ThemisUIRefConstants.CLASSLINKSIBLING);
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
                                final ThemisSolverFile pSource,
                                final ThemisSolverFile pTarget) {
        /* Access links between from the sibling to the child */
        final List<ThemisSolverClass> myList = pSource.getLocalReferences();
        final Element myCell = theBuilder.createElement(ThemisUIHTMLTag.TD);
        pRow.appendChild(myCell);
        theBuilder.addClassToElement(myCell, ThemisUIRefConstants.CLASSLINKSIBLING);

        /* If we have a link between the classes */
        if (myList.contains(pTarget.getTopLevel())) {
            /* Show the link */
            theBuilder.addClassToElement(myCell, ThemisUIRefConstants.CLASSLINKPRESENT);
        }
    }
}
