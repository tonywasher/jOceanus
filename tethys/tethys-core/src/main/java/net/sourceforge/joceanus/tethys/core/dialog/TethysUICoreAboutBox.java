/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.tethys.core.dialog;

import net.sourceforge.joceanus.tethys.api.base.TethysUINode;
import net.sourceforge.joceanus.tethys.api.base.TethysUIProgram;
import net.sourceforge.joceanus.tethys.api.button.TethysUIButton;
import net.sourceforge.joceanus.tethys.api.control.TethysUIControlFactory;
import net.sourceforge.joceanus.tethys.api.control.TethysUILabel;
import net.sourceforge.joceanus.tethys.api.dialog.TethysUIAboutBox;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIBoxPaneManager;
import net.sourceforge.joceanus.tethys.core.base.TethysUICoreComponent;
import net.sourceforge.joceanus.tethys.core.base.TethysUIResource;
import net.sourceforge.joceanus.tethys.core.factory.TethysUICoreFactory;

/**
 * About Box.
 */
public abstract class TethysUICoreAboutBox
        extends TethysUICoreComponent
        implements TethysUIAboutBox {
    /**
     * Version Prompt.
     */
    private static final String PROMPT_VERSION = TethysUIResource.ABOUT_VERSION.getValue();

    /**
     * Revision Prompt.
     */
    private static final String PROMPT_REVISION = TethysUIResource.ABOUT_REVISION.getValue();

    /**
     * BuiltOn Prompt.
     */
    private static final String PROMPT_BUILDDATE = TethysUIResource.ABOUT_BUILTON.getValue();

    /**
     * OK Text.
     */
    private static final String TEXT_OK = TethysUIResource.BUTTON_OK.getValue();

    /**
     * InSet size.
     */
    private static final int INSET_SIZE = 5;

    /**
     * The panel.
     */
    private final TethysUIBoxPaneManager thePanel;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysUICoreAboutBox(final TethysUICoreFactory<?> pFactory) {
        /* Access application details */
        final TethysUIProgram myApp = pFactory.getProgramDefinitions();

        /* Create the components */
        final TethysUIControlFactory myFactory = pFactory.controlFactory();
        final TethysUILabel myProduct = myFactory.newLabel(myApp.getName());
        final TethysUILabel myVersion = myFactory.newLabel(PROMPT_VERSION + " "
                + myApp.getVersion());
        final TethysUILabel myRevision = myFactory.newLabel(PROMPT_REVISION + " "
                + myApp.getRevision());
        final TethysUILabel myBuild = myFactory.newLabel(PROMPT_BUILDDATE + " "
                + myApp.getBuiltOn());
        final TethysUILabel myCopyright = myFactory.newLabel(myApp.getCopyright());

        /* Build the OK button */
        final TethysUIButton myOKButton = pFactory.buttonFactory().newButton();
        myOKButton.setText(TEXT_OK);
        myOKButton.getEventRegistrar().addEventListener(e -> closeDialog());

        /* Layout the panel */
        thePanel = pFactory.paneFactory().newVBoxPane();
        thePanel.setBorderPadding(INSET_SIZE);
        thePanel.addNode(myProduct);
        thePanel.addNode(myCopyright);
        thePanel.addStrut();
        thePanel.addNode(myVersion);
        thePanel.addNode(myRevision);
        thePanel.addNode(myBuild);
        thePanel.addStrut();
        thePanel.addNode(myOKButton);
    }

    /**
     * Close the Dialog.
     */
    protected abstract void closeDialog();

    @Override
    public TethysUINode getNode() {
        return thePanel.getNode();
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        thePanel.setEnabled(pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        thePanel.setVisible(pVisible);
    }

    @Override
    public Integer getId() {
        return thePanel.getId();
    }
}
