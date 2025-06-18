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
package net.sourceforge.joceanus.themis.xanalysis.solver.reflect;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.type.TypeParameter;

import java.lang.reflect.Constructor;

/**
 * Resolved constructor JavaParser representation.
 */
public class ThemisXAnalysisReflectConstructor
        extends ConstructorDeclaration {
    /**
     * The underlying method.
     */
    private final Constructor<?> theConstructor;

    /**
     * Constructor.
     * @param pConstructor the constructor definition.
     */
    ThemisXAnalysisReflectConstructor(final Constructor<?> pConstructor) {
        /* Store the method */
        theConstructor = pConstructor;

        /* Set the modifiers */
        setModifiers(ThemisXAnalysisReflectUtils.buildModifiers(theConstructor));

        /* Set Parameters */
        setParameters(ThemisXAnalysisReflectUtils.buildParameters(theConstructor));

        /* Build the thrown list */
        setThrownExceptions(ThemisXAnalysisReflectUtils.buildThrown(theConstructor));

        /* Map the type parameters */
        final NodeList<TypeParameter> myParams = ThemisXAnalysisReflectBaseUtils.buildTypeParams(theConstructor.getTypeParameters());
        setTypeParameters(myParams);
    }
}
