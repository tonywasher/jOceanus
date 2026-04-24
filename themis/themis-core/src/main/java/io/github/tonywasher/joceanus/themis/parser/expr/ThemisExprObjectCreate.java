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
package io.github.tonywasher.joceanus.themis.parser.expr;

import com.github.javaparser.ast.expr.ObjectCreationExpr;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisParserDef;

import java.util.List;

/**
 * Object Creation Expression Declaration.
 */
public class ThemisExprObjectCreate
        extends ThemisBaseExpression<ObjectCreationExpr> {
    /**
     * The type.
     */
    private final ThemisTypeInstance theType;

    /**
     * The arguments.
     */
    private final List<ThemisExpressionInstance> theArgs;

    /**
     * The scope.
     */
    private final ThemisExpressionInstance theScope;

    /**
     * The type arguments.
     */
    private final List<ThemisTypeInstance> theTypeArguments;

    /**
     * The anonymous class.
     */
    private final ThemisDeclarationInstance theAnon;

    /**
     * The method instance.
     */
    private ThemisMethodInstance theMethodInstance;

    /**
     * Constructor.
     *
     * @param pParser     the parser
     * @param pExpression the expression
     * @throws OceanusException on error
     */
    ThemisExprObjectCreate(final ThemisParserDef pParser,
                           final ObjectCreationExpr pExpression) throws OceanusException {
        super(pParser, pExpression);
        theType = pParser.parseType(pExpression.getType());
        theArgs = pParser.parseExprList(pExpression.getArguments());
        theScope = pParser.parseExpression(pExpression.getScope().orElse(null));
        theTypeArguments = pParser.parseTypeList(pExpression.getTypeArguments().orElse(null));
        theAnon = pExpression.getAnonymousClassBody().isPresent()
                ? pParser.parseDeclaration(new ThemisExprAnonClass(pExpression))
                : null;
    }

    /**
     * Obtain the Type.
     *
     * @return the type
     */
    public ThemisTypeInstance getType() {
        return theType;
    }

    /**
     * Obtain the arguments.
     *
     * @return the arguments
     */
    public List<ThemisExpressionInstance> getArgs() {
        return theArgs;
    }

    /**
     * Obtain the scope.
     *
     * @return the scope
     */
    public ThemisExpressionInstance getScope() {
        return theScope;
    }

    /**
     * Obtain the type arguments.
     *
     * @return the arguments
     */
    public List<ThemisTypeInstance> getTypeArguments() {
        return theTypeArguments;
    }

    /**
     * Obtain the body.
     *
     * @return the body
     */
    public ThemisDeclarationInstance getAnonymousClass() {
        return theAnon;
    }

    /**
     * The class instance.
     */
    private ThemisClassInstance theClassInstance;

    /**
     * Obtain the class instance.
     *
     * @return the class instance
     */
    public ThemisClassInstance getClassInstance() {
        return theClassInstance;
    }

    /**
     * Set the class instance.
     *
     * @param pClassInstance the class instance
     */
    public void setClassInstance(final ThemisClassInstance pClassInstance) {
        theClassInstance = pClassInstance;
    }

    /**
     * Obtain the method instance.
     *
     * @return the method instance
     */
    public ThemisMethodInstance getMethodInstance() {
        return theMethodInstance;
    }

    /**
     * Set the method instance.
     *
     * @param pMethodInstance the method instance
     */
    public void setMethodInstance(final ThemisMethodInstance pMethodInstance) {
        theMethodInstance = pMethodInstance;
    }
}
