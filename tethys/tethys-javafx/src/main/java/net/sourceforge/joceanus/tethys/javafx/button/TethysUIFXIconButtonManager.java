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
package net.sourceforge.joceanus.tethys.javafx.button;

import net.sourceforge.joceanus.tethys.core.button.TethysUICoreIconButtonManager;
import net.sourceforge.joceanus.tethys.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.tethys.javafx.base.TethysUIFXNode;

/**
 * Simple button that displays icon.
 *
 * @param <T> the object type
 */
public final class TethysUIFXIconButtonManager<T>
        extends TethysUICoreIconButtonManager<T> {
    /**
     * Constructor.
     *
     * @param pFactory the GUI factory
     * @param pClazz the value class
     */
    TethysUIFXIconButtonManager(final TethysUICoreFactory<?> pFactory,
                                final Class<T> pClazz) {
        /* Initialise underlying class */
        super(pFactory, pClazz);
    }

    @Override
    public TethysUIFXNode getNode() {
        return (TethysUIFXNode) super.getNode();
    }

    @Override
    public void setVisible(final boolean pVisible) {
        getNode().setManaged(pVisible);
        getNode().setVisible(pVisible);
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        getNode().setPreferredWidth(pWidth);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        getNode().setPreferredHeight(pHeight);
    }

    @Override
    public void setBorderPadding(final Integer pPadding) {
        super.setBorderPadding(pPadding);
        getNode().createWrapperPane(getBorderTitle(), getBorderPadding());
    }

    @Override
    public void setBorderTitle(final String pTitle) {
        super.setBorderTitle(pTitle);
        getNode().createWrapperPane(getBorderTitle(), getBorderPadding());
    }
}
