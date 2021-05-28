package com.example.mave.Diary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mave.CreateRetrofit;
import com.example.mave.Dto.AnswerDto.AllAnswerRequest;
import com.example.mave.Dto.AnswerDto.AllAnswerResponse;
import com.example.mave.Dto.AnswerDto.RegistAnswerRequest;
import com.example.mave.Dto.AnswerDto.RegistAnswerResponse;
import com.example.mave.PreferenceManager;
import com.example.mave.R;
import com.example.mave.repository.AnswerRepository;
import com.example.mave.repository.GroupRepository;
import com.example.mave.repository.QuestionRepository;
import com.example.mave.service.AnswerRetrofitService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.mave.Diary.Create_Diary.TAG;
import static com.example.mave.repository.GroupRepository.*;


public class Page2_sub_answer extends AppCompatActivity {

    private ListView listView;
    private Button btn_add, btn_custom;
    private EditText edt_title;
    private ListViewAdapter adapter;
    static int count = 0;
    private ImageView flower;
    private Context mContext;
    private TextView answerForQuestion;
    SharedPreferences pref;
    SharedPreferences.Editor editor;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_page2_sub_answer);

        edt_title = (EditText) findViewById(R.id.edt_answer);
        btn_add = (Button) findViewById(R.id.btn_answer);
        listView = (ListView) findViewById(R.id.listMemo);
        flower = (ImageView) findViewById(R.id.diary_flower);
        answerForQuestion = (TextView) findViewById(R.id.answer_for_question);
        //btn_custom = (Button) findViewById(R.id.customquestion);
        mContext = this;
        Intent intent = getIntent();
        String todayQuestion = intent.getStringExtra("todayQuestion");

        adapter = new ListViewAdapter(Page2_sub_answer.this);
        listView.setAdapter(adapter);

        Log.d(TAG, "질문 가져오자!!");
        answerForQuestion.setText(todayQuestion);

        Log.d(TAG,"답변 가져오자!!");
        takeAllAnswer(adapter);

        // 데이터 추가하기
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.addItem(edt_title.getText().toString());
                registAnswer(edt_title);
                edt_title.setText("");
                count++;
                /*SharedPreferences sharedPreferences = getSharedPreferences("ansewercount",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("addcount", count);
                editor.commit();*/
                PreferenceManager.setInt(mContext, "test", count);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void registAnswer(EditText edt_title) {

        AnswerRetrofitService answerRetrofitService = CreateRetrofit.createRetrofit().create(AnswerRetrofitService.class);
        RegistAnswerRequest request = new RegistAnswerRequest("hello1", getInstance().getGroupId(), edt_title.getText().toString());
        Call<RegistAnswerResponse> call = answerRetrofitService.registAnswer(1l, request);

        call.enqueue(new Callback<RegistAnswerResponse>() {
            @Override
            public void onResponse(Call<RegistAnswerResponse> call, Response<RegistAnswerResponse> response) {
                if (response.isSuccessful()) {
                    RegistAnswerResponse body = response.body();
                    Log.d(TAG, "response 성공!!");
//                            textTest.setText(body.getUserId().toString());
                } else {
                    Log.d(TAG, "response 실패 ㅠㅠ");

                }
            }

            @Override
            public void onFailure(Call<RegistAnswerResponse> call, Throwable t) {
                Log.d(TAG, "onFailure => " + t.getMessage());
            }
        });


    }

    public void takeAllAnswer(ListViewAdapter adapter){
        AnswerRetrofitService answerRetrofitService = CreateRetrofit.createRetrofit().create(AnswerRetrofitService.class);
        AllAnswerRequest request = new AllAnswerRequest(getInstance().getGroupId());
        Call<List<AllAnswerResponse>> call = answerRetrofitService.allAnswer(1l,request);

        call.enqueue(new Callback<List<AllAnswerResponse>>() {
            @Override
            public void onResponse(Call<List<AllAnswerResponse>> call, Response<List<AllAnswerResponse>> response) {
                if (response.isSuccessful()) {
                    List<AllAnswerResponse> body = response.body();
                    Log.d(TAG, "response 성공!!");
                    for (AllAnswerResponse allAnswerResponse : body) {
                        String answerContent = allAnswerResponse.getAnswerContent();
                        adapter.addItem(answerContent);

                    }
                    adapter.notifyDataSetChanged();

                } else {
                    Log.d(TAG, "response 실패 ㅠㅠ");

                }
            }

            @Override
            public void onFailure(Call<List<AllAnswerResponse>> call, Throwable t) {
                Log.d(TAG, "onFailure => " + t.getMessage());
            }
        });


    }

}

