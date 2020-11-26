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
			System.out.println("Valid input arguments weren't given");
			System.out.println("Please provide valid input.json path and output.json path");
			return; //exit main
		}
		String inputFilePath = args[0]; //input file path from arguments
		String outputFilePath = args[1];//output file path from arguments

		handleJsonInput(inputFilePath);

		System.out.println("SPL_PROJECT_2 Marina's Revenge!!!!");
	}

	private static void handleJsonInput(String inputFilePath) {
		Gson gson = new Gson();
		String jsonString = jsonToString(inputFilePath);
		if(jsonString.isEmpty()) {
			System.out.println("Empty json or error reading input json file");
			return; //exit main
		}
		try {
			JsonObject json = gson.fromJson(jsonString, JsonObject.class);
			long R2D2 = Long.parseLong(json.get("R2D2").toString());
			long Lando = Long.parseLong(json.get("Lando").toString());
			long Ewoks = Long.parseLong(json.get("Ewoks").toString());
			JsonArray attacksJson = json.get("attacks").getAsJsonArray();
			List<Attack> attackList = new ArrayList<>();
			Type listType = new TypeToken<List<Integer>>() {
			}.getType();
			for (int i = 0; i < attacksJson.size(); i++) {
				int duration = Integer.parseInt(attacksJson.get(i).getAsJsonObject().get("duration").toString());
				String serialsString = attacksJson.get(i).getAsJsonObject().get("serials").toString();
				List<Integer> serials = gson.fromJson(serialsString, listType);
				attackList.add(new Attack(serials, duration));
			}
		}
		catch (Exception e) {
			System.out.println("Problem parsing input json to objects");
			e.printStackTrace();
		}
	}
	private static String jsonToString(String inputFilePath)
	{
		File inputFile;

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
		return jsonString;
	}
}
