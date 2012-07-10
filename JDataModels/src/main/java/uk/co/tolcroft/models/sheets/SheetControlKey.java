/*******************************************************************************
 * Copyright 2012 Tony Washer
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
package uk.co.tolcroft.models.sheets;

import uk.co.tolcroft.models.data.ControlKey;
import uk.co.tolcroft.models.data.DataSet;

public class SheetControlKey extends SheetDataItem<ControlKey> {
	/**
	 * SheetName for Keys
	 */
	private static final String Keys	   		= ControlKey.listName;

	/**
	 * ControlKey data list
	 */
	private ControlKey.List 	theList			= null;

	/**
	 * DataSet
	 */
	private DataSet<?> 			theData			= null;

	/**
	 * Constructor for loading a spreadsheet
	 * @param pReader the spreadsheet reader
	 */
	protected SheetControlKey(SheetReader<?>	pReader) {
		/* Call super constructor */
		super(pReader, Keys);
		
		/* Access the Lists */
		theData	= pReader.getData();
		theList = theData.getControlKeys();
	}

	/**
	 *  Constructor for creating a spreadsheet
	 *  @param pWriter the Spreadsheet writer
	 */
	protected SheetControlKey(SheetWriter<?>	pWriter) {
		/* Call super constructor */
		super(pWriter, Keys);
		
		/* Access the Control list */
		theList = pWriter.getData().getControlKeys();
		setDataList(theList);
	}
	
	@Override
	protected void loadItem() throws Throwable {
		/* Access the IDs */
		int	myID 		= loadInteger(0);
		int	myTypeID 	= loadInteger(1);
		
		/* Access the binary values  */
		byte[] 	myHash		= loadBytes(2);
		byte[] 	myPublic	= loadBytes(3);
		byte[] 	myPrivate	= loadBytes(4);

		/* Add the Control */
		theList.addItem(myID, myTypeID, myHash, myPublic, myPrivate);
	}

	@Override
	protected void insertItem(ControlKey	pItem) throws Throwable  {
		/* Set the fields */
		writeInteger(0, pItem.getId());
		writeInteger(1, pItem.getKeyMode().getMode());
		writeBytes(2, pItem.getPasswordHash());
		writeBytes(3, pItem.getPublicKey());
		writeBytes(4, pItem.getPrivateKey());
	}

	@Override
	protected void preProcessOnWrite() throws Throwable {}		

	@Override
	protected void postProcessOnWrite() throws Throwable {		
		/* Set the five columns as the range */
		nameRange(5);
	}
}
