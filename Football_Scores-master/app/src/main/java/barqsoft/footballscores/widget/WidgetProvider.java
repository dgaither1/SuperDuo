package barqsoft.footballscores.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;

/**
 * Created by David Gaither on 2/10/16.
 */
public class WidgetProvider extends AppWidgetProvider {

    public static final int COL_HOME = 3;
    public static final int COL_AWAY = 4;
    public static final int COL_HOME_GOALS = 6;
    public static final int COL_AWAY_GOALS = 7;
    public static final int COL_DATE = 1;
    public static final int COL_LEAGUE = 5;
    public static final int COL_MATCHDAY = 9;
    public static final int COL_ID = 8;
    public static final int COL_MATCHTIME = 2;
    private static final String ACTION_CLICK = "ACTION_CLICK";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // Get all ids
        ComponentName thisWidget = new ComponentName(context,
                WidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int widgetId : allWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget_layout);

            Intent intent = new Intent(context, MainActivity.class);


            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            remoteViews.setOnClickPendingIntent(R.id.layout, pendingIntent);

            List<String> games = new ArrayList<>();


            for(int dayIndex = 0; dayIndex < 5; dayIndex++) {

                Date date = new Date(System.currentTimeMillis() + ((dayIndex - 2) * 86400000));
                SimpleDateFormat mformat = new SimpleDateFormat(context.getString(R.string.date_format));
                String[] dateString = new String[1];
                dateString[0] = mformat.format(date);

                CursorLoader loader = new CursorLoader(context, DatabaseContract.scores_table.buildScoreWithDate(),
                        null, null, dateString, null);

                Cursor results = loader.loadInBackground();


                if (results != null && results.getCount() > 0) {
                    results.moveToFirst();
                    for (int i = 0; i < results.getCount(); i++) {

                        if(Integer.valueOf(results.getString(COL_AWAY_GOALS)) == -1 && Integer.valueOf(results.getString(COL_HOME_GOALS)) == -1) {
                            games.add(results.getString(COL_HOME) + " - " + results.getString(COL_AWAY) + "\n" + results.getString(COL_DATE) + " - " + results.getString(COL_MATCHTIME));
                        } else {
                            games.add(results.getString(COL_HOME) + " - " + results.getString(COL_AWAY) + "\n" + results.getString(COL_HOME_GOALS) + " - " + results.getString(COL_AWAY_GOALS));
                        }

                        results.moveToNext();
                    }

                    results.close();
                }

            }

            remoteViews.setTextViewText(R.id.game_1, games.size() > 0 ? games.get(0) : "");
            remoteViews.setTextViewText(R.id.game_2, games.size() > 1 ? games.get(1) : "");
            remoteViews.setTextViewText(R.id.game_3, games.size() > 2 ? games.get(2) : "");
            remoteViews.setTextViewText(R.id.game_4, games.size() > 3 ? games.get(3) : "");
            remoteViews.setTextViewText(R.id.game_5, games.size() > 4 ? games.get(4) : "");
            remoteViews.setTextViewText(R.id.game_6, games.size() > 5 ? games.get(5) : "");

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

}
