const db = require('../config/db');

// LIKED SONGS
const getLikedSongs = async (req, res) => {
    const { userId } = req.params;
    try {
        const [songs] = await db.query(`
            SELECT s.*
            FROM songs s
            JOIN user_liked_songs uls ON s.id = uls.song_id
            WHERE uls.user_id = ?
        `, [userId]);

        if (songs.length === 0) {
            return res.status(200).json({ success: true, data: [] });
        }

        const songIds = songs.map(s => s.id);
        const [songArtists] = await db.query(`
            SELECT sa.song_id, sa.artist_id, sa.is_main_artist, a.name 
            FROM song_artists sa 
            JOIN artists a ON sa.artist_id = a.id
            WHERE sa.song_id IN (?)
        `, [songIds]);

        const artistsMap = {};
        songArtists.forEach(sa => {
            if (!artistsMap[sa.song_id]) {
                artistsMap[sa.song_id] = [];
            }
            artistsMap[sa.song_id].push({
                id: sa.artist_id,
                artist_id: sa.artist_id,
                name: sa.name,
                is_main_artist: sa.is_main_artist === 1 || sa.is_main_artist === true
            });
        });

        const mappedSongs = songs.map(s => {
            return {
                ...s,
                artists: artistsMap[s.id] || []
            };
        });

        res.status(200).json({ success: true, data: mappedSongs });
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
                SELECT s.*
                FROM songs s
                JOIN playlist_songs ps ON s.id = ps.song_id
                WHERE ps.playlist_id = ?
            `, [p.id]);

            if (songs.length > 0) {
                const songIds = songs.map(s => s.id);
                const [songArtists] = await db.query(`
                    SELECT sa.song_id, sa.artist_id, sa.is_main_artist, a.name 
                    FROM song_artists sa 
                    JOIN artists a ON sa.artist_id = a.id
                    WHERE sa.song_id IN (?)
                `, [songIds]);

                const artistsMap = {};
                songArtists.forEach(sa => {
                    if (!artistsMap[sa.song_id]) {
                        artistsMap[sa.song_id] = [];
                    }
                    artistsMap[sa.song_id].push({
                        id: sa.artist_id,
                        artist_id: sa.artist_id,
                        name: sa.name,
                        is_main_artist: sa.is_main_artist === 1 || sa.is_main_artist === true
                    });
                });

                p.songs = songs.map(s => {
                    return {
                        ...s,
                        artists: artistsMap[s.id] || []
                    };
                });
            } else {
                p.songs = [];
            }
        }

        res.status(200).json({ success: true, data: playlists });
    } catch (error) {
        console.error("Error in getUserPlaylists:", error);
        res.status(500).json({ success: false, message: error.message });
    }
};

const createPlaylist = async (req, res) => {
    const { userId, name } = req.body;
    console.log("Creating playlist request - User:", userId, "Name:", name);

    if (!userId || !name) {
        return res.status(400).json({ success: false, message: "Missing userId or playlist name" });
    }

    try {
        // Lấy ID cao nhất hiện tại để tạo ID mới dạng Pxxx
        const [lastPlaylist] = await db.query('SELECT id FROM playlists ORDER BY id DESC LIMIT 1');
        let newIdNumber = 1;
        if (lastPlaylist.length > 0) {
            const lastId = lastPlaylist[0].id;
            if (lastId.startsWith('P')) {
                newIdNumber = parseInt(lastId.substring(1)) + 1;
            }
        }
        const playlistId = 'P' + String(newIdNumber).padStart(3, '0');

        // Kiểm tra xem user có tồn tại trong MySQL không trước khi tạo playlist
        const [userCheck] = await db.query('SELECT id FROM users WHERE id = ?', [userId]);
        if (userCheck.length === 0) {
            console.error("User not found in MySQL:", userId);
            return res.status(404).json({ success: false, message: "User not found. Please login again." });
        }

        const [result] = await db.query('INSERT INTO playlists (id, user_id, name) VALUES (?, ?, ?)', [playlistId, userId, name]);
        console.log("Successfully created playlist in MySQL:", playlistId);

        res.status(201).json({
            success: true,
            message: "Playlist created successfully",
            data: { id: playlistId, name, songs: [] }
        });
    } catch (error) {
        console.error("Error in createPlaylist:", error);
        res.status(500).json({ success: false, message: "Server error: " + error.message });
    }
};

const deletePlaylist = async (req, res) => {
    const { playlistId } = req.body;
    if (!playlistId) {
        return res.status(400).json({ success: false, message: "Missing playlistId" });
    }
    try {
        // First delete all songs in the playlist due to foreign key constraints
        await db.query('DELETE FROM playlist_songs WHERE playlist_id = ?', [playlistId]);
        // Then delete the playlist itself
        await db.query('DELETE FROM playlists WHERE id = ?', [playlistId]);
        res.status(200).json({ success: true, message: 'Playlist deleted successfully' });
    } catch (error) {
        console.error("Error in deletePlaylist:", error);
        res.status(500).json({ success: false, message: error.message });
    }
};

const addSongToPlaylist = async (req, res) => {
    const { playlistId, songId } = req.body;
    console.log("Adding song to playlist - Playlist:", playlistId, "Song:", songId);

    if (!playlistId || !songId) {
        return res.status(400).json({ success: false, message: "Missing playlistId or songId" });
    }

    try {
        // Kiểm tra xem bài hát đã có trong playlist chưa (tránh duplicate)
        const [duplicateCheck] = await db.query('SELECT * FROM playlist_songs WHERE playlist_id = ? AND song_id = ?', [playlistId, songId]);
        if (duplicateCheck.length > 0) {
            return res.status(200).json({ success: true, message: 'Song already in playlist' });
        }

        await db.query('INSERT INTO playlist_songs (playlist_id, song_id) VALUES (?, ?)', [playlistId, songId]);
        console.log("Successfully added song to playlist in MySQL");

        res.status(200).json({ success: true, message: 'Added to playlist' });
    } catch (error) {
        console.error("Error in addSongToPlaylist:", error);
        res.status(500).json({ success: false, message: "Server error: " + error.message });
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
    const { userId, artistId, artistName } = req.body;
    if (!userId) {
        return res.status(400).json({ success: false, message: "Missing userId" });
    }

    try {
        let finalArtistId = artistId;

        // Nếu không có artistId nhưng có artistName, kiểm tra hoặc tạo mới artist
        if (!finalArtistId && artistName) {
            const [existing] = await db.query('SELECT id FROM artists WHERE name = ?', [artistName]);
            if (existing.length > 0) {
                finalArtistId = existing[0].id;
            } else {
                // Tạo ID Axxx
                const [lastArtist] = await db.query('SELECT id FROM artists ORDER BY id DESC LIMIT 1');
                let newIdNumber = 1;
                if (lastArtist.length > 0) {
                    const lastId = lastArtist[0].id;
                    if (lastId.startsWith('A')) {
                        newIdNumber = parseInt(lastId.substring(1)) + 1;
                    }
                }
                finalArtistId = 'A' + String(newIdNumber).padStart(3, '0');
                await db.query('INSERT INTO artists (id, name) VALUES (?, ?)', [finalArtistId, artistName]);
            }
        }

        if (!finalArtistId) {
            return res.status(400).json({ success: false, message: "Missing artistId or artistName" });
        }

        const [existingFollow] = await db.query('SELECT * FROM user_followed_artists WHERE user_id = ? AND artist_id = ?', [userId, finalArtistId]);
        if (existingFollow.length > 0) {
            await db.query('DELETE FROM user_followed_artists WHERE user_id = ? AND artist_id = ?', [userId, finalArtistId]);
            res.status(200).json({ success: true, message: 'Unfollowed artist', isFollowed: false });
        } else {
            await db.query('INSERT INTO user_followed_artists (user_id, artist_id) VALUES (?, ?)', [userId, finalArtistId]);
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
    deletePlaylist,
    addSongToPlaylist,
    getFollowedArtists,
    toggleFollowArtist
};
