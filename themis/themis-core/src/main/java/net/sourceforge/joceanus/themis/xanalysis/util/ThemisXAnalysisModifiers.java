/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.themis.xanalysis.util;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;

/**
 * Modifiers for an object.
 */
public class ThemisXAnalysisModifiers {
    /**
     * The list of Modifiers.
     */
    private final NodeList<Modifier> theModifiers;

    /**
     * Constructor.
     * @param pModifiers the Modifiers
     */
    public ThemisXAnalysisModifiers(final NodeList<Modifier> pModifiers) {
        theModifiers = pModifiers;
    }

    /**
     * Has private modifier?
     * @return true/false
     */
    boolean hasPrivate() {
        return theModifiers.contains(Modifier.privateModifier());
    }

    @Override
    public String toString() {
        return theModifiers.toString();
    }
}
