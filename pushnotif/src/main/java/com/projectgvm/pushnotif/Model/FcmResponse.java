package com.projectgvm.pushnotif.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FcmResponse implements Serializable{
	 
		/**
	 * 
	 */
		private long multicast_id;
		private int success;
		private int failure;
		private int canonical_ids;
		private List<Result> result;
		
		public long getMulticast_id() {
			return multicast_id;
		}
		public void setMulticast_id(long multicast_id) {
			this.multicast_id = multicast_id;
		}
		public int getSuccess() {
			return success;
		}
		public void setSuccess(int success) {
			this.success = success;
		}
		public int getFailure() {
			return failure;
		}
		public void setFailure(int failure) {
			this.failure = failure;
		}
		public int getCanonical_ids() {
			return canonical_ids;
		}
		public void setCanonical_ids(int canonical_ids) {
			this.canonical_ids = canonical_ids;
		}
		public FcmResponse() {
			super();
		}

		public FcmResponse(long multicast_id) {
			super();
			this.multicast_id = multicast_id;
		}
		public List<Result> getResults() {
			return result;
		}
		public void setResults(List<Result> result) {
			this.result = result;
		}
		@Override
		public String toString() {
			return "FcmResponse [multicast_id=" + multicast_id + ", success=" + success + ", failure=" + failure
					+ ", canonical_ids=" + canonical_ids + ", result="  + gainResult() + "]";
		}

	    public String gainResult () 
	    {
	    	String hasil = "";
	    	for(Result res : result)
	    	{
	    		hasil = res.getError();
	    	}
	    	
	    	return hasil;
	    }
		
		
}
