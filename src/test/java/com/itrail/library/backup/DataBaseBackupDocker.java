package com.itrail.library.backup;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Disabled
@DisplayName("Тестирование backup и restore from Docker")
public class DataBaseBackupDocker {

    @Test
    @DisplayName("Создание library.backup - через Docker -- library.backup в папку обязательно нужно самому создать")
    public void dumpPostgresDockerAlternative() throws Exception {
        Path backupPath = Paths.get(".", "src", "main", "resources", "db", "backup").toAbsolutePath().normalize();
        File backupDir = backupPath.toFile();
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }
        String backupFile = backupPath.toString() + File.separator + "library.backup";
        String command = String.format("docker exec -e PGPASSWORD=admin library_db pg_dump -U postgres -Fc lib > \"%s\"", backupFile);
        
        ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
                        processBuilder.redirectErrorStream(true);
        
        System.out.println("Executing: " + command);
        Process process = processBuilder.start();
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
        
        int exitCode = process.waitFor();
        if (exitCode == 0) {
            System.out.println("✓ Backup created: " + backupFile);
            // Проверяем размер файла
            File file = new File(backupFile);
            System.out.println("File size: " + file.length() + " bytes");
        } else {
            System.out.println("✗ Error code: " + exitCode);
        }
    }

    @Test
    @DisplayName("Загрузка в бд library.backup - через Docker")
    public void restorePostgresDocker() throws Exception {

        List<String> copyToDocker = List.of("docker", "cp", 
                                            "./src/main/resources/db/backup/library.backup", 
                                            "library_db:/tmp/library.backup" );
        processBuilder(copyToDocker);

        List<String> restore = List.of( "docker", "exec", "library_db",
                                        "pg_restore",
                                        "--host=localhost", 
                                        "--username=postgres",
                                        "--dbname=lib",
                                        "--format=c",
                                        "--verbose",
                                       "/tmp/library.backup" );
        processBuilder(restore);
    }
    
    private void processBuilder( List<String> commnds ) throws Exception{
        ProcessBuilder processBuilder = new ProcessBuilder( commnds );
                       processBuilder.environment().put("PGPASSWORD", "admin");
                       processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        try (BufferedReader reader = new BufferedReader( new InputStreamReader( process.getInputStream() ))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
        int exitCode = process.waitFor();
        if (exitCode == 0) {
            System.out.println("Success" );
        } else {   
            System.out.println( "Code >>> " + exitCode );
        }

    }
}
