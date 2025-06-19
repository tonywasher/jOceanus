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
package net.sourceforge.joceanus.themis.lethe.analysis;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.lethe.analysis.ThemisAnalysisContainer.ThemisAnalysisAdoptable;
import net.sourceforge.joceanus.themis.lethe.analysis.ThemisAnalysisFile.ThemisAnalysisObject;

/**
 * Anonymous Class.
 */
public class ThemisAnalysisAnonClass
        implements ThemisAnalysisObject, ThemisAnalysisAdoptable {
    /**
     * The anon sequence.
     */
    static final String ANON = "() " + ThemisAnalysisChar.BRACE_OPEN;

    /**
     * The diamond sequence.
     */
    static final String DIAMOND = "<>";

    /**
     * The full name of the class.
     */
    private final String theFullName;

    /**
     * The base name of the class.
     */
    private final String theBaseName;

    /**
     * The ancestors.
     */
    private final List<ThemisAnalysisReference> theAncestors;

    /**
     * The contents.
     */
    private final Deque<ThemisAnalysisElement> theContents;

    /**
     * The Parent.
     */
    private ThemisAnalysisContainer theParent;

    /**
     * The dataMap.
     */
    private final ThemisAnalysisDataMap theDataMap;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pLine the initial class line
     * @throws OceanusException on error
     */
    ThemisAnalysisAnonClass(final ThemisAnalysisParser pParser,
                            final ThemisAnalysisLine pLine) throws OceanusException {
        /* Strip the trailer */
        pLine.stripEndSequence(ANON);

        /* Check for diamonds */
        if (pLine.endsWithSequence(DIAMOND)) {
            pLine.stripEndSequence(DIAMOND);
        }

        /* Access the base of the anonymous class and strip the new keyword */
        theBaseName = pLine.stripLastToken();
        pLine.stripEndSequence(ThemisAnalysisKeyWord.NEW.getKeyWord());

        /* Access parent dataMap */
        theParent = pParser.getParent();
        final ThemisAnalysisDataMap myParentDataMap = theParent.getDataMap();

        /* Determine the full name */
        final int myId = myParentDataMap.getLocalId("");
        theFullName = theParent.determineFullChildName(Integer.toString(myId));

        /* Create local dataMap */
        theDataMap = new ThemisAnalysisDataMap(myParentDataMap);

        /* Parse the body */
        final Deque<ThemisAnalysisElement> myLines = ThemisAnalysisBuilder.processBody(pParser);

        /* Create a parser */
        theContents = new ArrayDeque<>();
        final ThemisAnalysisParser myParser = new ThemisAnalysisParser(myLines, theContents, this);

        /* Parse the ancestor and lines */
        final ThemisAnalysisLine myBaseLine = new ThemisAnalysisLine(theBaseName.toCharArray(), 0, theBaseName.length());
        final ThemisAnalysisReference myAncestor = ThemisAnalysisParser.parseDataType(theDataMap, myBaseLine);
        theAncestors = Collections.singletonList(myAncestor);
        myParser.processLines();
    }

    @Override
    public String getShortName() {
        return toString();
    }

    @Override
    public String getFullName() {
        return theFullName;
    }

    @Override
    public ThemisAnalysisProperties getProperties() {
        return null;
    }

    @Override
    public Deque<ThemisAnalysisElement> getContents() {
        return theContents;
    }

    @Override
    public ThemisAnalysisContainer getParent() {
        return this;
    }

    @Override
    public void setParent(final ThemisAnalysisContainer pParent) {
        theParent = pParent;
        theDataMap.setParent(pParent.getDataMap());
    }

    @Override
    public ThemisAnalysisDataMap getDataMap() {
        return theDataMap;
    }

    @Override
    public List<ThemisAnalysisReference> getAncestors() {
        return theAncestors;
    }

    @Override
    public int getNumLines() {
        return 0;
    }

    @Override
    public String toString() {
        return ThemisAnalysisKeyWord.NEW.toString() + " " + theBaseName + "()";
    }

    /**
     * Check for anonymous class.
     * @param pLine the line to check
     * @return true/false
     */
    static boolean checkAnon(final ThemisAnalysisLine pLine) {
        /* Check for possible anonymous class */
        if (!pLine.endsWithSequence(ANON)) {
            return false;
        }

        /* Take a copy of the line and strip the trailer */
        final ThemisAnalysisLine myLine = new ThemisAnalysisLine(pLine);
        myLine.stripEndSequence(ANON);

        /* Check for diamonds */
        if (myLine.endsWithSequence(DIAMOND)) {
            myLine.stripEndSequence(DIAMOND);
        }

        /* Check that this is an anonymous class */
        myLine.stripLastToken();
        final String myKey = myLine.peekLastToken();
        return ThemisAnalysisKeyWord.NEW.getKeyWord().equals(myKey);
    }
}
