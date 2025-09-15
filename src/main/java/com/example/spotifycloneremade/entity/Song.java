package com.example.spotifycloneremade.entity;

import com.example.spotifycloneremade.enums.SongType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;

@Getter
@Setter
@Entity
@Table(name = "songs")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "url")
    private String url;

    @Column(name = "public_id")
    private String publicId;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "genre")
    private String genre;

    @Column(name = "plays")
    private Integer plays = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id")
    private Artist artist;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private SongType type;

    /*@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_image_id")
    private SongImage songImage;*/
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "song_image_id")
    private SongImage songImage;

    @OneToMany(mappedBy = "song")
    private List<SongImage> songImages = new ArrayList<>();


    public static int getDurationFromMultipartFile(MultipartFile file) {
        try (InputStream stream = file.getInputStream()) {
            Metadata metadata = new Metadata();
            ContentHandler handler = new BodyContentHandler();
            AutoDetectParser parser = new AutoDetectParser();
            ParseContext context = new ParseContext();

            parser.parse(stream, handler, metadata, context);

            //System.out.println("🔍 Metadata from audio file:");
            /*for (String name : metadata.names()) {
                System.out.println(name + " = " + metadata.get(name));
            }*/

            String durationStr = metadata.get("xmpDM:duration");
            if (durationStr != null) {
                double durationSeconds = Double.parseDouble(durationStr.replace(",", "."));
                return (int) durationSeconds;
            } else {
                System.err.println("⚠️ Duration metadata not found.");
                return 0;
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to extract duration", e);
        }
    }



}