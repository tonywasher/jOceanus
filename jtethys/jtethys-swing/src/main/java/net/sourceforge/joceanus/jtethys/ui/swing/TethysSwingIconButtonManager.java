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

import javax.swing.BorderFactory;
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
         * Constructor.
         * @param pFactory the GUI factory
         */
        public TethysSwingSimpleIconButtonManager(final TethysSwingGuiFactory pFactory) {
            /* Initialise underlying class */
            super(pFactory);
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
        public void setBorderTitle(final String pTitle) {
            getNode().setBorder(BorderFactory.createTitledBorder(pTitle));
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
         * Constructor.
         * @param pFactory the GUI factory
         */
        public TethysSwingStateIconButtonManager(final TethysSwingGuiFactory pFactory) {
            /* Initialise underlying class */
            super(pFactory);
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
        public void setBorderTitle(final String pTitle) {
            getNode().setBorder(BorderFactory.createTitledBorder(pTitle));
        }
    }
}
