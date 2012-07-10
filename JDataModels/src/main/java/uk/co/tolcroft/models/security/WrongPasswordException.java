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
package uk.co.tolcroft.models.security;

import uk.co.tolcroft.models.ModelException;

public class WrongPasswordException extends ModelException {
	/* Serial id */
	private static final long serialVersionUID = 2437428363387806213L;

	/**
	 * Constructor
	 * @param s exception string
	 */
	public WrongPasswordException(String s) { super(ExceptionClass.VALIDATE, s); } 
}
