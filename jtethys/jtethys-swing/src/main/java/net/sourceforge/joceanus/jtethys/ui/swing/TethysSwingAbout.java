/*******************************************************************************
 * jTethys: Java Utilities
 * Copyright 2012,2016 Tony Washer
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

import java.awt.Color;
import java.awt.Dialog.ModalityType;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;

import net.sourceforge.joceanus.jtethys.ui.TethysAbout;

/**
 * Swing About Box.
 */
public class TethysSwingAbout
        extends TethysAbout<JComponent, Icon> {
    /**
     * The GuiFactory.
     */
    private final TethysSwingGuiFactory theGuiFactory;

    /**
     * The dialog stage.
     */
    private JDialog theDialog;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    public TethysSwingAbout(final TethysSwingGuiFactory pFactory) {
        /* Initialise underlying class */
        super(pFactory);

        /* Store parameters */
        theGuiFactory = pFactory;
    }

    @Override
    public void showDialog() {
        /* If we have not made the dialog yet */
        if (theDialog == null) {
            makeDialog();
        }

        /* Show the dialog */
        theDialog.setVisible(true);
    }

    /**
     * Make the dialog.
     */
    private void makeDialog() {
        /* Create the dialog */
        JFrame myFrame = theGuiFactory.getFrame();
        theDialog = new JDialog(myFrame);
        theDialog.setUndecorated(true);
        theDialog.setModalityType(ModalityType.APPLICATION_MODAL);

        /* Attach the node to the dialog */
        theDialog.getContentPane().add(getNode());
        theDialog.pack();
        theDialog.setLocationRelativeTo(myFrame);

        getNode().setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
    }

    @Override
    protected void closeDialog() {
        theDialog.setVisible(false);
    }
}