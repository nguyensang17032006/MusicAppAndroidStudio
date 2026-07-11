const path = require('path');
const fs = require('fs');

const uploadFile = async (req, res) => {
    try {
        if (!req.file) {
            return res.status(400).json({ success: false, message: "Vui lòng chọn file để upload!" });
        }

        // Tạo tên file duy nhất để tránh trùng lặp
        const fileName = Date.now() + path.extname(req.file.originalname);
        const uploadsDir = path.join(__dirname, '../uploads');
        
        // Kiểm tra và tạo thư mục uploads nếu chưa có
        if (!fs.existsSync(uploadsDir)) {
            fs.mkdirSync(uploadsDir, { recursive: true });
        }

        const uploadPath = path.join(uploadsDir, fileName);

        // Lưu file từ Buffer (multer memoryStorage) vào thư mục uploads
        fs.writeFileSync(uploadPath, req.file.buffer);

        // Trả về đường dẫn tương đối để lưu vào MySQL
        const fileUrl = `/uploads/${fileName}`;

        console.log("File saved to:", uploadPath);

        return res.status(200).json({
            success: true,
            message: "Upload file thành công!",
            file_url: fileUrl
        });

    } catch (error) {
        console.error("Upload Error:", error);
        return res.status(500).json({ success: false, message: error.message });
    }
};

module.exports = { uploadFile };
