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
package net.sourceforge.joceanus.jtethys.ui;

/**
 * About Box.
 * @param <N> the node type
 * @param <I> the Icon Type
 */
public abstract class TethysAbout<N, I>
        implements TethysNode<N> {
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
    private static final String TEXT_OK = TethysUIResource.ABOUT_OK.getValue();

    /**
     * InSet size.
     */
    private static final int INSET_SIZE = 5;

    /**
     * The panel.
     */
    private final TethysBoxPaneManager<N, I> thePanel;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysAbout(final TethysGuiFactory<N, I> pFactory) {
        /* Access application details */
        final TethysProgram myApp = pFactory.getProgramDefinitions();

        /* Create the components */
        final TethysLabel<N, I> myProduct = pFactory.newLabel(myApp.getName());
        final TethysLabel<N, I> myVersion = pFactory.newLabel(PROMPT_VERSION + " "
                                                              + myApp.getVersion());
        final TethysLabel<N, I> myRevision = pFactory.newLabel(PROMPT_REVISION + " "
                                                               + myApp.getRevision());
        final TethysLabel<N, I> myBuild = pFactory.newLabel(PROMPT_BUILDDATE + " "
                                                            + myApp.getBuiltOn());
        final TethysLabel<N, I> myCopyright = pFactory.newLabel(myApp.getCopyright());

        /* Build the OK button */
        final TethysButton<N, I> myOKButton = pFactory.newButton();
        myOKButton.setText(TEXT_OK);
        myOKButton.getEventRegistrar().addEventListener(e -> closeDialog());

        /* Layout the panel */
        thePanel = pFactory.newVBoxPane();
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
     * Show dialog.
     */
    public abstract void showDialog();

    /**
     * Close the Dialog.
     */
    protected abstract void closeDialog();

    @Override
    public N getNode() {
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
