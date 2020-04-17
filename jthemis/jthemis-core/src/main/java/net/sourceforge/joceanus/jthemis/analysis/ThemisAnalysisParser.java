/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2020 Tony Washer
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
package net.sourceforge.joceanus.jthemis.analysis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parser.
 */
public class ThemisAnalysisParser {
    /**
     * The keyWordMap.
     */
    private static final Map<String, Object> KEYWORDS = createKeyWordMap();

    /**
     * The parent container.
     */
    private final ThemisAnalysisContainer theParent;

    /**
     * The list of source lines.
     */
    private final List<ThemisAnalysisElement> theLines;

    /**
     * The list of output contents lines.
     */
    private final List<ThemisAnalysisElement> theContents;

    /**
     * The dataTypeMap.
     */
    private final Map<String, ThemisAnalysisDataType> theDataTypes;

    /**
     * Constructor.
     * @param pLines the source lines.
     * @param pContents the processed contents
     */
    ThemisAnalysisParser(final List<ThemisAnalysisElement> pLines,
                         final List<ThemisAnalysisElement> pContents) {
        this(pLines, pContents, null);
    }

    /**
     * Constructor.
     * @param pLines the source lines.
     * @param pContents the processed contents
     * @param pContainer the container
     */
    ThemisAnalysisParser(final List<ThemisAnalysisElement> pLines,
                         final List<ThemisAnalysisElement> pContents,
                         final ThemisAnalysisContainer pContainer) {
        /* Store parameters */
        theLines = pLines;
        theContents = pContents;
        theParent = pContainer;

        /* Create the dataTypeMap */
        theDataTypes = pContainer == null
                      ? createDataTypeMap()
                      : pContainer.getDataTypes();
    }

    /**
     * Constructor.
     * @param pParser the source parser.
     * @param pProcessed the processed output
     */
    ThemisAnalysisParser(final ThemisAnalysisParser pParser,
                         final List<ThemisAnalysisElement> pProcessed) {
        this(pParser.theLines, pProcessed, pParser.getParent());
    }

