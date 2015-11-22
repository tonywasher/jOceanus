/*******************************************************************************
 * jPrometheus: Application Framework
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
package net.sourceforge.joceanus.jprometheus.data;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.manager.GordianHashManager;
import net.sourceforge.joceanus.jgordianknot.zip.GordianZipFileContents;
import net.sourceforge.joceanus.jgordianknot.zip.GordianZipFileEntry;
import net.sourceforge.joceanus.jgordianknot.zip.GordianZipReadFile;
import net.sourceforge.joceanus.jgordianknot.zip.GordianZipWriteFile;
import net.sourceforge.joceanus.jmetis.data.Difference;
import net.sourceforge.joceanus.jmetis.data.JDataFields;
import net.sourceforge.joceanus.jmetis.data.JDataFormatter;
import net.sourceforge.joceanus.jmetis.data.JDataProfile;
import net.sourceforge.joceanus.jprometheus.JPrometheusDataException;
import net.sourceforge.joceanus.jprometheus.JPrometheusIOException;
import net.sourceforge.joceanus.jprometheus.data.DataValues.GroupedItem;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Formatter/Parser class for DataValues.
 * @param <T> the dataSet type
 * @param <E> the data type enum class
 */
public class DataValuesFormatter<T extends DataSet<T, E>, E extends Enum<E>> {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DataValuesFormatter.class);

    /**
     * Entry suffix.
     */
    private static final String SUFFIX_ENTRY = ".xml";

    /**
     * Delete error text.
     */
    private static final String ERROR_DELETE = "Failed to delete file";

    /**
     * The task control.
     */
    private final TaskControl<T> theTask;

    /**
     * The document builder.
     */
    private final DocumentBuilder theBuilder;

    /**
     * The transformer.
     */
    private final Transformer theXformer;

    /**
     * The Data version.
     */
    private Integer theVersion;

    /**
     * Constructor.
     * @param pTask the task control
     * @throws JPrometheusIOException on error
     */
    public DataValuesFormatter(final TaskControl<T> pTask) throws JPrometheusIOException {
        /* Store values */
        theTask = pTask;

        /* protect against exceptions */
        try {
            /* Create a Document builder */
            DocumentBuilderFactory myFactory = DocumentBuilderFactory.newInstance();
            theBuilder = myFactory.newDocumentBuilder();

            /* Create the transformer */
            TransformerFactory myXformFactory = TransformerFactory.newInstance();
            theXformer = myXformFactory.newTransformer();

        } catch (ParserConfigurationException | TransformerConfigurationException e) {
            throw new JPrometheusIOException("Failed to initialise parser", e);
        }
    }

    /**
     * Create a Backup ZipFile.
     * @param pData Data to write out
     * @param pFile the backup file to write to
     * @return success true/false
     * @throws JOceanusException on error
     */
    public boolean createBackup(final T pData,
                                final File pFile) throws JOceanusException {
        /* Obtain the active profile */
        JDataProfile myTask = theTask.getActiveTask();
        JDataProfile myStage = myTask.startTask("Writing");

        /* Create a similar security control */
        GordianHashManager mySecure = pData.getSecurity();
        GordianKeySetHash myBase = pData.getKeySetHash();
        GordianKeySetHash myHash = mySecure.similarKeySetHash(myBase);

        /* Access the data version */
        theVersion = pData.getControl().getDataVersion();

        /* Declare the number of stages */
        boolean bContinue = theTask.setNumStages(pData.getListMap().size());

        /* Protect the workbook access */
        try (GordianZipWriteFile myZipFile = new GordianZipWriteFile(myHash, pFile)) {
            /* Loop through the data lists */
            Iterator<DataList<?, E>> myIterator = pData.iterator();
            while (bContinue && myIterator.hasNext()) {
                DataList<?, E> myList = myIterator.next();

                /* Declare the new stage */
                if (!theTask.setNewStage(myList.listName())) {
                    return false;
                }

                /* If this list should be written */
                if (myList.includeDataXML()) {
                    /* Write the list details */
                    myStage.startTask(myList.listName());
                    bContinue = writeXMLListToFile(myList, myZipFile, true);
                }
            }

            /* Complete the task */
            myStage.end();

            /* return success */
            return bContinue;

        } catch (IOException e) {
            throw new JPrometheusIOException("Failed to create backup XML", e);
        } finally {
            /* Delete the file on error */
            if (!bContinue && !pFile.delete()) {
                /* Nothing that we can do. At least we tried */
                LOGGER.error(ERROR_DELETE);
            }
        }
    }

    /**
     * Create an Extract ZipFile.
     * @param pData Data to write out
     * @param pFile the extract file to write to
     * @return success true/false
     * @throws JOceanusException on error
     */
    public boolean createExtract(final T pData,
                                 final File pFile) throws JOceanusException {
        /* Obtain the active profile */
        JDataProfile myTask = theTask.getActiveTask();
        JDataProfile myStage = myTask.startTask("Writing");

        /* Access the data version */
        theVersion = pData.getControl().getDataVersion();

        /* Declare the number of stages */
        boolean bContinue = theTask.setNumStages(pData.getListMap().size());

        /* Protect the workbook access */
        try (GordianZipWriteFile myZipFile = new GordianZipWriteFile(pFile)) {
            /* Loop through the data lists */
            Iterator<DataList<?, E>> myIterator = pData.iterator();
            while (bContinue && myIterator.hasNext()) {
                DataList<?, E> myList = myIterator.next();

                /* Declare the new stage */
                if (!theTask.setNewStage(myList.listName())) {
                    return false;
                }

                /* If this list should be written */
                if (myList.includeDataXML()) {
                    /* Write the list details */
                    myStage.startTask(myList.listName());
                    bContinue = writeXMLListToFile(myList, myZipFile, false);
                }
            }

            /* Complete the task */
            myStage.end();

            /* return success */
            return bContinue;

        } catch (IOException e) {
            throw new JPrometheusIOException("Failed to create extract XML", e);
        } finally {
            /* Delete the file on error */
            if (!bContinue && !pFile.delete()) {
                /* Nothing that we can do. At least we tried */
                LOGGER.error(ERROR_DELETE);
            }
        }
    }

    /**
     * Write XML list to file.
     * @param pList the data list
     * @param pZipFile the output zipFile
     * @param pStoreIds do we include IDs in XML
     * @return continue true/false
     * @throws JOceanusException on error
     */
    private boolean writeXMLListToFile(final DataList<?, E> pList,
                                       final GordianZipWriteFile pZipFile,
                                       final boolean pStoreIds) throws JOceanusException {
        /* Access the list name */
        String myName = pList.listName() + SUFFIX_ENTRY;

        /* Protect the workbook access */
        try (OutputStream myStream = pZipFile.getOutputStream(new File(myName))) {
            /* Create a new document */
            Document myDocument = theBuilder.newDocument();

            /* Populate the document from the list */
            boolean bContinue = populateXML(myDocument, pList, pStoreIds);

            /* Format the XML and write to stream */
            theXformer.transform(new DOMSource(myDocument), new StreamResult(myStream));

            /* Return success */
            return bContinue;

        } catch (TransformerException | IOException e) {
            throw new JPrometheusIOException("Failed to transform XML", e);
        }
    }

    /**
     * Create XML for a list.
     * @param pDocument the document to hold the list.
     * @param pList the data list
     * @param pStoreIds do we include IDs in XML
     * @return continue true/false
     */
    private boolean populateXML(final Document pDocument,
                                final DataList<?, E> pList,
                                final boolean pStoreIds) {
        /* Create an element for the item */
        Element myElement = pDocument.createElement(pList.listName());
        pDocument.appendChild(myElement);

        /* Access the Data formatter */
        JDataFormatter myFormatter = pList.getDataSet().getDataFormatter();

        /* Declare the number of steps */
        int myTotal = pList.size();
        if (!theTask.setNumSteps(myTotal)) {
            return false;
        }

        /* Set the list type and size */
        myElement.setAttribute(DataValues.ATTR_TYPE, pList.getItemType().name());
        myElement.setAttribute(DataValues.ATTR_SIZE, Integer.toString(myTotal));
        myElement.setAttribute(DataValues.ATTR_VERS, Integer.toString(theVersion));

        /* Access the number of reporting steps */
        int mySteps = theTask.getReportingSteps();
        int myCount = 0;

        /* Iterate through the list */
        Iterator<?> myIterator = pList.iterator();
        while (myIterator.hasNext()) {
            Object myObject = myIterator.next();

            /* Ignore if not a DataItem */
            if (!(myObject instanceof DataItem)) {
                continue;
            }

            /* Access as DataItem */
            @SuppressWarnings("unchecked")
            DataItem<E> myItem = (DataItem<E>) myObject;

            /* Skip over child items */
            if ((myItem instanceof GroupedItem)
                && (((GroupedItem<?>) myItem).isChild())) {
                continue;
            }

            /* Create DataValues for item */
            DataValues<E> myValues = new DataValues<E>(myItem);

            /* Add the child to the list */
            Element myChild = myValues.createXML(pDocument, myFormatter, pStoreIds);
            myElement.appendChild(myChild);

            /* Report the progress */
            myCount++;
            if (((myCount % mySteps) == 0) && (!theTask.setStepsDone(myCount))) {
                return false;
            }
        }

        /* return success */
        return true;
    }

    /**
     * Load a ZipFile.
     * @param pData DataSet to load into
     * @param pFile the file to load
     * @return success true/false
     * @throws JOceanusException on error
     */
    public boolean loadZipFile(final T pData,
                               final File pFile) throws JOceanusException {
        /* Obtain the active profile */
        JDataProfile myTask = theTask.getActiveTask();
        JDataProfile myStage = myTask.startTask("Loading");
        myStage.startTask("Parsing");

        /* Access the zip file */
        GordianZipReadFile myZipFile = new GordianZipReadFile(pFile);

        /* Obtain the hash bytes from the file */
        byte[] myHashBytes = myZipFile.getHashBytes();

        /* If this is a secure ZipFile */
        if (myHashBytes != null) {
            /* Access the Security manager */
            GordianHashManager mySecurity = theTask.getSecurity();

            /* Obtain the initialised password hash */
            GordianKeySetHash myHash = mySecurity.resolveKeySetHash(myHashBytes, pFile.getName());

            /* Associate this keySetHash with the ZipFile */
            myZipFile.setKeySetHash(myHash);
        }

        /* Parse the Zip File */
        boolean bSuccess = parseZipFile(myStage, pData, myZipFile);

        /* Complete the task */
        myStage.end();
        return bSuccess;
    }

    /**
     * Parse a ZipFile.
     * @param pProfile the active profile
     * @param pData DataSet to load into
     * @param pZipFile the file to parse
     * @return success true/false
     * @throws JOceanusException on error
     */
    private boolean parseZipFile(final JDataProfile pProfile,
                                 final T pData,
                                 final GordianZipReadFile pZipFile) throws JOceanusException {
        /* Start new stage */
        JDataProfile myStage = pProfile.startTask("Loading");

        /* Declare the number of stages */
        boolean bContinue = theTask.setNumStages(pData.getListMap().size());

        /* Loop through the data lists */
        Iterator<DataList<?, E>> myIterator = pData.iterator();
        while (bContinue && myIterator.hasNext()) {
            DataList<?, E> myList = myIterator.next();

            /* Declare the new stage */
            if (!theTask.setNewStage(myList.listName())) {
                return false;
            }

            /* If this list should be read */
            if (myList.includeDataXML()) {
                /* Write the list details */
                myStage.startTask(myList.listName());
                bContinue = readXMLListFromFile(myList, pZipFile);
            }

            /* postProcessList after load */
            myList.postProcessOnLoad();
        }

        /* Create the control data */
        if (bContinue) {
            pData.getControlData().addNewControl(theVersion);
        }

        /* Complete the task */
        myStage.end();

        /* return success */
        return bContinue;
    }

    /**
     * Read XML list from file.
     * @param pList the data list
     * @param pZipFile the input zipFile
     * @return continue true/false
     * @throws JOceanusException on error
     */
    private boolean readXMLListFromFile(final DataList<?, E> pList,
                                        final GordianZipReadFile pZipFile) throws JOceanusException {
        /* Access the list name */
        String myName = pList.listName() + SUFFIX_ENTRY;

        /* Locate the correct entry */
        GordianZipFileContents myContents = pZipFile.getContents();
        GordianZipFileEntry myEntry = myContents.findFileEntry(myName);
        if (myEntry == null) {
            throw new JPrometheusDataException("List not found " + myName);
        }

        /* Protect the workbook access */
        try (InputStream myStream = pZipFile.getInputStream(myEntry)) {
            /* Read the document from the stream and parse it */
            Document myDocument = theBuilder.parse(myStream);

            /* Populate the list from the document */
            return parseXMLDocument(myDocument, pList);

        } catch (IOException | SAXException e) {
            throw new JPrometheusIOException("Failed to parse XML", e);
        }
    }

    /**
     * parse an XML document into DataValues.
     * @param pDocument the document that holds the list.
     * @param pList the data list
     * @return continue true/false
     * @throws JOceanusException on error
     */
    private boolean parseXMLDocument(final Document pDocument,
                                     final DataList<?, E> pList) throws JOceanusException {
        /* Access the parent element */
        Element myElement = pDocument.getDocumentElement();
        E myItemType = pList.getItemType();

        /* Check that the document name and dataType are correct */
        if ((!Difference.isEqual(myElement.getNodeName(), pList.listName()))
            || (!Difference.isEqual(myElement.getAttribute(DataValues.ATTR_TYPE), myItemType.name()))) {
            throw new JPrometheusDataException("Invalid list type");
        }

        /* If this is the first Data version */
        Integer myVersion = Integer.valueOf(myElement.getAttribute(DataValues.ATTR_VERS));
        if (theVersion == null) {
            theVersion = myVersion;
        } else if (!theVersion.equals(myVersion)) {
            throw new JPrometheusDataException("Inconsistent data version");
        }

        /* Access field types for list */
        JDataFields myFields = pList.getItemFields();

        /* Access the Data formatter */
        JDataFormatter myFormatter = pList.getDataSet().getDataFormatter();

        /* Declare the number of steps */
        int myTotal = getListCount(myFormatter, myElement);
        if (!theTask.setNumSteps(myTotal)) {
            return false;
        }

        /* Access the number of reporting steps */
        int mySteps = theTask.getReportingSteps();
        int myCount = 0;

        /* Loop through the children */
        for (Node myChild = myElement.getFirstChild(); myChild != null; myChild = myChild.getNextSibling()) {
            /* Ignore non-elements */
            if (!(myChild instanceof Element)) {
                continue;
            }

            /* Access as Element */
            Element myItem = (Element) myChild;

            /* Create DataArguments for item */
            DataValues<E> myValues = new DataValues<E>(myItem, myFields);

            /* Add the child to the list */
            pList.addValuesItem(myValues);

            /* Report the progress */
            myCount++;
            if (((myCount % mySteps) == 0) && (!theTask.setStepsDone(myCount))) {
                return false;
            }
        }

        /* Return success */
        return true;
    }

    /**
     * Obtain count attribute.
     * @param pFormatter the formatter.
     * @param pElement the element that holds the count.
     * @return the list count
     * @throws JOceanusException on error
     */
    private static Integer getListCount(final JDataFormatter pFormatter,
                                        final Element pElement) throws JOceanusException {
        try {
            /* Access the list count */
            String mySize = pElement.getAttribute(DataValues.ATTR_SIZE);
            return pFormatter.parseValue(mySize, Integer.class);
        } catch (NumberFormatException e) {
            throw new JPrometheusDataException("Invalid list count", e);
        }
    }
}
