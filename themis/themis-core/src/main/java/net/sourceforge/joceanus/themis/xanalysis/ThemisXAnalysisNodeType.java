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
package net.sourceforge.joceanus.themis.xanalysis;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.InitializerDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

/**
 * Node type.
 */
public enum ThemisXAnalysisNodeType {
    /**
     * Class/Interface Declaration.
     */
    CLASSINTERFACEDECL,

    /**
     * Enum Definition.
     */
    ENUMDECL,

    /**
     * Record Definition.
     */
    RECORDDECL,

    /**
     * Modifier.
     */
    MODIFIER,

    /**
     * Class/Interface Reference.
     */
    CLASSINTERFACEREF,

    /**
     * Constructor Declaration.
     */
    CONSTRUCTORDECL,

    /**
     * Method Declaration.
     */
    METHODDECL,

    /**
     * Initializer.
     */
    INITIALIZER,

    /**
     * Field Declaration.
     */
    FIELDDECL,

    /**
     * Enum Value.
     */
    ENUMVAL,

    /**
     * Variable Declaration.
     */
    VARDECL,

    /**
     * Statement.
     */
    STATEMENT,

    /**
     * Unknown.
     */
    UNKNOWN;

    /**
     * Determine Unit Type.
     * @param pUnit the compilation unit
     * @return the unitType
     */
    public static ThemisXAnalysisNodeType determineUnitType(final Node pUnit) {
        if (pUnit instanceof TypeDeclaration<?> myType) {
            if (myType.isClassOrInterfaceDeclaration()) {
                return CLASSINTERFACEDECL;
            }
            if (myType.isEnumDeclaration()) {
                return ENUMDECL;
            }
            if (myType.isRecordDeclaration()) {
                return RECORDDECL;
            }
        }
        if (pUnit instanceof Modifier) {
            return MODIFIER;
        }
        if (pUnit instanceof ClassOrInterfaceType) {
            return CLASSINTERFACEREF;
        }
        if (pUnit instanceof ConstructorDeclaration) {
            return CONSTRUCTORDECL;
        }
        if (pUnit instanceof MethodDeclaration) {
            return METHODDECL;
        }
        if (pUnit instanceof InitializerDeclaration) {
            return INITIALIZER;
        }
        if (pUnit instanceof FieldDeclaration) {
            return FIELDDECL;
        }
        if (pUnit instanceof EnumConstantDeclaration) {
            return ENUMVAL;
        }
        if (pUnit instanceof VariableDeclarator) {
            return VARDECL;
        }
        return UNKNOWN;
    }
}
