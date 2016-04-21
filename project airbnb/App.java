package com.ml.project.mlproject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import au.com.bytecode.opencsv.CSVWriter;

public class App {

	public static void main(String[] args) throws IOException {

		String csvFile = "/Users/shivugururaj/Documents/gdrive/spring 2016/machine learning/shivu/project/train_users_2.csv";
		CSVWriter writer = new CSVWriter(new FileWriter(
				"/Users/shivugururaj/Documents/gdrive/spring 2016/machine learning/shivu/project/newTrainFile.csv"));
		String line = "";
		BufferedReader bf = new BufferedReader(new FileReader(csvFile));
		bf.readLine();

		Map<String, String> labelMap = new HashMap<String, String>();
		labelMap.put("US", "0");
		labelMap.put("FR", "1");
		labelMap.put("CA", "2");
		labelMap.put("GB", "3");
		labelMap.put("ES", "4");
		labelMap.put("IT", "5");
		labelMap.put("PT", "6");
		labelMap.put("NL", "7");
		labelMap.put("DE", "8");
		labelMap.put("AU", "9");
		labelMap.put("other", "10");
		while ((line = bf.readLine()) != null) {

			char[] ageVector = new char[20];

			String[] lineEntry = line.split(",");

			String agevalue = lineEntry[5];
			String classLable = lineEntry[15];
			String dateFirstBooked = lineEntry[3];
			String gender = lineEntry[4];
			classLable = classLable.trim();
			dateFirstBooked = dateFirstBooked.trim();
			if (classLable.isEmpty() || dateFirstBooked.isEmpty() || classLable.equalsIgnoreCase("NDF")
					|| dateFirstBooked.equalsIgnoreCase("-unknown-") || classLable == null || dateFirstBooked == null) {
				// skip this entry
			}

			else {
				// handling gender data
				if (gender.trim().isEmpty() || gender == null) {
					gender = "-unknown-";
				}

				// update dates to season
				String[] firstBookedDate = dateFirstBooked.split("-");
				int bookingMonth = Integer.parseInt(firstBookedDate[1]);
				if (bookingMonth >= 1 && bookingMonth <= 4) {
					dateFirstBooked = "Spring";
				} else if (bookingMonth >= 5 && bookingMonth <= 7) {
					dateFirstBooked = "Summer";
				} else if (bookingMonth >= 8 && bookingMonth <= 10) {
					dateFirstBooked = "Fall";
				} else {
					dateFirstBooked = "Winter";
				}

				if (agevalue.isEmpty() || agevalue == null) {
					// default the age value , change the class label to number
					// and update the training file
					lineEntry[5] = "00000000000000000000";
					lineEntry[15] = labelMap.get(classLable);
					lineEntry[3] = dateFirstBooked;
					lineEntry[4] = gender;
					writer.writeNext(lineEntry);

				} else {
					// get the age value and check if its >5 or <100
					double ageVal = Double.parseDouble((agevalue.trim()));
					int age = (int) ageVal;

					if (age > 5 || age < 100) {
						// insert a vector value of this format
						// 00000000000000000010 , change the classlabel to
						// number and update the training file
						int index = age / 5;
						for (int i = 0; i < ageVector.length; i++) {
							if (i == index) {
								ageVector[index] = '1';

							} else
								ageVector[i] = '0';
						}
						lineEntry[5] = new String(ageVector);
						lineEntry[15] = labelMap.get(classLable);
						lineEntry[3] = dateFirstBooked;
						lineEntry[4] = gender;
						writer.writeNext(lineEntry);

					}
				}

			}

		}
		writer.close();

	}

}
