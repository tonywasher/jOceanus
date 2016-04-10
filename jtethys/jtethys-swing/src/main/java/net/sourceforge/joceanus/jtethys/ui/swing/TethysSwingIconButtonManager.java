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

import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JComponent;

import net.sourceforge.joceanus.jtethys.ui.TethysIconBuilder.TethysIconId;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysSimpleIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysStateIconButtonManager;

/**
 * Simple button that displays icon.
 */
public final class TethysSwingIconButtonManager {
    /**
     * Constructor.
     */
    private TethysSwingIconButtonManager() {
    }

    /**
     * Simple Swing IconButton Manager.
     * @param <T> the object type
     */
    public static class TethysSwingSimpleIconButtonManager<T>
            extends TethysSimpleIconButtonManager<T, JComponent, Icon> {
        /**
         * The node.
         */
        private JComponent theNode;

        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        public TethysSwingSimpleIconButtonManager(final TethysSwingGuiFactory pFactory) {
            /* Initialise underlying class */
            super(pFactory);
            theNode = super.getNode();
        }

        @Override
        public JComponent getNode() {
            return theNode;
        }

        @Override
        public void setVisible(final boolean pVisible) {
            theNode.setVisible(pVisible);
        }

        @Override
        public <K extends Enum<K> & TethysIconId> void setDetailsForValue(final T pValue,
                                                                          final T pNext,
                                                                          final K pId,
                                                                          final String pToolTip) {
            setNewValueForValue(pValue, pNext);
            setIconForValue(pValue, pId == null
                                                ? null
                                                : TethysSwingGuiUtils.getIconAtSize(pId, getWidth()));
            setTooltipForValue(pValue, pToolTip);
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
            theNode = TethysSwingGuiUtils.addPanelBorder(getBorderTitle(), getBorderPadding(), super.getNode());
        }
    }

    /**
     * State-based Swing IconButton Manager.
     * @param <T> the object type
     * @param <S> the state
     */
    public static class TethysSwingStateIconButtonManager<T, S>
            extends TethysStateIconButtonManager<T, S, JComponent, Icon> {
        /**
         * The node.
         */
        private JComponent theNode;

        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        public TethysSwingStateIconButtonManager(final TethysSwingGuiFactory pFactory) {
            /* Initialise underlying class */
            super(pFactory);
            theNode = super.getNode();
        }

        @Override
        public JComponent getNode() {
            return theNode;
        }

        @Override
        public void setVisible(final boolean pVisible) {
            theNode.setVisible(pVisible);
        }

        @Override
        public <K extends Enum<K> & TethysIconId> void setDetailsForValue(final T pValue,
                                                                          final T pNext,
                                                                          final K pId,
                                                                          final String pToolTip) {
            setNewValueForValue(pValue, pNext);
            setIconForValue(pValue, pId == null
                                                ? null
                                                : TethysSwingGuiUtils.getIconAtSize(pId, getWidth()));
            setTooltipForValue(pValue, pToolTip);
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
            theNode = TethysSwingGuiUtils.addPanelBorder(getBorderTitle(), getBorderPadding(), super.getNode());
        }
    }
}
