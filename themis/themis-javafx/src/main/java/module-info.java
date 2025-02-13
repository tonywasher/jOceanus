/*******************************************************************************
 * Themis: Java Project Framework
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

/**
 * Themis code analysis - javaFX.
 */
module net.sourceforge.joceanus.themis.javafx {
    /* javaFX */
    requires javafx.graphics;

    /* jOceanus */
    requires net.sourceforge.joceanus.tethys.core;
    requires net.sourceforge.joceanus.tethys.javafx;
    requires net.sourceforge.joceanus.themis.core;

    /* Exports */
    exports net.sourceforge.joceanus.themis.ui.javafx to javafx.graphics;
}
