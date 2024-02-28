/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2024 Tony Washer
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
 * KeyWords.
 */
public enum ThemisAnalysisKeyWord {
    /**
     * Package.
     */
    PACKAGE("package"),

    /**
     * Import.
     */
    IMPORT("import"),

    /**
     * Class.
     */
    CLASS("class"),

    /**
     * Enum.
     */
    ENUM("enum"),

    /**
     * Interface.
     */
    INTERFACE("interface"),

    /**
     * Annotation.
     */
    ANNOTATION("@interface"),

    /**
     * Extends.
     */
    EXTENDS("extends"),

    /**
     * Implements.
     */
    IMPLEMENTS("implements"),

    /**
     * Super.
     */
    SUPER("super"),

    /**
     * Throws.
     */
    THROWS("throws"),

    /**
     * If.
     */
    IF("if"),

    /**
     * Else.
     */
    ELSE("else"),

    /**
     * For.
     */
    FOR("for"),

    /**
     * Break.
     */
    BREAK("break"),

    /**
     * Continue.
     */
    CONTINUE("continue"),

    /**
     * Yield.
     */
    YIELD("yield"),

    /**
     * Do.
     */
    DO("do"),

    /**
     * While.
     */
    WHILE("while"),

    /**
     * Try.
     */
    TRY("try"),

    /**
     * Catch.
     */
    CATCH("catch"),

    /**
     * FINALLY.
     */
    FINALLY("finally"),

    /**
     * RETURN.
     */
    RETURN("return"),

    /**
     * THROW.
     */
    THROW("throw"),

    /**
     * NEW.
     */
    NEW("new"),

    /**
     * Switch.
     */
    SWITCH("switch"),

    /**
     * Case.
     */
    CASE("case"),

    /**
     * Default.
     */
    DEFAULT("default");

    /**
     * The keyWord.
     */
    private final String theKeyWord;

    /**
     * Constructor.
     * @param pKeyWord the keyWord
     */
    ThemisAnalysisKeyWord(final String pKeyWord) {
        theKeyWord = pKeyWord;
    }

    /**
     * Obtain the keyWord.
     * @return the keyWord
     */
    String getKeyWord() {
        return theKeyWord;
    }

    @Override
    public String toString() {
        return getKeyWord();
    }
}
