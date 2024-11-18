/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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
 ******************************************************************************/
/**
 * MoneyWise javaFX.
 */
module net.sourceforge.joceanus.moneywise.javafx {
    /* JavaFX Libraries */
    requires javafx.graphics;

    /* Exports */
    requires net.sourceforge.joceanus.moneywise.core;
    requires net.sourceforge.joceanus.jtethys.core;
    requires net.sourceforge.joceanus.jtethys.javafx;

    /* Exports */
    exports net.sourceforge.joceanus.moneywise.ui.javafx to javafx.graphics;
}