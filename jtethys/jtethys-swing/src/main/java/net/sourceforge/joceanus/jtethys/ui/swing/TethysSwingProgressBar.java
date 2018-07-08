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
package net.sourceforge.joceanus.jtethys.ui.swing;

import java.awt.Color;

import javax.swing.JProgressBar;
import javax.swing.plaf.basic.BasicProgressBarUI;

import net.sourceforge.joceanus.jtethys.ui.TethysProgressBar;

/**
 * Swing ProgressBar.
 */
public class TethysSwingProgressBar
        extends TethysProgressBar {
    /**
     * Field Adjuster.
     */
    private final TethysSwingDataFieldAdjust theAdjuster;

    /**
     * The Node.
     */
    private final TethysSwingNode theNode;

    /**
     * Progress Bar.
     */
    private final JProgressBar theProgress;

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     */
    TethysSwingProgressBar(final TethysSwingGuiFactory pFactory) {
        /* Initialise underlying class */
        super(pFactory);

        /* Create the progress bar */
        theProgress = new JProgressBar();
        theProgress.setMinimum(0);
        theProgress.setStringPainted(true);
        theProgress.setUI(new ProgressUI());

        /* Obtain the field adjuster */
        theAdjuster = pFactory.getFieldAdjuster();

        /* Listen to valueSet changes */
        pFactory.getValueSet().getEventRegistrar().addEventListener(e -> setProgressColour());

        /* Set the colour */
        setProgressColour();

        /* Create the node */
        theNode = new TethysSwingNode(theProgress);
    }

    @Override
    public TethysSwingNode getNode() {
        return theNode;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theProgress.setEnabled(pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theProgress.setVisible(pVisible);
    }

    @Override
    public void setProgress(final int pValue,
                            final int pMaximum) {
        theProgress.setMaximum(pMaximum);
        theProgress.setValue(pValue);
    }

    @Override
    public void setProgress(final double pValue) {
        setProgress((int) (MAX_DOUBLE_VALUE * pValue), MAX_DOUBLE_VALUE);
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        theNode.setPreferredWidth(pWidth);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        theNode.setPreferredHeight(pHeight);
    }

    @Override
    public void setBorderPadding(final Integer pPadding) {
        super.setBorderPadding(pPadding);
        theNode.createWrapperPane(getBorderTitle(), getBorderPadding());
    }

    @Override
    public void setBorderTitle(final String pTitle) {
        super.setBorderTitle(pTitle);
        theNode.createWrapperPane(getBorderTitle(), getBorderPadding());
    }

    /**
     * Set the progress colour.
     */
    private void setProgressColour() {
        theProgress.setForeground(theAdjuster.getProgressColor());
    }

    /**
     * ProgressBar UI.
     */
    private static final class ProgressUI
            extends BasicProgressBarUI {
        @Override
        protected Color getSelectionBackground() {
            return Color.black;
        }

        @Override
        protected Color getSelectionForeground() {
            return Color.black;
        }
    }
}
