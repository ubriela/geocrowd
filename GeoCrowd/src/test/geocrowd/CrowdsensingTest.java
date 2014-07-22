/**
 * *****************************************************************************
 * @ Year 2013 This is the source code of the following papers.
 *
 * 1) Geocrowd: A Server-Assigned Crowdsourcing Framework. Hien To, Leyla
 * Kazemi, Cyrus Shahabi.
 *
 *
 * Please contact the author Hien To, ubriela@gmail.com if you have any
 * question.
 *
 * Contributors: Hien To - initial implementation
 * *****************************************************************************
 */
package test.geocrowd;

import org.geocrowd.AlgorithmEnum;
import org.geocrowd.Crowdsensing;
import org.geocrowd.DatasetEnum;
import org.geocrowd.GenericCrowd;
import org.geocrowd.util.Constants;
import org.junit.Test;

// TODO: Auto-generated Javadoc
/**
 * The Class CrowdsensingTest.
 */
public class CrowdsensingTest {

    /**
     * Test.
     */
    @Test
    public void test() {
        long totalTime = 0;
        double avgTW = 0.0;
        double avgWT = 0.0;

        for (int k = 0; k < 1; k++) {

            System.out.println("+++++++ Iteration: " + (k + 1));
            GenericCrowd.DATA_SET = DatasetEnum.GOWALLA;
            GenericCrowd.algorithm = AlgorithmEnum.GREEDY_HIGH_TASK_COVERAGE;
            Crowdsensing crowdsensing = new Crowdsensing();
            // for (int i = 0; i < Constants.TIME_INSTANCE; i++) {
            for (int i = 0; i < Constants.TIME_INSTANCE; i++) {
                System.out.println("---------- Time instance: " + (i + 1));

                switch (GenericCrowd.DATA_SET) {
                    case GOWALLA:
                        crowdsensing.readTasks(Constants.gowallaTaskFileNamePrefix
                                + i + ".txt");
                        crowdsensing.readWorkers(Constants.gowallaWorkerFileNamePrefix
                                + k + ".txt");
                        break;
                    case YELP:
                        crowdsensing.readTasks(Constants.yelpTaskFileNamePrefix + i
                                + ".txt");
                        crowdsensing.readWorkers(Constants.yelpWorkerFileNamePrefix
                                + k + ".txt");
                        break;
                    case UNIFORM:
                        crowdsensing.readTasks(Constants.uniTaskFileNamePrefix + i
                                + ".txt");
                        crowdsensing.readWorkers(Constants.uniWorkerFileNamePrefix
                                + i + ".txt");
                        break;
                    case SKEWED:
                        crowdsensing.readTasks(Constants.skewedTaskFileNamePrefix
                                + i + ".txt");
                        crowdsensing
                                .readWorkers(Constants.skewedWorkerFileNamePrefix
                                        + i + ".txt");
                        break;
                    case SMALL:
                        crowdsensing.readTasks(Constants.smallTaskFileNamePrefix
                                + i + ".txt");
                        crowdsensing
                                .readWorkers(Constants.smallWorkerFileNamePrefix
                                        + i + ".txt");
                        break;
                }

                crowdsensing.matchingTasksWorkers();
                crowdsensing.computeAverageTaskPerWorker();
                crowdsensing.computeAverageWorkerPerTask();
                avgTW += crowdsensing.avgTW;
                avgWT += crowdsensing.avgWT;

                // debug
                System.out.println("#Tasks: " + crowdsensing.taskList.size());
                System.out.println("#Workers: "
                        + crowdsensing.workerList.size());
                System.out.println("scheduling...");
                double startTime = System.nanoTime();

                crowdsensing.minimizeWorkersMaximumTaskCoverage();

                double runtime = (System.nanoTime() - startTime) / 1000000000.0;
                totalTime += runtime;
                System.out.println("Time: " + runtime);
                crowdsensing.TimeInstance++;
            }

            System.out.println("*************SUMMARY ITERATION " + (k + 1)
                    + " *************");
            System.out.println("#Total workers: " + crowdsensing.WorkerCount);
            System.out.println("#Total tasks: " + crowdsensing.TaskCount);
            
            double avgAssignedWorkers = ((double) Crowdsensing.TotalAssignedWorkers)
                    / ((k + 1) * Constants.TIME_INSTANCE);
            double avgAssignedTasks = ((double) Crowdsensing.TotalAssignedTasks)
                    / ((k + 1) * Constants.TIME_INSTANCE);
            long avgTime = (totalTime) / ((k + 1) * Constants.TIME_INSTANCE);

            System.out.println("Total assigned workers: "
                    + Crowdsensing.TotalAssignedWorkers + "   #of rounds:" + (k + 1)
                    + "  avg: " + avgAssignedWorkers);
            System.out.println("Total assigned tasks: " + Crowdsensing.TotalAssignedTasks
                    + "   #of rounds:" + (k + 1) + "  avg: "
                    + avgAssignedTasks);
            System.out.println("Total time: " + totalTime + "   # of rounds: "
                    + (k + 1) + "  avg time:" + avgTime);

            System.out.println("avgTW/instance: "
                    + avgTW / ((k + 1) * Constants.TIME_INSTANCE));
            System.out.println("avgWT/instance: "
                    + avgWT / ((k + 1) * Constants.TIME_INSTANCE));
        } // end of for loop
    }

}
