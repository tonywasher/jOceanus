/*
 * Themis: Java Project Framework
 * Copyright 2012-2026. Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.tonywasher.joceanus.themis.solver.reflect;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.type.TypeParameter;

import java.lang.reflect.Method;

/**
 * Resolved method JavaParser representation.
 */
public class ThemisReflectMethod
        extends MethodDeclaration {
    /**
     * The underlying method.
     */
    private final Method theMethod;

    /**
     * Constructor.
     *
     * @param pMethod the method definition.
     */
    ThemisReflectMethod(final Method pMethod) {
        /* Store the method */
        theMethod = pMethod;

        /* Set the name */
        setName(new SimpleName(theMethod.getName()));

        /* Set the modifiers */
        setModifiers(ThemisReflectUtils.buildModifiers(theMethod));

        /* Mark as default */
        setDefault(theMethod.isDefault());

        /* Map the type parameters */
        final NodeList<TypeParameter> myParams = ThemisReflectBaseUtils.buildTypeParams(theMethod.getTypeParameters());
        setTypeParameters(myParams);

        /* Set the return type */
        setType(ThemisReflectUtils.buildClassOrInterface(theMethod.getReturnType()));

        /* Set Parameters */
        setParameters(ThemisReflectUtils.buildParameters(theMethod));

        /* Build the thrown list */
        setThrownExceptions(ThemisReflectUtils.buildThrown(theMethod));
    }
}
