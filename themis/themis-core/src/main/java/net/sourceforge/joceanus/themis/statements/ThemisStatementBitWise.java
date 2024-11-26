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
package net.sourceforge.joceanus.themis.statements;

import net.sourceforge.joceanus.themis.statements.ThemisStatementAssign.ThemisStatementAssignModifier;

/**
 * BitWise Operators.
 */
public enum ThemisStatementBitWise
    implements ThemisStatementOperator, ThemisStatementAssignModifier {
    /**
     * BitWise OR.
     */
    OR("|"),

    /**
     * BitWise AND.
     */
    AND("&"),

    /**
     * BitWise XOR.
     */
    XOR("^");

    /**
     * The sequence.
     */
    private final String theSequence;

    /**
     * Constructor.
     * @param pSequence the sequence
     */
    ThemisStatementBitWise(final String pSequence) {
        theSequence = pSequence;
    }

    @Override
    public String toString() {
        return theSequence;
    }
}