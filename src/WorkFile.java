import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


public class WorkFile {


    public static void main(String[] args) {

        StringBuilder strBuilder = new StringBuilder();


        //создаем директории в папке Games
        File src = new File("/Users/SIREN-A/Games/src");
        boolean created = src.mkdir();
        if (created)
            strBuilder.append("Folder has been created \n");

        File res = new File("/Users/SIREN-A/Games/res");
        if (res.mkdir())
            strBuilder.append("Folder has been created \n");

        File savegames = new File("/Users/SIREN-A/Games/savegames");
        if (savegames.mkdir())
            strBuilder.append("Folder has been created \n");

        File temp = new File("/Users/SIREN-A/Games/temp");
        if (temp.mkdir())
            strBuilder.append("Folder has been created \n");

        //каталоге src создаем две директории: main, test

        File main = new File("/Users/SIREN-A/Games/src/main");
        if (main.mkdir())
            strBuilder.append("Folder has been created \n");

        File test = new File("/Users/SIREN-A/Games/src/test");
        if (test.mkdir())
            strBuilder.append("Folder has been created \n");

        //В подкаталоге main создаем два файла: Main.java, Utils.java.

        File mainJava = new File("/Users/SIREN-A/Games/src/main/Main.java");
        try {
            if (mainJava.createNewFile())
                strBuilder.append("Файл был создан \n");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        File utilsJava = new File("/Users/SIREN-A/Games/src/main/Utils.java");
        try {
            if (utilsJava.createNewFile())
                strBuilder.append("Файл был создан \n");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        //В каталоге res создаем три директории: drawables, vectors, icons
        File drawables = new File("/Users/SIREN-A/Games/res/drawables");
        if (drawables.mkdir())
            strBuilder.append("Folder has been created \n");

        File vectors = new File("/Users/SIREN-A/Games/res/vectors");
        if (vectors.mkdir())
            strBuilder.append("Folder has been created \n");

        File icons = new File("/Users/SIREN-A/Games/res/icons");
        if (icons.mkdir())
            strBuilder.append("Folder has been created \n");

        //В директории temp создаем файл temp.txt
        File tempTXT = new File("/Users/SIREN-A/Games/temp/temp.txt");
        try {
            if (tempTXT.createNewFile())
                System.out.println("Файл был создан \n");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        // создание FileWriter
        String text = strBuilder.toString();
        System.out.println(text);
        try (FileWriter writer = new FileWriter("/Users/SIREN-A/Games/temp/temp.txt", false)) {

            writer.write(text);

        } catch (IOException e) {
            e.printStackTrace();
        }


        // задача 2 - создаем 3 обьекта класса
        GameProgress progressOne = new GameProgress(2, 2, 2, 2.5);
        GameProgress progressTwo = new GameProgress(3, 3, 3, 3.5);
        GameProgress progressThree = new GameProgress(4, 4, 4, 4.5);

        // задача 2.1 Сериализация
        saveGame("/Users/SIREN-A/Games/savegames/save1.dat", progressOne);
        saveGame("/Users/SIREN-A/Games/savegames/save2.dat", progressTwo);
        saveGame("/Users/SIREN-A/Games/savegames/save3.dat", progressThree);

        // Задача 2.2 запаковываем файлы сохранений из папки savegames в архив zip

        List<String> listZipFiles = new ArrayList<>();
        listZipFiles.add("/Users/SIREN-A/Games/savegames/save1.dat");
        listZipFiles.add("/Users/SIREN-A/Games/savegames/save2.dat");
        listZipFiles.add("/Users/SIREN-A/Games/savegames/save3.dat");

        zipFiles("/Users/SIREN-A/Games/savegames/zip.zip", listZipFiles);

        // Задача 2.3 удаляем файлы, не лежащие в архиве

        File fileDelete1 = new File("/Users/SIREN-A/Games/savegames/save1.dat");
        if (fileDelete1.delete()) {
            System.out.println("File has been deleted");
        }
        File fileDelete2 = new File("/Users/SIREN-A/Games/savegames/save2.dat");
        if (fileDelete2.delete()) {
            System.out.println("File has been deleted");
        }
        File fileDelete3 = new File("/Users/SIREN-A/Games/savegames/save3.dat");
        if (fileDelete3.delete()) {
            System.out.println("File has been deleted");
        }
        System.out.println();

        // задача 3.1 распаковка из zip

        openZip("/Users/SIREN-A/Games/savegames/zip.zip", "/Users/SIREN-A/Games/savegames");

        // задача 3.2  считывание и десериализация одного из разархивированных файлов и вывод в консоль

        openProgress("/Users/SIREN-A/Games/savegames/save1.dat");
        System.out.println(openProgress("/Users/SIREN-A/Games/savegames/save1.dat"));


    }

    private static void saveGame(String way, GameProgress obj) {
        try (ObjectOutputStream save = new ObjectOutputStream(new FileOutputStream(way))) {
            save.writeObject(obj);
        } catch (Exception ex) {

            System.out.println(ex.getMessage());
        }

    }

    private static void zipFiles(String wayToArchive, List<String> obj) {

        try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(wayToArchive))) {
            for (int i = 0; i < obj.size(); i++) {
                String zipFile = obj.get(i);
                try (FileInputStream fis = new FileInputStream(zipFile)) {
                    ZipEntry entry = new ZipEntry(zipFile);
                    zout.putNextEntry(entry);
                    // считываем содержимое файла в массив byte
                    byte[] buffer = new byte[fis.available()];
                    fis.read(buffer);
                    // добавляем содержимое к архиву
                    zout.write(buffer);
                    // закрываем текущую запись для новой записи
                    zout.closeEntry();
                } catch (Exception ex) {

                    System.out.println(ex.getMessage());
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void openZip(String wayToArchive, String unpackFolder) {
        try (ZipInputStream zin = new ZipInputStream(new FileInputStream(wayToArchive))) {
            ZipEntry entry;
            String name;
            long size;
            while ((entry = zin.getNextEntry()) != null) {

                name = entry.getName(); // получим название файла
                // распаковка
                FileOutputStream fout = new FileOutputStream(name);
                for (int c = zin.read(); c != -1; c = zin.read()) {
                    fout.write(c);
                }
                fout.flush();
                zin.closeEntry();
                fout.close();
            }
        } catch (Exception ex) {

            System.out.println(ex.getMessage());
        }
    }

    private static GameProgress openProgress(String wayToGame) {
        GameProgress gameProgress = null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(wayToGame))) {
            //десериализуем объект и скастим его в класс
            gameProgress = (GameProgress) ois.readObject();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }


        return gameProgress;
    }
}
