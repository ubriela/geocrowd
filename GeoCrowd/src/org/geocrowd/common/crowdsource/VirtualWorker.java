/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geocrowd.common.crowdsource;

import java.util.HashSet;

/**
 *
 * @author luan
 */
public class VirtualWorker extends  GenericWorker{
    
    private HashSet<Integer> workerIds=new HashSet<Integer>();

    public HashSet<Integer> getWorkerIds() {
        return workerIds;
    }

    public void setWorkerIds(HashSet<Integer> workerIds) {
        this.workerIds = workerIds;
    }
    
}
