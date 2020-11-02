package com.example.quizactivity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {
    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX_1 = "index";
    private static final String KEY_INDEX_2 = "answer";
    private static final String KEY_INDEX_3 = "true";
    private static final String KEY_INDEX_4 = "false";
    private static final String KEY_INDEX_5 = "cheate";
    private static final String KEY_INDEX_6 = "ischeater";
    private static final int REQUEST_CODE_CHEAT = 0;

    private Button mTrueButton;
    private Button mFalseButton;
    private Button mCheatButton;
    private Button mPreviousButton;
    private Button mNextButton;
    private TextView mQuestionTextView;

    private Question[] mQuestionBank = new Question[] {
            new Question(R.string.question_australia,true),
            new Question(R.string.question_oceans,true),
            new Question(R.string.question_mideast,true),
            new Question(R.string.question_africa,true),
            new Question(R.string.question_americas,true),
            new Question(R.string.question_asia,true)
    };

    private boolean[] mIsAnwser = new boolean[] {
            false,
            false,
            false,
            false,
            false,
            false,
    };

    private int mCurrentIndex = 0;
    private boolean mIsCheater;

    private int mTrueAnswer = 0;
    private int mFalseAnswer = 0;
    private int mCheateAnswer = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX_1);
            mIsAnwser = savedInstanceState.getBooleanArray(KEY_INDEX_2);
            mTrueAnswer = savedInstanceState.getInt(KEY_INDEX_3);
            mFalseAnswer = savedInstanceState.getInt(KEY_INDEX_4);
            mCheateAnswer = savedInstanceState.getInt(KEY_INDEX_5);
            mIsCheater = savedInstanceState.getBoolean(KEY_INDEX_6);
        }

        mQuestionTextView = findViewById(R.id.question_text_view);

        mTrueButton = findViewById(R.id.true_button);
        mFalseButton = findViewById(R.id.false_button);
        mCheatButton = findViewById(R.id.cheat_button);
        mPreviousButton = findViewById(R.id.previous_button);
        mNextButton = findViewById(R.id.next_button);

        updateQuestion();
        setListener();

        // hide actionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX_1, mCurrentIndex);
        savedInstanceState.putBooleanArray(KEY_INDEX_2, mIsAnwser);
        savedInstanceState.putInt(KEY_INDEX_3, mTrueAnswer);
        savedInstanceState.putInt(KEY_INDEX_4, mFalseAnswer);
        savedInstanceState.putInt(KEY_INDEX_5, mCheateAnswer);
        savedInstanceState.putBoolean(KEY_INDEX_6, mIsCheater);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    private void setListener() {
        OnClick onClick = new OnClick();
        mTrueButton.setOnClickListener(onClick);
        mFalseButton.setOnClickListener(onClick);
        mCheatButton.setOnClickListener(onClick);
        mPreviousButton.setOnClickListener(onClick);
        mNextButton.setOnClickListener(onClick);
    }

    private class OnClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.true_button:
                    checkAnswer(true);
                    mIsAnwser[mCurrentIndex] = true;
                    mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                    mIsCheater = false;
                    updateQuestion();
                    break;
                case R.id.false_button:
                    checkAnswer(false);
                    mIsAnwser[mCurrentIndex] = true;
                    mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                    updateQuestion();
                    break;
                case R.id.cheat_button:
                    boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                    Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
                    startActivityForResult(intent, REQUEST_CODE_CHEAT);
                    break;
                case R.id.previous_button:
                    mCurrentIndex = mCurrentIndex - 1;
                    if (mCurrentIndex < 0) {
                        mCurrentIndex = mQuestionBank.length - 1;
                    }
                    updateQuestion();
                    break;
                case R.id.next_button:
                    if (6 == (mCurrentIndex + 1)) {
                        String tmp = "True:" + mTrueAnswer + "\nFalse:" + mFalseAnswer + "\nCheate:" + mCheateAnswer + "\nUnAnswer:" + (mQuestionBank.length - mTrueAnswer - mFalseAnswer - mCheateAnswer);
                        Toast.makeText(QuizActivity.this, tmp, Toast.LENGTH_SHORT).show();
                    }
                    mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                    mIsCheater = false;
                    updateQuestion();
                    break;
            }
        }
    }

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);

        if (mIsAnwser[mCurrentIndex]) {
            mTrueButton.setVisibility(View.INVISIBLE);
            mFalseButton.setVisibility(View.INVISIBLE);
        } else {
            mTrueButton.setVisibility(View.VISIBLE);
            mFalseButton.setVisibility(View.VISIBLE);
        }
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean anserIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

        int messageResId = 0;

        if (mIsCheater) {
            mCheateAnswer = mCheateAnswer + 1;
            messageResId = R.string.judgment_toast;
        } else {
            if(userPressedTrue == anserIsTrue) {
                mTrueAnswer = mTrueAnswer + 1;
                messageResId = R.string.correct_toast;
            } else {
                mFalseAnswer = mFalseAnswer + 1;
                messageResId = R.string.incorrect_toast;
            }
        }

        Toast.makeText(QuizActivity.this, messageResId, Toast.LENGTH_SHORT).show();
    }
}
