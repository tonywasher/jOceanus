/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2022 Tony Washer
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

import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.SwingConstants;

import net.sourceforge.joceanus.jtethys.ui.TethysArrowIconId;
import net.sourceforge.joceanus.jtethys.ui.TethysButton;
import net.sourceforge.joceanus.jtethys.ui.TethysIcon;
import net.sourceforge.joceanus.jtethys.ui.TethysIconId;

/**
 * Tethys Swing Button.
 */
public class TethysSwingButton
        extends TethysButton {
    /**
     * The Node.
     */
    private final TethysSwingNode theNode;

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

        /* Create the button */
        theButton = new JButton();
        theButton.addActionListener(e -> handlePressed());

        /* Create the node */
        theNode = new TethysSwingNode(theButton);
    }

    @Override
    public TethysSwingNode getNode() {
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
    public void setIcon(final TethysIcon pIcon) {
        theButton.setIcon(TethysSwingIcon.getIcon(pIcon));
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
}
