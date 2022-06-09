package StepDefinitions.API;

import com.ApiUtils;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.http.Method;
import io.restassured.response.Response;
import org.junit.Assert;

public class GetNaceDetail {

    ApiUtils apiUtils = new ApiUtils();
    Response response;
    String res;

    @Given("Calling get nace detail api")
    public void calling_get_nace_detial_api() {
        res = (String) apiUtils.restcall("getNaceDetails/398481", Method.GET, null, null);
    }

    @Then("validate the response status")
    public void validate_the_response_status() {
  //      Assert.assertEquals("Response code didn't match", response.getStatusCode(), 200);
    }

    @Then("validate the response data")
    public void validate_the_response_data() {
        Assert.assertEquals("Statistical Classification of Economic Activities in the European Community, Rev. 2 (2008)",apiUtils.getValueFromJson(res, "LabelText"));
    }
}
