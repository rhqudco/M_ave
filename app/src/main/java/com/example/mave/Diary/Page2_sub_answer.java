package com.example.mave.Diary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mave.CreateRetrofit;
import com.example.mave.Dto.AnswerDto.AllAnswerRequest;
import com.example.mave.Dto.AnswerDto.AllAnswerResponse;
import com.example.mave.Dto.AnswerDto.RegistAnswerRequest;
import com.example.mave.Dto.AnswerDto.RegistAnswerResponse;
import com.example.mave.Dto.customQuestionDto.CreateCustomRequest;
import com.example.mave.Dto.customQuestionDto.CreateCustomResponse;
import com.example.mave.Dto.customQuestionDto.UseCustomRequest;
import com.example.mave.Dto.customQuestionDto.UseCustomResponse;
import com.example.mave.Dto.questionDto.TakeAllQuestionRequest;
import com.example.mave.Dto.questionDto.TakeAllQuestionResponse;
import com.example.mave.Dto.questionDto.TakeQuestionRequest;
import com.example.mave.Dto.questionDto.TakeQuestionResponse;
import com.example.mave.PreferenceManager;
import com.example.mave.R;
import com.example.mave.activities.MainActivity;
import com.example.mave.repository.AnswerRepository;
import com.example.mave.repository.GroupRepository;
import com.example.mave.repository.MemberRepository;
import com.example.mave.repository.QuestionRepository;
import com.example.mave.service.AnswerRetrofitService;
import com.example.mave.service.CustomQuestionRetrofitService;
import com.example.mave.service.QuestionRetrofitService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.royrodriguez.transitionbutton.utils.WindowUtils;

import java.lang.reflect.Member;
import java.security.acl.Group;
import java.util.ArrayList;
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
    private TextView TodayQuestion;
    private ImageButton calendar;
    SharedPreferences pref;
    SharedPreferences.Editor editor;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_page2_sub_answer);
        getSupportActionBar().hide();
