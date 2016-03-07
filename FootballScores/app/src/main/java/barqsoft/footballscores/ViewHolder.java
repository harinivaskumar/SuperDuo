package barqsoft.footballscores;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by yehya khaled on 2/26/2015.
 */
public class ViewHolder {
    public ImageView homeCrest;
    public TextView homeName;
    public ImageView awayCrest;
    public TextView awayName;
    public TextView score;
    public TextView date;

    private double matchId;

    public ViewHolder(View view) {
        homeCrest = (ImageView) view.findViewById(R.id.home_crest);
        homeName = (TextView) view.findViewById(R.id.home_name);
        awayCrest = (ImageView) view.findViewById(R.id.away_crest);
        awayName = (TextView) view.findViewById(R.id.away_name);
        score = (TextView) view.findViewById(R.id.score_textview);
        date = (TextView) view.findViewById(R.id.data_textview);
    }

    public void setMatchId(double matchId){
        this.matchId = matchId;
    }

    public double getMatchId(){
        return matchId;
    }
}