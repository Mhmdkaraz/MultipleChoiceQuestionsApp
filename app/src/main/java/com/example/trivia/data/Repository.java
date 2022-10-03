package com.example.trivia.data;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.trivia.controller.AppController;
import com.example.trivia.model.Question;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class Repository {
    ArrayList<Question> questionsArrayList = new ArrayList<>();
    String url = "https://raw.githubusercontent.com/curiousily/simple-quiz/master/script/statements-data.json";

    public List<Question> getQuestions(final AnswerListAsyncResponse callBack) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            for (int i = 0; i < response.length(); i++) {
                try {
                    Question question = new Question(response.getJSONArray(i).get(0).toString(), response.getJSONArray(i).getBoolean(1));
//                    Log.d("Repo", "onCreate: " + response.getJSONArray(i).get(0));
//                    Log.d("Repo", "onCreate: " + response.getJSONArray(i).getBoolean(1));
                    questionsArrayList.add(question);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (callBack != null) callBack.processFinished(questionsArrayList);
            Log.d("Hii", "getQuestions: " + questionsArrayList.get(1));
        }, error -> {

        });
        AppController.getInstance().addToRequestQueue(jsonArrayRequest);
        return questionsArrayList;
    }
}
