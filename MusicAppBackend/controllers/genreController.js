const db = require('../config/db');

const getAllGenres = async (req, res) => {
    try {
        const [rows] = await db.query('SELECT * FROM genres');
        res.status(200).json({ success: true, data: rows });
    } catch (error) {
        res.status(500).json({ success: false, message: error.message });
    }
};

module.exports = {
    getAllGenres
};
