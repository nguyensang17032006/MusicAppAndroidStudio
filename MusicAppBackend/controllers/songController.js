const db = require('../config/db');

const getAllSongs = async (req, res) => {
    try {
        const [rows] = await db.query(`
            SELECT s.*,
                   (SELECT GROUP_CONCAT(a.name SEPARATOR ', ')
                    FROM artists a
                    JOIN song_artists sa ON a.id = sa.artist_id
                    WHERE sa.song_id = s.id) as artist_names,
                   (SELECT GROUP_CONCAT(g.name SEPARATOR ', ')
                    FROM genres g
                    JOIN song_genres sg ON g.id = sg.genre_id
                    WHERE sg.song_id = s.id) as genre_names
            FROM songs s
        `);
        res.status(200).json({ success: true, data: rows });
    } catch (error) {
        res.status(500).json({ success: false, message: error.message });
    }
};

module.exports = {
    getAllSongs
};
