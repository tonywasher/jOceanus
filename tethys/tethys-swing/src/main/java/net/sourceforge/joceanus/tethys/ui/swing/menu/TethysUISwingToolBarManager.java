/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.tethys.ui.swing.menu;

import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import net.sourceforge.joceanus.tethys.ui.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.tethys.ui.core.menu.TethysUICoreToolBarManager;
import net.sourceforge.joceanus.tethys.ui.swing.base.TethysUISwingIcon;
import net.sourceforge.joceanus.tethys.ui.swing.base.TethysUISwingNode;
import net.sourceforge.joceanus.tethys.ui.swing.base.TethysUISwingUtils;

/**
 * JavaFX ToolBar Manager.
 */
public class TethysUISwingToolBarManager
        extends TethysUICoreToolBarManager {
    /**
     * The Node.
     */
    private final TethysUISwingNode theNode;

    /**
     * The ToolBar.
     */
    private final JToolBar theToolBar;

    /**
     * Constructor.
     * @param pFactory the factory
     */
    TethysUISwingToolBarManager(final TethysUICoreFactory<?> pFactory) {
        super(pFactory);
        theToolBar = new JToolBar();
        theToolBar.setFloatable(false);
        theNode = new TethysUISwingNode(theToolBar);
    }

    @Override
    public TethysUISwingNode getNode() {
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
    public void setPreferredWidth(final Integer pWidth) {
        theNode.setPreferredWidth(pWidth);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        theNode.setPreferredHeight(pHeight);
    }

    @Override
    public TethysUISwingIconElement newIcon(final TethysUIToolBarId pId) {
        final TethysUISwingIconElement myIcon = new TethysUISwingIconElement(this, pId);
        theToolBar.add(myIcon.getButton());
        return myIcon;
    }

    @Override
    public void newSeparator() {
        theToolBar.addSeparator();
    }

    /**
     * SwingIcon.
     */
    public static class TethysUISwingIconElement
            extends TethysUICoreToolElement {
        /**
         * The Button.
         */
        private final JButton theButton;

        /**
         * Constructor.
         * @param pManager the manager
         * @param pId the Id
         */
        TethysUISwingIconElement(final TethysUISwingToolBarManager pManager,
                                 final TethysUIToolBarId pId) {
            super(pManager, pId);
            theButton = new JButton();
            theButton.addActionListener(e -> handlePressed());
            setIcon(pId);
        }

        /**
         * Obtain the button.
         * @return the button
         */
        protected JButton getButton() {
            return theButton;
        }

        @Override
        protected TethysUISwingToolBarManager getManager() {
            return (TethysUISwingToolBarManager) super.getManager();
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

        /**
         * Set the icon.
         * @param pIcon the icon
         */
        private void setIcon(final TethysUIToolBarId pIcon) {
            theButton.setIcon(TethysUISwingIcon.getIcon(TethysUISwingUtils.getIconAtSize(pIcon, getManager().getIconWidth())));
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
