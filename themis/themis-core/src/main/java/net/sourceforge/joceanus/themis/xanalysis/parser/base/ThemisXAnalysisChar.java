/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.themis.xanalysis.parser.base;

/**
 * Character constants.
 */
public final class ThemisXAnalysisChar {
    /**
     * Private constructor.
     */
    private ThemisXAnalysisChar() {
    }

    /**
     * The lineFeed character.
     */
    public static final char LF = '\n';

    /**
     * The carriageReturn character.
     */
    public static final char CR = '\r';

    /**
     * Case terminator.
     */
    public static final char COLON = ':';

    /**
     * Statement separator.
     */
    public static final char COMMA = ',';

    /**
     * Statement terminator.
     */
    public static final char SEMICOLON = ';';

    /**
     * Period separator.
     */
    public static final char PERIOD = '.';

    /**
     * The annotation character.
     */
    public static final char ANNOTATION = '@';

    /**
     * The null character.
     */
    public static final char NULL = (char) 0;

    /**
     * The line comment character.
     */
    public static final char COMMENT = '/';

    /**
     * The escape character.
     */
    public static final char ESCAPE = '\\';

    /**
     * The equals character.
     */
    public static final char EQUAL = '=';

    /**
     * The single quote character.
     */
    public static final char SINGLEQUOTE = '\'';

    /**
     * The double quote character.
     */
    public static final char DOUBLEQUOTE = '"';

    /**
     * Open body.
     */
    public static final char BRACE_OPEN = '{';

    /**
     * Close body.
     */
    public static final char BRACE_CLOSE = '}';

    /**
     * Open parenthesis.
     */
    public static final char PARENTHESIS_OPEN = '(';

    /**
     * Close parenthesis.
     */
    public static final char PARENTHESIS_CLOSE = ')';

    /**
     * Open generic.
     */
    public static final char GENERIC_OPEN = '<';

    /**
     * Close generic.
     */
    public static final char GENERIC_CLOSE = '>';

    /**
     * Start array.
     */
    public static final char ARRAY_OPEN = '[';

    /**
     * End array.
     */
    public static final char ARRAY_CLOSE = ']';

    /**
     * Blank.
     */
    public static final char BLANK = ' ';

    /**
     * QuestionMark.
     */
    public static final char QUESTION = '?';

    /**
     * And symbol.
     */
    public static final char AND = '&';

    /**
     * Or symbol.
     */
    public static final char OR = '|';

    /**
     * Dollar symbol.
     */
    public static final char DOLLAR = '$';

    /**
     * Hyphen.
     */
    public static final char HYPHEN = '-';
}
