package de.bergtiger.dailyjoin.bdo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Bergtiger 
 * Extends {@link ArrayList} with a paging ability
 * @param <T> the type of elements in this list
 */
public class TigerList<T> extends ArrayList<T> {

	private static final long serialVersionUID = 1L;

	private int pageSize = 15;
	private int page = 0;
	private String header;

	/**
	 * new empty List
	 */
	public TigerList() {
		super();
	}

	/**
	 * new list with given items
	 * 
	 * @param list {@link Collection}
	 */
	public TigerList(Collection<? extends T> list) {
		super(list);
	}

	/**
	 * Get current Page (Starts with 0)
	 * 
	 * @return current page, first page = 0;
	 */
	public int getPage() {
		return page;
	}

	/**
	 * Set Page.
	 * minimal value 0, maximal value max page - 1
	 * @param page set current page, first page = 0;
	 */
	public void setPage(int page) {
		this.page = Math.max(0, Math.min(page, getPageMax() - 1));
	}

	/**
	 * Get page size.
	 * 
	 * @return max elements on each page
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * Set page size.
	 * 
	 * @param pageSize max elements on each page. minimum 1
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = Math.max(1, pageSize);
	}

	/**
	 * Get Max Page.
	 * 
	 * @return biggest page number
	 */
	public int getPageMax() {
		return (size() / pageSize) + ((size() % pageSize != 0) ? 1 : 0);
	}

	/**
	 * get elements from current page.
	 * @return sublist representing current page
	 */
	public List<T> currentPageSubList() {
		return pageSublist(page);
	}

	/**
	 * get elements from the given page
	 * @param page integer page
	 * @return sublist representing the page
	 */
	public List<T> pageSublist(int page) {
		if(page < 0)
			return null;
		if(page > getPageMax())
			return null;
		return subList(
				page * pageSize,
				Math.min((page + 1) * pageSize, size()));
	}

	/**
	 * set list header.
	 * 
	 * @param header information about this list
	 */
	public void setHeader(String header) {
		this.header = header;
	}

	/**
	 * get header information.
	 * 
	 * @return header information for this list
	 */
	public String getHeader() {
		return header;
	}
}
