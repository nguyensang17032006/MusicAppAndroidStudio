const supabase = require('../config/supabase');
const db = require('../config/db');

// ==========================================
// 1. GỬI MÃ OTP (CHỈ CẦN EMAIL)
// ==========================================
const sendOtpEmail = async (req, res) => {
    console.log("Gửi OTP cho email:", req.body.email);
    const { email } = req.body;
    try {
        const { data, error } = await supabase.auth.signInWithOtp({
            email: email,
            options: { shouldCreateUser: true }
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
    // Android gửi kèm cả password mà user đã nhập ở bước đầu tiên lên đây
    const { email, token, password, gender } = req.body;

    try {
        // BƯỚC A: Xác thực mã OTP trước
        const { data: otpData, error: otpError } = await supabase.auth.verifyOtp({
            email: email,
            token: token,
            type: 'email'
        });

        if (otpError) {
            return res.status(400).json({ success: false, message: "Mã OTP không chính xác hoặc đã hết hạn." });
        }

        const supabaseUser = otpData.user;
        const session = otpData.session;

        if (!supabaseUser || !session) {
            return res.status(400).json({ success: false, message: "Không thể khởi tạo phiên đăng nhập." });
        }

        // BƯỚC B: OTP đúng -> Tiến hành đặt mật khẩu luôn cho tài khoản này bằng Access Token vừa nhận
        const { error: passError } = await supabase.auth.updateUser(
            { password: password },
            { auth: { accessToken: session.access_token } }
        );

        if (passError) {
            return res.status(400).json({ success: false, message: "Không thể lưu mật khẩu: " + passError.message });
        }

        // BƯỚC C: Lưu thông tin vào MySQL
        const query = `INSERT INTO users (id, email, gender) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE email=email`;
        db.query(query, [supabaseUser.id, email, gender || null], (err, result) => {
            if (err) {
                console.error("Lỗi MySQL:", err);
                return res.status(500).json({ success: false, message: "Lỗi lưu database MySQL." });
            }

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
                    gender: supabaseUser.gender
                }
            });
        });

    } catch (err) {
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

        // Nếu thông tin xác thực từ Supabase trống
        if (!supabaseUser || !session) {
            return res.status(400).json({ success: false, message: "Đăng nhập thất bại. Vui lòng thử lại." });
        }

        // Tài khoản đăng nhập bằng mật khẩu đã được lưu ở bước đăng ký OTP rồi, 
        // nên ở đây chỉ cần trả thông tin phiên làm việc thẳng về cho Android parse mà không cần chèn MySQL nữa.
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
                gender: supabaseUser.gender
            }
        });
    } catch (err) {
        return res.status(500).json({ success: false, message: err.message });
    }
};

module.exports = {
    sendOtpEmail,
    verifyAndRegister,
    login
};