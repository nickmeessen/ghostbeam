package nl.nickmeessen.ghostbeam;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.widget.RemoteViews;
import android.widget.Toast;


public class GhostBeamProvider extends AppWidgetProvider {

    private static boolean isBeaming = false;
    private static Camera ghostCamera;

    @Override

    public void onReceive(Context context, Intent intent) {

        super.onReceive(context, intent);

        if (intent.getAction().equals("nl.nickmeessen.ghostbeam.BOOM")) {

            SharedPreferences settings = context.getSharedPreferences("washi", 0);
            SharedPreferences.Editor editor = settings.edit();

            editor.putBoolean("firstClicked", true);

            editor.commit();

            Toast.makeText(context, "Making widget invisible..", Toast.LENGTH_SHORT).show();

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

            Intent cIntent = new Intent("nl.nickmeessen.ghostbeam.BEAM");

            PendingIntent pendingIntentClick = PendingIntent.getBroadcast(context, 0, cIntent, 0);

            views.setOnClickPendingIntent(R.id.widget_button, pendingIntentClick);

            ComponentName widget = new ComponentName(context, GhostBeamProvider.class);

            appWidgetManager.updateAppWidget(widget, views);

        }

        if (intent.getAction().equals("nl.nickmeessen.ghostbeam.BEAM")) {

            if (isBeaming) {

                if (ghostCamera != null) {
                    ghostCamera.stopPreview();
                    ghostCamera.release();
                    ghostCamera = null;
                }

                isBeaming = false;

            } else {

                ghostCamera = Camera.open();

                if (ghostCamera == null) {

                    Toast.makeText(context, "No Camera Found!", Toast.LENGTH_SHORT).show();

                } else {

                    Camera.Parameters pms = ghostCamera.getParameters();

                    pms.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);

                    try {
                        ghostCamera.setParameters(pms);
                        ghostCamera.startPreview();
                        isBeaming = true;

                    } catch (Exception e) {
                        Toast.makeText(context, "Camera flash not supported!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        }

    }




    @Override

    public void onUpdate(Context context, AppWidgetManager mgr, int[] appWidgetIds) {

        final int N = appWidgetIds.length;

        for (int i = 0; i < N; i++) {

            int[] appWidgetId = appWidgetIds;

            SharedPreferences settings = context.getSharedPreferences("washi", 0);

            boolean firstClicked = settings.getBoolean("firstClicked", false);

            if (firstClicked) {

                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

                Intent cIntent = new Intent("nl.nickmeessen.ghostbeam.BEAM");

                PendingIntent pendingIntentClick = PendingIntent.getBroadcast(context, 0, cIntent, 0);

                views.setOnClickPendingIntent(R.id.widget_button, pendingIntentClick);

                mgr.updateAppWidget(appWidgetId, views);

            } else {

                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_first);

                Intent cIntent = new Intent("nl.nickmeessen.ghostbeam.BOOM");

                PendingIntent pendingIntentClick = PendingIntent.getBroadcast(context, 0, cIntent, 0);

                views.setOnClickPendingIntent(R.id.widget_button, pendingIntentClick);

                mgr.updateAppWidget(appWidgetId, views);

            }

        }

    }

}