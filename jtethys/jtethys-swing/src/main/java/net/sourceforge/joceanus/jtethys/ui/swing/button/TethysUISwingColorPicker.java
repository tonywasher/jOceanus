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
package net.sourceforge.joceanus.jtethys.ui.swing.button;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.sourceforge.joceanus.jtethys.ui.core.dialog.TethysUICoreColorPicker;
import net.sourceforge.joceanus.jtethys.ui.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.base.TethysUISwingNode;
import net.sourceforge.joceanus.jtethys.ui.swing.base.TethysUISwingUtils;

/**
 * Swing Colour Picker.
 */
public class TethysUISwingColorPicker
        extends TethysUICoreColorPicker<Color> {
    /**
     * The Node.
     */
    private final TethysUISwingNode theNode;

    /**
     * Button.
     */
    private final JButton theButton;

    /**
     * ColourChooser.
     */
    private final JColorChooser theChooser;

    /**
     * Dialog.
     */
    private JDialog theDialog;

    /**
     * Colour.
     */
    private Color theColour;

    /**
     * Constructor.
     * @param pFactory the factory
     */
    TethysUISwingColorPicker(final TethysUICoreFactory<?> pFactory) {
        super(pFactory);

        /* Create components */
        theButton = new JButton();
        theChooser = new JColorChooser();
        theChooser.setPreviewPanel(new JPanel());

        /* Create the node */
        theNode = new TethysUISwingNode(theButton);

        /* Add listener for selection */
        theChooser.getSelectionModel().addChangeListener(e -> handleSelection());

        /* Configure the button */
        theButton.addActionListener(e -> handleDialog());
        theButton.setHorizontalTextPosition(SwingConstants.RIGHT);
    }

    @Override
    public TethysUISwingNode getNode() {
        return theNode;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theButton.setEnabled(pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setVisible(pVisible);
    }

    @Override
    public void setValue(final String pValue) {
        super.setValue(pValue);
        theColour = Color.decode(pValue);
        updateButton();
    }

    /**
     * Update the button.
     */
    private void updateButton() {
        theButton.setText(getValue());
        theButton.setIcon(new TethysSwatch(theColour));
    }

    @Override
    public Color getColour() {
        return theColour;
    }

    /**
     * Obtain a swatch of the selected colour.
     * @return the swatch
     */
    public Icon getSwatch() {
        return new TethysSwatch(theColour);
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        theNode.setPreferredWidth(pWidth);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        theNode.setPreferredHeight(pHeight);
    }

    /**
     * Handle the dialog.
     */
    private void handleDialog() {
        /* If the dialog has not yet been created */
        if (theDialog == null) {
            /* Create the new dialog */
            theDialog = new JDialog();
            theDialog.setUndecorated(true);
            final JPanel myPanel = new JPanel(new BorderLayout());
            myPanel.setBorder(BorderFactory.createLineBorder(Color.black));
            myPanel.add(theChooser, BorderLayout.CENTER);
            theDialog.setContentPane(myPanel);
            theDialog.pack();

            /* Create focus listener */
            theDialog.addWindowFocusListener(new DialogFocus(theDialog));
        }

        /* Set the active colour */
        theChooser.setColor(theColour);

        /* Position the dialog just below the node */
        final Point myLoc = theButton.getLocationOnScreen();
        theDialog.setLocation(myLoc.x, myLoc.y
                + theButton.getHeight());

        /* display the dialog */
        theDialog.setVisible(true);
    }

    /**
     * Handle the dialog.
     */
    private void handleSelection() {
        /* Obtain colour selection */
        theColour = theChooser.getColor();
        handleNewValue(TethysUISwingUtils.colorToHexString(theColour));
        updateButton();
        theDialog.setVisible(false);
    }

    /**
     * Handle loss of focus.
     */
    private class DialogFocus
            extends WindowAdapter {
        /**
         * The dialog.
         */
        private final JDialog theDialog;

        /**
         * Constructor.
         * @param pDialog the dialog
         */
        DialogFocus(final JDialog pDialog) {
            theDialog = pDialog;
        }

        @Override
        public void windowLostFocus(final WindowEvent e) {
            /* Hide the dialog */
            theDialog.setVisible(false);

            /* Note that no selection has been made */
            handleFocusLoss();
        }
    }

    /**
     * A Swatch icon.
     */
    private static final class TethysSwatch
            implements Icon {
        /**
         * The Swatch size.
         */
        private static final int SWATCH_SIZE = 14;

        /**
         * The Swatch colour.
         */
        private final Color theColor;

        /**
         * Constructor.
         * @param pColor the colour.
         */
        TethysSwatch(final Color pColor) {
            theColor = pColor;
        }

        @Override
        public int getIconWidth() {
            return SWATCH_SIZE;
        }

        @Override
        public int getIconHeight() {
            return getIconWidth();
        }

        @Override
        public void paintIcon(final Component pComponent,
                              final Graphics pGraphics,
                              final int pX,
                              final int pY) {
            /* Cache the old colour */
            final Color myOldColor = pGraphics.getColor();

            /* Paint the region */
            pGraphics.setColor(theColor);
            pGraphics.fillRect(pX, pY, SWATCH_SIZE, SWATCH_SIZE);

            /* Paint the region */
            pGraphics.setColor(Color.BLACK);
            pGraphics.drawRect(pX, pY, SWATCH_SIZE, SWATCH_SIZE);

            /* Restore the old colour */
            pGraphics.setColor(myOldColor);
        }
    }
}
