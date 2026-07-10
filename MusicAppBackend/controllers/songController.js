const db = require('../config/db');
const { cloudinary } = require('../config/cloudinary');

const getAllSongs = async (req, res) => {
    try {
        // Fetch all songs
        const [songs] = await db.query('SELECT * FROM songs');

        // Fetch all song-artist associations
        const [songArtists] = await db.query(`
            SELECT sa.song_id, sa.artist_id, sa.is_main_artist, a.name 
            FROM song_artists sa 
            JOIN artists a ON sa.artist_id = a.id
        `);

        // Fetch all song-genre associations
        const [songGenres] = await db.query(`
            SELECT sg.song_id, sg.genre_id, g.name 
            FROM song_genres sg 
            JOIN genres g ON sg.genre_id = g.id
        `);

        // Group artists by song_id
        const artistsMap = {};
        songArtists.forEach(sa => {
            if (!artistsMap[sa.song_id]) {
                artistsMap[sa.song_id] = [];
            }
            artistsMap[sa.song_id].push({
                artist_id: sa.artist_id,
                name: sa.name,
                is_main_artist: sa.is_main_artist === 1 || sa.is_main_artist === true
            });
        });

        // Group genres by song_id
        const genresMap = {};
        songGenres.forEach(sg => {
            if (!genresMap[sg.song_id]) {
                genresMap[sg.song_id] = [];
            }
            genresMap[sg.song_id].push({
                genre_id: sg.genre_id,
                name: sg.name
            });
        });

        // Map artists and genres back into each song
        const mappedSongs = songs.map(s => {
            return {
                ...s,
                artists: artistsMap[s.id] || [],
                genres: genresMap[s.id] || []
            };
        });

        res.status(200).json({ success: true, data: mappedSongs });
    } catch (error) {
        res.status(500).json({ success: false, message: error.message });
    }
};

const saveSong = async (req, res) => {
    console.log("DEBUG saveSong - Request body:", req.body);
    console.log("DEBUG saveSong - Request files:", req.files);

    const { id, title, duration, file_url, cover_url } = req.body;
    let artists = [];
    let genres = [];

    try {
        if (req.body.artists) {
            artists = JSON.parse(req.body.artists);
        }
        if (req.body.genres) {
            genres = JSON.parse(req.body.genres);
        }
    } catch (e) {
        return res.status(400).json({ success: false, error: "Invalid artists or genres JSON structure" });
    }

    try {
        let finalFileUrl = file_url || '';
        let finalCoverUrl = cover_url || '';

        // Helper function for uploading to Cloudinary
        const uploadToCloudinary = (file, isAudio) => {
            return new Promise((resolve, reject) => {
                const folderName = isAudio ? 'music_app/tracks' : 'music_app/covers';
                const resourceType = isAudio ? 'video' : 'image';
                const fileBase64 = `data:${file.mimetype};base64,${file.buffer.toString('base64')}`;

                cloudinary.uploader.upload(fileBase64, {
                    folder: folderName,
                    resource_type: resourceType
                })
                    .then(result => resolve(result.secure_url))
                    .catch(err => reject(err));
            });
        };

        // Handle file uploads if any
        if (req.files) {
            if (req.files.audio && req.files.audio[0]) {
                finalFileUrl = await uploadToCloudinary(req.files.audio[0], true);
            }
            if (req.files.cover && req.files.cover[0]) {
                finalCoverUrl = await uploadToCloudinary(req.files.cover[0], false);
            }
        }

        console.log("DEBUG saveSong - SQL parameters prepared:", {
            id,
            title,
            finalFileUrl,
            finalCoverUrl,
            duration: duration || 0
        });

        if (!finalFileUrl && !file_url) {
            return res.status(400).json({ success: false, error: "Audio file is required" });
        }

        // Check if the song exists
        const [existing] = await db.query('SELECT id FROM songs WHERE id = ?', [id]);

        if (existing.length > 0) {
            // Update
            await db.query(
                'UPDATE songs SET title = ?, file_url = ?, cover_url = ?, duration = ? WHERE id = ?',
                [title, finalFileUrl, finalCoverUrl, duration || 0, id]
            );
        } else {
            // Insert
            await db.query(
                'INSERT INTO songs (id, title, file_url, cover_url, duration, views) VALUES (?, ?, ?, ?, ?, 0)',
                [id, title, finalFileUrl, finalCoverUrl, duration || 0]
            );
        }

        // Sync artists relations
        await db.query('DELETE FROM song_artists WHERE song_id = ?', [id]);
        for (const art of artists) {
            await db.query(
                'INSERT INTO song_artists (song_id, artist_id, is_main_artist) VALUES (?, ?, ?)',
                [id, art.artist_id, art.is_main_artist ? 1 : 0]
            );
        }

        // Sync genres relations
        await db.query('DELETE FROM song_genres WHERE song_id = ?', [id]);
        for (const genId of genres) {
            await db.query(
                'INSERT INTO song_genres (song_id, genre_id) VALUES (?, ?)',
                [id, genId]
            );
        }

        res.status(200).json({ success: true, message: "Saved successfully to MySQL database!" });
    } catch (error) {
        console.error("Error saving song:", error);
        res.status(500).json({ success: false, error: error.message });
    }
};

const deleteSong = async (req, res) => {
    const { id } = req.params;
    try {
        await db.query('DELETE FROM song_artists WHERE song_id = ?', [id]);
        await db.query('DELETE FROM song_genres WHERE song_id = ?', [id]);
        await db.query('DELETE FROM songs WHERE id = ?', [id]);

        res.status(200).json({ success: true, message: `Deleted song ${id} successfully.` });
    } catch (error) {
        console.error("Error deleting song:", error);
        res.status(500).json({ success: false, error: error.message });
    }
};

module.exports = {
    getAllSongs,
    saveSong,
    deleteSong
};
