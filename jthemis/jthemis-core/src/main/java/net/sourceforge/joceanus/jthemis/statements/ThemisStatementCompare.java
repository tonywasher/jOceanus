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
package net.sourceforge.joceanus.jthemis.statements;

/**
 * operators.
 */
public enum ThemisStatementCompare
    implements ThemisStatementOperator {
    /**
     * InstanceOf.
     */
    INSTANCEOF("instanceof"),

    /**
     * COMPAREEQ.
     */
    EQ("=="),

    /**
     * COMPARENE.
     */
    NE("!="),

    /**
     * COMPAREGE.
     */
    GE(">="),

    /**
     * COMPAREGT.
     */
    GT(">"),

    /**
     * COMPARELE.
     */
    LE("<="),

    /**
     * COMPARELT.
     */
    LT("<");

    /**
     * The sequence.
     */
    private final String theSequence;

    /**
     * Constructor.
     * @param pSequence the sequence
     */
    ThemisStatementCompare(final String pSequence) {
        theSequence = pSequence;
    }

    @Override
    public String toString() {
        return theSequence;
    }
}
