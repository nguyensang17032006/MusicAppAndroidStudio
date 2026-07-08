const { cloudinary } = require('../config/cloudinary');

const uploadFile = async (req, res) => {
    try {
        // Kiểm tra xem Android đã gửi file lên chưa (key trong form-data phải đặt tên là 'file')
        if (!req.file) {
            return res.status(400).json({ success: false, message: "Vui lòng chọn file để upload!" });
        }

        // Tự động phân loại folder trên Cloudinary dựa trên kiểu định dạng file (mimetype)
        const isAudio = req.file.mimetype.startsWith('audio/');
        const folderName = isAudio ? 'music_app/tracks' : 'music_app/covers';

        // Cloudinary quản lý file âm thanh/video trong nhóm 'video', file ảnh trong nhóm 'image'
        const resourceType = isAudio ? 'video' : 'image';

        // Chuyển đổi file binary từ bộ nhớ đệm (Buffer) sang chuỗi chuỗi Base64 để gửi lên Cloudinary
        const fileBase64 = `data:${req.file.mimetype};base64,${req.file.buffer.toString('base64')}`;

        // Tiến hành upload file thẳng lên Cloudinary
        const result = await cloudinary.uploader.upload(fileBase64, {
            folder: folderName,
            resource_type: resourceType
        });

        // Trả về dữ liệu URL sạch cho Android Studio
        return res.status(200).json({
            success: true,
            message: "Upload file lên Cloudinary thành công!",
            file_url: result.secure_url, // Đường link https để bạn lưu vào MySQL sau này
            public_id: result.public_id, // Mã định danh file (dùng khi muốn xóa file)
            duration: result.duration ? Math.round(result.duration) : 0 // Trả về số giây (nếu là file nhạc)
        });

    } catch (error) {
        return res.status(500).json({ success: false, message: error.message });
    }
};

module.exports = { uploadFile };