*** This repository is the implementation of the following papers: ***

1) Hien To, Leyla Kazemi, Cyrus Shahabi. Geocrowd: A Server-Assigned Crowdsourcing Framework.

Related studies:

https://bitbucket.org/hto/privategeocrowd/

https://bitbucket.org/hto/geocast/

---------------------- Version --------------------------
1.1

---------------------- Packages --------------------------

cplex: 
	solve bipartite matching with IBM cplex library

org.datasets.gowalla:
	preprocess gowalla dataset
	
org.datasets.yelp:
	preprocess yelp dataset
	
org.geocrowd:
	main package, with GeoCrowd.java
	
org.geocrowd.maxmatching:
	a package to solve weighted bipartite matching
	
org.geocrowd.maxflow:
	an opensource to solve max-flow using Ford-Fulkerson algorithm

test.geocrowd:
	test package, run experiments

---------------------- How to run? ----------------------
----------- Gowalla
Step 1: Run PreProcess.java
Step 2: Run GeoCrowd.java

Step 1: run PreProcessTest.java

1) testFilterInput():	to get data points in California region

2) generateWorkers():	generate workers in folder dataset/real/gowalla/worker. 
Each file is associated with a day. 
The format is as shown in WORKER INPUT FILES. 

3) computeLocationEntropy(): generate location entropy in file gowalla_loc_entropy (format shown below).

Step 2: run GeoCrowd.java

1) testGenerateGowallaTasks(): output file dataset\real\gowalla\task.

2) testGeoCrowd():

----------- Yelp

----------- SYN -----------------------------------------------
----------- SYN-UNIFORM
Step 1: Run randomly_distributed.m to generate worker set and task set
----------- SYN-SKEWED
Step 1: Run gaussian_clusters.m to generate worker set and task set
----------------------
Next: Run PreProcess.java, testGenerateSynWorkersTasks()

Step 2: Run GeoCrowd.java	testGeoCrowd().

Note that we need to set DATA_SET = 1 for SYN-SKEWED and DATASET = 2 for SYN-UNIFORM




---------------------- gowalla_totalCheckins_CA ----------------------
Extract data from Gowalla dataset. This is an intermediate input file
******
userID	datetime	lat	lng	pointID
2	2010-10-21T00:03:50Z	34.0430230998	-118.2671570778	14637
******

---------------------- gowalla_entropy ----------------------
Location entropy of each location id
******
loc_id,	loc_entropy
******

---------------------- gowalla_loc_entropy ----------------------
Location entropy of each grid cell (after grid discretization)
******
row,col,loc_entropy
******

---------------------- Real Data sets ----------------------
WORKER INPUT FILES:
******
worker_id, latitude, longitude, maxT, working_region[min_lat, min_lng, max_lat, max_lng], expertise_ids[expertise_id]
EAvzjtPx7kBk83GCWiaSDA,33.5130361,-112.08681055,4,[33.500805,-112.0995348,33.5252672,-112.0740863],[33,11,4,5,6]
******

TASK INPUT FILES:
******
latitude, longitude, time_instance, density, task_type
36.9778628999,-121.96604528228605,10,1.9609640474436814,1
******

---------------------- Synthetic Data sets ----------------------
WORKER INPUT FILES
latitude, longitude

TASK INPUT FILES:
latitude, longitude
				
