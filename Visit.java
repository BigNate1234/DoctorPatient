/**
 * 
 */
package edu.miami.cis324.hw4.nfurman;

import java.util.Date;

/**
 * @author nathan
 *
 */
public interface Visit<V, T> {
	public V getVisitor();
	public T getHost();
	public Date getDate();
	public String getDateFormatted();
	public Integer getVisitID();
}
