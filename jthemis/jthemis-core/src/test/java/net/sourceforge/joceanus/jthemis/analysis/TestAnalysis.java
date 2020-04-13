/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2020 Tony Washer
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
package net.sourceforge.joceanus.jthemis.analysis;

import java.io.File;

import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Test Analysis.
 */
public class TestAnalysis {
    /**
     * Main.
     */
    public static void main(final String[] pArgs) {
        try {
            final String myBase = "c:/Users/Tony/gitNew/jOceanus/jthemis/jthemis-core/src/main/java";
            final String myName = "net.sourceforge.joceanus.jthemis.analysis";
            final ThemisAnalysisPackage myPackage = new ThemisAnalysisPackage(new File(myBase), myName);
            int i = 0;
        } catch (OceanusException e) {
            e.printStackTrace();
        }
    }
}
