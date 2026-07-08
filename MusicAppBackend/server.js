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


const songRoutes = require('./routes/songRoutes');
const authRoutes = require('./routes/authRoutes');
const recommendRoutes = require('./routes/recommendRoutes');
const uploadRoutes = require('./routes/uploadRoutes');

app.use('/api/auth', authRoutes);
app.use('/api/songs', songRoutes);
app.use('/api/recommendations', recommendRoutes);
app.use('/api/upload', uploadRoutes);

app.use((err, req, res, next) => {
    if (err) {
        return res.status(400).json({ success: false, message: err.message });
    }
    next();
});

app.listen(PORT, () => {
    console.log(`Server is running on http://localhost:${PORT}`);
});