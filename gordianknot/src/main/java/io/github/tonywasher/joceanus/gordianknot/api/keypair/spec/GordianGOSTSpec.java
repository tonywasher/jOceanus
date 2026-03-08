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
 * Named GOST-2012 Elliptic Curves.
 */
public enum GordianGOSTSpec {
    /**
     * 512-paramSetA.
     */
    GOST512A,

    /**
     * 512-paramSetB.
     */
    GOST512B,

    /**
     * 512-paramSetC.
     */
    GOST512C,

    /**
     * 256-paramSetA.
     */
    GOST256A;
}
