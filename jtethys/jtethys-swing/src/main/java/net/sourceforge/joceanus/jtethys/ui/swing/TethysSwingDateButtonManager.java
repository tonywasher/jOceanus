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
package net.sourceforge.joceanus.jtethys.ui.swing;

import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;

import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.ui.TethysDateButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * JavaFX DateButton Manager.
 */
public class TethysSwingDateButtonManager
        extends TethysDateButtonManager<JComponent, Icon> {
    /**
     * The node.
     */
    private JComponent theNode;

    /**
     * The dialog.
     */
    private TethysSwingDateDialog theDialog;

    /**
     * Constructor.
     * @param pFactory the GuiFactory
     */
    protected TethysSwingDateButtonManager(final TethysSwingGuiFactory pFactory) {
        /* Initialise the super-class */
        super(pFactory);
        theNode = super.getNode();

        /* Set narrow margin */
        ((JButton) getNode()).setMargin(new Insets(1, 1, 1, 1));
    }

    @Override
    public JComponent getNode() {
        return theNode;
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setVisible(pVisible);
    }

    /**
     * Obtain the dialog.
     * @return the dialog
     */
    public TethysSwingDateDialog getDialog() {
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
            theDialog = new TethysSwingDateDialog(getConfig());

            /* Add listeners */
            TethysEventRegistrar<TethysUIEvent> myRegistrar = theDialog.getEventRegistrar();
            myRegistrar.addEventListener(TethysUIEvent.PREPAREDIALOG, e -> handleDialogRequest());
            myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewValue());
            myRegistrar.addEventListener(TethysUIEvent.WINDOWCLOSED, e -> handleDialogClosed());
        }
    }

    @Override
    protected void showDialog() {
        /* Make sure that the dialog exists */
        ensureDialog();

        /* Show the dialog under the node */
        theDialog.showDialogUnderNode(getNode());
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        Dimension myDim = theNode.getPreferredSize();
        myDim = new Dimension(pWidth, myDim.height);
        theNode.setPreferredSize(myDim);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        Dimension myDim = theNode.getPreferredSize();
        myDim = new Dimension(myDim.width, pHeight);
        theNode.setPreferredSize(myDim);
    }

    @Override
    public void setBorderPadding(final Integer pPadding) {
        super.setBorderPadding(pPadding);
        createWrapperPane();
    }

    @Override
    public void setBorderTitle(final String pTitle) {
        super.setBorderTitle(pTitle);
        createWrapperPane();
    }

    /**
     * create wrapper pane.
     */
    private void createWrapperPane() {
        theNode = TethysSwingGuiUtils.addPanelBorder(getBorderTitle(), getBorderPadding(), super.getNode());
    }
}
