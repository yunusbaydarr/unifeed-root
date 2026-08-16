package app.unifeed.seeder;

import app.unifeed.media.MediaStorageService;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;
import javax.imageio.ImageIO;
import org.springframework.web.multipart.MultipartFile;

/** Downloads seed artwork and always sends it through the same local media pipeline as uploads. */
final class SeedMediaFactory {
    private final MediaStorageService storage;
    private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();

    SeedMediaFactory(MediaStorageService storage) { this.storage = storage; }

    String avatar(String seed) { return store("https://api.dicebear.com/7.x/avataaars/png?seed=" + seed, MediaStorageService.Category.AVATAR, seed, "avatar.png").mediumPath(); }
    String clubImage(String seed) { return store("https://picsum.photos/seed/club-" + seed + "/800/600", MediaStorageService.Category.CLUB, seed, "club.jpg").mediumPath(); }
    String postImage(String seed) { return store("https://picsum.photos/seed/post-" + seed + "/800/600", MediaStorageService.Category.POST, seed, "post.jpg").mediumPath(); }

    private MediaStorageService.MediaPaths store(String url, MediaStorageService.Category category, String seed, String filename) {
        try {
            HttpResponse<byte[]> response = client.send(HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(4)).GET().build(), HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() >= 200 && response.statusCode() < 300 && response.body().length > 0) {
                return storage.store(new SeedMultipartFile(filename, response.body()), category);
            }
        } catch (Exception ignored) { /* Offline development still gets deterministic local artwork. */ }
        try { return storage.store(new SeedMultipartFile(filename, fallback(seed)), category); }
        catch (Exception exception) { throw new IllegalStateException("Demo media could not be stored", exception); }
    }

    private byte[] fallback(String seed) throws Exception {
        int hue = Math.floorMod(seed.toLowerCase(Locale.ROOT).hashCode(), 360);
        BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.getHSBColor(hue / 360f, .42f, .82f)); graphics.fillRect(0, 0, 800, 600);
        graphics.setColor(Color.WHITE); graphics.fillOval(280, 120, 240, 240); graphics.fillRoundRect(190, 350, 420, 180, 80, 80);
        graphics.dispose();
        ByteArrayOutputStream out = new ByteArrayOutputStream(); ImageIO.write(image, "png", out); return out.toByteArray();
    }

    private record SeedMultipartFile(String filename, byte[] bytes) implements MultipartFile {
        @Override public String getName() { return "file"; }
        @Override public String getOriginalFilename() { return filename; }
        @Override public String getContentType() { return "image/png"; }
        @Override public boolean isEmpty() { return bytes.length == 0; }
        @Override public long getSize() { return bytes.length; }
        @Override public byte[] getBytes() { return bytes.clone(); }
        @Override public java.io.InputStream getInputStream() { return new java.io.ByteArrayInputStream(bytes); }
        @Override public void transferTo(java.io.File destination) throws java.io.IOException { java.nio.file.Files.write(destination.toPath(), bytes); }
    }
}
