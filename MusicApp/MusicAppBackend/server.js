const express = require('express');
const mysql = require('mysql2');
const cors = require('cors');

const app = express();
app.use(cors());
app.use(express.json());

// Kết nối database MySQL
const db = mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: 'Khanh150206@',
    database: 'music_app_db'
});

db.connect(err => {
    if (err) {
        console.error('Lỗi kết nối MySQL:', err);
        return;
    }
    console.log('Đã kết nối MySQL thành công!');
});

// 1. API Lấy danh sách bài hát (Kèm tên nghệ sĩ và thể loại)
app.get('/api/songs', (req, res) => {
    const sql = `
        SELECT s.*,
               (SELECT GROUP_CONCAT(a.name SEPARATOR ', ')
                FROM song_artists sa
                JOIN artists a ON sa.artist_id = a.id
                WHERE sa.song_id = s.id) as artist_names,
               (SELECT GROUP_CONCAT(g.name SEPARATOR ', ')
                FROM song_genres sg
                JOIN genres g ON sg.genre_id = g.id
                WHERE sg.song_id = s.id) as genre_names
        FROM songs s
    `;
    db.query(sql, (err, results) => {
        if (err) return res.status(500).json({ error: err.message });
        res.json(results);
    });
});

// 2. API Lấy danh sách thể loại (Dùng cho GridView trang Search)
app.get('/api/genres', (req, res) => {
    db.query('SELECT * FROM genres', (err, results) => {
        if (err) return res.status(500).json({ error: err.message });
        res.json(results);
    });
});

// 3. API Lấy danh sách nghệ sĩ (Dùng cho trang Library)
app.get('/api/artists', (req, res) => {
    db.query('SELECT * FROM artists', (err, results) => {
        if (err) return res.status(500).json({ error: err.message });
        res.json(results);
    });
});

// 4. API Lấy thông tin User (Dùng cho Profile)
app.get('/api/users/:id', (req, res) => {
    const userId = req.params.id;
    db.query('SELECT * FROM users WHERE id = ?', [userId], (err, results) => {
        if (err) return res.status(500).json({ error: err.message });
        if (results.length === 0) return res.status(404).json({ message: 'User not found' });
        res.json(results[0]);
    });
});

const PORT = 3000;
app.listen(PORT, () => {
    console.log(`Server đang chạy tại http://localhost:${PORT}`);
});
