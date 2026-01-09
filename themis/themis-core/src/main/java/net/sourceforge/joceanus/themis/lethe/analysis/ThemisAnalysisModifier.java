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
package net.sourceforge.joceanus.themis.lethe.analysis;

/**
 * Modifiers.
 */
public enum ThemisAnalysisModifier {
    /**
     * Private.
     */
    PRIVATE("private"),

    /**
     * Protected.
     */
    PROTECTED("protected"),

    /**
     * Public.
     */
    PUBLIC("public"),

    /**
     * Static.
     */
    STATIC("static"),

    /**
     * Final.
     */
    FINAL("final"),

    /**
     * Abstract.
     */
    ABSTRACT("abstract"),

    /**
     * Synchronized.
     */
    SYNCHRONIZED("synchronized"),

    /**
     * DEFAULT.
     */
    DEFAULT("default"),

    /**
     * Native.
     */
    NATIVE("native"),

    /**
     * Volatile.
     */
    VOLATILE("volatile"),

    /**
     * Transient.
     */
    TRANSIENT("transient");

    /**
     * The modifier.
     */
    private final String theModifier;

    /**
     * Constructor.
     * @param pModifier the modifier
     */
    ThemisAnalysisModifier(final String pModifier) {
        theModifier = pModifier;
    }

    /**
     * Obtain the modifier.
     * @return the modifier
     */
    String getModifier() {
        return theModifier;
    }

    /**
     * Obtain modifier for token.
     * @param pToken the token
     * @return the modifier (or null)
     */
    static ThemisAnalysisModifier findModifier(final String pToken) {
        /* Loop through the modifiers */
        for (ThemisAnalysisModifier myModifier : values())    {
            /* If we found a modifier */
            if (pToken.equals(myModifier.getModifier())) {
                return myModifier;
            }
        }

        /* Not found */
        return null;
    }

    @Override
    public String toString() {
        return getModifier();
    }
}
