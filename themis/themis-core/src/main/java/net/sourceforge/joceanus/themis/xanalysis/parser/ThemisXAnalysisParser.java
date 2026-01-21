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
package net.sourceforge.joceanus.themis.xanalysis.parser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.Position;
import com.github.javaparser.Problem;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.modules.ModuleDeclaration;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.Type;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.exc.ThemisDataException;
import net.sourceforge.joceanus.themis.exc.ThemisIOException;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisChar;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisClassInstance;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisDeclarationInstance;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisExpressionInstance;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisModuleInstance;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisNodeInstance;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisStatementInstance;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisTypeInstance;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisModifierList;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;
import net.sourceforge.joceanus.themis.xanalysis.parser.decl.ThemisXAnalysisDeclParser;
import net.sourceforge.joceanus.themis.xanalysis.parser.expr.ThemisXAnalysisExprParser;
import net.sourceforge.joceanus.themis.xanalysis.parser.mod.ThemisXAnalysisModModule;
import net.sourceforge.joceanus.themis.xanalysis.parser.mod.ThemisXAnalysisModParser;
import net.sourceforge.joceanus.themis.xanalysis.parser.node.ThemisXAnalysisNodeCompilationUnit;
import net.sourceforge.joceanus.themis.xanalysis.parser.node.ThemisXAnalysisNodeParser;
import net.sourceforge.joceanus.themis.xanalysis.parser.proj.ThemisXAnalysisProject;
import net.sourceforge.joceanus.themis.xanalysis.parser.stmt.ThemisXAnalysisStmtParser;
import net.sourceforge.joceanus.themis.xanalysis.parser.type.ThemisXAnalysisTypeParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Code Parser.
 */
