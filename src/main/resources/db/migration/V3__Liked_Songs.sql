-- ===========================================
-- 6. LIKED_SONGS – uživatelé si ukládají oblíbené písničky
-- ===========================================

-- Přidáme počet liků do tabulky songs
ALTER TABLE songs ADD COLUMN likes BIGINT NOT NULL DEFAULT 0;

-- Tabulka liked_songs propojuje users <-> songs (many-to-many)
CREATE TABLE liked_songs (
                             user_id BIGINT NOT NULL,
                             song_id BIGINT NOT NULL,
                             liked_at TIMESTAMP NOT NULL DEFAULT now(),

                             PRIMARY KEY (user_id, song_id), -- každý uživatel může lajkout daný song jen jednou

                             CONSTRAINT fk_liked_songs_user
                                 FOREIGN KEY (user_id) REFERENCES users(profile_id) ON DELETE CASCADE,

                             CONSTRAINT fk_liked_songs_song
                                 FOREIGN KEY (song_id) REFERENCES songs(id) ON DELETE CASCADE
);

-- Indexy pro rychlé vyhledávání
CREATE INDEX idx_liked_songs_user_id ON liked_songs(user_id);
CREATE INDEX idx_liked_songs_song_id ON liked_songs(song_id);

ALTER TABLE users
    ADD liked_songs_count INT NOT NULL DEFAULT 0;