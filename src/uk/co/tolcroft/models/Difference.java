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
