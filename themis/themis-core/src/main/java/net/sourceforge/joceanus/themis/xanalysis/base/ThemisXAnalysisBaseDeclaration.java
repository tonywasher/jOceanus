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
package net.sourceforge.joceanus.themis.xanalysis.base;

import com.github.javaparser.ast.body.BodyDeclaration;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisDeclarationInstance;

/**
 * Declaration Base Class.
 * @param <T> the Declaration
 */
public abstract class ThemisXAnalysisBaseDeclaration<T extends BodyDeclaration<T>>
        implements ThemisXAnalysisDeclarationInstance {
    /**
     * The declaration.
     */
    private final T theDecl;

    /**
     * Constructor.
     * @param pDecl the declaration
     */
    protected ThemisXAnalysisBaseDeclaration(final T pDecl) {
        theDecl = pDecl;
    }

    /**
     * Obtain the declaration.
     * @return the declaration
     */
    public T getDeclaration() {
        return theDecl;
    }

    @Override
    public String toString() {
        return theDecl.toString();
    }
}
