package org.example;

import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, ParseException {
        JsonFileHandler handler = new JsonFileHandler();
        Scanner in = new Scanner(System.in);
        System.out.println("enter path of file");
        String path = in.nextLine();
        JSONArray json = handler.getTicketsFromFile(path);
        json = handler.filterTicketsByCities(json);
        handler.showTimeBetweenCities(json);
        handler.showDifferentBetweenAvgAndMedian(json);
    }
}