/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.tethys.test.ui.swing;

import net.sourceforge.joceanus.tethys.test.ui.TethysTest;
import net.sourceforge.joceanus.tethys.swing.launch.TethysUISwingLaunch;

/**
 * Tethys Swing Test main.
 */
public class TethysTestSwing
        extends TethysUISwingLaunch {
    /**
     * launch program.
     *
     * @param pArgs the arguments
     */
    public static void main(final String[] pArgs) {
        launch(new TethysTest());
    }
}