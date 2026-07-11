const mysql = require('mysql2');
require('dotenv').config();

const pool = mysql.createPool({
    host: process.env.DB_HOST,
    user: process.env.DB_USER,
    password: process.env.DB_PASSWORD,
    database: process.env.DB_NAME,
    waitForConnections: true,
    connectionLimit: 10,
    queueLimit: 0
});

const db = pool.promise();

pool.getConnection(async (err, connection) => {
    if (err) {
        console.error('Lỗi kết nối MySQL rồi bạn ơi:', err.message);
    } else {
        console.log('Kết nối cơ sở dữ liệu MySQL thành công!');
        
        // Thêm cột friend_code nếu chưa có
        try {
            await connection.promise().query(`
                ALTER TABLE users ADD COLUMN friend_code VARCHAR(10) UNIQUE
            `);
            console.log('✅ Đã tự động thêm cột friend_code vào bảng users!');
        } catch (e) {
            if (e.code !== 'ER_DUP_FIELDNAME') {
                console.error('Lỗi khi thêm cột friend_code:', e.message);
            }
        }
        
        // Cập nhật friend_code cho các user cũ (luôn chạy để đề phòng)
        try {
            await connection.promise().query(`
                UPDATE users SET friend_code = SUBSTRING(id, 1, 8) WHERE friend_code IS NULL
            `);
            console.log('✅ Đã kiểm tra và gán friend_code cho các user cũ!');
        } catch (e) {
            console.error('Lỗi khi cập nhật friend_code:', e.message);
        }
        
        connection.release();
    }
});

module.exports = db;