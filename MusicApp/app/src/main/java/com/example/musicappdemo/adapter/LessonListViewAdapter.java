package com.example.musicappdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.musicappdemo.R;
import com.example.musicappdemo.model.Lesson;

import java.util.List;

public class LessonListViewAdapter extends BaseAdapter {

    private Context context;
    private List<Lesson> lessonList;

    public LessonListViewAdapter(Context context, List<Lesson> lessonList) {
        this.context = context;
        this.lessonList = lessonList;
    }

    @Override
    public int getCount() {
        return lessonList.size();
    }

    @Override
    public Object getItem(int position) {
        return lessonList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_lesson, parent, false);
        }

        Lesson lesson = (Lesson) getItem(position);

        TextView tvTitle = convertView.findViewById(R.id.tvLessonTitle);
        TextView tvInfo = convertView.findViewById(R.id.tvLessonInfo);

        tvTitle.setText(lesson.getTitle());
        tvInfo.setText(lesson.getInfo());

        return convertView;
    }
}