    /**
     * Are there more lines to process?
     * @return true/false
     */
    boolean hasLines() {
        return !theLines.isEmpty();
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
    Map<String, ThemisAnalysisDataType> getDataTypes() {
        return theDataTypes;
    }

    /**
     * Pop next line from list.
     * @return the next line
     */
    ThemisAnalysisElement popNextLine() {
        /* Check that there is a line to pop */
        if (theLines.isEmpty()) {
            throw new IllegalStateException();
        }

        /* Access the first line and remove from the list */
        final ThemisAnalysisElement myLine = theLines.get(0);
        theLines.remove(0);

        /* return the line */
        return myLine;
    }

    /**
     * Peek next line from list.
     * @return the next line
     */
    ThemisAnalysisElement peekNextLine() {
        /* Check that there is a line to pop */
        if (theLines.isEmpty()) {
            throw new IllegalStateException();
        }

        /* Return the first line in the list */
        return theLines.get(0);
    }

    /**
     * Push line back onto stack.
     * @param pLine to line to push onto stack
     */
    void pushLine(final ThemisAnalysisElement pLine) {
        /* Insert the line at the front of the stack */
        theLines.add(0, pLine);
    }

    /**
     * Process a potential comment/blank line.
     * @param pLine the line
     * @return have we processed the line?
     */
    boolean processCommentsAndBlanks(final ThemisAnalysisLine pLine) {
        /* If this is a starting comment */
        if (ThemisAnalysisComment.isStartComment(pLine)) {
            /* Process the comment lines */
            final ThemisAnalysisComment myComment = new ThemisAnalysisComment(this, pLine);
            theContents.add(myComment);
            return true;
        }

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
     */
    boolean processImports(final ThemisAnalysisLine pLine) {
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
     */
    boolean processClass(final ThemisAnalysisLine pLine) {
        /* Access class type */
        final String myToken = pLine.peekNextToken();
        final Object myType = KEYWORDS.get(myToken);

        /* If we have a keyWord */
        if (myType instanceof ThemisAnalysisKeyWord) {
            /* If this is a class */
            switch ((ThemisAnalysisKeyWord) myType) {
                case CLASS:
                    /* Create the class */
                    pLine.stripStartSequence(myToken);
                    theContents.add(new ThemisAnalysisClass(this, pLine));
                    return true;

                /* If this is an interface */
                case INTERFACE:
                    /* Create the interface */
                    pLine.stripStartSequence(myToken);
                    theContents.add(new ThemisAnalysisInterface(this, pLine));
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
     */
    boolean processLanguage(final ThemisAnalysisLine pLine) {
        /* Access class type */
        final String myToken = pLine.peekNextToken();
        final Object myType = KEYWORDS.get(myToken);

        /* If we have a keyWord */
        if (myType instanceof ThemisAnalysisKeyWord) {
            /* Switch on the type */
            switch ((ThemisAnalysisKeyWord) myType) {
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

            /* Else handle an initializer block */
        } else if (ThemisAnalysisBuilder.BRACE_OPEN.equals(myToken)) {
            /* Create the block */
            theContents.add(new ThemisAnalysisBlock(this, pLine));
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
    boolean processCase(final ThemisAnalysisLine pLine) {
        /* Access case type */
        final Object myCase = parseCase(pLine);

        /* If we have a case */
        if (myCase != null) {
            theContents.add(new ThemisAnalysisCase(this, myCase));
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
        /* Access case type */
        final String myToken = pLine.peekNextToken();
        final Object myType = KEYWORDS.get(myToken);

        /* If we have a keyWord */
        if (myType instanceof ThemisAnalysisKeyWord) {
            /* If this is a case/default */
            final ThemisAnalysisKeyWord myKeyWord = (ThemisAnalysisKeyWord) myType;
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
     * @param pKeyWord the keyWord
     * @return have we processed the line?
     */
    ThemisAnalysisElement processExtra(final ThemisAnalysisKeyWord pKeyWord) {
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
                    return new ThemisAnalysisElse(this, myLine);

                /* If this is a catch */
                case CATCH:
                    /* Create the switch */
                    myLine.stripStartSequence(myToken);
                    return new ThemisAnalysisCatch(this, myLine);

                /* If this is a finally */
                case FINALLY:
                    /* Create the finally */
                    myLine.stripStartSequence(myToken);
                    return new ThemisAnalysisFinally(this, myLine);

                default:
                    break;
            }
        }

        /* Not processed */
        pushLine(myLine);
        return null;
    }

    /**
     * Process field and method constructs.
     * @param pLine the line
     * @return the field/method or null
     */
    ThemisAnalysisElement processFieldsAndMethods(final ThemisAnalysisLine pLine) {
        /* Look for a reference */
        final ThemisAnalysisReference myReference = parseDataType(pLine);
        if (myReference != null) {
            /* Access the name of the field or method */
            final String myName = pLine.stripNextToken();
            final boolean isMethod = pLine.startsWithSequence("(");
            if (!isMethod) {
                return new ThemisAnalysisField(this, myName, myReference, pLine);
            } else {
                return new ThemisAnalysisMethod(this, myName, myReference, pLine);
            }
        }

        /* Not processed */
        return null;
    }

    /**
     * Process field and method constructs.
     * @param pLine the line
     * @return have we processed the line?
     */
    ThemisAnalysisReference parseDataType(final ThemisAnalysisLine pLine) {
        /* Make sure that we are not started by a keyword */
        final String myToken = pLine.peekNextToken();
        if (KEYWORDS.get(myToken) != null) {
            return null;
        }

        /* Look to for a valid daatType */
        final ThemisAnalysisDataType myType = theDataTypes.get(myToken);
        if (myType == null) {
            return null;
        }
        pLine.stripStartSequence(myToken);

        /* Access any generic/array detail */
        final ThemisAnalysisGeneric myGeneric = ThemisAnalysisGeneric.isGeneric(pLine)
                                                ? new ThemisAnalysisGeneric(pLine)
                                                : null;
        final ThemisAnalysisArray myArray = ThemisAnalysisArray.isArray(pLine)
                                                ? new ThemisAnalysisArray(pLine)
                                                : null;

        /* Return the reference */
        return new ThemisAnalysisReference(myType, myGeneric, myArray);
    }

    /**
     * process the lines.
     */
    void processLines() {
        /* Loop through the lines */
        while (hasLines()) {
            /* Access next line */
            final ThemisAnalysisLine myLine = (ThemisAnalysisLine) popNextLine();

            /* Process comments and blanks */
            boolean processed = processCommentsAndBlanks(myLine);

            /* Process language constructs */
            if (!processed) {
                processed = processLanguage(myLine);
            }

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
     * Create the dataTypeMap.
     * @return the new map
     */
    private static Map<String, ThemisAnalysisDataType> createDataTypeMap() {
        /* create the map */
        final Map<String, ThemisAnalysisDataType> myMap = new HashMap<>();

        /* Add the primitives */
        for (ThemisAnalysisPrimitive myPrimitive : ThemisAnalysisPrimitive.values()) {
            myMap.put(myPrimitive.toString(), myPrimitive);
            if (myPrimitive.getBoxed() != null) {
                myMap.put(myPrimitive.getBoxed(), myPrimitive);
            }
        }

        /* return the map */
        return myMap;
    }
}
