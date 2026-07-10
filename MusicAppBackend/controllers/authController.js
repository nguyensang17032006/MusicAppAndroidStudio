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
    const { email, token, otp, password, gender } = req.body;
    const otpToken = token || otp;

    try {
        // BƯỚC A: Xác thực mã OTP trên Supabase
        const { data: otpData, error: otpError } = await supabase.auth.verifyOtp({
            email: email,
            token: String(otpToken).trim(),
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

        // BƯỚC B: Lưu thông tin vào MySQL (Chỉ chạy 1 lần duy nhất)
        const sql = `INSERT INTO users (id, email, gender) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE email=email`;

        try {
            await db.query(sql, [supabaseUser.id, email, gender || null]);
            console.log("🚀 Đã lưu thông tin vào MySQL thành công!");
        } catch (dbErr) {
            console.error("❌ Lỗi thực thi MySQL:", dbErr.message);
            return res.status(500).json({ success: false, message: "Lỗi lưu database MySQL: " + dbErr.message });
        }

        // BƯỚC C: Trả về dữ liệu sạch sẽ cho Android parse thành AuthResponse.java
        return res.status(200).json({
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
    const { userId, gender, avatar_url } = req.body;
    try {
        await db.query('UPDATE users SET gender = ?, avatar_url = ? WHERE id = ?', [gender, avatar_url, userId]);
        res.status(200).json({ success: true, message: 'Profile updated' });
    } catch (error) {
        res.status(500).json({ success: false, message: error.message });
    }
};

const verifyOtpForgotPassword = async (req, res) => {
    console.log("👉 Đang xác thực OTP quên mật khẩu...");
    const { email, token, otp } = req.body;
    const otpToken = token || otp;

    try {
        const { data: verifyData, error: verifyError } = await supabase.auth.verifyOtp({
            email: email,
            token: String(otpToken).trim(),
            type: 'recovery'
        });

        if (verifyError) {
            console.error("❌ Xác thực OTP thất bại:", verifyError.message);
            return res.status(400).json({ success: false, message: "Mã OTP không chính xác hoặc đã hết hạn." });
        }

        // 🔍 IN RA TERMINAL ĐỂ KIỂM TRA XEM SUPABASE TRẢ VỀ GÌ
        console.log("👉 Dữ liệu verifyData từ Supabase:", JSON.stringify(verifyData, null, 2));

        // Đôi khi token nằm trong verifyData.session, đôi khi nằm trực tiếp trong verifyData tùy phiên bản SDK
        const accessToken = verifyData.session?.access_token || verifyData.access_token;
        const refreshToken = verifyData.session?.refresh_token || verifyData.refresh_token;

        if (!accessToken) {
            console.error("❌ Supabase không trả về access_token cho luồng recovery này!");
            return res.status(400).json({ success: false, message: "Không thể khởi tạo phiên khôi phục mật khẩu." });
        }

        console.log("✅ OTP đúng! Đang gửi Access Token về cho Android...");

        // Trả về đúng định dạng mà Class AuthResponse bên Android đang mong đợi
        return res.status(200).json({
            access_token: accessToken,
            refresh_token: refreshToken || "",
            expires_in: verifyData.session?.expires_in || 3600,
            token_type: "bearer",
            user: { id: verifyData.user?.id || "", email: email }
        });

    } catch (err) {
        console.error("❌ Lỗi hệ thống verify OTP:", err.message);
        return res.status(500).json({ success: false, message: err.message });
    }
};

const sendOtpForgotPassword = async (req, res) => {
    const { email } = req.body;
    console.log("👉 Yêu cầu gửi OTP quên mật khẩu cho:", email);

    try {
        // Gọi hàm gửi mã reset password của Supabase
        const { data, error } = await supabase.auth.resetPasswordForEmail(email);

        if (error) {
            console.error("❌ Lỗi Supabase gửi OTP quên mật khẩu:", error.message);
            return res.status(400).json({ success: false, message: error.message });
        }

        return res.status(200).json({ success: true, message: "Mã OTP khôi phục mật khẩu đã được gửi!" });
    } catch (err) {
        console.error("❌ Lỗi hệ thống:", err.message);
        return res.status(500).json({ success: false, message: err.message });
    }
};
const updateNewPassword = async (req, res) => {
    console.log("👉 Nhận yêu cầu cập nhật mật khẩu mới...");
    const { accessToken, newPassword } = req.body;

    if (!accessToken || !newPassword) {
        return res.status(400).json({ success: false, message: "Thiếu dữ liệu xác thực hoặc mật khẩu mới!" });
    }

    try {
        // 1. Thiết lập session làm việc bằng chính Access Token Android vừa gửi lên
        await supabase.auth.setSession({
            access_token: accessToken,
            refresh_token: "" // Không cần refresh token ở bước đổi nhanh này
        });

        // 2. Gọi hàm updateUser thông thường để tự đổi mật khẩu chính mình
        const { error: updateError } = await supabase.auth.updateUser({
            password: newPassword
        });

        if (updateError) {
            console.error("❌ Lỗi cập nhật mật khẩu mới:", updateError.message);
            return res.status(400).json({ success: false, message: "Đổi mật khẩu thất bại: " + updateError.message });
        }

        console.log("✅ Đổi mật khẩu thành công hoàn toàn!");
        return res.status(200).json({ success: true, message: "Cập nhật mật khẩu mới thành công! Vui lòng đăng nhập lại." });

    } catch (err) {
        console.error("❌ Lỗi hệ thống khi đổi mật khẩu:", err.message);
        return res.status(500).json({ success: false, message: err.message });
    }
};
module.exports = {
    sendOtpEmail,
    verifyAndRegister,
    login,
    getUserProfile,
    updateProfile,
    sendOtpForgotPassword,
    verifyOtpForgotPassword,
    updateNewPassword
};