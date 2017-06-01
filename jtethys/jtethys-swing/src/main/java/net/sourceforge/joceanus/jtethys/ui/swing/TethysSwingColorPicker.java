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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.sourceforge.joceanus.jtethys.ui.TethysColorPicker;

/**
 * Swing Colour Picker.
 */
public class TethysSwingColorPicker
        extends TethysColorPicker<JComponent, Icon> {
    /**
     * The node.
     */
    private JComponent theNode;

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
     * @param pFactory the GUI Factory
     */
    protected TethysSwingColorPicker(final TethysSwingGuiFactory pFactory) {
        /* Create components */
        super(pFactory);
        theButton = new JButton();
        theNode = theButton;
        theChooser = new JColorChooser();
        theChooser.setPreviewPanel(new JPanel());

        /* Add listener for selection */
        theChooser.getSelectionModel().addChangeListener(e -> handleSelection());

        /* Configure the button */
        theButton.addActionListener(e -> handleDialog());
        theButton.setHorizontalTextPosition(SwingConstants.RIGHT);
    }

    @Override
    public JComponent getNode() {
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

    /**
     * Obtain the colour.
     * @return the colour
     */
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
        theNode = TethysSwingGuiUtils.addPanelBorder(getBorderTitle(), getBorderPadding(), theButton);
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
            JPanel myPanel = new JPanel(new BorderLayout());
            myPanel.setBorder(BorderFactory.createLineBorder(Color.black));
            myPanel.add(theChooser, BorderLayout.CENTER);
            theDialog.setContentPane(myPanel);
            theDialog.pack();

            /* Create focus listener */
            theDialog.addWindowFocusListener(new DialogFocus());
        }

        /* Set the active colour */
        theChooser.setColor(theColour);

        /* Position the dialog just below the node */
        Point myLoc = theButton.getLocationOnScreen();
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
        handleNewValue(TethysSwingGuiUtils.colorToHexString(theColour));
        updateButton();
        theDialog.setVisible(false);
    }

    /**
     * Handle loss of focus.
     */
    private class DialogFocus
            extends WindowAdapter {
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
        private TethysSwatch(final Color pColor) {
            theColor = pColor;
        }

        @Override
        public int getIconWidth() {
            return SWATCH_SIZE;
        }

        @Override
        public int getIconHeight() {
            return SWATCH_SIZE;
        }

        @Override
        public void paintIcon(final Component pComponent,
                              final Graphics pGraphics,
                              final int pX,
                              final int pY) {
            /* Cache the old colour */
            Color myOldColor = pGraphics.getColor();

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
