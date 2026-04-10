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

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.xanalysis.gui.base.ThemisXAnalysisUIBaseDocument;
import io.github.tonywasher.joceanus.themis.xanalysis.gui.base.ThemisXAnalysisUIHTMLAttr;
import io.github.tonywasher.joceanus.themis.xanalysis.gui.base.ThemisXAnalysisUIHTMLTag;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisChar;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverModule;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverPackage;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import java.util.Map;

/**
 * Document Builder for sibling reference.
 */
public class ThemisXAnalysisUIRefDocument
        extends ThemisXAnalysisUIBaseDocument {
    /**
     * The active module.
     */
    private Map<String, ThemisXAnalysisSolverPackage> thePackageMap;

    /**
     * The family builder.
     */
    private final ThemisXAnalysisUIRefFamily theFamily;

    /**
     * The local builder.
     */
    private final ThemisXAnalysisUIRefLocal theLocal;

    /**
     * Is the package childless?
     */
    private boolean isChildless;

    /**
     * Constructor.
     *
     * @throws OceanusException on error
     */
    ThemisXAnalysisUIRefDocument() throws OceanusException {
        /* Initialise underlying */
        //super();

        /* Creat builders */
        theFamily = new ThemisXAnalysisUIRefFamily(this);
        theLocal = new ThemisXAnalysisUIRefLocal(this);
    }

    /**
     * Set the module.
     *
     * @param pModule the module
     */
    void setModule(final ThemisXAnalysisSolverModule pModule) {
        /* Store the module */
        thePackageMap = pModule.getPackages();

        /* TODO select default package */
    }

    /**
     * Is the link a new package link?
     *
     * @param pLink the link
     * @return true/false
     */
    boolean isNewPackageLink(final String pLink) {
        return pLink.startsWith(ThemisXAnalysisUIRefConstants.LINKPACKAGE)
                || pLink.startsWith(ThemisXAnalysisUIRefConstants.LINKLOCAL);
    }

    /**
     * Format the new package.
     *
     * @param pLink the link to the new package
     * @return the HTML
     */
    String formatNewPackageLink(final String pLink) {
        /* If this is a package link */
        if (pLink.startsWith(ThemisXAnalysisUIRefConstants.LINKPACKAGE)) {
            /* Strip of the header and locate the package */
            final String myName = pLink.substring(ThemisXAnalysisUIRefConstants.LINKPACKAGE.length());
            final ThemisXAnalysisSolverPackage myPackage = thePackageMap.get(myName);

            /* Handle unknown package */
            if (myPackage == null) {
                throw new IllegalArgumentException("Unknown package");
            }

            /* Format the packageHTML */
            return formatPackage(myPackage, false);
        }

        /* If this is a local link */
        if (pLink.startsWith(ThemisXAnalysisUIRefConstants.LINKLOCAL)) {
            /* Strip of the header and locate the package */
            final String myName = pLink.substring(ThemisXAnalysisUIRefConstants.LINKLOCAL.length());
            final ThemisXAnalysisSolverPackage myPackage = thePackageMap.get(myName);

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
     * Create document for package.
     *
     * @param pPackage the package
     * @param pLocal   chose local Classes
     * @return the formatted document
     */
    private String formatPackage(final ThemisXAnalysisSolverPackage pPackage,
                                 final boolean pLocal) {
        /* Create new document and obtain the body */
        final Element myBody = newDocument();

        /* format the package Links */
        final Element myHeader = createElement(ThemisXAnalysisUIHTMLTag.H3);
        myBody.appendChild(myHeader);
        addPackageLink(myHeader, pPackage);

        /* Create new table */
        final Element myTable = createElement(ThemisXAnalysisUIHTMLTag.TABLE);
        myBody.appendChild(myTable);

        /* If we have no children or should be local */
        if (isChildless || pLocal) {
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
     * Add package link.
     *
     * @param pElement the element
     * @param pPackage the package
     */
    private void addPackageLink(final Element pElement,
                                final ThemisXAnalysisSolverPackage pPackage) {
        /* Determine whether parent is root */
        final boolean isRoot = pPackage.getPackageName().isEmpty();
        final String myName = isRoot ? "<Root>" : pPackage.getShortName();

        /* If we are not root */
        if (!isRoot) {
            /* Obtain the parent package */
            final ThemisXAnalysisSolverPackage myParent = pPackage.getParent();
            addPackageLink(pElement, myParent);

            /* Add link */
            final Text myPeriod = createTextNode(ThemisXAnalysisChar.PERIOD);
            pElement.appendChild(myPeriod);
        }

        /* Create link element */
        final Element myLink = createElement(ThemisXAnalysisUIHTMLTag.A);
        pElement.appendChild(myLink);
        final String myLinkRef = ThemisXAnalysisUIRefConstants.LINKPACKAGE + pPackage.getPackageName();
        setAttribute(myLink, ThemisXAnalysisUIHTMLAttr.HREF, myLinkRef);
        myLink.setTextContent(myName);
    }
}
