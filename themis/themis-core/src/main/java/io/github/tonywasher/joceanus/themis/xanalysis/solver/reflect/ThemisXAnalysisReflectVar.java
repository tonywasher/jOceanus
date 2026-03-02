/*
 * Themis: Java Project Framework
 * Copyright 2026. Tony Washer
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

package io.github.tonywasher.joceanus.themis.xanalysis.solver.reflect;

import com.github.javaparser.ast.body.VariableDeclarator;

import java.lang.reflect.Field;

/**
 * Resolved var JavaParser representation.
 */
public class ThemisXAnalysisReflectVar
        extends VariableDeclarator {
    /**
     * The underlying field.
     */
    private final Field theField;

    /**
     * Constructor.
     *
     * @param pField the field definition.
     */
    ThemisXAnalysisReflectVar(final Field pField) {
        /* Store the field */
        theField = pField;

        /* Set the return type */
        setName(theField.getName());
        setType(ThemisXAnalysisReflectUtils.buildClassOrInterface(theField.getType()));
    }
}
