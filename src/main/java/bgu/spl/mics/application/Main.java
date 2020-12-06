package bgu.spl.mics.application;

import bgu.spl.mics.*;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
//import bgu.spl.mics.example.messages.ExampleEvent;
//import bgu.spl.mics.example.services.ExampleEventHandlerService;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

/** This is the Main class of the application. You should parse the input file, 
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
	public static void main(String[] args) {
		if(args.length!=2)
		{
			System.out.println("No valid input arguments were given");
			System.out.println("Please provide valid input.json path and output.json path");
			return; //exit main
		}
		String inputFilePath = args[0]; //input file path from arguments
		String outputFilePath = args[1];//output file path from arguments
		Input input;
		try{
			input= getInputFromJson(inputFilePath);
		}
		catch (IOException e) {
			e.printStackTrace();
		}



		Diary recordDiary = Diary.getInstance();
		try {
			diaryToJson(outputFilePath,recordDiary);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	public static Input getInputFromJson(String filePath) throws IOException {
		Gson gson = new Gson();
		try (Reader reader = new FileReader(filePath)) {
			return gson.fromJson(reader, Input.class);
		}
	}

	public static void diaryToJson(String filePath, Diary recordDiary) throws IOException {
		Gson gson = new Gson();
		try (Writer writer = new FileWriter(filePath)) {
			gson.toJson(recordDiary, writer);
		}
	}
}


