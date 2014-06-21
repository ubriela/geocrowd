package test.geocrowd;

import static org.junit.Assert.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Hashtable;

import org.datasets.gowalla.PreProcess;
import org.geocrowd.DatasetEnum;
import org.geocrowd.common.SpecializedWorker;
import org.geocrowd.common.entropy.Coord;
import org.geocrowd.common.entropy.Observation;
import org.geocrowd.util.Constants;
import org.junit.Test;

public class PreProcessTest extends PreProcess {

	@Test
	public void testFilterInput() {
		PreProcess prep = new PreProcess();

		prep.filterInput(Constants.gowallaFileName_CA, 32.1713906, -124.3041035, 41.998434033, -114.0043464333);

		prep.computeBoundary(Constants.gowallaFileName_CA);
	}

	@Test
	public void generateWorkers() {
		PreProcess prep = new PreProcess();
		prep.DATA_SET = DatasetEnum.SKEWED;
		
		prep.readBoundary(prep.DATA_SET);
		prep.createGrid(prep.DATA_SET);

		// generating workers from Gowalla
		Hashtable<Date, ArrayList<SpecializedWorker>> hashTable = prep
				.generateRealWorkers(Constants.gowallaFileName_CA);
		prep.saveRealWorkers(hashTable);
	}
	
	@Test
	public void computeLocationEntropy() {
		PreProcess prep = new PreProcess();
		
		// compute occurrences of each location id from Gowalla
		// each location id is associated with a grid 
		Hashtable<Integer, ArrayList<Observation>> occurances = prep
				.readRealEntropyData(Constants.gowallaFileName_CA);
		
		// compute entropy of each location id 
		prep.computeLocationEntropy(occurances);
		
		// compute index (row, col) of each location id
		Hashtable<Integer, Coord> gridIndices = prep.locIdToCellIndices();
		prep.saveLocationEntropy(gridIndices);
	}
	

	// ------------------------------------------------------------
	@Test
	public void testExtractCoords() {
		PreProcess prep = new PreProcess();
		prep.DATA_SET = DatasetEnum.GOWALLA;
		
		// CA: 32.1713906, -124.3041035, 41.998434033, -114.0043464333
		// Los Angeles: 33.699476,-118.570633, 34.319887,-118.192978
		// Bay area: 37.246147,-122.67746, 37.990176,-121.839752
		// SF: 37.711049,-122.51524, 37.832899,-122.360744
		// Yelp: 
		prep.filterInput("dataset/real/gowalla_LA", 33.699476,-118.570633, 34.319887,-118.192978);
		prep.computeBoundary("dataset/real/gowalla_LA");
		prep.extractCoords("dataset/real/gowalla_LA");
		prep.extractMBRs("dataset/real/gowalla_LA");
		
	}

	@Test
	public void testGenerateSynWorkersTasks() {

		PreProcess prep = new PreProcess();
		prep.DATA_SET = DatasetEnum.SKEWED;
		
		prep.computeBoundary(Constants.skewedBoundary);
		prep.readBoundary(prep.DATA_SET);
		prep.createGrid(prep.DATA_SET);

		// generating workers
		prep.generateSynWorkers(true, true);

		// generating location density
		prep.saveSynLocationDensity(prep.computeSyncLocationDensity());

		// generate tasks
		prep.generateSynTasks();
	}
}
