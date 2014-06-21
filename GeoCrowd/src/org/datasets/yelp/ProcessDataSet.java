/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.datasets.yelp;


/**
 *
 * @author dkh
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import org.datasets.gowalla.Point;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;

public class ProcessDataSet {

    /**
     * @param args the command line arguments
     */
    public static double minLat = Double.MAX_VALUE;
    public static double maxLat = (-1) * Double.MAX_VALUE;
    public static double minLong = Double.MAX_VALUE;
    public static double maxLong = (-1) * Double.MAX_VALUE;
    static Hashtable<String, Hashtable<String, Double>> Business_Location = new Hashtable<>();
    static Hashtable<String, Hashtable<Integer, String>> Review = new Hashtable<>();
    static Hashtable<Integer, Hashtable<String, Hashtable<Integer, String>>> Review_Date = new Hashtable<>();
    static Hashtable<String, Hashtable<Integer, String>> Business_Categories = new Hashtable<>();
    static Hashtable<String, ArrayList> User_Categories = new Hashtable<>();
    static Hashtable<String, Long> User_ReviewCount = new Hashtable<>();
    static JSONParser parser = new JSONParser();
    // int count = 0;
    static ArrayList Expertise = new ArrayList();
    static String type = "none";
    static int total_expertise_user = 0;

    public static void Access_Business() {
        try {
            FileReader f = new FileReader(constant.business);
            BufferedReader in = new BufferedReader(f);
            while (in.ready()) {

                String line = in.readLine();
                Object obj = parser.parse(line);
                JSONObject jsonObject = (JSONObject) obj;
                String bus_id = jsonObject.get("business_id").toString();
                double lng = (double) jsonObject.get("longitude");
                double lat = (double) jsonObject.get("latitude");
                if (lat < minLat) {
                    minLat = lat;
                }
                if (lat > maxLat) {
                    maxLat = lat;
                }
                if (lng < minLong) {
                    minLong = lng;
                }
                if (lng > maxLong) {
                    maxLong = lng;
                }
                Hashtable<String, Double> longlat = new Hashtable<>();
                longlat.put("lng", lng);
                longlat.put("lat", lat);
                Business_Location.put(bus_id, longlat);

                JSONArray categories = (JSONArray) jsonObject.get("categories");

                if (categories.size() > 0) {
                    Hashtable<Integer, String> temp_business = new Hashtable<>();
                    temp_business.put(0, categories.get(0).toString());
                    Business_Categories.put(bus_id, temp_business);
                    if (!Expertise.contains(categories.get(0))) {
                        Expertise.add(categories.get(0));
                    }
                    if (categories.size() > 1) {
                        for (int i = 1; i < categories.size(); i++) {
                            Business_Categories.get(bus_id).put(i,
                                    categories.get(i).toString());
                            if (!Expertise.contains(categories.get(i))) {
                                Expertise.add(categories.get(i));
                            }
                        }
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void Access_User() {
        try {
            FileReader f = new FileReader(constant.user);
            BufferedReader in = new BufferedReader(f);
            while (in.ready()) {

                String line = in.readLine();
                Object obj = parser.parse(line);
                JSONObject jsonObject = (JSONObject) obj;
                String user = jsonObject.get("user_id").toString();
                long review = (long) jsonObject.get("review_count");
                if (review > constant.MaxReview) {
                    review = constant.MaxReview;
                }
                User_ReviewCount.put(user, review);

            }

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public static void Access_Review() {
        try {
            FileReader f = new FileReader(constant.curtail_review);
            BufferedReader in = new BufferedReader(f);
            while (in.ready()) {

                String line = in.readLine();
                Object obj = parser.parse(line);
                JSONObject jsonObject = (JSONObject) obj;
                String user = jsonObject.get("user_id").toString();
                String business = jsonObject.get("business_id").toString();
                int time_instance = DateIt(jsonObject.get("date").toString());

                if (Review.keySet().contains(user)) {
                    int t = Review.get(user).size();
                    Review.get(user).put(t, business);
                } else {
                    Hashtable<Integer, String> temp_business = new Hashtable<>();
                    temp_business.put(0, business);
                    Review.put(user, temp_business);
                }
                // SPLIT WORKER BY TIME INSTANCE
                if (Review_Date.containsKey(time_instance)) {
                    if (Review_Date.get(time_instance).containsKey(user)) {
                        int t = Review_Date.get(time_instance).get(user).size();
                        Review_Date.get(time_instance).get(user).put(t, business);
                    } else {
                        Hashtable<Integer, String> temp_business = new Hashtable<>();
                        temp_business.put(0, business);
                        Review_Date.get(time_instance).put(user, temp_business);

                    }
                } else {
                    Hashtable<Integer, String> temp_business = new Hashtable<>();
                    temp_business.put(0, business);
                    Hashtable<String, Hashtable<Integer, String>> temp_user = new Hashtable<>();
                    temp_user.put(user, temp_business);
                    Review_Date.put(time_instance, temp_user);
                }




            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Total user reviewed: " + Review.size());

    }

    public static void PreProcessTask() {
        JSONParser parser = new JSONParser();
        // int count = 0;
        ArrayList Expertise = new ArrayList();
        String type = "none";
        Expertise.add(type);
        int q = 0;
        int k = 1;
        // int c = 0;

        try {
            FileReader f = new FileReader(constant.business);
            BufferedReader in = new BufferedReader(f);
            while (in.ready()) {
                if (k == 500) {
                    q++;
                    k = 1;
                }
                k++;

                String line = in.readLine();
                Object obj = parser.parse(line);
                JSONObject jsonObject = (JSONObject) obj;

                // get review count of a business
                long rc = (long) jsonObject.get("review_count");

                double entropy = 0;

                // get business longitude
                double lng = (double) jsonObject.get("longitude");
                // get business lattitude
                double lat = (double) jsonObject.get("latitude");
                // get business categories
                JSONArray categories = (JSONArray) jsonObject.get("categories");
                type = "none";
                Random generator = new Random();
                if (categories.size() > 0) {
                    int i = generator.nextInt(categories.size());
                    type = categories.get(i).toString();
                    if (!Expertise.contains(type)) {
                        Expertise.add(type);
                    }
                }
                // print out business details
                // Utils.writefile(lat + "," + lng + "," + 1 + "," + q + "," +
                // entropy + "," + Expertise.indexOf(type), q);

            }

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public static void save_Statistic() {
        int total_expertise = Expertise.size();
        int total_user_review = Review.size();
        Iterator users = Review.keySet().iterator();
        int sum_exp = 0;
        int sum_review = 0;
        while (users.hasNext()) {
            String u_id = (String) users.next();
            sum_exp += User_Categories.get(u_id).size();
            sum_review += Review.get(u_id).size();
        }
        int avg_review = sum_review / total_user_review;
        int avg_exp_per_user = sum_exp / User_Categories.size();
        StringBuilder sb = new StringBuilder();

        sb.append("Total expertise: " + total_expertise);
        sb.append("\nTotal user with expertise: " + total_expertise_user);

        sb.append("\nTotal user reviewed: " + total_user_review);
        sb.append("\nAvg expertise per user: " + avg_exp_per_user);
        sb.append("\nAvg rating per user: " + avg_review);
        sb.append("\nTotal Business: " + Business_Location.size());
        sb.append("\nTotal Task requires Expertise: " + Business_Categories.size());
        Utils.writefile2(sb.toString(), constant.SaveStatistic);


    }

    public static Hashtable<Integer, Hashtable<Integer, Integer>> computeLocationDensity() {
        Hashtable<Integer, Hashtable<Integer, Integer>> Density;
        Density = new Hashtable<>();
        Iterator Business = Business_Location.keySet().iterator();
        while (Business.hasNext()) {
            String BusinessID = Business.next().toString();
            Double lat = Business_Location.get(BusinessID).get("lat");
            Double lng = Business_Location.get(BusinessID).get("lng");
            int row = getRowIdx(lat);
            int col = getColIdx(lng);
            if (Density.containsKey(row)) {
                if (Density.get(row).containsKey(col)) {
                    Density.get(row).put(col, Density.get(row).get(col) + 1);
                } else {
                    Density.get(row).put(col, 1);
                }
            } else {
                Hashtable<Integer, Integer> rows = new Hashtable<Integer, Integer>();
                rows.put(col, 1);
                Density.put(row, rows);
            }

        }

        return Density;

    }

    public static void saveLocationDensity(
            Hashtable<Integer, Hashtable<Integer, Integer>> Density) {

        StringBuffer sb = new StringBuffer();
        Iterator row_it = Density.keySet().iterator();
        while (row_it.hasNext()) {
            int row = (Integer) row_it.next();
            Iterator col_it = Density.get(row).keySet().iterator();
            while (col_it.hasNext()) {
                int col = (Integer) col_it.next();
                sb.append(row + "," + col + "," + Density.get(row).get(col));
                sb.append("\n");
            }
        }
        Utils.writefile2(sb.toString(), constant.entropy);
    }

    public static void saveBoundary() {
        Utils.writefile2(minLat + "," + minLong + "," + maxLat + "," + maxLong,
                constant.boundary);
    }

    public static void split_Worker_by_time() {

        Random gen = new Random();
        Iterator time_instance = Review_Date.keySet().iterator();
        StringBuilder sb = new StringBuilder();
        while (time_instance.hasNext()) {
            sb.delete(0, sb.length());
            int instance = (Integer) time_instance.next();
            Iterator users = Review_Date.get(instance).keySet().iterator();
            while (users.hasNext()) {
                StringBuilder sb_temp = new StringBuilder();
                String u_id = (String) users.next();
                Iterator businesses = Review_Date.get(instance).get(u_id).keySet().iterator();
                int i = gen.nextInt(Review_Date.get(instance).get(u_id).size());
                double minLatitude = Double.MAX_VALUE;
                double maxLatitude = (-1) * Double.MAX_VALUE;
                double minLongitude = Double.MAX_VALUE;
                double maxLongitude = (-1) * Double.MAX_VALUE;
                while (businesses.hasNext()) {
                    int col = (Integer) businesses.next();
                    double temp_lat = Business_Location.get(
                            Review_Date.get(instance).get(u_id).get(col).toString()).get("lat");
                    double temp_lng = Business_Location.get(
                            Review_Date.get(instance).get(u_id).get(col).toString()).get("lng");

                    if (temp_lat < minLatitude) {
                        minLatitude = temp_lat;
                    }
                    if (temp_lat > maxLatitude) {
                        maxLatitude = temp_lat;
                    }
                    if (temp_lng < minLongitude) {
                        minLongitude = temp_lng;
                    }
                    if (temp_lng > maxLongitude) {
                        maxLongitude = temp_lng;
                    }

                    // System.out.print (Review.get(u_id).get(col).toString());

                    if (Business_Categories.keySet().contains(
                            Review_Date.get(instance).get(u_id).get(col).toString())) {
                        for (int j = 0; j < Business_Categories.get(
                                Review_Date.get(instance).get(u_id).get(col).toString()).size(); j++) {
                            String expertise = Business_Categories
                                    .get(Review_Date.get(instance).get(u_id).get(col).toString())
                                    .get(j).toString();
                            if (!User_Categories.get(u_id).contains(expertise)) {
                                User_Categories.get(u_id).add(expertise);
                            }
                        }
                    }

                }

                double lat = (minLatitude + maxLatitude) / 2;
                double lon = (minLongitude + maxLongitude) / 2;
                sb_temp.append(u_id + "," + lat + "," + lon);


                sb_temp.append("," + Review_Date.get(instance).get(u_id).size());


                sb_temp.append(",[" + minLatitude + "," + minLongitude + ","
                        + maxLatitude + "," + maxLongitude + "]");

                if (User_Categories.get(u_id).size() != 0) {

                    sb_temp.append(",[");
                    for (int j = 0; j < User_Categories.get(u_id).size(); j++) {
                        if (j > 0 && j < User_Categories.get(u_id).size()) {
                            sb_temp.append(",");
                        }
                        sb_temp.append(String.valueOf(Expertise
                                .indexOf(User_Categories.get(u_id).get(j))));
                    }

                    sb_temp.append("]\n");

                    sb.append(sb_temp);

                    //  total_expertise_user++;
                } else {
                    // System.out.println(u_id + "-one empty here");
                }
            }
            Utils.writefile2(sb.toString(), constant.SplitWorkerByTime + instance + constant.suffix);
        }


    }
    
    public static void saveTaskWorkers() {
    	// Tasks
        Iterator Business = Business_Location.keySet().iterator();
        StringBuffer sb = new StringBuffer();
		
        while (Business.hasNext()) {
            String BusinessID = Business.next().toString();
            Double lat = Business_Location.get(BusinessID).get("lat");
            Double lng = Business_Location.get(BusinessID).get("lng");
            sb.append(lat + "\t" + lng + "\n");
        }
        
		FileWriter writer;
		try {
			writer = new FileWriter(constant.tasks_loc);
			BufferedWriter out = new BufferedWriter(writer);
			out = new BufferedWriter(writer);
			out.write(sb.toString());
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Workers
		
		
    }
    
    public static void saveWorkersMCD(String filename) {
    	Iterator users = Review.keySet().iterator();
        StringBuffer sb = new StringBuffer();
        
        while(users.hasNext()) {
        	String u_id = (String) users.next();
            
            Iterator businesses = Review.get(u_id).keySet().iterator();
            ArrayList<Point> points = new ArrayList<Point>();
            while (businesses.hasNext()) {
                int col = (Integer) businesses.next();
                String x = Review.get(u_id).get(col).toString();
                if (Business_Location.get(x) == null)
                	continue;
                double lat = Business_Location.get(x).get("lat");
                double lng = Business_Location.get(
                        Review.get(u_id).get(col).toString()).get("lng");
                points.add(new Point(lat, lng));
            }
            
            // compute MCD
            double mcd = org.geocrowd.util.Utils.MCD(points.get(0), points);
            sb.append(mcd + "\n");
        }
        
		FileWriter writer;
		try {
			writer = new FileWriter(filename);
			BufferedWriter out = new BufferedWriter(writer);
			out.write(sb.toString());
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }

    public static void saveUser_Worker() {

        Random gen = new Random();
        int c = 0;
        int file_i = 0;
        Iterator users = Review.keySet().iterator();
        StringBuilder sb = new StringBuilder();

        while (users.hasNext()) {
        	String u_id = (String) users.next();
            StringBuilder sb_temp = new StringBuilder();
            if (c >= constant.WorkerPerFile || !users.hasNext()) {
                System.out.println("Worker instance: " + file_i);
                Utils.writefile2(sb.toString(), constant.SaveWorker + file_i
                        + constant.suffix);
                c = 0;
                file_i++;
                sb.delete(0, sb.length());
            }

            
            ArrayList tempal = new ArrayList();
            if (!User_Categories.keySet().contains(u_id)) {
                User_Categories.put(u_id, tempal);
            }
            Iterator businesses = Review.get(u_id).keySet().iterator();
            int i = gen.nextInt(Review.get(u_id).size());
            double minLatitude = Double.MAX_VALUE;
            double maxLatitude = (-1) * Double.MAX_VALUE;
            double minLongitude = Double.MAX_VALUE;
            double maxLongitude = (-1) * Double.MAX_VALUE;
            while (businesses.hasNext()) {
                int col = (Integer) businesses.next();
                String x = Review.get(u_id).get(col).toString();
                if (Business_Location.get(x) == null)
                	continue;
                double temp_lat = Business_Location.get(x).get("lat");
                
                double temp_lng = Business_Location.get(
                        Review.get(u_id).get(col).toString()).get("lng");

                if (temp_lat < minLatitude) {
                    minLatitude = temp_lat;
                }
                if (temp_lat > maxLatitude) {
                    maxLatitude = temp_lat;
                }
                if (temp_lng < minLongitude) {
                    minLongitude = temp_lng;
                }
                if (temp_lng > maxLongitude) {
                    maxLongitude = temp_lng;
                }

                // System.out.print (Review.get(u_id).get(col).toString());

                if (Business_Categories.keySet().contains(
                        Review.get(u_id).get(col).toString())) {
                    for (int j = 0; j < Business_Categories.get(
                            Review.get(u_id).get(col).toString()).size(); j++) {
                        String expertise = Business_Categories
                                .get(Review.get(u_id).get(col).toString())
                                .get(j).toString();
                        if (!User_Categories.get(u_id).contains(expertise)) {
                            User_Categories.get(u_id).add(expertise);
                        }
                    }
                }

            }

            double lat = (minLatitude + maxLatitude) / 2;
            double lon = (minLongitude + maxLongitude) / 2;
            sb_temp.append(u_id + "," + lat + "," + lon);

            if (User_ReviewCount.containsKey(u_id)) {
                sb_temp.append("," + User_ReviewCount.get(u_id));
            } else {
                sb_temp.append("," + Review.get(u_id).size());
            }

            sb_temp.append(",[" + minLatitude + "," + minLongitude + ","
                    + maxLatitude + "," + maxLongitude + "]");

            if (User_Categories.get(u_id).size() != 0) {

                sb_temp.append(",[");
                for (int j = 0; j < User_Categories.get(u_id).size(); j++) {
                    if (j > 0 && j < User_Categories.get(u_id).size()) {
                        sb_temp.append(",");
                    }
                    sb_temp.append(String.valueOf(Expertise
                            .indexOf(User_Categories.get(u_id).get(j))));
                }

                sb_temp.append("]\n");

                sb.append(sb_temp);

                c++;
                total_expertise_user++;
            } else {
                // System.out.println(u_id + "-one empty here");
            }
        }
        System.out.println(c);

    }

    public static void saveBusiness_Task() {
        Hashtable<Integer, Hashtable<Integer, Integer>> Density = computeLocationDensity();
        Iterator businesses = Business_Categories.keySet().iterator();
        int c = 0;
        int time = 0;
        StringBuilder sb = new StringBuilder();
        while (businesses.hasNext()) {
            if (c >= constant.TaskPerFile) {
                System.out.println("Task instance: " + time);
                Utils.writefile2(sb.toString(), constant.SaveTask + time
                        + constant.suffix);
                c = 0;
                time++;
                sb.delete(0, sb.length());
            }
            String BusinessID = businesses.next().toString();
            Double lat = Business_Location.get(BusinessID).get("lat");
            Double lng = Business_Location.get(BusinessID).get("lng");
            int row = getRowIdx(lat);
            int col = getColIdx(lng);
            int dens = Density.get(row).get(col);
            Random generator = new Random();
            int chosen = generator.nextInt(Business_Categories.get(BusinessID)
                    .size());
            String TaskType = Business_Categories.get(BusinessID).get(chosen);
            int type_chosen = Expertise.indexOf(TaskType);
            sb.append(lat + "," + lng + "," + time + "," + dens + ","
                    + type_chosen);
            sb.append("\n");
            c++;
        }
    }

    public static int getRowIdx(double lat) {
        return (int) ((lat - minLat) / constant.realResolution);
    }

    public static int getColIdx(double lng) {
        return (int) ((lng - minLong) / constant.realResolution);
    }

    public static void Curtail_Review_File() {
        Hashtable<String, Hashtable<Integer, String>> Review = new Hashtable<>();
        JSONParser parser = new JSONParser();
        int c = 0;

        try {
            FileReader f = new FileReader(constant.review);
            BufferedReader in = new BufferedReader(f);
            StringBuffer sb = new StringBuffer();
            while (in.ready()) {
                if (c % 1000 == 0) {
                    System.out.println("Done. # of reviews is:" + c);
                }
                String line = in.readLine();
                Object obj = parser.parse(line);
                JSONObject jsonObject = (JSONObject) obj;
                String user = jsonObject.get("user_id").toString();
                String business = jsonObject.get("business_id").toString();
                String datereview = jsonObject.get("date").toString();
                JSONObject obj_towrite = new JSONObject();
                obj_towrite.put("business_id", business);
                obj_towrite.put("user_id", user);
                obj_towrite.put("date", datereview);
                sb.append(obj_towrite.toJSONString());
                sb.append("\n");
                c++;
            }
            Utils.writefile2(sb.toString(), constant.curtail_review);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static int DateIt(String t) {
        int to_return = 0;
        String[] temp = t.split("-");
        int[] number = new int[temp.length];
        for (int i = 0; i < temp.length; i++) {
            number[i] = Integer.parseInt(temp[i]);
        }
        switch (number[0]) {
            case 2005:
                to_return = 0;
                break;
            case 2006:
                to_return = 1;
                break;
            case 2013:
                to_return = 21;
                break;
            default:
                to_return = (number[1] / 4) + ((number[0] - 2007) * 3 + 2);
                break;
        }
        return to_return;
    }
}