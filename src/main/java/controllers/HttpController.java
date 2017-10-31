package controllers;

import com.google.inject.Inject;
import helpers.DatabaseHelper;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class HttpController {

    private static final String base_url = "/items/";

    DatabaseHelper databaseHelper = new DatabaseHelper();

    @RequestMapping(value = base_url + "searchitems", method = GET)
    public String searchItems(@RequestParam String query, @RequestParam(value="type", defaultValue="") String type) {
        return databaseHelper.searchData(type, query);
    }

    @RequestMapping(value = base_url + "searchitems", method = POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public String postDataToElasticSearch(@RequestBody String resource) {
        JSONObject jsonObject = new JSONObject(resource);
        return databaseHelper.setData(jsonObject);
    }

}