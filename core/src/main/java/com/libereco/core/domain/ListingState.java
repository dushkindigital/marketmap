/**
 * 
 */
package com.libereco.core.domain;

/**
 * @author rrached
 *
 * Describes the offering stage (new, listed, etc...) of the listed item.
 * According to the Libereco Technical Specification, pp. 7
 */
public enum ListingState {
	/**
	 * the listing exists in Libereco but not in any other marketplaces;
	 * the id field can be empty
	 */
	NEW,
	/**
	 * the listing has been posted in some or all of its target marketplaces
	 */
	LISTED,
	/**
	 * the listing has been removed from its marketplaces and no longer exists
	 * outside Libereco
	 */
	DEAD,
	/**
	 * the listing is being returned as a result of a call to find; fields in this
	 * listing can be used to create new listings, but this specific listing object
	 * exists outside the realm of the Libereco database - the field is undefined
	 */
	FOUND;
}
