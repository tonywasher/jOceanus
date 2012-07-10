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

public enum Difference {
	/**
	 * Identical
	 */
	Identical,
	
	/**
	 * Value Changed
	 */
	Different,
	
	/**
	 * Security Changed
	 */
	Security;
	
	/**
	 * Is there differences
	 */
	public boolean isDifferent() {
		switch(this) {
			case Identical:
				return false;
			default:
				return true;
		}
	}
	
	/**
	 * Is there no differences
	 */
	public boolean isIdentical() { return !isDifferent(); }
	
	/**
	 * Is there value differences
	 */
	public boolean isValueChanged() {
		switch(this) {
			case Different:
				return true;
			default:
				return false;
		}
	}
	
	/**
	 * Is there security differences
	 */
	public boolean isSecurityChanged() {
		switch(this) {
			case Security:
				return false;
			default:
				return true;
		}
	}

	/**
	 * Combine Differences
	 * @param pThat the difference to combine
	 */
	public Difference combine(Difference pThat) {
		switch(this) {
			case Identical:
				return pThat;
			case Security:
				return (pThat == Different) ? pThat : this;
			default:
				return this;
		}
	}
}
