/*******************************************************************************
 * Tethys: GUI Utilities
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.tethys.swing.control;

import net.sourceforge.joceanus.tethys.api.base.TethysUIAlignment;
import net.sourceforge.joceanus.tethys.api.base.TethysUIArrowIconId;
import net.sourceforge.joceanus.tethys.api.base.TethysUIIcon;
import net.sourceforge.joceanus.tethys.api.base.TethysUIIconId;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.tethys.core.control.TethysUICoreLabel;
import net.sourceforge.joceanus.tethys.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.tethys.swing.base.TethysUISwingArrowIcon;
import net.sourceforge.joceanus.tethys.swing.base.TethysUISwingIcon;
import net.sourceforge.joceanus.tethys.swing.base.TethysUISwingNode;
import net.sourceforge.joceanus.tethys.swing.base.TethysUISwingUtils;
import net.sourceforge.joceanus.tethys.swing.menu.TethysUISwingScrollMenu;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
     * The Context Menu.
     */
    private TethysUISwingScrollMenu<?> theContextMenu;

    /**
     * Has the context menu handler been set.
     */
    private boolean menuListenerSet;

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     */
    TethysUISwingLabel(final TethysUICoreFactory<?> pFactory) {
        super(pFactory);
        theLabel = new JLabel();
        setAlignment(TethysUIAlignment.WEST);
        theNode = new TethysUISwingNode(theLabel);
        setTextOnly();
    }

    @Override
    public void setText(final String pText) {
        theLabel.setText(pText);
    }

    @Override
    public void setIcon(final TethysUIArrowIconId pIcon) {
        setIcon(TethysUISwingArrowIcon.getIconForId(pIcon));
    }

    @Override
    public void setIcon(final TethysUIIconId pId) {
        setIcon(TethysUISwingUtils.getIconAtSize(pId, getIconSize()));
    }

    @Override
    public void setIcon(final TethysUIIcon pIcon) {
        theLabel.setIcon(TethysUISwingIcon.getIcon(pIcon));
    }

    @Override
    public void setErrorText() {
        theLabel.setForeground(Color.RED);
    }

    @Override
    public void setAlignment(final TethysUIAlignment pAlign) {
        theLabel.setHorizontalTextPosition(determineHAlignment(pAlign));
        theLabel.setHorizontalAlignment(determineHAlignment(pAlign));
        theLabel.setVerticalTextPosition(determineVAlignment(pAlign));
        theLabel.setVerticalAlignment(determineVAlignment(pAlign));
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

    @Override
    public void setContextMenu(final TethysUIScrollMenu<?> pMenu) {
        /* Record the menu */
        theContextMenu = (TethysUISwingScrollMenu<?>) pMenu;

        /* If the listener has not been set */
        if (!menuListenerSet) {
            /* Set the handler */
            theLabel.addMouseListener(new TethysLabelListener(this));
            menuListenerSet = true;
        }
    }

    /**
     * Handle mouse event.
     * @param pEvent the event
     */
    void handleContextMenu(final MouseEvent pEvent) {
        if (theContextMenu != null
                && pEvent.isPopupTrigger()) {
            theContextMenu.showMenuAtPosition(theLabel, pEvent.getX(), pEvent.getY());
        }
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
    public void setIconOnly() {
        theLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }

    @Override
    public void setTextAndIcon() {
        theLabel.setHorizontalAlignment(SwingConstants.CENTER);
        theLabel.setVerticalAlignment(SwingConstants.CENTER);
        theLabel.setHorizontalTextPosition(SwingConstants.LEFT);
    }

    @Override
    public void setIconAndText() {
        theLabel.setHorizontalAlignment(SwingConstants.CENTER);
        theLabel.setVerticalAlignment(SwingConstants.CENTER);
        theLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
    }

    @Override
    public void setTextOnly() {
        theLabel.setHorizontalAlignment(SwingConstants.CENTER);
        theLabel.setVerticalAlignment(SwingConstants.CENTER);
        theLabel.setHorizontalTextPosition(SwingConstants.CENTER);
    }

    @Override
    public Integer getWidth() {
        return (int) theLabel.getPreferredSize().getWidth();
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
     * Context Menu Listener.
     */
    private class TethysLabelListener
            extends MouseAdapter {
        /**
         * The label.
         */
        private final TethysUISwingLabel theLabel;

        /**
         * Constructor.
         * @param pLabel the label
         */
        TethysLabelListener(final TethysUISwingLabel pLabel) {
            theLabel = pLabel;
        }

        @Override
        public void mousePressed(final MouseEvent pEvent) {
            theLabel.handleContextMenu(pEvent);
        }

        @Override
        public void mouseReleased(final MouseEvent pEvent) {
            theLabel.handleContextMenu(pEvent);
        }
    }
}
