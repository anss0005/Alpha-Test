package com.example.testalphabetic;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class test extends AppCompatActivity {


    private TextView letterTextView, answerTextView, resultTextView;
    private char[] skyLetters = {'b', 'd', 'f', 'h', 'k', 'l', 't'};
    private char[] rootLetters = {'g', 'j', 'p', 'q', 'y'};
    private char[] grassLetters = {'a', 'c', 'e', 'i', 'm', 'n', 'o', 'r', 's', 'u', 'v', 'w', 'x', 'z'};
    private String answerString = "";
    private int questionCount = 0;
    private int correctAnswers = 0;

    private SQLiteDatabase database;
    private static final String DATABASE_NAME = "QuizResults.db";
    private static final String TABLE_NAME = "results";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_QUESTION = "question";
    private static final String COLUMN_CORRECT_ANSWER = "correct_answer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        letterTextView = findViewById(R.id.textView);
        answerTextView = findViewById(R.id.textView2);
        resultTextView = findViewById(R.id.resultTextView);

        Button skyButton = findViewById(R.id.button);
        skyButton.setOnClickListener(v -> checkAnswer("Sky Letter"));

        Button grassButton = findViewById(R.id.button2);
        grassButton.setOnClickListener(v -> checkAnswer("Grass Letter"));

        Button rootButton = findViewById(R.id.button3);
        rootButton.setOnClickListener(v -> checkAnswer("Root Letter"));

        createNewQuestion();

        SQLiteOpenHelper dbHelper = new SQLiteOpenHelper(this, DATABASE_NAME, null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_QUESTION + " TEXT, " +
                        COLUMN_CORRECT_ANSWER + " INTEGER)");
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
                onCreate(db);
            }
        };

        database = dbHelper.getWritableDatabase();

    }

    private void checkAnswer(String selectedAnswer) {
        if (selectedAnswer.equals(answerString)) {
            answerTextView.setText("Awesome, your answer is correct!");
            correctAnswers++;
            saveResultToDatabase(1);
        } else {
            answerTextView.setText("Incorrect! The answer is " + answerString);
            saveResultToDatabase(0);
        }

        questionCount++;
        if (questionCount < 5) {
            new Handler().postDelayed(this::createNewQuestion, 1000);
        } else {
            answerTextView.setText("Quiz completed!");
            disableButtons();
            showQuizResult();
            if (correctAnswers == 5) {
                showCongratulationsFragment();
            }
        }
    }

    private void createNewQuestion() {
        letterTextView.setText(String.valueOf(getRandomLetter()));
        answerTextView.setText("");
    }

    private char getRandomLetter() {
        Random random = new Random();
        int category = random.nextInt(3);
        char letter;
        switch (category) {
            case 0:
                letter = skyLetters[random.nextInt(skyLetters.length)];
                answerString = "Sky Letter";
                break;
            case 1:
                letter = grassLetters[random.nextInt(grassLetters.length)];
                answerString = "Grass Letter";
                break;
            default:
                letter = rootLetters[random.nextInt(rootLetters.length)];
                answerString = "Root Letter";
                break;
        }
        return letter;
    }

    private void disableButtons() {
        Button skyButton = findViewById(R.id.button);
        skyButton.setEnabled(false);
        Button grassButton = findViewById(R.id.button2);
        grassButton.setEnabled(false);
        Button rootButton = findViewById(R.id.button3);
        rootButton.setEnabled(false);
    }

    private void saveResultToDatabase(int isCorrect) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_QUESTION, "Question " + (questionCount + 1));
        values.put(COLUMN_CORRECT_ANSWER, isCorrect);

        long rowId = database.insert(TABLE_NAME, null, values);

        if (rowId == -1) {
            Toast.makeText(this, "Failed to save result to database", Toast.LENGTH_SHORT).show();
        }
    }

    private void showQuizResult() {
        Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, COLUMN_ID + " DESC", "5");

        int totalQuestions = 0;
        int correctAnswers = 0;

        if (cursor.moveToLast()) {
            do {
                @SuppressLint("Range") int correctAnswer = cursor.getInt(cursor.getColumnIndex(COLUMN_CORRECT_ANSWER));
                if (correctAnswer == 1) {
                    correctAnswers++;
                }
                totalQuestions++;
            } while (cursor.moveToPrevious() && totalQuestions < 5);
        }

        cursor.close();

        String resultText = "Quiz Score: " + correctAnswers + " out of " + totalQuestions;

        resultTextView.setText(resultText);
    }

    private void showCongratulationsFragment() {
        fragment fragment = new fragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.close();
    }



}