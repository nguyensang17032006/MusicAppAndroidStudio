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
        
        // Auto-migration: Thêm cột friend_code nếu chưa có
        try {
            await connection.promise().query(`
                ALTER TABLE users ADD COLUMN friend_code VARCHAR(10) UNIQUE
            `);
            console.log('✅ Đã tự động thêm cột friend_code vào bảng users!');
            
            // Cập nhật friend_code cho các user cũ bằng 8 ký tự đầu của id
            await connection.promise().query(`
                UPDATE users SET friend_code = SUBSTRING(id, 1, 8) WHERE friend_code IS NULL
            `);
            console.log('✅ Đã cập nhật friend_code cho các user cũ!');
        } catch (e) {
            // Bỏ qua lỗi nếu cột đã tồn tại (ER_DUP_FIELDNAME)
            if (e.code !== 'ER_DUP_FIELDNAME') {
                console.error('Lỗi khi migrate friend_code:', e.message);
            }
        }
        
        connection.release();
    }
});

module.exports = db;