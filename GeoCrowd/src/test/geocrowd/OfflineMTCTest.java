package test.geocrowd;

import java.util.HashSet;
import org.geocrowd.DatasetEnum;
import org.geocrowd.Geocrowd;
import org.geocrowd.OfflineMTC;
import org.geocrowd.common.Constants;

public class OfflineMTCTest {

    public static void main(String[] args) {
        test();
    }

    public static void test() {

        Geocrowd.DATA_SET = DatasetEnum.GOWALLA;

        OfflineMTC offlineMTC = new OfflineMTC();
        offlineMTC.budget = 100;
        for (int i = 0; i < 10; i++) {
            switch (Geocrowd.DATA_SET) {
                case GOWALLA:
                    offlineMTC.readTasks(Constants.gowallaTaskFileNamePrefix
                            + i + ".txt");
                    offlineMTC.readWorkers(Constants.gowallaWorkerFileNamePrefix
                            + i + ".txt", i);
                    break;
                case YELP:
                    offlineMTC.readTasks(Constants.yelpTaskFileNamePrefix + i
                            + ".txt");
                    offlineMTC.readWorkers(Constants.yelpWorkerFileNamePrefix
                            + i + ".txt", i);
                    break;
                case UNIFORM:
                    offlineMTC.readTasks(Constants.uniTaskFileNamePrefix + i
                            + ".txt");
                    offlineMTC.readWorkers(Constants.uniWorkerFileNamePrefix
                            + i + ".txt", i);
                    break;
                case SKEWED:
                    offlineMTC.readTasks(Constants.skewedTaskFileNamePrefix
                            + i + ".txt");
                    offlineMTC.readWorkers(Constants.skewedWorkerFileNamePrefix
                            + i + ".txt", i);
                    break;
                case SMALL_TEST:
                    offlineMTC.readTasks(Constants.smallTaskFileNamePrefix
                            + i + ".txt");
                    offlineMTC.readWorkers(Constants.smallWorkerFileNamePrefix
                            + i + ".txt", i);
                    break;
            }

            System.out.println("Number of workers: " + OfflineMTC.workerList.size());

            System.out.println("Number of tasks: " + OfflineMTC.taskList.size());
        }

        
        offlineMTC.matchingTasksWorkers();
        
        HashSet<Integer> workerSet =  offlineMTC.maxTaskCoverage();
        System.out.println(workerSet);

    }

}
