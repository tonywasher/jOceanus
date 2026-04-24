/*
 * Themis: Java Project Framework
 * Copyright 2026. Tony Washer
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

package io.github.tonywasher.joceanus.themis.stats;

import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisClassInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisMethodInstance;

/**
 * Stats Parser.
 */
public class ThemisStatsParser {
    /**
     * Parse the element.
     *
     * @param pElement  the owning element
     * @param pInstance the instance
     */
    void parseElement(final ThemisStatsElement pElement,
                      final ThemisInstance pInstance) {
        /* Loop through the children */
        for (ThemisInstance myChild : pInstance.getChildren()) {
            if (myChild instanceof ThemisClassInstance myClass) {
                final ThemisStatsClass myElement = new ThemisStatsClass(myClass);
                pElement.addClass(myElement);
                parseElement(myElement, myChild);
            } else if (myChild instanceof ThemisMethodInstance myMethod) {
                final ThemisStatsMethod myElement = new ThemisStatsMethod(myMethod);
                pElement.addMethod(myElement);
                parseElement(myElement, myChild);
            } else {
                parseElement(pElement, myChild);
            }
        }
    }
}
