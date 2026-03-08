/*
 * GordianKnot: Security Suite
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

package io.github.tonywasher.joceanus.gordianknot.api.keypair.spec;

/**
 * NTRU KeySpec.
 */
public enum GordianNTRUSpec {
    /**
     * HPS 509 2048.
     */
    HPS509,

    /**
     * HPS 677 2048.
     */
    HPS677,

    /**
     * HPS 821 4096.
     */
    HPS821,

    /**
     * HPS 1229 4096.
     */
    HPS1229,

    /**
     * HRSS 701.
     */
    HRSS701,

    /**
     * HRSS 1373.
     */
    HRSS1373;
}