//        WindowUtils.makeStatusbarTransparent(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); // EditText ????????? ????????? ????????? UI ?????? ??? ?????? ????????? ???
        edt_title = (EditText) findViewById(R.id.edt_answer);
        btn_add = (Button) findViewById(R.id.btn_answer);
        listView = (ListView) findViewById(R.id.listMemo);
        flower = (ImageView) findViewById(R.id.diary_flower);
        TodayQuestion = (TextView) findViewById(R.id.todayQuestion);
        btn_custom = (Button) findViewById(R.id.customquestion);
        calendar = (ImageButton) findViewById(R.id.imageButton2);
        mContext = this;
        Intent intent = getIntent();
        String todayQuestion = intent.getStringExtra("todayQuestion");


        adapter = new ListViewAdapter(Page2_sub_answer.this);
        listView.setAdapter(adapter);


        Log.d(TAG, "?????? ?????? - ?????? ????????????!!");
        TodayQuestion.setText(todayQuestion);


        Log.d(TAG, "?????? ?????? - ?????? ????????????!!");
        takeAllAnswer(adapter);

        // ????????? ????????????
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.addItem(MemberRepository.getInstance().getUserId(), edt_title.getText().toString());
                registAnswer();
                edt_title.setText("");
                count++;
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void registAnswer() {

        AnswerRetrofitService answerRetrofitService = CreateRetrofit.createRetrofit().create(AnswerRetrofitService.class);
        RegistAnswerRequest request = new RegistAnswerRequest(MemberRepository.getInstance().getUserId(), getInstance().getGroupId(), edt_title.getText().toString());
        Call<RegistAnswerResponse> call = answerRetrofitService.registAnswer(getInstance().getCompleteDate(), request);

        call.enqueue(new Callback<RegistAnswerResponse>() {
            @Override
            public void onResponse(Call<RegistAnswerResponse> call, Response<RegistAnswerResponse> response) {
                if (response.isSuccessful()) {
                    RegistAnswerResponse body = response.body();
                    Log.d(TAG, "response ??????!!");


                    if (body.getFinish()) {
                        completeDate++;
                        checkCustom();
                        Level_Up_Dialog dig_2 = new Level_Up_Dialog(Page2_sub_answer.this, Level_Up_Dialog.class);
                        // ????????? ??????????????? ?????? ??????
                        dig_2.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dig_2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dig_2.show();
                    } else {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Log.d(TAG, "response ?????? ??????");

                }
            }


            @Override
            public void onFailure(Call<RegistAnswerResponse> call, Throwable t) {
                Log.d(TAG, "onFailure => " + t.getMessage());
            }
        });


    }

    public void takeAllAnswer(ListViewAdapter adapter) {
        AnswerRetrofitService answerRetrofitService = CreateRetrofit.createRetrofit().create(AnswerRetrofitService.class);
        AllAnswerRequest request = new AllAnswerRequest(getInstance().getGroupId());
        Call<List<AllAnswerResponse>> call = answerRetrofitService.allAnswer(getInstance().getDiaryDate(), request);

        call.enqueue(new Callback<List<AllAnswerResponse>>() {
            @Override
            public void onResponse(Call<List<AllAnswerResponse>> call, Response<List<AllAnswerResponse>> response) {
                if (response.isSuccessful()) {
                    List<AllAnswerResponse> body = response.body();
                    Log.d(TAG, "response ??????!!");
                    for (AllAnswerResponse allAnswerResponse : body) {
                        String answerContent = allAnswerResponse.getAnswerContent();
                        String userId = allAnswerResponse.getUserId();
                        if (userId.equals(MemberRepository.getInstance().getUserId())) {
                            edt_title.setVisibility(View.GONE);
                            btn_add.setVisibility(View.GONE);
                        }
                        adapter.addItem(userId, answerContent);

                    }
                    adapter.notifyDataSetChanged();

                } else {
                    Log.d(TAG, "response ?????? ??????");

                }
            }

            @Override
            public void onFailure(Call<List<AllAnswerResponse>> call, Throwable t) {
                Log.d(TAG, "onFailure => " + t.getMessage());
            }
        });


    }

    private void questionRequest(Long diaryDate) {
        GroupRepository groupDB = getInstance();
        // ???????????? ?????? ????????????
        QuestionRetrofitService questionRetrofitService = CreateRetrofit.createRetrofit().create(QuestionRetrofitService.class);

        Log.d(TAG, "?????? ???????????????!!");
        Log.d(TAG, "????????? ????????? ????????? ?????? id???!? - " + groupDB.getGroupId());
        Log.d(TAG, "?????? ????????? ???????????????!? - " + diaryDate);

        TakeQuestionRequest request = new TakeQuestionRequest(groupDB.getGroupId());
        Call<TakeQuestionResponse> call = questionRetrofitService.takeQuestion(diaryDate, request);

        call.enqueue(new Callback<TakeQuestionResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<TakeQuestionResponse> call, Response<TakeQuestionResponse> response) {
                if (response.isSuccessful()) {
                    TakeQuestionResponse body = response.body();
                    Log.d(TAG, "response ??????!!");


                } else {
                    Log.d(TAG, "response ?????? ??????");

                }
            }


            @Override
            public void onFailure(Call<TakeQuestionResponse> call, Throwable t) {
                Log.d(TAG, "onFailure => " + t.getMessage());
            }
        });
    }

    private void checkCustom() throws NullPointerException {
        CustomQuestionRetrofitService customQuestionRetrofitService = CreateRetrofit.createRetrofit().create(CustomQuestionRetrofitService.class);
        CreateCustomRequest request = new CreateCustomRequest("customQuestion",
                GroupRepository.getInstance().getDiaryDate(),
                GroupRepository.getInstance().getGroupId());
        Call<CreateCustomResponse> call = customQuestionRetrofitService.checkCustom(request);

        call.enqueue(new Callback<CreateCustomResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<CreateCustomResponse> call, Response<CreateCustomResponse> response) {
                if (response.isSuccessful()) {
                    CreateCustomResponse body = response.body();
                    Log.d(TAG, "response ??????!!");
                    Log.d(TAG, "Custom ?????? ??????!!");

                    if (body.getQuestionContent() != null) {
                        Log.d(TAG, "Custom ?????? ??????!!");
                        Log.d(TAG, "????????? ?????? ????????????");
                        customQuestionRequest();
                    } else {
                        Log.d(TAG, "?????? ?????? ????????????");
                        questionRequest(completeDate);
                    }


                } else {
                    Log.d(TAG, "response ?????? ??????");

                }
            }

            @Override
            public void onFailure(Call<CreateCustomResponse> call, Throwable t) {
                Log.d(TAG, "onFailure => " + t.getMessage());
            }

        });
    }

    private void customQuestionRequest() {
        CustomQuestionRetrofitService customQuestionRetrofitService = CreateRetrofit.createRetrofit().create(CustomQuestionRetrofitService.class);
        UseCustomRequest request = new UseCustomRequest(GroupRepository.getInstance().getGroupId());
        Call<UseCustomResponse> call = customQuestionRetrofitService.useCustomQuestion(diaryDate, request);

        call.enqueue(new Callback<UseCustomResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<UseCustomResponse> call, Response<UseCustomResponse> response) {
                if (response.isSuccessful()) {
                    UseCustomResponse body = response.body();
                    Log.d(TAG, "response ??????!!");
                    Log.d(TAG, "Custom ?????? ??????????????????!!");

                } else {
                    Log.d(TAG, "response ?????? ??????");

                }
            }

            @Override
            public void onFailure(Call<UseCustomResponse> call, Throwable t) {
                Log.d(TAG, "onFailure => " + t.getMessage());
            }

        });
    }


}