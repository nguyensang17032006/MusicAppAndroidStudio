const express = require('express');
const cors = require('cors');
require('dotenv').config();
const { urlencoded } = require('express');

const db = require('./config/db');
const cloudinaryConfig = require('./config/cloudinary');
const supabase = require('./config/supabase');

const app = express();
const PORT = process.env.PORT || 5000;

app.use(cors());
app.use(express.json());
app.use(urlencoded({ extended: true }));
app.use('/uploads', express.static('uploads'));


const songRoutes = require('./routes/songRoutes');
const authRoutes = require('./routes/authRoutes');
const artistRoutes = require('./routes/artistRoutes');
const genreRoutes = require('./routes/genreRoutes');
const recommendRoutes = require('./routes/recommendRoutes');
const uploadRoutes = require('./routes/uploadRoutes');
const statsRoutes = require('./routes/statsRoutes');
const libraryRoutes = require('./routes/libraryRoutes');

app.use('/api/auth', authRoutes);
app.use('/api/songs', songRoutes);
app.use('/api/artists', artistRoutes);
app.use('/api/genres', genreRoutes);
app.use('/api/recommendations', recommendRoutes);
app.use('/api/upload', uploadRoutes);
app.use('/api/stats', statsRoutes);
app.use('/api/library', libraryRoutes);

app.use((err, req, res, next) => {
    if (err) {
        return res.status(400).json({ success: false, message: err.message });
    }
    next();
});

app.listen(PORT, () => {
    console.log(`Server is running on http://localhost:${PORT}`);
});