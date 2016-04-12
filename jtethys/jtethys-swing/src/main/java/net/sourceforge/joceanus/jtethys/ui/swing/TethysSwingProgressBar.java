/*******************************************************************************
 * jTethys: Java Utilities
 * Copyright 2012,2014 Tony Washer
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
import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.plaf.basic.BasicProgressBarUI;

import net.sourceforge.joceanus.jtethys.ui.TethysProgressBar;

/**
 * Swing ProgressBar.
 */
public class TethysSwingProgressBar
        extends TethysProgressBar<JComponent, Icon> {
    /**
     * Progress Bar.
     */
    private final JProgressBar theProgress;

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     */
    protected TethysSwingProgressBar(final TethysSwingGuiFactory pFactory) {
        super(pFactory);
        theProgress = new JProgressBar();
        theProgress.setMinimum(0);
        theProgress.setStringPainted(true);
        theProgress.setForeground(Color.green);
        theProgress.setUI(new ProgressUI());
    }

    @Override
    public JComponent getNode() {
        return theProgress;
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
        Dimension myDim = theProgress.getPreferredSize();
        myDim = new Dimension(pWidth, myDim.height);
        theProgress.setPreferredSize(myDim);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        Dimension myDim = theProgress.getPreferredSize();
        myDim = new Dimension(myDim.width, pHeight);
        theProgress.setPreferredSize(myDim);
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
        TethysSwingGuiUtils.setPanelBorder(getBorderTitle(), getBorderPadding(), theProgress);
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
