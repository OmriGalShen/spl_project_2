package bgu.spl.mics.application;

import bgu.spl.mics.*;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
//import bgu.spl.mics.example.messages.ExampleEvent;
//import bgu.spl.mics.example.services.ExampleEventHandlerService;

import java.io.*;
import java.util.*;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;

/** This is the Main class of the application. You should parse the input file, 
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
	public static void main(String[] args) {
		if(args.length!=2)
		{
			System.out.println("Valid input arguments weren't given");
			System.out.println("Please provide valid input.json path and output.json path");
			return;
		}
		String inputFilePath = args[0]; //from arguments
		String outputFilePath = args[1];//from arguments
		File inputFile;
		Gson gson = new Gson();
		String jsonString= "";
		try {
			inputFile = new File(inputFilePath);
			Scanner myReader = new Scanner(inputFile);

			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				jsonString+=data;
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("Input json path is not valid");
			e.printStackTrace();
		}
		JsonObject json = gson.fromJson(jsonString, JsonObject.class);
		long R2D2 = Long.parseLong(json.get("R2D2").toString());
		long Lando = Long.parseLong(json.get("Lando").toString());
		long Ewoks = Long.parseLong(json.get("Ewoks").toString());
		JsonArray attacksJson = json.get("attacks").getAsJsonArray();
		Attack[] attacks = new Attack[attacksJson.size()];
		for(int i=0;i<attacksJson.size();i++)
		{
			int duration = Integer.parseInt(attacksJson.get(i).getAsJsonObject().get("duration").toString());
			System.out.println("duration:"+duration);
		}

		System.out.println("SPL_PROJECT_2 Marina's Revenge!!!!");
	}
}
