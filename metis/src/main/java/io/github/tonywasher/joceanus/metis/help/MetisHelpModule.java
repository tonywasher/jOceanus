/*
 * Metis: Java Data Framework
 * Copyright 2012-2026. Tony Washer
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
package io.github.tonywasher.joceanus.metis.help;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.resource.OceanusResourceId;
import io.github.tonywasher.joceanus.oceanus.resource.OceanusResourceLoader;
import net.sourceforge.joceanus.tethys.api.control.TethysUIHTMLManager.TethysUIStyleSheetId;

import java.util.ArrayList;
import java.util.List;

/**
 * The help module that is implemented by each Help System.
 */
public abstract class MetisHelpModule {
    /**
     * HelpId.
     */
    public interface MetisHelpId extends OceanusResourceId {
    }

    /**
     * The Hash prime.
     */
    protected static final int HASH_PRIME = 19;

    /**
     * Document name for Help Contents.
     */
    protected static final String DOC_NAME = "HelpContents";

    /**
     * Attribute name for Initial page.
     */
    protected static final String ATTR_INITIAL = "initial";

    /**
     * The Help Entries.
     */
    private final List<MetisHelpEntry> theEntries;

    /**
     * The title of the Help System.
     */
    private final String theTitle;

    /**
     * The CSS of the help system.
     */
    private TethysUIStyleSheetId theCSS;

    /**
     * The initial entry of the help system.
     */
    private String theInitial;

    /**
     * Constructor.
     *
     * @param pTitle the title
     */
    protected MetisHelpModule(final String pTitle) {
        /* Store parameters */
        theTitle = pTitle;

        /* Create entry list */
        theEntries = new ArrayList<>();
    }

    /**
     * Set the initial name.
     *
     * @param pInitial the initial name
     */
    public void setInitialName(final String pInitial) {
        theInitial = pInitial;
    }

    /**
     * Obtain the initial name.
     *
     * @return the initial name
     */
    protected String getInitialName() {
        return theInitial;
    }

    /**
     * Obtain the title.
     *
     * @return the title
     */
    protected String getTitle() {
        return theTitle;
    }

    /**
     * Obtain the CSS.
     *
     * @return the CSS
     */
    protected TethysUIStyleSheetId getCSS() {
        return theCSS;
    }

    /**
     * Obtain the help entries.
     *
     * @return the help entries
     */
    public List<MetisHelpEntry> getHelpEntries() {
        return theEntries;
    }

    /**
     * Add root entry.
     *
     * @param pEntry the entry
     * @return the HelpEntry
     */
    public MetisHelpEntry addRootEntry(final MetisHelpEntry pEntry) {
        theEntries.add(pEntry);
        return pEntry;
    }

    /**
     * Define Standard Help entry.
     *
     * @param <K>     the type of the key
     * @param pName   the name
     * @param pHelpId the helpId
     * @return the HelpEntry
     */
    public static <K extends Enum<K> & MetisHelpId> MetisHelpEntry defineHelpEntry(final String pName,
                                                                                   final K pHelpId) {
        return defineTitledHelpEntry(pName, pName, pHelpId);
    }

    /**
     * Define Titled Help entry.
     *
     * @param <K>     the type of the key
     * @param pName   the name
     * @param pTitle  the title
     * @param pHelpId the helpId
     * @return the HelpEntry
     */
    public static <K extends Enum<K> & MetisHelpId> MetisHelpEntry defineTitledHelpEntry(final String pName,
                                                                                         final String pTitle,
                                                                                         final K pHelpId) {
        return new MetisHelpEntry(pName, pTitle, pHelpId);
    }

    /**
     * Define Contents Help entry.
     *
     * @param pName the name
     * @return the HelpEntry
     */
    public static MetisHelpEntry defineContentsEntry(final String pName) {
        return defineTitledContentsEntry(pName, pName);
    }

    /**
     * Define Contents Help entry.
     *
     * @param pName  the name
     * @param pTitle the title
     * @return the HelpEntry
     */
    public static MetisHelpEntry defineTitledContentsEntry(final String pName,
                                                           final String pTitle) {
        return new MetisHelpEntry(pName, pTitle);
    }

    /**
     * Load Help entries from the file system.
     *
     * @throws OceanusException on error
     */
    protected void loadHelpPages() throws OceanusException {
        loadHelpPages(theEntries);
    }

    /**
     * Load CSS.
     *
     * @param <K>  the keyType
     * @param pKey the styleSheetKey
     * @throws OceanusException on error
     */
    protected <K extends Enum<K> & TethysUIStyleSheetId> void loadCSS(final K pKey) throws OceanusException {
        theCSS = pKey;
    }

    /**
     * Load Help entries from the file system.
     *
     * @param pEntries the Help Entries
     * @throws OceanusException on error
     */
    private static void loadHelpPages(final List<MetisHelpEntry> pEntries) throws OceanusException {
        /* Loop through the entities */
        for (MetisHelpEntry myEntry : pEntries) {
            /* If we have a helpId */
            if (myEntry.getHelpId() != null) {
                /* Reset the builder */
                final String myPage = OceanusResourceLoader.loadResourceToString(myEntry.getHelpId());

                /* Set the HTML for the entry */
                myEntry.setHtml(myPage);
            }

            /* If we have children */
            if (myEntry.getChildren() != null) {
                /* Load the entries */
                loadHelpPages(myEntry.getChildren());
            }
        }
    }
}
