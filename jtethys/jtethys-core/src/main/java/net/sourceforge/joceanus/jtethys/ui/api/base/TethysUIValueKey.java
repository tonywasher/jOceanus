/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.api.base;

/**
 * ValueSet Keys.
 */
public final class TethysUIValueKey {
    /**
     * The base style.
     */
    private static final String STYLE_BASE = "-jtethys";

    /**
     * The color style.
     */
    private static final String STYLE_COLOR = STYLE_BASE + "-color";

    /**
     * The font style.
     */
    private static final String STYLE_FONT = STYLE_BASE + "-font";

    /**
     * The standard colour name.
     */
    public static final String COLOR_STANDARD = STYLE_COLOR + "-standard";

    /**
     * The error colour name.
     */
    public static final String COLOR_ERROR = STYLE_COLOR + "-error";

    /**
     * The background colour name.
     */
    public static final String COLOR_BACKGROUND = STYLE_COLOR + "-background";

    /**
     * The disabled colour name.
     */
    public static final String COLOR_DISABLED = STYLE_COLOR + "-disabled";

    /**
     * The progress colour name.
     */
    public static final String COLOR_PROGRESS = STYLE_COLOR + "-progress";

    /**
     * The zebra colour name.
     */
    public static final String COLOR_ZEBRA = STYLE_COLOR + "-zebra";

    /**
     * The changed colour name.
     */
    public static final String COLOR_CHANGED = STYLE_COLOR + "-changed";

    /**
     * The link colour name.
     */
    public static final String COLOR_LINK = STYLE_COLOR + "-link";

    /**
     * The value colour name.
     */
    public static final String COLOR_VALUE = STYLE_COLOR + "-data";

    /**
     * The negative colour name.
     */
    public static final String COLOR_NEGATIVE = STYLE_COLOR + "-negative";

    /**
     * The security colour name.
     */
    public static final String COLOR_SECURITY = STYLE_COLOR + "-security";

    /**
     * The header colour name.
     */
    public static final String COLOR_HEADER = STYLE_COLOR + "-header";

    /**
     * The standard font family name.
     */
    public static final String FONT_STANDARD = STYLE_FONT + "-standard";

    /**
     * The standard font pitch.
     */
    public static final String FONT_PITCH = STYLE_FONT + "-pitch";

    /**
     * The numeric font family name.
     */
    public static final String FONT_NUMERIC = STYLE_FONT + "-numeric";

    /**
     * Standard colour default.
     */
    public static final String DEFAULT_COLOR_STANDARD = "#000000";

    /**
     * Error colour default.
     */
    public static final String DEFAULT_COLOR_ERROR = "#ff0000";

    /**
     * Background colour default.
     */
    public static final String DEFAULT_COLOR_BACKGROUND = "#f5f5f5";

    /**
     * Disabled colour default.
     */
    public static final String DEFAULT_COLOR_DISABLED = "#778899";

    /**
     * Zebra colour default.
     */
    public static final String DEFAULT_COLOR_ZEBRA = "#e3e4fa";

    /**
     * Changed colour default.
     */
    public static final String DEFAULT_COLOR_CHANGED = "#8b008b";

    /**
     * Progress colour default.
     */
    public static final String DEFAULT_COLOR_PROGRESS = "#32cd32";

    /**
     * Link colour default.
     */
    public static final String DEFAULT_COLOR_LINK = "#c71585";

    /**
     * Value colour default.
     */
    public static final String DEFAULT_COLOR_VALUE = "#0000ff";

    /**
     * Negative colour default.
     */
    public static final String DEFAULT_COLOR_NEGATIVE = "#b22222";

    /**
     * Security colour default.
     */
    public static final String DEFAULT_COLOR_SECURITY = "#daa520";

    /**
     * Header colour default.
     */
    public static final String DEFAULT_COLOR_HEADER = "#0000cd";

    /**
     * Standard Font default.
     */
    public static final String DEFAULT_FONT_STANDARD = "Arial";

    /**
     * Numeric Font default.
     */
    public static final String DEFAULT_FONT_NUMERIC = "Courier";

    /**
     * Font Pitch default.
     */
    public static final String DEFAULT_FONT_PITCH = "12";

    /**
     * Private constructor.
     */
    private TethysUIValueKey() {
    }
}
