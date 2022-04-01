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
package net.sourceforge.joceanus.jtethys.ui.swing.control;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIAlignment;
import net.sourceforge.joceanus.jtethys.ui.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.jtethys.ui.core.control.TethysUICoreLabel;
import net.sourceforge.joceanus.jtethys.ui.swing.base.TethysUISwingNode;

/**
 * Tethys Swing Label.
 */
public final class TethysUISwingLabel
        extends TethysUICoreLabel {
    /**
     * The Node.
     */
    private final TethysUISwingNode theNode;

    /**
     * The Label.
     */
    private final JLabel theLabel;

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     */
    TethysUISwingLabel(final TethysUICoreFactory<?> pFactory) {
        super(pFactory);
        theLabel = new JLabel();
        setAlignment(TethysUIAlignment.WEST);
        theNode = new TethysUISwingNode(theLabel);
    }

    @Override
    public void setText(final String pText) {
        theLabel.setText(pText);
    }

    @Override
    public void setErrorText() {
        theLabel.setForeground(Color.RED);
    }

    @Override
    public void setAlignment(final TethysUIAlignment pAlign) {
        theLabel.setHorizontalTextPosition(determineHAlignment(pAlign));
        theLabel.setVerticalTextPosition(determineVAlignment(pAlign));
    }

    @Override
    public TethysUISwingNode getNode() {
        return theNode;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theLabel.setEnabled(pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setVisible(pVisible);
    }

    /**
     * Translate horizontal alignment.
     * @param pAlign the alignment
     * @return the FX alignment
     */
    private static int determineHAlignment(final TethysUIAlignment pAlign) {
        switch (pAlign) {
            case NORTHEAST:
            case EAST:
            case SOUTHEAST:
                return SwingConstants.RIGHT;
            case NORTH:
            case CENTRE:
            case SOUTH:
                return SwingConstants.CENTER;
            case NORTHWEST:
            case WEST:
            case SOUTHWEST:
            default:
                return SwingConstants.LEFT;
        }
    }

    /**
     * Translate vertical alignment.
     * @param pAlign the alignment
     * @return the FX alignment
     */
    private static int determineVAlignment(final TethysUIAlignment pAlign) {
        switch (pAlign) {
            case NORTHEAST:
            case NORTH:
            case NORTHWEST:
                return SwingConstants.TOP;
            case WEST:
            case CENTRE:
            case EAST:
                return SwingConstants.CENTER;
            case SOUTHWEST:
            case SOUTH:
            case SOUTHEAST:
            default:
                return SwingConstants.BOTTOM;
        }
    }

    @Override
    public Integer getWidth() {
        return theLabel.getWidth();
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
