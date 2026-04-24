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
import io.github.tonywasher.joceanus.themis.parser.base.ThemisChar;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverModule;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverPackage;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

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
     * The prefix.
     */
    private String thePrefix;

    /**
     * Is the package childless?
     */
    private boolean isChildless;

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

        /* DeterminePrefix */
        determinePrefix(pModule);
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
        /* Access the default package */
        final String myName = thePrefix == null ? ThemisSolverModule.ROOT : thePrefix;
        final ThemisSolverPackage myPackage = thePackageMap.get(myName);

        /* Handle unknown package */
        if (myPackage == null) {
            throw new IllegalArgumentException("No default package");
        }

        /* Format the packageHTML */
        return formatPackage(myPackage, false);
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

        /* format the package Links */
        final Element myHeader = createElement(ThemisUIHTMLTag.H3);
        myBody.appendChild(myHeader);
        addPackageLink(myHeader, pPackage);

        /* Create new table */
        final Element myTable = createElement(ThemisUIHTMLTag.TABLE);
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
        final Element myHeader = createElement(ThemisUIHTMLTag.H3);
        myBody.appendChild(myHeader);

        /* Create new table */
        final Element myTable = createElement(ThemisUIHTMLTag.TABLE);
        myBody.appendChild(myTable);

        /* Format link list */
        theLinks.formatLinks(pSource, pTarget, myTable);

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
                                final ThemisSolverPackage pPackage) {
        /* Obtain the package name */
        final String myName = getPackageLinkName(pPackage);
        final boolean doParent = myName.equals(pPackage.getShortName());

        /* Handle parent package */
        if (doParent) {
            /* Obtain the parent package */
            final ThemisSolverPackage myParent = pPackage.getParent();
            addPackageLink(pElement, myParent);

            /* Add link */
            final Text myPeriod = createTextNode(ThemisChar.PERIOD);
            pElement.appendChild(myPeriod);
        }

        /* Create link element */
        final Element myLink = createElement(ThemisUIHTMLTag.A);
        pElement.appendChild(myLink);
        final String myLinkRef = ThemisUIRefConstants.LINKPACKAGE + pPackage.getPackageName();
        setAttribute(myLink, ThemisUIHTMLAttr.HREF, myLinkRef);
        myLink.setTextContent(myName);
    }

    /**
     * Determine package link name.
     *
     * @param pPackage the package
     * @return the package linkName
     */
    private String getPackageLinkName(final ThemisSolverPackage pPackage) {
        /* Handle root case */
        if (pPackage.getPackageName().isEmpty()) {
            return ThemisUIResource.PACKAGE_ROOT.getValue();
        }

        /* Special handling for prefix */
        return pPackage.getPackageName().equals(thePrefix)
                ? "<" + pPackage.getPackageName() + ">" : pPackage.getShortName();
    }

    /**
     * Determine the prefix.
     *
     * @param pModule the module
     */
    private void determinePrefix(final ThemisSolverModule pModule) {
        /* Initialise the prefix */
        thePrefix = null;

        /* If we have a non-null modules */
        if (pModule != null) {
            /* Loop through the available packages */
            for (ThemisSolverPackage myPackage : pModule.getPackages().values()) {
                /* Adjust the prefix */
                adjustPrefix(myPackage);
            }
        }
    }

    /**
     * Adjust prefix.
     *
     * @param pPackage the package
     */
    private void adjustPrefix(final ThemisSolverPackage pPackage) {
        /* Ignore placeHolder */
        if (skipPackage(pPackage)) {
            return;
        }

        /* If we do not have a prefix */
        final String myName = pPackage.getPackageName();
        if (thePrefix == null) {
            thePrefix = myName;

            /* else if we need to change the prefix */
        } else if (!myName.startsWith(thePrefix)) {

            /* Determine length */
            final int myLength = Math.min(thePrefix.length(), myName.length());

            /* Loop while prefixes are the same */
            for (int i = 0; i < myLength; i++) {
                /* If we have found a difference */
                if (thePrefix.charAt(i) != myName.charAt(i)) {
                    /* Strip the prefix down */
                    thePrefix = thePrefix.substring(0, i);
                    break;
                }
            }

            /* If the package is a prefix of the prefix */
            if (thePrefix.startsWith(myName)) {
                thePrefix = myName;
            }
        }
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
