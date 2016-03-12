package barqsoft.footballscores;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by yehya khaled on 2/26/2015.
 */
public class ViewHolder {

    public TextView homeName;
    public ImageView homeCrest;
    public TextView score;
    public TextView time;
    public TextView awayName;
    public ImageView awayCrest;

    private double matchId;

    public ViewHolder(View view) {
        homeCrest = (ImageView) view.findViewById(R.id.home_crest);
        homeName = (TextView) view.findViewById(R.id.home_name);

        score = (TextView) view.findViewById(R.id.score_textview);
        time = (TextView) view.findViewById(R.id.time_textview);

        awayCrest = (ImageView) view.findViewById(R.id.away_crest);
        awayName = (TextView) view.findViewById(R.id.away_name);
    }

    public void setMatchId(double matchId){
        this.matchId = matchId;
    }

    public double getMatchId(){
        return matchId;
    }
}