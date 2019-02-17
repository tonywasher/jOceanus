/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2019 Tony Washer
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

import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import net.sourceforge.joceanus.jtethys.ui.TethysIcon;
import net.sourceforge.joceanus.jtethys.ui.TethysIconId;
import net.sourceforge.joceanus.jtethys.ui.TethysToolBarManager;

/**
 * JavaFX ToolBar Manager.
 */
public class TethysSwingToolBarManager
        extends TethysToolBarManager {
    /**
     * The Node.
     */
    private final TethysSwingNode theNode;

    /**
     * The ToolBar.
     */
    private final JToolBar theToolBar;

    /**
     * Constructor.
     * @param pFactory the factory
     */
    TethysSwingToolBarManager(final TethysSwingGuiFactory pFactory) {
        super(pFactory);
        theToolBar = new JToolBar();
        theToolBar.setFloatable(false);
        theNode = new TethysSwingNode(theToolBar);
    }

    @Override
    public TethysSwingNode getNode() {
        return theNode;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theToolBar.setEnabled(pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theToolBar.setVisible(pVisible);
    }

    @Override
    public <I> TethysSwingIconElement<I> newIcon(final I pId) {
        final TethysSwingIconElement<I> myIcon = new TethysSwingIconElement<>(this, pId);
        theToolBar.add(myIcon.getButton());
        return myIcon;
    }

    @Override
    public void newSeparator() {
        theToolBar.addSeparator();
    }

    /**
     * JavaFXIcon.
     * @param <S> the id type
     */
    public static class TethysSwingIconElement<S>
            extends TethysToolElement<S> {
        /**
         * The Button.
         */
        private final JButton theButton;

        /**
         * Constructor.
         * @param pManager the manager
         * @param pId the Id
         */
        TethysSwingIconElement(final TethysSwingToolBarManager pManager,
                               final S pId) {
            super(pManager, pId);
            theButton = new JButton();
            theButton.addActionListener(e -> handlePressed());
        }

        /**
         * Obtain the button.
         * @return the button
         */
        protected JButton getButton() {
            return theButton;
        }

        @Override
        protected TethysSwingToolBarManager getManager() {
            return (TethysSwingToolBarManager) super.getManager();
        }

        @Override
        protected void setVisible(final boolean pVisible) {
            theButton.setVisible(pVisible);
        }

        @Override
        protected void enableItem(final boolean pEnabled) {
            theButton.setEnabled(pEnabled);
        }

        @Override
        public void setText(final String pText) {
            theButton.setText(pText);
        }

        @Override
        public void setToolTip(final String pTip) {
            theButton.setToolTipText(pTip);
        }

        @Override
        public <K extends Enum<K> & TethysIconId> void setIcon(final K pId) {
            setIcon(TethysSwingGuiUtils.getIconAtSize(pId, getManager().getIconWidth()));
        }

        @Override
        public void setIcon(final TethysIcon pIcon) {
            theButton.setIcon(TethysSwingIcon.getIcon(pIcon));
        }

        @Override
        public void setIconOnly() {
            theButton.setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        public void setTextAndIcon() {
            theButton.setHorizontalAlignment(SwingConstants.CENTER);
            theButton.setVerticalAlignment(SwingConstants.CENTER);
            theButton.setHorizontalTextPosition(SwingConstants.CENTER);
            theButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        }

        @Override
        public void setTextOnly() {
            theButton.setHorizontalAlignment(SwingConstants.CENTER);
            theButton.setVerticalAlignment(SwingConstants.CENTER);
            theButton.setHorizontalTextPosition(SwingConstants.CENTER);
        }
    }
}
