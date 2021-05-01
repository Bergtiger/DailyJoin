package de.bergtiger.dailyjoin.bdo;

import java.util.ArrayList;
import java.util.Collection;

public class TigerList<T> extends ArrayList<T> {

    private static final long serialVersionUID = 1L;

    private int pageSize = 15;
    private int page = 0;
    /** only DailyJoin searched column*/
    private String column;

    public TigerList() {
        super();
    }

    public TigerList(Collection<? extends T> list) {
        super(list);
    }

    // Page
    /**
     * Get current Page (Starts with 0)
     * @return current page, first page = 0;
     */
    public int getPage() {
        return page;
    }

    /**
     * Set Page
     * @param page set current page, first page = 0;
     */
    public void setPage(int page) {
        this.page = Math.max(0, Math.min(page, getPageMax() - 1));
    }

    // PageSize
    /**
     * Get page size.
     * @return max elements on each page
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * Set page size.
     * @param pageSize max elements on each page. minimum 1
     */
    public void setPageSize(int pageSize) {
        this.pageSize = Math.max(1,pageSize);
    }

    /**
     * Get Max Page
     * @return biggest page number
     */
    public int getPageMax() {
        return (size() / pageSize) + ((size() % pageSize != 0) ? 1 : 0);
    }
    
    /**
     * set searched column.
     * @param column 
     */
    public void setColumn(String column) {
    	this.column = column;
    }
    
    /**
     * get searched column.
     * @return
     */
    public String getColumn() {
    	return column;
    }
}
