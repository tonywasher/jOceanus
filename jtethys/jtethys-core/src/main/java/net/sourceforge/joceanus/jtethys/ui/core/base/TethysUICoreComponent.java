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
package net.sourceforge.joceanus.jtethys.ui.core.base;

import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIComponent;

/**
 * Core Border Pane Manager.
 */
public abstract class TethysUICoreComponent
        implements TethysUIComponent {
    /**
     * Default icon width.
     */
    public static final int DEFAULT_ICONWIDTH = 16;

    /**
     * The Padding.
     */
    private Integer thePadding;

    /**
     * The Title.
     */
    private String theTitle;

    @Override
    public Integer getBorderPadding() {
        return thePadding;
    }

    @Override
    public String getBorderTitle() {
        return theTitle;
    }

    @Override
    public void setBorderPadding(final Integer pPadding) {
        thePadding = pPadding;
    }

    @Override
    public void setBorderTitle(final String pTitle) {
        theTitle = pTitle;
    }
}

