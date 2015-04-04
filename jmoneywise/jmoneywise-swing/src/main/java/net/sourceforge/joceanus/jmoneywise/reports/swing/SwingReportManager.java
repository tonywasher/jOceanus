/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.reports.swing;

import javax.swing.JEditorPane;
import javax.swing.text.html.StyleSheet;

import net.sourceforge.joceanus.jmoneywise.reports.BasicReport;
import net.sourceforge.joceanus.jmoneywise.reports.HTMLBuilder;
import net.sourceforge.joceanus.jmoneywise.reports.ReportManager;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jprometheus.swing.JOceanusSwingUtilitySet;
import net.sourceforge.joceanus.jtethys.JOceanusException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality to hide and restore sections of an HTML document. This is useful for displaying HTML documents in a jEditorPane, allowing a click to
 * open/close sections of the document.
 */
public class SwingReportManager
        extends ReportManager {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SwingReportManager.class);

    /**
     * The Current report.
     */
    private BasicReport theReport = null;

    /**
     * Constructor.
     * @param pView the view
     * @param pUtilitySet the utility set
     * @throws JOceanusException on error
     */
    public SwingReportManager(final View pView,
                              final JOceanusSwingUtilitySet pUtilitySet) throws JOceanusException {
        /* Create the builder */
        super(pView, pUtilitySet, new SwingHTMLBuilder(pView, pUtilitySet));
    }

    @Override
    public SwingHTMLBuilder getBuilder() {
        return (SwingHTMLBuilder) super.getBuilder();
    }

    /**
     * Build display styleSheet.
     * @param pSheet the styleSheet
     */
    public void buildDisplayStyleSheet(final StyleSheet pSheet) {
        /* Pass call to HTML builder */
        getBuilder().buildDisplayStyleSheet(pSheet);
    }

    @Override
    public void setReport(final BasicReport pReport) {
        /* Store the report */
        theReport = pReport;

        /* Clear the maps */
        super.setReport(pReport);
    }

    /**
     * Process link reference.
     * @param pId the id of the reference.
     * @param pWindow the window to update
     */
    public void processReference(final String pId,
                                 final JEditorPane pWindow) {
        String myText = null;

        try {
            /* If this is a table reference */
            if (pId.startsWith(HTMLBuilder.REF_TAB)) {

                /* If the section is hidden */
                if (isHiddenId(pId)) {
                    /* Restore the section and access text */
                    myText = restoreSection(pId);

                    /* else try to hide the section */
                } else {
                    myText = hideSection(pId);
                }
                /* else if this is a delayed table reference */
            } else if (pId.startsWith(HTMLBuilder.REF_DELAY)) {
                /* Process the delayed reference and return if no change */
                if (!theReport.processDelayedReference(getBuilder(), pId)) {
                    return;
                }

                /* Format the text */
                myText = formatXML();

                /* else if this is a filter reference */
            } else if (pId.startsWith(HTMLBuilder.REF_FILTER)) {
                /* Process the filter reference */
                AnalysisFilter<?, ?> myFilter = theReport.processFilterReference(pId);

                /* Fire Action event if necessary */
                if (myFilter != null) {
                    fireActionEvent(myFilter);
                }

                /* Return immediately */
                return;
            }
        } catch (JOceanusException e) {
            LOGGER.error("Failed to process reference", e);
            myText = null;
        }

        /* If we have new text */
        if (myText != null) {
            /* Set it into the window and adjust the scroll */
            pWindow.setText(myText);
            String myId = HTMLBuilder.REF_ID
                          + pId.substring(HTMLBuilder.REF_TAB.length());
            pWindow.scrollToReference(myId);
        }
    }
}
