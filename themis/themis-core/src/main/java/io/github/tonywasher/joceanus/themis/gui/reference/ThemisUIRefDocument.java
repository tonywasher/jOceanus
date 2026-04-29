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

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.gui.base.ThemisUIBaseDocument;
import io.github.tonywasher.joceanus.themis.gui.base.ThemisUIHTMLAttr;
import io.github.tonywasher.joceanus.themis.gui.base.ThemisUIHTMLTag;
import io.github.tonywasher.joceanus.themis.gui.base.ThemisUIResource;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverModule;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverPackage;
import org.w3c.dom.Element;

import java.util.Map;

/**
 * Document Builder for sibling reference.
 */
public class ThemisUIRefDocument
        extends ThemisUIBaseDocument {
    /**
     * The active module.
     */
    private Map<String, ThemisSolverPackage> thePackageMap;

    /**
     * The family builder.
     */
    private final ThemisUIRefFamily theFamily;

    /**
     * The local builder.
     */
    private final ThemisUIRefLocal theLocal;

    /**
     * The links builder.
     */
    private final ThemisUIRefLinks theLinks;

    /**
     * The default.
     */
    private ThemisSolverPackage theDefault;

    /**
     * Constructor.
     *
     * @throws OceanusException on error
     */
    ThemisUIRefDocument() throws OceanusException {
        /* Create builders */
        theFamily = new ThemisUIRefFamily(this);
        theLocal = new ThemisUIRefLocal(this);
        theLinks = new ThemisUIRefLinks(this);
    }

    /**
     * Set the module.
     *
     * @param pModule the module
     */
    void setModule(final ThemisSolverModule pModule) {
        /* Store the module */
        thePackageMap = pModule.getPackages();

        /* DetermineDefault */
        determineDefault(pModule);
    }

    /**
     * Is the link a new package link?
     *
     * @param pLink the link
     * @return true/false
     */
    boolean isNewPackageLink(final String pLink) {
        return pLink.startsWith(ThemisUIRefConstants.LINKPACKAGE)
                || pLink.startsWith(ThemisUIRefConstants.LINKLOCAL);
    }

    /**
     * Is the link a list link?
     *
     * @param pLink the link
     * @return true/false
     */
    boolean isListLink(final String pLink) {
        return pLink.startsWith(ThemisUIRefConstants.LINKLIST);
    }

    /**
     * Format the default package.
     *
     * @return the HTML
     */
    String formatDefaultPackage() {
        /* Handle no default package */
        if (theDefault == null) {
            throw new IllegalArgumentException("No default package");
        }

        /* Format the packageHTML */
        return formatPackage(theDefault, false);
    }

    /**
     * Format the new package.
     *
     * @param pLink the link to the new package
     * @return the HTML
     */
    String formatNewPackageLink(final String pLink) {
        /* If this is a package link */
        if (pLink.startsWith(ThemisUIRefConstants.LINKPACKAGE)) {
            /* Strip off the header and locate the package */
            final String myName = pLink.substring(ThemisUIRefConstants.LINKPACKAGE.length());
            final ThemisSolverPackage myPackage = thePackageMap.get(myName);

            /* Handle unknown package */
            if (myPackage == null) {
                throw new IllegalArgumentException("Unknown package");
            }

            /* Format the packageHTML */
            return formatPackage(myPackage, false);
        }

        /* If this is a local link */
        if (pLink.startsWith(ThemisUIRefConstants.LINKLOCAL)) {
            /* Strip off the header and locate the package */
            final String myName = pLink.substring(ThemisUIRefConstants.LINKLOCAL.length());
            final ThemisSolverPackage myPackage = thePackageMap.get(myName);

            /* Handle unknown package */
            if (myPackage == null) {
                throw new IllegalArgumentException("Unknown package");
            }

            /* Format the packageHTML */
            return formatPackage(myPackage, true);
        }

        /* Unrecognised link */
        throw new IllegalArgumentException("Invalid Link header");
    }

    /**
     * Format the linkList.
     *
     * @param pLink the link to the linkList
     * @return the HTML
     */
    String formatListLink(final String pLink) {
        /* If this is a list link */
        if (pLink.startsWith(ThemisUIRefConstants.LINKLIST)) {
            /* Strip off the header and locate the packages */
            final String myNames = pLink.substring(ThemisUIRefConstants.LINKLIST.length());
            final int myIndex = myNames.indexOf(ThemisUIRefConstants.SEPCHAR);
            final String mySourceName = myNames.substring(0, myIndex);
            final String myTargetName = myNames.substring(myIndex + 1);
            final ThemisSolverPackage mySource = thePackageMap.get(mySourceName);
            final ThemisSolverPackage myTarget = thePackageMap.get(myTargetName);

            /* Handle unknown packages */
            if (mySource == null || myTarget == null) {
                throw new IllegalArgumentException("Unknown package");
            }

            /* Format the listHTML */
            return formatLinkList(mySource, myTarget);
        }

        /* Unrecognised link */
        throw new IllegalArgumentException("Invalid Link header");
    }

    /**
     * Create document for package.
     *
     * @param pPackage the package
     * @param pLocal   chose local Classes
     * @return the formatted document
     */
    private String formatPackage(final ThemisSolverPackage pPackage,
                                 final boolean pLocal) {
        /* Create new document and obtain the body */
        final Element myBody = newDocument();

        /* Create the title */
        buildPackageTitle(myBody, pPackage);

        /* Create the links */
        buildPackageLinks(myBody, pPackage, pLocal);

        /* Create new table */
        final Element myTable = createElement(ThemisUIHTMLTag.TABLE);
        myBody.appendChild(myTable);

        /* If we have no children or should be local */
        if (pPackage.getChildren().isEmpty() || pLocal) {
            /* Format local classes */
            theLocal.formatLocal(pPackage, myTable);
        } else {
            /* Format family links */
            theFamily.formatFamily(pPackage, myTable);
        }

        /* Return the formatted HTML */
        return formatXML();
    }

    /**
     * Create document for linkList.
     *
     * @param pSource the source package
     * @param pTarget the target package
     * @return the formatted document
     */
    private String formatLinkList(final ThemisSolverPackage pSource,
                                  final ThemisSolverPackage pTarget) {
        /* Create new document and obtain the body */
        final Element myBody = newDocument();

        /* format the package Links */
        final Element myHeader = createElement(ThemisUIHTMLTag.H1);
        myBody.appendChild(myHeader);
        myHeader.setTextContent(ThemisUIResource.REF_LINKS.getValue());

        /* Create new table */
        final Element myTable = createElement(ThemisUIHTMLTag.TABLE);
        myBody.appendChild(myTable);

        /* Format link list */
        theLinks.formatLinks(pSource, pTarget, myTable);

        /* Return the formatted HTML */
        return formatXML();
    }

    /**
     * Build package title.
     *
     * @param pBody    the document body
     * @param pPackage the package
     */
    private void buildPackageTitle(final Element pBody,
                                   final ThemisSolverPackage pPackage) {
        /* Create the header */
        final Element myHeader = createElement(ThemisUIHTMLTag.H3);
        pBody.appendChild(myHeader);

        /* Set name of package */
        final String myName = pPackage.getPackageName().isEmpty()
                ? ThemisUIResource.REF_ROOT.getValue()
                : pPackage.getPackageName();
        myHeader.setTextContent(myName);
    }

    /**
     * Build package links.
     *
     * @param pBody    the document body
     * @param pPackage the package
     * @param pLocal   chose local Classes
     */
    private void buildPackageLinks(final Element pBody,
                                   final ThemisSolverPackage pPackage,
                                   final boolean pLocal) {
        /* Create the links */
        final Element myLinks = createElement(ThemisUIHTMLTag.TR);

        /* Create links */
        createHomeLink(myLinks, pPackage);
        createParentLink(myLinks, pPackage);
        if (pLocal) {
            createFamilyLink(myLinks, pPackage);
        }

        /* If we have links, then add to body */
        if (myLinks.hasChildNodes()) {
            /* Create the table */
            final Element myTable = createElement(ThemisUIHTMLTag.TABLE);
            pBody.appendChild(myTable);
            myTable.appendChild(myLinks);
            addClassToElement(myTable, ThemisUIRefConstants.CLASSNAVTABLE);
        }
    }

    /**
     * Create home link.
     *
     * @param pLinks   the document links
     * @param pPackage the package
     */
    private void createHomeLink(final Element pLinks,
                                final ThemisSolverPackage pPackage) {
        /* If we are not the default package */
        if (!pPackage.equals(theDefault)) {
            /* Create the cell */
            final Element myCell = createElement(ThemisUIHTMLTag.TD);
            pLinks.appendChild(myCell);

            /* Create the link */
            final Element myLink = createElement(ThemisUIHTMLTag.A);
            myCell.appendChild(myLink);

            /* Set link details */
            final String myLinkRef = ThemisUIRefConstants.LINKPACKAGE + theDefault.getPackageName();
            setAttribute(myLink, ThemisUIHTMLAttr.HREF, myLinkRef);

            /* Set name of package */
            myLink.setTextContent(ThemisUIResource.REF_HOME.getValue());
        }
    }

    /**
     * Create parent link.
     *
     * @param pLinks   the document links
     * @param pPackage the package
     */
    private void createParentLink(final Element pLinks,
                                  final ThemisSolverPackage pPackage) {
        /* If we are not the default package */
        final ThemisSolverPackage myParent = pPackage.getParent();
        if (!pPackage.equals(theDefault)
                && !theDefault.equals(myParent)) {
            /* Create the cell */
            final Element myCell = createElement(ThemisUIHTMLTag.TD);
            pLinks.appendChild(myCell);

            /* Create the link */
            final Element myLink = createElement(ThemisUIHTMLTag.A);
            myCell.appendChild(myLink);

            /* Set link details */
            final String myLinkRef = ThemisUIRefConstants.LINKPACKAGE + myParent.getPackageName();
            setAttribute(myLink, ThemisUIHTMLAttr.HREF, myLinkRef);

            /* Set name of package */
            myLink.setTextContent(ThemisUIResource.REF_PARENT.getValue());
        }
    }

    /**
     * Create parent link.
     *
     * @param pLinks   the document links
     * @param pPackage the package
     */
    private void createFamilyLink(final Element pLinks,
                                  final ThemisSolverPackage pPackage) {
        /* If we are not the default package */
        if (!pPackage.getChildren().isEmpty()) {
            /* Create the cell */
            final Element myCell = createElement(ThemisUIHTMLTag.TD);
            pLinks.appendChild(myCell);

            /* Create the link */
            final Element myLink = createElement(ThemisUIHTMLTag.A);
            myCell.appendChild(myLink);

            /* Set link details */
            final String myLinkRef = ThemisUIRefConstants.LINKPACKAGE + pPackage.getPackageName();
            setAttribute(myLink, ThemisUIHTMLAttr.HREF, myLinkRef);

            /* Set name of package */
            myLink.setTextContent(ThemisUIResource.REF_FAMILY.getValue());
        }
    }

    /**
     * Determine the default package.
     *
     * @param pModule the module
     */
    private void determineDefault(final ThemisSolverModule pModule) {
        /* Initialise the default */
        theDefault = null;

        /* If we have a non-null modules */
        if (pModule != null) {
            /* Loop through the available packages */
            for (ThemisSolverPackage myPackage : pModule.getPackages().values()) {
                /* Adjust the default */
                adjustDefault(myPackage);
            }
        }
    }

    /**
     * Adjust default.
     *
     * @param pPackage the package
     */
    private void adjustDefault(final ThemisSolverPackage pPackage) {
        /* Ignore placeHolder */
        if (skipPackage(pPackage)) {
            return;
        }

        /* Store first package as default */
        if (theDefault == null) {
            theDefault = pPackage;

            /* else we need to find a common parent */
        } else {
            theDefault = getCommonParent(theDefault, pPackage);
        }
    }

    /**
     * Obtain common parent.
     *
     * @param pFirst  the first package
     * @param pSecond the second package
     * @return the common parent
     */
    private ThemisSolverPackage getCommonParent(final ThemisSolverPackage pFirst,
                                                final ThemisSolverPackage pSecond) {
        if (pFirst.equals(pSecond)) {
            return pFirst;
        }
        final String myFirst = pFirst.getPackageName();
        final String mySecond = pSecond.getPackageName();
        return myFirst.length() >= mySecond.length()
                ? getCommonParent(pFirst.getParent(), pSecond)
                : getCommonParent(pFirst, pSecond.getParent());
    }

    /**
     * Should we skip the package?
     *
     * @param pPackage the package
     * @return true/false
     */
    private boolean skipPackage(final ThemisSolverPackage pPackage) {
        /* Skip placeholders */
        return pPackage.isPlaceHolder();
    }
}
