import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


public class WorkFile {

    public static void createFolder(String obj, StringBuilder builder, String str) {
        File newFolder = new File(obj);
        if (newFolder.mkdir())
            builder.append(str);

    }

    public static void createFile(String obj, StringBuilder builder, String str) {
        File newFile = new File(obj);
        try {
            if (newFile.createNewFile())
                builder.append(str);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }


    public static void main(String[] args) {

        StringBuilder strBuilder = new StringBuilder();

        //создаем директории в папке Games
        createFolder("/Users/SIREN-A/Games/src", strBuilder, "Folder src has been created \n");
        createFolder("/Users/SIREN-A/Games/res", strBuilder, "Folder res has been created \n");
        createFolder("/Users/SIREN-A/Games/savegames", strBuilder, "Folder savegames has been created \n");
        createFolder("/Users/SIREN-A/Games/temp", strBuilder, "Folder temp has been created \n");

        //в каталоге src создаем две директории: main, test
        createFolder("/Users/SIREN-A/Games/src/main", strBuilder, "Folder main (in src) has been created \n");
        createFolder("/Users/SIREN-A/Games/src/test", strBuilder, "Folder test (in src) has been created \n");

        //В подкаталоге main создаем два файла: Main.java, Utils.java.
        createFile("/Users/SIREN-A/Games/src/main/Main.java", strBuilder, "File Main.java has been created  \n");
        createFile("/Users/SIREN-A/Games/src/main/Utils.java", strBuilder, "File Utils.java has been created  \n");

        //В каталоге res создаем три директории: drawables, vectors, icons
        createFolder("/Users/SIREN-A/Games/res/drawables", strBuilder, "Folder drawables (in res) has been created \n");
        createFolder("/Users/SIREN-A/Games/res/vectors", strBuilder, "Folder vectors (in res) has been created \n");
        createFolder("/Users/SIREN-A/Games/res/icons", strBuilder, "Folder icons (in res) has been created \n");

        //В директории temp создаем файл temp.txt
        createFile("/Users/SIREN-A/Games/temp/temp.txt", strBuilder, "File temp.txt has been created  \n");

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

        for (int i = 0; i < listZipFiles.size(); i++) {
            File fileDelete = new File(listZipFiles.get(i));
            if (fileDelete.delete()) {
                System.out.println("File " + fileDelete.getName() + " deleted from folder");
            }
        }
        System.out.println();

        //задача 3.1 распаковка из zip

        openZip("/Users/SIREN-A/Games/savegames/zip.zip", "/Users/SIREN-A/Games/savegames/");

        //задача 3.2  считывание и десериализация одного из разархивированных файлов и вывод в консоль

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
                File zipFile = new File(obj.get(i));
                try (FileInputStream fis = new FileInputStream(zipFile)) {
                    ZipEntry entry = new ZipEntry(zipFile.getName());
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
        File folder = new File(unpackFolder);
        try (ZipInputStream zin = new ZipInputStream(new FileInputStream(wayToArchive))) {
            ZipEntry entry;
            String name;
            long size;
            while ((entry = zin.getNextEntry()) != null) {

                name = entry.getName(); // получим название файла
                // распаковка
                if (folder.exists()) {
                    FileOutputStream fout = new FileOutputStream(unpackFolder + name);
                    for (int c = zin.read(); c != -1; c = zin.read()) {
                        fout.write(c);
                    }
                    fout.flush();
                    zin.closeEntry();
                    fout.close();
                } else {
                    boolean created = folder.mkdir();
                }
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
