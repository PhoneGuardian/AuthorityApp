package it.polimi.guardian.authorityapp;

/**
 * Created by Nemanja on 16/12/2014.
 */

        import java.io.BufferedOutputStream;
        import java.io.BufferedReader;
        import java.io.File;
        import java.io.FileNotFoundException;
        import java.io.FileOutputStream;
        import java.io.FileReader;
        import java.io.IOException;
        import java.io.OutputStream;
        import java.io.OutputStreamWriter;

        import android.app.Activity;
        import android.os.Environment;
        import android.util.Log;
        import android.widget.Toast;

public class FileHelper extends Activity{

    public void writeToFile(String jsonString, String filename) {

        OutputStream outputStream = null;

        try {
            File dir = getAppDirectory("GuardianAuthority");
            File file = new File(dir, filename);
            String filepath = file.getAbsolutePath();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            outputStream = new BufferedOutputStream(fileOutputStream);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            outputStreamWriter.write(jsonString);
            outputStreamWriter.close();

            Log.d("FileHelper", "File write successful.");
            Log.d("FileHelper", "File path:"+filepath);
            Log.d("FileHelper", "File content:"+jsonString);

        }catch (FileNotFoundException e1) {
            Toast.makeText(this, "Failed to save file"+filename, Toast.LENGTH_LONG).show();
            Log.d("FileHelper - Error:",e1.getMessage());
            e1.printStackTrace();
        }catch(Exception e){
            Log.d("FileHelper - Error:",e.getMessage());
            Toast.makeText(this, "Failed to save file"+filename, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    Log.d("FileHelper - Error:",e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }


    public String readFromFile(String filename) {
        String ret = "";

        try {
            File dir = getAppDirectory("GuardianAuthority");
            File file = new File(dir, filename);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String receiveString = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ( (receiveString = bufferedReader.readLine()) != null ) {//take a line
                stringBuilder.append(receiveString);//and add it into stringBuilder
            }

            ret = stringBuilder.toString();//when everything is read, it is put in variable ret
            bufferedReader.close();
        }
        catch (FileNotFoundException e) {
            Log.e("FileHelper - Error: ", "File not found: " + e.toString());//create file
            //writeToFile("{}", filename);
        } catch (IOException e) {
            Log.e("FileHelper - Error: ", "Can not read file: " + e.toString());
        }

        return ret;
    }
    public  File getAppDirectory(String appFolderName) {
        File appDirectory = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            File sdCard = Environment.getExternalStorageDirectory();

            appDirectory = new File(
                    sdCard, 									//sd card
                    appFolderName								//+ folder name
            );

            if (appDirectory != null){
                if (! appDirectory.mkdirs()){ //create directory under filename of storageDir variable value

                    //true if the directory was created,
                    //false on failure or if the directory already existed.
                    if (! appDirectory.exists()){
                        //if directory doesn't exist
                        Toast.makeText(this, "Failed to create app directory.",Toast.LENGTH_SHORT).show();
                        return null;
                    }
                }
            }

        }
        else{
            Toast.makeText(this, "SD card is not available for READ/WRITE operations.",Toast.LENGTH_SHORT).show();
        }

        return appDirectory;
    }
    public String getAbsolutePath(String filename){

        File dir = getAppDirectory("GuardianAuthority");
        File file = new File(dir, filename);
        String filepath = file.getAbsolutePath();
        return filepath;
    }
    public boolean fileExists(String filename) {
        String ret = "";

        try {
            File dir = getAppDirectory("GuardianAuthority");
            File file = new File(dir, filename);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String receiveString = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ( (receiveString = bufferedReader.readLine()) != null ) {//take a line
                stringBuilder.append(receiveString);//and add it into stringBuilder
            }

            ret = stringBuilder.toString();//when everything is read, it is put in variable ret
            bufferedReader.close();
            return true;
        }
        catch (FileNotFoundException e) {
            Log.e("FileHelper - Error: ", "File not found: " + e.toString());//create file
            return false;
        } catch (IOException e) {
            Log.e("FileHelper - Error: ", "Can not read file: " + e.toString());
            return true;
        }
    }
    public boolean fileDelete(String filename) {
        try {
            File dir = getAppDirectory("GuardianAuthority");
            File file = new File(dir, filename);
            boolean deleted = file.delete();
            return deleted;
        } catch (Exception e) {
            Log.e("FileHelper - Error: ", "File not found: " + e.toString());//delete file
            return false;
        }
    }
}
