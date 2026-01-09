/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.themis.lethe.analysis;

import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.lethe.analysis.ThemisAnalysisDataMap.ThemisAnalysisDataType;
import net.sourceforge.joceanus.themis.lethe.analysis.ThemisAnalysisEmbedded.ThemisAnalysisEmbedType;
import net.sourceforge.joceanus.themis.lethe.analysis.ThemisAnalysisGeneric.ThemisAnalysisGenericBase;
import net.sourceforge.joceanus.themis.lethe.analysis.ThemisAnalysisScanner.ThemisAnalysisSource;
import net.sourceforge.joceanus.themis.exc.ThemisDataException;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parser.
 */
public class ThemisAnalysisParser
    implements ThemisAnalysisSource {
    /**
     * The keyWordMap.
     */
    static final Map<String, Object> KEYWORDS = createKeyWordMap();

    /**
     * The parent container.
     */
    private final ThemisAnalysisContainer theParent;

    /**
     * The list of source lines.
     */
    private final Deque<ThemisAnalysisElement> theLines;

    /**
     * The list of output contents lines.
     */
    private final Deque<ThemisAnalysisElement> theContents;

    /**
     * The dataMap.
     */
    private final ThemisAnalysisDataMap theDataMap;

    /**
     * Temporary parser?
     */
    private final boolean isTemporary;

    /**
     * Constructor.
     * @param pLines the source lines.
     * @param pContents the processed contents
     * @param pContainer the container
     */
    ThemisAnalysisParser(final Deque<ThemisAnalysisElement> pLines,
                         final Deque<ThemisAnalysisElement> pContents,
                         final ThemisAnalysisContainer pContainer) {
        /* Store parameters */
        theLines = pLines;
        theContents = pContents;
        theParent = pContainer;

        /* Create the dataTypeMap */
        theDataMap = pContainer.getDataMap();
        isTemporary = false;
    }

    /**
     * Constructor.
     * @param pParser the source parser.
     * @param pProcessed the processed output
     */
    ThemisAnalysisParser(final ThemisAnalysisParser pParser,
                         final Deque<ThemisAnalysisElement> pProcessed) {
        this(pParser.theLines, pProcessed, pParser.getParent());
    }

    /**
     * Temporary parser constructor.
     * @param pParser the base parser.
     */
    ThemisAnalysisParser(final ThemisAnalysisParser pParser) {
        /* Store parameters */
        theLines = pParser.theLines;
        theContents = pParser.theContents;
        theParent = pParser.theParent;

        /* Create the dataTypeMap */
        theDataMap = new ThemisAnalysisDataMap(pParser.getDataMap());
        isTemporary = true;
    }

    @Override
    public boolean hasLines() {
        return !theLines.isEmpty();
    }

    /**
     * Is this a temporary parser?
     * @return true/false
     */
    boolean isTemporary() {
        return isTemporary;
    }

    /**
     * Obtain the parent container.
     * @return the parent
     */
    ThemisAnalysisContainer getParent() {
        return theParent;
    }

    /**
     * Obtain the dataTypes map.
     * @return the dataTypesMap
     */
    ThemisAnalysisDataMap getDataMap() {
        return theDataMap;
    }

    @Override
    public ThemisAnalysisElement popNextLine() throws OceanusException {
        /* Check that there is a line to pop */
        if (theLines.isEmpty()) {
            throw new ThemisDataException("No more lines");
        }

        /* Access the first line and remove from the list */
        return theLines.removeFirst();
    }

    /**
     * Peek next line from list.
     * @return the next line
     * @throws OceanusException on error
     */
    ThemisAnalysisElement peekNextLine() throws OceanusException {
        /* Check that there is a line to pop */
        if (theLines.isEmpty()) {
            throw new ThemisDataException("No more lines");
        }

        /* Return the first line in the list */
        return theLines.getFirst();
    }

    @Override
    public void pushLine(final ThemisAnalysisElement pLine) {
        /* Insert the line at the front of the stack */
        theLines.offerFirst(pLine);
    }

    /**
     * Process a potential comment/blank line.
     * @param pLine the line
     * @return have we processed the line?
     * @throws OceanusException on error
     */
    boolean processCommentsAndBlanks(final ThemisAnalysisLine pLine) throws OceanusException {
        /* If this is a starting comment */
        if (ThemisAnalysisComment.isStartComment(pLine)) {
            /* Process the comment lines */
            final ThemisAnalysisComment myComment = new ThemisAnalysisComment(this, pLine);
            theContents.add(myComment);
            return true;
        }

        /* Strip Trailing comments and modifiers */
        pLine.stripTrailingComments();
        pLine.stripModifiers();

        /* If this is a blank line */
        if (ThemisAnalysisBlank.isBlank(pLine)) {
            /* Process the blank lines */
            final ThemisAnalysisBlank myBlank = new ThemisAnalysisBlank(this, pLine);
            theContents.add(myBlank);
            return true;
        }

        /* If this is an annotation line */
        if (ThemisAnalysisAnnotation.isAnnotation(pLine)) {
            /* Process the annotation lines */
            final ThemisAnalysisAnnotation myAnnotation = new ThemisAnalysisAnnotation(this, pLine);
            theContents.add(myAnnotation);
            return true;
        }

        /* Not processed */
        return false;
    }

    /**
     * Process a potential import line.
     * @param pLine the line
     * @return have we processed the line?
     * @throws OceanusException on error
     */
    boolean processImports(final ThemisAnalysisLine pLine) throws OceanusException {
        /* If this is an import line */
        if (ThemisAnalysisImports.isImport(pLine)) {
            /* Process the import lines */
            final ThemisAnalysisImports myImports = new ThemisAnalysisImports(this, pLine);
            theContents.add(myImports);
            return true;
        }

        /* Not processed */
        return false;
    }

    /**
     * Process a class/enum/interface line.
     * @param pLine the line
     * @return have we processed the line?
     * @throws OceanusException on error
     */
    boolean processClass(final ThemisAnalysisLine pLine) throws OceanusException {
        /* Access class type */
        final String myToken = pLine.peekNextToken();
        final Object myType = KEYWORDS.get(myToken);

        /* If we have a keyWord */
        if (myType instanceof ThemisAnalysisKeyWord myKeyWord) {
            /* If this is a class */
            switch (myKeyWord) {
                case CLASS:
                    /* Create the class */
                    pLine.stripStartSequence(myToken);
                    theContents.add(new ThemisAnalysisClass(this, pLine));
                    return true;

                /* If this is an interface/annotation */
                case INTERFACE:
                case ANNOTATION:
                    /* Create the interface */
                    pLine.stripStartSequence(myToken);
                    theContents.add(new ThemisAnalysisInterface(this, myType.equals(ThemisAnalysisKeyWord.ANNOTATION), pLine));
                    return true;

                /* If this is an enum */
                case ENUM:
                    /* Create the enum */
                    pLine.stripStartSequence(myToken);
                    theContents.add(new ThemisAnalysisEnum(this, pLine));
                    return true;

                default:
                    break;
            }
        }

        /* Not processed */
        return false;
    }

    /**
     * Process language constructs.
     * @param pLine the line
     * @return have we processed the line?
     * @throws OceanusException on error
     */
    boolean processLanguage(final ThemisAnalysisLine pLine) throws OceanusException {
        /* Access class type */
        final String myToken = pLine.peekNextToken();
        final Object myType = KEYWORDS.get(myToken);

        /* If we have a keyWord */
        if (myType instanceof ThemisAnalysisKeyWord myKeyWord) {
            /* Switch on the type */
            switch (myKeyWord) {
                /* If this is a while */
                case WHILE:
                    /* Create the while */
                    pLine.stripStartSequence(myToken);
                    theContents.add(new ThemisAnalysisWhile(this, pLine));
                    return true;

                /* If this is a doWhile */
                case DO:
                    /* Create the while */
                    pLine.stripStartSequence(myToken);
                    theContents.add(new ThemisAnalysisDoWhile(this));
                    return true;

                /* If this is a switch */
                case SWITCH:
                    /* Create the switch */
                    pLine.stripStartSequence(myToken);
                    theContents.add(new ThemisAnalysisSwitch(this, pLine));
                    return true;

                /* If this is a for */
                case FOR:
                    /* Create the for */
                    pLine.stripStartSequence(myToken);
                    theContents.add(new ThemisAnalysisFor(this, pLine));
                    return true;

                /* If this is an if */
                case IF:
                    /* Create the if */
                    pLine.stripStartSequence(myToken);
                    theContents.add(new ThemisAnalysisIf(this, pLine));
                    return true;

                /* If this is a try */
                case TRY:
                    /* Create the try */
                    pLine.stripStartSequence(myToken);
                    theContents.add(new ThemisAnalysisTry(this, pLine));
                    return true;

                default:
                    break;
            }
        }

        /* Not processed */
        return false;
    }

    /**
     * Process blocks.
     * @param pLine the line
     * @return have we processed the line?
     * @throws OceanusException on error
     */
    boolean processBlocks(final ThemisAnalysisLine pLine) throws OceanusException {
        /* handle a standard block */
        if (ThemisAnalysisBlock.checkBlock(pLine)) {
            theContents.add(new ThemisAnalysisBlock(this, pLine));
            return true;
        }

        /* check for an embedded lambda/Anonymous class/ArrayInit */
        final ThemisAnalysisEmbedType myEmbed = ThemisAnalysisEmbedded.checkForEmbedded(pLine);
        switch (myEmbed) {
            case LAMBDA:
            case ANON:
            case ARRAY:
                /* Process an embedded lambda/anon/init */
                theContents.add(new ThemisAnalysisEmbedded(this, myEmbed, pLine));
                return true;
            case METHOD:
                /* Process an embedded methodBody */
                theContents.add(new ThemisAnalysisMethodBody(this, pLine));
                return true;
            default:
                break;
        }

        /* Not processed */
        return false;
    }

    /**
     * Process a case/default line.
     * @param pOwner the owning switch
     * @param pLine the line
     * @return have we processed the line?
     * @throws OceanusException on error
     */
    boolean processCase(final ThemisAnalysisContainer pOwner,
                        final ThemisAnalysisLine pLine) throws OceanusException {
        /* Access case type */
        final Object myCase = parseCase(pLine);

        /* If we have a case */
        if (myCase != null) {
            theContents.add(new ThemisAnalysisCase(this, pOwner, myCase));
            return true;
        }

        /* Not processed */
        return false;
    }

    /**
     * Process a case/default line.
     * @param pLine the line
     * @return have we processed the line?
     */
    static Object parseCase(final ThemisAnalysisLine pLine) {
        /* Handle default clause */
        if (pLine.getProperties().hasModifier(ThemisAnalysisModifier.DEFAULT)) {
            return ThemisAnalysisKeyWord.DEFAULT;
        }

        /* Access case type */
        final String myToken = pLine.peekNextToken();
        final Object myType = KEYWORDS.get(myToken);

        /* If we have a keyWord */
        if (myType instanceof ThemisAnalysisKeyWord myKeyWord) {
            /* If this is a case/default */
            switch (myKeyWord) {
                case CASE:
                    pLine.stripStartSequence(myToken);
                    return pLine.stripNextToken();

                case DEFAULT:
                    pLine.stripStartSequence(myToken);
                    return myKeyWord;

                default:
                    return null;

            }
        }

        /* Not processed */
        return null;
    }

    /**
     * Process extra constructs.
     * @param pOwner the owning construct
     * @param pKeyWord the keyWord
     * @return have we processed the line?
     * @throws OceanusException on error
     */
    ThemisAnalysisElement processExtra(final ThemisAnalysisContainer pOwner,
                                       final ThemisAnalysisKeyWord pKeyWord) throws OceanusException {
        /* Just return if there are no more lines */
        if (!hasLines()) {
            return null;
        }

        /* Access keyWord */
        final ThemisAnalysisLine myLine = (ThemisAnalysisLine) popNextLine();
        final String myToken = myLine.peekNextToken();
        final Object myType = KEYWORDS.get(myToken);

        /* If we have a keyWord */
        if (pKeyWord.equals(myType)) {
            /* Switch on the type */
            switch ((ThemisAnalysisKeyWord) myType) {
                /* If this is an else */
                case ELSE:
                    /* Create the else */
                    myLine.stripStartSequence(myToken);
                    return new ThemisAnalysisElse(this, pOwner, myLine);

                /* If this is a catch */
                case CATCH:
                    /* Create the switch */
                    myLine.stripStartSequence(myToken);
                    return new ThemisAnalysisCatch(this, pOwner, myLine);

                /* If this is a finally */
                case FINALLY:
                    /* Create the finally */
                    myLine.stripStartSequence(myToken);
                    return new ThemisAnalysisFinally(this, pOwner, myLine);

                default:
                    break;
            }
        }

        /* Not processed */
        pushLine(myLine);
        return null;
    }

    /**
     * Process embedded block construct.
     * @param pEmbedded the embedded block
     * @return the field/statement
     * @throws OceanusException on error
     */
    ThemisAnalysisElement processEmbedded(final ThemisAnalysisEmbedded pEmbedded) throws OceanusException {
        /* Look for a reference */
        final ThemisAnalysisLine myLine = pEmbedded.getHeader();
        final ThemisAnalysisReference myReference = parsePotentialDataType(myLine);

        /* If we have a reference */
        if (myReference != null) {
            /* Create as field */
            final String myName = myLine.stripNextToken();
            return new ThemisAnalysisField(this, myName, myReference, pEmbedded);
        }

        /* Just convert to statement */
        final ThemisAnalysisKeyWord myKeyWord = determineStatementKeyWord(myLine);
        return new ThemisAnalysisStatement(myKeyWord, pEmbedded);
    }

    /**
     * Process methodBody construct.
     * @param pMethod the methodBody
     * @return the method
     * @throws OceanusException on error
     */
    ThemisAnalysisElement processMethodBody(final ThemisAnalysisMethodBody pMethod) throws OceanusException {
        /* Look for a reference */
        final ThemisAnalysisLine myLine = pMethod.getHeader();
        final ThemisAnalysisReference myReference = parsePotentialDataType(myLine);

        /* Access the name of the method */
        final String myName = myLine.stripNextToken();
        final boolean isMethod = myLine.startsWithChar(ThemisAnalysisChar.PARENTHESIS_OPEN);

        /* We must have a reference and be a method */
        if (myReference == null || !isMethod) {
            throw new ThemisDataException("Invalid Method Body");
        }

        /* Create as field */
        return new ThemisAnalysisMethod(this, myName, myReference, pMethod);
    }

    /**
     * Process field and method constructs.
     * @param pLine the line
     * @return the field/method or null
     * @throws OceanusException on error
     */
    ThemisAnalysisElement processFieldsAndMethods(final ThemisAnalysisLine pLine) throws OceanusException {
        /* Look for a reference */
        final ThemisAnalysisReference myReference = parsePotentialDataType(pLine);
        if (myReference != null) {
            /* Access the name of the field or method */
            final String myName = pLine.stripNextToken();
            final boolean isMethod = pLine.startsWithChar(ThemisAnalysisChar.PARENTHESIS_OPEN);
            if (!isMethod) {
                myReference.resolveGeneric(this);
                return new ThemisAnalysisField(this, myName, myReference, pLine);
            } else {
                return new ThemisAnalysisMethod(this, myName, myReference, pLine);
            }
        }

        /* Not processed */
        return null;
    }

    /**
     * Process a statement.
     * @param pLine the line
     * @return the statement
     * @throws OceanusException on error
     */
    ThemisAnalysisElement processStatement(final ThemisAnalysisLine pLine) throws OceanusException {
        /* Determine keyWord (if any)  */
        final ThemisAnalysisKeyWord myKeyWord = determineStatementKeyWord(pLine);
        return new ThemisAnalysisStatement(this, myKeyWord, pLine);
    }

    /**
     * Process a statement.
     * @param pLine the line
     * @return the statement
     */
    private static ThemisAnalysisKeyWord determineStatementKeyWord(final ThemisAnalysisLine pLine) {
        /* Look for a control keyWord */
        final String myToken = pLine.peekNextToken();
        final Object myType = KEYWORDS.get(myToken);

        /* If we have a keyWord */
        if (myType instanceof ThemisAnalysisKeyWord myKeyWord) {
            /* Switch on the type */
            switch (myKeyWord) {
                case RETURN:
                case THROW:
                case BREAK:
                case CONTINUE:
                case YIELD:
                    pLine.stripNextToken();
                    return myKeyWord;
                default:
                    break;
            }
        }

        /* No keyWord */
        return null;
    }

    /**
     * Parse a possible dataType.
     * @param pLine the line
     * @return the dataType or null
     * @throws OceanusException on error
     */
    private ThemisAnalysisReference parsePotentialDataType(final ThemisAnalysisLine pLine) throws OceanusException {
        /* Not a dataType if we start with a keyWord */
        final String myToken = pLine.peekNextToken();
        if (KEYWORDS.get(myToken) != null) {
            return null;
        }

        /* Determine whether this is a method call or declaration */
        final boolean isMethod = pLine.startsWithSequence(myToken + ThemisAnalysisChar.PARENTHESIS_OPEN);

        /* Look for a valid, existing dataType */
        ThemisAnalysisDataType myType = theDataMap.lookUpDataType(myToken, isMethod);
        if (myType == null) {
            /* If the line has generic definitions */
            final ThemisAnalysisProperties myProps = pLine.getProperties();
            if (myProps.hasGeneric()) {
                /* Create a temporary parser and resolve against the generics */
                final ThemisAnalysisParser myTempParser = new ThemisAnalysisParser(this);
                myProps.resolveGeneric(myTempParser);
                myType = myTempParser.getDataMap().lookUpTheDataType(myToken);
            }

            /* Return null if we haven't resolved it */
            if (myType == null) {
                return null;
            }
        }
        pLine.stripStartSequence(myToken);

        /* Return the reference */
        return buildReference(theDataMap, pLine, myType);
    }

    /**
     * Parse a dataType.
     * @param pDataMap the dataMap
     * @param pLine the line
     * @return the dataType or null
     * @throws OceanusException on error
     */
    static ThemisAnalysisReference parseDataType(final ThemisAnalysisDataMap pDataMap,
                                                 final ThemisAnalysisLine pLine) throws OceanusException {
        /* Cannot be started by a keyWord */
        String myToken = pLine.peekNextToken();
        if (KEYWORDS.get(myToken) != null) {
            throw new ThemisDataException("DataType required but keyWord found");
        }

        /* If the token ends with the VARARGS indication */
        if (myToken.endsWith(ThemisAnalysisArray.VARARGS)) {
            /* Strip the varArgs indication */
            myToken = myToken.substring(0, myToken.length() - ThemisAnalysisArray.VARARGS.length());
        }

        /* Look for a valid dataType */
        ThemisAnalysisDataType myType = pDataMap.lookUpDataType(myToken, false);
        if (myType == null) {
            /* Declare the unrecognised dataType */
            myType = pDataMap.declareUnknown(myToken);
        }
        pLine.stripStartSequence(myToken);

        /* Return the reference */
        return buildReference(pDataMap, pLine, myType);
    }

    /**
     * Create the reference.
     * @param pDataMap the dataMap
     * @param pLine the line
     * @param pType the dataType
     * @return the dataType or null
     * @throws OceanusException on error
     */
    private static ThemisAnalysisReference buildReference(final ThemisAnalysisDataMap pDataMap,
                                                          final ThemisAnalysisLine pLine,
                                                          final ThemisAnalysisDataType pType) throws OceanusException {
        /* Access any generic/array detail */
        final ThemisAnalysisGeneric myGeneric = ThemisAnalysisGeneric.isGeneric(pLine)
                                                ? new ThemisAnalysisGenericBase(pLine)
                                                : null;
        final ThemisAnalysisArray myArray = ThemisAnalysisArray.isArray(pLine)
                                            ? new ThemisAnalysisArray(pLine)
                                            : null;

        /* Return the reference */
        final ThemisAnalysisReference myRef = new ThemisAnalysisReference(pType, myGeneric, myArray);
        pDataMap.declareReference(myRef);
        return myRef;
    }

    /**
     * process the lines.
     * @throws OceanusException on error
     */
    void processLines() throws OceanusException {
        /* Loop through the lines */
        while (hasLines()) {
            /* Access next line */
            final ThemisAnalysisLine myLine = (ThemisAnalysisLine) popNextLine();

            /* Process comments/blanks/languageConstructs */
            final boolean processed = processCommentsAndBlanks(myLine)
                    || processClass(myLine)
                    || processLanguage(myLine)
                    || processBlocks(myLine);

            /* If we haven't processed yet */
            if (!processed) {
                /* Just add the line to contents at present */
                theContents.add(myLine);
            }
        }
    }

    /**
     * Create the keyWordMap.
     * @return the new map
     */
    private static Map<String, Object> createKeyWordMap() {
        /* create the map */
        final Map<String, Object> myMap = new HashMap<>();

        /* Add the modifiers */
        for (ThemisAnalysisModifier myModifier : ThemisAnalysisModifier.values()) {
            myMap.put(myModifier.toString(), myModifier);
        }

        /* Add the keyWords */
        for (ThemisAnalysisKeyWord myKeyWord : ThemisAnalysisKeyWord.values()) {
            myMap.put(myKeyWord.toString(), myKeyWord);
        }

        /* return the map */
        return myMap;
    }

    /**
     * Parse ancestors.
     * @param pHeaders the headers
     * @return the list of ancestors
     * @throws OceanusException on error
     */
    List<ThemisAnalysisReference> parseAncestors(final Deque<ThemisAnalysisElement> pHeaders) throws OceanusException {
        /* Create the list */
        final List<ThemisAnalysisReference> myAncestors = new ArrayList<>();
        final ThemisAnalysisLine myHeader = new ThemisAnalysisLine(pHeaders);

        /* Loop through the line */
        for (;;) {
            /* Strip leading comma */
            if (myHeader.startsWithChar(ThemisAnalysisChar.COMMA)) {
                myHeader.stripStartChar(ThemisAnalysisChar.COMMA);
            }

            /* Access first token */
            final String myToken = myHeader.peekNextToken();
            if (myToken.length() == 0) {
                return myAncestors;
            }

            /* Ignore keywords */
            if (KEYWORDS.get(myToken) != null) {
                /* Strip the token from the line */
                myHeader.stripNextToken();
            } else {
                /* Process the ancestor */
                final ThemisAnalysisReference myReference = parseDataType(theDataMap, myHeader);
                myReference.resolveGeneric(this);
                myAncestors.add(myReference);
            }
        }
    }

    /**
     * Parse parameters.
     * @param pParams the parameters
     * @return the parameter map
     * @throws OceanusException on error
     */
    Map<String, ThemisAnalysisReference> parseParameters(final ThemisAnalysisLine pParams) throws OceanusException {
        /* Create the list */
        final Map<String, ThemisAnalysisReference> myParams = new LinkedHashMap<>();

        /* Loop through the line */
        for (;;) {
            /* Strip leading comma */
            if (pParams.startsWithChar(ThemisAnalysisChar.COMMA)) {
                pParams.stripStartChar(ThemisAnalysisChar.COMMA);
            }

            /* Access first token */
            final String myToken = pParams.peekNextToken();
            if (myToken.length() == 0) {
                return myParams;
            }

            /* Ignore keywords */
            if (KEYWORDS.get(myToken) != null) {
                /* Strip the token from the line */
                pParams.stripNextToken();
            } else {
                /* Process the parameter */
                final ThemisAnalysisReference myReference = parseDataType(theDataMap, pParams);
                myReference.resolveGeneric(this);
                final String myVar = pParams.stripNextToken();
                myParams.put(myVar, myReference);
            }
        }
    }
}
