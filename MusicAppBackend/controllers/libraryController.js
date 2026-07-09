const db = require('../config/db');

// LIKED SONGS
const getLikedSongs = async (req, res) => {
    const { userId } = req.params;
    try {
        const [rows] = await db.query(`
            SELECT s.*,
                   (SELECT GROUP_CONCAT(a.name SEPARATOR ', ')
                    FROM artists a
                    JOIN song_artists sa ON a.id = sa.artist_id
                    WHERE sa.song_id = s.id) as artist_names
            FROM songs s
            JOIN user_liked_songs uls ON s.id = uls.song_id
            WHERE uls.user_id = ?
        `, [userId]);
        res.status(200).json({ success: true, data: rows });
    } catch (error) {
        console.error("Error in getLikedSongs:", error);
        res.status(500).json({ success: false, message: error.message });
    }
};

const toggleLikeSong = async (req, res) => {
    const { userId, songId } = req.body;
    if (!userId || !songId) {
        return res.status(400).json({ success: false, message: "Missing userId or songId" });
    }
    try {
        const [existing] = await db.query('SELECT * FROM user_liked_songs WHERE user_id = ? AND song_id = ?', [userId, songId]);
        if (existing.length > 0) {
            await db.query('DELETE FROM user_liked_songs WHERE user_id = ? AND song_id = ?', [userId, songId]);
            res.status(200).json({ success: true, message: 'Removed from liked songs', isLiked: false });
        } else {
            await db.query('INSERT INTO user_liked_songs (user_id, song_id) VALUES (?, ?)', [userId, songId]);
            res.status(200).json({ success: true, message: 'Added to liked songs', isLiked: true });
        }
    } catch (error) {
        console.error("Error in toggleLikeSong:", error);
        res.status(500).json({ success: false, message: error.message });
    }
};

// PLAYLISTS
const getUserPlaylists = async (req, res) => {
    const { userId } = req.params;
    try {
        const [playlists] = await db.query('SELECT * FROM playlists WHERE user_id = ?', [userId]);

        for (let p of playlists) {
            const [songs] = await db.query(`
                SELECT s.*,
                       (SELECT GROUP_CONCAT(a.name SEPARATOR ', ')
                        FROM artists a
                        JOIN song_artists sa ON a.id = sa.artist_id
                        WHERE sa.song_id = s.id) as artist_names
                FROM songs s
                JOIN playlist_songs ps ON s.id = ps.song_id
                WHERE ps.playlist_id = ?
            `, [p.id]);
            p.songs = songs;
        }

        res.status(200).json({ success: true, data: playlists });
    } catch (error) {
        console.error("Error in getUserPlaylists:", error);
        res.status(500).json({ success: false, message: error.message });
    }
};

const createPlaylist = async (req, res) => {
    const { userId, name } = req.body;
    console.log("Creating playlist for user:", userId, "Name:", name);
    if (!userId || !name) {
        return res.status(400).json({ success: false, message: "Missing userId or playlist name" });
    }
    try {
        // Tự tạo ID vì DB không có AUTO_INCREMENT (Ví dụ: P171567890123)
        const playlistId = 'P' + Date.now();
        const [result] = await db.query('INSERT INTO playlists (id, user_id, name) VALUES (?, ?, ?)', [playlistId, userId, name]);
        res.status(201).json({
            success: true,
            message: "Playlist created",
            data: { id: playlistId, name, songs: [] }
        });
    } catch (error) {
        console.error("Error in createPlaylist:", error);
        res.status(500).json({ success: false, message: error.message });
    }
};

const addSongToPlaylist = async (req, res) => {
    const { playlistId, songId } = req.body;
    try {
        await db.query('INSERT INTO playlist_songs (playlist_id, song_id) VALUES (?, ?)', [playlistId, songId]);
        res.status(200).json({ success: true, message: 'Added to playlist' });
    } catch (error) {
        console.error("Error in addSongToPlaylist:", error);
        res.status(500).json({ success: false, message: error.message });
    }
};

// FOLLOWED ARTISTS
const getFollowedArtists = async (req, res) => {
    const { userId } = req.params;
    try {
        const [rows] = await db.query(`
            SELECT a.*
            FROM artists a
            JOIN user_followed_artists ufa ON a.id = ufa.artist_id
            WHERE ufa.user_id = ?
        `, [userId]);
        res.status(200).json({ success: true, data: rows });
    } catch (error) {
        console.error("Error in getFollowedArtists:", error);
        res.status(500).json({ success: false, message: error.message });
    }
};

const toggleFollowArtist = async (req, res) => {
    const { userId, artistId } = req.body;
    if (!userId || !artistId) {
        return res.status(400).json({ success: false, message: "Missing userId or artistId" });
    }
    try {
        const [existing] = await db.query('SELECT * FROM user_followed_artists WHERE user_id = ? AND artist_id = ?', [userId, artistId]);
        if (existing.length > 0) {
            await db.query('DELETE FROM user_followed_artists WHERE user_id = ? AND artist_id = ?', [userId, artistId]);
            res.status(200).json({ success: true, message: 'Unfollowed artist', isFollowed: false });
        } else {
            await db.query('INSERT INTO user_followed_artists (user_id, artist_id) VALUES (?, ?)', [userId, artistId]);
            res.status(200).json({ success: true, message: 'Followed artist', isFollowed: true });
        }
    } catch (error) {
        console.error("Error in toggleFollowArtist:", error);
        res.status(500).json({ success: false, message: error.message });
    }
};

module.exports = {
    getLikedSongs,
    toggleLikeSong,
    getUserPlaylists,
    createPlaylist,
    addSongToPlaylist,
    getFollowedArtists,
    toggleFollowArtist
};
