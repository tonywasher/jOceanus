/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.tethys.ui.core.control;

import net.sourceforge.joceanus.tethys.OceanusException;
import net.sourceforge.joceanus.tethys.event.TethysEventManager;
import net.sourceforge.joceanus.tethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.tethys.logger.TethysLogManager;
import net.sourceforge.joceanus.tethys.logger.TethysLogger;
import net.sourceforge.joceanus.tethys.resource.TethysResourceLoader;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIValueSet;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.ui.api.control.TethysUIHTMLManager;
import net.sourceforge.joceanus.tethys.ui.core.base.TethysUICoreComponent;
import net.sourceforge.joceanus.tethys.ui.core.factory.TethysUICoreFactory;

/**
 * HTML Manager.
 */
public abstract class TethysUICoreHTMLManager
        extends TethysUICoreComponent
        implements TethysUIHTMLManager {
    /**
     * The logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(TethysUICoreHTMLManager.class);

    /**
     * Reference Separator.
     */
    private static final String REF_SEPARATOR = "#";

    /**
     * The id.
     */
    private final Integer theId;

    /**
     * The Event Manager.
     */
    private final TethysEventManager<TethysUIEvent> theEventManager;

    /**
     * The HTMLToFile Manager.
     */
    private final TethysUICoreHTMLToFile theHTMLToFile;

    /**
     * The ValueSet.
     */
    private final TethysUIValueSet theValueSet;

    /**
     * The Current reference.
     */
    private String theCurrentRef;

    /**
     * CSS Base.
     */
    private String theCSSBase;

    /**
     * CSS Processed.
     */
    private String theCSSProcessed;

    /**
     * HTML String.
     */
    private String theHTMLString;

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     */
    protected TethysUICoreHTMLManager(final TethysUICoreFactory<?> pFactory) {
        /* Build standard fields */
        theId = pFactory.getNextId();
        theEventManager = new TethysEventManager<>();

        /* Create the HTMLtoFile Manager */
        theHTMLToFile = new TethysUICoreHTMLToFile(pFactory, this);

        /* Obtain the valueSet */
        theValueSet = pFactory.getValueSet();

        /* Listen to valueSet changes */
        theValueSet.getEventRegistrar().addEventListener(e -> processCSS());
    }

    @Override
    public Integer getId() {
        return theId;
    }

    @Override
    public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Obtain the HTML String.
     * @return the string
     */
    protected String getHTMLString() {
        return theHTMLString;
    }

    /**
     * Obtain the processed CSS.
     * @return the CSS
     */
    protected String getProcessedCSS() {
        return theCSSProcessed;
    }

    @Override
    public void processReference(final String pReference) {
        /* Access local copy of reference */
        String myRef = pReference;
        String myInternal = null;

        /* If the name has a # in it */
        if (myRef.contains(REF_SEPARATOR)) {
            /* Split on the # */
            final String[] myTokens = myRef.split(REF_SEPARATOR);

            /* Allocate the values */
            myRef = myTokens[0];
            myInternal = myTokens[1];

            /* Handle an internal reference */
            if (myRef.length() == 0) {
                myRef = null;
            }
        }

        /* Check whether we need to switch pages */
        final boolean needSwitch = (myRef != null)
                && !myRef.equals(theCurrentRef);

        /* If we failed to switch pages */
        if (needSwitch
                && !loadNewPage(myRef)) {
            /* Don't attempt to scroll to internal reference */
            myInternal = null;
            LOGGER.error("Failed to load page <%s>", myRef);
        }

        /* If we have an internal reference */
        if (myInternal != null) {
            /* Scroll to the reference */
            scrollToReference(myInternal);
        }
    }

    @Override
    public void setHTMLContent(final String pHTMLString,
                               final String pReference) {
        /* Store the reference and string */
        theCurrentRef = pReference;
        theHTMLString = pHTMLString;

        /* load the content */
        loadHTMLContent(theHTMLString);
    }

    /**
     * Load HTML Contents.
     * @param pHTMLString the HTML content.
     */
    protected abstract void loadHTMLContent(String pHTMLString);

    /**
     * Load CSS Contents.
     */
    protected abstract void loadCSSContents();

    @Override
    public void setCSSContent(final TethysUIStyleSheetId pStyleSheet) throws OceanusException {
        /* Store the base sheet */
        theCSSBase = TethysResourceLoader.loadResourceToString(pStyleSheet);
        theCSSProcessed = null;

        /* Process the CSS */
        processCSS();
    }

    /**
     * Process the CSS.
     */
    protected void processCSS() {
        /* If we have a styleSheet */
        if (theCSSBase != null) {
            /* Process the CSS */
            theCSSProcessed = theValueSet.resolveValues(theCSSBase);
        }

        /* reLoad the CSS */
        loadCSSContents();
    }

    @Override
    public void saveToFile() {
        theHTMLToFile.writeToFile();
    }

    /**
     * Load new page.
     * @param pPageRef the page reference
     * @return was new page loaded? true/false
     */
    private boolean loadNewPage(final String pPageRef) {
        return !theEventManager.fireEvent(TethysUIEvent.BUILDPAGE, pPageRef);
    }
}
