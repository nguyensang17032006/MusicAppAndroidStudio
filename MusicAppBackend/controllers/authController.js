const supabase = require('../config/supabase');
const db = require('../config/db');

// ==========================================
// 1. GỬI MÃ OTP (CHỈ CẦN EMAIL)
// ==========================================
const sendOtpEmail = async (req, res) => {
    console.log("Gửi OTP cho email:", req.body.email);
    const { email, password } = req.body;
    try {
        const { data, error } = await supabase.auth.signUp({
            email: email,
            password: password
        });

        if (error) {
            console.error("Lỗi gửi OTP:", error);
            return res.status(400).json({ success: false, message: error.message });
        }
        return res.status(200).json({ success: true, message: "Mã OTP đã được gửi!" });
    } catch (err) {
        return res.status(500).json({ success: false, message: err.message });
    }
};

// ==========================================
// 2. XÁC THỰC OTP + ĐẶT MẬT KHẨU + LƯU MYSQL (TẤT CẢ TRONG 1)
// ==========================================
const verifyAndRegister = async (req, res) => {
    console.log("Xác thực OTP và đăng ký tài khoản với dữ liệu:", req.body);
    const { email, token, password, gender } = req.body;

    try {
        // BƯỚC A: Xác thực mã OTP (Dùng 'signup' là hoàn toàn chính xác với hàm auth.signUp)
        const { data: otpData, error: otpError } = await supabase.auth.verifyOtp({
            email: email,
            token: String(token).trim(), // Đảm bảo không mất số 0 ở đầu nếu có
            type: 'signup'
        });

        if (otpError) {
            console.error("❌ Lỗi Supabase Verify:", otpError.message);
            return res.status(400).json({ success: false, message: "Mã OTP không chính xác hoặc đã hết hạn." });
        }

        const supabaseUser = otpData.user;
        const session = otpData.session;

        if (!supabaseUser || !session) {
            console.error("❌ Lỗi: Không có session/user trả về.");
            return res.status(400).json({ success: false, message: "Không thể khởi tạo phiên đăng nhập." });
        }

        console.log("✅ Xác thực OTP trên Supabase thành công! Chuẩn bị lưu vào MySQL...");

        // BƯỚC B: Lưu thông tin vào MySQL
        const sql = `INSERT INTO users (id, email, gender) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE email=email`;

        try {
            await db.query(sql, [supabaseUser.id, email, gender || null]);
            console.log("🚀 Đã lưu thông tin vào MySQL thành công!");

            // Trả về dữ liệu sạch sẽ cho Android parse thành AuthResponse.java
            return res.status(201).json({
                access_token: session.access_token,
                refresh_token: session.refresh_token,
                expires_in: session.expires_in || 0,
                token_type: session.token_type || "bearer",
                user: {
                    id: supabaseUser.id,
                    email: supabaseUser.email,
                    email_confirmed_at: supabaseUser.email_confirmed_at,
                    created_at: supabaseUser.created_at,
                    gender: gender || null
                }
            });
        } catch (dbErr) {
            console.error("❌ Lỗi thực thi MySQL:", dbErr.message);
            return res.status(500).json({ success: false, message: "Lỗi lưu database MySQL: " + dbErr.message });
        }

    } catch (err) {
        console.error("❌ Lỗi hệ thống ngoài dự kiến:", err.message);
        return res.status(500).json({ success: false, message: err.message });
    }
};
// ==========================================
// 3. XỬ LÝ ĐĂNG NHẬP (BẰNG EMAIL & MẬT KHẨU TRUYỀN THỐNG)
// ==========================================
const login = async (req, res) => {
    const { email, password } = req.body;
    try {
        const { data, error } = await supabase.auth.signInWithPassword({
            email: email,
            password: password,
        });

        if (error) {
            return res.status(400).json({ success: false, message: error.message });
        }

        const supabaseUser = data.user;
        const session = data.session;

        if (!supabaseUser || !session) {
            return res.status(400).json({ success: false, message: "Đăng nhập thất bại. Vui lòng thử lại." });
        }

        // ĐẢM BẢO USER TỒN TẠI TRONG MYSQL (UPSERT)
        // Nếu chưa có thì chèn mới, nếu có rồi thì giữ nguyên (hoặc cập nhật email)
        const sql = `INSERT INTO users (id, email) VALUES (?, ?) ON DUPLICATE KEY UPDATE email=email`;
        try {
            await db.query(sql, [supabaseUser.id, supabaseUser.email]);
        } catch (dbErr) {
            console.error("Lỗi cập nhật User vào MySQL khi login:", dbErr.message);
        }

        // Lấy thông tin chi tiết (bao gồm gender) sau khi đã đảm bảo user tồn tại
        const [userRows] = await db.query('SELECT gender FROM users WHERE id = ?', [supabaseUser.id]);
        const gender = userRows.length > 0 ? userRows[0].gender : null;

        return res.status(200).json({
            access_token: session.access_token,
            refresh_token: session.refresh_token,
            expires_in: session.expires_in || 0,
            token_type: session.token_type || "bearer",
            user: {
                id: supabaseUser.id,
                email: supabaseUser.email,
                email_confirmed_at: supabaseUser.email_confirmed_at,
                last_sign_in_at: supabaseUser.last_sign_in_at,
                created_at: supabaseUser.created_at,
                gender: gender
            }
        });
    } catch (err) {
        return res.status(500).json({ success: false, message: err.message });
    }
};

const getUserProfile = async (req, res) => {
    const { userId } = req.params;
    try {
        const [rows] = await db.query('SELECT * FROM users WHERE id = ?', [userId]);
        if (rows.length > 0) {
            res.status(200).json({ success: true, data: rows[0] });
        } else {
            res.status(404).json({ success: false, message: 'User not found' });
        }
    } catch (error) {
        res.status(500).json({ success: false, message: error.message });
    }
};

const updateProfile = async (req, res) => {
    const { userId, gender } = req.body;
    try {
        await db.query('UPDATE users SET gender = ? WHERE id = ?', [gender, userId]);
        res.status(200).json({ success: true, message: 'Profile updated' });
    } catch (error) {
        res.status(500).json({ success: false, message: error.message });
    }
};

module.exports = {
    sendOtpEmail,
    verifyAndRegister,
    login,
    getUserProfile,
    updateProfile
};