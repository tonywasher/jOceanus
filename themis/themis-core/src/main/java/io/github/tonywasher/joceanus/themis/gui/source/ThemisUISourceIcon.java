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

package io.github.tonywasher.joceanus.themis.gui.source;

import io.github.tonywasher.joceanus.tethys.api.base.TethysUIIconId;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisDeclarationInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisExpressionInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisNodeInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisStatementInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisTypeInstance;
import io.github.tonywasher.joceanus.themis.parser.decl.ThemisDeclaration;
import io.github.tonywasher.joceanus.themis.parser.expr.ThemisExpression;
import io.github.tonywasher.joceanus.themis.parser.node.ThemisNode;
import io.github.tonywasher.joceanus.themis.parser.stmt.ThemisStatement;
import io.github.tonywasher.joceanus.themis.parser.type.ThemisType;

import java.io.InputStream;

/**
 * Icons for SourceTree entries.
 */
public enum ThemisUISourceIcon
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
    BLUELOWERK("blue/LowerK.png"),

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
    GREENLOWERI("green/LowerI.png"),

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
     * The Orange UpperCase X.
     */
    ORANGEUPPERX("orange/UpperX.png"),

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
    ThemisUISourceIcon(final String pSourceName) {
        theSource = pSourceName;
    }

    @Override
    public String getSourceName() {
        return theSource;
    }

    @Override
    public InputStream getInputStream() {
        return ThemisUISourceIcon.class.getResourceAsStream(theSource);
    }

    /**
     * Obtain the icon for the element.
     *
     * @param pElement the element
     * @return the icon
     */
    static TethysUIIconId getElementIcon(final ThemisInstance pElement) {
        /* Switch on the element type */
        return switch (pElement) {
            case ThemisDeclarationInstance myDecl -> getDeclarationIcon(myDecl);
            case ThemisNodeInstance myNode -> getNodeIcon(myNode);
            case ThemisExpressionInstance myExpr -> getExpressionIcon(myExpr);
            case ThemisStatementInstance myStmt -> getStatementIcon(myStmt);
            case ThemisTypeInstance myType -> getTypeIcon(myType);
            default -> throw new IllegalArgumentException("Unknown ThemisInstance");
        };
    }

    /**
     * Obtain the icon for the declaration.
     *
     * @param pElement the element
     * @return the icon
     */
    private static TethysUIIconId getDeclarationIcon(final ThemisDeclarationInstance pElement) {
        /* Switch on the id */
        return switch ((ThemisDeclaration) pElement.getId()) {
            case ANNOTATION -> REDUPPERA;
            case ANNOTATIONMEMBER -> REDLOWERM;
            case CLASSINTERFACE -> REDUPPERC;
            case COMPACT -> REDUPPERP;
            case CONSTRUCTOR -> REDUPPERN;
            case ENUM -> REDUPPERE;
            case ENUMVALUE -> REDUPPERV;
            case FIELD -> REDUPPERF;
            case INITIALIZER -> REDUPPERI;
            case METHOD -> REDUPPERM;
            case RECORD -> REDUPPERR;
            default -> throw new IllegalArgumentException("Unknown ThemisDeclaration");
        };
    }

    /**
     * Obtain the icon for the node.
     *
     * @param pElement the element
     * @return the icon
     */
    private static TethysUIIconId getNodeIcon(final ThemisNodeInstance pElement) {
        /* Switch on the id */
        return switch ((ThemisNode) pElement.getId()) {
            case ARRAYLEVEL -> PINKUPPERA;
            case CASE -> PINKLOWERC;
            case CATCH -> PINKUPPERT;
            case COMMENT -> PINKUPPERC;
            case COMPILATIONUNIT -> PINKUPPERU;
            case IMPORT -> PINKUPPERI;
            case MODIFIER -> PINKUPPERM;
            case NAME -> PINKUPPERN;
            case PACKAGE -> PINKUPPERK;
            case PARAMETER -> PINKUPPERP;
            case SIMPLENAME -> PINKLOWERN;
            case VALUEPAIR -> PINKLOWERP;
            case VARIABLE -> PINKUPPERV;
            default -> throw new IllegalArgumentException("Unknown ThemisNode");
        };
    }

    /**
     * Obtain the icon for the expression.
     *
     * @param pElement the element
     * @return the icon
     */
    private static TethysUIIconId getExpressionIcon(final ThemisExpressionInstance pElement) {
        /* Switch on the id */
        return switch ((ThemisExpression) pElement.getId()) {
            case ARRAYACCESS, MARKER, NORMAL, SINGLEMEMBER -> GREENLOWERA;
            case ARRAYCREATION, CAST, CHAR -> GREENLOWERC;
            case ARRAYINIT, INTEGER -> GREENLOWERI;
            case ASSIGN -> GREENUPPERA;
            case BINARY -> GREENUPPERB;
            case BOOLEAN -> GREENLOWERB;
            case CLASS -> GREENUPPERC;
            case CONDITIONAL, NULL -> GREENLOWERN;
            case DOUBLE -> GREENUPPERD;
            case ENCLOSED -> GREENUPPERE;
            case FIELDACCESS -> GREENUPPERF;
            case INSTANCEOF -> GREENUPPERI;
            case LAMBDA -> GREENUPPERL;
            case LONG -> GREENLOWERL;
            case METHODCALL -> GREENUPPERM;
            case METHODREFERENCE -> GREENLOWERM;
            case NAME -> GREENUPPERN;
            case OBJECTCREATE -> GREENUPPERO;
            case STRING -> GREENLOWERS;
            case SUPER -> GREENLOWERP;
            case SWITCH -> GREENUPPERS;
            case TEXTBLOCK -> GREENLOWERT;
            case THIS -> GREENUPPERT;
            case TYPE -> GREENLOWERY;
            case TYPEPATTERN -> GREENUPPERP;
            case UNARY -> GREENUPPERU;
            case VARIABLE -> GREENUPPERV;
            default -> throw new IllegalArgumentException("Unknown ThemisExpression");
        };
    }

    /**
     * Obtain the icon for the statement.
     *
     * @param pElement the element
     * @return the icon
     */
    private static TethysUIIconId getStatementIcon(final ThemisStatementInstance pElement) {
        /* Switch on the id */
        return switch ((ThemisStatement) pElement.getId()) {
            case ASSERT -> ORANGEUPPERA;
            case BLOCK -> ORANGEUPPERB;
            case BREAK -> ORANGELOWERB;
            case LOCALCLASS -> ORANGEUPPERC;
            case CONSTRUCTOR -> ORANGEUPPERN;
            case CONTINUE -> ORANGELOWERC;
            case DO -> ORANGEUPPERD;
            case EMPTY -> ORANGEUPPERE;
            case EXPRESSION -> ORANGEUPPERX;
            case FOR -> ORANGEUPPERF;
            case FOREACH -> ORANGELOWERF;
            case IF -> ORANGEUPPERI;
            case LABELED -> ORANGEUPPERL;
            case LOCALRECORD -> ORANGEUPPERR;
            case RETURN -> ORANGELOWERR;
            case SWITCH -> ORANGEUPPERS;
            case SYNCHRONIZED -> ORANGELOWERY;
            case THROW -> ORANGELOWERT;
            case TRY -> ORANGEUPPERT;
            case WHILE -> ORANGEUPPERW;
            case YIELD -> ORANGEUPPERY;
            default -> throw new IllegalArgumentException("Unknown ThemisStatement");
        };
    }

    /**
     * Obtain the icon for the type.
     *
     * @param pElement the element
     * @return the icon
     */
    private static TethysUIIconId getTypeIcon(final ThemisTypeInstance pElement) {
        /* Switch on the id */
        return switch ((ThemisType) pElement.getId()) {
            case ARRAY -> BLUEUPPERA;
            case CLASSINTERFACE -> BLUEUPPERC;
            case INTERSECTION -> BLUEUPPERI;
            case PARAMETER -> BLUEUPPERT;
            case PRIMITIVE -> BLUEUPPERP;
            case UNION -> BLUEUPPERU;
            case UNKNOWN -> BLUELOWERK;
            case VAR -> BLUEUPPERV;
            case VOID -> BLUELOWERV;
            case WILDCARD -> BLUEUPPERW;
            default -> throw new IllegalArgumentException("Unknown ThemisType");
        };
    }
}
