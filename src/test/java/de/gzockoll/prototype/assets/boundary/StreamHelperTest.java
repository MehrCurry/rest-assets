package de.gzockoll.prototype.assets.boundary;

import de.gzockoll.prototype.assets.entity.Media;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class StreamHelperTest {

    @Test
    public void testStreamOptional() throws Exception {
        Media media=Media.builder()
                .contentType(MediaType.TEXT_PLAIN_VALUE)
                .originalFilename("junit.txt")
                .length(12345)
                .build();
        media.setExistsInProduction(true);
        File file=new File(media.getFullname());
        file.getParentFile().mkdirs();
        PrintWriter pw=new PrintWriter(file);
        pw.println("Data");
        pw.close();

        ResponseEntity<InputStreamResource> result = StreamHelper.streamResult(Optional.of(media));
        assertThat(result.getHeaders().getContentLength()).isEqualTo(12345);
        assertThat(result.getHeaders().getContentType()).isEqualTo(MediaType.TEXT_PLAIN);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        FileUtils.deleteQuietly(file);
        deleteTree(file.getPath().split(File.separator)[0]);
    }

    @Test
    public void testStreamOptionalEmpty() throws Exception {
        ResponseEntity<InputStreamResource> result = StreamHelper.streamResult(Optional.empty());
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private void deleteTree(String path) throws IOException {
        Path directory = Paths.get(path);
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }

        });
    }

}