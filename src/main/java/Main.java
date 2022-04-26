import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class Main {

    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";

        //Parser from CSV to JSON
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json, "data.json");

        //parser from XML to JSON
        List<Employee> list2 = parseXML("data.xml");
        String json2 = listToJson(list2);
        writeString(json2, "data2.json");

        //CSV parser
        String json3 = readString("data2.json");
        List<Employee> employeeList = jsonToList(json3);
        System.out.println(employeeList);

    }

    private static List<Employee> jsonToList(String json3) {
        List<Employee> listOfEmployee = new ArrayList<>();
        JSONParser parser = new JSONParser();
        try {
            JSONArray jsonArray = (JSONArray) parser.parse(json3);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                Employee employee = gson.fromJson(String.valueOf(jsonObject), Employee.class);
                listOfEmployee.add(employee);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return listOfEmployee;
    }

    private static String readString(String fileName) {
        String json = new String();
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))){
            json = bufferedReader.readLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    //XML-parser
    private static List<Employee> parseXML(String fileName) {
        List<Employee> listOfEmployee = new ArrayList<>();
        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(fileName));

            Node root = document.getDocumentElement();
            NodeList nodeList = document.getElementsByTagName("employee");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);
                long id = Long.parseLong(element.getElementsByTagName("id").item(0)
                        .getChildNodes().item(0)
                        .getNodeValue());
                String firstName = element.getElementsByTagName("firstName").item(0)
                        .getChildNodes().item(0)
                        .getNodeValue();
                String lastName = element.getElementsByTagName("lastName").item(0)
                        .getChildNodes().item(0)
                        .getNodeValue();
                String country = element.getElementsByTagName("country").item(0)
                        .getChildNodes().item(0)
                        .getNodeValue();
                int age = Integer.parseInt(element.getElementsByTagName("age").item(0)
                        .getChildNodes().item(0)
                        .getNodeValue());

                listOfEmployee.add(new Employee(id, firstName, lastName, country, age));
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return listOfEmployee;
    }


    private static void writeString(String json, String fileName) {
        try(FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String listToJson(List<Employee> list) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        return gson.toJson(list, listType);

    }

    //CSV parser
    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> list = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))){

            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csvToBean = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();

            list = csvToBean.parse();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
