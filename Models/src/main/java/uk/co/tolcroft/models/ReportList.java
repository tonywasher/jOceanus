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
package uk.co.tolcroft.models;

import java.util.Iterator;

import uk.co.tolcroft.models.help.DebugDetail;
import uk.co.tolcroft.models.help.DebugManager;
import uk.co.tolcroft.models.help.DebugManager.DebugEntry;
import uk.co.tolcroft.models.help.DebugObject;

public abstract class ReportList<T extends ReportItem<T>> extends SortedList<T> 
												 		  implements DebugObject {
	/**
	 * Construct a top-level List
	 */
	public ReportList(Class<T> pClass) { super(pClass); }

	/**
	 * Construct a top-level List
	 */
	public ReportList(Class<T> pClass, boolean fromStart) { super(pClass, fromStart); }

	/**
	 * Obtain the type of the list
	 * @return the type of the list
	 */
	abstract public String itemType();
	
	/**
	 * Stub for extensions to add their own fields
	 * @param pDetail the debug detail
	 * @param pBuffer the string buffer 
	 */
	public void addHTMLFields(DebugDetail pDetail, StringBuilder pBuffer) {}
	
	@Override
	public StringBuilder buildDebugDetail(DebugDetail pDetail) {
		/* Local variables */
		StringBuilder	myString = new StringBuilder(10000);
			
		/* Format the table headers */
		myString.append("<table border=\"1\" width=\"90%\" align=\"center\">");
		myString.append("<thead><th>");
		myString.append(itemType());
		myString.append("List</th>");
		myString.append("<th>Property</th><th>Value</th></thead><tbody>");
			
		/* Start the status section */
		myString.append("<tr><th rowspan=\"2\">Status</th></tr>");
		myString.append("<tr><td>ListSize</td><td>"); 
		myString.append(sizeAll()); 
		myString.append("</td></tr>"); 

		/* Add any extra detail */
		addHTMLFields(pDetail, myString);
		
		/* Return the string */
		return myString;
	}
		
	@Override
	public void addChildEntries(DebugManager 	pManager,
								DebugEntry		pParent) { }
	
	@Override
	public Iterator<T> iterator() { return super.iterator(); }
	
	@Override
	public SortedListIterator<T> listIterator() { return super.listIterator(); }

	@Override
	public SortedListIterator<T> listIterator(int iIndex) { return super.listIterator(iIndex); }

	@Override
	public SortedListIterator<T> listIterator(boolean bShowAll) { return super.listIterator(bShowAll); }

	@Override
	public SortedListIterator<T> listIterator(T pItem, boolean bShowAll) { return super.listIterator(pItem, bShowAll); }
}
