const db = require('../config/db');

// =========================================================================
// 1. GHI NHẬN TƯƠNG TÁC (Android gọi khi user nghe nhạc hoặc bấm Thích)
// =========================================================================
const logInteraction = async (req, res) => {
    const { user_id, song_id, listen_duration, is_liked, is_skipped } = req.body;

    if (!user_id || !song_id) {
        return res.status(400).json({ success: false, message: "Thiếu thông tin người dùng hoặc bài hát!" });
    }

    try {
        // Chèn một dòng log tương tác mới vào MySQL
        await db.query(
            `INSERT INTO interaction_logs (user_id, song_id, listen_duration, is_liked, is_skipped) 
             VALUES (?, ?, ?, ?, ?)`,
            [user_id, song_id, listen_duration || 0, is_liked || false, is_skipped || false]
        );

        // Tự động tăng 1 lượt view cho bài hát đó trong bảng songs
        await db.query(`UPDATE songs SET views = views + 1 WHERE id = ?`, [song_id]);

        res.status(200).json({ success: true, message: "Đã ghi nhận tương tác thành công để luyện AI!" });
    } catch (error) {
        res.status(500).json({ success: false, message: error.message });
    }
};

// =========================================================================
// 2. THUẬT TOÁN GỢI Ý (Trả về danh sách bài hát cá nhân hóa cho màn hình Home)
// =========================================================================
const getRecommendations = async (req, res) => {
    const { user_id } = req.query;

    if (!user_id) {
        return res.status(400).json({ success: false, message: "Thiếu ID người dùng!" });
    }

    try {
        // BƯỚC A: Phân tích xem thể loại nhạc (genre_id) nào đem lại điểm số cao nhất cho user này
        // Quy tắc tính điểm đơn giản: Thích = +5 điểm, Nghe lâu = +3 điểm, Bị Skip = -5 điểm
        const [favoriteGenreResult] = await db.query(`
            SELECT sg.genre_id, 
                   SUM(CASE 
                       WHEN i.is_liked = 1 THEN 5 
                       WHEN i.is_skipped = 1 THEN -5 
                       ELSE 3 
                   END) as preference_score
            FROM interaction_logs i
            JOIN song_genres sg ON i.song_id = sg.song_id
            WHERE i.user_id = ? AND sg.genre_id IS NOT NULL
            GROUP BY sg.genre_id
            ORDER BY preference_score DESC
            LIMIT 1
        `, [user_id]);

        // TRƯỜNG HỢP 1: User mới hoàn toàn, chưa có dữ liệu tương tác
        // -> Gợi ý Top 10 bài hát có lượt xem (views) cao nhất hệ thống (Trending)
        if (favoriteGenreResult.length === 0 || favoriteGenreResult[0].preference_score <= 0) {
            const [trendingSongs] = await db.query(`
                SELECT s.*, 
                       (SELECT a.name 
                        FROM artists a 
                        JOIN song_artists sa ON a.id = sa.artist_id 
                        WHERE sa.song_id = s.id AND sa.is_main_artist = 1 
                        LIMIT 1) as artist_name
                FROM songs s
                ORDER BY s.views DESC 
                LIMIT 10
            `);
            return res.status(200).json({
                success: true,
                type: "trending",
                message: "Gợi ý các bài hát đang hot (Dành cho user mới)",
                data: trendingSongs
            });
        }

        // TRƯỜNG HỢP 2: Đã tìm ra gu nhạc của User
        const topGenreId = favoriteGenreResult[0].genre_id;

        // Tiến hành lấy ra 10 bài hát thuộc thể loại này (Ưu tiên các bài nhiều view)
        const [recommendedSongs] = await db.query(`
            SELECT s.*, 
                   (SELECT a.name 
                    FROM artists a 
                    JOIN song_artists sa ON a.id = sa.artist_id 
                    WHERE sa.song_id = s.id AND sa.is_main_artist = 1 
                    LIMIT 1) as artist_name
            FROM songs s
            JOIN song_genres sg ON s.id = sg.song_id
            WHERE sg.genre_id = ?
            ORDER BY s.views DESC
            LIMIT 10
        `, [topGenreId]);

        res.status(200).json({
            success: true,
            type: "personalized",
            message: "Gợi ý danh riêng cho bạn dựa trên lịch sử nghe nhạc",
            target_genre_id: topGenreId,
            data: recommendedSongs
        });

    } catch (error) {
        res.status(500).json({ success: false, message: error.message });
    }
};

module.exports = {
    logInteraction,
    getRecommendations
};