/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.core.kdf;

public enum GordianHKDFMode {
    /**
     * ExtractOnly.
     */
    EXTRACT,

    /**
     * ExpandOnly.
     */
    EXPAND,

    /**
     * Extract then Expand.
     */
    EXTRACTTHENEXPAND;

    /**
     * Do we extract in this mode?
     * @return true/false
     */
    public boolean doExtract() {
        switch (this) {
            case EXTRACT:
            case EXTRACTTHENEXPAND:
                return true;
            case EXPAND:
            default:
                return false;
        }
    }

    /**
     * Do we expand in this mode?
     * @return true/false
     */
    public boolean doExpand() {
        switch (this) {
            case EXPAND:
            case EXTRACTTHENEXPAND:
                return true;
            case EXTRACT:
            default:
                return false;
        }
    }
}
