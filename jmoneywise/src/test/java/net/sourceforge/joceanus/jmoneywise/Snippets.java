/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise;

import java.io.OutputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseList;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.DataValues;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Code snippets for future usage.
 */
public class Snippets {
    /**
     * create an XML document for a DataList.
     * @param pList the list
     * @return the document
     * @throws ParserConfigurationException on error
     */
    public Document createXMLList(final DataList<?, MoneyWiseList> pList) throws ParserConfigurationException {
        /* Create the new document */
        DocumentBuilderFactory myFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder myBuilder = myFactory.newDocumentBuilder();
        Document myDocument = myBuilder.newDocument();

        /**
         * TODO note that we should allow the list to optimise the output to allow for grouping.
         * <p>
         * Also note that we should omit ControlId. Since encryption will be applied to the resulting stream.
         */
        /* Create the standard XML */
        Element myDoc = DataValues.createXML(myDocument, pList);
        myDocument.appendChild(myDoc);
        return myDocument;
    }

    /**
     * create and format an XML document for a DataList.
     * @param pList the list
     * @param pStream the stream to write to
     */
    public void writeXMLList(final DataList<?, MoneyWiseList> pList,
                             final OutputStream pStream) {
        try {
            Document myDocument = createXMLList(pList);

            /* Create the transformer */
            TransformerFactory myXformFactory = TransformerFactory.newInstance();
            Transformer myXformer = myXformFactory.newTransformer();
            myXformer.transform(new DOMSource(myDocument), new StreamResult(pStream));
        } catch (Exception e) {

        }
    }

    /**
     * parse an XML document.
     * @param pDocument the document to parse
     * @return the list of values
     */
    public List<DataValues> parseXMLList(final Document pDocument) {
        /* Access the document element */
        Element myData = pDocument.getDocumentElement();

        /**
         * TODO we need to find a way of extracting the DataFields for a list from the list itself rather than via an element.
         */
        /* Obtain the list of data values */
        JDataFields myFields = null;
        List<DataValues> myResult = DataValues.parseXML(myData, myFields);
        return myResult;
    }

}
