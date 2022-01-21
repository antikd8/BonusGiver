package by.bstu.fit.drugov.bonusgiver.Helpers;

import android.content.Context;
import android.os.Environment;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import by.bstu.fit.drugov.bonusgiver.Models.BonusGiver;
import by.bstu.fit.drugov.bonusgiver.Models.Statistics;

public class JSONHelper {
    private static final String FILE_NAME = "students.json";

    public boolean exportToJSONPublic(Context context, ArrayList<BonusGiver> dataList) {
        Gson gson = new Gson();
        DataItems dataItems = new DataItems();
        dataItems.setUsers(Collections.singletonList(dataList));
        String jsonString = gson.toJson(dataItems);

        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(getExternalPath());
            context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            fileOutputStream.write(jsonString.getBytes());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public boolean exportToJSONPublicStats(Context context, List<Statistics> dataList) {
        Gson gson = new Gson();
        DataItems dataItems = new DataItems();
        dataItems.setUsers(Collections.singletonList(dataList));
        String jsonString = gson.toJson(dataItems);

        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(getExternalPath());
            context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            fileOutputStream.write(jsonString.getBytes());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    private File getExternalPath() {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), FILE_NAME);
    }

    public static boolean exportToJSON(Context context, List<BonusGiver> dataList) {

        Gson gson = new Gson();
        DataItems dataItems = new DataItems();
        dataItems.setUsers(Collections.singletonList(dataList));
        String jsonString = gson.toJson(dataItems);

        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            fileOutputStream.write(jsonString.getBytes());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

    public static List<Object> importFromJSON(Context context) {

        InputStreamReader streamReader = null;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = context.openFileInput(FILE_NAME);
            streamReader = new InputStreamReader(fileInputStream);
            Gson gson = new Gson();
            DataItems dataItems = gson.fromJson(streamReader, DataItems.class);
            return dataItems.getUsers();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (streamReader != null) {
                try {
                    streamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    private static class DataItems {
        private List<Object> bonuses;

        List<Object> getUsers() {
            return bonuses;
        }

        void setUsers(List<Object> bonuses) {
            this.bonuses = bonuses;
        }
    }
}
