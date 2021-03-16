 package com.example.livewallpaperusingretrofit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

 public class MainActivity extends AppCompatActivity {

     //initialize variables

    NestedScrollView nestedScrollView;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    ArrayList<MainData> dataArrayList = new ArrayList<>();
    MainAdapter adapter;
    int page =2, limit= 9;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //assign variables
        nestedScrollView = findViewById(R.id.scrollView);
        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar);

        //initialize adapter
        adapter = new MainAdapter(dataArrayList, MainActivity.this);
        //initialize layoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //set adapter
        recyclerView.setAdapter(adapter);
        
        //crate get data method
        
        getData(page,limit);

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                //check condition
                if (scrollY == v.getChildAt(0).getMeasuredHeight() -v.getMeasuredHeight()){
                    //when reach page last item position increase page size
                    page++;
                    progressBar.setVisibility(View.VISIBLE);
                    getData(page,limit);
                }
            }
        });
    }

     private void getData(int page, int limit) {
        //retrofit
         Retrofit retrofit = new Retrofit.Builder()
                 .baseUrl("https://picsum.photos/")
                 .addConverterFactory(ScalarsConverterFactory.create())
                 .build();

         //api
         Api api = retrofit.create(Api.class);

         //initialize call

         Call<String> call =  api.STRING_CALL(page,limit);
         call.enqueue(new Callback<String>() {
             @Override
             public void onResponse(Call<String> call, Response<String> response) {

                 //check condition
                 if (response.isSuccessful() && response.body() != null){
                     //when response is success and it's not empty
                     //hide progress bar
                     progressBar.setVisibility(View.GONE);
                     try {
                         //initialize json array
                         JSONArray jsonArray = new JSONArray(response.body());

                         //parse result
                         parseResult(jsonArray);
                     } catch (JSONException e) {
                         e.printStackTrace();
                     }
                 }
             }

             @Override
             public void onFailure(Call<String> call, Throwable t) {

             }
         });

     }

     private void parseResult(JSONArray jsonArray) {
        //loop it
         for (int i=0; i<jsonArray.length(); i++){

             try {
                 //initialize json object
                 JSONObject object = jsonArray.getJSONObject(i);

                 //main data
                  MainData data = new MainData();
                 //set image
                 data.setImage(object.getString("download_url"));
                 //set name
                 data.setName(object.getString("author"));
                 //add data
                 dataArrayList.add(data);
             } catch (JSONException e) {
                 e.printStackTrace();
             }

             //adapter
             adapter = new MainAdapter(dataArrayList, MainActivity.this);

             recyclerView.setAdapter(adapter);

         }
     }
 }