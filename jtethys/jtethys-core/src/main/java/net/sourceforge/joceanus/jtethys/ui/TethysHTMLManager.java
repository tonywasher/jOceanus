/*******************************************************************************
 * jTethys: Java Utilities
 * Copyright 2012,2017 Tony Washer
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
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;

/**
 * Tree Manager.
 * @param <N> the Node type
 * @param <I> the icon type
 */
public abstract class TethysHTMLManager<N, I>
        implements TethysEventProvider<TethysUIEvent>, TethysNode<N> {
    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TethysHTMLManager.class);

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
    private final TethysHTMLToFile<N, I> theHTMLToFile;

    /**
     * The ValueSet.
     */
    private final TethysValueSet theValueSet;

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
    protected TethysHTMLManager(final TethysGuiFactory<N, I> pFactory) {
        /* Build standard fields */
        theId = pFactory.getNextId();
        theEventManager = new TethysEventManager<>();

        /* Create the HTMLtoFile Manager */
        theHTMLToFile = new TethysHTMLToFile<>(pFactory, this);

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

    /**
     * Process selected reference.
     * @param pReference the reference
     */
    public void processReference(final String pReference) {
        /* Access local copy of reference */
        String myRef = pReference;
        String myInternal = null;

        /* If the name has a # in it */
        if (myRef.contains(REF_SEPARATOR)) {
            /* Split on the # */
            String[] myTokens = myRef.split(REF_SEPARATOR);

            /* Allocate the values */
            myRef = myTokens[0];
            myInternal = myTokens[1];

            /* Handle an internal reference */
            if (myRef.length() == 0) {
                myRef = null;
            }
        }

        /* Check whether we need to switch pages */
        boolean needSwitch = (myRef != null)
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

    /**
     * Set the HTML.
     * @param pHTMLString the HTML content.
     * @param pReference the reference
     */
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

    /**
     * Set the CSS.
     * @param pStyleSheet the CSS content.
     */
    public void setCSSContent(final String pStyleSheet) {
        /* Store the base sheet */
        theCSSBase = pStyleSheet;
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

    /**
     * Scroll to reference.
     * @param pReference the reference
     */
    public abstract void scrollToReference(String pReference);

    /**
     * Print the contents.
     */
    public abstract void printIt();

    /**
     * SaveToFile.
     */
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
