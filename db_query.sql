
-- =========================================================================
-- 1. KHỞI TẠO DATABASE ĐA NGHỆ SĨ & ĐA THỂ LOẠI (ĐỒNG BỘ ID CHUỖI 3 CHỮ SỐ)
-- =========================================================================
CREATE DATABASE IF NOT EXISTS music_app_db;
USE music_app_db;

DROP TABLE IF EXISTS user_liked_songs;
DROP TABLE IF EXISTS user_followed_artists;
DROP TABLE IF EXISTS user_genre_preferences;
DROP TABLE IF EXISTS interaction_logs;
DROP TABLE IF EXISTS playlist_songs;
DROP TABLE IF EXISTS playlists;
DROP TABLE IF EXISTS song_genres;
DROP TABLE IF EXISTS song_artists;
DROP TABLE IF EXISTS songs;
DROP TABLE IF EXISTS genres;
DROP TABLE IF EXISTS artists;
DROP TABLE IF EXISTS users;
-- Bảng 1: Người dùng
CREATE TABLE users (
    id VARCHAR(255) PRIMARY KEY, -- ID từ Supabase Auth
    email VARCHAR(255) NOT NULL,
    gender VARCHAR(50),          
    avatar_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bảng 2: Nghệ sĩ (ID: A001, A002...)
CREATE TABLE artists (
    id VARCHAR(50) PRIMARY KEY,  
    name VARCHAR(255) NOT NULL,
    avatar_url VARCHAR(500),
    bio TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bảng 3: Thể loại (ID: G001, G002... - Không có image_url)
CREATE TABLE genres (
    id VARCHAR(50) PRIMARY KEY,  
    name VARCHAR(100) NOT NULL UNIQUE
);

-- Bảng 4: Bài hát (Đã loại bỏ hoàn toàn artist_id đơn lẻ)
CREATE TABLE songs (
    id VARCHAR(50) PRIMARY KEY,  
    title VARCHAR(255) NOT NULL,
    file_url VARCHAR(500) NOT NULL,
    cover_url VARCHAR(500),
    duration INT DEFAULT 0,         
    views INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bảng 5: Danh sách phát (ID: P001, P002...)
CREATE TABLE playlists (
    id VARCHAR(50) PRIMARY KEY, 
    name VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    cover_url VARCHAR(500),
    is_private BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bảng 6: Trung gian xử lý Đồng tác giả (Many-to-Many giữa Songs và Artists)
CREATE TABLE song_artists (
    song_id VARCHAR(50),
    artist_id VARCHAR(50),
    is_main_artist BOOLEAN DEFAULT TRUE, -- 1: Chính, 0: ft./Collab
    PRIMARY KEY (song_id, artist_id)
);

-- Bảng 7: Trung gian Đa Thể Loại (Many-to-Many giữa Songs và Genres)
CREATE TABLE song_genres (
    song_id VARCHAR(50),         
    genre_id VARCHAR(50),        
    PRIMARY KEY (song_id, genre_id)
);

-- Bảng 8: Trung gian Bài hát trong Playlist
CREATE TABLE playlist_songs (
    playlist_id VARCHAR(50),     
    song_id VARCHAR(50),         
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (playlist_id, song_id)
);

-- Bảng 9: Nhật ký Tương tác AI
CREATE TABLE interaction_logs (
    id VARCHAR(50) PRIMARY KEY,  
    user_id VARCHAR(255) NOT NULL,
    song_id VARCHAR(50) NOT NULL, 
    listen_duration INT DEFAULT 0,  
    is_liked BOOLEAN DEFAULT FALSE, 
    is_skipped BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bảng 10: Điểm số sở thích thể loại của User cho mô hình AI
CREATE TABLE user_genre_preferences (
    user_id VARCHAR(255) NOT NULL,
    genre_id VARCHAR(50) NOT NULL, 
    preference_score DECIMAL(5,2) DEFAULT 0.00, 
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, genre_id)
);

-- Thêm bảng Bài hát yêu thích
CREATE TABLE user_liked_songs (
    user_id VARCHAR(255) NOT NULL,
    song_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, song_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (song_id) REFERENCES songs(id) ON DELETE CASCADE
);

-- Thêm bảng Theo dõi nghệ sĩ
CREATE TABLE user_followed_artists (
    user_id VARCHAR(255) NOT NULL,
    artist_id VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, artist_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (artist_id) REFERENCES artists(id) ON DELETE CASCADE
);
-- THIẾT LẬP RÀNG BUỘC KHÓA NGOẠI
ALTER TABLE playlists ADD CONSTRAINT fk_playlists_users FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE song_artists ADD CONSTRAINT fk_sa_songs FOREIGN KEY (song_id) REFERENCES songs(id) ON DELETE CASCADE, ADD CONSTRAINT fk_sa_artists FOREIGN KEY (artist_id) REFERENCES artists(id) ON DELETE CASCADE;
ALTER TABLE song_genres ADD CONSTRAINT fk_sg_songs FOREIGN KEY (song_id) REFERENCES songs(id) ON DELETE CASCADE, ADD CONSTRAINT fk_sg_genres FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE CASCADE;
ALTER TABLE playlist_songs ADD CONSTRAINT fk_ps_playlists FOREIGN KEY (playlist_id) REFERENCES playlists(id) ON DELETE CASCADE, ADD CONSTRAINT fk_ps_songs FOREIGN KEY (song_id) REFERENCES songs(id) ON DELETE CASCADE;
ALTER TABLE interaction_logs ADD CONSTRAINT fk_il_users FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, ADD CONSTRAINT fk_il_songs FOREIGN KEY (song_id) REFERENCES songs(id) ON DELETE CASCADE;
ALTER TABLE user_genre_preferences ADD CONSTRAINT fk_ugp_users FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, ADD CONSTRAINT fk_ugp_genres FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE CASCADE;