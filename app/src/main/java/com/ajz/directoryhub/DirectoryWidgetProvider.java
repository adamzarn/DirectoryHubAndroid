package com.ajz.directoryhub;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.ajz.directoryhub.activities.DirectoryActivity;
import com.ajz.directoryhub.activities.MainActivity;
import com.ajz.directoryhub.objects.CustomAddress;
import com.ajz.directoryhub.objects.Entry;
import com.ajz.directoryhub.objects.Person;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import static com.ajz.directoryhub.DirectoryHubApplication.getContext;


/**
 * Created by adamzarn on 11/9/17.
 */

public class DirectoryWidgetProvider extends AppWidgetProvider {

    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager,
                                final int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.directory_widget_provider);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        final String groupUid = preferences.getString("groupUid", "");
        final String groupName = preferences.getString("groupName", "");
        final Boolean isAdmin = preferences.getBoolean("isAdmin", false);

        if (!StringUtils.isMissing(groupUid)) {

            Intent intent = new Intent(context, DirectoryActivity.class);
            intent.putExtra("groupUid", groupUid);
            intent.putExtra("groupName", groupName);
            intent.putExtra("isAdmin", isAdmin);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widget_view, pendingIntent);

            mDatabase.child("Directories").child(groupUid).addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot ds) {

                    ArrayList<Entry> receivedEntries = new ArrayList<Entry>();
                    for (DataSnapshot entry : ds.getChildren()) {
                        if (entry != null) {
                            receivedEntries.add(makeEntry(entry));
                        }
                    }
                    int adultCount=0;
                    int childCount=0;
                    for (Entry entry : receivedEntries) {
                        for (Person person : entry.getPeople()) {
                            if (TextUtils.equals(person.getType(), get(R.string.child))) {
                                childCount++;
                            } else {
                                adultCount++;
                            }
                        }
                    }

                    views.setTextViewText(R.id.widget_group_name, groupName);
                    views.setTextViewText(R.id.widget_person_count, "Total people: " + (adultCount + childCount));
                    views.setTextViewText(R.id.widget_adult_count, "Adults: " + adultCount);
                    views.setTextViewText(R.id.widget_child_count, "Children: " + childCount);
                    appWidgetManager.updateAppWidget(appWidgetId, views);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        } else {

            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widget_view, pendingIntent);

            if (StringUtils.isMissing(groupName)) {
                views.setTextViewText(R.id.widget_group_name, get(R.string.app_name));
            } else {
                views.setTextViewText(R.id.widget_group_name, groupName);
            }
            views.setTextViewText(R.id.widget_person_count, "Total people: 0");
            views.setTextViewText(R.id.widget_adult_count, "Adults: 0");
            views.setTextViewText(R.id.widget_child_count, "Children: 0");
            appWidgetManager.updateAppWidget(appWidgetId, views);

        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private static Entry makeEntry(DataSnapshot entry) {

        String uid = entry.getKey();
        String name = getValue(entry, "name");
        String phone = getValue(entry, "phone");
        String email = getValue(entry, "email");

        String street = entry.child("Address").child("street").getValue().toString();
        String line2 = entry.child("Address").child("line2").getValue().toString();
        String line3 = entry.child("Address").child("line3").getValue().toString();
        String city = entry.child("Address").child("city").getValue().toString();
        String state = entry.child("Address").child("state").getValue().toString();
        String zip = entry.child("Address").child("zip").getValue().toString();

        CustomAddress address = new CustomAddress(street, line2, line3, city, state, zip);

        Map<String, Object> p = (Map<String, Object>) entry.child("People").getValue();
        ArrayList<Person> people = new ArrayList<Person>();
        for (Map.Entry<String, Object> person : p.entrySet()) {
            Map<String, Object> info = (Map<String, Object>) person.getValue();
            String personUid = person.getKey();
            String type = info.get("type").toString();
            String personName = info.get("name").toString();
            String personPhone = info.get("phone").toString();
            String personEmail = info.get("email").toString();
            int birthOrder = Integer.parseInt(info.get("birthOrder").toString());
            Person newPerson = new Person(personUid, type, personName, personPhone, personEmail, birthOrder);
            people.add(newPerson);
        }

        return new Entry(uid, name, phone, email, address, people);

    }

    private static String getValue(DataSnapshot ds, String key) {
        return ds.child(key).getValue().toString();
    }

    private static String get(int i) {
        return getContext().getResources().getString(i);
    }

}
