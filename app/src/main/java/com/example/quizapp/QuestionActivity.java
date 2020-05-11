package com.example.quizapp;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static android.graphics.Color.RED;
import static com.example.quizapp.SetsActivity.category_id;

public class QuestionActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView question,qcount,timer;
    private Button Option1,Option2,Option3,Option4;
    private List<Question> questionList;
    private int quesNum;
    CountDownTimer countDownTimer;
    private int score;
    private FirebaseFirestore firestore;
    private int setNo;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        question=findViewById(R.id.question);
        qcount=findViewById(R.id.number);
        timer=findViewById(R.id.count);

        Option1=findViewById(R.id.button);
        Option2=findViewById(R.id.button1);
        Option3=findViewById(R.id.button2);
        Option4=findViewById(R.id.button3);

        Option1.setOnClickListener(this);
        Option2.setOnClickListener(this);
        Option3.setOnClickListener(this);
        Option4.setOnClickListener(this);

        setNo=getIntent().getIntExtra("SETNO",1);
        loadingDialog=new Dialog(QuestionActivity.this);
        loadingDialog.setContentView(R.layout.loading_progressbar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_bg);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();

        firestore= FirebaseFirestore.getInstance();

        getQuestionList();
    }

    private void getQuestionList() {

        questionList=new ArrayList<>();

        firestore.collection("QUIZ").document("CAT"+String.valueOf(category_id)).collection("SET"+String.valueOf(setNo)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    QuerySnapshot questions = task.getResult();

                    for (QueryDocumentSnapshot doc : questions) {
                        questionList.add(new Question(doc.getString("QUESTION"),doc.getString("A"),doc.getString("B"),doc.getString("C"),doc.getString("D"),(int) ((long)doc.get("ANSWER"))));
                    }
                    setQuestion();

                }

                else{
                    Toast.makeText(QuestionActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
                loadingDialog.cancel();
            }
        });


    }

    private void setQuestion() {

        timer.setText(String.valueOf(10));
        question.setText(questionList.get(0).getQuestion());
        Option1.setText(questionList.get(0).getOptionA());
        Option2.setText(questionList.get(0).getOptionB());
        Option3.setText(questionList.get(0).getOptionC());
        Option4.setText(questionList.get(0).getOptionD());

        qcount.setText(String.valueOf(1)+"/"+String.valueOf(questionList.size()));

        startTimer();
        quesNum=0;
    }

    private void startTimer() {

        countDownTimer= new CountDownTimer(12000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(millisUntilFinished<10000)
                timer.setText(String.valueOf(millisUntilFinished/1000));
            }

            @Override
            public void onFinish() {
                changeQuestion();

            }
        };
        countDownTimer.start();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {

        int selectedOption=0;

        switch (v.getId()){
            case R.id.button:
                selectedOption=1;
                break;
            case R.id.button1:
                selectedOption=2;
                break;
            case R.id.button2:
                selectedOption=3;
                break;
            case R.id.button3:
                selectedOption=4;
                break;
            default:

        }
        countDownTimer.cancel();
        checkAnswer(selectedOption, v);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void checkAnswer(int selectedOption, View view) {
        if(selectedOption==questionList.get(quesNum).getCorrectAns()){
            ((Button)view).setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
            score++;
        }
        else
        {

            ((Button)view).setBackgroundTintList(ColorStateList.valueOf(RED));
            switch (questionList.get(quesNum).getCorrectAns()){
                case 1:
                    Option1.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
                case 2:
                    Option2.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
                case 3:
                    Option3.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
                case 4:
                    Option4.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
            }
        }

        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                changeQuestion();
            }
        },2000);
    }

    private void changeQuestion() {
        if(quesNum<questionList.size()-1){

            quesNum++;

            playAnim(question,0,0);
            playAnim(Option1,0,1);
            playAnim(Option2,0,2);
            playAnim(Option3,0,3);
            playAnim(Option4,0,4);

            qcount.setText(String.valueOf(quesNum+1)+"/"+String.valueOf(questionList.size()));

            timer.setText(String.valueOf(10));
            startTimer();
        }
        else{

            Intent intent=new Intent(QuestionActivity.this,ScoreActivity.class);
            intent.putExtra("SCORE",String.valueOf(score)+"/"+String.valueOf(questionList.size()));
            startActivity(intent);
            QuestionActivity.this.finish();
        }
    }

    private void playAnim(final View view, final int value, final int viewNum) {

        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500).setStartDelay(100).setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onAnimationEnd(Animator animation) {
                if(value==0)
                {
                    switch (viewNum){
                        case 0:
                            ((TextView)view).setText(questionList.get(quesNum).getQuestion());
                            break;
                        case 1:
                            ((Button)view).setText(questionList.get(quesNum).getOptionA());
                            break;
                        case 2:
                            ((Button)view).setText(questionList.get(quesNum).getOptionB());
                            break;
                        case 3:
                            ((Button)view).setText(questionList.get(quesNum).getOptionC());
                            break;
                        case 4:
                            ((Button)view).setText(questionList.get(quesNum).getOptionD());
                            break;
                    }

                    if(viewNum!=0)
                        ((Button)view).setBackgroundTintList((ColorStateList.valueOf(Color.parseColor("#F8BA51"))));
                    playAnim(view,1,viewNum);
                }

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

}
