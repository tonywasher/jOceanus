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
     * The list of source lines.
     */
    private final List<ThemisAnalysisLine> theLines;

    /**
     * The list of output lines.
     */
    private final List<ThemisAnalysisElement> theProcessed;

    /**
     * The keyWordMap.
     */
    private final Map<String, Object> theKeyWords;

    /**
     * The dataTypeMap.
     */
    private final Map<String, Object> theDataTypes;

    /**
     * Constructor.
     * @param pLines the lines.
     * @param pProcessed the processed output
     */
    ThemisAnalysisParser(final List<ThemisAnalysisLine> pLines,
                         final List<ThemisAnalysisElement> pProcessed) {
        this(pLines, pProcessed, null);
    }

    /**
     * Constructor.
     * @param pLines the lines.
     * @param pProcessed the processed output
     * @param pParser the underlying parser
     */
    ThemisAnalysisParser(final List<ThemisAnalysisLine> pLines,
                         final List<ThemisAnalysisElement> pProcessed,
                         final ThemisAnalysisParser pParser) {
        /* Store parameters */
        theLines = pLines;
        theProcessed = pProcessed;

        /* Create the keyWordMap */
        theKeyWords = pParser == null
                        ? createKeyWordMap()
                        : pParser.getKeyWords();

        /* Create the dataTypeMap */
        theDataTypes = pParser == null
                      ? createDataTypeMap()
                      : pParser.getDataTypes();
    }

    /**
     * Are there more lines to process?
     * @return true/false
     */
    boolean hasLines() {
        return !theLines.isEmpty();
    }

    /**
     * Obtain the keyWord map?
     * @return the keyWordMap
     */
    Map<String, Object> getKeyWords() {
        return theKeyWords;
    }

    /**
     * Obtain the dataTypes map?
     * @return the dataTypesMap
     */
    Map<String, Object> getDataTypes() {
        return theDataTypes;
    }

    /**
     * Pop next line from list.
     * @return the next line
     */
    ThemisAnalysisLine popNextLine() {
        /* Check that there is a line to pop */
        if (theLines.isEmpty()) {
            throw new IllegalStateException();
        }

        /* Access the first line and remove from the list */
        final ThemisAnalysisLine myLine = theLines.get(0);
        theLines.remove(0);

        /* return the line */
        return myLine;
    }

    /**
     * Peek next line from list.
     * @return the next line
     */
    ThemisAnalysisLine peekNextLine() {
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
    void pushLine(final ThemisAnalysisLine pLine) {
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
            theProcessed.add(myComment);
            return true;
        }

        /* If this is a blank line */
        if (ThemisAnalysisBlank.isBlank(pLine)) {
            /* Process the blank lines */
            final ThemisAnalysisBlank myBlank = new ThemisAnalysisBlank(this, pLine);
            theProcessed.add(myBlank);
            return true;
        }

        /* If this is an annotation line */
        if (ThemisAnalysisAnnotation.isAnnotation(pLine)) {
            /* Process the annotation lines */
            final ThemisAnalysisAnnotation myAnnotation = new ThemisAnalysisAnnotation(this, pLine);
            theProcessed.add(myAnnotation);
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
            theProcessed.add(myImports);
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
        final Object myType = theKeyWords.get(myToken);

        /* If we have a keyWord */
        if (myType instanceof ThemisAnalysisKeyWord) {
            /* If this is a class */
            switch ((ThemisAnalysisKeyWord) myType) {
                case CLASS:
                    /* Create the class */
                    pLine.stripStartSequence(myToken);
                     theProcessed.add(new ThemisAnalysisClass(this, pLine));
                    return true;

                /* If this is an interface */
                case INTERFACE:
                    /* Create the interface */
                    pLine.stripStartSequence(myToken);
                    theProcessed.add(new ThemisAnalysisInterface(this, pLine));
                    return true;

                /* If this is an enum */
                case ENUM:
                    /* Create the enum */
                    pLine.stripStartSequence(myToken);
                    theProcessed.add(new ThemisAnalysisEnum(this, pLine));
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
        final Object myType = theKeyWords.get(myToken);

        /* If we have a keyWord */
        if (myType instanceof ThemisAnalysisKeyWord) {
            /* Switch on the type */
            switch ((ThemisAnalysisKeyWord) myType) {
                /* If this is a while */
                case WHILE:
                    /* Create the while */
                    pLine.stripStartSequence(myToken);
                    theProcessed.add(new ThemisAnalysisWhile(this, pLine));
                    return true;

                /* If this is a doWhile */
                case DO:
                    /* Create the while */
                    pLine.stripStartSequence(myToken);
                    theProcessed.add(new ThemisAnalysisDoWhile(this));
                    return true;

                /* If this is a switch */
                case SWITCH:
                    /* Create the switch */
                    pLine.stripStartSequence(myToken);
                    theProcessed.add(new ThemisAnalysisSwitch(this, pLine));
                    return true;

                /* If this is a for */
                case FOR:
                    /* Create the for */
                    pLine.stripStartSequence(myToken);
                    theProcessed.add(new ThemisAnalysisFor(this, pLine));
                    return true;

                /* If this is an if */
                case IF:
                    /* Create the if */
                    pLine.stripStartSequence(myToken);
                    theProcessed.add(new ThemisAnalysisIf(this, pLine));
                    return true;

                /* If this is a try */
                case TRY:
                    /* Create the try */
                    pLine.stripStartSequence(myToken);
                    theProcessed.add(new ThemisAnalysisTry(this, pLine));
                    return true;

                default:
                    break;
            }
        }

        /* Not processed */
        return false;
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
        final ThemisAnalysisLine myLine = popNextLine();
        final String myToken = myLine.peekNextToken();
        final Object myType = theKeyWords.get(myToken);

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
     * Post-process the lines.
     */
    void postProcessLines() {
        /* Loop through the lines */
        while (hasLines()) {
            /* Access next line */
            final ThemisAnalysisLine myLine = popNextLine();

            /* Process comments and blanks */
            boolean processed = processCommentsAndBlanks(myLine);

            /* Process language constructs */
            if (!processed) {
                processed = processLanguage(myLine);
            }

            /* If we haven't processed yet */
            if (!processed) {
                /* Just add the line to processed at present */
                theProcessed.add(myLine);
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

    /**
     * Create the dataTypeMap.
     * @return the new map
     */
    private static Map<String, Object> createDataTypeMap() {
        /* create the map */
        final Map<String, Object> myMap = new HashMap<>();

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
