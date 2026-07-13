const db = require('../config/db');
const { cloudinary } = require('../config/cloudinary');

const getAllArtists = async (req, res) => {
    try {
        const [rows] = await db.query('SELECT * FROM artists');
        res.status(200).json({ success: true, data: rows });
    } catch (error) {
        res.status(500).json({ success: false, message: error.message });
    }
};

const saveArtist = async (req, res) => {
    console.log("DEBUG saveArtist - Request body:", req.body);
    console.log("DEBUG saveArtist - Request file:", req.file);

    const { id, name, bio, avatar_url } = req.body;

    try {
        let finalAvatarUrl = avatar_url || '';

        // Helper function for uploading to Cloudinary
        const uploadToCloudinary = (file) => {
            return new Promise((resolve, reject) => {
                const fileBase64 = `data:${file.mimetype};base64,${file.buffer.toString('base64')}`;

                cloudinary.uploader.upload(fileBase64, {
                    folder: 'music_app/artists',
                    resource_type: 'image'
                })
                    .then(result => resolve(result.secure_url))
                    .catch(err => reject(err));
            });
        };

        // Handle file uploads if any
        if (req.file) {
            finalAvatarUrl = await uploadToCloudinary(req.file);
        }

        if (!name) {
            return res.status(400).json({ success: false, error: "Artist name is required" });
        }

        // Check if the artist exists
        const [existing] = await db.query('SELECT id FROM artists WHERE id = ?', [id]);

        if (existing.length > 0) {
            // Update
            await db.query(
                'UPDATE artists SET name = ?, bio = ?, avatar_url = ? WHERE id = ?',
                [name, bio || null, finalAvatarUrl, id]
            );
        } else {
            // Insert
            await db.query(
                'INSERT INTO artists (id, name, bio, avatar_url, created_at) VALUES (?, ?, ?, ?, NOW())',
                [id, name, bio || null, finalAvatarUrl]
            );
        }

        res.status(200).json({ success: true, message: "Saved successfully to MySQL database!" });
    } catch (error) {
        console.error("Error saving artist:", error);
        res.status(500).json({ success: false, error: error.message });
    }
};

const deleteArtist = async (req, res) => {
    const { id } = req.params;
    try {
        await db.query('DELETE FROM user_followed_artists WHERE artist_id = ?', [id]);
        await db.query('DELETE FROM song_artists WHERE artist_id = ?', [id]);
        await db.query('DELETE FROM artists WHERE id = ?', [id]);

        res.status(200).json({ success: true, message: `Deleted artist ${id} successfully.` });
    } catch (error) {
        console.error("Error deleting artist:", error);
        res.status(500).json({ success: false, error: error.message });
    }
};

module.exports = {
    getAllArtists,
    saveArtist,
    deleteArtist
};
