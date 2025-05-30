/* *****************************************************************************
 * Astraeus: Post-Processing
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
package net.sourceforge.joceanus.astraeus.test.jar;

import net.sourceforge.joceanus.astraeus.jar.AstraeusLauncher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * Test build scripts.
 */
class TestBuildScripts {
    @Test
    void buildScripts() {
        /* Access the Backup location */
        final String myBackup = "../dist";
        final File myDir = new File(myBackup + "/bin");
        Assertions.assertDoesNotThrow(() -> AstraeusLauncher.processJarFiles(myDir), "Exception");
    }
}
