const db = require('../config/db');

const getAllArtists = async (req, res) => {
    try {
        const [rows] = await db.query('SELECT * FROM artists');
        res.status(200).json({ success: true, data: rows });
    } catch (error) {
        res.status(500).json({ success: false, message: error.message });
    }
};

module.exports = {
    getAllArtists
};
