/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.datasets.yelp;


/**
 *
 * @author dkh
 */
public class Execute {

    public static void main(String[] args) {
        // TODO code application logic here
        //ProcessDataSet.Curtail_Review_File();

        ProcessDataSet.Access_Business();
        ProcessDataSet.Access_User();
        ProcessDataSet.Access_Review();

        ProcessDataSet.saveBusiness_Task();
        ProcessDataSet.saveUser_Worker();

        ProcessDataSet.saveBoundary();

        ProcessDataSet.saveLocationDensity(ProcessDataSet
                .computeLocationDensity());
        ProcessDataSet.save_Statistic();

        //ProcessDataSet.split_Worker_by_time();

    }
}
