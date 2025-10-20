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

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.TypeParameter;
import net.sourceforge.joceanus.themis.xanalysis.solver.reflect.ThemisXAnalysisReflectBaseUtils.ThemisXAnalysisReflectParameter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;

/**
 * Solver utilities.
 */
public final class ThemisXAnalysisReflectUtils {
    /**
     * Private constructor.
     */
    private ThemisXAnalysisReflectUtils() {
    }

    /**
     * Build modifiers.
     * @param pClass the class
     * @return the modifier list
     */
    static NodeList<Modifier> buildModifiers(final Class<?> pClass) {
        final NodeList<Modifier> myModifiers = buildModifiers(pClass.getModifiers());
        if (pClass.isSealed()) {
            myModifiers.add(Modifier.sealedModifier());
        }
        return myModifiers;
    }

    /**
     * Build modifiers.
     * @param pConstructor the constructor
     * @return the modifier list
     */
    static NodeList<Modifier> buildModifiers(final Constructor<?> pConstructor) {
        return buildModifiers(pConstructor.getModifiers());
    }

    /**
     * Build modifiers.
     * @param pMethod the method
     * @return the modifier list
     */
    static NodeList<Modifier> buildModifiers(final Method pMethod) {
        return buildModifiers(pMethod.getModifiers());
    }

    /**
     * Build modifiers.
     * @param pFlags the flags
     * @return the modifier list
     */
    private static NodeList<Modifier> buildModifiers(final int pFlags) {
        final NodeList<Modifier> myModifiers = new NodeList<>();
        if (ThemisXAnalysisReflectBaseUtils.isPublic(pFlags)) {
            myModifiers.add(Modifier.publicModifier());
        }
        if (ThemisXAnalysisReflectBaseUtils.isProtected(pFlags)) {
            myModifiers.add(Modifier.protectedModifier());
        }
        if (ThemisXAnalysisReflectBaseUtils.isPrivate(pFlags)) {
            myModifiers.add(Modifier.privateModifier());
        }
        if (ThemisXAnalysisReflectBaseUtils.isStatic(pFlags)) {
            myModifiers.add(Modifier.staticModifier());
        }
        if (ThemisXAnalysisReflectBaseUtils.isFinal(pFlags)) {
            myModifiers.add(Modifier.finalModifier());
        }
        if (ThemisXAnalysisReflectBaseUtils.isSynchronized(pFlags)) {
            myModifiers.add(Modifier.synchronizedModifier());
        }
        if (ThemisXAnalysisReflectBaseUtils.isVolatile(pFlags)) {
            myModifiers.add(Modifier.volatileModifier());
        }
        if (ThemisXAnalysisReflectBaseUtils.isTransient(pFlags)) {
            myModifiers.add(Modifier.transientModifier());
        }
        if (ThemisXAnalysisReflectBaseUtils.isNative(pFlags)) {
            myModifiers.add(Modifier.nativeModifier());
        }
        if (ThemisXAnalysisReflectBaseUtils.isAbstract(pFlags)) {
            myModifiers.add(Modifier.abstractModifier());
        }
        return myModifiers;
    }

    /**
     * Build the extended list.
     * @param pClass the class
     * @return the extended list
     */
    static NodeList<ClassOrInterfaceType> buildExtended(final Class<?> pClass) {
        final Class<?> mySuperClass = pClass.getSuperclass();
        final NodeList<ClassOrInterfaceType> myExtends = new NodeList<>();
        if (mySuperClass != null) {
            final ClassOrInterfaceType myType = buildClassOrInterface(mySuperClass);
            myExtends.add(myType);
        }
        return myExtends;
    }

    /**
     * Build the implemented list.
     * @param pClass the class
     * @return the implemented list
     */
    static NodeList<ClassOrInterfaceType> buildImplements(final Class<?> pClass) {
        final NodeList<ClassOrInterfaceType> myImplements = new NodeList<>();
        for (Class<?> myClass: pClass.getInterfaces()) {
            final ClassOrInterfaceType myType = buildClassOrInterface(myClass);
            myImplements.add(myType);
        }
        return myImplements;
    }

    /**
     * Build the thrown list.
     * @param pConstructor the constructor
     * @return the thrown list
     */
    static NodeList<ReferenceType> buildThrown(final Constructor<?> pConstructor) {
        final NodeList<ReferenceType> myImplements = new NodeList<>();
        for (Class<?> myClass: pConstructor.getExceptionTypes()) {
            final ClassOrInterfaceType myType = buildClassOrInterface(myClass);
            myImplements.add(myType);
        }
        return myImplements;
    }

    /**
     * Build the thrown list.
     * @param pMethod the method
     * @return the thrown list
     */
    static NodeList<ReferenceType> buildThrown(final Method pMethod) {
        final NodeList<ReferenceType> myImplements = new NodeList<>();
        for (Class<?> myClass: pMethod.getExceptionTypes()) {
            final ClassOrInterfaceType myType = buildClassOrInterface(myClass);
            myImplements.add(myType);
        }
        return myImplements;
    }

    /**
     * Build the parameters.
     * @param pExecutable the executable
     * @return the parameter list
     */
    static NodeList<Parameter> buildParameters(final Executable pExecutable) {
        final NodeList<Parameter> myParameters = new NodeList<>();
        for (ThemisXAnalysisReflectParameter myParam : ThemisXAnalysisReflectBaseUtils.getParameters(pExecutable.getParameters())) {
            final Parameter myParm = new Parameter();
            myParm.setName(new SimpleName(myParam.getName()));
            myParm.setModifiers(buildModifiers(myParam.getModifiers()));
            myParm.setType(buildClassOrInterface(myParam.getType()));
            myParm.setVarArgs(myParam.isVarArgs());
            myParameters.add(myParm);
        }
        return myParameters;
    }

    /**
     * Build the classOrInterface reference.
     * @param pClass the class
     * @return the reference
     */
    static ClassOrInterfaceType buildClassOrInterface(final Class<?> pClass) {
        final ClassOrInterfaceType myType = ThemisXAnalysisReflectBaseUtils.createTypeForName(pClass.getCanonicalName());
        final NodeList<TypeParameter> myParams = ThemisXAnalysisReflectBaseUtils.buildTypeParams(pClass.getTypeParameters());
        myType.setTypeArguments(typeList(myParams));
        return myType;
    }

    /**
     * Map typeParameter list to type list.
     * @param pSource the source list
     * @return the type list
     */
    private static NodeList<Type> typeList(final NodeList<TypeParameter> pSource) {
        final NodeList<Type> myTypes = new NodeList<>();
        myTypes.addAll(pSource);
        return myTypes;
    }
}
