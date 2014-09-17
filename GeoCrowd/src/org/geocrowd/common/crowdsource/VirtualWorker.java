/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geocrowd.common.crowdsource;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author luan
 */
public class VirtualWorker extends  GenericWorker  implements Comparable<VirtualWorker> {
    
    private HashSet<Integer> workerIds=new HashSet<Integer>();

    public VirtualWorker(Set<Integer> r) {
		this.workerIds = (HashSet<Integer>) r;
	}

	public HashSet<Integer> getWorkerIds() {
        return workerIds;
    }

    public void setWorkerIds(HashSet<Integer> workerIds) {
        this.workerIds = workerIds;
    }
    
    

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((workerIds == null) ? 0 : workerIds.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VirtualWorker other = (VirtualWorker) obj;
		if (workerIds == null) {
			if (other.workerIds != null)
				return false;
		} else if (!workerIds.equals(other.workerIds))
			return false;
		return true;
	}

	@Override
	public int compareTo(VirtualWorker o) {
		/* the virtual workers with larger size at head */
		if (workerIds.size() > o.workerIds.size())
			return -1;
		else if (workerIds.size() < o.workerIds.size())
			return 1;
		return 0;
	}
}
