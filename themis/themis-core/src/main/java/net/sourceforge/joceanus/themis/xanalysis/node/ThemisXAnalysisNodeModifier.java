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
package net.sourceforge.joceanus.themis.xanalysis.node;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Modifier.Keyword;

/**
 * Import.
 */
public class ThemisXAnalysisNodeModifier
        extends ThemisXAnalysisBaseNode<Modifier> {
    /**
     * The Keyword.
     */
    private final Keyword theKeyword;

    /**
     * Constructor.
     * @param pModifier the modifier
     */
    ThemisXAnalysisNodeModifier(final Modifier pModifier) {
        super(pModifier);
        theKeyword = pModifier.getKeyword();
    }

    /**
     * Obtain the keyword.
     * @return the keyword
     */
    public Keyword getKeyword() {
        return theKeyword;
    }
}
