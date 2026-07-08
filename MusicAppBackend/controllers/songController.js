const db = require('../config/db');

const getAllSongs = async (req, res) => {
    try {
        const [rows] = await db.query(`
            SELECT s.*, a.name as artist_name 
            FROM songs s 
            LEFT JOIN artists a ON s.artist_id = a.id
        `);
        res.status(200).json({ success: true, data: rows });
    } catch (error) {
        res.status(500).json({ success: false, message: error.message });
    }
};


module.exports = {
    getAllSongs
};