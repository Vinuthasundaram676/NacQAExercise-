package com;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.http.Method;
import io.restassured.response.Response;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static io.restassured.RestAssured.given;

public class ApiUtils {

    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static Response response = null;
    JsonNode current = null;

    // ##########################################################################################################
    // ######################################## CALLING API #####################################################
    // ##########################################################################################################

    /**
     * This section will call the endpoint based on method, header and body
     *
     * @param endpoints
     * @param method
     * @param headerlist
     * @param requestBody
     * @return
     */
    public Object restcall(String endpoints, Method method, List<Header> headerlist, @Nullable Object requestBody) {

        Headers headers = new Headers(headerlist);

        response = null;
        switch (method) {
            case GET:
                RestAssured.useRelaxedHTTPSValidation();
               /* response = given()
                        .urlEncodingEnabled(false)
                        .headers(headers)
                        .get(endpoints);*/
                break;
            case POST:
                response = given()
                        .contentType(ContentType.JSON)
                        .headers(headers)
                        .body(requestBody).post(endpoints);
                break;
            case PUT:
                response = given()
                        .contentType(ContentType.JSON)
                        .headers(headers)
                        .body(requestBody).when().put(endpoints);
                break;
            case PATCH:
                response = given()
                        .contentType(ContentType.JSON)
                        .headers(headers)
                        .patch(endpoints);
                break;

            default:
                System.out.println(this.getClass() + " Invalid Method Requested" + method);
                return null;
        }
    //        printToConsole(endpoints, headers, method, requestBody);


        //Mocked data
        FileInputStream fis = null;
        StringBuilder sb = new StringBuilder();

        try {
            fis = new FileInputStream("src\\main\\resources\\Dummy.json");
            byte[] buffer = new byte[10];
            while (fis.read(buffer) != -1) {
                sb.append(new String(buffer));
                buffer = new byte[10];
            }
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
     //   System.out.println(sb.toString());
        return sb.toString();
    }

    /**
     * This section will write logs on console if debug mode is on
     *
     * @param endpoints
     * @param headers
     * @param method
     * @param requestBody
     */
    public void printToConsole(String endpoints, Headers headers, Method method, Object requestBody) {

        System.out.println(ANSI_GREEN + "Calling : " + ANSI_RESET + endpoints);
        System.out.println(ANSI_GREEN + "Method : " + ANSI_RESET + method);
        System.out.println(ANSI_GREEN + "Header : " + ANSI_RESET);
        System.out.println(headers.toString());
        System.out.print(ANSI_GREEN + "Request body : " + ANSI_RESET);

        if (method.equals(Method.POST) || method.equals(Method.PUT)) {
            String str[] = requestBody.toString().split(",");
            List<String> al = new ArrayList<String>();
            al = Arrays.asList(str);
            System.out.println();
            for (String s : al) {
                System.out.println(s);
            }
        } else {
            System.out.println("No Body");
        }
        System.out.println(ANSI_GREEN + "Response Code: " + ANSI_RESET + response.statusCode());
        System.out.println(ANSI_GREEN + "Response Body: " + ANSI_RESET);
        response.prettyPrint();
    }


    // ##########################################################################################################
    // ################################## Response Schema Validator #############################################
    // ##########################################################################################################

    /**
     * This section will validate the schema
     *
     * @param response
     * @param jsonSchema
     */
    public void schemaValidator(Response response, String jsonSchema) {


     //   File jsonFile = new File(schemaPathGenerator(jsonSchema));
       // response.then().assertThat().body(matchesJsonSchema(jsonFile));
    }

    // ##########################################################################################################
    // ################################## Extracting data from JSON #############################################
    // ##########################################################################################################

    /**
     * This method parses a Response return as JsonNode
     *
     * @param jsonData
     * @return
     */
    public JsonNode convertResponseToJsonNode(Object jsonData) {
        JsonNode jsonNode = null;
        try {
            if (jsonData instanceof String)
                jsonNode = new ObjectMapper().readTree(jsonData.toString());
            else if (jsonData instanceof Response)
                jsonNode = new ObjectMapper().readTree(((Response) jsonData).asString());
            else if (jsonData instanceof JsonNode)
                jsonNode = (JsonNode) jsonData;
            return jsonNode;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonNode;
    }

    /**
     * Based on key it will return only single value
     *
     * @param node
     * @param key
     * @return
     */
    public Object getValueFromJson(Object node, String key) {
        current = convertResponseToJsonNode(node);
        try {
            if (current.findValue(key).isBoolean()) {
                System.out.println(current.findValue(key));
                return current.findValue(key);
            }else {
                System.out.println(current.findValue(key).isTextual() ? current.findValue(key).textValue() : current.findValue(key).intValue());
                return current.findValue(key).isTextual() ? current.findValue(key).textValue() : current.findValue(key).intValue();
            }
        } catch (NullPointerException ex) {
            return null;
        }
    }

    /**
     * Based on key it will return all values in JsonNode
     *
     * @param node
     * @param key
     * @return
     */
    public List<JsonNode> getArrayFromJson(Object node, String key) {
        current = convertResponseToJsonNode(node);
        return current.findValues(key);
    }

    /**
     * finding key value based on root key
     *
     * @param node
     * @param rootkey
     * @param key
     * @return
     */
    public String getArrayFromJson(Object node, String rootkey, String key) {
        current = convertResponseToJsonNode(node);
        return current.findValues(rootkey).get(0).findValue(key).textValue();
    }

    public Object getValueFromJson(Object node, String rootkey, String key) {
        current = convertResponseToJsonNode(node);
        return current.findValues(rootkey).get(0).get(key).textValue();
    }

    public Object getValueFromJsonAsLoop(Response response, String[] key) {
        current = convertResponseToJsonNode(response);
        for (int i = 0; i < key.length - 1; i++) {
            current = current.findValues(key[i]).get(0);
        }
        if (current.findValue(key[key.length - 1]) == null)
            return null;
        else if (current.findValue(key[key.length - 1]).isBoolean())
            return current.findValue(key[key.length - 1]);
        else
            return current.findValue(key[key.length - 1]).asText();
    }

    public Object getValueFromJsonAsLoop(Response response, String keyValue) {
        current = convertResponseToJsonNode(response);
        String[] key = keyValue.split("/");
        for (int i = 0; i < key.length - 1; i++) {
            current = current.findValues(key[i]).get(0);
        }
        if (current.findValue(key[key.length - 1]) == null)
            return null;
        else if (current.findValue(key[key.length - 1]).isBoolean())
            return current.findValue(key[key.length - 1]);
        else
            return current.findValue(key[key.length - 1]).asText();
    }


}