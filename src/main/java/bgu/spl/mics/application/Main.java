package bgu.spl.mics.application;

import bgu.spl.mics.*;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;

import java.io.*;

import com.google.gson.*;

/** This is the Main class of the application. You should parse the input file, 
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
	public static void main(String[] args) {
		// <----------input ---------->
		if(args.length != 2) {
			System.out.println("No valid input arguments were given");
			System.out.println("Please provide valid input.json path and output.json path");
			return; // exit main
		}
		String inputFilePath = args[0]; // input file path from arguments
		String outputFilePath = args[1]; // output file path from arguments
		Input input = null;
		try {
			input= getInputFromJson(inputFilePath);
		} catch (IOException e) {
			System.out.println("IOException on input from json file");
			e.printStackTrace();
		}
		if(input == null)
			throw new NullPointerException("Problem reading input from json");

		// <--------main program ---------->
		starWars(input);

		// <----------output ---------->
		Diary recordDiary = Diary.getInstance();
		try {
			diaryToJson(outputFilePath, recordDiary);
		} catch (IOException e) {
			System.out.println("IOException on output of Diary to json format");
			e.printStackTrace();
		}
	}

	/**
	 * A static helper function to read input.json file
	 * @param filePath
	 * @return input json path
	 * @throws IOException
	 */
	private static Input getInputFromJson(String filePath) throws IOException {
		Gson gson = new Gson();
		try (Reader reader = new FileReader(filePath)) {
			return gson.fromJson(reader, Input.class);
		}
	}

	/**
	 * A static helper function to produce output.json file
	 * @param filePath output json path
	 * @param recordDiary diary to output
	 * @throws IOException
	 */
	private static void diaryToJson(String filePath, Diary recordDiary) throws IOException {
		Gson gson = new Gson();
		try (Writer writer = new FileWriter(filePath)) {
			gson.toJson(recordDiary, writer);
		}
	}

	/**
	 * Main program, here threads are declared and run
	 * @param input used to retrieve input info
	 */
	private static void starWars(Input input) {

		System.out.println("A Long time ago in a galaxy far far away..."); ///////////////////////////////////////////

		int numberOfMS = 5;
		MicroService[] microArray = new MicroService[numberOfMS];
		Thread[] threads = new Thread[numberOfMS];
		Ewoks ewoks = Ewoks.getInstance(input.getEwoks());

		microArray[0] = new C3POMicroservice();
		microArray[1] = new HanSoloMicroservice();
		microArray[2] = new LandoMicroservice(input.getLando());
		microArray[3] = new R2D2Microservice(input.getR2D2());
		microArray[4] = new LeiaMicroservice(input.getAttacks());

		for (int i=0; i < numberOfMS; i++) {
			threads[i] = new Thread(microArray[i]);
		}
		for (Thread thread : threads) {
			thread.start();
		}
		try {
			for (Thread thread : threads) {
				thread.join();
			}
		} catch (InterruptedException e) {
			System.out.println("InterruptedException on threads join()");
			e.printStackTrace();
		}

		System.out.println("STAR WARS - A NEW HOPE FOR A GOOD SPL PROJECT!"); ////////////////////////////////////////////
		System.out.println("MAY THE FORCE BE WITH YOU"); //////////////////////////////////////////
	}
}


