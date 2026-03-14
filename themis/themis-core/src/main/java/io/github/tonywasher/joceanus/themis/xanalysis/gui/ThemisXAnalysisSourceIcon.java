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

package io.github.tonywasher.joceanus.themis.xanalysis.gui;

import io.github.tonywasher.joceanus.tethys.api.base.TethysUIIconId;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisDeclarationInstance;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisExpressionInstance;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisNodeInstance;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisStatementInstance;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisTypeInstance;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.decl.ThemisXAnalysisDeclaration;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.expr.ThemisXAnalysisExpression;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.node.ThemisXAnalysisNode;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.stmt.ThemisXAnalysisStatement;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.type.ThemisXAnalysisType;

import java.io.InputStream;

/**
 * Icons for SourceTree entries.
 */
public enum ThemisXAnalysisSourceIcon
        implements TethysUIIconId {
    /**
     * The Red UpperCase A.
     */
    REDUPPERA("red/UpperA.png"),

    /**
     * The Red LowerCase A.
     */
    REDLOWERM("red/LowerM.png"),

    /**
     * The Red UpperCase C.
     */
    REDUPPERC("red/UpperC.png"),

    /**
     * The Red UpperCase E.
     */
    REDUPPERE("red/UpperE.png"),

    /**
     * The Red UpperCase F.
     */
    REDUPPERF("red/UpperF.png"),

    /**
     * The Red UpperCase I.
     */
    REDUPPERI("red/UpperI.png"),

    /**
     * The Red UpperCase M.
     */
    REDUPPERM("red/UpperM.png"),

    /**
     * The Red UpperCase N.
     */
    REDUPPERN("red/UpperN.png"),

    /**
     * The Red UpperCase P.
     */
    REDUPPERP("red/UpperP.png"),

    /**
     * The Red UpperCase R.
     */
    REDUPPERR("red/UpperR.png"),

    /**
     * The Red UpperCase V.
     */
    REDUPPERV("red/UpperV.png"),

    /**
     * The Pink UpperCase A.
     */
    PINKUPPERA("pink/UpperA.png"),

    /**
     * The Pink LowerCase C.
     */
    PINKLOWERC("pink/LowerC.png"),

    /**
     * The Pink UpperCase T.
     */
    PINKUPPERT("pink/UpperT.png"),

    /**
     * The Pink UpperCase C.
     */
    PINKUPPERC("pink/UpperC.png"),

    /**
     * The Pink UpperCase U.
     */
    PINKUPPERU("pink/UpperU.png"),

    /**
     * The Pink UpperCase I.
     */
    PINKUPPERI("pink/UpperI.png"),

    /**
     * The Pink UpperCase M.
     */
    PINKUPPERM("pink/UpperM.png"),

    /**
     * The Pink UpperCase N.
     */
    PINKUPPERN("pink/UpperN.png"),

    /**
     * The Pink UpperCase K.
     */
    PINKUPPERK("pink/UpperK.png"),

    /**
     * The Pink UpperCase P.
     */
    PINKUPPERP("pink/UpperP.png"),

    /**
     * The Pink LowerCase N.
     */
    PINKLOWERN("pink/LowerN.png"),

    /**
     * The Pink LowerCase P.
     */
    PINKLOWERP("pink/LowerP.png"),

    /**
     * The Pink UpperCase V.
     */
    PINKUPPERV("pink/UpperV.png"),

    /**
     * The Blue UpperCase A.
     */
    BLUEUPPERA("blue/UpperA.png"),

    /**
     * The Blue UpperCase C.
     */
    BLUEUPPERC("blue/UpperC.png"),

    /**
     * The Blue UpperCase I.
     */
    BLUEUPPERI("blue/UpperI.png"),

    /**
     * The Blue UpperCase T.
     */
    BLUEUPPERT("blue/UpperT.png"),

    /**
     * The Blue UpperCase P.
     */
    BLUEUPPERP("blue/UpperP.png"),

    /**
     * The Blue UpperCase U.
     */
    BLUEUPPERU("blue/UpperU.png"),

    /**
     * The Blue LowerCase k.
     */
    BLUELOWERK("blue/LowerrK.png"),

    /**
     * The Blue UpperCase V.
     */
    BLUEUPPERV("blue/UpperV.png"),

    /**
     * The Blue Lower v.
     */
    BLUELOWERV("blue/LowerV.png"),

    /**
     * The Blue UpperCase W.
     */
    BLUEUPPERW("blue/UpperW.png"),

    /**
     * The Green UpperCase C.
     */
    GREENUPPERC("green/UpperC.png"),

    /**
     * The Green LowerCase A.
     */
    GREENLOWERA("green/LowerA.png"),

    /**
     * The Green UpperCase C.
     */
    GREENLOWERC("green/LowerC.png"),

    /**
     * The Green LowerCase I.
     */
    GREENLOWERI("green/LowernI.png"),

    /**
     * The Green UpperCase A.
     */
    GREENUPPERA("green/UpperA.png"),

    /**
     * The Green UpperCase B.
     */
    GREENUPPERB("green/UpperB.png"),

    /**
     * The Green LowerCase B.
     */
    GREENLOWERB("green/LowerB.png"),

    /**
     * The Green LowerCase N.
     */
    GREENLOWERN("green/LowerN.png"),

    /**
     * The Green UpperCase D.
     */
    GREENUPPERD("green/UpperD.png"),

    /**
     * The Green UpperCase E.
     */
    GREENUPPERE("green/UpperE.png"),

    /**
     * The Green UpperCase F.
     */
    GREENUPPERF("green/UpperF.png"),

    /**
     * The Green UpperCase I.
     */
    GREENUPPERI("green/UpperI.png"),

    /**
     * The Green UpperCase L.
     */
    GREENUPPERL("green/UpperL.png"),

    /**
     * The Green LowerCase L.
     */
    GREENLOWERL("green/LowerL.png"),

    /**
     * The Green UpperCase M.
     */
    GREENUPPERM("green/UpperM.png"),

    /**
     * The Green LowerCase M.
     */
    GREENLOWERM("green/LowerM.png"),

    /**
     * The Green UpperCase N.
     */
    GREENUPPERN("green/UpperN.png"),

    /**
     * The Green UpperCase O.
     */
    GREENUPPERO("green/UpperO.png"),

    /**
     * The Green LowerCase S.
     */
    GREENLOWERS("green/LowerS.png"),

    /**
     * The Green LowerCase P.
     */
    GREENLOWERP("green/LowerP.png"),

    /**
     * The Green UpperCase S.
     */
    GREENUPPERS("green/UpperS.png"),

    /**
     * The Green UpperCase T.
     */
    GREENUPPERT("green/UpperT.png"),

    /**
     * The Green LowerCase T.
     */
    GREENLOWERT("green/LowerT.png"),

    /**
     * The Green LowerCase Y.
     */
    GREENLOWERY("green/LowerY.png"),

    /**
     * The Green UpperCase P.
     */
    GREENUPPERP("green/UpperP.png"),

    /**
     * The Green UpperCase U.
     */
    GREENUPPERU("green/UpperU.png"),

    /**
     * The Green UpperCase V.
     */
    GREENUPPERV("green/UpperV.png"),

    /**
     * The Orange UpperCase A.
     */
    ORANGEUPPERA("orange/UpperA.png"),

    /**
     * The Orange UpperCase B.
     */
    ORANGEUPPERB("orange/UpperB.png"),

    /**
     * The Orange LowerCase B.
     */
    ORANGELOWERB("orange/LowerB.png"),

    /**
     * The Orange UpperCase C.
     */
    ORANGEUPPERC("orange/UpperC.png"),

    /**
     * The Orange UpperCase N.
     */
    ORANGEUPPERN("orange/UpperN.png"),

    /**
     * The Orange LowerCase C.
     */
    ORANGELOWERC("orange/LowerC.png"),

    /**
     * The Orange UpperCase D.
     */
    ORANGEUPPERD("orange/UpperD.png"),

    /**
     * The Orange UpperCase E.
     */
    ORANGEUPPERE("orange/UpperE.png"),

    /**
     * The Orange UpperCase F.
     */
    ORANGEUPPERF("orange/UpperF.png"),

    /**
     * The Orange LowerCase F.
     */
    ORANGELOWERF("orange/LowerF.png"),

    /**
     * The Orange UpperCase I.
     */
    ORANGEUPPERI("orange/UpperI.png"),

    /**
     * The Orange UpperCase L.
     */
    ORANGEUPPERL("orange/UpperL.png"),

    /**
     * The Orange UpperCase R.
     */
    ORANGEUPPERR("orange/UpperR.png"),

    /**
     * The Orange LowerCase R.
     */
    ORANGELOWERR("orange/LowerR.png"),

    /**
     * The Orange UpperCase S.
     */
    ORANGEUPPERS("orange/UpperS.png"),

    /**
     * The Orange LowerCase y.
     */
    ORANGELOWERY("orange/LowerY.png"),

    /**
     * The Orange LowerCase T.
     */
    ORANGELOWERT("orange/LowerT.png"),

    /**
     * The Orange UpperCase T.
     */
    ORANGEUPPERT("orange/UpperT.png"),

    /**
     * The Orange UpperCase W.
     */
    ORANGEUPPERW("orange/UpperW.png"),

    /**
     * The Orange UpperCase Y.
     */
    ORANGEUPPERY("orange/UpperY.png");

    /**
     * Source name.
     */
    private final String theSource;

    /**
     * Constructor.
     *
     * @param pSourceName the source name
     */
    ThemisXAnalysisSourceIcon(final String pSourceName) {
        theSource = pSourceName;
    }

    @Override
    public String getSourceName() {
        return theSource;
    }

    @Override
    public InputStream getInputStream() {
        return ThemisXAnalysisSourceIcon.class.getResourceAsStream(theSource);
    }

    /**
     * Obtain the icon for the element.
     *
     * @param pElement the element
     * @return the icon
     */
    static TethysUIIconId getElementIcon(final ThemisXAnalysisInstance pElement) {
        /* Switch on the element type */
        if (pElement instanceof ThemisXAnalysisDeclarationInstance myDecl) {
            return getDeclarationIcon(myDecl);
        }
        if (pElement instanceof ThemisXAnalysisNodeInstance myNode) {
            return getNodeIcon(myNode);
        }
        if (pElement instanceof ThemisXAnalysisExpressionInstance myExpr) {
            return getExpressionIcon(myExpr);
        }
        if (pElement instanceof ThemisXAnalysisStatementInstance myStmt) {
            return getStatementIcon(myStmt);
        }
        if (pElement instanceof ThemisXAnalysisTypeInstance myType) {
            return getTypeIcon(myType);
        }
        throw new IllegalArgumentException("Unknown ThemisXAnalysisInstance");
    }

    /**
     * Obtain the icon for the declaration.
     *
     * @param pElement the element
     * @return the icon
     */
    private static TethysUIIconId getDeclarationIcon(final ThemisXAnalysisDeclarationInstance pElement) {
        /* Switch on the id */
        switch ((ThemisXAnalysisDeclaration) pElement.getId()) {
            case ANNOTATION:
                return REDUPPERA;
            case ANNOTATIONMEMBER:
                return REDLOWERM;
            case CLASSINTERFACE:
                return REDUPPERC;
            case COMPACT:
                return REDUPPERP;
            case CONSTRUCTOR:
                return REDUPPERN;
            case ENUM:
                return REDUPPERE;
            case ENUMVALUE:
                return REDUPPERV;
            case FIELD:
                return REDUPPERF;
            case INITIALIZER:
                return REDUPPERI;
            case METHOD:
                return REDUPPERM;
            case RECORD:
                return REDUPPERR;
            default:
                throw new IllegalArgumentException("Unknown ThemisXAnalysisDeclaration");
        }
    }

    /**
     * Obtain the icon for the node.
     *
     * @param pElement the element
     * @return the icon
     */
    private static TethysUIIconId getNodeIcon(final ThemisXAnalysisNodeInstance pElement) {
        /* Switch on the id */
        switch ((ThemisXAnalysisNode) pElement.getId()) {
            case ARRAYLEVEL:
                return PINKUPPERA;
            case CASE:
                return PINKLOWERC;
            case CATCH:
                return PINKUPPERT;
            case COMMENT:
                return PINKUPPERC;
            case COMPILATIONUNIT:
                return PINKUPPERU;
            case IMPORT:
                return PINKUPPERI;
            case MODIFIER:
                return PINKUPPERM;
            case NAME:
                return PINKUPPERN;
            case PACKAGE:
                return PINKUPPERK;
            case PARAMETER:
                return PINKUPPERP;
            case SIMPLENAME:
                return PINKLOWERN;
            case VALUEPAIR:
                return PINKLOWERP;
            case VARIABLE:
                return PINKUPPERV;
            default:
                throw new IllegalArgumentException("Unknown ThemisXAnalysisNode");
        }
    }

    /**
     * Obtain the icon for the expression.
     *
     * @param pElement the element
     * @return the icon
     */
    private static TethysUIIconId getExpressionIcon(final ThemisXAnalysisExpressionInstance pElement) {
        /* Switch on the id */
        switch ((ThemisXAnalysisExpression) pElement.getId()) {
            case ARRAYACCESS:
            case MARKER:
            case NORMAL:
            case SINGLEMEMBER:
                return GREENLOWERA;
            case ARRAYCREATION:
            case CAST:
            case CHAR:
                return GREENLOWERC;
            case ARRAYINIT:
            case INTEGER:
                return GREENLOWERI;
            case ASSIGN:
                return GREENUPPERA;
            case BINARY:
                return GREENUPPERB;
            case BOOLEAN:
                return GREENLOWERB;
            case CLASS:
                return GREENUPPERC;
            case CONDITIONAL:
            case NULL:
                return GREENLOWERN;
            case DOUBLE:
                return GREENUPPERD;
            case ENCLOSED:
                return GREENUPPERE;
            case FIELDACCESS:
                return GREENUPPERF;
            case INSTANCEOF:
                return GREENUPPERI;
            case LAMBDA:
                return GREENUPPERL;
            case LONG:
                return GREENLOWERL;
            case METHODCALL:
                return GREENUPPERM;
            case METHODREFERENCE:
                return GREENLOWERM;
            case NAME:
                return GREENUPPERN;
            case OBJECTCREATE:
                return GREENUPPERO;
            case STRING:
                return GREENLOWERS;
            case SUPER:
                return GREENLOWERP;
            case SWITCH:
                return GREENUPPERS;
            case TEXTBLOCK:
                return GREENLOWERT;
            case THIS:
                return GREENUPPERT;
            case TYPE:
                return GREENLOWERY;
            case TYPEPATTERN:
                return GREENUPPERP;
            case UNARY:
                return GREENUPPERU;
            case VARIABLE:
                return GREENUPPERV;
            default:
                throw new IllegalArgumentException("Unknown ThemisXAnalysisExpression");
        }
    }

    /**
     * Obtain the icon for the statement.
     *
     * @param pElement the element
     * @return the icon
     */
    private static TethysUIIconId getStatementIcon(final ThemisXAnalysisStatementInstance pElement) {
        /* Switch on the id */
        switch ((ThemisXAnalysisStatement) pElement.getId()) {
            case ASSERT:
                return ORANGEUPPERA;
            case BLOCK:
                return ORANGEUPPERB;
            case BREAK:
                return ORANGELOWERB;
            case LOCALCLASS:
                return ORANGEUPPERC;
            case CONSTRUCTOR:
                return ORANGEUPPERN;
            case CONTINUE:
                return ORANGELOWERC;
            case DO:
                return ORANGEUPPERD;
            case EMPTY:
                return ORANGEUPPERE;
            case FOR:
                return ORANGEUPPERF;
            case FOREACH:
                return ORANGELOWERF;
            case IF:
                return ORANGEUPPERI;
            case LABELED:
                return ORANGEUPPERL;
            case LOCALRECORD:
                return ORANGEUPPERR;
            case RETURN:
                return ORANGELOWERR;
            case SWITCH:
                return ORANGEUPPERS;
            case SYNCHRONIZED:
                return ORANGELOWERY;
            case THROW:
                return ORANGELOWERT;
            case TRY:
                return ORANGEUPPERT;
            case WHILE:
                return ORANGEUPPERW;
            case YIELD:
                return ORANGEUPPERY;
            default:
                throw new IllegalArgumentException("Unknown ThemisXAnalysisStatement");
        }
    }

    /**
     * Obtain the icon for the type.
     *
     * @param pElement the element
     * @return the icon
     */
    private static TethysUIIconId getTypeIcon(final ThemisXAnalysisTypeInstance pElement) {
        /* Switch on the id */
        switch ((ThemisXAnalysisType) pElement.getId()) {
            case ARRAY:
                return BLUEUPPERA;
            case CLASSINTERFACE:
                return BLUEUPPERC;
            case INTERSECTION:
                return BLUEUPPERI;
            case PARAMETER:
                return BLUEUPPERT;
            case PRIMITIVE:
                return BLUEUPPERP;
            case UNION:
                return BLUEUPPERU;
            case UNKNOWN:
                return BLUELOWERK;
            case VAR:
                return BLUEUPPERV;
            case VOID:
                return BLUELOWERV;
            case WILDCARD:
                return BLUEUPPERW;
            default:
                throw new IllegalArgumentException("Unknown ThemisXAnalysisType");
        }
    }
}
