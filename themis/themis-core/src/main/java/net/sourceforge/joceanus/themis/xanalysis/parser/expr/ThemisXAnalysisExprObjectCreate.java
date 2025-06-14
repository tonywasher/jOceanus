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
package net.sourceforge.joceanus.themis.xanalysis.parser.expr;

import com.github.javaparser.ast.expr.ObjectCreationExpr;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;

import java.util.List;

/**
 * Object Creation Expression Declaration.
 */
public class ThemisXAnalysisExprObjectCreate
        extends ThemisXAnalysisBaseExpression<ObjectCreationExpr> {
    /**
     * The type.
     */
    private final ThemisXAnalysisTypeInstance theType;

    /**
     * The arguments.
     */
    private final List<ThemisXAnalysisExpressionInstance> theArgs;

    /**
     * The scope.
     */
    private final ThemisXAnalysisExpressionInstance theScope;

    /**
     * The type arguments.
     */
    private final List<ThemisXAnalysisTypeInstance> theTypeArguments;

    /**
     * The anonymous class.
     */
    private final ThemisXAnalysisDeclarationInstance theAnon;

    /**
     * The method instance.
     */
    private ThemisXAnalysisMethodInstance theMethodInstance;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pExpression the expression
     * @throws OceanusException on error
     */
    ThemisXAnalysisExprObjectCreate(final ThemisXAnalysisParserDef pParser,
                                    final ObjectCreationExpr pExpression) throws OceanusException {
        super(pParser, pExpression);
        theType = pParser.parseType(pExpression.getType());
        theArgs = pParser.parseExprList(pExpression.getArguments());
        theScope = pParser.parseExpression(pExpression.getScope().orElse(null));
        theTypeArguments = pParser.parseTypeList(pExpression.getTypeArguments().orElse(null));
        theAnon = pExpression.getAnonymousClassBody().isPresent()
                    ? pParser.parseDeclaration(new ThemisXAnalysisExprAnonClass(pExpression))
                    : null;
    }

    /**
     * Obtain the Type.
     * @return the type
     */
    public ThemisXAnalysisTypeInstance getType() {
        return theType;
    }

    /**
     * Obtain the arguments.
     * @return the arguments
     */
    public List<ThemisXAnalysisExpressionInstance> getArgs() {
        return theArgs;
    }

    /**
     * Obtain the scope.
     * @return the scope
     */
    public ThemisXAnalysisExpressionInstance getScope() {
        return theScope;
    }

    /**
     * Obtain the type arguments.
     * @return the arguments
     */
    public List<ThemisXAnalysisTypeInstance> getTypeArguments() {
        return theTypeArguments;
    }

    /**
     * Obtain the body.
     * @return the body
     */
    public ThemisXAnalysisDeclarationInstance getAnonymousClass() {
        return theAnon;
    }

    /**
     * The class instance.
     */
    private ThemisXAnalysisClassInstance theClassInstance;

    /**
     * Obtain the class instance.
     * @return the class instance
     */
    public ThemisXAnalysisClassInstance getClassInstance() {
        return theClassInstance;
    }

    /**
     * Set the class instance.
     * @param pClassInstance the class instance
     */
    public void setClassInstance(final ThemisXAnalysisClassInstance pClassInstance) {
        theClassInstance = pClassInstance;
    }

    /**
     * Obtain the method instance.
     * @return the method instance
     */
    public ThemisXAnalysisMethodInstance getMethodInstance() {
        return theMethodInstance;
    }

    /**
     * Set the method instance.
     * @param pMethodInstance the method instance
     */
    public void setMethodInstance(final ThemisXAnalysisMethodInstance pMethodInstance) {
        theMethodInstance = pMethodInstance;
    }
}
