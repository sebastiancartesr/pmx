package org.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

public class JsonService {

    public int[][] readJsonMatrixFromFile(String filePath) throws IOException, JSONException {
        int[][] matrix = null;
        try {
            // Lee el archivo JSON
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();

            // Convierte el contenido del archivo a JSONArray
            String jsonContent = new String(data, "UTF-8");
            JSONArray jsonArray = new JSONArray(new JSONTokener(jsonContent));

            // Determina las dimensiones de la matriz (suponiendo que sea una matriz cuadrada)
            int size = jsonArray.length();
            matrix = new int[size][size];

            // Llena la matriz con los valores del JSONArray
            for (int i = 0; i < size; i++) {
                JSONArray rowArray = jsonArray.getJSONArray(i);
                for (int j = 0; j < size; j++) {
                    matrix[i][j] = rowArray.getInt(j);
                }
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return matrix;
    }
}
