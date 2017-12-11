package helpers;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;


public class DatabaseHelper {

    private TransportClient client;
    private static final String INDEX = "shakespeare";
    private static final String FIELD_DESCRIPTION = "line_number";
    private static final String FIELD_BRAND = "speaker";
    private static final String FIELD_MODEL= "text_entry";

    public DatabaseHelper() {
        try {
            client = new PreBuiltTransportClient(Settings.EMPTY)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void closeClient() {
        client.close();
    }

    public String searchData(String type,String query) {
        String retVal = "Nothing to show";
        try {
            SearchRequestBuilder builder = client.prepareSearch(INDEX);
            if (type != null) {
                if (!type.equals(""))
                    builder.setTypes(type);
            }
            if(query != null){
                builder.setFrom(0).setSize(500);
                QueryStringQueryBuilder queryBuilder = new QueryStringQueryBuilder("*" + query +"*")
                        .field(FIELD_DESCRIPTION)
                        .field(FIELD_MODEL)
                        .field(FIELD_BRAND);
                builder.setQuery(queryBuilder);
            }else {
                QueryBuilder qb = matchAllQuery();
                builder.setQuery(qb);
            }
            SearchResponse response = builder.get();
            SearchHits hits = response.getHits();
            SearchHit[] searchHits = hits.getHits();
            JSONObject responseObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            for(SearchHit h : searchHits){
                JSONObject jsonObject = new JSONObject(h.getSourceAsString());
                jsonArray.put(jsonObject);
            }
            responseObject.put("search_results", jsonArray);
            responseObject.put("hits", jsonArray.length());
            retVal = responseObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retVal;

    }

    public String deleteData(String index, String type, String id) {
        DeleteResponse response = client.prepareDelete(index, type, id).get();
        return response.toString();
    }

    public String getData(String index, String type, String id) {
        GetResponse response = client.prepareGet(index, type, id).get();
        if (response.isExists()) {
            byte[] source = response.getSourceAsBytes();
            String sourceString = new String(source);
            return sourceString;
        }
        return "Not found";

    }

    public String setData(JSONObject jsonContent) {
        String type = jsonContent.getString("type");
        jsonContent.remove("type");
        IndexResponse response = client.prepareIndex(INDEX, type)
                .setSource(jsonContent.toString(), XContentType.JSON).execute().actionGet();
        return response.toString();
    }


}
