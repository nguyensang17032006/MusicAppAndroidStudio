const cloudinary = require('cloudinary').v2;
const multer = require('multer');
require('dotenv').config();

// 1. Định danh tài khoản Cloudinary của bạn
cloudinary.config({
    cloud_name: process.env.CLOUDINARY_CLOUD_NAME,
    api_key: process.env.CLOUDINARY_API_KEY,
    api_secret: process.env.CLOUDINARY_API_SECRET
});

// 2. Cấu hình Multer lưu file tạm vào bộ nhớ RAM (Memory Storage) để xử lý nhanh
const storage = multer.memoryStorage();

// 3. Bộ lọc an toàn: Chỉ cho phép upload file định dạng Ảnh hoặc Âm thanh
const fileFilter = (req, file, cb) => {
    if (file.mimetype.startsWith('image/') || file.mimetype.startsWith('audio/')) {
        cb(null, true);
    } else {
        cb(new Error('Định dạng file không hợp lệ! Chỉ chấp nhận file ảnh hoặc file nhạc (.mp3, .wav...).'), false);
    }
};

// 4. Khởi tạo cấu hình upload hoàn chỉnh
const upload = multer({
    storage: storage,
    fileFilter: fileFilter,
    limits: { fileSize: 20 * 1024 * 1024 } // Giới hạn kích thước file tối đa là 20MB
});

console.log('✅ Cấu hình Cloudinary và bộ lọc Multer đã kích hoạt hoàn tất!');

module.exports = { cloudinary, upload };