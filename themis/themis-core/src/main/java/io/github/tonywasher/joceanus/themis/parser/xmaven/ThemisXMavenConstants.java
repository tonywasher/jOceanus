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

package io.github.tonywasher.joceanus.themis.parser.xmaven;

import java.util.List;

/**
 * Maven Constants.
 */
public final class ThemisXMavenConstants {
    /**
     * Private constructor.
     */
    private ThemisXMavenConstants() {
    }

    /**
     * Zero.
     */
    static final Long ZERO = 0L;

    /**
     * Alpha.
     */
    static final String ALPHA = "a";

    /**
     * Beta.
     */
    static final String BETA = "b";

    /**
     * Milestone.
     */
    static final String MILESTONE = "m";

    /**
     * RC.
     */
    static final String RC = "rc";

    /**
     * GA.
     */
    static final String GA = "ga";

    /**
     * Snapshot.
     */
    static final String SNAPSHOT = "snapshot";

    /**
     * SP.
     */
    static final String SP = "sp";

    /**
     * Special names.
     */
    static final List<String> NAMES = List.of(ALPHA, BETA, MILESTONE, RC, SNAPSHOT, SP);

    /**
     * SP Index.
     */
    static final int SP_INDEX = 5;
}

