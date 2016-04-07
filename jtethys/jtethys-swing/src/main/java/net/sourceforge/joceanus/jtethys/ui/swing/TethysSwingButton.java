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

import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingConstants;

import net.sourceforge.joceanus.jtethys.ui.TethysButton;
import net.sourceforge.joceanus.jtethys.ui.TethysIconBuilder.TethysIconId;

/**
 * Tethys Swing Button.
 */
public class TethysSwingButton
        extends TethysButton<JComponent, Icon> {
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
        theButton.addActionListener(e -> handlePressed());
    }

    @Override
    public JComponent getNode() {
        return theButton;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theButton.setEnabled(pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theButton.setVisible(pVisible);
    }

    @Override
    public void setText(final String pText) {
        theButton.setText(pText);
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
    public void setBorderTitle(final String pTitle) {
        theButton.setBorder(BorderFactory.createTitledBorder(pTitle));
    }
}
