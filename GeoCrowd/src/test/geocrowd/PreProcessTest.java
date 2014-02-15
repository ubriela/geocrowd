package test.geocrowd;

import static org.junit.Assert.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Hashtable;

import org.datasets.gowalla.PreProcess;
import org.geocrowd.Coord;
import org.geocrowd.Observation;
import org.geocrowd.Worker;
import org.junit.Test;

public class PreProcessTest extends PreProcess {


	@Test
	public void testFilterInput() {
		PreProcess prep = new PreProcess();
		prep.DATA_SET = 0;

//		prep.filterInput();

		prep.computeBoundary();
		
		// prep.readBoundary(Constants.gowallaBoundary);
//		prep.createGrid();
//
//		// generating workers from Gowalla
//		Hashtable<Date, ArrayList<Worker>> hashTable = prep
//				.generateRealWorkers();
//		prep.saveRealWorkers(hashTable);
//
//		// reading entropy from Gowalla
//		Hashtable<Integer, ArrayList<Observation>> hashTable1 = prep
//				.readRealEntropyData();
//		prep.computeLocationEntropy(hashTable1);
//		Hashtable<Integer, Coord> hashTable2 = prep.readCoordInfo();
//		prep.saveLocationEntropy(hashTable2);
	}
	

	@Test
	public void testExtractCoords() {
		PreProcess prep = new PreProcess();
		prep.DATA_SET = 0;
		
		// Los Angeles: 33.699476,-118.570633, 34.319887,-118.192978
		// San Francisco: 37.711049,-122.51524, 37.832899,-122.360744
		prep.filterInput("dataset/real/gowalla_SF", 37.711049,-122.51524, 37.832899,-122.360744);
		prep.computeBoundary();
		prep.extractCoords("dataset/real/gowalla_SF");
	}

	@Test
	public void testGenerateSkewedWorkers() {

		PreProcess prep = new PreProcess();
		prep.DATA_SET = 1;
		
		prep.computeBoundary();
		prep.readBoundary();
		prep.createGrid();

		// generating workers
		prep.generateSyncWorkers(true, true);

		// generating location density
		prep.saveSyncLocationDensity(prep.computeSyncLocationDensity());

		// generate tasks
		prep.generateSyncTasks();
	}
}
