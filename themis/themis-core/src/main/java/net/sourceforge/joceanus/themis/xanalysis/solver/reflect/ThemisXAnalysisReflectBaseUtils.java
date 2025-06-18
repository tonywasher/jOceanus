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
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.TypeParameter;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisChar;

import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

/**
 * Reflection utilities.
 * <p>
 * Separated from Utils due to name clashes between Reflection and JavaParser
 */
public final class ThemisXAnalysisReflectBaseUtils {
    /**
     * Private constructor.
     */
    private ThemisXAnalysisReflectBaseUtils() {
    }

    /**
     * Is the modifier public?
     * @param pFlags the flags
     * @return true/false
     */
    static boolean isPublic(final int pFlags) {
        return Modifier.isPublic(pFlags);
    }

    /**
     * Is the modifier private?
     * @param pFlags the flags
     * @return true/false
     */
    static boolean isPrivate(final int pFlags) {
        return Modifier.isPrivate(pFlags);
    }

    /**
     * Is the modifier protected?
     * @param pFlags the flags
     * @return true/false
     */
    static boolean isProtected(final int pFlags) {
        return Modifier.isProtected(pFlags);
    }

    /**
     * Is the modifier static?
     * @param pFlags the flags
     * @return true/false
     */
    static boolean isStatic(final int pFlags) {
        return Modifier.isStatic(pFlags);
    }

    /**
     * Is the modifier final?
     * @param pFlags the flags
     * @return true/false
     */
    static boolean isFinal(final int pFlags) {
        return Modifier.isFinal(pFlags);
    }

    /**
     * Is the modifier synchronized?
     * @param pFlags the flags
     * @return true/false
     */
    static boolean isSynchronized(final int pFlags) {
        return Modifier.isSynchronized(pFlags);
    }

    /**
     * Is the modifier volatile?
     * @param pFlags the flags
     * @return true/false
     */
    static boolean isVolatile(final int pFlags) {
        return Modifier.isVolatile(pFlags);
    }

    /**
     * Is the modifier transient?
     * @param pFlags the flags
     * @return true/false
     */
    static boolean isTransient(final int pFlags) {
        return Modifier.isTransient(pFlags);
    }

    /**
     * Is the modifier native?
     * @param pFlags the flags
     * @return true/false
     */
    static boolean isNative(final int pFlags) {
        return Modifier.isNative(pFlags);
    }

    /**
     * Is the modifier abstract?
     * @param pFlags the flags
     * @return true/false
     */
    static boolean isAbstract(final int pFlags) {
        return Modifier.isPublic(pFlags);
    }

    /**
     * Build typeParams.
     * @param pTypeVars the typeVariables
     * @return the typeParameters
     */
    static NodeList<TypeParameter> buildTypeParams(final TypeVariable<?>[] pTypeVars) {
        final NodeList<TypeParameter> myParams = new NodeList<>();
        for (TypeVariable<?> myVar : pTypeVars) {
            final TypeParameter myParam = new TypeParameter(myVar.getName());
            final NodeList<ClassOrInterfaceType> myBounds = new NodeList<>();
            for (Type myBoundType : myVar.getBounds()) {
                final ClassOrInterfaceType myBound = createTypeForName(myBoundType.getTypeName());
                myBounds.add(myBound);
            }
            myParam.setTypeBound(myBounds);
            myParams.add(myParam);
        }
        return myParams;
    }

    /**
     * Create type for Name.
     * @param pName the name to create a type for
     * @return the type.
     */
    static ClassOrInterfaceType createTypeForName(final String pName) {
        final int myLast = pName.lastIndexOf(ThemisXAnalysisChar.PERIOD);
        if (myLast != -1) {
            final ClassOrInterfaceType myScope = createTypeForName(pName.substring(0, myLast));
            return new ClassOrInterfaceType(myScope, pName.substring(myLast + 1));
        } else {
            final ClassOrInterfaceType myType = new ClassOrInterfaceType();
            myType.setName(new SimpleName(pName));
            return myType;
        }
    }

    /**
     * Obtain the parameters.
     * @param pParameters the parameters
     * @return the parameters
     */
    static List<ThemisXAnalysisReflectParameter> getParameters(final Parameter[] pParameters) {
        final List<ThemisXAnalysisReflectParameter> myList = new ArrayList<>();
        for (Parameter myParam : pParameters) {
            myList.add(new ThemisXAnalysisReflectParameter(myParam));
        }
        return myList;
    }

    /**
     * Simple class to encapsulate parameter.
     */
    static final class ThemisXAnalysisReflectParameter {
        /**
         * The parameter.
         */
        private final Parameter theParameter;

        /**
         * Constructor.
         * @param pParameter the reflection parameter
         */
        private ThemisXAnalysisReflectParameter(final Parameter pParameter) {
            theParameter = pParameter;
        }

        /**
         * Obtain the name.
         * @return the name
         */
        String getName() {
            return theParameter.getName();
        }

        /**
         * Obtain the modifiers.
         * @return the modifiers
         */
        int getModifiers() {
            return theParameter.getModifiers();
        }

        /**
         * Obtain the parameterType.
         * @return the type
         */
        Class<?> getType() {
            return theParameter.getType();
        }

        /**
         * is this parameter varArgs?
         * @return true/false
         */
        boolean isVarArgs() {
            return theParameter.isVarArgs();
        }
    }
}
