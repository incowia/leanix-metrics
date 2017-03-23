package net.leanix.metrics.excelimport;

/**
 * Typ of excel cell
 */
public enum Typ{
	f{
	    @Override
	    public String toString() {
	      return "f";
	    }
	  },
	t{
	    @Override
	    public String toString() {
	      return "t";
	    }
	  }
}