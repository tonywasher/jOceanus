/*
 * Oceanus: Java Utilities
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

package io.github.tonywasher.joceanus.oceanus.decimal;

/**
 * Decimal constants.
 *
 * @author Tony Washer
 */
public final class OceanusDecimalConstants {
    /**
     * Private constructor.
     */
    private OceanusDecimalConstants() {
    }

    /**
     * The Blank character.
     */
    public static final char CHAR_BLANK = ' ';

    /**
     * The Zero character.
     */
    public static final char CHAR_ZERO = '0';

    /**
     * The Minus character.
     */
    public static final char CHAR_MINUS = '-';

    /**
     * The Group character.
     */
    public static final char CHAR_GROUP = ',';

    /**
     * The Decimal character.
     */
    public static final String STR_DEC = ".";

    /**
     * The Currency separator.
     */
    public static final String STR_CURRSEP = ":";

    /**
     * PerCent adjustment.
     */
    public static final int ADJUST_PERCENT = 2;

    /**
     * PerMille adjustment.
     */
    public static final int ADJUST_PERMILLE = 3;
}
