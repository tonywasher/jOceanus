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
package net.sourceforge.joceanus.themis.xanalysis.parser.decl;

import com.github.javaparser.ast.body.BodyDeclaration;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisDeclarationInstance;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;

/**
 * Analysis BodyType Parser.
 */
public final class ThemisXAnalysisDeclParser {
    /**
     * Private Constructor.
     */
    private ThemisXAnalysisDeclParser() {
    }

    /**
     * Parse a declaration.
     *
     * @param pParser the parser
     * @param pDecl   the declaration
     * @return the parsed declaration
     * @throws OceanusException on error
     */
    public static ThemisXAnalysisDeclarationInstance parseDeclaration(final ThemisXAnalysisParserDef pParser,
                                                                      final BodyDeclaration<?> pDecl) throws OceanusException {
        /* Handle null Declaration */
        if (pDecl == null) {
            return null;
        }

        /* Create appropriate declaration */
        switch (ThemisXAnalysisDeclaration.determineDeclaration(pParser, pDecl)) {
            case ANNOTATION:
                return new ThemisXAnalysisDeclAnnotation(pParser, pDecl.asAnnotationDeclaration());
            case ANNOTATIONMEMBER:
                return new ThemisXAnalysisDeclAnnotationMember(pParser, pDecl.asAnnotationMemberDeclaration());
            case CLASSINTERFACE:
                return new ThemisXAnalysisDeclClassInterface(pParser, pDecl.asClassOrInterfaceDeclaration());
            case COMPACT:
                return new ThemisXAnalysisDeclCompact(pParser, pDecl.asCompactConstructorDeclaration());
            case CONSTRUCTOR:
                return new ThemisXAnalysisDeclConstructor(pParser, pDecl.asConstructorDeclaration());
            case ENUM:
                return new ThemisXAnalysisDeclEnum(pParser, pDecl.asEnumDeclaration());
            case ENUMVALUE:
                return new ThemisXAnalysisDeclEnumValue(pParser, pDecl.asEnumConstantDeclaration());
            case FIELD:
                return new ThemisXAnalysisDeclField(pParser, pDecl.asFieldDeclaration());
            case INITIALIZER:
                return new ThemisXAnalysisDeclInitializer(pParser, pDecl.asInitializerDeclaration());
            case METHOD:
                return new ThemisXAnalysisDeclMethod(pParser, pDecl.asMethodDeclaration());
            case RECORD:
                return new ThemisXAnalysisDeclRecord(pParser, pDecl.asRecordDeclaration());
            default:
                throw pParser.buildException("Unsupported Declaration Type", pDecl);
        }
    }
}
