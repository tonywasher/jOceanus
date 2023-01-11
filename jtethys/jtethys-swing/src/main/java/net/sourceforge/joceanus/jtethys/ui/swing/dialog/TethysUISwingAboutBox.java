/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.swing.dialog;

import java.awt.Color;
import java.awt.Dialog.ModalityType;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;

import net.sourceforge.joceanus.jtethys.ui.core.dialog.TethysUICoreAboutBox;
import net.sourceforge.joceanus.jtethys.ui.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.base.TethysUISwingNode;

/**
 * Swing About Box.
 */
public class TethysUISwingAboutBox
        extends TethysUICoreAboutBox {
    /**
     * The Frame.
     */
    private final JFrame theFrame;

    /**
     * The dialog stage.
     */
    private JDialog theDialog;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pFrame the frame
     */
    TethysUISwingAboutBox(final TethysUICoreFactory<?> pFactory,
                          final JFrame pFrame) {
        /* Initialise underlying class */
        super(pFactory);
        if (pFrame == null) {
            throw new IllegalArgumentException("Cannot create Dialog during initialisation");
        }

        /* Store parameters */
        theFrame = pFrame;
    }

    @Override
    public TethysUISwingNode getNode() {
        return (TethysUISwingNode) super.getNode();
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        getNode().setPreferredWidth(pWidth);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        getNode().setPreferredHeight(pHeight);
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
        theDialog = new JDialog(theFrame);
        theDialog.setUndecorated(true);
        theDialog.setModalityType(ModalityType.APPLICATION_MODAL);

        /* Attach the node to the dialog */
        theDialog.getContentPane().add(getNode().getNode());
        theDialog.pack();
        theDialog.setLocationRelativeTo(theFrame);

        getNode().getNode().setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
    }

    @Override
    protected void closeDialog() {
        theDialog.setVisible(false);
    }
}
