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
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.Type;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.exc.ThemisDataException;
import net.sourceforge.joceanus.themis.exc.ThemisIOException;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisChar;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisDeclarationInstance;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisExpressionInstance;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisNodeInstance;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisStatementInstance;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisTypeInstance;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisParser;
import net.sourceforge.joceanus.themis.xanalysis.decl.ThemisXAnalysisDeclaration;
import net.sourceforge.joceanus.themis.xanalysis.expr.ThemisXAnalysisExpression;
import net.sourceforge.joceanus.themis.xanalysis.node.ThemisXAnalysisNode;
import net.sourceforge.joceanus.themis.xanalysis.stmt.ThemisXAnalysisStatement;
import net.sourceforge.joceanus.themis.xanalysis.type.ThemisXAnalysisType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Code Parser.
 */
public class ThemisXAnalysisCodeParser
        implements ThemisXAnalysisParser {
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
    }

    /**
     * Set the current module
     * @param pModule the module
     */
    public void setCurrentModule(final String pModule) {
        theModule = pModule;
    }

    /**
     * Set the current package
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
    }

    /**
     * Process the file.
     * @return the parsed compilation unit
     * @throws OceanusException on error
     */
    public ThemisXAnalysisNodeInstance parseFile() throws OceanusException {
        /* Protect against exceptions */
        try (InputStream myStream = new FileInputStream(theCurrentFile);
             InputStreamReader myInputReader = new InputStreamReader(myStream, StandardCharsets.UTF_8);
             BufferedReader myReader = new BufferedReader(myInputReader)) {

            /* Parse the contents */
            final JavaParser myParser = new JavaParser();
            myParser.getParserConfiguration().setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_21);
            final ParseResult<CompilationUnit> myUnit = myParser.parse(myStream);
            if (!myUnit.isSuccessful()) {
                final Problem myProblem = myUnit.getProblem(0);
                throw new ThemisDataException(myProblem.getVerboseMessage());
            }
            return parseNode(myUnit.getResult().orElse(null));

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
        return ThemisXAnalysisDeclaration.parseDeclaration(this, pDecl);
    }

    @Override
    public ThemisXAnalysisNodeInstance parseNode(final Node pNode) throws OceanusException {
        return ThemisXAnalysisNode.parseNode(this, pNode);
    }

    @Override
    public ThemisXAnalysisTypeInstance parseType(final Type pType) throws OceanusException {
        return ThemisXAnalysisType.parseType(this, pType);
    }

    @Override
    public ThemisXAnalysisStatementInstance parseStatement(final Statement pStatement) throws OceanusException {
        return ThemisXAnalysisStatement.parseStatement(this, pStatement);
    }

    @Override
    public ThemisXAnalysisExpressionInstance parseExpression(final Expression pExpr) throws OceanusException {
        return ThemisXAnalysisExpression.parseExpression(this, pExpr);
    }
}
