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
package io.github.tonywasher.joceanus.tethys.core.button;

import io.github.tonywasher.joceanus.tethys.api.button.TethysUIButtonFactory;
import io.github.tonywasher.joceanus.tethys.api.control.TethysUIControl.TethysUIIconMapSet;
import io.github.tonywasher.joceanus.tethys.core.button.TethysUICoreIconButtonManager.TethysUICoreIconMapSet;

/**
 * Core Button factory.
 *
 * @param <C> the color type
 */
public abstract class TethysUICoreButtonFactory<C>
        implements TethysUIButtonFactory<C> {
    /**
     * Constructor.
     */
    protected TethysUICoreButtonFactory() {
    }

    @Override
    public <T> TethysUIIconMapSet<T> newIconMapSet() {
        return new TethysUICoreIconMapSet<>();
    }

    @Override
    public <T> TethysUIIconMapSet<T> newIconMapSet(final int pWidth) {
        return new TethysUICoreIconMapSet<>(pWidth);
    }
}
