package it.polimi.guardian.authorityapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Nemanja on 12/04/2015.
 */
public class HighscoreAdapter  extends BaseAdapter implements Serializable {
    private static final long serialVersionUID = 1L;

    protected ListView lvAllHighscores;
    private Context ctx;
    Activity activity;
    private List<Highscore> highscoresList = new ArrayList<>();
    private static LayoutInflater inflater = null;

    protected static class RowViewHolder {
        public TextView tvAuthorityUsername;
        public TextView tvScore;
    }
    public void refresh() {
        this.notifyDataSetChanged();
    }
    public HighscoreAdapter(Context context,Activity act, ListView lvHighscores, List<Highscore> jObjList){
        ctx = context;
        activity = act;
        lvAllHighscores = lvHighscores;
        highscoresList = new ArrayList<>();
        for(Highscore h: jObjList){
            highscoresList.add(h);
        }
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if(highscoresList == null)
            return 0;
        return highscoresList.size();
    }

    @Override
    public Object getItem(int position) {
        return highscoresList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)
            view = inflater.inflate(R.layout.list_of_highscores_row, null);
        //find items in row-view

        RowViewHolder holder = new RowViewHolder();
        holder.tvAuthorityUsername = (TextView) view.findViewById(R.id.tv_row_username);
        holder.tvScore = (TextView) view.findViewById(R.id.tv_row_score);
        //set their values
        holder.tvAuthorityUsername.setText(highscoresList.get(position).getAuthorityUsername());
        holder.tvScore.setText(Integer.toString(highscoresList.get(position).getScore()));
        //tag holder object to specific row-view object
        view.setTag(holder);

        return view;
    }
    public void updateHighscores(List<Highscore> jObjList) {
        highscoresList.clear();
        for(Highscore h: jObjList){
            highscoresList.add(h);
        }
        Collections.sort(highscoresList);
        this.refresh();
    }

}
