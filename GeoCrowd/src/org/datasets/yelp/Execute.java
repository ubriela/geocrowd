/*******************************************************************************
* @ Year 2013
* This is the source code of the following papers. 
* 
* 1) Geocrowd: A Server-Assigned Crowdsourcing Framework. Hien To, Leyla Kazemi, Cyrus Shahabi.
* 
* 
* Please contact the author Hien To, ubriela@gmail.com if you have any question.
*
* Contributors:
* Hien To - initial implementation
*******************************************************************************/
package org.datasets.yelp;


// TODO: Auto-generated Javadoc
/**
 * The Class Execute.
 * 
 * @author dkh
 */
public class Execute {

    /**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
    public static void main(String[] args) {
        // TODO code application logic here
        ProcessDataSet.Curtail_Review_File();

        ProcessDataSet.Access_Business();
        ProcessDataSet.Access_User();
        ProcessDataSet.Access_Review();

        ProcessDataSet.saveBusiness_Task();
        ProcessDataSet.saveUser_Worker();
        
//        ProcessDataSet.saveWorkersMCD("dataset/real/yelp/yelp_mcd.txt");
        ProcessDataSet.saveTaskWorkers();

        ProcessDataSet.saveBoundary();

        ProcessDataSet.saveLocationDensity(ProcessDataSet
                .computeLocationDensity());
        ProcessDataSet.save_Statistic();

        ProcessDataSet.split_Worker_by_time();

    }
}