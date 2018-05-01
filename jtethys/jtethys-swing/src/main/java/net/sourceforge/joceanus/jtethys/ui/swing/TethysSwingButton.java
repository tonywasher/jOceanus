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

import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingConstants;

import net.sourceforge.joceanus.jtethys.ui.TethysArrowIconId;
import net.sourceforge.joceanus.jtethys.ui.TethysButton;
import net.sourceforge.joceanus.jtethys.ui.TethysIconBuilder.TethysIconId;

/**
 * Tethys Swing Button.
 */
public class TethysSwingButton
        extends TethysButton<JComponent, Icon> {
    /**
     * The node.
     */
    private JComponent theNode;

    /**
     * The button.
     */
    private final JButton theButton;

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     */
    protected TethysSwingButton(final TethysSwingGuiFactory pFactory) {
        super(pFactory);
        theButton = new JButton();
        theNode = theButton;
        theButton.addActionListener(e -> handlePressed());
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
    public void setText(final String pText) {
        theButton.setText(pText);
    }

    @Override
    public void setIcon(final TethysArrowIconId pIcon) {
        setIcon(TethysSwingArrowIcon.getIconForId(pIcon));
    }

    @Override
    public <K extends Enum<K> & TethysIconId> void setIcon(final K pId) {
        setIcon(TethysSwingGuiUtils.getIconAtSize(pId, getIconWidth()));
    }

    @Override
    public void setIcon(final Icon pIcon) {
        theButton.setIcon(pIcon);
    }

    @Override
    public void setToolTip(final String pTip) {
        theButton.setToolTipText(pTip);
    }

    @Override
    public void setNullMargins() {
        theButton.setMargin(new Insets(0, 0, 0, 0));
    }

    @Override
    public void setIconOnly() {
        theButton.setHorizontalAlignment(SwingConstants.CENTER);
    }

    @Override
    public void setTextAndIcon() {
        theButton.setHorizontalAlignment(SwingConstants.CENTER);
        theButton.setVerticalAlignment(SwingConstants.CENTER);
        theButton.setHorizontalTextPosition(SwingConstants.LEFT);
    }

    @Override
    public void setTextOnly() {
        theButton.setHorizontalAlignment(SwingConstants.CENTER);
        theButton.setVerticalAlignment(SwingConstants.CENTER);
        theButton.setHorizontalTextPosition(SwingConstants.CENTER);
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
}
