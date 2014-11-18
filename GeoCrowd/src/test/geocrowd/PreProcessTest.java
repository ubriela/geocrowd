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
package test.geocrowd;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Hashtable;

import org.geocrowd.DatasetEnum;
import org.geocrowd.PreProcess;
import org.geocrowd.common.Constants;
import org.geocrowd.common.crowdsource.SpecializedWorker;
import org.geocrowd.common.entropy.Coord;
import org.geocrowd.common.entropy.Observation;
import org.junit.Test;

// TODO: Auto-generated Javadoc
/**
 * The Class PreProcessTest.
 */
public class PreProcessTest extends PreProcess {
    
    
    public static void main(String[] args){
        PreProcessTest preTest = new PreProcessTest();
//        preTest.testFilterInput();
//        preTest.generateWorkers();
//        preTest.computeLocationEntropy();
        preTest.testExtractCoords();
    }

	/**
	 * Compute location entropy.
	 */
	@Test
	public void computeLocationEntropy() {
		PreProcess prep = new PreProcess();
		PreProcess.DATA_SET = DatasetEnum.GOWALLA;
		
		prep.readBoundary(PreProcess.DATA_SET);
		prep.createGrid(PreProcess.DATA_SET);
		
		// compute occurrences of each location id from Gowalla
		// each location id is associated with a grid 
		Hashtable<Integer, ArrayList<Observation>> occurances = prep
				.readRealEntropyData(Constants.gowallaFileName_CA);
		
		// compute entropy of each location id 
		prep.computeLocationEntropy(occurances);
		
		// compute index (row, col) of each location id
		prep.debug();
		Hashtable<Integer, Coord> gridIndices = prep.locIdToCellIndices();
		prep.saveLocationEntropy(gridIndices);
	}

	/**
	 * Generate workers.
	 */
	@Test
	public void generateWorkers() {
		PreProcess prep = new PreProcess();
		PreProcess.DATA_SET = DatasetEnum.GOWALLA;
		
		prep.readBoundary(PreProcess.DATA_SET);
		prep.createGrid(PreProcess.DATA_SET);

		// generating workers from Gowalla
		Hashtable<Date, ArrayList<SpecializedWorker>> hashTable = prep
				.generateRealWorkers(Constants.gowallaFileName_CA);
		prep.saveRealWorkersMax(hashTable);
	}
	
	// ------------------------------------------------------------
	/**
	 * Test extract coords.
	 */
	@Test
	public void testExtractCoords() {
		PreProcess prep = new PreProcess();
		PreProcess.DATA_SET = DatasetEnum.GOWALLA;
		
		// CA: 32.1713906, -124.3041035, 41.998434033, -114.0043464333
		// Los Angeles: 33.699476,-118.570633, 34.319887,-118.192978
		// Bay area: 37.246147,-122.67746, 37.990176,-121.839752
		// SF: 37.711049,-122.51524, 37.832899,-122.360744
		// Yelp: 
//		prep.filterInput("dataset/real/gowalla/gowalla_CA", 32.1713906, -124.3041035, 41.998434033, -114.0043464333);
//		prep.computeBoundary("dataset/real/gowalla/gowalla_CA");
//		prep.extractCoords("dataset/real/gowalla/gowalla_CA");
//		prep.extractWorkersInstances("dataset/real/gowalla/gowalla_CA", "dataset/real/gowalla/worker/worker", 50);
		
		prep.regionEntropy();
	}
	

	/**
	 * Test filter input.
	 */
	@Test
	public void testFilterInput() {
		PreProcess prep = new PreProcess();
		PreProcess.DATA_SET = DatasetEnum.GOWALLA;
		
		prep.filterInput(Constants.gowallaFileName_CA, 32.1713906, -124.3041035, 41.998434033, -114.0043464333);

		prep.computeBoundary(Constants.gowallaFileName_CA);
	}

	/**
	 * Test generate syn workers tasks.
	 */
	@Test
	public void testGenerateSynWorkersTasks() {

		PreProcess prep = new PreProcess();
		PreProcess.DATA_SET = DatasetEnum.UNIFORM;
		
		prep.computeBoundary(Constants.skewedBoundary);
		prep.readBoundary(PreProcess.DATA_SET);
		prep.createGrid(PreProcess.DATA_SET);

		// generating workers
		prep.generateSynWorkers(true, true);

		// generating location density
		prep.saveLocationDensity(prep.computeSyncLocationDensity());

		// generate tasks
		prep.generateSynTasks();
	}
}
