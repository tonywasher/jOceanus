/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2020 Tony Washer
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
 * Generic construct.
 */
public class ThemisAnalysisGeneric {
    /**
     * Open generic.
     */
    static final char GENERIC_OPEN = '<';

    /**
     * Close generic.
     */
    static final char GENERIC_CLOSE = '>';

    /**
     * Start generic.
     */
    static final String GENERIC_START = Character.toString(GENERIC_OPEN);

    /**
     * End generic.
     */
    static final String GENERIC_END = Character.toString(GENERIC_CLOSE);

    /**
     * The contents of the generic.
     */
    private final String theContents;

    /**
     * Constructor.
     * @param pLine the line
     */
    ThemisAnalysisGeneric(final ThemisAnalysisLine pLine) {
        pLine.stripStartSequence(GENERIC_START);
        theContents = pLine.stripUpToChar(GENERIC_CLOSE);
        pLine.stripLeadingWhiteSpace();
    }

    /**
     * Is the line a generic?
     * @param pLine the line
     * @return true/false
     */
    static boolean isGeneric(final ThemisAnalysisLine pLine) {
        /* If we are started with a GENERIC_OPEN */
        return pLine.startsWithSequence(GENERIC_START);
    }

    @Override
    public String toString() {
        return GENERIC_START + theContents + GENERIC_END;
    }
}
