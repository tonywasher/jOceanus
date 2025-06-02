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
package net.sourceforge.joceanus.themis.xanalysis.parser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.Position;
import com.github.javaparser.Problem;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.modules.ModuleDeclaration;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.Type;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.exc.ThemisDataException;
import net.sourceforge.joceanus.themis.exc.ThemisIOException;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisChar;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisDeclarationInstance;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisExpressionInstance;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisModuleInstance;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisNodeInstance;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisStatementInstance;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisTypeInstance;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisParser;
import net.sourceforge.joceanus.themis.xanalysis.decl.ThemisXAnalysisDeclaration;
import net.sourceforge.joceanus.themis.xanalysis.expr.ThemisXAnalysisExpression;
import net.sourceforge.joceanus.themis.xanalysis.mod.ThemisXAnalysisMod;
import net.sourceforge.joceanus.themis.xanalysis.mod.ThemisXAnalysisModModule;
import net.sourceforge.joceanus.themis.xanalysis.node.ThemisXAnalysisNode;
import net.sourceforge.joceanus.themis.xanalysis.node.ThemisXAnalysisNodeCompilationUnit;
import net.sourceforge.joceanus.themis.xanalysis.stmt.ThemisXAnalysisStatement;
import net.sourceforge.joceanus.themis.xanalysis.type.ThemisXAnalysisType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Code Parser.
 */
public class ThemisXAnalysisCodeParser
        implements ThemisXAnalysisParser {
    /**
     * The parser.
     */
    private final JavaParser theParser;

    /**
     * The stack of the nodes that are being parsed.
     */
    private final Deque<ThemisXAnalysisInstance> theNodes;

    /**
     * The current module being parsed.
     */
    private String theModule;

    /**
     * The current package being parsed.
     */
    private String thePackage;

    /**
     * The File being parsed.
     */
    private File theCurrentFile;

    /**
     * Constructor.
     */
    public ThemisXAnalysisCodeParser() {
        theParser = new JavaParser();
        theParser.getParserConfiguration().setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_21);
        theNodes = new ArrayDeque<>();
    }

    /**
     * Set the current module.
     * @param pModule the module
     */
    public void setCurrentModule(final String pModule) {
        theModule = pModule;
    }

    /**
     * Set the current package.
     * @param pPackage the package
     */
    public void setCurrentPackage(final String pPackage) {
        thePackage = pPackage;
    }

    /**
     * Set the current file.
     * @param pFile the file
     */
    public void setCurrentFile(final File pFile) {
        theCurrentFile = pFile;
    }

    @Override
    public void registerInstance(final ThemisXAnalysisInstance pInstance) {
        /* Register with parent unless top-level node */
        final ThemisXAnalysisInstance myParent = theNodes.peekLast();
        if (myParent != null) {
            myParent.registerChild(pInstance);
        }

        /* Add to end of queue */
        theNodes.addLast(pInstance);
    }

    /**
     * Deregister instance.
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
        }
    }

    /**
     * Process the file as javaCode.
     * @return the parsed compilation unit
     * @throws OceanusException on error
     */
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

    /**
     * Process the file as a module-info instance.
     * @param pInfoFile the module-info file
     * @return the parsed moduleInfo
     * @throws OceanusException on error
     */
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
        final ThemisXAnalysisDeclarationInstance myInstance = ThemisXAnalysisDeclaration.parseDeclaration(this, pDecl);
        deRegisterInstance(myInstance);
        return myInstance;
    }

    @Override
    public ThemisXAnalysisNodeInstance parseNode(final Node pNode) throws OceanusException {
        final ThemisXAnalysisNodeInstance myInstance = ThemisXAnalysisNode.parseNode(this, pNode);
        deRegisterInstance(myInstance);
        return myInstance;
    }

    @Override
    public ThemisXAnalysisTypeInstance parseType(final Type pType) throws OceanusException {
        final ThemisXAnalysisTypeInstance myInstance = ThemisXAnalysisType.parseType(this, pType);
        deRegisterInstance(myInstance);
        return myInstance;
    }

    @Override
    public ThemisXAnalysisStatementInstance parseStatement(final Statement pStatement) throws OceanusException {
        final ThemisXAnalysisStatementInstance myInstance = ThemisXAnalysisStatement.parseStatement(this, pStatement);
        deRegisterInstance(myInstance);
        return myInstance;
    }

    @Override
    public ThemisXAnalysisExpressionInstance parseExpression(final Expression pExpr) throws OceanusException {
        final ThemisXAnalysisExpressionInstance myInstance = ThemisXAnalysisExpression.parseExpression(this, pExpr);
        deRegisterInstance(myInstance);
        return myInstance;
    }

    @Override
    public ThemisXAnalysisModuleInstance parseModule(final Node pDecl) throws OceanusException {
        final ThemisXAnalysisModuleInstance myInstance = ThemisXAnalysisMod.parseModule(this, pDecl);
        deRegisterInstance(myInstance);
        return myInstance;
    }
}