public class ThemisXAnalysisParser
        implements ThemisXAnalysisParserDef {
    /**
     * The underlying parser.
     */
    private final JavaParser theParser;

    /**
     * The stack of the nodes that are being parsed.
     */
    private final Deque<ThemisXAnalysisInstance> theNodes;

    /**
     * The current package being parsed.
     */
    private String thePackage;

    /**
     * The File being parsed.
     */
    private File theCurrentFile;

    /**
     * The List of classes in a file.
     */
    private final List<ThemisXAnalysisClassInstance> theClasses;

    /**
     * The Class Stack.
     */
    private final Deque<String> theClassStack;

    /**
     * The Current class index.
     */
    private int theClassIndex;

    /**
     * The project.
     */
    private ThemisXAnalysisProject theProject;

    /**
     * The Error.
     */
    private OceanusException theError;

    /**
     * Constructor.
     *
     * @param pLocation the project location
     */
    public ThemisXAnalysisParser(final File pLocation) {
        /* Initialise the parser */
        theParser = new JavaParser();
        theNodes = new ArrayDeque<>();
        theClassStack = new ArrayDeque<>();
        theClasses = new ArrayList<>();

        /* Protect against exceptions */
        try {
            /* Prepare the project */
            theProject = new ThemisXAnalysisProject(pLocation);

            /* Configure the parser */
            configureParser();

            /* Parse the javaCode */
            theProject.parseJavaCode(this);

            /* Store any exception */
        } catch (OceanusException e) {
            theError = e;
            theProject = null;
        }
    }

    /**
     * Obtain the project.
     *
     * @return the project
     */
    public ThemisXAnalysisProject getProject() {
        return theProject;
    }

    /**
     * Obtain the error.
     *
     * @return the error
     */
    public OceanusException getError() {
        return theError;
    }

    @Override
    public List<ThemisXAnalysisClassInstance> getClasses() {
        return theClasses;
    }

    /**
     * Configure the parser.
     */
    private void configureParser() {
        /* Access the parser */
        final ParserConfiguration myConfig = theParser.getParserConfiguration();
        myConfig.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_21);
    }

    @Override
    public void setCurrentPackage(final String pPackage) {
        thePackage = pPackage;
    }

    @Override
    public void setCurrentFile(final File pFile) {
        theCurrentFile = pFile;
        theClasses.clear();
        theClassStack.clear();
        theClassIndex = 0;
    }

    @Override
    public ThemisXAnalysisInstance registerInstance(final ThemisXAnalysisInstance pInstance) {
        /* Register with parent unless top-level node */
        final ThemisXAnalysisInstance myParent = theNodes.peekLast();
        if (myParent != null) {
            myParent.registerChild(pInstance);
        }

        /* Add to end of queue */
        theNodes.addLast(pInstance);
        return myParent;
    }

    /**
     * Deregister instance.
     *
     * @param pInstance the instance to deRegister
     */
    private void deRegisterInstance(final ThemisXAnalysisInstance pInstance) {
        /* If the instance is non-null */
        if (pInstance != null) {
            /* If it matches the current node */
            final ThemisXAnalysisInstance myCurrent = theNodes.peekLast();
            if (pInstance.equals(myCurrent)) {
                /* Remove from queue */
                theNodes.removeLast();
            }
            if (pInstance instanceof ThemisXAnalysisClassInstance) {
                theClassStack.removeLast();
            }
        }
    }

    @Override
    public String registerClass(final ThemisXAnalysisClassInstance pClass) {
        /* Add the class to the list */
        theClasses.add(pClass);

        /* Determine the name of the class */
        String myFullName = pClass.getFullName();
        if (myFullName == null) {
            final String myCurrentName = theClassStack.peekLast();
            myFullName = myCurrentName
                    + ThemisXAnalysisChar.PERIOD
                    + ThemisXAnalysisChar.DOLLAR
                    + ++theClassIndex;
            if (pClass.isLocalDeclaration()) {
                myFullName += pClass.getName();
            }
        }

        /* Add to the class stack */
        theClassStack.addLast(myFullName);

        /* Return the fullName */
        return myFullName;
    }

    @Override
    public ThemisXAnalysisNodeCompilationUnit parseJavaFile() throws OceanusException {
        /* Protect against exceptions */
        try (InputStream myStream = new FileInputStream(theCurrentFile)) {
            /* Parse the contents */
            final ParseResult<CompilationUnit> myUnit = theParser.parse(myStream);
            if (!myUnit.isSuccessful()) {
                final Problem myProblem = myUnit.getProblem(0);
                throw new ThemisDataException(myProblem.getVerboseMessage());
            }
            return (ThemisXAnalysisNodeCompilationUnit) parseNode(myUnit.getResult().orElse(null));

            /* Catch exceptions */
        } catch (IOException e) {
            /* Throw an exception */
            throw new ThemisIOException("Failed to load file "
                    + theCurrentFile.getAbsolutePath(), e);
        }
    }

    @Override
    public ThemisXAnalysisModModule parseModuleInfo(final File pInfoFile) throws OceanusException {
        /* Protect against exceptions */
        setCurrentFile(pInfoFile);
        try (InputStream myStream = new FileInputStream(theCurrentFile)) {
            /* Parse the contents */
            final String myText = new String(myStream.readAllBytes(), StandardCharsets.UTF_8);
            final ParseResult<ModuleDeclaration> myDecl = theParser.parseModuleDeclaration(myText);
            if (!myDecl.isSuccessful()) {
                final Problem myProblem = myDecl.getProblem(0);
                throw new ThemisDataException(myProblem.getVerboseMessage());
            }
            return (ThemisXAnalysisModModule) parseModule(myDecl.getResult().orElse(null));

            /* Catch exceptions */
        } catch (IOException e) {
            /* Throw an exception */
            throw new ThemisIOException("Failed to load file "
                    + theCurrentFile.getAbsolutePath(), e);
        }
    }

    @Override
    public OceanusException buildException(final String pMessage,
                                           final Node pNode) {
        /* Determine location of error */
        final Position myPos = pNode.getBegin().orElse(null);
        final String myLocation = pNode.getClass().getCanonicalName()
                + (myPos == null ? "" : ThemisXAnalysisChar.PARENTHESIS_OPEN
                + myPos.line
                + ThemisXAnalysisChar.COLON
                + myPos.column
                + ThemisXAnalysisChar.PARENTHESIS_CLOSE);

        /* Build full error message */
        final String myMsg = pMessage
                + ThemisXAnalysisChar.LF
                + myLocation
                + ThemisXAnalysisChar.LF
                + theCurrentFile.getAbsolutePath();

        /* Create exception */
        return new ThemisDataException(myMsg);
    }

    /**
     * Check the package name.
     *
     * @param pPackage the package name
     * @throws OceanusException on error
     */
    public void checkPackage(final PackageDeclaration pPackage) throws OceanusException {
        /* Check that package matches */
        if (!thePackage.equals(pPackage.getNameAsString())) {
            throw buildException("Mismatch on package", pPackage);
        }
    }

    @Override
    public ThemisXAnalysisDeclarationInstance parseDeclaration(final BodyDeclaration<?> pDecl) throws OceanusException {
        final ThemisXAnalysisDeclarationInstance myInstance = ThemisXAnalysisDeclParser.parseDeclaration(this, pDecl);
        deRegisterInstance(myInstance);
        return myInstance;
    }

    @Override
    public ThemisXAnalysisNodeInstance parseNode(final Node pNode) throws OceanusException {
        final ThemisXAnalysisNodeInstance myInstance = ThemisXAnalysisNodeParser.parseNode(this, pNode);
        deRegisterInstance(myInstance);
        return myInstance;
    }

    @Override
    public ThemisXAnalysisTypeInstance parseType(final Type pType) throws OceanusException {
        final ThemisXAnalysisTypeInstance myInstance = ThemisXAnalysisTypeParser.parseType(this, pType);
        deRegisterInstance(myInstance);
        return myInstance;
    }

    @Override
    public ThemisXAnalysisStatementInstance parseStatement(final Statement pStatement) throws OceanusException {
        final ThemisXAnalysisStatementInstance myInstance = ThemisXAnalysisStmtParser.parseStatement(this, pStatement);
        deRegisterInstance(myInstance);
        return myInstance;
    }

    @Override
    public ThemisXAnalysisExpressionInstance parseExpression(final Expression pExpr) throws OceanusException {
        final ThemisXAnalysisExpressionInstance myInstance = ThemisXAnalysisExprParser.parseExpression(this, pExpr);
        deRegisterInstance(myInstance);
        return myInstance;
    }

    @Override
    public ThemisXAnalysisModuleInstance parseModule(final Node pMod) throws OceanusException {
        final ThemisXAnalysisModuleInstance myInstance = ThemisXAnalysisModParser.parseModule(this, pMod);
        deRegisterInstance(myInstance);
        return myInstance;
    }

    @Override
    public ThemisXAnalysisModifierList parseModifierList(final NodeList<? extends Node> pNodeList) throws OceanusException {
        return ThemisXAnalysisNodeParser.parseModifierList(this, pNodeList);
    }
}
