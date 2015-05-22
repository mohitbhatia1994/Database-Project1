// Name :- Mohit Bhatia

package edu.buffalo.cse462;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class Project01_Main {
	
	static final String const1 = "jdbc:oracle:thin:";
	static final String sep = "/";
	static final String const2 = "@aos.acsu.buffalo.edu:1521/aos.buffalo.edu";
	
	static final String cityCol = "City";
	static final String latCol = "Latitude";
	static final String longCol = "Longitude";
	static final long R = 12742;
	static Connection conn;
	static String url = "";
	
	static final String query1 = "RANGE_QUERY";
	static final String query2 = "NN_QUERY";
	static final String query3 = "MIN_ROUNDTRIP_QUERY";
	
	static String sql = "SELECT City, Latitude, Longitude FROM MAP";
	static String sql1 = sql + " WHERE City = ?";
	
	static Double cityLat = 0d;
	static Double cityLong = 0d;
	static String tempCity = "";
	static Double tempCityLat = 0d;
	static Double tempCityLong = 0d;
	
	static String queryType = "";
	
	static Double radius;
	
	static SortedMap<Double,String> map = new TreeMap<Double,String>();

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		// TODO Auto-generated method stub
		if(args.length<2) {
			System.out.print("Please give proper username and password");
			return;
		}
		String username = args[0];
		String password = args[1];
		url = const1 + username + sep + password + const2;
		
		Class.forName("oracle.jdbc.driver.OracleDriver");
		conn = DriverManager.getConnection(url);

		if(args.length>2) {
			queryType = args[2];
			
			if(queryType.equals(query1)) {				
				if(args.length>4) {
					String city = args[3];
					radius = Double.parseDouble(args[4]);

					ResultSet rs = extractDataForParticularCity(sql1,city,null);
					while(rs.next()) {
						cityLat = rs.getDouble(latCol);
						cityLong = rs.getDouble(longCol);
					}
					
					rs = extractDataForParticularCity(sql1,null,null);
					iterateRows(rs, city);
				}
			} else if (queryType.equalsIgnoreCase(query2)) {
				if(args.length>4) {
					String city = args[3];
					Integer k = 0;
					try {
						k = Integer.parseInt(args[4]);
					} catch (Exception ex) {
						System.out.println(ex.getMessage());
					}				
					if(k>0) {
						ResultSet rs = extractDataForParticularCity(sql1,city,null);
						while(rs.next()) {
							cityLat = rs.getDouble(latCol);
							cityLong = rs.getDouble(longCol);
						}
						
						rs = extractDataForParticularCity(sql1,null,null);
						
						//Collection<String> cities = map.values();
						Set keys = map.keySet();
						Iterator iter = keys.iterator();
						for(int i=0;i<k && iter.hasNext();i++) {
							double key = (double)iter.next();
							String value = map.get(key);
							if(key==0d || value.equals(city)) {
								i--;
								continue;
							}
							System.out.println(value + " " + Math.round(key)*100/100.00);
						}
						map.clear();
						
						
					} else {
						System.out.println("k should be strictly positive");
					}
				}
				
			} else if (queryType.equalsIgnoreCase(query3)) {
				if(args.length>4) {
					String city1 = args[3];
					String city2 = args[4];
					double lat1 = 0d; double lat2 = 0d; double long1 = 0d; double long2 = 0d;
					int i =0;
					ResultSet rs = extractDataForParticularCity(sql1,city1,city2);
					while(rs.next()) {
						if(i==0) {
							lat1 = rs.getDouble(latCol);
							long1 = rs.getDouble(longCol);
							i++;
						}
						lat2 = rs.getDouble(latCol);
						long2 = rs.getDouble(longCol);
					}
					double distance = calculateDistance(lat1,long1,lat2,long2);
					
					rs = extractDataForParticularCity(sql1,null,null);
					double max = 0d;
					String shortTripCity = "";
					while(rs.next()) {
						tempCity = rs.getString(cityCol);
						if(tempCity.equals(city1) || tempCity.equals(city1)) continue;
						tempCityLat = rs.getDouble(latCol);
						tempCityLong = rs.getDouble(longCol);

						double distance1 = calculateDistance(lat1,long1,tempCityLat,tempCityLong);
						double distance2 = calculateDistance(lat2,long2,tempCityLat,tempCityLong);
						double totalDist = distance1 + distance2;
						if(totalDist>max) {
							max=totalDist;
							shortTripCity = tempCity;
						}
					}
					System.out.println(city1 + " " + city2 + " " + shortTripCity + " " + Math.round(max + distance)*100/100.00);
				}
			}
		}

	}
	
	public static void iterateRows(ResultSet rs, String city) throws SQLException {
		while(rs.next()) {
			tempCity = rs.getString(cityCol);
			if(tempCity.equals(city)) continue;
			tempCityLat = rs.getDouble(latCol);
			tempCityLong = rs.getDouble(longCol);
			double distance = Math.round(calculateDistance(cityLat,cityLong,tempCityLat,tempCityLong))*100/100.00;
			if(queryType.equals(query1)) {
				if(distance < radius) {
					System.out.println(tempCity + " " + distance);
				}
			} else {
				map.put(distance, tempCity);
			}
		}

	}
	
	public static double calculateDistance(double lat1, double long1, double lat2, double long2) {
		double latDist = Math.toRadians(lat2-lat1);
		double longDist = Math.toRadians(long2-long1);
		double h = Math.sin(latDist/2)*Math.sin(latDist/2)+Math.cos(lat1)*Math.cos(lat2)+Math.sin(longDist/2)*Math.sin(longDist/2);
		double c = Math.atan2(Math.sqrt(h), Math.sqrt(1-h));
		return R*c;
	}
	
	public static ResultSet extractDataForParticularCity(String sql, String city, String city2) throws SQLException {
		if(city==null) {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			return rs;
		}
		PreparedStatement prepstmt = conn.prepareStatement(sql);
		prepstmt.setString(1, city);
		ResultSet rs = prepstmt.executeQuery();
		return rs;
	}

}
