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
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIComponent;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIEvent;
import io.github.tonywasher.joceanus.tethys.api.control.TethysUIControlFactory;
import io.github.tonywasher.joceanus.tethys.api.control.TethysUIHTMLManager;
import io.github.tonywasher.joceanus.tethys.api.factory.TethysUIFactory;
import io.github.tonywasher.joceanus.tethys.api.pane.TethysUIBoxPaneManager;
import io.github.tonywasher.joceanus.tethys.api.pane.TethysUIPaneFactory;
import io.github.tonywasher.joceanus.tethys.api.pane.TethysUIScrollPaneManager;
import io.github.tonywasher.joceanus.themis.xanalysis.gui.base.ThemisXAnalysisUIStyleSheet;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverProject;

/**
 * Reference Panel.
 */
public class ThemisXAnalysisUIRefPanel
        implements TethysUIComponent {
    /**
     * The Reference Document.
     */
    private final ThemisXAnalysisUIRefDocument theDoc;

    /**
     * The Module select.
     */
    private final ThemisXAnalysisUIRefModuleSelect theSelect;

    /**
     * The DSM Html.
     */
    private final TethysUIHTMLManager theDSMHtml;

    /**
     * The Link Html.
     */
    private final TethysUIHTMLManager theLinkHtml;

    /**
     * The panel.
     */
    private final TethysUIBoxPaneManager thePane;

    /**
     * Constructor.
     *
     * @param pFactory the GUI factory
     * @throws OceanusException on error
     */
    public ThemisXAnalysisUIRefPanel(final TethysUIFactory<?> pFactory) throws OceanusException {
        /* Create the HTML panels */
        final TethysUIControlFactory myControls = pFactory.controlFactory();
        theDSMHtml = myControls.newHTMLManager();
        theDSMHtml.setCSSContent(ThemisXAnalysisUIStyleSheet.CSS);
        theLinkHtml = myControls.newHTMLManager();
        theLinkHtml.setCSSContent(ThemisXAnalysisUIStyleSheet.CSS);

        /* Create scroll-panes */
        final TethysUIPaneFactory myPanes = pFactory.paneFactory();
        final TethysUIScrollPaneManager myDSMScroll = myPanes.newScrollPane();
        myDSMScroll.setContent(theDSMHtml);
        final TethysUIScrollPaneManager myLinkScroll = myPanes.newScrollPane();
        myLinkScroll.setContent(theLinkHtml);

        /* Create the document builder */
        theDoc = new ThemisXAnalysisUIRefDocument();

        /* Create the module select */
        theSelect = new ThemisXAnalysisUIRefModuleSelect(pFactory);
        final TethysUIBoxPaneManager mySelect = myPanes.newHBoxPane();
        mySelect.addNode(myControls.newLabel("Module:"));
        mySelect.addNode(theSelect);
        mySelect.addSpacer();

        /* Create the panel */
        thePane = myPanes.newVBoxPane();
        thePane.addNode(mySelect);
        thePane.addNode(myDSMScroll);
        thePane.addNode(myLinkScroll);

        /* Handle module select */
        theSelect.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE,
                s -> selectModule());

        /* Handle Reference Select */
        theDSMHtml.getEventRegistrar().addEventListener(TethysUIEvent.BUILDPAGE, e -> {
            processReference(e.getDetails(String.class));
            e.consume();
        });
    }

    @Override
    public TethysUIComponent getUnderlying() {
        return thePane;
    }

    /**
     * Set the current project.
     *
     * @param pProject the current project
     */
    public void setCurrentProject(final ThemisXAnalysisSolverProject pProject) {
        /* Declare project to the moduleSelector */
        theSelect.setCurrentProject(pProject);
    }

    /**
     * Select module.
     */
    private void selectModule() {
        /* Select module and hide link list */
        theDoc.setModule(theSelect.getCurrentModule());
        theLinkHtml.setVisible(false);
    }

    /**
     * Process reference.
     *
     * @param pReference the reference
     */
    private void processReference(final String pReference) {
        /* If this is a new package */
        if (theDoc.isNewPackageLink(pReference)) {
            /* Format the package and hide link list */
            theDSMHtml.setHTMLContent(theDoc.formatNewPackageLink(pReference), "");
            theLinkHtml.setVisible(false);

            /* If this is a listLink */
        } else if (theDoc.isListLink(pReference)) {
            /* Format the list and show link list */
            theLinkHtml.setHTMLContent(theDoc.formatListLink(pReference), "");
            theLinkHtml.setVisible(true);
        }
    }
}
