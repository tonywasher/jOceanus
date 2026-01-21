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
package net.sourceforge.joceanus.themis.xanalysis.solver.reflect;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.exc.ThemisDataException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Solver member utilities. Split from Utils to prevent cycles.
 */
public final class ThemisXAnalysisReflectMemberUtils {
    /**
     * Private constructor.
     */
    private ThemisXAnalysisReflectMemberUtils() {
    }

    /**
     * Build the members.
     *
     * @param pClass the class
     * @return the constructor/method list
     */
    static NodeList<BodyDeclaration<?>> buildMembers(final Class<?> pClass) throws OceanusException {
        final NodeList<BodyDeclaration<?>> myMembers = new NodeList<>();
        try {
            for (Constructor<?> myConstructor : pClass.getConstructors()) {
                final ConstructorDeclaration myParsed = new ThemisXAnalysisReflectConstructor(myConstructor);
                myMembers.add(myParsed);
            }
            for (Method myMethod : pClass.getMethods()) {
                final MethodDeclaration myParsed = new ThemisXAnalysisReflectMethod(myMethod);
                myMembers.add(myParsed);
            }
        } catch (NoClassDefFoundError e) {
            throw new ThemisDataException("Method/Constructor not found", e);
        }
        return myMembers;
    }
}
