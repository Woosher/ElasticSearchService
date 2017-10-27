package controllers;

import com.google.inject.Inject;
import helpers.DatabaseHelper;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class HttpController {

    private static final String base_url = "/items/";

    DatabaseHelper databaseHelper = new DatabaseHelper();

    @RequestMapping(value = base_url + "searchitems", method = GET)
    public String searchItems(@RequestParam String query, @RequestParam(value="type", defaultValue="") String type) {
        return databaseHelper.searchData(type, query);
    }

}