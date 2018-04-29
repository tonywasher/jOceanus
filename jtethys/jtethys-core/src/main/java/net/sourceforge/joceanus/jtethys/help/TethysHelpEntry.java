/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jtethys.help;

import java.util.ArrayList;
import java.util.List;

/**
 * Help Entry class. This class provides structure to the help system, providing parent child
 * relationships to implement chapters and also providing maps between the name of a help page and
 * the file that holds the HTML for the page
 */
public class TethysHelpEntry {
    /**
     * Title of the entry.
     */
    private final String theTitle;

    /**
     * Name of the entry.
     */
    private final String theName;

    /**
     * FileName of the entry.
     */
    private final String theFileName;

    /**
     * Children of the entry.
     */
    private List<TethysHelpEntry> theChildren;

    /**
     * HTML.
     */
    private String theHtml;

    /**
     * Constructor for an HTML element.
     * @param pName the name by which this entry is referenced
     * @param pTitle the title for this page in the table of contents
     * @param pFileName the name of the file containing the HTML for this entry
     */
    public TethysHelpEntry(final String pName,
                           final String pTitle,
                           final String pFileName) {
        theName = pName;
        theTitle = pTitle;
        theFileName = pFileName;
    }

    /**
     * Constructor for a table of contents element.
     * @param pName the name by which this entry is referenced
     * @param pTitle the title for this page in the table of contents
     */
    public TethysHelpEntry(final String pName,
                           final String pTitle) {
        theName = pName;
        theTitle = pTitle;
        theFileName = null;
    }

    /**
     * Add child entry.
     * @param pChild the child
     * @return the HelpEntry
     */
    public TethysHelpEntry addChildEntry(final TethysHelpEntry pChild) {
        if (theChildren == null) {
            theChildren = new ArrayList<>();
        }
        theChildren.add(pChild);
        return pChild;
    }

    /**
     * Obtain the title.
     * @return the title
     */
    public String getTitle() {
        return theTitle;
    }

    /**
     * Obtain the name.
     * @return the name
     */
    public String getName() {
        return theName;
    }

    /**
     * Obtain the filename.
     * @return the filename
     */
    public String getFileName() {
        return theFileName;
    }

    /**
     * Obtain the children.
     * @return the children
     */
    public List<TethysHelpEntry> getChildren() {
        return theChildren;
    }

    /**
     * Obtain the HTML.
     * @return the HTML
     */
    public String getHtml() {
        return theHtml;
    }

    /**
     * Set the HTML.
     * @param pHtml the HTML
     */
    protected void setHtml(final String pHtml) {
        theHtml = pHtml;
    }

    @Override
    public String toString() {
        return getTitle();
    }
}
