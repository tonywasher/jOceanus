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
package io.github.tonywasher.joceanus.themis.xanalysis.parser.expr;

import com.github.javaparser.ast.expr.SwitchExpr;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;

import java.util.List;

/**
 * Switch Expression Declaration.
 */
public class ThemisXAnalysisExprSwitch
        extends ThemisXAnalysisBaseExpression<SwitchExpr> {
    /**
     * The selector.
     */
    private final ThemisXAnalysisExpressionInstance theSelector;

    /**
     * The cases.
     */
    private final List<ThemisXAnalysisNodeInstance> theCases;

    /**
     * Constructor.
     *
     * @param pParser     the parser
     * @param pExpression the expression
     * @throws OceanusException on error
     */
    ThemisXAnalysisExprSwitch(final ThemisXAnalysisParserDef pParser,
                              final SwitchExpr pExpression) throws OceanusException {
        super(pParser, pExpression);
        theSelector = pParser.parseExpression(pExpression.getSelector());
        theCases = pParser.parseNodeList(pExpression.getEntries());
    }

    /**
     * Obtain the selector.
     *
     * @return the selector
     */
    public ThemisXAnalysisExpressionInstance getSelector() {
        return theSelector;
    }

    /**
     * Obtain the cases.
     *
     * @return the cases
     */
    public List<ThemisXAnalysisNodeInstance> getCases() {
        return theCases;
    }
}
