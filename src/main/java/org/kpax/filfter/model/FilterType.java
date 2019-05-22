package org.kpax.filfter.model;

public enum FilterType {

	/**
	 * Checks if column value starts with the filter value.
	 */
	startsWith,
	/**
	 * Checks if column value ends with the filter value.
	 */
	endsWith,
	/**
	 * Checks if column value contains the filter value.
	 */
	contains,
	/**
	 * Checks if string representations of column value and filter value are same.
	 */
	exact,
	/**
	 * Checks if column value is less than the filter value.
	 */
	lt,
	/**
	 * Checks if column value is less than or equals the filter value.
	 */
	lte,
	/**
	 * Checks if column value is greater than the filter value.
	 */
	gt,
	/**
	 * Checks if column value is greater than or equals the filter value.
	 */
	gte,
	/**
	 * Checks if column value is less than the filter value or null.
	 */
	ltn,
	/**
	 * Checks if column value is less than or equals the filter value or null.
	 */
	lten,
	/**
	 * Checks if column value is greater than the filter value or null.
	 */
	gtn,
	/**
	 * Checks if column value is greater than or equals the filter value or null.
	 */
	gten,
	/**
	 * Checks if column value equals the filter value.
	 */
	equals,
	/**
	 * Checks if column value is different from filter value.
	 */
	ne,
	/**
	 * Checks if column value is in the collection of the filter value.
	 */
	in

}
