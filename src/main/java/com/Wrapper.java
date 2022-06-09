package com;

import io.restassured.response.Response;
import org.apache.commons.codec.binary.Base64;
import org.junit.ComparisonFailure;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

public class Wrapper {





    public String readFileAsString(String fileName) {
        try {
            //    System.out.println(new String(Files.readAllBytes(Paths.get(fileName))));
            return new String(Files.readAllBytes(Paths.get(fileName)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void waitFor(Object sec) {
        try {
            Thread.sleep(Integer.parseInt(sec.toString()) * 1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String schemaPathGenerator(String jsonSchema) {
        return "src" + File.separator + "test" + File.separator + "resources" + File.separator + "schema" + File.separator +
                jsonSchema.replace("/", File.separator).replace("\\", File.separator) + ".json";
    }

    public String pathGenerator(String filePath) {
        return filePath.replace("/", File.separator).replace("\\", File.separator);
    }

    /**
     * This will return age of the user based on his dob
     *
     * @param dob
     * @return
     */
    public int getAge(String dob) throws ParseException {
        int age = 0;
        try {
            Date dateOfBirth = new SimpleDateFormat("dd/MM/yyyy").parse(dob);
            Calendar today = Calendar.getInstance();
            Calendar birthDate = Calendar.getInstance();

            age = 0;

            birthDate.setTime(dateOfBirth);
            if (birthDate.after(today)) {
                throw new IllegalArgumentException("Can't be born in the future");
            }

            age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

            // If birth date is greater than todays date (after 2 days adjustment of leap year) then decrement age one year
            if ((birthDate.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR) > 3) ||
                    (birthDate.get(Calendar.MONTH) > today.get(Calendar.MONTH))) {
                age--;

                // If birth date and todays date are of same month and birth day of month is greater than todays day of month then decrement age
            } else if ((birthDate.get(Calendar.MONTH) == today.get(Calendar.MONTH)) &&
                    (birthDate.get(Calendar.DAY_OF_MONTH) > today.get(Calendar.DAY_OF_MONTH))) {
                age--;
            }
        } catch (ParseException e) {
            System.out.println(e.getStackTrace());
        }

        return age;
    }


    /**
     * Decoding Jwt token and storing the response in pojo
     *
     * @param jwtToken
     * @return
     */
    public String jwtDecode(String jwtToken) {
        //------------ Decode JWT ------------
        String[] split_string = jwtToken.split("\\.");

        String base64EncodedHeader = split_string[0];
        String base64EncodedBody = split_string[1];

        //~~~~~~~~~ JWT Header ~~~~~~~"
        Base64 base64Url = new Base64(true);
        String header = new String(base64Url.decode(base64EncodedHeader));
        //System.out.println("JWT Header : " + header);

        //~~~~~~~~~ JWT Body ~~~~~~~
        String body = new String(base64Url.decode(base64EncodedBody));
        return body;
    }

    /**
     * function is same as Assert.assertEquals
     *
     * @param response
     * @param message
     * @param actualValue
     * @param expectedValue
     */
    public void assertEqual(Response response, String message, Object expectedValue, Object actualValue) {
        if (!String.valueOf(expectedValue).equalsIgnoreCase(String.valueOf(actualValue))) {
            if (response != null) {
                //Logging.logInfo(this.getClass(), "response code is " + response.statusCode());
                response.prettyPrint();
            }
            throw new ComparisonFailure(message, String.valueOf(expectedValue), String.valueOf(actualValue));
        }
    }

    /**
     * function is same as Assert.assertNotEquals
     *
     * @param response
     * @param message
     * @param actualValue
     * @param expectedValue
     */
    public void assertNotEqual(Response response, String message, Object expectedValue, Object actualValue) {
        if (String.valueOf(expectedValue).equals(String.valueOf(actualValue))) {
            if (response != null) {
                //Logging.logInfo(this.getClass(), "response code is " + response.statusCode());
                response.prettyPrint();
            }
            throw new ComparisonFailure(message, String.valueOf(expectedValue), String.valueOf(actualValue));
        }
    }

    /**
     * This section will validate response contains the expected text
     *
     * @param response
     * @param message
     * @param actualValue
     * @param expectedValue
     */
    public void assertContains(Response response, String message, Object expectedValue, Object actualValue) {
        if (!String.valueOf(actualValue).contains(String.valueOf(expectedValue))) {
            if (response != null) {
                //Logging.logInfo(this.getClass(), "response code is " + response.statusCode());
                response.prettyPrint();
            }
            throw new ComparisonFailure(message, String.valueOf(expectedValue), String.valueOf(actualValue));
        }
    }

    /**
     * This section will validate the condition
     *
     * @param response
     * @param message
     * @param condition
     */
    public void assertTrue(Response response, String message, boolean condition) {
        if (!condition) {
            if (response != null) {
                //Logging.logInfo(this.getClass(), "response code is " + response.statusCode());
                response.prettyPrint();
            }
            throw new AssertionError(message);
        }
    }

    /**
     * Function is used to return date and Time in ESt time zone
     * format 2020-06-03 07:55:02
     */
    public String getDateTimeinEst() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        df.setTimeZone(cal.getTimeZone());
        return df.format(cal.getTime());
    }

    /**
     * Function is used to return current or previous date in ESt time zone
     * format 2020-06-03
     */
    public String getDateinEst(Integer day) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
        cal.add(Calendar.DATE, day);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setTimeZone(cal.getTimeZone());

        return df.format(cal.getTime());
    }


    /**
     * Function is used to return the remianing time in second ie 24*60*60(24 hr in sec) - current time in sec
     */
    public int getRemaingTimeTillMidnightInSecond(){
        return (86430 - LocalTime.now(ZoneId.of("America/New_York")).toSecondOfDay());
    }
}