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
package net.sourceforge.joceanus.tethys.ui.swing.button;

import java.awt.Insets;

import javax.swing.JButton;

import net.sourceforge.joceanus.tethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.ui.core.button.TethysUICoreDateButtonManager;
import net.sourceforge.joceanus.tethys.ui.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.tethys.ui.swing.base.TethysUISwingNode;

/**
 * JavaFX DateButton Manager.
 */
public final class TethysUISwingDateButtonManager
        extends TethysUICoreDateButtonManager {
    /**
     * The dialog.
     */
    private TethysUISwingDateDialog theDialog;

    /**
     * Constructor.
     * @param pFactory the GuiFactory
     */
    TethysUISwingDateButtonManager(final TethysUICoreFactory<?> pFactory) {
        /* Initialise the super-class */
        super(pFactory);

        /* Set narrow margin */
        getTheButton().setMargin(new Insets(1, 1, 1, 1));
    }

    @Override
    public TethysUISwingNode getNode() {
        return (TethysUISwingNode) super.getNode();
    }

    /**
     * Obtain the button.
     * @return the button
     */
    private JButton getTheButton() {
        return (JButton) getNode().getNode();
    }

    @Override
    public void setVisible(final boolean pVisible) {
        getNode().setVisible(pVisible);
    }

    /**
     * Obtain the dialog.
     * @return the dialog
     */
    public TethysUISwingDateDialog getDialog() {
        ensureDialog();
        return theDialog;
    }

    /**
     * Make sure that the dialog is created.
     */
    private void ensureDialog() {
        /* If the dialog does not exist */
        if (theDialog == null) {
            /* Create it */
            theDialog = new TethysUISwingDateDialog(getConfig());

            /* Add listeners */
            final TethysEventRegistrar<TethysUIEvent> myRegistrar = theDialog.getEventRegistrar();
            myRegistrar.addEventListener(TethysUIEvent.PREPAREDIALOG, e -> handleDialogRequest());
            myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewValue());
            myRegistrar.addEventListener(TethysUIEvent.WINDOWCLOSED, e -> handleNewValue());
        }
    }

    @Override
    protected void showDialog() {
        /* Make sure that the dialog exists */
        ensureDialog();

        /* Show the dialog under the node */
        theDialog.showDialogUnderNode(getNode().getNode());
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        getNode().setPreferredWidth(pWidth);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        getNode().setPreferredWidth(pHeight);
    }

    @Override
    public void setBorderPadding(final Integer pPadding) {
        super.setBorderPadding(pPadding);
        getNode().createWrapperPane(getBorderTitle(), getBorderPadding());
    }

    @Override
    public void setBorderTitle(final String pTitle) {
        super.setBorderTitle(pTitle);
        getNode().createWrapperPane(getBorderTitle(), getBorderPadding());
    }
}
