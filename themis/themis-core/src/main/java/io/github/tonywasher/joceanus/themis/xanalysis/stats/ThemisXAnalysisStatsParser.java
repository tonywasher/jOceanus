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

package io.github.tonywasher.joceanus.themis.xanalysis.stats;

import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisClassInstance;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisMethodInstance;

/**
 * Stats Parser.
 */
public class ThemisXAnalysisStatsParser {
    /**
     * Parse the element.
     */
    void parseElement(final ThemisXAnalysisStatsElement pElement,
                      final ThemisXAnalysisInstance pInstance) {
        /* Loop through the children */
        for (ThemisXAnalysisInstance myChild : pInstance.getChildren()) {
            if (myChild instanceof ThemisXAnalysisClassInstance myClass) {
                final ThemisXAnalysisStatsClass myElement = new ThemisXAnalysisStatsClass(myClass);
                pElement.addClass(myElement);
                parseElement(myElement, pInstance);
            } else if (myChild instanceof ThemisXAnalysisMethodInstance myMethod) {
                final ThemisXAnalysisStatsMethod myElement = new ThemisXAnalysisStatsMethod(myMethod);
                pElement.addMethod(myElement);
                parseElement(myElement, pInstance);
            } else {
                parseElement(pElement, myChild);
            }
        }
    }
}
