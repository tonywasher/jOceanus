/*
 * Tethys: GUI Utilities
 * Copyright 2012-2026. Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.tonywasher.joceanus.tethys.core.control;

import io.github.tonywasher.joceanus.tethys.api.base.TethysUIConstant;
import io.github.tonywasher.joceanus.tethys.api.control.TethysUILabel;
import io.github.tonywasher.joceanus.tethys.core.base.TethysUICoreComponent;
import io.github.tonywasher.joceanus.tethys.core.factory.TethysUICoreFactory;

/**
 * Label.
 */
public abstract class TethysUICoreLabel
        extends TethysUICoreComponent
        implements TethysUILabel {
    /**
     * The colon indicator.
     */
    public static final String STR_COLON = ":";

    /**
     * Default icon size.
     */
    protected static final int DEFAULT_ICONSIZE = TethysUIConstant.DEFAULT_ICONSIZE;

    /**
     * The id.
     */
    private final Integer theId;

    /**
     * The icon Size.
     */
    private int theSize;

    /**
     * Constructor.
     *
     * @param pFactory the GUI Factory
     */
    protected TethysUICoreLabel(final TethysUICoreFactory<?> pFactory) {
        theId = pFactory.getNextId();
        theSize = DEFAULT_ICONSIZE;
    }

    @Override
    public Integer getId() {
        return theId;
    }

    @Override
    public int getIconSize() {
        return theSize;
    }

    @Override
    public void setIconSize(final int pSize) {
        theSize = pSize;
    }
}
