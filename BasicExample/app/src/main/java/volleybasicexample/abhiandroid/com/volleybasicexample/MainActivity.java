package volleybasicexample.abhiandroid.com.volleybasicexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
//import org.json.simple.JSONObject;
import org.json.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.JSONObject;

import android.speech.tts.TextToSpeech;
import java.util.Locale;


import org.json.JSONException;

import android.widget.TextView;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private Button btnRequest;

    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    private String url = "https://search.api.tesco.com/browse/?tpnb=59726704&fields=name,price";
    private TextView mTxtDisplay;


    private TextToSpeech textToSpeech;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRequest = (Button) findViewById(R.id.buttonRequest);

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int ttsLang = textToSpeech.setLanguage(Locale.US);

                    if (ttsLang == TextToSpeech.LANG_MISSING_DATA
                            || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "The Language is not supported!");
                    } else {
                        Log.i("TTS", "Language Supported.");
                    }
                    Log.i("TTS", "Initialization success.");
                } else {
                    Toast.makeText(getApplicationContext(), "TTS Initialization failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnRequest.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v){

                                              sendAndRequestResponse();

                                          }
                                      }

        );

    }

    private void sendAndRequestResponse() {

        //RequestQueue initialized
        mRequestQueue = Volley.newRequestQueue(this);

        //String Request initialized
        mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

              //  HashMap<String, String> stringParams = new HashMap<>();
               // stringParams.put("name", "Vishal");

                JSONParser parser = new JSONParser();
                try {
                    //JSONObject json = (JSONObject) parser.parse(response);
                   // String finalResponse = String.valueOf(json.get("results"));
                    JSONObject uk = new JSONObject(response);
                    JSONObject ghs = uk.getJSONObject("uk");
                   JSONObject products = (JSONObject) ghs.get("ghs");

                    // get employee name and salary
                    JSONObject insideProducts = products.getJSONObject("products");
                    JSONArray results = insideProducts.getJSONArray("results");
                    JSONObject namObj = results.getJSONObject(0);
                    String name = namObj.optString("name");

                    /*JSONObject priceObj = results.getJSONObject(1);*/
                    Double price= namObj.optDouble("price");

                    String data = "You have added " + name + " to the shopping basket" + "and the price of the item is :" + price;
                    int length = results.length();
                    Toast.makeText(getApplicationContext(),"Response :"  + results.toString()+ name+price, Toast.LENGTH_LONG).show();

                    //display the response on screen
                    int speechStatus = textToSpeech.speak(data, TextToSpeech.QUEUE_FLUSH, null);

                    if (speechStatus == TextToSpeech.ERROR) {
                        Log.e("TTS", "Error in converting Text to Speech!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i(TAG,"Error :" + error.toString());
            }
        });

        mRequestQueue.add(mStringRequest);
    }
    }

