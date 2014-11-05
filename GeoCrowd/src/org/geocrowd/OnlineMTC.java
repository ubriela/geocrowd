package org.geocrowd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import maxcover.MaxCover;
import maxcover.MaxCoverAdapt;
import maxcover.MaxCoverBasic;
import org.datasets.yelp.Constant;
import static org.geocrowd.AlgorithmEnum.MAX_COVER_BASIC;
import static org.geocrowd.Geocrowd.algorithm;
import static org.geocrowd.Geocrowd.taskList;
import org.geocrowd.common.Constants;

public class OnlineMTC extends GeocrowdSensing {

    public int totalBudget = 0;

    public final int totalNumberTasks;

    public int numberArrivalTask = 0;

    public int lamda;
    public int beta;
    public int usedBudget;

    public OnlineMTC() throws IOException {
        if (AlgorithmEnum.BASIC != Geocrowd.algorithm) {
            this.totalNumberTasks = computeTotalTasks();
        } else {
            this.totalNumberTasks = -1;
        }
    }

    /**
     * Read tasks from file.
     *
     * @param fileName the file name
     */
    @Override
    public void readTasks(String fileName) {
        this.numberArrivalTask = Parser.parseSensingTasks(fileName, taskList);
        TaskCount += this.numberArrivalTask;

    }

    public HashSet<Integer> maxCoverage() {
        MaxCover maxCover = null;
        HashSet<Integer> assignedWorker = null;
        switch (algorithm) {

            case MAX_COVER_BASIC:
            case MAX_COVER_PRO_B:
                MaxCoverBasic maxCoverBasic = new MaxCoverBasic(getContainerWithDeadline(), TimeInstance);
                maxCoverBasic.budget = getBudget(algorithm);
                assignedWorker = maxCoverBasic.maxCover();
                maxCover = maxCoverBasic;
                break;

            case MAX_COVER_ADAPT_B:

                MaxCoverAdapt maxCoverAdapt = new MaxCoverAdapt(getContainerWithDeadline(), TimeInstance);
                /**
                 * compute lamda0
                 */
                MaxCoverBasic maxCoverBasic2 = new MaxCoverBasic(getContainerWithDeadline(), TimeInstance);
                maxCoverBasic2.budget = getBudget(MAX_COVER_BASIC);
                assignedWorker = maxCoverBasic2.maxCover();
                
                break;
        }

        /**
         * As all the tasks in the container are assigned, we need to remove
         * them from task list.
         */
        ArrayList<Integer> assignedTasks = new ArrayList<Integer>();
        // Iterator it = sc.universe.iterator();
        Iterator it = maxCover.assignedTaskSet.iterator();
        while (it.hasNext()) {
            Integer candidateIndex = (Integer) it.next();
            assignedTasks.add(candidateTaskIndices.get(candidateIndex));
        }

        /**
         * sorting is necessary to make sure that we don't mess things up when
         * removing elements from a list
         */
        Collections.sort(assignedTasks);
        for (int i = assignedTasks.size() - 1; i >= 0; i--) {
            /* remove the last elements first */
            taskList.remove((int) assignedTasks.get(i));
        }
        return assignedWorker;

    }

    private int getBudget(AlgorithmEnum algorithm) {
        switch (algorithm) {

            case MAX_COVER_BASIC:
                if (TimeInstance < Constant.TimeInstance - 1) {
                    return totalBudget / Constant.TimeInstance;
                } else {
                    return totalBudget - totalBudget / Constant.TimeInstance * (Constant.TimeInstance - 1);
                }
            case MAX_COVER_PRO_B:

                return totalBudget * numberArrivalTask / totalNumberTasks;

        }
        return 0;
    }

    private int computeTotalTasks() throws IOException {
        int numberTasks = 0;
        for (int i = 0; i < Constant.TimeInstance; i++) {
            switch (Geocrowd.DATA_SET) {
                case GOWALLA:
                    numberTasks += Parser.readNumberOfTasks(Constants.gowallaTaskFileNamePrefix
                            + i + ".txt");
                    break;
                case YELP:
                    numberTasks += Parser.readNumberOfTasks(Constants.yelpTaskFileNamePrefix
                            + i + ".txt");
                    break;
                case UNIFORM:
                    numberTasks += Parser.readNumberOfTasks(Constants.uniTaskFileNamePrefix
                            + i + ".txt");
                    break;
                case SKEWED:
                    numberTasks += Parser.readNumberOfTasks(Constants.skewedTaskFileNamePrefix
                            + i + ".txt");
                    break;
                case SMALL_TEST:
                    numberTasks += Parser.readNumberOfTasks(Constants.smallTaskFileNamePrefix
                            + i + ".txt");
                    break;
            }
        }
        return numberTasks;
    }
}
