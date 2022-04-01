/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2022 Tony Washer
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

/**
 * Character constants.
 */
public final class ThemisAnalysisChar {
    /**
     * Private constructor.
     */
    private ThemisAnalysisChar() {
    }

    /**
     * The lineFeed character.
     */
    static final char LF = '\n';

    /**
     * The carriageReturn character.
     */
    static final char CR = '\r';

    /**
     * Case terminator.
     */
    static final char COLON = ':';

    /**
     * Statement separator.
     */
    public static final char COMMA = ',';

    /**
     * Statement terminator.
     */
    static final char SEMICOLON = ';';

    /**
     * Period separator.
     */
    public static final char PERIOD = '.';

    /**
     * The annotation character.
     */
    static final char ANNOTATION = '@';

    /**
     * The null character.
     */
    static final char NULL = (char) 0;

    /**
     * The line comment character.
     */
    static final char COMMENT = '/';

    /**
     * The escape character.
     */
    static final char ESCAPE = '\\';

    /**
     * The equals character.
     */
    public static final char EQUAL = '=';

    /**
     * The single quote character.
     */
    static final char SINGLEQUOTE = '\'';

    /**
     * The double quote character.
     */
    static final char DOUBLEQUOTE = '"';

    /**
     * Open body.
     */
    static final char BRACE_OPEN = '{';

    /**
     * Close body.
     */
    static final char BRACE_CLOSE = '}';

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
    static final char GENERIC_OPEN = '<';

    /**
     * Close generic.
     */
    static final char GENERIC_CLOSE = '>';

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
    static final char QUESTION = '?';

    /**
     * And symbol.
     */
    static final char AND = '&';

    /**
     * Or symbol.
     */
    static final char OR = '|';
}
