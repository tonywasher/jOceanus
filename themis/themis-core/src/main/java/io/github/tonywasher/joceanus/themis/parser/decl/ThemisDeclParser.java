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
package io.github.tonywasher.joceanus.themis.parser.decl;

import com.github.javaparser.ast.body.BodyDeclaration;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisDeclarationInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisParserDef;

/**
 * Analysis BodyType Parser.
 */
public final class ThemisDeclParser {
    /**
     * Private Constructor.
     */
    private ThemisDeclParser() {
    }

    /**
     * Parse a declaration.
     *
     * @param pParser the parser
     * @param pDecl   the declaration
     * @return the parsed declaration
     * @throws OceanusException on error
     */
    public static ThemisDeclarationInstance parseDeclaration(final ThemisParserDef pParser,
                                                             final BodyDeclaration<?> pDecl) throws OceanusException {
        /* Handle null Declaration */
        if (pDecl == null) {
            return null;
        }

        /* Create appropriate declaration */
        return switch (ThemisDeclaration.determineDeclaration(pParser, pDecl)) {
            case ANNOTATION -> new ThemisDeclAnnotation(pParser, pDecl.asAnnotationDeclaration());
            case ANNOTATIONMEMBER -> new ThemisDeclAnnotationMember(pParser, pDecl.asAnnotationMemberDeclaration());
            case CLASSINTERFACE -> new ThemisDeclClassInterface(pParser, pDecl.asClassOrInterfaceDeclaration());
            case COMPACT -> new ThemisDeclCompact(pParser, pDecl.asCompactConstructorDeclaration());
            case CONSTRUCTOR -> new ThemisDeclConstructor(pParser, pDecl.asConstructorDeclaration());
            case ENUM -> new ThemisDeclEnum(pParser, pDecl.asEnumDeclaration());
            case ENUMVALUE -> new ThemisDeclEnumValue(pParser, pDecl.asEnumConstantDeclaration());
            case FIELD -> new ThemisDeclField(pParser, pDecl.asFieldDeclaration());
            case INITIALIZER -> new ThemisDeclInitializer(pParser, pDecl.asInitializerDeclaration());
            case METHOD -> new ThemisDeclMethod(pParser, pDecl.asMethodDeclaration());
            case RECORD -> new ThemisDeclRecord(pParser, pDecl.asRecordDeclaration());
            default -> throw pParser.buildException("Unsupported Declaration Type", pDecl);
        };
    }
}
