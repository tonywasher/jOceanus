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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import net.sourceforge.joceanus.jtethys.ui.TethysAlignment;
import net.sourceforge.joceanus.jtethys.ui.TethysLabel;

/**
 * Tethys Swing Label.
 */
public final class TethysSwingLabel
        extends TethysLabel {
    /**
     * The Node.
     */
    private final TethysSwingNode theNode;

    /**
     * The Label.
     */
    private final JLabel theLabel;

    /**
     * The Context Menu.
     */
    private TethysSwingScrollContextMenu<?> theContextMenu;

    /**
     * Has the context menu handler been set.
     */
    private boolean menuListenerSet;

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     */
    protected TethysSwingLabel(final TethysSwingGuiFactory pFactory) {
        super(pFactory);
        theLabel = new JLabel();
        setAlignment(TethysAlignment.WEST);
        theNode = new TethysSwingNode(theLabel);
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
    public void setAlignment(final TethysAlignment pAlign) {
        theLabel.setHorizontalTextPosition(determineHAlignment(pAlign));
        theLabel.setVerticalTextPosition(determineVAlignment(pAlign));
    }

    @Override
    public TethysSwingNode getNode() {
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
     * Set context menu.
     * @param pMenu the context menu.
     */
    public void setContextMenu(final TethysSwingScrollContextMenu<?> pMenu) {
        /* Record the menu */
        theContextMenu = pMenu;

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
    private int determineHAlignment(final TethysAlignment pAlign) {
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
    private int determineVAlignment(final TethysAlignment pAlign) {
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

    /**
     * Context Menu Listener.
     */
    private class TethysLabelListener
            extends MouseAdapter {
        /**
         * The label.
         */
        private final TethysSwingLabel theLabel;

        /**
         * Constructor.
         * @param pLabel the label
         */
        TethysLabelListener(final TethysSwingLabel pLabel) {
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
