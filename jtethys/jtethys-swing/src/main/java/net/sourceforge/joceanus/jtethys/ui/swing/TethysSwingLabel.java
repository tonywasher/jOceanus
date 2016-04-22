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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import net.sourceforge.joceanus.jtethys.ui.TethysAlignment;
import net.sourceforge.joceanus.jtethys.ui.TethysLabel;

/**
 * Tethys Swing Label.
 */
public class TethysSwingLabel
        extends TethysLabel<JComponent, Icon> {
    /**
     * The Node.
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
    public JComponent getNode() {
        return theLabel;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theLabel.setEnabled(pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theLabel.setVisible(pVisible);
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
            theLabel.addMouseListener(new TethysLabelListener());
            menuListenerSet = true;
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
        Dimension myDim = theLabel.getPreferredSize();
        myDim = new Dimension(pWidth, myDim.height);
        theLabel.setPreferredSize(myDim);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        Dimension myDim = theLabel.getPreferredSize();
        myDim = new Dimension(myDim.width, pHeight);
        theLabel.setPreferredSize(myDim);
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
        TethysSwingGuiUtils.setPanelBorder(getBorderTitle(), getBorderPadding(), theLabel);
    }

    /**
     * Context Menu Listener.
     */
    private class TethysLabelListener
            extends MouseAdapter {
        @Override
        public void mousePressed(final MouseEvent pEvent) {
            handleContextMenu(pEvent);
        }

        @Override
        public void mouseReleased(final MouseEvent pEvent) {
            handleContextMenu(pEvent);
        }

        /**
         * Handle mouse event.
         * @param pEvent the event
         */
        private void handleContextMenu(final MouseEvent pEvent) {
            if ((theContextMenu != null) && (pEvent.isPopupTrigger())) {
                theContextMenu.showMenuAtPosition(theLabel, pEvent.getX(), pEvent.getY());
            }
        }
    }
}
