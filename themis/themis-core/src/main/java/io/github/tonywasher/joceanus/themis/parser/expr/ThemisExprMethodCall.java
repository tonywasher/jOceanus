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

import com.github.javaparser.ast.expr.MethodCallExpr;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisParserDef;

import java.util.List;

/**
 * MethodCall Expression Declaration.
 */
public class ThemisExprMethodCall
        extends ThemisBaseExpression<MethodCallExpr> {
    /**
     * The name.
     */
    private final ThemisNodeInstance theName;

    /**
     * The arguments.
     */
    private final List<ThemisExpressionInstance> theArguments;

    /**
     * The type.
     */
    private final List<ThemisTypeInstance> theTypeParams;

    /**
     * The pattern.
     */
    private final ThemisExpressionInstance theScope;

    /**
     * The class instance.
     */
    private ThemisClassInstance theClassInstance;

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
    ThemisExprMethodCall(final ThemisParserDef pParser,
                         final MethodCallExpr pExpression) throws OceanusException {
        super(pParser, pExpression);
        theName = pParser.parseNode(pExpression.getName());
        theArguments = pParser.parseExprList(pExpression.getArguments());
        theTypeParams = pParser.parseTypeList(pExpression.getTypeArguments().orElse(null));
        theScope = pParser.parseExpression(pExpression.getScope().orElse(null));
    }

    /**
     * Obtain the name.
     *
     * @return the name
     */
    public ThemisNodeInstance getName() {
        return theName;
    }

    /**
     * Obtain the arguments.
     *
     * @return the arguments
     */
    public List<ThemisExpressionInstance> getArguments() {
        return theArguments;
    }

    /**
     * Obtain the typeParams.
     *
     * @return the typeParams
     */
    public List<ThemisTypeInstance> getTypeParams() {
        return theTypeParams;
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
