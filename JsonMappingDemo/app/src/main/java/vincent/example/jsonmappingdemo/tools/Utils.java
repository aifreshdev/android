package vincent.example.jsonmappingdemo.tools;

import android.content.Context;
import android.text.TextUtils;
import android.widget.EditText;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by vincent on 11/1/2018.
 */

public class Utils {

    public static boolean isEmpty(String str){
        return str == null || TextUtils.isEmpty(str) || str.trim().equals("") || str.length() == 0;
    }

    public static boolean isEmpty(EditText editText){
        if(editText != null){
            String str = editText.getText().toString();
            return TextUtils.isEmpty(editText.getText().toString()) || str.trim().equals("") || str.length() == 0;
        }

        return true;
    }

    public static <T> boolean isEmpty(ArrayList<T> array){
        return array == null || array.isEmpty();
    }

    public static String loadTestJsonData(Context context, String folder, String filename) throws IOException {
        InputStream is = context.getAssets().open(!isEmpty(folder) ? folder + File.separator + filename : filename);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        return new String(buffer, "UTF-8");
    }

}
