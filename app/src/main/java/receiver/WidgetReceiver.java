package receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import service.WidgetService;

/**
 * Created by Sin on 2016/10/7.
 * Description:
 */

public class WidgetReceiver extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.d("TAG", "onUpdate");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d("TAG", "onReceive");
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.d("TAG", "onEnabled");

        Intent intent = new Intent(context, WidgetService.class);
        context.startService(intent);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.d("TAG", "onDisabled");

        Intent intent = new Intent(context, WidgetService.class);
        context.stopService(intent);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Log.d("TAG", "onDeleted");
    }
}
