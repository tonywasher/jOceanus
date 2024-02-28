/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.jthemis.ui;

import java.io.InputStream;

import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIIconId;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisAnnotation;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisAnonClass;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisArrayInit;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisBlock;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisCase;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisCatch;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisClass;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisComment;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisDoWhile;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisElement;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisElse;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisEnum;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisField;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisFile;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisFinally;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisFor;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisIf;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisInterface;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisLambda;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisMethod;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisModule;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisPackage;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisProject;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisStatement;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisSwitch;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisTry;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisWhile;

/**
 * Source Icon Ids.
 */
public enum ThemisSourceIcon
        implements TethysUIIconId {
    /**
     * The project icon.
     */
    PROJECT("icons/UpperCaseOrangeJ.png"),

    /**
     * The module icon.
     */
    MODULE("icons/UpperCaseOrangeM.png"),

    /**
     * The package icon.
     */
    PACKAGE("icons/UpperCaseOrangeP.png"),

    /**
     * The file icon.
     */
    FILE("icons/UpperCaseOrangeF.png"),

    /**
     * The class icon.
     */
    CLASS("icons/UpperCaseOrangeC.png"),

    /**
     * The enum icon.
     */
    ENUM("icons/UpperCaseOrangeE.png"),

    /**
     * The interface icon.
     */
    INTERFACE("icons/UpperCaseOrangeI.png"),

    /**
     * The annotationDef icon.
     */
    ANNOTATIONDEF("icons/UpperCaseOrangeA.png"),

    /**
     * The for icon.
     */
    FOR("icons/UpperCaseGreenF.png"),

    /**
     * The doWhile icon.
     */
    DOWHILE("icons/UpperCaseGreenD.png"),

    /**
     * The while icon.
     */
    WHILE("icons/UpperCaseGreenW.png"),

    /**
     * The switch icon.
     */
    SWITCH("icons/UpperCaseGreenS.png"),

    /**
     * The case icon.
     */
    CASE("icons/LowerCaseGreenC.png"),

    /**
     * The try icon.
     */
    TRY("icons/UpperCaseGreenT.png"),

    /**
     * The catch icon.
     */
    CATCH("icons/LowerCaseGreenC.png"),

    /**
     * The finally icon.
     */
    FINALLY("icons/LowerCaseGreenF.png"),

    /**
     * The if icon.
     */
    IF("icons/UpperCaseGreenI.png"),

    /**
     * The else icon.
     */
    ELSE("icons/LowerCaseGreenE.png"),

    /**
     * The block icon.
     */
    BLOCK("icons/UpperCaseBlueB.png"),

    /**
     * The field icon.
     */
    FIELD("icons/UpperCaseBlueF.png"),

    /**
     * The lambda icon.
     */
    LAMBDA("icons/UpperCaseBlueL.png"),

    /**
     * The method icon.
     */
    METHOD("icons/UpperCaseBlueM.png"),

    /**
     * The statement icon.
     */
    STATEMENT("icons/UpperCaseBlueS.png"),

    /**
     * The comment icon.
     */
    COMMENT("icons/UpperCasePinkC.png"),

    /**
     * The javadoc icon.
     */
    JAVADOC("icons/UpperCasePinkD.png"),

    /**
     * The statement icon.
     */
    ANNOTATION("icons/UpperCasePinkA.png");

    /**
     * The blue colour.
     */
    private static final String BLUE = "#00008B";

    /**
     * The green colour.
     */
    private static final String GREEN = "#228B22";

    /**
     * The orange colour.
     */
    private static final String ORANGE = "#FF4500";

    /**
     * The pink colour.
     */
    private static final String PINK = "#FF1493";

    /**
     * Source name.
     */
    private final String theSource;

    /**
     * Constructor.
     * @param pSourceName the source name
     */
    ThemisSourceIcon(final String pSourceName) {
        theSource = pSourceName;
    }

    @Override
    public String getSourceName() {
        return theSource;
    }

    @Override
    public InputStream getInputStream() {
        return ThemisSourceIcon.class.getResourceAsStream(theSource);
    }

    /**
     * Obtain the colour for the element.
     * @param pElement the element
     * @return the color string
     */
    static String getElementColour(final ThemisAnalysisElement pElement) {
        /* Handle statement/Field/Block */
        if (pElement instanceof ThemisAnalysisStatement
            || pElement instanceof ThemisAnalysisField
            || pElement instanceof ThemisAnalysisBlock) {
            return BLUE;
        }

        /* Handle method/Lambda */
        if (pElement instanceof ThemisAnalysisMethod
            || pElement instanceof ThemisAnalysisArrayInit
            || pElement instanceof ThemisAnalysisLambda) {
            return BLUE;
        }

        /* Handle for/do/while */
        if (pElement instanceof ThemisAnalysisFor
                || pElement instanceof ThemisAnalysisWhile
                || pElement instanceof ThemisAnalysisDoWhile) {
            return GREEN;
        }

        /* Handle switch/case */
        if (pElement instanceof ThemisAnalysisSwitch
                || pElement instanceof ThemisAnalysisCase) {
            return GREEN;
        }

        /* Handle try/catch/finally */
        if (pElement instanceof ThemisAnalysisTry
                || pElement instanceof ThemisAnalysisCatch
                || pElement instanceof ThemisAnalysisFinally) {
            return GREEN;
        }

        /* Handle if/else */
        if (pElement instanceof ThemisAnalysisIf
                || pElement instanceof ThemisAnalysisElse) {
            return GREEN;
        }

        /* Handle project/module/package */
        if (pElement instanceof ThemisAnalysisProject
                || pElement instanceof ThemisAnalysisModule
                || pElement instanceof ThemisAnalysisPackage) {
            return ORANGE;
        }

        /* Handle file/interface */
        if (pElement instanceof ThemisAnalysisFile
                || pElement instanceof ThemisAnalysisInterface) {
            return ORANGE;
        }

        /* Handle class/enum */
        if (pElement instanceof ThemisAnalysisClass
                || pElement instanceof ThemisAnalysisAnonClass
                || pElement instanceof ThemisAnalysisEnum) {
            return ORANGE;
        }

        /* Handle comment/annotation */
        if (pElement instanceof ThemisAnalysisComment
                || pElement instanceof ThemisAnalysisAnnotation) {
            return PINK;
        }

        /* Not found */
        return null;
    }

    /**
     * Obtain the icon for the element.
     * @param pElement the element
     * @return the icon
     */
    static TethysUIIconId getElementIcon(final ThemisAnalysisElement pElement) {
        /* Determine the element colour */
        final String myColour = getElementColour(pElement);
        if (myColour == null) {
            return null;
        }

        /* Switch on the colour */
        switch (myColour) {
            case BLUE:
                return getBlueElementIcon(pElement);
            case GREEN:
                return getGreenElementIcon(pElement);
            case ORANGE:
                return getOrangeElementIcon(pElement);
            case PINK:
                return getPinkElementIcon(pElement);
            default:
                return null;
        }
    }

    /**
     * Obtain the icon for the Blue element.
     * @param pElement the element
     * @return the icon
     */
    private static TethysUIIconId getBlueElementIcon(final ThemisAnalysisElement pElement) {
        /* Handle statement */
        if (pElement instanceof ThemisAnalysisStatement) {
            return STATEMENT;
        }

        /* Handle Field */
        if (pElement instanceof ThemisAnalysisField) {
            return FIELD;
        }

        /* Handle Block */
        if (pElement instanceof ThemisAnalysisBlock) {
            return BLOCK;
        }

        /* Handle method */
        if (pElement instanceof ThemisAnalysisMethod) {
            return METHOD;
        }

        /* Handle Lambda */
        if (pElement instanceof ThemisAnalysisLambda) {
            return LAMBDA;
        }

        /* Not found */
        return null;
    }

    /**
     * Obtain the icon for the green element.
     * @param pElement the element
     * @return the iconId
     */
    private static TethysUIIconId getGreenElementIcon(final ThemisAnalysisElement pElement) {
        /* Handle for/do/while */
        if (pElement instanceof ThemisAnalysisFor) {
            return FOR;
        }

        /* Handle do */
        if (pElement instanceof ThemisAnalysisWhile) {
            return WHILE;
        }

        /* Handle while */
        if (pElement instanceof ThemisAnalysisDoWhile) {
            return DOWHILE;
        }

        /* Handle switch */
        if (pElement instanceof ThemisAnalysisSwitch) {
            return SWITCH;
        }

        /* Handle case */
        if (pElement instanceof ThemisAnalysisCase) {
            return CASE;
        }

        /* Handle try */
        if (pElement instanceof ThemisAnalysisTry) {
            return TRY;
        }

        /* Handle try/catch/finally */
        if (pElement instanceof ThemisAnalysisCatch) {
            return CATCH;
        }

        /* Handle finally */
        if (pElement instanceof ThemisAnalysisFinally) {
            return FINALLY;
        }

        /* Handle if */
        if (pElement instanceof ThemisAnalysisIf) {
            return IF;
        }

        /* Handle else */
        if (pElement instanceof ThemisAnalysisElse) {
            return ELSE;
        }

        /* Not found */
        return null;
    }

    /**
     * Obtain the iconId for the orange element.
     * @param pElement the element
     * @return the iconId
     */
    private static TethysUIIconId getOrangeElementIcon(final ThemisAnalysisElement pElement) {
        /* Handle project/module/package */
        if (pElement instanceof ThemisAnalysisProject) {
            return PROJECT;
        }

        /* Handle project/module/package */
        if (pElement instanceof ThemisAnalysisModule) {
            return MODULE;
        }

        /* Handle package */
        if (pElement instanceof ThemisAnalysisPackage) {
            return PACKAGE;
        }

        /* Handle file/interface */
        if (pElement instanceof ThemisAnalysisFile) {
            return FILE;
        }

        /* Handle interface */
        if (pElement instanceof ThemisAnalysisInterface) {
            return ((ThemisAnalysisInterface) pElement).isAnnotation() ? ANNOTATIONDEF : INTERFACE;
        }

        /* Handle class */
        if (pElement instanceof ThemisAnalysisClass
               || pElement instanceof ThemisAnalysisAnonClass) {
            return CLASS;
        }

        /* Handle class/enum */
        if (pElement instanceof ThemisAnalysisEnum) {
            return ENUM;
        }

        /* Not found */
        return null;
    }

    /**
     * Obtain the iconId for the pink element.
     * @param pElement the element
     * @return the iconId
     */
    private static TethysUIIconId getPinkElementIcon(final ThemisAnalysisElement pElement) {
        /* Handle comment */
        if (pElement instanceof ThemisAnalysisComment) {
            return ((ThemisAnalysisComment) pElement).isJavaDoc() ? JAVADOC : COMMENT;
        }

        /* Handle annotation */
        if (pElement instanceof ThemisAnalysisModule) {
            return ANNOTATION;
        }

        /* Not found */
        return null;
    }
}
