/*
 * MoneyWise: Finance Application
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
package io.github.tonywasher.joceanus.moneywise.ui.javafx;

import io.github.tonywasher.joceanus.moneywise.launch.MoneyWiseApp;
import io.github.tonywasher.joceanus.tethys.api.factory.TethysUILaunchProgram;
import io.github.tonywasher.joceanus.tethys.javafx.launch.TethysUIFXLaunch;

/**
 * MoneyWise javaFX StartUp.
 */
public class MoneyWise4FX
        extends TethysUIFXLaunch {
    /**
     * Constructor.
     */
    public MoneyWise4FX() {
    }

    @Override
    protected TethysUILaunchProgram getProgramInfo() {
        return new MoneyWiseApp();
    }
}
