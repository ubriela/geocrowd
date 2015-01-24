package test.datasets;

import static org.junit.Assert.*;

import java.lang.Character.UnicodeScript;
import java.util.ArrayList;
import java.util.Vector;

import org.datasets.syn.DatasetGenerator;
import org.datasets.syn.Distribution2DEnum;
import org.datasets.syn.InstancesGenerator;
import org.datasets.syn.WTCountGenerator;
import org.datasets.syn.WTCycleEnum;
import org.datasets.syn.dtype.Rectangle;
import org.junit.Test;

/**
 * Generate dataset
 * 
 * @author HT186010
 * 
 */
public class DatasetGeneratorTest {

	@Test
	public final void testGenerate2DPoints() {
		// DatasetGenerator dg = new
		// DatasetGenerator("./res/dataset/twod/test.txt");
		// DatasetGenerator.gaussianCluster = 10;
		// dg.generate2DDataset(1000, new Rectangle(0, 0, 999, 999),
		// Distribution2DEnum.GAUSSIAN_2D);

		// ArrayList<Integer> counts = WTCountGenerator.generateCounts(100,
		// 1000, WTCycleEnum.COSINE);
		// for (int i : counts)
		// System.out.println(i);

		InstancesGenerator ig = new InstancesGenerator(
		168, WTCycleEnum.COSINE, WTCycleEnum.COSINE, 
		100, 1000, new Rectangle(0, 0, 99, 99),
		Distribution2DEnum.GAUSSIAN_2D, Distribution2DEnum.GAUSSIAN_2D,
				"./res/dataset/worker/", "./res/dataset/task/");
	}

	@Test
	public final void testGeneratePoints() {
		// DatasetGenerator dg = new
		// DatasetGenerator("./res/restrnts_sampled100.txt");
		// int[] percentages = { 90, 80, 70, 60, 50, 40, 30, 20, 10, 5, 1 };
		// int[] sizes = {360000, 320000, 280000, 240000, 200000, 160000,
		// 120000, 80000, 40000, 20000, 4000};
		//
		// for (int i = 0; i < percentages.length; i++) {
		// dg.generateSamplingDataset(sizes[i], "./res/restrnts_sampled" +
		// percentages[i] + ".txt");
		// }

		// DatasetGenerator dg = new DatasetGenerator("./dataset/out/test.txt");
		// dg.generateOneDimDataset2(10000, 0, 10000, 2, true, true, 6);
	}
}